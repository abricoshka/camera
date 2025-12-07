package com.mediatek.camera.p005v2.mode.pip;

import android.content.ContentValues;
import android.graphics.RectF;
import android.hardware.camera2.CaptureRequest;
import android.media.CamcorderProfile;
import android.net.Uri;
import android.os.Handler;
import android.util.Size;
import android.view.Surface;
import android.view.ViewGroup;
import com.mediatek.camera.R;
import com.mediatek.camera.debug.LogHelper;
import com.mediatek.camera.p005v2.control.IControl$IAaaController;
import com.mediatek.camera.p005v2.mode.AbstractCameraMode;
import com.mediatek.camera.p005v2.mode.ModeController;
import com.mediatek.camera.p005v2.mode.pip.combination.FdAffectedRule;
import com.mediatek.camera.p005v2.mode.pip.combination.PictureSizeRule;
import com.mediatek.camera.p005v2.mode.pip.combination.PreviewSizeRule;
import com.mediatek.camera.p005v2.mode.pip.combination.VideoQualityRule;
import com.mediatek.camera.p005v2.module.ModuleListener;
import com.mediatek.camera.p005v2.platform.app.AppController;
import com.mediatek.camera.p005v2.services.FileSaver;
import com.mediatek.camera.p005v2.setting.ISettingRule;
import com.mediatek.camera.p005v2.setting.ISettingServant;
import com.mediatek.camera.p005v2.setting.SettingItem;
import com.mediatek.camera.p005v2.stream.ICaptureStream;
import com.mediatek.camera.p005v2.stream.IPreviewStream;
import com.mediatek.camera.p005v2.stream.IRecordStream;
import com.mediatek.camera.p005v2.stream.ImageInfo;
import com.mediatek.camera.p005v2.stream.StreamManager;
import com.mediatek.camera.p005v2.stream.pip.IPipStream;
import com.mediatek.camera.p005v2.util.Utils;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/* loaded from: classes.dex */
public class PipMode extends AbstractCameraMode {

    /* renamed from: -com-mediatek-camera-v2-module-ModuleListener$RequestTypeSwitchesValues */
    private static final /* synthetic */ int[] f73x71d17683 = null;
    private Surface mBottomCaptureSurface;
    private boolean mBottomIsSubCam;
    private Surface mBottomPreviewSurface;
    private CamcorderProfile mCameraCamcorderProfile;
    private ContentValues mCapContentValues;
    private ICaptureStream.CaptureStreamCallback mCaptureStreamCallback;
    private int mCurrentOrientation;
    private boolean mDuringCapture;
    private Runnable mEnableShutterButtonRunnable;
    private ISettingRule mFaceDetectionRule;
    private Handler mHandler;
    private boolean mIsPaused;
    private FileSaver.OnFileSavedListener mMediaSavedListener;
    private ModeController.ModeGestureListener mModeGestureListener;
    private final String mOriginalCameraIdStr;
    private PictureSizeRule mPictureSizeRule;
    private ModePipStreamCallback mPipCallback;
    private IPipStream mPipStreamController;
    private IPreviewStream.PreviewCallback mPreviewCallback;
    private PreviewSizeRule mPreviewSizeRule;
    private IRecordStream.RecordStreamStatus mRecordStreamCallback;
    private boolean mRecording;
    private int mRecordingRotation;
    private final LogHelper.Tag mTag;
    private Surface mTopCaptureSurface;
    private Surface mTopPreviewSurface;
    private ContentValues mVideoContentValues;
    private VideoQualityRule mVideoQualityRule;
    private String mVideoTempPath;

    /* renamed from: -getcom-mediatek-camera-v2-module-ModuleListener$RequestTypeSwitchesValues */
    private static /* synthetic */ int[] m39x960f455f() {
        if (f73x71d17683 != null) {
            return f73x71d17683;
        }
        int[] iArr = new int[ModuleListener.RequestType.valuesCustom().length];
        try {
            iArr[ModuleListener.RequestType.MANUAL.ordinal()] = 4;
        } catch (NoSuchFieldError e) {
        }
        try {
            iArr[ModuleListener.RequestType.PREVIEW.ordinal()] = 5;
        } catch (NoSuchFieldError e2) {
        }
        try {
            iArr[ModuleListener.RequestType.RECORDING.ordinal()] = 1;
        } catch (NoSuchFieldError e3) {
        }
        try {
            iArr[ModuleListener.RequestType.STILL_CAPTURE.ordinal()] = 2;
        } catch (NoSuchFieldError e4) {
        }
        try {
            iArr[ModuleListener.RequestType.VIDEO_SNAP_SHOT.ordinal()] = 3;
        } catch (NoSuchFieldError e5) {
        }
        try {
            iArr[ModuleListener.RequestType.ZERO_SHUTTER_DELAY.ordinal()] = 6;
        } catch (NoSuchFieldError e6) {
        }
        f73x71d17683 = iArr;
        return iArr;
    }

