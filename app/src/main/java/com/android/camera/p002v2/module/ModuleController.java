package com.android.camera.p002v2.module;

import android.app.Activity;

/* loaded from: classes.dex */
public interface ModuleController {
    void destroy();

    void init(Activity activity, boolean z, boolean z2);

    void onBeforeCameraPicked(String str);

    void onCameraPicked(String str);

    void onOrientationChanged(int i);

    void pause();

    void resume();
}
