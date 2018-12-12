package anime.project.dilidili.main.video;

import anime.project.dilidili.main.base.BaseLoadDataCallback;

public interface VideoContract {
    interface Model{
        void getData(String title, String url, LoadDataCallback callback);
    }

    interface View {
        void getVideoSuccess(String url);
        void getVideoEmpty();
        void getVideoError();
    }

    interface LoadDataCallback extends BaseLoadDataCallback {
        void success(String url);
        void error();
        void empty();
    }
}
