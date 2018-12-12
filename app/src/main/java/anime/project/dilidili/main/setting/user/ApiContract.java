package anime.project.dilidili.main.setting.user;

import java.util.List;

import anime.project.dilidili.bean.ApiBean;
import anime.project.dilidili.main.base.BaseLoadDataCallback;
import anime.project.dilidili.main.base.BaseView;

public interface ApiContract {
    interface Model{
        void getData(LoadDataCallback callback);
    }

    interface View extends BaseView {
        void showSuccess(List<ApiBean> list);
    }

    interface LoadDataCallback extends BaseLoadDataCallback {
        void success(List<ApiBean> list);
    }
}
