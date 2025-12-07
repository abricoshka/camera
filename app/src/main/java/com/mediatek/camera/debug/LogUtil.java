package com.mediatek.camera.debug;

import android.os.SystemProperties;

/* loaded from: classes.dex */
class LogUtil {
    private static int sLogLevelFromProperties = SystemProperties.getInt("debug.mtkcam.loglevel", -1);
    private static int sLogPersistLevelFromProperties = SystemProperties.getInt("persist.mtkcamapp.loglevel", -1);

    LogUtil() {
    }

    public static int getOverrideLevelFromProperty() {
        return sLogLevelFromProperties;
    }

    public static int getPersistLevelFromProperty() {
        return sLogPersistLevelFromProperties;
    }

    public static int getLogLevelFromSystemLevel(int i) {
        switch (i) {
            case 2:
                return 4;
            case 3:
                return 3;
            case 4:
                return 2;
            case 5:
                return 1;
            case 6:
                return 0;
            default:
                return -1;
        }
    }
}
