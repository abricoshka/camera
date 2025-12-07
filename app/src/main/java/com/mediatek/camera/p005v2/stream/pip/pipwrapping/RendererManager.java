package com.mediatek.camera.p005v2.stream.pip.pipwrapping;

import android.app.Activity;
import android.graphics.SurfaceTexture;
import android.opengl.Matrix;
import android.os.ConditionVariable;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.FrameMetricsAggregator;
import android.util.Size;
import android.view.Surface;
import com.mediatek.camera.debug.LogHelper;
import java.util.HashMap;
import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;
import junit.framework.Assert;

/* loaded from: classes.dex */
public class RendererManager {
    private static final LogHelper.Tag TAG = new LogHelper.Tag(RendererManager.class.getSimpleName());
    private final Activity mActivity;
    private BottomGraphicRenderer mBottomGraphicRenderer;
    private CaptureRenderer mCaptureRenderer;
    private Surface mCaptureSurface;
    private int mCurrentOrientation;
    private EGL10 mEgl;
    private EGLConfig mEglConfig;
    private EGLContext mEglContext;
    private EGLDisplay mEglDisplay;
    private EglHandler mEglHandler;
    private EGLSurface mEglSurface;
    private HandlerThread mEglThread;
    private SurfaceTextureWrapper mMainCap_StWrapper;
    private SurfaceTextureWrapper mMainPv_StWrapper;
    private FrameBuffer mPictureFb;
    private FrameBuffer mPreviewFrameBuffer;
    private RecorderRenderer mRecorderRenderer;
    private final RendererCallback mRendererCallback;
    private ScreenRenderer mScreenRenderer;
    private SurfaceTextureWrapper mSubCap_StWrapper;
    private SurfaceTextureWrapper mSubPv_StWrapper;
    private Handler mSurfaceTextureHandler;
    private TopGraphicRenderer mTopGraphicRenderer;
    private Object mRenderLock = new Object();
    private ConditionVariable mEglThreadBlockVar = new ConditionVariable();
    private HandlerThread mStFrameListener = new HandlerThread("PIP-StFrameListener");
    private int mRendererTexWidth = -1;
    private int mRendererTexHeight = -1;
    private boolean isBottomHasHighFrameRate = true;
    private AnimationRect mPreviewTopGraphicRect = null;
    private int mBackTempResId = 0;
    private int mFrontTempResId = 0;
    private int mHighlightTempResId = 0;
    private int mEditResId = 0;
    private boolean mPipSwitched = false;
    private boolean mBlockingForPvSizeChange = false;
    private boolean mNeedNotifyFirstFrameForSurfaceChanged = true;

    public interface RendererCallback {
        void onFristFrameAvailable(long j);
    }

    public RendererManager(Activity activity, RendererCallback rendererCallback) {
        this.mActivity = activity;
        this.mRendererCallback = rendererCallback;
    }

    public synchronized void init() {
        LogHelper.m23d(TAG, "[init]+ mEglHandler = " + this.mEglHandler);
        if (this.mEglHandler != null) {
            LogHelper.m26i(TAG, "[init]- ");
            return;
        }
        initializePreviewRendererThread();
        this.mEglHandler.sendMessageSync(11);
        LogHelper.m23d(TAG, "[init]- mEglHandler = " + this.mEglHandler);
    }

    public synchronized void unInit() {
        LogHelper.m23d(TAG, "[unInit]+ mEglHandler = " + this.mEglHandler);
        if (this.mEglHandler != null) {
            this.mEglHandler.removeCallbacksAndMessages(null);
            this.mEglThreadBlockVar.open();
            this.mEglHandler.sendMessageSync(10);
            Looper looper = this.mEglThread.getLooper();
            if (looper != null) {
                looper.quit();
            }
            this.mSurfaceTextureHandler = null;
            this.mEglHandler = null;
            this.mEglThread = null;
        }
        LogHelper.m23d(TAG, "[unInit]-");
    }

    public void updateEffectTemplates(int i, int i2, int i3, int i4) {
        LogHelper.m23d(TAG, "[updateEffectTemplates]+");
        if (this.mBackTempResId == i && this.mFrontTempResId == i2 && this.mHighlightTempResId == i3) {
            LogHelper.m28w(TAG, "[updateEffectTemplates]- no need to update effect");
            return;
        }
        if (this.mEglHandler != null) {
            this.mBackTempResId = i;
            this.mFrontTempResId = i2;
            this.mHighlightTempResId = i3;
            this.mEditResId = i4;
            this.mEglHandler.removeMessages(7);
            this.mEglHandler.obtainMessage(7).sendToTarget();
        }
        LogHelper.m23d(TAG, "[updateEffectTemplates]-");
    }

    public void updateTopGraphic(AnimationRect animationRect) {
        synchronized (this.mRenderLock) {
            this.mPreviewTopGraphicRect = animationRect;
        }
    }

    public void updateGSensorOrientation(int i) {
        this.mCurrentOrientation = i;
    }

    public void switchPIP() {
        LogHelper.m23d(TAG, "switchPIP , mPipSwitched:" + this.mPipSwitched);
        this.mPipSwitched = !this.mPipSwitched;
    }

