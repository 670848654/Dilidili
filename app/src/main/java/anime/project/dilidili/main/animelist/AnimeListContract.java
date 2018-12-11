package anime.project.dilidili.main.animelist;

public interface AnimeListContract {
    //获取数据
    void getData( String url, AnimeListModel.LoadDataCallback callback);
}
