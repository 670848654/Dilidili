package anime.project.dilidili.main.favorite;

import java.util.List;

import anime.project.dilidili.R;
import anime.project.dilidili.bean.AnimeListBean;
import anime.project.dilidili.database.DatabaseUtil;
import anime.project.dilidili.util.Utils;

public class FavoriteModel implements FavoriteContract.Model{

    @Override
    public void getData(FavoriteContract.LoadDataCallback callback) {
        List<AnimeListBean> list = DatabaseUtil.queryAllFavorite();
        if (list.size() > 0)
            callback.success(list);
        else
            callback.error(Utils.getString(R.string.empty_favorite));
    }
}
