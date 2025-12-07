package com.mediatek.camera.p005v2.stream.pip;

import android.app.Activity;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.media.CamcorderProfile;
import android.util.Size;
import android.view.Surface;
import com.mediatek.camera.debug.LogHelper;
import com.mediatek.camera.p005v2.stream.ICaptureStream;
import com.mediatek.camera.p005v2.stream.IPreviewStream;
import com.mediatek.camera.p005v2.stream.IRecordStream;
import com.mediatek.camera.p005v2.stream.pip.IPipGesture;
import com.mediatek.camera.p005v2.stream.pip.IPipStream;
import com.mediatek.camera.p005v2.stream.pip.pipwrapping.RendererManager;
import java.io.FileDescriptor;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/* loaded from: classes.dex */
public class PipStream implements IPreviewStream, ICaptureStream, IRecordStream, IPipStream {
    private static final LogHelper.Tag TAG = new LogHelper.Tag(PipStream.class.getSimpleName());
    private int mGsensorOrientation;
    private final ICaptureStream mNextCaptureStream;
    private final IPreviewStream mNextPreviewStream;
    private final IRecordStream mNextRecordStream;
    private Surface mOutputCaptureSurface;
    private Surface mOutputPreviewSurface;
    private IPipGesture mPipGestureController;
    private RendererManager mRendererManager;
    private final CopyOnWriteArrayList<IPipStream.PipStreamCallback> mPipCallbacks = new CopyOnWriteArrayList<>();
    private PipRendererCallback mPipRendererCallback = new PipRendererCallback(this, null);
    private PipGestureCallback mPipGestureCallback = new PipGestureCallback(this, 0 == true ? 1 : 0);
    private int mPreviewWidth = -1;
    private int mPreviewHeight = -1;
    private Surface mInputPreviewBottomSurface = null;
    private Surface mInputPreviewTopSurface = null;
    private Surface mInputPictureBottomSurface = null;
    private Surface mInputPictureTopSurface = null;

    public PipStream(IPreviewStream iPreviewStream, ICaptureStream iCaptureStream, IRecordStream iRecordStream) {
        LogHelper.m26i(TAG, "PipStream constructor");
        this.mNextPreviewStream = iPreviewStream;
        this.mNextCaptureStream = iCaptureStream;
        this.mNextRecordStream = iRecordStream;
    }

    @Override // com.mediatek.camera.p005v2.stream.pip.IPipStream
    public void registerPipStreamCallback(IPipStream.PipStreamCallback pipStreamCallback) {
        if (pipStreamCallback != null && (!this.mPipCallbacks.contains(pipStreamCallback))) {
            this.mPipCallbacks.add(pipStreamCallback);
        }
    }

    @Override // com.mediatek.camera.p005v2.stream.pip.IPipStream
    public void unregisterPipStreamCallback(IPipStream.PipStreamCallback pipStreamCallback) {
        if (pipStreamCallback != null && this.mPipCallbacks.contains(pipStreamCallback)) {
            this.mPipCallbacks.remove(pipStreamCallback);
        }
    }

    @Override // com.mediatek.camera.p005v2.stream.pip.IPipStream
    public void open(Activity activity) {
        LogHelper.m23d(TAG, "[open]+");
        this.mRendererManager = new RendererManager(activity, this.mPipRendererCallback);
        this.mRendererManager.init();
        this.mPipGestureController = new PipGestureImpl(activity, this.mPipGestureCallback);
        this.mPipGestureController.open();
        Iterator<T> it = this.mPipCallbacks.iterator();
        while (it.hasNext()) {
            ((IPipStream.PipStreamCallback) it.next()).onOpened();
        }
        LogHelper.m23d(TAG, "[open]-");
    }

