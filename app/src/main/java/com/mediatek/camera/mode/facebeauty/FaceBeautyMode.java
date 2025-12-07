package com.mediatek.camera.mode.facebeauty;

import android.hardware.Camera;
import android.hardware.Camera$AFDataCallback;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.p000v8.renderscript.ScriptIntrinsicBLAS;
import android.support.v4.app.FrameMetricsAggregator;
import android.view.View;
import com.mediatek.camera.AdditionManager;
import com.mediatek.camera.ICameraAddition;
import com.mediatek.camera.ICameraContext;
import com.mediatek.camera.ICameraMode;
import com.mediatek.camera.R;
import com.mediatek.camera.mode.CameraMode;
import com.mediatek.camera.mode.facebeauty.FaceBeautyParametersHelper;
import com.mediatek.camera.platform.ICameraAppUi;
import com.mediatek.camera.platform.ICameraDeviceManager;
import com.mediatek.camera.platform.ICameraView;
import com.mediatek.camera.platform.IFileSaver;
import com.mediatek.camera.platform.IFocusManager;
import com.mediatek.camera.util.Log;
import java.util.ArrayList;
import junit.framework.Assert;

/* loaded from: classes.dex */
public class FaceBeautyMode extends CameraMode implements ICameraAddition.Listener, IFocusManager.FocusListener {

    /* renamed from: -com-mediatek-camera-ICameraMode$ActionTypeSwitchesValues, reason: not valid java name */
    private static final /* synthetic */ int[] f104commediatekcameraICameraMode$ActionTypeSwitchesValues = null;
    private final Camera$AFDataCallback mAFDataCallback;
    private AdditionManager mAdditionManager;
    private final Camera.AutoFocusCallback mAutoFocusCallback;
    private final ICameraDeviceManager.ICameraDevice.AutoFocusMvCallback mAutoFocusMvCallback;
    private int mCapturedImageCount;
    private CfbCallback mCfbCallback;
    private FaceBeautyParametersHelper mFaceBeautyParametersHelper;
    private FaceBeautyPreviewSize mFaceBeautyPreviewSize;
    private IFileSaver.OnFileSavedListener mFileSavedListener;
    private Handler mHandler;
    private ICameraView mICameraView;
    private boolean mIsAutoFocusCallback;
    private Camera.PictureCallback mJpegPictureCallback;
    private FaceBeautyParametersHelper.ParameterListener mParameterListener;
    private Camera.PictureCallback mPostViewPictureCallback;
    private Camera.PictureCallback mRawPictureCallback;
    private Camera.ShutterCallback mShutterCallback;
    private final Camera.PictureCallback mUncompressedImageCallback;
    private ArrayList<Integer> mVfbFacesPoint;

