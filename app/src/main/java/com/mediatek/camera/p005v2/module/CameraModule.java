package com.mediatek.camera.p005v2.module;

import android.app.Activity;
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
import com.mediatek.camera.p005v2.mode.AbstractCameraMode;
import com.mediatek.camera.p005v2.module.ModuleListener;
import com.mediatek.camera.p005v2.platform.ModeChangeListener;
import com.mediatek.camera.p005v2.platform.app.AppController;
import com.mediatek.camera.p005v2.platform.device.CameraDeviceManager;
import com.mediatek.camera.p005v2.platform.device.CameraDeviceProxy;
import com.mediatek.camera.p005v2.util.Utils;
import java.util.HashMap;
import java.util.List;

/* loaded from: classes.dex */
public class CameraModule extends AbstractCameraModule {
    private static final LogHelper.Tag TAG = new LogHelper.Tag(CameraModule.class.getSimpleName());
    private CameraDeviceProxy mCameraDevice;
    private CameraDeviceManager mCameraDeviceManager;
    private Handler mCameraHandler;
    private CameraDeviceProxy.CameraSessionCallback mCameraSessionCallback;
    private CameraDeviceManager.CameraStateCallback mCameraStateCallback;
    private ModuleListener.RequestType mCurrentRepeatingRequest;
    private boolean mFirstFrameArrived;
    private ModeChangeListanerImpl mModeChangedListener;
    private int mOrientation;
    private CameraCaptureSession.CaptureCallback mPreviewCapProgressCallback;

