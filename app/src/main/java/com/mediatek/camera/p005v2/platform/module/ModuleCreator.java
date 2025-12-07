package com.mediatek.camera.p005v2.platform.module;

import com.mediatek.camera.p005v2.module.CameraModule;
import com.mediatek.camera.p005v2.module.DualCameraModule;
import com.mediatek.camera.p005v2.platform.app.AppController;

/* loaded from: classes.dex */
public class ModuleCreator {
    public static ModuleController create(AppController appController, boolean z) {
        if (z) {
            return new DualCameraModule(appController);
        }
        return new CameraModule(appController);
    }
}
