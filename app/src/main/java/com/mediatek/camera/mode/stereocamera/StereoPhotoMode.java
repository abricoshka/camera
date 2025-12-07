package com.mediatek.camera.mode.stereocamera;

import android.hardware.Camera;
import android.net.Uri;
import android.os.SystemProperties;
import android.util.Log;
import com.mediatek.camera.ICameraAddition;
import com.mediatek.camera.ICameraContext;
import com.mediatek.camera.ICameraMode;
import com.mediatek.camera.mode.PhotoMode;
import com.mediatek.camera.mode.stereocamera.settingrule.StereoZsdRule;
import com.mediatek.camera.platform.ICameraAppUi;
import com.mediatek.camera.platform.ICameraDeviceManager;
import com.mediatek.camera.platform.IFileSaver;
import com.mediatek.camera.setting.ParametersHelper;

/* loaded from: classes.dex */
public class StereoPhotoMode extends PhotoMode {

    /* renamed from: -com-mediatek-camera-ICameraMode$ActionTypeSwitchesValues, reason: not valid java name */
    private static final /* synthetic */ int[] f109commediatekcameraICameraMode$ActionTypeSwitchesValues = null;
    private IFileSaver.OnFileSavedListener mFileSaverListener;
    private final Camera.PictureCallback mJpegPictureCallback;
    private int mJpegRotation;
    private final Camera.PictureCallback mPostViewCallback;
    private final Camera.ShutterCallback mShutterCallback;
    private StereoGestureListener mStereoGestureListener;
    private final StereoPhotoDataCallback mStereoPhotoDataCallback;
    private final Camera.PictureCallback mUncompressedImageCallback;