    public PipMode(AppController appController, ModuleListener moduleListener) {
        super(appController, moduleListener);
        this.mRecording = false;
        this.mHandler = new Handler();
        this.mEnableShutterButtonRunnable = new Runnable() { // from class: com.mediatek.camera.v2.mode.pip.PipMode.1
            @Override // java.lang.Runnable
            public void run() {
                PipMode.this.mAppUi.setShutterButtonEnabled(true, false);
                PipMode.this.mAppUi.setShutterButtonEnabled(true, true);
            }
        };
        this.mMediaSavedListener = new FileSaver.OnFileSavedListener() { // from class: com.mediatek.camera.v2.mode.pip.PipMode.2
            @Override // com.mediatek.camera.v2.services.FileSaver.OnFileSavedListener
            public void onMediaSaved(Uri uri) {
                LogHelper.m23d(PipMode.this.mTag, "onMediaSaved uri = " + uri);
                PipMode.this.mAppUi.dismissSavingProgress();
                PipMode.this.mAppController.notifyNewMedia(uri);
            }
        };
        this.mModeGestureListener = new ModeController.ModeGestureListener() { // from class: com.mediatek.camera.v2.mode.pip.PipMode.3
            @Override // com.mediatek.camera.v2.mode.ModeController.ModeGestureListener
            public boolean onUp() {
                if (PipMode.this.mDuringCapture) {
                    return true;
                }
                if (PipMode.this.mPipStreamController != null) {
                    return PipMode.this.mPipStreamController.onUp();
                }
                return false;
            }

            @Override // com.mediatek.camera.v2.mode.ModeController.ModeGestureListener
            public boolean onSingleTapUp(float f, float f2) {
                if (PipMode.this.mDuringCapture) {
                    return true;
                }
                if (PipMode.this.mPipStreamController != null) {
                    return PipMode.this.mPipStreamController.onSingleTapUp(f, f2);
                }
                return false;
            }

            @Override // com.mediatek.camera.v2.mode.ModeController.ModeGestureListener
            public boolean onScroll(float f, float f2, float f3, float f4) {
                if (PipMode.this.mDuringCapture) {
                    return true;
                }
                if (PipMode.this.mPipStreamController != null) {
                    return PipMode.this.mPipStreamController.onScroll(f, f2, f3, f4);
                }
                return false;
            }

            @Override // com.mediatek.camera.v2.mode.ModeController.ModeGestureListener
            public boolean onLongPress(float f, float f2) {
                if (PipMode.this.mDuringCapture) {
                    return true;
                }
                if (PipMode.this.mPipStreamController != null) {
                    return PipMode.this.mPipStreamController.onLongPress(f, f2);
                }
                return false;
            }

            @Override // com.mediatek.camera.v2.mode.ModeController.ModeGestureListener
            public boolean onDown(float f, float f2) {
                if (PipMode.this.mDuringCapture) {
                    return true;
                }
                if (PipMode.this.mPipStreamController != null) {
                    return PipMode.this.mPipStreamController.onDown(f, f2);
                }
                return false;
            }
        };
        this.mPreviewCallback = new IPreviewStream.PreviewCallback() { // from class: com.mediatek.camera.v2.mode.pip.PipMode.4
            @Override // com.mediatek.camera.v2.stream.IPreviewStream.PreviewCallback
            public void surfaceAvailable(Surface surface, int i, int i2) {
                LogHelper.m23d(PipMode.this.mTag, "[surfaceAvailable]+");
                PipMode.this.mPipStreamController.setPreviewSurface(surface);
            }

            @Override // com.mediatek.camera.v2.stream.IPreviewStream.PreviewCallback
            public void surfaceDestroyed(Surface surface) {
                LogHelper.m23d(PipMode.this.mTag, "[surfaceDestroyed]+");
                PipMode.this.mPipStreamController.onActivityPause();
            }

            @Override // com.mediatek.camera.v2.stream.IPreviewStream.PreviewCallback
            public void surfaceSizeChanged(Surface surface, int i, int i2) {
                LogHelper.m23d(PipMode.this.mTag, "[surfaceSizeChanged]+");
                PipMode.this.mPipStreamController.setPreviewSurface(surface);
            }
        };
        this.mAppUi.switchShutterButtonLayout(R.layout.camera_shutter_photo_video_v2);
        this.mTag = new LogHelper.Tag(PipMode.class.getSimpleName() + "(" + this.mFeatureTag + ")");
        this.mVideoQualityRule = new VideoQualityRule(this.mSettingCtroller);
        this.mPreviewSizeRule = new PreviewSizeRule(this.mSettingCtroller);
        this.mPictureSizeRule = new PictureSizeRule(this.mSettingCtroller);
        this.mFaceDetectionRule = new FdAffectedRule(this.mSettingCtroller, "pref_face_detect_key");
        this.mSettingServant = this.mSettingCtroller.getSettingServant(this.mSettingCtroller.getCurrentCameraId());
        this.mSettingCtroller.addRule("photo_pip_key", "pref_camera_picturesize_key", this.mPictureSizeRule);
        this.mSettingCtroller.addRule("photo_pip_key", "pref_camera_picturesize_ratio_key", this.mPreviewSizeRule);
        this.mSettingCtroller.addRule("photo_pip_key", "pref_video_quality_key", this.mVideoQualityRule);
        this.mSettingCtroller.addRule("photo_pip_key", "pref_face_detect_key", this.mFaceDetectionRule);
        this.mOriginalCameraIdStr = this.mSettingServant.getCameraId();
        this.mPipCallback = new ModePipStreamCallback(new Handler(appController.getActivity().getMainLooper()));
    }

    @Override // com.mediatek.camera.p005v2.mode.AbstractCameraMode
    protected void updateCaredSettingChangedKeys() {
        super.updateCaredSettingChangedKeys();
        addCaredSettingChangedKeys("pref_camera_id_key");
    }

