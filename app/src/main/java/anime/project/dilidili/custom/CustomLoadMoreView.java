package anime.project.dilidili.custom;

import com.chad.library.adapter.base.loadmore.LoadMoreView;

import anime.project.dilidili.R;

public class CustomLoadMoreView extends LoadMoreView {
    @Override
    public int getLayoutId() {
        return R.layout.custom_load_more_view;
    }

    @Override
    protected int getLoadingViewId() {
        return R.id.load_more_loading_view;
    }

    @Override
    protected int getLoadFailViewId() {
        return R.id.load_more_load_fail_view;
    }

    @Override
    protected int getLoadEndViewId() { return R.id.load_more_load_end_view; }
}
