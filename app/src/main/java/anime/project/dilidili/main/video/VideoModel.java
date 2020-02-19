package anime.project.dilidili.main.video;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import anime.project.dilidili.application.DiliDili;
import anime.project.dilidili.bean.AnimeDescBean;
import anime.project.dilidili.config.AnimeType;
import anime.project.dilidili.database.DatabaseUtil;
import anime.project.dilidili.net.HttpGet;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class VideoModel implements VideoContract.Model {
    private final static Pattern NEW_PATTERN = Pattern.compile("player_data=(.*)");
    private String videoUrl;

    @Override
    public void getData(String title, String HTML_url, VideoContract.LoadDataCallback callback) {
        Log.e("HTML_url",HTML_url);
        new HttpGet(HTML_url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.error();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Document doc = Jsoup.parse(response.body().string());
                String fid = DatabaseUtil.getAnimeID(title);
                DatabaseUtil.addIndex(fid, HTML_url.replaceAll(DiliDili.DOMAIN, ""));
                callback.successDrama(getAllDrama(fid, doc.select("div.aside_cen2 > div.con24 >a"), HTML_url));
                Elements script = doc.select("script");
                // 新版本解析方式
                videoUrl = getSourceUrl(script);
                if (!videoUrl.isEmpty()) callback.success(videoUrl);
                else callback.empty();
            }
        });
    }

    private static List<AnimeDescBean> getAllDrama(String fid, Elements dramaList, String me) {
        List<AnimeDescBean> list = new ArrayList<>();
        try {
            String dataBaseDrama = DatabaseUtil.queryAllIndex(fid);
            String dramaTitle;
            for (int i = 0; i < dramaList.size(); i++) {
                String href = dramaList.get(i).attr("href").replaceAll(" ","");
                dramaTitle = dramaList.get(i).text();
                if (dataBaseDrama.contains(href))
                    list.add(new AnimeDescBean(AnimeType.TYPE_LEVEL_1, true, dramaTitle, href, "play"));
                else
                    list.add(new AnimeDescBean(AnimeType.TYPE_LEVEL_1, false, dramaTitle, href, "play"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return list;
        }
        return list;
    }

    /**
     * 获取视频源地址
     *
     * @param script
     */
    private static String getSourceUrl(Elements script) {
        String playerData = "";
        for (int i = 0; i < script.size(); i++) {
            Matcher m = NEW_PATTERN.matcher(script.eq(i).html());
            while (m.find()) {
                playerData = m.group().replaceAll("player_data=", "");
                break;
            }
        }
        Log.e("playerDate", playerData);
        try {
            JSONObject obj = new JSONObject(playerData);
            return obj.getString("url");
        } catch (JSONException e) {
            return "";
        }
    }
}
