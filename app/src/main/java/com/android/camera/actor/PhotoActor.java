package com.android.camera.actor;

import android.content.Intent;
import android.hardware.Camera;
import android.media.MediaActionSound;
import android.os.Handler;
import android.os.Message;
import android.os.SystemProperties;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import com.android.camera.CameraActivity;
import com.android.camera.CameraErrorCallback;
import com.android.camera.FeatureSwitcher;
import com.android.camera.FocusManager;
import com.android.camera.Log;
import com.android.camera.SaveRequest;
import com.android.camera.Storage;
import com.android.camera.bridge.SelfTimerManager;
import com.android.camera.p001ui.ShutterButton;
import com.mediatek.camera.ICameraMode;
import com.mediatek.camera.ISettingCtrl;
import com.mediatek.camera.ModuleManager;
import com.mediatek.camera.R;
import com.mediatek.camera.platform.ICameraAppUi;
import com.mediatek.camera.setting.SettingUtils;
import java.util.List;

/* loaded from: classes.dex */
public class PhotoActor extends CameraActor implements FocusManager.Listener, ShutterButton.OnShutterButtonListener {
    private final AutoFocusCallback mAutoFocusCallback;
    private final AutoFocusMoveCallback mAutoFocusMoveCallback;
    private long mAutoFocusTime;
    private CameraActivity mCameraActivity;
    private CameraCategory mCameraCategory;
    private MediaActionSound mCameraSound;
    private View.OnClickListener mCancelListener;
    private int mCurrentMode;
    private Camera.FaceDetectionListener mFaceDetectionListener;
    private long mFocusStartTime;
    private final Handler mHandler;
    private final ICameraAppUi mICameraAppUi;
    private boolean mIsAutoFocusCallback;
    private boolean mIsCameraClosed;
    private boolean mIsCameraKeyLongPressed;
    private boolean mIsInitialized;
    private boolean mIsKeyHalfPressed;
    private boolean mIsReleaseActor;
    private boolean mIsSelftimerCounting;
    private boolean mIsSnapshotOnIdle;
    private boolean mIsZSDEnabled;
    private ModuleManager mModuleManager;
    private View.OnClickListener mOkListener;
    private CameraActivity.OnLongPressListener mOnLongPressListener;
    private CameraActivity.OnSingleTapUpListener mOnSingleTapListener;
    private View.OnClickListener mRetakeListener;
    private SaveRequest mSaveRequest;
    private SelfTimerManager.SelfTimerListener mSelfTimerListener;
    private SelfTimerManager mSelfTimerManager;

