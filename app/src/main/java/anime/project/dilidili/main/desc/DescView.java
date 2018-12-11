package anime.project.dilidili.main.desc;

import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.util.List;

import anime.project.dilidili.bean.AnimeDescBean;
import anime.project.dilidili.bean.AnimeListBean;

public interface DescView {
    void showSuccessMainView(List<MultiItemEntity> list);
    void showSuccessDramaView(List<AnimeDescBean> list);
    void showSuccessDescView(AnimeListBean bean);
    void showSuccessFavorite(boolean favorite);
}
