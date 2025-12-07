package com.mediatek.camera.p005v2.platform.device;

import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CaptureRequest;
import android.view.Surface;
import com.mediatek.camera.p005v2.module.ModuleListener;
import java.util.List;

/* loaded from: classes.dex */
public interface CameraDeviceProxy {

    public interface CameraSessionCallback {
        void configuringSessionOutputs(List<Surface> list);

        CameraCaptureSession.CaptureCallback configuringSessionRequests(CaptureRequest.Builder builder, ModuleListener.RequestType requestType, ModuleListener.CaptureType captureType);

        void onSessionActive();

        void onSessionConfigured();
    }

    void close();

    void requestChangeCaptureRequets(boolean z, ModuleListener.RequestType requestType, ModuleListener.CaptureType captureType);

    void requestChangeSessionOutputs(boolean z);
}
