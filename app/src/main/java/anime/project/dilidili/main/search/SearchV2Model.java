package anime.project.dilidili.main.search;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import anime.project.dilidili.api.Api;
import anime.project.dilidili.application.DiliDili;
import anime.project.dilidili.bean.SearchBean;
import anime.project.dilidili.net.HttpGet;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class SearchV2Model implements SearchV2Contract.Model {
    private final static String ONLY = "- 在线&下载 - 嘀哩嘀哩";
    @Override
    public void getData(String title, int page, boolean isMain, SearchV2Contract.LoadDataCallback callback) {

        new HttpGet(String.format(Api.BAIDU_SEARCH_API, title, page, DiliDili.DOMAIN), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.error(isMain, e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Document doc = Jsoup.parse(response.body().string());
                Elements result = doc.select("div.result");
                String count = doc.select("span.support-text-top").text();
                if (result.size() > 0) {
                    int resultCount = Integer.parseInt(Pattern.compile("[^\\d]").matcher(count).replaceAll(""));
                    callback.pageCount((int) Math.ceil(resultCount / 10));
                    List<SearchBean> list = new ArrayList<>();
                    for (int i = 0; i < result.size(); i++) {
                        if (result.get(i).select("h3.c-title > a").text().contains(ONLY)) {
                            list.add(new SearchBean(
                                    result.get(i).select("h3.c-title > a").text().replaceAll(ONLY, ""),
                                    result.get(i).select("h3.c-title > a").attr("href"),
                                    result.get(i).select("div.c-abstract").text().replaceAll("<em>","").replaceAll("</em>","")
                            ));
                        }
                    }
                    callback.success(isMain, list);
                }else {
                    callback.error(isMain, "没有搜索到相关信息");
                }
            }
        });
    }
}
