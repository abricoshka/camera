package com.mediatek.camera.p005v2.platform.module;

import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.Surface;

/* loaded from: classes.dex */
public interface ModuleUi {

    public interface GestureListener {
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

    public interface PreviewAreaChangedListener {
        void onPreviewAreaChanged(RectF rectF);
    }

    public interface PreviewTouchedListener {
        boolean onPreviewTouched();
    }

    GestureListener getGestureListener();

    PreviewTouchedListener getPreviewTouchedListener();

    void onSurfaceAvailable(Surface surface, int i, int i2);

    boolean onSurfaceDestroyed(Surface surface);

    void onSurfaceSizeChanged(Surface surface, int i, int i2);
}
