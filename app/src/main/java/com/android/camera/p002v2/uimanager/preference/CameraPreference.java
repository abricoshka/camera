package com.android.camera.p002v2.uimanager.preference;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import com.android.camera.R$styleable;

/* loaded from: classes.dex */
public abstract class CameraPreference {
    private final String mTitle;

    public CameraPreference(Context context, AttributeSet attributeSet) {
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R$styleable.CameraPreference, 0, 0);
        this.mTitle = typedArrayObtainStyledAttributes.getString(0);
        typedArrayObtainStyledAttributes.recycle();
    }

    public String getTitle() {
        return this.mTitle;
    }
}
