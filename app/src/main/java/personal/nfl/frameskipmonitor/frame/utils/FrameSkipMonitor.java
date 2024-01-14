package personal.nfl.frameskipmonitor.frame.utils;

import android.annotation.TargetApi;
import android.os.Build;
import android.util.Log;
import android.view.Choreographer;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class FrameSkipMonitor implements Choreographer.FrameCallback {
    protected final String TAG = "FrameSkipMonitor";
    private static final long ONE_FRAME_TIME = 16600000; // 1 Frame time cost
    private static final long MIN_FRAME_TIME = ONE_FRAME_TIME * 3; // 3 Frame time cost
    private static final long MAX_FRAME_TIME = 60 * ONE_FRAME_TIME; // 60 Frame time cost, not record some special cases.

    private static final String SKIP_EVENT_NAME = "frame_skip";

    private static FrameSkipMonitor sInstance;

    private long mLastFrameNanoTime = 0;
    private HashMap<String, Long> mSkipRecordMap;
    private HashMap<String, Long> mActivityShowTimeMap;
    private String mActivityName;
    private long mActivityStartTime = 0;

    private FrameSkipMonitor() {
        mSkipRecordMap = new HashMap<>();
        mActivityShowTimeMap = new HashMap<>();
    }

    public static FrameSkipMonitor getInstance() {
        if (sInstance == null) {
            sInstance = new FrameSkipMonitor();
        }
        return sInstance;
    }

    public void setActivityName(String activityName) {
        mActivityName = activityName;
    }

    public void start() {
        Choreographer.getInstance().postFrameCallback(FrameSkipMonitor.getInstance());
    }

    /**
     * @param frameTimeNanos 纳秒 1ms = 1000000 * 1ns
     * 这里是在主线程执行的
     */
    @Override
    public void doFrame(long frameTimeNanos) {
        if (mLastFrameNanoTime != 0) {
            long frameInterval = frameTimeNanos - mLastFrameNanoTime;
            if (frameInterval > MIN_FRAME_TIME) {
                // 间隔大于 3 帧即视为卡顿
                Long time = mSkipRecordMap.get(mActivityName);
                mSkipRecordMap.put(mActivityName, time == null ? 0 : time + frameInterval);
            }
        }
        mLastFrameNanoTime = frameTimeNanos;
        Choreographer.getInstance().postFrameCallback(this);
        // Runtime.getRuntime().maxMemory();
    }

    public void report() {
        Choreographer.getInstance().removeFrameCallback(this);
        Iterator<Map.Entry<String, Long>> iterator = mSkipRecordMap.entrySet().iterator();
        StringBuilder result = new StringBuilder();
        long skipFrames = 0;
        Long activityShowTime = null;
        while (iterator.hasNext()) {
            Map.Entry<String, Long> entry = iterator.next();
            skipFrames = ((long) entry.getValue() / ONE_FRAME_TIME);
            activityShowTime = mActivityShowTimeMap.get(entry.getKey());

            result.append((String) entry.getKey())
                    .append(" showTime: ")
                    .append(activityShowTime);

            if (activityShowTime != null && activityShowTime > 0) {
                result.append("ms and skipFramesPerSecond: ")
                        // 每秒丢 5 帧可视为卡顿
                        .append(skipFrames * 1000 / activityShowTime).append(";\n");
            } else {
                result.append("ms and skipFramesCount: ")
                        .append(skipFrames);
            }
        }
        mSkipRecordMap.clear();
        Log.d(TAG, result.toString());
    }

    public void OnActivityResume() {
        mActivityStartTime = System.currentTimeMillis();
    }

    public void OnActivityPause() {
        mActivityShowTimeMap.put(mActivityName, System.currentTimeMillis() - mActivityStartTime);
    }
}
