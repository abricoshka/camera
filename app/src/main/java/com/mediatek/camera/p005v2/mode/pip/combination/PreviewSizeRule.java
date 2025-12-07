package com.mediatek.camera.p005v2.mode.pip.combination;

import com.mediatek.camera.debug.LogHelper;
import com.mediatek.camera.p005v2.setting.ISettingRule;
import com.mediatek.camera.p005v2.setting.ISettingServant;
import com.mediatek.camera.p005v2.setting.SettingCtrl;
import java.util.List;

/* loaded from: classes.dex */
public class PreviewSizeRule implements ISettingRule {
    private static final LogHelper.Tag TAG = new LogHelper.Tag(PreviewSizeRule.class.getSimpleName());
    private String mCurrentCameraId;
    private String mCurrentPictureRatio = null;
    private final SettingCtrl mSettingCtrl;
    private ISettingServant mSettingServant;

    public PreviewSizeRule(SettingCtrl settingCtrl) {
        this.mCurrentCameraId = null;
        this.mSettingCtrl = settingCtrl;
        this.mCurrentCameraId = this.mSettingCtrl.getCurrentCameraId();
        this.mSettingServant = this.mSettingCtrl.getSettingServant(this.mCurrentCameraId);
    }

    @Override // com.mediatek.camera.p005v2.setting.ISettingRule
    public void execute() {
        String currentCameraId = this.mSettingCtrl.getCurrentCameraId();
        boolean z = this.mCurrentCameraId != currentCameraId;
        if (!this.mCurrentCameraId.equals(currentCameraId)) {
            this.mSettingServant = this.mSettingCtrl.getSettingServant(currentCameraId);
            this.mCurrentCameraId = currentCameraId;
        }
        String settingValue = this.mSettingServant.getSettingValue("photo_pip_key");
        LogHelper.m23d(TAG, "pipKeyValue:" + settingValue);
        if (!"on".equalsIgnoreCase(settingValue)) {
            if ("off".equalsIgnoreCase(settingValue)) {
                LogHelper.m26i(TAG, "Exit");
                return;
            }
            return;
        }
        if (!z || this.mCurrentPictureRatio == null) {
            this.mCurrentPictureRatio = this.mSettingServant.getSettingValue("pref_camera_picturesize_ratio_key");
            this.mSettingCtrl.getSettingServant(getAnotherCameraId()).getSettingItem("pref_camera_picturesize_ratio_key").setValue(this.mCurrentPictureRatio);
        } else {
            this.mSettingServant.setSharedPreferencesValue("pref_camera_picturesize_ratio_key", this.mCurrentPictureRatio);
            this.mSettingServant.getSettingItem("pref_camera_picturesize_ratio_key").setValue(this.mCurrentPictureRatio);
        }
        LogHelper.m23d(TAG, "Enter mCurrentPictureRatio:" + this.mCurrentPictureRatio + " mCurrentCameraId: " + this.mCurrentCameraId + " isPipSwitched:" + z);
    }

    @Override // com.mediatek.camera.p005v2.setting.ISettingRule
    public void addLimitation(String str, List<String> list) {
    }

    private String getAnotherCameraId() {
        if ("0".equalsIgnoreCase(this.mCurrentCameraId)) {
            return "1";
        }
        return "0";
    }
}
