package anime.project.dilidili.main.player;

import android.app.PictureInPictureParams;
import android.app.ProgressDialog;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fanchen.sniffing.SniffingUICallback;
import com.fanchen.sniffing.SniffingVideo;
import com.fanchen.sniffing.web.SniffingUtil;

import java.util.ArrayList;
import java.util.List;

import anime.project.dilidili.R;
import anime.project.dilidili.adapter.DramaAdapter;
import anime.project.dilidili.application.DiliDili;
import anime.project.dilidili.bean.AnimeDescBean;
import anime.project.dilidili.main.base.BaseActivity;
import anime.project.dilidili.main.base.Presenter;
import anime.project.dilidili.main.video.VideoContract;
import anime.project.dilidili.main.video.VideoPresenter;
import anime.project.dilidili.util.SharedPreferencesUtils;
import anime.project.dilidili.util.StatusBarUtil;
import anime.project.dilidili.util.Utils;
import anime.project.dilidili.util.VideoUtils;
import butterknife.BindView;
import butterknife.OnClick;
import cn.jzvd.Jzvd;
import cn.jzvd.JzvdStd;

public class PlayerActivity extends BaseActivity implements VideoContract.View, JZPlayer.CompleteListener, JZPlayer.TouchListener, SniffingUICallback {
    @BindView(R.id.player)
    JZPlayer player;
    private String witchTitle, url, diliUrl;
    @BindView(R.id.rv_list)
    RecyclerView recyclerView;
    private List<AnimeDescBean> list = new ArrayList<>();
    private DramaAdapter dramaAdapter;
    private ProgressDialog p;
    private String animeTitle;
    private String[] videoUrlArr;
    @BindView(R.id.nav_view)
    LinearLayout linearLayout;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.anime_title)
    TextView titleView;
    @BindView(R.id.pic)
    TextView pic;
    private VideoPresenter presenter;

    @Override
    protected Presenter createPresenter() {
        return null;
    }

    @Override
    protected void loadData() {}

    @Override
    protected int setLayoutRes() {
        return R.layout.activity_play;
    }

    @Override
    protected void init() {
        DiliDili.addDestoryActivity(this, "player");
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        hideGap();
        Bundle bundle = getIntent().getExtras();
        init(bundle);
        initAdapter();
    }

    @Override
    protected void initBeforeView() {
        StatusBarUtil.setTranslucent(this, 0);
    }

    private void init(Bundle bundle) {
        //播放地址
        url = bundle.getString("url");
        //集数名称
        witchTitle = bundle.getString("title");
        //番剧名称
        animeTitle = bundle.getString("animeTitle");
        titleView.setText(animeTitle);
        //源地址
        diliUrl = bundle.getString("dili");
        //剧集list
        list = (List<AnimeDescBean>) bundle.getSerializable("list");
        //禁止冒泡
        linearLayout.setOnClickListener(view -> { return;});
        linearLayout.getBackground().mutate().setAlpha(150);
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        player.setListener(this, this, this);
        player.backButton.setOnClickListener(v -> finish());
//        if (Utils.isPad()) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            pic.setVisibility(View.GONE);
        } else {
            pic.setVisibility(View.VISIBLE);
        }
