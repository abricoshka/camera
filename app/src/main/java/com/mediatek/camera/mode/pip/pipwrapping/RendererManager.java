package com.mediatek.camera.mode.pip.pipwrapping;

import android.app.Activity;
import android.graphics.SurfaceTexture;
import android.opengl.EGLSurface;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.FrameMetricsAggregator;
import android.util.Size;
import android.view.Surface;
import com.android.camera.Util;
import com.mediatek.camera.util.Log;
import java.util.HashMap;

/* loaded from: classes.dex */
public class RendererManager {
    private static final String TAG = RendererManager.class.getSimpleName();
    private final Activity mActivity;
    private CaptureRendererHandler mCaptureEglHandler;
    private HandlerThread mCaptureEglThread;
    private int mCurrentOrientation;
    private PreviewRendererHandler mPreviewEglHandler;
    private HandlerThread mPreviewEglThread;
    private RecorderRenderer mRecorderRenderer;
    private int mPreviewTexWidth = -1;
    private int mPreviewTexHeight = -1;
    private boolean mIsBottomHasHighFrameRate = true;
    private boolean mPIPSwitched = false;
    private AnimationRect mPreviewTopGraphicRect = null;
    private int mBackResId = 0;
    private int mFrontResId = 0;
    private int mHighlightResId = 0;
    private int mEditBtnResId = 0;
    private int mDrawDrawFrameCount = 0;
    private long mDrawDrawStartTime = 0;

    public RendererManager(Activity activity) {
        this.mActivity = activity;
    }

    public void init() {
        Log.m31d(TAG, "[init]+");
        if (this.mPreviewEglHandler == null) {
            createPreviewGLThread();
        }
        initScreenRenderer();
        Log.m31d(TAG, "[init]-");
    }

    public void unInit() {
        Log.m31d(TAG, "[unInit]+");
        if (this.mPreviewEglHandler != null) {
            doReleaseAndQuitThread(this.mPreviewEglHandler, this.mPreviewEglThread);
            this.mPreviewEglHandler = null;
            this.mPreviewEglThread = null;
            this.mPreviewTexWidth = -1;
            this.mPreviewTexHeight = -1;
        }
        if (this.mCaptureEglHandler != null) {
            doReleaseAndQuitThread(this.mCaptureEglHandler, this.mCaptureEglThread);
            this.mCaptureEglHandler = null;
            this.mCaptureEglThread = null;
        }
        Log.m31d(TAG, "[unInit]-");
    }

    public void updateEffectTemplates(int i, int i2, int i3, int i4) {
        if ((this.mBackResId != i || this.mFrontResId != i2 || this.mHighlightResId != i3) && this.mPreviewEglHandler != null) {
            this.mBackResId = i;
            this.mFrontResId = i2;
            this.mHighlightResId = i3;
            this.mEditBtnResId = i4;
            this.mPreviewEglHandler.removeMessages(2);
            this.mPreviewEglHandler.obtainMessage(2).sendToTarget();
        }
    }

    public void setPreviewSize(int i, int i2) {
        Log.m31d(TAG, "[setPreviewTextureSize]+ width = " + i + " height = " + i2);
        if (this.mPreviewTexWidth == i && this.mPreviewTexHeight == i2) {
            Log.m34i(TAG, "setPreviewTextureSize same size set, ignore!");
            return;
        }
        if (this.mPreviewEglHandler != null) {
            this.mPreviewEglHandler.obtainMessage(3, i, i2, null).sendToTarget();
            waitDone(this.mPreviewEglHandler);
        }
        Log.m31d(TAG, "[setPreviewTextureSize]-");
    }

    public void setUpSurfaceTextures() {
        Log.m31d(TAG, "[setUpSurfaceTextures]+");
        boolean z = false;
        if (this.mPreviewEglHandler != null && (!this.mPreviewEglHandler.hasMessages(2))) {
            z = true;
        }
        setupPIPTextures();
        if (z && this.mPreviewEglHandler != null) {
            this.mPreviewEglHandler.obtainMessage(2).sendToTarget();
        }
        Log.m31d(TAG, "[setUpSurfaceTextures]-");
    }

    public void setPreviewSurfaceSync(Surface surface) {
        Log.m31d(TAG, "setPreviewSurfaceSync");
        if (this.mPreviewEglHandler != null && surface != null) {
            this.mPreviewEglHandler.obtainMessage(14, surface).sendToTarget();
            waitDone(this.mPreviewEglHandler);
        }
    }

    public void notifySurfaceViewDestroyed(Surface surface) {
        if (this.mPreviewEglHandler != null) {
            this.mPreviewEglHandler.obtainMessage(10, surface).sendToTarget();
            waitDone(this.mPreviewEglHandler);
        }
    }

    public SurfaceTexture getBottomPvSt() {
        Log.m31d(TAG, "getBottomSurfaceTexture mIsPIPSwitched = " + this.mPIPSwitched);
        return this.mPIPSwitched ? getTopSurfaceTexture() : getBottomSurfaceTexture();
    }

