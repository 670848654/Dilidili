package anime.project.dilidili.main.home;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.LinkedHashMap;

import anime.project.dilidili.R;
import anime.project.dilidili.application.DiliDili;
import anime.project.dilidili.net.HttpGet;
import anime.project.dilidili.util.Utils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class HomeModel implements HomeContract.Model {
    private static final String[] TABS = Utils.getArray(R.array.week_array);

    @Override
    public void getData(final HomeContract.LoadDataCallback callback) {
        new HttpGet(DiliDili.HOME_API, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.error(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) {
                try {
                    LinkedHashMap map = new LinkedHashMap();
                    JSONObject weekObj = new JSONObject();
                    Document body = Jsoup.parse(response.body().string());
                    Elements home = body.select("div.side >div.change >div.sldr >ul.wrp.animate");
                    if (home.size() > 0) {
                        Elements animeSub = body.getElementsByClass("top_menu").select("ul >li").get(1).select("a");
                        switch (animeSub.size()){
                            case 1:
                                map.put("url", animeSub.get(0).attr("href"));
                                map.put("title",  animeSub.get(0).text());
                                break;
                            case 2:
                                map.put("url", animeSub.get(1).attr("href"));
                                map.put("title",  animeSub.get(1).text());
                                break;
                        }
                        setDataToJson(TABS[0], home.select("li.elmnt-one >div.book.small >a"), weekObj);
                        setDataToJson(TABS[1], home.select("li.elmnt-two >div.book.small >a"), weekObj);
                        setDataToJson(TABS[2], home.select("li.elmnt-three >div.book.small >a"), weekObj);
                        setDataToJson(TABS[3], home.select("li.elmnt-four >div.book.small >a"), weekObj);
                        setDataToJson(TABS[4], home.select("li.elmnt-five >div.book.small >a"), weekObj);
                        setDataToJson(TABS[5], home.select("li.elmnt-six >div.book.small >a"), weekObj);
                        setDataToJson(TABS[6], home.select("li.elmnt-seven >div.book.small >a"), weekObj);
                        map.put("week", weekObj);
                        callback.success(map);
                    } else
                        callback.error("解析方法失效,等待更新");
                } catch (Exception e) {
                    e.printStackTrace();
                    callback.error("解析方法失效,等待更新");
                }
            }
        });
    }

    /**
     * 新番时间表
     *
     * @param title
     * @param els
     * @param jsonObject
     * @throws JSONException
     */
    public static void setDataToJson(String title, Elements els, JSONObject jsonObject) throws JSONException {
        JSONArray arr = new JSONArray();
        for (int i = 0, size = els.size(); i < size; i++) {
            JSONObject object = new JSONObject();
            object.put("title", els.get(i).select("img").attr("alt"));
            object.put("img", els.get(i).select("img").attr("src"));
            object.put("url", els.get(i).attr("href"));
            Elements arrs = els.get(i).select("figcaption").select("p");
            if (arrs.size() == 2) object.put("drama", arrs.get(1).text());
            else object.put("drama", "");
            if (!els.get(i).select("span").text().isEmpty()) object.put("new", true);
            else object.put("new", false);
            arr.put(object);
        }
        jsonObject.put(title, arr);
    }
}
