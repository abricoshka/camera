package com.android.camera.p001ui;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import com.android.camera.CameraActivity;

/* loaded from: classes.dex */
public abstract class ZoomControl extends RelativeLayout {
    private Handler mHandler;
    private OnZoomChangedListener mListener;
    protected int mOrientation;
    protected final Runnable mRunnable;
    private boolean mSmoothZoomSupported;
    private int mState;
    private int mStep;
    protected int mZoomIndex;
    protected int mZoomMax;
    protected CameraActivity mcontext;

    public interface OnZoomChangedListener {
        void onZoomStateChanged(int i);

        void onZoomValueChanged(int i);
    }

    public ZoomControl(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mRunnable = new Runnable() { // from class: com.android.camera.ui.ZoomControl.1
            @Override // java.lang.Runnable
            public void run() {
                ZoomControl.this.performZoom(ZoomControl.this.mState, false);
            }
        };
        this.mHandler = new Handler();
    }

    public void closeZoomControl() {
        stopZooming();
        if (!this.mSmoothZoomSupported) {
            this.mHandler.removeCallbacks(this.mRunnable);
        }
    }

    public void setZoomMax(int i) {
        this.mZoomMax = i;
        requestLayout();
    }

    public void setZoomIndex(int i) {
        if (i >= 0 && i <= this.mZoomMax) {
            this.mZoomIndex = i;
        }
        invalidate();
    }

    private boolean zoomIn() {
        if (this.mZoomIndex == this.mZoomMax) {
            return false;
        }
        return changeZoomIndex(this.mZoomIndex + this.mStep);
    }

    private boolean zoomOut() {
        if (this.mZoomIndex == 0) {
            return false;
        }
        return changeZoomIndex(this.mZoomIndex - this.mStep);
    }

    private void stopZooming() {
        if (!this.mSmoothZoomSupported || this.mListener == null) {
            return;
        }
        this.mListener.onZoomStateChanged(2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void performZoom(int i, boolean z) {
        if (this.mState == i && z) {
            return;
        }
        if (z) {
            this.mHandler.removeCallbacks(this.mRunnable);
        }
        this.mState = i;
        switch (i) {
            case 0:
                zoomIn();
                break;
            case 1:
                zoomOut();
                break;
            case 2:
                stopZooming();
                break;
        }
        if (!this.mSmoothZoomSupported) {
            this.mHandler.postDelayed(this.mRunnable, 1000 / this.mZoomMax);
        }
    }

    private boolean changeZoomIndex(int i) {
        int i2;
        if (this.mListener != null) {
            if (this.mSmoothZoomSupported) {
                i2 = i < this.mZoomIndex ? 1 : 0;
                if ((i2 == 0 && this.mZoomIndex != this.mZoomMax) || (i2 == 1 && this.mZoomIndex != 0)) {
                    this.mListener.onZoomStateChanged(i2);
                }
            } else {
                int i3 = i > this.mZoomMax ? this.mZoomMax : i;
                i2 = i3 >= 0 ? i3 : 0;
                this.mListener.onZoomValueChanged(i2);
                this.mZoomIndex = i2;
            }
        }
        return true;
    }

    @Override // android.view.View
    public void setActivated(boolean z) {
        super.setActivated(z);
    }

    public void setconntext(CameraActivity cameraActivity) {
        this.mcontext = cameraActivity;
    }
}
