package com.mediatek.camera.p005v2.detection;

import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import com.mediatek.camera.p005v2.module.ModuleListener;

/* loaded from: classes.dex */
public interface IDetectionCaptureObserver {
    void configuringRequests(CaptureRequest.Builder builder, ModuleListener.RequestType requestType);

    void onCaptureCompleted(CaptureRequest captureRequest, TotalCaptureResult totalCaptureResult);

    void onCaptureStarted(CaptureRequest captureRequest, long j, long j2);
}
