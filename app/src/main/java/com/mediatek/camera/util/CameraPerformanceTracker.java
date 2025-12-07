package com.mediatek.camera.util;

import android.os.Environment;
import android.os.Trace;
import java.io.File;
import java.util.HashMap;

/* loaded from: classes.dex */
public class CameraPerformanceTracker {
    private static final boolean DEBUG = new File(Environment.getExternalStorageDirectory().toString() + "/cameraPerformance.txt").exists();
    private static HashMap<String, String> mTimeTracker = new HashMap<>();
    private static CameraPerformanceTracker sInstance;

    private CameraPerformanceTracker() {
    }

    public static void onEvent(String str, String str2, boolean z) {
        if (!DEBUG) {
            return;
        }
        if (sInstance == null) {
            sInstance = new CameraPerformanceTracker();
        }
        long jCurrentTimeMillis = System.currentTimeMillis();
        if (z) {
            android.util.Log.i(str, str2 + "->begin");
            Trace.traceBegin(8L, str2);
            mTimeTracker.put(str2, String.valueOf(jCurrentTimeMillis));
        } else {
            android.util.Log.i(str, str2 + "->end");
            Trace.traceEnd(8L);
            if (mTimeTracker.containsKey(str2)) {
                android.util.Log.i("CameraPerformanceTracker", str2 + " duration = " + (jCurrentTimeMillis - Long.parseLong(mTimeTracker.get(str2))) + "ms");
                mTimeTracker.remove(str2);
            }
        }
    }
}
