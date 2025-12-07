package com.mediatek.camera.p005v2.module;

import com.mediatek.camera.p005v2.control.IControl$IAaaController;

/* loaded from: classes.dex */
public interface ModuleListener {
    IControl$IAaaController get3AController(String str);

    RequestType getRepeatingRequestType();

    void onPreviewSurfaceReady();

    void requestChangeCaptureRequets(boolean z, RequestType requestType, CaptureType captureType);

    void requestChangeCaptureRequets(boolean z, boolean z2, RequestType requestType, CaptureType captureType);

    void requestChangeSessionOutputs(boolean z);

    void requestChangeSessionOutputs(boolean z, boolean z2);

    public enum RequestType {
        PREVIEW,
        STILL_CAPTURE,
        RECORDING,
        VIDEO_SNAP_SHOT,
        ZERO_SHUTTER_DELAY,
        MANUAL;

        /* renamed from: values, reason: to resolve conflict with enum method */
        public static RequestType[] valuesCustom() {
            return values();
        }
    }

    public enum CaptureType {
        CAPTURE,
        CAPTURE_BURST,
        REPEATING_REQUEST,
        REPEATING_BURST;

        /* renamed from: values, reason: to resolve conflict with enum method */
        public static CaptureType[] valuesCustom() {
            return values();
        }
    }
}
