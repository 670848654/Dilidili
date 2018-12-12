package anime.project.dilidili.main.search;

import java.util.List;

import anime.project.dilidili.bean.SearchBean;
import anime.project.dilidili.main.base.BaseLoadDataCallback;
import anime.project.dilidili.main.base.BaseView;

public interface SearchContract {
    interface Model{
        void getData(String title, int page, boolean isMain, LoadDataCallback callback);
    }

    interface View extends BaseView {
        void showSuccessView(boolean isMain, List<SearchBean> list);
        void showErrorView(boolean isMain, String msg);
        void getPageCount(int pageCount);
    }

    interface LoadDataCallback extends BaseLoadDataCallback {
        void success(boolean isMain, List<SearchBean> list);
        void error(boolean isMain, String msg);
        void pageCount(int pageCount);
    }
}
