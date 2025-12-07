package com.android.camera;

import android.content.Context;
import android.graphics.Matrix;
import android.view.MotionEvent;
import android.view.View;
import com.android.camera.CameraActivity;
import com.android.camera.GestureRecognizer;
import com.android.camera.p001ui.PreviewSurfaceView;
import com.mediatek.camera.platform.ICameraAppUi;

/* loaded from: classes.dex */
public class GestureDispatcher implements GestureRecognizer.Listener, CameraActivity.OnOrientationListener {
    private CameraActivity mCameraActivity;
    private GestureDispatcherListener mGestureDispatcherListener;
    private ICameraAppUi.GestureListener mGestureListener;
    private int mGsensorOrientation = 0;
    private boolean mIgnorGestureForZooming;
    private View mLongPressArea;
    private View mSingleTapArea;

    public interface GestureDispatcherListener {
        boolean onDoubleTap(float f, float f2);

        boolean onDown(float f, float f2, int i, int i2);

        boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2);

        boolean onScale(float f, float f2, float f3);

        boolean onScaleBegin(float f, float f2);

        boolean onScroll(float f, float f2, float f3, float f4);
    }

    public GestureDispatcher(Context context) {
        this.mCameraActivity = (CameraActivity) context;
        this.mCameraActivity.addOnOrientationListener(this);
    }

    @Override // com.android.camera.GestureRecognizer.Listener
    public boolean onSingleTapUp(float f, float f2) {
        android.util.Log.d("GestureDispatcher", "[onSingleTapUp] (" + f + ", " + f2 + ")");
        if (FeatureSwitcher.isSupportDoubleTapUp()) {
            return false;
        }
        float[] pointMapCompensation = getPointMapCompensation(f, f2);
        android.util.Log.d("GestureDispatcher", "[onSingleTapUp] zoomlistener = (" + this.mGestureListener);
        if (this.mGestureListener == null || !this.mGestureListener.onSingleTapUp(pointMapCompensation[0], pointMapCompensation[1])) {
            return onSingleTapUp(Math.round(pointMapCompensation[0]), Math.round(pointMapCompensation[1]));
        }
        return false;
    }

    @Override // com.android.camera.GestureRecognizer.Listener
    public void onLongPress(float f, float f2) {
        android.util.Log.d("GestureDispatcher", "onLongPress(" + f + ", " + f2 + ")");
        float[] pointMapCompensation = getPointMapCompensation(f, f2);
        if (this.mGestureListener != null && this.mGestureListener.onLongPress(pointMapCompensation[0], pointMapCompensation[1])) {
            return;
        }
        onLongPress(Math.round(pointMapCompensation[0]), Math.round(pointMapCompensation[1]));
    }

    @Override // com.android.camera.GestureRecognizer.Listener
    public boolean onSingleTapConfirmed(float f, float f2) {
        android.util.Log.d("GestureDispatcher", "onSingleTapConfirmed(" + f + ", " + f2 + ")");
        getPointMapCompensation(f, f2);
        return false;
    }

    @Override // com.android.camera.GestureRecognizer.Listener
    public boolean onDoubleTap(float f, float f2) {
        if (!FeatureSwitcher.isSupportDoubleTapUp()) {
            return false;
        }
        float[] pointMapCompensation = getPointMapCompensation(f, f2);
        android.util.Log.d("GestureDispatcher", "onDoubleTap(" + f + ", " + f2);
        if (this.mGestureListener != null && this.mGestureListener.onDoubleTap(pointMapCompensation[0], pointMapCompensation[1])) {
            return false;
        }
        if (this.mGestureDispatcherListener != null) {
            return this.mGestureDispatcherListener.onDoubleTap(pointMapCompensation[0], pointMapCompensation[1]);
        }
        return true;
    }

    @Override // com.android.camera.GestureRecognizer.Listener
    public boolean onScroll(float f, float f2, float f3, float f4) {
        android.util.Log.d("GestureDispatcher", "onScroll(" + f + ", " + f2 + ", " + f3 + ", " + f4 + ")");
        if (this.mIgnorGestureForZooming) {
            return false;
        }
        if ((this.mGestureListener == null || !this.mGestureListener.onScroll(f, f2, f3, f4)) && this.mGestureDispatcherListener != null) {
            return this.mGestureDispatcherListener.onScroll(f, f2, f3, f4);
        }
        return false;
    }

    @Override // com.android.camera.GestureRecognizer.Listener
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
        android.util.Log.d("GestureDispatcher", "[onFling] (" + f + ", " + f2 + ")");
        if (this.mIgnorGestureForZooming) {
            return false;
        }
        if ((this.mGestureListener == null || !this.mGestureListener.onFling(motionEvent, motionEvent2, f, f2)) && this.mGestureDispatcherListener != null) {
            return this.mGestureDispatcherListener.onFling(motionEvent, motionEvent2, f, f2);
        }
        return false;
    }

    @Override // com.android.camera.GestureRecognizer.Listener
    public boolean onScaleBegin(float f, float f2) {
        android.util.Log.d("GestureDispatcher", "onScaleBegin(" + f + ", " + f2 + ")");
        this.mIgnorGestureForZooming = true;
        if (this.mGestureListener != null) {
            this.mGestureListener.onScaleBegin(f, f2);
        }
        if (this.mGestureDispatcherListener != null) {
            return this.mGestureDispatcherListener.onScaleBegin(f, f2);
        }
        return true;
    }

    @Override // com.android.camera.GestureRecognizer.Listener
    public boolean onScale(float f, float f2, float f3) {
        android.util.Log.d("GestureDispatcher", "onScale(" + f + ", " + f2 + ", " + f3);
        if (this.mGestureListener != null && this.mGestureListener.onScale(f, f2, f3)) {
            return false;
        }
        if (this.mGestureDispatcherListener != null) {
            return this.mGestureDispatcherListener.onScale(f, f2, f3);
        }
        return true;
    }

    @Override // com.android.camera.GestureRecognizer.Listener
    public void onScaleEnd() {
        android.util.Log.d("GestureDispatcher", "onScaleEnd()");
    }

    @Override // com.android.camera.GestureRecognizer.Listener
    public void onDown(float f, float f2) {
        android.util.Log.d("GestureDispatcher", "onDown()");
        this.mIgnorGestureForZooming = false;
        float[] fArrComputeVertex = computeVertex(f, f2);
        if ((this.mGestureListener == null || !this.mGestureListener.onDown(fArrComputeVertex[0], fArrComputeVertex[1], (int) fArrComputeVertex[2], (int) fArrComputeVertex[3])) && this.mGestureDispatcherListener != null) {
            this.mGestureDispatcherListener.onDown(fArrComputeVertex[0], fArrComputeVertex[1], (int) fArrComputeVertex[2], (int) fArrComputeVertex[3]);
        }
    }

    @Override // com.android.camera.GestureRecognizer.Listener
    public void onUp() {
        android.util.Log.d("GestureDispatcher", "onUp");
        if (this.mGestureListener == null || this.mGestureListener.onUp()) {
        }
    }

    @Override // com.android.camera.CameraActivity.OnOrientationListener
    public void onOrientationChanged(int i) {
        this.mGsensorOrientation = i;
    }

    public void setGestureListener(ICameraAppUi.GestureListener gestureListener) {
        this.mGestureListener = gestureListener;
    }

    public void setGestureDispatcherListener(GestureDispatcherListener gestureDispatcherListener) {
        this.mGestureDispatcherListener = gestureDispatcherListener;
    }

    public void setSingleTapUpListener(View view) {
        this.mSingleTapArea = view;
    }

    public void setLongPressListener(View view) {
        this.mLongPressArea = view;
    }

    public float[] computeVertex(float f, float f2) {
        float[] fArr = {0.0f, 0.0f, 0.0f, 0.0f};
        int unCropWidth = this.mCameraActivity.getUnCropWidth();
        int unCropHeight = this.mCameraActivity.getUnCropHeight();
        fArr[0] = f;
        fArr[1] = f2;
        fArr[2] = unCropWidth;
        fArr[3] = unCropHeight;
        return fArr;
    }

    private float[] getPointMapCompensation(float f, float f2) {
        float[] fArr = {f, f2};
        new Matrix().mapPoints(fArr);
        return fArr;
    }

    private boolean onSingleTapUp(int i, int i2) {
        android.util.Log.d("GestureDispatcher", "onSingleTapUp x = " + i + " y= " + i2);
        return onTouchScreen(i, i2, this.mSingleTapArea, 1);
    }

    private boolean onLongPress(int i, int i2) {
        return onTouchScreen(i, i2, this.mLongPressArea, 0);
    }

    private boolean onTouchScreen(int i, int i2, View view, int i3) {
        if (view == null) {
            return false;
        }
        PreviewSurfaceView previewSurfaceView = this.mCameraActivity.getPreviewSurfaceView();
        if (previewSurfaceView == null) {
            android.util.Log.d("GestureDispatcher", "onTouchScreen surfaceView is null");
            return false;
        }
        int[] relativeLocation = Util.getRelativeLocation(previewSurfaceView, view);
        int i4 = i - relativeLocation[0];
        int i5 = i2 - relativeLocation[1];
        if (i4 >= 0 && i4 < view.getWidth() && i5 >= 0 && i5 < view.getHeight()) {
            if (i3 == 0) {
                this.mCameraActivity.onLongPress(view, i4, i5);
            } else {
                this.mCameraActivity.onSingleTapUp(view, i4, i5);
            }
            return true;
        }
        this.mCameraActivity.onSingleTapUpBorder(view, i4, i5);
        return true;
    }
}
