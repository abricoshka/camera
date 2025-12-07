package com.mediatek.camera;

import android.content.SharedPreferences;
import com.mediatek.camera.setting.SettingItem;
import com.mediatek.camera.setting.preference.ListPreference;
import com.mediatek.camera.setting.preference.PreferenceGroup;

/* loaded from: classes.dex */
public interface ISettingCtrl {
    void addRule(String str, String str2, ISettingRule iSettingRule);

    void executeRule(String str, String str2);

    String getCameraMode(String str);

    String getDefaultValue(String str);

    ListPreference getListPreference(String str);

    PreferenceGroup getPreferenceGroup();

    SettingItem getSetting(String str);

    SettingItem getSetting(String str, int i);

    String getSettingValue(String str);

    void initializeSettings(int i, SharedPreferences sharedPreferences, SharedPreferences sharedPreferences2);

    boolean isSettingsInitialized();

    void onSettingChanged(String str, String str2);

    void resetSetting();

    void restoreSetting(int i);

    void setSettingValue(String str, String str2, int i);

    void updateSetting(SharedPreferences sharedPreferences);
}
