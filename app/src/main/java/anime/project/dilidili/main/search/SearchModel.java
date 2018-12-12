package anime.project.dilidili.main.search;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import anime.project.dilidili.api.Api;
import anime.project.dilidili.bean.SearchBean;
import anime.project.dilidili.net.OkHttpPost;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Response;

public class SearchModel implements SearchContract {

    @Override
    public void getData(String title, int page, boolean isMain, LoadDataCallback callback) {
        FormBody body = new FormBody.Builder()
                .add("keywords", title)
                .add("pagesize", "10")
                .add("page", page + "").build();
        new OkHttpPost(Api.SEARCH_API, body, 10, 20, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.error(isMain, e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();
                try {
                    List<SearchBean> list = new ArrayList<>();
                    JSONObject object = new JSONObject(json);
                    callback.pageCount(object.getInt("count") - 1);
                    if (object.getString("result").equals("null")){
                        callback.error(isMain, "没有搜索到相关动漫");
                    }else {
                        JSONArray array = new JSONArray(object.getString("result"));
                        if (array.length() > 0){
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject obj = new JSONObject(array.getString(i));
                                if (!obj.getString("zhuangtai").isEmpty())
                                    list.add(
                                            new SearchBean(
                                                    obj.getString("typename"),
                                                    Api.URL + obj.getString("typedir"),
                                                    obj.getString("suoluetudizhi"),
                                                    "标签：" + obj.getString("biaoqian"),
                                                    "看点：" + obj.getString("description"),
                                                    "状态：" + obj.getString("zhuangtai")
                                            )
                                    );
                            }
                            callback.success(isMain, list);
                        }else
                            callback.error(isMain, "没有搜索到相关动漫");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    callback.error(isMain, e.getMessage());
                }
            }
        });
    }

    public interface LoadDataCallback {
        void success(boolean isMain, List<SearchBean> list);
        void error(boolean isMain, String msg);
        void pageCount(int pageCount);
    }
}
