package anime.project.dilidili.main.favorite;

import java.util.List;

import anime.project.dilidili.main.base.BasePresenter;
import anime.project.dilidili.bean.AnimeListBean;
import anime.project.dilidili.main.base.Presenter;

public class FavoritePresenter extends Presenter<FavoriteContract.View> implements BasePresenter,FavoriteContract.LoadDataCallback {
    private FavoriteContract.View view;
    private FavoriteModel model;

    public FavoritePresenter(FavoriteContract.View view){
        super(view);
        this.view = view;
        model = new FavoriteModel();
    }

    @Override
    public void loadData(boolean isMain) {
        if (isMain){
            view.showLoadingView();
            view.showEmptyVIew();
        }
        model.getData(this);
    }

    @Override
    public void success(List<AnimeListBean> list) {
        view.showSuccessView(list);
    }

    @Override
    public void error(String msg) {
        view.showLoadErrorView(msg);
    }
}
