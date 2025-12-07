package com.mediatek.camera.mode;

import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera$AFDataCallback;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.FrameMetricsAggregator;
import android.view.View;
import com.android.camera.DngHelper;
import com.android.camera.Exif;
import com.mediatek.camera.AdditionManager;
import com.mediatek.camera.ICameraAddition;
import com.mediatek.camera.ICameraContext;
import com.mediatek.camera.ICameraMode;
import com.mediatek.camera.R;
import com.mediatek.camera.platform.ICameraAppUi;
import com.mediatek.camera.platform.ICameraDeviceManager;
import com.mediatek.camera.platform.IFileSaver;
import com.mediatek.camera.platform.IFocusManager;
import com.mediatek.camera.util.Log;
import com.mediatek.camera.util.Util;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import junit.framework.Assert;

/* loaded from: classes.dex */
public class PhotoMode extends CameraMode implements IFocusManager.FocusListener, ICameraAddition.Listener {

    /* renamed from: -com-mediatek-camera-ICameraMode$ActionTypeSwitchesValues, reason: not valid java name */
    private static final /* synthetic */ int[] f102commediatekcameraICameraMode$ActionTypeSwitchesValues = null;
    protected final Camera$AFDataCallback mAFDataCallback;
    protected AdditionManager mAdditionManager;
    private final Camera.AutoFocusCallback mAutoFocusCallback;
    protected final ICameraDeviceManager.ICameraDevice.AutoFocusMvCallback mAutoFocusMoveCallback;
    protected CameraCategory mCameraCategory;
    protected boolean mCameraClosed;
    protected long mCaptureStartTime;
    private int mCapturedImageCount;
    protected DngHelper mDngHelper;
    private MainHandler mHandler;
    private boolean mIsAutoFocusCallback;
    private byte[] mJpegImageData;
    private final Camera.PictureCallback mJpegPictureCallback;
    private final Camera.PictureCallback mPostViewCallback;
    private final Camera.ShutterCallback mShutterCallback;
    private long mShutterCallbackTime;
    private final Camera.PictureCallback mUncompressedImageCallback;

