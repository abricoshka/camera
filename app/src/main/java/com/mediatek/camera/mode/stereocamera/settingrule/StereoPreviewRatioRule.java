package com.mediatek.camera.mode.stereocamera.settingrule;

import android.util.Log;
import com.mediatek.camera.ICameraContext;
import com.mediatek.camera.platform.Parameters;
import com.mediatek.camera.setting.ParametersHelper;
import com.mediatek.camera.setting.SettingItem;
import com.mediatek.camera.setting.SettingItem.Record;
import com.mediatek.camera.setting.SettingUtils;
import com.mediatek.camera.setting.preference.ListPreference;

/* loaded from: classes.dex */
public class StereoPreviewRatioRule extends StereoSettingRule {
    private SettingItem mCurrentSettingItem;
    private SettingItem mPictureRatioSetting;

    public StereoPreviewRatioRule(ICameraContext iCameraContext, int i) {
        super(iCameraContext, i);
    }

    @Override // com.mediatek.camera.mode.stereocamera.settingrule.StereoSettingRule, com.mediatek.camera.ISettingRule
    public void execute() throws NumberFormatException {
        super.execute();
        Log.i("StereoPreviewRatioRule", "feature type = " + this.mFeatureType);
        if (this.mFeatureType == 2) {
            this.mCurrentSettingItem = this.mISettingCtrl.getSetting("photo_stereo_key");
        } else {
            this.mCurrentSettingItem = this.mISettingCtrl.getSetting("refocus_key");
        }
        this.mPictureRatioSetting = this.mISettingCtrl.getSetting("pref_camera_picturesize_ratio_key");
        String value = this.mPictureRatioSetting.getValue();
        String value2 = this.mCurrentSettingItem.getValue();
        Parameters parameters = this.mICameraDeviceManager.getCameraDevice(this.mICameraDeviceManager.getCurrentCameraId()).getParameters();
        if (this.mFeatureType != 2 && ParametersHelper.isVsDofSupported(parameters)) {
            Log.i("StereoPreviewRatioRule", "VsDof support 4:3");
            SettingUtils.setPreviewSize(this.mCameraContext.getActivity(), parameters, value);
            if (ParametersHelper.isDenoiseSupported(parameters)) {
                parameters.set("preview-frame-rate", 24);
                return;
            }
            return;
        }
        ListPreference listPreference = this.mPictureRatioSetting.getListPreference();
        if ("on".equals(value2)) {
            if (this.mPictureRatioSetting.isEnable()) {
                this.mPictureRatioSetting.setValue("1.7778");
                if (listPreference != null) {
                    listPreference.setOverrideValue("1.7778", true);
                }
                SettingUtils.setPreviewSize(this.mCameraContext.getActivity(), parameters, "1.7778");
            }
            if (this.mFeatureType != 2 && ParametersHelper.isDenoiseSupported(parameters)) {
                parameters.set("preview-frame-rate", 24);
            }
            SettingItem settingItem = this.mPictureRatioSetting;
            settingItem.getClass();
            SettingItem.Record record = settingItem.new Record("1.7778", "1.7778");
            if (this.mFeatureType == 2) {
                this.mPictureRatioSetting.addOverrideRecord("photo_stereo_key", record);
                return;
            } else {
                this.mPictureRatioSetting.addOverrideRecord("refocus_key", record);
                return;
            }
        }
        if (this.mFeatureType == 2) {
            this.mPictureRatioSetting.removeOverrideRecord("photo_stereo_key");
        } else {
            this.mPictureRatioSetting.removeOverrideRecord("refocus_key");
        }
        if (this.mPictureRatioSetting.getOverrideCount() > 0) {
            SettingItem.Record topOverrideRecord = this.mPictureRatioSetting.getTopOverrideRecord();
            if (topOverrideRecord != null) {
                String value3 = topOverrideRecord.getValue();
                String overrideValue = topOverrideRecord.getOverrideValue();
                this.mPictureRatioSetting.setValue(value3);
                ListPreference listPreference2 = this.mPictureRatioSetting.getListPreference();
                if (listPreference2 != null) {
                    listPreference2.setOverrideValue(overrideValue);
                    return;
                }
                return;
            }
            return;
        }
        ListPreference listPreference3 = this.mPictureRatioSetting.getListPreference();
        if (listPreference3 != null) {
            listPreference3.setOverrideValue(null);
        }
        this.mPictureRatioSetting.setValue(value);
        SettingUtils.setPreviewSize(this.mCameraContext.getActivity(), parameters, value);
    }
}
