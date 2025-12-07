package com.mediatek.camera.mode.facebeauty;

import com.mediatek.camera.ICameraContext;
import com.mediatek.camera.ISettingCtrl;
import com.mediatek.camera.ISettingRule;
import com.mediatek.camera.platform.ICameraDeviceManager;
import com.mediatek.camera.platform.Parameters;
import com.mediatek.camera.setting.ParametersHelper;
import com.mediatek.camera.setting.SettingItem;
import com.mediatek.camera.setting.SettingItem.Record;
import com.mediatek.camera.setting.preference.ListPreference;
import com.mediatek.camera.util.Log;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class FaceBeautyZsdRule implements ISettingRule {
    private List<String> mConditions = new ArrayList();
    private ICameraContext mICameraContext;
    private ICameraDeviceManager mICameraDeviceManager;
    private ISettingCtrl mISettingCtrl;

    public FaceBeautyZsdRule(ISettingCtrl iSettingCtrl, ICameraContext iCameraContext) {
        Log.m34i("FaceBeutyZsdRule", "[FaceBeautyZsdRule]constructor...");
        this.mISettingCtrl = iSettingCtrl;
        this.mICameraContext = iCameraContext;
        this.mICameraDeviceManager = iCameraContext.getCameraDeviceManager();
    }

    @Override // com.mediatek.camera.ISettingRule
    public void execute() {
        if (!this.mICameraContext.getFeatureConfig().isGmoRamOptSupport()) {
            return;
        }
        SettingItem setting = this.mISettingCtrl.getSetting("pref_camera_zsd_key");
        ListPreference listPreference = setting.getListPreference();
        String settingValue = this.mISettingCtrl.getSettingValue("face_beauty_key");
        int currentCameraId = this.mICameraDeviceManager.getCurrentCameraId();
        Parameters parameters = this.mICameraDeviceManager.getCameraDevice(currentCameraId).getParameters();
        int iIndexOf = this.mConditions.indexOf(settingValue);
        Log.m34i("FaceBeutyZsdRule", "[execute],valueIndex = " + iIndexOf);
        if (iIndexOf != -1) {
            if (listPreference != null) {
                listPreference.setOverrideValue("off");
                setting.getClass();
                setting.addOverrideRecord("face_beauty_key", setting.new Record("off", "off"));
                return;
            }
            return;
        }
        if (setting.getOverrideRecord("face_beauty_key") == null) {
            return;
        }
        setting.removeOverrideRecord("face_beauty_key");
        if (setting.getOverrideCount() > 0) {
            SettingItem.Record topOverrideRecord = setting.getTopOverrideRecord();
            if (topOverrideRecord != null) {
                ParametersHelper.setParametersValue(parameters, currentCameraId, "pref_camera_zsd_key", topOverrideRecord.getValue());
                if (listPreference != null) {
                    listPreference.setOverrideValue(topOverrideRecord.getOverrideValue());
                    return;
                }
                return;
            }
            return;
        }
        if (listPreference != null) {
            listPreference.setOverrideValue(null);
        }
    }

    @Override // com.mediatek.camera.ISettingRule
    public void addLimitation(String str, List<String> list, ISettingRule.MappingFinder mappingFinder) {
        this.mConditions.add(str);
    }
}
