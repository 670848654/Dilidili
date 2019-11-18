package anime.project.dilidili.main.webview.normal;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import anime.project.dilidili.R;
import anime.project.dilidili.adapter.DramaAdapter;
import anime.project.dilidili.adapter.WebviewAdapter;
import anime.project.dilidili.api.Api;
import anime.project.dilidili.application.DiliDili;
import anime.project.dilidili.bean.AnimeDescBean;
import anime.project.dilidili.bean.ApiBean;
import anime.project.dilidili.bean.WebviewBean;
import anime.project.dilidili.database.DatabaseUtil;
import anime.project.dilidili.main.base.BaseActivity;
import anime.project.dilidili.main.base.Presenter;
import anime.project.dilidili.main.video.VideoContract;
import anime.project.dilidili.main.video.VideoPresenter;
import anime.project.dilidili.services.ClearVideoCacheService;
import anime.project.dilidili.util.SharedPreferencesUtils;
import anime.project.dilidili.util.Utils;
import anime.project.dilidili.util.VideoUtils;
import butterknife.BindView;
import butterknife.OnClick;

public class NormalWebActivity extends BaseActivity implements VideoContract.View {
    private final static String REFERER = "referer";
    private List<WebviewBean> list = new ArrayList<>();
    private String url = "", diliUrl = "";
    private String animeTitle;
    private String witchTitle;
    private String api = Api.SOURCE_1_API;
    private String newUrl = "";
    @BindView(R.id.webview)
    NormalWebView normalWebView;
    private ProgressBar pg;
    private List<AnimeDescBean> dramaList = new ArrayList<>();
    private DramaAdapter dramaAdapter;
    private BottomSheetDialog mBottomSheetDialog;
    private ProgressDialog p;
    private String[] videoUrlArr;
    @BindView(R.id.drama)
    FloatingActionButton drama;
    @BindView(R.id.title)
    TextView title;
    /**
     * 视频全屏参数
     */
    protected static final FrameLayout.LayoutParams COVER_SCREEN_PARAMS = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    private View customView;
    private FrameLayout fullscreenContainer;
    private WebChromeClient.CustomViewCallback customViewCallback;
    private VideoPresenter presenter;
    private List<ApiBean> apiList;
    private WebSettings webSettings;
    private boolean mModel = false;
    private MenuItem menuItem;
    private Boolean isFullscreen = false;
    @BindView(R.id.activity_main)
    CoordinatorLayout coordinatorLayout;

    @Override
    protected Presenter createPresenter() {
        return null;
    }

    @Override
    protected void loadData() {

    }

    @Override
    protected int setLayoutRes() {
        return R.layout.activity_webview_normal;
    }

    @Override
    protected void init() {
        hideGap();
        getBundle();
        initFab();
        initView();
        initApiData();
        initAdapter();
        initWebView();
    }

    @Override
    protected void initBeforeView() {

    }

