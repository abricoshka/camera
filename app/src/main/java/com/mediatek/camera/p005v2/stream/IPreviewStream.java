package com.mediatek.camera.p005v2.stream;

import android.util.Size;
import android.view.Surface;
import java.util.Map;

/* loaded from: classes.dex */
public interface IPreviewStream {

    public interface PreviewCallback {
        void surfaceAvailable(Surface surface, int i, int i2);

        void surfaceDestroyed(Surface surface);

        void surfaceSizeChanged(Surface surface, int i, int i2);
    }

    public interface PreviewStreamCallback {
        void onFirstFrameAvailable();
    }

    public interface PreviewSurfaceCallback {
        void onPreviewSufaceIsReady(boolean z);
    }

    Map<String, Surface> getPreviewInputSurfaces();

    void onFirstFrameAvailable();

    void setOneShotPreviewSurfaceCallback(PreviewSurfaceCallback previewSurfaceCallback);

    void setPreviewCallback(PreviewCallback previewCallback);

    void setPreviewStreamCallback(PreviewStreamCallback previewStreamCallback);

    boolean updatePreviewSize(Size size);
}
