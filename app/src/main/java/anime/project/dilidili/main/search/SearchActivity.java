package anime.project.dilidili.main.search;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.android.material.snackbar.Snackbar;
import com.r0adkll.slidr.Slidr;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import anime.project.dilidili.R;
import anime.project.dilidili.adapter.SearchAdapter;
import anime.project.dilidili.main.base.BaseActivity;
import anime.project.dilidili.bean.SearchBean;
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
                page = 0;
                mPresenter = createPresenter();
                mPresenter.loadData(true);
            }
        });
    }

    public void initAdapter(){
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SearchAdapter(SearchActivity.this, searchList);
        adapter.openLoadAnimation();
        adapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (Utils.isFastClick()) {
                    final SearchBean bean = (SearchBean) adapter.getItem(position);
                    if (bean.getUrl().indexOf("ceshi") == -1) {
                        Bundle bundle = new Bundle();
                        bundle.putString("name", bean.getTitle());
                        bundle.putString("url", bean.getUrl());
                        startActivity(new Intent(SearchActivity.this, DescActivity.class).putExtras(bundle));
                    } else {
                        Snackbar.make(toolbar, Utils.getString(getApplicationContext(), R.string.ceshi_error), Snackbar.LENGTH_LONG).show();
                    }
                }
            }
        });
        adapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                mRecyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mSwipe.setEnabled(false);
                        if (page >= pageCount) {
                            //数据全部加载完毕
                            adapter.loadMoreEnd();
                            mSwipe.setEnabled(true);
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
                                mSwipe.setEnabled(true);
                            }
                        }
                    }

                }, 500);
            }
        }, mRecyclerView);
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
        mSearchView = (androidx.appcompat.widget.SearchView) MenuItemCompat.getActionView(item);
        mSearchView.onActionViewExpanded();
        mSearchView.setQueryHint(Utils.getString(SearchActivity.this, R.string.search_hint));
        mSearchView.setMaxWidth(1000);
        if (!title.isEmpty()){
            mSearchView.setQuery(title, false);
            mSearchView.clearFocus();
            Utils.hideKeyboard(mSearchView);
        }
        androidx.appcompat.widget.SearchView.SearchAutoComplete textView = mSearchView.findViewById(R.id.search_src_text);
        textView.setTextColor(getResources().getColor(R.color.md_white_1000));
        mSearchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
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
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isMain){
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
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isMain){
                    mSwipe.setRefreshing(false);
                    errorTitle.setText(msg);
                    adapter.setEmptyView(errorView);
                } else {
                    setLoadState(false);
                    Toast.makeText(SearchActivity.this, msg, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void getPageCount(int pageCount) {
        this.pageCount = pageCount;
    }
}
