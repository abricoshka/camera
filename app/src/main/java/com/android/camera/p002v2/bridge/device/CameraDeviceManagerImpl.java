package com.android.camera.p002v2.bridge.device;

import android.app.Activity;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.os.ConditionVariable;
import android.os.Handler;
import android.os.Looper;
import com.mediatek.camera.debug.LogHelper;
import com.mediatek.camera.p005v2.platform.device.CameraDeviceManager;
import com.mediatek.camera.p005v2.platform.device.CameraDeviceProxy;

/* loaded from: classes.dex */
public class CameraDeviceManagerImpl extends CameraDeviceManager {
    private static final LogHelper.Tag TAG = new LogHelper.Tag("DeviceManagerImpl");
    private final Activity mActivity;
    private final CameraManager mCameraManager;
    private final ConditionVariable mOpenConditionVariable = new ConditionVariable();
    private int mRetryCount = 0;

    public CameraDeviceManagerImpl(CameraManager cameraManager, Activity activity) {
        this.mCameraManager = cameraManager;
        this.mActivity = activity;
    }

    @Override // com.mediatek.camera.p005v2.platform.device.CameraDeviceManager
    public void open(String str, CameraDeviceManager.CameraStateCallback cameraStateCallback, CameraDeviceProxy.CameraSessionCallback cameraSessionCallback, Handler handler) throws CameraAccessException {
        this.mRetryCount = 0;
        doOpenCamera(str, cameraStateCallback, cameraSessionCallback, handler);
    }

    @Override // com.mediatek.camera.p005v2.platform.device.CameraDeviceManager
    public void openSync(String str, final CameraDeviceManager.CameraStateCallback cameraStateCallback, final CameraDeviceProxy.CameraSessionCallback cameraSessionCallback, final Handler handler) {
        if (handler.getLooper() == Looper.myLooper()) {
            throw new IllegalArgumentException("handler's looper must not be the current looper");
        }
        this.mOpenConditionVariable.close();
        try {
            this.mCameraManager.openCamera(str, new CameraDevice.StateCallback() { // from class: com.android.camera.v2.bridge.device.CameraDeviceManagerImpl.1
                @Override // android.hardware.camera2.CameraDevice.StateCallback
                public void onOpened(CameraDevice cameraDevice) {
                    LogHelper.m26i(CameraDeviceManagerImpl.TAG, "onOpened");
                    cameraStateCallback.onOpened(new CameraDeviceProxyImpl(CameraDeviceManagerImpl.this.mActivity, cameraDevice, cameraSessionCallback, handler));
                    CameraDeviceManagerImpl.this.mOpenConditionVariable.open();
                }

                @Override // android.hardware.camera2.CameraDevice.StateCallback
                public void onError(CameraDevice cameraDevice, int i) {
                    LogHelper.m26i(CameraDeviceManagerImpl.TAG, "onError CameraDevice:" + cameraDevice + ", error:" + i);
                    cameraStateCallback.onError(i);
                    CameraDeviceManagerImpl.this.mOpenConditionVariable.open();
                }

                @Override // android.hardware.camera2.CameraDevice.StateCallback
                public void onDisconnected(CameraDevice cameraDevice) {
                    LogHelper.m26i(CameraDeviceManagerImpl.TAG, "onDisconnected CameraDevice:" + cameraDevice);
                    cameraStateCallback.onDisconnected(null);
                    CameraDeviceManagerImpl.this.mOpenConditionVariable.open();
                }
            }, handler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        } finally {
            this.mOpenConditionVariable.block();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void doOpenCamera(final String str, final CameraDeviceManager.CameraStateCallback cameraStateCallback, final CameraDeviceProxy.CameraSessionCallback cameraSessionCallback, final Handler handler) throws CameraAccessException {
        try {
            this.mCameraManager.openCamera(str, new CameraDevice.StateCallback() { // from class: com.android.camera.v2.bridge.device.CameraDeviceManagerImpl.2
                @Override // android.hardware.camera2.CameraDevice.StateCallback
                public void onOpened(CameraDevice cameraDevice) {
                    LogHelper.m26i(CameraDeviceManagerImpl.TAG, "onOpened");
                    cameraStateCallback.onOpened(new CameraDeviceProxyImpl(CameraDeviceManagerImpl.this.mActivity, cameraDevice, cameraSessionCallback, handler));
                }

                @Override // android.hardware.camera2.CameraDevice.StateCallback
                public void onError(CameraDevice cameraDevice, int i) throws InterruptedException, CameraAccessException {
                    LogHelper.m26i(CameraDeviceManagerImpl.TAG, "onError CameraDevice:" + cameraDevice + ", error:" + i);
                    if (1 == i && CameraDeviceManagerImpl.this.mRetryCount < 1) {
                        CameraDeviceManagerImpl.this.mRetryCount++;
                        try {
                            Thread.sleep(1000L);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        CameraDeviceManagerImpl.this.doOpenCamera(str, cameraStateCallback, cameraSessionCallback, handler);
                        return;
                    }
                    cameraStateCallback.onError(i);
                }

                @Override // android.hardware.camera2.CameraDevice.StateCallback
                public void onDisconnected(CameraDevice cameraDevice) {
                    LogHelper.m26i(CameraDeviceManagerImpl.TAG, "onDisconnected");
                    cameraStateCallback.onDisconnected(null);
                }
            }, handler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }
}
