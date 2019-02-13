package anime.project.dilidili.main.home;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import anime.project.dilidili.R;
import anime.project.dilidili.adapter.FragmentAdapter;
import anime.project.dilidili.api.Api;
import anime.project.dilidili.application.DiliDili;
import anime.project.dilidili.bean.HomeWekBean;
import anime.project.dilidili.main.base.LazyFragment;
import anime.project.dilidili.main.desc.DescActivity;
import anime.project.dilidili.util.Utils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

@SuppressLint("ValidFragment")
public class WeekFragment extends LazyFragment {
    @BindView(R.id.rv_list)
    RecyclerView recyclerView;
    @BindView(R.id.loading)
    ProgressBar loading;
    private FragmentAdapter adapter;
    private List<HomeWekBean> list = new ArrayList<>();
    private DiliDili application;
    private View view;
    private View errorView;
    private TextView errorTitle;
    private String week;
    private Unbinder mUnBinder;

    public WeekFragment(String week) {
        this.week = week;
    }

    @Override
    protected View initViews(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_week, container, false);
            mUnBinder = ButterKnife.bind(this, view);
        } else {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null) {
                parent.removeView(view);
            }
        }
        errorView = getLayoutInflater().inflate(R.layout.base_error_view, (ViewGroup) recyclerView.getParent(), false);
        errorTitle = errorView.findViewById(R.id.title);
        if (application == null) application = (DiliDili) getActivity().getApplication();
        initAdapter();
        return view;
    }

    @Override
    protected void initData() {
        initWeekData();
    }

    public void initAdapter() {
        if (adapter == null) {
            recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
            adapter = new FragmentAdapter(getActivity(), list);
            adapter.openLoadAnimation();
            adapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);
            adapter.setOnItemClickListener((adapter, view, position) -> {
                if (!Utils.isFastClick()) return;
                HomeWekBean bean = (HomeWekBean) adapter.getItem(position);
                Bundle bundle = new Bundle();
                bundle.putString("name", bean.getTitle());
                bundle.putString("url", bean.getUrl().startsWith("http") ? bean.getUrl() : DiliDili.URL + bean.getUrl());
                startActivity(new Intent(getActivity(), DescActivity.class).putExtras(bundle));
            });
            recyclerView.setAdapter(adapter);
        }
    }

    private void initWeekData() {
        loading.setVisibility(View.GONE);
        if (adapter.getData().isEmpty()) {
            list = getList(week);
            if (list.size() == 0) {
                errorTitle.setText(application.error);
                adapter.setEmptyView(errorView);
            } else {
                adapter.setNewData(list);
            }
        }
    }

    private List getList(String week) {
        list = new ArrayList<>();
        if (application.week.length() > 0) {
            try {
                JSONArray arr = new JSONArray(application.week.getString(week));
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject object = new JSONObject(arr.getString(i));
                    list.add(new HomeWekBean(object.getString("title"),
                            object.getString("img"),
                            object.getString("url"),
                            object.getString("drama")));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnBinder.unbind();
    }
}
