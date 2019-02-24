package anime.project.dilidili.main.setting;

import android.content.DialogInterface;
import android.content.Intent;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.r0adkll.slidr.Slidr;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import anime.project.dilidili.R;
import anime.project.dilidili.application.DiliDili;
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
    @BindView(R.id.domain_default)
    TextView domain_default;
    @BindView(R.id.player_default)
    TextView player_default;
    @BindView(R.id.api)
    TextView api;
    private String url;

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
        Slidr.attach(this, Utils.defaultInit());
        initToolbar();
        getUserCustomSet();
    }

    @Override
    protected void initBeforeView() {

    }

    public void initToolbar() {
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(Utils.getString(R.string.setting_title));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(view -> finish());
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
        domain_default.setText(DiliDili.DOMAIN);
    }

    @OnClick({R.id.set_domain, R.id.set_player, R.id.set_api_source})
    public void onClick(RelativeLayout layout) {
        switch (layout.getId()) {
            case R.id.set_domain:
                setDomain();
                break;
            case R.id.set_player:
                setDefaultPlayer();
                break;
            case R.id.set_api_source:
                startActivity(new Intent(this,ApiActivity.class));
                break;
        }
    }

    public void setDomain() {
        AlertDialog alertDialog;
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_domain, null);
        Spinner spinner = view.findViewById(R.id.prefix);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                url = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        EditText editText = view.findViewById(R.id.domain);
        builder.setPositiveButton(Utils.getString(R.string.page_positive_edit), null);
        builder.setNegativeButton(Utils.getString(R.string.page_negative), null);
        builder.setNeutralButton(Utils.getString(R.string.page_def), null);
        builder.setTitle(Utils.getString(R.string.domain_title));
        builder.setCancelable(false);
        alertDialog = builder.setView(view).create();
        alertDialog.show();
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String text = editText.getText().toString();
            if (!text.equals("")) {
                if (Patterns.WEB_URL.matcher(text).matches()) {
                    setResult(0x20);
                    if (text.endsWith("/")) text = text.substring(0, text.length()-1);
                    url += text;
                    SharedPreferencesUtils.setParam(SettingActivity.this, "domain", url);
                    DiliDili.DOMAIN = url;
                    DiliDili.setApi();
                    domain_default.setText(url);
                    alertDialog.dismiss();
                    Utils.showSnackbar(toolbar, Utils.getString(R.string.set_domain_ok));
                }else editText.setError(Utils.getString(R.string.set_domain_error2));
            } else editText.setError(Utils.getString(R.string.set_domain_error1));
        });
        alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener(v -> {
            setResult(0x20);
            DiliDili.DOMAIN = Utils.getString(R.string.domain_url);
            DiliDili.setApi();
            SharedPreferencesUtils.setParam(SettingActivity.this, "domain", DiliDili.DOMAIN);
            domain_default.setText(DiliDili.DOMAIN);
            alertDialog.dismiss();
        });
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
