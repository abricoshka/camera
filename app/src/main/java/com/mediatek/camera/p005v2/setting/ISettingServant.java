package com.mediatek.camera.p005v2.setting;

import android.util.Size;
import java.util.List;
import java.util.Map;

/* loaded from: classes.dex */
public interface ISettingServant {

    public interface ISettingChangedListener {
        void onSettingChanged(Map<String, String> map);
    }

    void doSettingChange(String str, String str2, boolean z);

    String getCameraId();

    Size getPreviewSize();

    SettingItem getSettingItem(String str);

    String getSettingValue(String str);

    String getSharedPreferencesValue(String str);

    List<Size> getSupportedPreviewSizes();

    List<String> getSupportedValues(String str);

    void registerSettingChangedListener(ISettingChangedListener iSettingChangedListener, List<String> list, int i);

    void setSharedPreferencesValue(String str, String str2);

    void unRegisterSettingChangedListener(ISettingChangedListener iSettingChangedListener);
}
