package com.mediatek.camera.p005v2.stream.dng;

import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Size;
import android.view.Surface;
import com.mediatek.camera.debug.LogHelper;
import com.mediatek.camera.p005v2.stream.ICaptureStream;
import com.mediatek.camera.p005v2.stream.ImageInfo;
import com.mediatek.camera.p005v2.util.Utils;
import java.util.Map;

/* loaded from: classes.dex */
public class DngStream implements IDngStream {
    private ICaptureStream.CaptureStreamCallback mCallback;
    private ICaptureStream mCaptureStream;
    private CameraCharacteristics mCharacteristics;
    private Handler mRawCaptureHandler;
    private HandlerThread mRawCaptureHandlerThread;
    private int mRawCaptureHeight;
    private ImageReader.OnImageAvailableListener mRawCaptureImageListener = new ImageReader.OnImageAvailableListener() { // from class: com.mediatek.camera.v2.stream.dng.DngStream.1
        @Override // android.media.ImageReader.OnImageAvailableListener
        public void onImageAvailable(ImageReader imageReader) {
            LogHelper.m23d(DngStream.TAG, "mRawCaptureImageListener mCallback = " + DngStream.this.mCallback);
            if (DngStream.this.mCallback == null) {
                return;
            }
            Image imageAcquireLatestImage = imageReader.acquireLatestImage();
            ImageInfo imageInfo = new ImageInfo(Utils.acquireRawBytesAndClose(imageAcquireLatestImage), imageAcquireLatestImage.getWidth(), imageAcquireLatestImage.getHeight(), imageAcquireLatestImage.getFormat());
            synchronized (DngStream.this.mCallback) {
                DngStream.this.mCallback.onCaptureCompleted(imageInfo);
            }
        }
    };
    private Surface mRawCaptureSurface;
    private int mRawCaptureWidth;
    private ImageReader mRawImageReader;
    private Size mRawSize;
    private static final LogHelper.Tag TAG = new LogHelper.Tag(DngStream.class.getSimpleName());
    private static int MAX_RAW_CAPTURE_IMAGES = 1;

    public DngStream(ICaptureStream iCaptureStream) {
        this.mCaptureStream = iCaptureStream;
    }

    @Override // com.mediatek.camera.p005v2.stream.ICaptureStream
    public void setCaptureStreamCallback(ICaptureStream.CaptureStreamCallback captureStreamCallback) {
        this.mCallback = captureStreamCallback;
        if (this.mCaptureStream != null) {
            this.mCaptureStream.setCaptureStreamCallback(captureStreamCallback);
        }
    }

    @Override // com.mediatek.camera.p005v2.stream.ICaptureStream
    public boolean updateCaptureSize(Size size, int i) {
        boolean zUpdateCaptureSize = this.mCaptureStream.updateCaptureSize(size, i);
        if (this.mRawCaptureHandler == null) {
            this.mRawCaptureHandlerThread = new HandlerThread("ImageReaderStream.RawCaptureThread");
            this.mRawCaptureHandlerThread.start();
            this.mRawCaptureHandler = new Handler(this.mRawCaptureHandlerThread.getLooper());
        }
        if (this.mRawImageReader != null && this.mRawCaptureWidth == this.mRawSize.getWidth() && this.mRawCaptureHeight == this.mRawSize.getHeight()) {
            LogHelper.m23d(TAG, "[updateCaptureSize]- configure the same size, skip :  width  = " + this.mRawCaptureWidth + " height = " + this.mRawCaptureHeight);
            return zUpdateCaptureSize;
        }
        this.mRawCaptureWidth = this.mRawSize.getWidth();
        this.mRawCaptureHeight = this.mRawSize.getHeight();
        if (this.mRawImageReader != null) {
            this.mRawImageReader.close();
            this.mRawImageReader = null;
        }
        LogHelper.m23d(TAG, "[updateCaptureSize]-raw size:" + this.mRawSize.getWidth() + "x" + this.mRawSize.getHeight());
        ImageReader imageReader = this.mRawImageReader;
        this.mRawImageReader = ImageReader.newInstance(this.mRawSize.getWidth(), this.mRawSize.getHeight(), 32, MAX_RAW_CAPTURE_IMAGES);
        this.mRawImageReader.setOnImageAvailableListener(this.mRawCaptureImageListener, this.mRawCaptureHandler);
        this.mRawCaptureSurface = this.mRawImageReader.getSurface();
        LogHelper.m23d(TAG, "[updateCaptureSize]-Raw reader:" + this.mRawImageReader);
        return true;
    }

    @Override // com.mediatek.camera.p005v2.stream.ICaptureStream
    public Map<String, Surface> getCaptureInputSurface() {
        Map<String, Surface> captureInputSurface = this.mCaptureStream.getCaptureInputSurface();
        if (this.mRawCaptureSurface == null) {
            throw new IllegalStateException("You should call CaptureStream.updateCaptureSize firstly, when get input capture surface");
        }
        LogHelper.m23d(TAG, "getCaptureInputSurface:" + this.mRawCaptureSurface);
        captureInputSurface.put("PreviewStream.RawSurface", this.mRawCaptureSurface);
        return captureInputSurface;
    }

    @Override // com.mediatek.camera.p005v2.stream.ICaptureStream
    public void releaseCaptureStream() {
        if (this.mRawImageReader != null) {
            this.mRawImageReader.close();
            this.mRawImageReader = null;
            this.mRawCaptureSurface = null;
        }
        if (this.mRawCaptureHandlerThread != null) {
            this.mRawCaptureHandlerThread.quitSafely();
            this.mRawCaptureHandler = null;
        }
    }

    @Override // com.mediatek.camera.p005v2.stream.dng.IDngStream
    public void updateCameraCharacteristics(CameraCharacteristics cameraCharacteristics) {
        this.mCharacteristics = cameraCharacteristics;
        Size[] outputSizes = ((StreamConfigurationMap) this.mCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)).getOutputSizes(32);
        for (Size size : outputSizes) {
            LogHelper.m23d(TAG, "raw supported size:" + size);
        }
        this.mRawSize = outputSizes[0];
    }
}
