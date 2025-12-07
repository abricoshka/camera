package com.mediatek.camera.p005v2.mode;

import android.app.Activity;
import android.content.Intent;
import android.graphics.RectF;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.location.Location;
import android.media.AudioManager;
import android.util.Size;
import android.view.ViewGroup;
import com.android.camera.p002v2.app.location.LocationManager;
import com.mediatek.camera.debug.LogHelper;
import com.mediatek.camera.p005v2.mode.ModeController;
import com.mediatek.camera.p005v2.mode.normal.CaptureMode;
import com.mediatek.camera.p005v2.mode.normal.VideoHelper;
import com.mediatek.camera.p005v2.mode.pip.PipMode;
import com.mediatek.camera.p005v2.module.ModuleListener;
import com.mediatek.camera.p005v2.platform.app.AppController;
import com.mediatek.camera.p005v2.platform.app.AppUi;
import com.mediatek.camera.p005v2.services.CameraServices;
import com.mediatek.camera.p005v2.services.storage.IStorageService;
import com.mediatek.camera.p005v2.setting.ISettingServant;
import com.mediatek.camera.p005v2.setting.SettingCtrl;
import com.mediatek.camera.p005v2.stream.ICaptureStream;
import com.mediatek.camera.p005v2.stream.IPreviewStream;
import com.mediatek.camera.p005v2.stream.IRecordStream;
import com.mediatek.camera.p005v2.stream.ImageInfo;
import com.mediatek.camera.p005v2.stream.RecordStreamView;
import com.mediatek.camera.p005v2.stream.StreamManager;
import com.mediatek.camera.p005v2.util.Utils;
import java.util.ArrayList;
import java.util.Map;
import junit.framework.Assert;

/* loaded from: classes.dex */
public abstract class AbstractCameraMode implements ModeController, ISettingServant.ISettingChangedListener {
    protected static final Byte JPEG_QUALITY = (byte) 90;
    protected Activity mActivity;
    protected final AppController mAppController;
    protected final AppUi mAppUi;
    protected final CameraServices mCameraServices;
    protected ICaptureStream mCaptureController;
    private ICaptureStream.CaptureStreamCallback mCaptureStreamCallback;
    protected ArrayList<String> mCaredSettingChangedKeys = new ArrayList<>();
    protected final String mFeatureTag;
    protected Intent mIntent;
    protected boolean mIsCaptureIntent;
    protected Location mLocation;
    protected LocationManager mLocationManager;
    protected final ModuleListener mModuleListener;
    protected IPreviewStream mPreviewController;
    private IPreviewStream.PreviewStreamCallback mPreviewStreamCallback;
    protected IRecordStream mRecordController;
    private RecordStreamView mRecordStreamView;
    protected SettingCtrl mSettingCtroller;
    protected ISettingServant mSettingServant;
    protected IStorageService mStorageService;
    private final LogHelper.Tag mTag;
    protected VideoHelper mVideoHelper;

    protected abstract int getModeId();

    public AbstractCameraMode(AppController appController, ModuleListener moduleListener) {
        Assert.assertNotNull(appController);
        this.mAppController = appController;
        this.mAppUi = appController.getCameraAppUi();
        this.mActivity = appController.getActivity();
        this.mIntent = appController.getActivity().getIntent();
        this.mModuleListener = moduleListener;
        this.mCameraServices = appController.getServices();
        this.mStorageService = this.mCameraServices.getStorageService();
        this.mSettingCtroller = this.mCameraServices.getSettingController();
        this.mSettingServant = this.mSettingCtroller.getSettingServant(null);
        this.mFeatureTag = getTagByModeId(getModeId());
        this.mLocationManager = appController.getLocationManager();
        this.mTag = new LogHelper.Tag(AbstractCameraMode.class.getSimpleName() + "(" + this.mFeatureTag + ")");
        updateCaredSettingChangedKeys();
    }

