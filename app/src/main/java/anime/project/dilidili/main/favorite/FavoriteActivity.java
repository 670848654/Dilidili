package anime.project.dilidili.main.favorite;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.r0adkll.slidr.Slidr;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import anime.project.dilidili.R;
import anime.project.dilidili.database.DatabaseUtil;
import anime.project.dilidili.adapter.AnimeListAdapter;
import anime.project.dilidili.main.base.BaseActivity;
import anime.project.dilidili.main.base.BaseView;
import anime.project.dilidili.bean.AnimeListBean;
import anime.project.dilidili.main.desc.DescActivity;
import anime.project.dilidili.util.StatusBarUtil;
import anime.project.dilidili.util.Utils;
import butterknife.BindView;

public class FavoriteActivity extends BaseActivity implements BaseView,FavoriteView {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.rv_list)
    RecyclerView mRecyclerView;
    private AnimeListAdapter adapter;
    @BindView(R.id.mSwipe)
    SwipeRefreshLayout mSwipe;
    private List<AnimeListBean> favoriteList = new ArrayList<>();
    private FavoritePresenter presenter;

    @Override
    protected int setLayoutRes() {
        return R.layout.activity_anime;
    }

    @Override
    protected void init() {
        StatusBarUtil.setColorForSwipeBack(FavoriteActivity.this, getResources().getColor(R.color.night), 0);
        // 设置右滑动返回
        Slidr.attach(this,Utils.defaultInit());
        initViews(mRecyclerView);
        initToolbar();
        initSwipe();
        initAdapter();
        presenter = new FavoritePresenter(this, this);
        presenter.loadData(true);
    }

    @Override
    protected void initBeforeView() {

    }

    public void initToolbar(){
        toolbar.setTitle(Utils.getString(FavoriteActivity.this,R.string.favorite_title));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                finish();
            }
        });
    }

    public void initSwipe(){
        //不启用下拉刷新
        mSwipe.setEnabled(false);
    }

    public void initAdapter(){
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AnimeListAdapter(FavoriteActivity.this, favoriteList);
        adapter.openLoadAnimation();
        adapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, final int position) {
                if (Utils.isFastClick()) {
                    Bundle bundle = new Bundle();
                    bundle.putString("name", favoriteList.get(position).getTitle());
                    bundle.putString("url", favoriteList.get(position).getUrl());
                    startActivityForResult(new Intent(FavoriteActivity.this, DescActivity.class).putExtras(bundle),3000);
                }
            }
        });
        adapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final BaseQuickAdapter adapter, View view, final int position) {
                View v = adapter.getViewByPosition(mRecyclerView, position, R.id.tag);
                final PopupMenu popupMenu = new PopupMenu(FavoriteActivity.this, v);
                popupMenu.getMenuInflater().inflate(R.menu.favorite_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.remove_favorite:
                                removeFavorite(position);
                                break;
                        }
                        return true;
                    }
                });
                popupMenu.show();
                return true;
            }
        });
        mRecyclerView.setAdapter(adapter);
    }

    /**
     * 移除收藏
     */
    private void removeFavorite(int position){
        DatabaseUtil.deleteFavorite(favoriteList.get(position).getTitle());
        adapter.remove(position);
        if (favoriteList.size() <= 0){
            errorTitle.setText("收藏为空");
            adapter.setEmptyView(errorView);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 200 && requestCode == 3000) {
            presenter.loadData(true);
        }
    }

    @Override
    public void showLoadingView() {
        favoriteList.clear();
        adapter.setNewData(favoriteList);
    }

    @Override
    public void showLoadErrorView(String msg) {
        errorTitle.setText(msg);
        adapter.setEmptyView(errorView);
    }

    @Override
    public void showEmptyVIew() {
        adapter.setEmptyView(emptyView);
    }

    @Override
    public void showSuccessView(List<AnimeListBean> list) {
        favoriteList = list;
        adapter.setNewData(favoriteList);
    }
}
