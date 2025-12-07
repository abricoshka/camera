package com.android.camera.p002v2.app;

import android.os.Handler;

/* loaded from: classes.dex */
public interface OrientationManager {

    public interface OnOrientationChangeListener {
        void onOrientationChanged(int i);
    }

    void addOnOrientationChangeListener(Handler handler, OnOrientationChangeListener onOrientationChangeListener);

    void lockOrientation();

    void pause();

    void removeOnOrientationChangeListener(Handler handler, OnOrientationChangeListener onOrientationChangeListener);

    void resume();

    void unlockOrientation();
}
