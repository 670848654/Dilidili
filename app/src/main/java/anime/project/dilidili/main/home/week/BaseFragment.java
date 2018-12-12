package anime.project.dilidili.main.home.week;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import anime.project.dilidili.R;
import anime.project.dilidili.application.DiliDili;
import anime.project.dilidili.database.DatabaseUtil;
import anime.project.dilidili.adapter.FragmentAdapter;
import anime.project.dilidili.bean.HomeBean;
import anime.project.dilidili.main.desc.DescActivity;
import anime.project.dilidili.main.video.VideoContract;
import anime.project.dilidili.main.video.VideoPresenter;
import anime.project.dilidili.main.video.VideoUtils;
import anime.project.dilidili.main.video.VideoView;
import anime.project.dilidili.main.player.PlayerActivity;
import anime.project.dilidili.main.webview.WebActivity;
import anime.project.dilidili.util.SharedPreferencesUtils;
import anime.project.dilidili.util.Utils;
import butterknife.BindView;
import butterknife.ButterKnife;

public abstract class BaseFragment extends Fragment implements VideoContract.View {
    @BindView(R.id.rv_list)
    RecyclerView recyclerView;
    private FragmentAdapter adapter;
    private List<HomeBean> list;
    private DiliDili application;
    private ProgressDialog p;
    private String diliUrl;
    private String videoTitle;
    private AlertDialog alertDialog;
    private String[] videoUrlArr;
    private String[] videoTitleArr;
    private View view;
    private VideoPresenter presenter;
    private View errorView;
    public TextView errorTitle;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_week, container, false);
        ButterKnife.bind(this,view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        errorView = getLayoutInflater().inflate(R.layout.base_error_view, (ViewGroup) recyclerView.getParent(), false);
        errorTitle = errorView.findViewById(R.id.title);
        if (application == null) {
            application = (DiliDili) getActivity().getApplication();
        }
        initAdapter(getList(getWeek()));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != presenter)
            presenter.detachView();
    }

    public List getList(String week){
        list = new ArrayList<>();
        if (application.week.length() > 0){
            try {
                JSONArray arr = new JSONArray(application.week.getString(week));
                for (int i=0;i<arr.length();i++){
                    JSONObject object = new JSONObject(arr.getString(i));
                    list.add(new HomeBean(object.getString("title"),object.getString("url"),object.getString("watchTitle"),object.getString("watchUrl")));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    public void initAdapter(final List list){
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new FragmentAdapter(list);
        adapter.openLoadAnimation();
        adapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);
        adapter.isFirstOnly((Boolean) SharedPreferencesUtils.getParam(getActivity(), "anim_is_first", true));//init firstOnly state
        adapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                final HomeBean bean = (HomeBean) list.get(position);
                switch (view.getId()) {
                    case R.id.tag_group:
                        if (Utils.isFastClick()) {
                            Bundle bundle = new Bundle();
                            bundle.putString("name", bean.getTitle());
                            bundle.putString("url", bean.getUrl());
                            startActivity(new Intent(getActivity(), DescActivity.class).putExtras(bundle));
                        }
                        break;
                    case R.id.witch:
                        if (Utils.isFastClick()) {
                            p = Utils.getProDialog(getActivity(), "解析中,请稍后...");
                            diliUrl = bean.getWitchUrl();
                            videoTitle = bean.getTitle() + " - " + bean.getWitchTitle();
                            //创建番剧名
                            DatabaseUtil.addAnime(bean.getTitle());
                            presenter =  new VideoPresenter(bean.getTitle(), bean.getWitchUrl(),BaseFragment.this);
                            presenter.loadData(true);
                        }
                        break;
                }
            }
        });
        recyclerView.setAdapter(adapter);
        if (list.size() == 0)
        {
            errorTitle.setText(application.error);
            adapter.setEmptyView(errorView);
        }
    }

    public void goToPlay(String videoUrl){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                String[] arr = VideoUtils.removeByIndex(videoUrl.split("http"), 0);
                //如果播放地址只有1个
                if (arr.length == 1){
                    String url = "http"+arr[0];
                    if (url.contains(".m3u8") || url.contains(".mp4")){
                        switch ((Integer) SharedPreferencesUtils.getParam(getActivity(),"player",0)){
                            case 0:
                                //调用播放器
                                Bundle bundle = new Bundle();
                                bundle.putBoolean("is", true);
                                bundle.putString("title", videoTitle);
                                bundle.putString("url", url);
                                startActivity(new Intent(getActivity(), PlayerActivity.class).putExtras(bundle));
                                break;
                            case 1:
                                Utils.selectVideoPlayer(getActivity(),url);
                                break;
                        }
                    }else {
                        Bundle bundle = new Bundle();
                        bundle.putBoolean("is", true);
                        bundle.putString("url", url);
                        bundle.putString("dili", diliUrl);
                        startActivity(new Intent(getActivity(), WebActivity.class).putExtras(bundle));
                    }
                }else {
                    videoUrlArr = new String[arr.length];
                    videoTitleArr = new String[arr.length];
                    for (int i=0;i<arr.length;i++) {
                        String str = "http" + arr[i];
                        Log.e("video",str);
                        videoUrlArr[i] = str;
                        java.net.URL  urlHost;
                        try {
                            urlHost = new java.net.URL(str);
                            if (str.contains(".mp4"))
                                videoTitleArr[i] = urlHost.getHost() + " <MP4> <播放器>";
                            else if (str.contains(".m3u8"))
                                videoTitleArr[i] = urlHost.getHost() + " <M3U8> <播放器>";
                            else
                                videoTitleArr[i] = urlHost.getHost() + " <HTML>";
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }
                    }
                    selectVideoDialog();
                }
            }
        }, 200);

    }

    private void selectVideoDialog() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(getActivity());
        builder.setTitle("选择视频源");
        builder.setCancelable(false);
        builder.setItems(videoTitleArr, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int index) {
                if (videoUrlArr[index].contains(".m3u8") || videoUrlArr[index].contains(".mp4")){
                    switch ((Integer) SharedPreferencesUtils.getParam(getActivity(),"player",0)){
                        case 0:
                            //调用播放器
                            Bundle bundle = new Bundle();
                            bundle.putBoolean("is", true);
                            bundle.putString("title", videoTitle);
                            bundle.putString("url", videoUrlArr[index]);
                            startActivity(new Intent(getActivity(), PlayerActivity.class).putExtras(bundle));
                            break;
                        case 1:
                            Utils.selectVideoPlayer(getActivity(),videoUrlArr[index]);
                            break;
                    }
                }else {
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("is", true);
                    bundle.putString("url", videoUrlArr[index]);
                    bundle.putString("dili", diliUrl);
                    startActivity(new Intent(getActivity(), WebActivity.class).putExtras(bundle));
                }
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog = builder.create();
        alertDialog.show();
    }

    public abstract String getWeek();

    @Override
    public void getVideoSuccess(String url) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Utils.cancelProDoalog(p);
                goToPlay(url);
            }
        });
    }

    @Override
    public void getVideoEmpty() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Utils.cancelProDoalog(p);
                VideoUtils.showErrorInfo(getActivity(), diliUrl);
            }
        });
    }

    @Override
    public void getVideoError() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Utils.cancelProDoalog(p);
                //网络出错
                Toast.makeText(getActivity(), Utils.getString(getActivity(), R.string.error_700), Toast.LENGTH_LONG).show();
            }
        });
    }

}
