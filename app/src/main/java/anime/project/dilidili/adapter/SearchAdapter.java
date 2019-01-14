package anime.project.dilidili.adapter;

import android.content.Context;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import anime.project.dilidili.R;
import anime.project.dilidili.bean.SearchBean;
import anime.project.dilidili.util.Utils;

public class SearchAdapter extends BaseQuickAdapter<SearchBean, BaseViewHolder> {
    private Context context;
    public SearchAdapter(Context context, List list) {
        super(R.layout.item_search, list);
        this.context = context;
    }

    @Override
    protected void convert(BaseViewHolder helper, SearchBean item) {
        Utils.setImageVertical(context, item.getImg(), helper.getView(R.id.img));
        helper.setText(R.id.title, item.getTitle());
        helper.setText(R.id.tags, item.getTags());
        helper.setText(R.id.desc, item.getDesc());
        helper.setText(R.id.state, item.getState());
    }
}
