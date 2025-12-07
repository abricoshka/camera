package com.mediatek.camera.setting.rule;

import com.mediatek.camera.ICameraContext;
import com.mediatek.camera.ISettingCtrl;
import com.mediatek.camera.ISettingRule;
import com.mediatek.camera.platform.ICameraDeviceManager;
import com.mediatek.camera.platform.IModuleCtrl;
import com.mediatek.camera.platform.Parameters;
import com.mediatek.camera.setting.ParametersHelper;
import com.mediatek.camera.setting.SettingItem;
import com.mediatek.camera.setting.SettingItem.Record;
import com.mediatek.camera.setting.SettingUtils;
import com.mediatek.camera.setting.preference.ListPreference;
import com.mediatek.camera.util.Log;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class RuleContainer {
    private long PICTURE_SIZE_4M = 4000000;
    private ICameraContext mICameraContext;
    private ICameraDeviceManager mICameraDeviceManager;
    private IModuleCtrl mIModuleCtrl;
    private ISettingCtrl mISettingCtrl;

    public RuleContainer(ISettingCtrl iSettingCtrl, ICameraContext iCameraContext) {
        this.mISettingCtrl = iSettingCtrl;
        this.mICameraDeviceManager = iCameraContext.getCameraDeviceManager();
        this.mICameraContext = iCameraContext;
        this.mIModuleCtrl = this.mICameraContext.getModuleController();
    }

    public void addRule() {
        byte b = 0;
        this.mISettingCtrl.addRule("pref_hdr_key", "pref_camera_zsd_key", new HDRZSDRule());
        this.mISettingCtrl.addRule("pref_camera_picturesize_ratio_key", "pref_camera_picturesize_key", new PictureRatioSizeRule(this, null));
        if (!this.mIModuleCtrl.isNonePickIntent()) {
            this.mISettingCtrl.addRule("video_key", "camera_mode_key", new RecordingCameraModeRule(this, b == true ? 1 : 0));
        }
        if (!this.mICameraContext.getFeatureConfig().isLowRamOptSupport()) {
            return;
        }
        LowRamPictureRule lowRamPictureRule = new LowRamPictureRule("pref_hdr_key");
        lowRamPictureRule.addLimitation("on", null, null);
        this.mISettingCtrl.addRule("pref_hdr_key", "pref_camera_picturesize_key", lowRamPictureRule);
        LowRamPictureRule lowRamPictureRule2 = new LowRamPictureRule("perf_camera_ais_key");
        lowRamPictureRule2.addLimitation("ais", null, null);
        this.mISettingCtrl.addRule("perf_camera_ais_key", "pref_camera_picturesize_key", lowRamPictureRule2);
        LowRamPictureRule lowRamPictureRule3 = new LowRamPictureRule("pref_camera_scenemode_key");
        lowRamPictureRule3.addLimitation("night", null, null);
        this.mISettingCtrl.addRule("pref_camera_scenemode_key", "pref_camera_picturesize_key", lowRamPictureRule3);
    }

    private class LowRamPictureRule implements ISettingRule {
        private String mCondition;
        private String mConditionKey;
        private SettingItem mConditionSetting;
        private SettingItem mPictureSetting;

        public LowRamPictureRule(String str) {
            this.mConditionKey = str;
        }

        /* JADX WARN: Removed duplicated region for block: B:38:0x01a1  */
        @Override // com.mediatek.camera.ISettingRule
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        public void execute() {
            /*
                Method dump skipped, instructions count: 420
                To view this dump change 'Code comments level' option to 'DEBUG'
            */
            throw new UnsupportedOperationException("Method not decompiled: com.mediatek.camera.setting.rule.RuleContainer.LowRamPictureRule.execute():void");
        }

        @Override // com.mediatek.camera.ISettingRule
        public void addLimitation(String str, List<String> list, ISettingRule.MappingFinder mappingFinder) {
            this.mCondition = str;
        }

        private List<String> removeUnsupportedSize(CharSequence[] charSequenceArr, long j) {
            ArrayList arrayList = new ArrayList();
            for (CharSequence charSequence : charSequenceArr) {
                String string = charSequence.toString();
                int iIndexOf = string.indexOf(120);
                long j2 = Integer.parseInt(string.substring(0, iIndexOf));
                long j3 = Integer.parseInt(string.substring(iIndexOf + 1));
                if (j2 * j3 <= j) {
                    arrayList.add("" + j2 + "x" + j3);
                }
            }
            return arrayList;
        }
    }

    private class HDRZSDRule implements ISettingRule {
        public HDRZSDRule() {
        }

        @Override // com.mediatek.camera.ISettingRule
        public void execute() {
            String value = RuleContainer.this.mISettingCtrl.getSetting("pref_hdr_key").getValue();
            SettingItem setting = RuleContainer.this.mISettingCtrl.getSetting("pref_camera_zsd_key");
            int currentCameraId = RuleContainer.this.mICameraDeviceManager.getCurrentCameraId();
            Parameters parameters = RuleContainer.this.mICameraDeviceManager.getCameraDevice(currentCameraId).getParameters();
            boolean zIsZSDHDRSupported = RuleContainer.this.mICameraContext.getFeatureConfig().isZSDHDRSupported();
            Log.m34i("RuleContainer", "isZSDHDRSupported:" + zIsZSDHDRSupported);
            if (zIsZSDHDRSupported) {
                if ("on".equals(value)) {
                    RuleContainer.this.applyOverride("pref_hdr_key", "pref_camera_zsd_key", setting.getValue(), setting.getValue(), parameters, currentCameraId);
                } else {
                    RuleContainer.this.cancelOverride("pref_hdr_key", "pref_camera_zsd_key", parameters, currentCameraId);
                }
                if ("on".equals(setting.getValue()) && (!ParametersHelper.isSingleFrameCapHdrSupported(parameters))) {
                    ParametersHelper.setParametersValue(parameters, currentCameraId, "pref_hdr_key", "off");
                }
            }
            if (!zIsZSDHDRSupported) {
                if ("on".equals(value)) {
                    RuleContainer.this.applyOverride("pref_hdr_key", "pref_camera_zsd_key", "off", "off", parameters, currentCameraId);
                } else {
                    RuleContainer.this.cancelOverride("pref_hdr_key", "pref_camera_zsd_key", parameters, currentCameraId);
                }
            }
        }

        @Override // com.mediatek.camera.ISettingRule
        public void addLimitation(String str, List<String> list, ISettingRule.MappingFinder mappingFinder) {
        }
    }

    private class PictureRatioSizeRule implements ISettingRule {
        /* synthetic */ PictureRatioSizeRule(RuleContainer ruleContainer, PictureRatioSizeRule pictureRatioSizeRule) {
            this();
        }

        private PictureRatioSizeRule() {
        }

        @Override // com.mediatek.camera.ISettingRule
        public void execute() throws NumberFormatException {
            String str;
            String strBuildEnableList;
            Log.m31d("RuleContainer", "[PictureRatioSizeRule], execute");
            int currentCameraId = RuleContainer.this.mICameraDeviceManager.getCurrentCameraId();
            Parameters parameters = RuleContainer.this.mICameraDeviceManager.getCameraDevice(currentCameraId).getParameters();
            String value = RuleContainer.this.mISettingCtrl.getSetting("pref_camera_picturesize_ratio_key").getValue();
            SettingItem setting = RuleContainer.this.mISettingCtrl.getSetting("pref_camera_picturesize_key");
            String value2 = setting.getValue();
            List<String> listBuildSupportedPictureSizeByRatio = SettingUtils.buildSupportedPictureSizeByRatio(parameters, value);
            SettingUtils.filterLimitResolution(listBuildSupportedPictureSizeByRatio);
            SettingUtils.sortSizesInAscending(listBuildSupportedPictureSizeByRatio);
            if (listBuildSupportedPictureSizeByRatio.contains(value2)) {
                str = value2;
            } else {
                String str2 = listBuildSupportedPictureSizeByRatio.get(listBuildSupportedPictureSizeByRatio.size() - 1);
                setting.setValue(str2);
                str = str2;
            }
            ListPreference listPreference = setting.getListPreference();
            if (listBuildSupportedPictureSizeByRatio.size() == 1) {
                strBuildEnableList = str;
            } else if (listBuildSupportedPictureSizeByRatio.size() > 1) {
                strBuildEnableList = SettingUtils.buildEnableList((String[]) listBuildSupportedPictureSizeByRatio.toArray(new String[listBuildSupportedPictureSizeByRatio.size()]), str);
                if (listPreference != null) {
                    listPreference.setEnabled(true);
                }
            } else {
                strBuildEnableList = null;
            }
            if (listPreference != null) {
                listPreference.setOverrideValue(strBuildEnableList);
                listPreference.setValue(str);
            }
            Log.m31d("RuleContainer", "[PictureRatioSizeRule], set picture size, value:" + str + ", overrideValue:" + strBuildEnableList);
            ParametersHelper.setParametersValue(parameters, currentCameraId, "pref_camera_picturesize_key", str);
        }

        @Override // com.mediatek.camera.ISettingRule
        public void addLimitation(String str, List<String> list, ISettingRule.MappingFinder mappingFinder) {
        }
    }

    private class RecordingCameraModeRule implements ISettingRule {
        /* synthetic */ RecordingCameraModeRule(RuleContainer ruleContainer, RecordingCameraModeRule recordingCameraModeRule) {
            this();
        }

        private RecordingCameraModeRule() {
        }

        @Override // com.mediatek.camera.ISettingRule
        public void execute() {
            String value = RuleContainer.this.mISettingCtrl.getSetting("video_key").getValue();
            RuleContainer.this.mISettingCtrl.getSetting("camera_mode_key");
            int currentCameraId = RuleContainer.this.mICameraDeviceManager.getCurrentCameraId();
            Parameters parameters = RuleContainer.this.mICameraDeviceManager.getCameraDevice(currentCameraId).getParameters();
            Log.m34i("RuleContainer", "[RecordingCameraModeRule], execute, videoValue:" + value);
            if ("on".equals(value)) {
                RuleContainer.this.applyOverride("video_key", "camera_mode_key", String.valueOf(0), String.valueOf(0), parameters, currentCameraId);
                return;
            }
            RuleContainer.this.cancelOverride("video_key", "camera_mode_key", parameters, currentCameraId);
        }

        @Override // com.mediatek.camera.ISettingRule
        public void addLimitation(String str, List<String> list, ISettingRule.MappingFinder mappingFinder) {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setResultSettingValue(int i, String str, String str2, boolean z, SettingItem settingItem) {
        int currentCameraId = this.mICameraDeviceManager.getCurrentCameraId();
        Parameters parameters = this.mICameraDeviceManager.getCameraDevice(currentCameraId).getParameters();
        settingItem.setValue(str);
        ListPreference listPreference = settingItem.getListPreference();
        if (listPreference != null) {
            listPreference.setOverrideValue(str2, z);
        }
        ParametersHelper.setParametersValue(parameters, currentCameraId, settingItem.getKey(), str);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void applyOverride(String str, String str2, String str3, String str4, Parameters parameters, int i) {
        Log.m31d("RuleContainer", "[applyOverride], conditionKey:" + str + ", resultKey:" + str2 + ",overrideValue:" + str3 + ", value:" + str4);
        SettingItem setting = this.mISettingCtrl.getSetting(str2);
        ListPreference listPreference = setting.getListPreference();
        if (listPreference != null) {
            listPreference.setOverrideValue(str3);
        }
        setting.setValue(str4);
        ParametersHelper.setParametersValue(parameters, i, str2, str4);
        setting.getClass();
        setting.addOverrideRecord(str, setting.new Record(str3, str4));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void cancelOverride(String str, String str2, Parameters parameters, int i) {
        String value;
        String overrideValue = null;
        Log.m31d("RuleContainer", "[cancelOverride], conditionKey:" + str + ", resultKey:" + str2);
        SettingItem setting = this.mISettingCtrl.getSetting(str2);
        if (setting.getOverrideRecord(str) == null) {
            return;
        }
        setting.removeOverrideRecord(str);
        SettingItem.Record topOverrideRecord = setting.getTopOverrideRecord();
        if (topOverrideRecord != null) {
            overrideValue = topOverrideRecord.getOverrideValue();
            value = topOverrideRecord.getValue();
        } else {
            switch (setting.getType()) {
                case 0:
                case 1:
                    value = setting.getDefaultValue();
                    break;
                case 2:
                case 3:
                    ListPreference listPreference = setting.getListPreference();
                    if (listPreference == null) {
                        value = null;
                        break;
                    } else {
                        value = listPreference.getValue();
                        break;
                    }
                default:
                    value = null;
                    break;
            }
        }
        ListPreference listPreference2 = setting.getListPreference();
        if (listPreference2 != null) {
            listPreference2.setOverrideValue(overrideValue);
        }
        setting.setValue(value);
        ParametersHelper.setParametersValue(parameters, i, str2, value);
    }
}
