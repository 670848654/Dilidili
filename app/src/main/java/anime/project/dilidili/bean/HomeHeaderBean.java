package anime.project.dilidili.bean;

import com.chad.library.adapter.base.entity.AbstractExpandableItem;
import com.chad.library.adapter.base.entity.MultiItemEntity;

import anime.project.dilidili.adapter.HomeAdapter;

public class HomeHeaderBean extends AbstractExpandableItem<HomeBean> implements MultiItemEntity {
    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public HomeHeaderBean(String title){
        this.title = title;
    }

    @Override
    public int getLevel() {
        return HomeAdapter.TYPE_LEVEL_0;
    }

    @Override
    public int getItemType() {
        return 0;
    }
}
