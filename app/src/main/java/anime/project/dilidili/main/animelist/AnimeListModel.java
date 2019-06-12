package anime.project.dilidili.main.animelist;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import anime.project.dilidili.R;
import anime.project.dilidili.bean.AnimeListBean;
import anime.project.dilidili.net.HttpGet;
import anime.project.dilidili.util.Utils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AnimeListModel implements AnimeListContract.Model{

    @Override
    public void getData(String url, AnimeListContract.LoadDataCallback callback) {
        new HttpGet(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.error(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) {
                try{
                    Document body = Jsoup.parse(response.body().string());
                    Elements animeList = body.getElementsByClass("anime_list").select("dl");
                    if (animeList.size() > 0) {
                        List<AnimeListBean> list = new ArrayList<>();
                        for (int i = 0; i < animeList.size(); i++) {
                            AnimeListBean bean = new AnimeListBean();
                            bean.setImg(animeList.get(i).select("dt").select("img").attr("src"));
                            bean.setUrl(animeList.get(i).select("h3").select("a").attr("href"));
                            bean.setTitle(animeList.get(i).select("h3").text());
                            Elements label = animeList.get(i).getElementsByClass("d_label");
                            for (int k = 0;k < label.size(); k++){
                                String str = label.get(k).text();
                                if (str.contains("地区"))
                                    bean.setRegion(str);
                                else if (str.contains("年代"))
                                    bean.setYear(str);
                                else if (str.contains("标签"))
                                    bean.setTag(str);
                                else if (str.contains("制作"))
                                    bean.setPlay_count(str);
                            }
                            Elements p = animeList.get(i).select("p");
                            for (int j = 0;j < p.size(); j++){
                                String str = p.get(j).text();
                                if (str.contains("看点"))
                                    bean.setShow(str);
                                else if (str.contains("状态"))
                                    bean.setState(str);
                            }
                            list.add(bean);
                        }
                        callback.success(list);
                    } else {
                        callback.error(Utils.getString(R.string.parsing_error));
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    callback.error(e.getMessage());
                }
            }
        });
    }
}
