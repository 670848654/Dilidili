package anime.project.dilidili.ijkplayer;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.view.Surface;

import java.io.IOException;

import cn.jzvd.JZMediaInterface;
import cn.jzvd.JZMediaManager;
import cn.jzvd.JzvdMgr;
import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;
import tv.danmaku.ijk.media.player.IjkTimedText;

public class JZMediaIjkplayer extends JZMediaInterface implements IMediaPlayer.OnPreparedListener, IMediaPlayer.OnVideoSizeChangedListener, IMediaPlayer.OnCompletionListener, IMediaPlayer.OnErrorListener, IMediaPlayer.OnInfoListener, IMediaPlayer.OnBufferingUpdateListener, IMediaPlayer.OnSeekCompleteListener, IMediaPlayer.OnTimedTextListener {
    IjkMediaPlayer ijkMediaPlayer;

    @Override
    public void start() {
        ijkMediaPlayer.start();
    }

    @Override
    public void prepare() {
        ijkMediaPlayer = new IjkMediaPlayer();
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", 1);
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "opensles", 0);
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "overlay-format", IjkMediaPlayer.SDL_FCC_RV32);
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "framedrop", 1);
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "start-on-prepared", 0);
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "http-detect-range-support", 0);
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_CODEC, "skip_loop_filter", 48);
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_CODEC, "skip_loop_filter", 8);
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "analyzemaxduration", 100L);
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "probesize", 10240L);
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "flush_packets", 1L);
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "packet-buffering", 0L);
        ijkMediaPlayer.setOnPreparedListener(JZMediaIjkplayer.this);
        ijkMediaPlayer.setOnVideoSizeChangedListener(JZMediaIjkplayer.this);
        ijkMediaPlayer.setOnCompletionListener(JZMediaIjkplayer.this);
        ijkMediaPlayer.setOnErrorListener(JZMediaIjkplayer.this);
        ijkMediaPlayer.setOnInfoListener(JZMediaIjkplayer.this);
        ijkMediaPlayer.setOnBufferingUpdateListener(JZMediaIjkplayer.this);
        ijkMediaPlayer.setOnSeekCompleteListener(JZMediaIjkplayer.this);
        ijkMediaPlayer.setOnTimedTextListener(JZMediaIjkplayer.this);

        try {
            ijkMediaPlayer.setDataSource(jzDataSource.getCurrentUrl().toString());
            ijkMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            ijkMediaPlayer.setScreenOnWhilePlaying(true);
            ijkMediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void pause() {
        ijkMediaPlayer.pause();
    }

    @Override
    public boolean isPlaying() {
        return ijkMediaPlayer.isPlaying();
    }

    @Override
    public void seekTo(long time) {
        ijkMediaPlayer.seekTo(time);
    }

    @Override
    public void release() {
        if (ijkMediaPlayer != null)
            ijkMediaPlayer.release();
    }

    @Override
    public long getCurrentPosition() {
        return ijkMediaPlayer.getCurrentPosition();
    }

    @Override
    public long getDuration() {
        return ijkMediaPlayer.getDuration();
    }

    @Override
    public void setSurface(Surface surface) {
        ijkMediaPlayer.setSurface(surface);
    }

    @Override
    public void setVolume(float leftVolume, float rightVolume) {
        ijkMediaPlayer.setVolume(leftVolume, rightVolume);
    }

    @Override
    public void setSpeed(float speed) {
        ijkMediaPlayer.setSpeed(speed);
    }

    @Override
    public void onPrepared(IMediaPlayer iMediaPlayer) {
        ijkMediaPlayer.start();
        if (jzDataSource.getCurrentUrl().toString().toLowerCase().contains("mp3")) {
            JZMediaManager.instance().mainThreadHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (JzvdMgr.getCurrentJzvd() != null) {
                        JzvdMgr.getCurrentJzvd().onPrepared();
                    }
                }
            });
        }
    }

    @Override
    public void onVideoSizeChanged(IMediaPlayer iMediaPlayer, int i, int i1, int i2, int i3) {
        JZMediaManager.instance().currentVideoWidth = iMediaPlayer.getVideoWidth();
        JZMediaManager.instance().currentVideoHeight = iMediaPlayer.getVideoHeight();
        JZMediaManager.instance().mainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                if (JzvdMgr.getCurrentJzvd() != null) {
                    JzvdMgr.getCurrentJzvd().onVideoSizeChanged();
                }
            }
        });
    }

    @Override
    public void onCompletion(IMediaPlayer iMediaPlayer) {
        JZMediaManager.instance().mainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                if (JzvdMgr.getCurrentJzvd() != null) {
                    JzvdMgr.getCurrentJzvd().onAutoCompletion();
                }
            }
        });
    }

    @Override
    public boolean onError(IMediaPlayer iMediaPlayer, final int what, final int extra) {
        JZMediaManager.instance().mainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                if (JzvdMgr.getCurrentJzvd() != null) {
                    JzvdMgr.getCurrentJzvd().onError(what, extra);
                }
            }
        });
        return true;
    }

    @Override
    public boolean onInfo(IMediaPlayer iMediaPlayer, final int what, final int extra) {
        JZMediaManager.instance().mainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                if (JzvdMgr.getCurrentJzvd() != null) {
                    if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                        JzvdMgr.getCurrentJzvd().onPrepared();
                    } else {
                        JzvdMgr.getCurrentJzvd().onInfo(what, extra);
                    }
                }
            }
        });
        return false;
    }

    @Override
    public void onBufferingUpdate(IMediaPlayer iMediaPlayer, final int percent) {
        JZMediaManager.instance().mainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                if (JzvdMgr.getCurrentJzvd() != null) {
                    JzvdMgr.getCurrentJzvd().setBufferProgress(percent);
                }
            }
        });
    }

    @Override
    public void onSeekComplete(IMediaPlayer iMediaPlayer) {
        JZMediaManager.instance().mainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                if (JzvdMgr.getCurrentJzvd() != null) {
                    JzvdMgr.getCurrentJzvd().onSeekComplete();
                }
            }
        });
    }

    @Override
    public void onTimedText(IMediaPlayer iMediaPlayer, IjkTimedText ijkTimedText) {

    }
}