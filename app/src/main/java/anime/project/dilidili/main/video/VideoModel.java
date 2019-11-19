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

import anime.project.dilidili.bean.AnimeDescBean;
import anime.project.dilidili.config.AnimeType;
import anime.project.dilidili.database.DatabaseUtil;
import anime.project.dilidili.net.HttpGet;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class VideoModel implements VideoContract.Model {
    private final static String BAN = "BAN";
    private final static Pattern BAN_IP = Pattern.compile("禁止ip");
    private final static Pattern SCRIPT_PATTERN = Pattern.compile("sourceUrl = (.*?);");
    private final static Pattern NEW_PATTERN = Pattern.compile("Url = (.*?);");
    private final static Pattern WATCH_PATTERN = Pattern.compile("\\/[0-9]+");
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
                Matcher m = WATCH_PATTERN.matcher(HTML_url);
                while (m.find()) {
                    String url = m.group().replace("/","");
                    DatabaseUtil.addIndex(fid, url);
                    break;
                }
                callback.successDrama(getAllDrama(fid, doc.select("div.aside_cen2 > div.con24 >a"), HTML_url));
                Elements script = doc.select("script");
                //第一种方式
                videoUrl = getSourceUrl(script);
                if (videoUrl.equals(BAN)) {
                    callback.ban();
                }else {
                    if (videoUrl.isEmpty())
                        videoUrl = doc.getElementsByClass("player").select("a").attr("href");//尝试第二种方式[版权页面]
                    if (!videoUrl.isEmpty()) {
                        if (!Patterns.WEB_URL.matcher(videoUrl.replace(" ","")).matches()) callback.empty();
                        else callback.success(videoUrl);
                    } else callback.empty();
                }
            }
        });
    }

    private static List<AnimeDescBean> getAllDrama(String fid, Elements dramaList, String me) {
        List<AnimeDescBean> list = new ArrayList<>();
        try {
            String dataBaseDrama = DatabaseUtil.queryAllIndex(fid);
            String dramaTitle;
            String dramaUrl = "";
            for (int i = 0; i < dramaList.size(); i++) {
                String href = dramaList.get(i).attr("href");
                if (href.equals("javascript:void(0)")) href = me;
                Matcher m = WATCH_PATTERN.matcher(href);
                while (m.find()) {
                    dramaUrl = m.group().replace("/","");
                    break;
                }
                dramaTitle = dramaList.get(i).text();
                if (dataBaseDrama.contains(dramaUrl))
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
        String url = "";
        boolean hasBanIp = false;
        for (int i = 0; i < script.size(); i++) {
            Matcher m = BAN_IP.matcher(script.eq(i).html());
            while (m.find()) {
                hasBanIp = true;
                break;
            }
        }
        for (int i = 0; i < script.size(); i++) {
            Matcher m = SCRIPT_PATTERN.matcher(script.eq(i).html());
            while (m.find()) {
                url = m.group();
                url = url.substring(13, url.length());
                url = url.substring(0, url.length() - 2);
                break;
            }
        }
        if (url.isEmpty()) {
            //新版本解析方式
            for (int i = 0; i < script.size(); i++) {
                Matcher m = NEW_PATTERN.matcher(script.eq(i).html());
                while (m.find()) {
                    url = m.group();
                    url = url.substring(7, url.length());
                    url = url.substring(0, url.length() - 2);
                    break;
                }
            }
        }
        if (hasBanIp && url.isEmpty())
            return BAN;
        else {
            Log.e("视频播放地址", url);
            return url;
        }
    }
}
