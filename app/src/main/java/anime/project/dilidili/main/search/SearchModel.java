package anime.project.dilidili.main.search;

import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import anime.project.dilidili.api.Api;
import anime.project.dilidili.bean.SearchBean;
import anime.project.dilidili.net.HttpGet;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class SearchModel implements SearchContract.Model {

    @Override
    public void getData(String title, int page, boolean isMain, SearchContract.LoadDataCallback callback) {
        Log.e("url", String.format(Api.NEW_SEARCH_API, page, title));
        new HttpGet(String.format(Api.NEW_SEARCH_API, page, title), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.error(isMain, e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Document doc = Jsoup.parse(response.body().string());
                Elements result = doc.select("ul.serach-ul");
                if (result.size() > 0) {
                    if (isMain) {
                        Elements pages = doc.select("ul.pagination").select("li");
                        if (pages.size() > 0) {
                            callback.pageCount(Integer.parseInt(pages.get(pages.size() - 3).text()));
                        }
                    }
                    List<SearchBean> list = new ArrayList<>();
                    for (int i=0;i < result.size(); i++) {
                        list.add(
                                new SearchBean(
                                        result.get(i).select("h2").text(),
                                        result.get(i).select("a.list-img").attr("href"),
                                        result.get(i).select("img").attr("data-original"),
                                        result.get(i).select("p.plot").text()
                                        ));
                    }
                    callback.success(isMain, list);
                }else {
                    callback.error(isMain, "没有搜索到相关信息");
                }
            }
        });
    }
}
