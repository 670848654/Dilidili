package anime.project.dilidili.adapter;

import android.content.Context;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import anime.project.dilidili.R;
import anime.project.dilidili.bean.AnimeListBean;
import anime.project.dilidili.util.Utils;

public class AnimeListAdapter extends BaseQuickAdapter<AnimeListBean, BaseViewHolder> {
    private Context context;

    public AnimeListAdapter(Context context, List list) {
        super(R.layout.item_anime, list);
        this.context = context;
    }

    @Override
    protected void convert(BaseViewHolder helper, AnimeListBean item) {
        Utils.setDefaultImage(context, item.getImg(), helper.getView(R.id.img));
        helper.setText(R.id.title, item.getTitle());
        helper.setText(R.id.region, item.getRegion());
        helper.setText(R.id.year, item.getYear());
        helper.setText(R.id.tag, item.getTag());
        helper.setText(R.id.play_count, item.getPlay_count().isEmpty() || item.getPlay_count().equals(Utils.getString(R.string.project_msg)) ? Utils.getString(R.string.no_project_msg) : item.getPlay_count());
        helper.setText(R.id.show, item.getShow().isEmpty() || item.getShow().equals(Utils.getString(R.string.eye_msg)) ? Utils.getString(R.string.no_eye_msg) : item.getShow());
        helper.setText(R.id.state, item.getState());
    }
}