    /* renamed from: -getcom-mediatek-camera-ICameraMode$ActionTypeSwitchesValues, reason: not valid java name */
    private static /* synthetic */ int[] m1005getcommediatekcameraICameraMode$ActionTypeSwitchesValues() {
        if (f109commediatekcameraICameraMode$ActionTypeSwitchesValues != null) {
            return f109commediatekcameraICameraMode$ActionTypeSwitchesValues;
        }
        int[] iArr = new int[ICameraMode.ActionType.valuesCustom().length];
        try {
            iArr[ICameraMode.ActionType.ACTION_CANCEL_BUTTON_CLICK.ordinal()] = 3;
        } catch (NoSuchFieldError e) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_CAN_DO_AUTO_FOCUS.ordinal()] = 4;
        } catch (NoSuchFieldError e2) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_DISABLE_VIDEO_RECORD.ordinal()] = 5;
        } catch (NoSuchFieldError e3) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_FACE_DETECTED.ordinal()] = 1;
        } catch (NoSuchFieldError e4) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_NOTIFY_SURFCEVIEW_DESTROYED.ordinal()] = 6;
        } catch (NoSuchFieldError e5) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_NOTIFY_SURFCEVIEW_DISPLAY_IS_READY.ordinal()] = 7;
        } catch (NoSuchFieldError e6) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_OK_BUTTON_CLICK.ordinal()] = 8;
        } catch (NoSuchFieldError e7) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_BACK_KEY_PRESS.ordinal()] = 9;
        } catch (NoSuchFieldError e8) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_CAMERA_CLOSE.ordinal()] = 10;
        } catch (NoSuchFieldError e9) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_CAMERA_OPEN.ordinal()] = 2;
        } catch (NoSuchFieldError e10) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_CAMERA_PARAMETERS_READY.ordinal()] = 11;
        } catch (NoSuchFieldError e11) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_COMPENSATION_CHANGED.ordinal()] = 12;
        } catch (NoSuchFieldError e12) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_CONFIGURATION_CHANGED.ordinal()] = 13;
        } catch (NoSuchFieldError e13) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_FULL_SCREEN_CHANGED.ordinal()] = 14;
        } catch (NoSuchFieldError e14) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_KEY_EVENT_PRESS.ordinal()] = 15;
        } catch (NoSuchFieldError e15) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_LONG_PRESS.ordinal()] = 16;
        } catch (NoSuchFieldError e16) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_MEDIA_EJECT.ordinal()] = 17;
        } catch (NoSuchFieldError e17) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_PREVIEW_BUFFER_SIZE_CHANGED.ordinal()] = 18;
        } catch (NoSuchFieldError e18) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_PREVIEW_DISPLAY_SIZE_CHANGED.ordinal()] = 19;
        } catch (NoSuchFieldError e19) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_RESTORE_SETTINGS.ordinal()] = 20;
        } catch (NoSuchFieldError e20) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_SELFTIMER_STATE.ordinal()] = 21;
        } catch (NoSuchFieldError e21) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_SETTING_BUTTON_CLICK.ordinal()] = 22;
        } catch (NoSuchFieldError e22) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_SINGLE_TAP_UP.ordinal()] = 23;
        } catch (NoSuchFieldError e23) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_START_PREVIEW.ordinal()] = 24;
        } catch (NoSuchFieldError e24) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_STOP_PREVIEW.ordinal()] = 25;
        } catch (NoSuchFieldError e25) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_SURFACE_TEXTURE_READY.ordinal()] = 26;
        } catch (NoSuchFieldError e26) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_USER_INTERACTION.ordinal()] = 27;
        } catch (NoSuchFieldError e27) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ORITATION_CHANGED.ordinal()] = 28;
        } catch (NoSuchFieldError e28) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_PHOTO_SHUTTER_BUTTON_CLICK.ordinal()] = 29;
        } catch (NoSuchFieldError e29) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_PREVIEW_VISIBLE_CHANGED.ordinal()] = 30;
        } catch (NoSuchFieldError e30) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_SET_DISPLAYROTATION.ordinal()] = 31;
        } catch (NoSuchFieldError e31) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_SHUTTER_BUTTON_FOCUS.ordinal()] = 32;
        } catch (NoSuchFieldError e32) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_SHUTTER_BUTTON_LONG_PRESS.ordinal()] = 33;
        } catch (NoSuchFieldError e33) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_SWITCH_DEVICE.ordinal()] = 34;
        } catch (NoSuchFieldError e34) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_VIDEO_SHUTTER_BUTTON_CLICK.ordinal()] = 35;
        } catch (NoSuchFieldError e35) {
        }
        f109commediatekcameraICameraMode$ActionTypeSwitchesValues = iArr;
        return iArr;
    }

    public StereoPhotoMode(ICameraContext iCameraContext) {
        super(iCameraContext);
        this.mStereoPhotoDataCallback = new StereoPhotoDataCallback(this, null);
        this.mJpegRotation = 0;
        this.mUncompressedImageCallback = new Camera.PictureCallback() { // from class: com.mediatek.camera.mode.stereocamera.StereoPhotoMode.1
            @Override // android.hardware.Camera.PictureCallback
            public void onPictureTaken(byte[] bArr, Camera camera) {
                Log.i("StereoPhotoMode", "[UncompressedImageCallback]onCanCapture");
                StereoPhotoMode.this.restartPreview(false);
            }
        };
        this.mFileSaverListener = new IFileSaver.OnFileSavedListener() { // from class: com.mediatek.camera.mode.stereocamera.StereoPhotoMode.2
            @Override // com.mediatek.camera.platform.IFileSaver.OnFileSavedListener
            public void onFileSaved(Uri uri) {
                Log.d("StereoPhotoMode", "[onFileSaved]uri= " + uri);
            }
        };
        this.mJpegPictureCallback = new Camera.PictureCallback() { // from class: com.mediatek.camera.mode.stereocamera.StereoPhotoMode.3
            @Override // android.hardware.Camera.PictureCallback
            public void onPictureTaken(byte[] bArr, Camera camera) {
                Log.d("StereoPhotoMode", "[mJpegPictureCallback]");
                if (StereoPhotoMode.this.mCameraClosed) {
                    Log.i("StereoPhotoMode", "[mJpegPictureCallback] mCameraClosed:" + StereoPhotoMode.this.mCameraClosed);
                    StereoPhotoMode.this.mICameraAppUi.setSwipeEnabled(true);
                    StereoPhotoMode.this.mICameraAppUi.restoreViewState();
                } else {
                    if (bArr == null) {
                        Log.i("StereoPhotoMode", "[mJpegPictureCallback] jpegData is null");
                        StereoPhotoMode.this.mICameraAppUi.setSwipeEnabled(true);
                        StereoPhotoMode.this.mICameraAppUi.restoreViewState();
                        StereoPhotoMode.this.restartPreview(false);
                        return;
                    }
                    StereoPhotoMode.this.mIFocusManager.updateFocusUI();
                    StereoPhotoMode.this.mIFileSaver.savePhotoFile(bArr, null, StereoPhotoMode.this.mCaptureStartTime, StereoPhotoMode.this.mIModuleCtrl.getLocation(), 0, null);
                    Log.d("StereoPhotoMode", "[mJpegPictureCallback] end");
                }
            }
        };
        this.mShutterCallback = new Camera.ShutterCallback() { // from class: com.mediatek.camera.mode.stereocamera.StereoPhotoMode.4
            @Override // android.hardware.Camera.ShutterCallback
            public void onShutter() {
                Log.d("StereoPhotoMode", "[mShutterCallback] mShutterLag = " + (System.currentTimeMillis() - StereoPhotoMode.this.mCaptureStartTime) + "ms");
            }
        };
        this.mPostViewCallback = new Camera.PictureCallback() { // from class: com.mediatek.camera.mode.stereocamera.StereoPhotoMode.5
            @Override // android.hardware.Camera.PictureCallback
            public void onPictureTaken(byte[] bArr, Camera camera) throws Throwable {
                int i;
                int i2;
                Log.d("StereoPhotoMode", "[mPostViewCallback]");
                if (StereoPhotoMode.this.mCameraClosed) {
                    Log.i("StereoPhotoMode", "[mPostViewCallback] mCameraClosed:" + StereoPhotoMode.this.mCameraClosed);
                    return;
                }
                if (bArr == null) {
                    Log.i("StereoPhotoMode", "[mPostViewCallback] postViewData is null");
                    return;
                }
                if (StereoPhotoMode.this.mJpegRotation == 0 || StereoPhotoMode.this.mJpegRotation == 180) {
                    i = StereoPhotoMode.this.mICameraDevice.getParameters().getPreviewSize().height;
                    i2 = StereoPhotoMode.this.mICameraDevice.getParameters().getPreviewSize().width;
                } else {
                    i2 = StereoPhotoMode.this.mICameraDevice.getParameters().getPreviewSize().height;
                    i = StereoPhotoMode.this.mICameraDevice.getParameters().getPreviewSize().width;
                }
                if (StereoPhotoMode.this.isDebugOpened()) {
                    StereoPhotoMode.this.savePostViewBuffer(bArr, "/sdcard/postView.yuv");
                    Log.d("StereoPhotoMode", "previewWidth = " + i2 + ", previewHeight = " + i);
                }
                StereoPhotoMode.this.mICameraAppUi.updateThumbnailViewWithYuv(bArr, i2, i, 0, 17);
                Log.d("StereoPhotoMode", "[mPostViewCallback] end");
            }
        };
        this.mStereoGestureListener = new StereoGestureListener() { // from class: com.mediatek.camera.mode.stereocamera.StereoPhotoMode.6
            @Override // com.mediatek.camera.mode.stereocamera.StereoGestureListener, com.mediatek.camera.platform.ICameraAppUi.GestureListener
            public boolean onSingleTapUp(float f, float f2) {
                return false;
            }

            @Override // com.mediatek.camera.mode.stereocamera.StereoGestureListener, com.mediatek.camera.platform.ICameraAppUi.GestureListener
            public boolean onScale(float f, float f2, float f3) {
                return false;
            }

            @Override // com.mediatek.camera.mode.stereocamera.StereoGestureListener, com.mediatek.camera.platform.ICameraAppUi.GestureListener
            public boolean onScaleBegin(float f, float f2) {
                return false;
            }
        };
        Log.i("StereoPhotoMode", "[StereoPhotoMode]constructor...");
        this.mCameraCategory = new StereoPhotoCategory();
        StereoZsdRule stereoZsdRule = new StereoZsdRule(iCameraContext, 2);
        stereoZsdRule.addLimitation("on", null, null);
        this.mISettingCtrl.addRule("photo_stereo_key", "pref_camera_zsd_key", stereoZsdRule);
        setModeState(ICameraMode.ModeState.STATE_CLOSED);
    }

    @Override // com.mediatek.camera.mode.PhotoMode, com.mediatek.camera.mode.CameraMode, com.mediatek.camera.ICameraMode
    public boolean open() {
        Log.i("StereoPhotoMode", "[openMode] ...");
        super.open();
        this.mICameraAppUi.setGestureListener(this.mStereoGestureListener);
        this.mICameraAppUi.switchShutterType(ICameraAppUi.ShutterButtonType.SHUTTER_TYPE_PHOTO);
        return true;
    }

    @Override // com.mediatek.camera.mode.PhotoMode, com.mediatek.camera.mode.CameraMode, com.mediatek.camera.ICameraMode
    public boolean close() {
        Log.i("StereoPhotoMode", "[closeMode]...");
        this.mICameraAppUi.setGestureListener(null);
        super.close();
        return true;
    }

    @Override // com.mediatek.camera.mode.PhotoMode, com.mediatek.camera.mode.CameraMode, com.mediatek.camera.ICameraMode
    public boolean execute(ICameraMode.ActionType actionType, Object... objArr) {
        if (actionType != ICameraMode.ActionType.ACTION_ORITATION_CHANGED) {
            Log.i("StereoPhotoMode", "[execute]type = " + actionType);
        }
        switch (m1005getcommediatekcameraICameraMode$ActionTypeSwitchesValues()[actionType.ordinal()]) {
            case 2:
                updateDevice();
                this.mCameraClosed = false;
                ParametersHelper.setVsDofMode(this.mICameraDevice.getParameters(), false);
                ParametersHelper.setStereoCaptureMode(this.mICameraDevice.getParameters(), false);
                ParametersHelper.setDenoiseMode(this.mICameraDevice.getParameters(), true);
            case 1:
                return true;
            default:
                return super.execute(actionType, objArr);
        }
    }

    @Override // com.mediatek.camera.mode.CameraMode, com.mediatek.camera.ICameraMode
    public void resume() {
    }

    @Override // com.mediatek.camera.mode.CameraMode, com.mediatek.camera.ICameraMode
    public void pause() {
        Log.i("StereoPhotoMode", "pause");
    }

    @Override // com.mediatek.camera.mode.PhotoMode
    protected Camera.PictureCallback getUncompressedImageCallback() {
        return this.mUncompressedImageCallback;
    }

    @Override // com.mediatek.camera.mode.PhotoMode, com.mediatek.camera.platform.IFocusManager.FocusListener
    public boolean capture() {
        Log.i("StereoPhotoMode", "capture()");
        this.mIFileSaver.init(IFileSaver.FILE_TYPE.JPEG, 0, null, -1);
        this.mJpegRotation = Integer.parseInt(this.mICameraDevice.getParameters().get("rotation"));
        this.mICameraAppUi.showRemaining();
        this.mCaptureStartTime = System.currentTimeMillis();
        this.mCameraCategory.takePicture();
        setModeState(ICameraMode.ModeState.STATE_CAPTURING);
        return true;
    }

    private class StereoPhotoDataCallback implements ICameraDeviceManager.ICameraDevice.StereoDataCallback {
        /* synthetic */ StereoPhotoDataCallback(StereoPhotoMode stereoPhotoMode, StereoPhotoDataCallback stereoPhotoDataCallback) {
            this();
        }

        private StereoPhotoDataCallback() {
        }

        @Override // com.mediatek.camera.platform.ICameraDeviceManager.ICameraDevice.StereoDataCallback
        public void onJpsCapture(byte[] bArr) {
        }

        @Override // com.mediatek.camera.platform.ICameraDeviceManager.ICameraDevice.StereoDataCallback
        public void onMaskCapture(byte[] bArr) {
            if (bArr == null) {
                Log.i("StereoPhotoMode", "Mask data is null");
            } else {
                Log.i("StereoPhotoMode", "onMaskCapture maskData:" + bArr.length);
            }
        }

        @Override // com.mediatek.camera.platform.ICameraDeviceManager.ICameraDevice.StereoDataCallback
        public void onDepthMapCapture(byte[] bArr) {
        }

        @Override // com.mediatek.camera.platform.ICameraDeviceManager.ICameraDevice.StereoDataCallback
        public void onClearImageCapture(byte[] bArr) {
        }

        @Override // com.mediatek.camera.platform.ICameraDeviceManager.ICameraDevice.StereoDataCallback
        public void onLdcCapture(byte[] bArr) {
        }

        @Override // com.mediatek.camera.platform.ICameraDeviceManager.ICameraDevice.StereoDataCallback
        public void onN3dCapture(byte[] bArr) {
        }

        @Override // com.mediatek.camera.platform.ICameraDeviceManager.ICameraDevice.StereoDataCallback
        public void onDepthWrapperCapture(byte[] bArr) {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isDebugOpened() {
        boolean z = SystemProperties.getInt("debug.STEREO.enable_verify", 0) == 1;
        Log.i("StereoPhotoMode", "[isDebugOpened]return :" + z);
        return z;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isShot2ShotDebugOpened() {
        boolean z = SystemProperties.getInt("debug.bmdenoise.fasts2s", 0) == 0;
        Log.i("StereoPhotoMode", "[isShot2ShotDebugOpened]return :" + z);
        return z;
    }

    class StereoPhotoCategory extends PhotoMode.CameraCategory {
        public StereoPhotoCategory() {
            super();
        }

        @Override // com.mediatek.camera.mode.PhotoMode.CameraCategory
        public void takePicture() {
            if (!StereoPhotoMode.this.mAdditionManager.execute(ICameraAddition.AdditionActionType.ACTION_TAKEN_PICTURE, new Object[0])) {
                StereoPhotoMode.this.mICameraDevice.setStereoDataCallback(StereoPhotoMode.this.mStereoPhotoDataCallback);
                if (StereoPhotoMode.this.isShot2ShotDebugOpened()) {
                    StereoPhotoMode.this.mICameraDevice.takePicture(StereoPhotoMode.this.mShutterCallback, null, null, StereoPhotoMode.this.mJpegPictureCallback);
                } else {
                    StereoPhotoMode.this.mICameraDevice.takePicture(StereoPhotoMode.this.mShutterCallback, null, StereoPhotoMode.this.mPostViewCallback, StereoPhotoMode.this.mJpegPictureCallback);
                }
                StereoPhotoMode.this.mICameraAppUi.setViewState(ICameraAppUi.ViewState.VIEW_STATE_CAPTURE);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Removed duplicated region for block: B:34:0x0057 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public void savePostViewBuffer(byte[] r5, java.lang.String r6) throws java.lang.Throwable {
        /*
            r4 = this;
            r2 = 0
            java.lang.String r0 = "StereoPhotoMode"
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r3 = "[savePostViewBuffer]path = "
            java.lang.StringBuilder r1 = r1.append(r3)
            java.lang.StringBuilder r1 = r1.append(r6)
            java.lang.String r1 = r1.toString()
            android.util.Log.i(r0, r1)
            java.io.FileOutputStream r1 = new java.io.FileOutputStream     // Catch: java.io.IOException -> L37 java.lang.Throwable -> L53
            r1.<init>(r6)     // Catch: java.io.IOException -> L37 java.lang.Throwable -> L53
            r1.write(r5)     // Catch: java.lang.Throwable -> L66 java.io.IOException -> L68
            r1.close()     // Catch: java.lang.Throwable -> L66 java.io.IOException -> L68
            if (r1 == 0) goto L2b
            r1.close()     // Catch: java.io.IOException -> L2c
        L2b:
            return
        L2c:
            r0 = move-exception
            java.lang.String r1 = "StereoPhotoMode"
            java.lang.String r2 = "[savePostViewBuffer]ioexception:"
            android.util.Log.e(r1, r2, r0)
            goto L2b
        L37:
            r0 = move-exception
            r1 = r2
        L39:
            java.lang.String r2 = "StereoPhotoMode"
            java.lang.String r3 = "[savePostViewBuffer]Failed to write image,exception:"
            android.util.Log.e(r2, r3, r0)     // Catch: java.lang.Throwable -> L66
            if (r1 == 0) goto L2b
            r1.close()     // Catch: java.io.IOException -> L48
            goto L2b
        L48:
            r0 = move-exception
            java.lang.String r1 = "StereoPhotoMode"
            java.lang.String r2 = "[savePostViewBuffer]ioexception:"
            android.util.Log.e(r1, r2, r0)
            goto L2b
        L53:
            r0 = move-exception
            r1 = r2
        L55:
            if (r1 == 0) goto L5a
            r1.close()     // Catch: java.io.IOException -> L5b
        L5a:
            throw r0
        L5b:
            r1 = move-exception
            java.lang.String r2 = "StereoPhotoMode"
            java.lang.String r3 = "[savePostViewBuffer]ioexception:"
            android.util.Log.e(r2, r3, r1)
            goto L5a
        L66:
            r0 = move-exception
            goto L55
        L68:
            r0 = move-exception
            goto L39
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mediatek.camera.mode.stereocamera.StereoPhotoMode.savePostViewBuffer(byte[], java.lang.String):void");
    }
}
