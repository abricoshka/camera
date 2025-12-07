package com.android.camera.p002v2.bridge;

import com.mediatek.camera.p005v2.platform.ModeChangeListener;
import java.util.HashMap;
import java.util.Map;

/* loaded from: classes.dex */
public class ModeChangeAdapter {
    private static Map<String, Integer> sModeMapping = new HashMap();
    private final ModeChangeListener mModeChangeListener;

    static {
        sModeMapping.put("normal_key", 0);
        sModeMapping.put("pref_hdr_key", 1);
        sModeMapping.put("pref_photo_pip_key", 3);
    }

    public ModeChangeAdapter(ModeChangeListener modeChangeListener) {
        this.mModeChangeListener = modeChangeListener;
    }

    public void onModeChanged(String str) {
        this.mModeChangeListener.onModeSelected(sModeMapping.get(str).intValue());
    }

    public static int getModeIndexFromKey(String str) {
        return sModeMapping.get(str).intValue();
    }

    public static boolean isNeedSwitchModule(String str, String str2) {
        if ("pref_photo_pip_key".equals(str) || "pref_photo_pip_key".equals(str2)) {
            return true;
        }
        return false;
    }

    public static int getModuleIndex(String str) {
        if (!"pref_photo_pip_key".equals(str)) {
            return 0;
        }
        return 1;
    }
}
