package anime.project.dilidili.main.about;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Environment;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.r0adkll.slidr.Slidr;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import anime.project.dilidili.R;
import anime.project.dilidili.adapter.LogAdapter;
import anime.project.dilidili.api.Api;
import anime.project.dilidili.application.DiliDili;
import anime.project.dilidili.bean.LogBean;
import anime.project.dilidili.main.base.BaseActivity;
import anime.project.dilidili.main.base.Presenter;
import anime.project.dilidili.net.DownloadUtil;
import anime.project.dilidili.net.HttpGet;
import anime.project.dilidili.util.SharedPreferencesUtils;
import anime.project.dilidili.util.SwipeBackLayoutUtil;
import anime.project.dilidili.util.Utils;
import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AboutActivity extends BaseActivity {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.cache)
    TextView cache;
    @BindView(R.id.open_source)
    TextView open_source;
    @BindView(R.id.version)
    TextView version;
    private ProgressDialog p;
    private  String downloadUrl;
    private Call downCall;
    @BindView(R.id.footer)
    LinearLayout footer;

    @Override
    protected Presenter createPresenter() {
        return null;
    }

    @Override
    protected void loadData() {
    }

    @Override
    protected int setLayoutRes() {
        return R.layout.activity_about;
    }

    @Override
    protected void init() {
        Slidr.attach(this, Utils.defaultInit());
        initToolbar();
        initViews();
    }

    @Override
    protected void initBeforeView() {
        SwipeBackLayoutUtil.convertActivityToTranslucent(this);
    }

    public void initToolbar(){
        toolbar.setTitle("关于");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(view -> finish());
    }

    private void initViews(){
        LinearLayout.LayoutParams Params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, Utils.getNavigationBarHeight(this));
        footer.findViewById(R.id.footer).setLayoutParams(Params);
        version.setText(Utils.getASVersionName());
        cache.setText(Environment.getExternalStorageDirectory() + Utils.getString(R.string.cache_text));
        open_source.setOnClickListener(v -> {
            if (Utils.isFastClick()) startActivity(new Intent(AboutActivity.this,OpenSourceActivity.class));
        });
    }

    @OnClick({R.id.dilidili,R.id.github})
    public void openBrowser(CardView cardView) {
        switch (cardView.getId()) {
            case R.id.dilidili:
                Utils.viewInChrome(this, DiliDili.DOMAIN);
                break;
            case R.id.github:
                Utils.viewInChrome(this, Utils.getString(R.string.github_url));
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.about_menu, menu);
        MenuItem checkUpdateItem = menu.findItem(R.id.check_update);
        MenuItem updateLogItem = menu.findItem(R.id.update_log);
        if (!(Boolean) SharedPreferencesUtils.getParam(this, "darkTheme", false)) {
            checkUpdateItem.setIcon(R.drawable.baseline_update_black_48dp);
            updateLogItem.setIcon(R.drawable.baseline_log_black_48dp);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.update_log:
                showUpdateLogs();
                break;
            case R.id.check_update:
                if (Utils.isFastClick()) checkUpdate();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void showUpdateLogs() {
        AlertDialog alertDialog;
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_update_log, null);
        RecyclerView logs = view.findViewById(R.id.rv_list);
        logs.setLayoutManager(new LinearLayoutManager(this));
        LogAdapter logAdapter = new LogAdapter(createUpdateLogList());
        logs.setAdapter(logAdapter);
        builder.setPositiveButton(Utils.getString(R.string.page_positive), null);
        TextView title = new TextView(this);
        title.setText(Utils.getString(R.string.update_log));
        title.setPadding(30,30,30,30);
        title.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        title.setGravity(Gravity.LEFT);
        title.setTextSize(18);
        title.setTextColor(getResources().getColor(R.color.text_color_primary));
        builder.setCustomTitle(title);
        alertDialog = builder.setView(view).create();
        alertDialog.show();
    }

    public List createUpdateLogList() {
        List logsList = new ArrayList();
        logsList.add(new LogBean("版本：2.3.6-beta1","更改域名为http://www.dilidili.co\n由于网站改动幅度较大且不稳定，导致收藏的番剧链接异常，更新后收藏夹将被清空\n部分功能因网站原理暂不可用"));
        logsList.add(new LogBean("版本：2.3.5_b","修复内置播放器播放完毕后程序崩溃的问题"));
        logsList.add(new LogBean("版本：2.3.5_a","修复内置播放器使用Exo内核无限加载的问题"));
        logsList.add(new LogBean("版本：2.3.5","修复一些错误\n修复内置视频播放器存在的一些问题"));
        logsList.add(new LogBean("版本：2.3.4","修复视频播放器白额头的Bug"));
        logsList.add(new LogBean("版本：2.3.3","修复一些Bug"));
        logsList.add(new LogBean("版本：2.3.2","修复官方搜索，你可以在自定义设置中更改检索方式"));
        logsList.add(new LogBean("版本：2.3.1","修复更换嘀哩嘀哩域名后剧集某些异常的问题\n默认禁用X5内核，X5内核更新后会导致应用闪退（Android 10)，你可以在自定义设置中打开，若发生闪退则关闭该选项"));
        logsList.add(new LogBean("版本：2.3","修复更换嘀哩嘀哩域名后导致剧集播放记录消失的问题"));
        logsList.add(new LogBean("版本：2.2","修复一些Bugs\n修正部分界面布局\n适配沉浸式导航栏《仅支持原生导航栏，第三方魔改UI无效》（Test）"));
        logsList.add(new LogBean("版本：2.1","修复一些Bugs\n修正部分界面布局\n新增亮色主题（Test）"));
        logsList.add(new LogBean("版本：2.0","修复更新SDK后导致崩溃的严重问题"));
        logsList.add(new LogBean("版本：1.9_a9","升级SDK版本为29（Android 10）"));
        logsList.add(new LogBean("版本：1.9_a8","修复已知Bug\n部分UI更改，优化体验"));
        logsList.add(new LogBean("版本：1.9_a7","部分UI变更，优化体验\n修复存在的一些问题"));
        logsList.add(new LogBean("版本：1.9_a5","修复一个显示错误\n部分番剧解析异常（嘀哩嘀哩网站正在改版升级导致）\n番剧详情界面可以返回上一级了"));
        logsList.add(new LogBean("版本：1.9_a3","官方搜索服务器崩了，导致无法正常使用，暂时替换为百度搜索，可在自定义中切换搜索方式"));
        logsList.add(new LogBean("版本：1.9_a2","修复番剧详情中下载的一个错误\n部分细节更改"));
        logsList.add(new LogBean("版本：1.9_a1","修复使用分屏时导致程序崩溃的严重Bug"));
        logsList.add(new LogBean("版本：1.9","修复了一些隐性Bug\n部分UI更改"));
        logsList.add(new LogBean("版本：1.8_a8","修复了一些小错误"));
        logsList.add(new LogBean("版本：1.8_a7","修复一个错误"));
        logsList.add(new LogBean("版本：1.8_a6","修复剧集中操作不当导致的严重Bug"));
        logsList.add(new LogBean("版本：1.8_a5","修复D站改版后导致部分解析异常的Bug"));
        logsList.add(new LogBean("版本：1.8_a4","修复D站改版而导致某些番剧无法解析的错误"));
        logsList.add(new LogBean("版本：1.8_a3","修复Bug"));
        logsList.add(new LogBean("版本：1.8_a2","修复Bug"));
        logsList.add(new LogBean("版本：1.8_a1","修复更换格式不正确的域名导致崩溃的Bug"));
        logsList.add(new LogBean("版本：1.8","修复D站域名更换导致的问题"));
        logsList.add(new LogBean("版本：1.7.5","修复一个Bug"));
        logsList.add(new LogBean("版本：1.7.4","修复一个webview的错误"));
        logsList.add(new LogBean("版本：1.7.3","webview界面改动"));
        logsList.add(new LogBean("版本：1.7.2","修复部分Bug"));
        logsList.add(new LogBean("版本：1.7.1","修复部分Bug\n减小APK体积"));
        logsList.add(new LogBean("版本：1.7","修复Bug"));
        logsList.add(new LogBean("版本：1.6","修复Bug\n部分UI变更"));
        logsList.add(new LogBean("版本：1.5","修复Bug"));
        logsList.add(new LogBean("版本：1.4","修复Bug"));
        logsList.add(new LogBean("版本：1.3","修复Bug"));
        logsList.add(new LogBean("版本：1.2","修复Bug"));
        logsList.add(new LogBean("版本：1.1","修复Bug"));
        logsList.add(new LogBean("版本：1.0","第一个版本"));
        return logsList;
    }

    public void checkUpdate() {
        p = Utils.getProDialog(this, R.string.check_update_text);
        new Handler().postDelayed(() -> new HttpGet(Api.CHECK_UPDATE, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> {
                    Utils.cancelProDialog(p);
                    application.showErrorToastMsg("连接服务器超时，请重试");
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();
                try {
                    JSONObject obj = new JSONObject(json);
                    String newVersion = obj.getString("tag_name");
                    if (newVersion.equals(Utils.getASVersionName()))
                        runOnUiThread(() -> {
                            Utils.cancelProDialog(p);
                            application.showSuccessToastMsg("没有新版本");
                        });
                    else {
                        downloadUrl = obj.getJSONArray("assets").getJSONObject(0).getString("browser_download_url");
                        String body = obj.getString("body");
                        runOnUiThread(() -> {
                            Utils.cancelProDialog(p);
                           Utils.findNewVersion(AboutActivity.this,
                                   newVersion,
                                   body,
                                   (dialog, which) -> {
                                       p = Utils.showProgressDialog(AboutActivity.this);
                                       p.setButton(ProgressDialog.BUTTON_NEGATIVE, "取消", (dialog1, which1) -> {
                                           if (null != downCall)
                                               downCall.cancel();
                                           dialog1.dismiss();
                                       });
                                       p.show();
                                       downNewVersion(downloadUrl);
                                   },
                                   (dialog, which) ->
                                       dialog.dismiss()
                                   );
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }), 1000);
    }

    /**
     * 下载apk
     * @param url 下载地址
     */
    private void downNewVersion(String url) {
        downCall = DownloadUtil.get().downloadApk(url, new DownloadUtil.OnDownloadListener() {
            @Override
            public void onDownloadSuccess(final String fileName) {
                runOnUiThread(() -> {
                    Utils.cancelProDialog(p);
                    Utils.startInstall(AboutActivity.this);
                });
            }
            @Override
            public void onDownloading(final int progress) {
                runOnUiThread(() -> p.setProgress(progress));
            }
            @Override
            public void onDownloadFailed() {
                runOnUiThread(() -> {
                    Utils.cancelProDialog(p);
                    application.showErrorToastMsg("下载失败");
                });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10001) {
            Utils.startInstall(AboutActivity.this);
        }
    }
}
