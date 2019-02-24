package anime.project.dilidili.main.video;

import java.util.List;

import anime.project.dilidili.bean.AnimeDescBean;
import anime.project.dilidili.main.base.BaseLoadDataCallback;

public interface VideoContract {
    interface Model{
        void getData(String title, String url, LoadDataCallback callback);
    }

    interface View {
        void cancelDialog();
        void getVideoSuccess(String url);
        void getVideoEmpty();
        void getVideoError();
        void showSuccessDramaView(List<AnimeDescBean> list);
        void errorDramaView();
        void hasBanIp();
    }

    interface LoadDataCallback extends BaseLoadDataCallback {
        void ban();
        void success(String url);
        void error();
        void empty();
        void successDrama(List<AnimeDescBean> list);
    }
}
