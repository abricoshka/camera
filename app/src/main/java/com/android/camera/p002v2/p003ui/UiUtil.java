package com.android.camera.p002v2.p003ui;

import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;

/* loaded from: classes.dex */
public class UiUtil {
    private UiUtil() {
    }

    public static void setViewEnabledState(View view, boolean z) {
        if (view != null) {
            view.setAlpha(z ? 1.0f : 0.3f);
        }
    }

    public static void fadeIn(View view, float f, float f2, long j) {
        if (view.getVisibility() == 0) {
            return;
        }
        view.setVisibility(0);
        AlphaAnimation alphaAnimation = new AlphaAnimation(f, f2);
        alphaAnimation.setDuration(j);
        view.startAnimation(alphaAnimation);
    }

    public static void fadeIn(View view) {
        fadeIn(view, 0.0f, 1.0f, 400L);
        view.setEnabled(true);
    }

    public static void fadeOut(View view) {
        if (view.getVisibility() != 0) {
            return;
        }
        view.setEnabled(false);
        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
        alphaAnimation.setDuration(400L);
        view.startAnimation(alphaAnimation);
        view.setVisibility(8);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public static void setOrientation(View view, int i, boolean z) {
        if (view == 0) {
            return;
        }
        if (view instanceof Rotatable) {
            ((Rotatable) view).setOrientation(i, z);
            return;
        }
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            int childCount = viewGroup.getChildCount();
            for (int i2 = 0; i2 < childCount; i2++) {
                setOrientation(viewGroup.getChildAt(i2), i, z);
            }
        }
    }
}
