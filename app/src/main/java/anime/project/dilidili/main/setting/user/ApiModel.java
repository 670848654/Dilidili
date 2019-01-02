package anime.project.dilidili.main.setting.user;

import java.util.List;

import anime.project.dilidili.bean.ApiBean;
import anime.project.dilidili.database.DatabaseUtil;

public class ApiModel implements ApiContract.Model{

    @Override
    public void getData(ApiContract.LoadDataCallback callback) {
        List<ApiBean> list = DatabaseUtil.queryAllApi();
        if (list.size() > 0)
            callback.success(list);
        else
            callback.error("未自定义");
    }
}
