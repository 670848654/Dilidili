package anime.project.dilidili.main.desc;

import android.content.Context;

public interface DescContract {
     //获取数据
    void getData(Context context, String url, DescModel.LoadDataCallback callback);
}
