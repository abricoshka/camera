package com.mediatek.camera.mode.pip.pipwrapping;

import android.graphics.SurfaceTexture;
import android.opengl.EGL14;
import android.opengl.EGLConfig;
import android.opengl.EGLContext;
import android.opengl.EGLDisplay;
import android.opengl.EGLExt;
import android.opengl.EGLSurface;
import android.view.Surface;
import com.mediatek.camera.util.Log;

/* loaded from: classes.dex */
public final class EglCore {
    private EGLConfig mEGLConfig;
    private EGLContext mEGLContext;
    private EGLDisplay mEGLDisplay;
    private EGLConfigWrapper mEglConfigWrapper;
    private int mGlVersion;
    private int mOutputPixelFormat;
    private int[] mSupportedPixelFormats;

    public EglCore(EGLContext eGLContext, int i, int[] iArr) {
        this.mEGLDisplay = EGL14.EGL_NO_DISPLAY;
        this.mEGLContext = EGL14.EGL_NO_CONTEXT;
        this.mEGLConfig = null;
        this.mGlVersion = -1;
        this.mOutputPixelFormat = -1;
        this.mSupportedPixelFormats = new int[0];
        Log.m31d("EglCore", "inputFormats:" + iArr);
        if (iArr != null) {
            this.mSupportedPixelFormats = iArr;
        }
        init(eGLContext, i);
    }

    public EglCore(EGLContext eGLContext, int i) {
        this.mEGLDisplay = EGL14.EGL_NO_DISPLAY;
        this.mEGLContext = EGL14.EGL_NO_CONTEXT;
        this.mEGLConfig = null;
        this.mGlVersion = -1;
        this.mOutputPixelFormat = -1;
        this.mSupportedPixelFormats = new int[0];
        init(eGLContext, i);
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
        if (eGLSurface != null) {
            EGL14.eglDestroySurface(this.mEGLDisplay, eGLSurface);
        }
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

    public EGLSurface createOffscreenSurface(int i, int i2) {
        EGLSurface eGLSurfaceEglCreatePbufferSurface = EGL14.eglCreatePbufferSurface(this.mEGLDisplay, this.mEGLConfig, new int[]{12375, i, 12374, i2, 12344}, 0);
        GLUtil.checkEglError("eglCreatePbufferSurface");
        if (eGLSurfaceEglCreatePbufferSurface == null) {
            throw new RuntimeException("surface was null");
        }
        return eGLSurfaceEglCreatePbufferSurface;
    }

    public void makeCurrent(EGLSurface eGLSurface) {
        if (this.mEGLDisplay == EGL14.EGL_NO_DISPLAY) {
            Log.m31d("EglCore", "NOTE: makeCurrent w/o display");
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

    public int getPixelFormat() {
        return this.mOutputPixelFormat;
    }

    private void init(EGLContext eGLContext, int i) {
        if (this.mEGLDisplay != EGL14.EGL_NO_DISPLAY) {
            throw new RuntimeException("EGL already set up");
        }
        if (eGLContext == null) {
            eGLContext = EGL14.EGL_NO_CONTEXT;
        }
        this.mEGLDisplay = EGL14.eglGetDisplay(0);
        if (this.mEGLDisplay == EGL14.EGL_NO_DISPLAY) {
            throw new RuntimeException("unable to get EGL14 display");
        }
        int[] iArr = new int[2];
        if (!EGL14.eglInitialize(this.mEGLDisplay, iArr, 0, iArr, 1)) {
            this.mEGLDisplay = null;
            throw new RuntimeException("unable to initialize EGL14");
        }
        this.mEglConfigWrapper = new EGLConfigWrapper();
        if (this.mSupportedPixelFormats.length > 0) {
            this.mEglConfigWrapper.setSupportedFormats(this.mSupportedPixelFormats);
        }
        this.mEGLConfig = this.mEglConfigWrapper.chooseConfigEGL14(this.mEGLDisplay, (i & 1) != 0);
        this.mOutputPixelFormat = this.mEglConfigWrapper.getSelectedPixelFormat();
        this.mEGLContext = null;
        if ((i & 2) != 0) {
            this.mGlVersion = 3;
            this.mEGLContext = EGL14.eglCreateContext(this.mEGLDisplay, this.mEGLConfig, eGLContext, new int[]{12440, 3, 12344}, 0);
            if (EGL14.eglGetError() != 12288) {
                Log.m31d("EglCore", "GLES 3.x not available");
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
}
