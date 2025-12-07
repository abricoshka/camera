package com.mediatek.camera.mode.pip;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.location.Location;
import android.os.ConditionVariable;
import android.os.SystemClock;
import android.support.v4.app.FrameMetricsAggregator;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import com.android.camera.Storage;
import com.mediatek.camera.ICameraAddition;
import com.mediatek.camera.ICameraContext;
import com.mediatek.camera.ICameraMode;
import com.mediatek.camera.ISettingCtrl;
import com.mediatek.camera.ISettingRule;
import com.mediatek.camera.R;
import com.mediatek.camera.mode.VideoMode;
import com.mediatek.camera.mode.pip.PipController;
import com.mediatek.camera.mode.pip.recorder.MediaRecorderWrapper;
import com.mediatek.camera.platform.ICameraAppUi;
import com.mediatek.camera.platform.ICameraDeviceManager;
import com.mediatek.camera.platform.IFileSaver;
import com.mediatek.camera.platform.IFocusManager;
import com.mediatek.camera.platform.Parameters;
import com.mediatek.camera.setting.preference.ListPreference;
import com.mediatek.camera.util.Log;
import com.mediatek.camera.util.Util;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class PipVideoMode extends VideoMode implements MediaRecorderWrapper.OnInfoListener, PipController.Listener, ICameraAppUi.GestureListener {

    /* renamed from: -com-mediatek-camera-ICameraMode$ActionTypeSwitchesValues, reason: not valid java name */
    private static final /* synthetic */ int[] f107commediatekcameraICameraMode$ActionTypeSwitchesValues = null;
    private static final Long VIDEO_4G_SIZE = 4294967296L;
    private boolean mIsRecordingStarted;
    private View.OnClickListener mPIPVideoPauseResumeListner;
    private PipController mPipController;
    private MediaRecorderWrapper mRecorder;
    private ConditionVariable mStopConditionVariableSync;
    private boolean mStopVideoRecording;

    /* renamed from: -getcom-mediatek-camera-ICameraMode$ActionTypeSwitchesValues, reason: not valid java name */
    private static /* synthetic */ int[] m855getcommediatekcameraICameraMode$ActionTypeSwitchesValues() {
        if (f107commediatekcameraICameraMode$ActionTypeSwitchesValues != null) {
            return f107commediatekcameraICameraMode$ActionTypeSwitchesValues;
        }
        int[] iArr = new int[ICameraMode.ActionType.valuesCustom().length];
        try {
            iArr[ICameraMode.ActionType.ACTION_CANCEL_BUTTON_CLICK.ordinal()] = 22;
        } catch (NoSuchFieldError e) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_CAN_DO_AUTO_FOCUS.ordinal()] = 23;
        } catch (NoSuchFieldError e2) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_DISABLE_VIDEO_RECORD.ordinal()] = 24;
        } catch (NoSuchFieldError e3) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_FACE_DETECTED.ordinal()] = 25;
        } catch (NoSuchFieldError e4) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_NOTIFY_SURFCEVIEW_DESTROYED.ordinal()] = 1;
        } catch (NoSuchFieldError e5) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_NOTIFY_SURFCEVIEW_DISPLAY_IS_READY.ordinal()] = 2;
        } catch (NoSuchFieldError e6) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_OK_BUTTON_CLICK.ordinal()] = 26;
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
            iArr[ICameraMode.ActionType.ACTION_ON_CONFIGURATION_CHANGED.ordinal()] = 27;
        } catch (NoSuchFieldError e13) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_FULL_SCREEN_CHANGED.ordinal()] = 28;
        } catch (NoSuchFieldError e14) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_KEY_EVENT_PRESS.ordinal()] = 8;
        } catch (NoSuchFieldError e15) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_LONG_PRESS.ordinal()] = 9;
        } catch (NoSuchFieldError e16) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_MEDIA_EJECT.ordinal()] = 10;
        } catch (NoSuchFieldError e17) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_PREVIEW_BUFFER_SIZE_CHANGED.ordinal()] = 11;
        } catch (NoSuchFieldError e18) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_PREVIEW_DISPLAY_SIZE_CHANGED.ordinal()] = 29;
        } catch (NoSuchFieldError e19) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_RESTORE_SETTINGS.ordinal()] = 12;
        } catch (NoSuchFieldError e20) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_SELFTIMER_STATE.ordinal()] = 30;
        } catch (NoSuchFieldError e21) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_SETTING_BUTTON_CLICK.ordinal()] = 31;
        } catch (NoSuchFieldError e22) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_SINGLE_TAP_UP.ordinal()] = 13;
        } catch (NoSuchFieldError e23) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_START_PREVIEW.ordinal()] = 32;
        } catch (NoSuchFieldError e24) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_STOP_PREVIEW.ordinal()] = 14;
        } catch (NoSuchFieldError e25) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_SURFACE_TEXTURE_READY.ordinal()] = 33;
        } catch (NoSuchFieldError e26) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_USER_INTERACTION.ordinal()] = 15;
        } catch (NoSuchFieldError e27) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ORITATION_CHANGED.ordinal()] = 16;
        } catch (NoSuchFieldError e28) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_PHOTO_SHUTTER_BUTTON_CLICK.ordinal()] = 17;
        } catch (NoSuchFieldError e29) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_PREVIEW_VISIBLE_CHANGED.ordinal()] = 34;
        } catch (NoSuchFieldError e30) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_SET_DISPLAYROTATION.ordinal()] = 18;
        } catch (NoSuchFieldError e31) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_SHUTTER_BUTTON_FOCUS.ordinal()] = 19;
        } catch (NoSuchFieldError e32) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_SHUTTER_BUTTON_LONG_PRESS.ordinal()] = 35;
        } catch (NoSuchFieldError e33) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_SWITCH_DEVICE.ordinal()] = 20;
        } catch (NoSuchFieldError e34) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_VIDEO_SHUTTER_BUTTON_CLICK.ordinal()] = 21;
        } catch (NoSuchFieldError e35) {
        }
        f107commediatekcameraICameraMode$ActionTypeSwitchesValues = iArr;
        return iArr;
    }

    public PipVideoMode(ICameraContext iCameraContext) {
        super(iCameraContext);
        this.mIsRecordingStarted = false;
        this.mStopVideoRecording = false;
        this.mStopConditionVariableSync = new ConditionVariable();
        this.mPIPVideoPauseResumeListner = new View.OnClickListener() { // from class: com.mediatek.camera.mode.pip.PipVideoMode.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                Log.m31d("PipVideoMode", "[mPIPVideoPauseResumeListner.onClick()] mMediaRecoderRecordingPaused=" + PipVideoMode.this.mIsMediaRecoderRecordingPaused + ",mMediaRecorderRecording = " + PipVideoMode.this.mIsMediaRecorderRecording + " mIsRecordingStarted = " + PipVideoMode.this.mIsRecordingStarted);
                if (!PipVideoMode.this.mIsMediaRecorderRecording || (!PipVideoMode.this.mIsRecordingStarted)) {
                    return;
                }
                if (PipVideoMode.this.mIsMediaRecoderRecordingPaused) {
                    PipVideoMode.this.mRecordingView.setRecordingIndicator(true);
                    try {
                        PipVideoMode.this.mRecorder.resume(PipVideoMode.this.mRecorder);
                        PipVideoMode.this.mRecordingStartTime = SystemClock.uptimeMillis() - PipVideoMode.this.mRecordingPausedDuration;
                        PipVideoMode.this.mRecordingPausedDuration = 0L;
                        PipVideoMode.this.mIsMediaRecoderRecordingPaused = false;
                    } catch (IllegalStateException e) {
                        Log.m33e("PipVideoMode", "[mPIPVideoPauseResumeListner.onClick()] Could not start media recorder. ", e);
                        PipVideoMode.this.mICameraAppUi.showToast(R.string.toast_video_recording_not_available);
                        PipVideoMode.this.releaseMediaRecorder();
                    }
                } else {
                    PipVideoMode.this.pauseVideoRecording();
                }
                Log.m31d("PipVideoMode", "[mPIPVideoPauseResumeListner.onClick()] end.");
            }
        };
        Log.m31d("PipVideoMode", "[PipVideoMode]constructor...");
        this.mRecordingView.setListener(this.mPIPVideoPauseResumeListner);
        this.mICameraAppUi.setGestureListener(this);
        this.mPipController = PipController.instance(iCameraContext.getActivity());
        this.mPipController.init(iCameraContext, this);
        this.mStopConditionVariableSync.open();
        this.mCameraSound.load(2);
        setPipSettingRules(this.mICameraContext);
        setAntiFlickerRules(this.mICameraContext);
        setZsdRules(this.mICameraContext);
    }

    @Override // com.mediatek.camera.mode.VideoMode, com.mediatek.camera.mode.CameraMode, com.mediatek.camera.ICameraMode
    public boolean execute(ICameraMode.ActionType actionType, Object... objArr) {
        Log.m31d("PipVideoMode", "[execute]type = " + actionType);
        this.mAdditionManager.execute(actionType, true, objArr);
        switch (m855getcommediatekcameraICameraMode$ActionTypeSwitchesValues()[actionType.ordinal()]) {
            case 1:
                if (this.mPipController != null) {
                    this.mPipController.notifySurfaceViewDestroyed((Surface) objArr[0]);
                }
                return true;
            case 2:
                this.mPipController.setPreviewSurface(this.mIModuleCtrl.getPreviewSurface());
                return true;
            case 3:
                return onBackPressed();
            case 4:
                onCameraClose();
                if (this.mPipController != null) {
                    this.mPipController.pause();
                }
                return true;
            case 5:
                super.updateDevice();
                return true;
            case 6:
                doOnCameraParameterReady(((Boolean) objArr[0]).booleanValue());
                if (ICameraMode.ModeState.STATE_RECORDING != getModeState()) {
                    setModeState(ICameraMode.ModeState.STATE_IDLE);
                }
                return true;
            case FrameMetricsAggregator.DELAY_INDEX /* 7 */:
                if (this.mPipController != null) {
                    this.mPipController.onViewOrienationChanged(((Integer) objArr[0]).intValue());
                }
                if (this.mRecordingView != null) {
                    this.mRecordingView.onOrientationChanged(((Integer) objArr[0]).intValue());
                }
                return true;
            case 8:
                return onKeyDown(((Integer) objArr[0]).intValue(), (KeyEvent) objArr[1]);
            case 9:
                if (this.mICameraDevice != null && this.mICameraDevice.getParameters() != null && "auto".equals(this.mIFocusManager.getFocusMode())) {
                    this.mIFocusManager.cancelAutoFocus();
                }
                return true;
            case 10:
                onMediaEject();
                return true;
            case 11:
                onPreviewBufferSizeChanged(((Integer) objArr[0]).intValue(), ((Integer) objArr[1]).intValue());
                return true;
            case 12:
                onRestoreSettings();
                return true;
            case 13:
                if (objArr[0] != null && objArr[1] != null && objArr[2] != null) {
                    onSingleTapUp((View) objArr[0], ((Integer) objArr[1]).intValue(), ((Integer) objArr[2]).intValue());
                }
                return true;
            case 14:
                stopPreview();
                return true;
            case 15:
                return onUserInteraction();
            case 16:
                if (this.mPipController != null) {
                    this.mPipController.onGSensorOrientationChanged(((Integer) objArr[0]).intValue());
                }
                return true;
            case 17:
                takeASnapshot();
                return true;
            case 18:
                setDisplayRotation(((Integer) objArr[0]).intValue());
                return true;
            case 19:
                boolean zBooleanValue = ((Boolean) objArr[0]).booleanValue();
                Log.m31d("PipVideoMode", "ispressed  = " + zBooleanValue);
                if (!zBooleanValue) {
                    this.mIFocusManager.onShutterUp();
                }
                return true;
            case 20:
                switchDevice();
                return false;
            case 21:
                onVideoShutterButtonClick();
                return true;
            default:
                return false;
        }
    }

    @Override // com.mediatek.camera.mode.CameraMode, com.mediatek.camera.ICameraMode
    public void resume() {
        Log.m31d("PipVideoMode", "[resume]");
        if (this.mPipController != null) {
            this.mPipController.resume();
        }
    }

    @Override // com.mediatek.camera.mode.CameraMode, com.mediatek.camera.ICameraMode
    public void pause() {
        Log.m31d("PipVideoMode", "[pause]");
        if (this.mPipController != null) {
            this.mPipController.pause();
        }
    }

    @Override // com.mediatek.camera.mode.VideoMode, com.mediatek.camera.mode.CameraMode, com.mediatek.camera.ICameraMode
    public boolean close() {
        Log.m31d("PipVideoMode", "[close]...");
        IFocusManager iFocusManager = this.mIFocusManager;
        if (this.mICameraDevice != null) {
            this.mICameraDevice.cancelAutoFocus();
        }
        if (this.mRecordingView != null) {
            this.mRecordingView.uninit();
        }
        if (this.mPipController != null && ICameraMode.CameraModeType.EXT_MODE_PHOTO_PIP != this.mIModuleCtrl.getNextMode()) {
            this.mPipController.unInit(this.mICameraContext.getActivity());
            this.mPipController = null;
        }
        this.mIsAutoFocusCallback = false;
        this.mIFocusManager = null;
        this.mIsMediaRecoderRecordingPaused = false;
        return true;
    }

    @Override // com.mediatek.camera.mode.VideoMode
    public ICameraMode.CameraModeType getCameraModeType() {
        return ICameraMode.CameraModeType.EXT_MODE_VIDEO_PIP;
    }

    @Override // com.mediatek.camera.mode.VideoMode
    public void doStartPreview() {
        Log.m31d("PipVideoMode", "[doStartPreview()]");
        this.mICameraDeviceManager.getCameraDevice(this.mICameraDeviceManager.getCurrentCameraId()).startPreview();
        this.mICameraDeviceManager.getCameraDevice(getTopCameraId()).startPreview();
        this.mIFocusManager.onPreviewStarted();
        this.mAdditionManager.execute(ICameraAddition.AdditionActionType.ACTION_ON_START_PREVIEW, new Object[0]);
    }

    @Override // com.mediatek.camera.mode.VideoMode
    public void stopPreview() {
        Log.m31d("PipVideoMode", "[stopPreview()]");
        if (this.mPipController != null) {
            this.mPipController.stopSwitchPip();
        }
        if (this.mICameraDeviceManager.getCameraDevice(this.mICameraDeviceManager.getCurrentCameraId()) != null) {
            this.mICameraDeviceManager.getCameraDevice(this.mICameraDeviceManager.getCurrentCameraId()).stopPreview();
        }
        if (this.mICameraDeviceManager.getCameraDevice(getTopCameraId()) != null) {
            this.mICameraDeviceManager.getCameraDevice(getTopCameraId()).stopPreview();
        }
        this.mAdditionManager.execute(ICameraAddition.AdditionActionType.ACTION_ON_STOP_PREVIEW, new Object[0]);
    }

    @Override // com.mediatek.camera.mode.VideoMode
    public boolean onVideoShutterButtonClick() {
        Log.m31d("PipVideoMode", "[Video.onShutterButtonClick] () mMediaRecorderRecording=" + this.mIsMediaRecorderRecording);
        if (ICameraMode.ModeState.STATE_CLOSED == getModeState()) {
            Log.m34i("PipVideoMode", "[Video.onShutterButtonClick]mode state is closed,return");
            return false;
        }
        if (this.mIsMediaRecorderRecording) {
            stopVideoRecordingAsync(true);
        } else {
            if (Storage.getLeftSpace() <= 0) {
                backToLastModeIfNeed();
                Log.m34i("PipVideoMode", "[Video.onShutterButtonClick]current left space is full,return");
                return false;
            }
            this.mCameraSound.play(2);
            this.mICameraAppUi.setSwipeEnabled(false);
            startVideoRecording();
            if (!this.mIsMediaRecorderRecording) {
                this.mICameraAppUi.setSwipeEnabled(true);
            }
            if ("auto".equals(this.mIFocusManager.getFocusMode())) {
                autoFocus();
            }
        }
        return true;
    }

    @Override // com.mediatek.camera.mode.VideoMode
    public void initializeNormalRecorder() throws IllegalStateException, IllegalArgumentException {
        Log.m31d("PipVideoMode", "[initializeRecorder]");
        initializePipRecorder();
        if (this.mRecorder == null) {
            Log.m33e("PipVideoMode", "[initializeRecorder] Fail to initialize media recorder.", new Throwable());
        }
    }

    @Override // com.mediatek.camera.mode.VideoMode
    public boolean startNormalRecording() {
        Log.m31d("PipVideoMode", "[startNormalRecording()]");
        try {
            this.mPipController.startPushVideoBuffer();
            this.mRecorder.start();
            Log.m31d("PipVideoMode", "fetchParametersFromServer begin");
            this.mICameraDeviceManager.getCameraDevice(this.mICameraDeviceManager.getCurrentCameraId()).fetchParametersFromServer();
            this.mICameraDeviceManager.getCameraDevice(getTopCameraId()).fetchParametersFromServer();
            Log.m31d("PipVideoMode", "fetchParametersFromServer end");
            return true;
        } catch (RuntimeException e) {
            Log.m33e("PipVideoMode", "[startNormalRecording()] Could not start media recorder. ", e);
            releaseMediaRecorder();
            return false;
        }
    }

    @Override // com.mediatek.camera.mode.VideoMode
    protected void updateViewState(boolean z) {
        super.updateViewState(z);
        if (this.mPipController != null) {
            this.mPipController.hideModeViews(z);
        }
    }

    @Override // com.mediatek.camera.mode.VideoMode
    public void stopVideoRecordingAsync(boolean z) {
        this.mStopVideoRecording = true;
        Log.m31d("PipVideoMode", "[stopVideoRecordingAsync()] mMediaRecorderRecording=" + this.mIsMediaRecorderRecording + ", isVideoProcessing()" + isVideoProcessing() + ", mStopVideoRecording =" + this.mStopVideoRecording);
        this.mICameraAppUi.changeZoomForQuality();
        this.mICameraAppUi.setSwipeEnabled(true);
        this.mHandler.removeMessages(5);
        this.mICameraAppUi.setVideoShutterMask(false);
        if (ICameraMode.ModeState.STATE_SAVING == getModeState()) {
            return;
        }
        setModeState(ICameraMode.ModeState.STATE_SAVING);
        this.mRecordingView.hide();
        if (this.mIsMediaRecorderRecording) {
            this.mICameraAppUi.setVideoShutterEnabled(false);
            if (z) {
                this.mICameraAppUi.showProgress(this.mActivity.getResources().getString(R.string.saving));
            }
            this.mVideoSavingTask = new VideoMode.SavingTask();
            this.mVideoSavingTask.start();
        } else {
            releaseMediaRecorder();
            if (this.mStoppingAction == 3) {
                this.mVideoModeHelper.doReturnToCaller(false, this.mCurrentVideoUri);
            }
        }
        if (isVideoProcessing()) {
            this.mStopConditionVariableSync.close();
        }
    }

    @Override // com.mediatek.camera.mode.VideoMode
    public boolean takeASnapshot() {
        if (this.mStopVideoRecording) {
            return false;
        }
        Log.m31d("PipVideoMode", "[takeASnapshot] Video snapshot ");
        this.mPipController.takeVideoSnapshot(this.mIModuleCtrl.getOrientation(), isBackBottom());
        if (!this.mIModuleCtrl.isVideoCaptureIntent()) {
            this.mICameraAppUi.updateSnapShotUIView(true);
        }
        return true;
    }

    @Override // com.mediatek.camera.mode.pip.PipController.Listener
    public void onPIPPictureTaken(byte[] bArr) {
        Log.m31d("PipVideoMode", "[onPIPPictureTaken]");
        if (bArr == null) {
            Log.m34i("PipVideoMode", "[onPIPPictureTaken] jpegData is null return");
            return;
        }
        this.mIFileSaver.init(IFileSaver.FILE_TYPE.JPEG, 0, null, -1);
        long jCurrentTimeMillis = System.currentTimeMillis();
        String str = Util.createNameFormat(jCurrentTimeMillis, this.mActivity.getString(R.string.image_file_name_format)) + ".jpg";
        this.mIFileSaver.savePhotoFile(bArr, null, jCurrentTimeMillis, this.mIModuleCtrl.getLocation(), 0, null);
        this.mHandler.sendEmptyMessage(15);
    }

    @Override // com.mediatek.camera.mode.VideoMode
    public void stopRecording() {
        Log.m31d("PipVideoMode", "[stopRecording] begin");
        this.mPipController.stopPushVideoBuffer();
        playSound(2);
        try {
            try {
                this.mRecorder.stop();
                this.mStopConditionVariableSync.open();
                Log.m31d("PipVideoMode", "[stopRecording] end");
            } catch (RuntimeException e) {
                throw e;
            }
        } catch (Throwable th) {
            this.mStopConditionVariableSync.open();
            throw th;
        }
    }

    @Override // com.mediatek.camera.mode.VideoMode
    public void doAfterStopRecording(boolean z) {
        if (!this.mIModuleCtrl.isNonePickIntent()) {
            if (!z && this.mStoppingAction != 3) {
                if (this.mIModuleCtrl.isQuickCapture()) {
                    this.mStoppingAction = 2;
                } else {
                    this.mStoppingAction = 4;
                }
            }
        } else if (z) {
            this.mStoppingAction = 5;
        }
        releaseMediaRecorder();
        addVideoToMediaStore();
        synchronized (this.mVideoSavingTask) {
            this.mVideoSavingTask.notifyAll();
            this.mHandler.removeCallbacks(this.mVideoSavedRunnable);
            this.mHandler.post(this.mVideoSavedRunnable);
        }
    }

    @Override // com.mediatek.camera.mode.VideoMode
    public void addVideoToMediaStore() {
        if (this.mVideoFileDescriptor == null) {
            this.mIFileSaver.init(IFileSaver.FILE_TYPE.PIPVIDEO, this.mProfile.fileFormat, Integer.toString(this.mProfile.videoFrameWidth) + "x" + Integer.toString(this.mProfile.videoFrameHeight), Util.getRecordingRotation(this.mIModuleCtrl.getOrientation(), this.mICameraDeviceManager.getCurrentCameraId(), this.mICameraDeviceManager.getCameraInfo(this.mICameraDeviceManager.getCurrentCameraId())));
            this.mIFileSaver.saveVideoFile(this.mIModuleCtrl.getLocation(), this.mVideoTempPath, computeDuration(), 0, this.mFileSavedListener);
        }
    }

    @Override // com.mediatek.camera.mode.VideoMode
    protected void releaseMediaRecorder() {
        Log.m31d("PipVideoMode", "[releaseMediaRecorder()] mRecorder=" + this.mRecorder + " mRecorderCameraReleased = " + this.mIsRecorderCameraReleased);
        if (this.mRecorder != null && (!this.mIsRecorderCameraReleased)) {
            cleanupEmptyFile();
            this.mRecorder.release();
            this.mIsRecorderCameraReleased = true;
            this.mHandler.post(this.mReleaseOnInfoListener);
        }
        this.mVideoFilename = null;
    }

    @Override // com.mediatek.camera.mode.VideoMode
    protected void pauseVideoRecording() {
        Log.m31d("PipVideoMode", "[pauseVideoRecording()] mMediaRecorderRecording =" + this.mIsMediaRecorderRecording + " mMediaRecoderRecordingPaused = " + this.mIsMediaRecoderRecordingPaused);
        if (canDoPauseResumeAction()) {
            this.mRecordingView.setRecordingIndicator(false);
            try {
                this.mRecorder.pause(this.mRecorder);
            } catch (IllegalStateException e) {
                Log.m32e("PipVideoMode", "[pauseVideoRecording()]  Could not pause media recorder. ");
            }
            this.mRecordingPausedDuration = SystemClock.uptimeMillis() - this.mRecordingStartTime;
            this.mIsMediaRecoderRecordingPaused = true;
        }
    }

    @Override // com.mediatek.camera.mode.VideoMode
    protected void waitForRecorder() {
        this.mStopConditionVariableSync.block();
        Log.m31d("PipVideoMode", "[waitForRecorder] end");
    }

    @Override // com.mediatek.camera.mode.VideoMode
    protected void updateRecordingTime() {
        super.updateRecordingTime();
        Log.m31d("PipVideoMode", "mTotalRecordingDuration = " + this.mTotalRecordingDuration);
    }

    @Override // com.mediatek.camera.mode.VideoMode
    public void initializeShutterStatus() {
        this.mICameraAppUi.setPhotoShutterEnabled(true);
    }

    @Override // com.mediatek.camera.mode.CameraMode, com.mediatek.camera.ICameraMode
    public boolean isDisplayUseSurfaceView() {
        Log.m31d("PipVideoMode", "[isUseSurfaceView]");
        return true;
    }

    @Override // com.mediatek.camera.mode.CameraMode, com.mediatek.camera.ICameraMode
    public boolean isDeviceUseSurfaceView() {
        Log.m31d("PipVideoMode", "[isDeviceUseSurfaceView]");
        return false;
    }

    @Override // com.mediatek.camera.mode.CameraMode, com.mediatek.camera.ICameraMode
    public boolean isNeedDualCamera() {
        Log.m31d("PipVideoMode", "[isNeedDualCamera]");
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
    public void canDoStartPreview() {
    }

    @Override // com.mediatek.camera.mode.pip.PipController.Listener
    public int getButtomGraphicCameraId() {
        return this.mICameraDeviceManager.getCurrentCameraId();
    }

    @Override // com.mediatek.camera.mode.pip.PipController.Listener
    public void switchPIP() {
        Log.m31d("PipVideoMode", "switchPIP");
        if (this.mIFocusManager != null) {
            this.mIFocusManager.cancelAutoFocus();
        }
        this.mIModuleCtrl.switchCameraDevice();
        updateDevice();
    }

    @Override // com.mediatek.camera.mode.CameraMode, com.mediatek.camera.ICameraMode
    public SurfaceTexture getBottomSurfaceTexture() {
        Log.m31d("PipVideoMode", "[getBottomSurfaceTexture]");
        return this.mPipController.getBottomSurfaceTexture();
    }

    @Override // com.mediatek.camera.mode.CameraMode, com.mediatek.camera.ICameraMode
    public SurfaceTexture getTopSurfaceTexture() {
        Log.m31d("PipVideoMode", "[getTopSurfaceTexture]");
        return this.mPipController.getTopSurfaceTexture();
    }

    @Override // com.mediatek.camera.platform.ICameraAppUi.GestureListener
    public boolean onDown(float f, float f2, int i, int i2) {
        if (this.mPipController != null && getModeState() != ICameraMode.ModeState.STATE_FOCUSING) {
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

    @Override // com.mediatek.camera.mode.pip.recorder.MediaRecorderWrapper.OnInfoListener
    public void onInfo(MediaRecorderWrapper mediaRecorderWrapper, int i, int i2) {
        Log.m31d("PipVideoMode", "[onInfo] what = " + i + "   extra = " + i2);
        if (1998 == i) {
            this.mHandler.post(new Runnable() { // from class: com.mediatek.camera.mode.pip.PipVideoMode.2
                @Override // java.lang.Runnable
                public void run() {
                    PipVideoMode.this.mRecordingStartTime = SystemClock.uptimeMillis();
                    PipVideoMode.this.updateRecordingTime();
                }
            });
            this.mIsRecordingStarted = true;
        } else if (801 == i && this.mIsMediaRecorderRecording) {
            stopVideoRecordingAsync(true);
            this.mICameraAppUi.showToastForShort(R.string.video_reach_size_limit);
        }
    }

    private boolean canDoPauseResumeAction() {
        return this.mIsMediaRecorderRecording && (this.mIsMediaRecoderRecordingPaused ^ true) && ICameraMode.ModeState.STATE_SAVING != getModeState();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getTopCameraId() {
        if (this.mICameraDeviceManager.getCurrentCameraId() == this.mICameraDeviceManager.getBackCameraId()) {
            return this.mICameraDeviceManager.getFrontCameraId();
        }
        return this.mICameraDeviceManager.getBackCameraId();
    }

    private boolean isBackBottom() {
        return this.mICameraDeviceManager.getCurrentCameraId() == this.mICameraDeviceManager.getBackCameraId();
    }

    private void setDisplayRotation(int i) {
        Log.m34i("PipVideoMode", "[setDisplayOrientation] displayRotation = " + i);
        this.mPipController.setDisplayRotation(i);
    }

    private void onPreviewBufferSizeChanged(int i, int i2) {
        Log.m34i("PipVideoMode", "[onPreviewBufferSizeChanged] width = " + i + " height = " + i2);
        this.mPipController.setPreviewTextureSize(i, i2);
    }

    private void initializePipRecorder() throws IllegalStateException, IllegalArgumentException {
        Log.m31d("PipVideoMode", "[initializePipRecorder]...");
        this.mProfile = this.mVideoPreviewSizeRule.getProfile();
        this.mRecorder = new MediaRecorderWrapper();
        if (this.mIsRecordAudio) {
            this.mRecorder.setAudioSource(5);
            this.mRecorder.setAudioChannels(this.mProfile.audioChannels);
        }
        this.mRecorder.setVideoSource(2);
        this.mRecorder.setOutputFormat(this.mProfile.fileFormat);
        this.mRecorder.setVideoFrameRate(this.mProfile.videoFrameRate);
        Camera.Size previewSize = this.mICameraDevice.getParameters().getPreviewSize();
        this.mRecorder.setVideoSize(previewSize == null ? 0 : previewSize.width, previewSize == null ? 0 : previewSize.height);
        this.mRecorder.setVideoEncodingBitRate(this.mProfile.videoBitRate);
        this.mRecorder.setVideoEncoder(this.mProfile.videoCodec);
        if (this.mIsRecordAudio) {
            this.mRecorder.setAudioEncoder(this.mProfile.audioCodec);
            this.mRecorder.setAudioEncodingBitRate(this.mProfile.audioBitRate);
            this.mRecorder.setAudioSamplingRate(this.mProfile.audioSampleRate);
        }
        Location location = this.mIModuleCtrl.getLocation();
        if (location != null) {
            this.mRecorder.setLocation((long) location.getLatitude(), (long) location.getLongitude());
        }
        long availableSpace = Storage.getAvailableSpace() - Storage.RECORD_LOW_STORAGE_THRESHOLD;
        if (availableSpace >= VIDEO_4G_SIZE.longValue()) {
            availableSpace = VIDEO_4G_SIZE.longValue();
        }
        this.mRecorder.setMaxFileSize(availableSpace);
        generateVideoFilename(this.mProfile.fileFormat, null);
        this.mRecorder.setOutputFile(this.mVideoFilename);
        this.mRecorder.setParametersExtra();
        this.mRecorder.setOrientationHint(Util.getRecordingRotation(this.mIModuleCtrl.getOrientation(), this.mICameraDeviceManager.getBackCameraId(), this.mICameraDeviceManager.getCameraInfo(this.mICameraDeviceManager.getBackCameraId())));
        try {
            this.mRecorder.prepare();
            this.mRecorder.setOnInfoListener(this);
            this.mPipController.prepareRecording();
            this.mPipController.setRecordingSurface(this.mRecorder.getSurface(), Util.getSensorOrientation(this.mICameraDeviceManager.getCameraInfo(this.mICameraDeviceManager.getBackCameraId())));
            this.mHandler.removeCallbacks(this.mReleaseOnInfoListener);
            this.mIsRecordingStarted = false;
        } catch (IOException e) {
            Log.m33e("PipVideoMode", "[initializepipRecorder] prepare failed", e);
            releaseMediaRecorder();
            throw new RuntimeException(e);
        }
    }

    private void switchDevice() {
        if (this.mPipController != null) {
            this.mPipController.switchPIP();
        }
    }

    private void setPipSettingRules(ICameraContext iCameraContext) {
        Log.m31d("PipVideoMode", "[setPipSettingRules]...");
        PipVideoQualityRule pipVideoQualityRule = new PipVideoQualityRule(iCameraContext, "video_pip_key");
        pipVideoQualityRule.addLimitation("on", null, null);
        this.mISettingCtrl.addRule("video_pip_key", "pref_video_quality_key", pipVideoQualityRule);
    }

    private void setAntiFlickerRules(ICameraContext iCameraContext) {
        PipAntiFlickRule pipAntiFlickRule = new PipAntiFlickRule(iCameraContext);
        pipAntiFlickRule.addLimitation("on", null, null);
        this.mISettingCtrl.addRule("video_pip_key", "pref_camera_antibanding_key", pipAntiFlickRule);
    }

    private void setZsdRules(ICameraContext iCameraContext) {
        PipZsdRule pipZsdRule = new PipZsdRule(iCameraContext);
        pipZsdRule.addLimitation("on", null, null);
        this.mISettingCtrl.addRule("video_pip_key", "pref_camera_zsd_key", pipZsdRule);
    }

    private class PipAntiFlickRule implements ISettingRule {
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
        private String mCurrentAntiFlickValue = null;

        public PipAntiFlickRule(ICameraContext iCameraContext) {
            Log.m31d("PipVideoMode", "[PipAntiFlickRule]constructor...");
            this.mCameraContext = iCameraContext;
        }

        @Override // com.mediatek.camera.ISettingRule
        public void execute() {
            this.deviceManager = this.mCameraContext.getCameraDeviceManager();
            this.mBackCamDevice = this.deviceManager.getCameraDevice(this.deviceManager.getCurrentCameraId());
            if (this.mTopCamDevice != null) {
                this.mSwitchingPip = this.mTopCamDevice.getCameraId() == this.deviceManager.getCurrentCameraId();
            } else {
                this.mSwitchingPip = false;
            }
            this.mTopCamDevice = this.deviceManager.getCameraDevice(PipVideoMode.this.getTopCameraId());
            this.mISettingCtrl = this.mCameraContext.getSettingController();
            this.mParameters = this.mBackCamDevice.getParameters();
            if (this.mTopCamDevice != null) {
                this.mTopParameters = this.mTopCamDevice.getParameters();
            }
            int iConditionSatisfied = conditionSatisfied(this.mISettingCtrl.getSettingValue("video_pip_key"));
            String settingValue = this.mISettingCtrl.getSettingValue("pref_camera_antibanding_key");
            Log.m31d("PipVideoMode", "[execute]PipAntiFlickRule index = " + iConditionSatisfied + " antiFlickValue = " + settingValue + " mSwitchingPip = " + this.mSwitchingPip);
            if (iConditionSatisfied == -1) {
                this.mParameters.setAntibanding(settingValue);
                return;
            }
            if (this.mSwitchingPip) {
                settingValue = this.mCurrentAntiFlickValue;
                this.mISettingCtrl.setSettingValue("pref_camera_antibanding_key", settingValue, this.deviceManager.getCurrentCameraId());
                ListPreference listPreference = this.mISettingCtrl.getListPreference("pref_camera_antibanding_key");
                if (listPreference != null) {
                    listPreference.setValue(settingValue);
                }
            }
            this.mCurrentAntiFlickValue = settingValue;
            this.mParameters.setAntibanding(settingValue);
            if (this.mTopParameters != null) {
                this.mTopParameters.setAntibanding(settingValue);
            }
        }

        @Override // com.mediatek.camera.ISettingRule
        public void addLimitation(String str, List<String> list, ISettingRule.MappingFinder mappingFinder) {
            Log.m34i("PipVideoMode", "[addLimitation]condition = " + str);
            this.mConditions.add(str);
            this.mResults.add(list);
            this.mMappingFinders.add(mappingFinder);
        }

        private int conditionSatisfied(String str) {
            return this.mConditions.indexOf(str);
        }
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
        private String mCurrentAntiFlickValue = null;

        public PipZsdRule(ICameraContext iCameraContext) {
            Log.m31d("PipVideoMode", "[PipZsdRule]constructor...");
            this.mCameraContext = iCameraContext;
        }

        @Override // com.mediatek.camera.ISettingRule
        public void execute() {
            this.deviceManager = this.mCameraContext.getCameraDeviceManager();
            this.mBackCamDevice = this.deviceManager.getCameraDevice(this.deviceManager.getCurrentCameraId());
            if (this.mTopCamDevice != null) {
                this.mSwitchingPip = this.mTopCamDevice.getCameraId() == this.deviceManager.getCurrentCameraId();
            } else {
                this.mSwitchingPip = false;
            }
            this.mTopCamDevice = this.deviceManager.getCameraDevice(PipVideoMode.this.getTopCameraId());
            this.mISettingCtrl = this.mCameraContext.getSettingController();
            this.mParameters = this.mBackCamDevice.getParameters();
            if (this.mTopCamDevice != null) {
                this.mTopParameters = this.mTopCamDevice.getParameters();
            }
            int iConditionSatisfied = conditionSatisfied(this.mISettingCtrl.getSettingValue("video_pip_key"));
            String settingValue = this.mISettingCtrl.getSettingValue("pref_camera_zsd_key");
            if (settingValue == null) {
                Log.m31d("PipVideoMode", "[PipZsdRule.execute] don't support zsd!");
                return;
            }
            Log.m31d("PipVideoMode", "[execute]PipZsdRule zsdValue = " + settingValue + ", index = " + iConditionSatisfied);
            if (iConditionSatisfied == -1) {
                this.mParameters.setZSDMode(settingValue);
                return;
            }
            this.mParameters.setZSDMode("off");
            if (this.mTopParameters != null) {
                this.mTopParameters.setZSDMode("off");
            }
        }

        @Override // com.mediatek.camera.ISettingRule
        public void addLimitation(String str, List<String> list, ISettingRule.MappingFinder mappingFinder) {
            Log.m31d("PipVideoMode", "[addLimitation]condition = " + str);
            this.mConditions.add(str);
            this.mResults.add(list);
            this.mMappingFinders.add(mappingFinder);
        }

        private int conditionSatisfied(String str) {
            return this.mConditions.indexOf(str);
        }
    }
}
