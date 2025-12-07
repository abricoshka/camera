package com.mediatek.camera.setting.rule;

import android.hardware.Camera;
import com.mediatek.camera.ISettingRule;
import com.mediatek.camera.platform.ICameraDeviceManager;
import com.mediatek.camera.platform.Parameters;
import com.mediatek.camera.setting.ParametersHelper;
import com.mediatek.camera.setting.SettingGenerator;
import com.mediatek.camera.setting.SettingItem;
import com.mediatek.camera.setting.SettingUtils;
import com.mediatek.camera.setting.preference.ListPreference;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/* loaded from: classes.dex */
public class CommonRule implements ISettingRule {
    private String mConditionKey;
    private SettingItem mConditionSetting;
    private ICameraDeviceManager mICameraDeviceManager;
    private String mResultKey;
    private SettingItem mResultSetting;
    private SettingGenerator mSettingGenerator;
    private boolean mRestoreSupported = true;
    private List<String> mConditions = new ArrayList();
    private List<List<String>> mResults = new ArrayList();
    private List<ISettingRule.MappingFinder> mMappingFinder = new ArrayList();

    public CommonRule(String str, String str2, ICameraDeviceManager iCameraDeviceManager, SettingGenerator settingGenerator) {
        this.mConditionKey = str;
        this.mResultKey = str2;
        this.mICameraDeviceManager = iCameraDeviceManager;
        this.mSettingGenerator = settingGenerator;
    }

    /* JADX WARN: Removed duplicated region for block: B:51:0x0139  */
    @Override // com.mediatek.camera.ISettingRule
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public void execute() throws java.lang.NumberFormatException {
        /*
            Method dump skipped, instructions count: 328
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mediatek.camera.setting.rule.CommonRule.execute():void");
    }

    @Override // com.mediatek.camera.ISettingRule
    public void addLimitation(String str, List<String> list, ISettingRule.MappingFinder mappingFinder) {
        this.mConditions.add(str);
        this.mResults.add(list);
        this.mMappingFinder.add(mappingFinder);
    }

    private int conditionSatisfied(String str) {
        return this.mConditions.indexOf(str);
    }

    private void setResultSettingValue(int i, String str, String str2, boolean z) {
        int currentCameraId = this.mICameraDeviceManager.getCurrentCameraId();
        Parameters parameters = this.mICameraDeviceManager.getCameraDevice(currentCameraId).getParameters();
        this.mResultSetting.setValue(str);
        switch (i) {
            case 1:
                ParametersHelper.setParametersValue(parameters, currentCameraId, this.mResultSetting.getKey(), str);
                break;
            case 2:
                if ("disable-value".equals(str2)) {
                    this.mResultSetting.getListPreference().setEnabled(false);
                    break;
                } else {
                    this.mResultSetting.getListPreference().setOverrideValue(str2, z);
                    break;
                }
            case 3:
                ListPreference listPreference = this.mResultSetting.getListPreference();
                if ("disable-value".equals(str2)) {
                    if (listPreference != null) {
                        listPreference.setEnabled(false);
                        break;
                    }
                } else {
                    if (listPreference != null) {
                        if (this.mResultKey.equals("pref_camera_flashmode_key")) {
                            listPreference.setOverrideValue(str2, false);
                        } else {
                            listPreference.setOverrideValue(str2, z);
                        }
                    }
                    ParametersHelper.setParametersValue(parameters, currentCameraId, this.mResultSetting.getKey(), str);
                    break;
                }
                break;
        }
    }

    private String getResultSettingValue(List<String> list, int i) {
        ICameraDeviceManager.ICameraDevice cameraDevice = this.mICameraDeviceManager.getCameraDevice(this.mICameraDeviceManager.getCurrentCameraId());
        String value = this.mResultSetting.getValue();
        if (cameraDevice == null) {
            return value;
        }
        String parametersValue = value == null ? ParametersHelper.getParametersValue(cameraDevice.getParameters(), this.mResultSetting.getKey()) : value;
        ISettingRule.MappingFinder mappingFinder = this.mMappingFinder.get(i);
        if (mappingFinder != null) {
            return mappingFinder.find(parametersValue, list);
        }
        return !list.contains(parametersValue) ? list.get(0) : parametersValue;
    }

    private List<String> filterUnsupportedValue(List<String> list, String str) throws NumberFormatException {
        if (list.size() == 1 && "disable-value".equals(list.get(0))) {
            return list;
        }
        Parameters parameters = this.mICameraDeviceManager.getCameraDevice(this.mICameraDeviceManager.getCurrentCameraId()).getParameters();
        ArrayList arrayList = new ArrayList();
        if (str.equals("pref_camera_picturesize_key")) {
            List<String> listSizeListToStringList = sizeListToStringList(parameters.getSupportedPictureSizes());
            if (SettingUtils.getLimitResolution() > 0) {
                SettingUtils.filterLimitResolution(listSizeListToStringList);
            }
            int size = list.size();
            for (int i = 0; i < size; i++) {
                if (listSizeListToStringList.contains(list.get(i))) {
                    arrayList.add(list.get(i));
                }
            }
            return arrayList;
        }
        return list;
    }

    private List<String> sizeListToStringList(List<Camera.Size> list) {
        ArrayList arrayList = new ArrayList();
        for (Camera.Size size : list) {
            arrayList.add(String.format(Locale.ENGLISH, "%dx%d", Integer.valueOf(size.width), Integer.valueOf(size.height)));
        }
        return arrayList;
    }
}
