package anime.project.dilidili.main.home;

import java.util.LinkedHashMap;

import anime.project.dilidili.main.base.Base;
import anime.project.dilidili.main.base.BasePresenter;
import anime.project.dilidili.main.base.BaseView;

public class HomePresenter extends Base implements BasePresenter,HomeModel.LoadDataCallback {
    private HomeView homeView;
    private HomeModel model;

    public HomePresenter(BaseView baseView, HomeView homeView){
        this.baseView = baseView;
        this.homeView = homeView;
        model = new HomeModel();
    }

    @Override
    public void loadData(boolean isMain) {
        if (isMain)
            baseView.showLoadingView();
        model.getData(this);
    }

    @Override
    public void success(LinkedHashMap map) {
        homeView.showLoadSuccess(map);
    }

    @Override
    public void error(String msg) {
        baseView.showLoadErrorView(msg);
    }
}

