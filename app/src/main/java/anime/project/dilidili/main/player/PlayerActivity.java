package anime.project.dilidili.main.player;

import android.app.PictureInPictureParams;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import anime.project.dilidili.R;
import anime.project.dilidili.database.DatabaseUtil;
import anime.project.dilidili.main.base.BaseActivity;
import anime.project.dilidili.bean.AnimeDescBean;
import anime.project.dilidili.bean.DramaAdapter;
import anime.project.dilidili.main.base.Presenter;
import anime.project.dilidili.main.video.VideoContract;
import anime.project.dilidili.main.video.VideoPresenter;
import anime.project.dilidili.main.video.VideoUtils;
import anime.project.dilidili.main.webview.WebActivity;
import anime.project.dilidili.util.SharedPreferencesUtils;
import anime.project.dilidili.util.StatusBarUtil;
import anime.project.dilidili.util.Utils;
import butterknife.BindView;
import butterknife.OnClick;
import cn.jzvd.Jzvd;
import cn.jzvd.JzvdStd;

public class PlayerActivity extends BaseActivity implements VideoContract.View {
    @BindView(R.id.player)
    JzvdStd player;
    private String title, url, diliUrl;
    @BindView(R.id.rv_list)
    RecyclerView recyclerView;
    private List<AnimeDescBean> list = new ArrayList<>();
    private DramaAdapter dramaAdapter;
    private static Handler sHandler;
    private boolean isPip = false;
    private ProgressDialog p;
    private AlertDialog alertDialog;
    private String title_t;
    private String[] videoUrlArr;
    private String[] videoTitleArr;
    @BindView(R.id.nav_view)
    RelativeLayout relativeLayout;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.title_t)
    TextView titleView;
    @BindView(R.id.pic)
    ImageView pic;
    private boolean is;
    private VideoPresenter presenter;

    @Override
    protected Presenter createPresenter() {
        return null;
    }

    @Override
    protected void loadData() {

    }

    @Override
    protected int setLayoutRes() {
        return R.layout.activity_play;
    }

    @Override
    protected void init() {
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        //Android 9 异形屏适配
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            getWindow().setAttributes(lp);
        }
        Bundle bundle = getIntent().getExtras();
        init(bundle);
        if (!is)
            initAdapter();
    }

    @Override
    protected void initBeforeView() {
        StatusBarUtil.setTranslucent(this, 0);
    }

    private void init(Bundle bundle) {
        is = bundle.getBoolean("is");
        //播放地址
        url = bundle.getString("url");
        //集数名称
        title = bundle.getString("title");
        if (!is) {
            //番剧名称
            title_t = bundle.getString("title_t");
            titleView.setText(title_t);
            //源地址
            diliUrl = bundle.getString("dili");
            //剧集list
            list = new ArrayList<>();
            list = (List<AnimeDescBean>) bundle.getSerializable("list");
            //创建番剧名
            DatabaseUtil.addAnime(title_t);
            relativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    return;
                }
            });
            relativeLayout.getBackground().mutate().setAlpha(150);//0~255透明度值
        } else {
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }
        player.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Jzvd.releaseAllVideos();
                finish();
            }
        });
        if (Utils.isPad(this)){
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O)
                pic.setVisibility(View.GONE);
            else
                pic.setVisibility(View.VISIBLE);
        }else
            pic.setVisibility(View.GONE);
        player.setUp(url, title, Jzvd.SCREEN_WINDOW_FULLSCREEN);
        if (is){
            if (Utils.isPad(this)){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    Glide.with(PlayerActivity.this).load(R.drawable.baseline_picture_in_picture_alt_white_48dp).into(player.fullscreenButton);
                else
                    player.fullscreenButton.setVisibility(View.INVISIBLE);
            }else
                player.fullscreenButton.setVisibility(View.INVISIBLE);
        }
        else
            Glide.with(PlayerActivity.this).load(R.drawable.baseline_view_module_white_48dp).into(player.fullscreenButton);
        player.fullscreenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Utils.isFastClick())
                    if (is){
                        isPip = true;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                            enterPicInPic();
                    }else {
                        if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
                            drawerLayout.closeDrawer(GravityCompat.END);
                        }else
                            drawerLayout.openDrawer(GravityCompat.END);
                    }
            }
        });
        Glide.with(PlayerActivity.this).load(R.drawable.baseline_arrow_back_white_24dp).apply(new RequestOptions().centerCrop()).into(player.backButton);
        player.backButton.setPadding(0,0,15,0);
        Jzvd.FULLSCREEN_ORIENTATION = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE;
        Jzvd.NORMAL_ORIENTATION = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE;
        player.startButton.performClick();
        player.startVideo();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @OnClick(R.id.pic)
    public void startPic(){
        drawerLayout.closeDrawer(GravityCompat.END);
        isPip = true;
        enterPicInPic();
    }

    public void initAdapter() {
        recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        dramaAdapter = new DramaAdapter(this, list);
        recyclerView.setAdapter(dramaAdapter);
        dramaAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (Utils.isFastClick()) {
                    setResult(0x20);
                    drawerLayout.closeDrawer(GravityCompat.END);
                    final AnimeDescBean bean = list.get(position);
                    switch (bean.getType()) {
                        case "play":
                            p = Utils.getProDialog(PlayerActivity.this, "解析中,请稍后...");
                            Button v = (Button) adapter.getViewByPosition(recyclerView, position, R.id.tag_group);
                            v.setBackground(getResources().getDrawable(R.drawable.button_selected, null));
                            diliUrl = bean.getUrl();
                            title = bean.getTitle();
                            presenter = new VideoPresenter(title_t, bean.getUrl(),PlayerActivity.this);
                            presenter.loadData(true);
                            break;
                    }

                }
            }
        });
    }

    public void goToPlay(String videoUrl){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                String[] arr = VideoUtils.removeByIndex(videoUrl.split("http"), 0);
                //如果播放地址只有1个
                if (arr.length == 1) {
                    String url = "http" + arr[0];
                    if (url.contains(".m3u8") || url.contains(".mp4")) {
                        switch ((Integer) SharedPreferencesUtils.getParam(getApplicationContext(), "player", 0)) {
                            case 0:
                                //调用播放器
                                Jzvd.releaseAllVideos();
                                player.setUp(url, title, Jzvd.SCREEN_WINDOW_FULLSCREEN);
                                if (is)
                                    Glide.with(PlayerActivity.this).load(R.drawable.baseline_picture_in_picture_alt_white_48dp).into(player.fullscreenButton);
                                else
                                    Glide.with(PlayerActivity.this).load(R.drawable.baseline_view_module_white_48dp).into(player.fullscreenButton);
                                player.startVideo();
                                break;
                            case 1:
                                Jzvd.releaseAllVideos();
                                Utils.selectVideoPlayer(PlayerActivity.this, url);
                                break;
                        }
                    } else {
                        Bundle bundle = new Bundle();
                        bundle.putString("title", title_t);
                        bundle.putString("url", url);
                        bundle.putString("dili", diliUrl);
                        bundle.putSerializable("list", (Serializable) list);
                        startActivity(new Intent(PlayerActivity.this, WebActivity.class).putExtras(bundle));
                        PlayerActivity.this.finish();
                    }
                } else {
                    videoUrlArr = new String[arr.length];
                    videoTitleArr = new String[arr.length];
                    for (int i = 0; i < arr.length; i++) {
                        String str = "http" + arr[i];
                        Log.e("video", str);
                        videoUrlArr[i] = str;
                        java.net.URL urlHost;
                        try {
                            urlHost = new java.net.URL(str);
                            if (str.contains(".mp4"))
                                videoTitleArr[i] = urlHost.getHost() + " <MP4> <播放器>";
                            else if (str.contains(".m3u8"))
                                videoTitleArr[i] = urlHost.getHost() + " <M3U8> <播放器>";
                            else
                                videoTitleArr[i] = urlHost.getHost() + " <HTML>";
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }
                    }
                    selectVideoDialog();
                }
            }
        }, 200);
    }

    private void selectVideoDialog() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("选择视频源");
        builder.setCancelable(false);
        builder.setItems(videoTitleArr, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int index) {
                if (videoUrlArr[index].contains(".m3u8") || videoUrlArr[index].contains(".mp4")) {
                    switch ((Integer) SharedPreferencesUtils.getParam(getApplicationContext(), "player", 0)) {
                        case 0:
                            //调用播放器
                            Jzvd.releaseAllVideos();
                            player.setUp(url, title, Jzvd.SCREEN_WINDOW_FULLSCREEN);
                            if (is)
                                Glide.with(PlayerActivity.this).load(R.drawable.baseline_picture_in_picture_alt_white_48dp).into(player.fullscreenButton);
                            else
                                Glide.with(PlayerActivity.this).load(R.drawable.baseline_view_module_white_48dp).into(player.fullscreenButton);
                            player.startVideo();
                            break;
                        case 1:
                            Jzvd.releaseAllVideos();
                            Utils.selectVideoPlayer(PlayerActivity.this, videoUrlArr[index]);
                            break;
                    }
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putString("title", title_t);
                    bundle.putString("url", url);
                    bundle.putString("dili", diliUrl);
                    bundle.putSerializable("list", (Serializable) list);
                    startActivity(new Intent(PlayerActivity.this, WebActivity.class).putExtras(bundle));
                    PlayerActivity.this.finish();
                }
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (drawerLayout.isDrawerOpen(GravityCompat.END))
            drawerLayout.closeDrawer(GravityCompat.END);
        else {
            Jzvd.releaseAllVideos();
            finish();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!isPip) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                if (!PlayerActivity.this.isInMultiWindowMode())
                {
                    JzvdStd.goOnPlayOnPause();
                    Jzvd.FULLSCREEN_ORIENTATION = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE;
                    Jzvd.NORMAL_ORIENTATION = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE;
                }
            }else {
                JzvdStd.goOnPlayOnPause();
                Jzvd.FULLSCREEN_ORIENTATION = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE;
                Jzvd.NORMAL_ORIENTATION = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideNavBar();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (!PlayerActivity.this.isInMultiWindowMode())
                JzvdStd.goOnPlayOnResume();
        }else
            JzvdStd.goOnPlayOnResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Jzvd.releaseAllVideos();
        Jzvd.FULLSCREEN_ORIENTATION = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE;
        Jzvd.NORMAL_ORIENTATION = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE;
    }

    /**
     * Android 8.0 画中画
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void enterPicInPic() {
//        PictureInPictureParams.Builder builder = new PictureInPictureParams.Builder();
        // 设置宽高比例值，第一个参数表示分子，第二个参数表示分母
        // 下面的10/5=2，表示画中画窗口的宽度是高度的两倍
//        Rational aspectRatio = new Rational(10,5);
        // 设置画中画窗口的宽高比例
//        builder.setAspectRatio(aspectRatio);
        // 进入画中画模式，注意enterPictureInPictureMode是Android8.0之后新增的方法
//        enterPictureInPictureMode(builder.build());
        PictureInPictureParams builder = new PictureInPictureParams.Builder().build();
        enterPictureInPictureMode(builder);
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
        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                sHandler.postDelayed(mHideRunnable, 3000); // hide the navigation bar
            }
        });
    }

    Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            int flags;
            // This work only for android 4.4+
            // hide navigation bar permanently in android activity
            // touch the screen, the navigation bar will not show
            flags = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN;
            // must be executed in main thread :)
            getWindow().getDecorView().setSystemUiVisibility(flags);
        }
    };

    @Override
    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode, Configuration newConfig) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig);
        isPip = isInPictureInPictureMode;
        if (isInPictureInPictureMode) {
            player.fullscreenButton.setVisibility(View.INVISIBLE);
            player.backButton.setVisibility(View.INVISIBLE);
            player.titleTextView.setVisibility(View.INVISIBLE);
            player.batteryTimeLayout.setVisibility(View.INVISIBLE);
        } else {
            player.fullscreenButton.setVisibility(View.VISIBLE);
            player.backButton.setVisibility(View.VISIBLE);
            player.titleTextView.setVisibility(View.VISIBLE);
            player.batteryTimeLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void getVideoSuccess(String url) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Utils.cancelProDoalog(p);
                goToPlay(url);
            }
        });
    }

    @Override
    public void getVideoEmpty() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Utils.cancelProDoalog(p);
                VideoUtils.showErrorInfo(PlayerActivity.this, diliUrl);
            }
        });
    }

    @Override
    public void getVideoError() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Utils.cancelProDoalog(p);
                //网络出错
                Toast.makeText(PlayerActivity.this, Utils.getString(PlayerActivity.this, R.string.error_700), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != presenter)
            presenter.detachView();
    }
}
