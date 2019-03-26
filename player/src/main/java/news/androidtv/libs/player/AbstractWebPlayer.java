package news.androidtv.libs.player;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;
import android.webkit.WebView;

import com.google.android.media.tv.companionlibrary.TvPlayer;

import java.util.List;

/**
 * Created by Nick on 10/27/2016.
 */
public abstract class AbstractWebPlayer extends WebView implements TvPlayer {
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2526.35 Safari/537.36";
    private static final String TAG = AbstractWebPlayer.class.getSimpleName();
    private static boolean DEBUG = false;
    private static boolean LOG_ERROR = true;

    private WebEventsListener mWebListener;
    private List<VideoEventsListener> mVideoListeners;
    private long mCurrentPos;
    private long mDuration;

    public AbstractWebPlayer(Context context) {
        super(context);
        initialize(context);
    }

    public AbstractWebPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    public AbstractWebPlayer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context);
    }

    public AbstractWebPlayer(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initialize(context);
    }

    public void setDebug(boolean debug) {
        DEBUG = debug;
    }

    public void setLogError(boolean debug) {
        DEBUG = debug;
    }

    private void initialize(Context context) {
        mWebListener = new WebEventsListener() {
            @Override
            public void onWindowLoad() {
                if (DEBUG) {
                    Log.d(TAG, "Page has finished loading");
                }
                onPlayVideo();
            }

            @Override
            public void onVideoStatusEnded() {
                if (DEBUG) {
                    Log.d(TAG, "Video ended");
                }
                onEndVideo();
                if (mVideoListeners != null) {
                    for (VideoEventsListener listener : mVideoListeners) {
                        listener.onVideoEnded();
                    }
                }
            }

            @Override
            public void onPositionChanged(long position) {
                mCurrentPos = position;
            }

            @Override
            public void onReceivedDuration(long duration) {
                mDuration = duration;
            }

            @Override
            public void onError(String message) {
                if (DEBUG || LOG_ERROR) {
                    Log.e(TAG, message);
                }
            }
        };
        getSettings().setJavaScriptEnabled(true);
        getSettings().setSupportZoom(false);
        getSettings().setSupportMultipleWindows(false);
        setWebViewClient(new WebPlayerClient(this, mWebListener));
        addJavascriptInterface(new WebInterface(context, this), "Android");
        getSettings().setUserAgentString(USER_AGENT); //Claim to be a desktop
        setKeepScreenOn(true);
    }

    public void addVideoEventsListener(VideoEventsListener listener) {
        mVideoListeners.add(listener);
    }

    public void removeVideoEventsListener(VideoEventsListener listener) {
        mVideoListeners.remove(listener);
    }

    protected WebEventsListener getWebEventsListener() {
        return mWebListener;
    }

    public void setVideoUrlTo(final String url) {
        if (DEBUG) {
            Log.d(TAG, "Loading URL " + url);
        }
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                loadUrl(url);
            }
        });

//        String html = "<iframe src=" + url + " allow=\"autoplay; allowfullscreen\"></iframe>";
//
//        String html = "<iframe width=\"560\" height=\"315\" src=\"https://www.youtube.com/embed/95Rzsn-zjr0\" frameborder=\"0\" allow=\"accelerometer; autoplay; encrypted-media; gyroscope; picture-in-picture\" allowfullscreen></iframe>";
//        this.loadData(html, "text/html", null);
    }

    protected void runJavascript(final String js) {
        post(new Runnable() {
            @Override
            public void run() {
                if (DEBUG) {
                    if (js.length() < 80) {
                        Log.d(TAG, "Execute " + js);
                    } else {
                        Log.d(TAG, "Execute " + js.substring(0, 79));
                    }
                }
                loadUrl("javascript:try { " + js + "} catch(error) { Android.onError(error.message) }");
            }
        });
    }

    public long getCurrentPosition() {
        return mCurrentPos;
    }

    public long getDuration() {
        return mDuration;
    }

    @Override
    public void setSurface(Surface surface) {
        // Ignore this since we don't use surfaces
    }

    protected abstract void onPlayVideo();
    protected abstract void onEndVideo();

    public interface WebEventsListener {
        void onWindowLoad();
        void onVideoStatusEnded();
        void onPositionChanged(long position);
        void onReceivedDuration(long duration);
        void onError(String message);
    }

    public interface VideoEventsListener {
        void onVideoEnded();
    }
}