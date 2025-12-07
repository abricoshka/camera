package com.mediatek.camera.mode.stereocamera;

import android.hardware.Camera;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import com.mediatek.camera.ICameraAddition;
import com.mediatek.camera.ICameraContext;
import com.mediatek.camera.ICameraMode;
import com.mediatek.camera.R;
import com.mediatek.camera.mode.PhotoMode;
import com.mediatek.camera.mode.stereocamera.StereoView;
import com.mediatek.camera.mode.stereocamera.settingrule.StereoFdRule;
import com.mediatek.camera.mode.stereocamera.settingrule.StereoPictureSizeRule;
import com.mediatek.camera.mode.stereocamera.settingrule.StereoPreviewRatioRule;
import com.mediatek.camera.mode.stereocamera.settingrule.StereoZsdRule;
import com.mediatek.camera.platform.ICameraAppUi;
import com.mediatek.camera.platform.ICameraDeviceManager;
import com.mediatek.camera.platform.ICameraView;
import com.mediatek.camera.platform.IFileSaver;
import com.mediatek.camera.platform.Parameters;
import com.mediatek.camera.setting.ParametersHelper;
import com.mediatek.camera.util.Log;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/* loaded from: classes.dex */
public class StereoCameraMode extends PhotoMode implements StereoView.Listener {

    /* renamed from: -com-mediatek-camera-ICameraMode$ActionTypeSwitchesValues, reason: not valid java name */
    private static final /* synthetic */ int[] f108commediatekcameraICameraMode$ActionTypeSwitchesValues = null;
    private Date mCaptureDate;
    private byte[] mClearImage;
    private int mCurrentNum;
    private byte[] mDepthMap;
    private IFileSaver.OnFileSavedListener mFileSaverListener;
    private SimpleDateFormat mFormat;
    private final Handler mHandler;
    private String mImageName;
    private boolean mIsDngCapture;
    private boolean mIsDualCameraReady;
    private boolean mIsStereoCapture;
    private final Camera.PictureCallback mJpegPictureCallback;
    private byte[] mJpsData;
    private long mLastDate;
    private byte[] mLdcData;
    private byte[] mMaskAndConfigData;
    private byte[] mOriginalJpegData;
    private final Camera.PictureCallback mRawPictureCallback;
    private long mRawPictureCallbackTime;
    private int mSameSecondCount;
    private final SaveHandler mSaveHandler;
    private final Camera.ShutterCallback mShutterCallback;
    private long mShutterCallbackTime;
    private final WarningCallback mStereoCameraWarningCallback;
    private StereoGestureListener mStereoGestureListener;
    private final StereoPhotoDataCallback mStereoPhotoDataCallback;
    private ICameraView mStereoView;
    private Thread mWaitSavingDoneThread;
    private byte[] mXmpJpegData;

