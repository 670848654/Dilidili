package anime.project.dilidili.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import anime.project.dilidili.R;
import anime.project.dilidili.bean.AnimeListBean;
import anime.project.dilidili.util.Utils;

public class FavoriteListAdapter extends BaseQuickAdapter<AnimeListBean, BaseViewHolder> {
    public FavoriteListAdapter(List list) {
        super(R.layout.item_favorite, list);
    }

    @Override
    protected void convert(BaseViewHolder helper, AnimeListBean item) {
        Utils.setImageVertical(item.getImg(),helper.getView(R.id.img));
        helper.setText(R.id.title, item.getTitle());
    }
}
