package com.mediatek.camera.setting.preference;

import android.content.Context;
import android.util.AttributeSet;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class PreferenceGroup extends CameraPreference {
    private ArrayList<CameraPreference> list;

    public PreferenceGroup(Context context, AttributeSet attributeSet, SharedPreferencesTransfer sharedPreferencesTransfer) {
        super(context, attributeSet, sharedPreferencesTransfer);
        this.list = new ArrayList<>();
    }

    public void addChild(CameraPreference cameraPreference) {
        this.list.add(cameraPreference);
    }

    public CameraPreference get(int i) {
        return this.list.get(i);
    }

    public int size() {
        return this.list.size();
    }

    public ListPreference findPreference(String str) {
        ListPreference listPreferenceFindPreference;
        for (CameraPreference cameraPreference : this.list) {
            if (cameraPreference instanceof ListPreference) {
                ListPreference listPreference = (ListPreference) cameraPreference;
                if (listPreference.getKey().equals(str)) {
                    return listPreference;
                }
            } else if ((cameraPreference instanceof PreferenceGroup) && (listPreferenceFindPreference = ((PreferenceGroup) cameraPreference).findPreference(str)) != null) {
                return listPreferenceFindPreference;
            }
        }
        return null;
    }
}
