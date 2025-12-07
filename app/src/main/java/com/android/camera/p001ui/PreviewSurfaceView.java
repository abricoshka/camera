package com.android.camera.p001ui;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.android.camera.FeatureSwitcher;
import com.android.camera.Util;
import com.mediatek.camera.setting.SettingUtils;

/* loaded from: classes.dex */
public class PreviewSurfaceView extends SurfaceView {
    private double mAspectRatio;
    private Context mContext;
    private boolean mIsNeedLockSizeChange;
    private int mPreviewHeight;
    private int mPreviewWidth;

    public PreviewSurfaceView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mAspectRatio = 0.0d;
        this.mPreviewWidth = 0;
        this.mPreviewHeight = 0;
        this.mIsNeedLockSizeChange = false;
        this.mContext = context;
    }

    @Override // android.view.SurfaceView, android.view.View
    protected void onMeasure(int i, int i2) {
        int iRound;
        int size = View.MeasureSpec.getSize(i);
        int size2 = View.MeasureSpec.getSize(i2);
        boolean z = size > size2;
        int i3 = z ? size : size2;
        int iRound2 = z ? size2 : size;
        Log.v("xiaoyao", "ssssssssssssss= previewWidth=" + size + "\n previewHeight " + size2 + "\n longSide " + i3 + "\n shortSide " + iRound2 + "\n mAspectRatio " + this.mAspectRatio);
        if (this.mAspectRatio > 0.0d) {
            double dFindFullscreenRatio = SettingUtils.findFullscreenRatio(this.mContext);
            boolean z2 = FeatureSwitcher.isTablet() && FeatureSwitcher.isMultiWindow() && size < SettingUtils.getFullScreenWidth() && size2 < SettingUtils.getFullScreenHeight();
            Log.v("xiaoyao", "ssssssssssssss= vvvvvvvv=" + size + "\n mAspectRatio " + this.mAspectRatio + "\n fullScreenRatio " + dFindFullscreenRatio + "\n isMultiWin " + z2 + "\n previewWidth " + size + "\n previewHeight " + size2 + "\n getFullScreenWidth " + SettingUtils.getFullScreenWidth() + "\n getFullScreenHeight " + SettingUtils.getFullScreenHeight() + "\n Math.abs((mAspectRatio - fullScreenRatio)) " + Math.abs(this.mAspectRatio - dFindFullscreenRatio) + "\n ASPECT_TOLERANCE 0.03");
            com.android.camera.Log.m5d("PreviewSurfaceView", "4:3 or multiwindow case");
            if (i3 > iRound2 * this.mAspectRatio) {
                iRound = Math.round(((float) (iRound2 * this.mAspectRatio)) / 2.0f) * 2;
            } else {
                iRound2 = Math.round(((float) (i3 / this.mAspectRatio)) / 2.0f) * 2;
                iRound = i3;
            }
        } else {
            iRound = i3;
        }
        if (!z) {
            int i4 = iRound;
            iRound = iRound2;
            iRound2 = i4;
        }
        if (this.mIsNeedLockSizeChange) {
            iRound = 2;
            iRound2 = 2;
        }
        if (!this.mIsNeedLockSizeChange) {
            this.mPreviewWidth = iRound;
            this.mPreviewHeight = iRound2;
        }
        if (Util.isWfdEnabled(getContext()) || FeatureSwitcher.isTablet()) {
            if ((this.mPreviewWidth > this.mPreviewHeight) != (getContext().getResources().getConfiguration().orientation == 2)) {
                int i5 = iRound;
                iRound = iRound2;
                iRound2 = i5;
            }
        }
        setMeasuredDimension(iRound, iRound2);
        com.android.camera.Log.m5d("PreviewSurfaceView", "After onMeasure  aspectRatio = " + this.mAspectRatio + " previewWidth = " + iRound + " previewHeight = " + iRound2);
    }

    public boolean setAspectRatio(double d) {
        com.android.camera.Log.m5d("PreviewSurfaceView", "setAspectRatio aspectRatio = " + d);
        if (this.mAspectRatio != d) {
            this.mAspectRatio = d;
            onDrawBgSize(this.mAspectRatio);
            requestLayout();
            return true;
        }
        return false;
    }

    @Override // android.view.View
    public void setLayoutParams(ViewGroup.LayoutParams layoutParams) {
        super.setLayoutParams(layoutParams);
        Log.v("xiaoyao", "ssssssssssssss==setLayoutParams bbbb" + Log.getStackTraceString(new Throwable()));
    }

    public void shrink() {
        if (this.mIsNeedLockSizeChange) {
            return;
        }
        this.mIsNeedLockSizeChange = true;
        setLayoutSize(2, 2);
    }

    public void expand() {
        com.android.camera.Log.m5d("PreviewSurfaceView", "expand preview (" + this.mPreviewWidth + " , " + this.mPreviewHeight + ")");
        if (!this.mIsNeedLockSizeChange) {
            return;
        }
        if ((this.mPreviewWidth > this.mPreviewHeight) != (((double) getContext().getResources().getConfiguration().orientation) == 2.0d)) {
            int i = this.mPreviewWidth;
            this.mPreviewWidth = this.mPreviewHeight;
            this.mPreviewHeight = i;
        }
        this.mIsNeedLockSizeChange = false;
        setLayoutSize(this.mPreviewWidth, this.mPreviewHeight);
        Log.v("xiaoyao", "ssssssssssssss= mPreviewWidth=" + this.mPreviewWidth + "\n mPreviewHeight " + this.mPreviewHeight);
    }

    private void setLayoutSize(int i, int i2) {
        Log.v("xiaoyao", "ssssssssssssss= width=" + i + "\n height " + i2);
        Log.d("PreviewSurfaceView", "setLayoutSize mPreviewWidth = " + this.mPreviewWidth + " width = " + i + " mPreviewHeight = " + this.mPreviewHeight + " height = " + i2);
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

    private void onDrawBgSize(double d) {
    }
}