    @Override // com.mediatek.camera.p005v2.mode.AbstractCameraMode, com.mediatek.camera.v2.setting.ISettingServant.ISettingChangedListener
    public void onSettingChanged(Map<String, String> map) {
        String str;
        super.onSettingChanged(map);
        if (map.containsKey("pref_camera_id_key") && (str = map.get("pref_camera_id_key")) != null) {
            LogHelper.m23d(this.mTag, "onSettingChanged cameraId new :" + str);
            this.mSettingServant.unRegisterSettingChangedListener(this);
            this.mSettingServant = this.mSettingCtroller.getSettingServant(str);
            this.mSettingServant.registerSettingChangedListener(this, this.mCaredSettingChangedKeys, 1);
        }
    }

    @Override // com.mediatek.camera.p005v2.mode.AbstractCameraMode
    protected int getModeId() {
        return 3;
    }

    @Override // com.mediatek.camera.p005v2.mode.AbstractCameraMode
    public void open(StreamManager streamManager, ViewGroup viewGroup, boolean z) {
        LogHelper.m23d(this.mTag, "[open]+");
        super.open(streamManager, viewGroup, z);
        this.mPipStreamController = streamManager.getPipStreamController();
        this.mPipStreamController.registerPipStreamCallback(this.mPipCallback);
        this.mPipStreamController.open(this.mAppController.getActivity());
        this.mPipStreamController.onTemplateChanged(R.drawable.rear_01, R.drawable.front_01, R.drawable.front_01_focus, R.drawable.plus);
        if (!"0".equals(this.mSettingCtroller.getCurrentCameraId())) {
            this.mPipStreamController.switchingPip();
            this.mBottomIsSubCam = true;
        }
        this.mPreviewController.setPreviewCallback(this.mPreviewCallback);
        LogHelper.m23d(this.mTag, "[open]-");
    }

    @Override // com.mediatek.camera.p005v2.mode.AbstractCameraMode
    public void close() {
        LogHelper.m23d(this.mTag, "[close]+");
        this.mPipStreamController.close();
        this.mPipStreamController.unregisterPipStreamCallback(this.mPipCallback);
        super.close();
        if (!this.mOriginalCameraIdStr.equals(this.mSettingServant.getCameraId())) {
            this.mAppUi.performCameraPickerBtnClick(true);
        }
        LogHelper.m23d(this.mTag, "[close]-");
    }

    @Override // com.mediatek.camera.p005v2.mode.AbstractCameraMode
    public void resume() {
        LogHelper.m23d(this.mTag, "[resume]+");
        this.mIsPaused = false;
        this.mDuringCapture = false;
        this.mPipStreamController.resume();
        super.resume();
        LogHelper.m23d(this.mTag, "[resume]-");
    }

    @Override // com.mediatek.camera.p005v2.mode.AbstractCameraMode
    public void onActivityPause() {
    }

    @Override // com.mediatek.camera.p005v2.mode.AbstractCameraMode
    public void pause() {
        LogHelper.m23d(this.mTag, "[pause]+");
        this.mIsPaused = true;
        if (this.mRecording) {
            videoShutterClicked();
        }
        this.mPipStreamController.pause();
        super.pause();
        this.mBottomCaptureSurface = null;
        this.mTopCaptureSurface = null;
        LogHelper.m26i(this.mTag, "[pause]-");
    }

    @Override // com.mediatek.camera.p005v2.mode.AbstractCameraMode
    public void onOrientationChanged(int i) {
        super.onOrientationChanged(i);
        this.mCurrentOrientation = i;
        this.mPipStreamController.onOrientationChanged(i);
    }

    @Override // com.mediatek.camera.p005v2.mode.AbstractCameraMode
    public void onPreviewVisibilityChanged(int i) {
        super.onPreviewVisibilityChanged(i);
        LogHelper.m23d(this.mTag, "onPreviewVisibilityChanged visibility:" + i);
    }

    @Override // com.mediatek.camera.p005v2.mode.AbstractCameraMode
    public void onPreviewAreaChanged(RectF rectF) {
        LogHelper.m23d(this.mTag, "onPreviewAreaChanged previewArea:" + rectF);
        super.onPreviewAreaChanged(rectF);
        this.mPipStreamController.onPreviewAreaChanged(rectF);
    }

    @Override // com.mediatek.camera.p005v2.mode.AbstractCameraMode
    public ModeController.ModeGestureListener getModeGestureListener() {
        return this.mModeGestureListener;
    }

    @Override // com.mediatek.camera.p005v2.mode.ModeController
    public void onShutterPressed(boolean z) {
    }

    @Override // com.mediatek.camera.p005v2.mode.ModeController
    public void onShutterClicked(boolean z) {
        if (this.mIsPaused) {
            LogHelper.m28w(this.mTag, "onShutterClicked but mode is paused.");
            return;
        }
        if (z) {
            if (!this.mDuringCapture) {
                videoShutterClicked();
            }
        } else if (this.mRecording) {
            takeVss();
        } else {
            takePicture();
        }
    }

    @Override // com.mediatek.camera.p005v2.mode.ModeController
    public void onShutterLongPressed(boolean z) {
    }

    @Override // com.mediatek.camera.p005v2.mode.ModeController
    public void onShutterReleased(boolean z) {
    }

    @Override // com.mediatek.camera.p005v2.mode.AbstractCameraMode
    public boolean onBackPressed() {
        if (this.mRecording) {
            onShutterClicked(true);
            return true;
        }
        return super.onBackPressed();
    }

