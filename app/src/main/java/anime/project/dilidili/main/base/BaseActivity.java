package anime.project.dilidili.main.base;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import anime.project.dilidili.R;
import anime.project.dilidili.application.DiliDili;
import anime.project.dilidili.database.DatabaseUtil;
import anime.project.dilidili.util.Utils;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import pub.devrel.easypermissions.EasyPermissions;

public abstract class BaseActivity<V, P extends Presenter<V>> extends AppCompatActivity implements EasyPermissions.PermissionCallbacks{
    protected P mPresenter;
    public View errorView, emptyView;
    public TextView errorTitle;
    public DiliDili application;
    private Unbinder mUnBinder;
    protected boolean mActivityFinish = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initBeforeView();
        setContentView(setLayoutRes());
        mUnBinder = ButterKnife.bind(this);
        if (application == null) {
            application = (DiliDili) getApplication();
        }
        application.addActivity(this);
        if (EasyPermissions.hasPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            //创建database路路径
            Utils.creatFile();
            DatabaseUtil.CREATE_TABLES();
            init();
            initCustomViews();
            mPresenter = createPresenter();
            loadData();
        } else {
            EasyPermissions.requestPermissions(this, Utils.getString(R.string.permissions),
                    300, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
    }
    protected abstract P createPresenter();

    protected abstract void loadData();

    protected abstract int setLayoutRes();

    protected abstract void init();

    protected abstract void initBeforeView();

    protected void initCustomViews(){
        errorView = getLayoutInflater().inflate(R.layout.base_error_view, null);
        errorTitle = errorView.findViewById(R.id.title);
        emptyView = getLayoutInflater().inflate(R.layout.base_emnty_view, null);
    }

    /**
     * 隐藏虚拟导航按键
     */
    protected void hideNavBar() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    /**
     * 虚拟导航按键
     */
    protected void showNavBar(){
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
    }

    /**
     * Android 9 异形屏适配
     */
    protected void hideGap(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            getWindow().setAttributes(lp);
        }
    }

    @Override
    protected void onDestroy() {
        mActivityFinish = true;
        //取消View的关联
        if (null != mPresenter )
            mPresenter.detachView();
        mUnBinder.unbind();
        super.onDestroy();
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        init();
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        application.showToastMsg(Utils.getString(R.string.permissions_error));
        application.removeALLActivity();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }
}
