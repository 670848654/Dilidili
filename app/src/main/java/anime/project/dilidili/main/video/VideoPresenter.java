package anime.project.dilidili.main.video;

import anime.project.dilidili.main.base.BasePresenter;

public class VideoPresenter implements BasePresenter,VideoModel.LoadDataCallback {
    private VideoView videoView;
    private VideoModel playModel;
    private String title;
    private String url;

    public VideoPresenter(String title, String url, VideoView videoView){
        this.title = title;
        this.url = url;
        this.videoView = videoView;
        playModel = new VideoModel();
    }

    @Override
    public void loadData(boolean isMain) {
        playModel.getData(title, url, this);
    }

    @Override
    public void success(String url) {
        videoView.getVideoSuccess(url);
    }

    @Override
    public void error() {
        videoView.getVideoError();
    }

    @Override
    public void empty() {
        videoView.getVideoEmpty();
    }
}
