package anime.project.dilidili.main.animelist;

import java.util.List;

import anime.project.dilidili.main.base.Base;
import anime.project.dilidili.main.base.BasePresenter;
import anime.project.dilidili.main.base.BaseView;
import anime.project.dilidili.bean.AnimeListBean;

public class AnimeListPresenter extends Base implements BasePresenter,AnimeListModel.LoadDataCallback {
    private String url;
    private AnimeListView animeListView;
    private AnimeListModel model;

    public AnimeListPresenter(String url, AnimeListView animeListView, BaseView baseView){
        this.url = url;
        this.animeListView = animeListView;
        this.baseView = baseView;
        model = new AnimeListModel();
    }

    @Override
    public void loadData(boolean isMain) {
        if (isMain)
            baseView.showLoadingView();
        baseView.showEmptyVIew();
        model.getData(url, this);
    }

    @Override
    public void success(List<AnimeListBean> list) {
        animeListView.showSuccessView(list);
    }

    @Override
    public void error(String msg) {
        baseView.showLoadErrorView(msg);
    }
}
