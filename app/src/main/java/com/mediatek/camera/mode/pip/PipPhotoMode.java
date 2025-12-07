package com.mediatek.camera.mode.pip;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera$AFDataCallback;
import android.os.SystemProperties;
import android.support.v4.app.FrameMetricsAggregator;
import android.util.Size;
import android.view.MotionEvent;
import android.view.Surface;
import com.mediatek.camera.ICameraAddition;
import com.mediatek.camera.ICameraContext;
import com.mediatek.camera.ICameraMode;
import com.mediatek.camera.ISettingCtrl;
import com.mediatek.camera.ISettingRule;
import com.mediatek.camera.R;
import com.mediatek.camera.mode.PhotoMode;
import com.mediatek.camera.mode.pip.PipController;
import com.mediatek.camera.platform.ICameraAppUi;
import com.mediatek.camera.platform.ICameraDeviceManager;
import com.mediatek.camera.platform.Parameters;
import com.mediatek.camera.setting.ParametersHelper;
import com.mediatek.camera.setting.SettingItem;
import com.mediatek.camera.setting.SettingUtils;
import com.mediatek.camera.setting.preference.ListPreference;
import com.mediatek.camera.util.Log;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class PipPhotoMode extends PhotoMode implements PipController.Listener, ICameraAppUi.GestureListener {

    /* renamed from: -com-mediatek-camera-ICameraMode$ActionTypeSwitchesValues, reason: not valid java name */
    private static final /* synthetic */ int[] f106commediatekcameraICameraMode$ActionTypeSwitchesValues = null;
    private static int mIsSaveRawJpegEnable = SystemProperties.getInt("camera.pip.save.raw.jpeg.enable", 0);
    private Camera.PictureCallback mBottomJpegPictureCallback;
    private int mBottomPictureHeight;
    private int mBottomPictureWidth;
    private int mCaptureOrientation;
    private boolean mIsGestureEnable;
    private boolean mModeOpened;
    private final PipController mPipController;
    private final Camera.ShutterCallback mShutterCallback;
    private Camera.PictureCallback mTopJpegPictureCallback;
    private int mTopPictureHeight;
    private int mTopPictureWidth;

    /* renamed from: -getcom-mediatek-camera-ICameraMode$ActionTypeSwitchesValues, reason: not valid java name */
    private static /* synthetic */ int[] m841getcommediatekcameraICameraMode$ActionTypeSwitchesValues() {
        if (f106commediatekcameraICameraMode$ActionTypeSwitchesValues != null) {
            return f106commediatekcameraICameraMode$ActionTypeSwitchesValues;
        }
        int[] iArr = new int[ICameraMode.ActionType.valuesCustom().length];
        try {
            iArr[ICameraMode.ActionType.ACTION_CANCEL_BUTTON_CLICK.ordinal()] = 18;
        } catch (NoSuchFieldError e) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_CAN_DO_AUTO_FOCUS.ordinal()] = 1;
        } catch (NoSuchFieldError e2) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_DISABLE_VIDEO_RECORD.ordinal()] = 19;
        } catch (NoSuchFieldError e3) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_FACE_DETECTED.ordinal()] = 2;
        } catch (NoSuchFieldError e4) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_NOTIFY_SURFCEVIEW_DESTROYED.ordinal()] = 3;
        } catch (NoSuchFieldError e5) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_NOTIFY_SURFCEVIEW_DISPLAY_IS_READY.ordinal()] = 4;
        } catch (NoSuchFieldError e6) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_OK_BUTTON_CLICK.ordinal()] = 20;
        } catch (NoSuchFieldError e7) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_BACK_KEY_PRESS.ordinal()] = 5;
        } catch (NoSuchFieldError e8) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_CAMERA_CLOSE.ordinal()] = 21;
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
            iArr[ICameraMode.ActionType.ACTION_ON_CONFIGURATION_CHANGED.ordinal()] = 22;
        } catch (NoSuchFieldError e13) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_FULL_SCREEN_CHANGED.ordinal()] = 9;
        } catch (NoSuchFieldError e14) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_KEY_EVENT_PRESS.ordinal()] = 23;
        } catch (NoSuchFieldError e15) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_LONG_PRESS.ordinal()] = 24;
        } catch (NoSuchFieldError e16) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_MEDIA_EJECT.ordinal()] = 25;
        } catch (NoSuchFieldError e17) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_PREVIEW_BUFFER_SIZE_CHANGED.ordinal()] = 10;
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
            iArr[ICameraMode.ActionType.ACTION_ON_SELFTIMER_STATE.ordinal()] = 11;
        } catch (NoSuchFieldError e21) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_SETTING_BUTTON_CLICK.ordinal()] = 12;
        } catch (NoSuchFieldError e22) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_SINGLE_TAP_UP.ordinal()] = 28;
        } catch (NoSuchFieldError e23) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_START_PREVIEW.ordinal()] = 29;
        } catch (NoSuchFieldError e24) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_STOP_PREVIEW.ordinal()] = 30;
        } catch (NoSuchFieldError e25) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_SURFACE_TEXTURE_READY.ordinal()] = 31;
        } catch (NoSuchFieldError e26) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_USER_INTERACTION.ordinal()] = 32;
        } catch (NoSuchFieldError e27) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ORITATION_CHANGED.ordinal()] = 13;
        } catch (NoSuchFieldError e28) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_PHOTO_SHUTTER_BUTTON_CLICK.ordinal()] = 14;
        } catch (NoSuchFieldError e29) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_PREVIEW_VISIBLE_CHANGED.ordinal()] = 33;
        } catch (NoSuchFieldError e30) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_SET_DISPLAYROTATION.ordinal()] = 15;
        } catch (NoSuchFieldError e31) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_SHUTTER_BUTTON_FOCUS.ordinal()] = 34;
        } catch (NoSuchFieldError e32) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_SHUTTER_BUTTON_LONG_PRESS.ordinal()] = 16;
        } catch (NoSuchFieldError e33) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_SWITCH_DEVICE.ordinal()] = 17;
        } catch (NoSuchFieldError e34) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_VIDEO_SHUTTER_BUTTON_CLICK.ordinal()] = 35;
        } catch (NoSuchFieldError e35) {
        }
        f106commediatekcameraICameraMode$ActionTypeSwitchesValues = iArr;
        return iArr;
    }

    public PipPhotoMode(ICameraContext iCameraContext) {
        super(iCameraContext);
        this.mIsGestureEnable = false;
        this.mModeOpened = false;
        this.mBottomJpegPictureCallback = new Camera.PictureCallback() { // from class: com.mediatek.camera.mode.pip.PipPhotoMode.1
            @Override // android.hardware.Camera.PictureCallback
            public void onPictureTaken(byte[] bArr, Camera camera) throws Throwable {
                Log.m31d("PipPhotoMode", "[onPictureTaken]mBottomJpegPictureCallback...");
                if (PipPhotoMode.this.mPipController == null) {
                    Log.m32e("PipPhotoMode", "[onPictureTaken]mBottomJpegPictureCallback,mPipController is null!");
                    return;
                }
                PipPhotoMode.this.mPipController.takePicture(bArr, PipPhotoMode.this.mBottomPictureWidth, PipPhotoMode.this.mBottomPictureHeight, true, PipPhotoMode.this.mCaptureOrientation);
                if (PipPhotoMode.mIsSaveRawJpegEnable > 0) {
                    PipPhotoMode.this.saveRawJpeg(bArr, "/sdcard/bottom.jpg");
                }
            }
        };
        this.mShutterCallback = new Camera.ShutterCallback() { // from class: com.mediatek.camera.mode.pip.PipPhotoMode.2
            @Override // android.hardware.Camera.ShutterCallback
            public void onShutter() {
            }
        };
        this.mTopJpegPictureCallback = new Camera.PictureCallback() { // from class: com.mediatek.camera.mode.pip.PipPhotoMode.3
            @Override // android.hardware.Camera.PictureCallback
            public void onPictureTaken(byte[] bArr, Camera camera) throws Throwable {
                Log.m31d("PipPhotoMode", "[onPictureTaken]mTopJpegPictureCallback...");
                if (PipPhotoMode.this.mPipController == null) {
                    Log.m32e("PipPhotoMode", "[onPictureTaken]mTopJpegPictureCallback,mPipController is null!");
                    return;
                }
                PipPhotoMode.this.mPipController.takePicture(bArr, PipPhotoMode.this.mTopPictureWidth, PipPhotoMode.this.mTopPictureHeight, false, PipPhotoMode.this.mCaptureOrientation);
                if (PipPhotoMode.mIsSaveRawJpegEnable > 0) {
                    PipPhotoMode.this.saveRawJpeg(bArr, "/sdcard/top.jpg");
                }
            }
        };
        Log.m31d("PipPhotoMode", "[PipPhotoMode]constructor...");
        this.mModeOpened = true;
        this.mCameraCategory = new PipCameraCategory(this, null);
        this.mICameraAppUi.setGestureListener(this);
        this.mPipController = PipController.instance(iCameraContext.getActivity());
        this.mPipController.init(iCameraContext, this);
        setPipSettingRules(this.mICameraContext);
    }

    @Override // com.mediatek.camera.mode.CameraMode, com.mediatek.camera.ICameraMode
    public void resume() {
        Log.m31d("PipPhotoMode", "[resume]");
        this.mModeOpened = true;
        this.mPipController.resume();
    }

    @Override // com.mediatek.camera.mode.CameraMode, com.mediatek.camera.ICameraMode
    public void pause() {
        Log.m31d("PipPhotoMode", "[pause]");
        this.mModeOpened = false;
        this.mPipController.pause();
    }

    @Override // com.mediatek.camera.mode.CameraMode, com.mediatek.camera.ICameraMode
    public boolean isNeedDualCamera() {
        Log.m31d("PipPhotoMode", "isNeedDualCamera");
        return true;
    }

    @Override // com.mediatek.camera.mode.PhotoMode, com.mediatek.camera.mode.CameraMode, com.mediatek.camera.ICameraMode
    public boolean open() {
        Log.m31d("PipPhotoMode", "[open]...");
        return true;
    }

    @Override // com.mediatek.camera.mode.PhotoMode, com.mediatek.camera.mode.CameraMode, com.mediatek.camera.ICameraMode
    public boolean close() {
        Log.m31d("PipPhotoMode", "[close]...");
        this.mModeOpened = false;
        this.mICameraAppUi.setSwipeEnabled(true);
        this.mICameraAppUi.restoreViewState();
        if (ICameraMode.CameraModeType.EXT_MODE_VIDEO_PIP != this.mIModuleCtrl.getNextMode()) {
            this.mPipController.unInit(this.mICameraContext.getActivity());
        }
        this.mPipController.stopSwitchPip();
        super.close();
        return true;
    }

    @Override // com.mediatek.camera.mode.PhotoMode, com.mediatek.camera.mode.CameraMode, com.mediatek.camera.ICameraMode
    public boolean execute(ICameraMode.ActionType actionType, Object... objArr) {
        Log.m31d("PipPhotoMode", "[execute]type = " + actionType);
        this.mAdditionManager.execute(actionType, true, objArr);
        switch (m841getcommediatekcameraICameraMode$ActionTypeSwitchesValues()[actionType.ordinal()]) {
            case 1:
                break;
            case 2:
                break;
            case 3:
                this.mPipController.notifySurfaceViewDestroyed((Surface) objArr[0]);
                break;
            case 4:
                this.mPipController.setPreviewSurface(this.mIModuleCtrl.getPreviewSurface());
                break;
            case 5:
                if (this.mPipController.isPipEffectShowing()) {
                    this.mPipController.closeEffects();
                    break;
                }
                break;
            case 6:
                super.updateDevice();
                this.mCameraClosed = false;
                break;
            case FrameMetricsAggregator.DELAY_INDEX /* 7 */:
                super.executeAction(actionType, objArr);
                this.mPipController.setState(PipController.State.STATE_IDLE);
                break;
            case 8:
                this.mPipController.onViewOrienationChanged(((Integer) objArr[0]).intValue());
                break;
            case 9:
                this.mPipController.hideModeViews(!((Boolean) objArr[0]).booleanValue());
                break;
            case 10:
                onPreviewBufferSizeChanged(((Integer) objArr[0]).intValue(), ((Integer) objArr[1]).intValue());
                break;
            case 11:
                this.mIsGestureEnable = !((Boolean) objArr[0]).booleanValue();
                this.mPipController.hideModeViews(((Boolean) objArr[0]).booleanValue());
                break;
            case 12:
                this.mIsGestureEnable = !((Boolean) objArr[0]).booleanValue();
                this.mPipController.hideModeViews(((Boolean) objArr[0]).booleanValue());
                break;
            case 13:
                this.mPipController.onGSensorOrientationChanged(((Integer) objArr[0]).intValue());
                break;
            case 14:
                if (PipController.State.STATE_IDLE == this.mPipController.getState()) {
                    this.mPipController.closeEffects();
                    super.executeAction(actionType, objArr);
                    break;
                }
                break;
            case 15:
                setDisplayOrientation(((Integer) objArr[0]).intValue());
                break;
            case 16:
                this.mICameraAppUi.showInfo(this.mActivity.getString(R.string.pip_continuous_not_supported));
                break;
            case 17:
                switchDevice();
                break;
            default:
                super.executeAction(actionType, objArr);
                break;
        }
        return true;
    }

    @Override // com.mediatek.camera.mode.pip.PipController.Listener
    public int getGSensorOrientation() {
        return this.mIModuleCtrl.getOrientation();
    }

    @Override // com.mediatek.camera.mode.pip.PipController.Listener
    public int getViewRotation() {
        return this.mIModuleCtrl.getOrientationCompensation();
    }

    @Override // com.mediatek.camera.mode.pip.PipController.Listener
    public int getButtomGraphicCameraId() {
        return this.mICameraDeviceManager.getCurrentCameraId();
    }

    @Override // com.mediatek.camera.mode.pip.PipController.Listener
    public void switchPIP() {
        Log.m31d("PipPhotoMode", "[switchPIP]...");
        setAfDataCallback(null);
        if (this.mIFocusManager != null) {
            this.mIFocusManager.cancelAutoFocus();
            this.mIFocusManager.clearFocusAndFaceUi();
        }
        setAfMvCallback(null);
        this.mIModuleCtrl.switchCameraDevice();
        this.mIModuleCtrl.applyFocusParameters(false);
        updateDevice();
        setAfMvCallback(this.mAutoFocusMoveCallback);
        setAfDataCallback(this.mAFDataCallback);
    }

    @Override // com.mediatek.camera.mode.pip.PipController.Listener
    public void onPIPPictureTaken(byte[] bArr) {
        Log.m31d("PipPhotoMode", "[onPIPPictureTaken]...");
        if (bArr == null) {
            Log.m34i("PipPhotoMode", "[onPIPPictureTaken]jpegData is null,return!");
        } else {
            this.mIFileSaver.savePhotoFile(bArr, null, this.mCaptureStartTime, this.mIModuleCtrl.getLocation(), 0, null);
            System.gc();
        }
    }

    @Override // com.mediatek.camera.mode.pip.PipController.Listener
    public void canDoStartPreview() {
        Log.m31d("PipPhotoMode", "[canDoStartPreview]mCameraClosed = " + this.mCameraClosed + ", mModeResumed = " + this.mModeOpened);
        this.mActivity.runOnUiThread(new Runnable() { // from class: com.mediatek.camera.mode.pip.PipPhotoMode.4
            @Override // java.lang.Runnable
            public void run() {
                if (!PipPhotoMode.this.mCameraClosed && PipPhotoMode.this.mModeOpened) {
                    PipPhotoMode.this.restartPreview(false);
                }
            }
        });
    }

    @Override // com.mediatek.camera.mode.CameraMode, com.mediatek.camera.ICameraMode
    public boolean isDisplayUseSurfaceView() {
        Log.m31d("PipPhotoMode", "[isDisplayUseSurfaceView]");
        return true;
    }

    @Override // com.mediatek.camera.mode.CameraMode, com.mediatek.camera.ICameraMode
    public boolean isDeviceUseSurfaceView() {
        Log.m31d("PipPhotoMode", "[isDeviceUseSurfaceView]");
        return false;
    }

    @Override // com.mediatek.camera.mode.CameraMode, com.mediatek.camera.ICameraMode
    public SurfaceTexture getBottomSurfaceTexture() {
        return this.mPipController.getBottomSurfaceTexture();
    }

    @Override // com.mediatek.camera.mode.CameraMode, com.mediatek.camera.ICameraMode
    public SurfaceTexture getTopSurfaceTexture() {
        return this.mPipController.getTopSurfaceTexture();
    }

    @Override // com.mediatek.camera.mode.PhotoMode, com.mediatek.camera.platform.IFocusManager.FocusListener
    public void autoFocus() {
        Log.m31d("PipPhotoMode", "[autoFocus]");
        super.autoFocus();
        this.mPipController.closeEffects();
    }

    @Override // com.mediatek.camera.mode.PhotoMode, com.mediatek.camera.platform.IFocusManager.FocusListener
    public void cancelAutoFocus() {
        Log.m31d("PipPhotoMode", "[cancelAutoFocus]");
        super.cancelAutoFocus();
    }

    @Override // com.mediatek.camera.mode.PhotoMode
    protected void startPreview(boolean z) {
        Log.m31d("PipPhotoMode", "[startPreview] needStop = " + z);
        this.mPipController.stopSwitchPip();
        super.startPreview(z);
        this.mICameraDeviceManager.getCameraDevice(getTopCameraId()).startPreview();
        this.mIsGestureEnable = true;
    }

    @Override // com.mediatek.camera.mode.PhotoMode
    protected void stopPreview() {
        Log.m31d("PipPhotoMode", "[stopPreview]...");
        this.mPipController.stopSwitchPip();
        this.mIsGestureEnable = false;
        super.stopPreview();
        this.mICameraDeviceManager.getCameraDevice(getTopCameraId()).stopPreview();
    }

    @Override // com.mediatek.camera.mode.PhotoMode
    protected Camera.PictureCallback getUncompressedImageCallback() {
        return null;
    }

    private void setDisplayOrientation(int i) {
        Log.m31d("PipPhotoMode", "setDisplayOrientation displayRotation = " + i);
        this.mPipController.setDisplayRotation(i);
    }

    private void onPreviewBufferSizeChanged(int i, int i2) {
        this.mPipController.setPreviewTextureSize(i, i2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getTopCameraId() {
        if (this.mICameraDeviceManager.getCurrentCameraId() == this.mICameraDeviceManager.getBackCameraId()) {
            return this.mICameraDeviceManager.getFrontCameraId();
        }
        return this.mICameraDeviceManager.getBackCameraId();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Removed duplicated region for block: B:34:0x0057 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public void saveRawJpeg(byte[] r5, java.lang.String r6) throws java.lang.Throwable {
        /*
            r4 = this;
            r2 = 0
            java.lang.String r0 = "PipPhotoMode"
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r3 = "[saveRawJpeg]path = "
            java.lang.StringBuilder r1 = r1.append(r3)
            java.lang.StringBuilder r1 = r1.append(r6)
            java.lang.String r1 = r1.toString()
            com.mediatek.camera.util.Log.m31d(r0, r1)
            java.io.FileOutputStream r1 = new java.io.FileOutputStream     // Catch: java.io.IOException -> L37 java.lang.Throwable -> L53
            r1.<init>(r6)     // Catch: java.io.IOException -> L37 java.lang.Throwable -> L53
            r1.write(r5)     // Catch: java.lang.Throwable -> L66 java.io.IOException -> L68
            r1.close()     // Catch: java.lang.Throwable -> L66 java.io.IOException -> L68
            if (r1 == 0) goto L2b
            r1.close()     // Catch: java.io.IOException -> L2c
        L2b:
            return
        L2c:
            r0 = move-exception
            java.lang.String r1 = "PipPhotoMode"
            java.lang.String r2 = "[saveRawJpeg]ioexception:"
            com.mediatek.camera.util.Log.m33e(r1, r2, r0)
            goto L2b
        L37:
            r0 = move-exception
            r1 = r2
        L39:
            java.lang.String r2 = "PipPhotoMode"
            java.lang.String r3 = "[saveRawJpeg]Failed to write image,exception:"
            com.mediatek.camera.util.Log.m33e(r2, r3, r0)     // Catch: java.lang.Throwable -> L66
            if (r1 == 0) goto L2b
            r1.close()     // Catch: java.io.IOException -> L48
            goto L2b
        L48:
            r0 = move-exception
            java.lang.String r1 = "PipPhotoMode"
            java.lang.String r2 = "[saveRawJpeg]ioexception:"
            com.mediatek.camera.util.Log.m33e(r1, r2, r0)
            goto L2b
        L53:
            r0 = move-exception
            r1 = r2
        L55:
            if (r1 == 0) goto L5a
            r1.close()     // Catch: java.io.IOException -> L5b
        L5a:
            throw r0
        L5b:
            r1 = move-exception
            java.lang.String r2 = "PipPhotoMode"
            java.lang.String r3 = "[saveRawJpeg]ioexception:"
            com.mediatek.camera.util.Log.m33e(r2, r3, r1)
            goto L5a
        L66:
            r0 = move-exception
            goto L55
        L68:
            r0 = move-exception
            goto L39
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mediatek.camera.mode.pip.PipPhotoMode.saveRawJpeg(byte[], java.lang.String):void");
    }

    class PipCameraCategory extends PhotoMode.CameraCategory {
        /* synthetic */ PipCameraCategory(PipPhotoMode pipPhotoMode, PipCameraCategory pipCameraCategory) {
            this();
        }

        private PipCameraCategory() {
            super();
        }

        @Override // com.mediatek.camera.mode.PhotoMode.CameraCategory
        public void takePicture() throws InterruptedException {
            Log.m31d("PipPhotoMode", "[takePicture]...");
            PipPhotoMode.this.mPipController.stopSwitchPip();
            PipPhotoMode.this.mCaptureOrientation = PipPhotoMode.this.mIModuleCtrl.getOrientation();
            PipPhotoMode.this.updateTopPictureSize();
            PipPhotoMode.this.updateBottomPictureSize();
            PipPhotoMode.this.mPipController.setPictureSize(new Size(PipPhotoMode.this.mBottomPictureWidth, PipPhotoMode.this.mBottomPictureHeight), new Size(PipPhotoMode.this.mTopPictureWidth, PipPhotoMode.this.mTopPictureHeight));
            PipPhotoMode.this.mAdditionManager.execute(ICameraAddition.AdditionActionType.ACTION_TAKEN_PICTURE, new Object[0]);
            PipPhotoMode.this.mICameraDeviceManager.getCameraDevice(PipPhotoMode.this.getTopCameraId()).takePictureAsync(PipPhotoMode.this.mShutterCallback, null, null, PipPhotoMode.this.mTopJpegPictureCallback);
            PipPhotoMode.this.mICameraDeviceManager.getCameraDevice(PipPhotoMode.this.mICameraDeviceManager.getCurrentCameraId()).takePicture(null, null, null, PipPhotoMode.this.mBottomJpegPictureCallback);
            PipPhotoMode.this.mICameraAppUi.setViewState(ICameraAppUi.ViewState.VIEW_STATE_CAPTURE);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateBottomPictureSize() {
        Log.m31d("PipPhotoMode", "[updateBottomPictureSize]...");
        Camera.Size pictureSize = this.mICameraDeviceManager.getCameraDevice(this.mICameraDeviceManager.getCurrentCameraId()).getParameters().getPictureSize();
        if (pictureSize == null) {
            Log.m31d("PipPhotoMode", "updateBottomPictureSize size==null");
        } else if (this.mCaptureOrientation % 180 == 0) {
            this.mBottomPictureWidth = pictureSize.height;
            this.mBottomPictureHeight = pictureSize.width;
        } else {
            this.mBottomPictureWidth = pictureSize.width;
            this.mBottomPictureHeight = pictureSize.height;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateTopPictureSize() {
        Log.m31d("PipPhotoMode", "[updateTopPictureSize]...");
        Camera.Size pictureSize = this.mICameraDeviceManager.getCameraDevice(getTopCameraId()).getParameters().getPictureSize();
        if (pictureSize == null) {
            Log.m36w("PipPhotoMode", "[updateTopPictureSize]size == null");
        } else if (this.mCaptureOrientation % 180 == 0) {
            this.mTopPictureWidth = pictureSize.height;
            this.mTopPictureHeight = pictureSize.width;
        } else {
            this.mTopPictureWidth = pictureSize.width;
            this.mTopPictureHeight = pictureSize.height;
        }
    }

    private void setAfMvCallback(ICameraDeviceManager.ICameraDevice.AutoFocusMvCallback autoFocusMvCallback) {
        Log.m31d("PipPhotoMode", "[setAfMvCallback]...mICameraDevice = " + this.mICameraDevice);
        if (this.mICameraDevice != null) {
            this.mICameraDevice.setAutoFocusMoveCallback(autoFocusMvCallback);
        }
    }

    private void setAfDataCallback(Camera$AFDataCallback camera$AFDataCallback) {
        if (this.mActivity.getResources().getString(R.string.af_multi_mode).equals(this.mISettingCtrl.getSettingValue("pref_af_mode_key")) && this.mICameraDevice != null && this.mICameraDevice.getCamera() != null) {
            this.mICameraDevice.getCamera().setAFDataCallback(camera$AFDataCallback);
        }
    }

    private void setPipSettingRules(ICameraContext iCameraContext) {
        Log.m31d("PipPhotoMode", "[setPipSettingRules]...");
        PipVideoQualityRule pipVideoQualityRule = new PipVideoQualityRule(iCameraContext, "photo_pip_key");
        pipVideoQualityRule.addLimitation("on", null, null);
        this.mISettingCtrl.addRule("photo_pip_key", "pref_video_quality_key", pipVideoQualityRule);
        PipPreviewSizeRule pipPreviewSizeRule = new PipPreviewSizeRule(iCameraContext);
        pipPreviewSizeRule.addLimitation("on", null, null);
        this.mISettingCtrl.addRule("photo_pip_key", "pref_camera_picturesize_ratio_key", pipPreviewSizeRule);
        PipPictureSizeRule pipPictureSizeRule = new PipPictureSizeRule(iCameraContext);
        pipPictureSizeRule.addLimitation("on", null, null);
        this.mISettingCtrl.addRule("photo_pip_key", "pref_camera_picturesize_key", pipPictureSizeRule);
        PipZsdRule pipZsdRule = new PipZsdRule(iCameraContext);
        pipZsdRule.addLimitation("on", null, null);
        this.mISettingCtrl.addRule("photo_pip_key", "pref_camera_zsd_key", pipZsdRule);
        PipFlashRule pipFlashRule = new PipFlashRule(iCameraContext);
        pipFlashRule.addLimitation("on", null, null);
        this.mISettingCtrl.addRule("photo_pip_key", "pref_camera_flashmode_key", pipFlashRule);
        PipCameraModeRule pipCameraModeRule = new PipCameraModeRule(iCameraContext);
        pipCameraModeRule.addLimitation("on", null, null);
        this.mISettingCtrl.addRule("photo_pip_key", "camera_mode_key", pipCameraModeRule);
    }

    private class PipZsdRule implements ISettingRule {
        private ICameraDeviceManager deviceManager;
        private ICameraDeviceManager.ICameraDevice mBackCamDevice;
        private ICameraContext mCameraContext;
        private ISettingCtrl mISettingCtrl;
        private Parameters mParameters;
        private ICameraDeviceManager.ICameraDevice mTopCamDevice;
        private Parameters mTopParameters;
        private List<String> mConditions = new ArrayList();
        private List<List<String>> mResults = new ArrayList();
        private List<ISettingRule.MappingFinder> mMappingFinders = new ArrayList();
        private boolean mSwitchingPip = false;
        private String mCurrentZsdValue = null;

        public PipZsdRule(ICameraContext iCameraContext) {
            Log.m31d("PipPhotoMode", "[PipZsdRule]constructor...");
            this.mCameraContext = iCameraContext;
        }

        @Override // com.mediatek.camera.ISettingRule
        public void execute() throws NumberFormatException {
            this.deviceManager = this.mCameraContext.getCameraDeviceManager();
            this.mBackCamDevice = this.deviceManager.getCameraDevice(this.deviceManager.getCurrentCameraId());
            if (this.mTopCamDevice != null) {
                this.mSwitchingPip = this.mTopCamDevice.getCameraId() == this.deviceManager.getCurrentCameraId();
            } else {
                this.mSwitchingPip = false;
            }
            this.mTopCamDevice = this.deviceManager.getCameraDevice(PipPhotoMode.this.getTopCameraId());
            this.mISettingCtrl = this.mCameraContext.getSettingController();
            this.mParameters = this.mBackCamDevice.getParameters();
            if (this.mTopCamDevice != null) {
                this.mTopParameters = this.mTopCamDevice.getParameters();
            }
            String settingValue = this.mISettingCtrl.getSettingValue("pref_camera_picturesize_ratio_key");
            int iConditionSatisfied = conditionSatisfied(this.mISettingCtrl.getSettingValue("photo_pip_key"));
            if ("0321".equals(PipPhotoMode.this.mICameraContext.getFeatureConfig().whichDeanliChip()) || PipPhotoMode.this.mICameraContext.getFeatureConfig().isGmoRamOptSupport()) {
                PipPhotoMode.this.pipDenaliZSDRule(iConditionSatisfied);
                return;
            }
            String settingValue2 = this.mISettingCtrl.getSettingValue("pref_camera_zsd_key");
            if (settingValue2 == null) {
                Log.m31d("PipPhotoMode", "[PipZsdRule.execute] don't support zsd!");
                return;
            }
            if (this.mSwitchingPip) {
                settingValue2 = this.mCurrentZsdValue;
                this.mISettingCtrl.setSettingValue("pref_camera_zsd_key", settingValue2, this.deviceManager.getCurrentCameraId());
                ListPreference listPreference = this.mISettingCtrl.getListPreference("pref_camera_zsd_key");
                if (listPreference != null) {
                    listPreference.setValue(settingValue2);
                }
            }
            this.mCurrentZsdValue = settingValue2;
            Log.m31d("PipPhotoMode", "[execute]PipZsdRule index = " + iConditionSatisfied);
            if (iConditionSatisfied == -1) {
                this.mParameters.setZSDMode(settingValue2);
                return;
            }
            this.mParameters.setZSDMode(settingValue2);
            if (this.mTopParameters != null) {
                this.mTopParameters.setZSDMode(settingValue2);
            }
            SettingUtils.setPipPreviewSize(this.mCameraContext.getActivity(), this.mParameters, this.mTopParameters, this.mISettingCtrl, settingValue);
        }

        @Override // com.mediatek.camera.ISettingRule
        public void addLimitation(String str, List<String> list, ISettingRule.MappingFinder mappingFinder) {
            Log.m31d("PipPhotoMode", "[addLimitation]condition = " + str);
            this.mConditions.add(str);
            this.mResults.add(list);
            this.mMappingFinders.add(mappingFinder);
        }

        private int conditionSatisfied(String str) {
            return this.mConditions.indexOf(str);
        }
    }

    private class PipFlashRule implements ISettingRule {
        private ICameraDeviceManager deviceManager;
        private ICameraDeviceManager.ICameraDevice mBackCamDevice;
        private ICameraContext mCameraContext;
        private ISettingCtrl mISettingCtrl;
        private Parameters mParameters;
        private ICameraDeviceManager.ICameraDevice mTopCamDevice;
        private Parameters mTopParameters;
        private List<String> mConditions = new ArrayList();
        private List<List<String>> mResults = new ArrayList();
        private List<ISettingRule.MappingFinder> mMappingFinders = new ArrayList();

        public PipFlashRule(ICameraContext iCameraContext) {
            Log.m31d("PipPhotoMode", "[PipFlashRule]constructor...");
            this.mCameraContext = iCameraContext;
        }

        @Override // com.mediatek.camera.ISettingRule
        public void execute() {
            this.deviceManager = this.mCameraContext.getCameraDeviceManager();
            this.mBackCamDevice = this.deviceManager.getCameraDevice(this.deviceManager.getCurrentCameraId());
            this.mTopCamDevice = this.deviceManager.getCameraDevice(PipPhotoMode.this.getTopCameraId());
            this.mISettingCtrl = this.mCameraContext.getSettingController();
            this.mParameters = this.mBackCamDevice.getParameters();
            if (this.mTopCamDevice != null) {
                this.mTopParameters = this.mTopCamDevice.getParameters();
            }
            int iConditionSatisfied = conditionSatisfied(this.mISettingCtrl.getSettingValue("photo_pip_key"));
            String value = this.mISettingCtrl.getSetting("pref_camera_flashmode_key", this.deviceManager.getCurrentCameraId()).getValue();
            SettingItem setting = this.mISettingCtrl.getSetting("pref_camera_flashmode_key", PipPhotoMode.this.getTopCameraId());
            String value2 = setting != null ? setting.getValue() : null;
            Log.m31d("PipPhotoMode", "[execute]PipFlashRule index = " + iConditionSatisfied);
            if (iConditionSatisfied != -1) {
                Log.m31d("PipPhotoMode", "[execute]PipFlashRule currentFlashValue = " + value + ", topFlashValue = " + value2);
                if (value != null) {
                    this.mParameters.setFlashMode(value);
                }
                if (this.mTopParameters != null && value2 != null) {
                    this.mTopParameters.setFlashMode(value2);
                }
            }
        }

        @Override // com.mediatek.camera.ISettingRule
        public void addLimitation(String str, List<String> list, ISettingRule.MappingFinder mappingFinder) {
            Log.m31d("PipPhotoMode", "[addLimitation]condition = " + str);
            this.mConditions.add(str);
            this.mResults.add(list);
            this.mMappingFinders.add(mappingFinder);
        }

        private int conditionSatisfied(String str) {
            return this.mConditions.indexOf(str);
        }
    }

    private class PipCameraModeRule implements ISettingRule {
        private ICameraDeviceManager deviceManager;
        private ICameraDeviceManager.ICameraDevice mBackCamDevice;
        private ICameraContext mCameraContext;
        private ISettingCtrl mISettingCtrl;
        private Parameters mParameters;
        private ICameraDeviceManager.ICameraDevice mTopCamDevice;
        private Parameters mTopParameters;
        private List<String> mConditions = new ArrayList();
        private List<List<String>> mResults = new ArrayList();
        private List<ISettingRule.MappingFinder> mMappingFinders = new ArrayList();

        public PipCameraModeRule(ICameraContext iCameraContext) {
            Log.m31d("PipPhotoMode", "[PipCameraModeRule]constructor...");
            this.mCameraContext = iCameraContext;
        }

        @Override // com.mediatek.camera.ISettingRule
        public void execute() {
            this.deviceManager = this.mCameraContext.getCameraDeviceManager();
            this.mBackCamDevice = this.deviceManager.getCameraDevice(this.deviceManager.getCurrentCameraId());
            this.mTopCamDevice = this.deviceManager.getCameraDevice(PipPhotoMode.this.getTopCameraId());
            this.mISettingCtrl = this.mCameraContext.getSettingController();
            this.mParameters = this.mBackCamDevice.getParameters();
            if (this.mTopCamDevice != null) {
                this.mTopParameters = this.mTopCamDevice.getParameters();
            }
            int iConditionSatisfied = conditionSatisfied(this.mISettingCtrl.getSettingValue("photo_pip_key"));
            Log.m31d("PipPhotoMode", "[execute]PipCameraModeRule index = " + iConditionSatisfied);
            if (iConditionSatisfied != -1) {
                Log.m31d("PipPhotoMode", "[execute]PipCameraModeRule currentCameraMode = 1");
                this.mParameters.setCameraMode(1);
                if (this.mTopParameters != null) {
                    this.mTopParameters.setCameraMode(1);
                }
            }
        }

        @Override // com.mediatek.camera.ISettingRule
        public void addLimitation(String str, List<String> list, ISettingRule.MappingFinder mappingFinder) {
            Log.m31d("PipPhotoMode", "[addLimitation]condition = " + str);
            this.mConditions.add(str);
            this.mResults.add(list);
            this.mMappingFinders.add(mappingFinder);
        }

        private int conditionSatisfied(String str) {
            return this.mConditions.indexOf(str);
        }
    }

    private void switchDevice() {
        Log.m31d("PipPhotoMode", "[switchDevice]...");
        if (this.mPipController != null) {
            this.mPipController.switchPIP();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Removed duplicated region for block: B:47:0x010d  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public void pipDenaliZSDRule(int r10) {
        /*
            Method dump skipped, instructions count: 272
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mediatek.camera.mode.pip.PipPhotoMode.pipDenaliZSDRule(int):void");
    }

    private void setResultSettingValue(int i, String str, String str2, boolean z, SettingItem settingItem) {
        int currentCameraId = this.mICameraDeviceManager.getCurrentCameraId();
        Parameters parameters = this.mICameraDeviceManager.getCameraDevice(currentCameraId).getParameters();
        settingItem.setValue(str);
        ListPreference listPreference = settingItem.getListPreference();
        if (listPreference != null) {
            listPreference.setOverrideValue(str2, z);
        }
        ParametersHelper.setParametersValue(parameters, currentCameraId, settingItem.getKey(), str);
    }

    private boolean disableGesture() {
        return !this.mIsGestureEnable || this.mICameraAppUi.isSettingShowing() || getModeState() == ICameraMode.ModeState.STATE_FOCUSING;
    }

    @Override // com.mediatek.camera.platform.ICameraAppUi.GestureListener
    public boolean onDown(float f, float f2, int i, int i2) {
        if (disableGesture()) {
            return true;
        }
        if (this.mPipController != null) {
            return this.mPipController.onDown(f, f2, i, i2);
        }
        return false;
    }

    @Override // com.mediatek.camera.platform.ICameraAppUi.GestureListener
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
        return false;
    }

    @Override // com.mediatek.camera.platform.ICameraAppUi.GestureListener
    public boolean onScroll(float f, float f2, float f3, float f4) {
        if (this.mPipController != null) {
            return this.mPipController.onScroll(f, f2, f3, f4);
        }
        return false;
    }

    @Override // com.mediatek.camera.platform.ICameraAppUi.GestureListener
    public boolean onSingleTapUp(float f, float f2) {
        if (this.mPipController == null || getModeState() == ICameraMode.ModeState.STATE_FOCUSING) {
            return false;
        }
        this.mAdditionManager.execute(ICameraAddition.AdditionActionType.ACTION_ON_SWITCH_PIP, new Object[0]);
        return this.mPipController.onSingleTapUp(f, f2);
    }

    @Override // com.mediatek.camera.platform.ICameraAppUi.GestureListener
    public boolean onUp() {
        if (this.mPipController != null) {
            return this.mPipController.onUp();
        }
        return false;
    }

    @Override // com.mediatek.camera.platform.ICameraAppUi.GestureListener
    public boolean onDoubleTap(float f, float f2) {
        return false;
    }

    @Override // com.mediatek.camera.platform.ICameraAppUi.GestureListener
    public boolean onScale(float f, float f2, float f3) {
        return false;
    }

    @Override // com.mediatek.camera.platform.ICameraAppUi.GestureListener
    public boolean onScaleBegin(float f, float f2) {
        return false;
    }

    @Override // com.mediatek.camera.platform.ICameraAppUi.GestureListener
    public boolean onLongPress(float f, float f2) {
        if (this.mPipController != null && getModeState() != ICameraMode.ModeState.STATE_FOCUSING) {
            return this.mPipController.onLongPress(f, f2);
        }
        return false;
    }
}
