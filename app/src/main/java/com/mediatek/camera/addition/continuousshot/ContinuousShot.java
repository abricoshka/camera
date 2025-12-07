package com.mediatek.camera.addition.continuousshot;

import android.hardware.Camera;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import com.mediatek.camera.ICameraAddition;
import com.mediatek.camera.ICameraContext;
import com.mediatek.camera.ICameraMode;
import com.mediatek.camera.R;
import com.mediatek.camera.addition.CameraAddition;
import com.mediatek.camera.addition.continuousshot.MemoryManager;
import com.mediatek.camera.platform.ICameraAppUi;
import com.mediatek.camera.platform.ICameraDeviceManager;
import com.mediatek.camera.platform.ICameraView;
import com.mediatek.camera.platform.Parameters;
import com.mediatek.camera.util.CaptureSound;
import com.mediatek.camera.util.Log;
import dalvik.system.VMRuntime;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

/* loaded from: classes.dex */
public class ContinuousShot extends CameraAddition implements ICameraDeviceManager.ICameraDevice.ContinuousShotListener {

    /* renamed from: -com-mediatek-camera-ICameraAddition$AdditionActionTypeSwitchesValues */
    private static final /* synthetic */ int[] f67x29f76b14 = null;

    /* renamed from: -com-mediatek-camera-ICameraMode$ActionTypeSwitchesValues, reason: not valid java name */
    private static final /* synthetic */ int[] f98commediatekcameraICameraMode$ActionTypeSwitchesValues = null;

    /* renamed from: -com-mediatek-camera-addition-continuousshot-ContinuousShot$StateSwitchesValues */
    private static final /* synthetic */ int[] f68x854fa6d7 = null;
    private CaptureSound mCaptureSound;
    private int mCurrentShotsNum;
    private String[] mFeatureKey;
    private int[] mFeatureResId;
    private SimpleDateFormat mFormat;
    private Handler mHandler;
    private ICameraView mICameraView;
    private boolean mIsClearMemoryLimit;
    private boolean mIsDngOpenedBeforeCs;
    private boolean mIsSupportIndicator;
    private final Camera.PictureCallback mJpegPictureCallback;
    private ICameraAddition.Listener mListener;
    private long mLowStorageThreshold;
    private int mMaxCaptureNum;
    private MemoryManager mMemoryManager;
    private Date mPictureTakenDate;
    private final Camera.ShutterCallback mShutterCallback;
    private State mState;
    private Thread mWaitSavingDoneThread;

    /* renamed from: -getcom-mediatek-camera-ICameraAddition$AdditionActionTypeSwitchesValues */
    private static /* synthetic */ int[] m19xd5d340f0() {
        if (f67x29f76b14 != null) {
            return f67x29f76b14;
        }
        int[] iArr = new int[ICameraAddition.AdditionActionType.valuesCustom().length];
        try {
            iArr[ICameraAddition.AdditionActionType.ACTION_EFFECT_CLICK.ordinal()] = 14;
        } catch (NoSuchFieldError e) {
        }
        try {
            iArr[ICameraAddition.AdditionActionType.ACTION_ON_START_PREVIEW.ordinal()] = 15;
        } catch (NoSuchFieldError e2) {
        }
        try {
            iArr[ICameraAddition.AdditionActionType.ACTION_ON_STOP_PREVIEW.ordinal()] = 1;
        } catch (NoSuchFieldError e3) {
        }
        try {
            iArr[ICameraAddition.AdditionActionType.ACTION_ON_SWITCH_PIP.ordinal()] = 16;
        } catch (NoSuchFieldError e4) {
        }
        try {
            iArr[ICameraAddition.AdditionActionType.ACTION_TAKEN_PICTURE.ordinal()] = 2;
        } catch (NoSuchFieldError e5) {
        }
        f67x29f76b14 = iArr;
        return iArr;
    }

