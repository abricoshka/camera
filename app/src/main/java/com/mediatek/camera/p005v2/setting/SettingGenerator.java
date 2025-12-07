package com.mediatek.camera.p005v2.setting;

import com.mediatek.camera.debug.LogHelper;
import com.mediatek.camera.p005v2.util.SettingKeys;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* loaded from: classes.dex */
public class SettingGenerator {
    private static final LogHelper.Tag TAG = new LogHelper.Tag(SettingGenerator.class.getSimpleName());
    private Map<String, List<SettingItem>> mSettingItemMap = new HashMap();
    private String mCurrentCameraId = "0";

    public void initializeSettingItem(String[] strArr, String[] strArr2, Map<String, SettingCharacteristics> map) {
        for (String str : strArr2) {
            ArrayList arrayList = new ArrayList();
            SettingCharacteristics settingCharacteristics = map.get(str);
            for (String str2 : strArr) {
                SettingItem settingItem = new SettingItem(str2);
                arrayList.add(settingItem);
                int settingId = SettingKeys.getSettingId(str2);
                settingItem.setSettingId(settingId);
                String defaultValue = SettingDataBase.getDefaultValue(settingId);
                settingItem.setDefaultValue(defaultValue);
                settingItem.setLastValue(defaultValue);
                int settingType = SettingKeys.getSettingType(settingId);
                settingItem.setType(settingType);
                if (settingCharacteristics.getSupportedValues(str2) == null && settingType == 0) {
                    settingItem.setEnable(false);
                } else {
                    LogHelper.m26i(TAG, "SettingItem:" + settingItem.toString());
                }
            }
            this.mSettingItemMap.put(str, arrayList);
            getSettingItem("capture_mode_key", str).setValue("normal");
        }
    }

    public void updateCameraId(String str) {
        LogHelper.m26i(TAG, "[updateCameraId], cameraId:" + str + ", mCurrentCameraId:" + this.mCurrentCameraId);
        if (this.mCurrentCameraId != null && this.mCurrentCameraId.equals(str)) {
            return;
        }
        this.mCurrentCameraId = str;
    }

    public void configureSettingItems(String str) {
        List<SettingItem> list = this.mSettingItemMap.get(str);
        int i = 0;
        while (true) {
            int i2 = i;
            if (i2 < list.size()) {
                SettingItem settingItem = list.get(i2);
                settingItem.setLastValue(settingItem.getDefaultValue());
                settingItem.setOverrideValue(null);
                settingItem.clearAllOverrideRecord();
                i = i2 + 1;
            } else {
                return;
            }
        }
    }

    public SettingItem getSettingItem(String str) {
        return getSettingItem(str, this.mCurrentCameraId);
    }

    public SettingItem getSettingItem(String str, String str2) {
        if (str == null) {
            LogHelper.m28w(TAG, "the input key is null, return null.");
            return null;
        }
        List<SettingItem> list = this.mSettingItemMap.get(str2);
        int i = 0;
        while (true) {
            int i2 = i;
            if (i2 < list.size()) {
                SettingItem settingItem = list.get(i2);
                if (!str.equals(settingItem.getKey())) {
                    i = i2 + 1;
                } else {
                    return settingItem;
                }
            } else {
                LogHelper.m28w(TAG, "key:" + str + ", setting item return null");
                return null;
            }
        }
    }
}
