package anime.project.dilidili.main.favorite;

import java.util.List;

import anime.project.dilidili.bean.AnimeListBean;
import anime.project.dilidili.main.base.BaseLoadDataCallback;
import anime.project.dilidili.main.base.BaseView;

public interface FavoriteContract {
    interface Model{
        void getData(LoadDataCallback callback);
    }

    interface View extends BaseView {
        void showSuccessView(List<AnimeListBean> list);
    }

    interface LoadDataCallback extends BaseLoadDataCallback {
        void success(List<AnimeListBean> list);
    }

}
