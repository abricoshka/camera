package com.mediatek.camera.p005v2.module;

import android.app.Activity;
import android.graphics.RectF;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.os.Handler;
import android.os.HandlerThread;
import android.view.Surface;
import com.mediatek.camera.debug.LogHelper;
import com.mediatek.camera.p005v2.control.ControlImpl;
import com.mediatek.camera.p005v2.control.IControl$IAaaController;
import com.mediatek.camera.p005v2.detection.DetectionManager;
import com.mediatek.camera.p005v2.module.ModuleListener;
import com.mediatek.camera.p005v2.platform.app.AppController;
import com.mediatek.camera.p005v2.platform.device.CameraDeviceManager;
import com.mediatek.camera.p005v2.platform.device.CameraDeviceProxy;
import com.mediatek.camera.p005v2.util.Utils;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* loaded from: classes.dex */
public class DualCameraModule extends AbstractCameraModule {
    private static final LogHelper.Tag TAG = new LogHelper.Tag(DualCameraModule.class.getSimpleName());
    private Handler mCameraHandler;
    private HandlerThread mCameraHandlerThread;
    private CameraDeviceManager mCameraManager;
    private ModuleListener.RequestType mCurrentRepeatingRequest;
    private volatile boolean mIsCameraModulePaused;
    private boolean mIsDeviceNeedSwitch;
    private CameraDeviceProxy.CameraSessionCallback mMainCamSessionCallback;
    private CameraDeviceManager.CameraStateCallback mMainCamStateCallback;
    private volatile CameraDeviceProxy mMainCameraDevice;
    private CameraCaptureSession.CaptureCallback mMainSessionCapProgressCallback;
    private int mOrientation;
    private ControlImpl mSubAaaControl;
    private DetectionManager mSubCamDetectionManager;
    private CameraDeviceProxy.CameraSessionCallback mSubCamSessionCallback;
    private CameraDeviceManager.CameraStateCallback mSubCamStateCallback;
    private volatile CameraDeviceProxy mSubCameraDevice;
    private CameraCaptureSession.CaptureCallback mSubSessionCapProgressCallback;

