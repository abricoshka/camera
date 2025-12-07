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
public class RecorderRenderer extends Renderer implements Runnable {
    private EglCore mEglCore;
    private EncoderHandler mEncoderHandler;
    private boolean mIsCanRecordFirstFrame;
    private boolean mIsStopRecrodingReceived;
    private float[] mMMtx;
    private float[] mPMtx;
    private float[] mPosMtx;
    private int mProgram;
    private boolean mReady;
    private Object mReadyFence;
    private WindowSurface mRecordingEGLSurface;
    private Surface mRecordingSurface;
    private ConditionVariable mRenderThreadBlockVar;
    private float[] mRotateMtx;
    private boolean mRunning;
    private EGLContext mSharedEGLContext;
    private ConditionVariable mStartConditaionVariable;
    private ConditionVariable mStopConditionVariable;
    private int mSwapVideoBufferCount;
    private FloatBuffer mTexCoordBuf;
    private float[] mVMtx;
    private FloatBuffer mVtxBuf;
    private int maPositionHandle;
    private int maTexCoordHandle;
    private int muPosMtxHandle;
    private int muSamplerHandle;
    private int muTexRotateMtxHandle;

    public RecorderRenderer(Activity activity) {
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
        this.mReadyFence = new Object();
        this.mIsCanRecordFirstFrame = false;
        this.mIsStopRecrodingReceived = false;
        this.mSwapVideoBufferCount = 0;
        this.mStartConditaionVariable = new ConditionVariable();
        this.mStopConditionVariable = new ConditionVariable();
        this.mRenderThreadBlockVar = new ConditionVariable();
        new Thread(this, "PIP-RecorderRenderer").start();
    }

    public void init() {
        Log.m31d("RecorderRenderer", "init");
        initGL();
        this.mSharedEGLContext = EGL14.eglGetCurrentContext();
        this.mTexCoordBuf = createFloatBuffer(this.mTexCoordBuf, GLUtil.createTexCoord());
    }

    public void setRendererSize(int i, int i2, int i3) {
        Log.m31d("RecorderRenderer", "setRendererSize width = " + i + " height = " + i2);
        if (i == getRendererWidth() && i2 == getRendererHeight()) {
            return;
        }
        super.setRendererSize(i, i2);
        resetMatrix();
        Matrix.orthoM(this.mPMtx, 0, 0.0f, i, 0.0f, i2, -1.0f, 1.0f);
        initVertexData(getRendererWidth(), getRendererHeight(), i3);
    }

    public void setRecrodingSurface(Surface surface) {
        Log.m31d("RecorderRenderer", "setRecrodingSurface surface = " + surface);
        if (surface == null) {
            throw new NullPointerException("setRecrodingSurface, but surface is null!!!");
        }
        this.mRecordingSurface = surface;
        synchronized (this.mReadyFence) {
            if (this.mRunning) {
                Log.m36w("RecorderRenderer", "Encoder thread already running");
                return;
            }
            this.mRunning = true;
            while (!this.mReady) {
                try {
                    Log.m31d("RecorderRenderer", "wait for recording thread ready, current mReady = " + this.mReady);
                    this.mReadyFence.wait();
                } catch (InterruptedException e) {
                }
            }
            this.mEncoderHandler.obtainMessage(4).sendToTarget();
        }
    }

    public void startRecording() {
        Log.m31d("RecorderRenderer", "startRecording begin mEncoderHandler = " + this.mEncoderHandler);
        synchronized (this.mReadyFence) {
            this.mStartConditaionVariable.block(200L);
            if (this.mEncoderHandler != null) {
                this.mEncoderHandler.sendMessage(this.mEncoderHandler.obtainMessage(0));
            }
        }
        Log.m31d("RecorderRenderer", "startRecording end");
    }

    public void draw(int i, long j) {
        synchronized (this.mReadyFence) {
            if (!this.mReady || (!this.mIsCanRecordFirstFrame)) {
                Log.m36w("RecorderRenderer", "mReady = " + this.mReady + " mIsCanRecordFirstFrame = " + this.mIsCanRecordFirstFrame);
                return;
            }
            if (this.mEncoderHandler != null) {
                this.mRenderThreadBlockVar.close();
                this.mEncoderHandler.obtainMessage(2, (int) (j >> 32), (int) j, Integer.valueOf(i)).sendToTarget();
                this.mRenderThreadBlockVar.block();
                if (this.mIsStopRecrodingReceived && this.mSwapVideoBufferCount > 1) {
                    this.mReady = false;
                    this.mIsStopRecrodingReceived = false;
                    this.mEncoderHandler.sendEmptyMessage(1);
                    this.mEncoderHandler.sendEmptyMessage(5);
                    this.mStopConditionVariable.open();
                }
            }
        }
    }

    public void stopRecording() {
        Log.m31d("RecorderRenderer", "stopRecording mReady = " + this.mReady + " mIsStopRecrodingReceived = " + this.mIsStopRecrodingReceived);
        this.mStopConditionVariable.close();
        synchronized (this.mReadyFence) {
            this.mReady = true;
            this.mIsStopRecrodingReceived = true;
        }
        this.mStopConditionVariable.block(3000L);
    }

    @Override // java.lang.Runnable
    public void run() {
        Log.m31d("RecorderRenderer", "run");
        synchronized (this.mReadyFence) {
            Looper.prepare();
            Log.m31d("RecorderRenderer", "new EncoderHandler()");
            this.mEncoderHandler = new EncoderHandler();
            this.mReady = true;
            this.mReadyFence.notify();
        }
        Looper.loop();
        Log.m31d("RecorderRenderer", "Encoder thread exiting");
        synchronized (this.mReadyFence) {
            this.mRunning = false;
            this.mReady = false;
            this.mEncoderHandler = null;
        }
    }

