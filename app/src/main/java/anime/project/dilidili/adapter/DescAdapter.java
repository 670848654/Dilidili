package anime.project.dilidili.adapter;

import android.content.Context;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.util.List;

import anime.project.dilidili.R;
import anime.project.dilidili.bean.AnimeDescBean;
import anime.project.dilidili.bean.AnimeHeaderBean;
import anime.project.dilidili.config.AnimeType;
import anime.project.dilidili.util.Utils;

public class DescAdapter extends BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder> {
    private Context context;

    public DescAdapter(List data, Context context) {
        super(data);
        this.context = context;
        addItemType(AnimeType.TYPE_LEVEL_0, R.layout.item_head);
        addItemType(AnimeType.TYPE_LEVEL_1, R.layout.item_btn);
        addItemType(AnimeType.TYPE_LEVEL_2, R.layout.item_down);
        addItemType(AnimeType.TYPE_LEVEL_3, R.layout.item_favorite);
    }

    @Override
    protected void convert(final BaseViewHolder helper, MultiItemEntity item) {
        helper.addOnClickListener(R.id.down).addOnLongClickListener(R.id.down);
        switch (helper.getItemViewType()) {
            case AnimeType.TYPE_LEVEL_0:
                final AnimeHeaderBean mainHeaderBean = (AnimeHeaderBean) item;
                helper.setText(R.id.header, mainHeaderBean.getTitle()).setImageResource(R.id.arrow, mainHeaderBean.isExpanded() ? R.drawable.ic_keyboard_arrow_down_white_48dp : R.drawable.baseline_keyboard_arrow_right_white_48dp);
                helper.itemView.setOnClickListener(v -> {
                    int pos = helper.getAdapterPosition();
                    if (mainHeaderBean.isExpanded()) {
                        collapse(pos);
                    } else {
                        expand(pos);
                    }
                });
                break;
            case AnimeType.TYPE_LEVEL_1:
                final AnimeDescBean animeDescBean = (AnimeDescBean) item;
                String title = animeDescBean.getTitle();
                if (animeDescBean.getType().equals("play")) {
                    if (!title.equals(Utils.getString(R.string.no_resources))) {
                        title = title.substring(1, title.length());
                        title = title.substring(0, title.length() - 1);
                    }
                    helper.setText(R.id.tag_group, title);
                } else
                    helper.setText(R.id.tag_group, title);
                if (animeDescBean.isSelect())
                    helper.getView(R.id.tag_group).setBackground(context.getResources().getDrawable(R.drawable.button_selected, null));
                else
                    helper.getView(R.id.tag_group).setBackground(context.getResources().getDrawable(R.drawable.button_default, null));
                if (animeDescBean.getTitle().equals(Utils.getString(R.string.no_resources)))
                    helper.getView(R.id.tag_group).setEnabled(false);
                else
                    helper.getView(R.id.tag_group).setEnabled(true);
                break;
            case AnimeType.TYPE_LEVEL_2:
                final AnimeDescBean animeDescBean2 = (AnimeDescBean) item;
                helper.setText(R.id.down, animeDescBean2.getTitle());
                break;
            case AnimeType.TYPE_LEVEL_3:
                final AnimeDescBean bean = (AnimeDescBean) item;
                helper.setText(R.id.title, bean.getTitle());
                Utils.setImageVertical(bean.getImg(), helper.getView(R.id.img));
                break;
        }
    }
}
