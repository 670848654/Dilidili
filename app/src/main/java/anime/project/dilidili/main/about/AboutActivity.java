package anime.project.dilidili.main.about;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Environment;
import android.view.View;
import android.widget.TextView;

import com.r0adkll.slidr.Slidr;

import androidx.appcompat.widget.Toolbar;
import anime.project.dilidili.R;
import anime.project.dilidili.main.base.BaseActivity;
import anime.project.dilidili.util.StatusBarUtil;
import anime.project.dilidili.util.Utils;
import butterknife.BindView;

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
    protected int setLayoutRes() {
        return R.layout.activity_about;
    }

    @Override
    protected void init() {
        StatusBarUtil.setColorForSwipeBack(AboutActivity.this, getResources().getColor(R.color.night), 0);
        // 设置右滑动返回
        Slidr.attach(this, Utils.defaultInit());
        initToolbar();
        initViews();
    }

    @Override
    protected void initBeforView() {

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
}