    @Override // com.mediatek.camera.p005v2.stream.pip.IPipStream
    public void resume() {
        LogHelper.m23d(TAG, "[resume]+");
        resetPipStreamStatus();
        Iterator<T> it = this.mPipCallbacks.iterator();
        while (it.hasNext()) {
            ((IPipStream.PipStreamCallback) it.next()).onResumed();
        }
        this.mRendererManager.onActivityResume();
        LogHelper.m23d(TAG, "[resume]-");
    }

    @Override // com.mediatek.camera.p005v2.stream.pip.IPipStream
    public void onActivityPause() {
        LogHelper.m23d(TAG, "[onActivityPause]+");
        this.mRendererManager.onActivityPause();
        LogHelper.m23d(TAG, "[onActivityPause]-");
    }

    @Override // com.mediatek.camera.p005v2.stream.pip.IPipStream
    public void pause() {
        LogHelper.m23d(TAG, "[pause]+");
        Iterator<T> it = this.mPipCallbacks.iterator();
        while (it.hasNext()) {
            ((IPipStream.PipStreamCallback) it.next()).onPaused();
        }
        LogHelper.m23d(TAG, "[pause]-");
    }

    @Override // com.mediatek.camera.p005v2.stream.pip.IPipStream
    public void close() {
        LogHelper.m23d(TAG, "[close]+");
        this.mRendererManager.unInit();
        if (this.mPipGestureController != null) {
            this.mPipGestureController.release();
            this.mPipGestureController = null;
        }
        Iterator<T> it = this.mPipCallbacks.iterator();
        while (it.hasNext()) {
            ((IPipStream.PipStreamCallback) it.next()).onClosed();
        }
        LogHelper.m23d(TAG, "[close]-");
    }

    @Override // com.mediatek.camera.p005v2.stream.pip.IPipStream
    public void onTemplateChanged(int i, int i2, int i3, int i4) {
        if (this.mRendererManager != null) {
            this.mRendererManager.updateEffectTemplates(i, i2, i3, i4);
        }
    }

    @Override // com.mediatek.camera.p005v2.stream.pip.IPipStream
    public void switchingPip() {
        if (this.mRendererManager != null) {
            this.mRendererManager.switchPIP();
        }
    }

    @Override // com.mediatek.camera.p005v2.stream.pip.IPipStream
    public void setCaptureSize(Size size, Size size2) {
        if (size != null && size2 != null) {
            this.mInputPictureBottomSurface = null;
            this.mInputPictureTopSurface = null;
            this.mRendererManager.setPictureSize(size, size2);
        }
    }

    @Override // com.mediatek.camera.p005v2.stream.pip.IPipStream
    public void onOrientationChanged(int i) {
        this.mGsensorOrientation = i;
        if (this.mRendererManager != null && this.mPipGestureController != null) {
            this.mRendererManager.updateGSensorOrientation(i);
            this.mRendererManager.updateTopGraphic(this.mPipGestureController.getTopGraphicRect(this.mGsensorOrientation));
        }
    }

    @Override // com.mediatek.camera.p005v2.stream.pip.IPipStream
    public void onPreviewAreaChanged(RectF rectF) {
        if (this.mPipGestureController != null) {
            this.mPipGestureController.onPreviewAreaChanged(rectF);
        }
    }

    @Override // com.mediatek.camera.p005v2.stream.pip.IPipStream
    public boolean onDown(float f, float f2) {
        if (this.mPipGestureController == null || this.mRendererManager == null) {
            return false;
        }
        boolean zOnDown = this.mPipGestureController.onDown(f, f2);
        this.mRendererManager.updateTopGraphic(this.mPipGestureController.getTopGraphicRect(this.mGsensorOrientation));
        return zOnDown;
    }

    @Override // com.mediatek.camera.p005v2.stream.pip.IPipStream
    public boolean onScroll(float f, float f2, float f3, float f4) {
        if (this.mPipGestureController == null || this.mRendererManager == null) {
            return false;
        }
        boolean zOnScroll = this.mPipGestureController.onScroll(f, f2, f3, f4);
        this.mRendererManager.updateTopGraphic(this.mPipGestureController.getTopGraphicRect(this.mGsensorOrientation));
        return zOnScroll;
    }