    public DualCameraModule(AppController appController) {
        super(appController);
        this.mIsDeviceNeedSwitch = false;
        this.mOrientation = -1;
        this.mCurrentRepeatingRequest = ModuleListener.RequestType.PREVIEW;
        this.mIsCameraModulePaused = false;
        this.mMainCamStateCallback = new CameraDeviceManager.CameraStateCallback() { // from class: com.mediatek.camera.v2.module.DualCameraModule.1
            @Override // com.mediatek.camera.v2.platform.device.CameraDeviceManager.CameraStateCallback
            public void onOpened(CameraDeviceProxy cameraDeviceProxy) {
                LogHelper.m23d(DualCameraModule.TAG, "[mMainCamStateCallback.onOpened]");
                DualCameraModule.this.mPendingSwitchCameraId = -1;
                DualCameraModule.this.mMainCameraDevice = cameraDeviceProxy;
                DualCameraModule.this.requestChangeSessionOutputs(false, true);
            }

            @Override // com.mediatek.camera.v2.platform.device.CameraDeviceManager.CameraStateCallback
            public void onError(int i) {
                DualCameraModule.this.showErrorAndFinish(i);
            }
        };
        this.mMainCamSessionCallback = new CameraDeviceProxy.CameraSessionCallback() { // from class: com.mediatek.camera.v2.module.DualCameraModule.2

            /* renamed from: -com-mediatek-camera-v2-module-ModuleListener$RequestTypeSwitchesValues */
            private static final /* synthetic */ int[] f75x71d17683 = null;

            /* renamed from: -getcom-mediatek-camera-v2-module-ModuleListener$RequestTypeSwitchesValues */
            private static /* synthetic */ int[] m41x960f455f() {
                if (f75x71d17683 != null) {
                    return f75x71d17683;
                }
                int[] iArr = new int[ModuleListener.RequestType.valuesCustom().length];
                try {
                    iArr[ModuleListener.RequestType.MANUAL.ordinal()] = 4;
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
                    iArr[ModuleListener.RequestType.VIDEO_SNAP_SHOT.ordinal()] = 5;
                } catch (NoSuchFieldError e5) {
                }
                try {
                    iArr[ModuleListener.RequestType.ZERO_SHUTTER_DELAY.ordinal()] = 6;
                } catch (NoSuchFieldError e6) {
                }
                f75x71d17683 = iArr;
                return iArr;
            }

            @Override // com.mediatek.camera.v2.platform.device.CameraDeviceProxy.CameraSessionCallback
            public void onSessionConfigured() {
                DualCameraModule.this.requestChangeCaptureRequets(true, true, DualCameraModule.this.getDefaultRepeatingRequest(), ModuleListener.CaptureType.REPEATING_REQUEST);
            }

            @Override // com.mediatek.camera.v2.platform.device.CameraDeviceProxy.CameraSessionCallback
            public void onSessionActive() {
            }

            @Override // com.mediatek.camera.v2.platform.device.CameraDeviceProxy.CameraSessionCallback
            public CameraCaptureSession.CaptureCallback configuringSessionRequests(CaptureRequest.Builder builder, ModuleListener.RequestType requestType, ModuleListener.CaptureType captureType) {
                CameraCaptureSession.CaptureCallback captureCallback = null;
                switch (m41x960f455f()[requestType.ordinal()]) {
                    case 1:
                    case 2:
                        captureCallback = DualCameraModule.this.mMainSessionCapProgressCallback;
                        break;
                    case 3:
                        if (!DualCameraModule.this.mIsDeviceNeedSwitch) {
                            captureCallback = DualCameraModule.this.mCurrentMode.getCaptureCallback();
                            break;
                        }
                        break;
                }
                builder.set(CaptureRequest.SCALER_CROP_REGION, Utils.cropRegionForZoom(DualCameraModule.this.mAppController.getActivity(), "0", 1.0f));
                HashMap map = new HashMap();
                map.put(requestType, builder);
                DualCameraModule.this.mAaaControl.configuringSessionRequests(map, captureType, true);
                DualCameraModule.this.mDetectionManager.configuringSessionRequests(map, captureType);
                DualCameraModule.this.mCurrentMode.configuringSessionRequests(map, true);
                return captureCallback;
            }

            @Override // com.mediatek.camera.v2.platform.device.CameraDeviceProxy.CameraSessionCallback
            public void configuringSessionOutputs(List<Surface> list) {
                DualCameraModule.this.mCurrentMode.configuringSessionOutputs(list, true);
            }
        };
        this.mMainSessionCapProgressCallback = new CameraCaptureSession.CaptureCallback() { // from class: com.mediatek.camera.v2.module.DualCameraModule.3
            @Override // android.hardware.camera2.CameraCaptureSession.CaptureCallback
            public void onCaptureStarted(CameraCaptureSession cameraCaptureSession, CaptureRequest captureRequest, long j, long j2) {
                DualCameraModule.this.mAaaControl.onPreviewCaptureStarted(captureRequest, j, j2);
                DualCameraModule.this.mDetectionManager.onCaptureStarted(captureRequest, j, j2);
                DualCameraModule.this.mCurrentMode.onPreviewCaptureStarted(captureRequest, j, j2);
            }

            @Override // android.hardware.camera2.CameraCaptureSession.CaptureCallback
            public void onCaptureProgressed(CameraCaptureSession cameraCaptureSession, CaptureRequest captureRequest, CaptureResult captureResult) {
                DualCameraModule.this.mAaaControl.onPreviewCaptureProgressed(captureRequest, captureResult);
            }

            @Override // android.hardware.camera2.CameraCaptureSession.CaptureCallback
            public void onCaptureCompleted(CameraCaptureSession cameraCaptureSession, CaptureRequest captureRequest, TotalCaptureResult totalCaptureResult) {
                DualCameraModule.this.mAaaControl.onPreviewCaptureCompleted(captureRequest, totalCaptureResult);
                DualCameraModule.this.mDetectionManager.onCaptureCompleted(captureRequest, totalCaptureResult);
                DualCameraModule.this.mCurrentMode.onPreviewCaptureCompleted(captureRequest, totalCaptureResult);
            }
        };
        this.mSubCamStateCallback = new CameraDeviceManager.CameraStateCallback() { // from class: com.mediatek.camera.v2.module.DualCameraModule.4
            @Override // com.mediatek.camera.v2.platform.device.CameraDeviceManager.CameraStateCallback
            public void onOpened(CameraDeviceProxy cameraDeviceProxy) {
                LogHelper.m23d(DualCameraModule.TAG, "[mSubCamStateCallback.onOpened]");
                DualCameraModule.this.mPendingSwitchCameraId = -1;
                DualCameraModule.this.mSubCameraDevice = cameraDeviceProxy;
                DualCameraModule.this.requestChangeSessionOutputs(false, false);
            }

            @Override // com.mediatek.camera.v2.platform.device.CameraDeviceManager.CameraStateCallback
            public void onError(int i) {
                DualCameraModule.this.showErrorAndFinish(i);
            }
        };
        this.mSubCamSessionCallback = new CameraDeviceProxy.CameraSessionCallback() { // from class: com.mediatek.camera.v2.module.DualCameraModule.5

            /* renamed from: -com-mediatek-camera-v2-module-ModuleListener$RequestTypeSwitchesValues */
            private static final /* synthetic */ int[] f76x71d17683 = null;

            /* renamed from: -getcom-mediatek-camera-v2-module-ModuleListener$RequestTypeSwitchesValues */
            private static /* synthetic */ int[] m42x960f455f() {
                if (f76x71d17683 != null) {
                    return f76x71d17683;
                }
                int[] iArr = new int[ModuleListener.RequestType.valuesCustom().length];
                try {
                    iArr[ModuleListener.RequestType.MANUAL.ordinal()] = 4;
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
                    iArr[ModuleListener.RequestType.VIDEO_SNAP_SHOT.ordinal()] = 5;
                } catch (NoSuchFieldError e5) {
                }
                try {
                    iArr[ModuleListener.RequestType.ZERO_SHUTTER_DELAY.ordinal()] = 6;
                } catch (NoSuchFieldError e6) {
                }
                f76x71d17683 = iArr;
                return iArr;
            }

            @Override // com.mediatek.camera.v2.platform.device.CameraDeviceProxy.CameraSessionCallback
            public void onSessionConfigured() {
                DualCameraModule.this.requestChangeCaptureRequets(false, true, DualCameraModule.this.getDefaultRepeatingRequest(), ModuleListener.CaptureType.REPEATING_REQUEST);
            }

            @Override // com.mediatek.camera.v2.platform.device.CameraDeviceProxy.CameraSessionCallback
            public void onSessionActive() {
            }

            @Override // com.mediatek.camera.v2.platform.device.CameraDeviceProxy.CameraSessionCallback
            public CameraCaptureSession.CaptureCallback configuringSessionRequests(CaptureRequest.Builder builder, ModuleListener.RequestType requestType, ModuleListener.CaptureType captureType) {
                CameraCaptureSession.CaptureCallback captureCallback = null;
                switch (m42x960f455f()[requestType.ordinal()]) {
                    case 1:
                    case 2:
                        captureCallback = DualCameraModule.this.mSubSessionCapProgressCallback;
                        break;
                    case 3:
                        if (DualCameraModule.this.mIsDeviceNeedSwitch) {
                            captureCallback = DualCameraModule.this.mCurrentMode.getCaptureCallback();
                            break;
                        }
                        break;
                }
                builder.set(CaptureRequest.SCALER_CROP_REGION, Utils.cropRegionForZoom(DualCameraModule.this.mAppController.getActivity(), "1", 1.0f));
                HashMap map = new HashMap();
                map.put(requestType, builder);
                DualCameraModule.this.mSubAaaControl.configuringSessionRequests(map, captureType, true);
                DualCameraModule.this.mSubCamDetectionManager.configuringSessionRequests(map, captureType);
                DualCameraModule.this.mCurrentMode.configuringSessionRequests(map, false);
                return captureCallback;
            }

            @Override // com.mediatek.camera.v2.platform.device.CameraDeviceProxy.CameraSessionCallback
            public void configuringSessionOutputs(List<Surface> list) {
                DualCameraModule.this.mCurrentMode.configuringSessionOutputs(list, false);
            }
        };
        this.mSubSessionCapProgressCallback = new CameraCaptureSession.CaptureCallback() { // from class: com.mediatek.camera.v2.module.DualCameraModule.6
            @Override // android.hardware.camera2.CameraCaptureSession.CaptureCallback
            public void onCaptureStarted(CameraCaptureSession cameraCaptureSession, CaptureRequest captureRequest, long j, long j2) {
                DualCameraModule.this.mSubAaaControl.onPreviewCaptureStarted(captureRequest, j, j2);
                DualCameraModule.this.mSubCamDetectionManager.onCaptureStarted(captureRequest, j, j2);
                DualCameraModule.this.mCurrentMode.onPreviewCaptureStarted(captureRequest, j, j2);
            }

            @Override // android.hardware.camera2.CameraCaptureSession.CaptureCallback
            public void onCaptureProgressed(CameraCaptureSession cameraCaptureSession, CaptureRequest captureRequest, CaptureResult captureResult) {
                DualCameraModule.this.mSubAaaControl.onPreviewCaptureProgressed(captureRequest, captureResult);
            }

            @Override // android.hardware.camera2.CameraCaptureSession.CaptureCallback
            public void onCaptureCompleted(CameraCaptureSession cameraCaptureSession, CaptureRequest captureRequest, TotalCaptureResult totalCaptureResult) {
                DualCameraModule.this.mSubAaaControl.onPreviewCaptureCompleted(captureRequest, totalCaptureResult);
                DualCameraModule.this.mSubCamDetectionManager.onCaptureCompleted(captureRequest, totalCaptureResult);
                DualCameraModule.this.mCurrentMode.onPreviewCaptureCompleted(captureRequest, totalCaptureResult);
            }
        };
        this.mDetectionManager = new DetectionManager(appController, this, "0");
        this.mAaaControl = new ControlImpl(appController, this, true, "0");
        this.mSubCamDetectionManager = new DetectionManager(appController, this, "1");
        this.mSubAaaControl = new ControlImpl(appController, this, true, "1");
    }

