package anime.project.dilidili.adapter;

import android.content.Context;
import android.widget.Button;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import androidx.annotation.Nullable;
import anime.project.dilidili.R;
import anime.project.dilidili.bean.AnimeDescBean;

public class DramaAdapter extends BaseQuickAdapter<AnimeDescBean, BaseViewHolder> {
    private Context context;
    public DramaAdapter(Context context, @Nullable List<AnimeDescBean> data) {
        super(R.layout.item_btn, data);
        this.context = context;
    }

    @Override
    protected void convert(final BaseViewHolder helper, AnimeDescBean item) {
        String title = item.getTitle();
        Button btn = helper.getView(R.id.tag_group);
        helper.setText(R.id.tag_group, title);
        if (item.isSelect()) {
            helper.getView(R.id.tag_group).setBackgroundResource(R.drawable.button_selected);
            btn.setTextColor(context.getResources().getColor(R.color.item_selected_color));
        }         
        else {
            helper.getView(R.id.tag_group).setBackgroundResource(R.drawable.button_default);
            btn.setTextColor(context.getResources().getColor(R.color.text_color_primary));
        }
        if (item.getTitle().equals("没有资源"))
            helper.getView(R.id.tag_group).setEnabled(false);
        else
            helper.getView(R.id.tag_group).setEnabled(true);
    }
}
