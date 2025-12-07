package com.mediatek.camera.mode.pip.pipwrapping;

import android.app.Activity;
import android.opengl.EGL14;
import android.opengl.EGLContext;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.os.ConditionVariable;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Surface;
import com.mediatek.camera.util.Log;
import java.nio.Buffer;
import java.nio.FloatBuffer;

/* loaded from: classes.dex */
public class ScreenRenderer extends Renderer implements Runnable {
    private ConditionVariable mDrawLockableConditionVariable;
    private ResourceRenderer mEditTexRenderer;
    private int mEditTexSize;
    private EglCore mEglCore;
    private boolean mIsEGLSurfaceReady;
    private float[] mMMtx;
    private float[] mPMtx;
    private float[] mPosMtx;
    private ResourceRenderer mPressedTexRenderer;
    private WindowSurface mPreviewEGLSurface;
    private Surface mPreviewSurface;
    private int mProgram;
    private boolean mReady;
    private Object mReadyFence;
    private ConditionVariable mReleaseScreenSurfaceSync;
    private int mRenderTexHeight;
    private int mRenderTexWidth;
    private boolean mRunning;
    private ScreenHandler mScreenHandler;
    private EGLContext mSharedEGLContext;
    private FloatBuffer mTexCoordBuf;
    private float[] mTexRotateMtx;
    private int mTextureRotation;
    private FloatBuffer mTopGraphicPositionBuf;
    private ConditionVariable mUpdateEGLSurfaceSync;
    private float[] mVMtx;
    private FloatBuffer mVtxBuf;
    private int maPositionHandle;
    private int maTexCoordHandle;
    private int muPosMtxHandle;
    private int muSamplerHandle;
    private int muTexRotateMtxHandle;

    public ScreenRenderer(Activity activity) {
        super(activity);
        this.mTopGraphicPositionBuf = null;
        this.mRenderTexWidth = -1;
        this.mRenderTexHeight = -1;
        this.mTextureRotation = 0;
        this.mPosMtx = GLUtil.createIdentityMtx();
        this.mPMtx = GLUtil.createIdentityMtx();
        this.mVMtx = GLUtil.createIdentityMtx();
        this.mMMtx = GLUtil.createIdentityMtx();
        this.mTexRotateMtx = GLUtil.createIdentityMtx();
        this.mPreviewSurface = null;
        this.mEditTexSize = 0;
        this.mProgram = -1;
        this.maPositionHandle = -1;
        this.muTexRotateMtxHandle = -1;
        this.maTexCoordHandle = -1;
        this.muPosMtxHandle = -1;
        this.muSamplerHandle = -1;
        this.mReadyFence = new Object();
        this.mSharedEGLContext = null;
        this.mIsEGLSurfaceReady = false;
        this.mUpdateEGLSurfaceSync = new ConditionVariable();
        this.mReleaseScreenSurfaceSync = new ConditionVariable();
        this.mDrawLockableConditionVariable = new ConditionVariable();
        this.mEditTexRenderer = new ResourceRenderer(activity);
        this.mPressedTexRenderer = new ResourceRenderer(activity);
        this.mTexCoordBuf = createFloatBuffer(this.mTexCoordBuf, GLUtil.createTexCoord());
        new Thread(this, "PIP-ScreenRenderer").start();
    }

    public void init() {
        Log.m31d("ScreenRenderer", "init: " + this);
        initGL();
        this.mSharedEGLContext = EGL14.eglGetCurrentContext();
        this.mEditTexRenderer.init();
        this.mPressedTexRenderer.init();
    }

    public void updateScreenEffectTemplate(int i, int i2) {
        if (i > 0) {
            this.mPressedTexRenderer.updateTemplate(i);
        }
        if (i2 > 0) {
            this.mEditTexRenderer.updateTemplate(i2);
        }
    }

