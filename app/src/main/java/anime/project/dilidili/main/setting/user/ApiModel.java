package anime.project.dilidili.main.setting.user;

import java.util.List;

import anime.project.dilidili.database.DatabaseUtil;
import anime.project.dilidili.bean.ApiBean;

public class ApiModel implements ApiContract{

    @Override
    public void getData(LoadDataCallback callback) {
        List<ApiBean> list = DatabaseUtil.queryAllApi();
        if (list.size() > 0)
            callback.success(list);
        else
            callback.error("未自定义");
    }

    public interface LoadDataCallback{
        void success(List<ApiBean> list);
        void error(String msg);
    }
}
