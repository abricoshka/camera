package com.android.camera.p002v2.p003ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import com.android.camera.p002v2.util.CameraUtil;
import com.mediatek.camera.debug.LogHelper;

/* loaded from: classes.dex */
public class FixedAspectSurfaceView extends SurfaceView {
    private static final LogHelper.Tag TAG = new LogHelper.Tag(FixedAspectSurfaceView.class.getSimpleName());
    private double mAspectRatio;
    private boolean mIsNeedLockSizeChange;
    private int mPreviewHeight;
    private int mPreviewWidth;

    public FixedAspectSurfaceView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mAspectRatio = 0.0d;
        this.mPreviewWidth = 0;
        this.mPreviewHeight = 0;
        this.mIsNeedLockSizeChange = false;
    }

    @Override // android.view.SurfaceView, android.view.View
    protected void onMeasure(int i, int i2) {
        int iRound;
        int size = View.MeasureSpec.getSize(i);
        int size2 = View.MeasureSpec.getSize(i2);
        LogHelper.m23d(TAG, "onMeasure previewWidth = " + size + " previewHeight = " + size2);
        boolean z = size > size2;
        int i3 = z ? size : size2;
        if (!z) {
            size2 = size;
        }
        if (this.mAspectRatio > 0.0d) {
            if (Math.abs(this.mAspectRatio - CameraUtil.findFullscreenRatio(getContext())) <= 0.03d) {
                LogHelper.m23d(TAG, "full screen case");
                if (i3 < size2 * this.mAspectRatio) {
                    iRound = Math.round(((float) (size2 * this.mAspectRatio)) / 2.0f) * 2;
                } else {
                    size2 = Math.round(((float) (i3 / this.mAspectRatio)) / 2.0f) * 2;
                    iRound = i3;
                }
            } else {
                LogHelper.m23d(TAG, "4:3 case");
                if (i3 > size2 * this.mAspectRatio) {
                    iRound = Math.round(((float) (size2 * this.mAspectRatio)) / 2.0f) * 2;
                } else {
                    size2 = Math.round(((float) (i3 / this.mAspectRatio)) / 2.0f) * 2;
                    iRound = i3;
                }
            }
        } else {
            iRound = i3;
        }
        if (!z) {
            int i4 = iRound;
            iRound = size2;
            size2 = i4;
        }
        if (!this.mIsNeedLockSizeChange) {
            this.mPreviewWidth = iRound;
            this.mPreviewHeight = size2;
        }
        boolean z2 = this.mPreviewWidth > this.mPreviewHeight;
        boolean z3 = getContext().getResources().getConfiguration().orientation == 2;
        if (z2 == z3) {
            int i5 = size2;
            size2 = iRound;
            iRound = i5;
        }
        LogHelper.m23d(TAG, "originalPreviewIsLandscape = " + z2 + ",configurationIsLandscape = " + z3 + ",mPreviewWidth = " + this.mPreviewWidth + ",mPreviewHeight = " + this.mPreviewHeight);
        setMeasuredDimension(size2, iRound);
        LogHelper.m23d(TAG, "After onMeasure  aspectRatio = " + this.mAspectRatio + " previewWidth = " + size2 + " previewHeight = " + iRound);
    }

    public boolean setAspectRatio(double d) {
        LogHelper.m23d(TAG, "setAspectRatio aspectRatio = " + d);
        if (this.mAspectRatio != d) {
            this.mAspectRatio = d;
            requestLayout();
            return true;
        }
        return false;
    }

    public void shrink() {
        if (this.mIsNeedLockSizeChange) {
            return;
        }
        this.mIsNeedLockSizeChange = true;
        setLayoutSize(2, 2);
    }

    public void expand() {
        LogHelper.m23d(TAG, "expand mPreviewWidth = " + this.mPreviewWidth + " mPreviewHeight = " + this.mPreviewHeight);
        if (this.mPreviewWidth <= 2 || this.mPreviewHeight <= 2 || (!this.mIsNeedLockSizeChange)) {
            return;
        }
        if ((this.mPreviewWidth > this.mPreviewHeight) != (getContext().getResources().getConfiguration().orientation == 2)) {
            int i = this.mPreviewWidth;
            this.mPreviewWidth = this.mPreviewHeight;
            this.mPreviewHeight = i;
        }
        this.mIsNeedLockSizeChange = false;
        setLayoutSize(this.mPreviewWidth, this.mPreviewHeight);
    }

    private void setLayoutSize(int i, int i2) {
        LogHelper.m23d(TAG, "setLayoutSize mPreviewWidth = " + this.mPreviewWidth + " width = " + i + " mPreviewHeight = " + this.mPreviewHeight + " height = " + i2);
        if (i <= 0 || i2 <= 0 || this.mPreviewWidth <= 0 || this.mPreviewWidth <= 0) {
            return;
        }
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) getLayoutParams();
        if (layoutParams.width == i && layoutParams.height == i2) {
            return;
        }
        layoutParams.width = i;
        layoutParams.height = i2;
        layoutParams.setMargins(this.mPreviewWidth - i, this.mPreviewHeight - i2, 0, 0);
        setLayoutParams(layoutParams);
    }
}
