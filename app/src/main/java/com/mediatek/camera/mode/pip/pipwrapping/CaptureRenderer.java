package com.mediatek.camera.mode.pip.pipwrapping;

import android.app.Activity;
import android.opengl.EGL14;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.view.Surface;
import com.mediatek.camera.util.Log;
import java.nio.Buffer;
import java.nio.FloatBuffer;

/* loaded from: classes.dex */
public class CaptureRenderer extends Renderer {
    private static final String TAG = CaptureRenderer.class.getSimpleName();
    private WindowSurface mCaptureEGLSurface;
    private Surface mCaptureSurface;
    private EglCore mEglCore;
    private float[] mMMtx;
    private float[] mPMtx;
    private float[] mPosMtx;
    private int mProgram;
    private float[] mRotateMtx;
    private FloatBuffer mTexCoordBuf;
    private float[] mVMtx;
    private FloatBuffer mVtxBuf;
    private int maPositionHandle;
    private int maTexCoordHandle;
    private int muPosMtxHandle;
    private int muSamplerHandle;
    private int muTexRotateMtxHandle;

    public CaptureRenderer(Activity activity) {
        super(activity);
        this.mPosMtx = GLUtil.createIdentityMtx();
        this.mPMtx = GLUtil.createIdentityMtx();
        this.mVMtx = GLUtil.createIdentityMtx();
        this.mMMtx = GLUtil.createIdentityMtx();
        this.mRotateMtx = GLUtil.createIdentityMtx();
        this.mProgram = -1;
        this.maPositionHandle = -1;
        this.maTexCoordHandle = -1;
        this.muTexRotateMtxHandle = -1;
        this.muPosMtxHandle = -1;
        this.muSamplerHandle = -1;
    }

    public void init() {
        Log.m31d(TAG, "initScreenSurface");
        this.mEglCore = new EglCore(EGL14.eglGetCurrentContext(), 2);
        initGL();
        this.mTexCoordBuf = createFloatBuffer(this.mTexCoordBuf, GLUtil.createTexCoord());
    }

    public void setCaptureSize(int i, int i2, int i3) {
        int i4;
        Log.m31d(TAG, "setCaptureSize width = " + i + " height = " + i2 + " orientation:" + i3);
        int iMin = Math.min(i, i2);
        int iMax = Math.max(i, i2);
        if (i3 % 180 != 0) {
            this.mRotateMtx = GLUtil.createIdentityMtx();
            Matrix.translateM(this.mRotateMtx, 0, this.mRotateMtx, 0, 0.5f, 0.5f, 0.0f);
            Matrix.rotateM(this.mRotateMtx, 0, i3, 0.0f, 0.0f, 1.0f);
            Matrix.translateM(this.mRotateMtx, 0, -0.5f, -0.5f, 0.0f);
            i4 = iMin;
        } else {
            i4 = iMax;
            iMax = iMin;
        }
        setRendererSize(iMax, i4);
    }

    @Override // com.mediatek.camera.mode.pip.pipwrapping.Renderer
    public void setRendererSize(int i, int i2) {
        if (i == getRendererWidth() && i2 == getRendererHeight()) {
            return;
        }
        resetMatrix();
        super.setRendererSize(i, i2);
        Matrix.orthoM(this.mPMtx, 0, 0.0f, i, 0.0f, i2, -1.0f, 1.0f);
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

    public void draw(int i) {
        Log.m31d(TAG, "draw texId = " + i + " mCaptureSurface = " + this.mCaptureSurface);
        if (this.mCaptureSurface != null) {
            this.mCaptureEGLSurface = new WindowSurface(this.mEglCore, this.mCaptureSurface);
            this.mCaptureEGLSurface.makeCurrent();
            Log.m31d(TAG, "Surface width = " + this.mCaptureEGLSurface.getWidth() + " height = " + this.mCaptureEGLSurface.getHeight());
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
        GLES20.glUniformMatrix4fv(this.muTexRotateMtxHandle, 1, false, this.mRotateMtx, 0);
        GLES20.glUniform1i(this.muSamplerHandle, 0);
        GLES20.glActiveTexture(33984);
        GLES20.glBindTexture(3553, i);
        GLES20.glDrawArrays(5, 0, 6);
        this.mCaptureEGLSurface.swapBuffers();
        GLUtil.checkGlError("CaptureDraw_End");
        this.mCaptureEGLSurface.makeNothingCurrent();
        this.mCaptureEGLSurface.releaseEglSurface();
        this.mCaptureEGLSurface = null;
        Log.m31d(TAG, "draw end");
    }

    @Override // com.mediatek.camera.mode.pip.pipwrapping.Renderer
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

    private void initVertexData(float f, float f2) {
        this.mVtxBuf = createFloatBuffer(this.mVtxBuf, GLUtil.createFullSquareVtx(f, f2));
    }

    private void initGL() {
        Log.m31d(TAG, "ScreenRenderer");
        GLUtil.checkGlError("initGL_Start");
        this.mProgram = GLUtil.createProgram("attribute vec4 aPosition;\nattribute vec4 aTexCoord;\nuniform   mat4 uPosMtx;\nuniform   mat4 uTexRotateMtx;\nvarying   vec2 vTexCoord;\nvoid main() {\n  gl_Position = uPosMtx * aPosition;\n  vTexCoord     = (uTexRotateMtx * aTexCoord).xy;\n}\n", "precision mediump float;\nuniform sampler2D uSampler;\nvarying vec2      vTexCoord;\nvoid main() {\n  gl_FragColor = texture2D(uSampler, vTexCoord);\n}\n");
        this.maPositionHandle = GLES20.glGetAttribLocation(this.mProgram, "aPosition");
        this.maTexCoordHandle = GLES20.glGetAttribLocation(this.mProgram, "aTexCoord");
        this.muTexRotateMtxHandle = GLES20.glGetUniformLocation(this.mProgram, "uTexRotateMtx");
        this.muPosMtxHandle = GLES20.glGetUniformLocation(this.mProgram, "uPosMtx");
        this.muSamplerHandle = GLES20.glGetUniformLocation(this.mProgram, "uSampler");
        GLES20.glDisable(2929);
        GLES20.glDisable(2884);
        GLES20.glDisable(3042);
        GLUtil.checkGlError("initGL_E");
    }

    private void resetMatrix() {
        this.mPosMtx = GLUtil.createIdentityMtx();
        this.mPMtx = GLUtil.createIdentityMtx();
        this.mVMtx = GLUtil.createIdentityMtx();
        this.mMMtx = GLUtil.createIdentityMtx();
    }
}
