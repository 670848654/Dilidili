package anime.project.dilidili.adapter;

import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import androidx.annotation.Nullable;
import anime.project.dilidili.R;
import anime.project.dilidili.bean.HomeBean;

public class FragmentAdapter extends BaseQuickAdapter<HomeBean,BaseViewHolder> {

    public FragmentAdapter(@Nullable List<HomeBean> data) {
        super(R.layout.item_home, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, HomeBean item) {
        helper.addOnClickListener(R.id.tag_group).addOnClickListener(R.id.witch);
        helper.setText(R.id.tag_group, item.getTitle());
        if (item.getWitchTitle().isEmpty()) {
            helper.getView(R.id.witch).setVisibility(View.GONE);
        } else {
            helper.getView(R.id.witch).setVisibility(View.VISIBLE);
            helper.setText(R.id.witch, item.getWitchTitle());
        }
    }
}
