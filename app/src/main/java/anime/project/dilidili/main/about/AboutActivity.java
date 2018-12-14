package anime.project.dilidili.main.about;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.r0adkll.slidr.Slidr;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import androidx.appcompat.widget.Toolbar;
import anime.project.dilidili.R;
import anime.project.dilidili.api.Api;
import anime.project.dilidili.main.base.BaseActivity;
import anime.project.dilidili.main.base.Presenter;
import anime.project.dilidili.net.OkHttpGet;
import anime.project.dilidili.util.StatusBarUtil;
import anime.project.dilidili.util.Utils;
import butterknife.BindView;
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
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void initViews(){
        version.setText(Utils.getASVersionName());
        cache.setText(Environment.getExternalStorageDirectory() + Utils.getString(this, R.string.cache_text));
        open_source.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utils.isFastClick())
                    startActivity(new Intent(AboutActivity.this,OpenSourceActivity.class));
            }
        });
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
            if (Utils.isFastClick())
                checkUpdate();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void checkUpdate() {
        p = Utils.getProDialog(AboutActivity.this, "检测更新中...");
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                new OkHttpGet(Api.CHECK_UPDATE, 10, 20, new Callback() {
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
                                Bundle bundle = new Bundle();
                                bundle.putString("url",downloadUrl);
                                sendMessage(2,bundle);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }, 1000);
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
            Utils.cancelProDoalog(p);
            super.handleMessage(msg);
            switch (msg.what) {
                case -1:
                    Snackbar.make(toolbar,"连接服务器超时",Snackbar.LENGTH_LONG).setAction("重试", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            checkUpdate();
                        }
                    }).show();
                    break;
                case 1:
                    Toast.makeText(AboutActivity.this, "没有新版本", Toast.LENGTH_LONG).show();
                    break;
                case 2:
                    Toast.makeText(AboutActivity.this, "发现新版本,请手动下载", Toast.LENGTH_LONG).show();
                    Utils.viewInBrowser(AboutActivity.this,msg.getData().getString("url"));
                    break;
            }
        }
    };
}