    @Override // com.mediatek.camera.v2.setting.ISettingServant.ISettingChangedListener
    public void onSettingChanged(Map<String, String> map) {
        final boolean zDoSettingChanged = doSettingChanged(map);
        String str = map.get("pref_camera_picturesize_ratio_key");
        String str2 = map.get("pref_camera_picturesize_key");
        if (str != null || str2 != null) {
            updatePreviewSize(new IPreviewStream.PreviewSurfaceCallback() { // from class: com.mediatek.camera.v2.mode.AbstractCameraMode.1
                @Override // com.mediatek.camera.v2.stream.IPreviewStream.PreviewSurfaceCallback
                public void onPreviewSufaceIsReady(boolean z) {
                    boolean zUpdatePictureSize = AbstractCameraMode.this.updatePictureSize();
                    AbstractCameraMode.this.mModuleListener.onPreviewSurfaceReady();
                    if (zDoSettingChanged || z || zUpdatePictureSize) {
                        AbstractCameraMode.this.mModuleListener.requestChangeSessionOutputs(true);
                    }
                }
            });
        } else if (zDoSettingChanged) {
            this.mModuleListener.requestChangeSessionOutputs(true);
        }
    }

    public void open(StreamManager streamManager, ViewGroup viewGroup, boolean z) {
        this.mIsCaptureIntent = z;
        initializeStreamControllers(streamManager);
        this.mRecordStreamView = new RecordStreamView(this.mAppController.getActivity(), this.mRecordController, this.mAppUi.getModuleLayoutRoot(), z);
        this.mSettingServant.registerSettingChangedListener(this, this.mCaredSettingChangedKeys, 1);
        this.mVideoHelper = new VideoHelper(this.mCameraServices, this.mIntent, this.mIsCaptureIntent, this.mSettingCtroller);
    }

    public void close() {
        unInitializeStreamControllers();
        this.mRecordStreamView.close();
        this.mSettingServant.unRegisterSettingChangedListener(this);
    }

    public void resume() {
        updatePreviewSize(new IPreviewStream.PreviewSurfaceCallback() { // from class: com.mediatek.camera.v2.mode.AbstractCameraMode.2
            @Override // com.mediatek.camera.v2.stream.IPreviewStream.PreviewSurfaceCallback
            public void onPreviewSufaceIsReady(boolean z) {
                boolean zUpdatePictureSize = AbstractCameraMode.this.updatePictureSize() | z;
                AbstractCameraMode.this.mModuleListener.onPreviewSurfaceReady();
                if (zUpdatePictureSize) {
                    AbstractCameraMode.this.mModuleListener.requestChangeSessionOutputs(true);
                }
            }
        });
        this.mAppUi.setAllCommonViewEnable(true);
        this.mAppUi.setSwipeEnabled(true);
    }

    public void onActivityPause() {
    }

    public void pause() {
        this.mCaptureController.releaseCaptureStream();
    }

    public void onOrientationChanged(int i) {
    }

    public void onPreviewVisibilityChanged(int i) {
    }

    public void onPreviewAreaChanged(RectF rectF) {
    }

    public void onOkClick() {
    }

    public void onCancelClick() {
    }

    public ModeController.ModeGestureListener getModeGestureListener() {
        return null;
    }

    public void prepareSurfaceBeforeOpenCamera() {
        LogHelper.m26i(this.mTag, "[prepareSurfaceBeforeOpenCamera]+");
        updatePreviewSize(new IPreviewStream.PreviewSurfaceCallback() { // from class: com.mediatek.camera.v2.mode.AbstractCameraMode.3
            @Override // com.mediatek.camera.v2.stream.IPreviewStream.PreviewSurfaceCallback
            public void onPreviewSufaceIsReady(boolean z) {
                boolean zUpdatePictureSize = AbstractCameraMode.this.updatePictureSize() | z;
                AbstractCameraMode.this.mModuleListener.onPreviewSurfaceReady();
                if (zUpdatePictureSize) {
                    AbstractCameraMode.this.mModuleListener.requestChangeSessionOutputs(true);
                }
            }
        });
        LogHelper.m26i(this.mTag, "[prepareSurfaceBeforeOpenCamera]-");
    }

