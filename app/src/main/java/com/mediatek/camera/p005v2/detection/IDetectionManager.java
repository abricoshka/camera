package com.mediatek.camera.p005v2.detection;

import android.app.Activity;
import android.graphics.RectF;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.view.ViewGroup;
import com.mediatek.camera.p005v2.module.ModuleListener;
import java.util.Map;

/* loaded from: classes.dex */
public interface IDetectionManager {

    public interface IDetectionListener {
        ModuleListener.RequestType getRepeatingRequestType();

        void requestChangeCaptureRequest(boolean z, ModuleListener.RequestType requestType, ModuleListener.CaptureType captureType);
    }

    void close();

    void configuringSessionRequests(Map<ModuleListener.RequestType, CaptureRequest.Builder> map, ModuleListener.CaptureType captureType);

    void onCaptureCompleted(CaptureRequest captureRequest, TotalCaptureResult totalCaptureResult);

    void onCaptureStarted(CaptureRequest captureRequest, long j, long j2);

    void onLongPressed(float f, float f2);

    void onOrientationChanged(int i);

    void onPreviewAreaChanged(RectF rectF);

    void onSingleTapUp(float f, float f2);

    void open(Activity activity, ViewGroup viewGroup, boolean z);

    void pause();

    void resume();
}
