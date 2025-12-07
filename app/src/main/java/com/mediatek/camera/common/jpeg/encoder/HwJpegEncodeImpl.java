package com.mediatek.camera.common.jpeg.encoder;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.Log;
import android.view.Surface;
import com.mediatek.camera.common.jpeg.encoder.ImageReader;
import com.mediatek.camera.common.jpeg.encoder.JpegEncoder;
import java.nio.ByteBuffer;

/* loaded from: classes.dex */
class HwJpegEncodeImpl extends JpegEncoder {
    private static final String TAG = HwJpegEncodeImpl.class.getSimpleName();
    private HandlerThread mImageHandlerThread;
    private ImageReader mImageReader;
    private JpegEncoder.JpegCallback mJpegCallback;

    HwJpegEncodeImpl(Context context) {
    }

    @Override // com.mediatek.camera.common.jpeg.encoder.JpegEncoder
    public int[] getSupportedInputFormats() {
        return new int[]{1, 842094169, 3};
    }

    @Override // com.mediatek.camera.common.jpeg.encoder.JpegEncoder
    public Surface configInputSurface(JpegEncoder.JpegCallback jpegCallback, int i, int i2, int i3) {
        ImageListener imageListener = null;
        Log.d(TAG, "[configInputSurface] jpegCallback:" + jpegCallback + ",width:" + i + ",height:" + i2 + ",format:" + i3);
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
        this.mImageReader = ImageReader.newInstance(i, i2, i3, 2);
        this.mImageReader.setOnImageAvailableListener(new ImageListener(this, imageListener), new Handler(looper));
        return this.mImageReader.getSurface();
    }

    @Override // com.mediatek.camera.common.jpeg.encoder.JpegEncoder
    public void startEncodeAndReleaseWhenDown() {
    }

    private class ImageListener implements ImageReader.OnImageAvailableListener {
        /* synthetic */ ImageListener(HwJpegEncodeImpl hwJpegEncodeImpl, ImageListener imageListener) {
            this();
        }

        private ImageListener() {
        }

        @Override // com.mediatek.camera.common.jpeg.encoder.ImageReader.OnImageAvailableListener
        public void onImageAvailable(ImageReader imageReader) {
            Log.d(HwJpegEncodeImpl.TAG, "[onImageAvailable] reader:" + imageReader);
            HwJpegEncodeImpl.this.mJpegCallback.onJpegAvailable(HwJpegEncodeImpl.this.acquireJpegBytesAndClose(imageReader.acquireNextImage()));
            Log.d(HwJpegEncodeImpl.TAG, "[onImageAvailable] end");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public byte[] acquireJpegBytesAndClose(Image image) {
        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
        Log.d(TAG, "<acquireJpegBytesAndClose> start get buffer,size:" + buffer.remaining());
        byte[] bArr = new byte[buffer.remaining()];
        buffer.get(bArr);
        buffer.rewind();
        image.close();
        return bArr;
    }
}
