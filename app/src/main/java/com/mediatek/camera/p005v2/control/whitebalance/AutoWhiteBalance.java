package com.mediatek.camera.p005v2.control.whitebalance;

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
public class AutoWhiteBalance implements IWhiteBalance, ISettingServant.ISettingChangedListener {
    private static final LogHelper.Tag TAG = new LogHelper.Tag(AutoWhiteBalance.class.getSimpleName());
    private final IControl$IAaaListener mIAaaListener;
    private final ISettingServant mSettingServant;
    private ArrayList<String> mCaredSettingChangedKeys = new ArrayList<>();
    private String mAWBMode = null;

    public AutoWhiteBalance(ISettingServant iSettingServant, IControl$IAaaListener iControl$IAaaListener) {
        this.mIAaaListener = iControl$IAaaListener;
        this.mSettingServant = iSettingServant;
    }

    @Override // com.mediatek.camera.p005v2.control.whitebalance.IWhiteBalance
    public void open(Activity activity, ViewGroup viewGroup, boolean z) {
        updateCaredSettingChangedKeys();
        this.mSettingServant.registerSettingChangedListener(this, this.mCaredSettingChangedKeys, 1);
    }

    @Override // com.mediatek.camera.p005v2.control.whitebalance.IWhiteBalance
    public void resume() {
        updateAwbCompensation();
    }

    @Override // com.mediatek.camera.p005v2.control.whitebalance.IWhiteBalance
    public void pause() {
    }

    @Override // com.mediatek.camera.p005v2.control.whitebalance.IWhiteBalance
    public void close() {
        this.mSettingServant.unRegisterSettingChangedListener(this);
    }

    @Override // com.mediatek.camera.p005v2.control.whitebalance.IWhiteBalance
    public void configuringSessionRequest(ModuleListener.RequestType requestType, CaptureRequest.Builder builder, boolean z) {
        LogHelper.m26i(TAG, "[configuringSessionRequests] + camera id:" + this.mSettingServant.getCameraId());
        if (this.mAWBMode != null) {
            builder.set(CaptureRequest.CONTROL_AWB_MODE, Integer.valueOf(SettingConvertor.convertStringToEnum("pref_camera_whitebalance_key", this.mAWBMode)));
        }
        LogHelper.m26i(TAG, "[configuringSessionRequests]- requestType = " + requestType + " AWBMode = " + this.mAWBMode);
    }

    @Override // com.mediatek.camera.p005v2.control.whitebalance.IWhiteBalance
    public void onPreviewCaptureStarted(CaptureRequest captureRequest, long j, long j2) {
    }

    @Override // com.mediatek.camera.p005v2.control.whitebalance.IWhiteBalance
    public void onPreviewCaptureCompleted(CaptureRequest captureRequest, TotalCaptureResult totalCaptureResult) {
    }

    @Override // com.mediatek.camera.v2.setting.ISettingServant.ISettingChangedListener
    public void onSettingChanged(Map<String, String> map) {
        for (String str : new String[]{"pref_camera_whitebalance_key", "pref_camera_id_key"}) {
            if (map.get(str) != null) {
                updateAwbCompensation();
            }
        }
    }

    private void updateCaredSettingChangedKeys() {
        for (String str : new String[]{"pref_camera_whitebalance_key", "pref_camera_id_key"}) {
            if (str != null && (!this.mCaredSettingChangedKeys.contains(str))) {
                this.mCaredSettingChangedKeys.add(str);
            }
        }
    }

    private void updateAwbCompensation() {
        LogHelper.m26i(TAG, "[updateSceneMode]+");
        String settingValue = this.mSettingServant.getSettingValue("pref_camera_whitebalance_key");
        if (settingValue != null && (!settingValue.equals(this.mAWBMode))) {
            this.mAWBMode = settingValue;
            this.mIAaaListener.requestChangeCaptureRequets(false, this.mIAaaListener.getRepeatingRequestType(), ModuleListener.CaptureType.REPEATING_REQUEST);
        }
        LogHelper.m26i(TAG, "[updateSceneMode]- ");
    }
}
