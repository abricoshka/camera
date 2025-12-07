package com.mediatek.camera;

import android.app.Activity;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.support.v4.app.FrameMetricsAggregator;
import android.view.KeyEvent;
import android.view.Surface;
import android.view.View;
import com.mediatek.camera.ICameraMode;
import com.mediatek.camera.mode.DummyMode;
import com.mediatek.camera.mode.ModeFactory;
import com.mediatek.camera.platform.ICameraAppUi;
import com.mediatek.camera.platform.ICameraDeviceManager;
import com.mediatek.camera.platform.IFeatureConfig;
import com.mediatek.camera.platform.IFileSaver;
import com.mediatek.camera.platform.IFocusManager;
import com.mediatek.camera.platform.IModuleCtrl;
import com.mediatek.camera.platform.ISelfTimeManager;
import com.mediatek.camera.setting.SettingCtrl;
import com.mediatek.camera.util.Log;

/* loaded from: classes.dex */
public class ModuleManager {

    /* renamed from: -com-mediatek-camera-ICameraMode$CameraModeTypeSwitchesValues, reason: not valid java name */
    private static final /* synthetic */ int[] f96commediatekcameraICameraMode$CameraModeTypeSwitchesValues = null;
    private final Activity mActivity;
    private ICameraMode.CameraModeType mCurrentMode;
    private final ICameraAppUi mICameraAppUi;
    private final ICameraDeviceManager mICameraDeviceManager;
    private final IFeatureConfig mIFeatureConfig;
    private final IFileSaver mIFileSaver;
    private IFocusManager mIFocusManager;
    private IModuleCtrl mIModuleCtrl;
    private ISelfTimeManager mISelfTimeManager;
    private final ICameraContext mICameraContext = new CameraContextImpl(this, null);
    private ICameraMode mICameraMode = new DummyMode();
    private ISettingCtrl mISettingCtrl = new SettingCtrl(this.mICameraContext);
    private AdditionManager mAdditionManager = new AdditionManager(this.mICameraContext);

    /* renamed from: -getcom-mediatek-camera-ICameraMode$CameraModeTypeSwitchesValues, reason: not valid java name */
    private static /* synthetic */ int[] m581getcommediatekcameraICameraMode$CameraModeTypeSwitchesValues() {
        if (f96commediatekcameraICameraMode$CameraModeTypeSwitchesValues != null) {
            return f96commediatekcameraICameraMode$CameraModeTypeSwitchesValues;
        }
        int[] iArr = new int[ICameraMode.CameraModeType.valuesCustom().length];
        try {
            iArr[ICameraMode.CameraModeType.EXT_MODE_FACE_BEAUTY.ordinal()] = 1;
        } catch (NoSuchFieldError e) {
        }
        try {
            iArr[ICameraMode.CameraModeType.EXT_MODE_PANORAMA.ordinal()] = 2;
        } catch (NoSuchFieldError e2) {
        }
        try {
            iArr[ICameraMode.CameraModeType.EXT_MODE_PHOTO.ordinal()] = 3;
        } catch (NoSuchFieldError e3) {
        }
        try {
            iArr[ICameraMode.CameraModeType.EXT_MODE_PHOTO_PIP.ordinal()] = 4;
        } catch (NoSuchFieldError e4) {
        }
        try {
            iArr[ICameraMode.CameraModeType.EXT_MODE_PHOTO_STEREO.ordinal()] = 5;
        } catch (NoSuchFieldError e5) {
        }
        try {
            iArr[ICameraMode.CameraModeType.EXT_MODE_SLOW_MOTION.ordinal()] = 10;
        } catch (NoSuchFieldError e6) {
        }
        try {
            iArr[ICameraMode.CameraModeType.EXT_MODE_STEREO_CAMERA.ordinal()] = 6;
        } catch (NoSuchFieldError e7) {
        }
        try {
            iArr[ICameraMode.CameraModeType.EXT_MODE_VIDEO.ordinal()] = 7;
        } catch (NoSuchFieldError e8) {
        }
        try {
            iArr[ICameraMode.CameraModeType.EXT_MODE_VIDEO_PIP.ordinal()] = 8;
        } catch (NoSuchFieldError e9) {
        }
        try {
            iArr[ICameraMode.CameraModeType.EXT_MODE_VIDEO_STEREO.ordinal()] = 9;
        } catch (NoSuchFieldError e10) {
        }
        f96commediatekcameraICameraMode$CameraModeTypeSwitchesValues = iArr;
        return iArr;
    }

