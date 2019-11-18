package anime.project.dilidili.main.search;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.r0adkll.slidr.Slidr;

import java.util.ArrayList;
import java.util.List;

import anime.project.dilidili.R;
import anime.project.dilidili.adapter.SearchV2Adapter;
import anime.project.dilidili.bean.SearchBean;
import anime.project.dilidili.custom.CustomLoadMoreView;
import anime.project.dilidili.main.base.BaseActivity;
import anime.project.dilidili.main.desc.DescActivity;
import anime.project.dilidili.util.SwipeBackLayoutUtil;
import anime.project.dilidili.util.Utils;
import anime.project.dilidili.util.VideoUtils;
import butterknife.BindView;

public class SearchV2Activity extends BaseActivity<SearchV2Contract.View, SearchV2Presenter> implements SearchV2Contract.View {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.rv_list)
    RecyclerView mRecyclerView;
    @BindView(R.id.mSwipe)
    SwipeRefreshLayout mSwipe;
    private SearchV2Adapter adapter;
    private List<SearchBean> searchList = new ArrayList<>();
    private String title = "";
    private int page = 0;
    private int pageCount = 0;
    private boolean isErr = true;
    private SearchView mSearchView;
    private boolean isSearch = false;

    @Override
    protected SearchV2Presenter createPresenter() {
        return new SearchV2Presenter(title, page, this);
    }

    @Override
    protected void loadData() {
        if (!title.isEmpty())
            mPresenter.loadData(true);
    }

    @Override
    protected int setLayoutRes() {
        return R.layout.activity_anime;
    }

    @Override
    protected void init() {
        Slidr.attach(this, Utils.defaultInit());
        getBundle();
        initToolbar();
        initSwipe();
        initAdapter();
    }

    @Override
    protected void initBeforeView() {
        SwipeBackLayoutUtil.convertActivityToTranslucent(this);
    }

    public void getBundle() {
        Bundle bundle = getIntent().getExtras();
        if (null != bundle && !bundle.isEmpty())
            title = bundle.getString("title");
    }

    public void initToolbar() {
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(view -> finish());
    }

    public void initSwipe() {
        mSwipe.setEnabled(false);
        mSwipe.setColorSchemeResources(R.color.pink500, R.color.blue500, R.color.purple500);
    }

    public void initAdapter() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SearchV2Adapter(this, searchList);
        adapter.openLoadAnimation();
        adapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);
        adapter.setOnItemClickListener((adapter, view, position) -> {
            if (!Utils.isFastClick()) return;
            SearchBean bean = (SearchBean) adapter.getItem(position);
            String url = VideoUtils.getDiliUrl(bean.getUrl());
            String title = bean.getTitle();
            openAnimeDesc(title, url);
        });
        adapter.setLoadMoreView(new CustomLoadMoreView());
        adapter.setOnLoadMoreListener(() -> {
            isSearch = true;
            if (page >= pageCount) {
                //数据全部加载完毕
                adapter.loadMoreEnd();
                isSearch = false;
                application.showSuccessToastMsg(Utils.getString(R.string.no_more));
            } else {
                if (isErr) {
                    //成功获取更多数据
                    page++;
                    mPresenter = createPresenter();
                    mPresenter.loadData(false);
                } else {
                    //获取更多数据失败
                    isErr = true;
                    adapter.loadMoreFail();
                }
            }
        }, mRecyclerView);
        if (Utils.checkHasNavigationBar(this)) mRecyclerView.setPadding(0,0,0, Utils.getNavigationBarHeight(this) - 5);
        mRecyclerView.setAdapter(adapter);
    }

    public void openAnimeDesc(String title, String url) {
        Bundle bundle = new Bundle();
        bundle.putString("name", title);
        bundle.putString("url", url);
        startActivity(new Intent(SearchV2Activity.this, DescActivity.class).putExtras(bundle));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.search) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);
        final MenuItem item = menu.findItem(R.id.search);
        mSearchView = (SearchView) MenuItemCompat.getActionView(item);
        mSearchView.onActionViewExpanded();
        mSearchView.setQueryHint(Utils.getString(R.string.search_hint));
        mSearchView.setMaxWidth(2000);
        if (!title.isEmpty()) {
            mSearchView.setQuery(title, false);
            mSearchView.clearFocus();
            Utils.hideKeyboard(mSearchView);
        }
        SearchView.SearchAutoComplete textView = mSearchView.findViewById(R.id.search_src_text);
        mSearchView.findViewById(R.id.search_plate).setBackground(null);
        mSearchView.findViewById(R.id.submit_area).setBackground(null);
        textView.setTextColor(getResources().getColor(R.color.text_color_primary));
        textView.setHintTextColor(getResources().getColor(R.color.text_color_primary));
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (isSearch) {
                    application.showToastMsg("正在执行搜索操作，请稍后再试！");
                } else {
                    title = query.replaceAll(" ", "");
                    if (!title.isEmpty()) {
                        page = 0;
                        pageCount = 0;
                        mPresenter = createPresenter();
                        mPresenter.loadData(true);
                        toolbar.setTitle(title);
                        mSearchView.clearFocus();
                        Utils.hideKeyboard(mSearchView);
                    }
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return true;
    }

    public void setLoadState(boolean loadState) {
        isErr = loadState;
        adapter.loadMoreComplete();
    }

    @Override
    public void showLoadingView() {
        isSearch = true;
        searchList.clear();
        adapter.setNewData(searchList);
        mSwipe.setRefreshing(true);
    }

    @Override
    public void showLoadErrorView(String msg) {

    }

    @Override
    public void showEmptyVIew() {
        adapter.setEmptyView(emptyView);
    }

    @Override
    public void showSuccessView(boolean isMain, List<SearchBean> list) {
        runOnUiThread(() -> {
            isSearch = false;
            if (!mActivityFinish) {
                if (isMain) {
                    mSwipe.setRefreshing(false);
                    searchList = list;
                    adapter.setNewData(searchList);
                } else {
                    adapter.addData(list);
                    setLoadState(true);
                }
            }
        });
    }

    @Override
    public void showErrorView(boolean isMain, String msg) {
        runOnUiThread(() -> {
            isSearch = false;
            if (!mActivityFinish) {
                if (isMain) {
                    mSwipe.setRefreshing(false);
                    errorTitle.setText(msg);
                    adapter.setEmptyView(errorView);
                } else {
                    setLoadState(false);
                    application.showErrorToastMsg(msg);
                }
            }
        });
    }

    @Override
    public void getPageCount(int pageCount) {
        this.pageCount = pageCount;
    }
}
