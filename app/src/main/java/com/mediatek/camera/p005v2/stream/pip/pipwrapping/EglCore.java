package com.mediatek.camera.p005v2.stream.pip.pipwrapping;

import android.graphics.SurfaceTexture;
import android.opengl.EGL14;
import android.opengl.EGLConfig;
import android.opengl.EGLContext;
import android.opengl.EGLDisplay;
import android.opengl.EGLExt;
import android.opengl.EGLSurface;
import android.view.Surface;
import com.mediatek.camera.debug.LogHelper;

/* loaded from: classes.dex */
public final class EglCore {
    private static final LogHelper.Tag TAG = new LogHelper.Tag(EglCore.class.getSimpleName());
    private EGLConfig mEGLConfig;
    private EGLContext mEGLContext;
    private EGLDisplay mEGLDisplay;
    private int mGlVersion;

    public EglCore(EGLContext eGLContext, int i) {
        this.mEGLDisplay = EGL14.EGL_NO_DISPLAY;
        this.mEGLContext = EGL14.EGL_NO_CONTEXT;
        this.mEGLConfig = null;
        this.mGlVersion = -1;
        if (this.mEGLDisplay != EGL14.EGL_NO_DISPLAY) {
            throw new RuntimeException("EGL already set up");
        }
        eGLContext = eGLContext == null ? EGL14.EGL_NO_CONTEXT : eGLContext;
        this.mEGLDisplay = EGL14.eglGetDisplay(0);
        if (this.mEGLDisplay == EGL14.EGL_NO_DISPLAY) {
            throw new RuntimeException("unable to get EGL14 display");
        }
        int[] iArr = new int[2];
        if (!EGL14.eglInitialize(this.mEGLDisplay, iArr, 0, iArr, 1)) {
            this.mEGLDisplay = null;
            throw new RuntimeException("unable to initialize EGL14");
        }
        this.mEGLConfig = PipEGLConfigWrapper.getInstance().getEGLConfigChooser().chooseConfigEGL14(this.mEGLDisplay, (i & 1) != 0);
        this.mEGLContext = null;
        if ((i & 2) != 0) {
            this.mGlVersion = 3;
            this.mEGLContext = EGL14.eglCreateContext(this.mEGLDisplay, this.mEGLConfig, eGLContext, new int[]{12440, 3, 12344}, 0);
            if (EGL14.eglGetError() != 12288) {
                LogHelper.m23d(TAG, "GLES 3.x not available");
                this.mEGLContext = null;
            }
        }
        if (this.mEGLContext == null) {
            this.mGlVersion = 2;
            this.mEGLContext = EGL14.eglCreateContext(this.mEGLDisplay, this.mEGLConfig, eGLContext, new int[]{12440, 2, 12344}, 0);
        }
        GLUtil.checkEglError("eglCreateContext");
        if (this.mEGLContext == null) {
            throw new RuntimeException("null context");
        }
    }

    public void release() {
        if (this.mEGLDisplay != EGL14.EGL_NO_DISPLAY) {
            EGL14.eglDestroyContext(this.mEGLDisplay, this.mEGLContext);
            EGL14.eglReleaseThread();
            EGL14.eglTerminate(this.mEGLDisplay);
        }
        this.mEGLDisplay = EGL14.EGL_NO_DISPLAY;
        this.mEGLContext = EGL14.EGL_NO_CONTEXT;
        this.mEGLConfig = null;
    }

    public void releaseSurface(EGLSurface eGLSurface) {
        EGL14.eglDestroySurface(this.mEGLDisplay, eGLSurface);
    }

    public EGLSurface createWindowSurface(Object obj) {
        if (!(obj instanceof Surface) && (!(obj instanceof SurfaceTexture))) {
            throw new RuntimeException("invalid surface: " + obj);
        }
        EGLSurface eGLSurfaceEglCreateWindowSurface = EGL14.eglCreateWindowSurface(this.mEGLDisplay, this.mEGLConfig, obj, new int[]{12344}, 0);
        GLUtil.checkEglError("eglCreateWindowSurface");
        if (eGLSurfaceEglCreateWindowSurface == null) {
            throw new RuntimeException("surface was null");
        }
        return eGLSurfaceEglCreateWindowSurface;
    }

    public void makeCurrent(EGLSurface eGLSurface) {
        if (this.mEGLDisplay == EGL14.EGL_NO_DISPLAY) {
            LogHelper.m23d(TAG, "NOTE: makeCurrent w/o display");
        }
        if (!EGL14.eglMakeCurrent(this.mEGLDisplay, eGLSurface, eGLSurface, this.mEGLContext)) {
            throw new RuntimeException("eglMakeCurrent failed");
        }
    }

    public void makeNothingCurrent() {
        if (!EGL14.eglMakeCurrent(this.mEGLDisplay, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_CONTEXT)) {
            throw new RuntimeException("eglMakeCurrent failed");
        }
    }

    public boolean swapBuffers(EGLSurface eGLSurface) {
        return EGL14.eglSwapBuffers(this.mEGLDisplay, eGLSurface);
    }

    public void setPresentationTime(EGLSurface eGLSurface, long j) {
        EGLExt.eglPresentationTimeANDROID(this.mEGLDisplay, eGLSurface, j);
    }

    public int querySurface(EGLSurface eGLSurface, int i) {
        int[] iArr = new int[1];
        EGL14.eglQuerySurface(this.mEGLDisplay, eGLSurface, i, iArr, 0);
        return iArr[0];
    }
}