    public void releaseSurface() {
        Log.m31d("RecorderRenderer", "releaseSurface");
    }

    private void initGL() {
        GLUtil.checkGlError("initGL_Start");
        if (this.mProgram != -1) {
            return;
        }
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

    private void initVertexData(float f, float f2, int i) {
        this.mVtxBuf = createFloatBuffer(this.mVtxBuf, GLUtil.createFullSquareVtx(f, f2));
        Matrix.multiplyMM(this.mPosMtx, 0, this.mMMtx, 0, this.mVMtx, 0);
        Matrix.multiplyMM(this.mPosMtx, 0, this.mPMtx, 0, this.mPosMtx, 0);
        if (f < f2) {
            if (i % 180 == 0) {
                i += 90;
            }
            Matrix.translateM(this.mRotateMtx, 0, this.mRotateMtx, 0, 0.5f, 0.5f, 0.0f);
            Matrix.rotateM(this.mRotateMtx, 0, -i, 0.0f, 0.0f, 1.0f);
            Matrix.translateM(this.mRotateMtx, 0, -0.5f, -0.5f, 0.0f);
        }
    }

    private void resetMatrix() {
        this.mPosMtx = GLUtil.createIdentityMtx();
        this.mPMtx = GLUtil.createIdentityMtx();
        this.mVMtx = GLUtil.createIdentityMtx();
        this.mMMtx = GLUtil.createIdentityMtx();
        this.mRotateMtx = GLUtil.createIdentityMtx();
    }

    private class EncoderHandler extends Handler {
        public EncoderHandler() {
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            Log.m31d("RecorderRenderer", "handleMessage(" + message + ")");
            switch (message.what) {
                case 0:
                    RecorderRenderer.this.mIsCanRecordFirstFrame = true;
                    break;
                case 2:
                    doDraw(((Integer) message.obj).intValue(), (message.arg1 << 32) | (message.arg2 & 4294967295L));
                    break;
                case 4:
                    doUpdateSharedContext();
                    doStartVideoRecording(RecorderRenderer.this.mRecordingSurface);
                    break;
                case 5:
                    if (RecorderRenderer.this.mRecordingEGLSurface != null) {
                        RecorderRenderer.this.mRecordingEGLSurface.makeNothingCurrent();
                        RecorderRenderer.this.mRecordingEGLSurface.release();
                        RecorderRenderer.this.mRecordingEGLSurface = null;
                        Log.m31d("RecorderRenderer", "RecorderRendere swap buffer total count : " + RecorderRenderer.this.mSwapVideoBufferCount);
                    }
                    if (RecorderRenderer.this.mEglCore != null) {
                        RecorderRenderer.this.mEglCore.release();
                        RecorderRenderer.this.mEglCore = null;
                    }
                    Looper looperMyLooper = Looper.myLooper();
                    if (looperMyLooper != null) {
                        looperMyLooper.quit();
                        break;
                    }
                    break;
            }
        }

        private void doUpdateSharedContext() {
            Log.m31d("RecorderRenderer", "doUpdateSharedContext");
            RecorderRenderer.this.mEglCore = new EglCore(RecorderRenderer.this.mSharedEGLContext, 3, new int[]{3, 1, 842094169});
        }

        private void doStartVideoRecording(Surface surface) {
            Log.m31d("RecorderRenderer", "doStartVideoRecording");
            RecorderRenderer.this.mRecordingEGLSurface = new WindowSurface(RecorderRenderer.this.mEglCore, surface);
            RecorderRenderer.this.mRecordingEGLSurface.makeCurrent();
        }

        private void doDraw(int i, long j) {
            if (RecorderRenderer.this.mRecordingEGLSurface == null) {
                RecorderRenderer.this.mRenderThreadBlockVar.open();
                return;
            }
            GLUtil.checkGlError("RecordDraw_Start");
            GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
            GLES20.glClear(16640);
            GLES20.glUseProgram(RecorderRenderer.this.mProgram);
            RecorderRenderer.this.mVtxBuf.position(0);
            GLES20.glVertexAttribPointer(RecorderRenderer.this.maPositionHandle, 3, 5126, false, 12, (Buffer) RecorderRenderer.this.mVtxBuf);
            RecorderRenderer.this.mTexCoordBuf.position(0);
            GLES20.glVertexAttribPointer(RecorderRenderer.this.maTexCoordHandle, 2, 5126, false, 8, (Buffer) RecorderRenderer.this.mTexCoordBuf);
            GLES20.glEnableVertexAttribArray(RecorderRenderer.this.maPositionHandle);
            GLES20.glEnableVertexAttribArray(RecorderRenderer.this.maTexCoordHandle);
            GLES20.glUniformMatrix4fv(RecorderRenderer.this.muPosMtxHandle, 1, false, RecorderRenderer.this.mPosMtx, 0);
            GLES20.glUniformMatrix4fv(RecorderRenderer.this.muTexRotateMtxHandle, 1, false, RecorderRenderer.this.mRotateMtx, 0);
            GLES20.glUniform1i(RecorderRenderer.this.muSamplerHandle, 0);
            GLES20.glActiveTexture(33984);
            GLES20.glBindTexture(3553, i);
            GLES20.glDrawArrays(5, 0, 6);
            RecorderRenderer.this.mRecordingEGLSurface.setPresentationTime(j);
            RecorderRenderer.this.mSwapVideoBufferCount++;
            RecorderRenderer.this.mRenderThreadBlockVar.open();
            RecorderRenderer.this.mRecordingEGLSurface.swapBuffers();
            GLUtil.checkGlError("RecordDraw_End");
            RecorderRenderer.this.debugFrameRate("RecorderRenderer");
        }
    }
}