    @Override // com.mediatek.camera.p005v2.stream.pip.IPipStream
    public boolean onSingleTapUp(float f, float f2) {
        if (this.mPipGestureController != null) {
            return this.mPipGestureController.onSingleTapUp(f, f2);
        }
        return false;
    }

    @Override // com.mediatek.camera.p005v2.stream.pip.IPipStream
    public boolean onLongPress(float f, float f2) {
        if (this.mPipGestureController != null) {
            return this.mPipGestureController.onLongPress(f, f2);
        }
        return false;
    }

    @Override // com.mediatek.camera.p005v2.stream.pip.IPipStream
    public boolean onUp() {
        if (this.mPipGestureController == null || this.mRendererManager == null) {
            return false;
        }
        boolean zOnUp = this.mPipGestureController.onUp();
        this.mRendererManager.updateTopGraphic(this.mPipGestureController.getTopGraphicRect(this.mGsensorOrientation));
        return zOnUp;
    }

    @Override // com.mediatek.camera.p005v2.stream.IPreviewStream
    public boolean updatePreviewSize(Size size) {
        LogHelper.m23d(TAG, "[updatePreviewSize]+");
        if (!this.mNextPreviewStream.updatePreviewSize(size) && this.mPreviewWidth > 0 && this.mPreviewHeight > 0) {
            LogHelper.m26i(TAG, "[updatePreviewSize]- not change preview size.");
            return false;
        }
        this.mPreviewWidth = size.getWidth();
        this.mPreviewHeight = size.getHeight();
        this.mInputPreviewBottomSurface = null;
        this.mInputPreviewTopSurface = null;
        this.mOutputPreviewSurface = null;
        this.mRendererManager.setPreviewSize(size);
        if (this.mPipGestureController != null) {
            this.mPipGestureController.setPreviewSize(size);
            this.mRendererManager.updateTopGraphic(this.mPipGestureController.getTopGraphicRect(this.mGsensorOrientation));
        }
        LogHelper.m23d(TAG, "[updatePreviewSize]- preview size changed.");
        return true;
    }

    @Override // com.mediatek.camera.p005v2.stream.IPreviewStream
    public synchronized Map<String, Surface> getPreviewInputSurfaces() {
        LogHelper.m23d(TAG, "[getPreviewInputSurfaces]+");
        HashMap map = new HashMap();
        if (this.mInputPreviewBottomSurface == null || this.mInputPreviewTopSurface == null) {
            SurfaceTexture mainCamPvSt = this.mRendererManager.getMainCamPvSt();
            SurfaceTexture subCamPvSt = this.mRendererManager.getSubCamPvSt();
            if (mainCamPvSt == null || mainCamPvSt == null) {
                LogHelper.m26i(TAG, "[getPreviewInputSurfaces] null surface texture");
                return map;
            }
            this.mInputPreviewBottomSurface = new Surface(mainCamPvSt);
            this.mInputPreviewTopSurface = new Surface(subCamPvSt);
        }
        map.put("PipStreamController.Main", this.mInputPreviewBottomSurface);
        map.put("PipStreamController.Sub", this.mInputPreviewTopSurface);
        LogHelper.m23d(TAG, "[getPreviewInputSurfaces]-");
        return map;
    }

    @Override // com.mediatek.camera.p005v2.stream.IPreviewStream
    public void setPreviewStreamCallback(IPreviewStream.PreviewStreamCallback previewStreamCallback) {
        this.mNextPreviewStream.setPreviewStreamCallback(previewStreamCallback);
    }

    @Override // com.mediatek.camera.p005v2.stream.IPreviewStream
    public void setOneShotPreviewSurfaceCallback(IPreviewStream.PreviewSurfaceCallback previewSurfaceCallback) {
        this.mNextPreviewStream.setOneShotPreviewSurfaceCallback(previewSurfaceCallback);
    }

