package com.mediatek.camera.p005v2.stream.pip;

import android.app.Activity;
import android.graphics.RectF;
import android.util.Size;
import android.view.Surface;

/* loaded from: classes.dex */
public interface IPipStream {

    public interface PipStreamCallback {
        void onClosed();

        void onOpened();

        void onPaused();

        void onResumed();

        void onSwitchPipEventReceived();

        void onTopGraphicTouched();
    }

    void close();

    void onActivityPause();

    boolean onDown(float f, float f2);

    boolean onLongPress(float f, float f2);

    void onOrientationChanged(int i);

    void onPreviewAreaChanged(RectF rectF);

    boolean onScroll(float f, float f2, float f3, float f4);

    boolean onSingleTapUp(float f, float f2);

    void onTemplateChanged(int i, int i2, int i3, int i4);

    boolean onUp();

    void open(Activity activity);

    void pause();

    void registerPipStreamCallback(PipStreamCallback pipStreamCallback);

    void resume();

    void setCaptureSize(Size size, Size size2);

    void setPreviewSurface(Surface surface);

    void switchingPip();

    void unregisterPipStreamCallback(PipStreamCallback pipStreamCallback);
}
