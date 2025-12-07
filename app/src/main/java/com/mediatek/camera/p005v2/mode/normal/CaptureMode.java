package com.mediatek.camera.p005v2.mode.normal;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Rect;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.DngCreator;
import android.hardware.camera2.TotalCaptureResult;
import android.location.Location;
import android.media.CamcorderProfile;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.ConditionVariable;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.util.Range;
import android.util.Size;
import android.view.Surface;
import android.view.ViewGroup;
import com.mediatek.camera.R;
import com.mediatek.camera.debug.LogHelper;
import com.mediatek.camera.p005v2.exif.Exif;
import com.mediatek.camera.p005v2.mode.AbstractCameraMode;
import com.mediatek.camera.p005v2.module.ModuleListener;
import com.mediatek.camera.p005v2.platform.app.AppController;
import com.mediatek.camera.p005v2.services.FileSaver;
import com.mediatek.camera.p005v2.setting.SettingConvertor;
import com.mediatek.camera.p005v2.stream.ICaptureStream;
import com.mediatek.camera.p005v2.stream.IPreviewStream;
import com.mediatek.camera.p005v2.stream.IRecordStream;
import com.mediatek.camera.p005v2.stream.ImageInfo;
import com.mediatek.camera.p005v2.stream.StreamManager;
import com.mediatek.camera.p005v2.stream.dng.IDngStream;
import com.mediatek.camera.p005v2.util.Utils;
import com.mediatek.camera.p005v2.vendortag.TagRequest;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import junit.framework.Assert;

/* loaded from: classes.dex */
public class CaptureMode extends AbstractCameraMode {

    /* renamed from: -com-mediatek-camera-v2-module-ModuleListener$RequestTypeSwitchesValues */
    private static final /* synthetic */ int[] f72x71d17683 = null;
    private CamcorderProfile mCameraCamcorderProfile;
    private ContentValues mCapContentValues;
    private TotalCaptureResult mCaptureResult;
    private boolean mCaptureResultReady;
    private ICaptureStream.CaptureStreamCallback mCaptureStreamCallback;
    protected Surface mCaptureSurface;
    private int mCurrentOrientation;
    private Runnable mDisableHintString;
    private final int mDngImageSize;
    private final String mDngOff;
    private final String mDngOn;
    private IDngStream mDngStream;
    private boolean mDngUpdateRemainSize;
    private Runnable mEnableShutterButtonRunnable;
    private Handler mHandler;
    private int mImageHeight;
    private int mImageWidth;
    private boolean mIsConvertingDng;
    private boolean mIsInReviewMode;
    private boolean mIsJpegCallbackFinished;
    private boolean mIsNeedSaveVideo;
    private boolean mIsRawCallbackFinished;
    private byte[] mJpegData;
    protected FileSaver.OnFileSavedListener mMediaSavedListener;
    private boolean mPaused;
    protected Surface mPreviewSurface;
    private final Object mRawCallbackSync;
    protected Surface mRawCaptureSurface;
    private ContentValues mRawContentValues;
    private final Object mRawTODngSync;
    private IRecordStream.RecordStreamStatus mRecordStreamCallback;
    protected Surface mRecordSurface;
    private boolean mRecording;
    private int mRecordingRotation;
    private int mRequestDurationLimit;
    private long mRequestSizeLimit;
    private long mShutterDateTaken;
    private ConditionVariable mStopRecordingSync;
    private StreamManager mStreamManager;
    private final LogHelper.Tag mTag;
    private Uri mUri;
    private ContentValues mVideoContentValues;
    private ParcelFileDescriptor mVideoFileDescriptor;
    private String mVideoTempPath;
    private ICaptureStream.CaptureStreamCallback mVssStreamCallback;

    /* renamed from: -getcom-mediatek-camera-v2-module-ModuleListener$RequestTypeSwitchesValues */
    private static /* synthetic */ int[] m38x960f455f() {
        if (f72x71d17683 != null) {
            return f72x71d17683;
        }
        int[] iArr = new int[ModuleListener.RequestType.valuesCustom().length];
        try {
            iArr[ModuleListener.RequestType.MANUAL.ordinal()] = 5;
        } catch (NoSuchFieldError e) {
        }
        try {
            iArr[ModuleListener.RequestType.PREVIEW.ordinal()] = 1;
        } catch (NoSuchFieldError e2) {
        }
        try {
            iArr[ModuleListener.RequestType.RECORDING.ordinal()] = 2;
        } catch (NoSuchFieldError e3) {
        }
        try {
            iArr[ModuleListener.RequestType.STILL_CAPTURE.ordinal()] = 3;
        } catch (NoSuchFieldError e4) {
        }
        try {
            iArr[ModuleListener.RequestType.VIDEO_SNAP_SHOT.ordinal()] = 4;
        } catch (NoSuchFieldError e5) {
        }
        try {
            iArr[ModuleListener.RequestType.ZERO_SHUTTER_DELAY.ordinal()] = 6;
        } catch (NoSuchFieldError e6) {
        }
        f72x71d17683 = iArr;
        return iArr;
    }

