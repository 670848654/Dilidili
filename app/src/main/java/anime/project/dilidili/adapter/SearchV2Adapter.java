package anime.project.dilidili.adapter;

import android.content.Context;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import anime.project.dilidili.R;
import anime.project.dilidili.bean.SearchBean;

public class SearchV2Adapter extends BaseQuickAdapter<SearchBean, BaseViewHolder> {
    private Context context;
    public SearchV2Adapter(Context context, List<SearchBean> list) {
        super(R.layout.item_search_v2, list);
        this.context = context;
    }

    @Override
    protected void convert(BaseViewHolder helper, SearchBean item) {
        helper.setText(R.id.title, item.getTitle());
        helper.setText(R.id.desc, item.getDesc());
    }
}