    public SurfaceTexture getTopPvSt() {
        Log.m31d(TAG, "getTopSurfaceTexture mIsPIPSwitched = " + this.mPIPSwitched);
        return this.mPIPSwitched ? getBottomSurfaceTexture() : getTopSurfaceTexture();
    }

    public void updateTopGraphic(AnimationRect animationRect) {
        Log.m31d(TAG, "updateTopGraphic");
        this.mPreviewTopGraphicRect = animationRect;
    }

    public void updateGSensorOrientation(int i) {
        Log.m31d(TAG, "updateOrientation newOrientation = " + i);
        this.mCurrentOrientation = i;
    }

    public int getPreviewTextureWidth() {
        return this.mPreviewTexWidth;
    }

    public int getPreviewTextureHeight() {
        return this.mPreviewTexHeight;
    }

    public void prepareRecordSync() {
        if (this.mRecorderRenderer == null) {
            this.mRecorderRenderer = new RecorderRenderer(this.mActivity);
        }
        if (this.mPreviewEglHandler != null) {
            this.mPreviewEglHandler.removeMessages(4);
            this.mPreviewEglHandler.obtainMessage(4).sendToTarget();
            waitDone(this.mPreviewEglHandler);
        }
    }

    public void setRecordSurfaceSync(Surface surface, int i) {
        if (this.mPreviewEglHandler != null) {
            this.mPreviewEglHandler.obtainMessage(15, i, 0, surface).sendToTarget();
            waitDone(this.mPreviewEglHandler);
        }
    }

    public void startRecordSync() {
        if (this.mRecorderRenderer != null) {
            this.mRecorderRenderer.startRecording();
        }
    }

    public void stopRecordSync() {
        if (this.mRecorderRenderer != null) {
            this.mRecorderRenderer.stopRecording();
            this.mRecorderRenderer = null;
        }
    }

    public void switchPipSync() {
        Log.m31d(TAG, "switchPIP");
        if (this.mPreviewEglHandler != null) {
            this.mPreviewEglHandler.obtainMessage(6).sendToTarget();
            waitDone(this.mPreviewEglHandler);
        }
    }

    public void takeVideoSnapShot(int i, Surface surface) {
        Log.m31d(TAG, "takeVideoSnapShot orientation = " + i);
        if (this.mPreviewEglHandler != null) {
            this.mPreviewEglHandler.removeMessages(11);
            this.mPreviewEglHandler.obtainMessage(11, i, 0, surface).sendToTarget();
            waitDone(this.mPreviewEglHandler);
        }
    }

    public SurfaceTexture getBottomCapSt() {
        if (this.mCaptureEglHandler == null) {
            return null;
        }
        return this.mCaptureEglHandler.getBottomSt();
    }

    public SurfaceTexture getTopCapSt() {
        if (this.mCaptureEglHandler == null) {
            return null;
        }
        return this.mCaptureEglHandler.getTopSt();
    }

    public int initCapture(int[] iArr) {
        checkAndCreateCaptureGLThread(iArr);
        return this.mCaptureEglHandler.getPixelFormat();
    }

    public void setCaptureSurface(Surface surface) {
        if (this.mCaptureEglHandler != null) {
            this.mCaptureEglHandler.obtainMessage(17, surface).sendToTarget();
            waitDone(this.mCaptureEglHandler);
        }
    }

    public void setCaptureSize(Size size, Size size2) {
        if (this.mCaptureEglHandler != null) {
            HashMap map = new HashMap();
            map.put("pip_bottom", size);
            map.put("pip_top", size2);
            this.mCaptureEglHandler.obtainMessage(16, map).sendToTarget();
            waitDone(this.mCaptureEglHandler);
        }
    }

    public void setJpegRotation(boolean z, int i) {
        if (this.mCaptureEglHandler != null) {
            this.mCaptureEglHandler.setJpegRotation(z, i);
        }
    }

    public void unInitCapture() {
        if (this.mCaptureEglHandler != null) {
            doReleaseAndQuitThread(this.mCaptureEglHandler, this.mCaptureEglThread);
            this.mCaptureEglHandler = null;
            this.mCaptureEglThread = null;
        }
    }

    private void initScreenRenderer() {
        if (this.mPreviewEglHandler != null) {
            this.mPreviewEglHandler.obtainMessage(13).sendToTarget();
            waitDone(this.mPreviewEglHandler);
        }
    }

    private void setupPIPTextures() {
        Log.m31d(TAG, "setupPIPTextures");
        if (this.mPreviewEglHandler != null) {
            this.mPreviewEglHandler.obtainMessage(7).sendToTarget();
            waitDone(this.mPreviewEglHandler);
        }
    }

    private SurfaceTexture getBottomSurfaceTexture() {
        if (this.mPreviewEglHandler == null) {
            return null;
        }
        return this.mPreviewEglHandler.getBottomSt();
    }

    private SurfaceTexture getTopSurfaceTexture() {
        if (this.mPreviewEglHandler == null) {
            return null;
        }
        return this.mPreviewEglHandler.getTopSt();
    }