    public void setPreviewSize(Size size) {
        Assert.assertNotNull(size);
        LogHelper.m23d(TAG, "[setPreviewSize]+ width = " + size.getWidth() + " height = " + size.getHeight());
        if (this.mEglHandler != null) {
            this.mBlockingForPvSizeChange = true;
            this.mEglHandler.sendMessageSync(12, size);
        }
        LogHelper.m23d(TAG, "[setPreviewSize]-");
    }

    public void setPreviewSurface(Surface surface) {
        LogHelper.m23d(TAG, "[setPreviewSurface]+");
        if (this.mEglHandler != null && surface != null) {
            this.mEglHandler.sendMessageSync(13, surface);
        }
        LogHelper.m23d(TAG, "[setPreviewSurface]-");
    }

    public void onActivityPause() {
        if (this.mEglHandler != null) {
            this.mEglHandler.sendMessageSync(18);
        }
    }

    public void onActivityResume() {
        this.mNeedNotifyFirstFrameForSurfaceChanged = true;
    }

    public SurfaceTexture getMainCamPvSt() {
        LogHelper.m23d(TAG, "getMainCamPvSt");
        return _getMainPvSurfaceTexture();
    }

    public SurfaceTexture getSubCamPvSt() {
        LogHelper.m23d(TAG, "getSubCamPvSt");
        return _getSubPvSurfaceTexture();
    }

    public void setRecordingSurface(Surface surface) {
        LogHelper.m23d(TAG, "setRecordingSurface surfacee:" + surface);
        if (surface != null && this.mEglHandler != null) {
            this.mEglHandler.sendMessageSync(14, surface);
        }
    }

    public void prepareRecording() {
        LogHelper.m23d(TAG, "[prepareRecording]+ mRecorderRenderer:" + this.mRecorderRenderer + " mEglHandler:" + this.mEglHandler);
        if (this.mRecorderRenderer == null && this.mEglHandler != null) {
            this.mEglHandler.removeMessages(5);
            this.mEglHandler.sendMessageSync(5);
            synchronized (this.mRenderLock) {
                if (this.mRecorderRenderer == null) {
                    try {
                        this.mRenderLock.wait();
                    } catch (InterruptedException e) {
                        LogHelper.m24e(TAG, "unexpected interruption");
                    }
                }
            }
        }
        LogHelper.m23d(TAG, "[prepareRecording]-");
    }

    public void startRecording() {
        LogHelper.m23d(TAG, "[startRecording]+ mRecorderRenderer:" + this.mRecorderRenderer);
        if (this.mRecorderRenderer != null) {
            this.mRecorderRenderer.startRecording();
        }
        LogHelper.m23d(TAG, "[startRecording]-");
    }

    public void stopRecording() {
        LogHelper.m23d(TAG, "[stopRecording]+ mRecorderRenderer:" + this.mRecorderRenderer + " mEglHandler:" + this.mEglHandler);
        if (this.mRecorderRenderer != null && this.mEglHandler != null) {
            this.mRecorderRenderer.stopRecording();
            this.mEglHandler.removeMessages(6);
            this.mEglHandler.sendMessageSync(6);
        }
        LogHelper.m23d(TAG, "[stopRecording]-");
    }

    public void setPictureSize(Size size, Size size2) {
        LogHelper.m23d(TAG, "[setPictureSize]+ bottomCaptureSize:" + size + " topCaptureSize:" + size2);
        Assert.assertNotNull(size);
        Assert.assertNotNull(size2);
        if (this.mEglHandler != null) {
            HashMap map = new HashMap();
            map.put("pip_bottom", size);
            map.put("pip_top", size2);
            this.mEglHandler.sendMessageSync(15, map);
        }
        LogHelper.m23d(TAG, "[setPictureSize]-");
    }

    public void setCaptureOutputSurface(Surface surface) {
        LogHelper.m23d(TAG, "setCaptureOutputSurface surface:" + surface);
        this.mCaptureSurface = surface;
    }

    public SurfaceTexture getMainCamCapSt() {
        LogHelper.m23d(TAG, "getMainCamCapSt");
        if (this.mMainCap_StWrapper == null) {
            throw new IllegalStateException("please call setPictureSize firstly!");
        }
        return this.mMainCap_StWrapper.getSurfaceTexture();
    }

    public SurfaceTexture getSubCamCapSt() {
        LogHelper.m23d(TAG, "getSubCamCapSt");
        if (this.mSubCap_StWrapper == null) {
            throw new IllegalStateException("please call setPictureSize firstly!");
        }
        return this.mSubCap_StWrapper.getSurfaceTexture();
    }

    private void initializePreviewRendererThread() {
        synchronized (this.mRenderLock) {
            this.mEglThread = new HandlerThread("PIP-PreviewRealtimeRenderer");
            this.mEglThread.start();
            Looper looper = this.mEglThread.getLooper();
            if (looper == null) {
                throw new RuntimeException("why looper is null?");
            }
            this.mEglHandler = new EglHandler(looper);
            initialize();
        }
    }

