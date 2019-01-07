package anime.project.dilidili.main.search;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.r0adkll.slidr.Slidr;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import anime.project.dilidili.R;
import anime.project.dilidili.adapter.SearchAdapter;
import anime.project.dilidili.api.Api;
import anime.project.dilidili.bean.SearchBean;
import anime.project.dilidili.main.base.BaseActivity;
import anime.project.dilidili.main.desc.DescActivity;
import anime.project.dilidili.util.StatusBarUtil;
import anime.project.dilidili.util.Utils;
import butterknife.BindView;

public class SearchActivity extends BaseActivity<SearchContract.View, SearchPresenter> implements SearchContract.View {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.rv_list)
    RecyclerView mRecyclerView;
    @BindView(R.id.mSwipe)
    SwipeRefreshLayout mSwipe;
    private SearchAdapter adapter;
    private List<SearchBean> searchList = new ArrayList<>();
    private String title = "";
    private int page = 0;
    private int pageCount;
    private boolean isErr = true;
    private androidx.appcompat.widget.SearchView mSearchView;

    @Override
    protected SearchPresenter createPresenter() {
        return new SearchPresenter(title, page, this);
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
        StatusBarUtil.setColorForSwipeBack(SearchActivity.this, getResources().getColor(R.color.night), 0);
        Slidr.attach(this, Utils.defaultInit());
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
        if (null != bundle && !bundle.isEmpty())
            title = bundle.getString("title");
    }

    public void initToolbar(){
        toolbar.setTitle(title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(view -> finish());
    }

    public void initSwipe(){
        mSwipe.setEnabled(false);
        mSwipe.setColorSchemeResources(R.color.pink500, R.color.blue500, R.color.purple500);
    }

    public void initAdapter(){
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SearchAdapter(searchList);
        adapter.openLoadAnimation();
        adapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);
        adapter.setOnItemClickListener((adapter, view, position) -> {
            if (Utils.isFastClick()) {
                SearchBean bean = (SearchBean) adapter.getItem(position);
                if (bean.getUrl().indexOf("ceshi") == -1) {
                    Bundle bundle = new Bundle();
                    bundle.putString("name", bean.getTitle());
                    bundle.putString("url", bean.getUrl().startsWith("http") ? bean.getUrl() : Api.URL + bean.getUrl());
                    startActivity(new Intent(SearchActivity.this, DescActivity.class).putExtras(bundle));
                } else application.showSnackbarMsg(toolbar, Utils.getString(R.string.ceshi_error));
            }
        });
        adapter.setOnLoadMoreListener(() -> mRecyclerView.postDelayed(() -> {
            if (page >= pageCount) {
                //数据全部加载完毕
                adapter.loadMoreEnd();
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
        }, 500), mRecyclerView);
        mRecyclerView.setAdapter(adapter);
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
        mSearchView.setMaxWidth(1000);
        if (!title.isEmpty()){
            mSearchView.setQuery(title, false);
            mSearchView.clearFocus();
            Utils.hideKeyboard(mSearchView);
        }
        SearchView.SearchAutoComplete textView = mSearchView.findViewById(R.id.search_src_text);
        textView.setTextColor(getResources().getColor(R.color.md_white_1000));
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                title = query.replaceAll(" ","");
                if (!title.isEmpty()) {
                    page = 0;
                    mPresenter = createPresenter();
                    mPresenter.loadData(true);
                    toolbar.setTitle(title);
                    mSearchView.clearFocus();
                    Utils.hideKeyboard(mSearchView);
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
            if (isMain){
                mSwipe.setRefreshing(false);
                searchList = list;
                adapter.setNewData(searchList);
            } else {
                adapter.addData(list);
                setLoadState(true);
            }
        });
    }

    @Override
    public void showErrorView(boolean isMain, String msg) {
        runOnUiThread(() -> {
            if (isMain){
                mSwipe.setRefreshing(false);
                errorTitle.setText(msg);
                adapter.setEmptyView(errorView);
            } else {
                setLoadState(false);
                application.showToastMsg(msg);
            }
        });
    }

    @Override
    public void getPageCount(int pageCount) {
        this.pageCount = pageCount;
    }
}