    public CaptureMode(AppController appController, ModuleListener moduleListener) {
        super(appController, moduleListener);
        this.mDngOn = "on";
        this.mDngOff = "off";
        this.mDngImageSize = 30000000;
        this.mRawCallbackSync = new Object();
        this.mRawTODngSync = new Object();
        this.mRecording = false;
        this.mIsNeedSaveVideo = true;
        this.mHandler = new Handler();
        this.mStopRecordingSync = new ConditionVariable();
        this.mIsInReviewMode = false;
        this.mRequestSizeLimit = 0L;
        this.mCaptureResultReady = false;
        this.mIsConvertingDng = false;
        this.mDngUpdateRemainSize = false;
        this.mIsJpegCallbackFinished = false;
        this.mIsRawCallbackFinished = false;
        this.mMediaSavedListener = new FileSaver.OnFileSavedListener() { // from class: com.mediatek.camera.v2.mode.normal.CaptureMode.1
            @Override // com.mediatek.camera.v2.services.FileSaver.OnFileSavedListener
            public void onMediaSaved(Uri uri) {
                LogHelper.m26i(CaptureMode.this.mTag, "onMediaSaved uri = " + uri);
                CaptureMode.this.mUri = uri;
                CaptureMode.this.mAppUi.dismissSavingProgress();
                if (CaptureMode.this.mUri != null) {
                    CaptureMode.this.mAppController.notifyNewMedia(uri);
                }
            }
        };
        this.mCaptureStreamCallback = new ICaptureStream.CaptureStreamCallback() { // from class: com.mediatek.camera.v2.mode.normal.CaptureMode.2
            @Override // com.mediatek.camera.v2.stream.ICaptureStream.CaptureStreamCallback
            public void onCaptureCompleted(ImageInfo imageInfo) throws IOException {
                ExifInterface exifInterface;
                final int attributeInt;
                int attributeInt2;
                if (imageInfo.getFormat() == 256) {
                    int width = imageInfo.getWidth();
                    int heigth = imageInfo.getHeigth();
                    byte[] data = imageInfo.getData();
                    CaptureMode.this.mJpegData = data;
                    CaptureMode.this.mImageWidth = width;
                    CaptureMode.this.mImageHeight = heigth;
                    if (CaptureMode.this.mIsCaptureIntent) {
                        attributeInt = heigth;
                    } else {
                        try {
                            exifInterface = new ExifInterface(new ByteArrayInputStream(data));
                        } catch (IOException e) {
                            e.printStackTrace();
                            exifInterface = null;
                        }
                        if (exifInterface != null) {
                            attributeInt2 = exifInterface.getAttributeInt("ImageWidth", 0);
                            attributeInt = exifInterface.getAttributeInt("ImageLength", 0);
                        } else {
                            attributeInt = heigth;
                            attributeInt2 = width;
                        }
                        CaptureMode.this.updateCaptureContentValues(attributeInt2, attributeInt, Exif.getOrientation(data));
                        CaptureMode.this.mCameraServices.getMediaSaver().addImage(data, CaptureMode.this.mCapContentValues, CaptureMode.this.mMediaSavedListener, CaptureMode.this.mAppController.getActivity().getContentResolver());
                    }
                    CaptureMode.this.mIsJpegCallbackFinished = true;
                    CaptureMode.this.mAppController.getActivity().runOnUiThread(new Runnable() { // from class: com.mediatek.camera.v2.mode.normal.CaptureMode.2.1
                        @Override // java.lang.Runnable
                        public void run() {
                            if (CaptureMode.this.mIsCaptureIntent) {
                                CaptureMode.this.mAppUi.showReviewView(CaptureMode.this.mJpegData, attributeInt);
                                CaptureMode.this.mAppUi.switchShutterButtonLayout(R.layout.camera_shutter_ok_cancel_v2);
                            } else {
                                CaptureMode.this.enableCommonView();
                            }
                        }
                    });
                    return;
                }
                if (imageInfo.getFormat() == 32) {
                    if (!CaptureMode.this.mCaptureResultReady) {
                        synchronized (CaptureMode.this.mRawCallbackSync) {
                            try {
                                CaptureMode.this.mRawCallbackSync.wait();
                            } catch (InterruptedException e2) {
                                LogHelper.m24e(CaptureMode.this.mTag, "raw image callback, block error");
                                return;
                            }
                        }
                    }
                    CaptureMode.this.mCaptureResultReady = false;
                    CaptureMode.this.mIsConvertingDng = true;
                    CameraCharacteristics cameraCharacteristics = Utils.getCameraCharacteristics(CaptureMode.this.mAppController.getActivity(), CaptureMode.this.mSettingServant.getCameraId());
                    Rect rect = (Rect) cameraCharacteristics.get(CameraCharacteristics.SENSOR_INFO_PRE_CORRECTION_ACTIVE_ARRAY_SIZE);
                    if (rect == null) {
                        LogHelper.m26i(CaptureMode.this.mTag, "get raw size error");
                        return;
                    }
                    int iWidth = rect.width();
                    int iHeight = rect.height();
                    LogHelper.m26i(CaptureMode.this.mTag, "image callback, rawWidth = " + iWidth + ",rawHight = " + iHeight);
                    Size size = new Size(iWidth, iHeight);
                    byte[] data2 = imageInfo.getData();
                    int jpegRotation = Utils.getJpegRotation(CaptureMode.this.mCurrentOrientation, Utils.getCameraCharacteristics(CaptureMode.this.mAppController.getActivity(), CaptureMode.this.mSettingServant.getCameraId()));
                    ByteArrayOutputStream byteArrayOutputStreamConvertRawToDng = CaptureMode.this.convertRawToDng(data2, size, jpegRotation, cameraCharacteristics, CaptureMode.this.mCaptureResult);
                    synchronized (CaptureMode.this.mRawTODngSync) {
                        CaptureMode.this.mRawTODngSync.notify();
                    }
                    CaptureMode.this.mIsConvertingDng = false;
                    CaptureMode.this.mIsRawCallbackFinished = true;
                    if (byteArrayOutputStreamConvertRawToDng != null) {
                        CaptureMode.this.updateRawCaptureContentValues(iWidth, iHeight, jpegRotation);
                        try {
                            byte[] byteArray = byteArrayOutputStreamConvertRawToDng.toByteArray();
                            byteArrayOutputStreamConvertRawToDng.close();
                            CaptureMode.this.mCameraServices.getMediaSaver().addImage(byteArray, CaptureMode.this.mRawContentValues, CaptureMode.this.mMediaSavedListener, CaptureMode.this.mAppController.getActivity().getContentResolver());
                        } catch (IOException e3) {
                            LogHelper.m24e(CaptureMode.this.mTag, "dng output stream error");
                        }
                    }
                    CaptureMode.this.mAppController.getActivity().runOnUiThread(new Runnable() { // from class: com.mediatek.camera.v2.mode.normal.CaptureMode.2.2
                        @Override // java.lang.Runnable
                        public void run() {
                            CaptureMode.this.enableCommonView();
                        }
                    });
                }
            }
        };
        this.mVssStreamCallback = new ICaptureStream.CaptureStreamCallback() { // from class: com.mediatek.camera.v2.mode.normal.CaptureMode.3
            @Override // com.mediatek.camera.v2.stream.ICaptureStream.CaptureStreamCallback
            public void onCaptureCompleted(ImageInfo imageInfo) {
                if (imageInfo.getFormat() == 256) {
                    int width = imageInfo.getWidth();
                    int heigth = imageInfo.getHeigth();
                    byte[] data = imageInfo.getData();
                    int orientation = Exif.getOrientation(data);
                    LogHelper.m26i(CaptureMode.this.mTag, "parse jpeg orientation:" + orientation);
                    CaptureMode.this.updateCaptureContentValues(width, heigth, orientation);
                    CaptureMode.this.mCameraServices.getMediaSaver().addImage(data, CaptureMode.this.mCapContentValues, CaptureMode.this.mMediaSavedListener, CaptureMode.this.mAppController.getActivity().getContentResolver());
                    CaptureMode.this.mAppController.getActivity().runOnUiThread(new Runnable() { // from class: com.mediatek.camera.v2.mode.normal.CaptureMode.3.1
                        @Override // java.lang.Runnable
                        public void run() {
                            CaptureMode.this.mAppUi.setShutterButtonEnabled(true, false);
                        }
                    });
                }
            }
        };
        this.mEnableShutterButtonRunnable = new Runnable() { // from class: com.mediatek.camera.v2.mode.normal.CaptureMode.4
            @Override // java.lang.Runnable
            public void run() {
                CaptureMode.this.mAppUi.setShutterButtonEnabled(true, false);
                CaptureMode.this.mAppUi.setShutterButtonEnabled(true, true);
            }
        };
        this.mDisableHintString = new Runnable() { // from class: com.mediatek.camera.v2.mode.normal.CaptureMode.5
            @Override // java.lang.Runnable
            public void run() {
                CaptureMode.this.mAppUi.hideHint();
            }
        };
        this.mAppUi.switchShutterButtonLayout(R.layout.camera_shutter_photo_video_v2);
        this.mTag = new LogHelper.Tag(CaptureMode.class.getSimpleName() + "(" + this.mFeatureTag + ")");
    }

