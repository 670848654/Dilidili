package anime.project.dilidili.main.setting;

import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.r0adkll.slidr.Slidr;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import anime.project.dilidili.R;
import anime.project.dilidili.database.DatabaseUtil;
import anime.project.dilidili.main.base.BaseActivity;
import anime.project.dilidili.main.base.Presenter;
import anime.project.dilidili.main.setting.user.ApiActivity;
import anime.project.dilidili.util.SharedPreferencesUtils;
import anime.project.dilidili.util.StatusBarUtil;
import anime.project.dilidili.util.Utils;
import butterknife.BindView;
import butterknife.OnClick;

public class SettingActivity extends BaseActivity {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.player_default)
    TextView player_default;
    @BindView(R.id.api)
    TextView api;

    @Override
    protected Presenter createPresenter() {
        return null;
    }

    @Override
    protected void loadData() {

    }

    @Override
    protected int setLayoutRes() {
        return R.layout.activity_setting;
    }

    @Override
    protected void init() {
        StatusBarUtil.setColorForSwipeBack(SettingActivity.this, getResources().getColor(R.color.night), 0);
        // 设置右滑动返回
        Slidr.attach(this, Utils.defaultInit());
        initToolbar();
        getUserCustomSet();
    }

    @Override
    protected void initBeforeView() {

    }

    public void initToolbar() {
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(Utils.getString(SettingActivity.this,R.string.setting_title));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                finish();
            }
        });
    }

    public void getUserCustomSet() {
        api.setText(DatabaseUtil.queryAllApi().size() + "");
        switch ((Integer) SharedPreferencesUtils.getParam(getApplicationContext(), "player", 0)) {
            case 0:
                player_default.setText("内置");
                break;
            case 1:
                player_default.setText("外置");
                break;
        }
    }

    @OnClick({R.id.set_player, R.id.set_api_source})
    public void onClick(RelativeLayout layout) {
        switch (layout.getId()) {
            case R.id.set_player:
                setDefaultPlayer();
                break;
            case R.id.set_api_source:
                startActivity(new Intent(this,ApiActivity.class));
                break;
        }
    }

    public void setDefaultPlayer() {
        String [] item = {"内置","外置"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("请选择视频播放器");
        builder.setSingleChoiceItems(item, (Integer) SharedPreferencesUtils.getParam(getApplicationContext(), "player", 0), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case 0:
                        SharedPreferencesUtils.setParam(getApplicationContext(),"player",0);
                        player_default.setText("内置");
                        break;
                    case 1:
                        SharedPreferencesUtils.setParam(getApplicationContext(),"player",1);
                        player_default.setText("外置");
                        break;
                }
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        api.setText(DatabaseUtil.queryAllApi().size() + "");
    }
}
