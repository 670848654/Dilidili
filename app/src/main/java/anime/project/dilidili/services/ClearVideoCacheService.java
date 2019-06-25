package anime.project.dilidili.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import java.io.File;

import androidx.annotation.Nullable;
import anime.project.dilidili.util.Utils;

public class ClearVideoCacheService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(){
            @Override
            public void run() {
                Utils.deleteAllFiles(new File(android.os.Environment.getExternalStorageDirectory() + "/Android/data/anime.project.dilidili/cache"));
                Utils.deleteAllFiles(new File(android.os.Environment.getExternalStorageDirectory() + "/Android/data/anime.project.dilidili/files/VideoCache/main"));
                onDestroy();
            }
        }.start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
