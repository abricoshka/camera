package com.mediatek.camera.p005v2.mode.pip.combination;

import com.mediatek.camera.debug.LogHelper;
import com.mediatek.camera.p005v2.setting.ISettingRule;
import com.mediatek.camera.p005v2.setting.ISettingServant;
import com.mediatek.camera.p005v2.setting.SettingCtrl;
import com.mediatek.camera.p005v2.setting.SettingItem;
import com.mediatek.camera.v2.setting.SettingItem.Record;
import java.util.List;

/* loaded from: classes.dex */
public class FdAffectedRule implements ISettingRule {
    private static final LogHelper.Tag TAG = new LogHelper.Tag(FdAffectedRule.class.getSimpleName());
    private String mCurrentCameraId;
    private String mResultKey;
    private final SettingCtrl mSettingCtrl;
    private ISettingServant mSettingServant;

    public FdAffectedRule(SettingCtrl settingCtrl, String str) {
        this.mCurrentCameraId = null;
        this.mSettingCtrl = settingCtrl;
        this.mResultKey = str;
        this.mCurrentCameraId = this.mSettingCtrl.getCurrentCameraId();
        this.mSettingServant = this.mSettingCtrl.getSettingServant(this.mSettingCtrl.getCurrentCameraId());
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
        if ("on".equalsIgnoreCase(settingValue)) {
            closeFdSetting(this.mSettingCtrl.getSettingServant("1"));
            ISettingServant settingServant = this.mSettingCtrl.getSettingServant("0");
            closeFdSetting(settingServant);
            LogHelper.m23d(TAG, "Enter pip, " + this.mResultKey + " must off camera id:" + settingServant.getCameraId() + " isPipSwitched:" + z);
            return;
        }
        if ("off".equalsIgnoreCase(settingValue)) {
            restoreFdSetting(this.mSettingCtrl.getSettingServant("1"));
            restoreFdSetting(this.mSettingCtrl.getSettingServant("0"));
        }
    }

    @Override // com.mediatek.camera.p005v2.setting.ISettingRule
    public void addLimitation(String str, List<String> list) {
    }

    private void closeFdSetting(ISettingServant iSettingServant) {
        SettingItem settingItem = iSettingServant.getSettingItem(this.mResultKey);
        settingItem.setValue("off");
        settingItem.setOverrideValue("off");
        settingItem.getClass();
        settingItem.addOverrideRecord("photo_pip_key", settingItem.new Record("off", "off"));
    }

    private void restoreFdSetting(ISettingServant iSettingServant) {
        String sharedPreferencesValue;
        String overrideValue = null;
        SettingItem settingItem = iSettingServant.getSettingItem(this.mResultKey);
        if (settingItem.getOverrideRecord("photo_pip_key") == null) {
            LogHelper.m26i(TAG, "[execute], no override record, return");
            return;
        }
        settingItem.removeOverrideRecord("photo_pip_key");
        if (settingItem.getOverrideCount() > 0) {
            SettingItem.Record topOverrideRecord = settingItem.getTopOverrideRecord();
            if (topOverrideRecord != null) {
                sharedPreferencesValue = topOverrideRecord.getValue();
                overrideValue = topOverrideRecord.getOverrideValue();
            } else {
                sharedPreferencesValue = null;
            }
        } else {
            sharedPreferencesValue = iSettingServant.getSharedPreferencesValue(this.mResultKey);
        }
        settingItem.setValue(sharedPreferencesValue);
        settingItem.setOverrideValue(overrideValue);
    }
}
