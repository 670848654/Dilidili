package anime.project.dilidili.util;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AlertDialog;
import anime.project.dilidili.R;
import anime.project.dilidili.main.player.PlayerActivity;
import anime.project.dilidili.main.webview.DefaultWebActivity;
import anime.project.dilidili.main.webview.WebActivity;

public class VideoUtils {
    public static AlertDialog alertDialog;

    /**
     * 获取播放列表数组
     *
     * @param oldArr
     * @param index
     * @return
     */
    public static String[] removeByIndex(String[] oldArr, int index) {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < oldArr.length; i++) {
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
        builder.setPositiveButton(Utils.getString(R.string.play_not_found_positive), null);
        builder.setNegativeButton(Utils.getString(R.string.play_not_found_negative), null);
        builder.setTitle(Utils.getString(R.string.play_not_found_title));
        builder.setMessage(Utils.getString(R.string.error_800));
        builder.setCancelable(false);
        alertDialog = builder.create();
        alertDialog.show();
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            alertDialog.dismiss();
            context.startActivity(new Intent(context, DefaultWebActivity.class).putExtra("url", HTML_url));
        });
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(v -> alertDialog.dismiss());
    }

    public static void showMultipleVideoSources(Context context,
                                                String[] arr,
                                                String[] videoTitleArr,
                                                String[] videoUrlArr,
                                                DialogInterface.OnClickListener listener) {
        for (int i = 0; i < arr.length; i++) {
            String str = "http" + arr[i];
            Log.e("video", str);
            videoUrlArr[i] = str;
            java.net.URL urlHost;
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
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context);
        builder.setTitle("选择视频源");
        builder.setCancelable(false);
        builder.setItems(videoTitleArr, listener);
        builder.setNegativeButton("取消", (dialog, which) -> dialog.dismiss());
        alertDialog = builder.create();
        alertDialog.show();
    }
}