    public ModuleManager(Activity activity, IFileSaver iFileSaver, ICameraAppUi iCameraAppUi, IFeatureConfig iFeatureConfig, ICameraDeviceManager iCameraDeviceManager, IModuleCtrl iModuleCtrl, ISelfTimeManager iSelfTimeManager) {
        this.mActivity = activity;
        this.mIModuleCtrl = iModuleCtrl;
        this.mICameraAppUi = iCameraAppUi;
        this.mIFeatureConfig = iFeatureConfig;
        this.mIFileSaver = iFileSaver;
        this.mICameraDeviceManager = iCameraDeviceManager;
        this.mISelfTimeManager = iSelfTimeManager;
    }

    public void resume() {
        this.mICameraMode.resume();
        this.mAdditionManager.resume();
    }

    public void pause() {
        this.mICameraMode.pause();
        this.mAdditionManager.pause();
    }

    public void destory() {
        this.mAdditionManager.destory();
    }

    public void createMode(ICameraMode.CameraModeType cameraModeType) {
        Log.m31d("ModuleManager", "[createMode],newMode:" + cameraModeType + ",mCurrentMode:" + this.mCurrentMode);
        if (this.mCurrentMode == cameraModeType) {
            return;
        }
        this.mICameraMode.close();
        this.mCurrentMode = cameraModeType;
        this.mICameraMode = ModeFactory.getInstance().createMode(cameraModeType, this.mICameraContext);
        this.mAdditionManager.setCurrentMode(cameraModeType);
        this.mICameraMode.open();
    }

    public ISettingCtrl getSettingController() {
        return this.mISettingCtrl;
    }

    public boolean closeMode() {
        Log.m31d("ModuleManager", "[closeMode]");
        this.mICameraMode.close();
        this.mICameraMode = new DummyMode();
        this.mCurrentMode = null;
        return true;
    }

    public ICameraMode.ModeState getModeState() {
        return this.mICameraMode.getModeState();
    }

    public boolean onCameraOpen() {
        Log.m31d("ModuleManager", "[onCameraOpen]...");
        this.mAdditionManager.execute(ICameraMode.ActionType.ACTION_ON_CAMERA_OPEN, false, new Object[0]);
        return this.mICameraMode.execute(ICameraMode.ActionType.ACTION_ON_CAMERA_OPEN, new Object[0]);
    }

    public void onCameraClose() {
        Log.m31d("ModuleManager", "[onCameraClose]...");
        this.mAdditionManager.execute(ICameraMode.ActionType.ACTION_ON_CAMERA_CLOSE, false, new Object[0]);
        this.mICameraMode.execute(ICameraMode.ActionType.ACTION_ON_CAMERA_CLOSE, new Object[0]);
    }

    public void onCameraParameterReady(boolean z) {
        this.mAdditionManager.onCameraParameterReady(false);
        this.mICameraMode.execute(ICameraMode.ActionType.ACTION_ON_CAMERA_PARAMETERS_READY, Boolean.valueOf(z));
    }

    public void setFocusManager(IFocusManager iFocusManager) {
        this.mIFocusManager = iFocusManager;
    }

    public void onOrientationChanged(int i) {
        this.mAdditionManager.execute(ICameraMode.ActionType.ACTION_ORITATION_CHANGED, false, Integer.valueOf(i));
        this.mICameraMode.execute(ICameraMode.ActionType.ACTION_ORITATION_CHANGED, Integer.valueOf(i));
    }

