package com.mediatek.camera.p005v2.stream;

import android.util.Size;
import android.view.Surface;
import java.util.Map;

/* loaded from: classes.dex */
public interface ICaptureStream {

    public interface CaptureStreamCallback {
        void onCaptureCompleted(ImageInfo imageInfo);
    }

    Map<String, Surface> getCaptureInputSurface();

    void releaseCaptureStream();

    void setCaptureStreamCallback(CaptureStreamCallback captureStreamCallback);

    boolean updateCaptureSize(Size size, int i);
}
