package com.mediatek.camera.addition.remotecamera;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import com.mediatek.camera.ICameraAddition;
import com.mediatek.camera.ICameraContext;
import com.mediatek.camera.ICameraMode;
import com.mediatek.camera.addition.CameraAddition;
import com.mediatek.camera.addition.remotecamera.service.IMtkCameraService;
import com.mediatek.camera.addition.remotecamera.service.MtkCameraService;
import com.mediatek.camera.platform.ICameraAppUi;
import com.mediatek.camera.platform.ICameraDeviceManager;
import com.mediatek.camera.util.Log;
import java.util.List;

/* loaded from: classes.dex */
public class RemoteCameraAddition extends CameraAddition {

    /* renamed from: -com-mediatek-camera-ICameraAddition$AdditionActionTypeSwitchesValues */
    private static final /* synthetic */ int[] f70x29f76b14 = null;

    /* renamed from: -com-mediatek-camera-ICameraMode$ActionTypeSwitchesValues, reason: not valid java name */
    private static final /* synthetic */ int[] f100commediatekcameraICameraMode$ActionTypeSwitchesValues = null;
    private ServiceConnection mCameraConnection;
    private IMtkCameraService.Stub mCameraService;
    private boolean mHasNotifyParameterReady;
    private ICameraDeviceManager mICameraDeviceManager;
    private boolean mIsMtkCameraApServiceLaunched;
    private final Camera.PictureCallback mJpegPictureCallback;
    private MainHandler mMainHandler;
    private ICameraAddition.Listener mModeListener;
    private boolean mOpened;
    private int mOrientation;
    private Camera.PreviewCallback mPreviewCallback;

    /* renamed from: -getcom-mediatek-camera-ICameraAddition$AdditionActionTypeSwitchesValues */
    private static /* synthetic */ int[] m22xd5d340f0() {
        if (f70x29f76b14 != null) {
            return f70x29f76b14;
        }
        int[] iArr = new int[ICameraAddition.AdditionActionType.valuesCustom().length];
        try {
            iArr[ICameraAddition.AdditionActionType.ACTION_EFFECT_CLICK.ordinal()] = 4;
        } catch (NoSuchFieldError e) {
        }
        try {
            iArr[ICameraAddition.AdditionActionType.ACTION_ON_START_PREVIEW.ordinal()] = 1;
        } catch (NoSuchFieldError e2) {
        }
        try {
            iArr[ICameraAddition.AdditionActionType.ACTION_ON_STOP_PREVIEW.ordinal()] = 5;
        } catch (NoSuchFieldError e3) {
        }
        try {
            iArr[ICameraAddition.AdditionActionType.ACTION_ON_SWITCH_PIP.ordinal()] = 6;
        } catch (NoSuchFieldError e4) {
        }
        try {
            iArr[ICameraAddition.AdditionActionType.ACTION_TAKEN_PICTURE.ordinal()] = 2;
        } catch (NoSuchFieldError e5) {
        }
        f70x29f76b14 = iArr;
        return iArr;
    }

