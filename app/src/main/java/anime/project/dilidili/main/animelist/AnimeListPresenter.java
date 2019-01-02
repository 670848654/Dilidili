package anime.project.dilidili.main.animelist;

import java.util.List;

import anime.project.dilidili.bean.AnimeListBean;
import anime.project.dilidili.main.base.BasePresenter;
import anime.project.dilidili.main.base.Presenter;

public class AnimeListPresenter extends Presenter<AnimeListContract.View> implements BasePresenter,AnimeListContract.LoadDataCallback {
    private String url;
    private AnimeListContract.View view;
    private AnimeListModel model;

    public AnimeListPresenter(String url, AnimeListContract.View view){
        super(view);
        this.url = url;
        this.view = view;
        model = new AnimeListModel();
    }

    @Override
    public void loadData(boolean isMain) {
        if (isMain)
            view.showLoadingView();
        view.showEmptyVIew();
        model.getData(url, this);
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