    @Override // com.mediatek.camera.mode.pip.pipwrapping.Renderer
    public void setRendererSize(int i, int i2) {
        Log.m31d("ScreenRenderer", "setRendererSize width = " + i + " height = " + i2);
        if (!isMatchingSurfaceSize(i, i2)) {
            this.mIsEGLSurfaceReady = false;
        }
        super.setRendererSize(i, i2);
    }

    @Override // com.mediatek.camera.mode.pip.pipwrapping.Renderer
    protected void setSurface(Surface surface, boolean z, boolean z2) {
        Log.m31d("ScreenRenderer", "setSurface scaled = " + z + " rotated = " + z2 + ", mPreviewSurface = " + this.mPreviewSurface);
        if (skipUpdateSurface(surface)) {
            Log.m31d("ScreenRenderer", "the same surface skip update mPreviewSurface = " + this.mPreviewSurface);
            this.mIsEGLSurfaceReady = true;
            return;
        }
        this.mIsEGLSurfaceReady = false;
        super.setSurface(surface, z, z2);
        if (surface == null) {
            throw new RuntimeException("ScreenRenderer setSurface to null!!!!!");
        }
        this.mPreviewSurface = surface;
        waitRendererThreadActive();
        updateEGLSurface();
        this.mRenderTexWidth = this.mPreviewEGLSurface.getWidth();
        this.mRenderTexHeight = this.mPreviewEGLSurface.getHeight();
        this.mTextureRotation = getDisplayRotation(getActivity());
        updateRendererSize(this.mRenderTexWidth, this.mRenderTexHeight);
        this.mIsEGLSurfaceReady = true;
    }

    private boolean isMatchingSurfaceSize(int i, int i2) {
        Log.m31d("ScreenRenderer", "isMatchingSurfaceSize mRenderTexWidth:" + this.mRenderTexWidth + " mRenderTexHeight:" + this.mRenderTexHeight);
        return ((double) Math.abs((((float) Math.max(i, i2)) / ((float) Math.min(i, i2))) - (((float) Math.max(this.mRenderTexWidth, this.mRenderTexHeight)) / ((float) Math.min(this.mRenderTexWidth, this.mRenderTexHeight))))) <= 0.02d;
    }

    private boolean skipUpdateSurface(Surface surface) {
        boolean z = false;
        int displayRotation = getDisplayRotation(getActivity());
        boolean z2 = (this.mRenderTexWidth > this.mRenderTexHeight) == (displayRotation == 90 || displayRotation == 270);
        boolean zEquals = surface.equals(this.mPreviewSurface);
        boolean z3 = this.mRenderTexWidth > 2 && this.mRenderTexHeight > 2;
        if (this.mRenderTexWidth == getRendererWidth() && this.mRenderTexHeight == getRendererHeight()) {
            z = true;
        }
        boolean z4 = !surface.isValid();
        if (zEquals && z3 && z2 && z) {
            return true;
        }
        return z4;
    }

    private void waitRendererThreadActive() {
        synchronized (this.mReadyFence) {
            if (this.mRunning) {
                Log.m31d("ScreenRenderer", "screen renderer already running!");
                return;
            }
            this.mRunning = true;
            while (!this.mReady) {
                try {
                    Log.m31d("ScreenRenderer", "wait for screen renderer thread ready, current mReady = " + this.mReady);
                    this.mReadyFence.wait();
                } catch (InterruptedException e) {
                }
            }
            this.mDrawLockableConditionVariable.open();
        }
    }

    @Override // com.mediatek.camera.mode.pip.pipwrapping.Renderer
    public void release() {
        Log.m31d("ScreenRenderer", "release: " + this);
        synchronized (this.mReadyFence) {
            if (this.mScreenHandler != null) {
                this.mScreenHandler.removeCallbacksAndMessages(null);
                this.mReleaseScreenSurfaceSync.close();
                this.mScreenHandler.obtainMessage(1).sendToTarget();
                this.mReleaseScreenSurfaceSync.block(2000L);
            }
        }
        super.setRendererSize(-1, -1);
    }

