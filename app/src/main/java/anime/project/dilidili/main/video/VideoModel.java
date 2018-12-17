package anime.project.dilidili.main.video;

import android.util.Log;
import android.util.Patterns;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import anime.project.dilidili.R;
import anime.project.dilidili.bean.AnimeDescBean;
import anime.project.dilidili.config.AnimeType;
import anime.project.dilidili.database.DatabaseUtil;
import anime.project.dilidili.net.OkHttpGet;
import anime.project.dilidili.util.Utils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class VideoModel implements VideoContract.Model {

    @Override
    public void getData(String title,String HTML_url, VideoContract.LoadDataCallback callback) {

        new OkHttpGet(HTML_url, 10, 20, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.error();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String videoUrl;
                Document doc = Jsoup.parse(response.body().string());
                String fid = DatabaseUtil.getAnimeID(title);
                DatabaseUtil.addIndex(fid, HTML_url);
                callback.successDrama(getAllDrama(fid,  doc.select("div.aside_cen2 > div.con24 >a")));
                Elements script = doc.select("script");
                //第一种方式
                videoUrl = getSourceUrl(script);
                if (videoUrl.isEmpty()) {
                    //尝试第二种方式[版权页面]
                    videoUrl = doc.getElementsByClass("player").select("a").attr("href");
                    if (!Patterns.WEB_URL.matcher(videoUrl).matches())
                        callback.empty();
                    else
                        callback.success(videoUrl);
                } else if (!Patterns.WEB_URL.matcher(videoUrl).matches())
                    callback.empty();
                else
                    callback.success(videoUrl);
            }
        });
    }

    public static List<AnimeDescBean> getAllDrama(String fid, Elements dramaList){
        List<AnimeDescBean> list = new ArrayList<>();
        try {
            String dataBaseDrama = DatabaseUtil.queryAllIndex(fid);
            String dramaTitle;
            String dramaUrl;
            for (int i = 0; i < dramaList.size(); i++) {
                dramaUrl = dramaList.get(i).attr("href");
                dramaTitle = dramaList.get(i).text();
                if (dataBaseDrama.contains(dramaUrl))
                    list.add(new AnimeDescBean(AnimeType.TYPE_LEVEL_1, true, dramaTitle, dramaUrl, "play"));
                else
                    list.add(new AnimeDescBean(AnimeType.TYPE_LEVEL_1, false, dramaTitle, dramaUrl, "play"));
            }
        }catch (Exception e){
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
    public static String getSourceUrl(Elements script) {
        String url = "";
        for (int i = 0; i < script.size(); i++) {
            Elements e = script.eq(i);
            String str = e.html();
            String reg = "sourceUrl = (.*?);";
            Pattern p = Pattern.compile(reg);
            Matcher m = p.matcher(str);
            while (m.find()) {
                url = m.group();
                url = url.substring(13, url.length());
                url = url.substring(0, url.length() - 2);
                break;
            }
        }
        if (url.isEmpty()){
            //新版本解析方式
            for (int i = 0; i < script.size(); i++) {
                Elements e = script.eq(i);
                String str = e.html();
                String reg = "Url = (.*?);";
                Pattern p = Pattern.compile(reg);
                Matcher m = p.matcher(str);
                while (m.find()) {
                    url = m.group();
                    url = url.substring(7, url.length());
                    url = url.substring(0, url.length() - 2);
                    break;
                }
            }
        }
        Log.e("url",url);
        return url;
    }
}