    private void createPreviewGLThread() {
        this.mPreviewEglThread = new HandlerThread("Pip-PreviewGLThread");
        this.mPreviewEglThread.start();
        Looper looper = this.mPreviewEglThread.getLooper();
        if (looper == null) {
            throw new RuntimeException("why looper is null?");
        }
        this.mPreviewEglHandler = new PreviewRendererHandler(looper);
        this.mPreviewEglHandler.obtainMessage(0).sendToTarget();
        waitDone(this.mPreviewEglHandler);
    }

    private void checkAndCreateCaptureGLThread(int[] iArr) {
        if (this.mCaptureEglHandler == null) {
            this.mCaptureEglThread = new HandlerThread("Pip-CaptureGLThread");
            this.mCaptureEglThread.start();
            Looper looper = this.mCaptureEglThread.getLooper();
            if (looper == null) {
                throw new RuntimeException("why looper is null?");
            }
            this.mCaptureEglHandler = new CaptureRendererHandler(looper);
            this.mCaptureEglHandler.obtainMessage(0, iArr).sendToTarget();
            waitDone(this.mCaptureEglHandler);
        }
    }

    private void doReleaseAndQuitThread(Handler handler, HandlerThread handlerThread) {
        handler.removeCallbacksAndMessages(null);
        handler.obtainMessage(1).sendToTarget();
        waitDone(handler);
        Looper looper = handlerThread.getLooper();
        if (looper != null) {
            looper.quit();
        }
    }

    private abstract class PipRendererHandler extends Handler {
        private SurfaceTexture.OnFrameAvailableListener mBottomCamFrameAvailableListener;
        private long mBottomCamTimeStamp;
        private SurfaceTextureWrapper mBottomPrvSt;
        private float[] mBottomTransformMatrix;
        protected EglCore mEglCore;
        private final HandlerThread mFrameListener;
        private long mLatestBottomCamTimeStamp;
        private long mLatestTopCamTimeStamp;
        protected EGLSurface mOffScreenSurface;
        private BottomGraphicRenderer mPreviewBottomGraphicRenderer;
        private int mPreviewFboTexId;
        private FrameBuffer mPreviewFrameBuffer;
        private TopGraphicRenderer mPreviewTopGraphicRenderer;
        private ScreenRenderer mScreenRenderer;
        private Handler mSurfaceTextureHandler;
        private SurfaceTexture.OnFrameAvailableListener mTopCamFrameAvailableListener;
        private long mTopCamTimeStamp;
        private float[] mTopCamTransformMatrix;
        private SurfaceTextureWrapper mTopPrvSt;
        private CaptureRenderer mVssRenderer;

        /* synthetic */ PipRendererHandler(RendererManager rendererManager, Looper looper, PipRendererHandler pipRendererHandler) {
            this(looper);
        }

        private PipRendererHandler(Looper looper) {
            super(looper);
            this.mFrameListener = new HandlerThread("PIP-PreviewSTFListener");
            this.mPreviewFboTexId = -12345;
            this.mBottomTransformMatrix = new float[16];
            this.mTopCamTransformMatrix = new float[16];
            this.mLatestBottomCamTimeStamp = 0L;
            this.mLatestTopCamTimeStamp = 0L;
            this.mBottomCamTimeStamp = 0L;
            this.mTopCamTimeStamp = 0L;
            this.mBottomCamFrameAvailableListener = new SurfaceTexture.OnFrameAvailableListener() { // from class: com.mediatek.camera.mode.pip.pipwrapping.RendererManager.PipRendererHandler.1
                @Override // android.graphics.SurfaceTexture.OnFrameAvailableListener
                public void onFrameAvailable(SurfaceTexture surfaceTexture) {
                    if (PipRendererHandler.this.mBottomPrvSt != null) {
                        PipRendererHandler.this.obtainMessage(8, PipRendererHandler.this.mBottomPrvSt).sendToTarget();
                    }
                }
            };
            this.mTopCamFrameAvailableListener = new SurfaceTexture.OnFrameAvailableListener() { // from class: com.mediatek.camera.mode.pip.pipwrapping.RendererManager.PipRendererHandler.2
                @Override // android.graphics.SurfaceTexture.OnFrameAvailableListener
                public void onFrameAvailable(SurfaceTexture surfaceTexture) {
                    if (PipRendererHandler.this.mTopPrvSt != null) {
                        PipRendererHandler.this.obtainMessage(9, PipRendererHandler.this.mTopPrvSt).sendToTarget();
                    }
                }
            };
            this.mOffScreenSurface = null;
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            switch (message.what) {
                case 1:
                    doRelease();
                    break;
                case 2:
                    doUpdateTemplate();
                    break;
                case 3:
                    RendererManager.this.mPreviewTexWidth = Integer.valueOf(message.arg1).intValue();
                    RendererManager.this.mPreviewTexHeight = Integer.valueOf(message.arg2).intValue();
                    doUpdateRenderSize();
                    break;
                case 4:
                    setUpRenderForRecord();
                    break;
                case 6:
                    doSwitchPIP();
                    break;
                case FrameMetricsAggregator.DELAY_INDEX /* 7 */:
                    doSetupPIPTextures();
                    break;
                case 8:
                    doProcessPreviewFrame((SurfaceTextureWrapper) message.obj, true);
                    break;
                case 9:
                    doProcessPreviewFrame((SurfaceTextureWrapper) message.obj, false);
                    break;
                case 10:
                    if (this.mScreenRenderer != null) {
                        this.mScreenRenderer.notifySurfaceStatus((Surface) message.obj);
                        break;
                    }
                    break;
                case 11:
                    doVideoSnapShot(Integer.valueOf(message.arg1).intValue(), (Surface) message.obj);
                    break;
                case 13:
                    doInitScreenRenderer();
                    break;
                case 14:
                    doUpdatePreviewSurface((Surface) message.obj);
                    break;
                case 15:
                    doUpdateRecordingSurface((Surface) message.obj, message.arg1);
                    break;
            }
        }

