package com.mediatek.camera.mode.pip;

import android.app.Activity;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Size;
import android.view.Surface;
import com.mediatek.camera.ICameraContext;
import com.mediatek.camera.R;
import com.mediatek.camera.mode.pip.PipGestureManager;
import com.mediatek.camera.mode.pip.PipView;
import com.mediatek.camera.mode.pip.pipwrapping.PIPOperator;
import com.mediatek.camera.platform.ICameraView;
import com.mediatek.camera.util.Log;
import java.util.concurrent.ConcurrentHashMap;

/* loaded from: classes.dex */
public class PipController implements PipGestureManager.Listener, PIPOperator.Listener, PipView.Listener {
    private static ConcurrentHashMap<Context, PipController> pipControllerList = new ConcurrentHashMap<>();
    private Activity mActivity;
    private Listener mListener;
    private MainHandler mMainHandler;
    private PipGestureManager mPipGestureManager;
    private PIPOperator mPipOperator;
    private ICameraView mPipView = null;
    private Object mSyncLock = new Object();
    private State mCurState = State.STATE_IDLE;

    public interface Listener {
        void canDoStartPreview();

        int getButtomGraphicCameraId();

        int getGSensorOrientation();

        int getViewRotation();

        void onPIPPictureTaken(byte[] bArr);

        void switchPIP();
    }

    public enum State {
        STATE_SWITCHING,
        STATE_IDLE,
        STATE_RECORD_STARTING,
        STATE_UNINIT;

        /* renamed from: values, reason: to resolve conflict with enum method */
        public static State[] valuesCustom() {
            return values();
        }
    }

    public int getGSensorOrientation() {
        return this.mListener.getGSensorOrientation();
    }

    @Override // com.mediatek.camera.mode.pip.PipGestureManager.Listener
    public int getButtomGraphicCameraId() {
        return this.mListener.getButtomGraphicCameraId();
    }

    @Override // com.mediatek.camera.mode.pip.PipGestureManager.Listener
    public void notifyTopGraphicIsEdited() {
        if (this.mPipView != null) {
            this.mPipView.refresh();
        }
    }

    @Override // com.mediatek.camera.mode.pip.PipGestureManager.Listener
    public void switchPIP() {
        Log.m31d("PipController", "switchPIP");
        this.mMainHandler.removeMessages(1);
        if (getState() == State.STATE_UNINIT) {
            return;
        }
        this.mMainHandler.sendEmptyMessage(1);
    }

    public void stopSwitchPip() {
        this.mMainHandler.removeMessages(1);
    }

    @Override // com.mediatek.camera.mode.pip.pipwrapping.PIPOperator.Listener
    public void onPIPPictureTaken(byte[] bArr) {
        Log.m31d("PipController", "onPIPPictureTaken jpegData = " + bArr + " mListener = " + this.mListener);
        if (this.mListener != null) {
            this.mListener.onPIPPictureTaken(bArr);
        }
    }

    @Override // com.mediatek.camera.mode.pip.pipwrapping.PIPOperator.Listener
    public void unlockNextCapture() {
        Log.m31d("PipController", "canDoStartPreview mListener = " + this.mListener);
        if (this.mListener != null) {
            this.mListener.canDoStartPreview();
        }
    }

    @Override // com.mediatek.camera.mode.pip.PipView.Listener
    public void onUpdateEffect(int i, int i2, int i3, int i4) {
        Log.m31d("PipController", "onUpdateEffect mListener = " + this.mListener);
        updateEffectTemplates(i, i2, i3, i4);
    }

    public static synchronized PipController instance(Context context) {
        PipController pipController;
        Log.m31d("PipController", "instance pipControllerList size = " + pipControllerList.size());
        pipController = pipControllerList.get(context);
        if (pipController == null) {
            pipController = new PipController();
            pipControllerList.put(context, pipController);
        }
        return pipController;
    }

    public void setPreviewSurface(Surface surface) {
        Log.m31d("PipController", "setPreviewSurface mPipOperator = " + this.mPipOperator);
        synchronized (this.mSyncLock) {
            if (this.mPipOperator != null) {
                this.mPipOperator.setPreviewSurface(surface);
            }
        }
    }

    public void notifySurfaceViewDestroyed(Surface surface) {
        Log.m31d("PipController", "notifySurfaceViewDestroyed mPipOperator = " + this.mPipOperator);
        if (this.mPipOperator != null) {
            this.mPipOperator.notifySurfaceViewDestroyed(surface);
        }
    }

    public void setPreviewTextureSize(int i, int i2) {
        Log.m31d("PipController", "setTextureSize width = " + i + " height = " + i2);
        synchronized (this.mSyncLock) {
            if (this.mPipOperator != null && this.mPipGestureManager != null) {
                this.mPipOperator.setUpSurfaceTextures();
                this.mPipOperator.setPreviewTextureSize(i, i2);
                this.mPipGestureManager.setRendererSize(i, i2);
                this.mPipOperator.updateTopGraphic(this.mPipGestureManager.getTopGraphicRect());
            }
        }
    }

