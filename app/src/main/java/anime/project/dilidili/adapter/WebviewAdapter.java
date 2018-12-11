package anime.project.dilidili.adapter;

import android.content.Context;
import android.widget.Button;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import anime.project.dilidili.R;
import anime.project.dilidili.bean.WebviewBean;

public class WebviewAdapter extends BaseQuickAdapter<WebviewBean, BaseViewHolder> {
    private Context context;
    public WebviewAdapter(Context context, List list) {
        super(R.layout.item_webview, list);
        this.context = context;
    }

    @Override
    protected void convert(BaseViewHolder helper, WebviewBean item) {
        Button button = helper.getView(R.id.btn);
        helper.setText(R.id.btn,item.getTitle());
        button.setTextColor(context.getResources().getColor(R.color.grey1000));
        button.setBackgroundResource(R.drawable.button_unpress);
        if (item.isSelect()){
            button.setTextColor(context.getResources().getColor(R.color.md_white_1000));
            button.setBackgroundResource(R.drawable.button_onpress);
        }
        else
            button.setBackgroundResource(R.drawable.button_unpress);
    }
}