    /* renamed from: -getcom-mediatek-camera-ICameraMode$ActionTypeSwitchesValues, reason: not valid java name */
    private static /* synthetic */ int[] m766getcommediatekcameraICameraMode$ActionTypeSwitchesValues() {
        if (f104commediatekcameraICameraMode$ActionTypeSwitchesValues != null) {
            return f104commediatekcameraICameraMode$ActionTypeSwitchesValues;
        }
        int[] iArr = new int[ICameraMode.ActionType.valuesCustom().length];
        try {
            iArr[ICameraMode.ActionType.ACTION_CANCEL_BUTTON_CLICK.ordinal()] = 17;
        } catch (NoSuchFieldError e) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_CAN_DO_AUTO_FOCUS.ordinal()] = 1;
        } catch (NoSuchFieldError e2) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_DISABLE_VIDEO_RECORD.ordinal()] = 18;
        } catch (NoSuchFieldError e3) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_FACE_DETECTED.ordinal()] = 2;
        } catch (NoSuchFieldError e4) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_NOTIFY_SURFCEVIEW_DESTROYED.ordinal()] = 19;
        } catch (NoSuchFieldError e5) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_NOTIFY_SURFCEVIEW_DISPLAY_IS_READY.ordinal()] = 20;
        } catch (NoSuchFieldError e6) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_OK_BUTTON_CLICK.ordinal()] = 21;
        } catch (NoSuchFieldError e7) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_BACK_KEY_PRESS.ordinal()] = 3;
        } catch (NoSuchFieldError e8) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_CAMERA_CLOSE.ordinal()] = 4;
        } catch (NoSuchFieldError e9) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_CAMERA_OPEN.ordinal()] = 5;
        } catch (NoSuchFieldError e10) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_CAMERA_PARAMETERS_READY.ordinal()] = 6;
        } catch (NoSuchFieldError e11) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_COMPENSATION_CHANGED.ordinal()] = 7;
        } catch (NoSuchFieldError e12) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_CONFIGURATION_CHANGED.ordinal()] = 8;
        } catch (NoSuchFieldError e13) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_FULL_SCREEN_CHANGED.ordinal()] = 9;
        } catch (NoSuchFieldError e14) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_KEY_EVENT_PRESS.ordinal()] = 22;
        } catch (NoSuchFieldError e15) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_LONG_PRESS.ordinal()] = 23;
        } catch (NoSuchFieldError e16) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_MEDIA_EJECT.ordinal()] = 24;
        } catch (NoSuchFieldError e17) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_PREVIEW_BUFFER_SIZE_CHANGED.ordinal()] = 25;
        } catch (NoSuchFieldError e18) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_PREVIEW_DISPLAY_SIZE_CHANGED.ordinal()] = 26;
        } catch (NoSuchFieldError e19) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_RESTORE_SETTINGS.ordinal()] = 27;
        } catch (NoSuchFieldError e20) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_SELFTIMER_STATE.ordinal()] = 10;
        } catch (NoSuchFieldError e21) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_SETTING_BUTTON_CLICK.ordinal()] = 11;
        } catch (NoSuchFieldError e22) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_SINGLE_TAP_UP.ordinal()] = 12;
        } catch (NoSuchFieldError e23) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_START_PREVIEW.ordinal()] = 13;
        } catch (NoSuchFieldError e24) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_STOP_PREVIEW.ordinal()] = 28;
        } catch (NoSuchFieldError e25) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_SURFACE_TEXTURE_READY.ordinal()] = 29;
        } catch (NoSuchFieldError e26) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_USER_INTERACTION.ordinal()] = 30;
        } catch (NoSuchFieldError e27) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ORITATION_CHANGED.ordinal()] = 31;
        } catch (NoSuchFieldError e28) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_PHOTO_SHUTTER_BUTTON_CLICK.ordinal()] = 14;
        } catch (NoSuchFieldError e29) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_PREVIEW_VISIBLE_CHANGED.ordinal()] = 32;
        } catch (NoSuchFieldError e30) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_SET_DISPLAYROTATION.ordinal()] = 33;
        } catch (NoSuchFieldError e31) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_SHUTTER_BUTTON_FOCUS.ordinal()] = 15;
        } catch (NoSuchFieldError e32) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_SHUTTER_BUTTON_LONG_PRESS.ordinal()] = 16;
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
        f104commediatekcameraICameraMode$ActionTypeSwitchesValues = iArr;
        return iArr;
    }

    public FaceBeautyMode(ICameraContext iCameraContext) {
        super(iCameraContext);
        this.mCfbCallback = new CfbCallback();
        this.mVfbFacesPoint = new ArrayList<>();
        this.mIsAutoFocusCallback = false;
        this.mCapturedImageCount = 0;
        this.mShutterCallback = new Camera.ShutterCallback() { // from class: com.mediatek.camera.mode.facebeauty.FaceBeautyMode.1
            @Override // android.hardware.Camera.ShutterCallback
            public void onShutter() {
            }
        };
        this.mRawPictureCallback = new Camera.PictureCallback() { // from class: com.mediatek.camera.mode.facebeauty.FaceBeautyMode.2
            @Override // android.hardware.Camera.PictureCallback
            public void onPictureTaken(byte[] bArr, Camera camera) {
            }
        };
        this.mPostViewPictureCallback = new Camera.PictureCallback() { // from class: com.mediatek.camera.mode.facebeauty.FaceBeautyMode.3
            @Override // android.hardware.Camera.PictureCallback
            public void onPictureTaken(byte[] bArr, Camera camera) {
            }
        };
        this.mUncompressedImageCallback = new Camera.PictureCallback() { // from class: com.mediatek.camera.mode.facebeauty.FaceBeautyMode.4
            @Override // android.hardware.Camera.PictureCallback
            public void onPictureTaken(byte[] bArr, Camera camera) {
                Log.m31d("FaceBeautyMode", "[UncompressedImageCallback]onCanCapture");
                FaceBeautyMode.this.mCapturedImageCount++;
                FaceBeautyMode.this.startPreview(false);
            }
        };
        this.mJpegPictureCallback = new Camera.PictureCallback() { // from class: com.mediatek.camera.mode.facebeauty.FaceBeautyMode.5
            @Override // android.hardware.Camera.PictureCallback
            public void onPictureTaken(byte[] bArr, Camera camera) {
                Log.m31d("FaceBeautyMode", "VFBCallback[mJpegPictureCallback], time = " + System.currentTimeMillis() + ",data = " + bArr);
                if (ICameraMode.ModeState.STATE_CLOSED != FaceBeautyMode.this.getModeState()) {
                    FaceBeautyMode.this.mIFocusManager.updateFocusUI();
                    boolean z = !"on".equals(FaceBeautyMode.this.mISettingCtrl.getSettingValue("pref_camera_zsd_key")) && ICameraMode.ModeState.STATE_CAPTURING == FaceBeautyMode.this.getModeState() && FaceBeautyMode.this.mCapturedImageCount == 0;
                    FaceBeautyMode.this.startPreview(z);
                    FaceBeautyMode.this.mIModuleCtrl.startFaceDetection();
                    if (FaceBeautyMode.this.mCapturedImageCount > 0) {
                        FaceBeautyMode faceBeautyMode = FaceBeautyMode.this;
                        faceBeautyMode.mCapturedImageCount--;
                    }
                }
                if (FaceBeautyMode.this.mICameraView != null) {
                    FaceBeautyMode.this.mICameraView.update(ScriptIntrinsicBLAS.NO_TRANSPOSE, false);
                }
                if (bArr != null) {
                    FaceBeautyMode.this.mIFileSaver.init(IFileSaver.FILE_TYPE.JPEG, 0, null, -1);
                    FaceBeautyMode.this.mIFileSaver.savePhotoFile(bArr, null, System.currentTimeMillis(), FaceBeautyMode.this.mIModuleCtrl.getLocation(), 0, FaceBeautyMode.this.mFileSavedListener);
                }
            }
        };
        this.mFileSavedListener = new IFileSaver.OnFileSavedListener() { // from class: com.mediatek.camera.mode.facebeauty.FaceBeautyMode.6
            @Override // com.mediatek.camera.platform.IFileSaver.OnFileSavedListener
            public void onFileSaved(Uri uri) {
            }
        };
        this.mAutoFocusCallback = new Camera.AutoFocusCallback() { // from class: com.mediatek.camera.mode.facebeauty.FaceBeautyMode.7
            @Override // android.hardware.Camera.AutoFocusCallback
            public void onAutoFocus(boolean z, Camera camera) {
                if (ICameraMode.ModeState.STATE_CLOSED == FaceBeautyMode.this.getModeState()) {
                    return;
                }
                if (ICameraMode.ModeState.STATE_FOCUSING == FaceBeautyMode.this.getModeState()) {
                    FaceBeautyMode.this.mICameraAppUi.restoreViewState();
                    FaceBeautyMode.this.setModeState(ICameraMode.ModeState.STATE_IDLE);
                }
                FaceBeautyMode.this.mIFocusManager.onAutoFocus(z);
                FaceBeautyMode.this.mIsAutoFocusCallback = true;
            }
        };
        this.mAutoFocusMvCallback = new ICameraDeviceManager.ICameraDevice.AutoFocusMvCallback() { // from class: com.mediatek.camera.mode.facebeauty.FaceBeautyMode.8
            @Override // com.mediatek.camera.platform.ICameraDeviceManager.ICameraDevice.AutoFocusMvCallback
            public void onAutoFocusMoving(boolean z, Camera camera) {
                FaceBeautyMode.this.mIFocusManager.onAutoFocusMoving(z);
            }
        };
        this.mAFDataCallback = new Camera$AFDataCallback() { // from class: com.mediatek.camera.mode.facebeauty.FaceBeautyMode.9
            public void onAFData(byte[] bArr, Camera camera) {
                boolean z = FaceBeautyMode.this.mActivity.getResources().getString(R.string.af_multi_mode).equals(FaceBeautyMode.this.mISettingCtrl.getSettingValue("pref_af_mode_key"));
                if (bArr != null && bArr.length > 0 && z) {
                    FaceBeautyMode.this.mIFocusManager.setAfData(bArr);
                } else {
                    Log.m36w("FaceBeautyMode", "onAFData AF data is got in single AF mode with isMultiAfMode = " + z);
                    FaceBeautyMode.this.mIFocusManager.setAfData(null);
                }
            }
        };
        Log.m34i("FaceBeautyMode", "[FaceBeautyMode]constructor...");
        this.mFaceBeautyParametersHelper = new FaceBeautyParametersHelper(iCameraContext);
        this.mParameterListener = this.mFaceBeautyParametersHelper.getListener();
        if (this.mIFeatureConfig.isVfbEnable()) {
            this.mICameraView = this.mICameraAppUi.getCameraView(ICameraAppUi.SpecViewType.MODE_FACE_BEAUTY);
            this.mICameraView.init(this.mActivity, this.mICameraAppUi, this.mIModuleCtrl);
            this.mICameraView.setListener(this.mParameterListener);
            this.mICameraAppUi.changeBackToVFBModeStatues(false);
        } else if (this.mIFeatureConfig.isSlowMotionSupport()) {
            setCfbSlowMotionRule();
        }
        this.mHandler = new MainHandler(this.mActivity.getMainLooper());
        this.mAdditionManager = iCameraContext.getAdditionManager();
        if (this.mICameraContext.getFeatureConfig().isLowRamOptSupport()) {
            setFBLowRamRule();
        }
        if (this.mICameraContext.getFeatureConfig().isGmoRamOptSupport()) {
            setFBGmoRule();
        }
        if (this.mIFeatureConfig.isVfbEnable()) {
            setVFBPreviewSizeRule();
        }
    }

    @Override // com.mediatek.camera.mode.CameraMode, com.mediatek.camera.ICameraMode
    public void pause() {
        super.pause();
        Log.m34i("FaceBeautyMode", "[pause()] mICameraView = " + this.mICameraView);
        if (this.mICameraView != null) {
            this.mICameraView.hide();
        }
    }

    @Override // com.mediatek.camera.mode.CameraMode, com.mediatek.camera.ICameraMode
    public void resume() {
        super.resume();
        Log.m34i("FaceBeautyMode", "[resume()]");
        this.mICameraAppUi.setSwipeEnabled(true);
    }

    @Override // com.mediatek.camera.mode.CameraMode, com.mediatek.camera.ICameraMode
    public boolean close() {
        Log.m34i("FaceBeautyMode", "[closeMode]NextMode = " + this.mIModuleCtrl.getNextMode());
        if (this.mIModuleCtrl.getNextMode() != null) {
            if (this.mIFeatureConfig.isVfbEnable()) {
                if (ICameraMode.CameraModeType.EXT_MODE_VIDEO == this.mIModuleCtrl.getNextMode()) {
                    this.mICameraView.update(105, new Object[0]);
                } else {
                    setVFBPs(false);
                }
            }
            if (this.mHandler != null) {
                this.mHandler.sendEmptyMessage(109);
            }
            if (this.mICameraView != null) {
                this.mICameraView.update(ScriptIntrinsicBLAS.TRANSPOSE, new Object[0]);
            }
            changeFaceBeautyStatues(false);
        }
        removeAllMsg();
        this.mAdditionManager.close(true);
        return true;
    }

    @Override // com.mediatek.camera.mode.CameraMode, com.mediatek.camera.ICameraMode
    public boolean execute(ICameraMode.ActionType actionType, Object... objArr) {
        boolean z = true;
        this.mAdditionManager.execute(actionType, true, objArr);
        switch (m766getcommediatekcameraICameraMode$ActionTypeSwitchesValues()[actionType.ordinal()]) {
            case 1:
                if (ICameraMode.ModeState.STATE_CAPTURING == getModeState()) {
                    z = false;
                    break;
                }
                break;
            case 2:
                Camera.Face[] faceArr = (Camera.Face[]) objArr;
                if (this.mIFeatureConfig.isVfbEnable() && faceArr != null) {
                    this.mICameraView.update(102, Integer.valueOf(faceArr.length));
                    storeFaceBeautyLocation(faceArr);
                    this.mIModuleCtrl.setFaces(faceArr);
                    break;
                }
                break;
            case 3:
                if ((this.mICameraView != null && (!this.mICameraView.update(104, new Object[0]))) || (!this.mIFeatureConfig.isVfbEnable())) {
                    z = false;
                    break;
                }
                break;
            case 4:
                this.mAdditionManager.close(true);
                setModeState(ICameraMode.ModeState.STATE_CLOSED);
                if (this.mIFeatureConfig.isVfbEnable()) {
                    this.mICameraView.update(107, new Object[0]);
                    break;
                }
                break;
            case 5:
                super.updateDevice();
                break;
            case 6:
                super.updateDevice();
                super.updateFocusManager();
                if (this.mIFocusManager != null) {
                    this.mIFocusManager.setListener(this);
                }
                if (this.mActivity.getResources().getString(R.string.af_multi_mode).equals(this.mISettingCtrl.getSettingValue("pref_af_mode_key"))) {
                    this.mICameraDevice.getCamera().setAFDataCallback(this.mAFDataCallback);
                } else {
                    this.mICameraDevice.getCamera().setAFDataCallback(null);
                }
                setModeState(ICameraMode.ModeState.STATE_IDLE);
                if (isVfbOff()) {
                    this.mICameraAppUi.setCurrentMode(ICameraMode.CameraModeType.EXT_MODE_PHOTO);
                    break;
                } else {
                    if (this.mIFeatureConfig.isVfbEnable()) {
                        this.mIModuleCtrl.stopFaceDetection();
                    }
                    this.mFaceBeautyParametersHelper.updateParameters(this.mICameraDevice);
                    this.mHandler.sendEmptyMessage(101);
                    this.mAdditionManager.onCameraParameterReady(true);
                    break;
                }
            case FrameMetricsAggregator.DELAY_INDEX /* 7 */:
                if (this.mICameraView != null) {
                    this.mICameraView.update(103, Integer.valueOf(this.mIModuleCtrl.getOrientationCompensation()));
                    break;
                }
                break;
            case 8:
                if (this.mHandler != null) {
                    this.mHandler.sendEmptyMessage(201);
                    break;
                }
                break;
            case 9:
                if (this.mICameraView != null) {
                    this.mICameraView.update(106, (Boolean) objArr[0]);
                    break;
                }
                break;
            case 10:
                if (this.mICameraView != null) {
                    this.mICameraView.update(110, (Boolean) objArr[0]);
                    break;
                }
                break;
            case 11:
                if (this.mICameraView != null && (!isVfbOff())) {
                    this.mICameraView.update(108, objArr[0]);
                    break;
                }
                break;
            case 12:
                Assert.assertTrue(objArr.length == 3);
                onSinlgeTapUp((View) objArr[0], ((Integer) objArr[1]).intValue(), ((Integer) objArr[2]).intValue());
                break;
            case 13:
                Assert.assertTrue(objArr.length == 1);
                startPreview(((Boolean) objArr[0]).booleanValue());
                break;
            case 14:
                if (ICameraMode.ModeState.STATE_CAPTURING != getModeState()) {
                    if (this.mICameraView != null) {
                        this.mICameraView.update(105, new Object[0]);
                    }
                    if (this.mIFocusManager != null) {
                        this.mIFocusManager.focusAndCapture();
                        break;
                    }
                }
                break;
            case 15:
                break;
            case 16:
                this.mICameraAppUi.showInfo(this.mActivity.getString(R.string.pref_camera_capturemode_enrty_fb) + this.mActivity.getString(R.string.camera_continuous_not_supported));
                break;
            default:
                return false;
        }
        if (ICameraMode.ActionType.ACTION_FACE_DETECTED != actionType) {
            Log.m31d("FaceBeautyMode", "[execute]type =" + actionType + ",returnValue = " + z);
        }
        return z;
    }

    @Override // com.mediatek.camera.mode.CameraMode, com.mediatek.camera.ICameraMode
    public boolean open() {
        this.mAdditionManager.setListener(this);
        this.mAdditionManager.open(true);
        super.open();
        return true;
    }

    @Override // com.mediatek.camera.platform.IFocusManager.FocusListener
    public boolean capture() {
        startCapture();
        return true;
    }

    @Override // com.mediatek.camera.ICameraAddition.Listener
    public boolean restartPreview(boolean z) {
        return false;
    }

    @Override // com.mediatek.camera.ICameraAddition.Listener
    public void onFileSaveing() {
    }

    @Override // com.mediatek.camera.platform.IFocusManager.FocusListener
    public void autoFocus() {
        Log.m31d("FaceBeautyMode", "[autoFocus]...");
        this.mICameraDevice.autoFocus(this.mAutoFocusCallback);
        this.mICameraAppUi.setViewState(ICameraAppUi.ViewState.VIEW_STATE_FOCUSING);
        setModeState(ICameraMode.ModeState.STATE_FOCUSING);
    }

    @Override // com.mediatek.camera.platform.IFocusManager.FocusListener
    public void cancelAutoFocus() {
        if (!this.mIsAutoFocusCallback) {
            this.mICameraDevice.cancelAutoFocus();
            this.mIsAutoFocusCallback = true;
        }
        setFocusParameters();
        if (ICameraMode.ModeState.STATE_CAPTURING != getModeState()) {
            this.mICameraAppUi.restoreViewState();
            setModeState(ICameraMode.ModeState.STATE_IDLE);
        }
    }

    @Override // com.mediatek.camera.platform.IFocusManager.FocusListener
    public void startFaceDetection() {
        this.mIModuleCtrl.startFaceDetection();
    }

    @Override // com.mediatek.camera.platform.IFocusManager.FocusListener
    public void stopFaceDetection() {
        this.mIModuleCtrl.stopFaceDetection();
    }

    @Override // com.mediatek.camera.platform.IFocusManager.FocusListener
    public void setFocusParameters() {
        this.mIModuleCtrl.applyFocusParameters(!this.mIsAutoFocusCallback);
        this.mIsAutoFocusCallback = false;
    }

    @Override // com.mediatek.camera.platform.IFocusManager.FocusListener
    public void playSound(int i) {
        this.mCameraSound.play(i);
    }

    private void removeAllMsg() {
        if (this.mHandler != null) {
            this.mHandler.removeMessages(101);
            this.mHandler.removeMessages(102);
            this.mHandler.removeMessages(103);
        }
    }

    private class MainHandler extends Handler {
        public MainHandler(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            Log.m31d("FaceBeautyMode", "[handleMessage],msg = " + message.what);
            switch (message.what) {
                case 101:
                    if (FaceBeautyMode.this.mICameraView != null) {
                        FaceBeautyMode.this.mICameraView.update(101, new Object[0]);
                        FaceBeautyMode.this.mICameraView.show();
                    }
                    FaceBeautyMode.this.mIModuleCtrl.initializeFrameView(false);
                    FaceBeautyMode.this.setVFBParameters();
                    FaceBeautyMode.this.changeFaceBeautyStatues(true);
                    break;
                case 109:
                    if (FaceBeautyMode.this.mICameraView != null) {
                        FaceBeautyMode.this.mICameraView.update(109, new Object[0]);
                        break;
                    }
                    break;
                case 201:
                    if (FaceBeautyMode.this.mICameraView != null) {
                        FaceBeautyMode.this.mICameraView.uninit();
                        FaceBeautyMode.this.mICameraView.init(FaceBeautyMode.this.mActivity, FaceBeautyMode.this.mICameraAppUi, FaceBeautyMode.this.mIModuleCtrl);
                        break;
                    }
                    break;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setVFBParameters() {
        if (this.mICameraDevice != null && this.mIModuleCtrl.isNonePickIntent() && this.mIFeatureConfig.isVfbEnable()) {
            if (isOnlyMultiFaceBeautySupported()) {
                this.mICameraDevice.setParameter("fb-extreme-beauty", "false");
            }
            setVFBPs(true);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void changeFaceBeautyStatues(boolean z) {
        this.mIModuleCtrl.setFaceBeautyEnalbe(z);
    }

    private boolean isOnlyMultiFaceBeautySupported() {
        boolean zEquals = "false".equals(this.mICameraDevice.getParameter("fb-extreme-beauty-supported"));
        Log.m31d("FaceBeautyMode", "isOnlyMultiFaceBeautySupported = " + zEquals);
        return zEquals;
    }

    private void setVFBPs(boolean z) {
        this.mICameraDevice.setParameter("face-beauty", z ? "true" : "false");
        this.mICameraDevice.applyParameters();
    }

    private void setFBLowRamRule() {
        FaceBeautyPictureSizeRule faceBeautyPictureSizeRule = new FaceBeautyPictureSizeRule(this.mISettingCtrl, this.mICameraContext);
        this.mISettingCtrl.addRule("face_beauty_key", "pref_camera_picturesize_key", faceBeautyPictureSizeRule);
        faceBeautyPictureSizeRule.addLimitation("on", null, null);
    }

    private void setFBGmoRule() {
        FaceBeautyZsdRule faceBeautyZsdRule = new FaceBeautyZsdRule(this.mISettingCtrl, this.mICameraContext);
        this.mISettingCtrl.addRule("face_beauty_key", "pref_camera_zsd_key", faceBeautyZsdRule);
        faceBeautyZsdRule.addLimitation("on", null, null);
    }

    private void setCfbSlowMotionRule() {
        CaptureFaceBeautyRule captureFaceBeautyRule = new CaptureFaceBeautyRule(this.mICameraContext);
        this.mISettingCtrl.addRule("face_beauty_key", "pref_slow_motion_key", captureFaceBeautyRule);
        captureFaceBeautyRule.addLimitation("on", null, null);
    }

    private void setVFBPreviewSizeRule() {
        this.mFaceBeautyPreviewSize = new FaceBeautyPreviewSize(this.mICameraContext);
        this.mISettingCtrl.addRule("face_beauty_key", "pref_camera_picturesize_ratio_key", this.mFaceBeautyPreviewSize);
        this.mFaceBeautyPreviewSize.addLimitation("on", null, null);
    }

    private void onSinlgeTapUp(View view, int i, int i2) {
        if (ICameraMode.ModeState.STATE_IDLE != getModeState()) {
            return;
        }
        String focusMode = this.mIFocusManager != null ? this.mIFocusManager.getFocusMode() : null;
        if (this.mICameraDevice == null || focusMode == null || "infinity".equals(focusMode) || !this.mIFocusManager.getFocusAreaSupported()) {
            return;
        }
        if (this.mICameraView != null) {
            this.mICameraView.update(105, new Object[0]);
        }
        this.mIFocusManager.onSingleTapUp(i, i2);
    }

    private boolean startCapture() {
        if (ICameraMode.ModeState.STATE_IDLE != getModeState() || (!isEnoughSpace())) {
            Log.m31d("FaceBeautyMode", "[startCapture],invalid state, return!");
            return false;
        }
        if (this.mICameraView != null) {
            this.mICameraView.update(ScriptIntrinsicBLAS.NO_TRANSPOSE, true);
        }
        this.mICameraAppUi.setSwipeEnabled(false);
        this.mICameraAppUi.showRemaining();
        this.mICameraAppUi.setViewState(ICameraAppUi.ViewState.VIEW_STATE_CAPTURE);
        this.mICameraDevice.setcFBOrignalCallback(this.mCfbCallback);
        if (this.mIFeatureConfig.isVfbEnable()) {
            setvFBFacePoints();
        }
        setModeState(ICameraMode.ModeState.STATE_CAPTURING);
        this.mICameraDevice.takePicture(this.mShutterCallback, this.mRawPictureCallback, this.mPostViewPictureCallback, this.mJpegPictureCallback);
        return true;
    }

    private void storeFaceBeautyLocation(Camera.Face[] faceArr) {
        if (this.mVfbFacesPoint != null && this.mVfbFacesPoint.size() != 0) {
            this.mVfbFacesPoint.clear();
        }
        if (faceArr != null) {
            int i = 0;
            for (int i2 = 0; i2 < faceArr.length; i2++) {
                if (100 == faceArr[i2].score) {
                    int i3 = faceArr[i2].rect.left + ((faceArr[i2].rect.right - faceArr[i2].rect.left) / 2);
                    int i4 = faceArr[i2].rect.top + ((faceArr[i2].rect.bottom - faceArr[i2].rect.top) / 2);
                    int i5 = i + 1;
                    this.mVfbFacesPoint.add(i, Integer.valueOf(i3));
                    i = i5 + 1;
                    this.mVfbFacesPoint.add(i5, Integer.valueOf(i4));
                }
            }
        }
    }

    private void setvFBFacePoints() {
        String facePose;
        if (this.mVfbFacesPoint != null && (facePose = setFacePose()) != null) {
            this.mICameraDevice.setParameter("fb-face-pos", facePose);
            this.mICameraDevice.applyParameters();
        }
    }

    private String setFacePose() {
        String str = "";
        for (int i = 0; i < this.mVfbFacesPoint.size(); i++) {
            str = str + this.mVfbFacesPoint.get(i);
            if (i + 1 != this.mVfbFacesPoint.size()) {
                if (i % 2 != 0) {
                    str = str + ",";
                } else {
                    str = str + ":";
                }
            }
        }
        return str;
    }

    public class CfbCallback implements ICameraDeviceManager.ICameraDevice.cFbOriginalCallback {
        public CfbCallback() {
        }

        @Override // com.mediatek.camera.platform.ICameraDeviceManager.ICameraDevice.cFbOriginalCallback
        public void onOriginalCallback(byte[] bArr) {
            Log.m31d("FaceBeautyMode", "cFBCallback,[onOriginalCallback],data.length = " + bArr.length);
            if (!FaceBeautyMode.this.mIFeatureConfig.isVfbEnable()) {
                FaceBeautyMode.this.mIFileSaver.init(IFileSaver.FILE_TYPE.JPEG, 0, null, -1);
                FaceBeautyMode.this.mIFileSaver.savePhotoFile(bArr, null, System.currentTimeMillis(), FaceBeautyMode.this.mIModuleCtrl.getLocation(), 0, FaceBeautyMode.this.mFileSavedListener);
            }
        }
    }

    private boolean isVfbOff() {
        return this.mActivity.getResources().getString(R.string.pref_face_beauty_mode_off).equals(this.mISettingCtrl.getSettingValue("pref_face_beauty_multi_mode_key"));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startPreview(boolean z) {
        Log.m31d("FaceBeautyMode", "[startPreview]needStop = " + z);
        this.mIsAutoFocusCallback = false;
        this.mActivity.runOnUiThread(new Runnable() { // from class: com.mediatek.camera.mode.facebeauty.FaceBeautyMode.10
            @Override // java.lang.Runnable
            public void run() {
                FaceBeautyMode.this.mIFocusManager.resetTouchFocus();
            }
        });
        if (z) {
            stopPreview();
        }
        this.mIFocusManager.setAeLock(false);
        this.mIFocusManager.setAwbLock(false);
        this.mIModuleCtrl.applyFocusParameters(false);
        this.mICameraDevice.startPreview();
        this.mICameraAppUi.restoreViewState();
        this.mICameraDevice.setAutoFocusMoveCallback(this.mAutoFocusMvCallback);
        this.mIFocusManager.onPreviewStarted();
        this.mAdditionManager.execute(ICameraAddition.AdditionActionType.ACTION_ON_START_PREVIEW, new Object[0]);
        setModeState(ICameraMode.ModeState.STATE_IDLE);
        this.mICameraDevice.setUncompressedImageCallback(this.mUncompressedImageCallback);
    }

    private void stopPreview() {
        Log.m31d("FaceBeautyMode", "[stopPreview]mCurrentState = " + getModeState());
        if (ICameraMode.ModeState.STATE_CLOSED == getModeState()) {
            return;
        }
        this.mICameraDevice.cancelAutoFocus();
        this.mICameraDevice.stopPreview();
        if (this.mIFocusManager != null) {
            this.mActivity.runOnUiThread(new Runnable() { // from class: com.mediatek.camera.mode.facebeauty.FaceBeautyMode.11
                @Override // java.lang.Runnable
                public void run() {
                    FaceBeautyMode.this.mIFocusManager.onPreviewStopped();
                }
            });
        }
    }
}
