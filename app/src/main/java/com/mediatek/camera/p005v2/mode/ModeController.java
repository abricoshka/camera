package com.mediatek.camera.p005v2.mode;

import android.hardware.camera2.CaptureRequest;
import android.view.Surface;
import com.mediatek.camera.p005v2.module.ModuleListener;
import java.util.List;
import java.util.Map;

/* loaded from: classes.dex */
public interface ModeController {

    public interface ModeGestureListener {
        boolean onDown(float f, float f2);

        boolean onLongPress(float f, float f2);

        boolean onScroll(float f, float f2, float f3, float f4);

        boolean onSingleTapUp(float f, float f2);

        boolean onUp();
    }

    void configuringSessionOutputs(List<Surface> list, boolean z);

    void configuringSessionRequests(Map<ModuleListener.RequestType, CaptureRequest.Builder> map, boolean z);

    void onShutterClicked(boolean z);

    void onShutterLongPressed(boolean z);

    void onShutterPressed(boolean z);

    void onShutterReleased(boolean z);
}
