package com.mediatek.camera.mode.pip.pipwrapping;

import android.content.Context;
import android.media.Image;
import android.media.ImageReader;
import android.os.ConditionVariable;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Size;
import android.view.Surface;
import com.android.camera.Exif;
import com.mediatek.camera.common.jpeg.encoder.JpegEncoder;
import com.mediatek.camera.p005v2.exif.ExifInterface;
import com.mediatek.camera.util.Log;
import com.mediatek.camera.util.Util;
import com.mediatek.camera.util.jpegcodec.JpegDecoder;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/* loaded from: classes.dex */
public class PipCaptureExecutor {
    private static final String TAG = PipCaptureExecutor.class.getSimpleName();
    private final BlockingQueue<Runnable> mBlockingQueue;
    private final BlockingQueue<CaptureInitRunnable> mCaptureInitRunnableQueue;
    private final Context mContext;
    private byte[] mCurrentJpegHeader;
    private final ImageCallback mImageCallback;
    private final Executor mImageProcessExecutor;
    private ImageReader mImageReader;
    private ConditionVariable mImageReaderSync;
    private final Handler mImageReceiveHandler;
    private boolean mReleased;
    private final RendererManager mRendererManager;
    private JpegEncoder mVssJpegEncoder;
    private int mImageOffered = 0;
    private Object mImageReaderLock = new Object();
    private ImageReader.OnImageAvailableListener mImageAvailableListener = new ImageReader.OnImageAvailableListener() { // from class: com.mediatek.camera.mode.pip.pipwrapping.PipCaptureExecutor.1
        @Override // android.media.ImageReader.OnImageAvailableListener
        public void onImageAvailable(ImageReader imageReader) {
            byte[] bArrAcquireJpegBytesAndClose = null;
            Log.m31d(PipCaptureExecutor.TAG, "[onImageAvailable]+ mCurrentJpegHeader:" + PipCaptureExecutor.this.mCurrentJpegHeader);
            PipCaptureExecutor.this.mCaptureInitRunnableQueue.remove();
            synchronized (PipCaptureExecutor.this.mImageReaderLock) {
                if (PipCaptureExecutor.this.mImageReader != null) {
                    Image imageAcquireNextImage = imageReader.acquireNextImage();
                    if (256 == imageAcquireNextImage.getFormat()) {
                        bArrAcquireJpegBytesAndClose = Util.acquireJpegBytesAndClose(imageAcquireNextImage);
                        if (PipCaptureExecutor.this.mCurrentJpegHeader != null) {
                            try {
                                bArrAcquireJpegBytesAndClose = PipCaptureExecutor.this.setJpegRotationToZeroInExif(PipCaptureExecutor.this.mJpegHeaderWrapper.writeJpegHeader(bArrAcquireJpegBytesAndClose, PipCaptureExecutor.this.mCurrentJpegHeader));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            PipCaptureExecutor.this.mCurrentJpegHeader = null;
                        }
                    }
                    if (bArrAcquireJpegBytesAndClose != null) {
                        PipCaptureExecutor.this.mImageCallback.onPictureTaken(bArrAcquireJpegBytesAndClose);
                    }
                }
            }
            PipCaptureExecutor.this.releaseImageReader();
            Log.m31d(PipCaptureExecutor.TAG, "[onImageAvailable]- mImageReader: " + PipCaptureExecutor.this.mImageReader);
        }
    };
    private JpegEncoder.JpegCallback mVssJpegCallback = new JpegEncoder.JpegCallback() { // from class: com.mediatek.camera.mode.pip.pipwrapping.PipCaptureExecutor.2
        @Override // com.mediatek.camera.common.jpeg.encoder.JpegEncoder.JpegCallback
        public void onJpegAvailable(byte[] bArr) {
            PipCaptureExecutor.this.mImageCallback.onPictureTaken(bArr);
        }
    };
    private JpegEncoder.JpegCallback mCaptureJpegCallback = new JpegEncoder.JpegCallback() { // from class: com.mediatek.camera.mode.pip.pipwrapping.PipCaptureExecutor.3
        @Override // com.mediatek.camera.common.jpeg.encoder.JpegEncoder.JpegCallback
        public void onJpegAvailable(byte[] bArr) {
            Log.m31d(PipCaptureExecutor.TAG, "mCaptureJpegCallback [onJpegAvailable]+");
            PipCaptureExecutor.this.mCaptureInitRunnableQueue.remove();
            if (PipCaptureExecutor.this.mCurrentJpegHeader != null) {
                try {
                    bArr = PipCaptureExecutor.this.mJpegHeaderWrapper.writeJpegHeader(bArr, PipCaptureExecutor.this.mCurrentJpegHeader);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                PipCaptureExecutor.this.mCurrentJpegHeader = null;
            }
            PipCaptureExecutor.this.mImageCallback.onPictureTaken(bArr);
        }
    };
    private final JpegHeaderWrapper mJpegHeaderWrapper = new JpegHeaderWrapper();
    private final HandlerThread mImageReceiveThread = new HandlerThread("Pip-Image-Receive");

    public interface ImageCallback {
        void onPictureTaken(byte[] bArr);

        void unlockNextCapture();
    }

    public PipCaptureExecutor(Context context, RendererManager rendererManager, ImageCallback imageCallback) {
        this.mContext = context;
        this.mRendererManager = rendererManager;
        this.mImageCallback = imageCallback;
        this.mImageReceiveThread.start();
        this.mImageReceiveHandler = new Handler(this.mImageReceiveThread.getLooper());
        this.mBlockingQueue = new LinkedBlockingQueue();
        this.mCaptureInitRunnableQueue = new LinkedBlockingQueue();
        this.mImageProcessExecutor = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, this.mBlockingQueue);
        this.mImageReaderSync = new ConditionVariable();
        this.mImageReaderSync.open();
    }

    public void init() {
        Log.m31d(TAG, "init");
        this.mReleased = false;
        this.mBlockingQueue.clear();
        this.mCaptureInitRunnableQueue.clear();
    }

    public Surface getVssSurface(int i, int i2) {
        if (this.mReleased) {
            Log.m36w(TAG, "getVssSurface return null.");
            return null;
        }
        this.mVssJpegEncoder = JpegEncoder.newInstance(this.mContext, false);
        Surface surfaceConfigInputSurface = this.mVssJpegEncoder.configInputSurface(this.mVssJpegCallback, i, i2, 1);
        this.mVssJpegEncoder.startEncodeAndReleaseWhenDown();
        return surfaceConfigInputSurface;
    }

    public void setUpCapture(Size size, Size size2) throws InterruptedException {
        Log.m31d(TAG, "setUpCapture released:" + this.mReleased);
        if (this.mReleased) {
            return;
        }
        CaptureInitRunnable captureInitRunnable = new CaptureInitRunnable(size, size2);
        try {
            this.mCaptureInitRunnableQueue.put(captureInitRunnable);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        this.mImageProcessExecutor.execute(captureInitRunnable);
    }

    public void offerJpegData(byte[] bArr, Size size, boolean z) {
        Log.m31d(TAG, "[offerJpegData]+ isBottom:" + z + ",peding size:" + this.mBlockingQueue.size() + "released:" + this.mReleased);
        if (this.mReleased) {
            return;
        }
        this.mImageOffered++;
        this.mImageProcessExecutor.execute(new JpegProcessingRunnable(bArr, z));
        if (this.mImageOffered == 2) {
            this.mImageOffered = 0;
            if (!blockingWhenMaxCaptureCountReached(2, false)) {
                this.mImageCallback.unlockNextCapture();
            }
        }
        Log.m31d(TAG, "[offerJpegData]-");
    }

    public void unInit() {
        Log.m31d(TAG, "[unInit]+");
        this.mReleased = true;
        blockingWhenMaxCaptureCountReached(0, true);
        releaseImageReader();
        this.mRendererManager.unInitCapture();
        this.mCurrentJpegHeader = null;
        this.mImageOffered = 0;
        Log.m31d(TAG, "[unInit]- CaptureInit RunnableQueue size:" + this.mCaptureInitRunnableQueue.size() + " mBlockingQueue size:" + this.mBlockingQueue.size());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void releaseImageReader() {
        Log.m31d(TAG, "[releaseImageReader]+");
        synchronized (this.mImageReaderLock) {
            if (this.mImageReader != null) {
                this.mImageReader.close();
                this.mImageReader = null;
            }
        }
        this.mImageReaderSync.open();
        Log.m31d(TAG, "[releaseImageReader]-");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateJpegHeader(byte[] bArr) {
        this.mCurrentJpegHeader = bArr;
    }

    private class CaptureInitRunnable implements Runnable {
        private Size mBottomSz;
        private JpegEncoder mJpegEncoder;
        private Size mTopSz;

        public CaptureInitRunnable(Size size, Size size2) {
            this.mBottomSz = size;
            this.mTopSz = size2;
        }

        @Override // java.lang.Runnable
        public void run() {
            Log.m31d("CaptureInitRunnable", "CaptureInitRunnable [run]+");
            if (JpegEncoder.isHwEncoderSupported(PipCaptureExecutor.this.mContext)) {
                this.mJpegEncoder = JpegEncoder.newInstance(PipCaptureExecutor.this.mContext, true);
            } else {
                this.mJpegEncoder = JpegEncoder.newInstance(PipCaptureExecutor.this.mContext, false);
            }
            PipCaptureExecutor.this.mRendererManager.setCaptureSurface(this.mJpegEncoder.configInputSurface(PipCaptureExecutor.this.mCaptureJpegCallback, this.mBottomSz.getWidth(), this.mBottomSz.getHeight(), PipCaptureExecutor.this.mRendererManager.initCapture(this.mJpegEncoder.getSupportedInputFormats())));
            PipCaptureExecutor.this.mRendererManager.setCaptureSize(this.mBottomSz, this.mTopSz);
            this.mJpegEncoder.startEncodeAndReleaseWhenDown();
            Log.m31d("CaptureInitRunnable", "CaptureInitRunnable [run]-");
        }
    }

    private class JpegProcessingRunnable implements Runnable {
        private boolean mIsBottom;
        private byte[] mJpegData;
        private JpegDecoder mJpegDecoder;

        public JpegProcessingRunnable(byte[] bArr, boolean z) {
            this.mJpegData = bArr;
            this.mIsBottom = z;
        }

        @Override // java.lang.Runnable
        public void run() {
            Log.m31d("JpegProcessingRunnable", "JpegProcessingRunnable [run]+ isBottom:" + this.mIsBottom);
            this.mJpegDecoder = JpegDecoder.newInstance(this.mIsBottom ? PipCaptureExecutor.this.mRendererManager.getBottomCapSt() : PipCaptureExecutor.this.mRendererManager.getTopCapSt());
            PipCaptureExecutor.this.mRendererManager.setJpegRotation(this.mIsBottom, Exif.getOrientation(this.mJpegData));
            if (this.mIsBottom) {
                try {
                    PipCaptureExecutor.this.updateJpegHeader(PipCaptureExecutor.this.mJpegHeaderWrapper.readJpegHeader(this.mJpegData));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            this.mJpegDecoder.decode(this.mJpegData);
            this.mJpegDecoder.release();
            this.mJpegDecoder = null;
            this.mJpegData = null;
            Log.m31d("JpegProcessingRunnable", "JpegProcessingRunnable [run]-");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public byte[] setJpegRotationToZeroInExif(byte[] bArr) throws IOException {
        ExifInterface exifInterface = new ExifInterface();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            exifInterface.readExif(bArr);
            exifInterface.setTagValue(ExifInterface.TAG_ORIENTATION, (short) 1);
            exifInterface.writeExif(bArr, byteArrayOutputStream);
            byteArrayOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return byteArrayOutputStream.toByteArray();
    }

    private boolean blockingWhenMaxCaptureCountReached(int i, boolean z) {
        if (this.mBlockingQueue.size() >= i * 3) {
            if (z) {
                waitDone(this.mImageProcessExecutor);
                return true;
            }
            waitDoneAsync(this.mImageProcessExecutor);
            return true;
        }
        return false;
    }

    private boolean waitDone(Executor executor) {
        final Object obj = new Object();
        Runnable runnable = new Runnable() { // from class: com.mediatek.camera.mode.pip.pipwrapping.PipCaptureExecutor.4
            @Override // java.lang.Runnable
            public void run() {
                synchronized (obj) {
                    obj.notifyAll();
                }
            }
        };
        synchronized (obj) {
            executor.execute(runnable);
            try {
                obj.wait();
            } catch (InterruptedException e) {
                Log.m32e(TAG, "waitDone interrupted");
                return false;
            }
        }
        return true;
    }

    private void waitDoneAsync(Executor executor) {
        executor.execute(new Runnable() { // from class: com.mediatek.camera.mode.pip.pipwrapping.PipCaptureExecutor.5
            @Override // java.lang.Runnable
            public void run() {
                Log.m31d(PipCaptureExecutor.TAG, "waitDoneAsync comes!");
                PipCaptureExecutor.this.mImageCallback.unlockNextCapture();
            }
        });
    }
}
