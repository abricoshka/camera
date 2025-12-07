package com.mediatek.camera.common.jpeg.encoder;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.media.Image;
import android.media.ImageReader;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.Log;
import android.view.Surface;
import com.mediatek.camera.common.jpeg.encoder.JpegEncoder;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

@TargetApi(19)
/* loaded from: classes.dex */
class SwJpegEncodeImpl extends JpegEncoder {
    private static final String TAG = "CamAp_" + SwJpegEncodeImpl.class.getSimpleName();
    private boolean mCloseWhenEncodeDone;
    private HandlerThread mImageHandlerThread;
    private android.media.ImageReader mImageReader;
    private JpegEncoder.JpegCallback mJpegCallback;

    @Override // com.mediatek.camera.common.jpeg.encoder.JpegEncoder
    public int[] getSupportedInputFormats() {
        return new int[]{1};
    }

    @Override // com.mediatek.camera.common.jpeg.encoder.JpegEncoder
    public Surface configInputSurface(JpegEncoder.JpegCallback jpegCallback, int i, int i2, int i3) {
        ImageListener imageListener = null;
        this.mJpegCallback = jpegCallback;
        if (this.mImageReader != null && this.mImageReader.getWidth() == i && this.mImageReader.getHeight() == i2 && this.mImageReader.getImageFormat() == i3) {
            Log.i(TAG, "reuse old image reader width = " + i + " height = " + i2);
            return this.mImageReader.getSurface();
        }
        if (this.mImageReader != null) {
            this.mImageReader.close();
        }
        if (this.mImageHandlerThread == null) {
            this.mImageHandlerThread = new HandlerThread("ImageListener");
            this.mImageHandlerThread.start();
        }
        Looper looper = this.mImageHandlerThread.getLooper();
        if (looper == null) {
            throw new RuntimeException("why looper is null ?");
        }
        this.mImageReader = android.media.ImageReader.newInstance(i, i2, i3, 2);
        this.mImageReader.setOnImageAvailableListener(new ImageListener(this, imageListener), new Handler(looper));
        return this.mImageReader.getSurface();
    }

    @Override // com.mediatek.camera.common.jpeg.encoder.JpegEncoder
    public void startEncodeAndReleaseWhenDown() {
        Log.i(TAG, "startEncodeAndReleaseWhenDown");
        this.mCloseWhenEncodeDone = true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public synchronized void close() {
        Log.i(TAG, "close");
        if (this.mImageReader != null) {
            this.mImageReader.close();
            this.mImageReader = null;
            if (this.mImageHandlerThread.isAlive()) {
                this.mImageHandlerThread.quit();
                this.mImageHandlerThread = null;
            }
        }
        if (this.mImageHandlerThread != null && this.mImageHandlerThread.isAlive()) {
            this.mImageHandlerThread.quit();
            this.mImageHandlerThread = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public ByteBuffer acquireRgbBufferAndClose(android.media.Image image) {
        if (image.getPlanes()[0].getPixelStride() * image.getWidth() != image.getPlanes()[0].getRowStride()) {
            byte[] continuousRgbDataFromImage = getContinuousRgbDataFromImage(image);
            ByteBuffer byteBufferAllocateDirect = ByteBuffer.allocateDirect(continuousRgbDataFromImage.length);
            byteBufferAllocateDirect.put(continuousRgbDataFromImage);
            byteBufferAllocateDirect.rewind();
            return byteBufferAllocateDirect;
        }
        return image.getPlanes()[0].getBuffer();
    }

    private byte[] getContinuousRgbDataFromImage(android.media.Image image) {
        Log.i(TAG, "getContinuousRGBADataFromImage begin");
        if (image.getFormat() != 1 && image.getFormat() != 3) {
            Log.i(TAG, "error format = " + image.getFormat());
            return null;
        }
        int format = image.getFormat();
        int width = image.getWidth();
        int height = image.getHeight();
        Image.Plane[] planes = image.getPlanes();
        PixelFormat pixelFormat = new PixelFormat();
        PixelFormat.getPixelFormatInfo(format, pixelFormat);
        ByteBuffer buffer = planes[0].getBuffer();
        int rowStride = planes[0].getRowStride();
        int pixelStride = planes[0].getPixelStride();
        byte[] bArr = new byte[(pixelFormat.bitsPerPixel * (width * height)) / 8];
        int i = rowStride - (pixelStride * width);
        int i2 = 0;
        for (int i3 = 0; i3 < height; i3++) {
            int i4 = width * pixelStride;
            buffer.get(bArr, i2, i4);
            buffer.position(buffer.position() + i);
            i2 += i4;
        }
        Log.i(TAG, "getContinuousRGBADataFromImage end");
        return bArr;
    }

    private class ImageListener implements ImageReader.OnImageAvailableListener {
        /* synthetic */ ImageListener(SwJpegEncodeImpl swJpegEncodeImpl, ImageListener imageListener) {
            this();
        }

        private ImageListener() {
        }

        @Override // android.media.ImageReader.OnImageAvailableListener
        public void onImageAvailable(android.media.ImageReader imageReader) throws IOException {
            Log.i(SwJpegEncodeImpl.TAG, "onImageAvailable thread name = " + Thread.currentThread().getName());
            android.media.Image imageAcquireNextImage = imageReader.acquireNextImage();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ByteBuffer byteBufferAcquireRgbBufferAndClose = SwJpegEncodeImpl.this.acquireRgbBufferAndClose(imageAcquireNextImage);
            Bitmap bitmapCreateBitmap = Bitmap.createBitmap(imageAcquireNextImage.getWidth(), imageAcquireNextImage.getHeight(), Bitmap.Config.ARGB_8888);
            bitmapCreateBitmap.copyPixelsFromBuffer(byteBufferAcquireRgbBufferAndClose);
            byteBufferAcquireRgbBufferAndClose.clear();
            bitmapCreateBitmap.compress(Bitmap.CompressFormat.JPEG, 95, byteArrayOutputStream);
            bitmapCreateBitmap.recycle();
            imageAcquireNextImage.close();
            if (SwJpegEncodeImpl.this.mJpegCallback != null) {
                SwJpegEncodeImpl.this.mJpegCallback.onJpegAvailable(byteArrayOutputStream.toByteArray());
            }
            if (byteArrayOutputStream != null) {
                try {
                    byteArrayOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (SwJpegEncodeImpl.this.mCloseWhenEncodeDone) {
                SwJpegEncodeImpl.this.close();
                SwJpegEncodeImpl.this.mCloseWhenEncodeDone = false;
            }
        }
    }
}
