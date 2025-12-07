package com.mediatek.camera.p005v2.control.focus;

import android.content.Context;
import android.graphics.Region;
import android.util.AttributeSet;
import com.mediatek.camera.R;
import com.mediatek.camera.debug.LogHelper;
import com.mediatek.camera.p005v2.p006ui.UIRotateLayout;

/* loaded from: classes.dex */
public class AutoFocusRotateLayout extends UIRotateLayout {
    private static final LogHelper.Tag TAG = new LogHelper.Tag(AutoFocusRotateLayout.class.getSimpleName());
    private Runnable mDisappear;
    private Runnable mEndAction;
    private int mState;

    public AutoFocusRotateLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mDisappear = new Disappear(this, null);
        this.mEndAction = new EndAction(this, 0 == true ? 1 : 0);
    }

    public void onFocusStarted() {
        if (this.mState == 0) {
            setDrawable(R.drawable.ic_focus_focusing);
            animate().withLayer().setDuration(1000L).scaleX(1.5f).scaleY(1.5f);
            this.mState = 1;
        }
    }

    public void onFocusSucceeded() {
        if (this.mState == 1) {
            setDrawable(R.drawable.ic_focus_focused);
            animate().withLayer().setDuration(200L).scaleX(1.0f).scaleY(1.0f).withEndAction(this.mEndAction);
            this.mState = 2;
        }
    }

    public void onFocusFailed() {
        if (this.mState == 1) {
            setDrawable(R.drawable.ic_focus_focused);
            animate().withLayer().setDuration(200L).scaleX(1.0f).scaleY(1.0f).withEndAction(this.mEndAction);
            this.mState = 2;
        }
    }

    public void setPassiveFocusSuccess(boolean z) {
        if (this.mState == 1) {
            setDrawable(R.drawable.ic_focus_focused);
            animate().withLayer().setDuration(200L).scaleX(1.0f).scaleY(1.0f).withEndAction(this.mEndAction);
            this.mState = 2;
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    public boolean gatherTransparentRegion(Region region) {
        if (region != null) {
            int[] iArr = new int[2];
            int width = getWidth();
            int height = getHeight();
            getLocationInWindow(iArr);
            LogHelper.m26i(TAG, "location[0]:" + iArr[0] + " location[1]:" + iArr[1]);
            int i = (iArr[0] + (width / 2)) - width;
            int i2 = (iArr[1] + (height / 2)) - height;
            int i3 = i + (width * 2);
            int i4 = i2 + (height * 2);
            LogHelper.m26i(TAG, "gatherTransparentRegion l:" + i + " t:" + i2 + " r:" + i3 + " b:" + i4);
            region.op(i, i2, i3, i4, Region.Op.DIFFERENCE);
        }
        return true;
    }

    public void clear() {
        LogHelper.m23d(TAG, "clear mState = " + this.mState);
        animate().cancel();
        removeCallbacks(this.mDisappear);
        this.mDisappear.run();
        setScaleX(1.0f);
        setScaleY(1.0f);
    }

    private void setDrawable(int i) {
        this.mChild.setBackgroundDrawable(getResources().getDrawable(i));
    }

    private class EndAction implements Runnable {
        /* synthetic */ EndAction(AutoFocusRotateLayout autoFocusRotateLayout, EndAction endAction) {
            this();
        }

        private EndAction() {
        }

        @Override // java.lang.Runnable
        public void run() {
            AutoFocusRotateLayout.this.postDelayed(AutoFocusRotateLayout.this.mDisappear, 200L);
        }
    }

    private class Disappear implements Runnable {
        /* synthetic */ Disappear(AutoFocusRotateLayout autoFocusRotateLayout, Disappear disappear) {
            this();
        }

        private Disappear() {
        }

        @Override // java.lang.Runnable
        public void run() {
            LogHelper.m26i(AutoFocusRotateLayout.TAG, "Disappear run mState = " + AutoFocusRotateLayout.this.mState);
            AutoFocusRotateLayout.this.mChild.setBackgroundDrawable(null);
            AutoFocusRotateLayout.this.mState = 0;
        }
    }
}
