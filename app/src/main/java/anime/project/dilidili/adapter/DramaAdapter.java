package anime.project.dilidili.adapter;

import android.content.Context;

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
        helper.setText(R.id.tag_group, title);
        if (item.isSelect())
            helper.getView(R.id.tag_group).setBackground(context.getResources().getDrawable(R.drawable.button_selected,null));
        else
            helper.getView(R.id.tag_group).setBackground(context.getResources().getDrawable(R.drawable.button_default,null));
        if (item.getTitle().equals("没有资源"))
            helper.getView(R.id.tag_group).setEnabled(false);
        else
            helper.getView(R.id.tag_group).setEnabled(true);
    }
}
