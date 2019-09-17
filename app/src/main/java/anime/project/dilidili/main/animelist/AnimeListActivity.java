package anime.project.dilidili.main.animelist;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.r0adkll.slidr.Slidr;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import anime.project.dilidili.R;
import anime.project.dilidili.adapter.AnimeListAdapter;
import anime.project.dilidili.bean.AnimeListBean;
import anime.project.dilidili.main.base.BaseActivity;
import anime.project.dilidili.main.desc.DescActivity;
import anime.project.dilidili.main.search.SearchActivity;
import anime.project.dilidili.main.search.SearchV2Activity;
import anime.project.dilidili.util.SharedPreferencesUtils;
import anime.project.dilidili.util.SwipeBackLayoutUtil;
import anime.project.dilidili.util.Utils;
import anime.project.dilidili.util.VideoUtils;
import butterknife.BindView;
import butterknife.OnClick;

public class AnimeListActivity extends BaseActivity<AnimeListContract.View, AnimeListPresenter> implements AnimeListContract.View {
    @BindView(R.id.rv_list)
    RecyclerView mRecyclerView;
    private AnimeListAdapter adapter;
    @BindView(R.id.mSwipe)
    SwipeRefreshLayout mSwipe;
    private List<AnimeListBean> list = new ArrayList<>();
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.query)
    FloatingActionButton query;
    private String title, url;

    @Override
    protected AnimeListPresenter createPresenter() {
        return new AnimeListPresenter(url, this);
    }

    @Override
    protected void loadData() {
        mPresenter.loadData(true);
    }

    @Override
    protected int setLayoutRes() {
        return R.layout.activity_anime;
    }

    @Override
    protected void init() {
        Slidr.attach(this,Utils.defaultInit());
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

    public void getBundle(){
        Bundle bundle = getIntent().getExtras();
        if (!bundle.isEmpty()) {
            title = bundle.getString("title");
            url = bundle.getString("url");
        }
    }

    public void initToolbar(){
        toolbar.setTitle(title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(view -> finish());
    }

    @SuppressLint("RestrictedApi")
    public void initFab(){
        query.setVisibility(View.VISIBLE);
    }

    public void initSwipe(){
        mSwipe.setColorSchemeResources(R.color.pink500, R.color.blue500, R.color.purple500);
        mSwipe.setOnRefreshListener(() -> {
            list.clear();
            adapter.setNewData(list);
            mPresenter.loadData(true);
        });
    }

    public void initAdapter(){
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AnimeListAdapter(this, list);
        adapter.openLoadAnimation();
        adapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);
        adapter.setOnItemClickListener((adapter, view, position) -> {
            if (!Utils.isFastClick()) return;
            final AnimeListBean bean = (AnimeListBean) adapter.getItem(position);
            Bundle bundle = new Bundle();
            bundle.putString("name", bean.getTitle());
            String diliUrl = VideoUtils.getDiliUrl(bean.getUrl());
            bundle.putString("url", diliUrl);
            startActivity(new Intent(AnimeListActivity.this, DescActivity.class).putExtras(bundle));
        });
        mRecyclerView.setAdapter(adapter);
    }

    @OnClick(R.id.query)
    public void query(){
        if ((Integer) SharedPreferencesUtils.getParam(this,"search", 1) == 1)
            startActivity(new Intent(this, SearchV2Activity.class));
        else
            startActivity(new Intent(this, SearchActivity.class));
    }

    @Override
    public void showLoadingView() {
        mSwipe.setRefreshing(true);
    }

    @Override
    public void showSuccessView(List<AnimeListBean> animeList) {
        runOnUiThread(() -> {
            if (!mActivityFinish) {
                mSwipe.setRefreshing(false);
                list = animeList;
                adapter.setNewData(list);
            }
        });
    }

    @Override
    public void showLoadErrorView(String msg) {
        runOnUiThread(() -> {
            if (!mActivityFinish) {
                mSwipe.setRefreshing(false);
                errorTitle.setText(msg);
                adapter.setEmptyView(errorView);
            }
        });
    }

    @Override
    public void showEmptyVIew() {
        adapter.setEmptyView(emptyView);
    }
}
