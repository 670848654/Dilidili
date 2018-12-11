package anime.project.dilidili.main.setting.user;

import java.util.List;

import anime.project.dilidili.main.base.Base;
import anime.project.dilidili.main.base.BasePresenter;
import anime.project.dilidili.main.base.BaseView;
import anime.project.dilidili.bean.ApiBean;

public class ApiPresenter extends Base implements BasePresenter,ApiModel.LoadDataCallback {
    private ApiView apiView;
    private ApiModel model;

    public ApiPresenter(BaseView baseView, ApiView apiView){
        this.baseView = baseView;
        this.apiView = apiView;
        model = new ApiModel();
    }

    @Override
    public void loadData(boolean isMain) {
        if (isMain)
        {
            baseView.showLoadingView();
            baseView.showEmptyVIew();
        }
        model.getData(this);
    }

    @Override
    public void success(List<ApiBean> list) {
        apiView.showSuccess(list);
    }

    @Override
    public void error(String msg) {
        baseView.showLoadErrorView(msg);
    }
}
