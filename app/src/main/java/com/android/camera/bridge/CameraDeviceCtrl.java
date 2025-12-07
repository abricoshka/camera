package com.android.camera.bridge;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.os.ConditionVariable;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.android.camera.CameraActivity;
import com.android.camera.CameraDisabledException;
import com.android.camera.CameraHardwareException;
import com.android.camera.CameraHolder;
import com.android.camera.CameraManager;
import com.android.camera.ComboPreferences;
import com.android.camera.FocusManager;
import com.android.camera.Log;
import com.android.camera.ModeChecker;
import com.android.camera.SaveRequest;
import com.android.camera.Storage;
import com.android.camera.Util;
import com.android.camera.actor.CameraActor;
import com.android.camera.p001ui.FrameView;
import com.android.camera.p001ui.PreviewFrameLayout;
import com.android.camera.p001ui.PreviewSurfaceView;
import com.android.camera.p001ui.RotateLayout;
import com.mediatek.camera.ISettingCtrl;
import com.mediatek.camera.ModuleManager;
import com.mediatek.camera.R;
import com.mediatek.camera.platform.ICameraAppUi;
import com.mediatek.camera.setting.SettingUtils;
import com.mediatek.camera.util.CameraPerformanceTracker;
import com.mediatek.camera.util.ReflectUtil;
import java.lang.reflect.Method;
import junit.framework.Assert;

/* loaded from: classes.dex */
public class CameraDeviceCtrl implements SurfaceHolder.Callback {
    private static Method sCameraSetPropertyMethod = ReflectUtil.getMethod(Camera.class, "setProperty", String.class, String.class);
    private final CameraActivity mCameraActivity;
    private CameraActor mCameraActor;
    private CameraAppUiImpl mCameraAppUi;
    private CameraHandler mCameraHandler;
    private FrameLayout mCurSurfaceViewLayout;
    private RotateLayout mFocusAreaIndicator;
    private FocusManager mFocusManager;
    private ISettingCtrl mISettingCtrl;
    private boolean mIsFirstStartUp;
    private Camera.Size mLastPreviewSize;
    private FrameLayout mLastSurfaceViewLayout;
    private String mLastZsdMode;
    private final MainHandler mMainHandler;
    private ModuleManager mModuleManager;
    private final ComboPreferences mPreferences;
    private int mPreviewFrameHeight;
    private int mPreviewFrameWidth;
    private CamcorderProfile mProfile;
    private SurfaceTexture mSurfaceTexture;
    private PreviewSurfaceView mSurfaceView;
    private View mSurfaceViewCover;
    private SurfaceTexture mTopCamSurfaceTexture;
    private int mUnCropHeight;
    private int mUnCropWidth;
    private final String TAG = "CameraDeviceCtrl";
    private final ConditionVariable mWaitCameraStartUpThread = new ConditionVariable();
    private ICameraDeviceExt mDummyCameraDevice = new DummyCameraDevice();
    private ICameraDeviceExt mCurCameraDevice = this.mDummyCameraDevice;
    private ICameraDeviceExt mTopCameraDevice = this.mDummyCameraDevice;
    private ICameraDeviceExt mOldTopCameraDevice = this.mDummyCameraDevice;
    private final Object mCameraActorSync = new Object();
    private int mLastAudioBitRate = -1;
    private int mLastVideoBitRate = -1;
    private int mOrientation = 0;
    private boolean mIsSurfaceTextureReady = true;
    private boolean mIsNeedResetFocus = true;
    private boolean mIsOpenCameraFail = false;
    private boolean mIsFirstOpenCamera = true;
    private boolean mIsWaitForStartUpThread = false;
    private boolean mIsSwitchingPip = false;
    private boolean mIsSurfaceClear = true;
    private ConditionVariable mSycForLaunch = new ConditionVariable();
    private CameraState mCameraState = CameraState.STATE_CAMERA_CLOSED;
    private Camera.PreviewCallback mOneShotPreviewCallback = new Camera.PreviewCallback() { // from class: com.android.camera.bridge.CameraDeviceCtrl.1
        @Override // android.hardware.Camera.PreviewCallback
        public void onPreviewFrame(byte[] bArr, Camera camera) {
            CameraDeviceCtrl.this.detachSurfaceViewLayout();
            View viewFindViewById = CameraDeviceCtrl.this.mCameraActivity.findViewById(R.id.intentcover);
            if (viewFindViewById != null) {
                viewFindViewById.setVisibility(8);
            }
            CameraDeviceCtrl.this.hideRootCover();
        }
    };
    private CameraStartUpThread mCameraStartUpThread = new CameraStartUpThread();

    private enum CameraState {
        STATE_CAMERA_CLOSED,
        STATE_OPENING_CAMERA,
        STATE_CAMERA_OPENED;

        /* renamed from: values, reason: to resolve conflict with enum method */
        public static CameraState[] valuesCustom() {
            return values();
        }
    }

    public CameraDeviceCtrl(CameraActivity cameraActivity, ComboPreferences comboPreferences) {
        this.mIsFirstStartUp = false;
        this.mCameraActivity = cameraActivity;
        this.mPreferences = comboPreferences;
        this.mIsFirstStartUp = true;
        this.mMainHandler = new MainHandler(this.mCameraActivity.getMainLooper());
        this.mCameraStartUpThread.start();
        HandlerThread handlerThread = new HandlerThread("Camera Handler Thread");
        handlerThread.start();
        this.mCameraHandler = new CameraHandler(handlerThread.getLooper());
    }