    private SurfaceTexture _getMainPvSurfaceTexture() {
        synchronized (this.mRenderLock) {
            if (this.mEglHandler == null) {
                LogHelper.m28w(TAG, "call _getSubPvSurfaceTexture after init/un-init");
                return null;
            }
            if (this.mMainPv_StWrapper == null) {
                try {
                    this.mRenderLock.wait();
                } catch (InterruptedException e) {
                    LogHelper.m24e(TAG, "unexpected interruption");
                }
            }
            LogHelper.m23d(TAG, "_getMainPvSurfaceTexture mPreviewBottomSurfaceTexture = " + this.mMainPv_StWrapper.getSurfaceTexture() + " mEglHandler = " + this.mEglHandler);
            return this.mMainPv_StWrapper.getSurfaceTexture();
        }
    }

    private SurfaceTexture _getSubPvSurfaceTexture() {
        synchronized (this.mRenderLock) {
            if (this.mEglHandler == null) {
                LogHelper.m28w(TAG, "call _getSubPvSurfaceTexture after init/un-init");
                return null;
            }
            if (this.mSubPv_StWrapper == null) {
                try {
                    this.mRenderLock.wait();
                } catch (InterruptedException e) {
                    LogHelper.m24e(TAG, "unexpected interruption");
                }
            }
            this.mBlockingForPvSizeChange = false;
            LogHelper.m23d(TAG, "_getSubPvSurfaceTexture mPreviewTopSurfaceTexture = " + this.mSubPv_StWrapper.getSurfaceTexture() + " mEglHandler = " + this.mEglHandler);
            return this.mSubPv_StWrapper.getSurfaceTexture();
        }
    }

    private class EglHandler extends Handler {
        private SurfaceTexture.OnFrameAvailableListener mBottomCamFrameAvailableListener;
        private long mBottomCamTimeStamp;
        private SurfaceTexture.OnFrameAvailableListener mTopCamFrameAvailableListener;
        private long mTopCamTimeStamp;

        public EglHandler(Looper looper) {
            super(looper);
            this.mBottomCamTimeStamp = 0L;
            this.mTopCamTimeStamp = 0L;
            this.mBottomCamFrameAvailableListener = new SurfaceTexture.OnFrameAvailableListener() { // from class: com.mediatek.camera.v2.stream.pip.pipwrapping.RendererManager.EglHandler.1
                @Override // android.graphics.SurfaceTexture.OnFrameAvailableListener
                public void onFrameAvailable(SurfaceTexture surfaceTexture) {
                    if (RendererManager.this.mMainCap_StWrapper != null && surfaceTexture == RendererManager.this.mMainCap_StWrapper.getSurfaceTexture()) {
                        LogHelper.m23d(RendererManager.TAG, "TakePicture: Main Camera onFrameAvailable ");
                        RendererManager.this.mEglHandler.obtainMessage(16, RendererManager.this.mMainCap_StWrapper).sendToTarget();
                    } else if (surfaceTexture == RendererManager.this.mMainPv_StWrapper.getSurfaceTexture()) {
                        RendererManager.this.mEglHandler.obtainMessage(17, RendererManager.this.mMainPv_StWrapper).sendToTarget();
                    }
                }
            };
            this.mTopCamFrameAvailableListener = new SurfaceTexture.OnFrameAvailableListener() { // from class: com.mediatek.camera.v2.stream.pip.pipwrapping.RendererManager.EglHandler.2
                @Override // android.graphics.SurfaceTexture.OnFrameAvailableListener
                public void onFrameAvailable(SurfaceTexture surfaceTexture) {
                    if (RendererManager.this.mSubCap_StWrapper != null && surfaceTexture == RendererManager.this.mSubCap_StWrapper.getSurfaceTexture()) {
                        LogHelper.m23d(RendererManager.TAG, "TakePicture: Sub Camera onFrameAvailable ");
                        RendererManager.this.mEglHandler.obtainMessage(16, RendererManager.this.mSubCap_StWrapper).sendToTarget();
                    } else if (surfaceTexture == RendererManager.this.mSubPv_StWrapper.getSurfaceTexture()) {
                        RendererManager.this.mEglHandler.obtainMessage(17, RendererManager.this.mSubPv_StWrapper).sendToTarget();
                    }
                }
            };
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            switch (message.what) {
                case 5:
                    doSetUpRenderForRecord();
                    RendererManager.this.mEglThreadBlockVar.open();
                    break;
                case 6:
                    doReleaseRenderForRecord();
                    RendererManager.this.mEglThreadBlockVar.open();
                    break;
                case FrameMetricsAggregator.DELAY_INDEX /* 7 */:
                    doUpdateTemplate();
                    break;
                case 9:
                    releaseRenderers();
                    RendererManager.this.mEglThreadBlockVar.open();
                    break;
                case 10:
                    doRelease();
                    RendererManager.this.mEglThreadBlockVar.open();
                    break;
                case 11:
                    createRenderers();
                    RendererManager.this.mEglThreadBlockVar.open();
                    break;
                case 12:
                    Size size = (Size) message.obj;
                    RendererManager.this.mRendererTexWidth = size.getWidth();
                    RendererManager.this.mRendererTexHeight = size.getHeight();
                    doUpdateRenderSize();
                    RendererManager.this.mEglThreadBlockVar.open();
                    break;
                case 13:
                    doUpdatePreviewSurface((Surface) message.obj);
                    RendererManager.this.mEglThreadBlockVar.open();
                    break;
                case 14:
                    doUpdateRecordingSurface((Surface) message.obj);
                    RendererManager.this.mEglThreadBlockVar.open();
                    break;
                case 15:
                    HashMap map = (HashMap) message.obj;
                    doSetupPictureTextures((Size) map.get("pip_bottom"), (Size) map.get("pip_top"));
                    RendererManager.this.mEglThreadBlockVar.open();
                    break;
                case 16:
                    SurfaceTextureWrapper surfaceTextureWrapper = (SurfaceTextureWrapper) message.obj;
                    if (surfaceTextureWrapper != null) {
                        surfaceTextureWrapper.updateTexImage();
                        tryTakePicutre();
                        break;
                    }
                    break;
                case 17:
                    SurfaceTextureWrapper surfaceTextureWrapper2 = (SurfaceTextureWrapper) message.obj;
                    if (surfaceTextureWrapper2 != null) {
                        surfaceTextureWrapper2.updateTexImage();
                        draw();
                        break;
                    }
                    break;
                case 18:
                    if (RendererManager.this.mScreenRenderer != null) {
                        RendererManager.this.mScreenRenderer.onActivityPause();
                    }
                    RendererManager.this.mEglThreadBlockVar.open();
                    break;
            }
        }

