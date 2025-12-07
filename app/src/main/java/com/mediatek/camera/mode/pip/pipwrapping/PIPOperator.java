package com.mediatek.camera.mode.pip.pipwrapping;

import android.app.Activity;
import android.graphics.SurfaceTexture;
import android.util.Size;
import android.view.Surface;
import com.mediatek.camera.mode.pip.pipwrapping.PipCaptureExecutor;
import com.mediatek.camera.util.Log;

/* loaded from: classes.dex */
public class PIPOperator implements PipCaptureExecutor.ImageCallback {
    private static final String TAG = PIPOperator.class.getSimpleName();
    private Activity mActivity;
    private Listener mListener;
    private PipCaptureExecutor mPipCaptureExecutor;
    private RendererManager mRendererManager;

    public interface Listener {
        void onPIPPictureTaken(byte[] bArr);

        void unlockNextCapture();
    }

    public PIPOperator(Activity activity, Listener listener) {
        this.mActivity = activity;
        this.mListener = listener;
    }

    @Override // com.mediatek.camera.mode.pip.pipwrapping.PipCaptureExecutor.ImageCallback
    public void onPictureTaken(byte[] bArr) {
        Log.m31d(TAG, "onPIPPictureTaken jpegData = " + bArr);
        if (this.mListener != null) {
            this.mListener.onPIPPictureTaken(bArr);
        }
    }

    @Override // com.mediatek.camera.mode.pip.pipwrapping.PipCaptureExecutor.ImageCallback
    public void unlockNextCapture() {
        Log.m31d(TAG, "canDoStartPreview");
        if (this.mListener != null) {
            this.mListener.unlockNextCapture();
        }
    }

    public void initPIPRenderer() {
        Log.m31d(TAG, "initPIPRenderer");
        if (this.mRendererManager == null) {
            this.mRendererManager = new RendererManager(this.mActivity);
        }
        this.mRendererManager.init();
        if (this.mPipCaptureExecutor == null) {
            this.mPipCaptureExecutor = new PipCaptureExecutor(this.mActivity, this.mRendererManager, this);
        }
        this.mPipCaptureExecutor.init();
    }

    public void updateEffectTemplates(int i, int i2, int i3, int i4) {
        Log.m31d(TAG, "updateEffectTemplates");
        this.mRendererManager.updateEffectTemplates(i, i2, i3, i4);
    }

    public void unInitPIPRenderer() {
        if (this.mPipCaptureExecutor != null) {
            this.mPipCaptureExecutor.unInit();
        }
        this.mRendererManager.unInit();
    }

    public void setPreviewTextureSize(int i, int i2) {
        Log.m31d(TAG, "setTextureSize width = " + i + " height = " + i2);
        this.mRendererManager.setPreviewSize(i, i2);
    }

    public void setUpSurfaceTextures() {
        Log.m31d(TAG, "setUpSurfaceTextures");
        this.mRendererManager.setUpSurfaceTextures();
    }

    public void setPreviewSurface(Surface surface) {
        Log.m31d(TAG, "setPreviewSurface surface = " + surface);
        this.mRendererManager.setPreviewSurfaceSync(surface);
    }

    public void notifySurfaceViewDestroyed(Surface surface) {
        Log.m31d(TAG, "notifySurfaceViewDestroyed");
        this.mRendererManager.notifySurfaceViewDestroyed(surface);
    }

    public SurfaceTexture getBottomSurfaceTexture() {
        return this.mRendererManager.getBottomPvSt();
    }

    public SurfaceTexture getTopSurfaceTexture() {
        return this.mRendererManager.getTopPvSt();
    }

    public void updateTopGraphic(AnimationRect animationRect) {
        Log.m31d(TAG, "updateTopGraphicPostion topGraphic = " + animationRect);
        this.mRendererManager.updateTopGraphic(animationRect);
    }

    public void updateGSensorOrientation(int i) {
        this.mRendererManager.updateGSensorOrientation(i);
    }

    public void switchPIP() {
        this.mRendererManager.switchPipSync();
    }

    public void setPictureSize(Size size, Size size2) throws InterruptedException {
        this.mPipCaptureExecutor.setUpCapture(size, size2);
    }

    public void offerJpegData(byte[] bArr, int i, int i2, boolean z, int i3) {
        this.mPipCaptureExecutor.offerJpegData(bArr, new Size(i, i2), z);
    }

    public void prepareRecording() {
        Log.m31d(TAG, "prepareRecording");
        this.mRendererManager.prepareRecordSync();
    }

    public void setRecordingSurface(Surface surface, int i) {
        Log.m31d(TAG, "setRecordingSurface surface = " + surface);
        this.mRendererManager.setRecordSurfaceSync(surface, i);
    }

    public void startPushVideoBuffer() {
        Log.m31d(TAG, "startPushVideoBuffer");
        this.mRendererManager.startRecordSync();
    }

    public void stopPushVideoBuffer() {
        Log.m31d(TAG, "stopPushVideoBuffer");
        this.mRendererManager.stopRecordSync();
    }

    public void takeVideoSnapshot(int i, boolean z) {
        Log.m31d(TAG, "takeVideoSnapshot orientation = " + i);
        boolean z2 = i % 180 != 0;
        int iMax = Math.max(this.mRendererManager.getPreviewTextureHeight(), this.mRendererManager.getPreviewTextureWidth());
        int iMin = Math.min(this.mRendererManager.getPreviewTextureHeight(), this.mRendererManager.getPreviewTextureWidth());
        this.mRendererManager.takeVideoSnapShot(i, this.mPipCaptureExecutor.getVssSurface(z2 ? iMax : iMin, z2 ? iMin : iMax));
    }
}