    public void onFirstFrameAvailable() {
        if (this.mPreviewController != null) {
            this.mPreviewController.onFirstFrameAvailable();
        }
    }

    public void onPreviewCaptureStarted(CaptureRequest captureRequest, long j, long j2) {
    }

    public void onPreviewCaptureCompleted(CaptureRequest captureRequest, TotalCaptureResult totalCaptureResult) {
    }

    public CameraCaptureSession.CaptureCallback getCaptureCallback() {
        return null;
    }

    public boolean onBackPressed() {
        return false;
    }

    public boolean switchCamera(String str) {
        return false;
    }

    public void onPlay() {
    }

    public void onRetake() {
    }

    protected boolean doSettingChanged(Map<String, String> map) {
        return false;
    }

    protected void updateCaredSettingChangedKeys() {
        addCaredSettingChangedKeys("pref_camera_picturesize_ratio_key");
        addCaredSettingChangedKeys("pref_camera_picturesize_key");
    }

    protected void addCaredSettingChangedKeys(String str) {
        if (str != null && (!this.mCaredSettingChangedKeys.contains(str))) {
            this.mCaredSettingChangedKeys.add(str);
        }
    }

    protected IPreviewStream.PreviewStreamCallback getPreviewStreamCallback() {
        if (this.mPreviewStreamCallback == null) {
            this.mPreviewStreamCallback = new IPreviewStream.PreviewStreamCallback() { // from class: com.mediatek.camera.v2.mode.AbstractCameraMode.4
                @Override // com.mediatek.camera.v2.stream.IPreviewStream.PreviewStreamCallback
                public void onFirstFrameAvailable() {
                    AbstractCameraMode.this.mAppController.onPreviewStarted();
                }
            };
        }
        return this.mPreviewStreamCallback;
    }

    protected ICaptureStream.CaptureStreamCallback getCaptureStreamCallback() {
        if (this.mCaptureStreamCallback == null) {
            this.mCaptureStreamCallback = new ICaptureStream.CaptureStreamCallback() { // from class: com.mediatek.camera.v2.mode.AbstractCameraMode.5
                @Override // com.mediatek.camera.v2.stream.ICaptureStream.CaptureStreamCallback
                public void onCaptureCompleted(ImageInfo imageInfo) throws Throwable {
                    Assert.assertNotNull(imageInfo);
                    LogHelper.m26i(AbstractCameraMode.this.mTag, "onCaptureCompleted");
                    AbstractCameraMode.this.saveJpegInPath(imageInfo.getData(), "/sdcard/DCIM/Camera/test.jpeg");
                }
            };
        }
        return this.mCaptureStreamCallback;
    }

    protected IRecordStream.RecordStreamStatus getRecordStreamCallback() {
        return null;
    }

    protected int getCaptureFormat() {
        return 256;
    }

    protected Size getPreviewSize() {
        return this.mSettingServant.getPreviewSize();
    }

    public void onMediaEjected() {
    }

    protected Size getCaptureSize() {
        LogHelper.m26i(this.mTag, "[getCaptureSize]+");
        Size size = Utils.getSize(this.mSettingServant.getSettingValue("pref_camera_picturesize_key"));
        LogHelper.m26i(this.mTag, "[getCaptureSize]-");
        return size;
    }

    protected boolean changingModePreviewSize() {
        return false;
    }

    protected boolean changingModePictureSize() {
        return false;
    }

