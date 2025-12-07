package com.android.camera.p001ui;

import android.content.Context;
import android.graphics.Region;
import android.util.AttributeSet;
import android.widget.TextView;
import com.android.camera.CameraActivity;
import com.android.camera.Log;
import com.mediatek.camera.R;
import java.text.DecimalFormat;

/* loaded from: classes.dex */
public class FocusIndicatorRotateLayout extends RotateLayout implements FocusIndicator {
    private CameraActivity mContext;
    private Runnable mDisappear;
    private TextView mDistanceMeasurementChild;
    private Runnable mEndAction;
    private String mInfo;
    private boolean mIsNeedShow;
    private int mState;

    public FocusIndicatorRotateLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mDisappear = new Disappear(this, null);
        this.mEndAction = new EndAction(this, 0 == true ? 1 : 0);
        this.mContext = (CameraActivity) context;
    }

    public void setDistanceInfo(String str) {
        this.mInfo = str;
    }

    @Override // com.android.camera.p001ui.FocusIndicator
    public void needDistanceInfoShow(boolean z) {
        this.mIsNeedShow = z;
    }

    @Override // com.android.camera.p001ui.RotateLayout, android.view.View
    protected void onFinishInflate() {
        this.mDistanceMeasurementChild = (TextView) findViewById(R.id.distance_info);
        super.onFinishInflate();
    }

    private void setDrawable(int i) {
        this.mChild.setBackgroundDrawable(getResources().getDrawable(i));
        if (i == R.drawable.ic_focus_focused && this.mIsNeedShow) {
            this.mDistanceMeasurementChild.setText(formatInfo());
            this.mInfo = null;
            this.mIsNeedShow = false;
        }
    }

    private String formatInfo() {
        DecimalFormat decimalFormat = new DecimalFormat("0.0");
        if (this.mInfo != null) {
            if (Integer.valueOf(this.mInfo).intValue() > 9999) {
                return null;
            }
            if (Integer.valueOf(this.mInfo).intValue() < 100) {
                this.mInfo += "CM";
            } else {
                this.mInfo = decimalFormat.format(Integer.valueOf(this.mInfo).intValue() / 100.0f) + "M";
            }
        }
        return this.mInfo;
    }

    @Override // com.android.camera.p001ui.FocusIndicator
    public void showStart() {
        if (this.mState == 0) {
            setDrawable(R.drawable.ic_focus_focusing);
            animate().withLayer().setDuration(1000L).scaleX(1.5f).scaleY(1.5f);
            this.mState = 1;
        }
    }

    @Override // com.android.camera.p001ui.FocusIndicator
    public void showSuccess(boolean z) {
        if (this.mState == 1) {
            setDrawable(R.drawable.ic_focus_focused);
            animate().withLayer().setDuration(200L).scaleX(1.0f).scaleY(1.0f).withEndAction(z ? this.mEndAction : null);
            this.mState = 2;
        }
    }

    @Override // com.android.camera.p001ui.FocusIndicator
    public void showFail(boolean z) {
        Log.m5d("FocusIndicatorRotateLayout", "showFail mState = " + this.mState);
        if (this.mState == 1) {
            setDrawable(R.drawable.ic_focus_focused);
            animate().withLayer().setDuration(200L).scaleX(1.0f).scaleY(1.0f).withEndAction(z ? this.mEndAction : null);
            this.mState = 2;
        }
    }

    @Override // com.android.camera.p001ui.FocusIndicator
    public void clear() {
        animate().cancel();
        removeCallbacks(this.mDisappear);
        this.mDisappear.run();
        setScaleX(1.0f);
        setScaleY(1.0f);
        setRotation(0.0f);
    }

    private class EndAction implements Runnable {
        /* synthetic */ EndAction(FocusIndicatorRotateLayout focusIndicatorRotateLayout, EndAction endAction) {
            this();
        }

        private EndAction() {
        }

        @Override // java.lang.Runnable
        public void run() {
            FocusIndicatorRotateLayout.this.postDelayed(FocusIndicatorRotateLayout.this.mDisappear, 200L);
        }
    }

    private class Disappear implements Runnable {
        /* synthetic */ Disappear(FocusIndicatorRotateLayout focusIndicatorRotateLayout, Disappear disappear) {
            this();
        }

        private Disappear() {
        }

        @Override // java.lang.Runnable
        public void run() {
            Log.m5d("FocusIndicatorRotateLayout", "Disappear run mState = " + FocusIndicatorRotateLayout.this.mState);
            FocusIndicatorRotateLayout.this.mChild.setBackgroundDrawable(null);
            FocusIndicatorRotateLayout.this.mDistanceMeasurementChild.setText("");
            FocusIndicatorRotateLayout.this.mState = 0;
        }
    }

    public boolean isFocusing() {
        return this.mState != 0;
    }

    @Override // android.view.ViewGroup, android.view.View
    public boolean gatherTransparentRegion(Region region) {
        if (region != null) {
            int[] iArr = new int[2];
            int width = getWidth();
            int height = getHeight();
            getLocationInWindow(iArr);
            int i = (iArr[0] + (width / 2)) - width;
            int i2 = (iArr[1] + (height / 2)) - height;
            region.op(i, i2, i + (width * 2), i2 + (height * 2), Region.Op.DIFFERENCE);
        }
        return true;
    }
}
