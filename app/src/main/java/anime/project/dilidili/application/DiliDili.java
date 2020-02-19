package anime.project.dilidili.application;

import android.app.Activity;
import android.app.Application;
import android.widget.Toast;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.appcompat.app.AppCompatDelegate;

import com.bumptech.glide.Glide;
import com.tencent.smtt.sdk.QbSdk;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import anime.project.dilidili.R;
import anime.project.dilidili.util.SharedPreferencesUtils;
import anime.project.dilidili.util.Utils;
import es.dmoral.toasty.Toasty;

public class DiliDili extends Application {
    private static DiliDili appContext;
    private List<Activity> oList;
    private static Map<String, Activity> destoryMap = new HashMap<>();
    public static String DOMAIN;
    public static String TAG_API;
    public static String RECOMMEND_API;
    public String error;
    public JSONObject week = new JSONObject();

    public static DiliDili getInstance() {
        return appContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if ((Boolean) SharedPreferencesUtils.getParam(this, "darkTheme", false))
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        else
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        oList = new ArrayList<>();
        appContext = this;
        Utils.init(this);
        if (!(boolean) SharedPreferencesUtils.getParam(this, "v2.3.6", false)) {
            DOMAIN = Utils.getString(R.string.domain_url);
            SharedPreferencesUtils.setParam(this, "domain", DOMAIN);
            Utils.deleteDataBase();
            SharedPreferencesUtils.setParam(this,"search", 0);
        }else
            DOMAIN = (String) SharedPreferencesUtils.getParam(this, "domain", Utils.getString(R.string.domain_url));
        setApi();
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

    public static void setApi() {
        TAG_API = DiliDili.DOMAIN + "/anime/201510/";
        RECOMMEND_API = DiliDili.DOMAIN + "/zttj.html";
    }

    private void initTBS() {
        //搜集本地tbs内核信息并上报服务器，服务器返回结果决定使用哪个内核。
        QbSdk.setDownloadWithoutWifi(true);//非wifi条件下允许下载X5内核
        QbSdk.PreInitCallback cb = new QbSdk.PreInitCallback() {
            @Override
            public void onViewInitFinished(boolean arg0) {
//                if (arg0) showSuccessToastMsg("X5内核加载成功");
//                else showErrorToastMsg("X5内核加载失败");
            }
            @Override
            public void onCoreInitFinished() {
            }
        };
        //x5内核初始化接口
        QbSdk.initX5Environment(getApplicationContext(), cb);
    }

    public void showToastMsg(String msg){
        Toasty.warning(getApplicationContext(), msg, Toast.LENGTH_LONG, true).show();
    }

    public void showSuccessToastMsg(String msg){
        Toasty.success(getApplicationContext(), msg, Toast.LENGTH_LONG, true).show();
    }

    public void showErrorToastMsg(String msg){
        Toasty.error(getApplicationContext(), msg, Toast.LENGTH_LONG, true).show();
    }

    public void showCustomToastMsg(String msg, @DrawableRes int iconRes, @ColorRes int color){
        Toasty.custom(this, msg,
                iconRes, color, Toast.LENGTH_LONG, true, true).show();
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
