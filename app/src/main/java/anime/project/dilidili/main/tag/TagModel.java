package anime.project.dilidili.main.tag;

import com.chad.library.adapter.base.entity.MultiItemEntity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import anime.project.dilidili.api.Api;
import anime.project.dilidili.bean.HomeBean;
import anime.project.dilidili.bean.HomeHeaderBean;
import anime.project.dilidili.net.OkHttpGet;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class TagModel implements TagContract.Model{
    private List<MultiItemEntity> list = new ArrayList<>();

    @Override
    public void getData(TagContract.LoadDataCallback callback) {
        new OkHttpGet(Api.TAG_API, 10, 20, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.error(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) {
                try {
                    Document doc = Jsoup.parse(response.body().string());
                    Elements container = doc.getElementsByClass("nianfan");
                    if (container.size() > 0) {
                        for (int i = 0; i < container.size(); i++) {
                            Elements year = container.get(i).select("a");
                            String name = year.get(0).text();
                            if (!name.equals("00年代") && !name.equals("日本") && !name.equals("TV版")) {
                                setData(name.substring(0, name.length() - 1), year);
                            }
                        }
                        setZeroData();
                        setRegionData();
                        setModel();
                        String tagTitle = doc.getElementsByClass("tag-list").get(5).select("span").text();
                        Elements tag = doc.getElementsByClass("tag-list").get(5).select("a");
                        setDataTag(tagTitle.substring(0, tagTitle.length() - 1), tag);
                        Elements AzTag = doc.getElementsByClass("zmfl").get(0).select("ul >li");
                        setA_Z("动画头字母索引", AzTag);
                        callback.success(list);
                    } else {
                        //解析失败
                        callback.error("解析方法失效,等待更新");
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    callback.error(e.getMessage());
                }

            }
        });
    }

    public void setData(String title, Elements els) {
        HomeHeaderBean homeHeaderBean = new HomeHeaderBean(title);
        for (int j = 0; j < els.size(); j++) {
            if (j != 0){
                HomeBean homeBean = new HomeBean( els.get(j).text(),
                        Api.URL + els.get(j).attr("href"),
                        title + " - ",
                        true);
                homeHeaderBean.addSubItem(homeBean);
            }
        }
        list.add(homeHeaderBean);
    }

    public void setZeroData() {
        HomeHeaderBean homeHeaderBean = new HomeHeaderBean("更多");
        homeHeaderBean.addSubItem(new HomeBean("00年代", "http://www.dilidili.wang/anime/2010xq/", "更多 - ", true));
        homeHeaderBean.addSubItem(new HomeBean("更早", "http://www.dilidili.wang/anime/2000xqq/", "更多 - ", true));
        list.add(homeHeaderBean);
    }

    public void setRegionData() {
        HomeHeaderBean homeHeaderBean = new HomeHeaderBean("地区");
        homeHeaderBean.addSubItem(new HomeBean("日本", "http://www.dilidili.wang/riyu/", "地区 - ", true));
        homeHeaderBean.addSubItem(new HomeBean("中国", "http://www.dilidili.wang/guoyu/", "地区 - ", true));
        homeHeaderBean.addSubItem(new HomeBean("欧美", "http://www.dilidili.wang/yingyu/", "地区 - ", true));
        homeHeaderBean.addSubItem(new HomeBean("港台", "http://www.dilidili.wang/yueyu/", "地区 - ", true));
        list.add(homeHeaderBean);
    }

    public void setModel() {
        HomeHeaderBean homeHeaderBean = new HomeHeaderBean("播放方式");
        homeHeaderBean.addSubItem(new HomeBean("TV版", "http://www.dilidili.wang/tvdh/", "播放方式 - ", true));
        homeHeaderBean.addSubItem(new HomeBean("剧场版", "http://www.dilidili.wang/jcdh/", "播放方式 - ", true));
        homeHeaderBean.addSubItem(new HomeBean("独立电影", "http://www.dilidili.wang/independentfilm/", "播放方式 - ", true));
        list.add(homeHeaderBean);
    }

    public void setDataTag(String title, Elements tag) {
        HomeHeaderBean homeHeaderBean = new HomeHeaderBean(title);
        homeHeaderBean.addSubItem(new HomeBean("肉番", "http://www.dilidili.wang/roufan/", title + " - ", true));
        for (int i = 0; i < tag.size(); i++) {
            if (!tag.get(i).text().equals(""))
                homeHeaderBean.addSubItem(new HomeBean(tag.get(i).text(), Api.URL + tag.get(i).attr("href"), title + " - ", true));
        }
        homeHeaderBean.addSubItem(new HomeBean("日剧", "http://www.dilidili.wang/anime/riju/", title + " - ", true));
        list.add(homeHeaderBean);
    }

    public void setA_Z(String title, Elements tag) {
        HomeHeaderBean homeHeaderBean = new HomeHeaderBean(title);
        for (int i = 0; i < tag.size(); i++) {
            if (!tag.get(i).select("a").text().equals(""))
                homeHeaderBean.addSubItem(new HomeBean(tag.get(i).select("a").text(), tag.get(i).select("a").attr("href"), title + " - ", true));
        }
        list.add(homeHeaderBean);
    }
}
