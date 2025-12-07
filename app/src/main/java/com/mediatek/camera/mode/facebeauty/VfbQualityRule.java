package com.mediatek.camera.mode.facebeauty;

import android.media.CamcorderProfile;
import com.mediatek.camera.ICameraContext;
import com.mediatek.camera.ISettingCtrl;
import com.mediatek.camera.ISettingRule;
import com.mediatek.camera.platform.ICameraDeviceManager;
import com.mediatek.camera.platform.Parameters;
import com.mediatek.camera.setting.SettingItem;
import com.mediatek.camera.setting.SettingItem.Record;
import com.mediatek.camera.setting.SettingUtils;
import com.mediatek.camera.setting.preference.ListPreference;
import com.mediatek.camera.util.Log;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class VfbQualityRule implements ISettingRule {
    private String mConditionKey;
    private ICameraDeviceManager.ICameraDevice mICameraDevice;
    private ICameraDeviceManager mICameraDeviceManager;
    private ISettingCtrl mISettingCtrl;
    private static final String QUALITY_QCIF = Integer.toString(2);
    private static final String QUALITY_CIF = Integer.toString(3);
    private static final String QUALITY_480P = Integer.toString(4);
    private static final String QUALITY_720P = Integer.toString(5);
    private static final String QUALITY_1080P = Integer.toString(6);
    private static final String QUALITY_QVGA = Integer.toString(7);
    private static final String QUALITY_2160P = Integer.toString(8);
    private static final int[] NORMAL_SUPPORT_QUALIYS = {8, 6, 5, 4, 3, 7, 2};
    private static final String[] NORMAL_SUPPORT_QUALIYS_STRING = {QUALITY_2160P, QUALITY_1080P, QUALITY_720P, QUALITY_480P, QUALITY_CIF, QUALITY_QVGA, QUALITY_QCIF};
    private List<String> mConditions = new ArrayList();
    private List<List<String>> mResults = new ArrayList();
    private List<ISettingRule.MappingFinder> mMappingFinder = new ArrayList();
    private boolean mHasOverride = false;

    public VfbQualityRule(ICameraContext iCameraContext, String str) {
        this.mConditionKey = null;
        Log.m34i("VideoFaceBeautyVideoQualityRule", "[VfbQualityRule]constructor...");
        this.mConditionKey = str;
        this.mISettingCtrl = iCameraContext.getSettingController();
        this.mICameraDeviceManager = iCameraContext.getCameraDeviceManager();
    }

    @Override // com.mediatek.camera.ISettingRule
    public void execute() {
        String value;
        String strBuildEnableList = null;
        String settingValue = this.mISettingCtrl.getSettingValue("video_key");
        this.mICameraDevice = getCameraDevice();
        Parameters parameters = this.mICameraDevice.getParameters();
        int iConditionSatisfied = conditionSatisfied(settingValue);
        SettingItem setting = this.mISettingCtrl.getSetting("pref_video_quality_key");
        ListPreference listPreference = this.mISettingCtrl.getListPreference("pref_video_quality_key");
        Log.m34i("VideoFaceBeautyVideoQualityRule", "[execute] index = " + iConditionSatisfied);
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
            Log.m34i("VideoFaceBeautyVideoQualityRule", "set quality:" + strBuildEnableList);
            return;
        }
        if (parameters != null && "true".equals(parameters.get("face-beauty"))) {
            List<String> supportedVideoQualities = getSupportedVideoQualities();
            String quality = getQuality(setting.getValue(), supportedVideoQualities);
            setting.setValue(quality);
            Log.m34i("VideoFaceBeautyVideoQualityRule", "set quality:" + quality);
            if (listPreference != null && supportedVideoQualities != null) {
                strBuildEnableList = SettingUtils.buildEnableList((String[]) supportedVideoQualities.toArray(new String[supportedVideoQualities.size()]), quality);
                listPreference.setOverrideValue(strBuildEnableList);
            }
            setting.getClass();
            setting.addOverrideRecord(this.mConditionKey, setting.new Record(quality, strBuildEnableList));
        }
    }

    @Override // com.mediatek.camera.ISettingRule
    public void addLimitation(String str, List<String> list, ISettingRule.MappingFinder mappingFinder) {
        Log.m34i("VideoFaceBeautyVideoQualityRule", "[addLimitation]condition = " + str);
        this.mConditions.add(str);
        this.mResults.add(list);
        this.mMappingFinder.add(mappingFinder);
    }

    private ICameraDeviceManager.ICameraDevice getCameraDevice() {
        if (this.mICameraDeviceManager == null) {
            return null;
        }
        return this.mICameraDeviceManager.getCameraDevice(this.mICameraDeviceManager.getCurrentCameraId());
    }

    private int conditionSatisfied(String str) {
        int iIndexOf = this.mConditions.indexOf(str);
        Log.m34i("VideoFaceBeautyVideoQualityRule", "[conditionSatisfied]limitation index:" + iIndexOf);
        return iIndexOf;
    }

    private List<String> getSupportedVideoQualities() {
        int i = 0;
        Log.m34i("VideoFaceBeautyVideoQualityRule", "[getSupportedVideoQualities]");
        ArrayList arrayList = new ArrayList();
        int currentCameraId = this.mICameraDeviceManager.getCurrentCameraId();
        int length = NORMAL_SUPPORT_QUALIYS_STRING.length;
        int i2 = 4;
        if (currentCameraId == 1) {
            i2 = 2;
        }
        for (int i3 = 0; i3 < length && i < i2; i3++) {
            if (CamcorderProfile.hasProfile(currentCameraId, NORMAL_SUPPORT_QUALIYS[i3])) {
                i++;
                if (CamcorderProfile.get(currentCameraId, NORMAL_SUPPORT_QUALIYS[i3]).videoFrameWidth <= 1920) {
                    arrayList.add(NORMAL_SUPPORT_QUALIYS_STRING[i3]);
                }
            }
        }
        if (arrayList.size() > 0) {
            return arrayList;
        }
        return null;
    }

    private String getQuality(String str, List<String> list) {
        if (list != null && (!list.contains(str)) && Integer.toString(6).equals(str)) {
            str = Integer.toString(5);
        }
        return !list.contains(str) ? list.get(0) : str;
    }
}
