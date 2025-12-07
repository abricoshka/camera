package com.mediatek.camera.p005v2.stream.pip.pipwrapping;

import android.app.Activity;
import android.hardware.display.DisplayManager;
import android.opengl.EGL14;
import android.opengl.EGLContext;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.os.ConditionVariable;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Surface;
import com.mediatek.camera.debug.LogHelper;
import java.nio.Buffer;
import java.nio.FloatBuffer;

/* loaded from: classes.dex */
public class ScreenRenderer extends Renderer implements Runnable {
    private static final LogHelper.Tag TAG = new LogHelper.Tag(ScreenRenderer.class.getSimpleName());
    private DisplayManager.DisplayListener mDisplayListener;
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
        this.mDisplayListener = new DisplayManager.DisplayListener() { // from class: com.mediatek.camera.v2.stream.pip.pipwrapping.ScreenRenderer.1
            @Override // android.hardware.display.DisplayManager.DisplayListener
            public void onDisplayAdded(int i) {
            }

            @Override // android.hardware.display.DisplayManager.DisplayListener
            public void onDisplayChanged(int i) {
                LogHelper.m23d(ScreenRenderer.TAG, "onDisplayChanged displayId = " + i);
                ScreenRenderer.this.checkDisplayRotation();
            }

            @Override // android.hardware.display.DisplayManager.DisplayListener
            public void onDisplayRemoved(int i) {
            }
        };
        this.mEditTexRenderer = new ResourceRenderer(activity);
        this.mPressedTexRenderer = new ResourceRenderer(activity);
        this.mTexCoordBuf = createFloatBuffer(this.mTexCoordBuf, GLUtil.createTexCoord());
        new Thread(this, "PIP-ScreenRenderer").start();
        ((DisplayManager) activity.getSystemService("display")).registerDisplayListener(this.mDisplayListener, null);
    }

    protected void setSurface(Surface surface) {
        LogHelper.m23d(TAG, "setSurface surface = " + surface);
        this.mIsEGLSurfaceReady = false;
        if (surface == null) {
            throw new RuntimeException("ScreenRenderer setSurface to null!!!!!");
        }
        this.mPreviewSurface = surface;
        waitRendererThreadActive();
        updateEGLSurface();
        checkDisplayRotation();
        this.mIsEGLSurfaceReady = true;
    }

    public void init() {
        LogHelper.m23d(TAG, "init: " + this);
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

    public void onActivityPause() {
        synchronized (this.mReadyFence) {
            if (this.mScreenHandler != null && this.mPreviewEGLSurface != null) {
                this.mScreenHandler.removeCallbacksAndMessages(null);
                this.mReleaseScreenSurfaceSync.close();
                this.mScreenHandler.obtainMessage(4).sendToTarget();
                this.mReleaseScreenSurfaceSync.block(2000L);
            }
        }
    }

    @Override // com.mediatek.camera.p005v2.stream.pip.pipwrapping.Renderer
    public void setRendererSize(int i, int i2) {
        LogHelper.m23d(TAG, "setRendererSize width = " + i + " height = " + i2);
        if (i != getRendererWidth() || i2 != getRendererHeight()) {
            this.mIsEGLSurfaceReady = false;
        }
        super.setRendererSize(i, i2);
    }

    @Override // com.mediatek.camera.p005v2.stream.pip.pipwrapping.Renderer
    public void release() {
        LogHelper.m23d(TAG, "release: " + this);
        synchronized (this.mReadyFence) {
            if (this.mScreenHandler != null) {
                this.mScreenHandler.removeCallbacksAndMessages(null);
                this.mReleaseScreenSurfaceSync.close();
                this.mScreenHandler.obtainMessage(1).sendToTarget();
                this.mReleaseScreenSurfaceSync.block(2000L);
            }
        }
        super.setRendererSize(-1, -1);
        ((DisplayManager) getActivity().getSystemService("display")).unregisterDisplayListener(this.mDisplayListener);
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

    @Override // java.lang.Runnable
    public void run() {
        Looper.prepare();
        synchronized (this.mReadyFence) {
            LogHelper.m23d(TAG, "Screen renderer thread started!");
            this.mScreenHandler = new ScreenHandler(this, null);
            this.mReady = true;
            this.mReadyFence.notify();
        }
        Looper.loop();
        LogHelper.m23d(TAG, "Screen renderer thread exiting!");
        synchronized (this.mReadyFence) {
            this.mRunning = false;
            this.mReady = false;
            this.mScreenHandler = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void checkDisplayRotation() {
        if (this.mPreviewEGLSurface != null) {
            this.mRenderTexWidth = this.mPreviewEGLSurface.getWidth();
            this.mRenderTexHeight = this.mPreviewEGLSurface.getHeight();
            this.mTextureRotation = getDisplayRotation(getActivity());
            updateRendererSize(this.mRenderTexWidth, this.mRenderTexHeight);
        }
    }

    private void waitRendererThreadActive() {
        synchronized (this.mReadyFence) {
            if (this.mRunning) {
                LogHelper.m23d(TAG, "screen renderer already running!");
                return;
            }
            this.mRunning = true;
            while (!this.mReady) {
                try {
                    LogHelper.m23d(TAG, "wait for screen renderer thread ready, current mReady = " + this.mReady);
                    this.mReadyFence.wait();
                } catch (InterruptedException e) {
                }
            }
            this.mDrawLockableConditionVariable.open();
        }
    }

    private void updateRendererSize(int i, int i2) {
        LogHelper.m23d(TAG, "updateRendererSize width = " + i + " height = " + i2);
        resetMatrix();
        Matrix.orthoM(this.mPMtx, 0, 0.0f, i, 0.0f, i2, -1.0f, 1.0f);
        initVertexData(i, i2);
        this.mEditTexSize = Math.min(i, i2) / 10;
        int iMin = Math.min(i, i2);
        int iMax = Math.max(i, i2);
        if (this.mTextureRotation % 180 == 0) {
            this.mEditTexRenderer.setRendererSize(iMin, iMax, true);
            this.mPressedTexRenderer.setRendererSize(iMin, iMax, true);
        } else {
            this.mEditTexRenderer.setRendererSize(iMax, iMin, true);
            this.mPressedTexRenderer.setRendererSize(iMax, iMin, true);
        }
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
                    try {
                        doDraw((AnimationRect) message.obj, message.arg1, message.arg2 > 0);
                        break;
                    } catch (IllegalStateException e) {
                        LogHelper.m24e(ScreenRenderer.TAG, "gl error, ignore this doDraw pass");
                        return;
                    }
                case 4:
                    releaseEglSurface();
                    ScreenRenderer.this.mReleaseScreenSurfaceSync.open();
                    break;
            }
        }

        private void doSetupEGLSurface() {
            LogHelper.m23d(ScreenRenderer.TAG, "handleSetupSurface  mEglCore = " + ScreenRenderer.this.mEglCore + " mPreviewEGLSurface = " + ScreenRenderer.this.mPreviewEGLSurface + " mPreviewSurface = " + ScreenRenderer.this.mPreviewSurface);
            if (ScreenRenderer.this.mEglCore == null) {
                ScreenRenderer.this.mEglCore = new EglCore(ScreenRenderer.this.mSharedEGLContext, 2);
            }
            if (ScreenRenderer.this.mPreviewEGLSurface == null) {
                ScreenRenderer.this.mPreviewEGLSurface = new WindowSurface(ScreenRenderer.this.mEglCore, ScreenRenderer.this.mPreviewSurface);
                ScreenRenderer.this.mPreviewEGLSurface.makeCurrent();
            }
        }

        private void doUpdateEGLSurface() {
            LogHelper.m23d(ScreenRenderer.TAG, "updateEGLSurface mPreviewEGLSurface = " + ScreenRenderer.this.mPreviewEGLSurface);
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
            LogHelper.m23d(ScreenRenderer.TAG, "doReleaseSurface");
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
            LogHelper.m23d(ScreenRenderer.TAG, "releaseEglSurface");
            if (ScreenRenderer.this.mPreviewEGLSurface != null) {
                ScreenRenderer.this.mPreviewEGLSurface.makeNothingCurrent();
                ScreenRenderer.this.mPreviewEGLSurface.releaseEglSurface();
                ScreenRenderer.this.mPreviewEGLSurface = null;
            }
        }

        private void doDraw(AnimationRect animationRect, int i, boolean z) {
            System.currentTimeMillis();
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
                ScreenRenderer.this.mEditTexRenderer.draw(animationRect.getRightBottom()[0], animationRect.getRightBottom()[1], ScreenRenderer.this.mEditTexSize, null);
            }
            if (animationRect != null && z) {
                ScreenRenderer.this.mTopGraphicPositionBuf = ScreenRenderer.this.createFloatBuffer(ScreenRenderer.this.mTopGraphicPositionBuf, GLUtil.createTopRightRect(animationRect));
                ScreenRenderer.this.mTopGraphicPositionBuf.position(0);
                ScreenRenderer.this.mPressedTexRenderer.draw(0.0f, 0.0f, 0.0f, ScreenRenderer.this.mTopGraphicPositionBuf);
            }
            ScreenRenderer.this.mPreviewEGLSurface.swapBuffers();
            ScreenRenderer.this.mDrawLockableConditionVariable.open();
            ScreenRenderer.this.debugFrameRate(ScreenRenderer.TAG);
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