    protected boolean updatePreviewSize(IPreviewStream.PreviewSurfaceCallback previewSurfaceCallback) {
        LogHelper.m26i(this.mTag, "[updatePreviewSize]+");
        Size previewSize = getPreviewSize();
        if (previewSize == null) {
            LogHelper.m26i(this.mTag, "why preview size is nulll?");
            return false;
        }
        if (previewSurfaceCallback != null) {
            this.mPreviewController.setOneShotPreviewSurfaceCallback(previewSurfaceCallback);
        }
        boolean zUpdatePreviewSize = this.mPreviewController.updatePreviewSize(previewSize);
        if (changingModePreviewSize()) {
            zUpdatePreviewSize = true;
        }
        if (zUpdatePreviewSize) {
            this.mAppController.updatePreviewSize(previewSize.getWidth(), previewSize.getHeight());
        }
        LogHelper.m26i(this.mTag, "[updatePreviewSize]- previewSizeChanged:" + zUpdatePreviewSize + " preview size: " + previewSize.getWidth() + " x " + previewSize.getHeight());
        return zUpdatePreviewSize;
    }

    protected boolean updatePictureSize() {
        LogHelper.m26i(this.mTag, "[updatePictureSize]+");
        int captureFormat = getCaptureFormat();
        Size captureSize = getCaptureSize();
        if (captureSize == null) {
            LogHelper.m26i(this.mTag, "why picture size is nulll?");
            return false;
        }
        LogHelper.m26i(this.mTag, "[updatePictureSize]- pictureSize = " + captureSize.getWidth() + " x " + captureSize.getHeight() + " format = " + captureFormat);
        boolean zUpdateCaptureSize = this.mCaptureController.updateCaptureSize(captureSize, captureFormat);
        this.mCaptureController.setCaptureStreamCallback(getCaptureStreamCallback());
        if (changingModePictureSize()) {
            zUpdateCaptureSize = true;
        }
        if (zUpdateCaptureSize && (!"android.media.action.VIDEO_CAPTURE".equals(this.mIntent.getAction()))) {
            this.mAppUi.showLeftCounts(Utils.getImageSize(captureSize.getWidth() + "x" + captureSize.getHeight() + "-superfine"), true);
        }
        return zUpdateCaptureSize;
    }

    protected void pauseAudioPlayback() {
        LogHelper.m26i(this.mTag, "[pauseAudioPlayback]");
        AudioManager audioManager = (AudioManager) this.mActivity.getSystemService("audio");
        if (audioManager != null) {
            audioManager.requestAudioFocus(null, 3, 1);
        }
    }

    protected void releaseAudioFocus() {
        LogHelper.m26i(this.mTag, "[releaseAudioFocus]");
        AudioManager audioManager = (AudioManager) this.mActivity.getSystemService("audio");
        if (audioManager != null) {
            audioManager.abandonAudioFocus(null);
        }
    }

    private String getTagByModeId(int i) {
        switch (i) {
            case 0:
                return CaptureMode.class.getSimpleName();
            case 1:
            case 2:
            default:
                return null;
            case 3:
                return PipMode.class.getSimpleName();
        }
    }

    private void initializeStreamControllers(StreamManager streamManager) {
        this.mPreviewController = streamManager.getPreviewController(getModeId());
        this.mPreviewController.setPreviewStreamCallback(getPreviewStreamCallback());
        this.mCaptureController = streamManager.getCaptureController(getModeId());
        this.mRecordController = streamManager.getRecordController(getModeId());
        this.mRecordController.registerRecordingObserver(getRecordStreamCallback());
    }