    @Override // com.mediatek.camera.p005v2.module.AbstractCameraModule, com.mediatek.camera.p005v2.platform.module.ModuleController
    public void open(Activity activity, boolean z, boolean z2) {
        this.mSubAaaControl.open(activity, this.mAppUi.getModuleLayoutRoot(), z2);
        this.mSubCamDetectionManager.open(activity, this.mAppUi.getModuleLayoutRoot(), z2);
        super.open(activity, z, z2);
        initializeCameraStaticInfo(activity);
        this.mAbstractModuleUI = new DualCameraModuleUi(activity, this, this.mAppUi.getModuleLayoutRoot(), this.mStreamManager.getPreviewCallback());
        this.mAppController.setModuleUiListener(this.mAbstractModuleUI);
        this.mAppController.addPreviewAreaSizeChangedListener(this);
    }

    @Override // com.mediatek.camera.p005v2.module.AbstractCameraModule, com.mediatek.camera.p005v2.platform.module.ModuleController
    public void close() {
        LogHelper.m26i(TAG, "[close]+");
        super.close();
        this.mSubAaaControl.close();
        this.mSubCamDetectionManager.close();
        this.mAppController.removePreviewAreaSizeChangedListener(this);
        this.mIsDeviceNeedSwitch = false;
        LogHelper.m26i(TAG, "[close]-");
    }

