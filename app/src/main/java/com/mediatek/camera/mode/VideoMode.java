package com.mediatek.camera.mode;

import android.content.ContentResolver;
import android.hardware.Camera;
import android.location.Location;
import android.media.AudioManager;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.support.v4.app.FrameMetricsAggregator;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;
import com.android.camera.Storage;
import com.mediatek.camera.AdditionManager;
import com.mediatek.camera.ICameraAddition;
import com.mediatek.camera.ICameraContext;
import com.mediatek.camera.ICameraMode;
import com.mediatek.camera.R;
import com.mediatek.camera.mode.facebeauty.VfbQualityRule;
import com.mediatek.camera.mode.facebeauty.VideoFaceBeautyRule;
import com.mediatek.camera.p004ui.RecordingView;
import com.mediatek.camera.platform.ICameraAppUi;
import com.mediatek.camera.platform.ICameraDeviceManager;
import com.mediatek.camera.platform.IFileSaver;
import com.mediatek.camera.platform.IFocusManager;
import com.mediatek.camera.platform.Parameters;
import com.mediatek.camera.util.Log;
import com.mediatek.camera.util.Util;
import com.mediatek.media.MediaRecorderEx;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/* loaded from: classes.dex */
public class VideoMode extends CameraMode implements MediaRecorder.OnErrorListener, MediaRecorder.OnInfoListener, IFocusManager.FocusListener {

    /* renamed from: -com-mediatek-camera-ICameraMode$ActionTypeSwitchesValues, reason: not valid java name */
    private static final /* synthetic */ int[] f103commediatekcameraICameraMode$ActionTypeSwitchesValues = null;
    private Runnable backToLastModeRunnable;
    protected AdditionManager mAdditionManager;
    private final AutoFocusCallback mAutoFocusCallback;
    protected final ICameraDeviceManager.ICameraDevice.AutoFocusMvCallback mAutoFocusMoveCallback;
    protected String mConditionSettingKey;
    private ContentResolver mContentResolver;
    private int mCurrentShowIndicator;
    protected String mCurrentVideoFilename;
    protected Uri mCurrentVideoUri;
    protected IFileSaver.OnFileSavedListener mFileSavedListener;
    private long mFocusStartTime;
    private int mFocusState;
    protected Handler mHandler;
    protected boolean mIsAutoFocusCallback;
    protected boolean mIsMediaRecoderRecordingPaused;
    protected boolean mIsMediaRecorderRecording;
    private boolean mIsModeReleased;
    protected boolean mIsParameterExtraCanUse;
    protected boolean mIsRecordAudio;
    protected boolean mIsRecorderCameraReleased;
    private boolean mIsSetEisFrams;
    private boolean mIsTimeLapseEnable;
    protected MediaRecorder mMediaRecorder;
    protected Parameters mParameters;
    protected CamcorderProfile mProfile;
    protected long mRecordingPausedDuration;
    private int mRecordingStartOrientation;
    protected long mRecordingStartTime;
    protected RecordingView mRecordingView;
    protected final Runnable mReleaseOnInfoListener;
    private int mRequestDurationLimit;
    private long mRequestSizeLimit;
    private View.OnClickListener mRetakeListener;
    private View.OnClickListener mReviewPlayListener;
    protected int mSaveTempVideo;
    protected int mStoppingAction;
    protected long mTotalRecordingDuration;
    protected ParcelFileDescriptor mVideoFileDescriptor;
    protected String mVideoFilename;
    protected VideoHdrRule mVideoHdrRul;
    protected VideoModeHelper mVideoModeHelper;
    private View.OnClickListener mVideoPauseResumeListner;
    protected VideoPreviewRule mVideoPreviewSizeRule;
    protected Runnable mVideoSavedRunnable;
    protected Thread mVideoSavingTask;
    protected String mVideoTempPath;

