package anime.project.dilidili.main.video;

public interface VideoContract {
    //获取数据
    void getData(String title, String url, VideoModel.LoadDataCallback callback);
}
