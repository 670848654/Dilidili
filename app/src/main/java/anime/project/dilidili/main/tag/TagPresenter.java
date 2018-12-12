package anime.project.dilidili.main.tag;

import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.util.List;

import anime.project.dilidili.main.base.BasePresenter;
import anime.project.dilidili.main.base.Presenter;

public class TagPresenter extends Presenter<TagContract.View> implements BasePresenter,TagContract.LoadDataCallback {
    private TagContract.View view;
    private TagModel model;

    public TagPresenter(TagContract.View view){
        super(view);
        this.view = view;
        model = new TagModel();
    }

    @Override
    public void loadData(boolean isMain) {
        if (isMain){
            view.showLoadingView();
            view.showEmptyVIew();
        }
        model.getData(this);
    }

    @Override
    public void success(List<MultiItemEntity> list) {
        view.showSuccessView(list);
    }

    @Override
    public void error(String msg) {
        view.showLoadErrorView(msg);
    }
}