    public void draw(AnimationRect animationRect, int i, boolean z) {
        synchronized (this.mReadyFence) {
            if (this.mScreenHandler != null && this.mIsEGLSurfaceReady) {
                this.mDrawLockableConditionVariable.close();
                this.mScreenHandler.removeMessages(3);
                this.mScreenHandler.obtainMessage(3, i, z ? 1 : 0, animationRect).sendToTarget();
                this.mDrawLockableConditionVariable.block(100L);
            }
        }
    }

    public void notifySurfaceStatus(Surface surface) {
        synchronized (this.mReadyFence) {
            if (this.mScreenHandler != null && this.mPreviewEGLSurface != null && surface == this.mPreviewEGLSurface.getSurface()) {
                this.mScreenHandler.removeCallbacksAndMessages(null);
                this.mReleaseScreenSurfaceSync.close();
                this.mScreenHandler.obtainMessage(4).sendToTarget();
                this.mReleaseScreenSurfaceSync.block(2000L);
            }
        }
    }

    @Override // java.lang.Runnable
    public void run() {
        Looper.prepare();
        synchronized (this.mReadyFence) {
            Log.m31d("ScreenRenderer", "Screen renderer thread started!");
            this.mScreenHandler = new ScreenHandler(this, null);
            this.mReady = true;
            this.mReadyFence.notify();
        }
        Looper.loop();
        Log.m31d("ScreenRenderer", "Screen renderer thread exiting!");
        synchronized (this.mReadyFence) {
            this.mRunning = false;
            this.mReady = false;
            this.mScreenHandler = null;
        }
    }

    private void updateRendererSize(int i, int i2) {
        Log.m31d("ScreenRenderer", "updateRendererSize width = " + i + " height = " + i2);
        resetMatrix();
        Matrix.orthoM(this.mPMtx, 0, 0.0f, i, 0.0f, i2, -1.0f, 1.0f);
        initVertexData(i, i2);
        this.mEditTexSize = Math.min(i, i2) / 10;
        this.mEditTexRenderer.setRendererSize(i, i2);
        this.mPressedTexRenderer.setRendererSize(i, i2);
    }

    private int getDisplayRotation(Activity activity) {
        switch (activity.getWindowManager().getDefaultDisplay().getRotation()) {
        }
        return 0;
    }

    private class ScreenHandler extends Handler {
        /* synthetic */ ScreenHandler(ScreenRenderer screenRenderer, ScreenHandler screenHandler) {
            this();
        }

        private ScreenHandler() {
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            switch (message.what) {
                case 0:
                    doSetupEGLSurface();
                    break;
                case 1:
                    doReleaseSurface();
                    ScreenRenderer.this.mReleaseScreenSurfaceSync.open();
                    break;
                case 2:
                    doUpdateEGLSurface();
                    ScreenRenderer.this.mUpdateEGLSurfaceSync.open();
                    break;
                case 3:
                    doDraw((AnimationRect) message.obj, message.arg1, message.arg2 > 0);
                    break;
                case 4:
                    releaseEglSurface();
                    ScreenRenderer.this.mReleaseScreenSurfaceSync.open();
                    break;
            }
        }

        private void doSetupEGLSurface() {
            Log.m31d("ScreenRenderer", "handleSetupSurface  mEglCore = " + ScreenRenderer.this.mEglCore + " mPreviewEGLSurface = " + ScreenRenderer.this.mPreviewEGLSurface + " mPreviewSurface = " + ScreenRenderer.this.mPreviewSurface);
            if (ScreenRenderer.this.mEglCore == null) {
                ScreenRenderer.this.mEglCore = new EglCore(ScreenRenderer.this.mSharedEGLContext, 3, new int[]{3, 1, 842094169});
            }
            if (ScreenRenderer.this.mPreviewEGLSurface == null) {
                ScreenRenderer.this.mPreviewEGLSurface = new WindowSurface(ScreenRenderer.this.mEglCore, ScreenRenderer.this.mPreviewSurface);
                ScreenRenderer.this.mPreviewEGLSurface.makeCurrent();
            }
        }

