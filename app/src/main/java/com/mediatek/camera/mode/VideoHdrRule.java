package com.mediatek.camera.mode;

import com.mediatek.camera.ICameraContext;
import com.mediatek.camera.ISettingCtrl;
import com.mediatek.camera.ISettingRule;
import com.mediatek.camera.platform.ICameraDeviceManager;
import com.mediatek.camera.platform.IModuleCtrl;
import com.mediatek.camera.platform.Parameters;
import com.mediatek.camera.setting.ParametersHelper;
import com.mediatek.camera.setting.SettingItem;
import com.mediatek.camera.setting.SettingItem.Record;
import com.mediatek.camera.setting.preference.ListPreference;
import com.mediatek.camera.util.Log;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class VideoHdrRule implements ISettingRule {
    private ICameraContext mICameraContext;
    private ICameraDeviceManager.ICameraDevice mICameraDevice;
    private ICameraDeviceManager mICameraDeviceManager;
    private IModuleCtrl mIMoudleCtrl;
    private ISettingCtrl mISettingCtrl;
    private String mLastHdrValue;
    private Parameters mParameters;
    private String TAG = "VideoHdrRule";
    private List<String> mConditions = new ArrayList();
    private List<List<String>> mResults = new ArrayList();
    private List<ISettingRule.MappingFinder> mMappingFinder = new ArrayList();
    private boolean mHasOverride = false;

    public VideoHdrRule(ICameraContext iCameraContext) {
        Log.m34i(this.TAG, "[VideoPreviewRule]constructor...");
        this.mICameraContext = iCameraContext;
        this.mISettingCtrl = iCameraContext.getSettingController();
        this.mICameraDeviceManager = iCameraContext.getCameraDeviceManager();
        this.mIMoudleCtrl = iCameraContext.getModuleController();
    }

    @Override // com.mediatek.camera.ISettingRule
    public void execute() {
        String settingValue = this.mISettingCtrl.getSettingValue("video_key");
        boolean zEquals = "on".equals(this.mISettingCtrl.getSetting("pref_camera_zsd_key").getValue());
        getCameraDevice();
        if (conditionSatisfied(settingValue) == -1) {
            if (this.mHasOverride) {
                restoreHdrRule();
                this.mHasOverride = false;
            }
            if (this.mICameraContext.getFeatureConfig().isZSDHDRSupported() && zEquals) {
                ParametersHelper.setParametersValue(this.mParameters, this.mICameraDeviceManager.getCurrentCameraId(), "pref_hdr_key", "off");
                return;
            }
            return;
        }
        this.mLastHdrValue = this.mISettingCtrl.getSettingValue("pref_hdr_key");
        setHdrRule();
    }

    @Override // com.mediatek.camera.ISettingRule
    public void addLimitation(String str, List<String> list, ISettingRule.MappingFinder mappingFinder) {
        this.mConditions.add(str);
        this.mResults.add(list);
        this.mMappingFinder.add(mappingFinder);
    }

    private void setHdrRule() {
        this.mParameters = this.mICameraDevice.getParameters();
        List<String> parametersSupportedValues = ParametersHelper.getParametersSupportedValues(this.mParameters, "pref_hdr_key");
        if (parametersSupportedValues == null || parametersSupportedValues.indexOf("on") < 0) {
            this.mISettingCtrl.setSettingValue("pref_hdr_key", "off", this.mICameraDeviceManager.getCurrentCameraId());
            SettingItem setting = this.mISettingCtrl.getSetting("pref_hdr_key");
            if (!this.mHasOverride) {
                setting.setOverrideCount(setting.getOverrideCount() + 1);
                this.mHasOverride = true;
            }
            setting.getClass();
            setting.addOverrideRecord("video_key", setting.new Record("off", "off"));
            return;
        }
        ParametersHelper.setParametersValue(this.mParameters, this.mICameraDeviceManager.getCurrentCameraId(), "pref_hdr_key", this.mISettingCtrl.getSetting("pref_hdr_key").getValue());
    }

    private void restoreHdrRule() {
        String overrideValue;
        Log.m34i(this.TAG, "[restoreHdrRule], mLastHdrValue:" + this.mLastHdrValue + ", and mHasOverride:" + this.mHasOverride);
        if (!this.mHasOverride) {
            return;
        }
        SettingItem setting = this.mISettingCtrl.getSetting("pref_hdr_key");
        int overrideCount = setting.getOverrideCount();
        SettingItem.Record overrideRecord = setting.getOverrideRecord("video_key");
        if (overrideCount > 0 && overrideRecord != null) {
            overrideCount--;
            setting.setOverrideCount(overrideCount);
            setting.removeOverrideRecord("video_key");
            this.mHasOverride = false;
        }
        Log.m34i(this.TAG, "overrideCount:" + overrideCount);
        if (overrideCount > 0) {
            SettingItem.Record topOverrideRecord = setting.getTopOverrideRecord();
            if (topOverrideRecord != null) {
                this.mISettingCtrl.setSettingValue("pref_hdr_key", topOverrideRecord.getValue(), this.mICameraDeviceManager.getCurrentCameraId());
                overrideValue = topOverrideRecord.getOverrideValue();
            } else {
                overrideValue = null;
            }
        } else {
            this.mISettingCtrl.setSettingValue("pref_hdr_key", this.mLastHdrValue, this.mICameraDeviceManager.getCurrentCameraId());
            overrideValue = null;
        }
        ListPreference listPreference = this.mISettingCtrl.getListPreference("pref_hdr_key");
        if (listPreference != null) {
            listPreference.setOverrideValue(overrideValue);
        }
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
