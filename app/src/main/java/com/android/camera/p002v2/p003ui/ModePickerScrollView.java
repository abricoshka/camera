package com.android.camera.p002v2.p003ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;
import com.android.camera.Util;
import com.mediatek.camera.debug.LogHelper;

/* loaded from: classes.dex */
public class ModePickerScrollView extends ScrollView implements ModePickerScrollable {
    private static final LogHelper.Tag TAG = new LogHelper.Tag(ModePickerScrollView.class.getSimpleName());
    private View mBackground;
    private Runnable mHideRunnable;

    public ModePickerScrollView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mHideRunnable = new Runnable() { // from class: com.android.camera.v2.ui.ModePickerScrollView.1
            @Override // java.lang.Runnable
            public void run() {
                LogHelper.m23d(ModePickerScrollView.TAG, "mHideRunnable.run()");
                if (ModePickerScrollView.this.mBackground != null) {
                    Util.fadeOut(ModePickerScrollView.this.mBackground);
                }
            }
        };
    }

    @Override // android.widget.ScrollView, android.view.ViewGroup
    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        LogHelper.m23d(TAG, "onInterceptTouchEvent(" + motionEvent + ")");
        showBackground();
        return super.onInterceptTouchEvent(motionEvent);
    }

    @Override // android.widget.ScrollView, android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        LogHelper.m23d(TAG, "onTouchEvent(" + motionEvent + ")");
        showBackground();
        return super.onTouchEvent(motionEvent);
    }

    private void showBackground() {
        if (isEnabled() && this.mBackground != null) {
            Util.fadeIn(this.mBackground);
            removeCallbacks(this.mHideRunnable);
            postDelayed(this.mHideRunnable, 3000L);
        }
    }

    @Override // android.view.View
    public void setEnabled(boolean z) {
        super.setEnabled(z);
        if (!z) {
            this.mHideRunnable.run();
        }
        LogHelper.m23d(TAG, "setEnabled(" + z + ")");
    }

    @Override // com.android.camera.p002v2.p003ui.ModePickerScrollable
    public void setBackgroundView(View view) {
        LogHelper.m23d(TAG, "setBackgroundView(" + view + ")");
        this.mBackground = view;
    }
}
