package anime.project.dilidili.main.desc;

import com.chad.library.adapter.base.entity.MultiItemEntity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import anime.project.dilidili.R;
import anime.project.dilidili.application.DiliDili;
import anime.project.dilidili.bean.AnimeDescBean;
import anime.project.dilidili.bean.AnimeHeaderBean;
import anime.project.dilidili.bean.AnimeListBean;
import anime.project.dilidili.bean.DownBean;
import anime.project.dilidili.config.AnimeType;
import anime.project.dilidili.database.DatabaseUtil;
import anime.project.dilidili.net.HttpGet;
import anime.project.dilidili.util.Utils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class DescModel implements DescContract.Model {
    private String fid;
    private List<MultiItemEntity> list;
    private String dramaStr = "";

    @Override
    public void getData(String url, DescContract.LoadDataCallback callback) {
        new HttpGet(url, new Callback() {
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
                            Elements down = doc.getElementsByClass("time_pic").get(0).getElementsByClass("xfswiper3").select("ul.clear >li >a");
                            //ova或者其他
                            Elements playOva = doc.getElementsByClass("stitle").get(0).select("span >h2");
                            //推荐
                            Elements recommend = doc.getElementsByClass("swiper3").select("ul > li");
                            for (int i = 0; i < playDesc.size(); i++) {
                                String str = playDesc.get(i).text();
                                if (str.equals("在线")) {
                                    setData(play_list, "play");
                                } else if (str.equals("下载")) {
//                                    setData(str, down, "down");
                                    if (down.size() > 0) {
                                        List<DownBean> downList = new ArrayList<>();
                                        for (int j = 0; j < down.size(); j++) {
                                            if (!down.get(j).text().isEmpty()) {
                                                downList.add(
                                                        new DownBean(
                                                                down.get(j).text(),
                                                                down.get(j).attr("href")
                                                        )
                                                );
                                            }
                                        }
                                        callback.hasDown(downList);
                                    }
                                }
                            }
                            if (playOva.size() > 0) {
                                setDataOva(playOva, "ova");
                            }
                            if (recommend.size() > 0) {
                                setDataOther("相关推荐", recommend, "recommend");
                            }
                            callback.isFavorite(DatabaseUtil.checkFavorite(ainmeTitle));
                            callback.successMain(list);
                        } else {
                            callback.error(Utils.getString(R.string.no_playlist_error));
                        }
                    } else {
                        //解析失败
                        callback.error(Utils.getString(R.string.parsing_error));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    callback.error(e.getMessage());
                }
            }
        });
    }

    public void setData(Elements els, String type) {
        AnimeHeaderBean animeHeaderBean = new AnimeHeaderBean("在线");
        int k = 0;
        boolean select;
        for (int i = 0; i < els.size(); i++) {
            String name = els.get(i).select("a>em>span").text();
            String watchUrl = els.get(i).select("a").attr("href");
            if (!watchUrl.isEmpty()) {
                k++;
                watchUrl = watchUrl.substring(DiliDili.DOMAIN.length());
                if (dramaStr.contains(watchUrl))
                    select = true;
                else
                    select = false;
                animeHeaderBean.addSubItem(new AnimeDescBean(AnimeType.TYPE_LEVEL_1, select, name, els.get(i).select("a").attr("href"), type));
            }
        }
        if (k == 0)
            animeHeaderBean.addSubItem(new AnimeDescBean(AnimeType.TYPE_LEVEL_1, false, Utils.getString(R.string.no_resources), "", type));
        list.add(animeHeaderBean);
    }

    public void setDataOva(Elements els, String type) {
        AnimeHeaderBean animeHeaderBean = new AnimeHeaderBean("多季");
        for (int i = 0; i < els.size(); i++) {
            String str = els.get(i).select("a").text();
            if (!str.equals(""))
                animeHeaderBean.addSubItem(new AnimeDescBean(AnimeType.TYPE_LEVEL_3,false, str, els.get(i).select("a").attr("href"), type));
        }
        list.add(animeHeaderBean);
    }

    public void setDataOther(String title, Elements els, String type) {
        AnimeHeaderBean animeHeaderBean = new AnimeHeaderBean(title);
        for (int i = 0; i < els.size(); i++) {
            String str = els.get(i).text();
            if (!str.equals(""))
                animeHeaderBean.addSubItem(new AnimeDescBean(AnimeType.TYPE_LEVEL_2, els.get(i).select("p").text(), DiliDili.URL + els.get(i).select("a").attr("href"), els.get(i).select("img").attr("src"),type));
        }
        list.add(animeHeaderBean);
    }

}
