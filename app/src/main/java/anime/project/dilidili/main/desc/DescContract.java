package anime.project.dilidili.main.desc;

import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.util.List;

import anime.project.dilidili.bean.AnimeListBean;
import anime.project.dilidili.bean.DownBean;
import anime.project.dilidili.main.base.BaseLoadDataCallback;
import anime.project.dilidili.main.base.BaseView;

public interface DescContract {
    interface Model {
        void getData(String url, LoadDataCallback callback);
    }

    interface View extends BaseView {
        void showSuccessMainView(List<MultiItemEntity> list);
        void showSuccessDescView(AnimeListBean bean);
        void showSuccessFavorite(boolean favorite);
        void showDownView(List<DownBean> list);
    }

    interface LoadDataCallback extends BaseLoadDataCallback {
        void successMain(List<MultiItemEntity> list);
        void successDesc(AnimeListBean bean);
        void isFavorite(boolean favorite);
        void hasDown(List<DownBean> list);
    }
}