    private void unInitializeStreamControllers() {
        if (this.mPreviewController != null) {
            this.mPreviewController.setPreviewStreamCallback(null);
        }
        if (this.mCaptureController != null) {
            this.mCaptureController.setCaptureStreamCallback(null);
        }
        if (this.mRecordController != null) {
            this.mRecordController.unregisterCaptureObserver(getRecordStreamCallback());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:38:0x0063 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Type inference failed for: r1v13 */
    /* JADX WARN: Type inference failed for: r1v17 */
    /* JADX WARN: Type inference failed for: r1v18 */
    /* JADX WARN: Type inference failed for: r1v3, types: [java.lang.String] */
    /* JADX WARN: Type inference failed for: r1v4 */
    /* JADX WARN: Type inference failed for: r1v6, types: [java.io.FileOutputStream] */
    /* JADX WARN: Type inference failed for: r1v9 */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public void saveJpegInPath(byte[] r5, java.lang.String r6) throws java.lang.Throwable {
        /*
            r4 = this;
            r2 = 0
            com.mediatek.camera.debug.LogHelper$Tag r0 = r4.mTag
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r3 = "[saveJpegInPath]+ path = "
            java.lang.StringBuilder r1 = r1.append(r3)
            java.lang.StringBuilder r1 = r1.append(r6)
            java.lang.String r1 = r1.toString()
            com.mediatek.camera.debug.LogHelper.m26i(r0, r1)
            java.io.FileOutputStream r1 = new java.io.FileOutputStream     // Catch: java.io.IOException -> L3d java.lang.Throwable -> L5f
            r1.<init>(r6)     // Catch: java.io.IOException -> L3d java.lang.Throwable -> L5f
            r1.write(r5)     // Catch: java.lang.Throwable -> L79 java.io.IOException -> L7b
            r1.close()     // Catch: java.lang.Throwable -> L79 java.io.IOException -> L7b
            if (r1 == 0) goto L2a
            r1.close()     // Catch: java.io.IOException -> L33
        L2a:
            com.mediatek.camera.debug.LogHelper$Tag r0 = r4.mTag
            java.lang.String r1 = "[saveJpegInPath]-"
            com.mediatek.camera.debug.LogHelper.m26i(r0, r1)
        L32:
            return
        L33:
            r0 = move-exception
            com.mediatek.camera.debug.LogHelper$Tag r1 = r4.mTag
            java.lang.String r2 = "[saveJpegInPath]ioexception:"
            com.mediatek.camera.debug.LogHelper.m25e(r1, r2, r0)
            goto L2a
        L3d:
            r0 = move-exception
            r1 = r2
        L3f:
            com.mediatek.camera.debug.LogHelper$Tag r2 = r4.mTag     // Catch: java.lang.Throwable -> L79
            java.lang.String r3 = "[saveJpegInPath]Failed to write image,exception:"
            com.mediatek.camera.debug.LogHelper.m25e(r2, r3, r0)     // Catch: java.lang.Throwable -> L79
            if (r1 == 0) goto L4c
            r1.close()     // Catch: java.io.IOException -> L55
        L4c:
            com.mediatek.camera.debug.LogHelper$Tag r0 = r4.mTag
            java.lang.String r1 = "[saveJpegInPath]-"
            com.mediatek.camera.debug.LogHelper.m26i(r0, r1)
            goto L32
        L55:
            r0 = move-exception
            com.mediatek.camera.debug.LogHelper$Tag r1 = r4.mTag
            java.lang.String r2 = "[saveJpegInPath]ioexception:"
            com.mediatek.camera.debug.LogHelper.m25e(r1, r2, r0)
            goto L4c
        L5f:
            r0 = move-exception
            r1 = r2
        L61:
            if (r1 == 0) goto L66
            r1.close()     // Catch: java.io.IOException -> L6f
        L66:
            com.mediatek.camera.debug.LogHelper$Tag r1 = r4.mTag
            java.lang.String r2 = "[saveJpegInPath]-"
            com.mediatek.camera.debug.LogHelper.m26i(r1, r2)
            throw r0
        L6f:
            r1 = move-exception
            com.mediatek.camera.debug.LogHelper$Tag r2 = r4.mTag
            java.lang.String r3 = "[saveJpegInPath]ioexception:"
            com.mediatek.camera.debug.LogHelper.m25e(r2, r3, r1)
            goto L66
        L79:
            r0 = move-exception
            goto L61
        L7b:
            r0 = move-exception
            goto L3f
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mediatek.camera.p005v2.mode.AbstractCameraMode.saveJpegInPath(byte[], java.lang.String):void");
    }
}
