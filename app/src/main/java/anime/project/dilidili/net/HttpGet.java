package anime.project.dilidili.net;

import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class HttpGet {
    public HttpGet(String url, int connectTimeout, int readTimeout, Callback callback) {
        OkHttpClient client = new OkHttpClient.Builder().connectTimeout(connectTimeout, TimeUnit.SECONDS).readTimeout(readTimeout, TimeUnit.SECONDS).build();
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
    }
}
