package personal.nfl.frameskipmonitor.frame.utils;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import personal.nfl.frameskipmonitor.MyApplication;

import java.lang.ref.WeakReference;

public class MyActivityLifeCycle implements Application.ActivityLifecycleCallbacks {
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private boolean mPaused = true;
    private Runnable mCheckForegroundRunnable;
    private boolean mForeground = false;
    private static MyActivityLifeCycle sInstance;
    //当前Activity的弱引用
    private WeakReference<Activity> mActivityReference;

    protected final String TAG = "MyActivityLifeCycle";

    public static final int ACTIVITY_ON_RESUME = 0;
    public static final int ACTIVITY_ON_PAUSE = 1;

    private MyActivityLifeCycle() {
    }

    public static synchronized MyActivityLifeCycle getInstance() {
        if (sInstance == null) {
            sInstance = new MyActivityLifeCycle();
        }
        return sInstance;
    }

    public Activity getCurrentActivity() {
        if (mActivityReference != null) {
            return mActivityReference.get();
        }
        return null;
    }

    public boolean isForeground() {
        return mForeground;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        Log.d(TAG, activity.getLocalClassName() + " onActivityCreated");
        mActivityReference = new WeakReference<>(activity);
    }

    @Override
    public void onActivityStarted(Activity activity) {
        Log.d(TAG, activity.getLocalClassName() + " onActivityStarted");
    }

    @Override
    public void onActivityResumed(Activity activity) {
        String activityName = activity.getClass().getName();
        notifyActivityChanged(activityName, ACTIVITY_ON_RESUME);
        mPaused = false;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN
                /*&& ConfigManager.getInstance().isFrameSkipCheckOn()*//*配置是否开启统计*/) {
            FrameSkipMonitor.getInstance().setActivityName(activityName);
            FrameSkipMonitor.getInstance().OnActivityResume();
            if (!mForeground) {
                FrameSkipMonitor.getInstance().start();
            }
        }
        mForeground = true;
        if (mCheckForegroundRunnable != null) {
            mHandler.removeCallbacks(mCheckForegroundRunnable);
        }
        mActivityReference = new WeakReference<>(activity);
    }

    @Override
    public void onActivityPaused(Activity activity) {
        // pause事件后是否在前台要分情况判断
        notifyActivityChanged(activity.getClass().getName(), ACTIVITY_ON_PAUSE);
        mPaused = true;
        if (mCheckForegroundRunnable != null) {
            mHandler.removeCallbacks(mCheckForegroundRunnable);
        }
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
            FrameSkipMonitor.getInstance().OnActivityPause();
        }

        mHandler.postDelayed(mCheckForegroundRunnable = () -> {
            if (mPaused && mForeground) {
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
                    FrameSkipMonitor.getInstance().report();
                }
                mForeground = false;
            }
        }, 1000);

    }

    @Override
    public void onActivityStopped(Activity activity) {
        Log.d(TAG, activity.getLocalClassName() + " onActivityStopped");
    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        Log.d(TAG, activity.getLocalClassName() + " onActivityDestroyed");
    }

    private void notifyActivityChanged(String activityName, int lifeState) {
        Intent intent = new Intent("personal.nfl.frameskipmonitor.activity_change");
        intent.putExtra("activity_name", activityName);
        intent.putExtra("activity_life", lifeState);
        LocalBroadcastManager.getInstance(MyApplication.getApplication()).sendBroadcast(intent);
    }
}
