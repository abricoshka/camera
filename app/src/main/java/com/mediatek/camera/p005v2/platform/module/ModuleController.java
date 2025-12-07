package com.mediatek.camera.p005v2.platform.module;

import android.app.Activity;

/* loaded from: classes.dex */
public interface ModuleController {
    void close();

    boolean onBackPressed();

    void onBeforeCameraPicked(String str);

    void onCameraPicked(String str);

    void onOrientationChanged(int i);

    void onPreviewVisibilityChanged(int i);

    void open(Activity activity, boolean z, boolean z2);

    void pause();

    void resume();
}
