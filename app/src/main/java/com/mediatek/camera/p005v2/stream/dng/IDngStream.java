package com.mediatek.camera.p005v2.stream.dng;

import android.hardware.camera2.CameraCharacteristics;
import com.mediatek.camera.p005v2.stream.ICaptureStream;

/* loaded from: classes.dex */
public interface IDngStream extends ICaptureStream {
    void updateCameraCharacteristics(CameraCharacteristics cameraCharacteristics);
}
