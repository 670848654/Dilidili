package anime.project.dilidili.main.recommend;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import java.util.List;
import anime.project.dilidili.main.base.BasePresenter;
import anime.project.dilidili.main.base.Presenter;

public class RecommendPresenter extends Presenter<RecommendContract.View> implements BasePresenter,RecommendContract.LoadDataCallback {
    private RecommendContract.View view;
    private RecommendModel model;

    public RecommendPresenter(RecommendContract.View view){
        super(view);
        this.view = view;
        model = new RecommendModel();
    }

    @Override
    public void loadData(boolean isMain) {
        if (isMain)
        {
            view.showLoadingView();
            view.showEmptyVIew();
        }
        model.getData(this);
    }

    @Override
    public void success(List<MultiItemEntity> list) {
        view.showSuccessView(list);
    }

    @Override
    public void error(String msg) {
        view.showLoadErrorView(msg);
    }
}
