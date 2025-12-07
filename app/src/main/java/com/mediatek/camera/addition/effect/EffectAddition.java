package com.mediatek.camera.addition.effect;

import android.hardware.Camera;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.FrameMetricsAggregator;
import android.view.Surface;
import com.mediatek.camera.ICameraAddition;
import com.mediatek.camera.ICameraContext;
import com.mediatek.camera.ICameraMode;
import com.mediatek.camera.ISettingCtrl;
import com.mediatek.camera.addition.CameraAddition;
import com.mediatek.camera.addition.effect.Effect;
import com.mediatek.camera.addition.effect.EffectView;
import com.mediatek.camera.platform.ICameraAppUi;
import com.mediatek.camera.platform.ICameraDeviceManager;
import com.mediatek.camera.platform.ICameraView;
import com.mediatek.camera.platform.Parameters;
import com.mediatek.camera.setting.ParametersHelper;
import com.mediatek.camera.setting.preference.ListPreference;
import com.mediatek.camera.util.Log;
import java.util.List;

/* loaded from: classes.dex */
public class EffectAddition extends CameraAddition {

    /* renamed from: -com-mediatek-camera-ICameraAddition$AdditionActionTypeSwitchesValues */
    private static final /* synthetic */ int[] f69x29f76b14 = null;

    /* renamed from: -com-mediatek-camera-ICameraMode$ActionTypeSwitchesValues, reason: not valid java name */
    private static final /* synthetic */ int[] f99commediatekcameraICameraMode$ActionTypeSwitchesValues = null;
    private static final String[] MAX_SIZE_SUPPORT_BY_EFFECT = {"800x600", "960x540", "800x480", "900x600"};
    private State mCurrentState;
    private String mCurrrentFocusMode;
    private Effect mEffect;
    private ListPreference mEffectPreference;
    private ICameraView mICameraView;
    private boolean mIs3dnrOn;
    private boolean mIsFaceDetectionOpened;
    private MainHandler mMainHandler;
    private ICameraAddition.Listener mModeListener;
    private int mNormalPreviewHeight;
    private int mNormalPreviewWidth;
    private boolean mShowEffects;

    /* renamed from: -getcom-mediatek-camera-ICameraAddition$AdditionActionTypeSwitchesValues */
    private static /* synthetic */ int[] m21xd5d340f0() {
        if (f69x29f76b14 != null) {
            return f69x29f76b14;
        }
        int[] iArr = new int[ICameraAddition.AdditionActionType.valuesCustom().length];
        try {
            iArr[ICameraAddition.AdditionActionType.ACTION_EFFECT_CLICK.ordinal()] = 1;
        } catch (NoSuchFieldError e) {
        }
        try {
            iArr[ICameraAddition.AdditionActionType.ACTION_ON_START_PREVIEW.ordinal()] = 10;
        } catch (NoSuchFieldError e2) {
        }
        try {
            iArr[ICameraAddition.AdditionActionType.ACTION_ON_STOP_PREVIEW.ordinal()] = 11;
        } catch (NoSuchFieldError e3) {
        }
        try {
            iArr[ICameraAddition.AdditionActionType.ACTION_ON_SWITCH_PIP.ordinal()] = 12;
        } catch (NoSuchFieldError e4) {
        }
        try {
            iArr[ICameraAddition.AdditionActionType.ACTION_TAKEN_PICTURE.ordinal()] = 13;
        } catch (NoSuchFieldError e5) {
        }
        f69x29f76b14 = iArr;
        return iArr;
    }

