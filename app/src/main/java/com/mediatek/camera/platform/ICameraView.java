package com.mediatek.camera.platform;

import android.app.Activity;

/* loaded from: classes.dex */
public interface ICameraView {
    void hide();

    void init(Activity activity, ICameraAppUi iCameraAppUi, IModuleCtrl iModuleCtrl);

    boolean isShowing();

    void onOrientationChanged(int i);

    void refresh();

    void reset();

    void setEnabled(boolean z);

    void setListener(Object obj);

    void show();

    void uninit();

    boolean update(int i, Object... objArr);
}
