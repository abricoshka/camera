package com.mediatek.camera.p005v2.stream.pip;

import android.graphics.RectF;
import android.util.Size;
import com.mediatek.camera.p005v2.stream.pip.pipwrapping.AnimationRect;

/* loaded from: classes.dex */
public interface IPipGesture {

    public interface GestureCallback {
        void onTopGraphicSingleTapUp();

        void onTopGraphicTouched();
    }

    AnimationRect getTopGraphicRect(int i);

    boolean onDown(float f, float f2);

    boolean onLongPress(float f, float f2);

    void onPreviewAreaChanged(RectF rectF);

    boolean onScroll(float f, float f2, float f3, float f4);

    boolean onSingleTapUp(float f, float f2);

    boolean onUp();

    void open();

    void release();

    void setPreviewSize(Size size);
}
