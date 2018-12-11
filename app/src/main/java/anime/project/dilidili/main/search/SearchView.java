package anime.project.dilidili.main.search;

import java.util.List;

import anime.project.dilidili.bean.SearchBean;

public interface SearchView {
    void showSuccessView(boolean isMain, List<SearchBean> list);
    void showErrorView(boolean isMain, String msg);
    void getPageCount(int pageCount);
}
