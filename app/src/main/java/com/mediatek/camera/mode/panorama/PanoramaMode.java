package com.mediatek.camera.mode.panorama;

import android.hardware.Camera;
import android.hardware.Camera$AFDataCallback;
import android.media.MediaActionSound;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.FrameMetricsAggregator;
import com.mediatek.camera.ICameraContext;
import com.mediatek.camera.ICameraMode;
import com.mediatek.camera.R;
import com.mediatek.camera.mode.CameraMode;
import com.mediatek.camera.platform.ICameraAppUi;
import com.mediatek.camera.platform.ICameraDeviceManager;
import com.mediatek.camera.platform.ICameraView;
import com.mediatek.camera.platform.IFileSaver;
import com.mediatek.camera.util.Log;
import junit.framework.Assert;

/* loaded from: classes.dex */
public class PanoramaMode extends CameraMode {

    /* renamed from: -com-mediatek-camera-ICameraMode$ActionTypeSwitchesValues, reason: not valid java name */
    private static final /* synthetic */ int[] f105commediatekcameraICameraMode$ActionTypeSwitchesValues = null;
    private final Camera$AFDataCallback mAFDataCallback;
    private final ICameraDeviceManager.ICameraDevice.AutoFocusMvCallback mAutoFocusMoveCallback;
    private MediaActionSound mCameraSound;
    private long mCaptureTime;
    private int mCurrentNum;
    private Runnable mFalseShutterCallback;
    private IFileSaver.OnFileSavedListener mFileSaverListener;
    private ICameraView mICameraView;
    private boolean mIsInStopProcess;
    private boolean mIsMerging;
    private boolean mIsShowingCollimatedDrawable;
    private byte[] mJpegImageData;
    private Thread mLoadSoundTread;
    private Object mLock;
    private Handler mMainHandler;
    private Runnable mOnHardwareStop;
    private int mOrientation;
    private ICameraDeviceManager.ICameraDevice.PanoramaListener mPanoramaCallback;
    private ICameraDeviceManager.ICameraDevice.PanoramaMvListener mPanoramaMVCallback;
    private Runnable mRestartCaptureView;

