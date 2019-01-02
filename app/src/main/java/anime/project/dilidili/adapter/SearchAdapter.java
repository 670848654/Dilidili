package anime.project.dilidili.adapter;

import android.content.Context;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import anime.project.dilidili.R;
import anime.project.dilidili.bean.SearchBean;
import anime.project.dilidili.util.Utils;

public class SearchAdapter extends BaseQuickAdapter<SearchBean, BaseViewHolder> {
    public SearchAdapter(List list) {
        super(R.layout.item_search, list);
    }

    @Override
    protected void convert(BaseViewHolder helper, SearchBean item) {
        Utils.setImageVertical(item.getImg(), helper.getView(R.id.img));
        helper.setText(R.id.title, item.getTitle());
        helper.setText(R.id.tags, item.getTags());
        helper.setText(R.id.desc, item.getDesc());
        helper.setText(R.id.state, item.getState());
    }
}
