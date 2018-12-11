package anime.project.dilidili.main.tag;

import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.util.List;

import anime.project.dilidili.main.base.Base;
import anime.project.dilidili.main.base.BasePresenter;
import anime.project.dilidili.main.base.BaseView;

public class TagPresenter extends Base implements BasePresenter,TagModel.LoadDataCallback {
    private TagView tagView;
    private TagModel model;

    public TagPresenter(BaseView baseView, TagView tagView){
        this.baseView = baseView;
        this.tagView = tagView;
        model = new TagModel();
    }

    @Override
    public void loadData(boolean isMain) {
        if (isMain){
            baseView.showLoadingView();
            baseView.showEmptyVIew();
        }
        model.getData(this);
    }

    @Override
    public void success(List<MultiItemEntity> list) {
        tagView.showSuccessView(list);
    }

    @Override
    public void error(String msg) {
        baseView.showLoadErrorView(msg);
    }
}
