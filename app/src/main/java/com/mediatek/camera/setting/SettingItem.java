package com.mediatek.camera.setting;

import com.mediatek.camera.setting.preference.ListPreference;
import java.util.HashMap;
import java.util.Iterator;

/* loaded from: classes.dex */
public class SettingItem {
    private int mSettingId;
    private int mType;
    private boolean mEnable = true;
    private String mKey = null;
    private String mLastValue = null;
    private String mValue = null;
    private String mDefaultValue = null;
    private int mOverrideCount = 0;
    private ListPreference mListPeference = null;
    private HashMap<String, Record> mOverrideRecord = new HashMap<>();

    public SettingItem(int i) {
        this.mSettingId = i;
    }

    public int getSettingId() {
        return this.mSettingId;
    }

    public void setKey(String str) {
        this.mKey = str;
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

    public void setOverrideCount(int i) {
        this.mOverrideCount = i;
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
        String next = it.hasNext() ? it.next() : null;
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

    public void setListPreference(ListPreference listPreference) {
        this.mListPeference = listPreference;
    }

    public ListPreference getListPreference() {
        return this.mListPeference;
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

    public String toString() {
        return "SettingItem(mKey=" + this.mKey + ", settingId=" + this.mSettingId + ", mType=" + this.mType + ", mValue=" + this.mValue + ", mEnable=" + this.mEnable + ", mListPeference=" + this.mListPeference + ")";
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