        private void createRenderers() {
            LogHelper.m23d(RendererManager.TAG, "[createRenderers]+ mSurfaceTextureHandler = " + RendererManager.this.mSurfaceTextureHandler);
            synchronized (RendererManager.this.mRenderLock) {
                if (RendererManager.this.mSurfaceTextureHandler == null) {
                    if (!RendererManager.this.mStFrameListener.isAlive()) {
                        RendererManager.this.mStFrameListener.start();
                    }
                    RendererManager.this.mSurfaceTextureHandler = new Handler(RendererManager.this.mStFrameListener.getLooper());
                    RendererManager.this.mPreviewFrameBuffer = new FrameBuffer();
                    RendererManager.this.mPreviewFrameBuffer.init();
                    RendererManager.this.mBottomGraphicRenderer = new BottomGraphicRenderer(RendererManager.this.mActivity);
                    RendererManager.this.mTopGraphicRenderer = new TopGraphicRenderer(RendererManager.this.mActivity);
                    RendererManager.this.mScreenRenderer = new ScreenRenderer(RendererManager.this.mActivity);
                    RendererManager.this.mScreenRenderer.init();
                    RendererManager.this.mCaptureRenderer = new CaptureRenderer(RendererManager.this.mActivity);
                    RendererManager.this.mCaptureRenderer.init();
                    doUpdateTemplate();
                }
                RendererManager.this.isBottomHasHighFrameRate = true;
            }
            LogHelper.m23d(RendererManager.TAG, "[createRenderers]-");
        }

        private void doUpdateRenderSize() {
            LogHelper.m23d(RendererManager.TAG, "[doUpdateRenderSize]+ mPreviewTexWidth = " + RendererManager.this.mRendererTexWidth + " mPreviewTexHeight = " + RendererManager.this.mRendererTexHeight + " mPreviewFrameBuffer = " + RendererManager.this.mPreviewFrameBuffer);
            synchronized (RendererManager.this.mRenderLock) {
                if (RendererManager.this.mMainPv_StWrapper != null) {
                    RendererManager.this.mMainPv_StWrapper.release();
                    RendererManager.this.mMainPv_StWrapper = null;
                }
                RendererManager.this.mMainPv_StWrapper = new SurfaceTextureWrapper();
                RendererManager.this.mMainPv_StWrapper.setDefaultBufferSize(RendererManager.this.mRendererTexWidth, RendererManager.this.mRendererTexHeight);
                RendererManager.this.mMainPv_StWrapper.setOnFrameAvailableListener(this.mBottomCamFrameAvailableListener, RendererManager.this.mSurfaceTextureHandler);
                if (RendererManager.this.mSubPv_StWrapper != null) {
                    RendererManager.this.mSubPv_StWrapper.release();
                    RendererManager.this.mSubPv_StWrapper = null;
                }
                RendererManager.this.mSubPv_StWrapper = new SurfaceTextureWrapper();
                RendererManager.this.mSubPv_StWrapper.setDefaultBufferSize(RendererManager.this.mRendererTexWidth, RendererManager.this.mRendererTexHeight);
                RendererManager.this.mSubPv_StWrapper.setOnFrameAvailableListener(this.mTopCamFrameAvailableListener, RendererManager.this.mSurfaceTextureHandler);
                if (RendererManager.this.mPreviewFrameBuffer != null) {
                    RendererManager.this.mPreviewFrameBuffer.setRendererSize(RendererManager.this.mRendererTexWidth, RendererManager.this.mRendererTexHeight);
                }
                if (RendererManager.this.mBottomGraphicRenderer != null) {
                    RendererManager.this.mBottomGraphicRenderer.setRendererSize(RendererManager.this.mRendererTexWidth, RendererManager.this.mRendererTexHeight, false);
                }
                if (RendererManager.this.mTopGraphicRenderer != null) {
                    RendererManager.this.mTopGraphicRenderer.setRendererSize(RendererManager.this.mRendererTexWidth, RendererManager.this.mRendererTexHeight, true);
                }
                if (RendererManager.this.mScreenRenderer != null) {
                    RendererManager.this.mScreenRenderer.setRendererSize(RendererManager.this.mRendererTexWidth, RendererManager.this.mRendererTexHeight);
                }
                RendererManager.this.mRenderLock.notifyAll();
            }
            LogHelper.m23d(RendererManager.TAG, "[doUpdateRenderSize]-");
        }

