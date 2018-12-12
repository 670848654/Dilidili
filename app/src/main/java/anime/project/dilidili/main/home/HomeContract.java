package anime.project.dilidili.main.home;

import java.util.LinkedHashMap;

import anime.project.dilidili.main.base.BaseLoadDataCallback;
import anime.project.dilidili.main.base.BaseView;

public interface HomeContract {
    interface Model{
        void getData(LoadDataCallback callback);
    }

    interface View extends BaseView {
        void showLoadSuccess(LinkedHashMap map);
    }

    interface LoadDataCallback extends BaseLoadDataCallback {
        void success(LinkedHashMap map);
    }
}
