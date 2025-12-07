package com.android.camera.p002v2.p003ui;

import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.Surface;

/* loaded from: classes.dex */
public interface PreviewStatusListener {

    public interface OnGestureListener {
        boolean onDoubleTap(float f, float f2);

        boolean onDown(float f, float f2);

        boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2);

        boolean onLongPress(float f, float f2);

        boolean onScale(float f, float f2, float f3);

        boolean onScaleBegin(float f, float f2);

        boolean onScroll(float f, float f2, float f3, float f4);

        boolean onSingleTapConfirmed(float f, float f2);

        boolean onSingleTapUp(float f, float f2);

        boolean onUp();
    }

    public interface OnPreviewAreaChangedListener {
        void onPreviewAreaChanged(RectF rectF);
    }

    public interface OnPreviewTouchedListener {
        boolean onPreviewTouched();
    }

    OnGestureListener getGestureListener();

    OnPreviewTouchedListener getTouchListener();

    void surfaceAvailable(Surface surface, int i, int i2);

    void surfaceDestroyed(Surface surface);

    void surfaceSizeChanged(Surface surface, int i, int i2);
}
