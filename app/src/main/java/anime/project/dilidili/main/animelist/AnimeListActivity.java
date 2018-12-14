package anime.project.dilidili.main.animelist;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.r0adkll.slidr.Slidr;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import anime.project.dilidili.R;
import anime.project.dilidili.api.Api;
import anime.project.dilidili.adapter.AnimeListAdapter;
import anime.project.dilidili.main.base.BaseActivity;
import anime.project.dilidili.bean.AnimeListBean;
import anime.project.dilidili.main.desc.DescActivity;
import anime.project.dilidili.util.StatusBarUtil;
import anime.project.dilidili.util.Utils;
import butterknife.BindView;

public class AnimeListActivity extends BaseActivity<AnimeListContract.View, AnimeListPresenter> implements AnimeListContract.View {
    @BindView(R.id.rv_list)
    RecyclerView mRecyclerView;
    private AnimeListAdapter adapter;
    @BindView(R.id.mSwipe)
    SwipeRefreshLayout mSwipe;
    private List<AnimeListBean> list = new ArrayList<>();
    @BindView(R.id.toolbar)
    Toolbar toolbar;
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
        StatusBarUtil.setColorForSwipeBack(AnimeListActivity.this, getResources().getColor(R.color.night), 0);
        Slidr.attach(this,Utils.defaultInit());
        getBundle();
        initToolbar();
        initSwipe();
        initAdapter();
    }

    @Override
    protected void initBeforeView() {

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
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                finish();
            }
        });
    }

    public void initSwipe(){
        mSwipe.setColorSchemeResources(R.color.pink500, R.color.blue500, R.color.purple500);
        mSwipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                list.clear();
                adapter.setNewData(list);
                mPresenter.loadData(true);
            }
        });
    }

    public void initAdapter(){
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AnimeListAdapter(AnimeListActivity.this,list);
        adapter.openLoadAnimation();
        adapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (Utils.isFastClick()) {
                    final AnimeListBean bean = (AnimeListBean) adapter.getItem(position);
                    Bundle bundle = new Bundle();
                    bundle.putString("name", bean.getTitle());
                    if (bean.getUrl().indexOf("http") == -1)
                        bundle.putString("url", Api.URL + bean.getUrl());
                    else
                        bundle.putString("url", bean.getUrl());
                    startActivity(new Intent(AnimeListActivity.this, DescActivity.class).putExtras(bundle));
                }
            }
        });
        mRecyclerView.setAdapter(adapter);
    }

    @Override
    public void showLoadingView() {
        mSwipe.setRefreshing(true);
    }

    @Override
    public void showSuccessView(List<AnimeListBean> animeList) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mSwipe.setRefreshing(false);
                list = animeList;
                adapter.setNewData(list);
            }
        });
    }

    @Override
    public void showLoadErrorView(String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
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