    /* renamed from: -getcom-mediatek-camera-ICameraMode$ActionTypeSwitchesValues, reason: not valid java name */
    private static /* synthetic */ int[] m666getcommediatekcameraICameraMode$ActionTypeSwitchesValues() {
        if (f100commediatekcameraICameraMode$ActionTypeSwitchesValues != null) {
            return f100commediatekcameraICameraMode$ActionTypeSwitchesValues;
        }
        int[] iArr = new int[ICameraMode.ActionType.valuesCustom().length];
        try {
            iArr[ICameraMode.ActionType.ACTION_CANCEL_BUTTON_CLICK.ordinal()] = 4;
        } catch (NoSuchFieldError e) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_CAN_DO_AUTO_FOCUS.ordinal()] = 5;
        } catch (NoSuchFieldError e2) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_DISABLE_VIDEO_RECORD.ordinal()] = 6;
        } catch (NoSuchFieldError e3) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_FACE_DETECTED.ordinal()] = 7;
        } catch (NoSuchFieldError e4) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_NOTIFY_SURFCEVIEW_DESTROYED.ordinal()] = 8;
        } catch (NoSuchFieldError e5) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_NOTIFY_SURFCEVIEW_DISPLAY_IS_READY.ordinal()] = 9;
        } catch (NoSuchFieldError e6) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_OK_BUTTON_CLICK.ordinal()] = 10;
        } catch (NoSuchFieldError e7) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_BACK_KEY_PRESS.ordinal()] = 11;
        } catch (NoSuchFieldError e8) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_CAMERA_CLOSE.ordinal()] = 12;
        } catch (NoSuchFieldError e9) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_CAMERA_OPEN.ordinal()] = 13;
        } catch (NoSuchFieldError e10) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_CAMERA_PARAMETERS_READY.ordinal()] = 14;
        } catch (NoSuchFieldError e11) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_COMPENSATION_CHANGED.ordinal()] = 15;
        } catch (NoSuchFieldError e12) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_CONFIGURATION_CHANGED.ordinal()] = 16;
        } catch (NoSuchFieldError e13) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_FULL_SCREEN_CHANGED.ordinal()] = 17;
        } catch (NoSuchFieldError e14) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_KEY_EVENT_PRESS.ordinal()] = 18;
        } catch (NoSuchFieldError e15) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_LONG_PRESS.ordinal()] = 19;
        } catch (NoSuchFieldError e16) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_MEDIA_EJECT.ordinal()] = 20;
        } catch (NoSuchFieldError e17) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_PREVIEW_BUFFER_SIZE_CHANGED.ordinal()] = 21;
        } catch (NoSuchFieldError e18) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_PREVIEW_DISPLAY_SIZE_CHANGED.ordinal()] = 22;
        } catch (NoSuchFieldError e19) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_RESTORE_SETTINGS.ordinal()] = 23;
        } catch (NoSuchFieldError e20) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_SELFTIMER_STATE.ordinal()] = 24;
        } catch (NoSuchFieldError e21) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_SETTING_BUTTON_CLICK.ordinal()] = 25;
        } catch (NoSuchFieldError e22) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_SINGLE_TAP_UP.ordinal()] = 26;
        } catch (NoSuchFieldError e23) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_START_PREVIEW.ordinal()] = 27;
        } catch (NoSuchFieldError e24) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_STOP_PREVIEW.ordinal()] = 28;
        } catch (NoSuchFieldError e25) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_SURFACE_TEXTURE_READY.ordinal()] = 29;
        } catch (NoSuchFieldError e26) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_USER_INTERACTION.ordinal()] = 30;
        } catch (NoSuchFieldError e27) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ORITATION_CHANGED.ordinal()] = 1;
        } catch (NoSuchFieldError e28) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_PHOTO_SHUTTER_BUTTON_CLICK.ordinal()] = 31;
        } catch (NoSuchFieldError e29) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_PREVIEW_VISIBLE_CHANGED.ordinal()] = 32;
        } catch (NoSuchFieldError e30) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_SET_DISPLAYROTATION.ordinal()] = 33;
        } catch (NoSuchFieldError e31) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_SHUTTER_BUTTON_FOCUS.ordinal()] = 34;
        } catch (NoSuchFieldError e32) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_SHUTTER_BUTTON_LONG_PRESS.ordinal()] = 35;
        } catch (NoSuchFieldError e33) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_SWITCH_DEVICE.ordinal()] = 36;
        } catch (NoSuchFieldError e34) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_VIDEO_SHUTTER_BUTTON_CLICK.ordinal()] = 37;
        } catch (NoSuchFieldError e35) {
        }
        f100commediatekcameraICameraMode$ActionTypeSwitchesValues = iArr;
        return iArr;
    }

    public RemoteCameraAddition(ICameraContext iCameraContext) {
        super(iCameraContext);
        this.mIsMtkCameraApServiceLaunched = false;
        this.mHasNotifyParameterReady = false;
        this.mOpened = false;
        this.mCameraConnection = new ServiceConnection() { // from class: com.mediatek.camera.addition.remotecamera.RemoteCameraAddition.1
            @Override // android.content.ServiceConnection
            public void onServiceDisconnected(ComponentName componentName) {
                Log.m31d("RemoteCameraAddition", "CameraConnection, onServiceDisconnected()");
                RemoteCameraAddition.this.mCameraService = null;
            }

            @Override // android.content.ServiceConnection
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                Log.m31d("RemoteCameraAddition", "CameraConnection, onServiceConnected()");
                RemoteCameraAddition.this.mCameraService = (IMtkCameraService.Stub) iBinder;
            }
        };
        this.mJpegPictureCallback = new Camera.PictureCallback() { // from class: com.mediatek.camera.addition.remotecamera.RemoteCameraAddition.2
            @Override // android.hardware.Camera.PictureCallback
            public void onPictureTaken(byte[] bArr, Camera camera) {
                Log.m31d("RemoteCameraAddition", "[mJpegPictureCallback]onPictureTaken, jpegData:" + bArr);
                if (RemoteCameraAddition.this.mModeListener != null) {
                    RemoteCameraAddition.this.mModeListener.restartPreview(true);
                }
                if (bArr == null) {
                    return;
                }
                RemoteCameraAddition.this.mIFileSaver.savePhotoFile(bArr, null, System.currentTimeMillis(), RemoteCameraAddition.this.mIModuleCtrl.getLocation(), 0, null);
                Message messageObtain = Message.obtain();
                messageObtain.what = 102;
                messageObtain.obj = bArr;
                RemoteCameraAddition.this.sendMessageToService(messageObtain);
            }
        };
        this.mPreviewCallback = new Camera.PreviewCallback() { // from class: com.mediatek.camera.addition.remotecamera.RemoteCameraAddition.3
            @Override // android.hardware.Camera.PreviewCallback
            public void onPreviewFrame(byte[] bArr, Camera camera) {
                int currentCameraId = RemoteCameraAddition.this.mICameraDeviceManager.getCurrentCameraId();
                ICameraDeviceManager.ICameraDevice cameraDevice = RemoteCameraAddition.this.mICameraDeviceManager.getCameraDevice(currentCameraId);
                Message messageObtain = Message.obtain();
                messageObtain.what = 101;
                messageObtain.obj = bArr;
                messageObtain.arg1 = RemoteCameraAddition.this.getDisplayOrientation(-RemoteCameraAddition.this.mOrientation, currentCameraId);
                RemoteCameraAddition.this.sendMessageToService(messageObtain);
                if (cameraDevice != null) {
                    cameraDevice.addCallbackBuffer(bArr);
                }
            }
        };
        this.mIsMtkCameraApServiceLaunched = isMtkCameraApServiceLaunched(this.mActivity);
        this.mICameraDeviceManager = iCameraContext.getCameraDeviceManager();
        this.mMainHandler = new MainHandler(this.mActivity.getMainLooper());
    }

    @Override // com.mediatek.camera.ICameraAddition
    public void open() {
        if (this.mIsMtkCameraApServiceLaunched) {
            ICameraDeviceManager.ICameraDevice cameraDevice = this.mICameraDeviceManager.getCameraDevice(this.mICameraDeviceManager.getCurrentCameraId());
            if (cameraDevice == null) {
                Log.m36w("RemoteCameraAddition", "cameraDevice is null, return.");
                return;
            }
            cameraDevice.getParameters().setPreviewFormat(17);
            if (this.mCameraService == null) {
                bindCameraService();
            }
            this.mOpened = true;
        }
    }

    @Override // com.mediatek.camera.addition.CameraAddition, com.mediatek.camera.ICameraAddition
    public boolean isOpen() {
        return this.mOpened;
    }

    @Override // com.mediatek.camera.ICameraAddition
    public boolean isSupport() {
        Log.m34i("RemoteCameraAddition", "[isSupport], mIsMtkCameraApServiceLaunched:" + this.mIsMtkCameraApServiceLaunched);
        return this.mIsMtkCameraApServiceLaunched;
    }

    @Override // com.mediatek.camera.addition.CameraAddition, com.mediatek.camera.ICameraAddition
    public void pause() {
        clearPreviewCallback();
        if (this.mCameraService != null) {
            Message messageObtain = Message.obtain();
            messageObtain.what = 103;
            sendMessageToService(messageObtain);
        }
    }

    @Override // com.mediatek.camera.addition.CameraAddition, com.mediatek.camera.ICameraAddition
    public void destory() {
        Log.m31d("RemoteCameraAddition", "[destory]...");
        if (this.mCameraService != null) {
            unBindCameraService();
        }
    }

    @Override // com.mediatek.camera.ICameraAddition
    public void close() {
        clearPreviewCallback();
        this.mOpened = false;
    }

    @Override // com.mediatek.camera.addition.CameraAddition, com.mediatek.camera.ICameraAddition
    public boolean execute(ICameraMode.ActionType actionType, Object... objArr) {
        switch (m666getcommediatekcameraICameraMode$ActionTypeSwitchesValues()[actionType.ordinal()]) {
            case 1:
                this.mOrientation = ((Integer) objArr[0]).intValue();
                onOrientationChanged(this.mOrientation);
            default:
                return false;
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    @Override // com.mediatek.camera.addition.CameraAddition, com.mediatek.camera.ICameraAddition
    public boolean execute(ICameraAddition.AdditionActionType additionActionType, Object... objArr) {
        switch (m22xd5d340f0()[additionActionType.ordinal()]) {
            case 1:
                this.mMainHandler.sendEmptyMessage(0);
                return false;
            case 2:
                if (this.mIsMtkCameraApServiceLaunched) {
                    takePicture();
                    return true;
                }
                return false;
            default:
                return false;
        }
    }

    @Override // com.mediatek.camera.addition.CameraAddition, com.mediatek.camera.ICameraAddition
    public void setListener(ICameraAddition.Listener listener) {
        this.mModeListener = listener;
    }

    private void bindCameraService() {
        this.mHasNotifyParameterReady = false;
        this.mActivity.bindService(new Intent(this.mActivity, (Class<?>) MtkCameraService.class), this.mCameraConnection, 1);
    }

    private void unBindCameraService() {
        this.mActivity.unbindService(this.mCameraConnection);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendMessageToService(Message message) {
        if (this.mCameraService != null) {
            try {
                if (!this.mHasNotifyParameterReady) {
                    Message messageObtain = Message.obtain();
                    ICameraDeviceManager.ICameraDevice cameraDevice = this.mICameraDeviceManager.getCameraDevice(this.mICameraDeviceManager.getCurrentCameraId());
                    messageObtain.what = 100;
                    messageObtain.obj = cameraDevice.getParameters();
                    this.mCameraService.sendMessage(messageObtain);
                    this.mHasNotifyParameterReady = true;
                }
                Log.m31d("RemoteCameraAddition", "msg:" + message.what);
                this.mCameraService.sendMessage(message);
            } catch (Exception e) {
                Log.m32e("RemoteCameraAddition", "sendMessageToService exception");
            }
        }
    }

    private boolean isMtkCameraApServiceLaunched(Context context) throws SecurityException {
        List<ActivityManager.RunningServiceInfo> runningServices = ((ActivityManager) context.getSystemService("activity")).getRunningServices(Integer.MAX_VALUE);
        if (runningServices.size() <= 0) {
            return false;
        }
        for (int i = 0; i < runningServices.size(); i++) {
            if ("com.mediatek.camera.addition.remotecamera.service.MtkCameraService".equals(runningServices.get(i).service.getClassName())) {
                Log.m31d("RemoteCameraAddition", "isMtkCameraApServiceLaunched true");
                return true;
            }
        }
        return false;
    }

    private int getBufferSize() {
        ICameraDeviceManager.ICameraDevice cameraDevice = this.mICameraDeviceManager.getCameraDevice(this.mICameraDeviceManager.getCurrentCameraId());
        Camera.Size previewSize = cameraDevice.getParameters().getPreviewSize();
        int previewFormat = cameraDevice.getParameters().getPreviewFormat();
        return (ImageFormat.getBitsPerPixel(previewFormat) * (previewSize.height * previewSize.width)) / 8;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setPreviewCallback() {
        ICameraDeviceManager.ICameraDevice cameraDevice = this.mICameraDeviceManager.getCameraDevice(this.mICameraDeviceManager.getCurrentCameraId());
        if (cameraDevice == null) {
            Log.m36w("RemoteCameraAddition", "[setPreviewCallback], cameraDevice is null");
            return;
        }
        int bufferSize = getBufferSize();
        Log.m31d("RemoteCameraAddition", "[setPreviewCallback], bufferSize:" + bufferSize);
        for (int i = 0; i < 3; i++) {
            cameraDevice.addCallbackBuffer(new byte[bufferSize]);
        }
        cameraDevice.setPreviewCallbackWithBuffer(this.mPreviewCallback);
    }

    private void clearPreviewCallback() {
        Log.m31d("RemoteCameraAddition", "[clearPreviewCallback]...");
        ICameraDeviceManager.ICameraDevice cameraDevice = this.mICameraDeviceManager.getCameraDevice(this.mICameraDeviceManager.getCurrentCameraId());
        if (cameraDevice == null) {
            Log.m36w("RemoteCameraAddition", "[clearPreviewCallback], cameraDevice is null");
        } else {
            cameraDevice.setPreviewCallbackWithBuffer(null);
        }
    }

    private void onOrientationChanged(int i) {
        Log.m31d("RemoteCameraAddition", "[onOrientationChanged], orientation:" + i);
        Message message = new Message();
        message.what = 104;
        message.arg1 = i;
        sendMessageToService(message);
    }

    private void takePicture() {
        this.mICameraDeviceManager.getCameraDevice(this.mICameraDeviceManager.getCurrentCameraId()).takePicture(null, null, null, this.mJpegPictureCallback);
        this.mICameraAppUi.setViewState(ICameraAppUi.ViewState.VIEW_STATE_CAPTURE);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getDisplayOrientation(int i, int i2) {
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        Camera.getCameraInfo(i2, cameraInfo);
        if (cameraInfo.facing == 1) {
            return (360 - ((cameraInfo.orientation + i) % 360)) % 360;
        }
        return ((cameraInfo.orientation - i) + 360) % 360;
    }

    private class MainHandler extends Handler {
        public MainHandler(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            switch (message.what) {
                case 0:
                    if (RemoteCameraAddition.this.mIsMtkCameraApServiceLaunched) {
                        RemoteCameraAddition.this.setPreviewCallback();
                        break;
                    }
                    break;
            }
        }
    }
}