    @Override // com.mediatek.camera.p005v2.mode.AbstractCameraMode
    public boolean switchCamera(String str) {
        LogHelper.m23d(this.mTag, "switchCamera cameraId:" + str);
        this.mPipStreamController.switchingPip();
        this.mBottomIsSubCam = !this.mBottomIsSubCam;
        return true;
    }

    @Override // com.mediatek.camera.p005v2.mode.AbstractCameraMode
    protected ICaptureStream.CaptureStreamCallback getCaptureStreamCallback() {
        if (this.mCaptureStreamCallback == null) {
            this.mCaptureStreamCallback = new ICaptureStream.CaptureStreamCallback() { // from class: com.mediatek.camera.v2.mode.pip.PipMode.5
                @Override // com.mediatek.camera.v2.stream.ICaptureStream.CaptureStreamCallback
                public void onCaptureCompleted(ImageInfo imageInfo) {
                    byte[] data = imageInfo.getData();
                    int width = imageInfo.getWidth();
                    int heigth = imageInfo.getHeigth();
                    LogHelper.m23d(PipMode.this.mTag, "onCaptureCompleted width = " + width + ",heigth = " + heigth);
                    if (PipMode.this.mRecording) {
                        PipMode.this.mAppController.getActivity().runOnUiThread(new Runnable() { // from class: com.mediatek.camera.v2.mode.pip.PipMode.5.1
                            @Override // java.lang.Runnable
                            public void run() {
                                PipMode.this.mAppUi.setShutterButtonEnabled(true, false);
                            }
                        });
                        PipMode.this.mDuringCapture = false;
                    } else {
                        PipMode.this.mCameraServices.getSoundPlayback().play(3);
                        PipMode.this.mBottomCaptureSurface = null;
                        PipMode.this.mModuleListener.requestChangeSessionOutputs(true, true);
                        PipMode.this.mAppController.getActivity().runOnUiThread(new Runnable() { // from class: com.mediatek.camera.v2.mode.pip.PipMode.5.2
                            @Override // java.lang.Runnable
                            public void run() {
                                PipMode.this.mDuringCapture = false;
                                PipMode.this.mAppUi.setAllCommonViewEnable(true);
                                PipMode.this.mAppUi.setSwipeEnabled(true);
                            }
                        });
                    }
                    int jpegRotation = Utils.getJpegRotation(PipMode.this.mCurrentOrientation, Utils.getCameraCharacteristics(PipMode.this.mAppController.getActivity(), PipMode.this.mSettingServant.getCameraId()));
                    if (data != null) {
                        PipMode.this.updateCaptureContentValues(width, heigth, jpegRotation);
                        PipMode.this.mCameraServices.getMediaSaver().addImage(data, PipMode.this.mCapContentValues, PipMode.this.mMediaSavedListener, PipMode.this.mAppController.getActivity().getContentResolver());
                    }
                }
            };
        }
        return this.mCaptureStreamCallback;
    }

    @Override // com.mediatek.camera.p005v2.mode.AbstractCameraMode
    protected IRecordStream.RecordStreamStatus getRecordStreamCallback() {
        if (this.mRecordStreamCallback == null) {
            this.mRecordStreamCallback = new IRecordStream.RecordStreamStatus() { // from class: com.mediatek.camera.v2.mode.pip.PipMode.6
                @Override // com.mediatek.camera.v2.stream.IRecordStream.RecordStreamStatus
                public void onRecordingStarted(boolean z) {
                }

                @Override // com.mediatek.camera.v2.stream.IRecordStream.RecordStreamStatus
                public void onRecordingStoped() throws IOException {
                    LogHelper.m23d(PipMode.this.mTag, "onRecordingStoped");
                    PipMode.this.mSettingCtroller.doSettingChange("video_pip_key", "off");
                    PipMode.this.updateVideoContentValues();
                    PipMode.this.mCameraServices.getMediaSaver().addVideo(PipMode.this.mVideoTempPath, PipMode.this.mVideoContentValues, PipMode.this.mMediaSavedListener, PipMode.this.mAppController.getActivity().getContentResolver());
                    PipMode.this.mVideoTempPath = null;
                    PipMode.this.mCameraCamcorderProfile = null;
                }

                @Override // com.mediatek.camera.v2.stream.IRecordStream.RecordStreamStatus
                public void onInfo(int i, int i2) {
                }

                @Override // com.mediatek.camera.v2.stream.IRecordStream.RecordStreamStatus
                public void onError(int i, int i2) {
                }
            };
        }
        return this.mRecordStreamCallback;
    }

    @Override // com.mediatek.camera.p005v2.mode.AbstractCameraMode
    protected Size getPreviewSize() {
        LogHelper.m23d(this.mTag, "[getPreviewSize]+ mRecording: " + this.mRecording + " mCameraCamcorderProfile: " + this.mCameraCamcorderProfile);
        Size previewSize = getPreviewSize(this.mSettingServant);
        Size previewSize2 = getPreviewSize(this.mSettingCtroller.getSettingServant(getAnotherCameraId()));
        LogHelper.m23d(this.mTag, "getPreviewSize bottomSize:" + Utils.buildSize(previewSize) + " topSize:" + Utils.buildSize(previewSize2));
        if (Utils.compareSize(previewSize, previewSize2)) {
            return new Size(previewSize2.getHeight(), previewSize2.getWidth());
        }
        return new Size(previewSize.getHeight(), previewSize.getWidth());
    }

