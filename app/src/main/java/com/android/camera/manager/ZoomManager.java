package com.android.camera.manager;

import android.view.MotionEvent;
import android.view.View;
import com.android.camera.CameraActivity;
import com.android.camera.FeatureSwitcher;
import com.android.camera.GestureDispatcher;
import com.android.camera.Log;
import java.util.List;
import java.util.Locale;

/* loaded from: classes.dex */
public class ZoomManager extends ViewManager implements CameraActivity.Resumable, GestureDispatcher.GestureDispatcherListener {
    private static final boolean[] MATRIX_ZOOM_ENABLE = {true, true, true, true, true, true, true, true, true, true, true};
    private boolean isHDRecord;
    private CameraActivity mCameraActivity;
    private boolean mDeviceSupport;
    public boolean mGoOnChangeMode;
    private int mLastZoomRatio;
    private float mZoomIndexFactor;
    private List<Integer> mZoomRatios;

    public ZoomManager(CameraActivity cameraActivity) {
        super(cameraActivity);
        this.mLastZoomRatio = -1;
        this.mZoomIndexFactor = 1.0f;
        this.isHDRecord = false;
        this.mGoOnChangeMode = true;
        cameraActivity.addResumable(this);
        this.mCameraActivity = cameraActivity;
    }

    @Override // com.android.camera.manager.ViewManager
    protected View getView() {
        return null;
    }

    @Override // com.android.camera.CameraActivity.Resumable
    public void begin() {
    }

    @Override // com.android.camera.CameraActivity.Resumable
    public void resume() {
        this.mCameraActivity.setGestureDispatcherListener(this);
    }

    @Override // com.android.camera.CameraActivity.Resumable
    public void pause() {
        this.mCameraActivity.setGestureDispatcherListener(null);
    }

    @Override // com.android.camera.CameraActivity.Resumable
    public void finish() {
    }

    @Override // com.android.camera.GestureDispatcher.GestureDispatcherListener
    public boolean onDown(float f, float f2, int i, int i2) {
        this.mGoOnChangeMode = true;
        return false;
    }

