package anime.project.dilidili.adapter;

import android.view.View;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.util.List;

import anime.project.dilidili.R;
import anime.project.dilidili.bean.HomeBean;
import anime.project.dilidili.bean.HomeHeaderBean;

public class HomeAdapter extends BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder> {
    public static final int TYPE_LEVEL_0 = 0;
    public static final int TYPE_LEVEL_1 = 1;

    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    public HomeAdapter(List<MultiItemEntity> data) {
        super(data);
        addItemType(TYPE_LEVEL_0, R.layout.item_head);
        addItemType(TYPE_LEVEL_1, R.layout.item_home);
    }

    @Override
    protected void convert(final BaseViewHolder helper, MultiItemEntity item) {
        helper.addOnClickListener(R.id.tag_group).addOnClickListener(R.id.witch);
        switch (helper.getItemViewType()) {
            case TYPE_LEVEL_0:
                final HomeHeaderBean homeHeaderBean = (HomeHeaderBean) item;
                helper.setText(R.id.header, homeHeaderBean.getTitle()).setImageResource(R.id.arrow, homeHeaderBean.isExpanded() ? R.drawable.ic_keyboard_arrow_down_white_48dp : R.drawable.baseline_keyboard_arrow_right_white_48dp);
                helper.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int pos = helper.getAdapterPosition();
                        if (homeHeaderBean.isExpanded()) {
                            collapse(pos);
                        } else {
                            expand(pos);
                        }
                    }
                });
                break;
            case TYPE_LEVEL_1:
                final HomeBean homeBean = (HomeBean) item;
                helper.setText(R.id.tag_group, homeBean.getTitle());
                if (homeBean.getWitchTitle().isEmpty()) {
                    helper.getView(R.id.witch).setVisibility(View.GONE);
                } else {
                    helper.getView(R.id.witch).setVisibility(View.VISIBLE);
                    helper.setText(R.id.witch, homeBean.getWitchTitle());
                }
                break;
        }
    }
}