    public void onCompensationChanged(int i) {
        this.mAdditionManager.execute(ICameraMode.ActionType.ACTION_ON_COMPENSATION_CHANGED, false, Integer.valueOf(i));
        this.mICameraMode.execute(ICameraMode.ActionType.ACTION_ON_COMPENSATION_CHANGED, Integer.valueOf(i));
    }

    public boolean onShutterButtonFocus(boolean z) {
        this.mAdditionManager.execute(ICameraMode.ActionType.ACTION_SHUTTER_BUTTON_FOCUS, false, Boolean.valueOf(z));
        return this.mICameraMode.execute(ICameraMode.ActionType.ACTION_SHUTTER_BUTTON_FOCUS, Boolean.valueOf(z));
    }

    public boolean onPhotoShutterButtonClick() {
        this.mAdditionManager.execute(ICameraMode.ActionType.ACTION_PHOTO_SHUTTER_BUTTON_CLICK, false, new Object[0]);
        return this.mICameraMode.execute(ICameraMode.ActionType.ACTION_PHOTO_SHUTTER_BUTTON_CLICK, new Object[0]);
    }

    public boolean onVideoShutterButtonClick() {
        this.mAdditionManager.execute(ICameraMode.ActionType.ACTION_VIDEO_SHUTTER_BUTTON_CLICK, false, new Object[0]);
        return this.mICameraMode.execute(ICameraMode.ActionType.ACTION_VIDEO_SHUTTER_BUTTON_CLICK, new Object[0]);
    }

    public boolean onShutterButtonLongPressed() {
        this.mAdditionManager.execute(ICameraMode.ActionType.ACTION_SHUTTER_BUTTON_LONG_PRESS, false, new Object[0]);
        return this.mICameraMode.execute(ICameraMode.ActionType.ACTION_SHUTTER_BUTTON_LONG_PRESS, new Object[0]);
    }

    public void onPreviewDisplaySizeChanged(int i, int i2) {
        this.mAdditionManager.execute(ICameraMode.ActionType.ACTION_ON_PREVIEW_DISPLAY_SIZE_CHANGED, false, Integer.valueOf(i), Integer.valueOf(i2));
        this.mICameraMode.execute(ICameraMode.ActionType.ACTION_ON_PREVIEW_DISPLAY_SIZE_CHANGED, Integer.valueOf(i), Integer.valueOf(i2));
    }

    public void onPreviewBufferSizeChanged(int i, int i2) {
        this.mICameraMode.execute(ICameraMode.ActionType.ACTION_ON_PREVIEW_BUFFER_SIZE_CHANGED, Integer.valueOf(i), Integer.valueOf(i2));
    }

    public void onEffectClick() {
        if (this.mCurrentMode == ICameraMode.CameraModeType.EXT_MODE_PHOTO) {
            this.mAdditionManager.onEffectClick();
        }
    }

    public boolean onSingleTapUp(View view, int i, int i2) {
        this.mAdditionManager.execute(ICameraMode.ActionType.ACTION_ON_SINGLE_TAP_UP, false, view, Integer.valueOf(i), Integer.valueOf(i2));
        return this.mICameraMode.execute(ICameraMode.ActionType.ACTION_ON_SINGLE_TAP_UP, view, Integer.valueOf(i), Integer.valueOf(i2));
    }

    public boolean onLongPress(View view, int i, int i2) {
        this.mAdditionManager.execute(ICameraMode.ActionType.ACTION_ON_LONG_PRESS, false, view, Integer.valueOf(i), Integer.valueOf(i2));
        return this.mICameraMode.execute(ICameraMode.ActionType.ACTION_ON_LONG_PRESS, view, Integer.valueOf(i), Integer.valueOf(i2));
    }

    public boolean onOkButtonPress() {
        this.mAdditionManager.execute(ICameraMode.ActionType.ACTION_OK_BUTTON_CLICK, false, new Object[0]);
        return this.mICameraMode.execute(ICameraMode.ActionType.ACTION_OK_BUTTON_CLICK, new Object[0]);
    }

