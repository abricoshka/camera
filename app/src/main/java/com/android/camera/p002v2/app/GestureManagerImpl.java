package com.android.camera.p002v2.app;

import android.view.MotionEvent;
import android.view.View;
import com.android.camera.CameraActivity;
import com.android.camera.p002v2.app.GestureRecognizer;
import com.android.camera.p002v2.util.CameraUtil;
import com.mediatek.camera.debug.LogHelper;

/* loaded from: classes.dex */
public class GestureManagerImpl extends GestureManager {
    private static final LogHelper.Tag TAG = new LogHelper.Tag(GestureManagerImpl.class.getSimpleName());
    private final AppController mAppController;
    private final CameraActivity mCameraActivity;
    private GestureRecognizer mGestureRecoginzer;
    private MyListener mLocalGestureListener = new MyListener(this, null);
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() { // from class: com.android.camera.v2.app.GestureManagerImpl.1
        @Override // android.view.View.OnTouchListener
        public boolean onTouch(View view, MotionEvent motionEvent) {
            LogHelper.m26i(GestureManagerImpl.TAG, "Gesture ontouch");
            GestureManagerImpl.this.mGestureRecoginzer.onTouchEvent(motionEvent);
            return false;
        }
    };

    public GestureManagerImpl(AppController appController) {
        this.mAppController = appController;
        this.mCameraActivity = (CameraActivity) appController.getActivity();
        this.mGestureRecoginzer = new GestureRecognizer(this.mCameraActivity, this.mLocalGestureListener);
        this.mAppController.getPreviewManager().setGestureListener(this.mTouchListener);
    }

    @Override // com.android.camera.p002v2.app.GestureManager
    public void onOrientationChanged(int i) {
    }

    /* JADX INFO: Access modifiers changed from: private */
    public float[] convertPortraitDistanceByOrientation(float f, float f2) {
        float[] fArr = {f, f2};
        switch ((this.mGsensorOrientation + CameraUtil.getDisplayRotation(this.mCameraActivity)) % 360) {
            case 90:
                float f3 = -f;
                f = f2;
                f2 = f3;
                break;
            case 180:
                f = -f;
                f2 = -f2;
                break;
            case 270:
                f = -f2;
                f2 = f;
                break;
        }
        LogHelper.m26i(TAG, "display rotation:" + CameraUtil.getDisplayRotation(this.mCameraActivity) + " orientation :" + this.mGsensorOrientation);
        fArr[0] = f;
        fArr[1] = f2;
        return fArr;
    }

    private class MyListener implements GestureRecognizer.Listener {
        /* synthetic */ MyListener(GestureManagerImpl gestureManagerImpl, MyListener myListener) {
            this();
        }

        private MyListener() {
        }

        @Override // com.android.camera.v2.app.GestureRecognizer.Listener
        public void onDown(float f, float f2) {
            LogHelper.m26i(GestureManagerImpl.TAG, "onDown x:" + f + ",y:" + f2);
            GestureManagerImpl.this.mGestureNotifier.onDown(f, f2);
        }

        @Override // com.android.camera.v2.app.GestureRecognizer.Listener
        public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
            GestureManagerImpl.this.mGestureNotifier.onFling(motionEvent, motionEvent2, f, f2);
            return false;
        }

        @Override // com.android.camera.v2.app.GestureRecognizer.Listener
        public boolean onScroll(float f, float f2, float f3, float f4) {
            LogHelper.m26i(GestureManagerImpl.TAG, "onScroll (dx,dy)(" + f + "," + f2 + ") totalX = " + f3 + " totalY = " + f4);
            float[] fArrConvertPortraitDistanceByOrientation = GestureManagerImpl.this.convertPortraitDistanceByOrientation(f, f2);
            GestureManagerImpl.this.mGestureNotifier.onScroll(fArrConvertPortraitDistanceByOrientation[0], fArrConvertPortraitDistanceByOrientation[1], f3, f4);
            return false;
        }

        @Override // com.android.camera.v2.app.GestureRecognizer.Listener
        public boolean onSingleTapUp(float f, float f2) {
            GestureManagerImpl.this.mGestureNotifier.onSingleTapUp(f, f2);
            return false;
        }

        @Override // com.android.camera.v2.app.GestureRecognizer.Listener
        public boolean onSingleTapConfirmed(float f, float f2) {
            GestureManagerImpl.this.mGestureNotifier.onSingleTapConfirmed(f, f2);
            return false;
        }

        @Override // com.android.camera.v2.app.GestureRecognizer.Listener
        public void onUp() {
            GestureManagerImpl.this.mGestureNotifier.onUp();
        }

        @Override // com.android.camera.v2.app.GestureRecognizer.Listener
        public boolean onDoubleTap(float f, float f2) {
            GestureManagerImpl.this.mGestureNotifier.onDoubleTap(f, f2);
            return true;
        }

        @Override // com.android.camera.v2.app.GestureRecognizer.Listener
        public boolean onScale(float f, float f2, float f3) {
            GestureManagerImpl.this.mGestureNotifier.onScale(f, f2, f3);
            return true;
        }

        @Override // com.android.camera.v2.app.GestureRecognizer.Listener
        public boolean onScaleBegin(float f, float f2) {
            GestureManagerImpl.this.mGestureNotifier.onScaleBegin(f, f2);
            return true;
        }

        @Override // com.android.camera.v2.app.GestureRecognizer.Listener
        public void onScaleEnd() {
        }

        @Override // com.android.camera.v2.app.GestureRecognizer.Listener
        public void onLongPress(float f, float f2) {
            GestureManagerImpl.this.mGestureNotifier.onLongPress(f, f2);
        }
    }
}
