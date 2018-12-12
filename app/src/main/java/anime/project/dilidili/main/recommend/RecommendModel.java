package anime.project.dilidili.main.recommend;

import com.chad.library.adapter.base.entity.MultiItemEntity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import anime.project.dilidili.api.Api;
import anime.project.dilidili.bean.RecommendBean;
import anime.project.dilidili.bean.RecommendHeaderBean;
import anime.project.dilidili.net.OkHttpGet;
import anime.project.dilidili.util.Utils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class RecommendModel implements RecommendContract.Model{

    @Override
    public void getData(RecommendContract.LoadDataCallback callback) {
        new OkHttpGet(Api.RECOMMEND_API, 10, 20, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.error(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) {
                try {
                    Document doc = Jsoup.parse(response.body().string());
                    Elements anime = doc.getElementsByClass("main");
                    List<MultiItemEntity> list = new ArrayList<>();
                    if (anime.size() > 0) {
                        Elements title = anime.get(1).getElementsByClass("title");
                        Elements book = anime.get(1).getElementsByClass("book");
                        for (int i=0;i<title.size();i++){
                            RecommendHeaderBean recommendHeaderBean = new RecommendHeaderBean(title.get(i).select("a").get(0).text());
                            Elements recommecd = book.get(i).select("a");
                            for (int j=0;j<recommecd.size();j++){
                                RecommendBean recommendBean = new RecommendBean(
                                        recommecd.get(j).select("p").text(),
                                        Utils.getImageUrl(recommecd.get(j).select("div").attr("style")),
                                        recommecd.get(j).attr("href")
                                );
                                recommendHeaderBean.addSubItem(recommendBean);
                            }
                            list.add(recommendHeaderBean);
                        }
                        callback.success(list);
                    } else {
                        //解析失败
                        callback.error("解析方法失效,等待更新");
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    callback.error(e.getMessage());
                }
            }
        });
    }
}