    @Override // com.mediatek.camera.p005v2.stream.IPreviewStream
    public void setPreviewCallback(IPreviewStream.PreviewCallback previewCallback) {
        this.mNextPreviewStream.setPreviewCallback(previewCallback);
    }

    @Override // com.mediatek.camera.p005v2.stream.IPreviewStream
    public void onFirstFrameAvailable() {
    }

    @Override // com.mediatek.camera.p005v2.stream.ICaptureStream
    public void setCaptureStreamCallback(ICaptureStream.CaptureStreamCallback captureStreamCallback) {
        this.mNextCaptureStream.setCaptureStreamCallback(captureStreamCallback);
    }

    @Override // com.mediatek.camera.p005v2.stream.ICaptureStream
    public boolean updateCaptureSize(Size size, int i) {
        this.mNextCaptureStream.updateCaptureSize(size, i);
        return false;
    }

    @Override // com.mediatek.camera.p005v2.stream.ICaptureStream
    public Map<String, Surface> getCaptureInputSurface() {
        HashMap map = new HashMap();
        if (this.mInputPictureBottomSurface == null || this.mInputPictureTopSurface == null || this.mOutputCaptureSurface == null) {
            this.mInputPictureBottomSurface = new Surface(this.mRendererManager.getMainCamCapSt());
            this.mInputPictureTopSurface = new Surface(this.mRendererManager.getSubCamCapSt());
        }
        this.mOutputCaptureSurface = this.mNextCaptureStream.getCaptureInputSurface().get("CaptureStream.Surface");
        this.mRendererManager.setCaptureOutputSurface(this.mOutputCaptureSurface);
        map.put("PipStreamController.Main", this.mInputPictureBottomSurface);
        map.put("PipStreamController.Sub", this.mInputPictureTopSurface);
        return map;
    }

    @Override // com.mediatek.camera.p005v2.stream.ICaptureStream
    public void releaseCaptureStream() {
        this.mNextCaptureStream.releaseCaptureStream();
    }

    @Override // com.mediatek.camera.p005v2.stream.IRecordStream
    public void registerRecordingObserver(IRecordStream.RecordStreamStatus recordStreamStatus) {
        this.mNextRecordStream.registerRecordingObserver(recordStreamStatus);
    }

    @Override // com.mediatek.camera.p005v2.stream.IRecordStream
    public void unregisterCaptureObserver(IRecordStream.RecordStreamStatus recordStreamStatus) {
        this.mNextRecordStream.unregisterCaptureObserver(recordStreamStatus);
    }

    @Override // com.mediatek.camera.p005v2.stream.IRecordStream
    public void setRecordingProfile(CamcorderProfile camcorderProfile) {
        this.mNextRecordStream.setRecordingProfile(camcorderProfile);
    }

    @Override // com.mediatek.camera.p005v2.stream.IRecordStream
    public void setMaxDuration(int i) {
        this.mNextRecordStream.setMaxDuration(i);
    }

    @Override // com.mediatek.camera.p005v2.stream.IRecordStream
    public void setMaxFileSize(long j) {
        this.mNextRecordStream.setMaxFileSize(j);
    }

    @Override // com.mediatek.camera.p005v2.stream.IRecordStream
    public void setOutputFile(String str) {
        this.mNextRecordStream.setOutputFile(str);
    }

    @Override // com.mediatek.camera.p005v2.stream.IRecordStream
    public void setOutputFile(FileDescriptor fileDescriptor) {
        this.mNextRecordStream.setOutputFile(fileDescriptor);
    }

    @Override // com.mediatek.camera.p005v2.stream.IRecordStream
    public void setOrientationHint(int i) {
        this.mNextRecordStream.setOrientationHint(i);
    }

    @Override // com.mediatek.camera.p005v2.stream.IRecordStream
    public void setMediaRecorderParameters(List<String> list) {
        this.mNextRecordStream.setMediaRecorderParameters(list);
    }