    @Override // android.view.SurfaceHolder.Callback
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        CameraPerformanceTracker.onEvent("CameraDeviceCtrl", "SurfaceViewCreate", true);
        CameraPerformanceTracker.onEvent("CameraDeviceCtrl", "SurfaceViewCreate", false);
    }

    @Override // android.view.SurfaceHolder.Callback
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {
        if (this.mModuleManager.isDisplayUseSurfaceView()) {
            this.mModuleManager.notifySurfaceViewDisplayIsReady();
        }
        if (this.mIsFirstStartUp && surfaceHolder.isCreating() && this.mModuleManager.isDeviceUseSurfaceView()) {
            this.mCameraHandler.sendEmptyMessage(1);
        } else if (surfaceHolder.isCreating() && this.mModuleManager.isDeviceUseSurfaceView()) {
            this.mCurCameraDevice.setPreviewDisplayAsync(surfaceHolder);
        }
    }

    @Override // android.view.SurfaceHolder.Callback
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        notifySurfaceViewDestroy(surfaceHolder);
    }

    public void setModuleManager(ModuleManager moduleManager) {
        this.mModuleManager = moduleManager;
    }

    public void setSettingCtrl(ISettingCtrl iSettingCtrl) {
        this.mISettingCtrl = iSettingCtrl;
    }

    public void setCameraAppUi(CameraAppUiImpl cameraAppUiImpl) {
        this.mCameraAppUi = cameraAppUiImpl;
    }

    public void setCameraActor(CameraActor cameraActor) {
        synchronized (this.mCameraActorSync) {
            this.mCameraActor = cameraActor;
            this.mCameraActorSync.notifyAll();
        }
    }

    public void onPause() {
        showRootCover();
        if (this.mSurfaceView != null) {
            this.mSurfaceView.shrink();
        }
        this.mIsSurfaceClear = false;
        releaseSurfaceTexture();
        notifySurfaceViewDestroy(this.mSurfaceView.getHolder());
        if (this.mCameraActor.getMode() == 6 || this.mCameraActor.getMode() == 10) {
            closeCamera(false);
        } else {
            closeCamera(true);
        }
        this.mIsOpenCameraFail = false;
        Util.hideAlertDialog(this.mCameraActivity);
    }

    public void onResume() {
        if (this.mIsOpenCameraFail) {
            return;
        }
        if (this.mSurfaceView != null && (!this.mIsSurfaceClear)) {
            Log.m5d("CameraDeviceCtrl", "resume clear SurfaceView");
            this.mSurfaceView = null;
            this.mLastSurfaceViewLayout = this.mCurSurfaceViewLayout;
            detachSurfaceViewLayout();
            createSurfaceView();
        } else {
            setSurfaceViewVisible(0);
        }
        if (this.mSurfaceView != null) {
            this.mSurfaceView.expand();
        }
        Util.hideAlertDialog(this.mCameraActivity);
        openCamera();
    }

    public void onDestory() {
        setSurfaceViewVisible(8);
        this.mIsSurfaceClear = true;
        this.mCameraStartUpThread.terminate();
        if (this.mCameraHandler != null) {
            this.mCameraHandler.getLooper().quit();
        }
    }

    public void openCamera() {
        Log.m5d("CameraDeviceCtrl", "[openCamera] cameraState:" + getCameraState());
        if (getCameraState() != CameraState.STATE_CAMERA_CLOSED) {
            return;
        }
        this.mCameraStartUpThread.openCamera();
        setCameraState(CameraState.STATE_OPENING_CAMERA);
    }

    public void openCamera(int i) {
        Log.m5d("CameraDeviceCtrl", "[openCamera] cameraState:" + getCameraState() + ",cameraId:" + i);
        if (getCameraState() != CameraState.STATE_CAMERA_CLOSED) {
            return;
        }
        this.mCameraStartUpThread.setCameraId(i);
        this.mCameraStartUpThread.openCamera();
        setCameraState(CameraState.STATE_OPENING_CAMERA);
    }

    public boolean isCameraOpened() {
        if (getCameraState() != CameraState.STATE_CAMERA_OPENED) {
            return false;
        }
        return true;
    }

    public boolean isCameraIdle() {
        boolean zIsFocusCompleted = false;
        if (this.mCameraState == CameraState.STATE_CAMERA_OPENED && this.mFocusManager != null) {
            zIsFocusCompleted = this.mFocusManager.isFocusCompleted();
        }
        Log.m5d("CameraDeviceCtrl", "isCameraIdle() mCameraState=" + this.mCameraState + ", return " + zIsFocusCompleted);
        return zIsFocusCompleted;
    }

    public void switchCamera(int i) {
        Log.m5d("CameraDeviceCtrl", "switchCamera() cameraId=" + i + ", mIsOpenCameraFail:" + this.mIsOpenCameraFail);
        if (this.mIsOpenCameraFail) {
            return;
        }
        closeCamera(false);
        this.mCameraAppUi.collapseViewManager(true);
        clearFocusAndFace();
        this.mCameraAppUi.setCameraId(i);
        this.mCameraAppUi.changeBackToVFBModeStatues(false);
        this.mPreferences.setLocalId(this.mCameraActivity, i);
        SettingUtils.upgradeLocalPreferences(this.mPreferences.getLocal());
        SettingUtils.writePreferredCameraId(this.mPreferences, i);
        unInitializeFocusManager();
        openCamera(i);
    }

    public void openStereoCamera(int i, boolean z) {
        Log.m5d("CameraDeviceCtrl", "openStereoCamera() cameraId=" + i + ", mIsOpenCameraFail:" + this.mIsOpenCameraFail);
        if (i != -1) {
            this.mCameraStartUpThread.setCameraId(i);
            this.mPreferences.setLocalId(this.mCameraActivity, i);
            SettingUtils.upgradeLocalPreferences(this.mPreferences.getLocal());
        }
        this.mCameraStartUpThread.openCamera();
        setCameraState(CameraState.STATE_OPENING_CAMERA);
        if (z) {
            this.mWaitCameraStartUpThread.close();
            this.mCameraStartUpThread.resumeThread();
            this.mWaitCameraStartUpThread.block();
        }
    }

    public void closeCamera(boolean z) {
        Log.m5d("CameraDeviceCtrl", "[closeCamera] cameraState:" + getCameraState() + ", isExitAp:" + z);
        if (getCameraState() == CameraState.STATE_CAMERA_CLOSED) {
            return;
        }
        this.mCameraAppUi.setViewState(ICameraAppUi.ViewState.VIEW_STATE_CAMERA_CLOSED);
        waitCameraStartUpThread(true);
        if (this.mIsOpenCameraFail) {
            Log.m5d("CameraDeviceCtrl", "[closeCamera] mIsOpenCameraFail:" + this.mIsOpenCameraFail);
            return;
        }
        removeAllMessage();
        this.mCameraActor.onCameraClose();
        clearDeviceCallbacks();
        detachSurfaceViewLayout();
        CameraHolder.instance().release(z);
        if (this.mFocusManager != null) {
            this.mFocusManager.onCameraReleased();
        }
        this.mOldTopCameraDevice = this.mTopCameraDevice;
        this.mCurCameraDevice = this.mDummyCameraDevice;
        this.mTopCameraDevice = this.mDummyCameraDevice;
        this.mModuleManager.onCameraCloseDone();
        setCameraState(CameraState.STATE_CAMERA_CLOSED);
    }

    public CameraHolder getCameraHolder() {
        return CameraHolder.instance();
    }

    public void onModeChanged(boolean z) {
        Log.m5d("CameraDeviceCtrl", "[onModeChanged] isNeedRestart:" + z + ",camera is opened : " + isCameraOpened());
        if (isCameraOpened()) {
            this.mCameraAppUi.clearViewCallbacks();
            this.mCameraAppUi.applayViewCallbacks();
            this.mCameraAppUi.updateManager();
            unInitializeFocusManager();
            initializeFocusManager();
            applyDeviceCallbacks();
            applyParameters(z);
        }
    }

    public void onOrientationChanged(int i) {
        this.mOrientation = i;
        this.mCurCameraDevice.setJpegRotation(this.mOrientation);
        this.mCurCameraDevice.applyParametersToServer();
        this.mTopCameraDevice.setJpegRotation(this.mOrientation);
        this.mTopCameraDevice.applyParametersToServer();
    }

    public void applyParameters(boolean z) {
        boolean z2 = false;
        boolean zIsPreviewSizeChanged = isPreviewSizeChanged(this.mCurCameraDevice.getPreviewSize());
        boolean zIsPreviewRatioChanged = isPreviewRatioChanged(this.mCurCameraDevice.getPreviewSize());
        boolean zIsZsdChanged = isZsdChanged(this.mCurCameraDevice.getZsdMode());
        boolean zIsPictureSizeChanged = this.mCurCameraDevice.isPictureSizeChanged();
        boolean zIsHdrChanged = this.mCurCameraDevice.isHdrChanged();
        boolean z3 = zIsPreviewSizeChanged ? !this.mIsSwitchingPip : false;
        if (zIsZsdChanged || z3 || z) {
            zIsHdrChanged = true;
        }
        if (zIsHdrChanged) {
            this.mCameraActor.stopPreview();
            if (getCameraState() != CameraState.STATE_OPENING_CAMERA) {
                prepareSurfaceView(zIsPreviewRatioChanged);
            }
        }
        if (getCameraState() == CameraState.STATE_OPENING_CAMERA) {
            if (zIsPreviewRatioChanged && (!this.mIsFirstOpenCamera) && (!this.mIsWaitForStartUpThread)) {
                this.mMainHandler.sendMessageDelayed(this.mMainHandler.obtainMessage(18, true), 5L);
                this.mCameraStartUpThread.pauseThread();
            }
            this.mIsFirstOpenCamera = false;
            this.mMainHandler.sendEmptyMessage(17);
        } else {
            setPreviewFrameLayoutAspectRatio();
        }
        if (getCameraState() == CameraState.STATE_OPENING_CAMERA && this.mCameraStartUpThread.isCancel()) {
            return;
        }
        prepareParameter(zIsHdrChanged, zIsPreviewRatioChanged, zIsPreviewSizeChanged);
        this.mMainHandler.sendEmptyMessageDelayed(14, getDelayTime());
        if (getCameraState() == CameraState.STATE_OPENING_CAMERA && this.mCameraStartUpThread.isCancel()) {
            return;
        }
        String dngState = this.mCurCameraDevice.getDngState();
        String settingValue = this.mISettingCtrl.getSettingValue("pref_dng_key");
        boolean z4 = settingValue != null;
        boolean zIsVideoBitRateChanged = isVideoBitRateChanged();
        boolean zIsAudioBitRateChanged = isAudioBitRateChanged();
        if (dngState != null && z4) {
            z2 = !dngState.equals(settingValue);
        }
        if (zIsPictureSizeChanged || zIsPreviewSizeChanged || z2 || zIsVideoBitRateChanged || zIsAudioBitRateChanged || this.mCameraActivity.isVideoMode()) {
            this.mCameraAppUi.setDngState(this.mISettingCtrl.getSettingValue("pref_dng_key"));
            this.mCameraAppUi.showRemainingAways();
        }
        this.mCurCameraDevice.updateDngState(this.mISettingCtrl.getSettingValue("pref_dng_key"));
        this.mCurCameraDevice.updateParameters();
        this.mTopCameraDevice.updateParameters();
        this.mLastPreviewSize = this.mCurCameraDevice.getPreviewSize();
        this.mLastZsdMode = this.mCurCameraDevice.getZsdMode();
    }

    private void prepareParameter1(boolean z, boolean z2, boolean z3) {
        boolean zIsDeviceUseSurfaceView = this.mModuleManager.isDeviceUseSurfaceView();
        boolean zIsDisplayUseSurfaceView = this.mModuleManager.isDisplayUseSurfaceView();
        boolean zIsNeedDualCamera = this.mModuleManager.isNeedDualCamera();
        this.mCameraActivity.getCurrentMode();
        if (z) {
            switchPreview(zIsDeviceUseSurfaceView, zIsDisplayUseSurfaceView, zIsNeedDualCamera);
        }
        if (this.mCameraActor.getMode() == 0) {
            this.mCurCameraDevice.setPhotoModeBasicParameters();
        }
        this.mCurCameraDevice.setDisplayOrientation(zIsDeviceUseSurfaceView);
        this.mTopCameraDevice.setDisplayOrientation(zIsDeviceUseSurfaceView);
        this.mCurCameraDevice.setJpegRotation(this.mOrientation);
        this.mCameraAppUi.setZoomParameter();
        this.mCurCameraDevice.setPreviewFormat(842094169);
        if (this.mCurCameraDevice.isSceneModeChanged()) {
            this.mCurCameraDevice.applyParametersToServer();
        }
        this.mCurCameraDevice.applyParametersToServer();
        if (z3) {
            this.mCurCameraDevice.fetchParametersFromServer();
        }
        this.mTopCameraDevice.setJpegRotation(this.mOrientation);
        this.mTopCameraDevice.applyParametersToServer();
        if (z3) {
            this.mTopCameraDevice.fetchParametersFromServer();
        }
        this.mCameraActor.onCameraParameterReady(z);
        this.mCurCameraDevice.setOneShotPreviewCallback(this.mOneShotPreviewCallback);
    }

    public void applyParameters1(boolean z) {
        boolean z2 = false;
        boolean zIsPreviewSizeChanged = isPreviewSizeChanged(this.mCurCameraDevice.getPreviewSize());
        boolean zIsPreviewRatioChanged = isPreviewRatioChanged(this.mCurCameraDevice.getPreviewSize());
        boolean zIsZsdChanged = isZsdChanged(this.mCurCameraDevice.getZsdMode());
        boolean zIsPictureSizeChanged = this.mCurCameraDevice.isPictureSizeChanged();
        boolean zIsHdrChanged = this.mCurCameraDevice.isHdrChanged();
        boolean z3 = zIsPreviewSizeChanged ? !this.mIsSwitchingPip : false;
        if (zIsZsdChanged || z3 || z) {
            zIsHdrChanged = true;
        }
        if (zIsHdrChanged) {
            this.mCameraActor.stopPreview();
            if (getCameraState() != CameraState.STATE_OPENING_CAMERA) {
                prepareSurfaceView(zIsPreviewRatioChanged);
            }
        }
        if (getCameraState() == CameraState.STATE_OPENING_CAMERA) {
            if (zIsPreviewRatioChanged && (!this.mIsFirstOpenCamera) && (!this.mIsWaitForStartUpThread)) {
                this.mMainHandler.sendMessageDelayed(this.mMainHandler.obtainMessage(18, true), 5L);
                this.mCameraStartUpThread.pauseThread();
            }
            this.mIsFirstOpenCamera = false;
            this.mMainHandler.sendEmptyMessage(17);
        } else {
            setPreviewFrameLayoutAspectRatio();
        }
        if (getCameraState() == CameraState.STATE_OPENING_CAMERA && this.mCameraStartUpThread.isCancel()) {
            return;
        }
        prepareParameter1(zIsHdrChanged, zIsPreviewRatioChanged, zIsPreviewSizeChanged);
        this.mMainHandler.sendEmptyMessageDelayed(14, getDelayTime());
        if (getCameraState() == CameraState.STATE_OPENING_CAMERA && this.mCameraStartUpThread.isCancel()) {
            return;
        }
        String dngState = this.mCurCameraDevice.getDngState();
        String settingValue = this.mISettingCtrl.getSettingValue("pref_dng_key");
        boolean z4 = settingValue != null;
        boolean zIsVideoBitRateChanged = isVideoBitRateChanged();
        boolean zIsAudioBitRateChanged = isAudioBitRateChanged();
        if (dngState != null && z4) {
            z2 = !dngState.equals(settingValue);
        }
        if (zIsPictureSizeChanged || zIsPreviewSizeChanged || z2 || zIsVideoBitRateChanged || zIsAudioBitRateChanged || this.mCameraActivity.isVideoMode()) {
            this.mCameraAppUi.setDngState(this.mISettingCtrl.getSettingValue("pref_dng_key"));
            this.mCameraAppUi.showRemainingAways();
        }
        this.mCurCameraDevice.updateDngState(this.mISettingCtrl.getSettingValue("pref_dng_key"));
        this.mCurCameraDevice.updateParameters();
        this.mTopCameraDevice.updateParameters();
        this.mLastPreviewSize = this.mCurCameraDevice.getPreviewSize();
        this.mLastZsdMode = this.mCurCameraDevice.getZsdMode();
    }

    public void applyParameters() {
        this.mCameraActor.onCameraParameterReady(false);
    }

    public void applyParameterForFocus(boolean z) {
        applyFocusCapabilities(z);
        this.mCurCameraDevice.applyParametersToServer();
        if (this.mTopCameraDevice.isSupportFocusMode("continuous-picture")) {
            this.mTopCameraDevice.setFocusMode("continuous-picture");
        }
        this.mTopCameraDevice.applyParametersToServer();
    }

    public void applyParameterForCapture(SaveRequest saveRequest) {
        this.mCurCameraDevice.setJpegRotation(this.mOrientation);
        saveRequest.setJpegRotation(this.mCurCameraDevice.getJpegRotation());
        this.mCurCameraDevice.setGpsParameters(this.mCameraActivity.getLocationManager().getCurrentLocation());
        this.mCurCameraDevice.setCapturePath(saveRequest.getTempFilePath());
        this.mCurCameraDevice.applyParametersToServer();
        this.mTopCameraDevice.setJpegRotation(this.mOrientation);
        this.mTopCameraDevice.applyParametersToServer();
    }

    public void startAsyncZoom(int i) {
        if (this.mCurCameraDevice.isZoomSupported() && this.mCurCameraDevice.getZoom() != i) {
            this.mCurCameraDevice.setZoom(i);
        }
    }

    public void attachSurfaceViewLayout() {
        if (this.mSurfaceView == null) {
            createSurfaceView();
            this.mSurfaceView.setVisibility(0);
        }
    }

    public void detachSurfaceViewLayout() {
        if (this.mLastSurfaceViewLayout != null) {
            ((FrameLayout) this.mCameraActivity.findViewById(R.id.camera_surfaceview_root)).removeViewInLayout(this.mLastSurfaceViewLayout);
            this.mLastSurfaceViewLayout.setVisibility(8);
            this.mLastSurfaceViewLayout = null;
        }
    }

    public void setPreviewFull() {
        applyParameters1(false);
    }

    public void setPreviewFrameLayoutAspectRatio() {
        if (this.mCurCameraDevice != null && this.mCameraActivity.getCurrentWheelMode() != 3) {
            Log.m5d("CameraDeviceCtrl", "[setPreviewFrameLayoutAspectRatio] mCameraActivity.getCurrentMode()=" + this.mCameraActivity.getCurrentMode());
            if (!Util.isFullPreviewMode() && (!"1.7778".equals(this.mISettingCtrl.getSettingValue("pref_camera_picturesize_ratio_key")))) {
                this.mCurCameraDevice.setPreviewSize();
            } else {
                this.mCurCameraDevice.setPreviewSizeFull();
            }
        }
        PreviewFrameLayout previewFrameLayout = (PreviewFrameLayout) this.mCameraActivity.findViewById(R.id.frame);
        if (previewFrameLayout != null && this.mCurCameraDevice != null) {
            Camera.Size previewSize = this.mCurCameraDevice.getPreviewSize();
            if (previewSize == null) {
                return;
            }
            int i = previewSize.width;
            int i2 = previewSize.height;
            if (this.mSurfaceView != null && !this.mSurfaceView.setAspectRatio(i / i2)) {
                this.mModuleManager.notifySurfaceViewDisplayIsReady();
            }
            previewFrameLayout.setAspectRatio(i / i2);
        }
        if (this.mCurSurfaceViewLayout != null) {
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(-2, -2);
            if (!Util.isFullPreviewMode() && (!"1.7778".equals(this.mISettingCtrl.getSettingValue("pref_camera_picturesize_ratio_key")))) {
                layoutParams.setMargins(0, this.mCameraActivity.getResources().getDimensionPixelOffset(R.dimen.mode_picker_height), 0, 0);
            } else {
                layoutParams.setMargins(0, 0, 0, 0);
            }
        }
    }

    public Camera.Parameters getParameters() {
        return this.mCurCameraDevice.getParameters();
    }

    public ParametersExt getParametersExt() {
        return this.mCurCameraDevice.getParametersExt();
    }

    public int getCameraId() {
        int cameraId = this.mCurCameraDevice.getCameraId();
        return cameraId == this.mDummyCameraDevice.getCameraId() ? this.mCameraStartUpThread.getCameraId() : cameraId;
    }

    public CameraManager.CameraProxy getCameraDevice() {
        return this.mCurCameraDevice.getCameraDevice();
    }

    public ICameraDeviceExt getCurCameraDevice() {
        return this.mCurCameraDevice;
    }

    public ICameraDeviceExt getTopCameraDevice() {
        return this.mTopCameraDevice;
    }

    public FocusManager getFocusManager() {
        return this.mFocusManager;
    }

    public PreviewSurfaceView getSurfaceView() {
        return this.mSurfaceView;
    }

    public boolean isFirstStartUp() {
        return this.mIsFirstStartUp;
    }

    public int getDisplayOrientation() {
        return this.mCurCameraDevice.getDisplayOrientation();
    }

    public Camera.Parameters getTopParameters() {
        return this.mTopCameraDevice.getParameters();
    }

    public void onSizeChanged(int i, int i2) {
        if (this.mCurCameraDevice != null && this.mCurCameraDevice.getPreviewSize() != null) {
            Camera.Size previewSize = this.mCurCameraDevice.getPreviewSize();
            double d = previewSize.width / previewSize.height;
            if (i > i2) {
                this.mUnCropWidth = Math.max(i, (int) (i2 * d));
                this.mUnCropHeight = Math.max(i2, (int) (i / d));
            } else {
                this.mUnCropWidth = Math.max(i, (int) (i2 / d));
                this.mUnCropHeight = Math.max(i2, (int) (d * i));
            }
        } else {
            this.mUnCropWidth = i;
            this.mUnCropHeight = i2;
        }
        if (this.mFocusManager != null) {
            this.mFocusManager.setPreviewSize(this.mUnCropWidth, this.mUnCropHeight);
            this.mFocusManager.setCropPreviewSize(i, i2);
        }
        this.mPreviewFrameWidth = i;
        this.mPreviewFrameHeight = i2;
        if (this.mModuleManager != null) {
            this.mModuleManager.onPreviewDisplaySizeChanged(i, i2);
        }
    }

    public void setDisplayOrientation() {
        this.mCurCameraDevice.setDisplayOrientation(this.mModuleManager.isDeviceUseSurfaceView());
        int displayOrientation = this.mCurCameraDevice.getDisplayOrientation();
        FrameView frameView = this.mCameraActivity.getFrameView();
        if (frameView != null) {
            frameView.setDisplayOrientation(displayOrientation);
        }
        if (this.mFocusManager != null) {
            this.mFocusManager.setDisplayOrientation(displayOrientation);
        }
        this.mModuleManager.setDisplayRotation(Util.getDisplayRotation(this.mCameraActivity));
    }

    public int getPreviewFrameHeight() {
        return this.mPreviewFrameHeight;
    }

    public int getPreviewFrameWidth() {
        return this.mPreviewFrameWidth;
    }

    public int getUnCropWidth() {
        return this.mUnCropWidth;
    }

    public int getUnCropHeight() {
        return this.mUnCropHeight;
    }

    public boolean isOpenCameraFail() {
        return this.mIsOpenCameraFail;
    }

    public void hideRootCover() {
        this.mSurfaceViewCover = this.mCameraActivity.findViewById(R.id.camera_cover);
        if (this.mSurfaceViewCover != null && this.mSurfaceViewCover.getVisibility() != 4) {
            this.mSurfaceViewCover.setVisibility(4);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getPreferredCameraId(ComboPreferences comboPreferences) {
        int cameraFacingIntentExtras = Util.getCameraFacingIntentExtras(this.mCameraActivity);
        if (cameraFacingIntentExtras != -1) {
            return cameraFacingIntentExtras;
        }
        return SettingUtils.readPreferredCameraId(comboPreferences);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void initializeFocusManager() {
        CameraPerformanceTracker.onEvent("CameraDeviceCtrl", "InitFocusManager", true);
        this.mFocusAreaIndicator = (RotateLayout) this.mCameraActivity.findViewById(R.id.focus_indicator_rotate_layout);
        this.mFocusManager = new FocusManager(this.mCameraActivity, this.mPreferences, this.mFocusAreaIndicator, this.mCurCameraDevice.getInitialParams(), this.mCameraActor.getFocusManagerListener(), CameraHolder.instance().getCameraInfo()[this.mCurCameraDevice.getCameraId()].facing == 1, this.mCameraActivity.getMainLooper(), this.mCameraActor.getMode());
        this.mFocusManager.setPreviewSize(this.mUnCropWidth, this.mUnCropHeight);
        this.mFocusManager.setCropPreviewSize(this.mPreviewFrameWidth, this.mPreviewFrameHeight);
        this.mFocusManager.setDisplayOrientation(getDisplayOrientation());
        applyFocusCapabilities(true);
        this.mModuleManager.setFocusManager(new FocusManagerImpl(this.mFocusManager));
        CameraPerformanceTracker.onEvent("CameraDeviceCtrl", "InitFocusManager", false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void initializeSettingController() {
        if (!this.mISettingCtrl.isSettingsInitialized()) {
            this.mISettingCtrl.initializeSettings(R.xml.camera_preferences, this.mPreferences.getGlobal(), this.mPreferences.getLocal());
        }
        this.mISettingCtrl.updateSetting(this.mPreferences.getLocal());
        if (this.mCameraActor.getMode() == 9 && this.mISettingCtrl.getSetting("pref_hdr_key") != null) {
            this.mISettingCtrl.onSettingChanged("pref_hdr_key", "off");
        }
        this.mMainHandler.sendEmptyMessage(12);
    }

    private boolean isPreviewSizeChanged(Camera.Size size) {
        Assert.assertNotNull(size);
        if (this.mLastPreviewSize == null || !(!this.mLastPreviewSize.equals(size))) {
            return false;
        }
        return true;
    }

    private boolean isPreviewRatioChanged(Camera.Size size) {
        Assert.assertNotNull(size);
        if (this.mLastPreviewSize != null) {
            return !(((double) size.width) / ((double) size.height) == ((double) this.mLastPreviewSize.width) / ((double) this.mLastPreviewSize.height));
        }
        return false;
    }

    private boolean isZsdChanged(String str) {
        if (str == null) {
            return this.mLastZsdMode != null;
        }
        return !str.equals(this.mLastZsdMode);
    }

    private boolean isVideoBitRateChanged() {
        boolean z = true;
        if (this.mProfile == null) {
            return false;
        }
        if (this.mLastVideoBitRate != -1 && this.mLastVideoBitRate == this.mProfile.videoBitRate) {
            z = false;
        }
        this.mLastVideoBitRate = this.mProfile.videoBitRate;
        return z;
    }

    private boolean isAudioBitRateChanged() {
        boolean z = true;
        if (this.mProfile == null) {
            return false;
        }
        if (this.mLastAudioBitRate != -1 && this.mLastAudioBitRate == this.mProfile.audioBitRate) {
            z = false;
        }
        this.mLastAudioBitRate = this.mProfile.audioBitRate;
        return z;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void prepareSurfaceView(boolean z) {
        if (z && this.mSurfaceView != null && this.mSurfaceView.getVisibility() == 0) {
            this.mSurfaceView = null;
            detachSurfaceViewLayout();
            attachSurfaceViewLayout();
        }
    }

    private void prepareParameter(boolean z, boolean z2, boolean z3) {
        boolean zIsDeviceUseSurfaceView = this.mModuleManager.isDeviceUseSurfaceView();
        boolean zIsDisplayUseSurfaceView = this.mModuleManager.isDisplayUseSurfaceView();
        boolean zIsNeedDualCamera = this.mModuleManager.isNeedDualCamera();
        this.mCameraActivity.getCurrentMode();
        if (z) {
            switchPreview(zIsDeviceUseSurfaceView, zIsDisplayUseSurfaceView, zIsNeedDualCamera);
        }
        if (this.mCameraActor.getMode() == 0) {
            this.mCurCameraDevice.setPhotoModeBasicParameters();
        }
        this.mCurCameraDevice.setDisplayOrientation(zIsDeviceUseSurfaceView);
        this.mTopCameraDevice.setDisplayOrientation(zIsDeviceUseSurfaceView);
        this.mCurCameraDevice.setJpegRotation(this.mOrientation);
        this.mCameraAppUi.setZoomParameter();
        this.mCurCameraDevice.setPreviewFormat(842094169);
        if (this.mCurCameraDevice.isSceneModeChanged()) {
            this.mCurCameraDevice.applyParametersToServer();
        }
        this.mCurCameraDevice.applyParametersToServer();
        if (z3) {
            this.mCurCameraDevice.fetchParametersFromServer();
        }
        this.mTopCameraDevice.setJpegRotation(this.mOrientation);
        this.mTopCameraDevice.applyParametersToServer();
        if (z3) {
            this.mTopCameraDevice.fetchParametersFromServer();
        }
        this.mCameraActor.onCameraParameterReady(z);
        this.mCurCameraDevice.setOneShotPreviewCallback(this.mOneShotPreviewCallback);
        this.mMainHandler.sendEmptyMessage(13);
    }

    private void turnOnWhenShown() {
        if (this.mCurCameraDevice != this.mDummyCameraDevice && this.mFocusManager != null) {
            if (this.mIsNeedResetFocus) {
                this.mFocusManager.overrideFocusMode(null);
                this.mCurCameraDevice.setFocusMode(this.mFocusManager.getFocusMode());
            }
            String settingValue = this.mISettingCtrl.getSettingValue("pref_camera_flashmode_key");
            if (settingValue != null) {
                this.mCurCameraDevice.getParametersExt().setFlashMode(settingValue);
            }
        }
    }

    private void switchCameraPreview() {
        CameraPerformanceTracker.onEvent("CameraDeviceCtrl", "setPreviewDisp", true);
        setSurfaceViewStatusOnUiThread(0);
        this.mCurCameraDevice.setPreviewDisplayAsync(this.mSurfaceView.getHolder());
        CameraPerformanceTracker.onEvent("CameraDeviceCtrl", "setPreviewDisp", false);
    }

    private void setSurfaceViewStatusOnUiThread(final int i) {
        this.mCameraActivity.runOnUiThread(new Runnable() { // from class: com.android.camera.bridge.CameraDeviceCtrl.2
            @Override // java.lang.Runnable
            public void run() {
                CameraDeviceCtrl.this.setSurfaceViewVisible(i);
            }
        });
    }

    private void switchPreview(boolean z, boolean z2, boolean z3) {
        updatePreviewBufferSize(this.mCurCameraDevice.getPreviewSize());
        if (z) {
            setSurfaceViewStatusOnUiThread(0);
            this.mCurCameraDevice.setPreviewDisplayAsync(this.mSurfaceView.getHolder());
            return;
        }
        getSurfaceTexture(z2, z3);
        if (!z2) {
            setSurfaceViewStatusOnUiThread(4);
        } else {
            setSurfaceViewStatusOnUiThread(0);
            setPreviewTextureAsync();
        }
    }

    private void updatePreviewBufferSize(Camera.Size size) {
        android.util.Log.d("zbx", "updatePreviewBufferSize: size=" + size.width + "x" + size.height);
        int cameraDisplayOrientation = this.mCurCameraDevice.getCameraDisplayOrientation();
        if (size != null) {
            int i = size.width;
            int i2 = size.height;
            if (cameraDisplayOrientation % 180 == 0) {
                i2 = i;
                i = i2;
            }
            if (this.mModuleManager != null) {
                this.mModuleManager.onPreviewBufferSizeChanged(i2, i);
            }
        }
    }

    private void getSurfaceTexture(boolean z, boolean z2) {
        if (z) {
            this.mSurfaceTexture = this.mModuleManager.getBottomSurfaceTexture();
        }
        this.mTopCamSurfaceTexture = z2 ? this.mModuleManager.getTopSurfaceTexture() : null;
    }

    private void setPreviewTextureAsync() {
        if (this.mSurfaceTexture != null && this.mIsSurfaceTextureReady) {
            this.mCurCameraDevice.setPreviewTextureAsync(this.mSurfaceTexture);
            this.mCameraActor.setSurfaceTextureReady(true);
        }
        if (this.mTopCamSurfaceTexture != null && this.mIsSurfaceTextureReady) {
            this.mTopCameraDevice.setPreviewTextureAsync(this.mTopCamSurfaceTexture);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setSurfaceViewVisible(int i) {
        if (this.mSurfaceView == null || this.mSurfaceView.getVisibility() == i) {
            return;
        }
        this.mSurfaceView.setVisibility(i);
    }

    private void createSurfaceView() {
        FrameLayout frameLayout = (FrameLayout) this.mCameraActivity.findViewById(R.id.camera_surfaceview_root);
        this.mLastSurfaceViewLayout = this.mCurSurfaceViewLayout;
        this.mCurSurfaceViewLayout = (FrameLayout) this.mCameraActivity.getLayoutInflater().inflate(R.layout.camera_preview_layout, (ViewGroup) null);
        this.mSurfaceView = (PreviewSurfaceView) this.mCurSurfaceViewLayout.findViewById(R.id.camera_preview);
        this.mSurfaceView.setOnTouchListener(new View.OnTouchListener() { // from class: com.android.camera.bridge.CameraDeviceCtrl.3
            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View view, MotionEvent motionEvent) {
                CameraDeviceCtrl.this.mCameraActivity.getGestureRecognizer().onTouchEvent(motionEvent);
                return true;
            }
        });
        this.mSurfaceView.getHolder().addCallback(this);
        frameLayout.addView(this.mCurSurfaceViewLayout);
    }

    public void unInitializeFocusManager() {
        if (this.mFocusManager != null) {
            this.mFocusManager.removeMessages();
            this.mCameraActivity.runOnUiThread(new Runnable() { // from class: com.android.camera.bridge.CameraDeviceCtrl.4
                @Override // java.lang.Runnable
                public void run() {
                    CameraDeviceCtrl.this.mFocusManager.clearFocusAndFaceUi();
                }
            });
            this.mFocusManager.release();
        }
    }

    private class MainHandler extends Handler {
        MainHandler(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            Log.m5d("CameraDeviceCtrl", "[MainHandler.handleMessage] msg:" + message.what);
            switch (message.what) {
                case 9:
                    CameraDeviceCtrl.this.mCameraActor.onCameraOpenFailed();
                    Util.showErrorAndFinish(CameraDeviceCtrl.this.mCameraActivity, R.string.cannot_connect_camera_new);
                    CameraDeviceCtrl.this.mCameraActivity.onCameraOpenFailed();
                    break;
                case 10:
                    CameraDeviceCtrl.this.mCameraActor.onCameraDisabled();
                    Util.showErrorAndFinish(CameraDeviceCtrl.this.mCameraActivity, R.string.camera_disabled);
                    CameraDeviceCtrl.this.mCameraActivity.onCameraOpenFailed();
                    break;
                case 12:
                    CameraDeviceCtrl.this.mCameraActivity.onCameraPreferenceReady();
                    break;
                case 13:
                    CameraDeviceCtrl.this.mCameraActivity.onCameraParametersReady();
                    break;
                case 14:
                    CameraDeviceCtrl.this.hideRootCover();
                    break;
                case 16:
                    CameraDeviceCtrl.this.mCameraAppUi.refreshModeRelated();
                    CameraDeviceCtrl.this.mCameraAppUi.setViewState(ICameraAppUi.ViewState.VIEW_STATE_CAMERA_OPENED);
                    CameraDeviceCtrl.this.mCameraActivity.onCameraOpenDone();
                    break;
                case 17:
                    CameraDeviceCtrl.this.setPreviewFrameLayoutAspectRatio();
                    break;
                case 18:
                    CameraDeviceCtrl.this.prepareSurfaceView(((Boolean) message.obj).booleanValue());
                    CameraDeviceCtrl.this.mCameraStartUpThread.resumeThread();
                    break;
                case 19:
                    CameraDeviceCtrl.this.hideRootCover();
                    break;
            }
        }
    }

    private void showRootCover() {
        if (this.mSurfaceViewCover != null && this.mSurfaceViewCover.getVisibility() != 0) {
            this.mSurfaceViewCover.setVisibility(0);
        }
    }

    private void applyFocusCapabilities(boolean z) {
        FocusManager focusManager = this.mFocusManager;
        if (focusManager.getAeLockSupported()) {
            this.mCurCameraDevice.setAutoExposureLock(focusManager.getAeLock());
        }
        if (focusManager.getAwbLockSupported()) {
            this.mCurCameraDevice.setAutoWhiteBalanceLock(focusManager.getAwbLock());
        }
        if (focusManager.getFocusAreaSupported() && z) {
            this.mCurCameraDevice.setFocusAreas(focusManager.getFocusAreas());
        }
        if (focusManager.getMeteringAreaSupported() && z) {
            this.mCurCameraDevice.setMeteringAreas(focusManager.getMeteringAreas());
        }
        this.mCameraActor.handleFocus();
        this.mCurCameraDevice.setFocusMode(focusManager.getFocusMode());
    }

    public void waitCameraStartUpThread(boolean z) {
        if (getCameraState() != CameraState.STATE_OPENING_CAMERA) {
            return;
        }
        this.mIsWaitForStartUpThread = true;
        this.mWaitCameraStartUpThread.close();
        this.mCameraStartUpThread.resumeThread();
        if (z) {
            this.mSycForLaunch.open();
            this.mCameraStartUpThread.cancel();
        }
        if (this.mIsWaitForStartUpThread) {
            this.mWaitCameraStartUpThread.block(3000L);
        }
        Log.m5d("CameraDeviceCtrl", "waitCameraStartUpThread() end");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void cameraStartUpThreadDone() {
        this.mIsWaitForStartUpThread = false;
        this.mWaitCameraStartUpThread.open();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void applyDeviceCallbacks() {
        this.mCurCameraDevice.setErrorCallback(this.mCameraActor.getErrorCallback());
        this.mCurCameraDevice.setFaceDetectionListener(this.mCameraActor.getFaceDetectionListener());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void clearDeviceCallbacks() {
        this.mCurCameraDevice.setErrorCallback(null);
        this.mCurCameraDevice.setFaceDetectionListener(null);
    }

    private void removeAllMessage() {
        this.mMainHandler.removeMessages(12);
        this.mMainHandler.removeMessages(14);
        this.mMainHandler.removeMessages(16);
        this.mMainHandler.removeMessages(17);
        this.mMainHandler.removeMessages(13);
        this.mMainHandler.removeMessages(18);
    }

    private int getDelayTime() {
        int i = 0;
        if ("on".equals(this.mISettingCtrl.getSetting("pref_video_eis_key"))) {
            i = 20;
        }
        if ((2 == this.mCameraActivity.getPrevMode() || "on".equals(this.mISettingCtrl.getSettingValue("pref_hdr_key"))) && 8 == this.mCameraActivity.getCurrentMode()) {
            return 30;
        }
        return i;
    }

    private class CameraHandler extends Handler {
        CameraHandler(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            switch (message.what) {
                case 1:
                    CameraPerformanceTracker.onEvent("CameraDeviceCtrl", "setPreviewDisp", true);
                    CameraDeviceCtrl.this.mCurCameraDevice.setPreviewDisplayAsync(CameraDeviceCtrl.this.mSurfaceView.getHolder());
                    CameraPerformanceTracker.onEvent("CameraDeviceCtrl", "setPreviewDisp", false);
                    CameraDeviceCtrl.this.mSycForLaunch.open();
                    break;
            }
        }
    }

    private class CameraStartUpThread extends Thread {
        private CameraManager.CameraProxy mCameraDevice;
        private int mCameraId;
        private Camera.Parameters mParameters;
        private CameraManager.CameraProxy mTopCamDevice;
        private int mTopCamId;
        private Camera.Parameters mTopCamParameters;
        private volatile boolean mOpenCamera = false;
        private volatile boolean mIsActive = true;
        private ConditionVariable mConditionVariable = new ConditionVariable();
        private boolean mCancel = false;

        public CameraStartUpThread() {
            this.mCameraId = CameraDeviceCtrl.this.getPreferredCameraId(CameraDeviceCtrl.this.mPreferences);
            CameraDeviceCtrl.this.mPreferences.setLocalId(CameraDeviceCtrl.this.mCameraActivity, this.mCameraId);
            SettingUtils.upgradeLocalPreferences(CameraDeviceCtrl.this.mPreferences.getLocal());
            SettingUtils.writePreferredCameraId(CameraDeviceCtrl.this.mPreferences, this.mCameraId);
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            while (this.mIsActive) {
                synchronized (this) {
                    if (!this.mOpenCamera) {
                        CameraDeviceCtrl.this.cameraStartUpThreadDone();
                        waitWithoutInterrupt(this);
                    } else {
                        this.mOpenCamera = false;
                        if (CameraDeviceCtrl.this.mIsFirstStartUp) {
                            CameraPerformanceTracker.onEvent("CameraDeviceCtrl", "CameraStartUp", true);
                            if (firstOpenCamera() != 0) {
                                CameraDeviceCtrl.this.setCameraState(CameraState.STATE_CAMERA_CLOSED);
                                CameraDeviceCtrl.this.mIsFirstStartUp = false;
                            } else {
                                ModeChecker.updateModeMatrix(CameraDeviceCtrl.this.mCameraActivity, this.mCameraId);
                                CameraDeviceCtrl.this.mCurCameraDevice.setDisplayOrientation(true);
                                CameraDeviceCtrl.this.mCurCameraDevice.setPreviewSize();
                                synchronized (CameraDeviceCtrl.this.mCameraActorSync) {
                                    if (CameraDeviceCtrl.this.mCameraActor == null) {
                                        try {
                                            CameraDeviceCtrl.this.mCameraActorSync.wait();
                                        } catch (InterruptedException e) {
                                            Log.m6e("CameraDeviceCtrl", "mCameraActorSync.wait with InterruptedException");
                                        }
                                    }
                                }
                                CameraDeviceCtrl.this.mCameraActor.onCameraOpenDone();
                                CameraDeviceCtrl.this.mModuleManager.onCameraOpen();
                                CameraDeviceCtrl.this.initializeFocusManager();
                                if (CameraDeviceCtrl.this.mCameraActivity.getCurrentMode() == 0) {
                                    CameraDeviceCtrl.this.mCurCameraDevice.setPhotoModeParameters(CameraDeviceCtrl.this.mCameraActivity.isNonePickIntent());
                                }
                                if (this.mCancel) {
                                    Log.m5d("CameraDeviceCtrl", "[mIsFirstStartUp.run] cancel after openCamera");
                                    CameraDeviceCtrl.this.mIsFirstStartUp = false;
                                } else {
                                    if (CameraDeviceCtrl.this.mCameraActivity.isVideoCaptureIntent()) {
                                        CameraDeviceCtrl.this.initializeSettingController();
                                        CameraDeviceCtrl.this.mModuleManager.setModeSettingValue(CameraDeviceCtrl.this.mCameraActor.getCameraModeType(CameraDeviceCtrl.this.mCameraActor.getMode()), "on");
                                    }
                                    CameraDeviceCtrl.this.applyFirstParameters();
                                    CameraDeviceCtrl.this.mSycForLaunch.block(500L);
                                    CameraDeviceCtrl.this.mCurCameraDevice.setOneShotPreviewCallback(CameraDeviceCtrl.this.mOneShotPreviewCallback);
                                    CameraDeviceCtrl.this.mMainHandler.sendEmptyMessage(13);
                                    CameraDeviceCtrl.this.mMainHandler.sendEmptyMessage(14);
                                    CameraDeviceCtrl.this.mMainHandler.sendEmptyMessage(16);
                                    Storage.mkFileDir(Storage.getFileDirectory());
                                    CameraDeviceCtrl.this.clearDeviceCallbacks();
                                    CameraDeviceCtrl.this.applyDeviceCallbacks();
                                    CameraDeviceCtrl.this.mCameraAppUi.clearViewCallbacks();
                                    CameraDeviceCtrl.this.mCameraAppUi.applayViewCallbacks();
                                    if (this.mCancel) {
                                        Log.m5d("CameraDeviceCtrl", "[mIsFirstStartUp.run] cancel before initializeSettingController");
                                        CameraDeviceCtrl.this.mIsFirstStartUp = false;
                                    } else {
                                        if (!CameraDeviceCtrl.this.mCameraActivity.isVideoCaptureIntent()) {
                                            CameraDeviceCtrl.this.initializeSettingController();
                                            CameraDeviceCtrl.this.mModuleManager.setModeSettingValue(CameraDeviceCtrl.this.mCameraActor.getCameraModeType(CameraDeviceCtrl.this.mCameraActor.getMode()), "on");
                                        }
                                        if (this.mCancel) {
                                            Log.m5d("CameraDeviceCtrl", "[mIsFirstStartUp.run] cancel before applySecondParameters");
                                            CameraDeviceCtrl.this.mIsFirstStartUp = false;
                                        } else {
                                            CameraDeviceCtrl.this.applySecondParameters();
                                            CameraDeviceCtrl.this.setCameraState(CameraState.STATE_CAMERA_OPENED);
                                            CameraDeviceCtrl.this.mIsFirstStartUp = false;
                                            CameraPerformanceTracker.onEvent("CameraDeviceCtrl", "CameraStartUp", false);
                                        }
                                    }
                                }
                            }
                        } else if (openCamera(CameraDeviceCtrl.this.mModuleManager.isNeedDualCamera()) != 0) {
                            CameraDeviceCtrl.this.setCameraState(CameraState.STATE_CAMERA_CLOSED);
                        } else if (this.mCancel) {
                            Log.m5d("CameraDeviceCtrl", "[CameraStartUpThread.run] cancel after openCamera");
                        } else {
                            ModeChecker.updateModeMatrix(CameraDeviceCtrl.this.mCameraActivity, this.mCameraId);
                            CameraDeviceCtrl.this.unInitializeFocusManager();
                            CameraDeviceCtrl.this.initializeFocusManager();
                            CameraDeviceCtrl.this.setDisplayOrientation();
                            if (CameraDeviceCtrl.this.mCurCameraDevice != null) {
                                if (!Util.isFullPreviewMode() && (!"1.7778".equals(CameraDeviceCtrl.this.mISettingCtrl.getSettingValue("pref_camera_picturesize_ratio_key")))) {
                                    CameraDeviceCtrl.this.mCurCameraDevice.setPreviewSize();
                                } else {
                                    CameraDeviceCtrl.this.mCurCameraDevice.setPreviewSizeFull();
                                }
                            }
                            if (this.mCancel) {
                                Log.m5d("CameraDeviceCtrl", "[CameraStartUpThread.run] cancel after focusManager");
                            } else {
                                if (CameraDeviceCtrl.this.mCameraActivity.getCurrentMode() == 0) {
                                    CameraDeviceCtrl.this.mCurCameraDevice.setPhotoModeParameters(CameraDeviceCtrl.this.mCameraActivity.isNonePickIntent());
                                } else if (CameraDeviceCtrl.this.mCameraActivity.getCurrentMode() == 8 && (!CameraDeviceCtrl.this.mCameraActivity.isVideoCaptureIntent())) {
                                    CameraDeviceCtrl.this.mCurCameraDevice.getParametersExt().setCameraMode(2);
                                }
                                CameraDeviceCtrl.this.clearDeviceCallbacks();
                                CameraDeviceCtrl.this.applyDeviceCallbacks();
                                CameraDeviceCtrl.this.initializeSettingController();
                                if (this.mCancel) {
                                    Log.m5d("CameraDeviceCtrl", "[CameraStartUpThread.run] cancel after settingCtrl");
                                } else {
                                    CameraDeviceCtrl.this.mCameraAppUi.clearViewCallbacks();
                                    CameraDeviceCtrl.this.mCameraAppUi.applayViewCallbacks();
                                    if (CameraDeviceCtrl.this.mCameraActivity.isVideoCaptureIntent() || (CameraDeviceCtrl.this.mCameraActor.getMode() != 8 && CameraDeviceCtrl.this.mCameraActor.getMode() != 9)) {
                                        CameraDeviceCtrl.this.mModuleManager.setModeSettingValue(CameraDeviceCtrl.this.mCameraActor.getCameraModeType(CameraDeviceCtrl.this.mCameraActor.getMode()), "on");
                                    }
                                    if (this.mCancel) {
                                        Log.m5d("CameraDeviceCtrl", "[CameraStartUpThread.run] cancel after set setting value");
                                    } else {
                                        CameraDeviceCtrl.this.applyParameters(true);
                                        if (this.mCancel) {
                                            Log.m5d("CameraDeviceCtrl", "[CameraStartUpThread.run] cancel after applyParameters");
                                        } else {
                                            CameraDeviceCtrl.this.setCameraState(CameraState.STATE_CAMERA_OPENED);
                                            CameraDeviceCtrl.this.mMainHandler.sendEmptyMessage(16);
                                            Storage.mkFileDir(Storage.getFileDirectory());
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        public void pauseThread() {
            Log.m5d("CameraDeviceCtrl", "pause CameraStartUpThread");
            this.mConditionVariable.close();
            this.mConditionVariable.block();
        }

        public void resumeThread() {
            Log.m5d("CameraDeviceCtrl", "resume CameraStartUpThread");
            this.mConditionVariable.open();
        }

        public void setCameraId(int i) {
            this.mCameraId = i;
        }

        public int getCameraId() {
            return this.mCameraId;
        }

        public void cancel() {
            this.mCancel = true;
        }

        public boolean isCancel() {
            return this.mCancel;
        }

        public synchronized void openCamera() {
            this.mOpenCamera = true;
            this.mCancel = false;
            notifyAll();
        }

        public synchronized void terminate() {
            this.mIsActive = false;
            notifyAll();
        }

        private int openCamera(boolean z) throws InterruptedException {
            if (this.mCameraId != CameraDeviceCtrl.this.getPreferredCameraId(CameraDeviceCtrl.this.mPreferences)) {
                SettingUtils.writePreferredCameraId(CameraDeviceCtrl.this.mPreferences, this.mCameraId);
            }
            try {
                if (CameraDeviceCtrl.this.mCameraActivity.isNeedOpenStereoCamera() && CameraDeviceCtrl.sCameraSetPropertyMethod != null) {
                    ReflectUtil.callMethodOnObject(null, CameraDeviceCtrl.sCameraSetPropertyMethod, "client.appmode", "MtkStereo");
                }
                Util.openCamera(CameraDeviceCtrl.this.mCameraActivity, z, this.mCameraId);
                this.mCameraDevice = CameraHolder.instance().getCameraProxy(this.mCameraId);
                this.mTopCamId = this.mCameraId == CameraHolder.instance().getBackCameraId() ? CameraHolder.instance().getFrontCameraId() : CameraHolder.instance().getBackCameraId();
                this.mTopCamDevice = CameraHolder.instance().getCameraProxy(this.mTopCamId);
                this.mParameters = this.mCameraDevice == null ? null : CameraHolder.instance().getOriginalParameters(this.mCameraId);
                this.mTopCamParameters = this.mTopCamDevice != null ? CameraHolder.instance().getOriginalParameters(this.mTopCamId) : null;
                CameraDeviceCtrl.this.mCameraActor.onCameraOpenDone();
                if (this.mCameraDevice != null && this.mParameters != null) {
                    CameraDeviceCtrl.this.mCurCameraDevice = new CameraDeviceExt(CameraDeviceCtrl.this.mCameraActivity, this.mCameraDevice, this.mParameters, this.mCameraId, CameraDeviceCtrl.this.mPreferences);
                    SettingUtils.setZsdDefaultValue(CameraDeviceCtrl.this.mCurCameraDevice.getParametersExt().getZSDMode());
                    SettingUtils.setAntiBandingDefaultValue(CameraDeviceCtrl.this.mCurCameraDevice.getParametersExt().getAntibanding());
                } else {
                    Log.m5d("CameraDeviceCtrl", "[openCamera fail],mCameraDevice:" + this.mCameraDevice + ",mParameters:" + this.mParameters);
                }
                if (this.mTopCamDevice != null && this.mTopCamParameters != null) {
                    if (CameraDeviceCtrl.this.mOldTopCameraDevice.getCameraId() == this.mTopCamId) {
                        this.mTopCamParameters = CameraDeviceCtrl.this.mOldTopCameraDevice.getParameters();
                        CameraDeviceCtrl.this.mOldTopCameraDevice = CameraDeviceCtrl.this.mDummyCameraDevice;
                    }
                    CameraDeviceCtrl.this.mTopCameraDevice = new CameraDeviceExt(CameraDeviceCtrl.this.mCameraActivity, this.mTopCamDevice, this.mTopCamParameters, this.mTopCamId, CameraDeviceCtrl.this.mPreferences);
                } else {
                    Log.m5d("CameraDeviceCtrl", "[openCamera fail],mTopCamDevice:" + this.mTopCamDevice + ",mTopCamParameters:" + this.mTopCamParameters);
                }
                CameraDeviceCtrl.this.mIsOpenCameraFail = false;
                CameraDeviceCtrl.this.mModuleManager.onCameraOpen();
                return 0;
            } catch (CameraDisabledException e) {
                Log.m6e("CameraDeviceCtrl", "[runopenCamera]CameraDisabledException e:" + e);
                CameraDeviceCtrl.this.mIsOpenCameraFail = true;
                CameraDeviceCtrl.this.mMainHandler.sendEmptyMessage(10);
                return -2;
            } catch (CameraHardwareException e2) {
                Log.m6e("CameraDeviceCtrl", "[run.openCamera]CameraHardwareException e:" + e2);
                CameraDeviceCtrl.this.mIsOpenCameraFail = true;
                CameraDeviceCtrl.this.mMainHandler.sendEmptyMessage(9);
                return -1;
            }
        }

        private void waitWithoutInterrupt(Object obj) throws InterruptedException {
            try {
                obj.wait();
            } catch (InterruptedException e) {
                Log.m11w("CameraDeviceCtrl", "unexpected interrupt: " + obj);
            }
        }

        private int firstOpenCamera() throws InterruptedException {
            Log.m5d("CameraDeviceCtrl", "[run.firstOpenCamera] mCameraId:" + this.mCameraId);
            try {
                Util.openCamera(CameraDeviceCtrl.this.mCameraActivity, false, this.mCameraId);
                this.mCameraDevice = CameraHolder.instance().getCameraProxy(this.mCameraId);
                this.mParameters = this.mCameraDevice != null ? CameraHolder.instance().getOriginalParameters(this.mCameraId) : null;
                if (this.mCameraDevice != null && this.mParameters != null) {
                    CameraDeviceCtrl.this.mCurCameraDevice = new CameraDeviceExt(CameraDeviceCtrl.this.mCameraActivity, this.mCameraDevice, this.mParameters, this.mCameraId, CameraDeviceCtrl.this.mPreferences);
                    String zSDMode = CameraDeviceCtrl.this.mCurCameraDevice.getParametersExt().getZSDMode();
                    Log.m5d("CameraDeviceCtrl", "[run.firstOpenCamera], Get default ZSD value from parameters, value:" + zSDMode);
                    SettingUtils.setZsdDefaultValue(zSDMode);
                    SettingUtils.setAntiBandingDefaultValue(CameraDeviceCtrl.this.mCurCameraDevice.getParametersExt().getAntibanding());
                } else {
                    Log.m5d("CameraDeviceCtrl", "[openCamera fail],mCameraDevice:" + this.mCameraDevice + ",mParameters:" + this.mParameters);
                }
                CameraDeviceCtrl.this.mIsOpenCameraFail = false;
                return 0;
            } catch (CameraDisabledException e) {
                Log.m6e("CameraDeviceCtrl", "[run.firstOpenCamera]CameraDisabledException e:" + e);
                CameraDeviceCtrl.this.mIsOpenCameraFail = true;
                CameraDeviceCtrl.this.mMainHandler.sendEmptyMessage(10);
                return -2;
            } catch (CameraHardwareException e2) {
                Log.m6e("CameraDeviceCtrl", "[run.firstOpenCamera]CameraHardwareException e:" + e2);
                CameraDeviceCtrl.this.mIsOpenCameraFail = true;
                CameraDeviceCtrl.this.mMainHandler.sendEmptyMessage(9);
                return -1;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void applyFirstParameters() {
        Log.m5d("CameraDeviceCtrl", "applyFirstParameters");
        CameraPerformanceTracker.onEvent("CameraDeviceCtrl", "ApplyFirstParameters", true);
        this.mIsFirstOpenCamera = false;
        this.mMainHandler.sendEmptyMessage(17);
        switchCameraPreview();
        this.mCurCameraDevice.setJpegRotation(this.mOrientation);
        this.mCameraAppUi.setZoomParameter();
        this.mCurCameraDevice.setDisplayOrientation(true);
        this.mCurCameraDevice.setPreviewFormat(842094169);
        applyFocusCapabilities(false);
        if (!this.mCameraActivity.isImageCaptureIntent() && (!this.mCameraActivity.isVideoCaptureIntent())) {
            this.mCurCameraDevice.getParametersExt().setZSDMode(SettingUtils.getPreferenceValue(this.mCameraActivity, this.mPreferences, 26, SettingUtils.getZsdDefaultValue()));
        }
        this.mCurCameraDevice.getParametersExt().set("first-preview-frame-black", 1);
        this.mCurCameraDevice.applyParametersToServer();
        this.mCameraActor.onCameraParameterReady(true);
        CameraPerformanceTracker.onEvent("CameraDeviceCtrl", "ApplyFirstParameters", false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void applySecondParameters() {
        CameraPerformanceTracker.onEvent("CameraDeviceCtrl", "ApplySecondParameters", true);
        this.mCurCameraDevice.getParametersExt().set("first-preview-frame-black", 0);
        turnOnWhenShown();
        this.mCurCameraDevice.applyParametersToServer();
        this.mCameraActor.onCameraParameterReady(false);
        this.mMainHandler.sendEmptyMessage(13);
        this.mCameraAppUi.setDngState(this.mISettingCtrl.getSettingValue("pref_dng_key"));
        this.mCameraAppUi.showRemainingAways();
        this.mCurCameraDevice.updateParameters();
        this.mLastPreviewSize = this.mCurCameraDevice.getPreviewSize();
        this.mLastZsdMode = this.mCurCameraDevice.getZsdMode();
        CameraPerformanceTracker.onEvent("CameraDeviceCtrl", "ApplySecondParameters", false);
    }

    private CameraState getCameraState() {
        return this.mCameraState;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setCameraState(CameraState cameraState) {
        Log.m5d("CameraDeviceCtrl", "[setCameraState] cameraState:" + cameraState);
        this.mCameraState = cameraState;
    }

    public void doSwitchCameraDevice() {
        Log.m8i("CameraDeviceCtrl", "doSwitchCameraDevice");
        if (getCameraState() == CameraState.STATE_CAMERA_CLOSED) {
            return;
        }
        this.mIsSwitchingPip = true;
        this.mCameraAppUi.resetZoom();
        this.mCameraAppUi.setZoomParameter();
        this.mCurCameraDevice.stopFaceDetection();
        clearDeviceCallbacks();
        this.mCameraAppUi.dismissInfo();
        ICameraDeviceExt iCameraDeviceExt = this.mCurCameraDevice;
        this.mCurCameraDevice = this.mTopCameraDevice;
        this.mTopCameraDevice = iCameraDeviceExt;
        this.mCameraStartUpThread.setCameraId(this.mCurCameraDevice.getCameraId());
        applyDeviceCallbacks();
        initializeFocusManager();
        this.mCameraAppUi.collapseViewManager(true);
        clearFocusAndFace();
        this.mPreferences.setLocalId(this.mCameraActivity, this.mCurCameraDevice.getCameraId());
        SettingUtils.upgradeLocalPreferences(this.mPreferences.getLocal());
        SettingUtils.writePreferredCameraId(this.mPreferences, this.mCurCameraDevice.getCameraId());
        this.mCurCameraDevice.setPreviewSize();
        this.mISettingCtrl.updateSetting(this.mPreferences.getLocal());
        this.mMainHandler.sendEmptyMessage(12);
        if (this.mCameraActor.getMode() == 5) {
            this.mISettingCtrl.onSettingChanged("photo_pip_key", "on");
        } else {
            this.mISettingCtrl.onSettingChanged("video_pip_key", "on");
        }
        applyParameters(false);
        this.mIsSwitchingPip = false;
    }

    private void clearFocusAndFace() {
        if (this.mCameraActivity.getFrameView() != null) {
            this.mCameraActivity.getFrameView().clear();
        }
        if (this.mFocusManager != null) {
            this.mFocusManager.removeMessages();
        }
    }

    private void releaseSurfaceTexture() {
        this.mTopCamSurfaceTexture = null;
        this.mSurfaceTexture = null;
    }

    private void notifySurfaceViewDestroy(SurfaceHolder surfaceHolder) {
        if (this.mModuleManager.isDisplayUseSurfaceView()) {
            this.mModuleManager.notifySurfaceViewDestroyed(surfaceHolder.getSurface());
        }
    }
}