        public SurfaceTexture getBottomSt() {
            return this.mBottomPrvSt.getSurfaceTexture();
        }

        public SurfaceTexture getTopSt() {
            return this.mTopPrvSt.getSurfaceTexture();
        }

        protected void initEglCore() {
            this.mEglCore = new EglCore(null, 2, new int[]{1, 3});
        }

        protected void unInitEglCore() {
            Log.m31d(RendererManager.TAG, "[release]+");
            if (this.mEglCore != null) {
                this.mEglCore.releaseSurface(this.mOffScreenSurface);
                this.mEglCore.makeNothingCurrent();
                this.mEglCore.release();
                this.mEglCore = null;
            }
            Log.m31d(RendererManager.TAG, "[release]-");
        }

        private void doUpdateTemplate() {
            Log.m31d(RendererManager.TAG, "doUpdateTemplate backResourceId = " + RendererManager.this.mBackResId + " frontResourceId = " + RendererManager.this.mFrontResId + " fronthighlight = " + RendererManager.this.mHighlightResId);
            if (this.mPreviewTopGraphicRenderer != null) {
                this.mPreviewTopGraphicRenderer.initTemplateTexture(RendererManager.this.mBackResId, RendererManager.this.mFrontResId);
            }
            if (this.mScreenRenderer != null) {
                this.mScreenRenderer.updateScreenEffectTemplate(RendererManager.this.mHighlightResId, RendererManager.this.mEditBtnResId);
            }
            Log.m31d(RendererManager.TAG, "doUpdateTemplate end");
        }

        private void doUpdateRenderSize() {
            Log.m31d(RendererManager.TAG, "doUpdateRenderSize mPreviewTexWidth = " + RendererManager.this.mPreviewTexWidth + " mPreviewTexHeight = " + RendererManager.this.mPreviewTexHeight);
            if (!this.mFrameListener.isAlive()) {
                this.mFrameListener.start();
                this.mSurfaceTextureHandler = new Handler(this.mFrameListener.getLooper());
            }
            if (this.mBottomPrvSt == null) {
                this.mBottomPrvSt = new SurfaceTextureWrapper();
                this.mBottomPrvSt.setDefaultBufferSize(RendererManager.this.mPreviewTexWidth, RendererManager.this.mPreviewTexHeight);
                this.mBottomPrvSt.setOnFrameAvailableListener(this.mBottomCamFrameAvailableListener, this.mSurfaceTextureHandler);
            }
            this.mBottomPrvSt.setDefaultBufferSize(RendererManager.this.mPreviewTexWidth, RendererManager.this.mPreviewTexHeight);
            if (this.mTopPrvSt == null) {
                this.mTopPrvSt = new SurfaceTextureWrapper();
                this.mTopPrvSt.setDefaultBufferSize(RendererManager.this.mPreviewTexWidth, RendererManager.this.mPreviewTexHeight);
                this.mTopPrvSt.setOnFrameAvailableListener(this.mTopCamFrameAvailableListener, this.mSurfaceTextureHandler);
            }
            this.mTopPrvSt.setDefaultBufferSize(RendererManager.this.mPreviewTexWidth, RendererManager.this.mPreviewTexHeight);
            if (this.mPreviewFrameBuffer != null) {
                this.mPreviewFrameBuffer.setRendererSize(RendererManager.this.mPreviewTexWidth, RendererManager.this.mPreviewTexHeight);
            }
            if (this.mPreviewBottomGraphicRenderer != null) {
                this.mPreviewBottomGraphicRenderer.setRendererSize(RendererManager.this.mPreviewTexWidth, RendererManager.this.mPreviewTexHeight, false);
            }
            if (this.mPreviewTopGraphicRenderer != null) {
                this.mPreviewTopGraphicRenderer.setRendererSize(RendererManager.this.mPreviewTexWidth, RendererManager.this.mPreviewTexHeight);
            }
            if (this.mScreenRenderer != null) {
                this.mScreenRenderer.setRendererSize(RendererManager.this.mPreviewTexWidth, RendererManager.this.mPreviewTexHeight);
            }
        }