    /* renamed from: -getcom-mediatek-camera-ICameraMode$ActionTypeSwitchesValues, reason: not valid java name */
    private static /* synthetic */ int[] m739getcommediatekcameraICameraMode$ActionTypeSwitchesValues() {
        if (f102commediatekcameraICameraMode$ActionTypeSwitchesValues != null) {
            return f102commediatekcameraICameraMode$ActionTypeSwitchesValues;
        }
        int[] iArr = new int[ICameraMode.ActionType.valuesCustom().length];
        try {
            iArr[ICameraMode.ActionType.ACTION_CANCEL_BUTTON_CLICK.ordinal()] = 14;
        } catch (NoSuchFieldError e) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_CAN_DO_AUTO_FOCUS.ordinal()] = 1;
        } catch (NoSuchFieldError e2) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_DISABLE_VIDEO_RECORD.ordinal()] = 15;
        } catch (NoSuchFieldError e3) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_FACE_DETECTED.ordinal()] = 2;
        } catch (NoSuchFieldError e4) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_NOTIFY_SURFCEVIEW_DESTROYED.ordinal()] = 16;
        } catch (NoSuchFieldError e5) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_NOTIFY_SURFCEVIEW_DISPLAY_IS_READY.ordinal()] = 17;
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
            iArr[ICameraMode.ActionType.ACTION_ON_COMPENSATION_CHANGED.ordinal()] = 18;
        } catch (NoSuchFieldError e12) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_CONFIGURATION_CHANGED.ordinal()] = 19;
        } catch (NoSuchFieldError e13) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_FULL_SCREEN_CHANGED.ordinal()] = 20;
        } catch (NoSuchFieldError e14) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_KEY_EVENT_PRESS.ordinal()] = 21;
        } catch (NoSuchFieldError e15) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_LONG_PRESS.ordinal()] = 22;
        } catch (NoSuchFieldError e16) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_MEDIA_EJECT.ordinal()] = 23;
        } catch (NoSuchFieldError e17) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_PREVIEW_BUFFER_SIZE_CHANGED.ordinal()] = 24;
        } catch (NoSuchFieldError e18) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_PREVIEW_DISPLAY_SIZE_CHANGED.ordinal()] = 25;
        } catch (NoSuchFieldError e19) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_RESTORE_SETTINGS.ordinal()] = 8;
        } catch (NoSuchFieldError e20) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_SELFTIMER_STATE.ordinal()] = 26;
        } catch (NoSuchFieldError e21) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_SETTING_BUTTON_CLICK.ordinal()] = 27;
        } catch (NoSuchFieldError e22) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_SINGLE_TAP_UP.ordinal()] = 9;
        } catch (NoSuchFieldError e23) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_START_PREVIEW.ordinal()] = 10;
        } catch (NoSuchFieldError e24) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_STOP_PREVIEW.ordinal()] = 11;
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
            iArr[ICameraMode.ActionType.ACTION_PHOTO_SHUTTER_BUTTON_CLICK.ordinal()] = 12;
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
            iArr[ICameraMode.ActionType.ACTION_SHUTTER_BUTTON_FOCUS.ordinal()] = 13;
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
        f102commediatekcameraICameraMode$ActionTypeSwitchesValues = iArr;
        return iArr;
    }

    public PhotoMode(ICameraContext iCameraContext) {
        super(iCameraContext);
        this.mCaptureStartTime = 0L;
        this.mCameraClosed = false;
        this.mShutterCallbackTime = 0L;
        this.mIsAutoFocusCallback = false;
        this.mCapturedImageCount = 0;
        this.mAutoFocusMoveCallback = new ICameraDeviceManager.ICameraDevice.AutoFocusMvCallback() { // from class: com.mediatek.camera.mode.PhotoMode.1
            @Override // com.mediatek.camera.platform.ICameraDeviceManager.ICameraDevice.AutoFocusMvCallback
            public void onAutoFocusMoving(boolean z, Camera camera) {
                if (ICameraMode.ModeState.STATE_CAPTURING == PhotoMode.this.getModeState()) {
                    return;
                }
                if ("on".equals(PhotoMode.this.mISettingCtrl.getSettingValue("object_tracking_key"))) {
                    PhotoMode.this.mIFocusManager.clearFocusAndFaceUi();
                } else {
                    PhotoMode.this.mIFocusManager.onAutoFocusMoving(z);
                }
            }
        };
        this.mAFDataCallback = new Camera$AFDataCallback() { // from class: com.mediatek.camera.mode.PhotoMode.2
            public void onAFData(byte[] bArr, Camera camera) {
                boolean z = PhotoMode.this.mActivity.getResources().getString(R.string.af_multi_mode).equals(PhotoMode.this.mISettingCtrl.getSettingValue("pref_af_mode_key"));
                if (bArr != null && bArr.length > 0 && z) {
                    PhotoMode.this.mIFocusManager.setAfData(bArr);
                } else {
                    Log.m36w("PhotoMode", "onAFData AF data is got in single AF mode with isMultiAfMode = " + z);
                    PhotoMode.this.mIFocusManager.setAfData(null);
                }
            }
        };
        this.mAutoFocusCallback = new Camera.AutoFocusCallback() { // from class: com.mediatek.camera.mode.PhotoMode.3
            @Override // android.hardware.Camera.AutoFocusCallback
            public void onAutoFocus(boolean z, Camera camera) {
                if (PhotoMode.this.isCameraNotAvailable()) {
                    Log.m36w("PhotoMode", "[mAutoFocusCallback] camera is busy or closed, return");
                    return;
                }
                if (!PhotoMode.this.mISelfTimeManager.isSelfTimerCounting() && ICameraMode.ModeState.STATE_FOCUSING == PhotoMode.this.getModeState()) {
                    PhotoMode.this.mICameraAppUi.restoreViewState();
                }
                PhotoMode.this.setModeState(ICameraMode.ModeState.STATE_IDLE);
                PhotoMode.this.mIFocusManager.onAutoFocus(z);
                PhotoMode.this.mIsAutoFocusCallback = true;
            }
        };
        this.mShutterCallback = new Camera.ShutterCallback() { // from class: com.mediatek.camera.mode.PhotoMode.4
            @Override // android.hardware.Camera.ShutterCallback
            public void onShutter() {
                PhotoMode.this.mShutterCallbackTime = System.currentTimeMillis();
                Log.m31d("PhotoMode", "[mShutterCallback] mShutterLag = " + (PhotoMode.this.mShutterCallbackTime - PhotoMode.this.mCaptureStartTime) + "ms");
            }
        };
        this.mUncompressedImageCallback = new Camera.PictureCallback() { // from class: com.mediatek.camera.mode.PhotoMode.5
            @Override // android.hardware.Camera.PictureCallback
            public void onPictureTaken(byte[] bArr, Camera camera) {
                Log.m31d("PhotoMode", "[UncompressedImageCallback]onCanCapture");
                PhotoMode.this.mCapturedImageCount++;
                PhotoMode.this.restartPreview(false);
            }
        };
        this.mJpegPictureCallback = new Camera.PictureCallback() { // from class: com.mediatek.camera.mode.PhotoMode.6
            @Override // android.hardware.Camera.PictureCallback
            public void onPictureTaken(byte[] bArr, Camera camera) {
                if (bArr == null) {
                    Log.m36w("PhotoMode", "[mJpegPictureCallback] jpegData is null");
                    PhotoMode.this.mICameraAppUi.setSwipeEnabled(true);
                    PhotoMode.this.mICameraAppUi.restoreViewState();
                    PhotoMode.this.restartPreview(false);
                    return;
                }
                if (PhotoMode.this.mCameraClosed) {
                    Log.m31d("PhotoMode", "[onPictureTaken] mCameraClosed:" + PhotoMode.this.mCameraClosed);
                    return;
                }
                long jCurrentTimeMillis = System.currentTimeMillis();
                Log.m31d("PhotoMode", "[mJpegPictureCallback] jpegPictureCallbackTime = " + jCurrentTimeMillis + "ms");
                PhotoMode.this.mIFocusManager.updateFocusUI();
                boolean z = !PhotoMode.this.mIModuleCtrl.isImageCaptureIntent() && ICameraMode.ModeState.STATE_CAPTURING == PhotoMode.this.getModeState() && PhotoMode.this.mCapturedImageCount == 0;
                if (z) {
                    PhotoMode.this.restartPreview(true);
                }
                if (PhotoMode.this.mCapturedImageCount > 0) {
                    PhotoMode photoMode = PhotoMode.this;
                    photoMode.mCapturedImageCount--;
                }
                if (!PhotoMode.this.mIModuleCtrl.isImageCaptureIntent()) {
                    PhotoMode.this.mIFileSaver.savePhotoFile(bArr, null, PhotoMode.this.mCaptureStartTime, PhotoMode.this.mIModuleCtrl.getLocation(), 0, null);
                } else {
                    PhotoMode.this.mJpegImageData = bArr;
                    if (!PhotoMode.this.mIModuleCtrl.isQuickCapture()) {
                        PhotoMode.this.mICameraAppUi.showReview(null, null);
                        PhotoMode.this.mICameraAppUi.switchShutterType(ICameraAppUi.ShutterButtonType.SHUTTER_TYPE_OK_CANCEL);
                    } else {
                        PhotoMode.this.doAttach();
                    }
                }
                Log.m31d("PhotoMode", "[mJpegPictureCallback] jpegCallbackFinishTime = " + (System.currentTimeMillis() - jCurrentTimeMillis) + "ms");
            }
        };
        this.mPostViewCallback = new Camera.PictureCallback() { // from class: com.mediatek.camera.mode.PhotoMode.7
            @Override // android.hardware.Camera.PictureCallback
            public void onPictureTaken(byte[] bArr, Camera camera) throws NumberFormatException {
                if (PhotoMode.this.mCameraClosed) {
                    Log.m31d("PhotoMode", "[mPostViewCallback] mCameraClosed:" + PhotoMode.this.mCameraClosed);
                } else if (bArr != null) {
                    Camera.Size previewSize = PhotoMode.this.mICameraDevice.getParameters().getPreviewSize();
                    int i = Integer.parseInt(PhotoMode.this.mICameraDevice.getParameters().get("rotation"));
                    Log.m31d("PhotoMode", "[mPostViewCallback] width = " + previewSize.width + ", height = " + previewSize.height + ", jpegRotation = " + i);
                    PhotoMode.this.mICameraAppUi.updateThumbnailViewWithYuv(bArr, previewSize.width, previewSize.height, i, 17);
                }
            }
        };
        if (this.mIModuleCtrl.isImageCaptureIntent()) {
            this.mICameraAppUi.switchShutterType(ICameraAppUi.ShutterButtonType.SHUTTER_TYPE_PHOTO);
        } else {
            this.mICameraAppUi.switchShutterType(ICameraAppUi.ShutterButtonType.SHUTTER_TYPE_PHOTO_VIDEO);
        }
        this.mAdditionManager = iCameraContext.getAdditionManager();
        this.mCameraCategory = new CameraCategory();
        this.mHandler = new MainHandler(this.mActivity.getMainLooper());
        this.mDngHelper = DngHelper.getInstance(this.mActivity.getApplicationContext());
    }

    @Override // com.mediatek.camera.mode.CameraMode, com.mediatek.camera.ICameraMode
    public boolean open() {
        this.mAdditionManager.setListener(this);
        this.mAdditionManager.open(true);
        return true;
    }

    @Override // com.mediatek.camera.mode.CameraMode, com.mediatek.camera.ICameraMode
    public boolean close() {
        super.close();
        this.mIFileSaver.setRawFlagEnabled(false);
        this.mAdditionManager.close(true);
        if (ICameraMode.ModeState.STATE_CLOSED == getModeState()) {
            return true;
        }
        stopFaceDetection();
        onFaceDetected(0);
        return true;
    }

    @Override // com.mediatek.camera.mode.CameraMode, com.mediatek.camera.ICameraMode
    public boolean execute(ICameraMode.ActionType actionType, Object... objArr) {
        Log.m31d("PhotoMode", "[execute]type = " + actionType);
        boolean zExecute = this.mAdditionManager.execute(actionType, true, objArr);
        if (actionType == ICameraMode.ActionType.ACTION_ON_BACK_KEY_PRESS && zExecute) {
            return true;
        }
        return executeAction(actionType, objArr);
    }

    @Override // com.mediatek.camera.platform.IFocusManager.FocusListener
    public void autoFocus() {
        this.mICameraDevice.autoFocus(this.mAutoFocusCallback);
        this.mICameraAppUi.setViewState(ICameraAppUi.ViewState.VIEW_STATE_FOCUSING);
        setModeState(ICameraMode.ModeState.STATE_FOCUSING);
    }

    @Override // com.mediatek.camera.platform.IFocusManager.FocusListener
    public void cancelAutoFocus() {
        Log.m31d("PhotoMode", "[cancelAutoFocus]...current view state = " + this.mICameraAppUi.getViewState());
        if (ICameraAppUi.ViewState.VIEW_STATE_CAPTURE == this.mICameraAppUi.getViewState()) {
            return;
        }
        if (!this.mIsAutoFocusCallback) {
            this.mICameraDevice.cancelAutoFocus();
            this.mIsAutoFocusCallback = true;
        }
        if (!this.mISelfTimeManager.isSelfTimerCounting() && this.mICameraAppUi.getViewState() != ICameraAppUi.ViewState.VIEW_STATE_LOMOEFFECT_SETTING) {
            this.mICameraAppUi.restoreViewState();
        }
        setFocusParameters();
        setModeState(ICameraMode.ModeState.STATE_IDLE);
    }

    @Override // com.mediatek.camera.platform.IFocusManager.FocusListener
    public boolean capture() {
        long jCurrentTimeMillis = System.currentTimeMillis();
        this.mCaptureStartTime = System.currentTimeMillis();
        this.mJpegImageData = null;
        if ("on".equals(this.mISettingCtrl.getSettingValue("pref_dng_key"))) {
            this.mIFileSaver.setRawFlagEnabled(true);
            this.mIFileSaver.init(IFileSaver.FILE_TYPE.RAW, 0, null, -1);
            this.mIFileSaver.init(IFileSaver.FILE_TYPE.JPEG, 0, null, -1);
        } else {
            this.mIFileSaver.setRawFlagEnabled(false);
            this.mIFileSaver.init(IFileSaver.FILE_TYPE.JPEG, 0, null, -1);
        }
        this.mICameraAppUi.setSwipeEnabled(false);
        this.mICameraAppUi.showRemaining();
        this.mCameraCategory.takePicture();
        setModeState(ICameraMode.ModeState.STATE_CAPTURING);
        Log.m31d("PhotoMode", "[capture] Capture time = " + (System.currentTimeMillis() - jCurrentTimeMillis));
        return true;
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
        if (this.mCameraSound != null) {
            this.mCameraSound.play(i);
        }
    }

    protected boolean executeAction(ICameraMode.ActionType actionType, Object... objArr) {
        switch (m739getcommediatekcameraICameraMode$ActionTypeSwitchesValues()[actionType.ordinal()]) {
            case 1:
                Log.m31d("PhotoMode", "ACTION_CAN_DO_AUTO_FOCUS,mode state = " + getModeState());
                return ICameraMode.ModeState.STATE_CAPTURING != getModeState();
            case 2:
                if (objArr == null) {
                    return true;
                }
                Log.m31d("PhotoMode", "faceLength = " + ((Camera.Face[]) objArr).length);
                onFaceDetected(((Camera.Face[]) objArr).length);
                return true;
            case 3:
                doAttach();
                return true;
            case 4:
                return onBackPressed();
            case 5:
                onCameraClose();
                return true;
            case 6:
                super.updateDevice();
                this.mCameraClosed = false;
                return true;
            case FrameMetricsAggregator.DELAY_INDEX /* 7 */:
                Assert.assertTrue(objArr.length == 1);
                onCameraParameterReady(((Boolean) objArr[0]).booleanValue());
                return true;
            case 8:
                this.mICameraAppUi.changeBackToVFBModeStatues(false);
                return true;
            case 9:
                Assert.assertTrue(objArr.length == 3);
                onSingleTapUp((View) objArr[0], ((Integer) objArr[1]).intValue(), ((Integer) objArr[2]).intValue());
                return true;
            case 10:
                Assert.assertTrue(objArr.length == 1);
                startPreview(((Boolean) objArr[0]).booleanValue());
                return true;
            case 11:
                stopPreview();
                return true;
            case 12:
                onShutterButtonClick();
                return true;
            case 13:
                Assert.assertTrue(objArr.length == 1);
                if (objArr.length < 1) {
                    Log.m34i("PhotoMode", "[execute] illegal parameter");
                    return false;
                }
                onShutterButtonFocus(((Boolean) objArr[0]).booleanValue());
                return true;
            default:
                return false;
        }
    }

    private boolean onBackPressed() {
        Log.m31d("PhotoMode", "[onBackPressed] mCurrentState:" + getModeState());
        if (!this.mIModuleCtrl.isImageCaptureIntent() || this.mICameraAppUi.getShutterType() != ICameraAppUi.ShutterButtonType.SHUTTER_TYPE_OK_CANCEL) {
            return ICameraMode.ModeState.STATE_IDLE != getModeState();
        }
        this.mIModuleCtrl.setResultAndFinish(0, new Intent());
        return false;
    }

    private void onCameraParameterReady(boolean z) {
        Log.m31d("PhotoMode", "[onCameraParameterReady] startPreview:" + z + "modeState:" + getModeState());
        if (getModeState() == ICameraMode.ModeState.STATE_UNKNOWN) {
            setModeState(ICameraMode.ModeState.STATE_IDLE);
        }
        updateParameters();
        this.mICameraDevice.setAutoFocusMoveCallback(this.mAutoFocusMoveCallback);
        this.mAdditionManager.onCameraParameterReady(true);
    }

    private void onSingleTapUp(View view, int i, int i2) {
        if (this.mIFocusManager != null && !this.mCameraClosed) {
            if (ICameraMode.ModeState.STATE_IDLE != getModeState() && ICameraMode.ModeState.STATE_FOCUSING != getModeState()) {
                return;
            }
            String focusMode = this.mIFocusManager.getFocusMode();
            if (focusMode == null || "infinity".equals(focusMode)) {
                Log.m36w("PhotoMode", "[onSingleTapUp]focusMode:" + focusMode);
            } else if (!this.mIFocusManager.getFocusAreaSupported()) {
                Log.m34i("PhotoMode", "[onSingleTapUp] getFocusAreaSupported is false");
            } else {
                this.mIFocusManager.onSingleTapUp(i, i2);
            }
        }
    }

    private void onShutterButtonFocus(boolean z) {
        this.mICameraAppUi.collapseViewManager(true);
    }

    private void onShutterButtonClick() {
        if (!this.mIFileSaver.isEnoughSpace() || isCameraNotAvailable()) {
            Log.m36w("PhotoMode", "[onShutterButtonClick]return.");
            return;
        }
        Log.m34i("PhotoMode", "[CMCC Performance test][Camera][Camera] camera capture start [" + System.currentTimeMillis() + "]");
        if (this.mIFocusManager != null) {
            this.mIFocusManager.focusAndCapture();
        }
    }

    private void onCameraClose() {
        Log.m31d("PhotoMode", "[onCameraClose]");
        this.mAdditionManager.execute(ICameraAddition.AdditionActionType.ACTION_ON_STOP_PREVIEW, new Object[0]);
        this.mAdditionManager.close(true);
        this.mCameraClosed = true;
        this.mCapturedImageCount = 0;
        this.mHandler.removeMessages(101);
        onFaceDetected(0);
        if (this.mIModuleCtrl.isImageCaptureIntent()) {
            this.mICameraAppUi.hideReview();
        } else {
            ICameraMode.ModeState modeState = getModeState();
            if (ICameraMode.ModeState.STATE_FOCUSING == modeState) {
                this.mICameraAppUi.restoreViewState();
            } else if (ICameraMode.ModeState.STATE_CAPTURING == modeState) {
                this.mICameraAppUi.restoreViewState();
                this.mICameraAppUi.setSwipeEnabled(true);
            }
        }
        setModeState(ICameraMode.ModeState.STATE_CLOSED);
    }

    protected void getDngImageAndSaved(String str) {
        byte[] bArrCreateDngImage = this.mDngHelper.createDngImage(this.mIModuleCtrl.getJpegOrientation(), this.mIModuleCtrl.getLocation());
        if (bArrCreateDngImage != null) {
            this.mIFileSaver.saveRawFile(bArrCreateDngImage, this.mDngHelper.getRawWidth(), this.mDngHelper.getRawHeight(), str, this.mCaptureStartTime, this.mIModuleCtrl.getLocation(), 0, null);
        }
    }

    @Override // com.mediatek.camera.ICameraAddition.Listener
    public boolean restartPreview(boolean z) {
        Log.m31d("PhotoMode", "[restartPreview]needStop:" + z);
        this.mIsAutoFocusCallback = false;
        startPreview(z);
        this.mICameraAppUi.restoreViewState();
        this.mICameraAppUi.setSwipeEnabled(true);
        startFaceDetection();
        return true;
    }

    @Override // com.mediatek.camera.ICameraAddition.Listener
    public void onFileSaveing() {
        setModeState(ICameraMode.ModeState.STATE_SAVING);
    }

    protected void startPreview(boolean z) {
        if (!this.mIModuleCtrl.isFirstStartUp()) {
            this.mActivity.runOnUiThread(new Runnable() { // from class: com.mediatek.camera.mode.PhotoMode.8
                @Override // java.lang.Runnable
                public void run() {
                    if (PhotoMode.this.mIFocusManager != null) {
                        PhotoMode.this.mIFocusManager.resetTouchFocus();
                    }
                }
            });
            boolean z2 = "on".equals(this.mISettingCtrl.getSettingValue("pref_camera_zsd_key")) && ICameraMode.ModeState.STATE_CAPTURING == getModeState();
            Log.m31d("PhotoMode", "[startPreview] needStop:" + z + ",isZsdCapture = " + z2);
            if (z && (!z2)) {
                stopPreview();
            }
            this.mIFocusManager.setAeLock(false);
            this.mIFocusManager.setAwbLock(false);
            this.mIModuleCtrl.applyFocusParameters(false);
        }
        this.mICameraDevice.startPreview();
        this.mAdditionManager.execute(ICameraAddition.AdditionActionType.ACTION_ON_START_PREVIEW, new Object[0]);
        this.mIFocusManager.onPreviewStarted();
        setModeState(ICameraMode.ModeState.STATE_IDLE);
        if (!this.mIModuleCtrl.isFirstStartUp()) {
            this.mICameraDevice.setAutoFocusMoveCallback(this.mAutoFocusMoveCallback);
        }
        this.mICameraDevice.setUncompressedImageCallback(getUncompressedImageCallback());
    }

    protected void stopPreview() {
        Log.m31d("PhotoMode", "[stopPreview]mCurrentState = " + getModeState());
        this.mCapturedImageCount = 0;
        if (ICameraMode.ModeState.STATE_CLOSED == getModeState()) {
            Log.m31d("PhotoMode", "[stopPreview]Preview is stopped.");
            return;
        }
        if (this.mICameraDevice == null) {
            updateDevice();
        }
        stopFaceDetection();
        this.mAdditionManager.execute(ICameraAddition.AdditionActionType.ACTION_ON_STOP_PREVIEW, new Object[0]);
        this.mICameraDevice.cancelAutoFocus();
        this.mICameraDevice.setAutoFocusMoveCallback(null);
        this.mICameraDevice.stopPreview();
        this.mActivity.runOnUiThread(new Runnable() { // from class: com.mediatek.camera.mode.PhotoMode.9
            @Override // java.lang.Runnable
            public void run() {
                if (PhotoMode.this.mIFocusManager != null) {
                    PhotoMode.this.mIFocusManager.onPreviewStopped();
                }
            }
        });
    }

    protected Camera.PictureCallback getUncompressedImageCallback() {
        return this.mUncompressedImageCallback;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void doAttach() {
        FileOutputStream fileOutputStreamOpenFileOutput = null;
        outputStreamOpenOutputStream = null;
        outputStreamOpenOutputStream = null;
        OutputStream outputStreamOpenOutputStream = null;
        fileOutputStreamOpenFileOutput = null;
        fileOutputStreamOpenFileOutput = null;
        fileOutputStreamOpenFileOutput = null;
        Log.m31d("PhotoMode", "[doAttach] mCameraClosed:" + this.mCameraClosed);
        if (this.mCameraClosed) {
            return;
        }
        byte[] bArr = this.mJpegImageData;
        this.mIFileSaver.savePhotoFile(this.mJpegImageData, null, this.mCaptureStartTime, this.mIModuleCtrl.getLocation(), 0, null);
        Uri saveUri = this.mIModuleCtrl.getSaveUri();
        String cropValue = this.mIModuleCtrl.getCropValue();
        try {
            if (cropValue == null) {
                try {
                    if (saveUri == null) {
                        this.mIModuleCtrl.setResultAndFinish(-1, new Intent("inline-data").putExtra("data", Util.rotate(Util.makeBitmap(bArr, 51200), Exif.getOrientation(bArr))));
                        return;
                    } else {
                        outputStreamOpenOutputStream = this.mActivity.getContentResolver().openOutputStream(saveUri);
                        if (outputStreamOpenOutputStream != null) {
                            outputStreamOpenOutputStream.write(bArr);
                            outputStreamOpenOutputStream.close();
                        }
                        this.mIModuleCtrl.setResultAndFinish(-1);
                        return;
                    }
                } catch (IOException e) {
                    Log.m36w("PhotoMode", "IOException, when doAttach");
                    return;
                } finally {
                }
            }
            File fileStreamPath = this.mActivity.getFileStreamPath("crop-temp");
            fileStreamPath.delete();
            fileOutputStreamOpenFileOutput = this.mActivity.openFileOutput("crop-temp", 0);
            fileOutputStreamOpenFileOutput.write(bArr);
            fileOutputStreamOpenFileOutput.close();
            Uri uriFromFile = Uri.fromFile(fileStreamPath);
            Util.closeSilently(fileOutputStreamOpenFileOutput);
            Bundle bundle = new Bundle();
            if (cropValue.equals("circle")) {
                bundle.putString("circleCrop", "true");
            }
            if (saveUri != null) {
                bundle.putParcelable("output", saveUri);
            } else {
                bundle.putBoolean("return-data", true);
            }
            this.mIModuleCtrl.isSecureCamera();
            Intent intent = new Intent("com.android.camera.action.CROP");
            intent.setData(uriFromFile);
            intent.putExtras(bundle);
            this.mActivity.startActivityForResult(intent, 1000);
        } catch (FileNotFoundException e2) {
            this.mIModuleCtrl.setResultAndFinish(0);
        } catch (IOException e3) {
            this.mIModuleCtrl.setResultAndFinish(0);
        } finally {
        }
    }

    private void updateParameters() {
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
    }

    private final class MainHandler extends Handler {
        public MainHandler(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            Log.m31d("PhotoMode", "[handleMessage]msg id=" + message.what);
            switch (message.what) {
                case 101:
                    if (!PhotoMode.this.mCameraClosed) {
                        PhotoMode.this.restartPreview(true);
                        break;
                    }
                    break;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public class CameraCategory {
        protected CameraCategory() {
        }

        public void takePicture() {
            if (!PhotoMode.this.mAdditionManager.execute(ICameraAddition.AdditionActionType.ACTION_TAKEN_PICTURE, new Object[0])) {
                PhotoMode.this.mICameraDevice.takePicture(PhotoMode.this.mShutterCallback, null, PhotoMode.this.mPostViewCallback, PhotoMode.this.mJpegPictureCallback);
                PhotoMode.this.mICameraAppUi.setViewState(ICameraAppUi.ViewState.VIEW_STATE_CAPTURE);
            }
        }
    }

    private void onFaceDetected(int i) {
        this.mICameraAppUi.updateFaceBeatuyEntryViewVisible(isNeedShowFBEntry(i));
    }

    private boolean isNeedShowFBEntry(int i) {
        boolean zIsCfbEnable = false;
        if (this.mIModuleCtrl.isNonePickIntent() && this.mICameraAppUi.getViewState() == ICameraAppUi.ViewState.VIEW_STATE_NORMAL && this.mICameraContext.getFeatureConfig().isVfbEnable() && this.mISettingCtrl.getSettingValue("pref_face_beauty_multi_mode_key") != null && (!this.mActivity.getResources().getString(R.string.pref_face_beauty_mode_off).equals(this.mISettingCtrl.getSettingValue("pref_face_beauty_multi_mode_key"))) && (!"on".equals(this.mISettingCtrl.getSettingValue("pref_slow_motion_key"))) && (!"on".equals(this.mISettingCtrl.getSettingValue("pref_hdr_key"))) && i > 0 && "on".equals(this.mISettingCtrl.getSettingValue("pref_face_detect_key"))) {
            zIsCfbEnable = this.mICameraContext.getFeatureConfig().isCfbEnable();
        }
        Log.m31d("PhotoMode", "[isNeedShowFBEntry] faceLength = " + i + ",ViewState = " + this.mICameraAppUi.getViewState() + ",SlowMotion : " + this.mISettingCtrl.getSettingValue("pref_slow_motion_key") + ",hdr = " + this.mISettingCtrl.getSettingValue("pref_hdr_key") + ",FaceBeauty Setting: " + this.mISettingCtrl.getSettingValue("pref_face_beauty_multi_mode_key") + ",FD value = " + this.mISettingCtrl.getSettingValue("pref_face_detect_key") + ",isCFB supported = " + this.mICameraContext.getFeatureConfig().isCfbEnable() + ",isNeedShow = " + zIsCfbEnable);
        return zIsCfbEnable;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isCameraNotAvailable() {
        ICameraMode.ModeState modeState = getModeState();
        Log.m31d("PhotoMode", "isCameraNotAvailable modeState " + modeState);
        return ICameraMode.ModeState.STATE_CAPTURING == modeState || ICameraMode.ModeState.STATE_SAVING == modeState || ICameraMode.ModeState.STATE_CLOSED == modeState;
    }
}