    public SurfaceTexture getBottomSurfaceTexture() {
        synchronized (this.mSyncLock) {
            if (this.mPipOperator == null) {
                return null;
            }
            return this.mPipOperator.getBottomSurfaceTexture();
        }
    }

    public SurfaceTexture getTopSurfaceTexture() {
        synchronized (this.mSyncLock) {
            if (this.mPipOperator == null) {
                return null;
            }
            return this.mPipOperator.getTopSurfaceTexture();
        }
    }

    public void setPictureSize(Size size, Size size2) throws InterruptedException {
        if (this.mPipOperator != null) {
            this.mPipOperator.setPictureSize(size, size2);
        }
    }

    public void takePicture(byte[] bArr, int i, int i2, boolean z, int i3) {
        Log.m31d("PipController", "takePicture jpeg = " + bArr + " width = " + i + " height = " + i2 + " isBottomCamera = " + z);
        if (this.mPipOperator != null) {
            this.mPipOperator.offerJpegData(bArr, i, i2, z, i3);
        }
    }

    public void prepareRecording() {
        Log.m31d("PipController", "prepareRecording");
        if (this.mPipOperator != null) {
            this.mPipOperator.prepareRecording();
        }
    }

    public void setRecordingSurface(Surface surface, int i) {
        Log.m31d("PipController", "setRecordingSurface surface = " + surface + " orientation = " + i);
        if (this.mPipOperator != null) {
            this.mPipOperator.setRecordingSurface(surface, i);
        }
    }

    public void startPushVideoBuffer() {
        Log.m31d("PipController", "startPushVideoBuffer");
        if (this.mPipOperator != null) {
            this.mPipOperator.startPushVideoBuffer();
        }
    }

    public void stopPushVideoBuffer() {
        Log.m31d("PipController", "stopPushVideoBuffer");
        if (this.mPipOperator != null) {
            this.mPipOperator.stopPushVideoBuffer();
        }
    }

    public void takeVideoSnapshot(int i, boolean z) {
        Log.m31d("PipController", "takeVideoSnapshot orientation = " + i + " isBackBottom = " + z);
        if (this.mPipOperator != null) {
            this.mPipOperator.takeVideoSnapshot(i, z);
        }
    }

    public boolean onDown(float f, float f2, int i, int i2) {
        if (this.mPipGestureManager == null || this.mPipOperator == null) {
            return false;
        }
        boolean zOnDown = this.mPipGestureManager.onDown(f, f2, i, i2);
        this.mPipOperator.updateTopGraphic(this.mPipGestureManager.getTopGraphicRect());
        return zOnDown;
    }

    public boolean onUp() {
        if (this.mPipGestureManager == null || this.mPipOperator == null) {
            return false;
        }
        boolean zOnUp = this.mPipGestureManager.onUp();
        this.mPipOperator.updateTopGraphic(this.mPipGestureManager.getTopGraphicRect());
        return zOnUp;
    }

    public boolean onSingleTapUp(float f, float f2) {
        if (this.mPipView != null) {
            this.mPipView.update(1, new Object[0]);
        }
        if (this.mPipGestureManager == null || this.mPipOperator == null) {
            return false;
        }
        boolean zOnSingleTapUp = this.mPipGestureManager.onSingleTapUp(f, f2);
        this.mPipOperator.updateTopGraphic(this.mPipGestureManager.getTopGraphicRect());
        return zOnSingleTapUp;
    }

    public boolean onLongPress(float f, float f2) {
        if (this.mPipGestureManager == null || this.mPipOperator == null) {
            return false;
        }
        boolean zOnLongPress = this.mPipGestureManager.onLongPress(f, f2);
        this.mPipOperator.updateTopGraphic(this.mPipGestureManager.getTopGraphicRect());
        return zOnLongPress;
    }

    public boolean onScroll(float f, float f2, float f3, float f4) {
        if (this.mPipGestureManager == null || this.mPipOperator == null) {
            return false;
        }
        boolean zOnScroll = this.mPipGestureManager.onScroll(f, f2, f3, f4);
        this.mPipOperator.updateTopGraphic(this.mPipGestureManager.getTopGraphicRect());
        return zOnScroll;
    }

    public void onGSensorOrientationChanged(int i) {
        Log.m31d("PipController", "onGSensorOrientationChanged orientation = " + i);
        if (this.mPipOperator != null) {
            this.mPipOperator.updateGSensorOrientation(i);
        }
    }