        private void doUpdatePreviewSurface(Surface surface) {
            if (RendererManager.this.mScreenRenderer != null) {
                RendererManager.this.mScreenRenderer.setSurface(surface);
                RendererManager.this.mNeedNotifyFirstFrameForSurfaceChanged = true;
            }
        }

        private void releaseRenderers() {
            LogHelper.m23d(RendererManager.TAG, "[releaseRenderers]+");
            synchronized (RendererManager.this.mRenderLock) {
                if (RendererManager.this.mPreviewFrameBuffer != null) {
                    RendererManager.this.mPreviewFrameBuffer.unInit();
                    RendererManager.this.mPreviewFrameBuffer = null;
                }
                if (RendererManager.this.mPictureFb != null) {
                    RendererManager.this.mPictureFb.unInit();
                    RendererManager.this.mPictureFb = null;
                }
                if (RendererManager.this.mBottomGraphicRenderer != null) {
                    RendererManager.this.mBottomGraphicRenderer.release();
                    RendererManager.this.mBottomGraphicRenderer = null;
                }
                if (RendererManager.this.mTopGraphicRenderer != null) {
                    RendererManager.this.mTopGraphicRenderer.release();
                    RendererManager.this.mTopGraphicRenderer = null;
                }
                if (RendererManager.this.mCaptureRenderer != null) {
                    RendererManager.this.mCaptureRenderer.release();
                    RendererManager.this.mCaptureRenderer = null;
                }
                if (RendererManager.this.mRecorderRenderer != null) {
                    RendererManager.this.mRecorderRenderer.releaseSurface();
                    RendererManager.this.mRecorderRenderer = null;
                }
                if (RendererManager.this.mScreenRenderer != null) {
                    RendererManager.this.mScreenRenderer.release();
                    RendererManager.this.mScreenRenderer = null;
                }
                RendererManager.this.mRenderLock.notifyAll();
            }
            LogHelper.m23d(RendererManager.TAG, "[releaseRenderers]-");
        }

        private void doSetupPictureTextures(Size size, Size size2) {
            LogHelper.m23d(RendererManager.TAG, "doSetupPictureTextures");
            if (RendererManager.this.mPictureFb == null) {
                RendererManager.this.mPictureFb = new FrameBuffer();
                RendererManager.this.mPictureFb.init();
            }
            if (RendererManager.this.mMainCap_StWrapper == null) {
                RendererManager.this.mMainCap_StWrapper = new SurfaceTextureWrapper();
                RendererManager.this.mMainCap_StWrapper.setDefaultBufferSize(size.getWidth(), size.getHeight());
                RendererManager.this.mMainCap_StWrapper.setOnFrameAvailableListener(this.mBottomCamFrameAvailableListener, RendererManager.this.mSurfaceTextureHandler);
            }
            RendererManager.this.mMainCap_StWrapper.setDefaultBufferSize(size.getWidth(), size.getHeight());
            if (RendererManager.this.mSubCap_StWrapper == null) {
                RendererManager.this.mSubCap_StWrapper = new SurfaceTextureWrapper();
                RendererManager.this.mSubCap_StWrapper.setDefaultBufferSize(size2.getWidth(), size2.getHeight());
                RendererManager.this.mSubCap_StWrapper.setOnFrameAvailableListener(this.mTopCamFrameAvailableListener, RendererManager.this.mSurfaceTextureHandler);
            }
            RendererManager.this.mSubCap_StWrapper.setDefaultBufferSize(size2.getWidth(), size2.getHeight());
        }

        private void releaseTextures() {
            LogHelper.m23d(RendererManager.TAG, "[releaseTextures]+");
            if (RendererManager.this.mSubPv_StWrapper != null) {
                RendererManager.this.mSubPv_StWrapper.release();
                RendererManager.this.mSubPv_StWrapper = null;
            }
            if (RendererManager.this.mMainPv_StWrapper != null) {
                RendererManager.this.mMainPv_StWrapper.release();
                RendererManager.this.mMainPv_StWrapper = null;
            }
            RendererManager.this.mRendererTexWidth = -1;
            RendererManager.this.mRendererTexHeight = -1;
            if (RendererManager.this.mMainCap_StWrapper != null) {
                RendererManager.this.mMainCap_StWrapper.release();
                RendererManager.this.mMainCap_StWrapper = null;
            }
            if (RendererManager.this.mSubCap_StWrapper != null) {
                RendererManager.this.mSubCap_StWrapper.release();
                RendererManager.this.mSubCap_StWrapper = null;
            }
            LogHelper.m23d(RendererManager.TAG, "[releaseTextures]-");
        }

