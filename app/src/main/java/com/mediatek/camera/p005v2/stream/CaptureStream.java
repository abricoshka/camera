package com.mediatek.camera.p005v2.stream;

import android.graphics.Bitmap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Size;
import android.view.Surface;
import com.mediatek.camera.debug.LogHelper;
import com.mediatek.camera.p005v2.stream.ICaptureStream;
import com.mediatek.camera.p005v2.stream.pip.pipwrapping.PipEGLConfigWrapper;
import com.mediatek.camera.p005v2.util.Utils;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import junit.framework.Assert;

/* loaded from: classes.dex */
public class CaptureStream implements ICaptureStream {
    private ICaptureStream.CaptureStreamCallback mCallback;
    private Handler mCaptureHandler;
    private HandlerThread mCaptureHandlerThread;
    private int mCaptureHeight;
    private ImageReader.OnImageAvailableListener mCaptureImageListener = new ImageReader.OnImageAvailableListener() { // from class: com.mediatek.camera.v2.stream.CaptureStream.1
        @Override // android.media.ImageReader.OnImageAvailableListener
        public void onImageAvailable(ImageReader imageReader) {
            int format;
            int width;
            int height;
            byte[] bArrAcquireJpegBytesAndClose = null;
            if (CaptureStream.this.mCallback != null) {
                synchronized (CaptureStream.this.mImageReader) {
                    Image imageAcquireLatestImage = imageReader.acquireLatestImage();
                    format = imageAcquireLatestImage.getFormat();
                    width = imageAcquireLatestImage.getWidth();
                    height = imageAcquireLatestImage.getHeight();
                    if (1 == format) {
                        bArrAcquireJpegBytesAndClose = CaptureStream.this.compressRGBA888ToJpeg(imageAcquireLatestImage);
                    } else if (256 == format) {
                        bArrAcquireJpegBytesAndClose = Utils.acquireJpegBytesAndClose(imageAcquireLatestImage);
                    }
                }
                CaptureStream.this.mCallback.onCaptureCompleted(new ImageInfo(bArrAcquireJpegBytesAndClose, width, height, format));
            }
        }
    };
    private Surface mCaptureSurface;
    private int mCaptureWidth;
    private ImageReader mImageReader;
    private static final LogHelper.Tag TAG = new LogHelper.Tag(CaptureStream.class.getSimpleName());
    private static int MAX_CAPTURE_IMAGES = 2;

    @Override // com.mediatek.camera.p005v2.stream.ICaptureStream
    public void setCaptureStreamCallback(ICaptureStream.CaptureStreamCallback captureStreamCallback) {
        this.mCallback = captureStreamCallback;
    }

    @Override // com.mediatek.camera.p005v2.stream.ICaptureStream
    public synchronized boolean updateCaptureSize(Size size, int i) {
        LogHelper.m26i(TAG, "[updateCaptureSize]+ size:" + size.getWidth() + "x" + size.getHeight());
        Assert.assertNotNull(size);
        checkSupportedFormat(i);
        if (this.mCaptureHandler == null) {
            this.mCaptureHandlerThread = new HandlerThread("ImageReaderStream.CaptureThread");
            this.mCaptureHandlerThread.start();
            this.mCaptureHandler = new Handler(this.mCaptureHandlerThread.getLooper());
        }
        int width = size.getWidth();
        int height = size.getHeight();
        if (this.mImageReader != null && this.mImageReader.getWidth() == width && this.mImageReader.getHeight() == height && this.mImageReader.getImageFormat() == i) {
            LogHelper.m26i(TAG, "[updateCaptureSize]- configure the same size, skip :  width  = " + width + " height = " + height + " format = " + i);
            return false;
        }
        this.mCaptureWidth = width;
        this.mCaptureHeight = height;
        this.mImageReader = ImageReader.newInstance(this.mCaptureWidth, this.mCaptureHeight, i, MAX_CAPTURE_IMAGES);
        this.mImageReader.setOnImageAvailableListener(this.mCaptureImageListener, this.mCaptureHandler);
        this.mCaptureSurface = this.mImageReader.getSurface();
        LogHelper.m26i(TAG, "[updateCaptureSize]- mCaptureSurface:" + this.mCaptureSurface);
        return true;
    }

    @Override // com.mediatek.camera.p005v2.stream.ICaptureStream
    public Map<String, Surface> getCaptureInputSurface() {
        LogHelper.m26i(TAG, "[getCaptureInputSurface]+");
        HashMap map = new HashMap();
        if (this.mCaptureSurface == null) {
            throw new IllegalStateException("You should call CaptureStream.updateCaptureSize firstly, when get input capture surface");
        }
        map.put("CaptureStream.Surface", this.mCaptureSurface);
        LogHelper.m26i(TAG, "[getCaptureInputSurface]- mCaptureSurface:" + this.mCaptureSurface);
        return map;
    }

    private void checkSupportedFormat(int i) {
        boolean z = false;
        switch (i) {
            case 1:
            case 256:
                z = true;
                break;
        }
        if (!z) {
            throw new IllegalArgumentException("ImageReaderStream unsupported format : " + i);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public byte[] compressRGBA888ToJpeg(Image image) throws Throwable {
        ByteBuffer buffer;
        ByteArrayOutputStream byteArrayOutputStream;
        byte[] byteArray = null;
        try {
            Bitmap bitmapCreateBitmap = Bitmap.createBitmap(image.getWidth(), image.getHeight(), PipEGLConfigWrapper.getInstance().getBitmapConfig());
            if (image.getPlanes()[0].getPixelStride() * image.getWidth() != image.getPlanes()[0].getRowStride()) {
                LogHelper.m26i(TAG, "getPixelStride = " + image.getPlanes()[0].getPixelStride() + " getRowStride = " + image.getPlanes()[0].getRowStride());
                byte[] continuousRGBADataFromImage = Utils.getContinuousRGBADataFromImage(image);
                buffer = ByteBuffer.allocateDirect(continuousRGBADataFromImage.length);
                buffer.put(continuousRGBADataFromImage);
                buffer.rewind();
            } else {
                buffer = image.getPlanes()[0].getBuffer();
            }
            System.gc();
            bitmapCreateBitmap.copyPixelsFromBuffer(buffer);
            buffer.clear();
            try {
                byteArrayOutputStream = new ByteArrayOutputStream();
                try {
                    bitmapCreateBitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
                    bitmapCreateBitmap.recycle();
                    byteArray = byteArrayOutputStream.toByteArray();
                    if (byteArrayOutputStream != null) {
                        try {
                            byteArrayOutputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    System.gc();
                    image.close();
                } catch (Throwable th) {
                    th = th;
                    byteArrayOutputStream.toByteArray();
                    if (byteArrayOutputStream != null) {
                        try {
                            byteArrayOutputStream.close();
                        } catch (IOException e2) {
                            e2.printStackTrace();
                        }
                    }
                    throw th;
                }
            } catch (Throwable th2) {
                th = th2;
                byteArrayOutputStream = null;
            }
        } catch (IllegalArgumentException e3) {
            e3.printStackTrace();
        }
        return byteArray;
    }

    @Override // com.mediatek.camera.p005v2.stream.ICaptureStream
    public void releaseCaptureStream() {
        LogHelper.m26i(TAG, "releaseCaptureStream");
        if (this.mImageReader != null) {
            this.mImageReader.close();
            this.mImageReader = null;
        }
        if (this.mCaptureHandlerThread != null) {
            this.mCaptureHandlerThread.quitSafely();
            this.mCaptureHandler = null;
        }
    }
}
