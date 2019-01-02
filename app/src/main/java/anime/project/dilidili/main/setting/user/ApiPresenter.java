package anime.project.dilidili.main.setting.user;

import java.util.List;

import anime.project.dilidili.bean.ApiBean;
import anime.project.dilidili.main.base.BasePresenter;
import anime.project.dilidili.main.base.Presenter;

public class ApiPresenter extends Presenter<ApiContract.View> implements BasePresenter,ApiContract.LoadDataCallback {
    private ApiContract.View view;
    private ApiModel model;

    public ApiPresenter(ApiContract.View view){
        super(view);
        this.view = view;
        model = new ApiModel();
    }

    @Override
    public void loadData(boolean isMain) {
        if (isMain)
        {
            view.showLoadingView();
            view.showEmptyVIew();
        }
        model.getData(this);
    }

    @Override
    public void success(List<ApiBean> list) {
        view.showSuccess(list);
    }

    @Override
    public void error(String msg) {
        view.showLoadErrorView(msg);
    }
}
