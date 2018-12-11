package anime.project.dilidili.main.search;

import java.util.List;

import anime.project.dilidili.main.base.Base;
import anime.project.dilidili.main.base.BasePresenter;
import anime.project.dilidili.main.base.BaseView;
import anime.project.dilidili.bean.SearchBean;

public class SearchPresenter extends Base implements BasePresenter,SearchModel.LoadDataCallback {
    private String title;
    private int page;
    private SearchView searchView;
    private SearchModel model;

    public SearchPresenter(String title, int page, BaseView baseView, SearchView searchView){
        this.title = title;
        this.page = page;
        this.baseView = baseView;
        this.searchView = searchView;
        model = new SearchModel();
    }

    @Override
    public void loadData(boolean isMain) {
        if (isMain)
        {
            baseView.showLoadingView();
            baseView.showEmptyVIew();
        }
        model.getData(title, page, isMain, this);
    }

    @Override
    public void success(boolean isMain, List<SearchBean> list) {
        searchView.showSuccessView(isMain, list);
    }

    @Override
    public void error(boolean isMain, String msg) {
        searchView.showErrorView(isMain, msg);
    }

    @Override
    public void pageCount(int pageCount) {
        searchView.getPageCount(pageCount);
    }
}