    @Override // com.mediatek.camera.p005v2.mode.AbstractCameraMode
    protected int getModeId() {
        return 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void enableCommonView() {
        String settingValue = this.mSettingServant.getSettingValue("dng_key");
        if (settingValue == null || "off".equals(settingValue)) {
            this.mIsRawCallbackFinished = true;
        }
        if (!this.mIsJpegCallbackFinished || (!this.mIsRawCallbackFinished)) {
            return;
        }
        this.mAppUi.setAllCommonViewEnable(true);
        this.mAppUi.setSwipeEnabled(true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public ByteArrayOutputStream convertRawToDng(byte[] bArr, Size size, int i, CameraCharacteristics cameraCharacteristics, TotalCaptureResult totalCaptureResult) {
        ByteArrayOutputStream byteArrayOutputStream;
        try {
            try {
                DngCreator dngCreator = new DngCreator(cameraCharacteristics, totalCaptureResult);
                byteArrayOutputStream = new ByteArrayOutputStream();
                try {
                    ByteBuffer byteBufferWrap = ByteBuffer.wrap(bArr);
                    int dngOrientation = Utils.getDngOrientation(i);
                    LogHelper.m23d(this.mTag, "convertRawToDng, orientation = " + i + ", dngOrientation = " + dngOrientation);
                    dngCreator.setOrientation(dngOrientation);
                    Location currentLocation = this.mLocationManager.getCurrentLocation();
                    if (currentLocation != null) {
                        dngCreator.setLocation(currentLocation);
                    }
                    dngCreator.writeByteBuffer(byteArrayOutputStream, size, byteBufferWrap, 0L);
                } catch (IOException e) {
                    LogHelper.m24e(this.mTag, "convertRawToDng, dng write error");
                    return byteArrayOutputStream;
                }
            } catch (IOException e2) {
                byteArrayOutputStream = null;
            }
            return byteArrayOutputStream;
        } finally {
            LogHelper.m26i(this.mTag, "convertRawToDng");
        }
    }

    @Override // com.mediatek.camera.p005v2.mode.AbstractCameraMode
    protected ICaptureStream.CaptureStreamCallback getCaptureStreamCallback() {
        if (this.mRecording) {
            return this.mVssStreamCallback;
        }
        return this.mCaptureStreamCallback;
    }

    @Override // com.mediatek.camera.p005v2.mode.AbstractCameraMode
    protected IRecordStream.RecordStreamStatus getRecordStreamCallback() {
        if (this.mRecordStreamCallback == null) {
            this.mRecordStreamCallback = new IRecordStream.RecordStreamStatus() { // from class: com.mediatek.camera.v2.mode.normal.CaptureMode.6
                @Override // com.mediatek.camera.v2.stream.IRecordStream.RecordStreamStatus
                public void onRecordingStarted(boolean z) {
                }

                @Override // com.mediatek.camera.v2.stream.IRecordStream.RecordStreamStatus
                public void onRecordingStoped() throws IOException {
                    LogHelper.m26i(CaptureMode.this.mTag, "onRecordingStoped");
                    CaptureMode.this.mSettingCtroller.doSettingChange("video_key", "off", false);
                    CaptureMode.this.mAppController.getActivity().runOnUiThread(new Runnable() { // from class: com.mediatek.camera.v2.mode.normal.CaptureMode.6.1
                        @Override // java.lang.Runnable
                        public void run() {
                            if (CaptureMode.this.mIsCaptureIntent) {
                                CaptureMode.this.mAppUi.showReviewView(Utils.createBitmapFromVideo(CaptureMode.this.mVideoTempPath, CaptureMode.this.mVideoFileDescriptor != null ? CaptureMode.this.mVideoFileDescriptor.getFileDescriptor() : null, CaptureMode.this.mCameraCamcorderProfile.videoFrameWidth));
                                CaptureMode.this.mAppUi.switchShutterButtonLayout(R.layout.camera_shutter_ok_cancel_v2);
                                CaptureMode.this.mIsInReviewMode = true;
                            }
                        }
                    });
                    if (CaptureMode.this.mVideoFileDescriptor == null && CaptureMode.this.mIsNeedSaveVideo) {
                        CaptureMode.this.updateVideoContentValues();
                        CaptureMode.this.mCameraServices.getMediaSaver().addVideo(CaptureMode.this.mVideoTempPath, CaptureMode.this.mVideoContentValues, CaptureMode.this.mMediaSavedListener, CaptureMode.this.mAppController.getActivity().getContentResolver());
                    }
                    CaptureMode.this.mVideoTempPath = null;
                }

                @Override // com.mediatek.camera.v2.stream.IRecordStream.RecordStreamStatus
                public void onInfo(int i, int i2) throws IOException {
                    LogHelper.m27v(CaptureMode.this.mTag, "[onInfo] what = " + i + "   extra = " + i2);
                    switch (i) {
                        case 800:
                            if (CaptureMode.this.mRecording) {
                                CaptureMode.this.videoShutterButtonClicked();
                                break;
                            }
                            break;
                        case 801:
                            if (CaptureMode.this.mRecording) {
                                CaptureMode.this.mAppUi.showHint(CaptureMode.this.mActivity.getResources().getString(R.string.video_reach_size_limit));
                                CaptureMode.this.mHandler.postDelayed(CaptureMode.this.mDisableHintString, 3000L);
                                CaptureMode.this.videoShutterButtonClicked();
                                break;
                            }
                            break;
                    }
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
        double d = (!this.mRecording || this.mCameraCamcorderProfile == null) ? 0.0d : this.mCameraCamcorderProfile.videoFrameWidth / this.mCameraCamcorderProfile.videoFrameHeight;
        if ("android.media.action.VIDEO_CAPTURE".equals(this.mIntent.getAction())) {
            this.mCameraCamcorderProfile = this.mVideoHelper.fetchProfile(this.mVideoHelper.getRecordingQuality(Integer.valueOf(this.mSettingServant.getCameraId()).intValue()), Integer.valueOf(this.mSettingServant.getCameraId()).intValue());
            d = this.mCameraCamcorderProfile.videoFrameWidth / this.mCameraCamcorderProfile.videoFrameHeight;
        }
        Size optimalPreviewSize = d != 0.0d ? Utils.getOptimalPreviewSize(this.mActivity, Utils.filterSizesByBound(this.mSettingCtroller.getSettingServant(this.mSettingCtroller.getCurrentCameraId()).getSupportedPreviewSizes(), new Size(this.mCameraCamcorderProfile.videoFrameWidth, this.mCameraCamcorderProfile.videoFrameHeight)), d) : null;
        if (optimalPreviewSize != null) {
            LogHelper.m26i(this.mTag, "getPreviewSize size = " + optimalPreviewSize);
            return optimalPreviewSize;
        }
        return super.getPreviewSize();
    }

    @Override // com.mediatek.camera.p005v2.mode.AbstractCameraMode
    public void onMediaEjected() throws IOException {
        LogHelper.m26i(this.mTag, "onMediaEjected mRecording = " + this.mRecording);
        if (this.mRecording) {
            this.mIsNeedSaveVideo = false;
            videoShutterButtonClicked();
        }
    }

    @Override // com.mediatek.camera.p005v2.mode.AbstractCameraMode
    protected Size getCaptureSize() {
        if (this.mRecording && this.mCameraCamcorderProfile != null) {
            return new Size(this.mCameraCamcorderProfile.videoFrameWidth, this.mCameraCamcorderProfile.videoFrameHeight);
        }
        CameraCharacteristics cameraCharacteristics = Utils.getCameraCharacteristics(this.mAppController.getActivity(), this.mSettingServant.getCameraId());
        if ("on".equals(this.mSettingServant.getSettingValue("dng_key"))) {
            this.mDngStream.updateCameraCharacteristics(cameraCharacteristics);
        }
        return super.getCaptureSize();
    }

    @Override // com.mediatek.camera.p005v2.mode.AbstractCameraMode
    protected boolean changingModePictureSize() {
        this.mCaptureSurface = this.mCaptureController.getCaptureInputSurface().get("CaptureStream.Surface");
        LogHelper.m26i(this.mTag, "changingModePictureSize :" + this.mCaptureSurface);
        if ("on".equals(this.mSettingServant.getSettingValue("dng_key"))) {
            this.mRawCaptureSurface = this.mDngStream.getCaptureInputSurface().get("PreviewStream.RawSurface");
            return false;
        }
        return false;
    }

    @Override // com.mediatek.camera.p005v2.mode.AbstractCameraMode
    protected boolean updatePictureSize() {
        boolean zUpdateCaptureSize;
        LogHelper.m26i(this.mTag, "[updatePictureSize]+");
        int captureFormat = getCaptureFormat();
        int i = 30000000;
        Size captureSize = getCaptureSize();
        if (captureSize == null) {
            LogHelper.m26i(this.mTag, "why picture size is nulll?");
            return false;
        }
        LogHelper.m26i(this.mTag, "[updatePictureSize]- pictureSize = " + captureSize.getWidth() + " x " + captureSize.getHeight() + " format = " + captureFormat);
        if ("on".equals(this.mSettingServant.getSettingValue("dng_key"))) {
            zUpdateCaptureSize = this.mDngStream.updateCaptureSize(captureSize, captureFormat);
            this.mDngStream.setCaptureStreamCallback(getCaptureStreamCallback());
        } else {
            this.mDngStream.releaseCaptureStream();
            zUpdateCaptureSize = this.mCaptureController.updateCaptureSize(captureSize, captureFormat);
            this.mCaptureController.setCaptureStreamCallback(getCaptureStreamCallback());
            i = 0;
        }
        if (changingModePictureSize()) {
            zUpdateCaptureSize = true;
        }
        if ((zUpdateCaptureSize && (!"android.media.action.VIDEO_CAPTURE".equals(this.mIntent.getAction()))) || (this.mDngUpdateRemainSize && (!this.mRecording))) {
            this.mAppUi.showLeftCounts(i + Utils.getImageSize(captureSize.getWidth() + "x" + captureSize.getHeight() + "-superfine"), true);
        }
        this.mDngUpdateRemainSize = false;
        return zUpdateCaptureSize;
    }

    @Override // com.mediatek.camera.p005v2.mode.AbstractCameraMode
    public void open(StreamManager streamManager, ViewGroup viewGroup, boolean z) {
        LogHelper.m26i(this.mTag, "[open]+");
        this.mStreamManager = streamManager;
        super.open(streamManager, viewGroup, z);
        this.mDngStream = streamManager.getDngStreamController();
        LogHelper.m26i(this.mTag, "[open]-");
    }

    @Override // com.mediatek.camera.p005v2.mode.AbstractCameraMode
    public void resume() {
        LogHelper.m26i(this.mTag, "[resume]+");
        this.mPaused = false;
        super.resume();
        if ("android.media.action.IMAGE_CAPTURE".equals(this.mIntent.getAction())) {
            this.mAppUi.switchShutterButtonLayout(R.layout.camera_shutter_photo_v2);
        }
        if ("android.media.action.VIDEO_CAPTURE".equals(this.mIntent.getAction()) && (!this.mIsInReviewMode)) {
            this.mAppUi.switchShutterButtonLayout(R.layout.camera_shutter_video_v2);
            this.mAppUi.showLeftTime(((this.mCameraCamcorderProfile.videoBitRate + this.mCameraCamcorderProfile.audioBitRate) >> 3) / 1000);
        }
        LogHelper.m26i(this.mTag, "[resume]-");
    }

    @Override // com.mediatek.camera.p005v2.mode.AbstractCameraMode
    public void pause() throws IOException {
        LogHelper.m26i(this.mTag, "[pause]+");
        if (this.mIsConvertingDng) {
            synchronized (this.mRawTODngSync) {
                try {
                    this.mRawTODngSync.wait();
                } catch (InterruptedException e) {
                    LogHelper.m24e(this.mTag, "pause, block error");
                }
            }
        }
        this.mPaused = true;
        super.pause();
        this.mDngStream.releaseCaptureStream();
        this.mIsConvertingDng = false;
        if (this.mRecording) {
            onShutterClicked(true);
        }
        if (this.mIsCaptureIntent && "android.media.action.IMAGE_CAPTURE".equals(this.mIntent.getAction())) {
            this.mAppUi.switchShutterButtonLayout(R.layout.camera_shutter_photo_v2);
            this.mAppUi.hideReviewView();
        }
        LogHelper.m26i(this.mTag, "[pause]-");
    }

    @Override // com.mediatek.camera.p005v2.mode.AbstractCameraMode
    public void close() {
        LogHelper.m26i(this.mTag, "[close]+");
        super.close();
        LogHelper.m26i(this.mTag, "[close]-");
    }

    @Override // com.mediatek.camera.p005v2.mode.ModeController
    public void onShutterPressed(boolean z) {
    }

    @Override // com.mediatek.camera.p005v2.mode.ModeController
    public void onShutterClicked(boolean z) throws IOException {
        if (z) {
            videoShutterButtonClicked();
        } else if (this.mRecording) {
            videoSnapshotShutterButtonClicked();
        } else {
            photoShutterButtonClicked();
        }
    }

    @Override // com.mediatek.camera.p005v2.mode.ModeController
    public void onShutterLongPressed(boolean z) {
    }

    @Override // com.mediatek.camera.p005v2.mode.ModeController
    public void onShutterReleased(boolean z) {
    }

    @Override // com.mediatek.camera.p005v2.mode.AbstractCameraMode
    public void onOrientationChanged(int i) {
        this.mCurrentOrientation = i;
    }

    @Override // com.mediatek.camera.p005v2.mode.AbstractCameraMode
    public void onOkClick() throws Throwable {
        String action = this.mIntent.getAction();
        LogHelper.m26i(this.mTag, "[onOkClick], action:" + action);
        if ("android.media.action.IMAGE_CAPTURE".equals(action)) {
            doPhotoAttach();
        } else if ("android.media.action.VIDEO_CAPTURE".equals(action)) {
            doVideoAttach();
        }
        this.mIsInReviewMode = false;
    }

    @Override // com.mediatek.camera.p005v2.mode.AbstractCameraMode
    public void onCancelClick() {
        LogHelper.m26i(this.mTag, "[onCancelClick]...");
        doCancel();
        this.mIsInReviewMode = false;
    }

    @Override // com.mediatek.camera.p005v2.mode.AbstractCameraMode
    protected boolean doSettingChanged(Map<String, String> map) {
        boolean z;
        if (map.get("dng_key") != null) {
            boolean z2 = !this.mRecording;
            this.mDngUpdateRemainSize = true;
            updatePictureSize();
            z = z2;
        } else {
            z = false;
        }
        if (map.get("pref_video_3dnr_key") != null) {
            this.mModuleListener.requestChangeCaptureRequets(false, this.mRecording ? ModuleListener.RequestType.RECORDING : ModuleListener.RequestType.PREVIEW, ModuleListener.CaptureType.REPEATING_REQUEST);
        }
        return z;
    }

    @Override // com.mediatek.camera.p005v2.mode.AbstractCameraMode
    protected void updateCaredSettingChangedKeys() {
        super.updateCaredSettingChangedKeys();
        addCaredSettingChangedKeys("dng_key");
        addCaredSettingChangedKeys("pref_video_3dnr_key");
    }

    @Override // com.mediatek.camera.p005v2.mode.ModeController
    public void configuringSessionOutputs(List<Surface> list, boolean z) {
        LogHelper.m26i(this.mTag, "configuringOutputs");
        Assert.assertNotNull(list);
        checkPreviewSurfaceReady();
        LogHelper.m26i(this.mTag, "configuringOutputs, preview surface: " + this.mPreviewSurface);
        list.add(this.mPreviewSurface);
        LogHelper.m26i(this.mTag, "configuringOutputs, capture surface: " + this.mCaptureSurface);
        if (this.mCaptureSurface != null && this.mCaptureSurface.isValid()) {
            list.add(this.mCaptureSurface);
        }
        LogHelper.m26i(this.mTag, "configuringOutputs, raw surface:" + this.mRawCaptureSurface);
        if ("on".equals(this.mSettingServant.getSettingValue("dng_key")) && this.mRawCaptureSurface != null && (!this.mRecording)) {
            list.add(this.mRawCaptureSurface);
        }
        LogHelper.m26i(this.mTag, "configuringOutputs, record surface:" + this.mRecordSurface);
        if (this.mRecording && this.mRecordSurface != null) {
            list.add(this.mRecordSurface);
        }
    }

    @Override // com.mediatek.camera.p005v2.mode.ModeController
    public void configuringSessionRequests(Map<ModuleListener.RequestType, CaptureRequest.Builder> map, boolean z) {
        Set<ModuleListener.RequestType> setKeySet = map.keySet();
        for (ModuleListener.RequestType requestType : setKeySet) {
            configre3DnrValue(map.get(requestType));
            LogHelper.m26i(this.mTag, "configuringSessionRequests requestType = " + requestType + " request number = " + setKeySet.size());
            switch (m38x960f455f()[requestType.ordinal()]) {
                case 1:
                    configuringPreviewRequests(map.get(requestType));
                    break;
                case 2:
                    configuringRecordingRequests(map.get(requestType));
                    break;
                case 3:
                    configuringCaptureRequests(map.get(requestType));
                    break;
                case 4:
                    configuringCaptureRequests(map.get(requestType));
                    configreEisValue(map.get(requestType));
                    break;
            }
        }
    }

    @Override // com.mediatek.camera.p005v2.mode.AbstractCameraMode
    public CameraCaptureSession.CaptureCallback getCaptureCallback() {
        return new CameraCaptureSession.CaptureCallback() { // from class: com.mediatek.camera.v2.mode.normal.CaptureMode.7
            @Override // android.hardware.camera2.CameraCaptureSession.CaptureCallback
            public void onCaptureStarted(CameraCaptureSession cameraCaptureSession, CaptureRequest captureRequest, long j, long j2) {
                CaptureMode.this.mCameraServices.getSoundPlayback().play(3);
            }

            @Override // android.hardware.camera2.CameraCaptureSession.CaptureCallback
            public void onCaptureCompleted(CameraCaptureSession cameraCaptureSession, CaptureRequest captureRequest, TotalCaptureResult totalCaptureResult) {
                LogHelper.m26i(CaptureMode.this.mTag, "CaptureCallback onCaptureCompleted request: " + captureRequest + " result: " + totalCaptureResult);
                CaptureMode.this.mCaptureResultReady = true;
                CaptureMode.this.mCaptureResult = totalCaptureResult;
                synchronized (CaptureMode.this.mRawCallbackSync) {
                    CaptureMode.this.mRawCallbackSync.notify();
                }
            }
        };
    }

    @Override // com.mediatek.camera.p005v2.mode.AbstractCameraMode
    public boolean onBackPressed() throws IOException {
        if (this.mRecording) {
            onShutterClicked(true);
            return true;
        }
        return false;
    }

    @Override // com.mediatek.camera.p005v2.mode.AbstractCameraMode
    public void onPlay() {
        LogHelper.m26i(this.mTag, "[onPlay]...");
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addFlags(1);
        Bundle extras = intent.getExtras();
        intent.putExtra("CanShare", extras != null ? extras.getBoolean("CanShare", true) : true);
        intent.setDataAndType(this.mUri, this.mVideoHelper.convertOutputFormatToMimeType(this.mCameraCamcorderProfile.fileFormat));
        this.mActivity.startActivity(intent);
    }

    @Override // com.mediatek.camera.p005v2.mode.AbstractCameraMode
    public void onRetake() {
        String action = this.mIntent.getAction();
        LogHelper.m26i(this.mTag, "[onRetake], action:" + action);
        this.mAppUi.hideReviewView();
        if ("android.media.action.IMAGE_CAPTURE".equals(action)) {
            this.mAppUi.switchShutterButtonLayout(R.layout.camera_shutter_photo_v2);
        } else if ("android.media.action.VIDEO_CAPTURE".equals(action)) {
            this.mAppUi.switchShutterButtonLayout(R.layout.camera_shutter_video_v2);
        }
        this.mAppUi.setAllCommonViewEnable(true);
        this.mAppUi.setShutterButtonEnabled(true, false);
    }

    private void configuringPreviewRequests(CaptureRequest.Builder builder) {
        Assert.assertNotNull(builder);
        if (this.mPreviewSurface != null && this.mPreviewSurface.isValid()) {
            builder.addTarget(this.mPreviewSurface);
        }
    }

    private void configuringCaptureRequests(CaptureRequest.Builder builder) {
        Assert.assertNotNull(builder);
        builder.addTarget(this.mPreviewSurface);
        if (this.mCaptureSurface != null) {
            builder.addTarget(this.mCaptureSurface);
        }
        if ("on".equals(this.mSettingServant.getSettingValue("dng_key")) && this.mRawCaptureSurface != null) {
            builder.addTarget(this.mRawCaptureSurface);
        }
        builder.set(CaptureRequest.JPEG_QUALITY, JPEG_QUALITY);
        builder.set(CaptureRequest.JPEG_ORIENTATION, Integer.valueOf(Utils.getJpegRotation(this.mCurrentOrientation, Utils.getCameraCharacteristics(this.mAppController.getActivity(), this.mSettingServant.getCameraId()))));
    }

    private void configuringRecordingRequests(CaptureRequest.Builder builder) {
        builder.addTarget(this.mPreviewSurface);
        if (this.mRecordSurface != null) {
            builder.addTarget(this.mRecordSurface);
        }
        configreEisValue(builder);
        configreAeFpsRange(builder);
    }

    private void photoShutterButtonClicked() {
        LogHelper.m26i(this.mTag, "photoShutterButtonClicked");
        if (this.mStorageService.getCaptureStorageSpace() <= 0) {
            LogHelper.m28w(this.mTag, "Not enough space or storage not available, remaining:" + this.mStorageService.getCaptureStorageSpace());
            return;
        }
        this.mIsJpegCallbackFinished = false;
        this.mIsRawCallbackFinished = false;
        this.mShutterDateTaken = System.currentTimeMillis();
        this.mAppUi.setAllCommonViewEnable(false);
        this.mAppUi.setSwipeEnabled(false);
        this.mModuleListener.get3AController(null).aePreTriggerAndCapture();
    }

    private void videoSnapshotShutterButtonClicked() {
        this.mAppUi.setShutterButtonEnabled(false, false);
        this.mModuleListener.requestChangeCaptureRequets(false, ModuleListener.RequestType.VIDEO_SNAP_SHOT, ModuleListener.CaptureType.CAPTURE);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void videoShutterButtonClicked() throws IOException {
        LogHelper.m26i(this.mTag, "videoShutterButtonClicked mRecording = " + this.mRecording);
        if (!this.mRecording && (this.mStorageService.getRecordStorageSpace() <= 0 || this.mPaused)) {
            LogHelper.m28w(this.mTag, "Not enough space or storage not available, remaining:" + this.mStorageService.getRecordStorageSpace());
            this.mAppUi.showPickerManagerUi();
            return;
        }
        this.mAppUi.setShutterButtonEnabled(false, false);
        this.mAppUi.setShutterButtonEnabled(false, true);
        if (this.mRecording) {
            stopRecording();
            this.mAppUi.stopShowCommonUI(false);
            if (!this.mIsCaptureIntent) {
                this.mAppUi.showSettingUi();
                this.mAppUi.showIndicatorManagerUi();
                this.mAppUi.showPickerManagerUi();
            }
            this.mAppUi.switchShutterButtonImageResource(R.drawable.btn_video, true);
            this.mAppUi.setSwipeEnabled(true);
            this.mAppUi.showModeOptionsUi();
            this.mAppUi.setThumbnailManagerEnable(true);
            Size captureSize = getCaptureSize();
            String str = captureSize.getWidth() + "x" + captureSize.getHeight() + "-superfine";
            if (this.mIsCaptureIntent) {
                return;
            }
            this.mAppUi.showLeftCounts(Utils.getImageSize(str), true);
            return;
        }
        this.mIsNeedSaveVideo = true;
        this.mCameraServices.getSoundPlayback().play(1);
        startRecording();
        this.mAppUi.stopShowCommonUI(true);
        this.mAppUi.hideSettingUi();
        this.mAppUi.switchShutterButtonImageResource(R.drawable.btn_video_mask, true);
        Utils.getCameraCharacteristics(this.mAppController.getActivity(), this.mSettingServant.getCameraId());
        this.mAppUi.setSwipeEnabled(false);
        this.mAppUi.dismissInfo(true);
        this.mAppUi.hideModeOptionsUi();
        this.mAppUi.hideIndicatorManagerUi();
        this.mAppUi.hidePickerManagerUi();
        this.mAppUi.setThumbnailManagerEnable(false);
        this.mAppUi.showLeftTime(((this.mCameraCamcorderProfile.videoBitRate + this.mCameraCamcorderProfile.audioBitRate) >> 3) / 1000);
    }

    private void startRecording() throws IOException {
        LogHelper.m26i(this.mTag, "[startRecording]+");
        this.mRecording = true;
        pauseAudioPlayback();
        prepareRecording();
        updatePictureSize();
        updatePreviewSize(new IPreviewStream.PreviewSurfaceCallback() { // from class: com.mediatek.camera.v2.mode.normal.CaptureMode.8
            @Override // com.mediatek.camera.v2.stream.IPreviewStream.PreviewSurfaceCallback
            public void onPreviewSufaceIsReady(boolean z) {
                LogHelper.m26i(CaptureMode.this.mTag, "[startRecording] onPreviewSufaceIsReady");
                CaptureMode.this.mModuleListener.requestChangeSessionOutputs(true);
                CaptureMode.this.mSettingCtroller.doSettingChange("video_key", "on", false);
                CaptureMode.this.mModuleListener.requestChangeCaptureRequets(true, ModuleListener.RequestType.RECORDING, ModuleListener.CaptureType.REPEATING_REQUEST);
                CaptureMode.this.mRecordController.startRecord();
                CaptureMode.this.mAppController.enableKeepScreenOn(true);
                CaptureMode.this.mHandler.postDelayed(CaptureMode.this.mEnableShutterButtonRunnable, 500L);
                LogHelper.m26i(CaptureMode.this.mTag, "[startRecording]-");
            }
        });
    }

    private void stopRecording() {
        LogHelper.m26i(this.mTag, "stopRecording");
        this.mRecording = false;
        releaseAudioFocus();
        if (this.mPaused) {
            doStopRecording(true);
        } else {
            updatePictureSize();
            updatePreviewSize(new IPreviewStream.PreviewSurfaceCallback() { // from class: com.mediatek.camera.v2.mode.normal.CaptureMode.9
                @Override // com.mediatek.camera.v2.stream.IPreviewStream.PreviewSurfaceCallback
                public void onPreviewSufaceIsReady(boolean z) {
                    CaptureMode.this.doStopRecording(false);
                }
            });
        }
    }

    private void initializeRequestedLimits() throws IOException {
        closeVideoFileDescriptor();
        initializeLimiteds();
    }

    private void initializeLimiteds() {
        Uri uri;
        this.mRequestSizeLimit = this.mVideoHelper.getRequestSizeLimit(this.mCameraCamcorderProfile, true, this.mIsCaptureIntent, this.mIntent);
        this.mRequestDurationLimit = this.mIntent.getIntExtra("android.intent.extra.durationLimit", 0);
        if (this.mIsCaptureIntent && (uri = (Uri) this.mIntent.getParcelableExtra("output")) != null) {
            try {
                this.mVideoFileDescriptor = this.mActivity.getContentResolver().openFileDescriptor(uri, "rw");
                this.mUri = uri;
            } catch (FileNotFoundException e) {
                LogHelper.m24e(this.mTag, e.toString());
            }
        }
    }

    private void closeVideoFileDescriptor() throws IOException {
        this.mVideoHelper.closeVideoFileDescriptor(this.mVideoFileDescriptor);
        this.mVideoFileDescriptor = null;
    }

    private void prepareRecording() throws IOException {
        int iIntValue = Integer.valueOf(this.mSettingServant.getCameraId()).intValue();
        this.mCameraCamcorderProfile = this.mVideoHelper.fetchProfile(this.mVideoHelper.getRecordingQuality(iIntValue), iIntValue);
        String settingValue = this.mSettingServant.getSettingValue("pref_camera_scenemode_key");
        if (settingValue != null && SettingConvertor.SceneMode.NIGHT.toString().equalsIgnoreCase(settingValue)) {
            this.mCameraCamcorderProfile.videoFrameRate /= 2;
            this.mCameraCamcorderProfile.videoBitRate /= 2;
        }
        boolean zEquals = "on".equals(this.mSettingServant.getSettingValue("pref_camera_recordaudio_key"));
        this.mRecordingRotation = Utils.getRecordingRotation(this.mCurrentOrientation, Utils.getCameraCharacteristics(this.mAppController.getActivity(), this.mSettingServant.getCameraId()));
        if (this.mIsCaptureIntent) {
            initializeRequestedLimits();
        }
        if (this.mVideoFileDescriptor != null) {
            this.mRecordController.setOutputFile(this.mVideoFileDescriptor.getFileDescriptor());
        } else {
            this.mVideoTempPath = this.mVideoHelper.generateVideoFileName(this.mCameraCamcorderProfile.fileFormat, null);
            this.mRecordController.setOutputFile(this.mVideoTempPath);
        }
        LogHelper.m26i(this.mTag, "prepareRecording enableAudio = " + zEquals);
        prepareMediaRecordingParamters();
        this.mRecordController.setMaxFileSize(this.mVideoHelper.getRecorderMaxSize(this.mRequestSizeLimit));
        this.mRecordController.setMaxDuration(this.mRequestDurationLimit * 1000);
        this.mRecordController.setRecordingProfile(this.mCameraCamcorderProfile);
        this.mRecordController.enalbeAudioRecording(zEquals);
        this.mRecordController.setOutputFile(this.mVideoTempPath);
        this.mRecordController.setOrientationHint(this.mRecordingRotation);
        this.mRecordController.setAudioSource(5);
        this.mRecordController.setVideoSource(2);
        this.mRecordController.prepareRecord();
        this.mRecordSurface = this.mRecordController.getRecordInputSurface();
    }

    protected void checkPreviewSurfaceReady() {
        this.mPreviewSurface = this.mPreviewController.getPreviewInputSurfaces().get("PreviewStream.Surface");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateRawCaptureContentValues(int i, int i2, int i3) {
        this.mRawContentValues = new ContentValues();
        String strCreateDngName = Utils.createDngName(this.mShutterDateTaken);
        String str = strCreateDngName + ".dng";
        String str2 = this.mStorageService.getFileDirectory() + '/' + str;
        this.mRawContentValues.put("datetaken", Long.valueOf(this.mShutterDateTaken));
        this.mRawContentValues.put("title", strCreateDngName);
        this.mRawContentValues.put("_display_name", str);
        this.mRawContentValues.put("_data", str2);
        this.mRawContentValues.put("mime_type", "image/x-adobe-dng");
        this.mRawContentValues.put("width", Integer.valueOf(i));
        this.mRawContentValues.put("height", Integer.valueOf(i2));
        this.mRawContentValues.put("orientation", Integer.valueOf(i3));
        this.mLocation = this.mLocationManager.getCurrentLocation();
        if (this.mLocation != null) {
            this.mRawContentValues.put("latitude", Double.valueOf(this.mLocation.getLatitude()));
            this.mRawContentValues.put("longitude", Double.valueOf(this.mLocation.getLongitude()));
        }
        LogHelper.m26i(this.mTag, "updateRawCaptureContentValues filename: " + str);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateCaptureContentValues(int i, int i2, int i3) {
        long jCurrentTimeMillis;
        String strCreateJpegName;
        this.mCapContentValues = new ContentValues();
        if ("on".equals(this.mSettingServant.getSettingValue("dng_key"))) {
            jCurrentTimeMillis = this.mShutterDateTaken;
            strCreateJpegName = Utils.createDngName(jCurrentTimeMillis);
        } else {
            jCurrentTimeMillis = System.currentTimeMillis();
            strCreateJpegName = Utils.createJpegName(jCurrentTimeMillis);
        }
        String str = strCreateJpegName + ".jpg";
        LogHelper.m26i(this.mTag, "updateCaptureContentValues filename: " + str);
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
        LogHelper.m26i(this.mTag, "updateCaptureContentValues orientation: " + i3 + ", width: " + i + ", height: " + i2);
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

    /* JADX WARN: Multi-variable type inference failed */
    private void doPhotoAttach() throws Throwable {
        Throwable th;
        OutputStream outputStreamOpenOutputStream;
        FileOutputStream fileOutputStreamOpenFileOutput = null;
        int orientation = Exif.getOrientation(this.mJpegData);
        updateCaptureContentValues(this.mImageWidth, this.mImageHeight, orientation);
        this.mCameraServices.getMediaSaver().addImage(this.mJpegData, this.mCapContentValues, this.mMediaSavedListener, this.mAppController.getActivity().getContentResolver());
        Uri uri = (Uri) this.mIntent.getParcelableExtra("output");
        String stringExtra = this.mIntent.getStringExtra("crop");
        try {
            if (stringExtra != null) {
                File fileStreamPath = this.mActivity.getFileStreamPath("crop-temp");
                fileStreamPath.delete();
                fileOutputStreamOpenFileOutput = this.mActivity.openFileOutput("crop-temp", 0);
                fileOutputStreamOpenFileOutput.write(this.mJpegData);
                fileOutputStreamOpenFileOutput.close();
                Uri uriFromFile = Uri.fromFile(fileStreamPath);
                Utils.closeSilently(fileOutputStreamOpenFileOutput);
                Bundle bundle = new Bundle();
                if (stringExtra.equals("circle")) {
                    bundle.putString("circleCrop", "true");
                }
                if (uri != 0) {
                    bundle.putParcelable("output", uri);
                } else {
                    bundle.putBoolean("return-data", true);
                }
                Intent intent = new Intent("com.android.camera.action.CROP");
                intent.setData(uriFromFile);
                intent.putExtras(bundle);
                this.mActivity.startActivityForResult(intent, 1000);
                return;
            }
            try {
                if (uri == 0) {
                    this.mAppController.setResultExAndFinish(-1, new Intent("inline-data").putExtra("data", Utils.rotate(Utils.makeBitmap(this.mJpegData, 51200), orientation)));
                    return;
                }
                try {
                    outputStreamOpenOutputStream = this.mActivity.getContentResolver().openOutputStream(uri);
                    if (outputStreamOpenOutputStream != null) {
                        try {
                            outputStreamOpenOutputStream.write(this.mJpegData);
                            outputStreamOpenOutputStream.close();
                        } catch (IOException e) {
                            LogHelper.m24e(this.mTag, "IOException, when doAttach");
                            Utils.closeSilently(outputStreamOpenOutputStream);
                            return;
                        }
                    }
                    this.mAppController.setResultExAndFinish(-1);
                    Utils.closeSilently(outputStreamOpenOutputStream);
                } catch (IOException e2) {
                    outputStreamOpenOutputStream = null;
                } catch (Throwable th2) {
                    th = th2;
                    throw th;
                }
            } catch (Throwable th3) {
                fileOutputStreamOpenFileOutput = uri;
                th = th3;
            }
        } catch (FileNotFoundException e3) {
            this.mAppController.setResultExAndFinish(0);
            LogHelper.m24e(this.mTag, "FileNotFoundException, when doAttach");
        } catch (IOException e4) {
            this.mAppController.setResultExAndFinish(0);
            LogHelper.m24e(this.mTag, "IOException2, when doAttach");
        } finally {
            Utils.closeSilently(fileOutputStreamOpenFileOutput);
        }
    }

    private void doVideoAttach() {
        Intent intent = new Intent();
        intent.setData(this.mUri);
        intent.addFlags(1);
        this.mAppController.setResultExAndFinish(-1, intent);
    }

    private void doCancel() {
        this.mAppController.setResultExAndFinish(0, new Intent());
    }

    private void configre3DnrValue(CaptureRequest.Builder builder) {
        List<String> supportedValues = this.mSettingServant.getSupportedValues("pref_video_3dnr_key");
        if (supportedValues == null || supportedValues.size() <= 1) {
            LogHelper.m26i(this.mTag, "not support 3dnr,not configure 3dnr session");
            return;
        }
        String settingValue = this.mSettingServant.getSettingValue("pref_video_3dnr_key");
        LogHelper.m26i(this.mTag, "configre3DnrValue nr3dValue = " + settingValue);
        if ("on".equals(settingValue)) {
            builder.set(TagRequest.STATISTICS_3DNR_MODE, 1);
        } else {
            builder.set(TagRequest.STATISTICS_3DNR_MODE, 0);
        }
    }

    private void configreAeFpsRange(CaptureRequest.Builder builder) {
        Range[] rangeArr = (Range[]) Utils.getCameraCharacteristics(this.mAppController.getActivity(), this.mSettingServant.getCameraId()).get(CameraCharacteristics.CONTROL_AE_AVAILABLE_TARGET_FPS_RANGES);
        int i = 0;
        int iIntValue = this.mCameraCamcorderProfile.videoFrameRate;
        while (true) {
            int i2 = i;
            if (i2 < rangeArr.length) {
                if (rangeArr[i2].contains((Range) Integer.valueOf(this.mCameraCamcorderProfile.videoFrameRate)) && ((Integer) rangeArr[i2].getLower()).intValue() <= iIntValue) {
                    iIntValue = ((Integer) rangeArr[i2].getLower()).intValue();
                }
                i = i2 + 1;
            } else {
                Range range = new Range(Integer.valueOf(iIntValue), Integer.valueOf(this.mCameraCamcorderProfile.videoFrameRate));
                builder.set(CaptureRequest.CONTROL_AE_TARGET_FPS_RANGE, range);
                LogHelper.m26i(this.mTag, "configreAeFpsRange = " + range.toString());
                return;
            }
        }
    }

    private void configreEisValue(CaptureRequest.Builder builder) {
        String settingValue = this.mSettingServant.getSettingValue("pref_video_eis_key");
        LogHelper.m26i(this.mTag, "configuringRecordingRequests eisValue = " + settingValue);
        if ("on".equals(settingValue)) {
            builder.set(CaptureRequest.CONTROL_VIDEO_STABILIZATION_MODE, 1);
        } else {
            builder.set(CaptureRequest.CONTROL_VIDEO_STABILIZATION_MODE, 0);
        }
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

    /* JADX INFO: Access modifiers changed from: private */
    public void doStopRecording(boolean z) {
        LogHelper.m26i(this.mTag, "[doStopRecording]+ mRecordSurface : " + this.mRecordSurface);
        this.mRecordSurface = null;
        if (!z) {
            this.mModuleListener.requestChangeSessionOutputs(true);
            this.mModuleListener.requestChangeCaptureRequets(true, ModuleListener.RequestType.PREVIEW, ModuleListener.CaptureType.REPEATING_REQUEST);
        }
        try {
            this.mRecordController.stopRecord(true);
            if (this.mVideoFileDescriptor == null && this.mIsNeedSaveVideo) {
                this.mAppUi.showSavingProgress(this.mAppController.getActivity().getResources().getString(R.string.saving));
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
            LogHelper.m24e(this.mTag, "doStopRecording with exception:" + e);
            this.mCameraCamcorderProfile = null;
        } finally {
            this.mCameraServices.getSoundPlayback().play(2);
            this.mAppController.enableKeepScreenOn(false);
            this.mAppUi.setShutterButtonEnabled(true, false);
            this.mAppUi.setShutterButtonEnabled(true, true);
            LogHelper.m26i(this.mTag, "[doStopRecording]-");
        }
    }
}
