package anime.project.dilidili.main.recommend;

import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.util.List;

import anime.project.dilidili.main.base.BaseLoadDataCallback;
import anime.project.dilidili.main.base.BaseView;

public interface RecommendContract {
    interface Model{
        void getData(LoadDataCallback callback);
    }

    interface View extends BaseView {
        void showSuccessView(List<MultiItemEntity> list);
    }

    interface LoadDataCallback extends BaseLoadDataCallback {
        void success(List<MultiItemEntity> list);
    }
}
