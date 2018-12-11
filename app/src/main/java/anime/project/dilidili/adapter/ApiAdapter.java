package anime.project.dilidili.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import anime.project.dilidili.R;
import anime.project.dilidili.bean.ApiBean;

public class ApiAdapter extends BaseQuickAdapter<ApiBean, BaseViewHolder> {
    public ApiAdapter(List list) {
        super(R.layout.item_api, list);
    }

    @Override
    protected void convert(BaseViewHolder helper, ApiBean item) {
        helper.addOnClickListener(R.id.delete);
        helper.setText(R.id.title, item.getTitle());
        helper.setText(R.id.url, item.getUrl());
    }
}
