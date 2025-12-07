package com.android.camera.p001ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;
import com.android.camera.Log;
import com.android.camera.Util;

/* loaded from: classes.dex */
public class ModePickerScrollView extends ScrollView implements ModePickerScrollable {
    private View mBackground;
    private Runnable mHideRunnable;

    public ModePickerScrollView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mHideRunnable = new Runnable() { // from class: com.android.camera.ui.ModePickerScrollView.1
            @Override // java.lang.Runnable
            public void run() {
                Log.m5d("ModePickerScrollView", "mHideRunnable.run()");
                if (ModePickerScrollView.this.mBackground != null) {
                    Util.fadeOut(ModePickerScrollView.this.mBackground);
                }
            }
        };
    }

    @Override // android.widget.ScrollView, android.view.ViewGroup
    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        Log.m5d("ModePickerScrollView", "onInterceptTouchEvent(" + motionEvent + ")");
        showBackground();
        return super.onInterceptTouchEvent(motionEvent);
    }

    @Override // android.widget.ScrollView, android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        Log.m5d("ModePickerScrollView", "onTouchEvent(" + motionEvent + ")");
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
        Log.m5d("ModePickerScrollView", "setEnabled(" + z + ")");
    }

    @Override // com.android.camera.p001ui.ModePickerScrollable
    public void setBackgroundView(View view) {
        Log.m5d("ModePickerScrollView", "setBackgroundView(" + view + ")");
        this.mBackground = view;
    }
}
