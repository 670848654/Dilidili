package anime.project.dilidili.main.start;

import android.content.Intent;
import android.os.Handler;

import anime.project.dilidili.R;
import anime.project.dilidili.main.base.BaseActivity;
import anime.project.dilidili.main.base.Presenter;
import anime.project.dilidili.main.home.HomeActivity;
import anime.project.dilidili.util.SharedPreferencesUtils;
import anime.project.dilidili.util.StatusBarUtil;

public class StartActivity extends BaseActivity {
    @Override
    protected Presenter createPresenter() {
        return null;
    }

    @Override
    protected void loadData() {

    }

    @Override
    protected int setLayoutRes() {
        return R.layout.activity_start;
    }

    @Override
    protected void init() {
        StatusBarUtil.setColor(StartActivity.this, getResources().getColor(R.color.night), 0);
        SharedPreferencesUtils.setParam(this,"initX5","ok");
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(StartActivity.this, HomeActivity.class));
                StartActivity.this.finish();
            }
        }, 1500);
    }

    @Override
    protected void initBeforeView() {

    }
}
