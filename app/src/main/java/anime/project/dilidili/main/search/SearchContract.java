package anime.project.dilidili.main.search;

public interface SearchContract {
    void getData(String title, int page, boolean isMain, SearchModel.LoadDataCallback callback);
}