    /* renamed from: -getcom-mediatek-camera-ICameraMode$ActionTypeSwitchesValues, reason: not valid java name */
    private static /* synthetic */ int[] m599getcommediatekcameraICameraMode$ActionTypeSwitchesValues() {
        if (f98commediatekcameraICameraMode$ActionTypeSwitchesValues != null) {
            return f98commediatekcameraICameraMode$ActionTypeSwitchesValues;
        }
        int[] iArr = new int[ICameraMode.ActionType.valuesCustom().length];
        try {
            iArr[ICameraMode.ActionType.ACTION_CANCEL_BUTTON_CLICK.ordinal()] = 14;
        } catch (NoSuchFieldError e) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_CAN_DO_AUTO_FOCUS.ordinal()] = 15;
        } catch (NoSuchFieldError e2) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_DISABLE_VIDEO_RECORD.ordinal()] = 16;
        } catch (NoSuchFieldError e3) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_FACE_DETECTED.ordinal()] = 17;
        } catch (NoSuchFieldError e4) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_NOTIFY_SURFCEVIEW_DESTROYED.ordinal()] = 18;
        } catch (NoSuchFieldError e5) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_NOTIFY_SURFCEVIEW_DISPLAY_IS_READY.ordinal()] = 19;
        } catch (NoSuchFieldError e6) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_OK_BUTTON_CLICK.ordinal()] = 20;
        } catch (NoSuchFieldError e7) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_BACK_KEY_PRESS.ordinal()] = 1;
        } catch (NoSuchFieldError e8) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_CAMERA_CLOSE.ordinal()] = 21;
        } catch (NoSuchFieldError e9) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_CAMERA_OPEN.ordinal()] = 22;
        } catch (NoSuchFieldError e10) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_CAMERA_PARAMETERS_READY.ordinal()] = 2;
        } catch (NoSuchFieldError e11) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_COMPENSATION_CHANGED.ordinal()] = 3;
        } catch (NoSuchFieldError e12) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_CONFIGURATION_CHANGED.ordinal()] = 23;
        } catch (NoSuchFieldError e13) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_FULL_SCREEN_CHANGED.ordinal()] = 24;
        } catch (NoSuchFieldError e14) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_KEY_EVENT_PRESS.ordinal()] = 25;
        } catch (NoSuchFieldError e15) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_LONG_PRESS.ordinal()] = 26;
        } catch (NoSuchFieldError e16) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_MEDIA_EJECT.ordinal()] = 27;
        } catch (NoSuchFieldError e17) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_PREVIEW_BUFFER_SIZE_CHANGED.ordinal()] = 28;
        } catch (NoSuchFieldError e18) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_PREVIEW_DISPLAY_SIZE_CHANGED.ordinal()] = 29;
        } catch (NoSuchFieldError e19) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_RESTORE_SETTINGS.ordinal()] = 30;
        } catch (NoSuchFieldError e20) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_SELFTIMER_STATE.ordinal()] = 31;
        } catch (NoSuchFieldError e21) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_SETTING_BUTTON_CLICK.ordinal()] = 32;
        } catch (NoSuchFieldError e22) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_SINGLE_TAP_UP.ordinal()] = 33;
        } catch (NoSuchFieldError e23) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_START_PREVIEW.ordinal()] = 34;
        } catch (NoSuchFieldError e24) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_STOP_PREVIEW.ordinal()] = 35;
        } catch (NoSuchFieldError e25) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_SURFACE_TEXTURE_READY.ordinal()] = 36;
        } catch (NoSuchFieldError e26) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_USER_INTERACTION.ordinal()] = 37;
        } catch (NoSuchFieldError e27) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ORITATION_CHANGED.ordinal()] = 38;
        } catch (NoSuchFieldError e28) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_PHOTO_SHUTTER_BUTTON_CLICK.ordinal()] = 39;
        } catch (NoSuchFieldError e29) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_PREVIEW_VISIBLE_CHANGED.ordinal()] = 40;
        } catch (NoSuchFieldError e30) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_SET_DISPLAYROTATION.ordinal()] = 41;
        } catch (NoSuchFieldError e31) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_SHUTTER_BUTTON_FOCUS.ordinal()] = 4;
        } catch (NoSuchFieldError e32) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_SHUTTER_BUTTON_LONG_PRESS.ordinal()] = 5;
        } catch (NoSuchFieldError e33) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_SWITCH_DEVICE.ordinal()] = 42;
        } catch (NoSuchFieldError e34) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_VIDEO_SHUTTER_BUTTON_CLICK.ordinal()] = 43;
        } catch (NoSuchFieldError e35) {
        }
        f98commediatekcameraICameraMode$ActionTypeSwitchesValues = iArr;
        return iArr;
    }

    /* renamed from: -getcom-mediatek-camera-addition-continuousshot-ContinuousShot$StateSwitchesValues */
    private static /* synthetic */ int[] m20x98aed9b3() {
        if (f68x854fa6d7 != null) {
            return f68x854fa6d7;
        }
        int[] iArr = new int[State.valuesCustom().length];
        try {
            iArr[State.STATE_CAPTURE_STARTED.ordinal()] = 1;
        } catch (NoSuchFieldError e) {
        }
        try {
            iArr[State.STATE_CAPTURING.ordinal()] = 2;
        } catch (NoSuchFieldError e2) {
        }
        try {
            iArr[State.STATE_INIT.ordinal()] = 3;
        } catch (NoSuchFieldError e3) {
        }
        try {
            iArr[State.STATE_OPENED.ordinal()] = 4;
        } catch (NoSuchFieldError e4) {
        }
        try {
            iArr[State.STATE_SAVING.ordinal()] = 5;
        } catch (NoSuchFieldError e5) {
        }
        try {
            iArr[State.STATE_STOPPED.ordinal()] = 6;
        } catch (NoSuchFieldError e6) {
        }
        f68x854fa6d7 = iArr;
        return iArr;
    }

    private enum State {
        STATE_INIT,
        STATE_OPENED,
        STATE_CAPTURE_STARTED,
        STATE_CAPTURING,
        STATE_STOPPED,
        STATE_SAVING;

        /* renamed from: values, reason: to resolve conflict with enum method */
        public static State[] valuesCustom() {
            return values();
        }
    }

    public ContinuousShot(ICameraContext iCameraContext) {
        super(iCameraContext);
        this.mFeatureKey = new String[]{null, "pref_hdr_key", "pref_asd_key"};
        this.mFeatureResId = new int[]{R.string.normal_camera_continuous_not_supported, R.string.pref_camera_hdr_title, R.string.pref_camera_capturemode_entry_asd};
        this.mLowStorageThreshold = 50000000L;
        this.mPictureTakenDate = new Date();
        this.mState = State.STATE_INIT;
        this.mIsDngOpenedBeforeCs = false;
        this.mShutterCallback = new Camera.ShutterCallback() { // from class: com.mediatek.camera.addition.continuousshot.ContinuousShot.1
            @Override // android.hardware.Camera.ShutterCallback
            public void onShutter() {
                Log.m31d("ContinuousShot", "[onShutter]mState = " + ContinuousShot.this.getCurrentState());
                if (ContinuousShot.this.getCurrentState() == State.STATE_CAPTURING) {
                    ContinuousShot.this.mMemoryManager.start();
                    ContinuousShot.this.mCaptureSound.play();
                    ContinuousShot.this.mPictureTakenDate.setTime(System.currentTimeMillis());
                }
            }
        };
        this.mJpegPictureCallback = new Camera.PictureCallback() { // from class: com.mediatek.camera.addition.continuousshot.ContinuousShot.2
            @Override // android.hardware.Camera.PictureCallback
            public void onPictureTaken(byte[] bArr, Camera camera) {
                if (ContinuousShot.this.getCurrentState() != State.STATE_CAPTURING) {
                    Log.m36w("ContinuousShot", "[onPictureTaken]Continuous Shot haven't start or have stopped!");
                    return;
                }
                if (bArr == null) {
                    Log.m36w("ContinuousShot", "[onPictureTaken]Data is null!");
                    ContinuousShot.this.stopContinuousShot(false);
                    return;
                }
                if (!ContinuousShot.this.mIFileSaver.isEnoughSpace()) {
                    Log.m36w("ContinuousShot", "[onPictureTaken]Don't have enough storage!");
                    ContinuousShot.this.stopContinuousShot(false);
                    return;
                }
                String strCreateFileName = ContinuousShot.this.createFileName(ContinuousShot.this.mCurrentShotsNum++);
                ContinuousShot.this.showSpeedIndicator();
                ContinuousShot.this.mIFileSaver.savePhotoFile(bArr, strCreateFileName, System.currentTimeMillis(), ContinuousShot.this.mIModuleCtrl.getLocation(), 0, null);
                if (ContinuousShot.this.mCurrentShotsNum == ContinuousShot.this.mMaxCaptureNum) {
                    ContinuousShot.this.stopContinuousShot(false);
                }
                MemoryManager.MemoryAction memoryAction = ContinuousShot.this.mMemoryManager.getMemoryAction(bArr.length, ContinuousShot.this.mIFileSaver.getWaitingDataSize());
                if (memoryAction == MemoryManager.MemoryAction.STOP) {
                    ContinuousShot.this.stopContinuousShot(false);
                } else if (memoryAction == MemoryManager.MemoryAction.ADJSUT_SPEED) {
                    ContinuousShot.this.mICameraDevice.setContinuousShotSpeed(ContinuousShot.this.mMemoryManager.getSuitableContinuousShotSpeed());
                }
                Log.m34i("ContinuousShot", "[onPictureTaken]mCurrentShotsNum = " + ContinuousShot.this.mCurrentShotsNum);
            }
        };
        this.mMemoryManager = new MemoryManager(this.mActivity);
        this.mHandler = new MainHandler(this.mActivity.getMainLooper());
        this.mCaptureSound = new CaptureSound(this.mActivity);
        this.mFormat = new SimpleDateFormat(this.mActivity.getString(R.string.image_file_name_format), Locale.ENGLISH);
        this.mICameraView = this.mICameraAppUi.getCameraView(ICameraAppUi.SpecViewType.ADDITION_CONTINUE_SHOT);
        this.mICameraView.onOrientationChanged(this.mIModuleCtrl.getOrientationCompensation());
        if (this.mIFeatureConfig.isMtkFatOnNandSupport() || this.mIFeatureConfig.isGmoRomOptSupport()) {
            this.mLowStorageThreshold = 10000000L;
            Log.m31d("ContinuousShot", "[ContinuousShot]LOW_STORAGE_THRESHOLD= 10000000");
        }
    }

    @Override // com.mediatek.camera.addition.CameraAddition, com.mediatek.camera.ICameraAddition
    public void setListener(ICameraAddition.Listener listener) {
        this.mListener = listener;
    }

    @Override // com.mediatek.camera.ICameraAddition
    public void open() {
        State currentState = getCurrentState();
        Log.m31d("ContinuousShot", "[open]state = " + currentState);
        if (currentState == State.STATE_OPENED) {
            return;
        }
        this.mCaptureSound.load();
        this.mIsSupportIndicator = "true".equals(this.mICameraDevice.getParameter("cshot-indicator"));
        setState(State.STATE_OPENED);
        Log.m31d("ContinuousShot", "[open]Indicator support is " + this.mIsSupportIndicator);
    }

    @Override // com.mediatek.camera.addition.CameraAddition, com.mediatek.camera.ICameraAddition
    public boolean isOpen() {
        boolean z = false;
        if (State.STATE_INIT != this.mState) {
            z = true;
        }
        Log.m31d("ContinuousShot", "[isOpen] isOpen:" + z);
        return z;
    }

    @Override // com.mediatek.camera.ICameraAddition
    public void close() {
        State currentState = getCurrentState();
        Log.m31d("ContinuousShot", "[close]state = " + currentState);
        if (currentState == State.STATE_INIT) {
            return;
        }
        if (currentState == State.STATE_CAPTURING) {
            stopContinuousShot(true);
            onConinuousShotDone(this.mCurrentShotsNum);
        } else if (currentState == State.STATE_STOPPED) {
            onConinuousShotDone(this.mCurrentShotsNum);
        }
        this.mCaptureSound.release();
        this.mHandler.removeMessages(1002);
        setState(State.STATE_INIT);
    }

    @Override // com.mediatek.camera.ICameraAddition
    public boolean isSupport() {
        boolean z = false;
        updateCameraDevice();
        if (this.mICameraDevice == null) {
            return false;
        }
        List<String> supportedCaptureMode = this.mICameraDevice.getParameters().getSupportedCaptureMode();
        if (supportedCaptureMode != null && supportedCaptureMode.indexOf("continuousshot") >= 0) {
            z = true;
        }
        Log.m34i("ContinuousShot", "[isSupport]isSupport = " + z);
        return z;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Removed duplicated region for block: B:4:0x0029 A[RETURN] */
    @Override // com.mediatek.camera.addition.CameraAddition, com.mediatek.camera.ICameraAddition
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public boolean execute(com.mediatek.camera.ICameraMode.ActionType r7, java.lang.Object... r8) {
        /*
            r6 = this;
            r5 = 1
            r4 = 0
            java.lang.String r0 = "ContinuousShot"
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "[execute] ActionType = "
            java.lang.StringBuilder r1 = r1.append(r2)
            java.lang.StringBuilder r1 = r1.append(r7)
            java.lang.String r1 = r1.toString()
            com.mediatek.camera.util.Log.m31d(r0, r1)
            int[] r0 = m599getcommediatekcameraICameraMode$ActionTypeSwitchesValues()
            int r1 = r7.ordinal()
            r0 = r0[r1]
            switch(r0) {
                case 1: goto L97;
                case 2: goto Lc3;
                case 3: goto L69;
                case 4: goto L34;
                case 5: goto L2a;
                default: goto L29;
            }
        L29:
            return r4
        L2a:
            boolean r0 = r6.canShot()
            if (r0 == 0) goto L33
            r6.startContinuousShot()
        L33:
            return r5
        L34:
            int r0 = r8.length
            if (r0 == r5) goto L41
            java.lang.String r0 = "ContinuousShot"
            java.lang.String r1 = "[execute]Shutter button focus parameter error!"
            com.mediatek.camera.util.Log.m32e(r0, r1)
            return r4
        L41:
            r0 = r8[r4]
            java.lang.Boolean r0 = (java.lang.Boolean) r0
            boolean r0 = r0.booleanValue()
            java.lang.String r1 = "ContinuousShot"
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "[execute]press is "
            java.lang.StringBuilder r2 = r2.append(r3)
            java.lang.StringBuilder r2 = r2.append(r0)
            java.lang.String r2 = r2.toString()
            com.mediatek.camera.util.Log.m31d(r1, r2)
            if (r0 != 0) goto L68
            r6.stopContinuousShot(r4)
        L68:
            return r5
        L69:
            java.lang.String r0 = "ContinuousShot"
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "[execute]onOrientation = "
            java.lang.StringBuilder r1 = r1.append(r2)
            r2 = r8[r4]
            java.lang.StringBuilder r1 = r1.append(r2)
            java.lang.String r1 = r1.toString()
            com.mediatek.camera.util.Log.m31d(r0, r1)
            boolean r0 = r6.mIsSupportIndicator
            if (r0 == 0) goto L29
            com.mediatek.camera.platform.ICameraView r1 = r6.mICameraView
            r0 = r8[r4]
            java.lang.Integer r0 = (java.lang.Integer) r0
            int r0 = r0.intValue()
            r1.onOrientationChanged(r0)
            goto L29
        L97:
            com.mediatek.camera.addition.continuousshot.ContinuousShot$State r0 = r6.getCurrentState()
            java.lang.String r1 = "ContinuousShot"
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "[execute]state = "
            java.lang.StringBuilder r2 = r2.append(r3)
            java.lang.StringBuilder r2 = r2.append(r0)
            java.lang.String r2 = r2.toString()
            com.mediatek.camera.util.Log.m31d(r1, r2)
            com.mediatek.camera.addition.continuousshot.ContinuousShot$State r1 = com.mediatek.camera.addition.continuousshot.ContinuousShot.State.STATE_CAPTURING
            if (r0 == r1) goto Lbd
            com.mediatek.camera.addition.continuousshot.ContinuousShot$State r1 = com.mediatek.camera.addition.continuousshot.ContinuousShot.State.STATE_STOPPED
            if (r0 != r1) goto Lbe
        Lbd:
            return r5
        Lbe:
            com.mediatek.camera.addition.continuousshot.ContinuousShot$State r1 = com.mediatek.camera.addition.continuousshot.ContinuousShot.State.STATE_SAVING
            if (r0 != r1) goto L29
            goto Lbd
        Lc3:
            r6.updateParameters()
            r6.updateFocusManager()
            com.mediatek.camera.platform.ICameraView r0 = r6.mICameraView
            com.mediatek.camera.platform.IModuleCtrl r1 = r6.mIModuleCtrl
            int r1 = r1.getOrientationCompensation()
            r0.onOrientationChanged(r1)
            goto L29
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mediatek.camera.addition.continuousshot.ContinuousShot.execute(com.mediatek.camera.ICameraMode$ActionType, java.lang.Object[]):boolean");
    }

    @Override // com.mediatek.camera.addition.CameraAddition, com.mediatek.camera.ICameraAddition
    public boolean execute(ICameraAddition.AdditionActionType additionActionType, Object... objArr) {
        Log.m31d("ContinuousShot", "[execute] AdditionActionType = " + additionActionType);
        switch (m19xd5d340f0()[additionActionType.ordinal()]) {
            case 1:
                stopContinuousShot(true);
                return true;
            case 2:
                return takePicture();
            default:
                return false;
        }
    }

    @Override // com.mediatek.camera.platform.ICameraDeviceManager.ICameraDevice.ContinuousShotListener
    public void onConinuousShotDone(int i) {
        WaitSavingDoneThread waitSavingDoneThread = null;
        Log.m31d("ContinuousShot", "[onContinuousShotCallback]Capture number = " + i);
        showSavingProcess(true);
        if (this.mIsSupportIndicator) {
            this.mICameraView.hide();
        }
        this.mICameraDevice.setContinuousShotCallback(null);
        setState(State.STATE_SAVING);
        this.mWaitSavingDoneThread = new WaitSavingDoneThread(this, waitSavingDoneThread);
        this.mWaitSavingDoneThread.start();
    }

    private void updateParameters() {
        this.mICameraDevice = this.mICameraDeviceManager.getCameraDevice(this.mICameraDeviceManager.getCurrentCameraId());
    }

    private void startContinuousShot() {
        Log.m31d("ContinuousShot", "[startContinuousShot]State = " + getCurrentState());
        if (getCurrentState() != State.STATE_OPENED) {
            return;
        }
        if (!this.mIFileSaver.isEnoughSpace()) {
            Log.m36w("ContinuousShot", "[startContinuousShot]Don't have enough storage!");
            return;
        }
        if (this.mICameraContext.getFeatureConfig().isLowRamOptSupport()) {
            this.mMaxCaptureNum = 20;
        } else {
            this.mMaxCaptureNum = Integer.valueOf(this.mISettingCtrl.getSettingValue("pref_camera_shot_number")).intValue();
        }
        if (isDngOpened()) {
            this.mIsDngOpenedBeforeCs = true;
            closeDng();
        }
        this.mCurrentShotsNum = 0;
        clearMemoryLimit();
        this.mMemoryManager.init(getLeftStorage());
        this.mIModuleCtrl.disableOrientationListener();
        this.mICameraAppUi.setThumbnailRefreshInterval(500);
        this.mICameraDevice.getParameters().setBurstShotNum(this.mMaxCaptureNum);
        this.mICameraDevice.getParameters().setCaptureMode("continuousshot");
        if ("0321".equals(this.mICameraContext.getFeatureConfig().whichDeanliChip()) && "on".equals(this.mISettingCtrl.getSettingValue("pref_camera_zsd_key")) && getSupportedValues(this.mICameraDevice.getParameters(), "3dnr-mode-values").indexOf("off") >= 0) {
            this.mICameraDevice.getParameters().set("3dnr-mode", "off");
        }
        this.mICameraDevice.applyParameters();
        this.mICameraDevice.setContinuousShotCallback(this);
        setState(State.STATE_CAPTURE_STARTED);
        this.mIFocusManager.clearFocusAndFaceUi();
        this.mIFocusManager.focusAndCapture();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void stopContinuousShot(boolean z) {
        State currentState = getCurrentState();
        Log.m31d("ContinuousShot", "[stopContinuousShot]state = " + currentState);
        if (currentState != State.STATE_CAPTURING && currentState != State.STATE_CAPTURE_STARTED) {
            return;
        }
        if (!z) {
            this.mICameraDevice.cancelContinuousShot();
        }
        this.mICameraDevice.getParameters().setCaptureMode("normal");
        if ("0321".equals(this.mICameraContext.getFeatureConfig().whichDeanliChip()) && "on".equals(this.mISettingCtrl.getSettingValue("pref_camera_zsd_key")) && getSupportedValues(this.mICameraDevice.getParameters(), "3dnr-mode-values").indexOf(this.mISettingCtrl.getSettingValue("pref_video_3dnr_key")) >= 0) {
            this.mICameraDevice.getParameters().set("3dnr-mode", this.mISettingCtrl.getSettingValue("pref_video_3dnr_key"));
        }
        this.mICameraDevice.applyParameters();
        if (this.mCurrentShotsNum != 0) {
            showSavingProcess(false);
        }
        if (currentState == State.STATE_CAPTURE_STARTED) {
            this.mHandler.sendEmptyMessage(1001);
            setState(State.STATE_OPENED);
        } else {
            this.mCaptureSound.stop();
            setState(State.STATE_STOPPED);
        }
    }

    private boolean takePicture() {
        Log.m31d("ContinuousShot", "[takePicture]...");
        if (getCurrentState() != State.STATE_CAPTURE_STARTED) {
            Log.m36w("ContinuousShot", "[takePicture]Don't in Continuous Shot mode!");
            return false;
        }
        List<String> supportedFocusModes = this.mICameraDevice.getParameters().getSupportedFocusModes();
        if (supportedFocusModes != null && supportedFocusModes.indexOf("continuous-picture") >= 0) {
            this.mICameraDevice.getParameters().setFocusMode("continuous-picture");
        }
        this.mICameraDevice.applyParameters();
        this.mICameraDevice.takePicture(this.mShutterCallback, null, null, this.mJpegPictureCallback);
        this.mICameraAppUi.setViewState(ICameraAppUi.ViewState.VIEW_STATE_CONTINUOUS_CAPTURE);
        setState(State.STATE_CAPTURING);
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setState(State state) {
        if (this.mState == state) {
            return;
        }
        switch (m20x98aed9b3()[state.ordinal()]) {
            case 1:
                if (this.mState != State.STATE_OPENED) {
                    Log.m32e("ContinuousShot", "[setState]Error!");
                    break;
                }
                break;
            case 2:
                if (this.mState != State.STATE_CAPTURE_STARTED) {
                    Log.m32e("ContinuousShot", "[setState]Error!");
                    break;
                }
                break;
            case 3:
                if (this.mState != State.STATE_OPENED && this.mState != State.STATE_SAVING) {
                    Log.m32e("ContinuousShot", "[setState]Error!");
                    break;
                }
                break;
            case 4:
                if (this.mState != State.STATE_INIT && this.mState != State.STATE_SAVING && this.mState != State.STATE_CAPTURE_STARTED) {
                    Log.m32e("ContinuousShot", "[setState]Error!");
                    break;
                }
                break;
            case 5:
                if (this.mState != State.STATE_STOPPED) {
                    Log.m32e("ContinuousShot", "[setState]Error!");
                    break;
                }
                break;
            case 6:
                if (this.mState != State.STATE_CAPTURING) {
                    Log.m32e("ContinuousShot", "[setState]Error!");
                    break;
                }
                break;
        }
        this.mState = state;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public State getCurrentState() {
        return this.mState;
    }

    private class WaitSavingDoneThread extends Thread {
        /* synthetic */ WaitSavingDoneThread(ContinuousShot continuousShot, WaitSavingDoneThread waitSavingDoneThread) {
            this();
        }

        private WaitSavingDoneThread() {
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            ContinuousShot.this.mIFileSaver.waitDone();
            if (ContinuousShot.this.mIsDngOpenedBeforeCs) {
                ContinuousShot.this.mIsDngOpenedBeforeCs = false;
                ContinuousShot.this.openDng();
            }
            State currentState = ContinuousShot.this.getCurrentState();
            Log.m34i("ContinuousShot", "[WaitSavingDoneThread]state = " + currentState);
            if (currentState == State.STATE_SAVING) {
                ContinuousShot.this.setState(State.STATE_OPENED);
                ContinuousShot.this.mHandler.sendEmptyMessage(1002);
            }
            ContinuousShot.this.mHandler.sendEmptyMessage(1001);
        }
    }

    private void showSavingProcess(boolean z) {
        if (z && this.mCurrentShotsNum == 0) {
            Log.m31d("ContinuousShot", "[showSavingProcess]CurrentNum = " + this.mCurrentShotsNum);
            return;
        }
        String string = this.mActivity.getString(R.string.saving);
        if (z) {
            string = String.format(Locale.ENGLISH, this.mActivity.getString(R.string.continuous_saving_pictures), Integer.valueOf(this.mCurrentShotsNum));
        }
        this.mICameraAppUi.showProgress(string);
        if (this.mListener != null) {
            this.mListener.onFileSaveing();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void dismissSavingProgress() {
        Log.m31d("ContinuousShot", "[dismissSavingProgress]");
        this.mICameraAppUi.dismissProgress();
        this.mICameraAppUi.restoreViewState();
        this.mICameraAppUi.setSwipeEnabled(true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showSpeedIndicator() {
        if (!this.mIsSupportIndicator) {
            return;
        }
        if (getCurrentState() != State.STATE_CAPTURING) {
            Log.m36w("ContinuousShot", "[showSpeedIndicator]ContinuousShot don't run!");
            return;
        }
        Log.m31d("ContinuousShot", "[showSpeedIndicator]mCurrentShotsNum = " + this.mCurrentShotsNum);
        this.mICameraView.update(0, String.format(Locale.ENGLISH, "%02d", Integer.valueOf(this.mCurrentShotsNum)) + "/" + Integer.toString(this.mMaxCaptureNum));
        this.mHandler.removeMessages(1000);
        this.mHandler.sendEmptyMessageDelayed(1000, 1000L);
    }

    private class MainHandler extends Handler {
        public MainHandler(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            Log.m31d("ContinuousShot", "[handleMessage]msg.what = " + message.what);
            switch (message.what) {
                case 1000:
                    if (ContinuousShot.this.mIsSupportIndicator) {
                        ContinuousShot.this.mICameraView.uninit();
                        break;
                    }
                    break;
                case 1001:
                    ContinuousShot.this.mICameraAppUi.setThumbnailRefreshInterval(0);
                    ContinuousShot.this.mIModuleCtrl.enableOrientationListener();
                    ContinuousShot.this.dismissSavingProgress();
                    break;
                case 1002:
                    if (ContinuousShot.this.mListener != null) {
                        if (ContinuousShot.this.getCurrentState() == State.STATE_OPENED) {
                            ContinuousShot.this.mListener.restartPreview(false);
                            break;
                        }
                    } else {
                        Log.m32e("ContinuousShot", "[handleMessage]mListener is null, can't restart preview!");
                        break;
                    }
                    break;
            }
        }
    }

    boolean showContinuousNonsupportInfo(String str, int i) {
        if (str == null) {
            if (this.mIModuleCtrl.isImageCaptureIntent() || (!isSupport())) {
                String string = this.mActivity.getString(i);
                this.mICameraAppUi.showInfo(string);
                Log.m31d("ContinuousShot", "[showContinuousNonsupportInfo]" + string);
                return true;
            }
            return false;
        }
        if ("on".equals(this.mISettingCtrl.getSettingValue(str))) {
            String str2 = this.mActivity.getString(i) + this.mActivity.getString(R.string.camera_continuous_not_supported);
            this.mICameraAppUi.showInfo(str2);
            Log.m31d("ContinuousShot", "[showContinuousNonsupportInfo]info: " + str2);
            return true;
        }
        return false;
    }

    private boolean canShot() {
        int length = this.mFeatureKey.length;
        Log.m31d("ContinuousShot", "[canShot]featureNum = " + length);
        for (int i = 0; i < length; i++) {
            if (showContinuousNonsupportInfo(this.mFeatureKey[i], this.mFeatureResId[i])) {
                return false;
            }
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String createFileName(int i) {
        return this.mFormat.format(this.mPictureTakenDate) + "_" + i + "CS.jpg";
    }

    private void clearMemoryLimit() {
        if (this.mIsClearMemoryLimit) {
            Log.m31d("ContinuousShot", "[clearMemoryLimit]Clearing");
        }
        System.currentTimeMillis();
        VMRuntime.getRuntime().clearGrowthLimit();
        System.currentTimeMillis();
        this.mIsClearMemoryLimit = true;
    }

    private long getLeftStorage() {
        return this.mIFileSaver.getAvailableSpace() - this.mLowStorageThreshold;
    }

    private static ArrayList<String> split(String str) {
        if (str == null) {
            return null;
        }
        TextUtils.SimpleStringSplitter simpleStringSplitter = new TextUtils.SimpleStringSplitter(',');
        simpleStringSplitter.setString(str);
        ArrayList<String> arrayList = new ArrayList<>();
        Iterator it = simpleStringSplitter.iterator();
        while (it.hasNext()) {
            arrayList.add((String) it.next());
        }
        return arrayList;
    }

    private static List<String> getSupportedValues(Parameters parameters, String str) {
        if (parameters != null) {
            return split(parameters.get(str));
        }
        return null;
    }

    private boolean isDngOpened() {
        return "on".equalsIgnoreCase(this.mICameraContext.getSettingController().getSettingValue("pref_dng_key"));
    }

    private void closeDng() {
        Log.m31d("ContinuousShot", "[closeDng]...");
        this.mIFileSaver.setRawFlagEnabled(false);
        this.mICameraContext.getSettingController().setSettingValue("pref_dng_key", "off", this.mICameraDevice.getCameraId());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void openDng() {
        Log.m31d("ContinuousShot", "[openDng]");
        this.mICameraContext.getSettingController().setSettingValue("pref_dng_key", "on", this.mICameraDevice.getCameraId());
    }
}