    @Override // com.mediatek.camera.p005v2.module.AbstractCameraModule, com.mediatek.camera.p005v2.platform.module.ModuleController
    public void resume() {
        LogHelper.m26i(TAG, "[resume]+");
        super.resume();
        this.mSubAaaControl.resume();
        this.mSubCamDetectionManager.resume();
        this.mIsCameraModulePaused = false;
        this.mCameraHandlerThread = new HandlerThread("DualCameraModule.CameraHandler");
        this.mCameraHandlerThread.start();
        this.mCameraHandler = new Handler(this.mCameraHandlerThread.getLooper());
        HandlerThread handlerThread = new HandlerThread("tmpThread_openCam");
        handlerThread.start();
        new Handler(handlerThread.getLooper()).post(new Runnable() { // from class: com.mediatek.camera.v2.module.DualCameraModule.7
            @Override // java.lang.Runnable
            public void run() {
                DualCameraModule.this.openCamera();
            }
        });
        LogHelper.m26i(TAG, "[resume]-");
    }

    @Override // com.mediatek.camera.p005v2.module.AbstractCameraModule, com.mediatek.camera.p005v2.platform.module.ModuleController
    public void pause() {
        LogHelper.m26i(TAG, "[pause]+");
        this.mIsCameraModulePaused = true;
        this.mCurrentMode.onActivityPause();
        closeCamera();
        super.pause();
        this.mSubAaaControl.pause();
        this.mSubCamDetectionManager.pause();
        LogHelper.m26i(TAG, "[pause]-");
    }

    @Override // com.mediatek.camera.p005v2.module.AbstractCameraModule, com.mediatek.camera.v2.platform.module.ModuleUi.PreviewAreaChangedListener
    public void onPreviewAreaChanged(RectF rectF) {
        super.onPreviewAreaChanged(rectF);
        this.mSubAaaControl.onPreviewAreaChanged(rectF);
        this.mSubCamDetectionManager.onPreviewAreaChanged(rectF);
    }