    public boolean onCancelButtonPress() {
        this.mAdditionManager.execute(ICameraMode.ActionType.ACTION_CANCEL_BUTTON_CLICK, false, new Object[0]);
        return this.mICameraMode.execute(ICameraMode.ActionType.ACTION_CANCEL_BUTTON_CLICK, new Object[0]);
    }

    public void setSurfaceTextureReady(boolean z) {
        this.mICameraMode.execute(ICameraMode.ActionType.ACTION_ON_SURFACE_TEXTURE_READY, Boolean.valueOf(z));
    }

    public boolean onBackPressed() {
        boolean zExecute = this.mAdditionManager.execute(ICameraMode.ActionType.ACTION_ON_BACK_KEY_PRESS, false, new Object[0]) | this.mICameraMode.execute(ICameraMode.ActionType.ACTION_ON_BACK_KEY_PRESS, new Object[0]);
        Log.m34i("ModuleManager", "onBackPressed, reslult = " + zExecute);
        return zExecute;
    }

    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        this.mAdditionManager.execute(ICameraMode.ActionType.ACTION_ON_KEY_EVENT_PRESS, false, Integer.valueOf(i), keyEvent);
        return this.mICameraMode.execute(ICameraMode.ActionType.ACTION_ON_KEY_EVENT_PRESS, Integer.valueOf(i), keyEvent);
    }

    public void onMediaEject() {
        this.mICameraMode.execute(ICameraMode.ActionType.ACTION_ON_MEDIA_EJECT, new Object[0]);
    }

    public void onRestoreSettings() {
        this.mICameraMode.execute(ICameraMode.ActionType.ACTION_ON_RESTORE_SETTINGS, new Object[0]);
    }

    public boolean onUserInteraction() {
        return this.mICameraMode.execute(ICameraMode.ActionType.ACTION_ON_USER_INTERACTION, new Object[0]);
    }

    public void setDisplayRotation(int i) {
        this.mICameraMode.execute(ICameraMode.ActionType.ACTION_SET_DISPLAYROTATION, Integer.valueOf(i));
    }

    public void onFaceDetected(Camera.Face[] faceArr) {
        this.mICameraMode.execute(ICameraMode.ActionType.ACTION_FACE_DETECTED, faceArr);
    }

    public void onSelfTimerState(boolean z) {
        this.mICameraMode.execute(ICameraMode.ActionType.ACTION_ON_SELFTIMER_STATE, Boolean.valueOf(z));
    }

    public SurfaceTexture getBottomSurfaceTexture() {
        return this.mICameraMode.getBottomSurfaceTexture();
    }

    public SurfaceTexture getTopSurfaceTexture() {
        return this.mICameraMode.getTopSurfaceTexture();
    }

    public boolean isNeedDualCamera() {
        return this.mICameraMode.isNeedDualCamera();
    }

    public boolean isDisplayUseSurfaceView() {
        return this.mICameraMode.isDisplayUseSurfaceView();
    }

    public boolean isDeviceUseSurfaceView() {
        return this.mICameraMode.isDeviceUseSurfaceView();
    }

    public boolean startPreview(boolean z) {
        return this.mICameraMode.execute(ICameraMode.ActionType.ACTION_ON_START_PREVIEW, Boolean.valueOf(z));
    }

    public boolean stopPreview() {
        return this.mICameraMode.execute(ICameraMode.ActionType.ACTION_ON_STOP_PREVIEW, new Object[0]);
    }

    public void onSettingContainerShowing(boolean z) {
        this.mICameraMode.execute(ICameraMode.ActionType.ACTION_ON_SETTING_BUTTON_CLICK, Boolean.valueOf(z));
    }

    public boolean switchDevice() {
        this.mAdditionManager.execute(ICameraMode.ActionType.ACTION_SWITCH_DEVICE, false, new Object[0]);
        return this.mICameraMode.execute(ICameraMode.ActionType.ACTION_SWITCH_DEVICE, new Object[0]);
    }

    public void notifySurfaceViewDisplayIsReady() {
        this.mICameraMode.execute(ICameraMode.ActionType.ACTION_NOTIFY_SURFCEVIEW_DISPLAY_IS_READY, new Object[0]);
    }

    public void notifySurfaceViewDestroyed(Surface surface) {
        this.mICameraMode.execute(ICameraMode.ActionType.ACTION_NOTIFY_SURFCEVIEW_DESTROYED, surface);
    }

    public void configurationChanged() {
        this.mICameraMode.execute(ICameraMode.ActionType.ACTION_ON_CONFIGURATION_CHANGED, new Object[0]);
    }

    public void setModeSettingValue(ICameraMode.CameraModeType cameraModeType, String str) {
        String settingValue = getSettingValue(cameraModeType);
        if (settingValue != null) {
            this.mISettingCtrl.onSettingChanged(settingValue, str);
        }
    }

    public void setVideoRecorderEnable(boolean z) {
        this.mICameraAppUi.setVideoShutterEnabled(z);
        this.mICameraAppUi.updateVideoShutterStatues(z);
        if (!z) {
            this.mICameraMode.execute(ICameraMode.ActionType.ACTION_DISABLE_VIDEO_RECORD, new Object[0]);
        }
    }

    public void onCameraCloseDone() {
        this.mICameraDeviceManager.onCameraCloseDone();
    }

    public boolean canDoAutoFocus() {
        return this.mICameraMode.execute(ICameraMode.ActionType.ACTION_CAN_DO_AUTO_FOCUS, new Object[0]);
    }

    private class CameraContextImpl implements ICameraContext {
        /* synthetic */ CameraContextImpl(ModuleManager moduleManager, CameraContextImpl cameraContextImpl) {
            this();
        }

        private CameraContextImpl() {
        }

        @Override // com.mediatek.camera.ICameraContext
        public IFileSaver getFileSaver() {
            return ModuleManager.this.mIFileSaver;
        }

        @Override // com.mediatek.camera.ICameraContext
        public ICameraDeviceManager getCameraDeviceManager() {
            return ModuleManager.this.mICameraDeviceManager;
        }

        @Override // com.mediatek.camera.ICameraContext
        public IModuleCtrl getModuleController() {
            return ModuleManager.this.mIModuleCtrl;
        }

        @Override // com.mediatek.camera.ICameraContext
        public IFocusManager getFocusManager() {
            return ModuleManager.this.mIFocusManager;
        }

        @Override // com.mediatek.camera.ICameraContext
        public ICameraAppUi getCameraAppUi() {
            return ModuleManager.this.mICameraAppUi;
        }

        @Override // com.mediatek.camera.ICameraContext
        public IFeatureConfig getFeatureConfig() {
            return ModuleManager.this.mIFeatureConfig;
        }

        @Override // com.mediatek.camera.ICameraContext
        public Activity getActivity() {
            return ModuleManager.this.mActivity;
        }

        @Override // com.mediatek.camera.ICameraContext
        public ISettingCtrl getSettingController() {
            return ModuleManager.this.mISettingCtrl;
        }

        @Override // com.mediatek.camera.ICameraContext
        public ISelfTimeManager getSelfTimeManager() {
            return ModuleManager.this.mISelfTimeManager;
        }

        @Override // com.mediatek.camera.ICameraContext
        public AdditionManager getAdditionManager() {
            return ModuleManager.this.mAdditionManager;
        }
    }

    private String getSettingValue(ICameraMode.CameraModeType cameraModeType) {
        switch (m581getcommediatekcameraICameraMode$CameraModeTypeSwitchesValues()[cameraModeType.ordinal()]) {
            case 1:
                return "face_beauty_key";
            case 2:
                return "panorama_key";
            case 3:
                return "normal_key";
            case 4:
                return "photo_pip_key";
            case 5:
                return "photo_stereo_key";
            case 6:
                return "refocus_key";
            case FrameMetricsAggregator.DELAY_INDEX /* 7 */:
                return "video_key";
            case 8:
                return "video_pip_key";
            case 9:
                return "video_stereo_key";
            default:
                return null;
        }
    }
}