    public CameraModule(AppController appController) {
        super(appController);
        this.mOrientation = -1;
        this.mFirstFrameArrived = false;
        this.mCurrentRepeatingRequest = ModuleListener.RequestType.PREVIEW;
        this.mCameraStateCallback = new CameraDeviceManager.CameraStateCallback() { // from class: com.mediatek.camera.v2.module.CameraModule.1
            @Override // com.mediatek.camera.v2.platform.device.CameraDeviceManager.CameraStateCallback
            public void onOpened(CameraDeviceProxy cameraDeviceProxy) {
                LogHelper.m23d(CameraModule.TAG, "onOpened[mCameraStateCallback],mPreviewSurfaceIsReadyForOpen = " + CameraModule.this.mPreviewSurfaceIsReadyForOpen);
                CameraModule.this.mPendingSwitchCameraId = -1;
                CameraModule.this.mCameraDevice = cameraDeviceProxy;
                if (CameraModule.this.mPreviewSurfaceIsReadyForOpen) {
                    CameraModule.this.requestChangeSessionOutputs(true);
                }
                CameraModule.this.releaseOpenCloseLock();
            }

            @Override // com.mediatek.camera.v2.platform.device.CameraDeviceManager.CameraStateCallback
            public void onError(int i) {
                CameraModule.this.showErrorAndFinish(i);
            }
        };
        this.mCameraSessionCallback = new CameraDeviceProxy.CameraSessionCallback() { // from class: com.mediatek.camera.v2.module.CameraModule.2

            /* renamed from: -com-mediatek-camera-v2-module-ModuleListener$RequestTypeSwitchesValues */
            private static final /* synthetic */ int[] f74x71d17683 = null;

            /* renamed from: -getcom-mediatek-camera-v2-module-ModuleListener$RequestTypeSwitchesValues */
            private static /* synthetic */ int[] m40x960f455f() {
                if (f74x71d17683 != null) {
                    return f74x71d17683;
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
                f74x71d17683 = iArr;
                return iArr;
            }

            @Override // com.mediatek.camera.v2.platform.device.CameraDeviceProxy.CameraSessionCallback
            public void onSessionConfigured() {
                CameraModule.this.requestChangeCaptureRequets(false, CameraModule.this.getDefaultRepeatingRequest(), ModuleListener.CaptureType.REPEATING_REQUEST);
            }

            @Override // com.mediatek.camera.v2.platform.device.CameraDeviceProxy.CameraSessionCallback
            public void onSessionActive() {
                CameraModule.this.mFirstFrameArrived = false;
            }

            @Override // com.mediatek.camera.v2.platform.device.CameraDeviceProxy.CameraSessionCallback
            public CameraCaptureSession.CaptureCallback configuringSessionRequests(CaptureRequest.Builder builder, ModuleListener.RequestType requestType, ModuleListener.CaptureType captureType) {
                CameraCaptureSession.CaptureCallback captureCallback = null;
                switch (m40x960f455f()[requestType.ordinal()]) {
                    case 1:
                    case 2:
                        captureCallback = CameraModule.this.mPreviewCapProgressCallback;
                        break;
                    case 3:
                        captureCallback = CameraModule.this.mCurrentMode.getCaptureCallback();
                        break;
                }
                builder.set(CaptureRequest.SCALER_CROP_REGION, Utils.cropRegionForZoom(CameraModule.this.mAppController.getActivity(), CameraModule.this.mCameraId, 1.0f));
                HashMap map = new HashMap();
                map.put(requestType, builder);
                CameraModule.this.mAaaControl.configuringSessionRequests(map, captureType, true);
                CameraModule.this.mDetectionManager.configuringSessionRequests(map, captureType);
                CameraModule.this.mCurrentMode.configuringSessionRequests(map, true);
                return captureCallback;
            }

            @Override // com.mediatek.camera.v2.platform.device.CameraDeviceProxy.CameraSessionCallback
            public void configuringSessionOutputs(List<Surface> list) {
                CameraModule.this.mCurrentMode.configuringSessionOutputs(list, true);
            }
        };
        this.mPreviewCapProgressCallback = new CameraCaptureSession.CaptureCallback() { // from class: com.mediatek.camera.v2.module.CameraModule.3
            @Override // android.hardware.camera2.CameraCaptureSession.CaptureCallback
            public void onCaptureStarted(CameraCaptureSession cameraCaptureSession, CaptureRequest captureRequest, long j, long j2) {
                CameraModule.this.mAaaControl.onPreviewCaptureStarted(captureRequest, j, j2);
                CameraModule.this.mDetectionManager.onCaptureStarted(captureRequest, j, j2);
                CameraModule.this.mCurrentMode.onPreviewCaptureStarted(captureRequest, j, j2);
            }

            @Override // android.hardware.camera2.CameraCaptureSession.CaptureCallback
            public void onCaptureProgressed(CameraCaptureSession cameraCaptureSession, CaptureRequest captureRequest, CaptureResult captureResult) {
                CameraModule.this.mAaaControl.onPreviewCaptureProgressed(captureRequest, captureResult);
            }

            @Override // android.hardware.camera2.CameraCaptureSession.CaptureCallback
            public void onCaptureCompleted(CameraCaptureSession cameraCaptureSession, CaptureRequest captureRequest, TotalCaptureResult totalCaptureResult) {
                CameraModule.this.mAaaControl.onPreviewCaptureCompleted(captureRequest, totalCaptureResult);
                CameraModule.this.mDetectionManager.onCaptureCompleted(captureRequest, totalCaptureResult);
                CameraModule.this.mCurrentMode.onPreviewCaptureCompleted(captureRequest, totalCaptureResult);
                if (!CameraModule.this.mFirstFrameArrived) {
                    CameraModule.this.mCurrentMode.onFirstFrameAvailable();
                    CameraModule.this.mFirstFrameArrived = true;
                }
            }
        };
        this.mDetectionManager = new DetectionManager(appController, this, null);
        this.mAaaControl = new ControlImpl(appController, this, true, null);
    }

    @Override // com.mediatek.camera.p005v2.module.AbstractCameraModule, com.mediatek.camera.p005v2.platform.module.ModuleController
    public void open(Activity activity, boolean z, boolean z2) {
        LogHelper.m26i(TAG, "[open]+");
        super.open(activity, z, z2);
        HandlerThread handlerThread = new HandlerThread("CameraModule.CameraHandler");
        handlerThread.start();
        this.mCameraHandler = new Handler(handlerThread.getLooper());
        this.mAbstractModuleUI = new CameraModuleUi(activity, this, this.mAppUi.getModuleLayoutRoot(), this.mStreamManager.getPreviewCallback());
        this.mAppController.setModuleUiListener(this.mAbstractModuleUI);
        this.mModeChangedListener = new ModeChangeListanerImpl(this, null);
        this.mAppController.setModeChangeListener(this.mModeChangedListener);
        this.mCameraDeviceManager = this.mAppController.getCameraManager();
        this.mCameraId = this.mSettingController.getCurrentCameraId();
        this.mAppController.addPreviewAreaSizeChangedListener(this);
        LogHelper.m26i(TAG, "[open]-");
    }

    @Override // com.mediatek.camera.p005v2.module.AbstractCameraModule, com.mediatek.camera.p005v2.platform.module.ModuleController
    public void resume() {
        LogHelper.m26i(TAG, "[resume]+");
        super.resume();
        openCamera();
        LogHelper.m26i(TAG, "[resume]-");
    }

    @Override // com.mediatek.camera.p005v2.module.AbstractCameraModule, com.mediatek.camera.p005v2.platform.module.ModuleController
    public void pause() {
        LogHelper.m26i(TAG, "[pause]+");
        closeCamera();
        super.pause();
        LogHelper.m26i(TAG, "[pause]-");
    }

    @Override // com.mediatek.camera.p005v2.module.AbstractCameraModule, com.mediatek.camera.p005v2.platform.module.ModuleController
    public void close() {
        super.close();
        LogHelper.m26i(TAG, "[close]+");
        this.mCameraHandler.getLooper().quitSafely();
        this.mAppController.removePreviewAreaSizeChangedListener(this);
        LogHelper.m26i(TAG, "[close]-");
    }

    @Override // com.mediatek.camera.p005v2.module.AbstractCameraModule, com.mediatek.camera.p005v2.platform.module.ModuleController
    public void onBeforeCameraPicked(String str) {
        LogHelper.m23d(TAG, "[onBeforeCameraPicked]+");
        if (this.mPaused) {
            LogHelper.m23d(TAG, "[onBeforeCameraPicked]- mPaused = " + this.mPaused);
        } else {
            closeCamera();
            LogHelper.m23d(TAG, "[onBeforeCameraPicked]-");
        }
    }

    @Override // com.mediatek.camera.p005v2.module.AbstractCameraModule, com.mediatek.camera.p005v2.platform.module.ModuleController
    public void onCameraPicked(String str) {
        LogHelper.m23d(TAG, "[onCameraPicked]+ newCameraId: " + str);
        if (this.mPaused) {
            LogHelper.m23d(TAG, "[onCameraPicked]- mPaused = " + this.mPaused);
            return;
        }
        super.onCameraPicked(str);
        this.mPendingSwitchCameraId = Integer.valueOf(str).intValue();
        this.mCameraId = String.valueOf(this.mPendingSwitchCameraId);
        openCamera();
        this.mCurrentMode.switchCamera(str);
        LogHelper.m23d(TAG, "[onCameraPicked]- ");
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
        this.mCurrentMode.onOrientationChanged(this.mOrientation);
        this.mDetectionManager.onOrientationChanged(this.mOrientation);
    }

    @Override // com.mediatek.camera.p005v2.module.AbstractCameraModule
    public boolean onSingleTapUp(float f, float f2) {
        LogHelper.m26i(TAG, "[onSingleTapUp]+ x = " + f + " y = " + f2);
        if (this.mPaused || this.mCameraDevice == null) {
            LogHelper.m26i(TAG, "[onSingleTapUp]- mPaused : " + this.mPaused + " mCameraDevice: " + this.mCameraDevice);
            return false;
        }
        if (super.onSingleTapUp(f, f2)) {
            return true;
        }
        this.mDetectionManager.onSingleTapUp(f, f2);
        this.mAaaControl.onSingleTapUp(f, f2);
        LogHelper.m26i(TAG, "[onSingleTapUp]-");
        return false;
    }

    @Override // com.mediatek.camera.p005v2.module.AbstractCameraModule
    public boolean onLongPress(float f, float f2) {
        LogHelper.m26i(TAG, "onLongPress x = " + f + " y = " + f2);
        if (super.onLongPress(f, f2)) {
            return true;
        }
        this.mDetectionManager.onLongPressed(f, f2);
        return false;
    }

    @Override // com.mediatek.camera.p005v2.module.AbstractCameraModule, com.mediatek.camera.p005v2.module.ModuleListener
    public void requestChangeCaptureRequets(boolean z, ModuleListener.RequestType requestType, ModuleListener.CaptureType captureType) {
        sendRequestChanging(requestType, captureType, z);
    }

    @Override // com.mediatek.camera.p005v2.module.AbstractCameraModule, com.mediatek.camera.p005v2.module.ModuleListener
    public void requestChangeCaptureRequets(boolean z, boolean z2, ModuleListener.RequestType requestType, ModuleListener.CaptureType captureType) {
        sendRequestChanging(requestType, captureType, z2);
    }

    @Override // com.mediatek.camera.p005v2.module.AbstractCameraModule, com.mediatek.camera.p005v2.module.ModuleListener
    public void requestChangeSessionOutputs(boolean z) {
        if (this.mCameraDevice == null) {
            LogHelper.m23d(TAG, "requestChangeSessionOutputs but CameraDevice is null!!!");
        } else {
            this.mCameraDevice.requestChangeSessionOutputs(z);
        }
    }

    @Override // com.mediatek.camera.p005v2.module.AbstractCameraModule, com.mediatek.camera.p005v2.module.ModuleListener
    public IControl$IAaaController get3AController(String str) {
        return this.mAaaControl;
    }

    @Override // com.mediatek.camera.p005v2.module.ModuleListener
    public ModuleListener.RequestType getRepeatingRequestType() {
        return getDefaultRepeatingRequest();
    }

    @Override // com.mediatek.camera.p005v2.module.AbstractCameraModule
    protected void closeMode(AbstractCameraMode abstractCameraMode) {
        super.closeMode(abstractCameraMode);
        abstractCameraMode.pause();
        abstractCameraMode.close();
    }

    @Override // com.mediatek.camera.p005v2.module.AbstractCameraModule
    protected void openMode(AbstractCameraMode abstractCameraMode) {
        super.openMode(abstractCameraMode);
        abstractCameraMode.open(this.mStreamManager, this.mAppUi.getModuleLayoutRoot(), this.mIsCaptureIntent);
        doModeChange(this.mOldModeIndex, this.mCurrentModeIndex);
        abstractCameraMode.resume();
    }

    @Override // com.mediatek.camera.p005v2.module.AbstractCameraModule
    protected boolean checkSatisfyCaptureCondition() {
        if (this.mCameraDevice == null) {
            LogHelper.m23d(TAG, "checkSatisfyCaptureCondition Photo Shutter Cliecked but mCameraDevice = " + this.mCameraDevice);
            return false;
        }
        return true;
    }

    private class ModeChangeListanerImpl implements ModeChangeListener {
        /* synthetic */ ModeChangeListanerImpl(CameraModule cameraModule, ModeChangeListanerImpl modeChangeListanerImpl) {
            this();
        }

        private ModeChangeListanerImpl() {
        }

        @Override // com.mediatek.camera.p005v2.platform.ModeChangeListener
        public void onModeSelected(int i) {
            CameraModule.this.switchToNewMode(i);
        }
    }

    private void openCamera() {
        if (this.mCameraDeviceManager == null) {
            throw new IllegalStateException("openCamera, but CameraManager is null!");
        }
        this.mCurrentMode.prepareSurfaceBeforeOpenCamera();
        this.mCurrentRepeatingRequest = ModuleListener.RequestType.PREVIEW;
        acquireOpenCloseLock();
        this.mCameraDeviceManager.open(this.mCameraId, this.mCameraStateCallback, this.mCameraSessionCallback, this.mCameraHandler);
    }

    private void closeCamera() {
        LogHelper.m23d(TAG, "closeCamera");
        acquireOpenCloseLock();
        if (this.mCameraDevice != null) {
            this.mCameraDevice.close();
            this.mCameraDevice = null;
        }
        releaseOpenCloseLock();
        this.mPreviewSurfaceIsReadyForOpen = false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public ModuleListener.RequestType getDefaultRepeatingRequest() {
        ModuleListener.RequestType requestType = ModuleListener.RequestType.PREVIEW;
        if (this.mCurrentRepeatingRequest == ModuleListener.RequestType.RECORDING) {
            return ModuleListener.RequestType.RECORDING;
        }
        return requestType;
    }

    private void sendRequestChanging(ModuleListener.RequestType requestType, ModuleListener.CaptureType captureType, boolean z) {
        if (this.mCameraDevice == null) {
            LogHelper.m23d(TAG, "requestChangeCaptureRequets but CameraDevice is null!!!");
            return;
        }
        if (ModuleListener.RequestType.RECORDING == requestType || ModuleListener.RequestType.PREVIEW == requestType) {
            this.mCurrentRepeatingRequest = requestType;
        }
        this.mCameraDevice.requestChangeCaptureRequets(z, requestType, captureType);
    }
}
