package com.mediatek.camera.mode.pip;

import android.media.CamcorderProfile;
import com.mediatek.camera.ICameraContext;
import com.mediatek.camera.ISettingCtrl;
import com.mediatek.camera.ISettingRule;
import com.mediatek.camera.platform.ICameraDeviceManager;
import com.mediatek.camera.setting.SettingItem;
import com.mediatek.camera.setting.SettingItem.Record;
import com.mediatek.camera.setting.SettingUtils;
import com.mediatek.camera.setting.preference.ListPreference;
import com.mediatek.camera.util.Log;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class PipVideoQualityRule implements ISettingRule {
    private ICameraContext mCameraContext;
    private String mConditionKey;
    private ICameraDeviceManager mICameraDeviceManager;
    private ISettingCtrl mISettingCtrl;
    private static final String QUALITY_480P = Integer.toString(4);
    private static final String QUALITY_720P = Integer.toString(5);
    private static final String QUALITY_1080P = Integer.toString(6);
    private List<String> mConditions = new ArrayList();
    private List<List<String>> mResults = new ArrayList();
    private List<ISettingRule.MappingFinder> mMappingFinder = new ArrayList();

    public PipVideoQualityRule(ICameraContext iCameraContext, String str) {
        this.mConditionKey = null;
        Log.m31d("PipVideoQualityRule", "[PipVideoQualityRule]constructor...");
        this.mCameraContext = iCameraContext;
        this.mConditionKey = str;
        this.mICameraDeviceManager = iCameraContext.getCameraDeviceManager();
    }

    @Override // com.mediatek.camera.ISettingRule
    public void execute() {
        String value;
        String strBuildEnableList = null;
        Log.m31d("PipVideoQualityRule", "[execute]...");
        this.mISettingCtrl = this.mCameraContext.getSettingController();
        int iConditionSatisfied = conditionSatisfied(this.mISettingCtrl.getSettingValue(this.mConditionKey));
        Log.m31d("PipVideoQualityRule", "[execute], mConditionKey:" + this.mConditionKey + ", index = " + iConditionSatisfied);
        SettingItem setting = this.mISettingCtrl.getSetting("pref_video_quality_key");
        ListPreference listPreference = this.mISettingCtrl.getListPreference("pref_video_quality_key");
        if (iConditionSatisfied == -1) {
            int overrideCount = setting.getOverrideCount();
            if (setting.getOverrideRecord(this.mConditionKey) == null) {
                return;
            }
            setting.removeOverrideRecord(this.mConditionKey);
            if (overrideCount - 1 > 0) {
                SettingItem.Record topOverrideRecord = setting.getTopOverrideRecord();
                if (topOverrideRecord != null) {
                    strBuildEnableList = topOverrideRecord.getValue();
                    setting.setValue(strBuildEnableList);
                    String overrideValue = topOverrideRecord.getOverrideValue();
                    setting.setValue(strBuildEnableList);
                    if (listPreference != null) {
                        listPreference.setOverrideValue(overrideValue);
                    }
                }
            } else {
                if (listPreference != null) {
                    value = listPreference.getValue();
                    listPreference.setOverrideValue(null);
                } else {
                    value = null;
                }
                setting.setValue(value);
                strBuildEnableList = value;
            }
            Log.m31d("PipVideoQualityRule", "set quality:" + strBuildEnableList);
            return;
        }
        List<String> supportedPIPVideoQualities = getSupportedPIPVideoQualities();
        String quality = getQuality(setting.getValue(), supportedPIPVideoQualities);
        setting.setValue(quality);
        Log.m31d("PipVideoQualityRule", "set quality:" + quality);
        if (listPreference != null && supportedPIPVideoQualities != null) {
            strBuildEnableList = SettingUtils.buildEnableList((String[]) supportedPIPVideoQualities.toArray(new String[supportedPIPVideoQualities.size()]), quality);
            listPreference.setOverrideValue(strBuildEnableList);
        }
        setting.getClass();
        setting.addOverrideRecord(this.mConditionKey, setting.new Record(quality, strBuildEnableList));
    }

    @Override // com.mediatek.camera.ISettingRule
    public void addLimitation(String str, List<String> list, ISettingRule.MappingFinder mappingFinder) {
        Log.m31d("PipVideoQualityRule", "[addLimitation]condition = " + str);
        this.mConditions.add(str);
        this.mResults.add(list);
        this.mMappingFinder.add(mappingFinder);
    }

    private int conditionSatisfied(String str) {
        return this.mConditions.indexOf(str);
    }

    private List<String> getSupportedPIPVideoQualities() {
        Log.m31d("PipVideoQualityRule", "getSupportedPIPVideoQualities");
        ArrayList arrayList = new ArrayList();
        if (!"0321".equals(this.mCameraContext.getFeatureConfig().whichDeanliChip()) && checkSatisfyVideoPIPQuality(6)) {
            arrayList.add(QUALITY_1080P);
        }
        if (checkSatisfyVideoPIPQuality(5)) {
            arrayList.add(QUALITY_720P);
        }
        if (checkSatisfyVideoPIPQuality(4)) {
            arrayList.add(QUALITY_480P);
        }
        if (arrayList.size() > 0) {
            return arrayList;
        }
        return null;
    }

    private boolean checkSatisfyVideoPIPQuality(int i) {
        int backCameraId = this.mICameraDeviceManager.getBackCameraId();
        return CamcorderProfile.hasProfile(backCameraId, i) && CamcorderProfile.get(backCameraId, i).videoFrameWidth <= 1920;
    }

    private String getQuality(String str, List<String> list) {
        String string = (list != null && (list.contains(str) ^ true) && Integer.toString(6).equals(str)) ? Integer.toString(5) : str;
        if (list != null && (!list.contains(string))) {
            return list.get(0);
        }
        return string;
    }
}
