package com.mediatek.camera.util.jpegcodec;

import android.graphics.SurfaceTexture;

/* loaded from: classes.dex */
public abstract class JpegDecoder {
    public abstract void decode(byte[] bArr);

    public abstract void release();

    public static JpegDecoder newInstance(SurfaceTexture surfaceTexture) {
        return new HwJpegDecodeImpl(surfaceTexture);
    }
}
