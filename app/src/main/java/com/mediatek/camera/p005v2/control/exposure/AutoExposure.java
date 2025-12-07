package com.mediatek.camera.p005v2.control.exposure;

import android.app.Activity;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.view.ViewGroup;
import com.mediatek.camera.debug.LogHelper;
import com.mediatek.camera.p005v2.control.IControl$IAaaListener;
import com.mediatek.camera.p005v2.module.ModuleListener;
import com.mediatek.camera.p005v2.setting.ISettingServant;
import com.mediatek.camera.p005v2.setting.SettingConvertor;
import java.util.ArrayList;
import java.util.Map;

/* loaded from: classes.dex */
public class AutoExposure implements IExposure, ISettingServant.ISettingChangedListener {
    private static final LogHelper.Tag TAG = new LogHelper.Tag(AutoExposure.class.getSimpleName());
    private Activity mActivity;
    private final IControl$IAaaListener mIaaaListener;
    private String mSensitivity;
    private final ISettingServant mSettingServant;
    private ArrayList<String> mCaredSettingChangedKeys = new ArrayList<>();
    private int mAEMode = 1;
    private int mFlashMode = 0;
    private String mExposureCompensation = null;
    private int mAntiBandingMode = 3;
    private boolean mNeedAePretrigger = false;
    private boolean mAePreTriggerAndCaptureEnabled = false;
    private boolean mAePreTriggerRequestProcessed = false;

    public AutoExposure(ISettingServant iSettingServant, IControl$IAaaListener iControl$IAaaListener) {
        this.mIaaaListener = iControl$IAaaListener;
        this.mSettingServant = iSettingServant;
    }

    @Override // com.mediatek.camera.p005v2.control.exposure.IExposure
    public void open(Activity activity, ViewGroup viewGroup, boolean z) {
        this.mActivity = activity;
        updateCaredSettingChangedKeys();
        this.mSettingServant.registerSettingChangedListener(this, this.mCaredSettingChangedKeys, 1);
    }

    @Override // com.mediatek.camera.p005v2.control.exposure.IExposure
    public void resume() {
        this.mAEMode = 1;
        this.mFlashMode = 0;
        this.mExposureCompensation = null;
        this.mNeedAePretrigger = false;
        this.mAePreTriggerAndCaptureEnabled = false;
        this.mAePreTriggerRequestProcessed = false;
    }

    @Override // com.mediatek.camera.p005v2.control.exposure.IExposure
    public void pause() {
    }

    @Override // com.mediatek.camera.p005v2.control.exposure.IExposure
    public void close() {
        this.mSettingServant.unRegisterSettingChangedListener(this);
    }

    @Override // com.mediatek.camera.p005v2.control.exposure.IExposure
    public void configuringSessionRequest(ModuleListener.RequestType requestType, CaptureRequest.Builder builder, ModuleListener.CaptureType captureType, boolean z) {
        LogHelper.m26i(TAG, "[configuringSessionRequests]+ mAePretriggerRequested:" + this.mNeedAePretrigger);
        updateExposureCompensation();
        updateAeFlashMode(requestType);
        updateAntiBandingMode();
        updateSensitivity();
        builder.set(CaptureRequest.FLASH_MODE, Integer.valueOf(this.mFlashMode));
        builder.set(CaptureRequest.CONTROL_AE_MODE, Integer.valueOf(this.mAEMode));
        if (this.mExposureCompensation != null && this.mAEMode != 0) {
            builder.set(CaptureRequest.CONTROL_AE_EXPOSURE_COMPENSATION, Integer.valueOf(Integer.parseInt(this.mExposureCompensation)));
        }
        builder.set(CaptureRequest.CONTROL_AE_ANTIBANDING_MODE, Integer.valueOf(this.mAntiBandingMode));
        if (this.mSensitivity != null && (!this.mSensitivity.equals("auto"))) {
            builder.set(CaptureRequest.SENSOR_SENSITIVITY, Integer.valueOf(Integer.parseInt(this.mSensitivity)));
        }
        setAePreCaptureTriggerValue(captureType, builder);
        LogHelper.m26i(TAG, "[configuringSessionRequests]- requestType = " + requestType + " AEMode = " + this.mAEMode + " FlashMode = " + this.mFlashMode + " mExposureCompensation = " + this.mExposureCompensation + " mAntiBandingMode = " + this.mAntiBandingMode + " mSensitivity = " + this.mSensitivity);
    }

    @Override // com.mediatek.camera.p005v2.control.exposure.IExposure
    public void onPreviewCaptureStarted(CaptureRequest captureRequest, long j, long j2) {
    }

    @Override // com.mediatek.camera.p005v2.control.exposure.IExposure
    public void onPreviewCaptureCompleted(CaptureRequest captureRequest, TotalCaptureResult totalCaptureResult) {
        checkAeState(captureRequest, totalCaptureResult);
    }

    @Override // com.mediatek.camera.p005v2.control.exposure.IExposure
    public void aePreTriggerAndCapture() {
        LogHelper.m26i(TAG, "[aePreTriggerAndCapture]+");
        String settingValue = this.mSettingServant.getSettingValue("pref_camera_flashmode_key");
        if (!"on".equals(settingValue) && (!"auto".equals(settingValue))) {
            this.mIaaaListener.requestChangeCaptureRequets(false, ModuleListener.RequestType.STILL_CAPTURE, ModuleListener.CaptureType.CAPTURE);
            LogHelper.m26i(TAG, "[aePreTriggerAndCapture]- flash:" + settingValue);
            return;
        }
        this.mNeedAePretrigger = true;
        this.mAePreTriggerAndCaptureEnabled = true;
        this.mIaaaListener.requestChangeCaptureRequets(true, this.mIaaaListener.getRepeatingRequestType(), ModuleListener.CaptureType.CAPTURE);
        this.mIaaaListener.requestChangeCaptureRequets(true, this.mIaaaListener.getRepeatingRequestType(), ModuleListener.CaptureType.REPEATING_REQUEST);
        LogHelper.m26i(TAG, "[aePreTriggerAndCapture]-");
    }

