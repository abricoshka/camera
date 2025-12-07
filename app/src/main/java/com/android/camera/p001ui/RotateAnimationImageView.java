package com.android.camera.p001ui;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import com.android.camera.Log;

/* loaded from: classes.dex */
public class RotateAnimationImageView extends RotateImageView {
    public RotateAnimationImageView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @Override // android.widget.ImageView
    public void setImageResource(int i) {
        setAnimationRunning(false);
        super.setImageResource(i);
        setAnimationRunning(true);
    }

    private void setAnimationRunning(boolean z) {
        Log.m5d("RotateAniImageView", "setAnimationRunning(" + z + ")");
        AnimationDrawable animationDrawable = getDrawable() instanceof AnimationDrawable ? (AnimationDrawable) getDrawable() : null;
        if (animationDrawable != null) {
            if (z && (!animationDrawable.isRunning())) {
                animationDrawable.start();
            }
            if (!z && animationDrawable.isRunning()) {
                animationDrawable.stop();
            }
        }
    }
}
