package anime.project.dilidili.main.about;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.r0adkll.slidr.Slidr;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import anime.project.dilidili.R;
import anime.project.dilidili.api.Api;
import anime.project.dilidili.application.DiliDili;
import anime.project.dilidili.main.base.BaseActivity;
import anime.project.dilidili.main.base.Presenter;
import anime.project.dilidili.net.DownloadUtil;
import anime.project.dilidili.net.HttpGet;
import anime.project.dilidili.util.StatusBarUtil;
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
    private Call call;

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
        StatusBarUtil.setColorForSwipeBack(AboutActivity.this, getResources().getColor(R.color.night), 0);
        Slidr.attach(this, Utils.defaultInit());
        initToolbar();
        initViews();
    }

    @Override
    protected void initBeforeView() {

    }

    public void initToolbar(){
        toolbar.setTitle("关于");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(view -> finish());
    }

    private void initViews(){
        version.setText(Utils.getASVersionName());
        cache.setText(Environment.getExternalStorageDirectory() + Utils.getString(R.string.cache_text));
        open_source.setOnClickListener(v -> {
            if (Utils.isFastClick()) startActivity(new Intent(AboutActivity.this,OpenSourceActivity.class));
        });
    }

    @OnClick(R.id.dilidili)
    public void openDilidili(){
        Utils.viewInChrome(this, DiliDili.URL);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.about_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.check_update) {
            if (Utils.isFastClick()) checkUpdate();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void checkUpdate() {
        p = Utils.getProDialog(this, R.string.check_update_text);
        Handler handler = new Handler();
        handler.postDelayed(() -> new HttpGet(Api.CHECK_UPDATE, 10, 20, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                sendMessage(-1,null);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();
                try {
                    JSONObject obj = new JSONObject(json);
                    String newVersion = obj.getString("tag_name");
                    if (newVersion.equals(Utils.getASVersionName()))
                        sendMessage(1,null);
                    else {
                        String downloadUrl = obj.getJSONArray("assets").getJSONObject(0).getString("browser_download_url");
                        String body = obj.getString("body");
                        Bundle bundle = new Bundle();
                        bundle.putString("url",downloadUrl);
                        bundle.putString("body",body);
                        sendMessage(2,bundle);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }), 1000);
    }

    public void sendMessage(int id, Bundle bundle) {
        Message msg = new Message();
        msg.what = id;
        if (bundle != null)
            msg.setData(bundle);
        handler.sendMessage(msg);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Utils.cancelProDialog(p);
            super.handleMessage(msg);
            switch (msg.what) {
                case -1:
                    application.showSnackbarMsg(toolbar, "连接服务器超时", "重试", view -> checkUpdate());
                    break;
                case 1:
                    application.showToastMsg("没有新版本");
                    break;
                case 2:
                    findNewVersion(msg.getData().getString("url"), msg.getData().getString("body"));
//                    Utils.viewInChrome(AboutActivity.this, msg.getData().getString("url"));
                    break;
            }
        }
    };

    private void findNewVersion(String downUrl, String body) {
        AlertDialog alertDialog;
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setMessage(body);
        builder.setTitle("发现新版本");
        builder.setPositiveButton("马上更新", (dialog, which) -> {
            p = Utils.showProgressDialog(AboutActivity.this);
            p.setButton(ProgressDialog.BUTTON_NEGATIVE, "取消", (dialog1, which1) -> {
                if (null != call)
                    call.cancel();
                dialog1.dismiss();
            });
            p.show();
            downNewVersion(downUrl);
        });
        builder.setNegativeButton("暂不更新", (dialog, which) -> {
            dialog.dismiss();
        });
        builder.setCancelable(false);
        alertDialog = builder.create();
        alertDialog.show();
    }

    private void downNewVersion(String url) {
        call = DownloadUtil.get().downloadApk(url, new DownloadUtil.OnDownloadListener() {
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
                    application.showSnackbarMsg(toolbar, "下载失败~");
                });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10001){
            Utils.startInstall(AboutActivity.this);
        }
    }
}
