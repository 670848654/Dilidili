package anime.project.dilidili.adapter;

import android.content.Context;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.util.List;

import anime.project.dilidili.R;
import anime.project.dilidili.bean.RecommendBean;
import anime.project.dilidili.bean.RecommendHeaderBean;
import anime.project.dilidili.config.RecommendType;
import anime.project.dilidili.util.Utils;

public class RecommendAdapter extends BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder> {
    private Context context;
    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    public RecommendAdapter(Context context , List<MultiItemEntity> data) {
        super(data);
        this.context = context;
        addItemType(RecommendType.TYPE_LEVEL_0, R.layout.item_head);
        addItemType(RecommendType.TYPE_LEVEL_1, R.layout.item_recommend);
    }

    @Override
    protected void convert(final BaseViewHolder helper, MultiItemEntity item) {
        helper.addOnClickListener(R.id.tag_group).addOnClickListener(R.id.witch);
        switch (helper.getItemViewType()) {
            case RecommendType.TYPE_LEVEL_0:
                final RecommendHeaderBean recommendHeaderBean = (RecommendHeaderBean) item;
                helper.setText(R.id.header, recommendHeaderBean.getTitle()).setImageResource(R.id.arrow, recommendHeaderBean.isExpanded() ? R.drawable.ic_keyboard_arrow_down_white_48dp : R.drawable.baseline_keyboard_arrow_right_white_48dp);
                helper.itemView.setOnClickListener(v -> {
                    int pos = helper.getAdapterPosition();
                    if (recommendHeaderBean.isExpanded()) {
                        collapse(pos);
                    } else {
                        expand(pos);
                    }
                });
                break;
            case RecommendType.TYPE_LEVEL_1:
                final RecommendBean recommendBean = (RecommendBean) item;
                helper.setText(R.id.title,recommendBean.getTitle());
                Utils.setImageVertical(context,recommendBean.getImg(), helper.getView(R.id.img));
                break;
        }
    }
}
