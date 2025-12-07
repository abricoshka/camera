package com.mediatek.camera.addition.effect;

import android.graphics.ImageFormat;
import android.graphics.Point;
import android.hardware.Camera;
import android.os.ConditionVariable;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.Trace;
import android.view.Surface;
import com.mediatek.camera.ICameraContext;
import com.mediatek.camera.platform.ICameraDeviceManager;
import com.mediatek.camera.util.Log;
import com.mediatek.matrixeffect.MatrixEffect;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class Effect {
    private ICameraContext mCameraContext;
    private EffectHandler mHandler;
    private long mInputStartTime;
    private Listener mListener;
    private MatrixEffect mMatrixEffect;
    private int mCacheIndex = 0;
    private int mInputFrames = 0;
    private int mCurrentNumOfProcess = 0;
    private int mNumOfDropFrame = 6;
    private int[] mEffectIndexs = new int[12];
    private Surface[] mSurfaceArray = new Surface[12];
    private byte[][] mEffectsBuffers = new byte[36][];
    private byte[][] mPreviewCallbackBuffers = new byte[3][];
    private ArrayList<byte[]> mCacheBuffer = new ArrayList<>();
    private boolean mRealsed = true;
    private boolean mRegisterBufferDone = false;
    private ConditionVariable mReleaseCondition = new ConditionVariable();
    private Camera.PreviewCallback mPreviewCallback = new Camera.PreviewCallback() { // from class: com.mediatek.camera.addition.effect.Effect.1
        @Override // android.hardware.Camera.PreviewCallback
        public void onPreviewFrame(byte[] bArr, Camera camera) {
            ICameraDeviceManager cameraDeviceManager = Effect.this.mCameraContext.getCameraDeviceManager();
            ICameraDeviceManager.ICameraDevice cameraDevice = cameraDeviceManager.getCameraDevice(cameraDeviceManager.getCurrentCameraId());
            Log.m31d("Effect", "[onPreviewFrame]mCurrentNumOfProcess:" + Effect.this.mCurrentNumOfProcess + ",mNumOfDropFrame:" + Effect.this.mNumOfDropFrame + ", mRegisterBufferDone:" + Effect.this.mRegisterBufferDone);
            if (Effect.this.mInputFrames == 0) {
                Effect.this.mInputStartTime = System.currentTimeMillis();
            }
            Effect.this.mInputFrames++;
            if (Effect.this.mInputFrames % 20 == 0) {
                Log.m31d("Effect", "[onPreviewFrame]pv callback Fps:" + (20000 / (System.currentTimeMillis() - Effect.this.mInputStartTime)));
                Effect.this.mInputStartTime = System.currentTimeMillis();
            }
            if (Effect.this.mCurrentNumOfProcess == 2 && Effect.this.mHandler != null) {
                Effect.this.mHandler.removeMessages(103);
                Effect.this.mCurrentNumOfProcess = 1;
                Log.m31d("Effect", "dropFrame");
            }
            if (Effect.this.mCurrentNumOfProcess < 2 && (!Effect.this.isNeedDropFrame()) && Effect.this.mRegisterBufferDone) {
                Effect.this.processEffect(bArr);
            } else if (Effect.this.isNeedDropFrame()) {
                Effect.this.mNumOfDropFrame++;
            }
            if (cameraDevice != null) {
                cameraDevice.addCallbackBuffer(bArr);
            }
        }
    };
    private MatrixEffect.EffectsCallback mEffectsCallback = new MatrixEffect.EffectsCallback() { // from class: com.mediatek.camera.addition.effect.Effect.2
        public void onEffectsDone() {
            Log.m31d("Effect", "[onEffectsDone]...");
            if (Effect.this.mListener != null) {
                Effect.this.mListener.onEffectsDone();
            }
        }
    };

    public interface Listener {
        void onEffectsDone();
    }

    public Effect(ICameraContext iCameraContext) {
        Log.m31d("Effect", "[Effect]constructor...");
        this.mCameraContext = iCameraContext;
    }

    public void setListener(Listener listener) {
        this.mListener = listener;
    }

    public void onInitialize() {
        Log.m31d("Effect", "[onInitialize]mHandler:" + this.mHandler);
        this.mMatrixEffect = MatrixEffect.getInstance();
        if (this.mHandler == null) {
            HandlerThread handlerThread = new HandlerThread("draw buffer handler thread", 10);
            handlerThread.start();
            this.mHandler = new EffectHandler(handlerThread.getLooper());
        }
        for (int i = 0; i < this.mEffectIndexs.length; i++) {
            this.mEffectIndexs[i] = -1;
        }
        int bufferSize = getBufferSize();
        if (this.mCacheBuffer.size() == 0) {
            for (int i2 = 0; i2 < 3; i2++) {
                this.mCacheBuffer.add(new byte[bufferSize]);
            }
        }
        this.mCacheIndex = 0;
        this.mMatrixEffect.setCallback(this.mEffectsCallback);
        this.mHandler.sendEmptyMessage(100);
    }

    public void onSurfaceAvailable(Surface surface, int i, int i2, int i3) {
        this.mSurfaceArray[i3] = surface;
        if (!this.mRegisterBufferDone) {
            Point pointResizeSurfaceBuffer = resizeSurfaceBuffer(i, i2);
            int i4 = pointResizeSurfaceBuffer.x;
            int i5 = pointResizeSurfaceBuffer.y;
            int i6 = ((i4 * i5) * 3) / 2;
            for (int i7 = 0; i7 < 36; i7++) {
                if (this.mEffectsBuffers[i7] == null) {
                    this.mEffectsBuffers[i7] = new byte[i6];
                }
            }
            Log.m31d("Effect", "[onSurfaceAvailable]Register buffer size, bufferWidth:" + i4 + ", bufferHeight:" + i5);
            if (this.mHandler != null) {
                this.mHandler.obtainMessage(101, i4, i5).sendToTarget();
                this.mRegisterBufferDone = true;
            }
        }
        if (this.mHandler != null) {
            this.mHandler.obtainMessage(102, i3, 0, surface).sendToTarget();
        }
    }

    public void onUpdateEffect(int i, int i2) {
        this.mEffectIndexs[i] = i2;
    }

    public void onReceivePreviewFrame(boolean z) {
        Log.m31d("Effect", "[onReceivePreviewFrame]receive:" + z);
        ICameraDeviceManager cameraDeviceManager = this.mCameraContext.getCameraDeviceManager();
        ICameraDeviceManager.ICameraDevice cameraDevice = cameraDeviceManager.getCameraDevice(cameraDeviceManager.getCurrentCameraId());
        if (!z) {
            if (this.mHandler != null) {
                this.mHandler.removeMessages(103);
            }
            if (cameraDevice != null) {
                cameraDevice.setPreviewCallbackWithBuffer(null);
                return;
            }
            return;
        }
        int bufferSize = getBufferSize();
        for (int i = 0; i < this.mPreviewCallbackBuffers.length; i++) {
            if (this.mPreviewCallbackBuffers[i] == null) {
                this.mPreviewCallbackBuffers[i] = new byte[bufferSize];
            }
            cameraDevice.addCallbackBuffer(this.mPreviewCallbackBuffers[i]);
        }
        cameraDevice.setPreviewCallbackWithBuffer(this.mPreviewCallback);
        this.mNumOfDropFrame = 0;
    }

    public void onRelease() {
        Log.m31d("Effect", "[onRelease]mRealsed:" + this.mRealsed + ", mHandler:" + this.mHandler);
        if (!this.mRealsed && this.mHandler != null) {
            if (this.mHandler.hasMessages(103)) {
                this.mHandler.removeMessages(103);
            }
            this.mHandler.sendEmptyMessage(104);
            Log.m31d("Effect", "waiting for release effect in onRelease()");
            this.mReleaseCondition.block();
            this.mReleaseCondition.close();
            this.mHandler.getLooper().quit();
            this.mHandler = null;
        }
    }

    public void release() {
        Log.m31d("Effect", "[release], mRealsed:" + this.mRealsed + ", mHandler:" + this.mHandler);
        if (this.mRealsed) {
            return;
        }
        if (this.mHandler != null) {
            this.mHandler.sendEmptyMessage(104);
            Log.m31d("Effect", "waiting for release effect in release()");
            this.mReleaseCondition.block();
            this.mReleaseCondition.close();
            this.mHandler.getLooper().quit();
            this.mHandler = null;
        }
        this.mMatrixEffect = null;
    }

    private int getBufferSize() {
        ICameraDeviceManager cameraDeviceManager = this.mCameraContext.getCameraDeviceManager();
        ICameraDeviceManager.ICameraDevice cameraDevice = cameraDeviceManager.getCameraDevice(cameraDeviceManager.getCurrentCameraId());
        Camera.Size previewSize = cameraDevice.getParameters().getPreviewSize();
        int previewFormat = cameraDevice.getParameters().getPreviewFormat();
        Log.m31d("Effect", "[getBufferSize]size.width = " + previewSize.width + " size.height = " + previewSize.height + ", PreviewFormat:" + previewFormat);
        return (ImageFormat.getBitsPerPixel(previewFormat) * (previewSize.height * previewSize.width)) / 8;
    }

    private Point resizeSurfaceBuffer(int i, int i2) {
        Log.m31d("Effect", "[resizeSurfaceBuffer], input size, width:" + i + ", height:" + i2);
        int i3 = 2;
        int i4 = i2;
        int i5 = i;
        while (true) {
            if (i5 > 480 || i4 > 320) {
                i5 = i / i3;
                i4 = i2 / i3;
                i3++;
            } else {
                int i6 = (i5 / 32) * 32;
                int i7 = (i4 / 16) * 16;
                Point point = new Point(i6, i7);
                Log.m31d("Effect", "[resizeSurfaceBuffer], output size, width:" + i6 + ",height:" + i7);
                return point;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isNeedDropFrame() {
        return this.mNumOfDropFrame < 6;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void processEffect(byte[] bArr) {
        if (bArr == null) {
            Log.m36w("Effect", "[processEffect] data is null,return!");
            return;
        }
        byte[] bArr2 = this.mCacheBuffer.get(this.mCacheIndex);
        if (bArr.length != bArr2.length) {
            Log.m36w("Effect", "[processEffect]preview buffer size is larger,return!");
            return;
        }
        System.arraycopy(bArr, 0, bArr2, 0, bArr.length);
        this.mCacheIndex = (this.mCacheIndex + 1) % 3;
        this.mCurrentNumOfProcess++;
        if (this.mHandler != null) {
            this.mHandler.sendMessage(this.mHandler.obtainMessage(103, this.mCacheIndex, 0, bArr2));
        }
    }

    private class EffectHandler extends Handler {
        public EffectHandler(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            Log.m31d("Effect", "[handleMessage]msg.what = " + message.what + ",mRealsed = " + Effect.this.mRealsed);
            switch (message.what) {
                case 100:
                    ICameraDeviceManager cameraDeviceManager = Effect.this.mCameraContext.getCameraDeviceManager();
                    Camera.Size previewSize = cameraDeviceManager.getCameraDevice(cameraDeviceManager.getCurrentCameraId()).getParameters().getPreviewSize();
                    Effect.this.mMatrixEffect.initialize(previewSize.width, previewSize.height, 12, 11);
                    Effect.this.mRealsed = false;
                    break;
                case 101:
                    if (!Effect.this.mRealsed) {
                        Effect.this.mMatrixEffect.setBuffers(message.arg1, message.arg2, Effect.this.mEffectsBuffers);
                        break;
                    }
                    break;
                case 102:
                    if (!Effect.this.mRealsed) {
                        Effect.this.mMatrixEffect.setSurface((Surface) message.obj, message.arg1);
                        break;
                    }
                    break;
                case 103:
                    long jCurrentTimeMillis = System.currentTimeMillis();
                    Trace.traceBegin(8L, "process frame");
                    Effect.this.mMatrixEffect.process((byte[]) message.obj, Effect.this.mEffectIndexs);
                    Trace.traceEnd(8L);
                    Effect effect = Effect.this;
                    effect.mCurrentNumOfProcess--;
                    Log.m31d("Effect", "process_time:" + (System.currentTimeMillis() - jCurrentTimeMillis));
                    break;
                case 104:
                    Effect.this.releaseEffect();
                    break;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void releaseEffect() {
        Log.m31d("Effect", "[releaseEffect]mRealsed:" + this.mRealsed);
        if (!this.mRealsed) {
            this.mMatrixEffect.setCallback((MatrixEffect.EffectsCallback) null);
            this.mMatrixEffect.release();
            this.mRealsed = true;
            this.mRegisterBufferDone = false;
            for (int i = 0; i < this.mEffectsBuffers.length; i++) {
                this.mEffectsBuffers[i] = null;
            }
            for (int i2 = 0; i2 < this.mPreviewCallbackBuffers.length; i2++) {
                this.mPreviewCallbackBuffers[i2] = null;
            }
            for (int i3 = 0; i3 < this.mSurfaceArray.length; i3++) {
                this.mSurfaceArray[i3] = null;
            }
            this.mCacheBuffer.clear();
        }
        this.mReleaseCondition.open();
    }
}
