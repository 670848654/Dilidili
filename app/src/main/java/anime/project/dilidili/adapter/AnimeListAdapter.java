package anime.project.dilidili.adapter;

import android.content.Context;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import anime.project.dilidili.R;
import anime.project.dilidili.bean.AnimeListBean;
import anime.project.dilidili.util.Utils;

public class AnimeListAdapter extends BaseQuickAdapter<AnimeListBean, BaseViewHolder> {
    public AnimeListAdapter(List list) {
        super(R.layout.item_anime, list);
    }

    @Override
    protected void convert(BaseViewHolder helper, AnimeListBean item) {
        Utils.setImageVertical(item.getImg(),helper.getView(R.id.img));
        helper.setText(R.id.title, item.getTitle());
        helper.setText(R.id.region, item.getRegion());
        helper.setText(R.id.year, item.getYear());
        helper.setText(R.id.tag, item.getTag());
        helper.setText(R.id.play_count, item.getPlay_count() == null ? "制作：未知" : item.getPlay_count());
        helper.setText(R.id.show, item.getShow());
        helper.setText(R.id.state, item.getState());
    }
}
