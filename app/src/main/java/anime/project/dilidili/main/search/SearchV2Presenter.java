package anime.project.dilidili.main.search;

import java.util.List;

import anime.project.dilidili.bean.SearchBean;
import anime.project.dilidili.main.base.BasePresenter;
import anime.project.dilidili.main.base.Presenter;

public class SearchV2Presenter extends Presenter<SearchV2Contract.View> implements BasePresenter,SearchV2Contract.LoadDataCallback {
    private String title;
    private int page;
    private SearchV2Contract.View view;
    private SearchV2Model model;

    public SearchV2Presenter(String title, int page, SearchV2Contract.View view){
        super(view);
        this.title = title;
        this.page = page;
        this.view = view;
        model = new SearchV2Model();
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
