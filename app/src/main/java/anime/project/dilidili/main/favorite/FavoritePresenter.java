package anime.project.dilidili.main.favorite;

import java.util.List;

import anime.project.dilidili.main.base.Base;
import anime.project.dilidili.main.base.BasePresenter;
import anime.project.dilidili.main.base.BaseView;
import anime.project.dilidili.bean.AnimeListBean;

public class FavoritePresenter extends Base implements BasePresenter,FavoriteModel.LoadDataCallback {
    private FavoriteView favoriteView;
    private FavoriteModel model;

    public FavoritePresenter(BaseView baseView, FavoriteView favoriteView){
        this.baseView = baseView;
        this.favoriteView = favoriteView;
        model = new FavoriteModel();
    }

    @Override
    public void loadData(boolean isMain) {
        if (isMain){
            baseView.showLoadingView();
            baseView.showEmptyVIew();
        }
        model.getData(this);
    }

    @Override
    public void success(List<AnimeListBean> list) {
        favoriteView.showSuccessView(list);
    }

    @Override
    public void error(String msg) {
        baseView.showLoadErrorView(msg);
    }
}
