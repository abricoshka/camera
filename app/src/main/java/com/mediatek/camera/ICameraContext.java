package com.mediatek.camera;

import android.app.Activity;
import com.mediatek.camera.platform.ICameraAppUi;
import com.mediatek.camera.platform.ICameraDeviceManager;
import com.mediatek.camera.platform.IFeatureConfig;
import com.mediatek.camera.platform.IFileSaver;
import com.mediatek.camera.platform.IFocusManager;
import com.mediatek.camera.platform.IModuleCtrl;
import com.mediatek.camera.platform.ISelfTimeManager;

/* loaded from: classes.dex */
public interface ICameraContext {
    Activity getActivity();

    AdditionManager getAdditionManager();

    ICameraAppUi getCameraAppUi();

    ICameraDeviceManager getCameraDeviceManager();

    IFeatureConfig getFeatureConfig();

    IFileSaver getFileSaver();

    IFocusManager getFocusManager();

    IModuleCtrl getModuleController();

    ISelfTimeManager getSelfTimeManager();

    ISettingCtrl getSettingController();
}
