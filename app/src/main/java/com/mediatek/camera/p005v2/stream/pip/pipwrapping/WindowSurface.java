package com.mediatek.camera.p005v2.stream.pip.pipwrapping;

import android.view.Surface;

/* loaded from: classes.dex */
public class WindowSurface extends EglSurfaceBase {
    private Surface mSurface;

    public WindowSurface(EglCore eglCore, Surface surface) {
        super(eglCore);
        createWindowSurface(surface);
        this.mSurface = surface;
    }

    public void release() {
        releaseEglSurface();
        if (this.mSurface != null) {
            this.mSurface.release();
            this.mSurface = null;
        }
    }
}