    @Override // com.android.camera.GestureDispatcher.GestureDispatcherListener
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
        return false;
    }

    @Override // com.android.camera.GestureDispatcher.GestureDispatcherListener
    public boolean onScroll(float f, float f2, float f3, float f4) {
        int i;
        int i2;
        if (!isEnabled()) {
            return false;
        }
        switch (getOrientation()) {
            case 0:
                i = (int) f3;
                i2 = (int) f4;
                break;
            case 90:
                i = (int) f4;
                i2 = (int) f3;
                break;
            case 180:
                i = -((int) f3);
                i2 = -((int) f4);
                break;
            case 270:
                i = -((int) f4);
                i2 = -((int) f3);
                break;
            default:
                i2 = 0;
                i = 0;
                break;
        }
        if (Math.abs(i) > 60 && Math.abs(i) > Math.abs(i2) && this.mGoOnChangeMode) {
            getContext().onScrollRestMode(i > 0);
            this.mGoOnChangeMode = false;
        }
        return false;
    }

    @Override // com.android.camera.GestureDispatcher.GestureDispatcherListener
    public boolean onDoubleTap(float f, float f2) {
        int maxZoomIndex = 0;
        if (!FeatureSwitcher.isSupportDoubleTapUp()) {
            return false;
        }
        Log.m5d("ZoomManager", "onDoubleTap(" + f + ", " + f2 + ") mZoomIndexFactor=" + this.mZoomIndexFactor + ", isAppSupported()=" + isAppSupported() + ", isEnabled()=" + isEnabled());
        if (!isAppSupported() || (!isEnabled())) {
            return false;
        }
        if (findZoomIndex(this.mLastZoomRatio) == 0) {
            maxZoomIndex = getMaxZoomIndex();
            this.mZoomIndexFactor = getMaxZoomIndexFactor();
        } else {
            this.mZoomIndexFactor = 1.0f;
        }
        performZoom(maxZoomIndex, true);
        return true;
    }

    @Override // com.android.camera.GestureDispatcher.GestureDispatcherListener
    public boolean onScale(float f, float f2, float f3) {
        if (!isAppSupported() || (!isEnabled()) || Float.isNaN(f3) || Float.isInfinite(f3)) {
            return false;
        }
        this.mZoomIndexFactor *= f3;
        if (this.mZoomIndexFactor <= 1.0f) {
            this.mZoomIndexFactor = 1.0f;
        } else if (this.mZoomIndexFactor >= getMaxZoomIndexFactor()) {
            this.mZoomIndexFactor = getMaxZoomIndexFactor();
        }
        performZoom(findZoomIndex(Math.round(this.mZoomIndexFactor * 100.0f)), true);
        Log.m5d("ZoomManager", "onScale() mZoomIndexFactor=" + this.mZoomIndexFactor);
        return true;
    }

    @Override // com.android.camera.GestureDispatcher.GestureDispatcherListener
    public boolean onScaleBegin(float f, float f2) {
        Log.m5d("ZoomManager", "onScaleBegin(" + f + ", " + f2 + ")");
        return true;
    }

    public void resetZoom() {
        Log.m5d("ZoomManager", "resetZoom() mZoomRatios=" + this.mZoomRatios + ", mLastZoomRatio=" + this.mLastZoomRatio);
        this.mZoomIndexFactor = 1.0f;
        if (isValidZoomIndex(0)) {
            this.mLastZoomRatio = this.mZoomRatios.get(0).intValue();
        }
    }

    public void performZoom(int i, boolean z) {
        Log.m5d("ZoomManager", "performZoom(" + i + ", " + z + ", mDeviceSupport=" + this.mDeviceSupport);
        if (getContext().getCameraDevice() != null && this.mDeviceSupport && isValidZoomIndex(i)) {
            getContext().startAsyncZoom(i);
            int iIntValue = this.mZoomRatios.get(i).intValue();
            if (this.mLastZoomRatio != iIntValue) {
                this.mLastZoomRatio = iIntValue;
            }
        }
        if (z) {
            getContext().getCameraAppUI().showInfo("x" + String.format(Locale.ENGLISH, "%.1f", Float.valueOf(this.mLastZoomRatio / 100.0f)));
        }
    }

    public void setZoomParameter() {
        int iFindZoomIndex;
        if (!isAppSupported()) {
            resetZoom();
            performZoom(0, false);
            return;
        }
        if (getContext().getParameters() == null) {
            Log.m5d("ZoomManager", "setZoomParameter() getContext().getParameters() is null!");
            return;
        }
        this.mDeviceSupport = getContext().getParameters().isZoomSupported();
        this.mZoomRatios = getContext().getParameters().getZoomRatios();
        int zoom = getContext().getParameters().getZoom();
        if (this.mZoomRatios != null) {
            int size = this.mZoomRatios.size();
            int iIntValue = this.mZoomRatios.get(zoom).intValue();
            int iIntValue2 = this.mZoomRatios.get(size - 1).intValue();
            int iIntValue3 = this.mZoomRatios.get(0).intValue();
            if (this.mLastZoomRatio == -1 || this.mLastZoomRatio == iIntValue) {
                this.mLastZoomRatio = iIntValue;
                iFindZoomIndex = zoom;
            } else {
                iFindZoomIndex = findZoomIndex(this.mLastZoomRatio);
            }
            int iIntValue4 = this.mZoomRatios.get(iFindZoomIndex).intValue();
            performZoom(iFindZoomIndex, iIntValue4 != this.mLastZoomRatio);
            Log.m5d("ZoomManager", "onCameraParameterReady() index = " + zoom + ", len = " + size + ", maxRatio = " + iIntValue2 + ", minRatio = " + iIntValue3 + ", curRatio = " + iIntValue + ", finalIndex = " + iFindZoomIndex + ", newRatio = " + iIntValue4 + ", mSupportZoom = " + this.mDeviceSupport + ", mLastZoomRatio = " + this.mLastZoomRatio);
        }
    }

    public void changeZoomForQuality() {
        this.isHDRecord = false;
    }

    private boolean isAppSupported() {
        boolean zIsZoomEnable = isZoomEnable(getContext().getCurrentMode());
        if (this.isHDRecord) {
            zIsZoomEnable = false;
        }
        Log.m5d("ZoomManager", "isAppSupported() return " + zIsZoomEnable);
        return zIsZoomEnable;
    }

    private boolean isZoomEnable(int i) {
        if (i > -1 && i < MATRIX_ZOOM_ENABLE.length) {
            return MATRIX_ZOOM_ENABLE[i];
        }
        throw new RuntimeException("Get zoom enable out of index");
    }

    private int findZoomIndex(int i) {
        if (this.mZoomRatios != null) {
            int size = this.mZoomRatios.size();
            if (size == 1) {
                return 0;
            }
            int iIntValue = this.mZoomRatios.get(size - 1).intValue();
            if (i <= this.mZoomRatios.get(0).intValue()) {
                return 0;
            }
            if (i >= iIntValue) {
                return size - 1;
            }
            for (int i2 = 0; i2 < size - 1; i2++) {
                int iIntValue2 = this.mZoomRatios.get(i2).intValue();
                int iIntValue3 = this.mZoomRatios.get(i2 + 1).intValue();
                if (i >= iIntValue2 && i < iIntValue3) {
                    return i2;
                }
            }
        }
        return 0;
    }

    private boolean isValidZoomIndex(int i) {
        boolean z = false;
        if (this.mZoomRatios != null && i >= 0 && i < this.mZoomRatios.size()) {
            z = true;
        }
        Log.m5d("ZoomManager", "isValidZoomIndex(" + i + ") return " + z);
        return z;
    }

    private int getMaxZoomIndex() {
        if (this.mZoomRatios == null) {
            return -1;
        }
        return this.mZoomRatios.size() - 1;
    }

    private float getMaxZoomIndexFactor() {
        return getMaxZoomRatio() / 100.0f;
    }

    private int getMaxZoomRatio() {
        if (this.mZoomRatios == null) {
            return -1;
        }
        return this.mZoomRatios.get(this.mZoomRatios.size() - 1).intValue();
    }
}
