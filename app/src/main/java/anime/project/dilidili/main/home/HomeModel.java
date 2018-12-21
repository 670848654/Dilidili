package anime.project.dilidili.main.home;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.LinkedHashMap;

import anime.project.dilidili.api.Api;
import anime.project.dilidili.net.HttpGet;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class HomeModel implements HomeContract.Model{

    @Override
    public void getData(final HomeContract.LoadDataCallback callback) {
        new HttpGet(Api.HOME_API, 10, 20, new Callback() {
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
                    Elements container = body.getElementsByClass("change");
                    if (container.size() > 0) {
                        map.put("url", body.getElementsByClass("top_menu").select("ul >li").get(1).select("a").get(0).attr("href"));
                        map.put("title", body.getElementsByClass("top_menu").select("ul >li").get(1).select("a").get(0).text());
                        setDataToJson("Monday", container.get(1).getElementsByClass("elmnt-one").select("ul >li"), weekObj);
                        setDataToJson("Tuesday", container.get(1).getElementsByClass("elmnt-two").select("ul >li"), weekObj);
                        setDataToJson("Wednesday", container.get(1).getElementsByClass("elmnt-three").select("ul >li"), weekObj);
                        setDataToJson("Thursday", container.get(1).getElementsByClass("elmnt-four").select("ul >li"), weekObj);
                        setDataToJson("Friday", container.get(1).getElementsByClass("elmnt-five").select("ul >li"), weekObj);
                        setDataToJson("Saturday", container.get(1).getElementsByClass("elmnt-six").select("ul >li"), weekObj);
                        setDataToJson("Sunday", container.get(1).getElementsByClass("elmnt-seven").select("ul >li"), weekObj);
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
     * @param title
     * @param els
     * @param jsonObject
     * @throws JSONException
     */
    public static void setDataToJson(String title, Elements els,JSONObject jsonObject) throws JSONException {
        JSONArray arr = new JSONArray();
        int size;
        for (int j = 0; j < els.size(); j++) {
            JSONObject object = new JSONObject();
            size = els.get(j).select("a").size();
            if (size > 1) {
                object.put("title", els.get(j).select("a").get(0).text());
                object.put("url", els.get(j).select("a").get(0).attr("href"));
                object.put("watchTitle", els.get(j).select("a").get(1).text());
                object.put("watchUrl", els.get(j).select("a").get(1).attr("href"));
            } else {
                object.put("title", els.get(j).select("a").text());
                object.put("url", els.get(j).select("a").attr("href"));
                object.put("watchTitle", "");
                object.put("watchUrl", "");
            }
            arr.put(object);
        }
        jsonObject.put(title, arr);
    }
}
