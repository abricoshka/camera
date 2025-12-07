package com.android.camera.externaldevice;

import com.android.camera.CameraActivity;
import java.util.Iterator;
import java.util.Vector;

/* loaded from: classes.dex */
public class ExternalDeviceManager {
    private Vector<IExternalDeviceCtrl> mIDeviceConnected = new Vector<>();

    public ExternalDeviceManager(CameraActivity cameraActivity) {
        this.mIDeviceConnected.add(new WfdDeviceCtrl(cameraActivity));
    }

    public boolean onCreate() {
        Iterator<T> it = this.mIDeviceConnected.iterator();
        while (it.hasNext()) {
            ((IExternalDeviceCtrl) it.next()).onCreate();
        }
        return false;
    }

    public boolean onResume() {
        Iterator<T> it = this.mIDeviceConnected.iterator();
        while (it.hasNext()) {
            ((IExternalDeviceCtrl) it.next()).onResume();
        }
        return false;
    }

    public boolean onPause() {
        Iterator<T> it = this.mIDeviceConnected.iterator();
        while (it.hasNext()) {
            ((IExternalDeviceCtrl) it.next()).onPause();
        }
        return false;
    }

    public void addListener(Object obj) {
        Iterator<T> it = this.mIDeviceConnected.iterator();
        while (it.hasNext()) {
            ((IExternalDeviceCtrl) it.next()).addListener(obj);
        }
    }
}
