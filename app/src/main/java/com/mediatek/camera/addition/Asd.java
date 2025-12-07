package com.mediatek.camera.addition;

import com.mediatek.camera.ICameraContext;
import com.mediatek.camera.platform.ICameraDeviceManager;
import com.mediatek.camera.util.Log;

/* loaded from: classes.dex */
public class Asd extends CameraAddition {
    private final ICameraDeviceManager.ICameraDevice.AsdListener mASDCaptureCallback;
    private AsdState mCurrentState;
    private int mLastScene;

    private enum AsdState {
        STATE_IDLE,
        STATE_OPENED;

        /* renamed from: values, reason: to resolve conflict with enum method */
        public static AsdState[] valuesCustom() {
            return values();
        }
    }

    public Asd(ICameraContext iCameraContext) {
        super(iCameraContext);
        this.mLastScene = -1;
        this.mCurrentState = AsdState.STATE_IDLE;
        this.mASDCaptureCallback = new ICameraDeviceManager.ICameraDevice.AsdListener() { // from class: com.mediatek.camera.addition.Asd.1
            @Override // com.mediatek.camera.platform.ICameraDeviceManager.ICameraDevice.AsdListener
            public void onDeviceCallback(int i) {
                boolean z = true;
                Log.m31d("Asd", "[onDeviceCallback] onDetected scene = " + i + ",mLastScene:" + Asd.this.mLastScene);
                if (Asd.this.mLastScene != i) {
                    if (i != 2 && i != 8) {
                        z = false;
                    }
                    Asd.this.mICameraAppUi.onDetectedSceneMode(i, z);
                    Asd.this.mLastScene = i;
                }
            }
        };
    }

    @Override // com.mediatek.camera.ICameraAddition
    public boolean isSupport() {
        if (!"on".equals(this.mISettingCtrl.getSettingValue("pref_asd_key"))) {
            return false;
        }
        return true;
    }

    @Override // com.mediatek.camera.ICameraAddition
    public void open() {
        startAsd();
    }

    @Override // com.mediatek.camera.addition.CameraAddition, com.mediatek.camera.ICameraAddition
    public boolean isOpen() {
        boolean z = false;
        if (AsdState.STATE_IDLE != this.mCurrentState) {
            z = true;
        }
        Log.m31d("Asd", "[isOpen] isOpen:" + z);
        return z;
    }

    @Override // com.mediatek.camera.ICameraAddition
    public void close() {
        stopAsd();
    }

    public void startAsd() {
        Log.m31d("Asd", "[startAsd]...");
        updateCameraDevice();
        if (this.mICameraDevice == null) {
            return;
        }
        this.mICameraDevice.setAsdCallback(this.mASDCaptureCallback);
        this.mCurrentState = AsdState.STATE_OPENED;
    }

    private void stopAsd() {
        Log.m31d("Asd", "[stopAsd]mCurrentState = " + this.mCurrentState);
        if (this.mCurrentState == AsdState.STATE_IDLE) {
            return;
        }
        if (this.mICameraDevice != null) {
            this.mICameraDevice.setAsdCallback(null);
            this.mICameraAppUi.restoreSceneMode();
        }
        this.mLastScene = -1;
        this.mCurrentState = AsdState.STATE_IDLE;
    }
}
