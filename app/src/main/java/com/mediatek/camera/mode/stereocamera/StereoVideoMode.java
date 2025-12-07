package com.mediatek.camera.mode.stereocamera;

import com.mediatek.camera.ICameraContext;
import com.mediatek.camera.ICameraMode;
import com.mediatek.camera.R;
import com.mediatek.camera.mode.VideoMode;
import com.mediatek.camera.mode.stereocamera.StereoView;
import com.mediatek.camera.platform.ICameraAppUi;
import com.mediatek.camera.platform.ICameraView;
import com.mediatek.camera.platform.Parameters;
import com.mediatek.camera.setting.ParametersHelper;
import com.mediatek.camera.util.Log;
import com.mediatek.camera.util.Util;

/* loaded from: classes.dex */
public class StereoVideoMode extends VideoMode implements StereoView.Listener {

    /* renamed from: -com-mediatek-camera-ICameraMode$ActionTypeSwitchesValues, reason: not valid java name */
    private static final /* synthetic */ int[] f110commediatekcameraICameraMode$ActionTypeSwitchesValues = null;
    private StereoGestureListener mStereoGestureListener;
    private ICameraView mStereoView;

    /* renamed from: -getcom-mediatek-camera-ICameraMode$ActionTypeSwitchesValues, reason: not valid java name */
    private static /* synthetic */ int[] m1009getcommediatekcameraICameraMode$ActionTypeSwitchesValues() {
        if (f110commediatekcameraICameraMode$ActionTypeSwitchesValues != null) {
            return f110commediatekcameraICameraMode$ActionTypeSwitchesValues;
        }
        int[] iArr = new int[ICameraMode.ActionType.valuesCustom().length];
        try {
            iArr[ICameraMode.ActionType.ACTION_CANCEL_BUTTON_CLICK.ordinal()] = 4;
        } catch (NoSuchFieldError e) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_CAN_DO_AUTO_FOCUS.ordinal()] = 5;
        } catch (NoSuchFieldError e2) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_DISABLE_VIDEO_RECORD.ordinal()] = 6;
        } catch (NoSuchFieldError e3) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_FACE_DETECTED.ordinal()] = 7;
        } catch (NoSuchFieldError e4) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_NOTIFY_SURFCEVIEW_DESTROYED.ordinal()] = 8;
        } catch (NoSuchFieldError e5) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_NOTIFY_SURFCEVIEW_DISPLAY_IS_READY.ordinal()] = 9;
        } catch (NoSuchFieldError e6) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_OK_BUTTON_CLICK.ordinal()] = 10;
        } catch (NoSuchFieldError e7) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_BACK_KEY_PRESS.ordinal()] = 11;
        } catch (NoSuchFieldError e8) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_CAMERA_CLOSE.ordinal()] = 12;
        } catch (NoSuchFieldError e9) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_CAMERA_OPEN.ordinal()] = 1;
        } catch (NoSuchFieldError e10) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_CAMERA_PARAMETERS_READY.ordinal()] = 13;
        } catch (NoSuchFieldError e11) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_COMPENSATION_CHANGED.ordinal()] = 14;
        } catch (NoSuchFieldError e12) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_CONFIGURATION_CHANGED.ordinal()] = 15;
        } catch (NoSuchFieldError e13) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_FULL_SCREEN_CHANGED.ordinal()] = 16;
        } catch (NoSuchFieldError e14) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_KEY_EVENT_PRESS.ordinal()] = 17;
        } catch (NoSuchFieldError e15) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_LONG_PRESS.ordinal()] = 18;
        } catch (NoSuchFieldError e16) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_MEDIA_EJECT.ordinal()] = 19;
        } catch (NoSuchFieldError e17) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_PREVIEW_BUFFER_SIZE_CHANGED.ordinal()] = 20;
        } catch (NoSuchFieldError e18) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_PREVIEW_DISPLAY_SIZE_CHANGED.ordinal()] = 21;
        } catch (NoSuchFieldError e19) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_RESTORE_SETTINGS.ordinal()] = 22;
        } catch (NoSuchFieldError e20) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_SELFTIMER_STATE.ordinal()] = 23;
        } catch (NoSuchFieldError e21) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_SETTING_BUTTON_CLICK.ordinal()] = 24;
        } catch (NoSuchFieldError e22) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_SINGLE_TAP_UP.ordinal()] = 2;
        } catch (NoSuchFieldError e23) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_START_PREVIEW.ordinal()] = 25;
        } catch (NoSuchFieldError e24) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_STOP_PREVIEW.ordinal()] = 26;
        } catch (NoSuchFieldError e25) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_SURFACE_TEXTURE_READY.ordinal()] = 27;
        } catch (NoSuchFieldError e26) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_USER_INTERACTION.ordinal()] = 28;
        } catch (NoSuchFieldError e27) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ORITATION_CHANGED.ordinal()] = 29;
        } catch (NoSuchFieldError e28) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_PHOTO_SHUTTER_BUTTON_CLICK.ordinal()] = 30;
        } catch (NoSuchFieldError e29) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_PREVIEW_VISIBLE_CHANGED.ordinal()] = 31;
        } catch (NoSuchFieldError e30) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_SET_DISPLAYROTATION.ordinal()] = 32;
        } catch (NoSuchFieldError e31) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_SHUTTER_BUTTON_FOCUS.ordinal()] = 33;
        } catch (NoSuchFieldError e32) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_SHUTTER_BUTTON_LONG_PRESS.ordinal()] = 3;
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
        f110commediatekcameraICameraMode$ActionTypeSwitchesValues = iArr;
        return iArr;
    }

    public StereoVideoMode(ICameraContext iCameraContext) {
        super(iCameraContext);
        this.mStereoGestureListener = new StereoGestureListener() { // from class: com.mediatek.camera.mode.stereocamera.StereoVideoMode.1
            @Override // com.mediatek.camera.mode.stereocamera.StereoGestureListener, com.mediatek.camera.platform.ICameraAppUi.GestureListener
            public boolean onSingleTapUp(float f, float f2) {
                return false;
            }

            @Override // com.mediatek.camera.mode.stereocamera.StereoGestureListener, com.mediatek.camera.platform.ICameraAppUi.GestureListener
            public boolean onLongPress(float f, float f2) {
                return false;
            }
        };
        Log.m31d("StereoVideoMode", "[StereoVideoMode]constructor...");
    }

    @Override // com.mediatek.camera.mode.VideoMode, com.mediatek.camera.mode.CameraMode, com.mediatek.camera.ICameraMode
    public boolean open() {
        Log.m31d("StereoVideoMode", "[openMode]...");
        super.open();
        initStereoView();
        this.mICameraAppUi.setGestureListener(this.mStereoGestureListener);
        return true;
    }

    @Override // com.mediatek.camera.mode.VideoMode, com.mediatek.camera.mode.CameraMode, com.mediatek.camera.ICameraMode
    public boolean close() {
        Log.m31d("StereoVideoMode", "[closeMode]...");
        uninitStereoView();
        this.mICameraAppUi.setGestureListener(null);
        super.close();
        return true;
    }

    @Override // com.mediatek.camera.mode.VideoMode, com.mediatek.camera.mode.CameraMode, com.mediatek.camera.ICameraMode
    public boolean execute(ICameraMode.ActionType actionType, Object... objArr) {
        if (actionType != ICameraMode.ActionType.ACTION_ORITATION_CHANGED) {
            Log.m31d("StereoVideoMode", "[execute]type = " + actionType);
        }
        switch (m1009getcommediatekcameraICameraMode$ActionTypeSwitchesValues()[actionType.ordinal()]) {
            case 1:
                updateDevice();
                return true;
            case 2:
                Parameters parameters = this.mICameraDevice.getParameters();
                if (parameters == null || (parameters != null && parameters.getMaxNumFocusAreas() > 0)) {
                    return super.execute(actionType, objArr);
                }
                this.mStereoView.update(0, (Integer) objArr[1], (Integer) objArr[2]);
                return true;
            case 3:
                this.mICameraAppUi.showInfo(this.mActivity.getString(R.string.accessibility_switch_to_dual_camera) + this.mActivity.getString(R.string.camera_continuous_not_supported));
                return true;
            default:
                return super.execute(actionType, objArr);
        }
    }

    @Override // com.mediatek.camera.mode.CameraMode, com.mediatek.camera.ICameraMode
    public void resume() {
    }

    @Override // com.mediatek.camera.mode.CameraMode, com.mediatek.camera.ICameraMode
    public void pause() {
    }

    @Override // com.mediatek.camera.mode.stereocamera.StereoView.Listener
    public void onVsDofLevelChanged(String str) {
        setVsDofLevelParameter(str);
    }

    @Override // com.mediatek.camera.mode.stereocamera.StereoView.Listener
    public void onTouchPositionChanged(String str) {
        setTouchPositionParameter(str);
    }

    @Override // com.mediatek.camera.mode.VideoMode
    public ICameraMode.CameraModeType getCameraModeType() {
        return ICameraMode.CameraModeType.EXT_MODE_VIDEO_STEREO;
    }

    @Override // com.mediatek.camera.mode.VideoMode
    protected void initializeShutterStatus() {
        this.mICameraAppUi.setPhotoShutterEnabled(false);
    }

    @Override // com.mediatek.camera.mode.VideoMode
    protected boolean startNormalRecording() throws IllegalStateException {
        boolean zStartNormalRecording = super.startNormalRecording();
        try {
            Util.setParametersExtra(this.mMediaRecorder, "media-param-audio-stop-first=1");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return zStartNormalRecording;
    }

    @Override // com.mediatek.camera.mode.VideoMode
    protected void doStartPreview() {
        Parameters parameters = this.mICameraDevice.getParameters();
        if (ParametersHelper.isDenoiseSupported(parameters)) {
            parameters.set("preview-frame-rate", 24);
            this.mICameraDevice.applyParameters();
        }
        super.doStartPreview();
    }

    private void initStereoView() {
        if (this.mStereoView == null) {
            this.mStereoView = this.mICameraAppUi.getCameraView(ICameraAppUi.SpecViewType.MODE_STEREO);
            this.mStereoView.init(this.mActivity, this.mICameraAppUi, this.mIModuleCtrl);
            this.mStereoView.setListener(this);
            this.mStereoView.show();
        }
    }

    private void uninitStereoView() {
        if (this.mStereoView != null) {
            this.mStereoView.uninit();
        }
    }

    private void setVsDofLevelParameter(String str) {
        Log.m31d("StereoVideoMode", "[setVsDofLevelParameter] level = " + str + "device = " + this.mICameraDevice);
        if (this.mICameraDevice != null) {
            this.mICameraDevice.setParameter("stereo-dof-level", str);
            this.mICameraDevice.applyParameters();
        }
    }

    private void setTouchPositionParameter(String str) {
        Log.m34i("StereoVideoMode", "[setTouchPositionParameter] value = " + str + "device = " + this.mICameraDevice);
        if (this.mICameraDevice != null) {
            this.mICameraDevice.setParameter("stereo-touch-position", str);
            this.mICameraDevice.applyParameters();
        }
    }
}
