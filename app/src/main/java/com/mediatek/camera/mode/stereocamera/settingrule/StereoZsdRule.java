package com.mediatek.camera.mode.stereocamera.settingrule;

import android.util.Log;
import com.mediatek.camera.ICameraContext;
import com.mediatek.camera.platform.Parameters;
import com.mediatek.camera.setting.ParametersHelper;
import com.mediatek.camera.setting.SettingItem;
import com.mediatek.camera.setting.SettingItem.Record;
import com.mediatek.camera.setting.preference.ListPreference;

/* loaded from: classes.dex */
public class StereoZsdRule extends StereoSettingRule {
    private SettingItem mCurrentSettingItem;
    private SettingItem mZsdItem;

    public StereoZsdRule(ICameraContext iCameraContext, int i) {
        super(iCameraContext, i);
    }

    @Override // com.mediatek.camera.mode.stereocamera.settingrule.StereoSettingRule, com.mediatek.camera.ISettingRule
    public void execute() {
        super.execute();
        if (this.mFeatureType == 2) {
            this.mCurrentSettingItem = this.mISettingCtrl.getSetting("photo_stereo_key");
        } else {
            if (!ParametersHelper.isVsDofSupported(this.mICameraDeviceManager.getCameraDevice(this.mICameraDeviceManager.getCurrentCameraId()).getParameters())) {
                Log.i("StereoZsdRule", "VsDof only support zsd capture");
                return;
            }
            this.mCurrentSettingItem = this.mISettingCtrl.getSetting("refocus_key");
        }
        this.mZsdItem = this.mISettingCtrl.getSetting("pref_camera_zsd_key");
        String value = this.mZsdItem.getValue();
        String value2 = this.mCurrentSettingItem.getValue();
        Parameters parameters = this.mICameraDeviceManager.getCameraDevice(this.mICameraDeviceManager.getCurrentCameraId()).getParameters();
        ListPreference listPreference = this.mZsdItem.getListPreference();
        if ("on".equals(value2)) {
            value = "on";
            if (this.mZsdItem.isEnable()) {
                this.mZsdItem.setValue("on");
                if (listPreference != null) {
                    listPreference.setOverrideValue("on", true);
                }
            }
            SettingItem settingItem = this.mZsdItem;
            settingItem.getClass();
            SettingItem.Record record = settingItem.new Record("on", "on");
            if (this.mFeatureType == 2) {
                this.mZsdItem.addOverrideRecord("photo_stereo_key", record);
            } else {
                this.mZsdItem.addOverrideRecord("refocus_key", record);
            }
        } else {
            if (this.mFeatureType == 2) {
                this.mZsdItem.removeOverrideRecord("photo_stereo_key");
            } else {
                this.mZsdItem.removeOverrideRecord("refocus_key");
            }
            if (this.mZsdItem.getOverrideCount() > 0) {
                SettingItem.Record topOverrideRecord = this.mZsdItem.getTopOverrideRecord();
                if (topOverrideRecord != null) {
                    String value3 = topOverrideRecord.getValue();
                    String overrideValue = topOverrideRecord.getOverrideValue();
                    this.mZsdItem.setValue(value3);
                    ListPreference listPreference2 = this.mZsdItem.getListPreference();
                    if (listPreference2 != null) {
                        listPreference2.setOverrideValue(overrideValue);
                    }
                }
            } else {
                ListPreference listPreference3 = this.mZsdItem.getListPreference();
                if (listPreference3 != null) {
                    listPreference3.setOverrideValue(null);
                    value = listPreference3.getValue();
                }
                this.mZsdItem.setValue(value);
            }
        }
        parameters.set("zsd-mode", value);
    }
}
