package anime.project.dilidili.main.webview;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.smtt.export.external.interfaces.IX5WebChromeClient;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebView;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import anime.project.dilidili.R;
import anime.project.dilidili.adapter.DramaAdapter;
import anime.project.dilidili.adapter.WebviewAdapter;
import anime.project.dilidili.api.Api;
import anime.project.dilidili.bean.AnimeDescBean;
import anime.project.dilidili.bean.ApiBean;
import anime.project.dilidili.bean.WebviewBean;
import anime.project.dilidili.database.DatabaseUtil;
import anime.project.dilidili.main.base.BaseActivity;
import anime.project.dilidili.main.base.Presenter;
import anime.project.dilidili.main.player.PlayerActivity;
import anime.project.dilidili.main.video.VideoContract;
import anime.project.dilidili.main.video.VideoPresenter;
import anime.project.dilidili.main.video.VideoUtils;
import anime.project.dilidili.util.SharedPreferencesUtils;
import anime.project.dilidili.util.StatusBarUtil;
import anime.project.dilidili.util.Utils;
import butterknife.BindView;

public class WebActivity extends BaseActivity implements VideoContract.View {
    @BindView(R.id.rv_list)
    RecyclerView recyclerView;
    private WebviewAdapter adapter;
    private List<WebviewBean> list = new ArrayList<>();
    private final static String REFERER = "referer";
    private String url = "", diliUrl = "";
    private String animeTilte;
    private String witchTitle;
    private String api;
    private String newUrl = "";
    @BindView(R.id.x5_webview)
    X5WebView mX5WebView;
    private ProgressBar pg;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.title)
    TextView titleView;
    @BindView(R.id.rv_list_two)
    RecyclerView recyclerView2;
    private List<AnimeDescBean> dramaList = new ArrayList<>();
    private DramaAdapter dramaAdapter;
    private ProgressDialog p;
    private AlertDialog alertDialog;
    private String[] videoUrlArr;
    private String[] videoTitleArr;
    /**
     * 视频全屏参数
     */
    protected static final FrameLayout.LayoutParams COVER_SCREEN_PARAMS = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    private View customView;
    private FrameLayout fullscreenContainer;
    private IX5WebChromeClient.CustomViewCallback customViewCallback;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.nav_view)
    LinearLayout linearLayout;
    private VideoPresenter presenter;
    private List<ApiBean> apiList;

    @Override
    protected Presenter createPresenter() {
        return null;
    }

    @Override
    protected void loadData() {

    }

    @Override
    protected int setLayoutRes() {
        return R.layout.activity_webview;
    }

    @Override
    protected void init() {
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        //Android P 异形屏适配
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            getWindow().setAttributes(lp);
        }
        hideNavBar();
        getBundle();
        initToolbar();
        initView();
        initApiData();
        initAdapter();
        initWebView();
    }

    @Override
    protected void initBeforeView() {
        StatusBarUtil.setTranslucent(this, 0);
    }

    public void initToolbar() {
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(view -> {
            if (drawerLayout.isDrawerOpen(GravityCompat.END))
                drawerLayout.closeDrawer(GravityCompat.END);
            else
                finish();
        });
    }

    public void getBundle() {
        Bundle bundle = getIntent().getExtras();
        if (!bundle.isEmpty()) {
            animeTilte = bundle.getString("title");
            url = bundle.getString("url");
            diliUrl = bundle.getString("dili");
            dramaList = (List<AnimeDescBean>) bundle.getSerializable("list");
            titleView.setText(animeTilte);
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }
    }

    public void initView() {
        pg = findViewById(R.id.progressBar);
    }

    private void initApiData() {
        apiList = DatabaseUtil.queryAllApi();
    }

    public void initAdapter() {
        list.add(new WebviewBean(Utils.getString(WebActivity.this, R.string.source_1), Api.SOURCE_1_API, true, false, false));
        list.add(new WebviewBean(Utils.getString(WebActivity.this, R.string.source_2), Api.SOURCE_2_API, false, false, false));
        list.add(new WebviewBean(Utils.getString(WebActivity.this, R.string.source_3), Api.SOURCE_3_API, false, false, false));
        list.add(new WebviewBean(Utils.getString(WebActivity.this, R.string.source_4), Api.SOURCE_4_API, false, false, false));
        list.add(new WebviewBean(Utils.getString(WebActivity.this, R.string.source_5), Api.SOURCE_5_API, false, false, false));
        list.add(new WebviewBean(Utils.getString(WebActivity.this, R.string.source_6), Api.SOURCE_6_API, false, false, false));
        if (apiList.size() > 0) {
            for (int i = 0; i < apiList.size(); i++) {
                list.add(new WebviewBean(apiList.get(i).getTitle(), apiList.get(i).getUrl(), false, false, false));
            }
        }
        list.add(new WebviewBean(Utils.getString(WebActivity.this, R.string.source_8), "", false, true, false));
        list.add(new WebviewBean(Utils.getString(WebActivity.this, R.string.source_9), "", false, false, true));
        recyclerView.setNestedScrollingEnabled(false);
        LinearLayoutManager ms = new LinearLayoutManager(this);
        ms.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(ms);
        adapter = new WebviewAdapter(this, list);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener((adapter, view, position) -> {
            if (list.get(position).isOriginalPage()) {
                Utils.viewInBrowser(WebActivity.this, diliUrl);
            } else if (list.get(position).isOriginalAddress()) {
                Utils.viewInBrowser(WebActivity.this, url);
            } else {
                for (int i = 0; i < list.size(); i++) {
                    list.get(i).setSelect(false);
                }
                list.get(position).setSelect(true);
                adapter.notifyDataSetChanged();
                Map<String, String> map = new HashMap<>();
                map.put(REFERER, diliUrl);
                api = list.get(position).getUrl();
                newUrl = api + url;
                mX5WebView.loadUrl(newUrl, map);
            }
        });
        recyclerView2.setLayoutManager(new GridLayoutManager(this, 4));
        dramaAdapter = new DramaAdapter(this, dramaList);
        recyclerView2.setAdapter(dramaAdapter);
        dramaAdapter.setOnItemClickListener((adapter, view, position) -> {
            if (Utils.isFastClick()) {
                setResult(0x20);
                drawerLayout.closeDrawer(GravityCompat.END);
                final AnimeDescBean bean = (AnimeDescBean) adapter.getItem(position);
                switch (bean.getType()) {
                    case "play":
                        p = Utils.getProDialog(WebActivity.this, "解析中,请稍后...");
                        Button v = (Button) adapter.getViewByPosition(recyclerView2, position, R.id.tag_group);
                        v.setBackground(getResources().getDrawable(R.drawable.button_selected, null));
                        diliUrl = bean.getUrl();
                        witchTitle = animeTilte + " - " + bean.getTitle();
                        presenter = new VideoPresenter(animeTilte, bean.getUrl(), WebActivity.this);
                        presenter.loadData(true);
                        break;
                }

            }
        });
    }

    public void goToPlay(String videoUrl) {
        new Handler().postDelayed(() -> {
            String[] arr = VideoUtils.removeByIndex(videoUrl.split("http"), 0);
            //如果播放地址只有1个
            if (arr.length == 1) {
                String url = "http" + arr[0];
                if (url.contains(".m3u8") || url.contains(".mp4")) {
                    switch ((Integer) SharedPreferencesUtils.getParam(getApplicationContext(), "player", 0)) {
                        case 0:
                            //调用播放器
                            Bundle bundle = new Bundle();
                            bundle.putString("title", witchTitle);
                            bundle.putString("url", url);
                            bundle.putString("animeTitle", animeTilte);
                            bundle.putString("dili", diliUrl);
                            bundle.putSerializable("list", (Serializable) dramaList);
                            startActivity(new Intent(WebActivity.this, PlayerActivity.class).putExtras(bundle));
                            WebActivity.this.finish();
                            break;
                        case 1:
                            Utils.selectVideoPlayer(WebActivity.this, url);
                            break;
                    }
                } else {
                    //视频源地址
                    java.net.URL urlHost;
                    try {
                        urlHost = new java.net.URL(url);
                        toolbar.setTitle(urlHost.getHost());
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                    Map<String, String> map = new HashMap<>();
                    map.put(REFERER, diliUrl);
                    newUrl = api + url;
                    mX5WebView.loadUrl(newUrl, map);
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
        }, 200);
    }

    private void selectVideoDialog() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("选择视频源");
        builder.setCancelable(false);
        builder.setItems(videoTitleArr, (dialog, index) -> {
            if (videoUrlArr[index].contains(".m3u8") || videoUrlArr[index].contains(".mp4")) {
                switch ((Integer) SharedPreferencesUtils.getParam(getApplicationContext(), "player", 0)) {
                    case 0:
                        //调用播放器
                        Bundle bundle = new Bundle();
                        bundle.putString("title", witchTitle);
                        bundle.putString("url", videoUrlArr[index]);
                        bundle.putString("animeTitle", animeTilte);
                        bundle.putString("dili", diliUrl);
                        bundle.putSerializable("list", (Serializable) dramaList);
                        startActivity(new Intent(WebActivity.this, PlayerActivity.class).putExtras(bundle));
                        WebActivity.this.finish();
                        break;
                    case 1:
                        Utils.selectVideoPlayer(WebActivity.this, videoUrlArr[index]);
                        break;
                }
            } else {
                //视频源地址
                java.net.URL urlHost;
                try {
                    urlHost = new java.net.URL(url);
                    toolbar.setTitle(urlHost.getHost());
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                Map<String, String> map = new HashMap<>();
                map.put(REFERER, diliUrl);
                newUrl = api + url;
                mX5WebView.loadUrl(newUrl, map);
            }
        });
        builder.setNegativeButton("取消", (dialog, which) -> dialog.dismiss());
        alertDialog = builder.create();
        alertDialog.show();
    }

    public void initWebView() {
        linearLayout.setOnClickListener(view -> {
            return;
        });
        linearLayout.getBackground().mutate().setAlpha(150);//0~255透明度值
        getWindow().getDecorView().addOnLayoutChangeListener((v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
            ArrayList<View> outView = new ArrayList<>();
            getWindow().getDecorView().findViewsWithText(outView, "缓存", View.FIND_VIEWS_WITH_TEXT);
            if (outView != null && outView.size() > 0) {
                outView.get(0).setVisibility(View.GONE);
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mX5WebView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
        }
        //需要升级complieversion为21以上，不升级的话，用反射的方式来实现，如下代码所示
        try {
            Method m = WebSettings.class.getMethod("setMixedContentMode", int.class);
            if (m == null) {
                Log.e("WebSettings", "Error getting setMixedContentMode method");
            } else {
                m.invoke(mX5WebView.getSettings(), 2); // 2 = MIXED_CONTENT_COMPATIBILITY_MODE
                Log.i("WebSettings", "Successfully set MIXED_CONTENT_COMPATIBILITY_MODE");
            }
        } catch (Exception ex) {
            Log.e("WebSettings", "Error calling setMixedContentMode: " + ex.getMessage(), ex);
        }
        //视频源地址
        java.net.URL urlHost;
        try {
            urlHost = new java.net.URL(url);
            toolbar.setTitle(urlHost.getHost());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        if (url.indexOf("yylep") != -1) {
            newUrl = url;
            Map<String, String> map = new HashMap<>();
            map.put(REFERER, diliUrl);
            mX5WebView.loadUrl(newUrl, map);
        } else {
            newUrl = Api.SOURCE_1_API + url;
            mX5WebView.loadUrl(newUrl);
        }
        initHardwareAccelerate();
        if (null != mX5WebView.getX5WebViewExtension()) {
            Bundle data = new Bundle();
            data.putBoolean("standardFullScreen", false);
            //true表示标准全屏，false表示X5全屏；不设置默认false，
            data.putBoolean("supportLiteWnd", false);
            //false：关闭小窗；true：开启小窗；不设置默认true，
            data.putInt("DefaultVideoScreen", 2);
            //1：以页面内开始播放，2：以全屏开始播放；不设置默认：1
            mX5WebView.getX5WebViewExtension().invokeMiscMethod("setVideoParams", data);
        } else
            Toast.makeText(this, "X5内核加载失败,切换到系统内核", Toast.LENGTH_LONG).show();
        mX5WebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    pg.setVisibility(View.GONE);
                } else {
                    pg.setVisibility(View.VISIBLE);
                    pg.setProgress(newProgress);
                }

            }

            /** 视频播放相关的方法 **/

            @Override
            public View getVideoLoadingProgressView() {
                FrameLayout frameLayout = new FrameLayout(WebActivity.this);
                frameLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                return frameLayout;
            }

            @Override
            public void onShowCustomView(View view, IX5WebChromeClient.CustomViewCallback callback) {
                showCustomView(view, callback);
            }

            @Override
            public void onHideCustomView() {
                hideFullCustomView();
            }
        });
    }

    /**
     * 视频播放全屏
     **/
    private void showCustomView(View view, IX5WebChromeClient.CustomViewCallback callback) {
        // if a view already exists then immediately terminate the new one
        if (customView != null) {
            callback.onCustomViewHidden();
            return;
        }
        WebActivity.this.getWindow().getDecorView();
        FrameLayout decor = (FrameLayout) getWindow().getDecorView();
        fullscreenContainer = new FullscreenHolder(WebActivity.this);
        fullscreenContainer.addView(view, COVER_SCREEN_PARAMS);
        decor.addView(fullscreenContainer, COVER_SCREEN_PARAMS);
        customView = view;
        setStatusBarVisibility(false);
        customViewCallback = callback;
        // 设置横屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
    }

    /**
     * 隐藏视频全屏
     */
    private void hideFullCustomView() {
        if (customView == null) {
            return;
        }
        setStatusBarVisibility(true);
        FrameLayout decor = (FrameLayout) getWindow().getDecorView();
        decor.removeView(fullscreenContainer);
        fullscreenContainer = null;
        customView = null;
        customViewCallback.onCustomViewHidden();
        mX5WebView.setVisibility(View.VISIBLE);
        // 设置竖屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    private void setStatusBarVisibility(boolean visible) {
        int flag = visible ? 0 : WindowManager.LayoutParams.FLAG_FULLSCREEN;
        getWindow().setFlags(flag, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    public void getVideoSuccess(String url) {
        runOnUiThread(() -> {
            Utils.cancelProDoalog(p);
            goToPlay(url);
        });
    }

    @Override
    public void getVideoEmpty() {
        runOnUiThread(() -> {
            Utils.cancelProDoalog(p);
            VideoUtils.showErrorInfo(WebActivity.this, diliUrl);
        });
    }

    @Override
    public void getVideoError() {
        Utils.cancelProDoalog(p);
        //网络出错
        Toast.makeText(WebActivity.this, Utils.getString(WebActivity.this, R.string.error_700), Toast.LENGTH_LONG).show();
    }

    @Override
    public void showSuccessDramaView(List<AnimeDescBean> list) {
        dramaList = list;
        runOnUiThread(() -> dramaAdapter.setNewData(dramaList));
    }

    @Override
    public void errorDramaView() {
        runOnUiThread(() -> Toast.makeText(WebActivity.this, "获取剧集信息出错", Toast.LENGTH_LONG).show());
    }


    /**
     * 全屏容器界面
     */
    static class FullscreenHolder extends FrameLayout {

        public FullscreenHolder(Context ctx) {
            super(ctx);
            setBackgroundColor(ctx.getResources().getColor(android.R.color.black));
        }

        @Override
        public boolean onTouchEvent(MotionEvent evt) {
            return true;
        }
    }

    /**
     * 启用硬件加速
     */
    private void initHardwareAccelerate() {
        try {
            if (Integer.parseInt(Build.VERSION.SDK) >= 11) {
                getWindow()
                        .setFlags(
                                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
            }
        } catch (Exception e) {
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    //销毁Webview
    @Override
    protected void onDestroy() {
        //释放资源
        if (mX5WebView != null)
            mX5WebView.destroy();
        if (null != presenter)
            presenter.detachView();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.web_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.module) {
            if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
                drawerLayout.closeDrawer(GravityCompat.END);
            } else
                drawerLayout.openDrawer(GravityCompat.END);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
