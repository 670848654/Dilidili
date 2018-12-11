package anime.project.dilidili.bean;

import android.content.Context;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import androidx.annotation.Nullable;
import anime.project.dilidili.R;

/**
 * Created by Administrator on 2018/1/24.
 */

public class DramaAdapter extends BaseQuickAdapter<AnimeDescBean, BaseViewHolder> {
    private Context context;
    public DramaAdapter(Context context, @Nullable List<AnimeDescBean> data) {
        super(R.layout.item_btn, data);
        this.context = context;
    }

    @Override
    protected void convert(final BaseViewHolder helper, AnimeDescBean item) {
        String title = item.getTitle();
        if(item.getType().equals("play")) {
            if (!title.equals("没有资源")){
                title = title.substring(1, title.length());
                title = title.substring(0,title.length() - 1);
            }
            helper.setText(R.id.tag_group, title);
        } else
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