        private void doUpdateEGLSurface() {
            Log.m31d("ScreenRenderer", "updateEGLSurface mPreviewEGLSurface = " + ScreenRenderer.this.mPreviewEGLSurface);
            if (ScreenRenderer.this.mPreviewEGLSurface != null) {
                ScreenRenderer.this.mPreviewEGLSurface.makeNothingCurrent();
                ScreenRenderer.this.mPreviewEGLSurface.releaseEglSurface();
                ScreenRenderer.this.mPreviewEGLSurface = new WindowSurface(ScreenRenderer.this.mEglCore, ScreenRenderer.this.mPreviewSurface);
                ScreenRenderer.this.mPreviewEGLSurface.makeCurrent();
                return;
            }
            doSetupEGLSurface();
        }

        private void doReleaseSurface() {
            releaseEglSurface();
            if (ScreenRenderer.this.mEglCore != null) {
                ScreenRenderer.this.mEglCore.release();
                ScreenRenderer.this.mEglCore = null;
            }
            ScreenRenderer.this.mEditTexRenderer.releaseResource();
            ScreenRenderer.this.mPressedTexRenderer.releaseResource();
            ScreenRenderer.this.mIsEGLSurfaceReady = false;
            ScreenRenderer.this.mPreviewSurface = null;
            ScreenRenderer.this.mProgram = -1;
            Looper looperMyLooper = Looper.myLooper();
            if (looperMyLooper != null) {
                looperMyLooper.quit();
            }
        }

        private void releaseEglSurface() {
            Log.m31d("ScreenRenderer", "releaseEglSurface");
            if (ScreenRenderer.this.mPreviewEGLSurface != null) {
                ScreenRenderer.this.mPreviewEGLSurface.makeNothingCurrent();
                ScreenRenderer.this.mPreviewEGLSurface.releaseEglSurface();
                ScreenRenderer.this.mPreviewEGLSurface = null;
            }
        }

        private void doDraw(AnimationRect animationRect, int i, boolean z) {
            if (ScreenRenderer.this.getRendererWidth() <= 0 || ScreenRenderer.this.getRendererHeight() <= 0 || ScreenRenderer.this.mPreviewEGLSurface == null) {
                return;
            }
            GLUtil.checkGlError("ScreenDraw_Start");
            GLES20.glViewport(0, 0, ScreenRenderer.this.mRenderTexWidth, ScreenRenderer.this.mRenderTexHeight);
            GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
            GLES20.glClear(16640);
            GLES20.glUseProgram(ScreenRenderer.this.mProgram);
            ScreenRenderer.this.mVtxBuf.position(0);
            GLES20.glVertexAttribPointer(ScreenRenderer.this.maPositionHandle, 3, 5126, false, 12, (Buffer) ScreenRenderer.this.mVtxBuf);
            ScreenRenderer.this.mTexCoordBuf.position(0);
            GLES20.glVertexAttribPointer(ScreenRenderer.this.maTexCoordHandle, 2, 5126, false, 8, (Buffer) ScreenRenderer.this.mTexCoordBuf);
            GLES20.glEnableVertexAttribArray(ScreenRenderer.this.maPositionHandle);
            GLES20.glEnableVertexAttribArray(ScreenRenderer.this.maTexCoordHandle);
            GLES20.glUniformMatrix4fv(ScreenRenderer.this.muPosMtxHandle, 1, false, ScreenRenderer.this.mPosMtx, 0);
            GLES20.glUniformMatrix4fv(ScreenRenderer.this.muTexRotateMtxHandle, 1, false, ScreenRenderer.this.mTexRotateMtx, 0);
            GLES20.glUniform1i(ScreenRenderer.this.muSamplerHandle, 0);
            GLES20.glActiveTexture(33984);
            GLES20.glBindTexture(3553, i);
            GLES20.glDrawArrays(5, 0, 6);
            if (animationRect != null) {
                animationRect.changeCooridnateSystem(ScreenRenderer.this.mRenderTexWidth, ScreenRenderer.this.mRenderTexHeight, ScreenRenderer.this.mTextureRotation);
                ScreenRenderer.this.mEditTexRenderer.draw(animationRect.getRightBottom()[0], animationRect.getRightBottom()[1], ScreenRenderer.this.mEditTexSize, null, null);
            }
            if (animationRect != null && z) {
                ScreenRenderer.this.mTopGraphicPositionBuf = ScreenRenderer.this.createFloatBuffer(ScreenRenderer.this.mTopGraphicPositionBuf, GLUtil.createTopRightRect(animationRect));
                ScreenRenderer.this.mTopGraphicPositionBuf.position(0);
                ScreenRenderer.this.mPressedTexRenderer.draw(0.0f, 0.0f, 0.0f, ScreenRenderer.this.mTopGraphicPositionBuf, null);
            }
            ScreenRenderer.this.mPreviewEGLSurface.swapBuffers();
            ScreenRenderer.this.mDrawLockableConditionVariable.open();
            ScreenRenderer.this.debugFrameRate("ScreenRenderer");
            GLUtil.checkGlError("ScreenDraw_End");
        }
    }