//        } else
//            pic.setVisibility(View.GONE);
        player.setUp(url, witchTitle, Jzvd.SCREEN_FULLSCREEN, JZExoPlayer.class);
        player.fullscreenButton.setOnClickListener(view -> {
            if (!Utils.isFastClick()) return;
            if (drawerLayout.isDrawerOpen(GravityCompat.END)) drawerLayout.closeDrawer(GravityCompat.END);
            else drawerLayout.openDrawer(GravityCompat.END);
        });
        Jzvd.FULLSCREEN_ORIENTATION = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE;
        Jzvd.NORMAL_ORIENTATION = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE;
        player.startButton.performClick();
        player.startVideo();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @OnClick(R.id.pic)
    public void startPic() {
        drawerLayout.closeDrawer(GravityCompat.END);
        new Handler().postDelayed(this::enterPicInPic, 500);
    }

    public void initAdapter() {
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        dramaAdapter = new DramaAdapter(this, list);
        recyclerView.setAdapter(dramaAdapter);
        dramaAdapter.setOnItemClickListener((adapter, view, position) -> {
            if (!Utils.isFastClick()) return;
            setResult(0x20);
            drawerLayout.closeDrawer(GravityCompat.END);
            final AnimeDescBean bean = (AnimeDescBean) adapter.getItem(position);
            switch (bean.getType()) {
                case "play":
                    p = Utils.getProDialog(PlayerActivity.this, R.string.parsing);
                    Button v = (Button) adapter.getViewByPosition(recyclerView, position, R.id.tag_group);
                    v.setBackgroundResource(R.drawable.button_selected);
                    v.setTextColor(getResources().getColor(R.color.item_selected_color));
                    bean.setSelect(true);
                    diliUrl = VideoUtils.getDiliUrl(bean.getUrl());
                    witchTitle = animeTitle + " - " + bean.getTitle();
                    presenter = new VideoPresenter(animeTitle, diliUrl, PlayerActivity.this);
                    presenter.loadData(true);
                    break;
            }

        });
    }

    /**
     * 播放视频
     * @param animeUrl
     */
    private void playAnime(String animeUrl) {
        url = animeUrl;
        if (url.contains(".mp4") || url.contains(".m3u8")) {
            cancelDialog();
            switch ((Integer) SharedPreferencesUtils.getParam(getApplicationContext(), "player", 0)) {
                case 0:
                    //调用播放器
                    Jzvd.releaseAllVideos();
                    player.setUp(url, witchTitle, Jzvd.SCREEN_FULLSCREEN, JZExoPlayer.class);
                    player.startVideo();
                    break;
                case 1:
                    Jzvd.releaseAllVideos();
                    Utils.selectVideoPlayer(PlayerActivity.this, url);
                    break;
            }
        }else {
            DiliDili.getInstance().showToastMsg(Utils.getString(R.string.should_be_used_web));
            SniffingUtil.get().activity(this).referer(animeUrl).callback(this).url(animeUrl).start();
        }
    }

    @OnClick({R.id.select_player, R.id.open_in_browser})
    public void onClick(TextView view) {
        switch (view.getId()) {
            case R.id.select_player:
                Utils.selectVideoPlayer(this, url);
                break;
            case R.id.open_in_browser:
                Utils.viewInChrome(this, diliUrl);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.END)) drawerLayout.closeDrawer(GravityCompat.END);
        else finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!inMultiWindow()) JzvdStd.goOnPlayOnPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideNavBar();
        if (!inMultiWindow()) JzvdStd.goOnPlayOnResume();
    }

    /**
     * 是否为分屏模式
     * @return
     */
    public boolean inMultiWindow(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) return this.isInMultiWindowMode();
        else return false;
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

    @Override
    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode, Configuration newConfig) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig);
        if (isInPictureInPictureMode) player.startPIP();
        else player.exitPIP();
    }

    @Override
    public void cancelDialog() {
        Utils.cancelProDialog(p);
    }

    @Override
    public void getVideoSuccess(String url) {
        runOnUiThread(() -> {
            hideNavBar();
            playAnime(url);
        });
    }

    @Override
    public void getVideoEmpty() {
        runOnUiThread(() -> {
            DiliDili.getInstance().showToastMsg(Utils.getString(R.string.open_web_view));
            VideoUtils.openDefaultWebview(this, diliUrl);
            this.finish();
        });
    }

    @Override
    public void getVideoError() {
        //网络出错
        runOnUiThread(() -> {
            hideNavBar();
            application.showErrorToastMsg(Utils.getString(R.string.error_700));
        });
    }

    @Override
    public void showSuccessDramaView(List<AnimeDescBean> dramaList) {
        list = dramaList;
        runOnUiThread(() -> dramaAdapter.setNewData(list));

    }

    @Override
    public void errorDramaView() {
        runOnUiThread(() -> application.showErrorToastMsg("获取剧集信息出错"));
    }

    @Override
    protected void onDestroy() {
        if (null != presenter) presenter.detachView();
        JzvdStd.releaseAllVideos();
        super.onDestroy();
    }

    @Override
    public void complete() {
        application.showSuccessToastMsg("播放完毕");
        if (!drawerLayout.isDrawerOpen(GravityCompat.END)) drawerLayout.openDrawer(GravityCompat.END);
    }

    @Override
    public void touch() {
        hideNavBar();
    }

    @Override
    public void onSniffingStart(View webView, String url) {

    }

    @Override
    public void onSniffingFinish(View webView, String url) {
        SniffingUtil.get().releaseWebView();
        cancelDialog();
    }

    @Override
    public void onSniffingSuccess(View webView, String url, List<SniffingVideo> videos) {
        List<String> urls = new ArrayList<>();
        for (SniffingVideo video : videos) {
            urls.add(video.getUrl());
        }
        VideoUtils.showMultipleVideoSources(this,
                urls,
                (dialog, index) -> playAnime(urls.get(index)), (dialog, which) -> {
                    cancelDialog();
                    dialog.dismiss();
                }, 1);
    }

    @Override
    public void onSniffingError(View webView, String url, int errorCode) {
        DiliDili.getInstance().showToastMsg(Utils.getString(R.string.open_web_view));
        VideoUtils.openDefaultWebview(this, diliUrl);
        this.finish();
    }
}
