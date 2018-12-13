package anime.project.dilidili.main.desc;

import android.content.Context;

import com.chad.library.adapter.base.entity.MultiItemEntity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import anime.project.dilidili.R;
import anime.project.dilidili.api.Api;
import anime.project.dilidili.database.DatabaseUtil;
import anime.project.dilidili.bean.AnimeDescBean;
import anime.project.dilidili.bean.AnimeHeaderBean;
import anime.project.dilidili.bean.AnimeListBean;
import anime.project.dilidili.config.AnimeType;
import anime.project.dilidili.net.OkHttpGet;
import anime.project.dilidili.util.Utils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class DescModel implements DescContract.Model {
    private String fid;
    private List<MultiItemEntity> list;
    private List<AnimeDescBean> drama;
    private String dramaStr = "";

    @Override
    public void getData(Context context, String url, DescContract.LoadDataCallback callback) {
        new OkHttpGet(url, 10, 20, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.error(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) {
                try {
                    Document doc = Jsoup.parse(response.body().string());
                    Elements detail = doc.getElementsByClass("detail");
                    list = new ArrayList<>();
                    //新版解析方案
                    if (detail.size() > 0) {
                        AnimeListBean bean = new AnimeListBean();
                        String ainmeTitle = detail.get(0).select("h1").text();
                        bean.setImg(detail.get(0).select("img").attr("src"));
                        bean.setTitle(ainmeTitle);
                        //创建index
                        DatabaseUtil.addAnime(ainmeTitle);
                        fid = DatabaseUtil.getAnimeID(ainmeTitle);
                        dramaStr = DatabaseUtil.queryAllIndex(fid);
                        Elements desc1 = detail.get(0).getElementsByClass("d_label");
                        Elements desc2 = detail.get(0).getElementsByClass("d_label2");
                        bean.setUrl(url);
                        bean.setRegion(desc1.get(0).text());
                        bean.setYear(desc1.get(1).text());
                        bean.setTag(desc1.get(2).text());
                        bean.setState(desc1.get(3).text());
                        bean.setShow(desc2.get(0).text());
                        bean.setPlay_count("播放：未统计");
//                        desc_t = desc2.get(1).text();
                        callback.successDesc(bean);
                        Elements playDesc = doc.getElementsByClass("stitle").get(0).select("span >a");
                        Elements play = doc.getElementsByClass("time_pic");
                        if (play.size() > 0) {
                            //分集
                            Elements play_list = doc.getElementsByClass("time_pic").get(0).getElementsByClass("swiper-slide").select("ul.clear >li");
                            //下载
                            Elements down = doc.getElementsByClass("time_pic").get(0).getElementsByClass("xfswiper3").select("ul.clear >li");
                            //ova或者其他
                            Elements playOva = doc.getElementsByClass("stitle").get(0).select("span >h2");
                            //推荐
                            Elements recommend = doc.getElementsByClass("swiper3").select("ul > li");
                            for (int i = 0; i < playDesc.size(); i++) {
                                String str = playDesc.get(i).text();
                                if (str.equals("在线")) {
                                    drama = new ArrayList<>();
                                    setData(context, str, play_list, "play");
                                } else if (str.equals("下载")) {
                                    setData(context, str, down, "down");
                                }
                            }
                            if (playOva.size() > 0) {
                                setDataOva(playOva, "html");
                            }
                            if (recommend.size() > 0) {
                                setDataOther("相关推荐", recommend, "html");
                            }
                            callback.isFavorite(DatabaseUtil.checkFavorite(ainmeTitle));
                            callback.successDrama(drama);
                            callback.successMain(list);
                        } else {
                            callback.error("根据国家相关部门规定\n本动画不准上架");
                        }
                    } else {
                        //解析失败
                        callback.error("解析方法不适用此番\n等待修复(也许)");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    callback.error("解析方法失效,等待更新");
                }
            }
        });
    }

    public void setData(Context context, String title, Elements els, String type) {
        AnimeHeaderBean animeHeaderBean = new AnimeHeaderBean(title);
        int k = 0;
        boolean select;
        switch (title) {
            case "在线":
                for (int i = 0; i < els.size(); i++) {
                    String name = els.get(i).select("a>em>span").text();
                    String watchUrl = els.get(i).select("a").attr("href");
                    if (!watchUrl.isEmpty()) {
                        k++;
                        if (dramaStr.contains(watchUrl))
                            select = true;
                        else
                            select = false;
                        animeHeaderBean.addSubItem(new AnimeDescBean(AnimeType.TYPE_LEVEL_1, select, name, els.get(i).select("a").attr("href"), type));
                        drama.add(new AnimeDescBean(AnimeType.TYPE_LEVEL_1, select, name, els.get(i).select("a").attr("href"), type));
                    }
                }
                if (k == 0)
                    animeHeaderBean.addSubItem(new AnimeDescBean(AnimeType.TYPE_LEVEL_1, false, Utils.getString(context, R.string.no_resources), "", type));
                break;
            case "下载":
                for (int i = 0; i < els.size(); i++) {
                    String str = els.get(i).select("a").text();
                    if (!str.equals("")) {
                        k++;
                        animeHeaderBean.addSubItem(new AnimeDescBean(AnimeType.TYPE_LEVEL_1, false, str, els.get(i).select("a").attr("href"), type));
                    }
                }
                if (k == 0)
                    animeHeaderBean.addSubItem(new AnimeDescBean(AnimeType.TYPE_LEVEL_1, false, Utils.getString(context, R.string.no_resources), "", type));
                break;
        }
        list.add(animeHeaderBean);
    }

    public void setDataOva(Elements els, String type) {
        AnimeHeaderBean animeHeaderBean = new AnimeHeaderBean("该番剧相关");
        for (int i = 0; i < els.size(); i++) {
            String str = els.get(i).select("a").text();
            if (!str.equals(""))
                animeHeaderBean.addSubItem(new AnimeDescBean(AnimeType.TYPE_LEVEL_1,false, str, els.get(i).select("a").attr("href"), type));
        }
        list.add(animeHeaderBean);
    }

    public void setDataOther(String title, Elements els, String type) {
        AnimeHeaderBean animeHeaderBean = new AnimeHeaderBean(title);
        for (int i = 0; i < els.size(); i++) {
            String str = els.get(i).text();
            if (!str.equals(""))
                animeHeaderBean.addSubItem(new AnimeDescBean(AnimeType.TYPE_LEVEL_2, els.get(i).select("p").text(), Api.URL + els.get(i).select("a").attr("href"), els.get(i).select("img").attr("src"),type));
        }
        list.add(animeHeaderBean);
    }

}