    @Override // com.mediatek.camera.p005v2.module.AbstractCameraModule, com.mediatek.camera.v2.setting.ISettingServant.ISettingChangedListener
    public void onSettingChanged(Map<String, String> map) {
        String str;
        super.onSettingChanged(map);
        if ((map != null ? map.containsKey("pref_camera_id_key") : false) && (str = map.get("pref_camera_id_key")) != this.mCameraId) {
            this.mIsDeviceNeedSwitch = !this.mIsDeviceNeedSwitch;
            this.mCameraId = str;
            requestChangeCaptureRequets(false, getDefaultRepeatingRequest(), ModuleListener.CaptureType.REPEATING_REQUEST);
            LogHelper.m26i(TAG, "onSettingChanged cameraId:" + this.mCameraId);
        }
    }

    @Override // com.mediatek.camera.p005v2.module.AbstractCameraModule, com.mediatek.camera.p005v2.platform.module.ModuleController
    public void onCameraPicked(String str) {
        LogHelper.m26i(TAG, "onCameraPicked newCameraId: " + str);
        super.onCameraPicked(str);
        this.mCurrentMode.switchCamera(str);
    }

    @Override // com.mediatek.camera.p005v2.platform.module.ModuleController
    public void onOrientationChanged(int i) {
        if (i == -1) {
            return;
        }
        int iRoundOrientation = Utils.roundOrientation(i, this.mOrientation);
        if (this.mOrientation != iRoundOrientation) {
            this.mOrientation = iRoundOrientation;
        }
        this.mAbstractModuleUI.onOrientationChanged(this.mOrientation);
        this.mAaaControl.onOrientationChanged(iRoundOrientation);
        this.mSubAaaControl.onOrientationChanged(iRoundOrientation);
        this.mDetectionManager.onOrientationChanged(iRoundOrientation);
        this.mSubCamDetectionManager.onOrientationChanged(iRoundOrientation);
        this.mCurrentMode.onOrientationChanged(iRoundOrientation);
    }

    @Override // com.mediatek.camera.p005v2.module.AbstractCameraModule
    public boolean onSingleTapUp(float f, float f2) {
        if (this.mPaused || this.mMainCameraDevice == null || this.mSubCameraDevice == null) {
            LogHelper.m26i(TAG, "[onSingleTapUp]- mPaused : " + this.mPaused);
            return false;
        }
        if (super.onSingleTapUp(f, f2)) {
            return true;
        }
        if (this.mIsDeviceNeedSwitch) {
            this.mSubAaaControl.onSingleTapUp(f, f2);
            this.mSubCamDetectionManager.onSingleTapUp(f, f2);
        } else {
            this.mDetectionManager.onSingleTapUp(f, f2);
            this.mAaaControl.onSingleTapUp(f, f2);
        }
        LogHelper.m26i(TAG, "[onSingleTapUp]-");
        return false;
    }

    @Override // com.mediatek.camera.p005v2.module.AbstractCameraModule
    public boolean onLongPress(float f, float f2) {
        LogHelper.m26i(TAG, "onLongPress x = " + f + " y = " + f2);
        if (super.onLongPress(f, f2)) {
            return true;
        }
        if (this.mIsDeviceNeedSwitch) {
            this.mSubCamDetectionManager.onLongPressed(f, f2);
            return false;
        }
        this.mDetectionManager.onLongPressed(f, f2);
        return false;
    }

    @Override // com.mediatek.camera.p005v2.module.AbstractCameraModule, com.mediatek.camera.p005v2.module.ModuleListener
    public void requestChangeCaptureRequets(boolean z, ModuleListener.RequestType requestType, ModuleListener.CaptureType captureType) {
        super.requestChangeCaptureRequets(z, requestType, captureType);
        requestChangeCaptureRequets(true, z, requestType, captureType);
        requestChangeCaptureRequets(false, z, requestType, captureType);
    }

