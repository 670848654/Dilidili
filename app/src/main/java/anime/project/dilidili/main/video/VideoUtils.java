package anime.project.dilidili.main.video;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AlertDialog;
import anime.project.dilidili.R;
import anime.project.dilidili.main.webview.DefaultWebActivity;
import anime.project.dilidili.util.Utils;

public class VideoUtils {
    public static AlertDialog alertDialog;

    /**
     * 获取播放列表数组
     * @param oldArr
     * @param index
     * @return
     */
    public static String[] removeByIndex(String[] oldArr, int index) {
        List<String> list = new ArrayList<>();
        for(int i=0;i<oldArr.length;i++){
            list.add(oldArr[i]);
        }
        list.remove(index);
        String[] newArr = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            newArr[i] = list.get(i);
        }
        return newArr;
    }

    public static void showErrorInfo(Context context, String HTML_url) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setPositiveButton(Utils.getString(context, R.string.play_not_found_positive), null);
        builder.setNegativeButton(Utils.getString(context, R.string.play_not_found_negative), null);
        builder.setTitle(Utils.getString(context, R.string.play_not_found_title));
        builder.setMessage(Utils.getString(context, R.string.error_800));
        builder.setCancelable(false);
        alertDialog = builder.create();
        alertDialog.show();
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                context.startActivity(new Intent(context, DefaultWebActivity.class).putExtra("url", HTML_url));
            }
        });
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
    }
}