    public PhotoActor(CameraActivity cameraActivity, ModuleManager moduleManager, int i) {
        super(cameraActivity);
        this.mCurrentMode = 0;
        this.mIsInitialized = false;
        this.mIsCameraClosed = false;
        this.mIsSnapshotOnIdle = false;
        this.mIsSelftimerCounting = false;
        this.mIsAutoFocusCallback = false;
        this.mIsKeyHalfPressed = false;
        this.mIsCameraKeyLongPressed = false;
        this.mIsReleaseActor = false;
        this.mHandler = new MainHandler(this, null);
        this.mAutoFocusCallback = new AutoFocusCallback(this, 0 == true ? 1 : 0);
        this.mAutoFocusMoveCallback = new AutoFocusMoveCallback(this, 0 == true ? 1 : 0);
        this.mOkListener = new View.OnClickListener() { // from class: com.android.camera.actor.PhotoActor.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                PhotoActor.this.mModuleManager.onOkButtonPress();
            }
        };
        this.mCancelListener = new View.OnClickListener() { // from class: com.android.camera.actor.PhotoActor.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (PhotoActor.this.mModuleManager.onCancelButtonPress()) {
                    return;
                }
                PhotoActor.this.doCancel();
            }
        };
        this.mRetakeListener = new View.OnClickListener() { // from class: com.android.camera.actor.PhotoActor.3
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (PhotoActor.this.mIsCameraClosed) {
                    return;
                }
                PhotoActor.this.mICameraAppUi.hideReview();
                PhotoActor.this.mICameraAppUi.switchShutterType(ICameraAppUi.ShutterButtonType.SHUTTER_TYPE_PHOTO);
                PhotoActor.this.restartPreview(true);
            }
        };
        this.mOnSingleTapListener = new CameraActivity.OnSingleTapUpListener() { // from class: com.android.camera.actor.PhotoActor.4
            @Override // com.android.camera.CameraActivity.OnSingleTapUpListener
            public void onSingleTapUp(View view, int i2, int i3) {
                if (PhotoActor.this.mIsCameraClosed || ICameraAppUi.ViewState.VIEW_STATE_CAMERA_CLOSED == PhotoActor.this.mICameraAppUi.getViewState()) {
                    Log.m11w("PhotoActor", "[mOnSingleTapListener]mIsCameraClosed is true,return.");
                    return;
                }
                if (PhotoActor.this.mSelfTimerManager.isSelfTimerCounting()) {
                    Log.m11w("PhotoActor", "[mOnSingleTapListener] self timer is counting,return.");
                    return;
                }
                FocusManager focusManager = PhotoActor.this.mCameraActivity.getFocusManager();
                if (focusManager == null) {
                    return;
                }
                if (PhotoActor.this.mModuleManager.onSingleTapUp(view, i2, i3)) {
                    Log.m5d("PhotoActor", "[onSingleTapUp] module manager has handled it,return.");
                } else {
                    focusManager.onSingleTapUp(i2, i3);
                }
            }
        };
        this.mOnLongPressListener = new CameraActivity.OnLongPressListener() { // from class: com.android.camera.actor.PhotoActor.5
            @Override // com.android.camera.CameraActivity.OnLongPressListener
            public void onLongPress(View view, int i2, int i3) {
                if (PhotoActor.this.mIsCameraClosed) {
                    Log.m11w("PhotoActor", "[mOnLongPressListener]mIsCameraClosed is true,return.");
                } else if (PhotoActor.this.mSelfTimerManager.isSelfTimerCounting()) {
                    Log.m11w("PhotoActor", "[mOnLongPressListener] self timer is counting,return.");
                } else {
                    PhotoActor.this.mModuleManager.onLongPress(view, i2, i3);
                }
            }
        };
        this.mFaceDetectionListener = new Camera.FaceDetectionListener() { // from class: com.android.camera.actor.PhotoActor.6
            @Override // android.hardware.Camera.FaceDetectionListener
            public void onFaceDetection(Camera.Face[] faceArr, Camera camera) {
                boolean z = false;
                boolean zIsFaceDetectionRunning = PhotoActor.this.mCameraActivity.getCameraDevice().isFaceDetectionRunning();
                if (FeatureSwitcher.isVfbEnable() && PhotoActor.this.mCameraActivity.getCurrentMode() == 2) {
                    z = true;
                }
                if (!zIsFaceDetectionRunning && (!z)) {
                    return;
                }
                if (PhotoActor.this.mCameraActivity.getCameraDevice() != null) {
                    if (faceArr != null && faceArr.length > 0) {
                        PhotoActor.this.mCameraActivity.getFocusManager().clearFocusUi();
                    }
                    PhotoActor.this.mCameraActivity.getFrameView().setFaces(faceArr);
                }
                PhotoActor.this.mModuleManager.onFaceDetected(faceArr);
            }
        };
        this.mSelfTimerListener = new SelfTimerManager.SelfTimerListener() { // from class: com.android.camera.actor.PhotoActor.7
            @Override // com.android.camera.bridge.SelfTimerManager.SelfTimerListener
            public void onTimerStart() {
                PhotoActor.this.mModuleManager.onSelfTimerState(true);
            }

            @Override // com.android.camera.bridge.SelfTimerManager.SelfTimerListener
            public void onTimerStop() {
            }

            @Override // com.android.camera.bridge.SelfTimerManager.SelfTimerListener
            public void onTimerTimeout() {
                Log.m5d("PhotoActor", "[onTimerTimeout]");
                PhotoActor.this.onShutterButtonClick(null);
                PhotoActor.this.mModuleManager.onSelfTimerState(false);
            }
        };
        this.mCameraActivity = cameraActivity;
        this.mCameraCategory = new CameraCategory(this, 0 == true ? 1 : 0);
        this.mICameraAppUi = cameraActivity.getCameraAppUI();
        if (this.mCameraActivity.isImageCaptureIntent()) {
            this.mICameraAppUi.switchShutterType(ICameraAppUi.ShutterButtonType.SHUTTER_TYPE_PHOTO);
        } else {
            this.mICameraAppUi.switchShutterType(ICameraAppUi.ShutterButtonType.SHUTTER_TYPE_PHOTO_VIDEO);
        }
        this.mCameraSound = new MediaActionSound();
        this.mCameraSound.load(1);
        this.mModuleManager = moduleManager;
        prepareCurrentMode(i);
        this.mICameraAppUi.setReviewListener(this.mRetakeListener, null);
        this.mSelfTimerManager = (SelfTimerManager) this.mContext.getSelfTimeManager();
    }

    @Override // com.android.camera.actor.CameraActor
    public void onCameraParameterReady(boolean z) {
        super.onCameraParameterReady(z);
        Log.m5d("PhotoActor", "[onCameraParameterReady]startPreview = " + z);
        this.mModuleManager.onCameraParameterReady(z);
        if (z && !this.mModuleManager.startPreview(true)) {
            startPreview(true);
        }
        if (this.mCameraActivity.getISettingCtrl() != null && this.mCameraActivity.getISettingCtrl().getSettingValue("pref_camera_self_timer_key") != null) {
            this.mSelfTimerManager.setSelfTimerDuration(this.mCameraActivity.getISettingCtrl().getSettingValue("pref_camera_self_timer_key"));
        }
        this.mCameraActivity.setCameraState(1);
        this.mHandler.removeMessages(102);
        this.mHandler.sendEmptyMessage(102);
    }

    @Override // com.android.camera.actor.CameraActor
    public void stopPreview() {
        Log.m5d("PhotoActor", "[stopPreview] getCameraState()=" + this.mCameraActivity.getCameraState());
        if (this.mCameraActivity.getCameraState() != 0) {
            this.mIsZSDEnabled = "on".equals(this.mCameraActivity.getISettingCtrl().getSettingValue("pref_camera_zsd_key"));
            this.mCameraActivity.setCameraState(0);
            if (this.mModuleManager.stopPreview()) {
                return;
            }
            if (this.mCameraActivity.getCameraDevice() != null) {
                this.mCameraActivity.getCameraDevice().cancelAutoFocus();
                this.mCameraActivity.getCameraDevice().stopPreview();
                if (this.mModuleManager.getModeState() == ICameraMode.ModeState.STATE_IDLE) {
                    this.mICameraAppUi.restoreViewState();
                }
            }
            this.mCameraActivity.runOnUiThread(new Runnable() { // from class: com.android.camera.actor.PhotoActor.8
                @Override // java.lang.Runnable
                public void run() {
                    if (PhotoActor.this.mCameraActivity.getFocusManager() != null) {
                        PhotoActor.this.mCameraActivity.getFocusManager().onPreviewStopped();
                    }
                }
            });
        }
    }

    @Override // com.android.camera.ui.ShutterButton.OnShutterButtonListener
    public void onShutterButtonLongPressed(ShutterButton shutterButton) {
        int cameraState = this.mCameraActivity.getCameraState();
        Log.m5d("PhotoActor", "[onShutterButtonLongPressed] current state = " + cameraState);
        if (4 != cameraState && cameraState != 0 && !this.mModuleManager.onShutterButtonLongPressed() && this.mCameraActivity.isImageCaptureIntent()) {
            this.mICameraAppUi.showInfo(this.mCameraActivity.getString(R.string.normal_camera_continuous_not_supported));
        }
    }

    @Override // com.android.camera.ui.ShutterButton.OnShutterButtonListener
    public void onShutterButtonFocus(ShutterButton shutterButton, boolean z) {
        Log.m5d("PhotoActor", "[onShutterButtonFocus]pressed = " + z + ",mIsCameraClosed = " + this.mIsCameraClosed);
        this.mICameraAppUi.collapseSetting(true);
        if (FeatureSwitcher.isSubSettingEnabled()) {
            this.mICameraAppUi.collapseSubSetting(true);
        }
        if (z && isCameraReady()) {
            this.mModuleManager.onShutterButtonFocus(true);
        } else if (!z && (!this.mIsCameraClosed)) {
            this.mModuleManager.onShutterButtonFocus(false);
        }
    }

    @Override // com.android.camera.ui.ShutterButton.OnShutterButtonListener
    public void onShutterButtonClick(ShutterButton shutterButton) {
        int cameraState = this.mCameraActivity.getCameraState();
        ICameraMode.ModeState modeState = this.mModuleManager.getModeState();
        ICameraAppUi.ViewState viewState = this.mICameraAppUi.getViewState();
        Log.m5d("PhotoActor", "[onShutterButtonClick] cameraState = " + cameraState + ", modeState = " + modeState + ", currentViewState = " + viewState);
        if (ICameraAppUi.ViewState.VIEW_STATE_LOMOEFFECT_SETTING == viewState || ICameraAppUi.ViewState.VIEW_STATE_SAVING == viewState || ICameraAppUi.ViewState.VIEW_STATE_CONTINUOUS_CAPTURE == viewState || ICameraAppUi.ViewState.VIEW_STATE_CAMERA_CLOSED == viewState || ICameraAppUi.ViewState.VIEW_STATE_REVIEW == viewState || ICameraMode.ModeState.STATE_CAPTURING == modeState) {
            return;
        }
        if (this.mICameraAppUi.updateRemainStorage() > 0) {
            if (4 != cameraState && cameraState != 0) {
                if (this.mSelfTimerManager.startSelfTimer()) {
                    this.mModuleManager.onSelfTimerState(true);
                    this.mICameraAppUi.setSwipeEnabled(false);
                    this.mICameraAppUi.setViewState(ICameraAppUi.ViewState.VIEW_STATE_CAPTURE);
                    this.mIsSelftimerCounting = true;
                    return;
                }
                this.mIsSelftimerCounting = false;
                this.mModuleManager.onPhotoShutterButtonClick();
                return;
            }
            return;
        }
        Log.m5d("PhotoActor", "remain storage is less than 0");
        this.mICameraAppUi.showRemaining();
    }

    @Override // com.android.camera.FocusManager.Listener
    public void autoFocus() {
        Log.m5d("PhotoActor", "[autoFocus]...");
        if (this.mCameraActivity.getCameraDevice() == null) {
            Log.m6e("PhotoActor", "[autoFocus]device is null,return!");
            return;
        }
        this.mFocusStartTime = System.currentTimeMillis();
        this.mCameraActivity.getCameraDevice().autoFocus(this.mAutoFocusCallback);
        this.mCameraActivity.setCameraState(2);
        this.mICameraAppUi.setViewState(ICameraAppUi.ViewState.VIEW_STATE_FOCUSING);
    }

    @Override // com.android.camera.FocusManager.Listener
    public void cancelAutoFocus() {
        Log.m5d("PhotoActor", "[cancelAutoFocus] mode state:" + this.mModuleManager.getModeState());
        if (this.mCameraActivity.getCameraDevice() == null || ICameraMode.ModeState.STATE_CLOSED == this.mModuleManager.getModeState()) {
            Log.m6e("PhotoActor", "[cancelAutoFocus]device is null,return!");
            return;
        }
        if (!this.mIsAutoFocusCallback) {
            this.mCameraActivity.getCameraDevice().cancelAutoFocus();
            this.mIsAutoFocusCallback = true;
        }
        if (!this.mIsSelftimerCounting && this.mCameraActivity.getCameraState() != 0) {
            this.mCameraActivity.setCameraState(1);
            if (this.mModuleManager.getModeState() == ICameraMode.ModeState.STATE_IDLE) {
                this.mICameraAppUi.restoreViewState();
            }
        }
        setFocusParameters();
    }

    @Override // com.android.camera.FocusManager.Listener
    public void playSound(int i) {
        Log.m5d("PhotoActor", "[playSound]soundId =" + i);
        this.mCameraSound.play(i);
    }

    @Override // com.android.camera.FocusManager.Listener
    public void setFocusParameters() {
        Log.m5d("PhotoActor", "[setFocusParameters]sIsAutoFocusCallback =" + this.mIsAutoFocusCallback);
        this.mCameraActivity.applyParameterForFocus(!this.mIsAutoFocusCallback);
        this.mIsAutoFocusCallback = false;
    }

    @Override // com.android.camera.actor.CameraActor
    public void release() {
        Log.m8i("PhotoActor", "[release]...");
        this.mIsReleaseActor = true;
        this.mHandler.removeMessages(102);
        if (this.mCameraSound != null) {
            this.mCameraSound.release();
            this.mCameraSound = null;
        }
        resetPhotoActor();
        this.mCameraCategory.onLeaveActor();
        this.mModuleManager.closeMode();
    }

    @Override // com.android.camera.actor.CameraActor
    public void onRestoreSettings() {
        this.mModuleManager.onRestoreSettings();
    }

    @Override // com.android.camera.actor.CameraActor
    public int getMode() {
        return this.mCurrentMode;
    }

    @Override // com.android.camera.actor.CameraActor
    public ShutterButton.OnShutterButtonListener getPhotoShutterButtonListener() {
        return this;
    }

    @Override // com.android.camera.actor.CameraActor
    public Camera.FaceDetectionListener getFaceDetectionListener() {
        return this.mFaceDetectionListener;
    }

    @Override // com.android.camera.actor.CameraActor
    public View.OnClickListener getOkListener() {
        return this.mOkListener;
    }

    @Override // com.android.camera.actor.CameraActor
    public View.OnClickListener getCancelListener() {
        return this.mCancelListener;
    }

    @Override // com.android.camera.actor.CameraActor
    public Camera.ErrorCallback getErrorCallback() {
        return new CameraErrorCallback(this.mCameraActivity);
    }

    @Override // com.android.camera.actor.CameraActor
    public CameraActivity.OnSingleTapUpListener getonSingleTapUpListener() {
        return this.mOnSingleTapListener;
    }

    @Override // com.android.camera.actor.CameraActor
    public CameraActivity.OnLongPressListener getonLongPressListener() {
        return this.mOnLongPressListener;
    }

    @Override // com.android.camera.actor.CameraActor
    public void onCameraOpenDone() {
        this.mIsCameraClosed = false;
    }

    @Override // com.android.camera.actor.CameraActor
    public boolean onBackPressed() {
        Log.m5d("PhotoActor", "[onBackPressed] isCameraIdle =" + isCameraIdle() + ",CameraState:" + this.mContext.getCameraState() + ",mIsSelftimerCounting = " + this.mIsSelftimerCounting);
        if (isCameraIdle()) {
            return this.mModuleManager.onBackPressed();
        }
        if (this.mIsSelftimerCounting) {
            this.mSelfTimerManager.stopSelfTimer();
            this.mIsSelftimerCounting = false;
            this.mCameraActivity.setCameraState(1);
            this.mModuleManager.onSelfTimerState(false);
            this.mICameraAppUi.setSwipeEnabled(true);
            this.mICameraAppUi.restoreViewState();
        }
        if (this.mCameraActivity.isImageCaptureIntent() && this.mICameraAppUi.getShutterType() == ICameraAppUi.ShutterButtonType.SHUTTER_TYPE_OK_CANCEL) {
            this.mCancelListener.onClick(null);
        }
        return this.mContext.getCameraState() != 2;
    }

    @Override // com.android.camera.actor.CameraActor
    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        Log.m5d("PhotoActor", "[onKeyDown] keyCode = " + i);
        switch (i) {
            case 23:
            case 66:
                this.mICameraAppUi.collapseViewManager(true);
                if (this.mIsInitialized && keyEvent.getRepeatCount() == 0) {
                    onShutterButtonFocus(null, true);
                    ImageView photoShutter = this.mICameraAppUi.getPhotoShutter();
                    if (photoShutter != null) {
                        if (photoShutter.isInTouchMode()) {
                            photoShutter.requestFocusFromTouch();
                        } else {
                            photoShutter.requestFocus();
                        }
                        photoShutter.setPressed(true);
                    }
                }
                return true;
            case 27:
                return true;
            case 80:
                if (this.mIsInitialized && this.mCameraActivity.isFullScreen() && keyEvent.getRepeatCount() == 0) {
                    if (!canTakePicture() || (!this.mModuleManager.canDoAutoFocus())) {
                        Log.m11w("PhotoActor", "[onKeyDown]Do not do focus if there is not enough storage,return!");
                        return true;
                    }
                    this.mICameraAppUi.collapseViewManager(true);
                    this.mIsKeyHalfPressed = true;
                    this.mCameraActivity.getFocusManager().onShutterDown();
                }
                return true;
            default:
                return false;
        }
    }

    @Override // com.android.camera.actor.CameraActor
    public boolean onKeyUp(int i, KeyEvent keyEvent) {
        Log.m5d("PhotoActor", "[onKeyUp]keyCode = " + i);
        switch (i) {
            case 27:
                if (this.mIsInitialized && (!this.mIsCameraKeyLongPressed) && keyEvent.getRepeatCount() == 0 && this.mCameraActivity.isFullScreen()) {
                    if (this.mCameraActivity.getOrietation() == -1) {
                        Log.m11w("PhotoActor", "[onKeyUp]getOrietation is ORIENTATION_UNKNOWN,Delay capturing action to make sure orientation is in correct state.return! ");
                        return false;
                    }
                    if (this.mSaveRequest != null && this.mSaveRequest.isQueueFull()) {
                        Log.m11w("PhotoActor", "[onKeyUp]not response camera physical key, when numbers of saveTask over 3,.return! ");
                        return false;
                    }
                    onShutterButtonClick(null);
                }
                this.mIsCameraKeyLongPressed = false;
                return true;
            case 80:
                if (this.mIsInitialized) {
                    onShutterButtonFocus(null, false);
                    this.mIsKeyHalfPressed = false;
                    this.mCameraActivity.getFocusManager().onShutterUp();
                }
                return true;
            default:
                return false;
        }
    }

    @Override // com.android.camera.actor.CameraActor
    public boolean handleFocus() {
        if (!this.mCameraActivity.isFullScreen()) {
            Log.m5d("PhotoActor", "[handleFocus] is not full screen.");
            return false;
        }
        Log.m5d("PhotoActor", "[handleFocus]mKeyHalfPressed = " + this.mIsKeyHalfPressed);
        if (this.mIsKeyHalfPressed) {
            overrideFocusMode("auto");
            return true;
        }
        overrideFocusMode(null);
        return false;
    }

    @Override // com.android.camera.actor.CameraActor, com.android.camera.FocusManager.Listener
    public void startFaceDetection() {
        Log.m5d("PhotoActor", "[startFaceDetection]");
        ICameraMode.ModeState modeState = this.mModuleManager.getModeState();
        if (ICameraMode.ModeState.STATE_CLOSED == modeState || ICameraMode.ModeState.STATE_FOCUSING == modeState || (!isSupportFaceDetect())) {
            Log.m11w("PhotoActor", "[startFaceDetection]Don't support FD detection");
            return;
        }
        if (this.mCameraActivity.getCameraDevice() != null && this.mCameraActivity.getParameters().getMaxNumDetectedFaces() > 0) {
            this.mCameraActivity.runOnUiThread(new Runnable() { // from class: com.android.camera.actor.PhotoActor.9
                @Override // java.lang.Runnable
                public void run() {
                    PhotoActor.this.mCameraActivity.getFrameManager().initializeFrameView(false);
                }
            });
            this.mCameraActivity.getCameraDevice().setFaceDetectionListener(this.mFaceDetectionListener);
            if (FeatureSwitcher.isVfbEnable() && this.mCameraActivity.getCurrentMode() == 2) {
                Log.m5d("PhotoActor", "[vFB]current is in VFB mode,not need startFD, however it still need set face detection listener.");
            } else {
                this.mCameraActivity.getCameraDevice().startFaceDetection();
            }
        }
    }

    @Override // com.android.camera.actor.CameraActor, com.android.camera.FocusManager.Listener
    public void stopFaceDetection() {
        if (this.mCameraActivity.getCameraDevice() != null && this.mCameraActivity.getParameters().getMaxNumDetectedFaces() > 0) {
            Log.m5d("PhotoActor", "[stopFaceDetection]will call stopFaceDetection ");
            this.mCameraActivity.getCameraDevice().setFaceDetectionListener(null);
            this.mCameraActivity.getCameraDevice().stopFaceDetection();
            this.mCameraActivity.runOnUiThread(new Runnable() { // from class: com.android.camera.actor.PhotoActor.10
                @Override // java.lang.Runnable
                public void run() {
                    if (PhotoActor.this.mCameraActivity.getFrameView() != null) {
                        PhotoActor.this.mCameraActivity.getFrameView().clear();
                    }
                }
            });
        }
    }

    @Override // com.android.camera.FocusManager.Listener
    public boolean capture() {
        return false;
    }

    @Override // com.android.camera.FocusManager.Listener
    public boolean readyToCapture() {
        return false;
    }

    @Override // com.android.camera.actor.CameraActor
    public boolean onUserInteraction() {
        this.mCameraActivity.keepScreenOnAwhile();
        return true;
    }

    @Override // com.android.camera.actor.CameraActor
    public FocusManager.Listener getFocusManagerListener() {
        return this;
    }

    @Override // com.android.camera.actor.CameraActor
    public void onCameraClose() {
        Log.m5d("PhotoActor", "[onCameraClose]mCameraClosed =" + this.mIsCameraClosed + ", SelfTimerManagerIsCounting = " + this.mSelfTimerManager.isSelfTimerCounting());
        this.mIsCameraClosed = true;
        if (this.mSelfTimerManager.isSelfTimerCounting()) {
            this.mSelfTimerManager.stopSelfTimer();
            this.mICameraAppUi.setSwipeEnabled(true);
            this.mICameraAppUi.setViewState(ICameraAppUi.ViewState.VIEW_STATE_NORMAL);
            this.mIsSelftimerCounting = false;
        }
        this.mHandler.removeMessages(103);
        this.mHandler.removeMessages(102);
        this.mModuleManager.onCameraClose();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void initializeAfterPreview() {
        Log.m5d("PhotoActor", "[initializeAfterPreview]...mSelfTimerManager = " + this.mSelfTimerManager);
        if (this.mIsCameraClosed || this.mCameraActivity.getCameraDevice() == null) {
            Log.m11w("PhotoActor", "[initializeAfterPreview mCamera]mCameraClosed= " + this.mIsCameraClosed);
            return;
        }
        this.mIsAutoFocusCallback = false;
        this.mCameraActivity.keepScreenOnAwhile();
        if (ICameraMode.ModeState.STATE_CAPTURING != this.mModuleManager.getModeState() && ICameraMode.ModeState.STATE_RECORDING != this.mModuleManager.getModeState() && this.mCurrentMode != 7) {
            this.mCameraCategory.switchShutterButton();
        }
        this.mSelfTimerManager.setTimerListener(this.mSelfTimerListener);
        if (isSupportFaceDetect()) {
            startFaceDetection();
        } else {
            stopFaceDetection();
        }
        if (this.mIsInitialized) {
            Log.m5d("PhotoActor", "[initializeAfterPreview mCamera]has initialized.");
        } else {
            this.mCameraActivity.getFrameManager().initializeFrameView(false);
            this.mIsInitialized = true;
        }
    }

    private void startPreview(boolean z) {
        Log.m5d("PhotoActor", "[startPreview]needStop = " + z);
        this.mCameraActivity.runOnUiThread(new Runnable() { // from class: com.android.camera.actor.PhotoActor.11
            @Override // java.lang.Runnable
            public void run() {
                PhotoActor.this.mCameraActivity.getFocusManager().resetTouchFocus();
            }
        });
        if (z) {
            stopPreview();
        }
        if (!this.mIsSnapshotOnIdle) {
            if ("continuous-picture".equals(this.mCameraActivity.getFocusManager().getFocusMode())) {
                this.mCameraActivity.getCameraDevice().cancelAutoFocus();
                this.mCameraActivity.getCameraDevice().setAutoFocusMoveCallback(this.mAutoFocusMoveCallback);
            }
            this.mCameraActivity.getFocusManager().setAeLock(false);
            this.mCameraActivity.getFocusManager().setAwbLock(false);
        }
        if (isPowerDebug()) {
            if (SettingUtils.isSupported("infinity", this.mCameraActivity.getParameters().getSupportedFocusModes())) {
                overrideFocusMode("infinity");
                this.mCameraActivity.getParameters().setFocusMode(this.mCameraActivity.getFocusManager().getFocusMode());
                Log.m5d("PhotoActor", "set debug focus     FOCUS_MODE_INFINITY ");
            }
        } else {
            setFocusParameters();
            Log.m5d("PhotoActor", "[startPreview]set setFocusParameters normal");
        }
        this.mCameraActivity.getCameraDevice().startPreviewAsync();
        this.mCameraActivity.getFocusManager().onPreviewStarted();
    }

    private void prepareCurrentMode(int i) {
        Log.m5d("PhotoActor", "[prepareCurrentMode] mCurrentMode:" + this.mCurrentMode + ",newMode:" + i);
        this.mCurrentMode = i;
        ICameraMode.CameraModeType cameraModeType = getCameraModeType(this.mCurrentMode);
        if (cameraModeType == null) {
            cameraModeType = ICameraMode.CameraModeType.EXT_MODE_PHOTO;
        }
        this.mModuleManager.createMode(cameraModeType);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void restartPreview(boolean z) {
        Log.m5d("PhotoActor", "[restartPreview]needStop = " + z);
        if (!this.mModuleManager.startPreview(z)) {
            startPreview(z);
        }
        this.mCameraActivity.setCameraState(1);
        this.mICameraAppUi.restoreViewState();
        startFaceDetection();
    }

    private void overrideFocusMode(String str) {
        Log.m5d("PhotoActor", "[overrideFocusMode]focusMode = " + str);
        if (str == null || this.mCameraActivity.getParameters() == null) {
            return;
        }
        List<String> supportedFocusModes = this.mCameraActivity.getParameters().getSupportedFocusModes();
        if (!SettingUtils.isSupported(str, supportedFocusModes)) {
            str = getNotSupportedFocusMode(supportedFocusModes);
        }
        if (!this.mIsCameraClosed && this.mCameraActivity.getFocusManager() != null) {
            this.mCameraActivity.getFocusManager().overrideFocusMode(str);
        }
    }

    private String getNotSupportedFocusMode(List<String> list) {
        if (SettingUtils.isSupported("infinity", list)) {
            return "infinity";
        }
        if (SettingUtils.isSupported("fixed", list)) {
            return "fixed";
        }
        return null;
    }

    private void resetPhotoActor() {
        this.mIsAutoFocusCallback = false;
        this.mICameraAppUi.dismissInfo();
    }

    private class CameraCategory {
        /* synthetic */ CameraCategory(PhotoActor photoActor, CameraCategory cameraCategory) {
            this();
        }

        private CameraCategory() {
        }

        public void switchShutterButton() {
            ISettingCtrl settingController = PhotoActor.this.mModuleManager.getSettingController();
            boolean zEquals = false;
            if (settingController != null) {
                zEquals = "on".equals(settingController.getSettingValue("pref_slow_motion_key"));
            }
            if (PhotoActor.this.mCameraActivity.isImageCaptureIntent()) {
                PhotoActor.this.mICameraAppUi.switchShutterType(ICameraAppUi.ShutterButtonType.SHUTTER_TYPE_PHOTO);
            } else if (zEquals) {
                PhotoActor.this.mICameraAppUi.switchShutterType(ICameraAppUi.ShutterButtonType.SHUTTER_TYPE_SLOW_VIDEO);
            } else {
                PhotoActor.this.mICameraAppUi.switchShutterType(ICameraAppUi.ShutterButtonType.SHUTTER_TYPE_PHOTO_VIDEO);
                PhotoActor.this.mICameraAppUi.getCameraView(ICameraAppUi.CommonUiType.SHUTTER).refresh();
            }
        }

        public boolean canshot() {
            return 1 <= Storage.getLeftSpace();
        }

        public void onLeaveActor() {
            Log.m5d("PhotoActor", "[onLeaveActor]");
            if (PhotoActor.this.mIsCameraClosed && PhotoActor.this.mCameraActivity.getFocusManager() != null && "auto".equals(PhotoActor.this.mCameraActivity.getFocusManager().getCurrentFocusMode(PhotoActor.this.mContext)) && PhotoActor.this.mCameraActivity.getCameraDevice() != null) {
                Log.m5d("PhotoActor", "[onLeaveActor]will cancel auto focus.");
                PhotoActor.this.mCameraActivity.getCameraDevice().cancelAutoFocus();
            }
            PhotoActor.this.mICameraAppUi.restoreViewState();
            if (PhotoActor.this.mCameraActivity.isImageCaptureIntent()) {
                PhotoActor.this.mICameraAppUi.hideReview();
                PhotoActor.this.mICameraAppUi.switchShutterType(ICameraAppUi.ShutterButtonType.SHUTTER_TYPE_PHOTO);
            }
        }
    }

    private boolean isPowerDebug() {
        return SystemProperties.getInt("camera_af_power_debug", 0) == 1;
    }

    private final class AutoFocusCallback implements Camera.AutoFocusCallback {
        /* synthetic */ AutoFocusCallback(PhotoActor photoActor, AutoFocusCallback autoFocusCallback) {
            this();
        }

        private AutoFocusCallback() {
        }

        @Override // android.hardware.Camera.AutoFocusCallback
        public void onAutoFocus(boolean z, Camera camera) {
            if (PhotoActor.this.mIsCameraClosed) {
                return;
            }
            PhotoActor.this.mAutoFocusTime = System.currentTimeMillis() - PhotoActor.this.mFocusStartTime;
            Log.m5d("PhotoActor", "mAutoFocusTime = " + PhotoActor.this.mAutoFocusTime + "ms,cameraState = " + PhotoActor.this.mCameraActivity.getCameraState());
            if (!PhotoActor.this.mIsSelftimerCounting && PhotoActor.this.mCameraActivity.getCameraState() == 2) {
                PhotoActor.this.mCameraActivity.setCameraState(1);
            }
            PhotoActor.this.mCameraActivity.getFocusManager().onAutoFocus(z);
            PhotoActor.this.mIsAutoFocusCallback = true;
        }
    }

    private final class AutoFocusMoveCallback implements Camera.AutoFocusMoveCallback {
        /* synthetic */ AutoFocusMoveCallback(PhotoActor photoActor, AutoFocusMoveCallback autoFocusMoveCallback) {
            this();
        }

        private AutoFocusMoveCallback() {
        }

        @Override // android.hardware.Camera.AutoFocusMoveCallback
        public void onAutoFocusMoving(boolean z, Camera camera) {
            Log.m5d("PhotoActor", "[onAutoFocusMoving]moving = " + z);
            PhotoActor.this.mCameraActivity.getFocusManager().onAutoFocusMoving(z);
        }
    }

    private boolean canTakePicture() {
        if (isCameraIdle()) {
            return this.mCameraCategory.canshot();
        }
        return false;
    }

    private boolean isCameraIdle() {
        if (this.mIsSelftimerCounting) {
            return false;
        }
        if (this.mCameraActivity.getCameraState() == 1) {
            return true;
        }
        if (this.mCameraActivity.getFocusManager() == null || !this.mCameraActivity.getFocusManager().isFocusCompleted()) {
            return false;
        }
        return this.mCameraActivity.getCameraState() != 4;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void doCancel() {
        this.mCameraActivity.setResultExAndFinish(0, new Intent());
    }

    private boolean isSupportFaceDetect() {
        String settingValue = this.mModuleManager.getSettingController().getSettingValue("pref_face_detect_key");
        Log.m5d("PhotoActor", "[isSupportFaceDetect]faceDetection =" + settingValue);
        return "on".equals(settingValue);
    }

    private class MainHandler extends Handler {
        /* synthetic */ MainHandler(PhotoActor photoActor, MainHandler mainHandler) {
            this();
        }

        private MainHandler() {
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            Log.m5d("PhotoActor", "[handleMessage]msg.what =" + message.what + ",mIsReleaseActor = " + PhotoActor.this.mIsReleaseActor + ",mIsCameraClosed = " + PhotoActor.this.mIsCameraClosed);
            switch (message.what) {
                case 102:
                    if (!PhotoActor.this.mIsReleaseActor) {
                        PhotoActor.this.initializeAfterPreview();
                        break;
                    }
                    break;
                case 103:
                    if (!PhotoActor.this.mIsCameraClosed) {
                        PhotoActor.this.restartPreview(true);
                        break;
                    }
                    break;
            }
        }
    }

    private boolean isCameraReady() {
        boolean zIsSelfTimerEnabled = true;
        int cameraState = this.mCameraActivity.getCameraState();
        if (!this.mIsCameraClosed && 4 != cameraState && cameraState != 0) {
            zIsSelfTimerEnabled = this.mSelfTimerManager.isSelfTimerEnabled();
        }
        boolean z = !zIsSelfTimerEnabled;
        Log.m5d("PhotoActor", "[isCameraReady] cameraState = " + cameraState + ", isSelfTimerEnalbe = " + this.mSelfTimerManager.isSelfTimerEnabled() + ",isCameraReady = " + z);
        return z;
    }
}