    /* renamed from: -getcom-mediatek-camera-ICameraMode$ActionTypeSwitchesValues, reason: not valid java name */
    private static /* synthetic */ int[] m809getcommediatekcameraICameraMode$ActionTypeSwitchesValues() {
        if (f105commediatekcameraICameraMode$ActionTypeSwitchesValues != null) {
            return f105commediatekcameraICameraMode$ActionTypeSwitchesValues;
        }
        int[] iArr = new int[ICameraMode.ActionType.valuesCustom().length];
        try {
            iArr[ICameraMode.ActionType.ACTION_CANCEL_BUTTON_CLICK.ordinal()] = 1;
        } catch (NoSuchFieldError e) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_CAN_DO_AUTO_FOCUS.ordinal()] = 2;
        } catch (NoSuchFieldError e2) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_DISABLE_VIDEO_RECORD.ordinal()] = 13;
        } catch (NoSuchFieldError e3) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_FACE_DETECTED.ordinal()] = 14;
        } catch (NoSuchFieldError e4) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_NOTIFY_SURFCEVIEW_DESTROYED.ordinal()] = 15;
        } catch (NoSuchFieldError e5) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_NOTIFY_SURFCEVIEW_DISPLAY_IS_READY.ordinal()] = 16;
        } catch (NoSuchFieldError e6) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_OK_BUTTON_CLICK.ordinal()] = 3;
        } catch (NoSuchFieldError e7) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_BACK_KEY_PRESS.ordinal()] = 4;
        } catch (NoSuchFieldError e8) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_CAMERA_CLOSE.ordinal()] = 5;
        } catch (NoSuchFieldError e9) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_CAMERA_OPEN.ordinal()] = 6;
        } catch (NoSuchFieldError e10) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_CAMERA_PARAMETERS_READY.ordinal()] = 7;
        } catch (NoSuchFieldError e11) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_COMPENSATION_CHANGED.ordinal()] = 8;
        } catch (NoSuchFieldError e12) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_CONFIGURATION_CHANGED.ordinal()] = 17;
        } catch (NoSuchFieldError e13) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_FULL_SCREEN_CHANGED.ordinal()] = 9;
        } catch (NoSuchFieldError e14) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_KEY_EVENT_PRESS.ordinal()] = 18;
        } catch (NoSuchFieldError e15) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_LONG_PRESS.ordinal()] = 19;
        } catch (NoSuchFieldError e16) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_MEDIA_EJECT.ordinal()] = 20;
        } catch (NoSuchFieldError e17) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_PREVIEW_BUFFER_SIZE_CHANGED.ordinal()] = 21;
        } catch (NoSuchFieldError e18) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_PREVIEW_DISPLAY_SIZE_CHANGED.ordinal()] = 22;
        } catch (NoSuchFieldError e19) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_RESTORE_SETTINGS.ordinal()] = 23;
        } catch (NoSuchFieldError e20) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_SELFTIMER_STATE.ordinal()] = 24;
        } catch (NoSuchFieldError e21) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_SETTING_BUTTON_CLICK.ordinal()] = 25;
        } catch (NoSuchFieldError e22) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_SINGLE_TAP_UP.ordinal()] = 10;
        } catch (NoSuchFieldError e23) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_START_PREVIEW.ordinal()] = 26;
        } catch (NoSuchFieldError e24) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_STOP_PREVIEW.ordinal()] = 27;
        } catch (NoSuchFieldError e25) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_SURFACE_TEXTURE_READY.ordinal()] = 28;
        } catch (NoSuchFieldError e26) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_USER_INTERACTION.ordinal()] = 29;
        } catch (NoSuchFieldError e27) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ORITATION_CHANGED.ordinal()] = 30;
        } catch (NoSuchFieldError e28) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_PHOTO_SHUTTER_BUTTON_CLICK.ordinal()] = 11;
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
            iArr[ICameraMode.ActionType.ACTION_SHUTTER_BUTTON_LONG_PRESS.ordinal()] = 12;
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
        f105commediatekcameraICameraMode$ActionTypeSwitchesValues = iArr;
        return iArr;
    }

    public PanoramaMode(ICameraContext iCameraContext) {
        super(iCameraContext);
        this.mCurrentNum = 0;
        this.mOrientation = 0;
        this.mCaptureTime = 0L;
        this.mJpegImageData = null;
        this.mIsInStopProcess = false;
        this.mIsMerging = false;
        this.mLock = new Object();
        this.mAutoFocusMoveCallback = new ICameraDeviceManager.ICameraDevice.AutoFocusMvCallback() { // from class: com.mediatek.camera.mode.panorama.PanoramaMode.1
            @Override // com.mediatek.camera.platform.ICameraDeviceManager.ICameraDevice.AutoFocusMvCallback
            public void onAutoFocusMoving(boolean z, Camera camera) {
                Log.m34i("PanoramaMode", "[onAutoFocusMoving]moving = " + z);
                PanoramaMode.this.mIFocusManager.onAutoFocusMoving(z);
            }
        };
        this.mAFDataCallback = new Camera$AFDataCallback() { // from class: com.mediatek.camera.mode.panorama.PanoramaMode.2
            public void onAFData(byte[] bArr, Camera camera) {
                boolean z = PanoramaMode.this.mActivity.getResources().getString(R.string.af_multi_mode).equals(PanoramaMode.this.mISettingCtrl.getSettingValue("pref_af_mode_key"));
                if (bArr != null && bArr.length > 0 && z) {
                    PanoramaMode.this.mIFocusManager.setAfData(bArr);
                } else {
                    Log.m36w("PanoramaMode", "onAFData AF data is got in single AF mode with isMultiAfMode = " + z);
                    PanoramaMode.this.mIFocusManager.setAfData(null);
                }
            }
        };
        this.mFileSaverListener = new IFileSaver.OnFileSavedListener() { // from class: com.mediatek.camera.mode.panorama.PanoramaMode.3
            @Override // com.mediatek.camera.platform.IFileSaver.OnFileSavedListener
            public void onFileSaved(Uri uri) {
                Log.m34i("PanoramaMode", "[onFileSaved]uri = " + uri);
                PanoramaMode.this.mMainHandler.sendEmptyMessage(1000);
            }
        };
        this.mFalseShutterCallback = new Runnable() { // from class: com.mediatek.camera.mode.panorama.PanoramaMode.4
            @Override // java.lang.Runnable
            public void run() {
                PanoramaMode.this.mIFocusManager.resetTouchFocus();
                PanoramaMode.this.mIFocusManager.updateFocusUI();
            }
        };
        this.mPanoramaMVCallback = new ICameraDeviceManager.ICameraDevice.PanoramaMvListener() { // from class: com.mediatek.camera.mode.panorama.PanoramaMode.5
            @Override // com.mediatek.camera.platform.ICameraDeviceManager.ICameraDevice.PanoramaMvListener
            public void onFrame(int i, int i2) {
                PanoramaMode.this.mMainHandler.obtainMessage(1005, i, i2).sendToTarget();
            }
        };
        this.mPanoramaCallback = new ICameraDeviceManager.ICameraDevice.PanoramaListener() { // from class: com.mediatek.camera.mode.panorama.PanoramaMode.6
            @Override // com.mediatek.camera.platform.ICameraDeviceManager.ICameraDevice.PanoramaListener
            public void onCapture(byte[] bArr) {
                PanoramaMode.this.onPictureTaken(bArr);
            }
        };
        Log.m34i("PanoramaMode", "[PanoramaMode]constructor...");
        this.mICameraView = this.mICameraAppUi.getCameraView(ICameraAppUi.SpecViewType.MODE_PANORAMA);
        this.mICameraView.init(this.mActivity, this.mICameraAppUi, this.mIModuleCtrl);
        this.mCameraSound = new MediaActionSound();
        this.mLoadSoundTread = new LoadSoundTread(this, null);
        this.mLoadSoundTread.start();
        this.mMainHandler = new PanoramaHandler(this.mActivity.getMainLooper());
    }

    @Override // com.mediatek.camera.mode.CameraMode, com.mediatek.camera.ICameraMode
    public void pause() {
        super.pause();
        Log.m34i("PanoramaMode", "[pasue]mMainHandler = " + this.mMainHandler + ", mIsMerging = " + this.mIsMerging);
        if (this.mMainHandler != null) {
            this.mMainHandler.removeMessages(1008);
            this.mMainHandler.sendEmptyMessage(1008);
            if (this.mIsMerging) {
                this.mIsMerging = false;
                this.mMainHandler.removeMessages(1000);
                this.mMainHandler.sendEmptyMessage(1000);
            }
        }
    }

    @Override // com.mediatek.camera.mode.CameraMode, com.mediatek.camera.ICameraMode
    public boolean close() {
        Log.m34i("PanoramaMode", "[close] ");
        safeStop();
        if (ICameraMode.ModeState.STATE_CAPTURING == getModeState()) {
            this.mICameraView.reset();
            this.mICameraView.hide();
            this.mICameraDevice.stopAutoRama(false);
            this.mICameraDevice.setAutoRamaCallback(null);
            this.mICameraDevice.setAutoRamaMoveCallback(null);
        }
        if (this.mCameraSound != null) {
            this.mCameraSound.release();
            this.mCameraSound = null;
        }
        this.mICameraAppUi.setSwipeEnabled(true);
        this.mICameraView.uninit();
        return true;
    }

    @Override // com.mediatek.camera.mode.CameraMode, com.mediatek.camera.ICameraMode
    public boolean execute(ICameraMode.ActionType actionType, Object... objArr) {
        boolean z = true;
        switch (m809getcommediatekcameraICameraMode$ActionTypeSwitchesValues()[actionType.ordinal()]) {
            case 1:
                if (!this.mIsInStopProcess) {
                    this.mICameraAppUi.setOkButtonEnabled(false);
                    stopCapture(false);
                    break;
                }
                break;
            case 2:
                if (ICameraMode.ModeState.STATE_CAPTURING == getModeState()) {
                    z = false;
                    break;
                }
                break;
            case 3:
                stopCapture(true);
                break;
            case 4:
                return onBackPressed();
            case 5:
                stopCapture(false);
                setModeState(ICameraMode.ModeState.STATE_CLOSED);
                break;
            case 6:
                super.updateDevice();
                break;
            case FrameMetricsAggregator.DELAY_INDEX /* 7 */:
                super.updateDevice();
                super.updateFocusManager();
                setModeState(ICameraMode.ModeState.STATE_IDLE);
                this.mICameraDevice.setAutoFocusMoveCallback(this.mAutoFocusMoveCallback);
                if (this.mActivity.getResources().getString(R.string.af_multi_mode).equals(this.mISettingCtrl.getSettingValue("pref_af_mode_key"))) {
                    this.mICameraDevice.getCamera().setAFDataCallback(this.mAFDataCallback);
                    break;
                } else {
                    this.mICameraDevice.getCamera().setAFDataCallback(null);
                    break;
                }
            case 8:
                Assert.assertTrue(objArr.length == 1);
                this.mOrientation = ((Integer) objArr[0]).intValue();
                this.mMainHandler.sendEmptyMessage(1001);
                break;
            case 9:
                Assert.assertTrue(objArr.length == 1);
                Log.m34i("PanoramaMode", "[execute]type = " + actionType + ",full:" + ((Boolean) objArr[0]));
                if (((Boolean) objArr[0]).booleanValue()) {
                    this.mMainHandler.sendEmptyMessage(1011);
                    break;
                } else {
                    this.mMainHandler.sendEmptyMessage(1012);
                    break;
                }
            case 10:
                Log.m34i("PanoramaMode", "current state : " + getModeState());
                z = ICameraMode.ModeState.STATE_IDLE != getModeState();
                break;
            case 11:
                capture();
                break;
            case 12:
                this.mMainHandler.sendEmptyMessage(1013);
                break;
            default:
                z = false;
                break;
        }
        Log.m34i("PanoramaMode", "[execute]type = " + actionType + ",returnValue = " + z);
        return z;
    }

    private boolean capture() {
        Log.m34i("PanoramaMode", "[capture] current state = " + getModeState() + ",mIsMerging = " + this.mIsMerging);
        if (!isEnoughSpace() || ICameraMode.ModeState.STATE_IDLE != getModeState() || this.mIsMerging) {
            Log.m36w("PanoramaMode", "[capture]return,mIsCameraClosed = " + getModeState());
            return false;
        }
        if (!startCapture()) {
            Log.m36w("PanoramaMode", "[capture]not capture.");
            return false;
        }
        this.mIFocusManager.resetTouchFocus();
        this.mIFocusManager.updateFocusUI();
        this.mIFocusManager.setAwbLock(true);
        this.mIModuleCtrl.applyFocusParameters(false);
        this.mICameraAppUi.switchShutterType(ICameraAppUi.ShutterButtonType.SHUTTER_TYPE_OK_CANCEL);
        this.mIFileSaver.init(IFileSaver.FILE_TYPE.PANORAMA, 0, null, -1);
        this.mCaptureTime = System.currentTimeMillis();
        this.mICameraAppUi.setSwipeEnabled(false);
        this.mICameraAppUi.showRemaining();
        this.mICameraAppUi.setViewState(ICameraAppUi.ViewState.VIEW_STATE_CONTINUOUS_CAPTURE);
        ICameraView cameraView = this.mICameraAppUi.getCameraView(ICameraAppUi.CommonUiType.THUMBNAIL);
        if (cameraView != null) {
            cameraView.hide();
        }
        this.mMainHandler.sendEmptyMessage(1009);
        this.mIModuleCtrl.stopFaceDetection();
        this.mICameraDevice.setAutoFocusMoveCallback(null);
        showGuideString(1);
        this.mMainHandler.postDelayed(this.mFalseShutterCallback, 300L);
        return true;
    }

    private void stopCapture(boolean z) {
        Log.m31d("PanoramaMode", "[stopCapture]isMerge = " + z + ",current mode state = " + getModeState());
        if (ICameraMode.ModeState.STATE_CAPTURING == getModeState()) {
            this.mMainHandler.sendEmptyMessage(1010);
            stop(z);
        }
    }

    private boolean onBackPressed() {
        if (ICameraMode.ModeState.STATE_CAPTURING != getModeState()) {
            return false;
        }
        stopCapture(false);
        return true;
    }

    private boolean startCapture() {
        Log.m31d("PanoramaMode", "[startCapture]modeState = " + getModeState() + ",mIsInStopProcess = " + this.mIsInStopProcess);
        if (ICameraMode.ModeState.STATE_IDLE != getModeState() || !(!this.mIsInStopProcess)) {
            return false;
        }
        setModeState(ICameraMode.ModeState.STATE_CAPTURING);
        this.mCurrentNum = 0;
        this.mIsShowingCollimatedDrawable = false;
        this.mICameraDevice.setAutoRamaCallback(this.mPanoramaCallback);
        this.mICameraDevice.setAutoRamaMoveCallback(this.mPanoramaMVCallback);
        this.mICameraDevice.startAutoRama(9);
        this.mICameraAppUi.setOkButtonEnabled(false);
        this.mICameraView.show();
        return true;
    }

    private class LoadSoundTread extends Thread {
        /* synthetic */ LoadSoundTread(PanoramaMode panoramaMode, LoadSoundTread loadSoundTread) {
            this();
        }

        private LoadSoundTread() {
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            PanoramaMode.this.mCameraSound.load(0);
        }
    }

    private class PanoramaHandler extends Handler {
        public PanoramaHandler(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            if (message.what != 1005) {
                Log.m34i("PanoramaMode", "[handleMessage]msg.what = " + message.what);
            }
            switch (message.what) {
                case 1000:
                    PanoramaMode.this.mICameraAppUi.dismissProgress();
                    PanoramaMode.this.mICameraAppUi.setSwipeEnabled(true);
                    PanoramaMode.this.resetCapture();
                    break;
                case 1001:
                    PanoramaMode.this.mICameraView.onOrientationChanged(PanoramaMode.this.mOrientation);
                    break;
                case 1002:
                    PanoramaMode.this.mActivity.getWindow().clearFlags(128);
                    break;
                case 1003:
                    PanoramaMode.this.mIModuleCtrl.lockOrientation();
                    break;
                case 1004:
                    PanoramaMode.this.saveFile();
                    break;
                case 1005:
                    boolean z = PanoramaMode.this.mIsShowingCollimatedDrawable || ICameraMode.ModeState.STATE_CAPTURING != PanoramaMode.this.getModeState() || PanoramaMode.this.mCurrentNum < 1;
                    PanoramaMode.this.mICameraView.update(1, Integer.valueOf(message.arg1), Integer.valueOf(message.arg2), Boolean.valueOf(z));
                    break;
                case 1006:
                    PanoramaMode.this.mICameraView.update(0, Integer.valueOf(message.arg1));
                    break;
                case 1007:
                    PanoramaMode.this.mICameraView.update(2, Integer.valueOf(message.arg1));
                    break;
                case 1008:
                    PanoramaMode.this.mICameraView.reset();
                    PanoramaMode.this.mICameraView.hide();
                    break;
                case 1009:
                    PanoramaMode.this.mICameraView.update(3, Integer.valueOf(message.arg1));
                    break;
                case 1010:
                    PanoramaMode.this.mICameraView.update(4, Integer.valueOf(message.arg1));
                    break;
                case 1011:
                    PanoramaMode.this.mICameraView.init(PanoramaMode.this.mActivity, PanoramaMode.this.mICameraAppUi, PanoramaMode.this.mIModuleCtrl);
                    break;
                case 1012:
                    PanoramaMode.this.mICameraView.uninit();
                    break;
                case 1013:
                    PanoramaMode.this.mICameraAppUi.showInfo(PanoramaMode.this.mActivity.getString(R.string.pano_dialog_title) + PanoramaMode.this.mActivity.getString(R.string.camera_continuous_not_supported));
                    break;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void resetCapture() {
        Log.m31d("PanoramaMode", "[resetCapture]...current mode state = " + getModeState());
        if (ICameraMode.ModeState.STATE_CLOSED != getModeState()) {
            this.mIFocusManager.setAeLock(false);
            this.mIFocusManager.setAwbLock(false);
            this.mIModuleCtrl.applyFocusParameters(false);
            this.mIModuleCtrl.startFaceDetection();
            this.mICameraDevice.setAutoFocusMoveCallback(this.mAutoFocusMoveCallback);
            showGuideString(0);
        }
        this.mICameraAppUi.switchShutterType(ICameraAppUi.ShutterButtonType.SHUTTER_TYPE_PHOTO_VIDEO);
        this.mICameraAppUi.restoreViewState();
        this.mICameraAppUi.setSwipeEnabled(true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void saveFile() {
        Log.m31d("PanoramaMode", "[saveFile]...");
        this.mIFileSaver.savePhotoFile(this.mJpegImageData, null, this.mCaptureTime, this.mIModuleCtrl.getLocation(), 0, this.mFileSaverListener);
        this.mMainHandler.sendEmptyMessage(1010);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onPictureTaken(byte[] bArr) {
        Log.m31d("PanoramaMode", "[onPictureTaken]modeState = " + getModeState() + ",mCurrentNum = " + this.mCurrentNum);
        if (ICameraMode.ModeState.STATE_IDLE == getModeState()) {
            Log.m36w("PanoramaMode", "[onPictureTaken]modeState is STATE_IDLE,return.");
            return;
        }
        if (this.mCurrentNum == 9 || this.mIsMerging) {
            Log.m31d("PanoramaMode", "[onPictureTaken]autorama done,mCurrentNum = " + this.mCurrentNum);
            this.mJpegImageData = bArr;
            this.mIsMerging = false;
            onHardwareStopped(true);
        } else if (this.mCurrentNum >= 0 && this.mCurrentNum < 9) {
            if (this.mCameraSound != null) {
                this.mCameraSound.play(0);
            }
            this.mMainHandler.obtainMessage(1006, this.mCurrentNum, 0).sendToTarget();
            if (this.mCurrentNum > 0) {
                if (this.mIsShowingCollimatedDrawable) {
                    this.mMainHandler.removeCallbacks(this.mRestartCaptureView);
                    this.mMainHandler.removeCallbacks(this.mOnHardwareStop);
                }
                this.mIsShowingCollimatedDrawable = true;
                this.mRestartCaptureView = new Runnable() { // from class: com.mediatek.camera.mode.panorama.PanoramaMode.7
                    @Override // java.lang.Runnable
                    public void run() {
                        PanoramaMode.this.mIsShowingCollimatedDrawable = false;
                        PanoramaMode.this.mMainHandler.obtainMessage(1007, PanoramaMode.this.mCurrentNum, 0).sendToTarget();
                    }
                };
                this.mMainHandler.postDelayed(this.mRestartCaptureView, 500L);
            }
        }
        this.mCurrentNum++;
        if (this.mCurrentNum == 2) {
            this.mICameraAppUi.setOkButtonEnabled(true);
        }
        if (this.mCurrentNum == 9) {
            stop(true);
        }
    }

    private void stop(boolean z) {
        Log.m31d("PanoramaMode", "[stop]isMerge = " + z + ",modeState=" + getModeState() + ",mIsMerging = " + this.mIsMerging);
        if (ICameraMode.ModeState.STATE_CAPTURING != getModeState()) {
            Log.m34i("PanoramaMode", "[stop] current mode state is not capturing,so return");
            return;
        }
        if (this.mIsMerging) {
            Log.m34i("PanoramaMode", "[stop] current is also in merging,so cancle this time");
            return;
        }
        this.mIsMerging = z;
        if (!z) {
            this.mICameraDevice.setAutoRamaCallback(null);
        } else {
            this.mICameraAppUi.showProgress(this.mActivity.getString(R.string.saving));
            this.mICameraAppUi.dismissInfo();
        }
        this.mICameraDevice.setAutoRamaMoveCallback(null);
        this.mMainHandler.removeMessages(1005);
        this.mMainHandler.removeMessages(1008);
        this.mMainHandler.sendEmptyMessage(1008);
        stopAsync(z);
        this.mICameraAppUi.setSwipeEnabled(true);
        this.mIModuleCtrl.unlockOrientation();
    }

    private void stopAsync(final boolean z) {
        Log.m34i("PanoramaMode", "[stopAsync]isMerge=" + z + ",mIsInStopProcess = " + this.mIsInStopProcess);
        if (this.mIsInStopProcess) {
            return;
        }
        Thread thread = new Thread(new Runnable() { // from class: com.mediatek.camera.mode.panorama.PanoramaMode.8
            @Override // java.lang.Runnable
            public void run() {
                PanoramaMode.this.doStop(z);
                PanoramaMode panoramaMode = PanoramaMode.this;
                final boolean z2 = z;
                panoramaMode.mOnHardwareStop = new Runnable() { // from class: com.mediatek.camera.mode.panorama.PanoramaMode.8.1
                    @Override // java.lang.Runnable
                    public void run() {
                        if (!z2) {
                            PanoramaMode.this.onHardwareStopped(false);
                        }
                    }
                };
                PanoramaMode.this.mMainHandler.post(PanoramaMode.this.mOnHardwareStop);
                synchronized (PanoramaMode.this.mLock) {
                    PanoramaMode.this.mIsInStopProcess = false;
                    PanoramaMode.this.mLock.notifyAll();
                }
            }
        });
        synchronized (this.mLock) {
            this.mIsInStopProcess = true;
        }
        thread.start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void doStop(boolean z) {
        Log.m31d("PanoramaMode", "[doStop]isMerge=" + z);
        this.mICameraDevice.stopAutoRama(z);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onHardwareStopped(boolean z) {
        Log.m31d("PanoramaMode", "[onHardwareStopped]isMerge = " + z);
        if (z) {
            this.mICameraDevice.setAutoRamaCallback(null);
        }
        onCaptureDone(z);
    }

    private void onCaptureDone(boolean z) {
        Log.m31d("PanoramaMode", "[onCaptureDone]isMerge = " + z);
        if (z && this.mJpegImageData != null) {
            this.mMainHandler.sendEmptyMessage(1004);
        } else {
            resetCapture();
        }
        setModeState(ICameraMode.ModeState.STATE_IDLE);
    }

    private void safeStop() {
        Log.m34i("PanoramaMode", "[safeStop] check stopAsync thread state, if running,we must wait");
        while (this.mIsInStopProcess) {
            try {
                synchronized (this.mLock) {
                    this.mLock.wait();
                }
            } catch (InterruptedException e) {
                Log.m36w("PanoramaMode", "InterruptedException in waitLock");
            }
        }
    }

    private void showGuideString(int i) {
        int i2 = 0;
        switch (i) {
            case 0:
                i2 = R.string.panorama_guide_shutter;
                break;
            case 1:
                i2 = R.string.panorama_guide_choose_direction;
                break;
            case 2:
                i2 = R.string.panorama3d_guide_capture;
                break;
        }
        if (i2 != 0) {
            this.mICameraAppUi.showInfo(this.mActivity.getString(i2), 5000);
        }
    }
}
