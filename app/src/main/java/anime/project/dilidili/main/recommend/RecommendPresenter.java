package anime.project.dilidili.main.recommend;

import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.util.List;

import anime.project.dilidili.main.base.Base;
import anime.project.dilidili.main.base.BasePresenter;
import anime.project.dilidili.main.base.BaseView;

public class RecommendPresenter extends Base implements BasePresenter,RecommendModel.LoadDataCallback {
    private RecommendView recommendView;
    private RecommendModel model;

    public RecommendPresenter(BaseView baseView, RecommendView recommendView){
        this.baseView = baseView;
        this.recommendView = recommendView;
        model = new RecommendModel();
    }

    @Override
    public void loadData(boolean isMain) {
        if (isMain)
        {
            baseView.showLoadingView();
            baseView.showEmptyVIew();
        }
        model.getData(this);
    }

    @Override
    public void success(List<MultiItemEntity> list) {
        recommendView.showSuccessView(list);
    }

    @Override
    public void error(String msg) {
        baseView.showLoadErrorView(msg);
    }
}
