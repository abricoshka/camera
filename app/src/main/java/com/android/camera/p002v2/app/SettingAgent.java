package com.android.camera.p002v2.app;

import android.os.Handler;
import java.util.List;
import java.util.Map;

/* loaded from: classes.dex */
public interface SettingAgent {

    public interface SettingChangedListener {
        void onSettingResult(Map<String, String> map, Map<String, String> map2);
    }

    void clearSharedPreferencesValue(String[] strArr, String str);

    void configurateSetting(Map<String, String> map);

    void doSettingChange(String str, String str2);

    void doSettingChange(Map<String, String> map);

    String getSharedPreferencesValue(String str, String str2);

    List<String> getSupportedValues(String str, String str2);

    void registerSettingChangedListener(SettingChangedListener settingChangedListener, Handler handler);
}