    public void initFab() {
        if (Utils.checkHasNavigationBar(this)) {
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) drama.getLayoutParams();
            params.setMargins(Utils.dpToPx(this, 16),
                    Utils.dpToPx(this, 16),
                    Utils.dpToPx(this, 16),
                    Utils.getNavigationBarHeight(this));
            drama.setLayoutParams(params);
        }
    }

    public void getBundle() {
        Bundle bundle = getIntent().getExtras();
        if (!bundle.isEmpty()) {
            witchTitle = bundle.getString("witchTitle");
            animeTitle = bundle.getString("title");
            url = bundle.getString("url");
            diliUrl = bundle.getString("dili");
            dramaList = (List<AnimeDescBean>) bundle.getSerializable("list");
        }
    }

    public void initView() {
        pg = findViewById(R.id.progressBar);
        if (Utils.checkHasNavigationBar(this)) {
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) coordinatorLayout.getLayoutParams();
            params.setMargins(0,
                    0,
                    0,
                    Utils.getNavigationBarHeight(this));
            coordinatorLayout.setLayoutParams(params);
        }
    }

    private void initApiData() {
        apiList = DatabaseUtil.queryAllApi();
    }

    public void initAdapter() {
        list.add(new WebviewBean(Utils.getString(R.string.source_1), Api.SOURCE_1_API, true, false, false));
        list.add(new WebviewBean(Utils.getString(R.string.source_2), Api.SOURCE_2_API, false, false, false));
        list.add(new WebviewBean(Utils.getString(R.string.source_3), Api.SOURCE_3_API, false, false, false));
        list.add(new WebviewBean(Utils.getString(R.string.source_4), Api.SOURCE_4_API, false, false, false));
        list.add(new WebviewBean(Utils.getString(R.string.source_5), Api.SOURCE_5_API, false, false, false));
        list.add(new WebviewBean(Utils.getString(R.string.source_6), Api.SOURCE_6_API, false, false, false));
        if (apiList.size() > 0) {
            for (int i = 0; i < apiList.size(); i++) {
                list.add(new WebviewBean(apiList.get(i).getTitle(), apiList.get(i).getUrl(), false, false, false));
            }
        }
        list.add(new WebviewBean(Utils.getString(R.string.source_8), "", false, true, false));
        list.add(new WebviewBean(Utils.getString(R.string.source_9), "", false, false, true));
        View dramaView = LayoutInflater.from(this).inflate(R.layout.dialog_webview, null);
        RecyclerView lineRecyclerView = dramaView.findViewById(R.id.line_list);
        lineRecyclerView.setNestedScrollingEnabled(false);
        lineRecyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        WebviewAdapter webviewAdapter = new WebviewAdapter(this, list);
        webviewAdapter.setOnItemClickListener((adapter, view, position) -> {
            if (list.get(position).isOriginalPage()) {
                Utils.viewInChrome(NormalWebActivity.this, diliUrl);
            } else if (list.get(position).isOriginalAddress()) {
                Utils.viewInChrome(NormalWebActivity.this, url);
            } else {
                mBottomSheetDialog.dismiss();
                for (int i = 0; i < list.size(); i++) {
                    list.get(i).setSelect(false);
                }
                list.get(position).setSelect(true);
                adapter.notifyDataSetChanged();
                Map<String, String> map = new HashMap<>();
                map.put(REFERER, diliUrl);
                api = list.get(position).getUrl();
                newUrl = api + url;
                normalWebView.loadUrl(newUrl, map);
            }
        });
        lineRecyclerView.setAdapter(webviewAdapter);
        RecyclerView dramaRecyclerView = dramaView.findViewById(R.id.drama_list);
        dramaRecyclerView.setNestedScrollingEnabled(false);
        TextView titleTextView = dramaView.findViewById(R.id.title);
        titleTextView.setText(animeTitle);
        dramaRecyclerView.setLayoutManager(new GridLayoutManager(this, 5));
        dramaAdapter = new DramaAdapter(this, dramaList);
        dramaAdapter.setOnItemClickListener((adapter, view, position) -> {
            if (!Utils.isFastClick()) return;
            setResult(0x20);
            mBottomSheetDialog.dismiss();
            final AnimeDescBean bean = (AnimeDescBean) adapter.getItem(position);
            switch (bean.getType()) {
                case "play":
//                    toolbar.setSubtitle("loading...");
                    p = Utils.getProDialog(NormalWebActivity.this, R.string.parsing);
                    Button v = (Button) adapter.getViewByPosition(dramaRecyclerView, position, R.id.tag_group);
                    v.setBackgroundResource(R.drawable.button_selected);
                    v.setTextColor(getResources().getColor(R.color.item_selected_color));
                    bean.setSelect(true);
                    diliUrl = VideoUtils.getDiliUrl(bean.getUrl());
                    witchTitle = animeTitle + " - 第" + bean.getTitle()+"话";
                    presenter = new VideoPresenter(animeTitle, diliUrl, NormalWebActivity.this);
                    presenter.loadData(true);
                    break;
            }
        });
        dramaRecyclerView.setAdapter(dramaAdapter);
        mBottomSheetDialog = new BottomSheetDialog(this);
        mBottomSheetDialog.setContentView(dramaView);
    }

    public void goToPlay(String videoUrl) {
        new Handler().postDelayed(() -> {
            //获取播放地址数组
            String[] arr = VideoUtils.removeByIndex(videoUrl.split("http"), 0);
            if (arr.length == 1) oneSource(arr);
            else multipleSource(arr);
        }, 200);
    }

    public void initWebView() {
        webSettings = normalWebView.getSettings();
//        webSettings.setUserAgentString(PHONE_USER_AGENT);
        getWindow().getDecorView().addOnLayoutChangeListener((v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
            ArrayList<View> outView = new ArrayList<>();
            getWindow().getDecorView().findViewsWithText(outView, "下载该视频", View.FIND_VIEWS_WITH_TEXT);
            if (outView != null && outView.size() > 0) {
                outView.get(0).setVisibility(View.GONE);
            }
        });
        normalWebView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
        //视频源地址
//        java.net.URL urlHost;
//        try {
//            urlHost = new java.net.URL(url);
//            toolbar.setSubtitle(urlHost.getHost());
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        }
        if (url.contains("yylep")) {
            newUrl = url;
            Map<String, String> map = new HashMap<>();
            map.put(REFERER, diliUrl);
            normalWebView.loadUrl(newUrl, map);
        } else {
            newUrl = Api.SOURCE_1_API + url;
            normalWebView.loadUrl(newUrl);
        }
        initHardwareAccelerate();
        normalWebView.setWebChromeClient(new WebChromeClient() {
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
                FrameLayout frameLayout = new FrameLayout(NormalWebActivity.this);
                frameLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                return frameLayout;
            }

            @Override
            public void onShowCustomView(View view, CustomViewCallback callback) {
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
    private void showCustomView(View view, WebChromeClient.CustomViewCallback callback) {
        // if a view already exists then immediately terminate the new one
        if (customView != null) {
            callback.onCustomViewHidden();
            return;
        }
        NormalWebActivity.this.getWindow().getDecorView();
        FrameLayout decor = (FrameLayout) getWindow().getDecorView();
        fullscreenContainer = new FullscreenHolder(NormalWebActivity.this);
        fullscreenContainer.addView(view, COVER_SCREEN_PARAMS);
        decor.addView(fullscreenContainer, COVER_SCREEN_PARAMS);
        customView = view;
        isFullscreen = true;
        hideNavBar();
        customViewCallback = callback;
        // 设置横屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
    }

    /**
     * 隐藏视频全屏
     */
    private void hideFullCustomView() {
        if (customView == null) {
            return;
        }
        isFullscreen = false;
        showNavBar();
        FrameLayout decor = (FrameLayout) getWindow().getDecorView();
        decor.removeView(fullscreenContainer);
        fullscreenContainer = null;
        customView = null;
        customViewCallback.onCustomViewHidden();
        normalWebView.setVisibility(View.VISIBLE);
        // 设置竖屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }


    @Override
    public void cancelDialog() {
        Utils.cancelProDialog(p);
    }

    @Override
    public void getVideoSuccess(String url) {
        runOnUiThread(() -> goToPlay(url));
    }

    @Override
    public void getVideoEmpty() {
        runOnUiThread(() -> VideoUtils.showErrorInfo(NormalWebActivity.this, diliUrl));
    }

    @Override
    public void getVideoError() {
        //网络出错
        runOnUiThread(() -> application.showToastMsg(Utils.getString(R.string.error_700)));
    }

    @Override
    public void showSuccessDramaView(List<AnimeDescBean> list) {
        dramaList = list;
        runOnUiThread(() -> dramaAdapter.setNewData(dramaList));
    }

    @Override
    public void errorDramaView() {
        runOnUiThread(() -> application.showToastMsg("获取剧集信息出错"));
    }

    @Override
    public void hasBanIp() {
        Log.e("ban", "发现禁止IP");
        runOnUiThread(() -> {
            application.showToastMsg((Utils.getString(R.string.has_ban_ip)));
            presenter = new VideoPresenter(animeTitle, diliUrl + DiliDili.NEW_VERSION, this);
            presenter.loadData(true);
        });
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
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                    WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        if (isFullscreen){
            hideFullCustomView();
        }else {
            if (normalWebView.canGoBack()) normalWebView.goBack();//返回上个页面
            else finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isFullscreen) hideNavBar();
        else showNavBar();
    }

    @Override
    protected void onDestroy() {
        //销毁Webview
        if (normalWebView != null)
            normalWebView.destroy();
        if (null != presenter)
            presenter.detachView();
        startService(new Intent(this, ClearVideoCacheService.class));
        super.onDestroy();
    }


    /**
     * 只有一个播放地址
     * @param arr
     */
    private void oneSource(String[] arr) {
        url = "http" + arr[0];
        if (url.contains(".m3u8") || url.contains(".mp4")) {
            switch ((Integer) SharedPreferencesUtils.getParam(getApplicationContext(), "player", 0)) {
                case 0:
                    //调用播放器
                    VideoUtils.openPlayer(false, this, witchTitle, url, animeTitle, diliUrl, dramaList);
                    break;
                case 1:
                    Utils.selectVideoPlayer(NormalWebActivity.this, url);
                    break;
            }
        } else loadUrl();
    }

    /**
     * 多个播放地址
     * @param arr
     */
    private void multipleSource(String[] arr) {
        videoUrlArr = new String[arr.length];
        String[] videoTitleArr = new String[arr.length];
        VideoUtils.showMultipleVideoSources(this,
                arr,
                videoTitleArr,
                videoUrlArr,
                (dialog, index) -> {
                    url = videoUrlArr[index];
                    if (url.contains(".m3u8") || url.contains(".mp4")) {
                        switch ((Integer) SharedPreferencesUtils.getParam(getApplicationContext(), "player", 0)) {
                            case 0:
                                //调用播放器
                                VideoUtils.openPlayer(false, this, witchTitle, url, animeTitle, diliUrl, dramaList);
                                break;
                            case 1:
                                Utils.selectVideoPlayer(NormalWebActivity.this, url);
                                break;
                        }
                    } else loadUrl();
                });
    }

    /**
     * 加载新地址
     */
    private void loadUrl() {
//        toolbar.setTitle(witchTitle);
//        //视频源地址
//        URL urlHost;
//        try {
//            urlHost = new URL(url);
//            toolbar.setSubtitle(urlHost.getHost());
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        }
        title.setText(witchTitle);
        Map<String, String> map = new HashMap<>();
        map.put(REFERER, diliUrl);
        newUrl = api + url;
        normalWebView.loadUrl(newUrl, map);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.web_menu, menu);
        menuItem = menu.findItem(R.id.model);
        return true;
    }

    @OnClick(R.id.drama)
    public void dramaClick() {
        if (!mBottomSheetDialog.isShowing()) mBottomSheetDialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.model:
                if (mModel) {
                    //切换成手机版
                    mModel = false;
                    webSettings.setUserAgentString(NormalWebView.PHONE_USER_AGENT);
                    menuItem.setIcon(R.drawable.baseline_stay_primary_portrait_white_48dp);
                    menuItem.setTitle(Utils.getString(R.string.phone_model));
                    application.showToastMsg("已切换成手机版");
                } else {
                    //切换成电脑版
                    mModel = true;
                    webSettings.setUserAgentString(NormalWebView.PC_USER_AGENT);
                    menuItem.setIcon(R.drawable.baseline_language_white_48dp);
                    menuItem.setTitle(Utils.getString(R.string.pc_model));
                    application.showToastMsg("已切换成电脑版");
                }
                Map<String, String> map = new HashMap<>();
                map.put(REFERER, diliUrl);
                normalWebView.loadUrl(newUrl, map);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.back)
    public void back() {
        finish();
    }
}
