package com.mediatek.camera.p005v2.control;

import com.mediatek.camera.p005v2.module.ModuleListener;

/* loaded from: classes.dex */
public interface IControl$IAaaListener {
    ModuleListener.RequestType getRepeatingRequestType();

    void requestChangeCaptureRequets(boolean z, ModuleListener.RequestType requestType, ModuleListener.CaptureType captureType);
}
