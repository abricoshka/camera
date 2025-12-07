package com.mediatek.camera.p005v2.stream.pip.pipwrapping;

import android.app.Activity;
import android.opengl.EGL14;
import android.opengl.EGLContext;
import android.opengl.EGLDisplay;
import android.opengl.EGLSurface;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.view.Surface;
import com.mediatek.camera.debug.LogHelper;
import java.nio.Buffer;
import java.nio.FloatBuffer;

/* loaded from: classes.dex */
public class CaptureRenderer extends Renderer {
    private static final LogHelper.Tag TAG = new LogHelper.Tag(CaptureRenderer.class.getSimpleName());
    private WindowSurface mCaptureEGLSurface;
    private Surface mCaptureSurface;
    private EglCore mEglCore;
    private float[] mMMtx;
    private float[] mPMtx;
    private float[] mPosMtx;
    private int mProgram;
    private EGLContext mSavedEglContext;
    private EGLDisplay mSavedEglDisplay;
    private EGLSurface mSavedEglDrawSurface;
    private EGLSurface mSavedEglReadSurface;
    private FloatBuffer mTexCoordBuf;
    private float[] mVMtx;
    private FloatBuffer mVtxBuf;
    private int maPositionHandle;
    private int maTexCoordHandle;
    private int muPosMtxHandle;
    private int muSamplerHandle;

    public CaptureRenderer(Activity activity) {
        super(activity);
        this.mPosMtx = GLUtil.createIdentityMtx();
        this.mPMtx = GLUtil.createIdentityMtx();
        this.mVMtx = GLUtil.createIdentityMtx();
        this.mMMtx = GLUtil.createIdentityMtx();
        this.mProgram = -1;
        this.maPositionHandle = -1;
        this.maTexCoordHandle = -1;
        this.muPosMtxHandle = -1;
        this.muSamplerHandle = -1;
        this.mSavedEglDisplay = null;
        this.mSavedEglDrawSurface = null;
        this.mSavedEglReadSurface = null;
        this.mSavedEglContext = null;
    }

    public void init() {
        LogHelper.m23d(TAG, "initScreenSurface");
        this.mEglCore = new EglCore(EGL14.eglGetCurrentContext(), 2);
        initGL();
        this.mTexCoordBuf = createFloatBuffer(this.mTexCoordBuf, GLUtil.createTexCoord());
    }

    @Override // com.mediatek.camera.p005v2.stream.pip.pipwrapping.Renderer
    public void setRendererSize(int i, int i2) {
        LogHelper.m23d(TAG, "setRendererSize width = " + i + " height = " + i2);
        if (i == getRendererWidth() && i2 == getRendererHeight()) {
            return;
        }
        resetMatrix();
        super.setRendererSize(i, i2);
        Matrix.orthoM(this.mPMtx, 0, 0.0f, i, 0.0f, i2, -1.0f, 1.0f);
        Matrix.translateM(this.mMMtx, 0, 0.0f, i2, 0.0f);
        Matrix.scaleM(this.mMMtx, 0, this.mMMtx, 0, 1.0f, -1.0f, 1.0f);
        Matrix.multiplyMM(this.mPosMtx, 0, this.mMMtx, 0, this.mVMtx, 0);
        Matrix.multiplyMM(this.mPosMtx, 0, this.mPMtx, 0, this.mPosMtx, 0);
        initVertexData(i, i2);
    }

    public void setCaptureSurface(Surface surface) {
        if (surface == null) {
            throw new RuntimeException("setCaptureSurface capture surface is null!!!!!");
        }
        this.mCaptureSurface = surface;
    }

    private void initVertexData(float f, float f2) {
        this.mVtxBuf = createFloatBuffer(this.mVtxBuf, GLUtil.createFullSquareVtx(f, f2));
    }