    @Override // com.mediatek.camera.v2.setting.ISettingServant.ISettingChangedListener
    public void onSettingChanged(Map<String, String> map) {
        int i = 0;
        String[] strArr = {"pref_camera_exposure_key", "pref_camera_flashmode_key", "pref_camera_antibanding_key", "pref_camera_iso_key"};
        while (true) {
            int i2 = i;
            if (i2 < strArr.length) {
                if (map.get(strArr[i2]) != null) {
                    requestChangeCaptureRequets();
                }
                i = i2 + 1;
            } else {
                return;
            }
        }
    }

    private void updateCaredSettingChangedKeys() {
        for (String str : new String[]{"pref_camera_exposure_key", "pref_camera_flashmode_key", "pref_camera_antibanding_key", "pref_camera_iso_key"}) {
            if (str != null && (!this.mCaredSettingChangedKeys.contains(str))) {
                this.mCaredSettingChangedKeys.add(str);
            }
        }
    }

    private void updateExposureCompensation() {
        String settingValue = this.mSettingServant.getSettingValue("pref_camera_exposure_key");
        LogHelper.m26i(TAG, "[updateExposureCompensation]+ EV=" + settingValue);
        if (settingValue != null && (!settingValue.equals(this.mExposureCompensation))) {
            this.mExposureCompensation = settingValue;
        }
        LogHelper.m26i(TAG, "[updateExposureCompensation]- ");
    }

    private void updateAeFlashMode(ModuleListener.RequestType requestType) {
        String settingValue = this.mSettingServant.getSettingValue("pref_camera_flashmode_key");
        LogHelper.m26i(TAG, "updateAeFlashMode flash from setting" + settingValue);
        if (settingValue == null) {
            return;
        }
        if ("on".equalsIgnoreCase(settingValue)) {
            if (isRecordingRequestType(requestType)) {
                this.mAEMode = 1;
                this.mFlashMode = 2;
                return;
            } else {
                this.mAEMode = 3;
                this.mFlashMode = 0;
                return;
            }
        }
        if ("auto".equalsIgnoreCase(settingValue)) {
            this.mAEMode = 2;
            this.mFlashMode = 0;
        } else {
            this.mAEMode = 1;
            this.mFlashMode = 0;
        }
    }

    private boolean isRecordingRequestType(ModuleListener.RequestType requestType) {
        return requestType == ModuleListener.RequestType.RECORDING || requestType == ModuleListener.RequestType.VIDEO_SNAP_SHOT;
    }

    private void updateAntiBandingMode() {
        int iConvertStringToEnum = SettingConvertor.convertStringToEnum("pref_camera_antibanding_key", this.mSettingServant.getSettingValue("pref_camera_antibanding_key"));
        LogHelper.m23d(TAG, "[updateAntiBandingMode]+ antiBandingMode=" + iConvertStringToEnum);
        this.mAntiBandingMode = iConvertStringToEnum;
        LogHelper.m23d(TAG, "[updateAntiBandingMode]- ");
    }

    private void updateSensitivity() {
        this.mSensitivity = this.mSettingServant.getSettingValue("pref_camera_iso_key");
        LogHelper.m23d(TAG, "[updateSensitivity], mSensitivity=" + this.mSensitivity);
    }

    private void requestChangeCaptureRequets() {
        this.mIaaaListener.requestChangeCaptureRequets(false, this.mIaaaListener.getRepeatingRequestType(), ModuleListener.CaptureType.REPEATING_REQUEST);
    }

    private void setAePreCaptureTriggerValue(ModuleListener.CaptureType captureType, CaptureRequest.Builder builder) {
        if (this.mNeedAePretrigger && captureType == ModuleListener.CaptureType.CAPTURE) {
            builder.set(CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER, 1);
            this.mNeedAePretrigger = false;
        } else {
            builder.set(CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER, 0);
        }
    }

    private void checkAeState(CaptureRequest captureRequest, TotalCaptureResult totalCaptureResult) {
        Integer num = (Integer) captureRequest.get(CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER);
        Integer num2 = (Integer) totalCaptureResult.get(TotalCaptureResult.CONTROL_AE_STATE);
        LogHelper.m26i(TAG, "aeStateCheck aeState:" + num2 + " aePrecaptureTrigger:" + num);
        if (num2 != null && num != null && this.mAePreTriggerAndCaptureEnabled) {
            if (!this.mAePreTriggerRequestProcessed) {
                this.mAePreTriggerRequestProcessed = num.intValue() == 1;
            }
            if (this.mAePreTriggerRequestProcessed) {
                if (num2.intValue() == 2 || num2.intValue() == 4) {
                    this.mActivity.runOnUiThread(new Runnable() { // from class: com.mediatek.camera.v2.control.exposure.AutoExposure.1
                        @Override // java.lang.Runnable
                        public void run() {
                            LogHelper.m26i(AutoExposure.TAG, "Ae pre capture trigger completed submit still capture!");
                            AutoExposure.this.mIaaaListener.requestChangeCaptureRequets(false, ModuleListener.RequestType.STILL_CAPTURE, ModuleListener.CaptureType.CAPTURE);
                        }
                    });
                    this.mAePreTriggerAndCaptureEnabled = false;
                    this.mAePreTriggerRequestProcessed = false;
                }
            }
        }
    }
}
