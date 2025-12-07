package com.mediatek.camera.setting.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import com.android.camera.R$styleable;

/* loaded from: classes.dex */
public abstract class CameraPreference {
    protected SharedPreferencesTransfer mPrefTransfer;
    private final String mTitle;

    public CameraPreference(Context context, AttributeSet attributeSet, SharedPreferencesTransfer sharedPreferencesTransfer) {
        this.mPrefTransfer = sharedPreferencesTransfer;
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R$styleable.CameraPreference, 0, 0);
        this.mTitle = typedArrayObtainStyledAttributes.getString(0);
        typedArrayObtainStyledAttributes.recycle();
    }

    public String getTitle() {
        return this.mTitle;
    }

    public SharedPreferences getSharedPreferences(String str) {
        return this.mPrefTransfer.getSharedPreferences(str);
    }
}
