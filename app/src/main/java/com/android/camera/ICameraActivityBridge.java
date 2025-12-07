package com.android.camera;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.KeyEvent;

/* loaded from: classes.dex */
public interface ICameraActivityBridge {
    void onActivityResult(int i, int i2, Intent intent);

    boolean onBackPressed();

    void onConfigurationChanged(Configuration configuration);

    void onCreate(Bundle bundle);

    void onDestroy();

    boolean onKeyDown(int i, KeyEvent keyEvent);

    boolean onKeyUp(int i, KeyEvent keyEvent);

    void onPause();

    void onRequestLocationPermissionResult(String[] strArr, int[] iArr);

    void onRestart();

    void onResume();

    boolean onUserInteraction();
}
