package com.mediatek.camera.p005v2.setting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes.dex */
public class SettingItem {
    private String mKey;
    private int mSettingId;
    private int mType;
    private boolean mEnable = true;
    private String mLastValue = null;
    private String mValue = null;
    private String mDefaultValue = null;
    private String mOverrideValue = null;
    private List<SettingItem> mEffectedSetting = new ArrayList();
    private HashMap<String, Record> mOverrideRecord = new HashMap<>();

    public SettingItem(String str) {
        this.mKey = null;
        this.mKey = str;
    }

    public int getSettingId() {
        return this.mSettingId;
    }

    public void setSettingId(int i) {
        this.mSettingId = i;
    }

    public String getKey() {
        return this.mKey;
    }

    public void setValue(String str) {
        this.mValue = str;
    }

    public String getValue() {
        return this.mValue;
    }

    public void setLastValue(String str) {
        this.mLastValue = str;
    }

    public String getLastValue() {
        return this.mLastValue;
    }

    public void setOverrideValue(String str) {
        this.mOverrideValue = str;
    }

    public String getOverrideValue() {
        return this.mOverrideValue;
    }

    public void addEffectdSetting(SettingItem settingItem) {
        if (!this.mEffectedSetting.contains(settingItem)) {
            this.mEffectedSetting.add(settingItem);
        }
    }

    public int getOverrideCount() {
        return this.mOverrideRecord.size();
    }

    public void addOverrideRecord(String str, Record record) {
        this.mOverrideRecord.put(str, record);
    }

    public void removeOverrideRecord(String str) {
        this.mOverrideRecord.remove(str);
    }

    public Record getOverrideRecord(String str) {
        return this.mOverrideRecord.get(str);
    }

    public Record getTopOverrideRecord() {
        Iterator<String> it = this.mOverrideRecord.keySet().iterator();
        String next = null;
        while (it.hasNext()) {
            next = it.next();
        }
        if (next != null) {
            return this.mOverrideRecord.get(next);
        }
        return null;
    }

    public void clearAllOverrideRecord() {
        if (this.mOverrideRecord != null) {
            this.mOverrideRecord.clear();
        }
    }

    public void setDefaultValue(String str) {
        this.mDefaultValue = str;
    }

    public String getDefaultValue() {
        return this.mDefaultValue;
    }

    public void setType(int i) {
        this.mType = i;
    }

    public int getType() {
        return this.mType;
    }

    public void setEnable(boolean z) {
        this.mEnable = z;
    }

    public boolean isEnable() {
        return this.mEnable;
    }

    public class Record {
        private String mOverrideValue;
        private String mValue;

        public Record(String str, String str2) {
            this.mValue = str;
            this.mOverrideValue = str2;
        }

        public String getValue() {
            return this.mValue;
        }

        public String getOverrideValue() {
            return this.mOverrideValue;
        }
    }
}