    @Override // com.mediatek.camera.p005v2.mode.AbstractCameraMode
    protected int getCaptureFormat() {
        return 1;
    }

    @Override // com.mediatek.camera.p005v2.mode.AbstractCameraMode
    protected Size getCaptureSize() {
        if (this.mRecording && this.mCameraCamcorderProfile != null) {
            Size previewSize = getPreviewSize(this.mSettingServant);
            Size previewSize2 = getPreviewSize(this.mSettingCtroller.getSettingServant(getAnotherCameraId()));
            if (Utils.compareSize(previewSize, previewSize2)) {
                return new Size(previewSize2.getWidth(), previewSize2.getHeight());
            }
            return new Size(previewSize.getWidth(), previewSize.getHeight());
        }
        Size cameraCaptureSize = getCameraCaptureSize("0");
        Size cameraCaptureSize2 = getCameraCaptureSize("1");
        LogHelper.m23d(this.mTag, "getCaptureSize bottomSize:" + Utils.buildSize(cameraCaptureSize) + " subCamCaptureSize:" + Utils.buildSize(cameraCaptureSize2));
        if (cameraCaptureSize == null || cameraCaptureSize2 == null) {
            return null;
        }
        if (Utils.compareSize(cameraCaptureSize, cameraCaptureSize2)) {
            return cameraCaptureSize;
        }
        return cameraCaptureSize2;
    }

    @Override // com.mediatek.camera.p005v2.mode.AbstractCameraMode
    protected boolean changingModePictureSize() {
        super.changingModePictureSize();
        if (this.mRecording) {
            this.mPipStreamController.setCaptureSize(getPreviewSize(), getPreviewSize());
            return false;
        }
        this.mPipStreamController.setCaptureSize(isMainCamCapUsePvSize() ? getPreviewSize() : getCameraCaptureSize("0"), isSubCamCapUsePvSize() ? getPreviewSize() : getCameraCaptureSize("1"));
        return true;
    }

    @Override // com.mediatek.camera.p005v2.mode.ModeController
    public void configuringSessionOutputs(List<Surface> list, boolean z) {
        LogHelper.m23d(this.mTag, "[configuringSessionOutputs]+ mainCamera = " + z);
        waitPreviewSurfaceReady();
        if (z) {
            updatePictureSize();
            updateCaptureSurfaces(true);
            list.add(this.mBottomPreviewSurface);
            if (this.mBottomCaptureSurface != null) {
                list.add(this.mBottomCaptureSurface);
            }
        } else {
            updatePictureSize();
            updateCaptureSurfaces(false);
            list.add(this.mTopPreviewSurface);
            if (this.mTopCaptureSurface != null) {
                list.add(this.mTopCaptureSurface);
            }
        }
        LogHelper.m23d(this.mTag, "[configuringSessionOutputs]-  output surface size = " + list.size());
    }

    @Override // com.mediatek.camera.p005v2.mode.ModeController
    public void configuringSessionRequests(Map<ModuleListener.RequestType, CaptureRequest.Builder> map, boolean z) {
        LogHelper.m23d(this.mTag, "[configuringSessionRequests]+ isMainCamera = " + z);
        for (ModuleListener.RequestType requestType : map.keySet()) {
            configuringPreviewRequests(map.get(requestType), z);
            switch (m39x960f455f()[requestType.ordinal()]) {
                case 1:
                    configreEisValue(map.get(requestType));
                    break;
                case 2:
                    configuringCaptureRequests(map.get(requestType), z);
                    break;
                case 3:
                    configuringCaptureRequests(map.get(requestType), z);
                    configreEisValue(map.get(requestType));
                    break;
            }
        }
        LogHelper.m23d(this.mTag, "[configuringSessionRequests]- ");
    }

    private Size getCameraCaptureSize(String str) {
        SettingItem settingItem = this.mSettingCtroller.getSettingServant(str).getSettingItem("pref_camera_picturesize_key");
        if (settingItem == null) {
            return null;
        }
        String value = settingItem.getValue();
        LogHelper.m23d(this.mTag, "getCameraCaptureSize camera id:" + str + " captureSizeString:" + value);
        if (value != null) {
            return Utils.getSize(value);
        }
        return null;
    }

    private String getAnotherCameraId() {
        if ("0".equalsIgnoreCase(this.mSettingServant.getCameraId())) {
            return "1";
        }
        return "0";
    }

    private void waitPreviewSurfaceReady() {
        Map<String, Surface> previewInputSurfaces = this.mPreviewController.getPreviewInputSurfaces();
        this.mBottomPreviewSurface = previewInputSurfaces.get("PipStreamController.Main");
        this.mTopPreviewSurface = previewInputSurfaces.get("PipStreamController.Sub");
    }

    private void configreEisValue(CaptureRequest.Builder builder) {
        String settingValue = this.mSettingServant.getSettingValue("pref_video_eis_key");
        LogHelper.m23d(this.mTag, "configuringRecordingRequests eisValue = " + settingValue);
        if ("on".equals(settingValue)) {
            builder.set(CaptureRequest.CONTROL_VIDEO_STABILIZATION_MODE, 1);
        } else {
            builder.set(CaptureRequest.CONTROL_VIDEO_STABILIZATION_MODE, 0);
        }
    }