    /* renamed from: -getcom-mediatek-camera-ICameraMode$ActionTypeSwitchesValues, reason: not valid java name */
    private static /* synthetic */ int[] m633getcommediatekcameraICameraMode$ActionTypeSwitchesValues() {
        if (f99commediatekcameraICameraMode$ActionTypeSwitchesValues != null) {
            return f99commediatekcameraICameraMode$ActionTypeSwitchesValues;
        }
        int[] iArr = new int[ICameraMode.ActionType.valuesCustom().length];
        try {
            iArr[ICameraMode.ActionType.ACTION_CANCEL_BUTTON_CLICK.ordinal()] = 10;
        } catch (NoSuchFieldError e) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_CAN_DO_AUTO_FOCUS.ordinal()] = 11;
        } catch (NoSuchFieldError e2) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_DISABLE_VIDEO_RECORD.ordinal()] = 12;
        } catch (NoSuchFieldError e3) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_FACE_DETECTED.ordinal()] = 13;
        } catch (NoSuchFieldError e4) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_NOTIFY_SURFCEVIEW_DESTROYED.ordinal()] = 14;
        } catch (NoSuchFieldError e5) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_NOTIFY_SURFCEVIEW_DISPLAY_IS_READY.ordinal()] = 15;
        } catch (NoSuchFieldError e6) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_OK_BUTTON_CLICK.ordinal()] = 16;
        } catch (NoSuchFieldError e7) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_BACK_KEY_PRESS.ordinal()] = 1;
        } catch (NoSuchFieldError e8) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_CAMERA_CLOSE.ordinal()] = 2;
        } catch (NoSuchFieldError e9) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_CAMERA_OPEN.ordinal()] = 17;
        } catch (NoSuchFieldError e10) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_CAMERA_PARAMETERS_READY.ordinal()] = 18;
        } catch (NoSuchFieldError e11) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_COMPENSATION_CHANGED.ordinal()] = 3;
        } catch (NoSuchFieldError e12) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_CONFIGURATION_CHANGED.ordinal()] = 4;
        } catch (NoSuchFieldError e13) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_FULL_SCREEN_CHANGED.ordinal()] = 5;
        } catch (NoSuchFieldError e14) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_KEY_EVENT_PRESS.ordinal()] = 19;
        } catch (NoSuchFieldError e15) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_LONG_PRESS.ordinal()] = 20;
        } catch (NoSuchFieldError e16) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_MEDIA_EJECT.ordinal()] = 21;
        } catch (NoSuchFieldError e17) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_PREVIEW_BUFFER_SIZE_CHANGED.ordinal()] = 22;
        } catch (NoSuchFieldError e18) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_ON_PREVIEW_DISPLAY_SIZE_CHANGED.ordinal()] = 6;
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
            iArr[ICameraMode.ActionType.ACTION_ORITATION_CHANGED.ordinal()] = 31;
        } catch (NoSuchFieldError e28) {
        }
        try {
            iArr[ICameraMode.ActionType.ACTION_PHOTO_SHUTTER_BUTTON_CLICK.ordinal()] = 7;
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
            iArr[ICameraMode.ActionType.ACTION_VIDEO_SHUTTER_BUTTON_CLICK.ordinal()] = 8;
        } catch (NoSuchFieldError e35) {
        }
        f99commediatekcameraICameraMode$ActionTypeSwitchesValues = iArr;
        return iArr;
    }

    private enum State {
        STATE_OPEN,
        STATE_CLOSE;

        /* renamed from: values, reason: to resolve conflict with enum method */
        public static State[] valuesCustom() {
            return values();
        }
    }

    public EffectAddition(ICameraContext iCameraContext) {
        super(iCameraContext);
        this.mNormalPreviewWidth = 0;
        this.mNormalPreviewHeight = 0;
        this.mShowEffects = false;
        this.mCurrentState = State.STATE_CLOSE;
        this.mCurrrentFocusMode = null;
        this.mIsFaceDetectionOpened = false;
        this.mIs3dnrOn = false;
        EffectListener effectListener = new EffectListener(this, null);
        this.mEffect = new Effect(iCameraContext);
        this.mEffect.setListener(effectListener);
        this.mICameraView = this.mICameraAppUi.getCameraView(ICameraAppUi.SpecViewType.ADDITION_EFFECT);
        this.mICameraView.init(iCameraContext.getActivity(), this.mICameraAppUi, this.mIModuleCtrl);
        this.mICameraView.setListener(new EffectViewListener(this));
        this.mMainHandler = new MainHandler(this.mActivity.getMainLooper());
    }

    @Override // com.mediatek.camera.ICameraAddition
    public void open() {
        Log.m31d("EffectAddition", "[open]...");
        this.mCurrentState = State.STATE_OPEN;
    }

    @Override // com.mediatek.camera.ICameraAddition
    public boolean isSupport() {
        updateCameraDevice();
        updateFocusManager();
        return true;
    }

    @Override // com.mediatek.camera.addition.CameraAddition, com.mediatek.camera.ICameraAddition
    public void pause() {
        Log.m31d("EffectAddition", "[pause]...");
        this.mShowEffects = false;
        this.mICameraView.update(1, false, 0);
    }

    @Override // com.mediatek.camera.addition.CameraAddition, com.mediatek.camera.ICameraAddition
    public void destory() {
        Log.m31d("EffectAddition", "[destory]...");
        if (this.mEffect != null) {
            this.mEffect.release();
        }
    }

    @Override // com.mediatek.camera.ICameraAddition
    public void close() {
        Log.m31d("EffectAddition", "[close]...");
        this.mCurrentState = State.STATE_CLOSE;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    @Override // com.mediatek.camera.addition.CameraAddition, com.mediatek.camera.ICameraAddition
    public boolean execute(ICameraMode.ActionType actionType, Object... objArr) {
        Log.m31d("EffectAddition", "[execute]type = " + actionType);
        Message messageObtainMessage = this.mMainHandler.obtainMessage();
        switch (m633getcommediatekcameraICameraMode$ActionTypeSwitchesValues()[actionType.ordinal()]) {
            case 1:
                messageObtainMessage.what = 1;
                boolean z = this.mShowEffects;
                this.mMainHandler.sendMessage(messageObtainMessage);
                return z;
            case 2:
                messageObtainMessage.what = 4;
                this.mMainHandler.sendMessage(messageObtainMessage);
                return false;
            case 3:
                messageObtainMessage.what = 0;
                messageObtainMessage.arg1 = ((Integer) objArr[0]).intValue();
                this.mMainHandler.sendMessage(messageObtainMessage);
                return false;
            case 4:
                messageObtainMessage.what = 8;
                this.mMainHandler.sendMessage(messageObtainMessage);
                return false;
            case 5:
                messageObtainMessage.what = 7;
                messageObtainMessage.obj = objArr[0];
                this.mMainHandler.sendMessage(messageObtainMessage);
                return false;
            case 6:
                messageObtainMessage.what = 2;
                messageObtainMessage.arg1 = ((Integer) objArr[0]).intValue();
                messageObtainMessage.arg2 = ((Integer) objArr[1]).intValue();
                this.mMainHandler.sendMessage(messageObtainMessage);
                return false;
            case FrameMetricsAggregator.DELAY_INDEX /* 7 */:
                messageObtainMessage.what = 5;
                this.mMainHandler.sendMessage(messageObtainMessage);
                return false;
            case 8:
                messageObtainMessage.what = 6;
                this.mMainHandler.sendMessage(messageObtainMessage);
                return false;
            default:
                return false;
        }
    }

    @Override // com.mediatek.camera.addition.CameraAddition, com.mediatek.camera.ICameraAddition
    public boolean execute(ICameraAddition.AdditionActionType additionActionType, Object... objArr) throws NumberFormatException {
        Log.m31d("EffectAddition", "[execute]type = " + additionActionType);
        switch (m21xd5d340f0()[additionActionType.ordinal()]) {
            case 1:
                showEffect();
                break;
        }
        return false;
    }

    @Override // com.mediatek.camera.addition.CameraAddition, com.mediatek.camera.ICameraAddition
    public void setListener(ICameraAddition.Listener listener) {
        this.mModeListener = listener;
    }

    private class EffectViewListener implements EffectView.Listener {
        private EffectAddition mEffectAddition;

        public EffectViewListener(EffectAddition effectAddition) {
            this.mEffectAddition = effectAddition;
        }

        @Override // com.mediatek.camera.addition.effect.EffectView.Listener
        public void onInitialize() {
            EffectAddition.this.mEffect.onInitialize();
        }

        @Override // com.mediatek.camera.addition.effect.EffectView.Listener
        public void onSurfaceAvailable(Surface surface, int i, int i2, int i3) {
            EffectAddition.this.mEffect.onSurfaceAvailable(surface, i, i2, i3);
        }

        @Override // com.mediatek.camera.addition.effect.EffectView.Listener
        public void onUpdateEffect(int i, int i2) {
            EffectAddition.this.mEffect.onUpdateEffect(i, i2);
        }

        @Override // com.mediatek.camera.addition.effect.EffectView.Listener
        public void onRelease() {
            EffectAddition.this.mEffect.onRelease();
        }

        @Override // com.mediatek.camera.addition.effect.EffectView.Listener
        public void onItemClick(String str) {
            if (EffectAddition.this.mICameraDevice.getParameters() != null && (!r0.getColorEffect().equals(str))) {
                Log.m31d("EffectAddition", "effect selected:" + str);
                ISettingCtrl settingController = EffectAddition.this.mICameraContext.getSettingController();
                if (settingController != null) {
                    settingController.onSettingChanged("pref_camera_coloreffect_key", str);
                    EffectAddition.this.mICameraDevice.applyParameters();
                }
            }
            this.mEffectAddition.hideEffect(true, 3000);
        }

        @Override // com.mediatek.camera.addition.effect.EffectView.Listener
        public void hideEffect(boolean z, int i) {
            this.mEffectAddition.hideEffect(z, i);
        }
    }

    private class EffectListener implements Effect.Listener {
        /* synthetic */ EffectListener(EffectAddition effectAddition, EffectListener effectListener) {
            this();
        }

        private EffectListener() {
        }

        @Override // com.mediatek.camera.addition.effect.Effect.Listener
        public void onEffectsDone() {
            EffectAddition.this.mICameraView.update(3, new Object[0]);
        }
    }

    private void showEffect() throws NumberFormatException {
        updateCameraDevice();
        Log.m31d("EffectAddition", "[showEffect]mShowEffects = " + this.mShowEffects + ", mICameraDevice:" + this.mICameraDevice);
        if (this.mICameraDevice != null && !this.mShowEffects) {
            this.mShowEffects = true;
            this.mICameraAppUi.setSwipeEnabled(false);
            this.mEffectPreference = this.mISettingCtrl.getListPreference("pref_camera_coloreffect_key");
            setEffectParameters(this.mICameraDevice);
            this.mEffect.onReceivePreviewFrame(true);
            this.mICameraView.update(0, this.mEffectPreference, Boolean.valueOf(this.mICameraDeviceManager.getCurrentCameraId() == this.mICameraDeviceManager.getFrontCameraId()));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void hideEffect(boolean z, int i) {
        Log.m31d("EffectAddition", "[hideEffect], mShowEffects:" + this.mShowEffects);
        if (this.mShowEffects) {
            this.mShowEffects = false;
            this.mICameraAppUi.setSwipeEnabled(true);
            this.mEffect.onReceivePreviewFrame(false);
            resetParameters(this.mICameraDevice);
            this.mICameraView.update(1, Boolean.valueOf(z), Integer.valueOf(i));
        }
    }

    private void setEffectParameters(ICameraDeviceManager.ICameraDevice iCameraDevice) throws NumberFormatException {
        Parameters parameters = iCameraDevice.getParameters();
        int currentCameraId = this.mICameraDeviceManager.getCurrentCameraId();
        if (parameters != null) {
            if (parameters.getColorEffect() != "none") {
                parameters.setColorEffect("none");
            }
            Camera.Size previewSize = parameters.getPreviewSize();
            this.mNormalPreviewWidth = previewSize.width;
            this.mNormalPreviewHeight = previewSize.height;
            Camera.Size effectPreviewSize = getEffectPreviewSize(parameters, previewSize);
            parameters.setPreviewSize(effectPreviewSize.width, effectPreviewSize.height);
            if (this.mModeListener != null) {
                this.mModeListener.restartPreview(true);
            }
            this.mICameraAppUi.setViewState(ICameraAppUi.ViewState.VIEW_STATE_LOMOEFFECT_SETTING);
            this.mCurrrentFocusMode = parameters.getFocusMode();
            if (parameters.getSupportedFocusModes().contains("infinity")) {
                parameters.setFocusMode("infinity");
            }
            if ("on".equals(this.mISettingCtrl.getSettingValue("pref_face_detect_key"))) {
                this.mIsFaceDetectionOpened = true;
                this.mIModuleCtrl.stopFaceDetection();
            }
            if ("on".equals(this.mISettingCtrl.getSettingValue("pref_video_3dnr_key"))) {
                this.mIs3dnrOn = true;
                ParametersHelper.setParametersValue(parameters, currentCameraId, "pref_video_3dnr_key", "off");
            }
            iCameraDevice.applyParameters();
        }
    }

    private void resetParameters(ICameraDeviceManager.ICameraDevice iCameraDevice) {
        Parameters parameters = iCameraDevice.getParameters();
        int currentCameraId = this.mICameraDeviceManager.getCurrentCameraId();
        if (parameters != null) {
            parameters.setPreviewSize(this.mNormalPreviewWidth, this.mNormalPreviewHeight);
            parameters.setColorEffect(this.mEffectPreference.getValue());
            if (this.mModeListener != null) {
                this.mModeListener.restartPreview(true);
            }
            parameters.setFocusMode(this.mCurrrentFocusMode);
            if (this.mIsFaceDetectionOpened) {
                this.mIsFaceDetectionOpened = false;
                this.mIModuleCtrl.startFaceDetection();
            }
            if (this.mIs3dnrOn) {
                this.mIs3dnrOn = false;
                ParametersHelper.setParametersValue(parameters, currentCameraId, "pref_video_3dnr_key", "on");
            }
            iCameraDevice.applyParameters();
        }
    }

    private Camera.Size getEffectPreviewSize(Parameters parameters, Camera.Size size) throws NumberFormatException {
        int i = size.width;
        int i2 = size.height;
        double d = i / i2;
        int i3 = 0;
        int i4 = 0;
        for (int i5 = 0; i5 < MAX_SIZE_SUPPORT_BY_EFFECT.length; i5++) {
            String str = MAX_SIZE_SUPPORT_BY_EFFECT[i5];
            int iIndexOf = str.indexOf(120);
            if (iIndexOf != -1) {
                int i6 = Integer.parseInt(str.substring(0, iIndexOf));
                int i7 = Integer.parseInt(str.substring(iIndexOf + 1));
                if (i6 != 0 && i7 != 0 && Math.abs((i6 / i7) - d) <= 0.02d) {
                    i4 = i7;
                    i3 = i6;
                }
            }
        }
        if (i * i2 > i3 * i4 || i % 32 != 0) {
            List<Camera.Size> supportedPreviewSizes = parameters.getSupportedPreviewSizes();
            int size2 = supportedPreviewSizes.size() - 1;
            while (true) {
                if (size2 < 0) {
                    break;
                }
                Camera.Size size3 = supportedPreviewSizes.get(size2);
                int i8 = size3.width;
                int i9 = size3.height;
                double d2 = i8 / i9;
                if (i9 * i8 <= i3 * i4 && Math.abs(d2 - d) <= 0.001d && i8 % 32 == 0) {
                    size = size3;
                    break;
                }
                size2--;
            }
        }
        Log.m31d("EffectAddition", "[getEffectPreviewSize] preview size:" + size.width + ", " + size.height);
        return size;
    }

    private class MainHandler extends Handler {
        public MainHandler(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            Log.m31d("EffectAddition", "[handleMessage]msg.what = " + message.what);
            switch (message.what) {
                case 0:
                    EffectAddition.this.mICameraView.onOrientationChanged(message.arg1);
                    break;
                case 1:
                    EffectAddition.this.hideEffect(true, 3000);
                    break;
                case 2:
                    EffectAddition.this.mICameraView.update(2, Integer.valueOf(message.arg1), Integer.valueOf(message.arg2));
                    break;
                case 4:
                    EffectAddition.this.mICameraView.update(4, new Object[0]);
                    break;
                case 5:
                    EffectAddition.this.hideEffect(false, 0);
                    break;
                case 6:
                    EffectAddition.this.hideEffect(false, 0);
                    break;
                case FrameMetricsAggregator.DELAY_INDEX /* 7 */:
                    if (!((Boolean) message.obj).booleanValue()) {
                        EffectAddition.this.hideEffect(false, 0);
                        break;
                    }
                    break;
                case 8:
                    EffectAddition.this.hideEffect(false, 0);
                    break;
            }
        }
    }
}
