package com.android.camera.p002v2.bridge;

import android.app.Activity;
import com.android.camera.p002v2.CameraModule;
import com.android.camera.p002v2.app.AppController;
import com.android.camera.p002v2.module.ModuleController;
import com.mediatek.camera.p005v2.platform.module.ModuleCreator;
import junit.framework.Assert;

/* loaded from: classes.dex */
public class ModuleControllerAdapter extends CameraModule implements ModuleController {
    private final com.mediatek.camera.p005v2.platform.module.ModuleController mCurrentModule;

    public ModuleControllerAdapter(AppController appController, int i) {
        super(appController);
        Assert.assertNotNull(appController);
        this.mCurrentModule = ModuleCreator.create(appController.getAppControllerAdapter(), i == 1);
    }

    @Override // com.android.camera.p002v2.module.ModuleController
    public void init(Activity activity, boolean z, boolean z2) {
        this.mCurrentModule.open(activity, z, z2);
    }

    @Override // com.android.camera.p002v2.module.ModuleController
    public void resume() {
        this.mCurrentModule.resume();
    }

    @Override // com.android.camera.p002v2.module.ModuleController
    public void pause() {
        this.mCurrentModule.pause();
    }

    @Override // com.android.camera.p002v2.module.ModuleController
    public void destroy() {
        this.mCurrentModule.close();
    }

    @Override // com.android.camera.p002v2.CameraModule
    public void onPreviewVisibilityChanged(int i) {
        int i2 = 0;
        switch (i) {
            case 1:
                i2 = 1;
                break;
        }
        this.mCurrentModule.onPreviewVisibilityChanged(i2);
    }

    @Override // com.android.camera.p002v2.module.ModuleController
    public void onOrientationChanged(int i) {
        this.mCurrentModule.onOrientationChanged(i);
    }

    @Override // com.android.camera.p002v2.CameraModule
    public boolean onBackPressed() {
        return this.mCurrentModule.onBackPressed();
    }

    @Override // com.android.camera.p002v2.module.ModuleController
    public void onBeforeCameraPicked(String str) {
        this.mCurrentModule.onBeforeCameraPicked(str);
    }

    @Override // com.android.camera.p002v2.module.ModuleController
    public void onCameraPicked(String str) {
        this.mCurrentModule.onCameraPicked(str);
    }
}
