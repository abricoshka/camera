package com.mediatek.camera.util.jpegcodec;

import android.graphics.SurfaceTexture;
import android.util.Log;

/* loaded from: classes.dex */
class HwJpegDecodeImpl extends JpegDecoder {
    private static final String TAG = HwJpegDecodeImpl.class.getSimpleName();
    private long mNativeContext;

    private static native void native_classInit();

    private native void native_decode(byte[] bArr);

    private native void native_release();

    private native void native_setup(int i, int i2, int i3, byte[] bArr);

    private native void native_setup(SurfaceTexture surfaceTexture);

    static {
        System.loadLibrary("jni_jpegdecoder");
        native_classInit();
    }

    HwJpegDecodeImpl(SurfaceTexture surfaceTexture) {
        native_setup(surfaceTexture);
    }

    HwJpegDecodeImpl(int i, int i2, int i3, byte[] bArr) {
        native_setup(i, i2, i3, bArr);
    }

    @Override // com.mediatek.camera.util.jpegcodec.JpegDecoder
    public void decode(byte[] bArr) {
        Log.i(TAG, "[decode], jpegData:" + bArr);
        native_decode(bArr);
    }

    @Override // com.mediatek.camera.util.jpegcodec.JpegDecoder
    public void release() {
        Log.i(TAG, "[release]");
        native_release();
    }
}
