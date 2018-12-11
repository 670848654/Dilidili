package anime.project.dilidili.main.desc;

import android.content.Context;

import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.util.List;

import anime.project.dilidili.main.base.Base;
import anime.project.dilidili.main.base.BasePresenter;
import anime.project.dilidili.main.base.BaseView;
import anime.project.dilidili.bean.AnimeDescBean;
import anime.project.dilidili.bean.AnimeListBean;

public class DescPresenter extends Base implements BasePresenter,DescModel.LoadDataCallback {
    private Context context;
    private String url;
    private DescView descView;
    private DescModel model;

    public DescPresenter(Context context, String url, BaseView baseView, DescView descView){
        this.context = context;
        this.url = url;
        this.baseView = baseView;
        this.descView = descView;
        model = new DescModel();
    }

    @Override
    public void loadData(boolean isMain) {
        if (isMain)
            baseView.showLoadingView();
        model.getData(context, url, this);
    }

    @Override
    public void successMain(List<MultiItemEntity> list) {
        descView.showSuccessMainView(list);
    }

    @Override
    public void successDrama(List<AnimeDescBean> list) {
        descView.showSuccessDramaView(list);
    }

    @Override
    public void successDesc(AnimeListBean bean) {
        descView.showSuccessDescView(bean);
    }

    @Override
    public void isFavorite(boolean favorite) {
        descView.showSuccessFavorite(favorite);
    }

    @Override
    public void error(String msg) {
        baseView.showLoadErrorView(msg);
    }
}
