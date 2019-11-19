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
        Log.e("url",String.format(Api.NEW_SEARCH_API, title, page));
        new HttpGet(String.format(Api.NEW_SEARCH_API, title, page), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.error(isMain, e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Document doc = Jsoup.parse(response.body().string());
                Elements result = doc.select("div.col-sm-6");
                if (result.size() > 0) {
                    if (isMain) {
                        Elements resultCountHtml = doc.select("input[name=TotalResult]");
                        Elements pagesizeHtml = doc.select("input[name=pagesize]");
                        int resultCount = resultCountHtml.size() > 0 ? Integer.parseInt(resultCountHtml.get(0).val()) : 1;
                        int pagesize = pagesizeHtml.size() > 0 ? Integer.parseInt(pagesizeHtml.get(0).val()) : 12;
                        callback.pageCount((int) Math.ceil(resultCount / pagesize));
                    }
                    List<SearchBean> list = new ArrayList<>();
                    for (int i=0;i < result.size(); i++) {
                        list.add(
                                new SearchBean(
                                        result.get(i).select("h2.post-title").text(),
                                        result.get(i).select("a").attr("href"),
                                        result.get(i).select("img").attr("src"),
                                        result.get(i).select("p").text()
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
