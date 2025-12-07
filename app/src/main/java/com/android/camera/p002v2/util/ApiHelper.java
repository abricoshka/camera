package com.android.camera.p002v2.util;

import android.os.Build;

/* loaded from: classes.dex */
public class ApiHelper {
    public static final boolean AT_LEAST_16;
    public static final boolean HAS_ANNOUNCE_FOR_ACCESSIBILITY;
    public static final boolean HAS_APP_GALLERY;
    public static final boolean HAS_AUTO_FOCUS_MOVE_CALLBACK;
    public static final boolean HAS_CAMERA_HDR;
    public static final boolean HAS_CAMERA_HDR_PLUS;
    public static final boolean HAS_DISPLAY_LISTENER;
    public static final boolean HAS_HIDEYBARS;
    public static final boolean HAS_MEDIA_ACTION_SOUND;
    public static final boolean HAS_MEDIA_COLUMNS_WIDTH_AND_HEIGHT;
    public static final boolean HAS_ORIENTATION_LOCK;
    public static final boolean HAS_ROBOTO_MEDIUM_FONT;
    public static final boolean HAS_ROTATION_ANIMATION;
    public static final boolean HAS_SET_BEAM_PUSH_URIS;
    public static final boolean HAS_SURFACE_TEXTURE_RECORDING;
    public static final boolean IS_NEXUS_4;
    public static final boolean IS_NEXUS_5;
    public static final boolean IS_NEXUS_6;

    static {
        AT_LEAST_16 = Build.VERSION.SDK_INT >= 16;
        HAS_APP_GALLERY = Build.VERSION.SDK_INT >= 15;
        HAS_ANNOUNCE_FOR_ACCESSIBILITY = Build.VERSION.SDK_INT >= 16;
        HAS_AUTO_FOCUS_MOVE_CALLBACK = Build.VERSION.SDK_INT >= 16;
        HAS_MEDIA_ACTION_SOUND = Build.VERSION.SDK_INT >= 16;
        HAS_MEDIA_COLUMNS_WIDTH_AND_HEIGHT = Build.VERSION.SDK_INT >= 16;
        HAS_SET_BEAM_PUSH_URIS = Build.VERSION.SDK_INT >= 16;
        HAS_SURFACE_TEXTURE_RECORDING = Build.VERSION.SDK_INT >= 16;
        HAS_ROBOTO_MEDIUM_FONT = Build.VERSION.SDK_INT >= 16;
        HAS_CAMERA_HDR_PLUS = isKitKatOrHigher();
        HAS_CAMERA_HDR = Build.VERSION.SDK_INT >= 17;
        HAS_DISPLAY_LISTENER = Build.VERSION.SDK_INT >= 17;
        HAS_ORIENTATION_LOCK = Build.VERSION.SDK_INT >= 18;
        HAS_ROTATION_ANIMATION = Build.VERSION.SDK_INT >= 18;
        HAS_HIDEYBARS = isKitKatOrHigher();
        IS_NEXUS_4 = "mako".equalsIgnoreCase(Build.DEVICE);
        IS_NEXUS_5 = "LGE".equalsIgnoreCase(Build.MANUFACTURER) ? "hammerhead".equalsIgnoreCase(Build.DEVICE) : false;
        IS_NEXUS_6 = "motorola".equalsIgnoreCase(Build.MANUFACTURER) ? "shamu".equalsIgnoreCase(Build.DEVICE) : false;
    }

    public static int getIntFieldIfExists(Class<?> cls, String str, Class<?> cls2, int i) {
        try {
            return cls.getDeclaredField(str).getInt(cls2);
        } catch (Exception e) {
            return i;
        }
    }

    public static boolean isKitKatOrHigher() {
        if (Build.VERSION.SDK_INT < 19) {
            return "KeyLimePie".equals(Build.VERSION.CODENAME);
        }
        return true;
    }
}