    private void initGL() {
        if (this.mProgram != -1) {
            return;
        }
        GLUtil.checkGlError("initGL_Start");
        this.mProgram = GLUtil.createProgram("attribute vec4 aPosition;\nattribute vec4 aTexCoord;\nuniform   mat4 uPosMtx;\nuniform   mat4 uTexRotateMtx;\nvarying   vec2 vTexCoord;\nvoid main() {\n  gl_Position = uPosMtx * aPosition;\n  vTexCoord   = (uTexRotateMtx * aTexCoord).xy;\n}\n", "precision mediump float;\nuniform sampler2D uSampler;\nvarying vec2      vTexCoord;\nvoid main() {\n  gl_FragColor = texture2D(uSampler, vTexCoord);\n}\n");
        this.maPositionHandle = GLES20.glGetAttribLocation(this.mProgram, "aPosition");
        this.maTexCoordHandle = GLES20.glGetAttribLocation(this.mProgram, "aTexCoord");
        this.muPosMtxHandle = GLES20.glGetUniformLocation(this.mProgram, "uPosMtx");
        this.muTexRotateMtxHandle = GLES20.glGetUniformLocation(this.mProgram, "uTexRotateMtx");
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
        this.mTexRotateMtx = GLUtil.createIdentityMtx();
    }

    private void updateEGLSurface() {
        synchronized (this.mReadyFence) {
            this.mUpdateEGLSurfaceSync.close();
            if (this.mScreenHandler != null) {
                this.mScreenHandler.removeMessages(3);
                this.mScreenHandler.obtainMessage(2).sendToTarget();
            }
            this.mUpdateEGLSurfaceSync.block();
        }
    }

    private void initVertexData(float f, float f2) {
        Matrix.translateM(this.mTexRotateMtx, 0, this.mTexRotateMtx, 0, 0.5f, 0.5f, 0.0f);
        Matrix.rotateM(this.mTexRotateMtx, 0, -this.mTextureRotation, 0.0f, 0.0f, 1.0f);
        Matrix.translateM(this.mTexRotateMtx, 0, -0.5f, -0.5f, 0.0f);
        this.mVtxBuf = createFloatBuffer(this.mVtxBuf, GLUtil.createFullSquareVtx(f, f2));
        Matrix.multiplyMM(this.mPosMtx, 0, this.mMMtx, 0, this.mVMtx, 0);
        Matrix.multiplyMM(this.mPosMtx, 0, this.mPMtx, 0, this.mPosMtx, 0);
    }
}
