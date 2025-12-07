package com.mediatek.camera.setting.preference;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.AttributeSet;

/* loaded from: classes.dex */
public class RecordLocationPreference extends IconListPreference {
    private final ContentResolver mResolver;

    public RecordLocationPreference(Context context, AttributeSet attributeSet, SharedPreferencesTransfer sharedPreferencesTransfer) {
        super(context, attributeSet, sharedPreferencesTransfer);
        this.mResolver = context.getContentResolver();
    }

    @Override // com.mediatek.camera.setting.preference.ListPreference
    public String getValue() {
        return get(getSharedPreferences("pref_camera_recordlocation_key"), this.mResolver) ? "on" : "off";
    }

    public static boolean get(SharedPreferences sharedPreferences, ContentResolver contentResolver) {
        return "on".equals(sharedPreferences.getString("pref_camera_recordlocation_key", "none"));
    }
}
