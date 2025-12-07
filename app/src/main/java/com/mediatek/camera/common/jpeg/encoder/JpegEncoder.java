package com.mediatek.camera.common.jpeg.encoder;

import android.content.Context;
import android.view.Surface;

/* loaded from: classes.dex */
public abstract class JpegEncoder {

    public interface JpegCallback {
        void onJpegAvailable(byte[] bArr);
    }

    public abstract int[] getSupportedInputFormats();

    public abstract void startEncodeAndReleaseWhenDown();

    public static boolean isHwEncoderSupported(Context context) {
        return true;
    }

    public static JpegEncoder newInstance(Context context, boolean z) {
        if (z) {
            return new HwJpegEncodeImpl(context);
        }
        return new SwJpegEncodeImpl();
    }

    public Surface configInputSurface(JpegCallback jpegCallback, int i, int i2, int i3) {
        return null;
    }
}
