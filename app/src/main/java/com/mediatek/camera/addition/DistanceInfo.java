package com.mediatek.camera.addition;

import com.mediatek.camera.ICameraContext;
import com.mediatek.camera.ICameraMode;
import com.mediatek.camera.platform.ICameraDeviceManager;
import com.mediatek.camera.util.Log;

/* loaded from: classes.dex */
public class DistanceInfo extends CameraAddition {

    /* renamed from: -com-mediatek-camera-ICameraMode$ActionTypeSwitchesValues, reason: not valid java name */
    private static final /* synthetic */ int[] f97commediatekcameraICameraMode$ActionTypeSwitchesValues = null;
    private boolean mCameraClosed;
    private ICameraDeviceManager.ICameraDevice.StereoDistanceCallback mDistanceListener;

    /* renamed from: -getcom-mediatek-camera-ICameraMode$ActionTypeSwitchesValues, reason: not valid java name */
    private static /* synthetic */ int[] m584getcommediatekcameraICameraMode$ActionTypeSwitchesValues() {
        if (f97commediatekcameraICameraMode$ActionTypeSwitchesValues != null) {
            return f97commediatekcameraICameraMode$ActionTypeSwitchesValues;
        }
        int[] iArr = new int[ICameraMode.ActionType.valuesCustom().length];
        try {
            iArr[ICameraMode.ActionType.ACTION_CANCEL_BUTTON_CLICK.ordinal()] = 3;
        } catch (NoSuchFieldError e) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_CAN_DO_AUTO_FOCUS.ordinal()] = 4;
        } catch (NoSuchFieldError e2) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_DISABLE_VIDEO_RECORD.ordinal()] = 5;
        } catch (NoSuchFieldError e3) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_FACE_DETECTED.ordinal()] = 6;
        } catch (NoSuchFieldError e4) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_NOTIFY_SURFCEVIEW_DESTROYED.ordinal()] = 7;
        } catch (NoSuchFieldError e5) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_NOTIFY_SURFCEVIEW_DISPLAY_IS_READY.ordinal()] = 8;
        } catch (NoSuchFieldError e6) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_OK_BUTTON_CLICK.ordinal()] = 9;
        } catch (NoSuchFieldError e7) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_BACK_KEY_PRESS.ordinal()] = 10;
        } catch (NoSuchFieldError e8) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_CAMERA_CLOSE.ordinal()] = 1;
        } catch (NoSuchFieldError e9) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_CAMERA_OPEN.ordinal()] = 2;
        } catch (NoSuchFieldError e10) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_CAMERA_PARAMETERS_READY.ordinal()] = 11;
        } catch (NoSuchFieldError e11) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_COMPENSATION_CHANGED.ordinal()] = 12;
        } catch (NoSuchFieldError e12) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_CONFIGURATION_CHANGED.ordinal()] = 13;
        } catch (NoSuchFieldError e13) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_FULL_SCREEN_CHANGED.ordinal()] = 14;
        } catch (NoSuchFieldError e14) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_KEY_EVENT_PRESS.ordinal()] = 15;
        } catch (NoSuchFieldError e15) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_LONG_PRESS.ordinal()] = 16;
        } catch (NoSuchFieldError e16) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_MEDIA_EJECT.ordinal()] = 17;
        } catch (NoSuchFieldError e17) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_PREVIEW_BUFFER_SIZE_CHANGED.ordinal()] = 18;
        } catch (NoSuchFieldError e18) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_PREVIEW_DISPLAY_SIZE_CHANGED.ordinal()] = 19;
        } catch (NoSuchFieldError e19) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_RESTORE_SETTINGS.ordinal()] = 20;
        } catch (NoSuchFieldError e20) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_SELFTIMER_STATE.ordinal()] = 21;
        } catch (NoSuchFieldError e21) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_SETTING_BUTTON_CLICK.ordinal()] = 22;
        } catch (NoSuchFieldError e22) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_SINGLE_TAP_UP.ordinal()] = 23;
        } catch (NoSuchFieldError e23) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_START_PREVIEW.ordinal()] = 24;
        } catch (NoSuchFieldError e24) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_STOP_PREVIEW.ordinal()] = 25;
        } catch (NoSuchFieldError e25) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_SURFACE_TEXTURE_READY.ordinal()] = 26;
        } catch (NoSuchFieldError e26) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_USER_INTERACTION.ordinal()] = 27;
        } catch (NoSuchFieldError e27) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ORITATION_CHANGED.ordinal()] = 28;
        } catch (NoSuchFieldError e28) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_PHOTO_SHUTTER_BUTTON_CLICK.ordinal()] = 29;
        } catch (NoSuchFieldError e29) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_PREVIEW_VISIBLE_CHANGED.ordinal()] = 30;
        } catch (NoSuchFieldError e30) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_SET_DISPLAYROTATION.ordinal()] = 31;
        } catch (NoSuchFieldError e31) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_SHUTTER_BUTTON_FOCUS.ordinal()] = 32;
        } catch (NoSuchFieldError e32) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_SHUTTER_BUTTON_LONG_PRESS.ordinal()] = 33;
        } catch (NoSuchFieldError e33) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_SWITCH_DEVICE.ordinal()] = 34;
        } catch (NoSuchFieldError e34) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_VIDEO_SHUTTER_BUTTON_CLICK.ordinal()] = 35;
        } catch (NoSuchFieldError e35) {
        }
        f97commediatekcameraICameraMode$ActionTypeSwitchesValues = iArr;
        return iArr;
    }

    public DistanceInfo(ICameraContext iCameraContext) {
        super(iCameraContext);
        this.mCameraClosed = false;
        this.mDistanceListener = new ICameraDeviceManager.ICameraDevice.StereoDistanceCallback() { // from class: com.mediatek.camera.addition.DistanceInfo.1
            @Override // com.mediatek.camera.platform.ICameraDeviceManager.ICameraDevice.StereoDistanceCallback
            public void onInfo(String str) {
                if (str == null) {
                    Log.m36w("DistanceInfo", "[onInfo]distance info is null, please check");
                } else {
                    DistanceInfo.this.updateFocusManager();
                    DistanceInfo.this.mIFocusManager.setDistanceInfo(str);
                }
            }
        };
    }

    @Override // com.mediatek.camera.ICameraAddition
    public void open() {
        updateCameraDevice();
        if (this.mICameraDevice != null) {
            this.mICameraDevice.setStereoDistanceCallback(this.mDistanceListener);
        }
    }

    @Override // com.mediatek.camera.addition.CameraAddition, com.mediatek.camera.ICameraAddition
    public boolean isOpen() {
        return false;
    }

    @Override // com.mediatek.camera.ICameraAddition
    public void close() {
        if (this.mICameraDevice != null) {
            this.mICameraDevice.setStereoDistanceCallback(null);
        }
    }

    @Override // com.mediatek.camera.ICameraAddition
    public boolean isSupport() {
        boolean z = false;
        if (this.mIFeatureConfig.isDualCameraEnable() && "on".equals(this.mISettingCtrl.getSettingValue("pref_distance_key"))) {
            z = true;
        }
        Log.m31d("DistanceInfo", "[isSupport] isSupport:" + z);
        return z;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    @Override // com.mediatek.camera.addition.CameraAddition, com.mediatek.camera.ICameraAddition
    public boolean execute(ICameraMode.ActionType actionType, Object... objArr) {
        switch (m584getcommediatekcameraICameraMode$ActionTypeSwitchesValues()[actionType.ordinal()]) {
            case 1:
                Log.m31d("DistanceInfo", "[execute] type:" + actionType);
                this.mCameraClosed = true;
                return false;
            case 2:
                Log.m31d("DistanceInfo", "[execute] type:" + actionType);
                this.mCameraClosed = false;
                return false;
            default:
                return false;
        }
    }
}
