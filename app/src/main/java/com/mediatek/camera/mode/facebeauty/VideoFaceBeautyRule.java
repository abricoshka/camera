package com.mediatek.camera.mode.facebeauty;

import com.mediatek.camera.ICameraContext;
import com.mediatek.camera.ISettingCtrl;
import com.mediatek.camera.ISettingRule;
import com.mediatek.camera.platform.ICameraDeviceManager;
import com.mediatek.camera.platform.Parameters;
import com.mediatek.camera.setting.SettingItem;
import com.mediatek.camera.setting.SettingItem.Record;
import com.mediatek.camera.setting.preference.ListPreference;
import com.mediatek.camera.util.Log;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class VideoFaceBeautyRule implements ISettingRule {
    private ICameraDeviceManager.ICameraDevice mICameraDevice;
    private ICameraDeviceManager mICameraDeviceManager;
    private ISettingCtrl mISettingCtrl;
    private String mLastValue;
    private String TAG = "VideoFaceBeautyVideoRule";
    private List<String> mConditions = new ArrayList();
    private List<List<String>> mResults = new ArrayList();
    private List<ISettingRule.MappingFinder> mMappingFinder = new ArrayList();

    public VideoFaceBeautyRule(ICameraContext iCameraContext) {
        Log.m34i(this.TAG, "[VideoFaceBeautyRule]constructor...");
        this.mISettingCtrl = iCameraContext.getSettingController();
        this.mICameraDeviceManager = iCameraContext.getCameraDeviceManager();
    }

    @Override // com.mediatek.camera.ISettingRule
    public void execute() {
        String overrideValue = null;
        String settingValue = this.mISettingCtrl.getSettingValue("video_key");
        getCameraDevice();
        Parameters parameters = this.mICameraDevice.getParameters();
        if (conditionSatisfied(settingValue) == -1) {
            SettingItem setting = this.mISettingCtrl.getSetting("pref_slow_motion_key");
            if (setting.getOverrideRecord("video_key") == null) {
                return;
            }
            setting.removeOverrideRecord("video_key");
            int overrideCount = setting.getOverrideCount();
            Log.m34i(this.TAG, "overrideCount:" + overrideCount);
            if (overrideCount > 0) {
                SettingItem.Record topOverrideRecord = setting.getTopOverrideRecord();
                if (topOverrideRecord != null) {
                    setting.setValue(topOverrideRecord.getValue());
                    overrideValue = topOverrideRecord.getOverrideValue();
                }
            } else {
                this.mISettingCtrl.setSettingValue("pref_slow_motion_key", this.mLastValue, this.mICameraDeviceManager.getCurrentCameraId());
            }
            ListPreference listPreference = this.mISettingCtrl.getListPreference("pref_slow_motion_key");
            if (listPreference != null) {
                listPreference.setOverrideValue(overrideValue);
                return;
            }
            return;
        }
        if (parameters != null && "true".equals(parameters.get("face-beauty"))) {
            SettingItem setting2 = this.mISettingCtrl.getSetting("pref_slow_motion_key");
            this.mLastValue = setting2.getValue();
            setting2.setValue("off");
            ListPreference listPreference2 = this.mISettingCtrl.getListPreference("pref_slow_motion_key");
            if (listPreference2 != null) {
                listPreference2.setOverrideValue("disable-value");
            }
            setting2.getClass();
            setting2.addOverrideRecord("video_key", setting2.new Record("off", "disable-value"));
        }
    }

    @Override // com.mediatek.camera.ISettingRule
    public void addLimitation(String str, List<String> list, ISettingRule.MappingFinder mappingFinder) {
        this.mConditions.add(str);
        this.mResults.add(list);
        this.mMappingFinder.add(mappingFinder);
    }

    private int conditionSatisfied(String str) {
        int iIndexOf = this.mConditions.indexOf(str);
        Log.m34i(this.TAG, "[conditionSatisfied]limitation index:" + iIndexOf);
        return iIndexOf;
    }

    private void getCameraDevice() {
        if (this.mICameraDeviceManager != null) {
            this.mICameraDevice = this.mICameraDeviceManager.getCameraDevice(this.mICameraDeviceManager.getCurrentCameraId());
        }
    }
}
