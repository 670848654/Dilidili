package anime.project.dilidili.main.setting.user;

import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.r0adkll.slidr.Slidr;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import anime.project.dilidili.R;
import anime.project.dilidili.adapter.ApiAdapter;
import anime.project.dilidili.bean.ApiBean;
import anime.project.dilidili.database.DatabaseUtil;
import anime.project.dilidili.main.base.BaseActivity;
import anime.project.dilidili.util.StatusBarUtil;
import anime.project.dilidili.util.SwipeBackLayoutUtil;
import anime.project.dilidili.util.Utils;
import butterknife.BindView;
import butterknife.OnClick;

public class ApiActivity extends BaseActivity<ApiContract.View, ApiPresenter> implements ApiContract.View {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.rv_list)
    RecyclerView mRecyclerView;
    @BindView(R.id.mSwipe)
    SwipeRefreshLayout mSwipe;
    private ApiAdapter adapter;
    private List<ApiBean> apiList = new ArrayList<>();
    private AlertDialog alertDialog;

    @Override
    protected ApiPresenter createPresenter() {
        return new ApiPresenter(this);
    }

    @Override
    protected void loadData() {
        mPresenter.loadData(true);
    }

    @Override
    protected int setLayoutRes() {
        return R.layout.activity_api;
    }

    @Override
    protected void init() {
        StatusBarUtil.setColorForSwipeBack(this, getResources().getColor(R.color.night), 0);
        Slidr.attach(this, Utils.defaultInit());;
        initToolbar();
        initSwipe();
        initAdapter();
    }

    @Override
    protected void initBeforeView() {
        SwipeBackLayoutUtil.convertActivityToTranslucent(this);
    }

    public void initToolbar() {
        toolbar.setTitle(Utils.getString(R.string.api_title));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(view -> finish());
    }

    public void initSwipe() {
        mSwipe.setEnabled(false);
    }

    public void initAdapter() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ApiAdapter(apiList);
        adapter.openLoadAnimation();
        adapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);
        adapter.setOnItemClickListener((adapter, view, position) -> {
            if (Utils.isFastClick()) setApi(true, position);
        });
        adapter.setOnItemChildClickListener((adapter, view, position) -> {
            if (Utils.isFastClick()) delete(position);
        });
        mRecyclerView.setAdapter(adapter);
    }

    public void delete(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("删除后将无法恢复。\n是否删除？");
        builder.setPositiveButton(Utils.getString(R.string.page_positive), (dialog, i) -> {
            DatabaseUtil.deleteApi(apiList.get(position).getId());
            adapter.remove(position);
            if (apiList.size() == 0){
                adapter.notifyDataSetChanged();
                errorTitle.setText("未自定义");
                adapter.setEmptyView(errorView);
            }
        });
        builder.setNegativeButton(Utils.getString(R.string.page_negative), null);
        alertDialog = builder.create();
        alertDialog.show();
    }

    @OnClick(R.id.add)
    public void addApi() {
        setApi(false, 0);
    }

    /**
     * 设置api
     *
     * @param isEdit   是否为修改
     * @param position
     */
    public void setApi(boolean isEdit, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_user_api_custom, null);
        final EditText title = view.findViewById(R.id.title);
        final EditText url = view.findViewById(R.id.url);
        if (isEdit) {
            title.setText(adapter.getData().get(position).getTitle());
            url.setText(adapter.getData().get(position).getUrl());
        }
        builder.setPositiveButton(Utils.getString(R.string.page_positive), null);
        builder.setNegativeButton(Utils.getString(R.string.page_negative), null);
        builder.setTitle(R.string.page_title);
        alertDialog = builder.setView(view).create();
        alertDialog.show();
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String name = title.getText().toString().replaceAll(" ", "");
            String api = url.getText().toString().replaceAll(" ", "");
            int error = 0;
            if (name.isEmpty()) {
                error++;
                title.setError(Utils.getString(R.string.api_error_1));
            }
            if (api.isEmpty()) {
                error++;
                url.setError(Utils.getString(R.string.api_error_1));
            }
            if (!Patterns.WEB_URL.matcher(api).matches()) {
                error++;
                url.setError(Utils.getString(R.string.api_error_2));
            }
            if (error > 0) return;
            else {
                if (isEdit) {
                    DatabaseUtil.updateApi(adapter.getData().get(position).getId(), name, api);
                    adapter.getData().get(position).setTitle(name);
                    adapter.getData().get(position).setUrl(api);
                    adapter.notifyDataSetChanged();
                } else {
                    ApiBean bean = new ApiBean(UUID.randomUUID().toString(), name, api);
                    DatabaseUtil.addApi(bean);
                    adapter.addData(0, bean);
                }
                alertDialog.dismiss();
            }
        });
    }

    @Override
    public void showLoadingView() {
        apiList = new ArrayList<>();
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
    public void showSuccess(List<ApiBean> list) {
        apiList = list;
        adapter.setNewData(apiList);
    }
}
