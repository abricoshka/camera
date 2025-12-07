package com.android.camera;

import android.app.Activity;
import android.hardware.Camera;
import com.mediatek.camera.R;

/* loaded from: classes.dex */
public class CameraErrorCallback implements Camera.ErrorCallback {
    private final Activity mActivity;

    public CameraErrorCallback(Activity activity) {
        this.mActivity = activity;
    }

    @Override // android.hardware.Camera.ErrorCallback
    public void onError(int i, Camera camera) {
        android.util.Log.e("CameraErrorCallback", "onError got camera error callback. error = " + i);
        if ((100 == i || 2 == i || 1 == i) && !this.mActivity.isFinishing()) {
            this.mActivity.runOnUiThread(new Runnable() { // from class: com.android.camera.CameraErrorCallback.1
                @Override // java.lang.Runnable
                public void run() {
                    Util.showErrorAndFinish(CameraErrorCallback.this.mActivity, R.string.cannot_connect_camera_new);
                }
            });
        }
    }
}