    private void configuringPreviewRequests(CaptureRequest.Builder builder, boolean z) {
        if (z) {
            if (this.mBottomPreviewSurface != null && this.mBottomPreviewSurface.isValid()) {
                builder.addTarget(this.mBottomPreviewSurface);
                return;
            }
            return;
        }
        if (this.mTopPreviewSurface != null && this.mTopPreviewSurface.isValid()) {
            builder.addTarget(this.mTopPreviewSurface);
        }
    }

    private void configuringCaptureRequests(CaptureRequest.Builder builder, boolean z) {
        if (z) {
            if (this.mBottomCaptureSurface != null) {
                builder.addTarget(this.mBottomCaptureSurface);
            }
        } else if (this.mTopCaptureSurface != null) {
            builder.addTarget(this.mTopCaptureSurface);
        }
        builder.set(CaptureRequest.JPEG_QUALITY, JPEG_QUALITY);
    }

    private void takePicture() {
        LogHelper.m23d(this.mTag, "takePicture");
        if (this.mStorageService.getCaptureStorageSpace() <= 0) {
            LogHelper.m28w(this.mTag, "Not enough space or storage not available, remaining:" + this.mStorageService.getCaptureStorageSpace());
            return;
        }
        this.mDuringCapture = true;
        this.mAppUi.setAllCommonViewEnable(false);
        this.mAppUi.setSwipeEnabled(false);
        IControl$IAaaController iControl$IAaaController = this.mModuleListener.get3AController("1");
        IControl$IAaaController iControl$IAaaController2 = this.mModuleListener.get3AController("0");
        if (!isMainCamCapUsePvSize()) {
            iControl$IAaaController2 = iControl$IAaaController;
            iControl$IAaaController = iControl$IAaaController2;
        }
        iControl$IAaaController2.aePreTriggerAndCapture();
        updatePictureSize();
        updateCaptureSurfaces(!this.mBottomIsSubCam);
        this.mModuleListener.requestChangeSessionOutputs(true, !this.mBottomIsSubCam);
        iControl$IAaaController.aePreTriggerAndCapture();
    }

    private boolean isMainCamCapUsePvSize() {
        if (this.mDuringCapture) {
            return this.mBottomIsSubCam;
        }
        return true;
    }

    private boolean isSubCamCapUsePvSize() {
        if (this.mDuringCapture) {
            return !this.mBottomIsSubCam;
        }
        return true;
    }

    private void takeVss() {
        this.mDuringCapture = true;
        this.mAppUi.setShutterButtonEnabled(false, false);
        this.mModuleListener.requestChangeCaptureRequets(true, false, ModuleListener.RequestType.VIDEO_SNAP_SHOT, ModuleListener.CaptureType.CAPTURE);
        this.mModuleListener.requestChangeCaptureRequets(false, false, ModuleListener.RequestType.VIDEO_SNAP_SHOT, ModuleListener.CaptureType.CAPTURE);
    }

    private void videoShutterClicked() {
        LogHelper.m23d(this.mTag, "videoShutterButtonClicked");
        if (!this.mRecording && this.mStorageService.getRecordStorageSpace() <= 0) {
            LogHelper.m28w(this.mTag, "Not enough space or storage not available, remaining:" + this.mStorageService.getRecordStorageSpace());
            this.mAppUi.showPickerManagerUi();
            return;
        }
        this.mAppUi.setShutterButtonEnabled(false, false);
        this.mAppUi.setShutterButtonEnabled(false, true);
        if (this.mRecording) {
            stopRecording();
            this.mAppUi.stopShowCommonUI(false);
            this.mAppUi.switchShutterButtonImageResource(R.drawable.btn_video, true);
            this.mAppUi.setSwipeEnabled(true);
            this.mAppUi.showModeOptionsUi();
            this.mAppUi.showSettingUi();
            this.mAppUi.showIndicatorManagerUi();
            this.mAppUi.showPickerManagerUi();
            this.mAppUi.setThumbnailManagerEnable(true);
            this.mAppUi.setAllCommonViewEnable(true);
            Size captureSize = getCaptureSize();
            this.mAppUi.showLeftCounts(Utils.getImageSize(captureSize.getWidth() + "x" + captureSize.getHeight() + "-superfine"), true);
            return;
        }
        this.mCameraServices.getSoundPlayback().play(1);
        startRecording();
        this.mAppUi.stopShowCommonUI(true);
        this.mAppUi.switchShutterButtonImageResource(R.drawable.btn_video_mask, true);
        this.mAppUi.setSwipeEnabled(false);
        this.mAppUi.hideModeOptionsUi();
        this.mAppUi.hideSettingUi();
        this.mAppUi.hideIndicatorManagerUi();
        this.mAppUi.hidePickerManagerUi();
        this.mAppUi.setThumbnailManagerEnable(false);
        this.mAppUi.showLeftTime(((this.mCameraCamcorderProfile.videoBitRate + this.mCameraCamcorderProfile.audioBitRate) >> 3) / 1000);
    }