    @Override // com.mediatek.camera.p005v2.stream.IRecordStream
    public void enalbeAudioRecording(boolean z) {
        this.mNextRecordStream.enalbeAudioRecording(z);
    }

    @Override // com.mediatek.camera.p005v2.stream.IRecordStream
    public void setAudioSource(int i) {
        this.mNextRecordStream.setAudioSource(i);
    }

    @Override // com.mediatek.camera.p005v2.stream.IRecordStream
    public void setVideoSource(int i) {
        this.mNextRecordStream.setVideoSource(i);
    }

    @Override // com.mediatek.camera.p005v2.stream.IRecordStream
    public void prepareRecord() {
        this.mNextRecordStream.prepareRecord();
        this.mRendererManager.prepareRecording();
    }

    @Override // com.mediatek.camera.p005v2.stream.IRecordStream
    public void startRecord() {
        this.mRendererManager.startRecording();
        this.mNextRecordStream.startRecord();
    }

    @Override // com.mediatek.camera.p005v2.stream.IRecordStream
    public void pauseRecord() {
        this.mNextRecordStream.pauseRecord();
    }

    @Override // com.mediatek.camera.p005v2.stream.IRecordStream
    public void resumeRecord() {
        this.mNextRecordStream.resumeRecord();
    }

    @Override // com.mediatek.camera.p005v2.stream.IRecordStream
    public void stopRecord(boolean z) {
        this.mRendererManager.stopRecording();
        this.mNextRecordStream.stopRecord(z);
    }

    @Override // com.mediatek.camera.p005v2.stream.IRecordStream
    public Surface getRecordInputSurface() {
        Surface recordInputSurface = this.mNextRecordStream.getRecordInputSurface();
        this.mRendererManager.setRecordingSurface(recordInputSurface);
        return recordInputSurface;
    }

    @Override // com.mediatek.camera.p005v2.stream.pip.IPipStream
    public void setPreviewSurface(Surface surface) {
        this.mRendererManager.setPreviewSurface(surface);
    }

    private class PipRendererCallback implements RendererManager.RendererCallback {
        /* synthetic */ PipRendererCallback(PipStream pipStream, PipRendererCallback pipRendererCallback) {
            this();
        }

        private PipRendererCallback() {
        }

        @Override // com.mediatek.camera.v2.stream.pip.pipwrapping.RendererManager.RendererCallback
        public void onFristFrameAvailable(long j) {
            PipStream.this.mNextPreviewStream.onFirstFrameAvailable();
        }
    }

    private class PipGestureCallback implements IPipGesture.GestureCallback {
        /* synthetic */ PipGestureCallback(PipStream pipStream, PipGestureCallback pipGestureCallback) {
            this();
        }

        private PipGestureCallback() {
        }

        @Override // com.mediatek.camera.v2.stream.pip.IPipGesture.GestureCallback
        public void onTopGraphicTouched() {
            Iterator it = PipStream.this.mPipCallbacks.iterator();
            while (it.hasNext()) {
                ((IPipStream.PipStreamCallback) it.next()).onTopGraphicTouched();
            }
        }

        @Override // com.mediatek.camera.v2.stream.pip.IPipGesture.GestureCallback
        public void onTopGraphicSingleTapUp() {
            Iterator it = PipStream.this.mPipCallbacks.iterator();
            while (it.hasNext()) {
                ((IPipStream.PipStreamCallback) it.next()).onSwitchPipEventReceived();
            }
        }
    }

    private void resetPipStreamStatus() {
        this.mOutputPreviewSurface = null;
        this.mOutputCaptureSurface = null;
        this.mInputPreviewBottomSurface = null;
        this.mInputPreviewTopSurface = null;
        this.mInputPictureBottomSurface = null;
        this.mInputPictureTopSurface = null;
        this.mPreviewWidth = -1;
        this.mPreviewHeight = -1;
    }
}
