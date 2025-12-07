package com.mediatek.camera.debug;

import android.os.Build;
import android.util.Log;

/* loaded from: classes.dex */
public class LogHelper {
    private static final Tag TAG = new Tag("Log");

    public static final class Tag {
        private static final int MAX_TAG_LEN = 23 - "CamAp_".length();
        final String mValue;

        public Tag(String str) {
            int length = str.length() - MAX_TAG_LEN;
            if (length > 0) {
                LogHelper.m28w(LogHelper.TAG, "Tag " + str + " is " + length + " chars longer than limit.");
            }
            this.mValue = "CamAp_" + (length > 0 ? str.substring(0, MAX_TAG_LEN) : str);
        }

        public String toString() {
            return this.mValue;
        }
    }

    /* renamed from: d */
    public static void m23d(Tag tag, String str) {
        if (isLoggable(tag, 3)) {
            Log.d(tag.toString(), str);
        }
    }

    /* renamed from: e */
    public static void m24e(Tag tag, String str) {
        if (isLoggable(tag, 6)) {
            Log.e(tag.toString(), str);
        }
    }

    /* renamed from: e */
    public static void m25e(Tag tag, String str, Throwable th) {
        if (isLoggable(tag, 6)) {
            Log.e(tag.toString(), str, th);
        }
    }

    /* renamed from: i */
    public static void m26i(Tag tag, String str) {
        if (isLoggable(tag, 4)) {
            Log.i(tag.toString(), str);
        }
    }

    /* renamed from: v */
    public static void m27v(Tag tag, String str) {
        if (isLoggable(tag, 2)) {
            Log.v(tag.toString(), str);
        }
    }

    /* renamed from: w */
    public static void m28w(Tag tag, String str) {
        if (isLoggable(tag, 5)) {
            Log.w(tag.toString(), str);
        }
    }

    /* renamed from: w */
    public static void m29w(Tag tag, String str, Throwable th) {
        if (isLoggable(tag, 5)) {
            Log.w(tag.toString(), str, th);
        }
    }

    private static boolean isLoggable(Tag tag, int i) {
        try {
            if (LogUtil.getOverrideLevelFromProperty() > -1 || LogUtil.getPersistLevelFromProperty() > -1) {
                boolean z = LogUtil.getLogLevelFromSystemLevel(i) <= LogUtil.getOverrideLevelFromProperty();
                boolean z2 = LogUtil.getLogLevelFromSystemLevel(i) <= LogUtil.getPersistLevelFromProperty();
                if (z) {
                    return true;
                }
                return z2;
            }
            if (isDebugOsBuild()) {
                return true;
            }
            return shouldLog(tag, i);
        } catch (IllegalArgumentException e) {
            m24e(TAG, "Tag too long:" + tag);
            return false;
        }
    }

    private static boolean shouldLog(Tag tag, int i) {
        if (Log.isLoggable("CamAp_", i)) {
            return true;
        }
        return Log.isLoggable(tag.toString(), i);
    }

    private static boolean isDebugOsBuild() {
        if ("userdebug".equals(Build.TYPE)) {
            return true;
        }
        return "eng".equals(Build.TYPE);
    }
}
