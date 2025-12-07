package com.android.camera;

import android.os.SystemProperties;

/* loaded from: classes.dex */
public final class Log {
    private static final boolean LOG_ENABLE = SystemProperties.get("ro.build.type").equals("eng");
    private static int sLogLevelFromProperties = SystemProperties.getInt("debug.mtkcam.loglevel", 2);
    private static int sLogPersistLevelFromProperties = SystemProperties.getInt("persist.mtkcamapp.loglevel", 2);

    private Log() {
    }

    /* renamed from: v */
    public static void m10v(String str, String str2) {
        if (LOG_ENABLE || isLoggable(4)) {
            android.util.Log.v("CamAp_" + str, str2);
        }
    }

    /* renamed from: d */
    public static void m5d(String str, String str2) {
        if (LOG_ENABLE || isLoggable(3)) {
            android.util.Log.d("CamAp_" + str, str2);
        }
    }

    /* renamed from: i */
    public static void m8i(String str, String str2) {
        if (LOG_ENABLE || isLoggable(2)) {
            android.util.Log.i("CamAp_" + str, str2);
        }
    }

    /* renamed from: i */
    public static void m9i(String str, String str2, Throwable th) {
        if (LOG_ENABLE || isLoggable(2)) {
            android.util.Log.i("CamAp_" + str, str2, th);
        }
    }

    /* renamed from: w */
    public static void m11w(String str, String str2) {
        if (LOG_ENABLE || isLoggable(1)) {
            android.util.Log.w("CamAp_" + str, str2);
        }
    }

    /* renamed from: w */
    public static void m12w(String str, String str2, Throwable th) {
        if (LOG_ENABLE || isLoggable(1)) {
            android.util.Log.w("CamAp_" + str, str2, th);
        }
    }

    /* renamed from: e */
    public static void m6e(String str, String str2) {
        if (LOG_ENABLE || isLoggable(0)) {
            android.util.Log.e("CamAp_" + str, str2);
        }
    }

    /* renamed from: e */
    public static void m7e(String str, String str2, Throwable th) {
        if (LOG_ENABLE || isLoggable(0)) {
            android.util.Log.e("CamAp_" + str, str2, th);
        }
    }

    private static boolean isLoggable(int i) {
        return i <= sLogLevelFromProperties || i <= sLogPersistLevelFromProperties;
    }
}
