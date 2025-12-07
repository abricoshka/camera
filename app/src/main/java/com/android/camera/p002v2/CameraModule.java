package com.android.camera.p002v2;

import com.android.camera.p002v2.app.AppController;
import com.android.camera.p002v2.module.ModuleController;

/* loaded from: classes.dex */
public abstract class CameraModule implements ModuleController {
    public CameraModule(AppController appController) {
    }

    public boolean onBackPressed() {
        return false;
    }

    public void onPreviewVisibilityChanged(int i) {
    }
}
