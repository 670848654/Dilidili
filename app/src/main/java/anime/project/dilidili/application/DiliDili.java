package anime.project.dilidili.application;

import android.app.Activity;
import android.app.Application;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.tencent.smtt.sdk.QbSdk;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import anime.project.dilidili.main.player.JZExoPlayer;
import anime.project.dilidili.util.Utils;
import cn.jzvd.JzvdStd;

public class DiliDili extends Application {
    private static DiliDili appContext;
    private List<Activity> oList;
    public String error;
    public JSONObject week = new JSONObject();
    private static Map<String, Activity> destoryMap = new HashMap<>();

    public static DiliDili getInstance() {
        return appContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        JzvdStd.setMediaInterface(new JZExoPlayer());
        oList = new ArrayList<>();
        appContext = this;
        Utils.init(this);
        initTBS();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        if (level == TRIM_MEMORY_UI_HIDDEN) {
            Glide.get(this).clearMemory();
        }
        Glide.get(this).trimMemory(level);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Glide.get(this).clearMemory();
    }

    private void initTBS() {
        //搜集本地tbs内核信息并上报服务器，服务器返回结果决定使用哪个内核。
        QbSdk.setDownloadWithoutWifi(true);//非wifi条件下允许下载X5内核
        QbSdk.PreInitCallback cb = new QbSdk.PreInitCallback() {
            @Override
            public void onViewInitFinished(boolean arg0) {
                if (arg0) showToastMsg("X5内核加载成功");
                else showToastMsg("X5内核加载失败,切换到系统内核");
            }
            @Override
            public void onCoreInitFinished() {
            }
        };
        //x5内核初始化接口
        QbSdk.initX5Environment(getApplicationContext(), cb);
    }

    public void showToastMsg(String msg){
        Toast.makeText(appContext, msg, Toast.LENGTH_SHORT).show();
    }

    public void showSnackbarMsg(View view, String msg){
        Snackbar.make(view, msg, Snackbar.LENGTH_LONG).show();
    }

    public void showSnackbarMsg(View view, String msg, String actionName, View.OnClickListener listener){
        Snackbar.make(view, msg, Snackbar.LENGTH_LONG).setAction(actionName, listener).show();
    }

    public void addActivity(Activity activity) {
        if (!oList.contains(activity)) {
            oList.add(activity);
        }
    }

    public void removeActivity(Activity activity) {
        if (oList.contains(activity)) {
            oList.remove(activity);
            activity.finish();
        }
    }

    public void removeALLActivity() {
        for (Activity activity : oList) {
            activity.finish();
        }
    }

    public static void addDestoryActivity(Activity activity, String activityName) {
        destoryMap.put(activityName, activity);
    }

    public static void destoryActivity(String activityName) {
        Set<String> keySet = destoryMap.keySet();
        if (keySet.size() > 0) {
            for (String key : keySet) {
                if (activityName.equals(key)) {
                    destoryMap.get(key).finish();
                }
            }
        }
    }
}
