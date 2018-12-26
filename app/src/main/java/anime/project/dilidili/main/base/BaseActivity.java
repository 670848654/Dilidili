package anime.project.dilidili.main.base;

import android.Manifest;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import java.io.File;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import anime.project.dilidili.R;
import anime.project.dilidili.application.DiliDili;
import anime.project.dilidili.database.DatabaseUtil;
import anime.project.dilidili.util.Utils;
import butterknife.ButterKnife;
import pub.devrel.easypermissions.EasyPermissions;

public abstract class BaseActivity<V, P extends Presenter<V>> extends AppCompatActivity implements EasyPermissions.PermissionCallbacks{
    protected P mPresenter;
    public View errorView, emptyView, userEmptyView;
    public TextView errorTitle;
    public DiliDili application;
    private static Handler sHandler;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initBeforeView();
        setContentView(setLayoutRes());
        ButterKnife.bind(this);
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
            EasyPermissions.requestPermissions(this, Utils.getString(BaseActivity.this, R.string.permissions),
                    300, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
    }
    protected abstract P createPresenter();

    protected abstract void loadData();

    protected abstract int setLayoutRes();

    protected abstract void init();

    protected abstract void initBeforeView();

    public void initCustomViews(){
        errorView = getLayoutInflater().inflate(R.layout.base_error_view, null);
        errorTitle = errorView.findViewById(R.id.title);
        emptyView = getLayoutInflater().inflate(R.layout.base_emnty_view, null);
        userEmptyView = getLayoutInflater().inflate(R.layout.base_emtpy_favorite_view, null);
    }

    /**
     * 隐藏虚拟按键
     */
    public void hideNavBar() {
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        sHandler = new Handler();
        sHandler.post(mHideRunnable); // hide the navigation bar
        final View decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener(visibility -> {
            sHandler.postDelayed(mHideRunnable, 3000); // hide the navigation bar
        });
    }

    Runnable mHideRunnable = () -> {
        int flags;
        // This work only for android 4.4+
        // hide navigation bar permanently in android activity
        // touch the screen, the navigation bar will not show
        flags = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        // must be executed in main thread :)
        getWindow().getDecorView().setSystemUiVisibility(flags);
    };

    @Override
    protected void onDestroy() {
        //取消View的关联
        if (null != mPresenter )
            mPresenter.detachView();
        Utils.deleteAllFiles(new File(android.os.Environment.getExternalStorageDirectory() + "/Android/data/anime.project.dilidili/cache"));
        Utils.deleteAllFiles(new File(android.os.Environment.getExternalStorageDirectory() + "/Android/data/anime.project.dilidili/files/VideoCache/main"));
        super.onDestroy();
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        init();
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        application.showToastMsg(Utils.getString(BaseActivity.this, R.string.permissions_error));
        application.removeALLActivity();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }
}
