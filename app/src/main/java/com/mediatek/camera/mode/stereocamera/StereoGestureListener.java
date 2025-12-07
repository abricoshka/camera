package com.mediatek.camera.mode.stereocamera;

import android.view.MotionEvent;
import com.mediatek.camera.platform.ICameraAppUi;

/* loaded from: classes.dex */
public class StereoGestureListener implements ICameraAppUi.GestureListener {
    @Override // com.mediatek.camera.platform.ICameraAppUi.GestureListener
    public boolean onDown(float f, float f2, int i, int i2) {
        return true;
    }

    @Override // com.mediatek.camera.platform.ICameraAppUi.GestureListener
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
        return true;
    }

    @Override // com.mediatek.camera.platform.ICameraAppUi.GestureListener
    public boolean onScroll(float f, float f2, float f3, float f4) {
        return true;
    }

    @Override // com.mediatek.camera.platform.ICameraAppUi.GestureListener
    public boolean onSingleTapUp(float f, float f2) {
        return true;
    }

    @Override // com.mediatek.camera.platform.ICameraAppUi.GestureListener
    public boolean onUp() {
        return true;
    }

    @Override // com.mediatek.camera.platform.ICameraAppUi.GestureListener
    public boolean onDoubleTap(float f, float f2) {
        return true;
    }

    @Override // com.mediatek.camera.platform.ICameraAppUi.GestureListener
    public boolean onScale(float f, float f2, float f3) {
        return true;
    }

    @Override // com.mediatek.camera.platform.ICameraAppUi.GestureListener
    public boolean onScaleBegin(float f, float f2) {
        return true;
    }

    @Override // com.mediatek.camera.platform.ICameraAppUi.GestureListener
    public boolean onLongPress(float f, float f2) {
        return true;
    }
}
