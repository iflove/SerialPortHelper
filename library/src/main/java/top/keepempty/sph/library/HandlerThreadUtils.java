package top.keepempty.sph.library;

import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Process;

import com.androidz.logextlibrary.Logg;

import java.util.concurrent.atomic.AtomicInteger;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


/**
 * HandlerThread And Handler Wrapper Utility class
 * Created by rentianlong on 2018/12/27
 */
public final class HandlerThreadUtils {
    private static final String TAG = "HandlerThreadUtil";
    private final static AtomicInteger THREAD_RUNNING_NUM = new AtomicInteger();
    private Handler mAsyncHandler;
    private HandlerThread mThread;
    private String mName;

    public HandlerThreadUtils(@NonNull String name, @Nullable Handler.Callback callback, int priority) {
        mName = String.format("%s#%s: %s, callBack@%s", TAG, THREAD_RUNNING_NUM.incrementAndGet(), name, callback == null ? "null" : callback.hashCode());
        mThread = new HandlerThread(mName, priority);
        mThread.start();
        mAsyncHandler = new Handler(mThread.getLooper(), callback);
        Logg.d(TAG, "startHandlerThread: " + mName);
    }

    public HandlerThreadUtils(@NonNull String name, @Nullable Handler.Callback callback) {
        this(name, callback, Process.THREAD_PRIORITY_DEFAULT);
    }

    public HandlerThreadUtils(Handler.Callback callback) {
        this("DEFAULT", callback, Process.THREAD_PRIORITY_DEFAULT);
    }

    public void postDelayed(int what, long delayMillis) {
        if (mAsyncHandler != null) {
            mAsyncHandler.sendEmptyMessageDelayed(what, delayMillis);
        }
    }

    public void removeMessages(int what) {
        if (mAsyncHandler != null) {
            if (mAsyncHandler.hasMessages(what)) {
                mAsyncHandler.removeMessages(what);
            }
        }
    }

    public final void removeMessages(int what, Object object) {
        if (mAsyncHandler != null) {
            mAsyncHandler.removeMessages(what, object);
        }
    }

    public Handler getHandler() {
        return mAsyncHandler;
    }

    public void run(@androidx.annotation.NonNull Runnable r) {
        if (mAsyncHandler != null) {
            mAsyncHandler.post(r);
        }
    }

    public void stopThread() {
        if (mThread != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                mThread.quitSafely();
            } else {
                mThread.quit();
            }
            mThread = null;
            THREAD_RUNNING_NUM.decrementAndGet();
            if (BuildConfig.DEBUG) {
                Logg.d(TAG, "stopThread: " + mName);
            }
        }
        if (mAsyncHandler != null) {
            mAsyncHandler.removeCallbacksAndMessages(null);
            mAsyncHandler = null;
        }
    }

    public void sendMessage(int what, Object obj) {
        if (mAsyncHandler != null) {
            mAsyncHandler.sendMessage(mAsyncHandler.obtainMessage(what, obj));
        }
    }
}