        private void doSetUpRenderForRecord() {
            synchronized (RendererManager.this.mRenderLock) {
                RendererManager.this.mRecorderRenderer = new RecorderRenderer(RendererManager.this.mActivity);
                RendererManager.this.mRecorderRenderer.init();
                RendererManager.this.mRenderLock.notifyAll();
            }
        }

        private void doUpdateRecordingSurface(Surface surface) {
            synchronized (RendererManager.this.mRenderLock) {
                if (RendererManager.this.mRecorderRenderer == null) {
                    throw new IllegalStateException("Before update record surface, please call prepareRecording firstly!");
                }
                RendererManager.this.mRecorderRenderer.setRecrodingSurface(surface, !RendererManager.this.mPipSwitched);
                RendererManager.this.mRenderLock.notifyAll();
            }
        }

        private void doReleaseRenderForRecord() {
            if (RendererManager.this.mRecorderRenderer != null) {
                RendererManager.this.mRecorderRenderer.releaseSurface();
                RendererManager.this.mRecorderRenderer = null;
            }
        }

        private void doUpdateTemplate() {
            if (RendererManager.this.mTopGraphicRenderer != null && RendererManager.this.mBackTempResId > 0 && RendererManager.this.mFrontTempResId > 0) {
                RendererManager.this.mTopGraphicRenderer.initTemplateTexture(RendererManager.this.mBackTempResId, RendererManager.this.mFrontTempResId);
            }
            if (RendererManager.this.mScreenRenderer != null && RendererManager.this.mHighlightTempResId > 0 && RendererManager.this.mEditResId > 0) {
                RendererManager.this.mScreenRenderer.updateScreenEffectTemplate(RendererManager.this.mHighlightTempResId, RendererManager.this.mEditResId);
            }
        }

        private void doRelease() {
            LogHelper.m23d(RendererManager.TAG, "[doRelease]+");
            releaseTextures();
            releaseRenderers();
            RendererManager.this.releaseEgl();
            LogHelper.m23d(RendererManager.TAG, "[doRelease]-");
        }

        private boolean doTimestampSync() {
            if (RendererManager.this.mMainPv_StWrapper == null || RendererManager.this.mSubPv_StWrapper == null || RendererManager.this.mMainPv_StWrapper.getBufferTimeStamp() == 0 || RendererManager.this.mSubPv_StWrapper.getBufferTimeStamp() == 0) {
                return false;
            }
            if (RendererManager.this.isBottomHasHighFrameRate && this.mBottomCamTimeStamp != RendererManager.this.mMainPv_StWrapper.getBufferTimeStamp()) {
                this.mBottomCamTimeStamp = RendererManager.this.mMainPv_StWrapper.getBufferTimeStamp();
                this.mTopCamTimeStamp = RendererManager.this.mSubPv_StWrapper.getBufferTimeStamp();
                return true;
            }
            if (RendererManager.this.isBottomHasHighFrameRate || this.mTopCamTimeStamp == RendererManager.this.mSubPv_StWrapper.getBufferTimeStamp()) {
                return false;
            }
            this.mBottomCamTimeStamp = RendererManager.this.mMainPv_StWrapper.getBufferTimeStamp();
            this.mTopCamTimeStamp = RendererManager.this.mSubPv_StWrapper.getBufferTimeStamp();
            return true;
        }