        private void setUpRenderForRecord() {
            if (RendererManager.this.mRecorderRenderer != null) {
                RendererManager.this.mRecorderRenderer.init();
            }
        }

        private void doSwitchPIP() {
            doSwitchTextures();
            RendererManager.this.mPIPSwitched = !RendererManager.this.mPIPSwitched;
        }

        private void doSwitchTextures() {
            float[] fArr = this.mTopCamTransformMatrix;
            this.mTopCamTransformMatrix = this.mBottomTransformMatrix;
            this.mBottomTransformMatrix = fArr;
        }

        private void doInitScreenRenderer() {
            this.mScreenRenderer = new ScreenRenderer(RendererManager.this.mActivity);
            this.mScreenRenderer.init();
        }

        private void doSetupPIPTextures() {
            Log.m31d(RendererManager.TAG, "doInitiSurfaceTextures mPreviewFrameBuffer = " + this.mPreviewFrameBuffer);
            resetTimeStamp();
            if (this.mPreviewFrameBuffer == null) {
                this.mPreviewFrameBuffer = new FrameBuffer();
                this.mPreviewFboTexId = this.mPreviewFrameBuffer.getFboTexId();
                this.mPreviewBottomGraphicRenderer = new BottomGraphicRenderer(RendererManager.this.mActivity);
                this.mPreviewTopGraphicRenderer = new TopGraphicRenderer(RendererManager.this.mActivity);
                if (RendererManager.this.mPIPSwitched) {
                    doSwitchTextures();
                }
            }
            RendererManager.this.mIsBottomHasHighFrameRate = Util.isBottomHasHighFrameRate(RendererManager.this.mActivity);
        }

        private void doUpdatePreviewSurface(Surface surface) {
            if (this.mScreenRenderer != null) {
                this.mScreenRenderer.setSurface(surface, true, true);
            }
        }

        private void doReleasePIPTexturesAndRenderers() {
            Log.m31d(RendererManager.TAG, "doReleasePIPSurfaceTextures");
            doReleasePIPTextures();
            releasePIPRenderers();
        }

        private void doUpdateRecordingSurface(Surface surface, int i) {
            if (RendererManager.this.mRecorderRenderer != null) {
                RendererManager.this.mRecorderRenderer.setRendererSize(RendererManager.this.mPreviewTexWidth, RendererManager.this.mPreviewTexHeight, i);
                RendererManager.this.mRecorderRenderer.setRecrodingSurface(surface);
            }
        }

        private void releasePIPRenderers() {
            Log.m31d(RendererManager.TAG, "releasePIPRenderers");
            this.mPreviewBottomGraphicRenderer = null;
            if (this.mPreviewTopGraphicRenderer != null) {
                this.mPreviewTopGraphicRenderer.release();
                this.mPreviewTopGraphicRenderer = null;
            }
            if (RendererManager.this.mRecorderRenderer != null) {
                RendererManager.this.mRecorderRenderer.releaseSurface();
                RendererManager.this.mRecorderRenderer = null;
            }
            if (this.mScreenRenderer != null) {
                this.mScreenRenderer.release();
                this.mScreenRenderer = null;
            }
            if (this.mSurfaceTextureHandler != null) {
                this.mFrameListener.quitSafely();
                this.mSurfaceTextureHandler = null;
            }
        }

        private void doVideoSnapShot(int i, Surface surface) {
            this.mVssRenderer = new CaptureRenderer(RendererManager.this.mActivity);
            this.mVssRenderer.init();
            this.mVssRenderer.setCaptureSize(RendererManager.this.mPreviewTexWidth, RendererManager.this.mPreviewTexHeight, i);
            this.mVssRenderer.setCaptureSurface(surface);
        }

        private void doRelease() {
            Log.m31d(RendererManager.TAG, "doRelease");
            if (this.mTopPrvSt != null) {
                this.mTopPrvSt.release();
                this.mTopPrvSt = null;
            }
            if (this.mBottomPrvSt != null) {
                this.mBottomPrvSt.release();
                this.mBottomPrvSt = null;
            }
            doReleasePIPTexturesAndRenderers();
        }

        private void doReleasePIPTextures() {
            Log.m31d(RendererManager.TAG, "_doReleasePIPTextures");
            if (this.mPreviewFrameBuffer != null) {
                this.mPreviewFrameBuffer.release();
                this.mPreviewFrameBuffer = null;
                this.mPreviewFboTexId = -12345;
            }
        }

        private void doProcessPreviewFrame(SurfaceTextureWrapper surfaceTextureWrapper, boolean z) {
            if (surfaceTextureWrapper == null) {
                return;
            }
            surfaceTextureWrapper.updateTexImage();
            if (z) {
                doUpdateBottomCamTimeStamp();
            } else {
                doUpdateTopCamTimeStamp();
            }
            draw();
        }

