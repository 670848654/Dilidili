package anime.project.dilidili.main.animelist;

import java.util.List;

import anime.project.dilidili.bean.AnimeListBean;
import anime.project.dilidili.main.base.BaseLoadDataCallback;
import anime.project.dilidili.main.base.BaseView;

public interface AnimeListContract {
    interface Model{
        void getData( String url, LoadDataCallback callback);
    }

    interface View extends BaseView{
        void showSuccessView(List<AnimeListBean> list);
    }

    interface LoadDataCallback extends BaseLoadDataCallback {
        void success(List<AnimeListBean> list);
    }
}
