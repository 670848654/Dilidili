package anime.project.dilidili.util;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import anime.project.dilidili.R;
import anime.project.dilidili.application.DiliDili;
import anime.project.dilidili.bean.AnimeDescBean;
import anime.project.dilidili.main.player.PlayerActivity;
import anime.project.dilidili.main.webview.normal.NormalWebActivity;
import anime.project.dilidili.main.webview.x5.X5WebActivity;

public class VideoUtils {
    private static AlertDialog alertDialog;

    /**
     * 获取播放列表数组
     *
     * @param oldArr
     * @param index
     * @return
     */
    public static String[] removeByIndex(String[] oldArr, int index) {
        List<String> list = new ArrayList<>();
        Collections.addAll(list, oldArr);
        list.remove(index);
        String[] newArr = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            newArr[i] = list.get(i);
        }
        return newArr;
    }

    /**
     * 解析失败提示弹窗
     *
     * @param context
     * @param HTML_url
     */
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
//            context.startActivity(new Intent(context, DefaultNormalWebActivity.class).putExtra("url", HTML_url));
            Utils.viewInChrome(context, HTML_url);
        });
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(v -> alertDialog.dismiss());
    }

    /**
     * 发现多个播放地址时弹窗
     *
     * @param context
     * @param arr
     * @param videoTitleArr
     * @param videoUrlArr
     * @param listener
     */
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
                if (str.contains(".mp4")) videoTitleArr[i] = urlHost.getHost() + " <MP4> <播放器>";
                else if (str.contains(".m3u8"))
                    videoTitleArr[i] = urlHost.getHost() + " <M3U8> <播放器>";
                else videoTitleArr[i] = urlHost.getHost() + " <HTML>";
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

    /**
     * 打开播放器
     *
     * @param isDescActivity
     * @param activity
     * @param witchTitle
     * @param url
     * @param animeTitle
     * @param diliUrl
     * @param list
     */
    public static void openPlayer(boolean isDescActivity, Activity activity, String witchTitle, String url, String animeTitle, String diliUrl, List<AnimeDescBean> list) {
        Bundle bundle = new Bundle();
        bundle.putString("title", witchTitle);
        bundle.putString("url", url);
        bundle.putString("animeTitle", animeTitle);
        bundle.putString("dili", diliUrl);
        bundle.putSerializable("list", (Serializable) list);
        DiliDili.destoryActivity("player");
        if (isDescActivity)
            activity.startActivityForResult(new Intent(activity, PlayerActivity.class).putExtras(bundle), 0x10);
        else {
            activity.startActivity(new Intent(activity, PlayerActivity.class).putExtras(bundle));
            activity.finish();
        }
    }

    /**
     * 打开webview
     *
     * @param isDescActivity
     * @param activity
     * @param witchTitle
     * @param animeTitle
     * @param url
     * @param diliUrl
     * @param list
     */
    public static void openWebview(boolean isDescActivity, Activity activity, String witchTitle, String animeTitle, String url, String diliUrl, List<AnimeDescBean> list) {
        Bundle bundle = new Bundle();
        bundle.putString("witchTitle", witchTitle);
        bundle.putString("title", animeTitle);
        bundle.putString("url", url);
        bundle.putString("dili", diliUrl);
        bundle.putSerializable("list", (Serializable) list);
        if (isDescActivity) {
            if (Utils.loadX5())
                activity.startActivityForResult(new Intent(activity, X5WebActivity.class).putExtras(bundle), 0x10);
            else
                activity.startActivityForResult(new Intent(activity, NormalWebActivity.class).putExtras(bundle), 0x10);
        } else {
            if (Utils.loadX5())
                activity.startActivity(new Intent(activity, X5WebActivity.class).putExtras(bundle));
            else
                activity.startActivity(new Intent(activity, NormalWebActivity.class).putExtras(bundle));
            activity.finish();
        }
    }

    /**
     * 获取链接
     *
     * @param url
     * @return
     */
    public static String getDiliUrl(String url) {
        if (url.contains("http://www.dilidili.wang"))
            url = url.replace("http://www.dilidili.wang", DiliDili.DOMAIN);
        else
            url = url.startsWith("http") ? url : DiliDili.DOMAIN + url;
        return url;
    }
}
