package com.android.camera.externaldevice;

/* loaded from: classes.dex */
public interface IExternalDeviceCtrl {

    public interface Listener {
        void onStateChanged(boolean z);
    }

    void addListener(Object obj);

    boolean onCreate();

    boolean onPause();

    boolean onResume();
}