    public void draw(int i) {
        saveRenderState();
        LogHelper.m23d(TAG, "draw texId = " + i + " mCaptureSurface = " + this.mCaptureSurface);
        if (this.mCaptureSurface != null) {
            this.mCaptureEGLSurface = new WindowSurface(this.mEglCore, this.mCaptureSurface);
            this.mCaptureEGLSurface.makeCurrent();
            LogHelper.m26i(TAG, "Surface width = " + this.mCaptureEGLSurface.getWidth() + " height = " + this.mCaptureEGLSurface.getHeight());
        }
        if (getRendererWidth() <= 0 || getRendererHeight() <= 0 || this.mCaptureEGLSurface == null) {
            return;
        }
        GLUtil.checkGlError("CaptureDraw_Start");
        GLES20.glViewport(0, 0, getRendererWidth(), getRendererHeight());
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        GLES20.glClear(16640);
        GLES20.glUseProgram(this.mProgram);
        this.mVtxBuf.position(0);
        GLES20.glVertexAttribPointer(this.maPositionHandle, 3, 5126, false, 12, (Buffer) this.mVtxBuf);
        this.mTexCoordBuf.position(0);
        GLES20.glVertexAttribPointer(this.maTexCoordHandle, 2, 5126, false, 8, (Buffer) this.mTexCoordBuf);
        GLES20.glEnableVertexAttribArray(this.maPositionHandle);
        GLES20.glEnableVertexAttribArray(this.maTexCoordHandle);
        GLES20.glUniformMatrix4fv(this.muPosMtxHandle, 1, false, this.mPosMtx, 0);
        GLES20.glUniform1i(this.muSamplerHandle, 0);
        GLES20.glActiveTexture(33984);
        GLES20.glBindTexture(3553, i);
        GLES20.glDrawArrays(5, 0, 6);
        this.mCaptureEGLSurface.swapBuffers();
        GLUtil.checkGlError("CaptureDraw_End");
        this.mCaptureEGLSurface.makeNothingCurrent();
        this.mCaptureEGLSurface.releaseEglSurface();
        this.mCaptureEGLSurface = null;
        restoreRenderState();
        LogHelper.m23d(TAG, "draw end");
    }

    @Override // com.mediatek.camera.p005v2.stream.pip.pipwrapping.Renderer
    public void release() {
        if (this.mCaptureEGLSurface != null) {
            this.mCaptureEGLSurface.makeNothingCurrent();
            this.mCaptureEGLSurface.release();
            this.mCaptureEGLSurface = null;
        }
        if (this.mEglCore != null) {
            this.mEglCore.release();
            this.mEglCore = null;
        }
    }

    private void initGL() {
        LogHelper.m23d(TAG, "initGL");
        GLUtil.checkGlError("initGL_Start");
        this.mProgram = GLUtil.createProgram("attribute vec4 aPosition;\nattribute vec4 aTexCoord;\nuniform   mat4 uPosMtx;\nvarying   vec2 vTexCoord;\nvoid main() {\n  gl_Position = uPosMtx * aPosition;\n  vTexCoord   = aTexCoord.xy;\n}\n", "precision mediump float;\nuniform sampler2D uSampler;\nvarying vec2      vTexCoord;\nvoid main() {\n  gl_FragColor = texture2D(uSampler, vTexCoord);\n}\n");
        this.maPositionHandle = GLES20.glGetAttribLocation(this.mProgram, "aPosition");
        this.maTexCoordHandle = GLES20.glGetAttribLocation(this.mProgram, "aTexCoord");
        this.muPosMtxHandle = GLES20.glGetUniformLocation(this.mProgram, "uPosMtx");
        this.muSamplerHandle = GLES20.glGetUniformLocation(this.mProgram, "uSampler");
        GLES20.glDisable(2929);
        GLES20.glDisable(2884);
        GLES20.glDisable(3042);
        GLUtil.checkGlError("initGL_E");
    }

    public void saveRenderState() {
        this.mSavedEglDisplay = EGL14.eglGetCurrentDisplay();
        this.mSavedEglDrawSurface = EGL14.eglGetCurrentSurface(12377);
        this.mSavedEglReadSurface = EGL14.eglGetCurrentSurface(12378);
        this.mSavedEglContext = EGL14.eglGetCurrentContext();
    }

    public void restoreRenderState() {
        if (!EGL14.eglMakeCurrent(this.mSavedEglDisplay, this.mSavedEglDrawSurface, this.mSavedEglReadSurface, this.mSavedEglContext)) {
            throw new RuntimeException("eglMakeCurrent failed");
        }
    }

    private void resetMatrix() {
        this.mPosMtx = GLUtil.createIdentityMtx();
        this.mPMtx = GLUtil.createIdentityMtx();
        this.mVMtx = GLUtil.createIdentityMtx();
        this.mMMtx = GLUtil.createIdentityMtx();
    }
}
