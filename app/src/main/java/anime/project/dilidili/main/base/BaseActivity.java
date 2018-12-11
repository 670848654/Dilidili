package anime.project.dilidili.main.base;

import android.Manifest;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import anime.project.dilidili.R;
import anime.project.dilidili.application.DiliDili;
import anime.project.dilidili.database.DatabaseUtil;
import anime.project.dilidili.util.Utils;
import butterknife.ButterKnife;
import pub.devrel.easypermissions.EasyPermissions;

public abstract class BaseActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks{
    public View errorView, emptyView, userEmptyView;
    public TextView errorTitle;
    public DiliDili application;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(setLayoutRes());
        ButterKnife.bind(this);
        if (application == null) {
            // 得到Application对象
            application = (DiliDili) getApplication();
        }
        application.addActivity_(this);
        if (EasyPermissions.hasPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            //创建database路路径
            Utils.creatFile();
            DatabaseUtil.CREATE_TABLES();
            init();
        } else {
            EasyPermissions.requestPermissions(this, Utils.getString(BaseActivity.this, R.string.permissions),
                    300, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
    }

    protected abstract int setLayoutRes();

    protected abstract void init();

    protected abstract void initBeforView();

    public void initViews(RecyclerView recyclerView){
        errorView = getLayoutInflater().inflate(R.layout.base_error_view, (ViewGroup) recyclerView.getParent(), false);
        errorTitle = errorView.findViewById(R.id.title);
        emptyView = getLayoutInflater().inflate(R.layout.base_emnty_view, (ViewGroup) recyclerView.getParent(), false);
        userEmptyView = getLayoutInflater().inflate(R.layout.base_emtpy_favorite_view, (ViewGroup) recyclerView.getParent(), false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Utils.deleteAllFiles(new File(android.os.Environment.getExternalStorageDirectory() + "/Android/data/anime.project.dilidili/cache"));
        Utils.deleteAllFiles(new File(android.os.Environment.getExternalStorageDirectory() + "/Android/data/anime.project.dilidili/files/VideoCache/main"));
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        init();
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        Toast.makeText(this, Utils.getString(BaseActivity.this, R.string.permissions_error), Toast.LENGTH_SHORT).show();
        application.removeALLActivity_();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }
}
