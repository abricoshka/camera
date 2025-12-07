package com.mediatek.camera.p005v2.platform.app;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import com.android.camera.p002v2.app.location.LocationManager;
import com.mediatek.camera.p005v2.platform.ModeChangeListener;
import com.mediatek.camera.p005v2.platform.device.CameraDeviceManager;
import com.mediatek.camera.p005v2.platform.module.ModuleUi;
import com.mediatek.camera.p005v2.services.CameraServices;

/* loaded from: classes.dex */
public interface AppController {
    void addPreviewAreaSizeChangedListener(ModuleUi.PreviewAreaChangedListener previewAreaChangedListener);

    void enableKeepScreenOn(boolean z);

    Activity getActivity();

    AppContext getAppContext();

    AppUi getCameraAppUi();

    CameraDeviceManager getCameraManager();

    int getCurrentModeIndex();

    LocationManager getLocationManager();

    int getOldModeIndex();

    CameraServices getServices();

    void notifyNewMedia(Uri uri);

    void onPreviewStarted();

    void removePreviewAreaSizeChangedListener(ModuleUi.PreviewAreaChangedListener previewAreaChangedListener);

    void setModeChangeListener(ModeChangeListener modeChangeListener);

    void setModuleUiListener(ModuleUi moduleUi);

    void setResultExAndFinish(int i);

    void setResultExAndFinish(int i, Intent intent);

    void showErrorAndFinish(int i);

    void updatePreviewSize(int i, int i2);

    void updateStorageSpaceAndHint();
}
