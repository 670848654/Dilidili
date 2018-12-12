package anime.project.dilidili.main.recommend;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.r0adkll.slidr.Slidr;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import anime.project.dilidili.R;
import anime.project.dilidili.api.Api;
import anime.project.dilidili.adapter.RecommendAdapter;
import anime.project.dilidili.main.base.BaseActivity;
import anime.project.dilidili.main.base.BaseView;
import anime.project.dilidili.bean.RecommendBean;
import anime.project.dilidili.config.RecommendType;
import anime.project.dilidili.main.desc.DescActivity;
import anime.project.dilidili.util.StatusBarUtil;
import anime.project.dilidili.util.Utils;
import butterknife.BindView;

public class RecommendActivity extends BaseActivity implements BaseView,RecommendView {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.rv_list)
    RecyclerView mRecyclerView;
    private RecommendAdapter adapter;
    @BindView(R.id.mSwipe)
    SwipeRefreshLayout mSwipe;
    private List<MultiItemEntity> recommendList = new ArrayList<>();
    private RecommendPresenter presenter;

    @Override
    protected int setLayoutRes() {
        return R.layout.activity_anime;
    }

    @Override
    protected void init() {
        StatusBarUtil.setColorForSwipeBack(this, getResources().getColor(R.color.night), 0);
        // 设置右滑动返回
        Slidr.attach(this, Utils.defaultInit());
        initViews(mRecyclerView);
        initToolbar();
        initSwipe();
        initAdapter();
        presenter = new RecommendPresenter(this, this);
        presenter.loadData(true);
    }

    @Override
    protected void initBeforeView() {

    }

    public void initToolbar(){
        toolbar.setTitle(Utils.getString(this,R.string.recommend_title));
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
                recommendList.clear();
                adapter.setNewData(recommendList);
                presenter.loadData(true);
            }
        });
    }

    public void initAdapter(){
        adapter = new RecommendAdapter(this, recommendList);
        adapter.openLoadAnimation();
        adapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (Utils.isFastClick()) {
                    final RecommendBean bean = (RecommendBean) recommendList.get(position);
                    Bundle bundle = new Bundle();
                    if (bean.getUrl().indexOf("http") == -1)
                        bundle.putString("url", Api.URL + bean.getUrl());
                    else
                        bundle.putString("url", bean.getUrl());
                    startActivity(new Intent(RecommendActivity.this, DescActivity.class).putExtras(bundle));
                }
            }
        });
        mRecyclerView.setAdapter(adapter);
        final GridLayoutManager manager = new GridLayoutManager(this, 3);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return adapter.getItemViewType(position) == RecommendType.TYPE_LEVEL_1 ? 1 : manager.getSpanCount();
            }
        });
        // important! setLayoutManager should be called after setAdapter
        mRecyclerView.setLayoutManager(manager);
    }

    @Override
    public void showLoadingView() {
        mSwipe.setRefreshing(true);
    }

    @Override
    public void showSuccessView(List<MultiItemEntity> list) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mSwipe.setRefreshing(false);
                recommendList = list;
                adapter.setNewData(recommendList);
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
