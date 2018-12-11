package anime.project.dilidili.main.about;

import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.r0adkll.slidr.Slidr;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import anime.project.dilidili.R;
import anime.project.dilidili.adapter.SourceAdapter;
import anime.project.dilidili.main.base.BaseActivity;
import anime.project.dilidili.bean.SourceBean;
import anime.project.dilidili.util.StatusBarUtil;
import anime.project.dilidili.util.Utils;
import butterknife.BindView;

public class OpenSourceActivity extends BaseActivity {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.mSwipe)
    SwipeRefreshLayout mSwipe;
    @BindView(R.id.rv_list)
    RecyclerView recyclerView;
    private SourceAdapter adapter;
    private List<SourceBean> list = new ArrayList<>();

    @Override
    protected int setLayoutRes() {
        return R.layout.activity_source;
    }

    @Override
    protected void init() {
        StatusBarUtil.setColorForSwipeBack(OpenSourceActivity.this, getResources().getColor(R.color.night), 0);
        Slidr.attach(this, Utils.defaultInit());
        initToolbar();
        initSwipe();
        initList();
        initAdapter();
    }

    @Override
    protected void initBeforView() {

    }

    public void initToolbar(){
        toolbar.setTitle("开源相关");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                supportFinishAfterTransition();
            }
        });
    }

    public void initSwipe(){
        mSwipe.setEnabled(false);
    }

    public void initList(){
        list.add(new SourceBean("jsoup","jhy","jsoup: Java HTML Parser, with best of DOM, CSS, and jquery","https://github.com/jhy/jsoup"));
        list.add(new SourceBean("BaseRecyclerViewAdapterHelper","CymChad","BRVAH:Powerful and flexible RecyclerAdapter","https://github.com/CymChad/BaseRecyclerViewAdapterHelper"));
        list.add(new SourceBean("MaterialDrawer","mikepenz","The flexible, easy to use, all in one drawer library for your Android project. Now brand new with material 2 design.","https://github.com/mikepenz/MaterialDrawer"));
        list.add(new SourceBean("Glide","bumptech","An image loading and caching library for Android focused on smooth scrolling","https://github.com/bumptech/glide"));
        list.add(new SourceBean("EasyPermissions","googlesamples","Simplify Android M system permissions","https://github.com/googlesamples/easypermissions"));
        list.add(new SourceBean("MaterialEditText","rengwuxian","EditText in Material Design","https://github.com/rengwuxian/MaterialEditText"));
        list.add(new SourceBean("JiaoZiVideoPlayer","lipangit","Android VideoPlayer MediaPlayer VideoView MediaView Float View And Fullscreen","https://github.com/lipangit/JiaoZiVideoPlayer"));
        list.add(new SourceBean("ijkplayer","Bilibili","Android/iOS video player based on FFmpeg n3.4, with MediaCodec, VideoToolbox support.","https://github.com/Bilibili/ijkplayer"));
        list.add(new SourceBean("Blurry","wasabeef","Blurry is an easy blur library for Android","https://github.com/wasabeef/Blurry"));
        list.add(new SourceBean("Slidr","r0adkll","Easily add slide to dismiss functionality to an Activity","https://github.com/r0adkll/Slidr"));
        list.add(new SourceBean("butterknife","JakeWharton","Bind Android views and callbacks to fields and methods.","https://github.com/JakeWharton/butterknife"));
        list.add(new SourceBean("okhttp","square","An HTTP+HTTP/2 client for Android and Java applications.","https://github.com/square/okhttp"));
    }

    public void initAdapter(){
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SourceAdapter(list);
        adapter.openLoadAnimation();
        adapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (Utils.isFastClick())
                    Utils.viewInBrowser(OpenSourceActivity.this,list.get(position).getUrl());
            }
        });
        recyclerView.setAdapter(adapter);
    }
}