    public void onViewOrienationChanged(int i) {
        Log.m31d("PipController", "onViewOrienationChanged orientation = " + i);
        if (this.mPipGestureManager != null) {
            this.mPipGestureManager.onViewOrientationChanged(i);
        }
        if (this.mPipOperator != null) {
            this.mPipOperator.updateTopGraphic(this.mPipGestureManager.getTopGraphicRect());
        }
        if (this.mPipView != null) {
            this.mPipView.update(2, Integer.valueOf(i));
        }
    }

    public void setDisplayRotation(int i) {
        Log.m31d("PipController", "setDisplayRotation displayRotation = " + i);
        if (this.mPipGestureManager != null && this.mPipOperator != null) {
            this.mPipGestureManager.setDisplayRotation(i);
            this.mPipGestureManager.onViewOrientationChanged(this.mListener.getViewRotation());
            this.mPipOperator.updateGSensorOrientation(getGSensorOrientation());
            this.mPipOperator.updateTopGraphic(this.mPipGestureManager.getTopGraphicRect());
        }
    }

    public void hideModeViews(boolean z) {
        Log.m31d("PipController", "hideModeViews hide = " + z);
        if (this.mPipView != null) {
            if (z) {
                this.mPipView.hide();
            } else {
                this.mPipView.show();
            }
        }
    }

    public boolean isPipEffectShowing() {
        if (this.mPipView != null && this.mPipView.isShowing()) {
            return true;
        }
        return false;
    }

    public void closeEffects() {
        if (this.mPipView != null) {
            this.mPipView.refresh();
        }
    }

    public void init(ICameraContext iCameraContext, Listener listener) {
        Log.m31d("PipController", "init mPipOperator = " + this.mPipOperator);
        this.mActivity = iCameraContext.getActivity();
        this.mMainHandler = new MainHandler(this.mActivity.getMainLooper());
        this.mListener = listener;
        synchronized (this.mSyncLock) {
            if (this.mPipGestureManager == null) {
                this.mPipGestureManager = new PipGestureManager(this.mActivity, this);
            }
            if (this.mPipOperator == null) {
                this.mPipOperator = new PIPOperator(this.mActivity, this);
                this.mPipOperator.initPIPRenderer();
            }
            updateEffectTemplates(R.drawable.rear_01, R.drawable.front_01, R.drawable.front_01_focus, R.drawable.plus);
            if (this.mPipView != null) {
                this.mPipView.show();
            }
        }
    }

    public void pause() {
        Log.m34i("PipController", "pause");
        setState(State.STATE_UNINIT);
        this.mMainHandler.removeMessages(1);
        synchronized (this.mSyncLock) {
            if (this.mPipOperator != null) {
                this.mPipOperator.unInitPIPRenderer();
            }
            if (this.mPipView != null) {
                this.mPipView.hide();
            }
        }
    }

    public void resume() {
        Log.m31d("PipController", "resume mPipOperator = " + this.mPipOperator);
        synchronized (this.mSyncLock) {
            if (this.mPipOperator == null) {
                this.mPipOperator = new PIPOperator(this.mActivity, this);
            }
            this.mPipOperator.initPIPRenderer();
            if (this.mPipView != null) {
                this.mPipView.show();
            }
        }
        setState(State.STATE_IDLE);
    }

    public void unInit(Context context) {
        Log.m31d("PipController", "unInit pipControllerList size = " + pipControllerList.size());
        synchronized (this.mSyncLock) {
            if (this.mPipOperator != null) {
                this.mPipOperator.unInitPIPRenderer();
                this.mPipOperator = null;
            }
            if (this.mPipView != null) {
                this.mPipView.uninit();
            }
            this.mPipGestureManager = null;
            pipControllerList.remove(context);
        }
    }

    public void setState(State state) {
        Log.m31d("PipController", "setState state = " + state);
        this.mCurState = state;
    }

    public State getState() {
        Log.m31d("PipController", "getState");
        return this.mCurState;
    }

    private PipController() {
        Log.m31d("PipController", "PIPController");
    }

    private void updateEffectTemplates(int i, int i2, int i3, int i4) {
        Log.m31d("PipController", "updateEffectTemplates");
        if (this.mPipOperator != null) {
            this.mPipOperator.updateEffectTemplates(i, i2, i3, i4);
        }
    }

    private class MainHandler extends Handler {
        public MainHandler(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            Log.m31d("PipController", "[handleMessage] msg:" + message.what);
            switch (message.what) {
                case 1:
                    PipController.this.mCurState = State.STATE_SWITCHING;
                    synchronized (PipController.this.mSyncLock) {
                        if (PipController.this.mPipOperator != null) {
                            PipController.this.mPipOperator.switchPIP();
                        }
                        if (PipController.this.mListener != null) {
                            PipController.this.mListener.switchPIP();
                        }
                    }
                    Log.m31d("PipController", "switchPIP end");
                    return;
                default:
                    return;
            }
        }
    }
}
