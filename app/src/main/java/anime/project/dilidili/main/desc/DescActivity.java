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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.r0adkll.slidr.Slidr;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import anime.project.dilidili.R;
import anime.project.dilidili.adapter.DescAdapter;
import anime.project.dilidili.application.DiliDili;
import anime.project.dilidili.bean.AnimeDescBean;
import anime.project.dilidili.bean.AnimeListBean;
import anime.project.dilidili.bean.DownBean;
import anime.project.dilidili.config.AnimeType;
import anime.project.dilidili.database.DatabaseUtil;
import anime.project.dilidili.main.animelist.AnimeListActivity;
import anime.project.dilidili.main.base.BaseActivity;
import anime.project.dilidili.main.video.VideoContract;
import anime.project.dilidili.main.video.VideoPresenter;
import anime.project.dilidili.util.SharedPreferencesUtils;
import anime.project.dilidili.util.StatusBarUtil;
import anime.project.dilidili.util.SwipeBackLayoutUtil;
import anime.project.dilidili.util.Utils;
import anime.project.dilidili.util.VideoUtils;
import butterknife.BindView;
import jp.wasabeef.blurry.Blurry;

public class DescActivity extends BaseActivity<DescContract.View, DescPresenter> implements DescContract.View, VideoContract.View {
    private final static Pattern NUM_PATTERN = Pattern.compile("^[0-9]*$");
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.rv_list)
    RecyclerView mRecyclerView;
    @BindView(R.id.anime_img)
    ImageView animeImg;
    @BindView(R.id.region)
    AppCompatTextView region;
    @BindView(R.id.year)
    AppCompatTextView year;
    @BindView(R.id.tag)
    AppCompatTextView tag;
    @BindView(R.id.show)
    AppCompatTextView show;
    @BindView(R.id.state)
    AppCompatTextView state;
    private DescAdapter adapter;
    @BindView(R.id.mSwipe)
    SwipeRefreshLayout mSwipe;
    private List<MultiItemEntity> multiItemList = new ArrayList<>();
    private List<AnimeDescBean> drama = new ArrayList<>();
    @BindView(R.id.title_img)
    ImageView imageView;
    private String url, diliUrl, dramaUrl;
    private String animeTitle;
    private String witchTitle;
    private ProgressDialog p;
    @BindView(R.id.favorite)
    FloatingActionButton favorite;
    private boolean isFavorite;
    private String[] videoUrlArr;
    private VideoPresenter videoPresenter;
    private AnimeListBean animeListBean = new AnimeListBean();
    private List<String> animeUrlList = new ArrayList();
    private boolean mIsLoad = false;
    private List<DownBean> downBeanList;
    private MenuItem downView;

    @Override
    protected DescPresenter createPresenter() {
        return new DescPresenter(diliUrl, this);
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
        StatusBarUtil.setColorForSwipeBack(this, getResources().getColor(R.color.colorPrimaryDark), 0);
        StatusBarUtil.setTranslucentForImageView(this, 0, toolbar);
        Slidr.attach(this, Utils.defaultInit());
        getBundle();
        initToolbar();
        initFab();
        initSwipe();
        initAdapter();
    }

    @Override
    protected void initBeforeView() {
        SwipeBackLayoutUtil.convertActivityToTranslucent(this);
    }

    public void getBundle() {
        Bundle bundle = getIntent().getExtras();
        if (!bundle.isEmpty()) {
            diliUrl = bundle.getString("url");
            animeTitle = bundle.getString("name");
            animeUrlList.add(diliUrl);
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
            if (Utils.isFastClick()) favoriteAnime();
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

    @SuppressLint("RestrictedApi")
    public void initAdapter() {
        adapter = new DescAdapter(this, multiItemList);
        adapter.openLoadAnimation();
        adapter.setOnItemClickListener((adapter, view, position) -> {
            if (!Utils.isFastClick()) return;
            final AnimeDescBean bean = (AnimeDescBean) adapter.getItem(position);
            switch (bean.getType()) {
                case "play":
                    p = Utils.getProDialog(DescActivity.this, R.string.parsing);
                    Button v = (Button) adapter.getViewByPosition(mRecyclerView, position, R.id.tag_group);
                    v.setBackgroundResource(R.drawable.button_selected);
                    v.setTextColor(getResources().getColor(R.color.item_selected_color));
                    bean.setSelect(true);
                    dramaUrl = VideoUtils.getDiliUrl(bean.getUrl());
                    witchTitle = animeTitle + " - " + bean.getTitle();
                    videoPresenter = new VideoPresenter(animeListBean.getTitle(), dramaUrl, DescActivity.this);
                    videoPresenter.loadData(true);
                    break;
                case "ova":
                    animeTitle = bean.getTitle();
                    diliUrl = VideoUtils.getDiliUrl(bean.getUrl());
                    animeUrlList.add(diliUrl);
                    openAnimeDesc();
                    break;
                case "recommend":
                    animeTitle = bean.getTitle();
                    diliUrl = VideoUtils.getDiliUrl(bean.getUrl());
                    if (diliUrl.contains("/anime/")) {
                        String[] arr = bean.getUrl().split("/");
                        Matcher m = NUM_PATTERN.matcher(arr[arr.length - 1]);
                        boolean isAnimeList = false;
                        while (m.find()) {
                            isAnimeList = true;
                            break;
                        }
                        if (isAnimeList) openAnimeList();
                        else {
                            animeUrlList.add(diliUrl);
                            openAnimeDesc();
                        }
                    } else openAnimeList();
                    break;
            }
        });
        if (Utils.checkHasNavigationBar(this)) mRecyclerView.setPadding(0,0,0, Utils.getNavigationBarHeight(this) - 5);
        mRecyclerView.setAdapter(adapter);
    }

    @SuppressLint("RestrictedApi")
    public void openAnimeDesc() {
        toolbar.setTitle(Utils.getString(R.string.loading));
        animeImg.setImageDrawable(getDrawable(R.drawable.loading));
        region.setText("");
        year.setText("");
        tag.setText("");
        show.setText("");
        state.setText("");
        mSwipe.setRefreshing(true);
        imageView.setImageDrawable(null);
        animeListBean = new AnimeListBean();
        favorite.setVisibility(View.GONE);
        mPresenter = new DescPresenter(diliUrl, this);
        multiItemList.clear();
        adapter.setNewData(multiItemList);
        mPresenter.loadData(true);
    }

    public void openAnimeList() {
        Bundle bundle = new Bundle();
        bundle.putString("title", animeTitle);
        bundle.putString("url", diliUrl);
        startActivity(new Intent(DescActivity.this, AnimeListActivity.class).putExtras(bundle));
    }

    public void goToPlay(String videoUrl) {
        new Handler().postDelayed(() -> {
            String[] arr = VideoUtils.removeByIndex(videoUrl.split("http"), 0);
            if (arr.length == 1) oneSource(arr);
            else multipleSource(arr);
        }, 200);
    }

    /**
     * 只有一个播放地址
     *
     * @param arr
     */
    private void oneSource(String[] arr) {
        url = "http" + arr[0];
        if (url.contains(".m3u8") || url.contains(".mp4")) {
            switch ((Integer) SharedPreferencesUtils.getParam(getApplicationContext(), "player", 0)) {
                case 0:
                    //调用播放器
                    VideoUtils.openPlayer(true, this, witchTitle, url, animeTitle, diliUrl, drama);
                    break;
                case 1:
                    Utils.selectVideoPlayer(DescActivity.this, url);
                    break;
            }
        } else VideoUtils.openWebview(true, this, witchTitle, animeTitle, url, diliUrl, drama);
    }

    /**
     * 多个播放地址
     *
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
                                VideoUtils.openPlayer(true, this, witchTitle, url, animeTitle, diliUrl, drama);
                                break;
                            case 1:
                                Utils.selectVideoPlayer(DescActivity.this, url);
                                break;
                        }
                    } else
                        VideoUtils.openWebview(true, this, witchTitle, animeTitle, url, diliUrl, drama);
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0x10 && resultCode == 0x20) {
            mSwipe.setRefreshing(true);
            multiItemList = new ArrayList<>();
            adapter.notifyDataSetChanged();
            mPresenter.loadData(true);
        }
    }

    @Override
    public void onBackPressed() {
        if (animeUrlList.size() == 1) super.onBackPressed();
        else {
            if (!mIsLoad) {
                animeUrlList.remove(animeUrlList.size() - 1);
                diliUrl = animeUrlList.get(animeUrlList.size() - 1);
                openAnimeDesc();
            } else DiliDili.getInstance().showToastMsg(Utils.getString(R.string.load_desc_info));
        }
    }

    public void favoriteAnime() {
        setResult(200);
        isFavorite = DatabaseUtil.favorite(animeListBean);
        if (isFavorite) {
            Glide.with(DescActivity.this).load(R.drawable.baseline_favorite_white_48dp).into(favorite);
            application.showCustomToastMsg(Utils.getString(R.string.join_ok),
                    R.drawable.ic_add_favorite_48dp, R.color.green300);
        } else {
            Glide.with(DescActivity.this).load(R.drawable.baseline_favorite_border_white_48dp).into(favorite);
            application.showCustomToastMsg(Utils.getString(R.string.join_error),
                    R.drawable.ic_remove_favorite_48dp, R.color.red300);
        }
    }

    public void setCollapsingToolbar() {
        Glide.with(DescActivity.this).asBitmap().load(animeListBean.getImg()).into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                Blurry.with(DescActivity.this)
                        .radius(4)
                        .sampling(2)
                        .async()
                        .from(resource)
                        .into(imageView);
            }
        });
        toolbar.setTitle(animeListBean.getTitle());
        Utils.setDefaultImage(this, animeListBean.getImg(), animeImg);
        region.setText(animeListBean.getRegion().isEmpty() ? Utils.getString(R.string.no_region_msg) : animeListBean.getRegion());
        year.setText(animeListBean.getYear().isEmpty() ? Utils.getString(R.string.no_year_msg) : animeListBean.getYear());
        tag.setText(animeListBean.getTag().isEmpty() ? Utils.getString(R.string.no_tag_msg) : animeListBean.getTag());
        show.setText(animeListBean.getShow().isEmpty() ? Utils.getString(R.string.no_show_msg) : animeListBean.getShow());
        state.setText(animeListBean.getState().isEmpty() ? Utils.getString(R.string.no_state_msg) : animeListBean.getState());
    }

    @Override
    public void showLoadingView() {
        mIsLoad = true;
        showEmptyVIew();
    }

    @Override
    public void showLoadErrorView(String msg) {
        runOnUiThread(() -> {
            if (!mActivityFinish) {
                mIsLoad = false;
                mSwipe.setRefreshing(false);
                setCollapsingToolbar();
                mRecyclerView.setLayoutManager(new LinearLayoutManager(DescActivity.this));
                errorTitle.setText(msg);
                adapter.setEmptyView(errorView);
            }
        });
    }

    @Override
    public void showEmptyVIew() {
        mSwipe.setRefreshing(true);
        if (downView != null && downView.isVisible())
            downView.setVisible(false);
        adapter.setEmptyView(emptyView);
    }

    @Override
    public void showSuccessMainView(List<MultiItemEntity> list) {
        runOnUiThread(() -> {
            if (!mActivityFinish) {
                mIsLoad = false;
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
                                index = 5;
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
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.down:
                showDownDialog();
                break;
            case R.id.open_in_browser:
                Utils.viewInChrome(this, diliUrl);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.desc_menu, menu);
        downView = menu.findItem(R.id.down);
        return true;
    }

    private void showDownDialog() {
        AlertDialog alertDialog;
        String[] downArr = new String[downBeanList.size()];
        for (int i = 0; i < downBeanList.size(); i++) {
            downArr[i] = downBeanList.get(i).getTitle();
        }
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle(Utils.getString(R.string.down_title));
        builder.setItems(downArr, (dialogInterface, i) -> {
            Utils.putTextIntoClip(downBeanList.get(i).getTitle());
            application.showSuccessToastMsg(downBeanList.get(i).getTitle() + Utils.getString(R.string.down_copy));
            Utils.viewInChrome(DescActivity.this, downBeanList.get(i).getUrl());
        });
        alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void showSuccessDramaView(List<AnimeDescBean> list) {
        drama = list;
    }

    @Override
    public void errorDramaView() {
    }

    @Override
    public void hasBanIp() {
        Log.e("ban", "发现禁止IP");
        runOnUiThread(() -> {
            application.showErrorToastMsg((Utils.getString(R.string.has_ban_ip)));
            videoPresenter = new VideoPresenter(animeListBean.getTitle(), diliUrl + DiliDili.NEW_VERSION, this);
            videoPresenter.loadData(true);
        });
    }

    @Override
    public void showSuccessDescView(AnimeListBean bean) {
        animeListBean = bean;
        animeTitle = animeListBean.getTitle();
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void showSuccessFavorite(boolean is) {
        isFavorite = is;
        runOnUiThread(() -> {
            if (!mActivityFinish) {
                if (!favorite.isShown()) {
                    if (isFavorite)
                        Glide.with(DescActivity.this).load(R.drawable.baseline_favorite_white_48dp).into(favorite);
                    else
                        Glide.with(DescActivity.this).load(R.drawable.baseline_favorite_border_white_48dp).into(favorite);
                    favorite.startAnimation(Utils.animationOut(1));
                    favorite.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public void showDownView(List<DownBean> list) {
        runOnUiThread(() -> {
            downBeanList = list;
            if (downView != null)
                downView.setVisible(true);
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
        runOnUiThread(() -> VideoUtils.showErrorInfo(DescActivity.this, dramaUrl));
    }

    @Override
    public void getVideoError() {
        //网络出错
        runOnUiThread(() -> application.showErrorToastMsg(Utils.getString(R.string.error_700)));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != videoPresenter)
            videoPresenter.detachView();
    }
}