    /* renamed from: -getcom-mediatek-camera-ICameraMode$ActionTypeSwitchesValues, reason: not valid java name */
    private static /* synthetic */ int[] m748getcommediatekcameraICameraMode$ActionTypeSwitchesValues() {
        if (f103commediatekcameraICameraMode$ActionTypeSwitchesValues != null) {
            return f103commediatekcameraICameraMode$ActionTypeSwitchesValues;
        }
        int[] iArr = new int[ICameraMode.ActionType.valuesCustom().length];
        try {
            iArr[ICameraMode.ActionType.ACTION_CANCEL_BUTTON_CLICK.ordinal()] = 17;
        } catch (NoSuchFieldError e) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_CAN_DO_AUTO_FOCUS.ordinal()] = 18;
        } catch (NoSuchFieldError e2) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_DISABLE_VIDEO_RECORD.ordinal()] = 1;
        } catch (NoSuchFieldError e3) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_FACE_DETECTED.ordinal()] = 19;
        } catch (NoSuchFieldError e4) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_NOTIFY_SURFCEVIEW_DESTROYED.ordinal()] = 20;
        } catch (NoSuchFieldError e5) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_NOTIFY_SURFCEVIEW_DISPLAY_IS_READY.ordinal()] = 21;
        } catch (NoSuchFieldError e6) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_OK_BUTTON_CLICK.ordinal()] = 2;
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
            iArr[ICameraMode.ActionType.ACTION_ON_FULL_SCREEN_CHANGED.ordinal()] = 22;
        } catch (NoSuchFieldError e14) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_KEY_EVENT_PRESS.ordinal()] = 9;
        } catch (NoSuchFieldError e15) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_LONG_PRESS.ordinal()] = 23;
        } catch (NoSuchFieldError e16) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_MEDIA_EJECT.ordinal()] = 10;
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
            iArr[ICameraMode.ActionType.ACTION_ON_RESTORE_SETTINGS.ordinal()] = 11;
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
            iArr[ICameraMode.ActionType.ACTION_ON_SINGLE_TAP_UP.ordinal()] = 12;
        } catch (NoSuchFieldError e23) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_START_PREVIEW.ordinal()] = 28;
        } catch (NoSuchFieldError e24) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_STOP_PREVIEW.ordinal()] = 13;
        } catch (NoSuchFieldError e25) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_SURFACE_TEXTURE_READY.ordinal()] = 29;
        } catch (NoSuchFieldError e26) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_USER_INTERACTION.ordinal()] = 14;
        } catch (NoSuchFieldError e27) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ORITATION_CHANGED.ordinal()] = 30;
        } catch (NoSuchFieldError e28) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_PHOTO_SHUTTER_BUTTON_CLICK.ordinal()] = 15;
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
            iArr[ICameraMode.ActionType.ACTION_SHUTTER_BUTTON_LONG_PRESS.ordinal()] = 34;
        } catch (NoSuchFieldError e33) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_SWITCH_DEVICE.ordinal()] = 35;
        } catch (NoSuchFieldError e34) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_VIDEO_SHUTTER_BUTTON_CLICK.ordinal()] = 16;
        } catch (NoSuchFieldError e35) {
        }
        f103commediatekcameraICameraMode$ActionTypeSwitchesValues = iArr;
        return iArr;
    }

    public VideoMode(ICameraContext iCameraContext) {
        super(iCameraContext);
        this.mIsMediaRecoderRecordingPaused = false;
        this.mIsMediaRecorderRecording = false;
        this.mIsRecorderCameraReleased = true;
        this.mIsAutoFocusCallback = false;
        this.mIsRecordAudio = false;
        this.mIsParameterExtraCanUse = false;
        this.mIsTimeLapseEnable = false;
        this.mRecordingPausedDuration = 0L;
        this.mTotalRecordingDuration = 0L;
        this.mStoppingAction = 1;
        this.mSaveTempVideo = SystemProperties.getInt("camera.save.temp.video", 0);
        this.mConditionSettingKey = "video_key";
        this.mCurrentShowIndicator = 0;
        this.mFocusState = 0;
        this.mRequestSizeLimit = 0L;
        this.mIsSetEisFrams = false;
        this.mIsModeReleased = false;
        this.mAutoFocusCallback = new AutoFocusCallback(this, null);
        this.mAutoFocusMoveCallback = new ICameraDeviceManager.ICameraDevice.AutoFocusMvCallback() { // from class: com.mediatek.camera.mode.VideoMode.1
            @Override // com.mediatek.camera.platform.ICameraDeviceManager.ICameraDevice.AutoFocusMvCallback
            public void onAutoFocusMoving(boolean z, Camera camera) {
                Log.m34i("VideoMode", "[onAutoFocusMoving]moving = " + z);
                VideoMode.this.mIFocusManager.onAutoFocusMoving(z);
            }
        };
        this.mReleaseOnInfoListener = new Runnable() { // from class: com.mediatek.camera.mode.VideoMode.2
            @Override // java.lang.Runnable
            public void run() {
                if (VideoMode.this.mMediaRecorder != null) {
                    VideoMode.this.mMediaRecorder.setOnInfoListener(null);
                    VideoMode.this.mMediaRecorder.setOnErrorListener(null);
                    VideoMode.this.mMediaRecorder = null;
                }
            }
        };
        this.mVideoSavedRunnable = new Runnable() { // from class: com.mediatek.camera.mode.VideoMode.3
            @Override // java.lang.Runnable
            public void run() throws IOException {
                Log.m31d("VideoMode", "[mVideoSavedRunnable.run()] begin , mStoppingAction = " + VideoMode.this.mStoppingAction);
                VideoMode.this.updateViewState(false);
                VideoMode.this.mICameraAppUi.dismissProgress();
                VideoMode.this.mICameraAppUi.setVideoShutterEnabled(true);
                switch ((ICameraMode.ModeState.STATE_CLOSED != VideoMode.this.getModeState() || VideoMode.this.mStoppingAction == 1 || VideoMode.this.mStoppingAction == 5) ? VideoMode.this.mStoppingAction : 4) {
                    case 2:
                        VideoMode.this.mVideoModeHelper.doReturnToCaller(true, VideoMode.this.mCurrentVideoUri);
                        break;
                    case 3:
                        VideoMode.this.mVideoModeHelper.doReturnToCaller(false, VideoMode.this.mCurrentVideoUri);
                        break;
                    case 4:
                        VideoMode.this.showAlert();
                        break;
                }
                if (ICameraMode.ModeState.STATE_CLOSED == VideoMode.this.getModeState()) {
                    VideoMode.this.closeVideoFileDescriptor();
                }
                if (ICameraMode.ModeState.STATE_CLOSED != VideoMode.this.getModeState() && (VideoMode.this.mFocusState == -1 || VideoMode.this.mFocusState == 1)) {
                    VideoMode.this.changeFocusState();
                }
                VideoMode.this.setModeState(ICameraMode.ModeState.STATE_IDLE);
                VideoMode.this.backToLastModeIfNeed();
                Log.m31d("VideoMode", "[mVideoSavedRunnable.run()] end ");
            }
        };
        this.backToLastModeRunnable = new Runnable() { // from class: com.mediatek.camera.mode.VideoMode.4
            @Override // java.lang.Runnable
            public void run() {
                VideoMode.this.mIModuleCtrl.backToLastMode();
                VideoMode.this.mICameraAppUi.setPhotoShutterEnabled(true);
            }
        };
        this.mFileSavedListener = new IFileSaver.OnFileSavedListener() { // from class: com.mediatek.camera.mode.VideoMode.5
            @Override // com.mediatek.camera.platform.IFileSaver.OnFileSavedListener
            public void onFileSaved(Uri uri) {
                Log.m31d("VideoMode", "[onFileSaved] uri = " + uri);
                VideoMode.this.mCurrentVideoUri = uri;
                VideoMode.this.mHandler.removeMessages(9);
                VideoMode.this.setModeState(ICameraMode.ModeState.STATE_IDLE);
            }
        };
        this.mVideoPauseResumeListner = new View.OnClickListener() { // from class: com.mediatek.camera.mode.VideoMode.6
            @Override // android.view.View.OnClickListener
            public void onClick(View view) throws IllegalStateException {
                Log.m34i("VideoMode", "[mVideoPauseResumeListner.onClick()] mMediaRecoderRecordingPaused = " + VideoMode.this.mIsMediaRecoderRecordingPaused);
                if (ICameraMode.ModeState.STATE_RECORDING != VideoMode.this.getModeState()) {
                    return;
                }
                if (VideoMode.this.mIsMediaRecoderRecordingPaused) {
                    VideoMode.this.mRecordingView.setRecordingIndicator(true);
                    try {
                        VideoMode.this.mMediaRecorder.resume();
                        VideoMode.this.mRecordingStartTime = SystemClock.uptimeMillis() - VideoMode.this.mRecordingPausedDuration;
                        VideoMode.this.mRecordingPausedDuration = 0L;
                        VideoMode.this.mIsMediaRecoderRecordingPaused = false;
                    } catch (IllegalStateException e) {
                        Log.m33e("VideoMode", "[mVideoPauseResumeListner] Could not start media recorder. ", e);
                        VideoMode.this.mICameraAppUi.showToast(R.string.toast_video_recording_not_available);
                        VideoMode.this.releaseMediaRecorder();
                    }
                } else {
                    VideoMode.this.pauseVideoRecording();
                }
                Log.m31d("VideoMode", "[mVideoPauseResumeListner.onClick()] end");
            }
        };
        this.mReviewPlayListener = new View.OnClickListener() { // from class: com.mediatek.camera.mode.VideoMode.7
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                Log.m31d("VideoMode", "[mReviewPlayListener],onClick");
                VideoMode.this.mVideoModeHelper.startPlayVideoActivity(VideoMode.this.mCurrentVideoUri, VideoMode.this.mProfile);
            }
        };
        this.mRetakeListener = new View.OnClickListener() { // from class: com.mediatek.camera.mode.VideoMode.8
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                Log.m31d("VideoMode", "[mRetakeListener],onClick");
                VideoMode.this.deleteCurrentVideo();
                VideoMode.this.mICameraAppUi.hideReview();
                VideoMode.this.mICameraAppUi.setVideoShutterEnabled(true);
                VideoMode.this.mICameraAppUi.switchShutterType(ICameraAppUi.ShutterButtonType.SHUTTER_TYPE_VIDEO);
            }
        };
        Log.m31d("VideoMode", "[VideoMode]constructor...");
        setModeState(ICameraMode.ModeState.STATE_UNKNOWN);
        this.mAdditionManager = iCameraContext.getAdditionManager();
        this.mIsModeReleased = false;
        this.mRecordingView = new RecordingView(this.mActivity);
        this.mRecordingView.setListener(this.mVideoPauseResumeListner);
        this.mRecordingView.setOrientation(this.mIModuleCtrl.getOrientationCompensation());
        this.mRecordingView.getView();
        this.mHandler = new MainHandler(this.mActivity.getMainLooper());
        setVideoRule();
        this.mICameraAppUi.switchShutterType(this.mIModuleCtrl.isNonePickIntent() ? ICameraAppUi.ShutterButtonType.SHUTTER_TYPE_PHOTO_VIDEO : ICameraAppUi.ShutterButtonType.SHUTTER_TYPE_VIDEO);
        this.mVideoModeHelper = new VideoModeHelper(this.mActivity, this.mIModuleCtrl, this.mISettingCtrl);
        initVideoRecordingFirst();
        initializeRecordingView();
        this.mIsMediaRecoderRecordingPaused = false;
        this.mRecordingPausedDuration = 0L;
        this.mTotalRecordingDuration = 0L;
        this.mIsRecorderCameraReleased = false;
        this.mRecordingView.showTime(0L, false);
        this.mRecordingView.setTimeVisible(true);
    }

    @Override // com.mediatek.camera.mode.CameraMode, com.mediatek.camera.ICameraMode
    public boolean execute(ICameraMode.ActionType actionType, Object... objArr) throws IllegalStateException, IOException, IllegalArgumentException {
        Log.m34i("VideoMode", "[execute]type = " + actionType);
        this.mAdditionManager.execute(actionType, true, objArr);
        switch (m748getcommediatekcameraICameraMode$ActionTypeSwitchesValues()[actionType.ordinal()]) {
            case 1:
                stopVideoRecordingAsync(true);
                return true;
            case 2:
                this.mVideoModeHelper.doReturnToCaller(true, this.mCurrentVideoUri);
                return true;
            case 3:
                return onBackPressed();
            case 4:
                onCameraClose();
                setModeState(ICameraMode.ModeState.STATE_CLOSED);
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
                if (this.mRecordingView != null) {
                    this.mRecordingView.onOrientationChanged(((Integer) objArr[0]).intValue());
                }
                return true;
            case 8:
                this.mRecordingView.reInflate();
                return true;
            case 9:
                if (((KeyEvent) objArr[1]).getAction() == 0) {
                    return onKeyDown(((Integer) objArr[0]).intValue(), (KeyEvent) objArr[1]);
                }
                return true;
            case 10:
                onMediaEject();
                return true;
            case 11:
                onRestoreSettings();
                return true;
            case 12:
                if (objArr[0] != null && objArr[1] != null && objArr[2] != null) {
                    onSingleTapUp((View) objArr[0], ((Integer) objArr[1]).intValue(), ((Integer) objArr[2]).intValue());
                }
                return true;
            case 13:
                stopPreview();
                return true;
            case 14:
                return onUserInteraction();
            case 15:
                takeASnapshot();
                return true;
            case 16:
                onVideoShutterButtonClick();
                return true;
            default:
                return false;
        }
    }

    @Override // com.mediatek.camera.mode.CameraMode, com.mediatek.camera.ICameraMode
    public boolean open() {
        this.mAdditionManager.open(true);
        return true;
    }

    @Override // com.mediatek.camera.mode.CameraMode, com.mediatek.camera.ICameraMode
    public boolean close() {
        Log.m31d("VideoMode", "[close]...");
        this.mHandler.removeMessages(6);
        if (!this.mIModuleCtrl.isVideoCaptureIntent()) {
            this.mICameraAppUi.updateSnapShotUIView(false);
        }
        if (this.mIFocusManager != null) {
            this.mIFocusManager.removeMessages();
        }
        if (this.mRecordingView != null) {
            this.mRecordingView.uninit();
        }
        this.mIsMediaRecoderRecordingPaused = false;
        this.mIsAutoFocusCallback = false;
        this.mIsModeReleased = true;
        this.mIFocusManager = null;
        this.mICameraAppUi.setPhotoShutterEnabled(true);
        return true;
    }

    @Override // com.mediatek.camera.platform.IFocusManager.FocusListener
    public boolean capture() {
        return false;
    }

    @Override // com.mediatek.camera.platform.IFocusManager.FocusListener
    public void startFaceDetection() {
    }

    @Override // com.mediatek.camera.platform.IFocusManager.FocusListener
    public void stopFaceDetection() {
    }

    @Override // com.mediatek.camera.platform.IFocusManager.FocusListener
    public void setFocusParameters() {
        this.mIModuleCtrl.applyFocusParameters(!this.mIsAutoFocusCallback);
    }

    @Override // com.mediatek.camera.platform.IFocusManager.FocusListener
    public void playSound(int i) {
        if (i == 1 && this.mIsMediaRecorderRecording && (!this.mIsMediaRecoderRecordingPaused)) {
            Log.m34i("VideoMode", "[playSound]Don't play focus sound when recording");
        } else {
            this.mCameraSound.play(i);
        }
    }

    @Override // com.mediatek.camera.platform.IFocusManager.FocusListener
    public void autoFocus() {
        Log.m31d("VideoMode", "[autoFocus]");
        this.mFocusStartTime = System.currentTimeMillis();
        this.mICameraDevice.autoFocus(this.mAutoFocusCallback);
        setFocusState(1);
    }

    @Override // com.mediatek.camera.platform.IFocusManager.FocusListener
    public void cancelAutoFocus() {
        Log.m34i("VideoMode", "[cancelAutoFocus]  mICameraDevice = " + this.mICameraDevice + " mIsAutoFocusCallback = " + this.mIsAutoFocusCallback);
        if (!this.mIsAutoFocusCallback && this.mICameraDevice != null) {
            this.mICameraDevice.cancelAutoFocus();
            this.mIsAutoFocusCallback = true;
        }
        setFocusState(3);
        setFocusParameters();
        this.mIsAutoFocusCallback = false;
    }

    @Override // android.media.MediaRecorder.OnErrorListener
    public void onError(MediaRecorder mediaRecorder, int i, int i2) {
        Log.m32e("VideoMode", "[onError] what = " + i + ". extra = " + i2);
        if (1 == i || -1103 == i2) {
            stopVideoRecordingAsync(true);
        }
    }

    @Override // android.media.MediaRecorder.OnInfoListener
    public void onInfo(MediaRecorder mediaRecorder, int i, int i2) {
        int i3;
        Log.m35v("VideoMode", "[onInfo] what = " + i + "   extra = " + i2);
        switch (i) {
            case 800:
                stopVideoRecordingAsync(true);
                return;
            case 801:
                stopVideoRecordingAsync(true);
                this.mICameraAppUi.showToastForShort(R.string.video_reach_size_limit);
                return;
            case 895:
                long requestSizeLimit = this.mVideoModeHelper.getRequestSizeLimit(null, false);
                if (0 < requestSizeLimit && 100 >= (i3 = (int) ((i2 * 100) / requestSizeLimit))) {
                    this.mRecordingView.setCurrentSize(i2);
                    this.mRecordingView.setSizeProgress(i3);
                    return;
                }
                return;
            case 899:
                this.mICameraAppUi.showToast(R.string.video_bad_performance_auto_stop);
                stopVideoRecordingAsync(true);
                return;
            case 1998:
                this.mRecordingStartTime = SystemClock.uptimeMillis();
                updateRecordingTime();
                setEisFramesToRecorder();
                return;
            case 1999:
                if (this.mVideoSavingTask != null) {
                    synchronized (this.mVideoSavingTask) {
                        Log.m34i("VideoMode", "[onInfo] MediaRecorder camera released");
                        this.mVideoSavingTask.notifyAll();
                    }
                    return;
                }
                return;
            default:
                return;
        }
    }

    public ICameraMode.CameraModeType getCameraModeType() {
        return ICameraMode.CameraModeType.EXT_MODE_VIDEO;
    }

    public class SavingTask extends Thread {
        public SavingTask() {
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            boolean z;
            Log.m34i("VideoMode", "[SavingTask.run()] begin " + this + ", mMediaRecorderRecording = " + VideoMode.this.mIsMediaRecorderRecording);
            if (VideoMode.this.mIsMediaRecorderRecording) {
                try {
                    VideoMode.this.stopRecording();
                    VideoMode.this.mCurrentVideoFilename = VideoMode.this.mVideoFilename;
                    Log.m34i("VideoMode", "[SavingTask.run()] Setting current video filename: " + VideoMode.this.mCurrentVideoFilename);
                    z = false;
                } catch (RuntimeException e) {
                    Log.m33e("VideoMode", "[SavingTask.run()] stop fail", e);
                    z = true;
                    if (VideoMode.this.mVideoFilename != null) {
                        VideoMode.this.mVideoModeHelper.deleteVideoFile(VideoMode.this.mVideoFilename);
                    }
                }
            } else {
                z = false;
            }
            VideoMode.this.doAfterStopRecording(z);
            VideoMode.this.mIsMediaRecorderRecording = false;
            Log.m34i("VideoMode", "[SavingTask.run()] end " + this + ", mCurrentVideoUri = " + VideoMode.this.mCurrentVideoUri);
        }
    }

    protected void doOnCameraParameterReady(boolean z) {
        Log.m31d("VideoMode", "[onCameraParameterReady](" + z + ")");
        updateParameters();
        if (this.mIsMediaRecorderRecording) {
            Log.m31d("VideoMode", "mIsMediaRecorderRecording is true so not doOnCameraParameterReady");
            this.mAdditionManager.onCameraParameterReady(true);
            return;
        }
        this.mParameters = this.mICameraDevice.getParameters();
        this.mParameters.setColorEffect("none");
        this.mHandler.sendEmptyMessage(6);
        setFocusParameters();
        this.mContentResolver = this.mActivity.getContentResolver();
        if (!this.mIModuleCtrl.isNonePickIntent()) {
            keepScreenOnAwhile();
        }
        if (z) {
            startPreview();
        } else if (!this.mIModuleCtrl.isNonePickIntent()) {
            this.mVideoPreviewSizeRule.updateProfile();
        }
        restoreReviewIfNeed();
        this.mAdditionManager.onCameraParameterReady(true);
    }

    protected void initVideoRecordingFirst() {
        this.mIsRecordAudio = this.mVideoModeHelper.getMicrophone() ? !isSlowMotionIsOn() : false;
        Log.m31d("VideoMode", "[initVideoRecordingFirst], = ,mRecordAudio = " + this.mIsRecordAudio);
    }

    protected void doStartPreview() {
        Log.m31d("VideoMode", "[doStartPreview] mICameraDevice = " + this.mICameraDevice + "mIFocusManager = " + this.mIFocusManager);
        if (this.mICameraDevice != null) {
            this.mICameraDevice.startPreview();
            this.mICameraDevice.setAutoFocusMoveCallback(this.mAutoFocusMoveCallback);
        }
        if (this.mIFocusManager != null) {
            this.mIFocusManager.onPreviewStarted();
        }
    }

    protected void stopPreview() {
        Log.m31d("VideoMode", "[stopPreview]");
        if (this.mICameraDevice != null) {
            this.mICameraDevice.cancelAutoFocus();
            this.mICameraDevice.stopPreview();
        }
        this.mAdditionManager.execute(ICameraAddition.AdditionActionType.ACTION_ON_STOP_PREVIEW, new Object[0]);
    }

    protected boolean onVideoShutterButtonClick() throws IllegalStateException, IOException, IllegalArgumentException {
        Log.m34i("VideoMode", "[onVideoShutterButtonClick], mMediaRecorderRecording = " + this.mIsMediaRecorderRecording);
        if (ICameraMode.ModeState.STATE_CLOSED == getModeState()) {
            Log.m34i("VideoMode", "[onShutterButtonClick],mode state is closed,so return ");
            return false;
        }
        if (this.mIsMediaRecorderRecording) {
            this.mICameraAppUi.updataVideoRecordingManager(false);
            stopVideoRecordingAsync(true);
        } else {
            if (Storage.getLeftSpace() <= 0) {
                this.mICameraAppUi.restoreViewState();
                backToLastModeIfNeed();
                Log.m34i("VideoMode", "[onShutterButtonClick],Storage have no space ");
                return false;
            }
            if (!this.mIModuleCtrl.isNonePickIntent() && this.mIFocusManager != null) {
                this.mIFocusManager.resetTouchFocus();
                this.mIFocusManager.updateFocusUI();
            }
            this.mICameraAppUi.updataVideoRecordingManager(true);
            this.mICameraAppUi.setSwipeEnabled(false);
            startVideoRecording();
        }
        return true;
    }

    protected boolean startVideoRecording() throws IllegalStateException, IOException, IllegalArgumentException {
        Log.m34i("VideoMode", "[startVideoRecording()] mIsMediaRecorderRecording = " + this.mIsMediaRecorderRecording);
        if (ICameraMode.ModeState.STATE_IDLE != getModeState() || this.mIsMediaRecorderRecording) {
            Log.m34i("VideoMode", "[startVideoRecording()] current state is :" + getModeState());
            return false;
        }
        setModeState(ICameraMode.ModeState.STATE_RECORDING);
        updateViewState(true);
        initVideoRecordingFirst();
        initializeRecordingView();
        this.mCurrentVideoUri = null;
        initializeNormalRecorder();
        pauseAudioPlayback();
        this.mIModuleCtrl.previewStarted();
        if (!startRecording()) {
            Log.m34i("VideoMode", "[startVideoRecording()] mStartRecordingFailed.");
            this.mICameraAppUi.showToast(R.string.video_recording_error);
            this.mICameraAppUi.updataVideoRecordingManager(false);
            stopVideoRecordingAsync(true);
            this.mICameraAppUi.setVideoShutterMask(false);
            backToLastTheseCase();
            return false;
        }
        this.mIsMediaRecoderRecordingPaused = false;
        this.mRecordingPausedDuration = 0L;
        this.mTotalRecordingDuration = 0L;
        this.mIsRecorderCameraReleased = false;
        this.mStoppingAction = 1;
        this.mIsMediaRecorderRecording = true;
        this.mRecordingStartOrientation = this.mIModuleCtrl.getOrientation();
        this.mRecordingStartTime = SystemClock.uptimeMillis();
        updateRecordingTime();
        Log.m34i("VideoMode", " mIsParameterExtraCanUse = " + this.mIsParameterExtraCanUse);
        if (this.mIsParameterExtraCanUse) {
            this.mHandler.removeMessages(5);
            long requestSizeLimit = this.mVideoModeHelper.getRequestSizeLimit(null, false);
            if (requestSizeLimit > 0) {
                this.mRecordingView.setTotalSize(requestSizeLimit);
                this.mRecordingView.setCurrentSize(0L);
                this.mRecordingView.setSizeProgress(0);
                this.mRecordingView.setRecordingSizeVisible(true);
            }
        }
        keepScreenOn();
        Log.m31d("VideoMode", "[startVideoRecording()] end");
        return true;
    }

    protected void updateViewState(boolean z) {
        Log.m31d("VideoMode", "[updateViewState] hide:" + z);
        this.mICameraAppUi.setViewState(z ? ICameraAppUi.ViewState.VIEW_STATE_RECORDING : ICameraAppUi.ViewState.VIEW_STATE_NORMAL);
    }

    protected void initializeRecordingView() {
        this.mRecordingView.setRecordingIndicator(false);
        this.mRecordingView.setPauseResumeVisible(false);
        this.mRecordingView.show();
    }

    protected void initializeNormalRecorder() throws IllegalStateException, IOException, IllegalArgumentException {
        Log.m31d("VideoMode", "[initializeNormalRecorder()]");
        if (!this.mIModuleCtrl.isNonePickIntent()) {
            this.mVideoPreviewSizeRule.updateProfile();
        }
        this.mProfile = this.mVideoPreviewSizeRule.getProfile();
        initializeRequestedLimits();
        this.mMediaRecorder = new MediaRecorder();
        this.mICameraDevice.unlock();
        this.mMediaRecorder.setCamera(this.mICameraDevice.getCamera());
        if (this.mIsRecordAudio) {
            this.mMediaRecorder.setAudioSource(5);
        }
        this.mMediaRecorder.setVideoSource(1);
        try {
            this.mMediaRecorder.setOutputFormat(this.mProfile.fileFormat);
        } catch (Exception e) {
        }
        if (!isSlowMotionIsOn()) {
            this.mMediaRecorder.setVideoFrameRate(this.mProfile.videoFrameRate);
        }
        this.mMediaRecorder.setVideoEncodingBitRate(this.mProfile.videoBitRate);
        this.mMediaRecorder.setVideoSize(this.mProfile.videoFrameWidth, this.mProfile.videoFrameHeight);
        this.mMediaRecorder.setVideoEncoder(this.mProfile.videoCodec);
        MediaRecorderEx.setVideoBitOffSet(this.mMediaRecorder, 1, true);
        if (this.mIsRecordAudio) {
            this.mMediaRecorder.setAudioEncodingBitRate(this.mProfile.audioBitRate);
            this.mMediaRecorder.setAudioChannels(this.mProfile.audioChannels);
            this.mMediaRecorder.setAudioSamplingRate(this.mProfile.audioSampleRate);
            this.mMediaRecorder.setAudioEncoder(this.mProfile.audioCodec);
        }
        this.mMediaRecorder.setMaxDuration(this.mRequestDurationLimit * 1000);
        Location location = this.mIModuleCtrl.getLocation();
        if (location != null) {
            this.mMediaRecorder.setLocation((float) location.getLatitude(), (float) location.getLongitude());
        }
        try {
            this.mMediaRecorder.setMaxFileSize(this.mVideoModeHelper.getRecorderMaxSize(this.mRequestSizeLimit));
        } catch (RuntimeException e2) {
            Log.m37w("VideoMode", "initializeNormalRecorder()", e2);
        }
        Log.m31d("VideoMode", "[initializeNormalRecorder()], mVideoFileDescriptor = " + this.mVideoFileDescriptor);
        if (this.mVideoFileDescriptor != null) {
            this.mMediaRecorder.setOutputFile(this.mVideoFileDescriptor.getFileDescriptor());
        } else {
            generateVideoFilename(this.mProfile.fileFormat, null);
            this.mMediaRecorder.setOutputFile(this.mVideoFilename);
        }
        setSlowMotionVideoFileSpeed(this.mMediaRecorder, this.mProfile);
        setMediaRecorderParameters(this.mMediaRecorder);
        setOrientationHint(Util.getRecordingRotation(this.mIModuleCtrl.getOrientation(), this.mICameraDeviceManager.getCurrentCameraId(), this.mICameraDeviceManager.getCameraInfo(this.mICameraDeviceManager.getCurrentCameraId())));
        try {
            this.mMediaRecorder.prepare();
            this.mMediaRecorder.setOnErrorListener(this);
            this.mMediaRecorder.setOnInfoListener(this);
            this.mMediaRecorder.setOnCameraReleasedListener(this);
            this.mHandler.removeCallbacks(this.mReleaseOnInfoListener);
        } catch (IOException e3) {
            Log.m33e("VideoMode", "[initializeNormalRecorder()] prepare failed", e3);
            releaseMediaRecorder();
            throw new RuntimeException(e3);
        }
    }

    protected void pauseAudioPlayback() {
        Log.m31d("VideoMode", "[pauseAudioPlayback]");
        AudioManager audioManager = (AudioManager) this.mActivity.getSystemService("audio");
        if (audioManager != null) {
            audioManager.requestAudioFocus(null, 3, 1);
        }
    }

    protected void releaseAudioFocus() {
        Log.m31d("VideoMode", "[releaseAudioFocus]");
        AudioManager audioManager = (AudioManager) this.mActivity.getSystemService("audio");
        if (audioManager != null) {
            audioManager.abandonAudioFocus(null);
        }
    }

    protected boolean startRecording() throws IllegalStateException {
        boolean zStartNormalRecording = startNormalRecording();
        this.mICameraAppUi.setVideoShutterMask(true);
        return zStartNormalRecording;
    }

    protected boolean startNormalRecording() throws IllegalStateException {
        Log.m31d("VideoMode", "[startNormalRecording()]");
        this.mIsSetEisFrams = false;
        try {
            this.mMediaRecorder.start();
            this.mICameraDevice.fetchParametersFromServer();
            return true;
        } catch (RuntimeException e) {
            Log.m33e("VideoMode", "[startNormalRecording()] Could not start media recorder. ", e);
            releaseMediaRecorder();
            this.mICameraDevice.lock();
            return false;
        }
    }

    protected void updateRecordingTime() {
        if (!this.mIsMediaRecorderRecording) {
            return;
        }
        this.mTotalRecordingDuration = SystemClock.uptimeMillis() - this.mRecordingStartTime;
        if (this.mIsMediaRecoderRecordingPaused) {
            this.mTotalRecordingDuration = this.mRecordingPausedDuration;
        }
        this.mRecordingView.showTime(this.mTotalRecordingDuration, false);
        this.mCurrentShowIndicator = 1 - this.mCurrentShowIndicator;
        if (this.mIsMediaRecoderRecordingPaused && 1 == this.mCurrentShowIndicator) {
            this.mRecordingView.setTimeVisible(false);
        } else {
            this.mRecordingView.setTimeVisible(true);
        }
        long j = 500;
        if (!this.mIsMediaRecoderRecordingPaused) {
            j = 1000 - (this.mTotalRecordingDuration % 1000);
        }
        Log.m31d("VideoMode", "[updateRecordingTime()],actualNextUpdateDelay = " + j);
        this.mHandler.sendEmptyMessageDelayed(5, j);
    }

    protected void stopVideoRecordingAsync(boolean z) {
        Log.m34i("VideoMode", "[stopVideoRecordingAsync()] mMediaRecorderRecording = " + this.mIsMediaRecorderRecording + ",needShowSavingUi = " + z);
        if (ICameraMode.ModeState.STATE_RECORDING != getModeState() || (!this.mIsMediaRecorderRecording)) {
            Log.m34i("VideoMode", "[stopVideoRecordingAsync] current state is " + getModeState());
            return;
        }
        releaseAudioFocus();
        setModeState(ICameraMode.ModeState.STATE_SAVING);
        this.mICameraAppUi.setVideoShutterMask(false);
        this.mHandler.removeMessages(5);
        initializeRecordingView();
        this.mTotalRecordingDuration = 0L;
        if (!this.mIsTimeLapseEnable) {
            this.mRecordingView.showTime(this.mTotalRecordingDuration, false);
        }
        this.mRecordingView.setTimeVisible(true);
        this.mICameraAppUi.setVideoShutterEnabled(false);
        if (z) {
            this.mHandler.sendEmptyMessage(9);
            if (this.mIsSetEisFrams) {
                this.mParameters.set("eis25-mode", 0);
                this.mICameraDevice.applyParameters();
            }
        }
        this.mVideoSavingTask = new SavingTask();
        this.mVideoSavingTask.start();
        Log.m31d("VideoMode", "[stopVideoRecordingAsync()] end of stopVideoRecordingAsync");
    }

    protected void stopVideoOnPause() throws IOException {
        boolean zHasCallbacks;
        boolean zIsVideoProcessing = false;
        Log.m31d("VideoMode", "[stopVideoOnPause()] mMediaRecorderRecording =  " + this.mIsMediaRecorderRecording);
        if (this.mIsMediaRecorderRecording) {
            if (!this.mIModuleCtrl.isNonePickIntent()) {
                this.mStoppingAction = 4;
            }
            this.mICameraAppUi.updataVideoRecordingManager(false);
            stopVideoRecordingAsync(false);
            zIsVideoProcessing = isVideoProcessing();
        } else {
            releaseMediaRecorder();
            if (this.mVideoSavingTask != null) {
                synchronized (this.mVideoSavingTask) {
                    zHasCallbacks = this.mHandler.hasCallbacks(this.mVideoSavedRunnable);
                    Log.m31d("VideoMode", "[stopVideoOnPause()] has mVideoSavedRunnable = " + zHasCallbacks);
                }
            } else {
                zHasCallbacks = false;
            }
            if (this.mIModuleCtrl.isNonePickIntent() && (!isVideoProcessing())) {
                boolean z = !zHasCallbacks;
            }
        }
        if (zIsVideoProcessing) {
            waitForRecorder();
        } else if (ICameraMode.ModeState.STATE_IDLE == getModeState()) {
            closeVideoFileDescriptor();
        }
        Log.m31d("VideoMode", "[stopVideoOnPause()]  videoSaving = " + zIsVideoProcessing + ", mVideoSavingTask = " + this.mVideoSavingTask + ", mMediaRecorderRecording = " + this.mIsMediaRecorderRecording);
    }

    protected void stopRecording() throws IllegalStateException {
        Log.m34i("VideoMode", "[stopRecording] begin");
        this.mMediaRecorder.stop();
        this.mMediaRecorder.setOnCameraReleasedListener(null);
        Log.m34i("VideoMode", "[stopRecording] end");
    }

    protected void doAfterStopRecording(boolean z) {
        Log.m34i("VideoMode", "[doAfterStopRecording],fail = " + z);
        if (!this.mIModuleCtrl.isNonePickIntent()) {
            if (!z && 3 != this.mStoppingAction) {
                if (this.mIModuleCtrl.isQuickCapture()) {
                    this.mStoppingAction = 2;
                } else {
                    this.mStoppingAction = 4;
                }
            } else if (z) {
                this.mStoppingAction = 5;
            }
        } else if (z) {
            this.mStoppingAction = 5;
        }
        releaseMediaRecorder();
        if (!z) {
            addVideoToMediaStore();
        }
        synchronized (this.mVideoSavingTask) {
            this.mVideoSavingTask.notifyAll();
            this.mHandler.removeCallbacks(this.mVideoSavedRunnable);
            this.mHandler.post(this.mVideoSavedRunnable);
        }
    }

    protected void releaseMediaRecorder() {
        Log.m31d("VideoMode", "[releaseMediaRecorder()] mMediaRecorder = " + this.mMediaRecorder + " mRecorderCameraReleased = " + this.mIsRecorderCameraReleased);
        if (this.mMediaRecorder != null && (!this.mIsRecorderCameraReleased)) {
            cleanupEmptyFile();
            this.mMediaRecorder.reset();
            this.mMediaRecorder.release();
            this.mIsRecorderCameraReleased = true;
            this.mHandler.post(this.mReleaseOnInfoListener);
        }
        this.mVideoFilename = null;
    }

    protected void addVideoToMediaStore() {
        if (this.mVideoFileDescriptor == null) {
            this.mIFileSaver.init(IFileSaver.FILE_TYPE.VIDEO, this.mProfile.fileFormat, Integer.toString(this.mProfile.videoFrameWidth) + "x" + Integer.toString(this.mProfile.videoFrameHeight), Util.getRecordingRotation(this.mRecordingStartOrientation, this.mICameraDeviceManager.getCurrentCameraId(), this.mICameraDeviceManager.getCameraInfo(this.mICameraDeviceManager.getCurrentCameraId())));
            this.mIFileSaver.saveVideoFile(this.mIModuleCtrl.getLocation(), this.mVideoTempPath, computeDuration(), 0, this.mFileSavedListener);
            this.mCurrentVideoFilename = this.mIFileSaver.getVideoSaveRequest().getFilePath();
            Log.m31d("VideoMode", "[addVideoToMediaStore] mCurrentVideoFilename =  " + this.mCurrentVideoFilename);
        }
    }

    protected void backToLastModeIfNeed() {
        Log.m31d("VideoMode", "[backToLastModeIfNeed()]");
        if (this.mIModuleCtrl.isVideoCaptureIntent()) {
            if (!this.mICameraAppUi.getCameraView(ICameraAppUi.CommonUiType.REVIEW).isShowing()) {
                this.mICameraAppUi.switchShutterType(ICameraAppUi.ShutterButtonType.SHUTTER_TYPE_VIDEO);
            }
        } else {
            this.mIModuleCtrl.backToLastMode();
            this.mICameraAppUi.setPhotoShutterEnabled(true);
        }
    }

    protected boolean takeASnapshot() {
        Log.m31d("VideoMode", "[takeASnapshot]");
        if (ICameraMode.ModeState.STATE_RECORDING != getModeState()) {
            Log.m34i("VideoMode", "[takeASnapshot] Video snapshot fail state = " + getModeState());
            return false;
        }
        Toast.makeText(this.mActivity, this.mActivity.getResources().getString(R.string.saving), 0).show();
        this.mICameraAppUi.updateSnapShotUIView(true);
        this.mICameraAppUi.setPhotoShutterEnabled(false);
        this.mICameraDevice.takePicture(null, null, null, new JpegPictureCallback());
        return true;
    }

    public final class JpegPictureCallback implements Camera.PictureCallback {
        public JpegPictureCallback() {
        }

        @Override // android.hardware.Camera.PictureCallback
        public void onPictureTaken(byte[] bArr, Camera camera) {
            Log.m31d("VideoMode", "[onPictureTaken]");
            if (bArr == null) {
                VideoMode.this.mHandler.sendEmptyMessage(15);
                Log.m34i("VideoMode", "[onPictureTaken],data is null,return");
                return;
            }
            VideoMode.this.mIFileSaver.init(IFileSaver.FILE_TYPE.JPEG, 0, null, -1);
            long jCurrentTimeMillis = System.currentTimeMillis();
            String str = Util.createNameFormat(jCurrentTimeMillis, VideoMode.this.mActivity.getString(R.string.image_file_name_format)) + ".jpg";
            VideoMode.this.mIFileSaver.savePhotoFile(bArr, null, jCurrentTimeMillis, VideoMode.this.mIModuleCtrl.getLocation(), 0, null);
            VideoMode.this.mHandler.sendEmptyMessage(15);
        }
    }

    protected void initializeShutterStatus() {
        if (this.mIsModeReleased) {
            Log.m31d("VideoMode", "[initializeShutterStatus] mode is closed,so return");
        } else {
            this.mICameraAppUi.setPhotoShutterEnabled(this.mParameters.isVideoSnapshotSupported());
        }
    }

    protected void pauseVideoRecording() throws IllegalStateException {
        Log.m31d("VideoMode", "[pauseVideoRecording()] mMediaRecorderRecording = " + this.mIsMediaRecorderRecording + " mMediaRecoderRecordingPaused = " + this.mIsMediaRecoderRecordingPaused);
        this.mRecordingView.setRecordingIndicator(false);
        if (this.mIsMediaRecorderRecording && (!this.mIsMediaRecoderRecordingPaused)) {
            try {
                this.mMediaRecorder.pause();
            } catch (IllegalStateException e) {
                Log.m32e("VideoMode", "[pauseVideoRecording()] Could not pause media recorder. ");
            }
            this.mRecordingPausedDuration = SystemClock.uptimeMillis() - this.mRecordingStartTime;
            this.mIsMediaRecoderRecordingPaused = true;
        }
    }

    protected boolean onUserInteraction() {
        Log.m31d("VideoMode", "[onUserInteraction] mMediaRecorderRecording = " + this.mIsMediaRecorderRecording);
        return this.mIsMediaRecorderRecording;
    }

    protected void onSingleTapUp(View view, int i, int i2) {
        Log.m34i("VideoMode", "[onSingleTapUp] view = " + view + ",x = " + i + ",y = " + i2);
        if (ICameraMode.ModeState.STATE_UNKNOWN == getModeState() || ICameraAppUi.ViewState.VIEW_STATE_PRE_RECORDING == this.mICameraAppUi.getViewState()) {
            return;
        }
        String focusMode = this.mIFocusManager != null ? this.mIFocusManager.getFocusMode() : null;
        if (focusMode == null || "infinity".equals(focusMode) || "continuous-picture".equals(focusMode)) {
            Log.m34i("VideoMode", "[onSingleTapUp] focus mode is error ,so return");
            return;
        }
        if (ICameraMode.ModeState.STATE_CLOSED == getModeState() || this.mICameraDevice == null) {
            Log.m34i("VideoMode", "[onSingleTapUp] mode state is closed or cameraDevice is null,so return");
            return;
        }
        if (!this.mIFocusManager.getFocusAreaSupported()) {
            Log.m34i("VideoMode", "[onSingleTapUp] focusArea is not supported");
            return;
        }
        Log.m34i("VideoMode", "[onSingleTapUp](" + i + ", " + i2 + "),focusMode = " + focusMode + ",mMediaRecorderRecording = " + this.mIsMediaRecorderRecording);
        if (this.mIsMediaRecorderRecording) {
            setFocusState(-1);
        }
        this.mIFocusManager.onSingleTapUp(i, i2);
    }

    protected void onMediaEject() {
        stopVideoRecordingAsync(true);
    }

    protected boolean onBackPressed() {
        Log.m31d("VideoMode", "[onBackPressed()] CurrentModeState " + getModeState());
        if (ICameraMode.ModeState.STATE_IDLE == getModeState()) {
            return false;
        }
        if (ICameraMode.ModeState.STATE_RECORDING == getModeState()) {
            stopVideoRecordingAsync(true);
        }
        return true;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Removed duplicated region for block: B:6:0x0033 A[RETURN] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    protected boolean onKeyDown(int r6, android.view.KeyEvent r7) throws java.lang.IllegalStateException, java.io.IOException, java.lang.IllegalArgumentException {
        /*
            r5 = this;
            r4 = 0
            r3 = 1
            java.lang.String r0 = "VideoMode"
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "keyCode = "
            java.lang.StringBuilder r1 = r1.append(r2)
            java.lang.StringBuilder r1 = r1.append(r6)
            java.lang.String r2 = " event = "
            java.lang.StringBuilder r1 = r1.append(r2)
            java.lang.StringBuilder r1 = r1.append(r7)
            java.lang.String r1 = r1.toString()
            com.mediatek.camera.util.Log.m31d(r0, r1)
            com.mediatek.camera.ICameraMode$ModeState r0 = com.mediatek.camera.ICameraMode.ModeState.STATE_CLOSED
            com.mediatek.camera.ICameraMode$ModeState r1 = r5.getModeState()
            if (r0 != r1) goto L30
            return r3
        L30:
            switch(r6) {
                case 23: goto L34;
                case 27: goto L34;
                case 82: goto L4c;
                default: goto L33;
            }
        L33:
            return r4
        L34:
            int r0 = r7.getRepeatCount()
            if (r0 != 0) goto L33
            com.mediatek.camera.platform.ICameraAppUi r0 = r5.mICameraAppUi
            com.mediatek.camera.platform.ICameraAppUi$CommonUiType r1 = com.mediatek.camera.platform.ICameraAppUi.CommonUiType.REVIEW
            com.mediatek.camera.platform.ICameraView r0 = r0.getCameraView(r1)
            boolean r0 = r0.isShowing()
            if (r0 != 0) goto L4b
            r5.onVideoShutterButtonClick()
        L4b:
            return r3
        L4c:
            boolean r0 = r5.mIsMediaRecorderRecording
            if (r0 == 0) goto L33
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mediatek.camera.mode.VideoMode.onKeyDown(int, android.view.KeyEvent):boolean");
    }

    protected String generateVideoFilename(int i, String str) {
        if (!Storage.isStorageReady()) {
            Storage.initializeStorageState();
        }
        String str2 = "videorecorder" + this.mVideoModeHelper.convertOutputFormatToFileExt(i);
        if (str == null) {
            this.mVideoTempPath = Storage.getFileDirectory() + '/' + str2 + ".tmp";
        } else {
            this.mVideoTempPath = Storage.getFileDirectory() + '/' + str2 + "_" + str + ".tmp";
        }
        this.mVideoFilename = this.mVideoTempPath;
        Log.m31d("VideoMode", "[generateVideoFilename] mVideoFilename = " + this.mVideoFilename);
        return this.mVideoFilename;
    }

    protected void cleanupEmptyFile() {
        if (this.mVideoFilename != null) {
            File file = new File(this.mVideoFilename);
            if (file.length() == 0 && file.delete()) {
                Log.m31d("VideoMode", "[cleanupEmptyFile] Empty video file deleted: " + this.mVideoFilename);
                this.mVideoFilename = null;
            }
        }
    }

    protected void onRestoreSettings() {
        if (!this.mIModuleCtrl.isNonePickIntent()) {
            updateViewState(false);
        } else {
            this.mICameraAppUi.setPhotoShutterEnabled(true);
        }
    }

    protected void onCameraClose() throws IOException {
        Log.m31d("VideoMode", "[onCameraClose()]");
        this.mHandler.removeMessages(5);
        this.mHandler.removeMessages(15);
        if (!this.mIModuleCtrl.isVideoCaptureIntent()) {
            this.mICameraAppUi.updateSnapShotUIView(false);
        }
        if (this.mFocusState == 1) {
            this.mICameraAppUi.restoreViewState();
            this.mFocusState = 0;
        }
        if (this.mFocusState == 1) {
            this.mICameraAppUi.restoreViewState();
            this.mFocusState = 0;
        }
        if (this.mFocusState == 1) {
            this.mICameraAppUi.restoreViewState();
            this.mFocusState = 0;
        }
        this.mIsAutoFocusCallback = false;
        if (this.mIFocusManager != null) {
            this.mIFocusManager.onPreviewStopped();
        }
        stopVideoOnPause();
        if (this.mICameraDevice == null) {
            return;
        }
        resetScreenOn();
    }

    protected void setOrientationHint(int i) {
        if (this.mMediaRecorder != null) {
            this.mMediaRecorder.setOrientationHint(i);
        }
    }

    protected boolean isVideoProcessing() {
        boolean zIsAlive = this.mVideoSavingTask != null ? this.mVideoSavingTask.isAlive() : false;
        Log.m31d("VideoMode", "[isVideoProcessing] : " + zIsAlive);
        return zIsAlive;
    }

    protected void waitForRecorder() {
        synchronized (this.mVideoSavingTask) {
            while (!this.mIsRecorderCameraReleased) {
                try {
                    Log.m31d("VideoMode", "[waitForRecorder] wait for releasing camera done in MediaRecorder");
                    this.mVideoSavingTask.wait();
                } catch (InterruptedException e) {
                    Log.m37w("VideoMode", "[waitForRecorder] Got notify from Media recorder()", e);
                }
            }
        }
    }

    protected long computeDuration() throws IOException {
        long duration = this.mVideoModeHelper.getDuration(this.mCurrentVideoFilename);
        Log.m31d("VideoMode", "[computeDuration()] return " + duration);
        return duration;
    }

    protected void deleteCurrentVideo() {
        Log.m31d("VideoMode", "[deleteCurrentVideo()] mCurrentVideoFilename = " + this.mCurrentVideoFilename + " mSaveTempVideo = " + this.mSaveTempVideo);
        if (this.mCurrentVideoFilename != null) {
            if (this.mSaveTempVideo > 0) {
                this.mVideoModeHelper.renameVideoFile(this.mCurrentVideoFilename);
            } else {
                this.mVideoModeHelper.deleteVideoFile(this.mCurrentVideoFilename);
            }
            this.mCurrentVideoFilename = null;
            if (this.mCurrentVideoUri != null) {
                this.mContentResolver.delete(this.mCurrentVideoUri, null, null);
                this.mCurrentVideoUri = null;
            }
        }
    }

    protected void setSlowMotionVideoFileSpeed(MediaRecorder mediaRecorder, CamcorderProfile camcorderProfile) throws IllegalStateException {
        if (isSlowMotionIsOn()) {
            mediaRecorder.setVideoFrameRate(30);
            mediaRecorder.setCaptureRate(camcorderProfile.videoFrameRate);
        }
    }

    private boolean isSlowMotionIsOn() {
        return "on".equals(this.mISettingCtrl.getSettingValue("pref_slow_motion_key"));
    }

    protected void updateParameters() {
        super.updateDevice();
        super.updateFocusManager();
        if (this.mIFocusManager != null) {
            this.mIFocusManager.setListener(this);
        }
    }

    private void startPreview() {
        Log.m31d("VideoMode", "[startPreview]...");
        this.mActivity.runOnUiThread(new Runnable() { // from class: com.mediatek.camera.mode.VideoMode.9
            @Override // java.lang.Runnable
            public void run() {
                if (VideoMode.this.mIFocusManager != null) {
                    VideoMode.this.mIFocusManager.resetTouchFocus();
                }
            }
        });
        stopPreview();
        doStartPreview();
        this.mAdditionManager.execute(ICameraAddition.AdditionActionType.ACTION_ON_START_PREVIEW, new Object[0]);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showAlert() throws IOException {
        Log.m31d("VideoMode", "[showAlert()]");
        if (Storage.isStorageReady()) {
            if (this.mVideoFileDescriptor != null) {
                this.mICameraAppUi.showReview(null, this.mVideoFileDescriptor.getFileDescriptor());
            } else if (this.mCurrentVideoFilename != null) {
                this.mICameraAppUi.showReview(this.mCurrentVideoFilename, null);
            }
            this.mICameraAppUi.setReviewListener(this.mRetakeListener, this.mReviewPlayListener);
            this.mICameraAppUi.switchShutterType(ICameraAppUi.ShutterButtonType.SHUTTER_TYPE_OK_CANCEL);
            if (ICameraMode.ModeState.STATE_CLOSED == getModeState()) {
                closeVideoFileDescriptor();
            }
        }
    }

    private class MainHandler extends Handler {
        public MainHandler(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            Log.m31d("VideoMode", "[MainHandler.handleMessage](" + message + ")");
            switch (message.what) {
                case 5:
                    VideoMode.this.updateRecordingTime();
                    break;
                case 6:
                    VideoMode.this.initializeShutterStatus();
                    break;
                case FrameMetricsAggregator.DELAY_INDEX /* 7 */:
                    VideoMode.this.resetScreenOn();
                    break;
                case 9:
                    VideoMode.this.mICameraAppUi.showProgress(VideoMode.this.mActivity.getResources().getString(R.string.saving));
                    break;
                case 15:
                    if (!VideoMode.this.mIModuleCtrl.isVideoCaptureIntent()) {
                        VideoMode.this.mICameraAppUi.updateSnapShotUIView(false);
                    }
                    VideoMode.this.mICameraAppUi.setPhotoShutterEnabled(true);
                    break;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void resetScreenOn() {
        Log.m31d("VideoMode", "[resetScreenOn()]");
        this.mActivity.runOnUiThread(new Runnable() { // from class: com.mediatek.camera.mode.VideoMode.10
            @Override // java.lang.Runnable
            public void run() {
                VideoMode.this.mHandler.removeMessages(7);
                VideoMode.this.mActivity.getWindow().clearFlags(128);
            }
        });
    }

    private void keepScreenOnAwhile() {
        this.mActivity.runOnUiThread(new Runnable() { // from class: com.mediatek.camera.mode.VideoMode.11
            @Override // java.lang.Runnable
            public void run() {
                Log.m31d("VideoMode", "[keepScreenOnAwhile()]");
                VideoMode.this.mHandler.removeMessages(7);
                VideoMode.this.mActivity.getWindow().addFlags(128);
                VideoMode.this.mHandler.sendEmptyMessageDelayed(7, 120000L);
            }
        });
    }

    private void keepScreenOn() {
        Log.m31d("VideoMode", "[keepScreenOn()]");
        this.mActivity.runOnUiThread(new Runnable() { // from class: com.mediatek.camera.mode.VideoMode.12
            @Override // java.lang.Runnable
            public void run() {
                if (VideoMode.this.mActivity.getWindow() != null) {
                    VideoMode.this.mHandler.removeMessages(7);
                    VideoMode.this.mActivity.getWindow().addFlags(128);
                }
            }
        });
    }

    private void initializeRequestedLimits() throws IOException {
        closeVideoFileDescriptor();
        initializeLimiteds();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void closeVideoFileDescriptor() throws IOException {
        this.mVideoModeHelper.closeVideoFileDescriptor(this.mVideoFileDescriptor);
        this.mVideoFileDescriptor = null;
    }

    private void restoreReviewIfNeed() {
        Uri uri;
        if (this.mICameraAppUi.getCameraView(ICameraAppUi.CommonUiType.REVIEW).isShowing() && !this.mIModuleCtrl.isNonePickIntent() && this.mVideoFileDescriptor == null && (uri = (Uri) this.mIModuleCtrl.getIntent().getParcelableExtra("output")) != null) {
            try {
                if (uri.toString().startsWith("content://mms_temp_file")) {
                    this.mVideoFileDescriptor = this.mContentResolver.openFileDescriptor(uri, "r");
                } else {
                    this.mVideoFileDescriptor = this.mContentResolver.openFileDescriptor(uri, "rw");
                }
            } catch (FileNotFoundException e) {
                Log.m33e("VideoMode", "initializeNormalRecorder()", e);
            }
        }
        Log.m31d("VideoMode", "[restoreReviewIfNeed()]  mVideoFileDescriptor = " + this.mVideoFileDescriptor + ", mCurrentVideoFilename = " + this.mCurrentVideoFilename);
    }

    private void backToLastTheseCase() {
        this.mICameraAppUi.restoreViewState();
        if (this.mIModuleCtrl.isVideoCaptureIntent()) {
            if (!this.mICameraAppUi.getCameraView(ICameraAppUi.CommonUiType.REVIEW).isShowing()) {
                this.mICameraAppUi.switchShutterType(ICameraAppUi.ShutterButtonType.SHUTTER_TYPE_VIDEO);
            }
        } else {
            this.mIModuleCtrl.backToLastMode();
            this.mICameraAppUi.setPhotoShutterEnabled(true);
        }
    }

    private void initializeLimiteds() {
        Uri uri;
        this.mRequestSizeLimit = this.mVideoModeHelper.getRequestSizeLimit(this.mProfile, true);
        this.mRequestDurationLimit = this.mVideoModeHelper.getRequestDurationLimited();
        if (this.mIModuleCtrl.isVideoCaptureIntent() && (uri = (Uri) this.mIModuleCtrl.getIntent().getParcelableExtra("output")) != null) {
            try {
                this.mVideoFileDescriptor = this.mContentResolver.openFileDescriptor(uri, "rw");
                this.mCurrentVideoUri = uri;
            } catch (FileNotFoundException e) {
                Log.m32e("VideoMode", e.toString());
            }
        }
    }

    private void setVideoRule() {
        VideoFaceBeautyRule videoFaceBeautyRule = new VideoFaceBeautyRule(this.mICameraContext);
        this.mISettingCtrl.addRule("video_key", "pref_slow_motion_key", videoFaceBeautyRule);
        videoFaceBeautyRule.addLimitation("on", null, null);
        VfbQualityRule vfbQualityRule = new VfbQualityRule(this.mICameraContext, "face_beauty_key");
        this.mISettingCtrl.addRule("video_key", "pref_video_quality_key", vfbQualityRule);
        vfbQualityRule.addLimitation("on", null, null);
        this.mVideoPreviewSizeRule = new VideoPreviewRule(this.mICameraContext, getCameraModeType());
        this.mISettingCtrl.addRule(this.mVideoPreviewSizeRule.getConditionKey(), "pref_camera_picturesize_ratio_key", this.mVideoPreviewSizeRule);
        this.mVideoPreviewSizeRule.addLimitation("on", null, null);
        this.mVideoHdrRul = new VideoHdrRule(this.mICameraContext);
        this.mISettingCtrl.addRule("video_key", "pref_hdr_key", this.mVideoHdrRul);
        this.mVideoHdrRul.addLimitation("off ", null, null);
    }

    private final class AutoFocusCallback implements Camera.AutoFocusCallback {
        /* synthetic */ AutoFocusCallback(VideoMode videoMode, AutoFocusCallback autoFocusCallback) {
            this();
        }

        private AutoFocusCallback() {
        }

        @Override // android.hardware.Camera.AutoFocusCallback
        public void onAutoFocus(boolean z, Camera camera) {
            Log.m31d("VideoMode", "mAutoFocusTime = " + (System.currentTimeMillis() - VideoMode.this.mFocusStartTime) + "ms,mFocusManager.onAutoFocus( " + z + ")");
            if (ICameraMode.ModeState.STATE_CLOSED == VideoMode.this.getModeState() || VideoMode.this.mIFocusManager == null) {
                return;
            }
            VideoMode.this.setFocusState(2);
            VideoMode.this.mIFocusManager.onAutoFocus(z);
            VideoMode.this.mIsAutoFocusCallback = true;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void changeFocusState() {
        Log.m31d("VideoMode", "[changeFocusState()]");
        if (this.mICameraDevice != null) {
            this.mICameraDevice.cancelAutoFocus();
        }
        this.mIsAutoFocusCallback = false;
        if (this.mIFocusManager != null) {
            this.mIFocusManager.resetTouchFocus();
            this.mIFocusManager.updateFocusUI();
        }
        setFocusParameters();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setFocusState(int i) {
        Log.m31d("VideoMode", "[setFocusState](" + i + ") mMediaRecorderRecording = " + this.mIsMediaRecorderRecording);
        this.mFocusState = i;
        if (this.mIsMediaRecorderRecording || ICameraMode.ModeState.STATE_CLOSED == getModeState() || ICameraAppUi.ViewState.VIEW_STATE_PRE_RECORDING == this.mICameraAppUi.getViewState()) {
            return;
        }
        switch (i) {
            case 1:
                this.mICameraAppUi.setViewState(ICameraAppUi.ViewState.VIEW_STATE_FOCUSING);
                break;
            case 2:
            case 3:
                if (ICameraAppUi.ViewState.VIEW_STATE_REVIEW != this.mICameraAppUi.getViewState()) {
                    updateViewState(false);
                    break;
                }
                break;
        }
    }

    private void setMediaRecorderParameters(MediaRecorder mediaRecorder) {
        try {
            Util.setParametersExtra(mediaRecorder, "media-recorder-info=1998");
            Util.setParametersExtra(mediaRecorder, "media-recorder-info=1999");
            if (this.mIModuleCtrl.isVideoCaptureIntent()) {
                Util.setParametersExtra(mediaRecorder, "media-recorder-info=895");
            }
            this.mIsParameterExtraCanUse = true;
        } catch (Exception e) {
            this.mIsParameterExtraCanUse = false;
            e.printStackTrace();
        }
        try {
            Util.setParametersExtra(mediaRecorder, "media-recorder-info=899");
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    private void setEisFramesToRecorder() {
        if ("on".equals(this.mISettingCtrl.getSettingValue("pref_video_eis_key"))) {
            this.mParameters = this.mICameraDevice.getParameters();
            if (this.mParameters == null || this.mParameters.get("eis-supported-frames") == null) {
                Log.m34i("VideoMode", "mParameters or eis-supported-frames is null");
                return;
            }
            Log.m34i("VideoMode", "eis-supported-frames =" + this.mParameters.get("eis-supported-frames"));
            try {
                Util.setParametersExtra(this.mMediaRecorder, "media-param-eis=" + this.mParameters.get("eis-supported-frames"));
                this.mIsSetEisFrams = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
