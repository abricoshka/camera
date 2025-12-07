package com.android.camera.p001ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import com.android.camera.Log;
import com.mediatek.camera.R;
import com.mediatek.camera.setting.SettingUtils;

/* loaded from: classes.dex */
public class PreviewFrameLayout extends RelativeLayout {
    private double mAspectRatio;
    private View mBorder;
    private OnSizeChangedListener mListener;

    public interface OnSizeChangedListener {
        void onSizeChanged(int i, int i2);
    }

    public PreviewFrameLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mBorder = findViewById(R.id.preview_border);
    }

    public void setAspectRatio(double d) {
        Log.m5d("PreviewFrameLayout", "setAspectRatio(" + d + ") mAspectRatio=" + this.mAspectRatio + ", " + this);
        if (d <= 0.0d) {
            throw new IllegalArgumentException();
        }
        if (this.mAspectRatio != d) {
            this.mAspectRatio = d;
            requestLayout();
        }
    }

    public void showBorder(boolean z) {
        this.mBorder.setVisibility(z ? 0 : 4);
    }

    @Override // android.widget.RelativeLayout, android.view.View
    protected void onMeasure(int i, int i2) {
        int iRound;
        int size = View.MeasureSpec.getSize(i);
        int size2 = View.MeasureSpec.getSize(i2);
        int paddingLeft = getPaddingLeft() + getPaddingRight();
        int paddingTop = getPaddingTop() + getPaddingBottom();
        int i3 = size - paddingLeft;
        int iRound2 = size2 - paddingTop;
        boolean z = i3 > iRound2;
        int i4 = z ? i3 : iRound2;
        if (!z) {
            iRound2 = i3;
        }
        if (Math.abs(this.mAspectRatio - SettingUtils.getFullScreenRatio()) <= 0.03d) {
            iRound = i4;
        } else if (i4 > iRound2 * this.mAspectRatio) {
            iRound = Math.round(((float) (iRound2 * this.mAspectRatio)) / 2.0f) * 2;
        } else {
            iRound2 = Math.round(((float) (i4 / this.mAspectRatio)) / 2.0f) * 2;
            iRound = i4;
        }
        if (!z) {
            int i5 = iRound;
            iRound = iRound2;
            iRound2 = i5;
        }
        int i6 = iRound + paddingLeft;
        int i7 = iRound2 + paddingTop;
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(i6, 1073741824), View.MeasureSpec.makeMeasureSpec(i7, 1073741824));
        Log.m5d("PreviewFrameLayout", "onMeasure() width = " + i6 + " height = " + i7 + ", " + this);
    }

    public void setOnSizeChangedListener(OnSizeChangedListener onSizeChangedListener) {
        this.mListener = onSizeChangedListener;
    }

    @Override // android.view.View
    protected void onSizeChanged(int i, int i2, int i3, int i4) {
        Log.m5d("PreviewFrameLayout", "onSizeChanged(" + i + ", " + i2 + ", " + i3 + ", " + i4 + ") " + this);
        if (this.mListener != null) {
            this.mListener.onSizeChanged(i, i2);
        }
    }
}
