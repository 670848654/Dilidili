package anime.project.dilidili.adapter;

import android.content.Context;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import anime.project.dilidili.R;
import anime.project.dilidili.application.DiliDili;
import anime.project.dilidili.bean.HomeWekBean;
import anime.project.dilidili.util.Utils;

public class FragmentAdapter extends BaseQuickAdapter<HomeWekBean,BaseViewHolder> {
    private Context context;
    public FragmentAdapter(Context context, List<HomeWekBean> data) {
        super(R.layout.item_home_week, data);
        this.context = context;
    }

    @Override
    protected void convert(BaseViewHolder helper, HomeWekBean item) {
        Utils.setCircleImage(context, item.getImg().startsWith("http") ? item.getImg() : DiliDili.URL + item.getImg(),helper.getView(R.id.img));
        helper.setText(R.id.title, item.getTitle());
        helper.setText(R.id.drama, item.getDrama());
        if (item.isHasNew()) helper.setVisible(R.id.new_img, true);
        else helper.setVisible(R.id.new_img, false);
    }
}
