package anime.project.dilidili.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import anime.project.dilidili.R;
import anime.project.dilidili.bean.SourceBean;


public class SourceAdapter extends BaseQuickAdapter<SourceBean, BaseViewHolder> {

    public SourceAdapter(List<SourceBean> list) {
        super(R.layout.item_source, list);
    }

    @Override
    protected void convert(BaseViewHolder helper, SourceBean item) {
        helper.setText(R.id.title,item.getTitle());
        helper.setText(R.id.author,item.getAuthor());
        helper.setText(R.id.desc,item.getDesc());
    }
}
