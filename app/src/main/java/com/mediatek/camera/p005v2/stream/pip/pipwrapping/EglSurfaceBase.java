package com.mediatek.camera.p005v2.stream.pip.pipwrapping;

import android.opengl.EGL14;
import android.opengl.EGLSurface;

/* loaded from: classes.dex */
public class EglSurfaceBase {
    protected EglCore mEglBase;
    private EGLSurface mEGLSurface = EGL14.EGL_NO_SURFACE;
    private int mWidth = -1;
    private int mHeight = -1;

    protected EglSurfaceBase(EglCore eglCore) {
        this.mEglBase = eglCore;
    }

    public void createWindowSurface(Object obj) {
        if (this.mEGLSurface != EGL14.EGL_NO_SURFACE) {
            throw new IllegalStateException("surface already created");
        }
        this.mEGLSurface = this.mEglBase.createWindowSurface(obj);
        this.mWidth = this.mEglBase.querySurface(this.mEGLSurface, 12375);
        this.mHeight = this.mEglBase.querySurface(this.mEGLSurface, 12374);
    }

    public int getWidth() {
        return this.mWidth;
    }

    public int getHeight() {
        return this.mHeight;
    }

    public void releaseEglSurface() {
        this.mEglBase.releaseSurface(this.mEGLSurface);
        this.mEGLSurface = EGL14.EGL_NO_SURFACE;
        this.mHeight = -1;
        this.mWidth = -1;
    }

    public void makeCurrent() {
        this.mEglBase.makeCurrent(this.mEGLSurface);
    }

    public void makeNothingCurrent() {
        this.mEglBase.makeNothingCurrent();
    }

    public boolean swapBuffers() {
        return this.mEglBase.swapBuffers(this.mEGLSurface);
    }

    public void setPresentationTime(long j) {
        this.mEglBase.setPresentationTime(this.mEGLSurface, j);
    }
}
