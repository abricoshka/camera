package com.mediatek.camera.p005v2.setting.rule;

import com.mediatek.camera.debug.LogHelper;
import com.mediatek.camera.p005v2.setting.ISettingRule;
import com.mediatek.camera.p005v2.setting.SettingCtrl;
import com.mediatek.camera.p005v2.setting.SettingItem;
import com.mediatek.camera.p005v2.util.SettingKeys;
import com.mediatek.camera.p005v2.util.Utils;
import com.mediatek.camera.v2.setting.SettingItem.Record;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class CommonRule implements ISettingRule {
    private static final LogHelper.Tag TAG = new LogHelper.Tag(CommonRule.class.getSimpleName());
    private String mConditionKey;
    private SettingItem mConditionSetting;
    private String mResultKey;
    private SettingItem mResultSetting;
    private SettingCtrl mSettingCtrl;
    private List<String> mConditions = new ArrayList();
    private List<List<String>> mResults = new ArrayList();

    public CommonRule(String str, String str2, SettingCtrl settingCtrl) {
        this.mConditionKey = str;
        this.mResultKey = str2;
        this.mSettingCtrl = settingCtrl;
    }

    @Override // com.mediatek.camera.p005v2.setting.ISettingRule
    public void execute() {
        String resultSettingValue;
        String strBuildEnableList;
        String overrideValue = null;
        this.mConditionSetting = this.mSettingCtrl.getSettingItem(this.mConditionKey);
        this.mResultSetting = this.mSettingCtrl.getSettingItem(this.mResultKey);
        String value = this.mConditionSetting.getValue();
        int iConditionSatisfied = conditionSatisfied(value);
        String value2 = this.mResultSetting.getValue();
        LogHelper.m26i(TAG, "[execute], conditionSetting:" + this.mConditionKey + ", conditionValue:" + value + ", resultSetting:" + this.mResultKey + ", resultSettingValue:" + value2 + ", index = " + iConditionSatisfied);
        if (iConditionSatisfied == -1) {
            if (this.mResultSetting.getOverrideRecord(this.mConditionKey) == null) {
                LogHelper.m26i(TAG, "[execute], no override record, return");
                return;
            }
            this.mResultSetting.removeOverrideRecord(this.mConditionKey);
            if (this.mResultSetting.getOverrideCount() > 0) {
                SettingItem.Record topOverrideRecord = this.mResultSetting.getTopOverrideRecord();
                if (topOverrideRecord != null) {
                    value2 = topOverrideRecord.getValue();
                    overrideValue = topOverrideRecord.getOverrideValue();
                }
            } else {
                value2 = this.mSettingCtrl.getSharePreferenceValue(this.mResultKey);
                if (value2 == null) {
                    value2 = this.mResultSetting.getDefaultValue();
                }
            }
            this.mResultSetting.setValue(value2);
            this.mResultSetting.setOverrideValue(overrideValue);
            LogHelper.m26i(TAG, "[execute], result: value = " + value2 + ", overrideValue =" + overrideValue);
            return;
        }
        List<String> list = this.mResults.get(iConditionSatisfied);
        LogHelper.m26i(TAG, "[execute], resultValues:" + list);
        if (list != null && list.size() == 1 && "disable-value".equals(list.get(0))) {
            strBuildEnableList = "disable-value";
            resultSettingValue = this.mResultSetting.getValue();
        } else {
            List<String> listFilterUnsupportedValue = filterUnsupportedValue(list, this.mResultKey);
            if (listFilterUnsupportedValue.size() == 0) {
                LogHelper.m26i(TAG, "[execute], resultValuesAfterFilter is null");
                return;
            } else {
                resultSettingValue = getResultSettingValue(listFilterUnsupportedValue, iConditionSatisfied);
                strBuildEnableList = listFilterUnsupportedValue.size() == 1 ? resultSettingValue : Utils.buildEnableList((String[]) listFilterUnsupportedValue.toArray(new String[listFilterUnsupportedValue.size()]));
            }
        }
        this.mResultSetting.setValue(resultSettingValue);
        this.mResultSetting.setOverrideValue(strBuildEnableList);
        SettingItem settingItem = this.mResultSetting;
        settingItem.getClass();
        this.mResultSetting.addOverrideRecord(this.mConditionKey, settingItem.new Record(resultSettingValue, strBuildEnableList));
        LogHelper.m26i(TAG, "[execute], result: value = " + resultSettingValue + ", overrideValue =" + strBuildEnableList);
    }

    @Override // com.mediatek.camera.p005v2.setting.ISettingRule
    public void addLimitation(String str, List<String> list) {
        this.mConditions.add(str);
        this.mResults.add(list);
    }

    private int conditionSatisfied(String str) {
        return this.mConditions.indexOf(str);
    }

    private String getResultSettingValue(List<String> list, int i) {
        String value = this.mResultSetting.getValue();
        if (!list.contains(value)) {
            if ("pref_camera_picturesize_ratio_key".equals(this.mConditionKey) && "pref_camera_picturesize_key".equals(this.mResultKey)) {
                String str = list.get(list.size() - 1);
                this.mSettingCtrl.setSharedPreferencesValue("pref_camera_picturesize_key", str);
                return str;
            }
            return list.get(0);
        }
        return value;
    }

    private List<String> filterUnsupportedValue(List<String> list, String str) {
        if (1 == SettingKeys.getSettingType(str)) {
            return list;
        }
        List<String> supportedValues = this.mSettingCtrl.getSupportedValues(str);
        ArrayList arrayList = new ArrayList();
        if (supportedValues == null) {
            return arrayList;
        }
        for (String str2 : list) {
            if (supportedValues.contains(str2)) {
                arrayList.add(str2);
            }
        }
        return arrayList;
    }
}
