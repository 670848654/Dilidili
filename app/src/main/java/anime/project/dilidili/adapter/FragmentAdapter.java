package anime.project.dilidili.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import androidx.annotation.Nullable;
import anime.project.dilidili.R;
import anime.project.dilidili.api.Api;
import anime.project.dilidili.bean.HomeWekBean;
import anime.project.dilidili.util.Utils;

public class FragmentAdapter extends BaseQuickAdapter<HomeWekBean,BaseViewHolder> {

    public FragmentAdapter(@Nullable List<HomeWekBean> data) {
        super(R.layout.item_home_week, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, HomeWekBean item) {
        Utils.setImageVertical(item.getImg().startsWith("http") ? item.getImg() : Api.URL + item.getImg(),helper.getView(R.id.img));
        helper.setText(R.id.title, item.getTitle());
        helper.setText(R.id.drama, item.getDrama());
    }
}