    private void startRecording() {
        LogHelper.m23d(this.mTag, "[startRecording]+");
        this.mRecording = true;
        pauseAudioPlayback();
        prepareRecording();
        updatePictureSize();
        updateCaptureSurfaces(true);
        updatePreviewSize(new IPreviewStream.PreviewSurfaceCallback() { // from class: com.mediatek.camera.v2.mode.pip.PipMode.7
            @Override // com.mediatek.camera.v2.stream.IPreviewStream.PreviewSurfaceCallback
            public void onPreviewSufaceIsReady(boolean z) {
                LogHelper.m23d(PipMode.this.mTag, "startRecording onPreviewSufaceIsReady");
                PipMode.this.mModuleListener.requestChangeSessionOutputs(true);
                PipMode.this.mSettingCtroller.doSettingChange("video_pip_key", "on");
                PipMode.this.mModuleListener.requestChangeCaptureRequets(true, ModuleListener.RequestType.RECORDING, ModuleListener.CaptureType.REPEATING_REQUEST);
                PipMode.this.mRecordController.startRecord();
                PipMode.this.mAppController.enableKeepScreenOn(true);
                PipMode.this.mHandler.postDelayed(PipMode.this.mEnableShutterButtonRunnable, 500L);
            }
        });
    }

    private void stopRecording() {
        LogHelper.m23d(this.mTag, "[stopRecording]+");
        this.mRecording = false;
        releaseAudioFocus();
        if (this.mIsPaused) {
            doStopRecording();
        } else {
            updatePictureSize();
            updatePreviewSize(new IPreviewStream.PreviewSurfaceCallback() { // from class: com.mediatek.camera.v2.mode.pip.PipMode.8
                @Override // com.mediatek.camera.v2.stream.IPreviewStream.PreviewSurfaceCallback
                public void onPreviewSufaceIsReady(boolean z) {
                    PipMode.this.doStopRecording();
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void doStopRecording() {
        LogHelper.m23d(this.mTag, "[doStopRecording]+");
        this.mBottomCaptureSurface = null;
        this.mTopCaptureSurface = null;
        try {
            this.mRecordController.stopRecord(true);
            this.mAppUi.showSavingProgress(this.mAppController.getActivity().getResources().getString(R.string.saving));
        } catch (RuntimeException e) {
            e.printStackTrace();
            LogHelper.m24e(this.mTag, "stopRecording with exception:" + e);
            this.mVideoTempPath = null;
            this.mCameraCamcorderProfile = null;
        } finally {
            this.mModuleListener.requestChangeSessionOutputs(true, true);
            this.mModuleListener.requestChangeCaptureRequets(true, true, ModuleListener.RequestType.PREVIEW, ModuleListener.CaptureType.REPEATING_REQUEST);
            this.mModuleListener.requestChangeSessionOutputs(true, false);
            this.mModuleListener.requestChangeCaptureRequets(false, true, ModuleListener.RequestType.PREVIEW, ModuleListener.CaptureType.REPEATING_REQUEST);
            this.mCameraServices.getSoundPlayback().play(2);
            this.mAppController.enableKeepScreenOn(false);
            this.mAppUi.setShutterButtonEnabled(true, false);
            this.mAppUi.setShutterButtonEnabled(true, true);
            LogHelper.m23d(this.mTag, "[doStopRecording]-");
        }
    }

    private void prepareRecording() {
        int iIntValue = Integer.valueOf(this.mSettingServant.getCameraId()).intValue();
        this.mCameraCamcorderProfile = this.mVideoHelper.fetchProfile(this.mVideoHelper.getRecordingQuality(iIntValue), iIntValue);
        boolean zEquals = "on".equals(this.mSettingServant.getSettingValue("pref_camera_recordaudio_key"));
        this.mRecordingRotation = Utils.getRecordingRotation(this.mCurrentOrientation, Utils.getCameraCharacteristics(this.mAppController.getActivity(), this.mSettingServant.getCameraId()));
        this.mVideoTempPath = this.mVideoHelper.generateVideoFileName(this.mCameraCamcorderProfile.fileFormat, null);
        LogHelper.m23d(this.mTag, "prepareRecording enableAudio = " + zEquals);
        prepareMediaRecordingParamters();
        this.mRecordController.setRecordingProfile(this.mCameraCamcorderProfile);
        this.mRecordController.enalbeAudioRecording(zEquals);
        this.mRecordController.setOutputFile(this.mVideoTempPath);
        this.mRecordController.setOrientationHint(this.mRecordingRotation);
        this.mRecordController.setAudioSource(5);
        this.mRecordController.setVideoSource(2);
        this.mRecordController.prepareRecord();
        this.mRecordController.getRecordInputSurface();
    }

    private void updateCaptureSurfaces(boolean z) {
        Map<String, Surface> captureInputSurface = this.mCaptureController.getCaptureInputSurface();
        if (z) {
            this.mBottomCaptureSurface = captureInputSurface.get("PipStreamController.Main");
        } else {
            this.mTopCaptureSurface = captureInputSurface.get("PipStreamController.Sub");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateCaptureContentValues(int i, int i2, int i3) {
        this.mCapContentValues = new ContentValues();
        long jCurrentTimeMillis = System.currentTimeMillis();
        String strCreateJpegName = Utils.createJpegName(jCurrentTimeMillis);
        String str = strCreateJpegName + ".jpg";
        String str2 = this.mStorageService.getFileDirectory() + '/' + str;
        String str3 = str2 + ".tmp";
        this.mCapContentValues.put("datetaken", Long.valueOf(jCurrentTimeMillis));
        this.mCapContentValues.put("title", strCreateJpegName);
        this.mCapContentValues.put("_display_name", str);
        this.mCapContentValues.put("_data", str2);
        this.mCapContentValues.put("mime_type", "image/jpeg");
        this.mCapContentValues.put("width", Integer.valueOf(i));
        this.mCapContentValues.put("height", Integer.valueOf(i2));
        this.mCapContentValues.put("orientation", Integer.valueOf(i3));
        this.mLocation = this.mLocationManager.getCurrentLocation();
        if (this.mLocation != null) {
            this.mCapContentValues.put("latitude", Double.valueOf(this.mLocation.getLatitude()));
            this.mCapContentValues.put("longitude", Double.valueOf(this.mLocation.getLongitude()));
        }
        LogHelper.m23d(this.mTag, "updateCaptureContentValues orientation: " + i3);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateVideoContentValues() throws IOException {
        long jCurrentTimeMillis = System.currentTimeMillis();
        String strCreateFileTitle = this.mVideoHelper.createFileTitle(jCurrentTimeMillis, this.mAppController);
        String strConvertOutputFormatToMimeType = this.mVideoHelper.convertOutputFormatToMimeType(this.mCameraCamcorderProfile.fileFormat);
        String str = strCreateFileTitle + this.mVideoHelper.convertOutputFormatToFileExt(this.mCameraCamcorderProfile.fileFormat);
        String str2 = this.mStorageService.getFileDirectory() + '/' + str;
        long duration = this.mVideoHelper.getDuration(this.mVideoTempPath);
        this.mVideoContentValues = new ContentValues();
        this.mVideoContentValues.put("duration", Long.valueOf(duration));
        this.mVideoContentValues.put("title", strCreateFileTitle);
        this.mVideoContentValues.put("_display_name", str);
        this.mVideoContentValues.put("datetaken", Long.valueOf(jCurrentTimeMillis));
        this.mVideoContentValues.put("date_modified", Long.valueOf(jCurrentTimeMillis / 1000));
        this.mVideoContentValues.put("mime_type", strConvertOutputFormatToMimeType);
        this.mVideoContentValues.put("_data", str2);
        this.mVideoContentValues.put("width", Integer.valueOf(this.mCameraCamcorderProfile.videoFrameWidth));
        this.mVideoContentValues.put("height", Integer.valueOf(this.mCameraCamcorderProfile.videoFrameHeight));
        this.mVideoContentValues.put("resolution", Integer.toString(this.mCameraCamcorderProfile.videoFrameWidth) + "x" + Integer.toString(this.mCameraCamcorderProfile.videoFrameHeight));
        this.mVideoContentValues.put("_size", Long.valueOf(new File(this.mVideoTempPath).length()));
        this.mLocation = this.mLocationManager.getCurrentLocation();
        if (this.mLocation != null) {
            this.mVideoContentValues.put("latitude", Double.valueOf(this.mLocation.getLatitude()));
            this.mVideoContentValues.put("longitude", Double.valueOf(this.mLocation.getLongitude()));
        }
    }

    private class ModePipStreamCallback implements IPipStream.PipStreamCallback {
        private Runnable mCamPickerBtnClieckRunnale = new Runnable() { // from class: com.mediatek.camera.v2.mode.pip.PipMode.ModePipStreamCallback.1
            @Override // java.lang.Runnable
            public void run() {
                PipMode.this.mAppUi.performCameraPickerBtnClick(PipMode.this.mRecording);
            }
        };
        private final Handler mHandler;

        public ModePipStreamCallback(Handler handler) {
            this.mHandler = handler;
        }

        @Override // com.mediatek.camera.v2.stream.pip.IPipStream.PipStreamCallback
        public void onOpened() {
        }

        @Override // com.mediatek.camera.v2.stream.pip.IPipStream.PipStreamCallback
        public void onClosed() {
        }

        @Override // com.mediatek.camera.v2.stream.pip.IPipStream.PipStreamCallback
        public void onPaused() {
        }

        @Override // com.mediatek.camera.v2.stream.pip.IPipStream.PipStreamCallback
        public void onResumed() {
        }

        @Override // com.mediatek.camera.v2.stream.pip.IPipStream.PipStreamCallback
        public void onTopGraphicTouched() {
        }

        @Override // com.mediatek.camera.v2.stream.pip.IPipStream.PipStreamCallback
        public void onSwitchPipEventReceived() {
            this.mHandler.removeCallbacks(this.mCamPickerBtnClieckRunnale);
            this.mHandler.post(this.mCamPickerBtnClieckRunnale);
        }
    }

    private Size getPreviewSize(ISettingServant iSettingServant) {
        List<Size> supportedPreviewSizes = iSettingServant.getSupportedPreviewSizes();
        if (this.mRecording && this.mCameraCamcorderProfile != null) {
            return Utils.getOptimalSize(supportedPreviewSizes, this.mCameraCamcorderProfile.videoFrameWidth, this.mCameraCamcorderProfile.videoFrameHeight);
        }
        String settingValue = iSettingServant.getSettingValue("pref_camera_picturesize_ratio_key");
        return Utils.getOptimalPreviewSize(this.mActivity, Utils.filterSizesByBound(supportedPreviewSizes, new Size(1920, 1088)), Double.parseDouble(settingValue));
    }

    private void prepareMediaRecordingParamters() {
        ArrayList arrayList = new ArrayList();
        arrayList.add("media-recorder-info=1998");
        arrayList.add("media-recorder-info=899");
        arrayList.add("media-recorder-info=1999");
        if (this.mIsCaptureIntent) {
            arrayList.add("media-recorder-info=895");
        }
        this.mRecordController.setMediaRecorderParameters(arrayList);
    }
}