        private void doUpdateTopCamTimeStamp() {
            if (this.mTopPrvSt == null) {
                return;
            }
            this.mLatestTopCamTimeStamp = this.mTopPrvSt.getBufferTimeStamp();
            if (RendererManager.this.mPIPSwitched) {
                this.mBottomTransformMatrix = this.mTopPrvSt.getBufferTransformMatrix();
            } else {
                this.mTopCamTransformMatrix = this.mTopPrvSt.getBufferTransformMatrix();
            }
        }

        private void doUpdateBottomCamTimeStamp() {
            if (this.mBottomPrvSt == null) {
                return;
            }
            this.mLatestBottomCamTimeStamp = this.mBottomPrvSt.getBufferTimeStamp();
            if (RendererManager.this.mPIPSwitched) {
                this.mTopCamTransformMatrix = this.mBottomPrvSt.getBufferTransformMatrix();
            } else {
                this.mBottomTransformMatrix = this.mBottomPrvSt.getBufferTransformMatrix();
            }
        }

        private void resetTimeStamp() {
            this.mBottomCamTimeStamp = 0L;
            this.mLatestBottomCamTimeStamp = 0L;
            this.mTopCamTimeStamp = 0L;
            this.mLatestTopCamTimeStamp = 0L;
        }

        private boolean doTimestampSync() {
            if (this.mLatestBottomCamTimeStamp == 0 || this.mLatestTopCamTimeStamp == 0) {
                return false;
            }
            if (RendererManager.this.mIsBottomHasHighFrameRate && this.mBottomCamTimeStamp != this.mLatestBottomCamTimeStamp) {
                this.mBottomCamTimeStamp = this.mLatestBottomCamTimeStamp;
                this.mTopCamTimeStamp = this.mLatestTopCamTimeStamp;
                return true;
            }
            if (RendererManager.this.mIsBottomHasHighFrameRate || this.mTopCamTimeStamp == this.mLatestTopCamTimeStamp) {
                return false;
            }
            this.mBottomCamTimeStamp = this.mLatestBottomCamTimeStamp;
            this.mTopCamTimeStamp = this.mLatestTopCamTimeStamp;
            return true;
        }

        private void draw() {
            if (doTimestampSync() && this.mPreviewFrameBuffer != null) {
                drawToFbo();
                if (RendererManager.this.mRecorderRenderer != null) {
                    RendererManager.this.mRecorderRenderer.draw(this.mPreviewFboTexId, this.mBottomPrvSt.getBufferTimeStamp());
                }
                this.mScreenRenderer.draw(RendererManager.this.mPreviewTopGraphicRect.copy(), this.mPreviewFboTexId, RendererManager.this.mPreviewTopGraphicRect.getHighLightStatus());
                if (this.mVssRenderer != null) {
                    this.mVssRenderer.draw(this.mPreviewFboTexId);
                    this.mVssRenderer.release();
                    this.mVssRenderer = null;
                    this.mEglCore.makeCurrent(this.mOffScreenSurface);
                }
                RendererManager.this.updateFrameCounter();
            }
        }

        private void drawToFbo() {
            this.mPreviewFrameBuffer.setupFrameBufferGraphics(RendererManager.this.mPreviewTexWidth, RendererManager.this.mPreviewTexHeight);
            this.mPreviewBottomGraphicRenderer.draw(RendererManager.this.mPIPSwitched ? this.mTopPrvSt.getTextureId() : this.mBottomPrvSt.getTextureId(), this.mBottomTransformMatrix, GLUtil.createIdentityMtx(), false);
            this.mPreviewTopGraphicRenderer.draw(RendererManager.this.mPIPSwitched ? this.mBottomPrvSt.getTextureId() : this.mTopPrvSt.getTextureId(), this.mTopCamTransformMatrix, GLUtil.createIdentityMtx(), RendererManager.this.mPreviewTopGraphicRect.copy(), RendererManager.this.mCurrentOrientation, false);
            this.mPreviewFrameBuffer.setScreenBufferGraphics();
        }
    }

    private class PreviewRendererHandler extends PipRendererHandler {
        public PreviewRendererHandler(Looper looper) {
            super(RendererManager.this, looper, null);
        }

        @Override // com.mediatek.camera.mode.pip.pipwrapping.RendererManager.PipRendererHandler, android.os.Handler
        public void handleMessage(Message message) {
            super.handleMessage(message);
            switch (message.what) {
                case 0:
                    initEglCore();
                    this.mOffScreenSurface = this.mEglCore.createOffscreenSurface(1, 1);
                    this.mEglCore.makeCurrent(this.mOffScreenSurface);
                    break;
                case 1:
                    unInitEglCore();
                    break;
            }
        }
    }

    private class CaptureRendererHandler extends PipRendererHandler {
        private SurfaceTexture.OnFrameAvailableListener mBottomCamFrameAvailableListener;
        private SurfaceTextureWrapper mBottomCapSt;
        private int mBottomJpegRotation;
        private BottomGraphicRenderer mBottomRenderer;
        private WindowSurface mCapEglSurface;
        private final HandlerThread mFrameListener;
        private Handler mSurfaceTextureHandler;
        private SurfaceTexture.OnFrameAvailableListener mTopCamFrameAvailableListener;
        private SurfaceTextureWrapper mTopCapSt;
        private int mTopJpegRotation;
        private TopGraphicRenderer mTopRenderer;

