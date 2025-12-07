package com.mediatek.camera.p005v2.platform.device;

import android.app.Activity;
import android.hardware.camera2.CameraManager;
import android.os.Handler;
import com.android.camera.p002v2.bridge.device.CameraDeviceManagerImpl;
import com.mediatek.camera.p005v2.platform.device.CameraDeviceProxy;

/* loaded from: classes.dex */
public abstract class CameraDeviceManager {
    public abstract void open(String str, CameraStateCallback cameraStateCallback, CameraDeviceProxy.CameraSessionCallback cameraSessionCallback, Handler handler);

    public abstract void openSync(String str, CameraStateCallback cameraStateCallback, CameraDeviceProxy.CameraSessionCallback cameraSessionCallback, Handler handler);

    public static abstract class CameraStateCallback {
        public abstract void onError(int i);

        public abstract void onOpened(CameraDeviceProxy cameraDeviceProxy);

        public void onDisconnected(CameraDeviceProxy cameraDeviceProxy) {
        }
    }

    public static CameraDeviceManager get(Activity activity) {
        return create(activity);
    }

    private static CameraDeviceManager create(Activity activity) {
        return new CameraDeviceManagerImpl((CameraManager) activity.getSystemService("camera"), activity);
    }
}
