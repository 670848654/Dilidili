package anime.project.dilidili.main.start;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.widget.LinearLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import anime.project.dilidili.R;
import anime.project.dilidili.api.Api;
import anime.project.dilidili.main.base.BaseActivity;
import anime.project.dilidili.main.base.Presenter;
import anime.project.dilidili.main.home.HomeActivity;
import anime.project.dilidili.net.DownloadUtil;
import anime.project.dilidili.net.HttpGet;
import anime.project.dilidili.util.SharedPreferencesUtils;
import anime.project.dilidili.util.Utils;
import butterknife.BindView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class StartActivity extends BaseActivity {
    @BindView(R.id.check_update)
    LinearLayout linearLayout;
    private ProgressDialog p;
    private String downUrl;
    private Call downCall;

    @Override
    protected Presenter createPresenter() {
        return null;
    }

    @Override
    protected void loadData() {}

    @Override
    protected int setLayoutRes() {
        return R.layout.activity_start;
    }

    @Override
    protected void init() {
        SharedPreferencesUtils.setParam(this,"initX5","init");
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            linearLayout.setVisibility(View.VISIBLE);
            checkUpdate();
        }, 1000);
    }

    @Override
    protected void initBeforeView() {}

    private void checkUpdate() {
        new HttpGet(Api.CHECK_UPDATE, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> {
                    application.showErrorToastMsg("网络连接超时，检测更新失败");
                    openMain();
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();
                try {
                    JSONObject obj = new JSONObject(json);
                    String newVersion = obj.getString("tag_name");
                    if (newVersion.equals(Utils.getASVersionName()))
                        runOnUiThread(() -> openMain());
                    else {
                        downUrl = obj.getJSONArray("assets").getJSONObject(0).getString("browser_download_url");
                        String body = obj.getString("body");
                        runOnUiThread(() -> Utils.findNewVersion(StartActivity.this,
                                newVersion,
                                body,
                                (dialog, which) -> {
                                    p = Utils.showProgressDialog(StartActivity.this);
                                    p.setButton(ProgressDialog.BUTTON_NEGATIVE, "取消", (dialog1, which1) -> {
                                        if (null != downCall)
                                            downCall.cancel();
                                        dialog1.dismiss();
                                    });
                                    p.show();
                                    downNewVersion(downUrl);
                                },
                                (dialog, which) -> {
                                    dialog.dismiss();
                                    openMain();
                                })
                        );
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
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
                    Utils.startInstall(StartActivity.this);
                    StartActivity.this.finish();
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
                    application.showErrorToastMsg("下载失败~");
                    openMain();
                });
            }
        });
    }

    private void openMain() {
        startActivity(new Intent(StartActivity.this, HomeActivity.class));
        StartActivity.this.finish();
    }
}
