package anime.project.dilidili.main.favorite;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.r0adkll.slidr.Slidr;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import anime.project.dilidili.R;
import anime.project.dilidili.adapter.FavoriteListAdapter;
import anime.project.dilidili.bean.AnimeListBean;
import anime.project.dilidili.database.DatabaseUtil;
import anime.project.dilidili.main.base.BaseActivity;
import anime.project.dilidili.main.desc.DescActivity;
import anime.project.dilidili.util.StatusBarUtil;
import anime.project.dilidili.util.SwipeBackLayoutUtil;
import anime.project.dilidili.util.Utils;
import anime.project.dilidili.util.VideoUtils;
import butterknife.BindView;

public class FavoriteActivity extends BaseActivity<FavoriteContract.View, FavoritePresenter> implements FavoriteContract.View {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.rv_list)
    RecyclerView mRecyclerView;
    private FavoriteListAdapter adapter;
    @BindView(R.id.mSwipe)
    SwipeRefreshLayout mSwipe;
    private List<AnimeListBean> favoriteList = new ArrayList<>();

    @Override
    protected FavoritePresenter createPresenter() {
        return new FavoritePresenter(this);
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
        StatusBarUtil.setColorForSwipeBack(FavoriteActivity.this, getResources().getColor(R.color.night), 0);
        Slidr.attach(this,Utils.defaultInit());
        initToolbar();
        initSwipe();
        initAdapter();
    }

    @Override
    protected void initBeforeView() {
        SwipeBackLayoutUtil.convertActivityToTranslucent(this);
    }

    public void initToolbar(){
        toolbar.setTitle(Utils.getString(R.string.favorite_title));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(view -> finish());
    }

    public void initSwipe(){
        //不启用下拉刷新
        mSwipe.setEnabled(false);
    }

    public void initAdapter(){
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        adapter = new FavoriteListAdapter(this, favoriteList);
        adapter.openLoadAnimation();
        adapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);
        adapter.setOnItemClickListener((adapter, view, position) -> {
            if (!Utils.isFastClick()) return;
            AnimeListBean bean = (AnimeListBean) adapter.getItem(position);
            Bundle bundle = new Bundle();
            bundle.putString("name", bean.getTitle());
            String url = VideoUtils.getDiliUrl(bean.getUrl());
            bundle.putString("url", url);
            startActivityForResult(new Intent(FavoriteActivity.this, DescActivity.class).putExtras(bundle),3000);
        });
        adapter.setOnItemLongClickListener((adapter, view, position) -> {
            View v = adapter.getViewByPosition(mRecyclerView, position, R.id.img);
            final PopupMenu popupMenu = new PopupMenu(FavoriteActivity.this, v);
            popupMenu.getMenuInflater().inflate(R.menu.favorite_menu, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(menuItem -> {
                switch (menuItem.getItemId()) {
                    case R.id.remove_favorite:
                        removeFavorite(position);
                        break;
                }
                return true;
            });
            popupMenu.show();
            return true;
        });
        mRecyclerView.setAdapter(adapter);
    }

    /**
     * 移除收藏
     */
    private void removeFavorite(int position){
        DatabaseUtil.deleteFavorite(favoriteList.get(position).getTitle());
        adapter.remove(position);
        Utils.showSnackbar(toolbar, Utils.getString(R.string.join_error));
        if (favoriteList.size() <= 0){
            errorTitle.setText(Utils.getString(R.string.empty_favorite));
            adapter.setEmptyView(errorView);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 200 && requestCode == 3000) {
            mPresenter.loadData(true);
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
