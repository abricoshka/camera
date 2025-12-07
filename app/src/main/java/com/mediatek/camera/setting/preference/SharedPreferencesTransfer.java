package com.mediatek.camera.setting.preference;

import android.content.SharedPreferences;

/* loaded from: classes.dex */
public class SharedPreferencesTransfer {
    private SharedPreferences mGlobalPref;
    private SharedPreferences mLocalPref;

    public SharedPreferencesTransfer(SharedPreferences sharedPreferences, SharedPreferences sharedPreferences2) {
        this.mGlobalPref = sharedPreferences;
        this.mLocalPref = sharedPreferences2;
    }

    public void updateLocalPreferences(SharedPreferences sharedPreferences) {
        this.mLocalPref = sharedPreferences;
    }

    public SharedPreferences getSharedPreferences(String str) {
        if (isGlobal(str)) {
            return this.mGlobalPref;
        }
        return this.mLocalPref;
    }

    private static boolean isGlobal(String str) {
        if (str.equals("pref_camera_id_key") || str.equals("pref_camera_recordlocation_key")) {
            return true;
        }
        return str.equals("pref_face_beauty_multi_mode_key");
    }
}
