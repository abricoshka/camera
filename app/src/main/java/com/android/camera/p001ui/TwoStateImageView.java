package com.android.camera.p001ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/* loaded from: classes.dex */
public class TwoStateImageView extends ImageView {
    private boolean mFilterEnabled;

    public TwoStateImageView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mFilterEnabled = false;
    }

    public TwoStateImageView(Context context) {
        this(context, null);
    }

    @Override // android.view.View
    public void setEnabled(boolean z) {
        super.setEnabled(z);
        if (this.mFilterEnabled) {
            if (z) {
                setAlpha(1.0f);
            } else {
                setAlpha(0.4f);
            }
        }
    }
}
