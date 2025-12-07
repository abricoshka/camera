package com.android.camera;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;

/* loaded from: classes.dex */
public class DisableCameraReceiver extends BroadcastReceiver {
    private static final String[] ACTIVITIES = {"com.android.camera.CameraLauncher", "com.android.camera.VideoCamera", "com.android.camera.CameraActivity", "com.android.camera.SecureCameraActivity", "com.android.camera.CaptureActivity"};

    private void disableComponent(Context context, String str) {
    }

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        if (!(FeatureSwitcher.isOnlyCheckBackCamera() ? hasBackCamera() : hasCamera())) {
            android.util.Log.d("DisableCameraReceiver", "disable all camera activities");
            for (int i = 0; i < ACTIVITIES.length; i++) {
                disableComponent(context, ACTIVITIES[i]);
            }
        }
        disableComponent(context, "com.android.camera.DisableCameraReceiver");
    }

    private boolean hasCamera() {
        int numberOfCameras = Camera.getNumberOfCameras();
        android.util.Log.d("DisableCameraReceiver", "number of camera: " + numberOfCameras);
        return numberOfCameras > 0;
    }

    private boolean hasBackCamera() {
        int numberOfCameras = Camera.getNumberOfCameras();
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == 0) {
                android.util.Log.d("DisableCameraReceiver", "back camera found: " + i);
                return true;
            }
        }
        android.util.Log.d("DisableCameraReceiver", "no back camera");
        return false;
    }
}