    @Override // com.mediatek.camera.p005v2.module.AbstractCameraModule, com.mediatek.camera.p005v2.module.ModuleListener
    public void requestChangeCaptureRequets(boolean z, boolean z2, ModuleListener.RequestType requestType, ModuleListener.CaptureType captureType) {
        if (ModuleListener.RequestType.RECORDING == requestType || ModuleListener.RequestType.PREVIEW == requestType) {
            this.mCurrentRepeatingRequest = requestType;
        }
        if (z) {
            if (this.mMainCameraDevice == null) {
                LogHelper.m26i(TAG, "requestChangeCaptureRequets but main camera is null!");
                return;
            } else {
                this.mMainCameraDevice.requestChangeCaptureRequets(z2, requestType, captureType);
                return;
            }
        }
        if (this.mSubCameraDevice == null) {
            LogHelper.m26i(TAG, "requestChangeCaptureRequets but sub camera is null!");
        } else {
            this.mSubCameraDevice.requestChangeCaptureRequets(z2, requestType, captureType);
        }
    }

    @Override // com.mediatek.camera.p005v2.module.AbstractCameraModule, com.mediatek.camera.p005v2.module.ModuleListener
    public void requestChangeSessionOutputs(boolean z) {
        requestChangeSessionOutputs(z, true);
        requestChangeSessionOutputs(z, false);
    }

    @Override // com.mediatek.camera.p005v2.module.AbstractCameraModule, com.mediatek.camera.p005v2.module.ModuleListener
    public void requestChangeSessionOutputs(boolean z, boolean z2) {
        if (z2) {
            if (this.mMainCameraDevice == null) {
                LogHelper.m26i(TAG, "requestChangeSessionOutputs but main camera is null!");
                return;
            } else {
                this.mMainCameraDevice.requestChangeSessionOutputs(z);
                return;
            }
        }
        if (this.mSubCameraDevice == null) {
            LogHelper.m26i(TAG, "requestChangeSessionOutputs but sub camera is null!");
        } else {
            this.mSubCameraDevice.requestChangeSessionOutputs(z);
        }
    }

    @Override // com.mediatek.camera.p005v2.module.AbstractCameraModule, com.mediatek.camera.p005v2.module.ModuleListener
    public IControl$IAaaController get3AController(String str) {
        if ("0".equals(str)) {
            return this.mAaaControl;
        }
        return this.mSubAaaControl;
    }

    @Override // com.mediatek.camera.p005v2.module.ModuleListener
    public ModuleListener.RequestType getRepeatingRequestType() {
        return getDefaultRepeatingRequest();
    }

    private void initializeCameraStaticInfo(Activity activity) {
        this.mCameraManager = this.mAppController.getCameraManager();
        this.mCameraId = this.mSettingController.getCurrentCameraId();
        this.mIsDeviceNeedSwitch = this.mCameraId == "1";
        LogHelper.m26i(TAG, "initializeCameraStaticInfo mCameraId = " + this.mCameraId);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void openCamera() {
        LogHelper.m26i(TAG, "[openCamera]+, mIsCameraModulePaused:" + this.mIsCameraModulePaused);
        if (this.mCameraManager == null) {
            throw new IllegalStateException("openCamera, but CameraManager is null!");
        }
        acquireOpenCloseLock();
        if (!this.mIsCameraModulePaused) {
            this.mCurrentMode.prepareSurfaceBeforeOpenCamera();
        }
        if (!this.mIsCameraModulePaused) {
            this.mCameraManager.openSync("0", this.mMainCamStateCallback, this.mMainCamSessionCallback, this.mCameraHandler);
        }
        if (!this.mIsCameraModulePaused) {
            this.mCameraManager.openSync("1", this.mSubCamStateCallback, this.mSubCamSessionCallback, this.mCameraHandler);
        }
        releaseOpenCloseLock();
        LogHelper.m26i(TAG, "[openCamera]-");
    }

    private void closeCamera() {
        LogHelper.m26i(TAG, "[closeCamera]+, mMainCameraDevice:" + this.mMainCameraDevice + ",mSubCameraDevice:" + this.mSubCameraDevice);
        acquireOpenCloseLock();
        if (this.mMainCameraDevice != null) {
            this.mMainCameraDevice.close();
            this.mMainCameraDevice = null;
        }
        if (this.mSubCameraDevice != null) {
            this.mSubCameraDevice.close();
            this.mSubCameraDevice = null;
        }
        releaseOpenCloseLock();
        LogHelper.m26i(TAG, "[closeCamera]-");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public ModuleListener.RequestType getDefaultRepeatingRequest() {
        ModuleListener.RequestType requestType = ModuleListener.RequestType.PREVIEW;
        if (this.mCurrentRepeatingRequest == ModuleListener.RequestType.RECORDING) {
            return ModuleListener.RequestType.RECORDING;
        }
        return requestType;
    }
}