        public CaptureRendererHandler(Looper looper) {
            super(RendererManager.this, looper, null);
            this.mFrameListener = new HandlerThread("PIP-CaptureSTFListener");
            this.mBottomCapSt = null;
            this.mTopCapSt = null;
            this.mCapEglSurface = null;
            this.mBottomJpegRotation = 0;
            this.mTopJpegRotation = 0;
            this.mBottomCamFrameAvailableListener = new SurfaceTexture.OnFrameAvailableListener() { // from class: com.mediatek.camera.mode.pip.pipwrapping.RendererManager.CaptureRendererHandler.1
                @Override // android.graphics.SurfaceTexture.OnFrameAvailableListener
                public void onFrameAvailable(SurfaceTexture surfaceTexture) {
                    if (CaptureRendererHandler.this.mBottomCapSt != null) {
                        CaptureRendererHandler.this.obtainMessage(18, CaptureRendererHandler.this.mBottomCapSt).sendToTarget();
                    }
                }
            };
            this.mTopCamFrameAvailableListener = new SurfaceTexture.OnFrameAvailableListener() { // from class: com.mediatek.camera.mode.pip.pipwrapping.RendererManager.CaptureRendererHandler.2
                @Override // android.graphics.SurfaceTexture.OnFrameAvailableListener
                public void onFrameAvailable(SurfaceTexture surfaceTexture) {
                    if (CaptureRendererHandler.this.mTopCapSt != null) {
                        CaptureRendererHandler.this.obtainMessage(18, CaptureRendererHandler.this.mTopCapSt).sendToTarget();
                    }
                }
            };
        }

        @Override // com.mediatek.camera.mode.pip.pipwrapping.RendererManager.PipRendererHandler, android.os.Handler
        public void handleMessage(Message message) {
            Log.m31d(RendererManager.TAG, "handleMessage:" + message.what);
            switch (message.what) {
                case 0:
                    this.mEglCore = new EglCore(null, 3, (int[]) message.obj);
                    break;
                case 1:
                    releaseRenderer();
                    unInitEglCore();
                    break;
                case 16:
                    HashMap map = (HashMap) message.obj;
                    setUpTexturesForCapture((Size) map.get("pip_bottom"), (Size) map.get("pip_top"));
                    break;
                case 17:
                    if (this.mEglCore != null) {
                        this.mCapEglSurface = new WindowSurface(this.mEglCore, (Surface) message.obj);
                        this.mCapEglSurface.makeCurrent();
                    }
                    this.mBottomJpegRotation = 0;
                    this.mTopJpegRotation = 0;
                    break;
                case 18:
                    ((SurfaceTextureWrapper) message.obj).updateTexImage();
                    tryTakePicutre();
                    break;
            }
        }

        public int getPixelFormat() {
            return this.mEglCore.getPixelFormat();
        }

        @Override // com.mediatek.camera.mode.pip.pipwrapping.RendererManager.PipRendererHandler
        public SurfaceTexture getBottomSt() {
            return this.mBottomCapSt.getSurfaceTexture();
        }

        @Override // com.mediatek.camera.mode.pip.pipwrapping.RendererManager.PipRendererHandler
        public SurfaceTexture getTopSt() {
            return this.mTopCapSt.getSurfaceTexture();
        }

        public void setJpegRotation(boolean z, int i) {
            if (z) {
                this.mBottomJpegRotation = i;
            } else {
                this.mTopJpegRotation = i;
            }
        }

        private void setUpTexturesForCapture(Size size, Size size2) {
            Log.m31d(RendererManager.TAG, "[setUpTexturesForCapture]+");
            if (!this.mFrameListener.isAlive()) {
                this.mFrameListener.start();
                this.mSurfaceTextureHandler = new Handler(this.mFrameListener.getLooper());
            }
            if (this.mBottomCapSt == null) {
                this.mBottomCapSt = new SurfaceTextureWrapper();
                this.mBottomCapSt.setDefaultBufferSize(size.getWidth(), size.getHeight());
                this.mBottomCapSt.setOnFrameAvailableListener(this.mBottomCamFrameAvailableListener, this.mSurfaceTextureHandler);
            }
            this.mBottomCapSt.setDefaultBufferSize(size.getWidth(), size.getHeight());
            if (this.mBottomRenderer == null) {
                this.mBottomRenderer = new BottomGraphicRenderer(RendererManager.this.mActivity);
            }
            if (this.mTopCapSt == null) {
                this.mTopCapSt = new SurfaceTextureWrapper();
                this.mTopCapSt.setDefaultBufferSize(size2.getWidth(), size2.getHeight());
                this.mTopCapSt.setOnFrameAvailableListener(this.mTopCamFrameAvailableListener, this.mSurfaceTextureHandler);
            }
            this.mTopCapSt.setDefaultBufferSize(size2.getWidth(), size2.getHeight());
            if (this.mTopRenderer == null) {
                this.mTopRenderer = new TopGraphicRenderer(RendererManager.this.mActivity);
            }
            Log.m31d(RendererManager.TAG, "[setUpTexturesForCapture]-");
        }

