package com.mediatek.camera.p005v2.control.whitebalance;

import android.app.Activity;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.view.ViewGroup;
import com.mediatek.camera.p005v2.module.ModuleListener;

/* loaded from: classes.dex */
public interface IWhiteBalance {
    void close();

    void configuringSessionRequest(ModuleListener.RequestType requestType, CaptureRequest.Builder builder, boolean z);

    void onPreviewCaptureCompleted(CaptureRequest captureRequest, TotalCaptureResult totalCaptureResult);

    void onPreviewCaptureStarted(CaptureRequest captureRequest, long j, long j2);

    void open(Activity activity, ViewGroup viewGroup, boolean z);

    void pause();

    void resume();
}
