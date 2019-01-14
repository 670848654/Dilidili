package anime.project.dilidili.adapter;

import android.content.Context;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import anime.project.dilidili.R;
import anime.project.dilidili.bean.AnimeListBean;
import anime.project.dilidili.util.Utils;

public class FavoriteListAdapter extends BaseQuickAdapter<AnimeListBean, BaseViewHolder> {
    private Context context;
    public FavoriteListAdapter(Context context, List list) {
        super(R.layout.item_favorite, list);
        this.context = context;
    }

    @Override
    protected void convert(BaseViewHolder helper, AnimeListBean item) {
        Utils.setDefaultImage(context, item.getImg(),helper.getView(R.id.img));
        helper.setText(R.id.title, item.getTitle());
    }
}