        private void tryTakePicutre() {
            if (this.mBottomCapSt != null && this.mBottomCapSt.getBufferTimeStamp() > 0 && this.mTopCapSt != null && this.mTopCapSt.getBufferTimeStamp() > 0) {
                Log.m31d(RendererManager.TAG, "[tryTakePicutre]+");
                this.mBottomRenderer.setRendererSize(this.mBottomCapSt.getWidth(), this.mBottomCapSt.getHeight(), true);
                this.mTopRenderer.initTemplateTexture(RendererManager.this.mBackResId, RendererManager.this.mFrontResId);
                this.mTopRenderer.setRendererSize(this.mBottomCapSt.getWidth(), this.mBottomCapSt.getHeight());
                AnimationRect animationRectCopy = RendererManager.this.mPreviewTopGraphicRect.copy();
                animationRectCopy.changeCooridnateSystem(this.mBottomCapSt.getWidth(), this.mBottomCapSt.getHeight(), 360 - RendererManager.this.mCurrentOrientation);
                boolean zBottomGraphicIsMainCamera = Util.bottomGraphicIsMainCamera(RendererManager.this.mActivity);
                boolean z = !zBottomGraphicIsMainCamera;
                boolean z2 = zBottomGraphicIsMainCamera;
                GLES20.glViewport(0, 0, this.mBottomCapSt.getWidth(), this.mBottomCapSt.getHeight());
                GLES20.glEnable(3042);
                GLES20.glBlendFunc(1, 771);
                this.mBottomRenderer.draw(this.mBottomCapSt.getTextureId(), GLUtil.createIdentityMtx(), getTexMatrixByRotation(this.mBottomJpegRotation), z);
                this.mTopRenderer.draw(this.mTopCapSt.getTextureId(), GLUtil.createIdentityMtx(), getTexMatrixByRotation(this.mTopJpegRotation), animationRectCopy.copy(), RendererManager.this.mCurrentOrientation > 0 ? -RendererManager.this.mCurrentOrientation : -1, z2);
                this.mCapEglSurface.swapBuffers();
                doReleaseCaptureSt();
                this.mCapEglSurface.makeNothingCurrent();
                this.mCapEglSurface.releaseEglSurface();
                this.mCapEglSurface = null;
                Log.m31d(RendererManager.TAG, "[tryTakePicutre]-");
            }
        }

        private float[] getTexMatrixByRotation(int i) {
            float[] fArrCreateIdentityMtx = GLUtil.createIdentityMtx();
            Matrix.translateM(fArrCreateIdentityMtx, 0, fArrCreateIdentityMtx, 0, 0.5f, 0.5f, 0.0f);
            Matrix.rotateM(fArrCreateIdentityMtx, 0, -i, 0.0f, 0.0f, 1.0f);
            Matrix.translateM(fArrCreateIdentityMtx, 0, -0.5f, -0.5f, 0.0f);
            return fArrCreateIdentityMtx;
        }

        private void releaseRenderer() {
            if (this.mBottomRenderer != null) {
                this.mBottomRenderer.release();
                this.mBottomRenderer = null;
            }
            if (this.mTopRenderer != null) {
                this.mTopRenderer.release();
                this.mTopRenderer = null;
            }
            doReleaseCaptureSt();
            if (this.mSurfaceTextureHandler != null) {
                this.mFrameListener.quitSafely();
                this.mSurfaceTextureHandler = null;
            }
        }

        private void doReleaseCaptureSt() {
            if (this.mBottomCapSt != null) {
                this.mBottomCapSt.release();
                this.mBottomCapSt = null;
            }
            if (this.mTopCapSt != null) {
                this.mTopCapSt.release();
                this.mTopCapSt = null;
            }
        }
    }

    private boolean waitDone(Handler handler) {
        final Object obj = new Object();
        Runnable runnable = new Runnable() { // from class: com.mediatek.camera.mode.pip.pipwrapping.RendererManager.1
            @Override // java.lang.Runnable
            public void run() {
                synchronized (obj) {
                    obj.notifyAll();
                }
            }
        };
        synchronized (obj) {
            handler.post(runnable);
            try {
                obj.wait();
            } catch (InterruptedException e) {
                Log.m32e(TAG, "waitDone interrupted");
                return false;
            }
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateFrameCounter() {
        this.mDrawDrawFrameCount++;
        if (this.mDrawDrawFrameCount % 300 == 0) {
            long jCurrentTimeMillis = System.currentTimeMillis();
            int i = (int) (jCurrentTimeMillis - this.mDrawDrawStartTime);
            Log.m31d(TAG, "[AP-->Wrapping][Preview] Drawing frame, fps = " + ((this.mDrawDrawFrameCount * 1000.0f) / i) + " in last " + i + " millisecond.");
            this.mDrawDrawStartTime = jCurrentTimeMillis;
            this.mDrawDrawFrameCount = 0;
        }
    }
}
