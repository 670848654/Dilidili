package anime.project.dilidili.main.tag;

import android.content.Intent;
import android.os.Bundle;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.r0adkll.slidr.Slidr;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import anime.project.dilidili.R;
import anime.project.dilidili.adapter.TagAdapter;
import anime.project.dilidili.bean.HomeBean;
import anime.project.dilidili.main.animelist.AnimeListActivity;
import anime.project.dilidili.main.base.BaseActivity;
import anime.project.dilidili.util.StatusBarUtil;
import anime.project.dilidili.util.SwipeBackLayoutUtil;
import anime.project.dilidili.util.Utils;
import anime.project.dilidili.util.VideoUtils;
import butterknife.BindView;

public class TagActivity extends BaseActivity<TagContract.View, TagPresenter> implements TagContract.View {
    private final static Pattern YEAR = Pattern.compile("([0-9])");
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.rv_list)
    RecyclerView mRecyclerView;
    private TagAdapter adapter;
    @BindView(R.id.mSwipe)
    SwipeRefreshLayout mSwipe;
    private List<MultiItemEntity> tagList = new ArrayList<>();

    @Override
    protected TagPresenter createPresenter() {
        return new TagPresenter(this);
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
        StatusBarUtil.setColorForSwipeBack(this, getResources().getColor(R.color.night), 0);
        Slidr.attach(this, Utils.defaultInit());
        initToolbar();
        initSwipe();
        initAdapter();
    }

    @Override
    protected void initBeforeView() {
        SwipeBackLayoutUtil.convertActivityToTranslucent(this);
    }

    public void initToolbar() {
        toolbar.setTitle(Utils.getString(R.string.tag_title));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(view -> finish());
    }

    public void initSwipe() {
        mSwipe.setColorSchemeResources(R.color.pink500, R.color.blue500, R.color.purple500);
        mSwipe.setOnRefreshListener(() -> {
            tagList.clear();
            adapter.setNewData(tagList);
            mPresenter.loadData(true);
        });
    }

    public void initAdapter() {
        adapter = new TagAdapter(tagList);
        adapter.openLoadAnimation();
        adapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);
        adapter.setOnItemClickListener((adapter, view, position) -> {
            if (!Utils.isFastClick()) return;
            final HomeBean bean = (HomeBean) adapter.getItem(position);
            StringBuilder title = new StringBuilder();
            Matcher m = YEAR.matcher(bean.getDesc());
            while (m.find()) title.append(m.group());
            if (title.length() > 0) title.append("年");
            Bundle bundle = new Bundle();
            bundle.putString("title", title + bean.getTitle());
            bundle.putString("url", VideoUtils.getDiliUrl(bean.getUrl()));
            startActivity(new Intent(TagActivity.this, AnimeListActivity.class).putExtras(bundle));
        });
        mRecyclerView.setAdapter(adapter);
    }

    @Override
    public void showLoadingView() {
        mSwipe.setRefreshing(true);
    }

    @Override
    public void showSuccessView(List<MultiItemEntity> list) {
        runOnUiThread(() -> {
            if (!mActivityFinish) {
                final GridLayoutManager manager = new GridLayoutManager(this, 4);
                manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {
                        return adapter.getItemViewType(position) == TagAdapter.TYPE_LEVEL_1 ? 1 : manager.getSpanCount();
                    }
                });
                // important! setLayoutManager should be called after setAdapter
                mRecyclerView.setLayoutManager(manager);
                mSwipe.setRefreshing(false);
                tagList = list;
                adapter.setNewData(tagList);
            }
        });
    }

    @Override
    public void showLoadErrorView(String msg) {
        runOnUiThread(() -> {
            if (!mActivityFinish) {
                mRecyclerView.setLayoutManager(new LinearLayoutManager(TagActivity.this));
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