    /* renamed from: -getcom-mediatek-camera-ICameraMode$ActionTypeSwitchesValues, reason: not valid java name */
    private static /* synthetic */ int[] m977getcommediatekcameraICameraMode$ActionTypeSwitchesValues() {
        if (f108commediatekcameraICameraMode$ActionTypeSwitchesValues != null) {
            return f108commediatekcameraICameraMode$ActionTypeSwitchesValues;
        }
        int[] iArr = new int[ICameraMode.ActionType.valuesCustom().length];
        try {
            iArr[ICameraMode.ActionType.ACTION_CANCEL_BUTTON_CLICK.ordinal()] = 6;
        } catch (NoSuchFieldError e) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_CAN_DO_AUTO_FOCUS.ordinal()] = 7;
        } catch (NoSuchFieldError e2) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_DISABLE_VIDEO_RECORD.ordinal()] = 8;
        } catch (NoSuchFieldError e3) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_FACE_DETECTED.ordinal()] = 9;
        } catch (NoSuchFieldError e4) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_NOTIFY_SURFCEVIEW_DESTROYED.ordinal()] = 10;
        } catch (NoSuchFieldError e5) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_NOTIFY_SURFCEVIEW_DISPLAY_IS_READY.ordinal()] = 11;
        } catch (NoSuchFieldError e6) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_OK_BUTTON_CLICK.ordinal()] = 12;
        } catch (NoSuchFieldError e7) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_BACK_KEY_PRESS.ordinal()] = 13;
        } catch (NoSuchFieldError e8) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_CAMERA_CLOSE.ordinal()] = 14;
        } catch (NoSuchFieldError e9) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_CAMERA_OPEN.ordinal()] = 1;
        } catch (NoSuchFieldError e10) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_CAMERA_PARAMETERS_READY.ordinal()] = 15;
        } catch (NoSuchFieldError e11) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_COMPENSATION_CHANGED.ordinal()] = 16;
        } catch (NoSuchFieldError e12) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_CONFIGURATION_CHANGED.ordinal()] = 2;
        } catch (NoSuchFieldError e13) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_FULL_SCREEN_CHANGED.ordinal()] = 17;
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
            iArr[ICameraMode.ActionType.ACTION_ON_SINGLE_TAP_UP.ordinal()] = 3;
        } catch (NoSuchFieldError e23) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_START_PREVIEW.ordinal()] = 4;
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
            iArr[ICameraMode.ActionType.ACTION_SHUTTER_BUTTON_LONG_PRESS.ordinal()] = 5;
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
        f108commediatekcameraICameraMode$ActionTypeSwitchesValues = iArr;
        return iArr;
    }

    public StereoCameraMode(ICameraContext iCameraContext) {
        super(iCameraContext);
        this.mIsDualCameraReady = true;
        this.mIsStereoCapture = true;
        this.mIsDngCapture = false;
        this.mStereoPhotoDataCallback = new StereoPhotoDataCallback(this, null);
        this.mStereoCameraWarningCallback = new WarningCallback(this, 0 == true ? 1 : 0);
        this.mCurrentNum = 0;
        this.mCaptureDate = new Date();
        this.mLastDate = 0L;
        this.mSameSecondCount = 0;
        this.mFileSaverListener = new IFileSaver.OnFileSavedListener() { // from class: com.mediatek.camera.mode.stereocamera.StereoCameraMode.1
            @Override // com.mediatek.camera.platform.IFileSaver.OnFileSavedListener
            public void onFileSaved(Uri uri) {
                Log.m31d("StereoCameraMode", "[onFileSaved]uri= " + uri);
            }
        };
        this.mJpegPictureCallback = new Camera.PictureCallback() { // from class: com.mediatek.camera.mode.stereocamera.StereoCameraMode.2
            @Override // android.hardware.Camera.PictureCallback
            public void onPictureTaken(byte[] bArr, Camera camera) {
                Log.m31d("StereoCameraMode", "[mJpegPictureCallback]");
                if (StereoCameraMode.this.mCameraClosed) {
                    Log.m36w("StereoCameraMode", "[mJpegPictureCallback] mCameraClosed:" + StereoCameraMode.this.mCameraClosed);
                    StereoCameraMode.this.mICameraAppUi.setSwipeEnabled(true);
                    StereoCameraMode.this.mICameraAppUi.restoreViewState();
                } else {
                    if (bArr == null) {
                        Log.m36w("StereoCameraMode", "[mJpegPictureCallback] jpegData is null");
                        StereoCameraMode.this.mICameraAppUi.setSwipeEnabled(true);
                        StereoCameraMode.this.mICameraAppUi.restoreViewState();
                        StereoCameraMode.this.restartPreview(false);
                        return;
                    }
                    StereoCameraMode.this.mOriginalJpegData = bArr;
                    StereoCameraMode.this.mIFocusManager.updateFocusUI();
                    if (!StereoCameraMode.this.mIsStereoCapture) {
                        StereoCameraMode.this.saveFile(StereoCameraMode.this.mOriginalJpegData, 0, null);
                    }
                    StereoCameraMode.this.notifyMergeData();
                    Log.m31d("StereoCameraMode", "[mJpegPictureCallback] end");
                }
            }
        };
        this.mShutterCallback = new Camera.ShutterCallback() { // from class: com.mediatek.camera.mode.stereocamera.StereoCameraMode.3
            @Override // android.hardware.Camera.ShutterCallback
            public void onShutter() {
                StereoCameraMode.this.mShutterCallbackTime = System.currentTimeMillis();
                Log.m31d("StereoCameraMode", "[mShutterCallback] mShutterLag = " + (StereoCameraMode.this.mShutterCallbackTime - StereoCameraMode.this.mCaptureStartTime) + "ms");
                Log.m31d("StereoCameraMode", "[mShutterCallback]");
            }
        };
        this.mRawPictureCallback = new Camera.PictureCallback() { // from class: com.mediatek.camera.mode.stereocamera.StereoCameraMode.4
            @Override // android.hardware.Camera.PictureCallback
            public void onPictureTaken(byte[] bArr, Camera camera) {
                StereoCameraMode.this.mRawPictureCallbackTime = System.currentTimeMillis();
                Log.m31d("StereoCameraMode", "mShutterToRawCallbackTime = " + (StereoCameraMode.this.mRawPictureCallbackTime - StereoCameraMode.this.mShutterCallbackTime) + "ms");
                if (bArr == null) {
                    Log.m31d("StereoCameraMode", "[mRawPictureCallback] data is null ");
                } else {
                    StereoCameraMode.this.mDngHelper.setRawdata(bArr);
                    StereoCameraMode.this.getDngImageAndSaved(null);
                }
            }
        };
        this.mStereoGestureListener = new StereoGestureListener() { // from class: com.mediatek.camera.mode.stereocamera.StereoCameraMode.5
            @Override // com.mediatek.camera.mode.stereocamera.StereoGestureListener, com.mediatek.camera.platform.ICameraAppUi.GestureListener
            public boolean onSingleTapUp(float f, float f2) {
                return false;
            }

            @Override // com.mediatek.camera.mode.stereocamera.StereoGestureListener, com.mediatek.camera.platform.ICameraAppUi.GestureListener
            public boolean onLongPress(float f, float f2) {
                return false;
            }
        };
        Log.m31d("StereoCameraMode", "[StereoCameraMode]constructor...");
        this.mHandler = new StereoPhotoHandler(this.mActivity.getMainLooper());
        this.mCameraCategory = new StereoPhotoCategory();
        this.mFormat = new SimpleDateFormat(this.mActivity.getString(R.string.image_file_name_format), Locale.ENGLISH);
        HandlerThread handlerThread = new HandlerThread("Stereo Save Handler Thread");
        handlerThread.start();
        this.mSaveHandler = new SaveHandler(handlerThread.getLooper());
        setRefocusSettingRules(iCameraContext);
        setModeState(ICameraMode.ModeState.STATE_CLOSED);
    }

    @Override // com.mediatek.camera.mode.PhotoMode, com.mediatek.camera.mode.CameraMode, com.mediatek.camera.ICameraMode
    public boolean open() {
        Log.m31d("StereoCameraMode", "[openMode] ...");
        super.open();
        this.mHandler.sendEmptyMessage(10003);
        this.mICameraAppUi.setGestureListener(this.mStereoGestureListener);
        return true;
    }

    @Override // com.mediatek.camera.mode.PhotoMode, com.mediatek.camera.mode.CameraMode, com.mediatek.camera.ICameraMode
    public boolean close() {
        WaitSavingDoneThread waitSavingDoneThread = null;
        Log.m31d("StereoCameraMode", "[closeMode]...");
        if (this.mICameraDevice != null && ParametersHelper.isVsDofSupported(this.mICameraDevice.getParameters())) {
            if (ICameraMode.CameraModeType.EXT_MODE_VIDEO_STEREO != this.mIModuleCtrl.getNextMode()) {
                this.mStereoView.reset();
            }
            uninitStereoView();
        }
        this.mICameraAppUi.setGestureListener(null);
        this.mWaitSavingDoneThread = new WaitSavingDoneThread(this, waitSavingDoneThread);
        this.mWaitSavingDoneThread.start();
        if (this.mSaveHandler != null) {
            this.mSaveHandler.getLooper().quit();
        }
        super.close();
        return true;
    }

    @Override // com.mediatek.camera.mode.PhotoMode, com.mediatek.camera.mode.CameraMode, com.mediatek.camera.ICameraMode
    public boolean execute(ICameraMode.ActionType actionType, Object... objArr) {
        if (actionType != ICameraMode.ActionType.ACTION_ORITATION_CHANGED) {
            Log.m31d("StereoCameraMode", "[execute]type = " + actionType);
        }
        switch (m977getcommediatekcameraICameraMode$ActionTypeSwitchesValues()[actionType.ordinal()]) {
            case 1:
                updateDevice();
                this.mCameraClosed = false;
                this.mICameraDevice.setStereoWarningCallback(this.mStereoCameraWarningCallback);
                if (ParametersHelper.isVsDofSupported(this.mICameraDevice.getParameters())) {
                    ParametersHelper.setVsDofMode(this.mICameraDevice.getParameters(), true);
                    ParametersHelper.setStereoCaptureMode(this.mICameraDevice.getParameters(), true);
                } else {
                    ParametersHelper.setStereoCaptureMode(this.mICameraDevice.getParameters(), true);
                    ParametersHelper.setVsDofMode(this.mICameraDevice.getParameters(), false);
                }
                ParametersHelper.setDenoiseMode(this.mICameraDevice.getParameters(), false);
                return true;
            case 2:
                if (this.mHandler != null) {
                    this.mHandler.sendEmptyMessage(10005);
                }
                return true;
            case 3:
                Parameters parameters = this.mICameraDevice.getParameters();
                if (parameters == null || (parameters != null && parameters.getMaxNumFocusAreas() > 0)) {
                    return super.execute(actionType, objArr);
                }
                if (!this.mCameraClosed && ICameraMode.ModeState.STATE_IDLE == getModeState()) {
                    this.mStereoView.update(0, (Integer) objArr[1], (Integer) objArr[2]);
                }
                return true;
            case 4:
                super.execute(actionType, objArr);
                this.mHandler.sendEmptyMessage(10003);
                return true;
            case 5:
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

    @Override // com.mediatek.camera.mode.PhotoMode
    protected Camera.PictureCallback getUncompressedImageCallback() {
        return null;
    }

    @Override // com.mediatek.camera.mode.PhotoMode, com.mediatek.camera.platform.IFocusManager.FocusListener
    public boolean capture() {
        Log.m31d("StereoCameraMode", "capture()");
        this.mCurrentNum = 0;
        this.mIsStereoCapture = this.mIsDualCameraReady;
        if ("on".equals(this.mISettingCtrl.getSettingValue("pref_dng_key"))) {
            this.mIFileSaver.setRawFlagEnabled(true);
            this.mIFileSaver.init(IFileSaver.FILE_TYPE.RAW, 0, null, -1);
            this.mIFileSaver.init(IFileSaver.FILE_TYPE.JPEG, 0, null, -1);
            this.mIsDngCapture = true;
        } else {
            this.mIFileSaver.setRawFlagEnabled(false);
            this.mIFileSaver.init(IFileSaver.FILE_TYPE.JPEG, 0, null, -1);
            this.mIsDngCapture = false;
        }
        this.mCaptureStartTime = System.currentTimeMillis();
        this.mCaptureDate.setTime(this.mCaptureStartTime);
        this.mImageName = createName();
        this.mICameraAppUi.showRemaining();
        this.mCameraCategory.takePicture();
        setModeState(ICameraMode.ModeState.STATE_CAPTURING);
        return true;
    }

    private class StereoPhotoHandler extends Handler {
        public StereoPhotoHandler(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            Log.m31d("StereoCameraMode", "[handleMessage]msg.what= " + message.what);
            switch (message.what) {
                case 10003:
                    if (StereoCameraMode.this.mICameraDevice != null && ParametersHelper.isVsDofSupported(StereoCameraMode.this.mICameraDevice.getParameters())) {
                        StereoCameraMode.this.initStereoView();
                        break;
                    }
                    break;
                case 10005:
                    StereoCameraMode.this.reInitStereoView();
                    break;
            }
        }
    }

    private class SaveHandler extends Handler {
        SaveHandler(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            Log.m31d("StereoCameraMode", "Save handleMessage msg.what = " + message.what + ", msg.obj = " + message.obj);
            switch (message.what) {
                case 10004:
                    StereoDataGroup stereoDataGroup = (StereoDataGroup) message.obj;
                    Log.m31d("StereoCameraMode", "notifyMergeData mXmpJpegData: " + StereoCameraMode.this.mXmpJpegData);
                    if (StereoCameraMode.this.mXmpJpegData != null) {
                        StereoCameraMode.this.saveFile(StereoCameraMode.this.mXmpJpegData, 1, stereoDataGroup.getPictureName());
                        break;
                    }
                    break;
            }
        }
    }

    private class WaitSavingDoneThread extends Thread {
        /* synthetic */ WaitSavingDoneThread(StereoCameraMode stereoCameraMode, WaitSavingDoneThread waitSavingDoneThread) {
            this();
        }

        private WaitSavingDoneThread() {
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            Log.m31d("StereoCameraMode", "[WaitSavingDoneThread]wait");
            StereoCameraMode.this.mIFileSaver.waitDone();
            Log.m31d("StereoCameraMode", "[WaitSavingDoneThread]waitDone!");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void saveFile(byte[] bArr, int i, String str) {
        Log.m34i("StereoCameraMode", "[saveFile]...");
        this.mIFileSaver.savePhotoFile(bArr, str, this.mCaptureStartTime, this.mIModuleCtrl.getLocation(), i, this.mFileSaverListener);
    }

    private void setRefocusSettingRules(ICameraContext iCameraContext) {
        Log.m31d("StereoCameraMode", "[setRefocusSettingRules]...");
        StereoPreviewRatioRule stereoPreviewRatioRule = new StereoPreviewRatioRule(iCameraContext, 0);
        stereoPreviewRatioRule.addLimitation("on", null, null);
        this.mISettingCtrl.addRule("refocus_key", "pref_camera_picturesize_ratio_key", stereoPreviewRatioRule);
        StereoPictureSizeRule stereoPictureSizeRule = new StereoPictureSizeRule(iCameraContext, 0);
        stereoPictureSizeRule.addLimitation("on", null, null);
        this.mISettingCtrl.addRule("refocus_key", "pref_camera_picturesize_key", stereoPictureSizeRule);
        StereoZsdRule stereoZsdRule = new StereoZsdRule(iCameraContext, 1);
        stereoZsdRule.addLimitation("on", null, null);
        this.mISettingCtrl.addRule("refocus_key", "pref_camera_zsd_key", stereoZsdRule);
        StereoFdRule stereoFdRule = new StereoFdRule(iCameraContext, 1);
        stereoFdRule.addLimitation("on", null, null);
        this.mISettingCtrl.addRule("refocus_key", "pref_face_detect_key", stereoFdRule);
    }

    private class StereoPhotoDataCallback implements ICameraDeviceManager.ICameraDevice.StereoDataCallback {
        /* synthetic */ StereoPhotoDataCallback(StereoCameraMode stereoCameraMode, StereoPhotoDataCallback stereoPhotoDataCallback) {
            this();
        }

        private StereoPhotoDataCallback() {
        }

        @Override // com.mediatek.camera.platform.ICameraDeviceManager.ICameraDevice.StereoDataCallback
        public void onJpsCapture(byte[] bArr) {
            if (bArr == null) {
                Log.m36w("StereoCameraMode", "JPS data is null");
                return;
            }
            Log.m31d("StereoCameraMode", "onJpsCapture jpsData:" + bArr.length);
            StereoCameraMode.this.mJpsData = bArr;
            StereoCameraMode.this.notifyMergeData();
        }

        @Override // com.mediatek.camera.platform.ICameraDeviceManager.ICameraDevice.StereoDataCallback
        public void onMaskCapture(byte[] bArr) {
            if (bArr == null) {
                Log.m36w("StereoCameraMode", "Mask data is null");
                return;
            }
            Log.m31d("StereoCameraMode", "onMaskCapture maskData:" + bArr.length);
            StereoCameraMode.this.mMaskAndConfigData = bArr;
            StereoCameraMode.this.notifyMergeData();
        }

        @Override // com.mediatek.camera.platform.ICameraDeviceManager.ICameraDevice.StereoDataCallback
        public void onDepthMapCapture(byte[] bArr) {
            if (bArr == null) {
                Log.m36w("StereoCameraMode", "depth data is null");
                return;
            }
            Log.m31d("StereoCameraMode", "onDepthMapCapture depthData:" + bArr.length);
            StereoCameraMode.this.mDepthMap = bArr;
            StereoCameraMode.this.notifyMergeData();
        }

        @Override // com.mediatek.camera.platform.ICameraDeviceManager.ICameraDevice.StereoDataCallback
        public void onClearImageCapture(byte[] bArr) {
            if (bArr == null) {
                Log.m36w("StereoCameraMode", " clearImage data is null");
                return;
            }
            Log.m31d("StereoCameraMode", "onClearImageCapture clearImageData:" + bArr.length);
            StereoCameraMode.this.mClearImage = bArr;
            StereoCameraMode.this.notifyMergeData();
        }

        @Override // com.mediatek.camera.platform.ICameraDeviceManager.ICameraDevice.StereoDataCallback
        public void onLdcCapture(byte[] bArr) {
            if (bArr == null) {
                Log.m36w("StereoCameraMode", " ldc data is null");
                return;
            }
            Log.m31d("StereoCameraMode", "onLdcCapture ldcData:" + bArr.length);
            StereoCameraMode.this.mLdcData = bArr;
            StereoCameraMode.this.notifyMergeData();
        }

        @Override // com.mediatek.camera.platform.ICameraDeviceManager.ICameraDevice.StereoDataCallback
        public void onN3dCapture(byte[] bArr) {
        }

        @Override // com.mediatek.camera.platform.ICameraDeviceManager.ICameraDevice.StereoDataCallback
        public void onDepthWrapperCapture(byte[] bArr) {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyMergeData() {
        Log.m31d("StereoCameraMode", "notifyMergeData mCurrentNum = " + this.mCurrentNum);
        this.mCurrentNum++;
        if (ParametersHelper.isVsDofSupported(this.mICameraDevice.getParameters())) {
            if (this.mCurrentNum == 6) {
                Log.m31d("StereoCameraMode", "notifyMergeData Vs Dof");
                restartPreview(true);
                if (this.mIsStereoCapture) {
                    this.mSaveHandler.obtainMessage(10004, new StereoDataGroup(generateName("_STEREO"), this.mOriginalJpegData, this.mJpsData, this.mMaskAndConfigData, this.mDepthMap, this.mClearImage, this.mLdcData)).sendToTarget();
                }
                this.mCurrentNum = 0;
                return;
            }
            return;
        }
        if (this.mCurrentNum == 3) {
            Log.m31d("StereoCameraMode", "notifyMergeData refocus");
            restartPreview(true);
            if (this.mIsStereoCapture) {
                String strGenerateName = generateName("_STEREO");
                if (this.mXmpJpegData != null) {
                    saveFile(this.mXmpJpegData, 1, strGenerateName);
                }
            }
            this.mCurrentNum = 0;
        }
    }

    private class WarningCallback implements ICameraDeviceManager.ICameraDevice.StereoWarningCallback {
        /* synthetic */ WarningCallback(StereoCameraMode stereoCameraMode, WarningCallback warningCallback) {
            this();
        }

        private WarningCallback() {
        }

        @Override // com.mediatek.camera.platform.ICameraDeviceManager.ICameraDevice.StereoWarningCallback
        public void onWarning(int i) {
            Log.m31d("StereoCameraMode", "onWarning type = " + i);
            switch (i) {
                case 0:
                    StereoCameraMode.this.mICameraAppUi.showToast(R.string.dual_camera_lens_covered_toast);
                    StereoCameraMode.this.mIsDualCameraReady = false;
                    break;
                case 1:
                    StereoCameraMode.this.mICameraAppUi.showToast(R.string.dual_camera_lowlight_toast);
                    StereoCameraMode.this.mIsDualCameraReady = false;
                    break;
                case 2:
                    StereoCameraMode.this.mICameraAppUi.showToast(R.string.dual_camera_too_close_toast);
                    StereoCameraMode.this.mIsDualCameraReady = false;
                    break;
                case 3:
                    StereoCameraMode.this.mIsDualCameraReady = true;
                    break;
                default:
                    Log.m36w("StereoCameraMode", "Warning message don't need to show");
                    break;
            }
        }
    }

    private String generateName(String str) {
        String str2 = null;
        if (str == "_RAW") {
            if (this.mICameraDevice != null && ParametersHelper.isVsDofSupported(this.mICameraDevice.getParameters()) && this.mIsStereoCapture) {
                str2 = this.mImageName + "_STEREO_RAW.dng";
            }
        } else if (this.mICameraDevice != null && ParametersHelper.isVsDofSupported(this.mICameraDevice.getParameters())) {
            if (this.mIsDngCapture && this.mIsStereoCapture) {
                str2 = this.mImageName + "_STEREO_RAW.jpg";
            } else if (!this.mIsDngCapture && this.mIsStereoCapture) {
                str2 = this.mImageName + "_STEREO.jpg";
            }
        }
        Log.m31d("StereoCameraMode", "generateName type = " + str + ", name = " + str2);
        return str2;
    }

    class StereoPhotoCategory extends PhotoMode.CameraCategory {
        public StereoPhotoCategory() {
            super();
        }

        @Override // com.mediatek.camera.mode.PhotoMode.CameraCategory
        public void takePicture() {
            StereoCameraMode.this.mAdditionManager.execute(ICameraAddition.AdditionActionType.ACTION_TAKEN_PICTURE, new Object[0]);
            StereoCameraMode.this.mICameraDevice.setStereoDataCallback(StereoCameraMode.this.mStereoPhotoDataCallback);
            StereoCameraMode.this.mICameraDevice.getParameters().setRefocusJpsFileName("refocus");
            StereoCameraMode.this.mICameraDevice.takePicture(StereoCameraMode.this.mShutterCallback, null, null, StereoCameraMode.this.mJpegPictureCallback);
            StereoCameraMode.this.mICameraAppUi.setViewState(ICameraAppUi.ViewState.VIEW_STATE_CAPTURE);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void initStereoView() {
        if (this.mStereoView == null) {
            this.mStereoView = this.mICameraAppUi.getCameraView(ICameraAppUi.SpecViewType.MODE_STEREO);
            this.mStereoView.init(this.mActivity, this.mICameraAppUi, this.mIModuleCtrl);
            this.mStereoView.setListener(this);
            this.mStereoView.show();
            return;
        }
        this.mStereoView.refresh();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void reInitStereoView() {
        if (this.mStereoView != null) {
            this.mStereoView.uninit();
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
        Log.m31d("StereoCameraMode", "[setVsDofLevelParameter] level = " + str);
        this.mICameraDevice.setParameter("stereo-dof-level", str);
        this.mICameraDevice.applyParameters();
    }

    private void setTouchPositionParameter(String str) {
        Log.m34i("StereoCameraMode", "[setTouchPositionParameter] value = " + str);
        this.mICameraDevice.setParameter("stereo-touch-position", str);
        this.mICameraDevice.applyParameters();
    }

    private String createName() {
        String str = this.mFormat.format(this.mCaptureDate);
        long j = this.mCaptureStartTime;
        if (j / 1000 == this.mLastDate / 1000) {
            this.mSameSecondCount++;
            str = str + "_" + this.mSameSecondCount;
        } else {
            this.mLastDate = j;
            this.mSameSecondCount = 0;
        }
        Log.m31d("StereoCameraMode", "[createName] result = " + str);
        return str;
    }
}
