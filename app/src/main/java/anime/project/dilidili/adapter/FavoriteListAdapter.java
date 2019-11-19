package anime.project.dilidili.adapter;

import android.content.Context;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import anime.project.dilidili.R;
import anime.project.dilidili.application.DiliDili;
import anime.project.dilidili.bean.AnimeListBean;
import anime.project.dilidili.util.Utils;

public class FavoriteListAdapter extends BaseQuickAdapter<AnimeListBean, BaseViewHolder> {
    private Context context;
    public FavoriteListAdapter(Context context, List<AnimeListBean> list) {
        super(R.layout.item_favorite, list);
        this.context = context;
    }

    @Override
    protected void convert(BaseViewHolder helper, AnimeListBean item) {
        String img = item.getImg();
        Utils.setCardDefaultBg(context, helper.getView(R.id.card_view), helper.getView(R.id.title));
        if (img.contains("http://www.dilidili.wang")) {
            img = img.replace("http://www.dilidili.wang", DiliDili.DOMAIN);
            Utils.setDefaultImage(context, img, helper.getView(R.id.img));
            Utils.setCardBg(context, img, helper.getView(R.id.card_view), helper.getView(R.id.title));
        }else {
            Utils.setDefaultImage(context, DiliDili.DOMAIN + item.getImg(), helper.getView(R.id.img));
            Utils.setCardBg(context, DiliDili.DOMAIN + item.getImg(), helper.getView(R.id.card_view), helper.getView(R.id.title));
        }
        helper.setText(R.id.title, item.getTitle());

    }
}