        private void draw() {
            SurfaceTextureWrapper surfaceTextureWrapper;
            SurfaceTextureWrapper surfaceTextureWrapper2;
            synchronized (RendererManager.this.mRenderLock) {
                if (doTimestampSync() && (!RendererManager.this.mBlockingForPvSizeChange)) {
                    RendererManager.this.mPreviewFrameBuffer.setupFrameBufferGraphics(RendererManager.this.mRendererTexWidth, RendererManager.this.mRendererTexHeight);
                    SurfaceTextureWrapper surfaceTextureWrapper3 = RendererManager.this.mSubPv_StWrapper;
                    SurfaceTextureWrapper surfaceTextureWrapper4 = RendererManager.this.mMainPv_StWrapper;
                    boolean z = RendererManager.this.mPipSwitched;
                    if (z) {
                        SurfaceTextureWrapper surfaceTextureWrapper5 = RendererManager.this.mMainPv_StWrapper;
                        surfaceTextureWrapper = RendererManager.this.mSubPv_StWrapper;
                        surfaceTextureWrapper2 = surfaceTextureWrapper5;
                    } else {
                        surfaceTextureWrapper = surfaceTextureWrapper4;
                        surfaceTextureWrapper2 = surfaceTextureWrapper3;
                    }
                    RendererManager.this.mBottomGraphicRenderer.draw(surfaceTextureWrapper.getTextureId(), surfaceTextureWrapper.getBufferTransformMatrix(), GLUtil.createIdentityMtx(), false);
                    RendererManager.this.mTopGraphicRenderer.draw(surfaceTextureWrapper2.getTextureId(), surfaceTextureWrapper2.getBufferTransformMatrix(), GLUtil.createIdentityMtx(), RendererManager.this.mPreviewTopGraphicRect.copy(), RendererManager.this.mCurrentOrientation, false);
                    RendererManager.this.mPreviewFrameBuffer.setScreenBufferGraphics();
                    long bufferTimeStamp = z ? surfaceTextureWrapper2.getBufferTimeStamp() : surfaceTextureWrapper.getBufferTimeStamp();
                    if (RendererManager.this.mRecorderRenderer != null) {
                        RendererManager.this.mRecorderRenderer.draw(RendererManager.this.mPreviewFrameBuffer.getFboTexId(), bufferTimeStamp);
                    }
                    if (RendererManager.this.mNeedNotifyFirstFrameForSurfaceChanged) {
                        RendererManager.this.mRendererCallback.onFristFrameAvailable(surfaceTextureWrapper.getBufferTimeStamp());
                        RendererManager.this.mNeedNotifyFirstFrameForSurfaceChanged = false;
                    }
                    RendererManager.this.mScreenRenderer.draw(RendererManager.this.mPreviewTopGraphicRect.copy(), RendererManager.this.mPreviewFrameBuffer.getFboTexId(), RendererManager.this.mPreviewTopGraphicRect.getHighLightStatus());
                }
            }
        }

        private void tryTakePicutre() {
            SurfaceTextureWrapper surfaceTextureWrapper;
            SurfaceTextureWrapper surfaceTextureWrapper2;
            if (RendererManager.this.mMainCap_StWrapper != null && RendererManager.this.mMainCap_StWrapper.getBufferTimeStamp() > 0 && RendererManager.this.mSubCap_StWrapper != null && RendererManager.this.mSubCap_StWrapper.getBufferTimeStamp() > 0) {
                boolean z = RendererManager.this.mPipSwitched;
                SurfaceTextureWrapper surfaceTextureWrapper3 = RendererManager.this.mSubCap_StWrapper;
                SurfaceTextureWrapper surfaceTextureWrapper4 = RendererManager.this.mMainCap_StWrapper;
                if (z) {
                    SurfaceTextureWrapper surfaceTextureWrapper5 = RendererManager.this.mMainCap_StWrapper;
                    surfaceTextureWrapper = RendererManager.this.mSubCap_StWrapper;
                    surfaceTextureWrapper2 = surfaceTextureWrapper5;
                } else {
                    surfaceTextureWrapper = surfaceTextureWrapper4;
                    surfaceTextureWrapper2 = surfaceTextureWrapper3;
                }
                RendererManager.this.mPictureFb.setRendererSize(surfaceTextureWrapper.getWidth(), surfaceTextureWrapper.getHeight());
                RendererManager.this.mBottomGraphicRenderer.setRendererSize(surfaceTextureWrapper.getWidth(), surfaceTextureWrapper.getHeight(), z);
                RendererManager.this.mTopGraphicRenderer.setRendererSize(surfaceTextureWrapper.getWidth(), surfaceTextureWrapper.getHeight(), false);
                RendererManager.this.mCaptureRenderer.setRendererSize(surfaceTextureWrapper.getWidth(), surfaceTextureWrapper.getHeight());
                RendererManager.this.mCaptureRenderer.setCaptureSurface(RendererManager.this.mCaptureSurface);
                RendererManager.this.mPictureFb.setupFrameBufferGraphics(surfaceTextureWrapper.getWidth(), surfaceTextureWrapper.getHeight());
                float[] fArrCreateIdentityMtx = GLUtil.createIdentityMtx();
                if (RendererManager.this.mCurrentOrientation % 180 != 0) {
                    Matrix.translateM(fArrCreateIdentityMtx, 0, fArrCreateIdentityMtx, 0, 0.5f, 0.5f, 0.0f);
                    Matrix.rotateM(fArrCreateIdentityMtx, 0, -(RendererManager.this.mPipSwitched ? 180 : 0), 0.0f, 0.0f, 1.0f);
                    Matrix.translateM(fArrCreateIdentityMtx, 0, -0.5f, -0.5f, 0.0f);
                }
                AnimationRect animationRectCopy = RendererManager.this.mPreviewTopGraphicRect.copy();
                int i = 90;
                if (RendererManager.this.mCurrentOrientation % 180 == 0 && RendererManager.this.mPipSwitched) {
                    i = 270;
                }
                animationRectCopy.changeToLandscapeCooridnateSystem(surfaceTextureWrapper.getWidth(), surfaceTextureWrapper.getHeight(), i);
                RendererManager.this.mBottomGraphicRenderer.draw(surfaceTextureWrapper.getTextureId(), GLUtil.createIdentityMtx(), fArrCreateIdentityMtx, false);
                RendererManager.this.mTopGraphicRenderer.draw(surfaceTextureWrapper2.getTextureId(), GLUtil.createIdentityMtx(), fArrCreateIdentityMtx, animationRectCopy.copy(), ((RendererManager.this.mCurrentOrientation - (RendererManager.this.mPipSwitched ? 270 : 90)) + 360) % 360, RendererManager.this.mPipSwitched);
                RendererManager.this.mPictureFb.setScreenBufferGraphics();
                if (RendererManager.this.mCaptureRenderer != null) {
                    RendererManager.this.mCaptureRenderer.draw(RendererManager.this.mPictureFb.getFboTexId());
                }
                RendererManager.this.mBottomGraphicRenderer.setRendererSize(RendererManager.this.mRendererTexWidth, RendererManager.this.mRendererTexHeight, false);
                RendererManager.this.mTopGraphicRenderer.setRendererSize(RendererManager.this.mRendererTexWidth, RendererManager.this.mRendererTexHeight, true);
                RendererManager.this.mMainCap_StWrapper.resetSTStatus();
                RendererManager.this.mSubCap_StWrapper.resetSTStatus();
            }
        }

