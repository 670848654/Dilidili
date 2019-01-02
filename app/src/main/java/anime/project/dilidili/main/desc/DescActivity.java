package anime.project.dilidili.main.desc;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.r0adkll.slidr.Slidr;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import anime.project.dilidili.R;
import anime.project.dilidili.adapter.DescAdapter;
import anime.project.dilidili.api.Api;
import anime.project.dilidili.bean.AnimeDescBean;
import anime.project.dilidili.bean.AnimeListBean;
import anime.project.dilidili.config.AnimeType;
import anime.project.dilidili.database.DatabaseUtil;
import anime.project.dilidili.main.base.BaseActivity;
import anime.project.dilidili.main.player.PlayerActivity;
import anime.project.dilidili.main.video.VideoContract;
import anime.project.dilidili.main.video.VideoPresenter;
import anime.project.dilidili.main.video.VideoUtils;
import anime.project.dilidili.main.webview.WebActivity;
import anime.project.dilidili.util.SharedPreferencesUtils;
import anime.project.dilidili.util.StatusBarUtil;
import anime.project.dilidili.util.Utils;
import butterknife.BindView;
import jp.wasabeef.blurry.Blurry;

public class DescActivity extends BaseActivity<DescContract.View, DescPresenter> implements DescContract.View, VideoContract.View {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.rv_list)
    RecyclerView mRecyclerView;
    private DescAdapter adapter;
    @BindView(R.id.mSwipe)
    SwipeRefreshLayout mSwipe;
    private List<MultiItemEntity> multiItemList = new ArrayList<>();
    private List<AnimeDescBean> drama = new ArrayList<>();
    @BindView(R.id.title_img)
    ImageView imageView;
    @BindView(R.id.collaps_toolbar_layout)
    CollapsingToolbarLayout ct;
    private String url, diliUrl;
    private String animeTitle;
    private String witchTitle;
    private ProgressDialog p;
    private AlertDialog alertDialog;
    @BindView(R.id.favorite)
    FloatingActionButton favorite;
    private boolean isFavorite;
    private String[] videoUrlArr;
    private String[] videoTitleArr;
    private VideoPresenter videoPresenter;
    private AnimeListBean animeListBean = new AnimeListBean();

    @Override
    protected DescPresenter createPresenter() {
        return new DescPresenter(this, url, this);
    }

    @Override
    protected void loadData() {
        mPresenter.loadData(true);
    }

    @Override
    protected int setLayoutRes() {
        return R.layout.activity_desc;
    }

    @Override
    protected void init() {
        StatusBarUtil.setColorForSwipeBack(DescActivity.this, getResources().getColor(R.color.night), 0);
        Slidr.attach(this, Utils.defaultInit());
        getBundle();
        initToolbar();
        initFab();
        initSwipe();
        initAdapter();
    }

    @Override
    protected void initBeforeView() {

    }

    public void getBundle() {
        Bundle bundle = getIntent().getExtras();
        if (!bundle.isEmpty()) {
            url = bundle.getString("url");
            animeTitle = bundle.getString("name");
        }
    }

    public void initToolbar() {
        toolbar.setTitle(Utils.getString(R.string.loading));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(view -> finish());
    }

    public void initFab() {
        favorite.setOnClickListener(view -> {
            if (Utils.isFastClick())
                favoriteAnime();
        });
    }

    public void initSwipe() {
        mSwipe.setColorSchemeResources(R.color.pink500, R.color.blue500, R.color.purple500);
        mSwipe.setOnRefreshListener(() -> {
            multiItemList.clear();
            adapter.setNewData(multiItemList);
            mPresenter.loadData(true);
        });
        mSwipe.setRefreshing(true);
    }

    public void initAdapter() {
        adapter = new DescAdapter(multiItemList, DescActivity.this);
        adapter.openLoadAnimation();
        adapter.setOnItemClickListener((adapter, view, position) -> {
            if (Utils.isFastClick()) {
                final AnimeDescBean bean = (AnimeDescBean) adapter.getItem(position);
                switch (bean.getType()) {
                    case "play":
                        p = Utils.getProDialog(DescActivity.this, R.string.parsing);
                        Button v = (Button) adapter.getViewByPosition(mRecyclerView, position, R.id.tag_group);
                        v.setBackground(getResources().getDrawable(R.drawable.button_selected, null));
                        diliUrl = bean.getUrl();
                        witchTitle = animeTitle + " - " + bean.getTitle();
                        videoPresenter = new VideoPresenter(animeListBean.getTitle(), bean.getUrl(), DescActivity.this);
                        videoPresenter.loadData(true);
                        break;
                    case "html":
                        Bundle bundle = new Bundle();
                        if (bean.getUrl().indexOf("http") == -1)
                            bundle.putString("url", Api.URL + bean.getUrl());
                        else
                            bundle.putString("url", bean.getUrl());
                        startActivityForResult(new Intent(DescActivity.this, DescActivity.class).putExtras(bundle), 3000);
                        break;
                }
            }
        });
        adapter.setOnItemChildClickListener((adapter, view, position) -> {
            if (Utils.isFastClick()) {
                final AnimeDescBean bean = (AnimeDescBean) adapter.getItem(position);
                switch (bean.getType()) {
                    case "down":
                        if (!bean.getUrl().isEmpty())
                            Utils.viewInBrowser(bean.getUrl());
                        else
                            Utils.showSnackbar(toolbar, Utils.getString(R.string.no_resources));
                        break;
                }
            }
        });
        adapter.setOnItemChildLongClickListener((adapter, view, position) -> {
            if (Utils.isFastClick()) {
                final AnimeDescBean bean = (AnimeDescBean) adapter.getItem(position);
                switch (bean.getType()) {
                    case "down":
                        Utils.putTextIntoClip(bean.getTitle());
                        application.showToastMsg(bean.getTitle() + "已复制到剪切板");
                        break;
                }
            }
            return true;
        });
        mRecyclerView.setAdapter(adapter);
        adapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);
        adapter.isFirstOnly((Boolean) SharedPreferencesUtils.getParam(DescActivity.this, "anim_is_first", true));//init firstOnly state
    }

    public void goToPlay(String videoUrl) {
        new Handler().postDelayed(() -> {
            String[] arr = VideoUtils.removeByIndex(videoUrl.split("http"), 0);
            //如果播放地址只有1个
            if (arr.length == 1) {
                String url = "http" + arr[0];
                if (url.contains(".m3u8") || url.contains(".mp4")) {
                    Bundle bundle = new Bundle();
                    switch ((Integer) SharedPreferencesUtils.getParam(getApplicationContext(), "player", 0)) {
                        case 0:
                            //调用播放器
                            bundle.putString("animeTitle", animeTitle);
                            bundle.putString("title", witchTitle);
                            bundle.putString("url", url);
                            bundle.putString("dili", diliUrl);
                            bundle.putSerializable("list", (Serializable) drama);
                            startActivityForResult(new Intent(DescActivity.this, PlayerActivity.class).putExtras(bundle), 0x10);
                            break;
                        case 1:
                            Utils.selectVideoPlayer(url);
                            break;
                    }
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putString("title", animeTitle);
                    bundle.putString("url", url);
                    bundle.putString("dili", diliUrl);
                    bundle.putSerializable("list", (Serializable) drama);
                    startActivityForResult(new Intent(DescActivity.this, WebActivity.class).putExtras(bundle), 0x10);
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
        builder.setItems(videoTitleArr, (dialogInterface, index) -> {
            if (videoUrlArr[index].contains(".m3u8") || videoUrlArr[index].contains(".mp4")) {
                switch ((Integer) SharedPreferencesUtils.getParam(getApplicationContext(), "player", 0)) {
                    case 0:
                        //调用播放器
                        Bundle bundle = new Bundle();
                        bundle.putString("title", witchTitle);
                        bundle.putString("animeTitle", animeTitle);
                        bundle.putString("url", videoUrlArr[index]);
                        bundle.putString("dili", diliUrl);
                        bundle.putSerializable("list", (Serializable) drama);
                        startActivityForResult(new Intent(DescActivity.this, PlayerActivity.class).putExtras(bundle), 0x10);
                        break;
                    case 1:
                        Utils.selectVideoPlayer(videoUrlArr[index]);
                        break;
                }
            } else {
                Bundle bundle = new Bundle();
                bundle.putString("title", animeTitle);
                bundle.putString("url", videoUrlArr[index]);
                bundle.putString("dili", diliUrl);
                bundle.putSerializable("list", (Serializable) drama);
                startActivityForResult(new Intent(DescActivity.this, WebActivity.class).putExtras(bundle), 0x10);
            }
        });
        builder.setNegativeButton("取消", (dialogInterface, which) -> dialogInterface.dismiss());
        alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 200 && requestCode == 3000) {
            setResult(200);
        } else if (resultCode == 0x20 && requestCode == 0x10) {
            mSwipe.setRefreshing(true);
            multiItemList = new ArrayList<>();
            adapter.notifyDataSetChanged();
            mPresenter.loadData(true);
        }
    }

    public void favoriteAnime() {
        setResult(200);
        isFavorite = DatabaseUtil.favorite(animeListBean);
        if (isFavorite) {
            Glide.with(DescActivity.this).load(R.drawable.baseline_favorite_white_48dp).into(favorite);
            Utils.showSnackbar(toolbar, Utils.getString(R.string.join_ok));
        } else {
            Glide.with(DescActivity.this).load(R.drawable.baseline_favorite_border_white_48dp).into(favorite);
            Utils.showSnackbar(toolbar, Utils.getString(R.string.join_error));
        }
    }

    public void setCollapsingToolbar() {
        Glide.with(DescActivity.this).asBitmap().load(animeListBean.getImg()).into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                if (null == resource)
                    imageView.setImageDrawable(getDrawable(R.drawable.error));
                else
                    Blurry.with(DescActivity.this)
                            .radius(4)
                            .sampling(2)
                            .async()
                            .from(resource)
                            .into(imageView);
            }
        });
        ct.setTitle(animeListBean.getTitle());
    }

    @Override
    public void showLoadingView() {
        showEmptyVIew();
    }

    @Override
    public void showLoadErrorView(String msg) {
        runOnUiThread(() -> {
            mSwipe.setRefreshing(false);
            setCollapsingToolbar();
            mRecyclerView.setLayoutManager(new LinearLayoutManager(DescActivity.this));
            errorTitle.setText(msg);
            adapter.setEmptyView(errorView);
        });
    }

    @Override
    public void showEmptyVIew() {
        mSwipe.setRefreshing(true);
        adapter.setEmptyView(emptyView);
    }

    @Override
    public void showSuccessMainView(List<MultiItemEntity> list) {
        runOnUiThread(() -> {
            final GridLayoutManager manager = new GridLayoutManager(DescActivity.this, 15);
            manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    int index = 0;
                    switch (adapter.getItemViewType(position)) {
                        case AnimeType.TYPE_LEVEL_0:
                            index = manager.getSpanCount();
                            break;
                        case AnimeType.TYPE_LEVEL_1:
                            index = 3;
                            break;
                        case AnimeType.TYPE_LEVEL_2:
                            index = manager.getSpanCount();
                            break;
                        case AnimeType.TYPE_LEVEL_3:
                            index = 5;
                            break;
                    }
                    return index;
                }
            });
            // important! setLayoutManager should be called after setAdapter
            mRecyclerView.setLayoutManager(manager);
            multiItemList = list;
            mSwipe.setRefreshing(false);
            setCollapsingToolbar();
            adapter.setNewData(multiItemList);
            adapter.expand(0);
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.open_in_browser:
                Utils.viewInBrowser(url);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.desc_menu, menu);
        return true;
    }

    @Override
    public void showSuccessDramaView(List<AnimeDescBean> list) {
        drama = list;
    }

    @Override
    public void errorDramaView() {
    }

    @Override
    public void showSuccessDescView(AnimeListBean bean) {
        animeListBean = bean;
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void showSuccessFavorite(boolean is) {
        isFavorite = is;
        runOnUiThread(() -> {
            if (!favorite.isShown()) {
                if (isFavorite)
                    Glide.with(DescActivity.this).load(R.drawable.baseline_favorite_white_48dp).into(favorite);
                else
                    Glide.with(DescActivity.this).load(R.drawable.baseline_favorite_border_white_48dp).into(favorite);
                favorite.startAnimation(Utils.animationOut(1));
                favorite.setVisibility(View.VISIBLE);
            }
        });
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
        runOnUiThread(() -> VideoUtils.showErrorInfo(DescActivity.this, diliUrl));
    }

    @Override
    public void getVideoError() {
        //网络出错
        runOnUiThread(() -> application.showToastMsg(Utils.getString(R.string.error_700)));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != videoPresenter)
            videoPresenter.detachView();
    }
}
