package com.mediatek.camera.p005v2.control.focus;

import android.app.Activity;
import android.graphics.RectF;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.view.ViewGroup;
import com.mediatek.camera.p005v2.module.ModuleListener;

/* loaded from: classes.dex */
public interface IFocus {
    void close();

    void configuringSessionRequest(ModuleListener.RequestType requestType, CaptureRequest.Builder builder, ModuleListener.CaptureType captureType, boolean z);

    void onOrientationCompensationChanged(int i);

    void onPreviewAreaChanged(RectF rectF);

    void onPreviewCaptureCompleted(CaptureRequest captureRequest, TotalCaptureResult totalCaptureResult);

    void onPreviewCaptureProgressed(CaptureRequest captureRequest, CaptureResult captureResult);

    void onPreviewCaptureStarted(CaptureRequest captureRequest, long j, long j2);

    void onSingleTapUp(float f, float f2);

    void open(Activity activity, ViewGroup viewGroup, boolean z);

    void pause();

    void resume();
}