        public void sendMessageSync(int i) {
            RendererManager.this.mEglThreadBlockVar.close();
            sendEmptyMessage(i);
            RendererManager.this.mEglThreadBlockVar.block();
        }

        public void sendMessageSync(int i, Object obj) {
            RendererManager.this.mEglThreadBlockVar.close();
            obtainMessage(i, obj).sendToTarget();
            RendererManager.this.mEglThreadBlockVar.block();
        }
    }

    private void initialize() {
        this.mEglHandler.post(new Runnable() { // from class: com.mediatek.camera.v2.stream.pip.pipwrapping.RendererManager.1
            @Override // java.lang.Runnable
            public void run() {
                RendererManager.this.mEgl = (EGL10) EGLContext.getEGL();
                RendererManager.this.mEglDisplay = RendererManager.this.mEgl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
                if (RendererManager.this.mEglDisplay == EGL10.EGL_NO_DISPLAY) {
                    throw new RuntimeException("eglGetDisplay failed");
                }
                int[] iArr = new int[2];
                if (!RendererManager.this.mEgl.eglInitialize(RendererManager.this.mEglDisplay, iArr)) {
                    throw new RuntimeException("eglInitialize failed");
                }
                LogHelper.m27v(RendererManager.TAG, "<initialize> EGL version: " + iArr[0] + '.' + iArr[1]);
                RendererManager.this.mEglConfig = PipEGLConfigWrapper.getInstance().getEGLConfigChooser().chooseConfig(RendererManager.this.mEgl, RendererManager.this.mEglDisplay);
                RendererManager.this.mEglContext = RendererManager.this.mEgl.eglCreateContext(RendererManager.this.mEglDisplay, RendererManager.this.mEglConfig, EGL10.EGL_NO_CONTEXT, new int[]{12440, 2, 12344});
                if (RendererManager.this.mEglContext == null || RendererManager.this.mEglContext == EGL10.EGL_NO_CONTEXT) {
                    throw new RuntimeException("failed to createContext");
                }
                LogHelper.m27v(RendererManager.TAG, "<initialize> EGL context: create success");
                RendererManager.this.mEglSurface = RendererManager.this.mEgl.eglCreatePbufferSurface(RendererManager.this.mEglDisplay, RendererManager.this.mEglConfig, null);
                if (RendererManager.this.mEglSurface == null || RendererManager.this.mEglSurface == EGL10.EGL_NO_SURFACE) {
                    LogHelper.m28w(RendererManager.TAG, "createWindowSurface error eglError = " + RendererManager.this.mEgl.eglGetError());
                    throw new RuntimeException("failed to createWindowSurface mEglSurface = " + RendererManager.this.mEglSurface + " EGL_NO_SURFACE = " + EGL10.EGL_NO_SURFACE);
                }
                LogHelper.m27v(RendererManager.TAG, "<initialize> EGL surface: create success");
                if (!RendererManager.this.mEgl.eglMakeCurrent(RendererManager.this.mEglDisplay, RendererManager.this.mEglSurface, RendererManager.this.mEglSurface, RendererManager.this.mEglContext)) {
                    throw new RuntimeException("failed to eglMakeCurrent");
                }
                LogHelper.m27v(RendererManager.TAG, "<initialize> EGL make current: success");
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void releaseEgl() {
        LogHelper.m23d(TAG, "[releaseEgl]+");
        this.mEgl.eglDestroySurface(this.mEglDisplay, this.mEglSurface);
        this.mEgl.eglDestroyContext(this.mEglDisplay, this.mEglContext);
        this.mEgl.eglMakeCurrent(this.mEglDisplay, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT);
        this.mEgl.eglTerminate(this.mEglDisplay);
        this.mEglSurface = null;
        this.mEglContext = null;
        this.mEglDisplay = null;
        LogHelper.m23d(TAG, "[releaseEgl]-");
    }
}
