package anime.project.dilidili.application;

import android.app.Activity;
import android.app.Application;
import android.widget.Toast;

import com.tencent.smtt.sdk.QbSdk;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import anime.project.dilidili.ijkplayer.JZMediaIjkplayer;
import anime.project.dilidili.util.Utils;
import cn.jzvd.JzvdStd;

public class DiliDili extends Application {
    private static DiliDili appContext;
    private List<Activity> oList;
    public String error;
    public JSONObject week = new JSONObject();

    public static DiliDili getInstance() {
        return appContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        JzvdStd.setMediaInterface(new JZMediaIjkplayer());
        oList = new ArrayList<>();
        appContext = this;
        Utils.init(this);
        initTBS();
    }

    private void initTBS() {
        //搜集本地tbs内核信息并上报服务器，服务器返回结果决定使用哪个内核。
        QbSdk.setDownloadWithoutWifi(true);//非wifi条件下允许下载X5内核
        QbSdk.PreInitCallback cb = new QbSdk.PreInitCallback() {
            @Override
            public void onViewInitFinished(boolean arg0) {
                //x5內核初始化完成的回调，为true表示x5内核加载成功，否则表示x5内核加载失败，会自动切换到系统内核。
                if (arg0)
                    Toast.makeText(DiliDili.this, "X5内核加载成功", Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(DiliDili.this, "X5内核加载失败,切换到系统内核", Toast.LENGTH_LONG).show();
            }
            @Override
            public void onCoreInitFinished() {
            }
        };
        //x5内核初始化接口
        QbSdk.initX5Environment(getApplicationContext(), cb);
    }

    /**
     * 添加Activity
     */
    public void addActivity_(Activity activity) {
        if (!oList.contains(activity)) {
            oList.add(activity);
        }
    }

    /**
     * 销毁单个Activity
     */
    public void removeActivity_(Activity activity) {
        if (oList.contains(activity)) {
            oList.remove(activity);
            activity.finish();
        }
    }

    /**
     * 销毁所有的Activity
     */
    public void removeALLActivity_() {
        for (Activity activity : oList) {
            activity.finish();
        }
    }
}
