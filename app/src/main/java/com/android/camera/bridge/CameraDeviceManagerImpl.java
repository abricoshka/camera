package com.android.camera.bridge;

import android.app.Activity;
import android.hardware.Camera;
import com.android.camera.CameraHolder;
import com.mediatek.camera.platform.ICameraDeviceManager;
import com.mediatek.camera.util.Log;
import junit.framework.Assert;

/* loaded from: classes.dex */
public class CameraDeviceManagerImpl implements ICameraDeviceManager {
    private Activity mActivity;
    private CameraDeviceCtrl mCameraDeviceCtrl;
    private CameraHolder mCameraHolder;
    private ICameraDeviceManager.ICameraDevice mICurCameraDevice = null;
    private ICameraDeviceManager.ICameraDevice mITopCameraDevice = null;

    public CameraDeviceManagerImpl(Activity activity, CameraDeviceCtrl cameraDeviceCtrl) {
        Assert.assertNotNull(activity);
        Assert.assertNotNull(cameraDeviceCtrl);
        this.mActivity = activity;
        this.mCameraDeviceCtrl = cameraDeviceCtrl;
        this.mCameraHolder = cameraDeviceCtrl.getCameraHolder();
    }

    @Override // com.mediatek.camera.platform.ICameraDeviceManager
    public void onCameraCloseDone() {
        this.mICurCameraDevice = null;
        this.mITopCameraDevice = null;
    }

    @Override // com.mediatek.camera.platform.ICameraDeviceManager
    public int getBackCameraId() {
        return this.mCameraHolder.getBackCameraId();
    }

    @Override // com.mediatek.camera.platform.ICameraDeviceManager
    public int getFrontCameraId() {
        return this.mCameraHolder.getFrontCameraId();
    }

    @Override // com.mediatek.camera.platform.ICameraDeviceManager
    public int getCurrentCameraId() {
        return this.mCameraDeviceCtrl.getCameraId();
    }

    @Override // com.mediatek.camera.platform.ICameraDeviceManager
    public Camera.CameraInfo getCameraInfo(int i) {
        return this.mCameraHolder.getCameraInfo()[i];
    }

    @Override // com.mediatek.camera.platform.ICameraDeviceManager
    public Camera.CameraInfo[] getCameraInfo() {
        return this.mCameraHolder.getCameraInfo();
    }

    @Override // com.mediatek.camera.platform.ICameraDeviceManager
    public int getNumberOfCameras() {
        return this.mCameraHolder.getNumberOfCameras();
    }

    @Override // com.mediatek.camera.platform.ICameraDeviceManager
    public ICameraDeviceManager.ICameraDevice getCameraDevice(int i) {
        if (-1 == i) {
            Log.m36w("CameraDeviceManagerImpl", "[getCameraDevice] cameraId is UNKNOWN,return!");
            return null;
        }
        ICameraDeviceExt curCameraDevice = this.mCameraDeviceCtrl.getCurCameraDevice();
        ICameraDeviceExt topCameraDevice = this.mCameraDeviceCtrl.getTopCameraDevice();
        int cameraId = curCameraDevice.getCameraId();
        int cameraId2 = topCameraDevice.getCameraId();
        if (this.mICurCameraDevice == null || cameraId != this.mICurCameraDevice.getCameraId()) {
            Log.m31d("CameraDeviceManagerImpl", "[getCameraDevice] new mICurCameraDevice.curCameraId:" + cameraId);
            this.mICurCameraDevice = new CameraDeviceImpl(this.mActivity, curCameraDevice);
        }
        if (this.mITopCameraDevice == null || cameraId2 != this.mITopCameraDevice.getCameraId()) {
            Log.m31d("CameraDeviceManagerImpl", "[getCameraDevice] new mITopCameraDevice.topCameraId:" + cameraId2);
            this.mITopCameraDevice = new CameraDeviceImpl(this.mActivity, topCameraDevice);
        }
        if (this.mICurCameraDevice.getCameraId() == i) {
            return this.mICurCameraDevice;
        }
        if (this.mITopCameraDevice.getCameraId() == i) {
            return this.mITopCameraDevice;
        }
        Log.m36w("CameraDeviceManagerImpl", "[getCameraDevice]return null,cameraId = " + i + ",mICurCameraDevice.getCameraId() = " + this.mICurCameraDevice.getCameraId() + ",mITopCameraDevice.getCameraId() = " + this.mITopCameraDevice.getCameraId());
        return null;
    }
}
