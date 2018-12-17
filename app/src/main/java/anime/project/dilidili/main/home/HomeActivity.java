package anime.project.dilidili.main.home;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONObject;

import java.util.Date;
import java.util.LinkedHashMap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;
import anime.project.dilidili.R;
import anime.project.dilidili.adapter.WeekAdapter;
import anime.project.dilidili.api.Api;
import anime.project.dilidili.custom.VpSwipeRefreshLayout;
import anime.project.dilidili.database.DatabaseUtil;
import anime.project.dilidili.main.about.AboutActivity;
import anime.project.dilidili.main.animelist.AnimeListActivity;
import anime.project.dilidili.main.base.BaseActivity;
import anime.project.dilidili.main.favorite.FavoriteActivity;
import anime.project.dilidili.main.recommend.RecommendActivity;
import anime.project.dilidili.main.search.SearchActivity;
import anime.project.dilidili.main.setting.SettingActivity;
import anime.project.dilidili.main.tag.TagActivity;
import anime.project.dilidili.util.SharedPreferencesUtils;
import anime.project.dilidili.util.StatusBarUtil;
import anime.project.dilidili.util.Utils;
import butterknife.BindView;

public class HomeActivity extends BaseActivity<HomeContract.View, HomePresenter> implements NavigationView.OnNavigationItemSelectedListener, HomeContract.View {
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.nav_view)
    NavigationView navigationView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.mSwipe)
    VpSwipeRefreshLayout mSwipe;
    private ImageView imageView;
    private String animeUrl = "", title = "";
    @BindView(R.id.tab)
    TabLayout tab;
    @BindView(R.id.viewpager)
    ViewPager viewpager;
    private WeekAdapter adapter;
    private int position;
    private int week;
    private SearchView mSearchView;
    private String[] tabs = new String[]{"周一", "周二", "周三", "周四", "周五", "周六", "周日"};

    @Override
    protected HomePresenter createPresenter() {
        return new HomePresenter(this);
    }

    @Override
    protected void loadData() {
        mPresenter.loadData(true);
    }

    @Override
    protected int setLayoutRes() {
        return R.layout.activity_home;
    }

    @Override
    protected void init() {
        initToolbar();
        initDrawer();
        initSwipe();
        initFragment();
    }

    @Override
    protected void initBeforeView() {

    }

    public void initToolbar() {
        toolbar.setTitle(getResources().getString(R.string.app_name));
        toolbar.setSubtitle(getResources().getString(R.string.app_sub_name));
        setSupportActionBar(toolbar);
    }

    public void initDrawer() {
        StatusBarUtil.setColorForDrawerLayout(this, drawer, getResources().getColor(R.color.night), 0);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        int[][] states = new int[][]{
                new int[]{-android.R.attr.state_checked},
                new int[]{android.R.attr.state_checked}
        };
        int[] colors = new int[]{getResources().getColor(R.color.md_white_1000),
                getResources().getColor(R.color.pinka200)
        };
        ColorStateList csl = new ColorStateList(states, colors);
        navigationView.setItemTextColor(csl);
        navigationView.setItemIconTintList(csl);
        View view = navigationView.getHeaderView(0);
        imageView = view.findViewById(R.id.imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.showSnackbar(imageView, Utils.getString(HomeActivity.this, R.string.huaji));
                final ObjectAnimator animator = Utils.nope(imageView);
                animator.setRepeatCount(ValueAnimator.INFINITE);
                animator.start();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        animator.cancel();
                    }
                }, 500);
            }
        });
        navigationView.getBackground().mutate().setAlpha(150);//0~255透明度值
        navigationView.setNavigationItemSelectedListener(this);
    }

    public void initSwipe() {
        mSwipe.setColorSchemeResources(R.color.pink500, R.color.blue500, R.color.purple500);
        mSwipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.loadData(true);
            }
        });
    }

    public void initFragment() {
        week = Utils.getWeekOfDate(new Date());
        for (int i = 0; i < tabs.length; i++) {
            tab.addTab(tab.newTab());
        }
        tab.setupWithViewPager(viewpager);
        setWeekAdapter();
        tab.setSelectedTabIndicatorColor(getResources().getColor(R.color.pinka200));
        tab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                position = tab.getPosition();
                viewpager.setCurrentItem(position);
                switch (position) {

                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        if (Boolean.parseBoolean(SharedPreferencesUtils.getParam(Utils.getContext(), "show_x5_info", true).toString()))
            Utils.showX5Info(this);
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
        mSearchView.setQueryHint(Utils.getString(HomeActivity.this, R.string.search_hint));
        mSearchView.setMaxWidth(1000);
        SearchView.SearchAutoComplete textView = mSearchView.findViewById(R.id.search_src_text);
        textView.setTextColor(getResources().getColor(R.color.md_white_1000));
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!query.replaceAll(" ", "").isEmpty()) {
                    Utils.hideKeyboard(mSearchView);
                    mSearchView.clearFocus();
                    startActivity(new Intent(HomeActivity.this, SearchActivity.class).putExtra("title", query));
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

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Snackbar.make(toolbar, Utils.getString(HomeActivity.this, R.string.exit_app), Snackbar.LENGTH_LONG).setAction(Utils.getString(HomeActivity.this, R.string.exit), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            }).show();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (Utils.isFastClick()) {
            switch (item.getItemId()) {
                case R.id.new_anim:
                    goToNewAnime(animeUrl, title);
                    break;
                case R.id.recommend_anime:
                    startActivity(new Intent(this, RecommendActivity.class));
                    break;
                case R.id.find_anim:
                    startActivity(new Intent(this, TagActivity.class));
                    break;
                case R.id.about:
                    startActivity(new Intent(this, AboutActivity.class));
                    break;
                case R.id.favorite:
                    startActivity(new Intent(this, FavoriteActivity.class));
                    break;
                case R.id.setting:
                    startActivity(new Intent(this, SettingActivity.class));
                    break;
            }
        }
        return true;
    }

    public void goToNewAnime(String url, String title) {
        if (url.equals("")) {
            Utils.showSnackbar(imageView, Utils.getString(this, R.string.empty));
        } else {
            Bundle bundle = new Bundle();
            bundle.putString("title", title);
            if (url.indexOf("http") == -1)
                bundle.putString("url", Api.URL + url);
            else
                bundle.putString("url", url);
            startActivity(new Intent(this, AnimeListActivity.class).putExtras(bundle));
        }
    }

    @Override
    public void showLoadingView() {
        mSwipe.setRefreshing(true);
        application.error = "";
    }

    @Override
    public void showLoadErrorView(String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mSwipe.setRefreshing(false);
                navigationView.getMenu().getItem(0).setTitle(Utils.getString(HomeActivity.this, R.string.menu_load_error));
                Toast.makeText(HomeActivity.this, msg, Toast.LENGTH_LONG).show();
                application.error = msg;
                setWeekAdapter();
            }
        });
    }

    @Override
    public void showEmptyVIew() {
    }

    @Override
    public void showLoadSuccess(LinkedHashMap map) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mSwipe.setRefreshing(false);
                application.week = map.get("week") == null ? new JSONObject() : (JSONObject) map.get("week");
                title = map.get("title").toString() == null ? "加载失败" : map.get("title").toString();
                animeUrl = map.get("url").toString() == null ? "" : map.get("url").toString();
                navigationView.getMenu().getItem(0).setTitle(title);
                setWeekAdapter();
            }
        });
    }

    public void setWeekAdapter() {
        adapter = new WeekAdapter(getSupportFragmentManager(), tab.getTabCount());
        viewpager.setAdapter(adapter);
        viewpager.setCurrentItem(week);
        for (int i = 0; i < tabs.length; i++) {
            tab.getTabAt(i).setText(tabs[i]);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DatabaseUtil.closeDB();
    }
}
