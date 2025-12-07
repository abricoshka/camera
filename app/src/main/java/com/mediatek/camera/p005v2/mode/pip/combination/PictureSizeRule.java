package com.mediatek.camera.p005v2.mode.pip.combination;

import android.util.Size;
import com.mediatek.camera.debug.LogHelper;
import com.mediatek.camera.p005v2.setting.ISettingRule;
import com.mediatek.camera.p005v2.setting.ISettingServant;
import com.mediatek.camera.p005v2.setting.SettingCtrl;
import com.mediatek.camera.p005v2.util.Utils;
import java.util.List;

/* loaded from: classes.dex */
public class PictureSizeRule implements ISettingRule {
    private static final LogHelper.Tag TAG = new LogHelper.Tag(PictureSizeRule.class.getSimpleName());
    private String mCurrentCameraId;
    private final SettingCtrl mSettingCtrl;
    private ISettingServant mSettingServant;

    public PictureSizeRule(SettingCtrl settingCtrl) {
        this.mCurrentCameraId = null;
        this.mSettingCtrl = settingCtrl;
        this.mCurrentCameraId = this.mSettingCtrl.getCurrentCameraId();
        this.mSettingServant = this.mSettingCtrl.getSettingServant(this.mSettingCtrl.getCurrentCameraId());
    }

    @Override // com.mediatek.camera.p005v2.setting.ISettingRule
    public void execute() {
        String currentCameraId = this.mSettingCtrl.getCurrentCameraId();
        if (!this.mCurrentCameraId.equals(currentCameraId)) {
            this.mSettingServant = this.mSettingCtrl.getSettingServant(currentCameraId);
            this.mCurrentCameraId = currentCameraId;
        }
        String settingValue = this.mSettingServant.getSettingValue("photo_pip_key");
        if (!"on".equalsIgnoreCase(settingValue)) {
            if ("off".equalsIgnoreCase(settingValue)) {
                LogHelper.m26i(TAG, "Exit");
            }
        } else {
            Size size = Utils.getSize(this.mSettingServant.getSettingValue("pref_camera_picturesize_key"));
            ISettingServant settingServant = this.mSettingCtrl.getSettingServant(getAnotherCameraId());
            Size sizeFilterSupportedSize = Utils.filterSupportedSize(Utils.getSizeList(settingServant.getSupportedValues("pref_camera_picturesize_key")), size, this.mSettingServant.getPreviewSize());
            settingServant.getSettingItem("pref_camera_picturesize_key").setValue(Utils.buildSize(sizeFilterSupportedSize));
            LogHelper.m23d(TAG, "Enter Bottom:" + Utils.buildSize(size) + " Top:" + Utils.buildSize(sizeFilterSupportedSize) + " mCurrentCameraId:" + this.mCurrentCameraId);
        }
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
