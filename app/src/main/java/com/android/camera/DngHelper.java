package com.android.camera;

import android.content.Context;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.DngCreator;
import android.location.Location;
import android.util.Size;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/* loaded from: classes.dex */
public class DngHelper {
    private static Context sContext;
    private boolean mCameraClosed;
    private long mCaptureStartTime;
    private CaptureResult mRawCaptureResult;
    private CameraCharacteristics mRawCharacteristic;
    private byte[] mRawImageData;
    private Size mRawSize;

    /* synthetic */ DngHelper(DngHelper dngHelper) {
        this();
    }

    private DngHelper() {
        this.mCaptureStartTime = 0L;
        this.mCameraClosed = false;
    }

    private static class Singleton {
        private static final DngHelper INSTANCE = new DngHelper(null);

        private Singleton() {
        }
    }

    public static DngHelper getInstance(Context context) {
        sContext = context;
        return Singleton.INSTANCE;
    }

    public void setRawdata(byte[] bArr) {
        android.util.Log.d("DngHelper", "rawPictureCallbackTime = " + System.currentTimeMillis() + "ms");
        this.mRawImageData = bArr;
    }

    public byte[] createDngImage(int i, Location location) {
        byte[] byteArray;
        if (this.mRawImageData != null) {
            try {
                if (this.mRawCaptureResult != null) {
                    try {
                        DngCreator dngCreator = new DngCreator(this.mRawCharacteristic, this.mRawCaptureResult);
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        ByteBuffer byteBufferWrap = ByteBuffer.wrap(this.mRawImageData);
                        android.util.Log.d("DngHelper", "createDngImage = " + i);
                        dngCreator.setOrientation(getDngOrientation(i));
                        if (location != null) {
                            dngCreator.setLocation(location);
                        }
                        dngCreator.writeByteBuffer(byteArrayOutputStream, this.mRawSize, byteBufferWrap, 0L);
                        byteArray = byteArrayOutputStream.toByteArray();
                        byteArrayOutputStream.close();
                        byteBufferWrap.clear();
                    } catch (IOException e) {
                        android.util.Log.e("DngHelper", "createDngImage, dng write error");
                        this.mRawImageData = null;
                        this.mRawCaptureResult = null;
                        byteArray = null;
                    }
                    android.util.Log.i("DngHelper", "createDngImage");
                    return byteArray;
                }
            } finally {
                this.mRawImageData = null;
                this.mRawCaptureResult = null;
            }
        }
        return null;
    }

    public int getRawWidth() {
        return this.mRawSize.getWidth();
    }

    public int getRawHeight() {
        return this.mRawSize.getHeight();
    }

    private int getDngOrientation(int i) {
        if (i == 0) {
            return 1;
        }
        if (i == 90) {
            return 6;
        }
        if (i == 180) {
            return 3;
        }
        return 8;
    }
}
