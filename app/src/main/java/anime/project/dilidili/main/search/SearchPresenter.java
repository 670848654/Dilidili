package anime.project.dilidili.main.search;

import java.util.List;

import anime.project.dilidili.bean.SearchBean;
import anime.project.dilidili.main.base.BasePresenter;
import anime.project.dilidili.main.base.Presenter;

public class SearchPresenter extends Presenter<SearchContract.View> implements BasePresenter,SearchContract.LoadDataCallback {
    private String title;
    private int page;
    private SearchContract.View view;
    private SearchModel model;

    public SearchPresenter(String title, int page, SearchContract.View view){
        super(view);
        this.title = title;
        this.page = page;
        this.view = view;
        model = new SearchModel();
    }

    @Override
    public void loadData(boolean isMain) {
        if (isMain)
        {
            view.showLoadingView();
            view.showEmptyVIew();
        }
        model.getData(title, page, isMain, this);
    }

    @Override
    public void success(boolean isMain, List<SearchBean> list) {
        view.showSuccessView(isMain, list);
    }

    @Override
    public void error(boolean isMain, String msg) {
        view.showErrorView(isMain, msg);
    }

    @Override
    public void pageCount(int pageCount) {
        view.getPageCount(pageCount);
    }

    @Override
    public void error(String msg) {

    }
}
