package com.mediatek.camera.setting.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import com.android.camera.R$styleable;
import com.mediatek.camera.setting.SettingUtils;
import com.mediatek.camera.util.Log;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class ListPreference extends CameraPreference {
    private boolean mClickable;
    private final CharSequence[] mDefaultValues;
    private boolean mEnabled;
    private CharSequence[] mEntries;
    private CharSequence[] mEntryValues;
    protected String[] mExtendedValues;
    protected boolean mIsShownInSetting;
    private final String mKey;
    private boolean mLoaded;
    private CharSequence[] mOriginalEntries;
    private CharSequence[] mOriginalEntryValues;
    private CharSequence[] mOriginalSupportedEntries;
    private CharSequence[] mOriginalSupportedEntryValues;
    private String mOverrideValue;
    protected String mValue;
    private boolean mVisibled;

    public ListPreference(Context context, AttributeSet attributeSet, SharedPreferencesTransfer sharedPreferencesTransfer) {
        super(context, attributeSet, sharedPreferencesTransfer);
        this.mIsShownInSetting = true;
        this.mLoaded = false;
        this.mEnabled = true;
        this.mClickable = true;
        this.mVisibled = true;
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R$styleable.ListPreference, 0, 0);
        this.mKey = typedArrayObtainStyledAttributes.getString(0);
        TypedValue typedValuePeekValue = typedArrayObtainStyledAttributes.peekValue(1);
        if (typedValuePeekValue != null && typedValuePeekValue.type == 1) {
            this.mDefaultValues = typedArrayObtainStyledAttributes.getTextArray(1);
        } else {
            this.mDefaultValues = new CharSequence[1];
            this.mDefaultValues[0] = typedArrayObtainStyledAttributes.getString(1);
        }
        setEntries(typedArrayObtainStyledAttributes.getTextArray(3));
        setEntryValues(typedArrayObtainStyledAttributes.getTextArray(2));
        typedArrayObtainStyledAttributes.recycle();
        this.mOriginalEntryValues = this.mEntryValues;
        this.mOriginalEntries = this.mEntries;
    }

    public void reloadValue() {
        this.mLoaded = false;
    }

    public String toString() {
        return "ListPreference(mKey=" + this.mKey + ", mTitle=" + getTitle() + ", mOverride=" + this.mOverrideValue + ", mEnable=" + this.mEnabled + ", mValue=" + this.mValue + ", mClickable=" + this.mClickable + ")";
    }

    public String getKey() {
        return this.mKey;
    }

    public CharSequence[] getEntries() {
        return this.mEntries;
    }

    public CharSequence[] getEntryValues() {
        return this.mEntryValues;
    }

    public void setOriginalEntries(CharSequence[] charSequenceArr) {
        if (charSequenceArr == null) {
            charSequenceArr = new CharSequence[0];
        }
        this.mOriginalEntries = charSequenceArr;
    }

    public void setOriginalEntryValues(CharSequence[] charSequenceArr) {
        if (charSequenceArr == null) {
            charSequenceArr = new CharSequence[0];
        }
        this.mOriginalEntryValues = charSequenceArr;
    }

    public void setEntries(CharSequence[] charSequenceArr) {
        if (charSequenceArr == null) {
            charSequenceArr = new CharSequence[0];
        }
        this.mEntries = charSequenceArr;
    }

    public void setEntryValues(CharSequence[] charSequenceArr) {
        if (charSequenceArr == null) {
            charSequenceArr = new CharSequence[0];
        }
        this.mEntryValues = charSequenceArr;
    }

    public String getValue() {
        if (!this.mLoaded) {
            String strFindSupportedDefaultValue = findSupportedDefaultValue();
            this.mValue = getSharedPreferences(this.mKey).getString(this.mKey, strFindSupportedDefaultValue);
            this.mLoaded = true;
            if (!isValid(this.mValue)) {
                this.mValue = strFindSupportedDefaultValue;
            }
        }
        return this.mValue;
    }

    public String getDefaultValue() {
        if (this.mDefaultValues == null || this.mDefaultValues.length <= 0 || this.mDefaultValues[0] == null) {
            return null;
        }
        return String.valueOf(this.mDefaultValues[0]);
    }

    public String findSupportedDefaultValue() {
        for (int i = 0; i < this.mDefaultValues.length; i++) {
            for (int i2 = 0; i2 < this.mEntryValues.length; i2++) {
                if (this.mEntryValues[i2].equals(this.mDefaultValues[i])) {
                    return this.mDefaultValues[i].toString();
                }
            }
        }
        return null;
    }

    public void setValue(String str) {
        if (findIndexOfValue(str) < 0) {
            throw new IllegalArgumentException();
        }
        this.mValue = str;
        persistStringValue(str);
    }

    public void setValueIndex(int i) {
        if (i < 0 || i >= this.mEntryValues.length) {
            Log.m37w("ListPreference", "setValueIndex(" + i + ")", new Throwable());
        } else {
            setValue(this.mEntryValues[i].toString());
        }
    }

    public int findIndexOfValue(String str) {
        int length = this.mEntryValues.length;
        for (int i = 0; i < length; i++) {
            if (SettingUtils.equals(this.mEntryValues[i], str)) {
                return i;
            }
        }
        Log.m36w("ListPreference", "[findIndexOfValue]" + str + ") not find!!");
        return -1;
    }

    public String getEntry() {
        int iFindIndexOfValue = findIndexOfValue(getValue());
        if (iFindIndexOfValue < 0 || iFindIndexOfValue >= this.mEntries.length) {
            Log.m37w("ListPreference", "[getEntry]", new Throwable());
            return null;
        }
        return this.mEntries[iFindIndexOfValue].toString();
    }

    public void filterUnsupported(List<String> list) {
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        int length = this.mOriginalEntryValues.length;
        for (int i = 0; i < length; i++) {
            if (list.indexOf(this.mOriginalEntryValues[i].toString()) >= 0) {
                arrayList.add(this.mOriginalEntries[i]);
                arrayList2.add(this.mOriginalEntryValues[i]);
            }
        }
        int size = arrayList.size();
        this.mEntries = (CharSequence[]) arrayList.toArray(new CharSequence[size]);
        this.mEntryValues = (CharSequence[]) arrayList2.toArray(new CharSequence[size]);
        this.mOriginalSupportedEntries = this.mEntries;
        this.mOriginalSupportedEntryValues = this.mEntryValues;
    }

    public void filterUnsupportedEntries(List<String> list) {
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        int length = this.mEntries.length;
        for (int i = 0; i < length; i++) {
            if (list.indexOf(this.mEntries[i]) >= 0) {
                arrayList.add(this.mEntries[i]);
                arrayList2.add(this.mEntryValues[i]);
            }
        }
        int size = arrayList.size();
        this.mEntries = (CharSequence[]) arrayList.toArray(new CharSequence[size]);
        this.mEntryValues = (CharSequence[]) arrayList2.toArray(new CharSequence[size]);
        this.mOriginalSupportedEntries = this.mEntries;
        this.mOriginalSupportedEntryValues = this.mEntryValues;
    }

    public void filterDisabled(List<String> list) {
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        int length = this.mOriginalSupportedEntryValues.length;
        for (int i = 0; i < length; i++) {
            if (list.indexOf(this.mOriginalSupportedEntryValues[i].toString()) >= 0) {
                arrayList.add(this.mOriginalSupportedEntries[i]);
                arrayList2.add(this.mOriginalSupportedEntryValues[i]);
            }
        }
        int size = arrayList.size();
        this.mEntries = (CharSequence[]) arrayList.toArray(new CharSequence[size]);
        this.mEntryValues = (CharSequence[]) arrayList2.toArray(new CharSequence[size]);
    }

    public void restoreSupported() {
        if (this.mOriginalSupportedEntries != null) {
            this.mEntries = this.mOriginalSupportedEntries;
        }
        if (this.mOriginalSupportedEntryValues != null) {
            this.mEntryValues = this.mOriginalSupportedEntryValues;
        }
    }

    public void setOverrideValue(String str, boolean z) {
        this.mOverrideValue = str;
        if (str == null) {
            this.mEnabled = true;
            if (z) {
                restoreSupported();
            }
        } else if (SettingUtils.isBuiltList(str)) {
            this.mOverrideValue = SettingUtils.getDefaultValue(str);
            filterDisabled(SettingUtils.getEnabledList(str));
            if (this.mEntryValues.length <= 1) {
                this.mEnabled = false;
            }
        } else if (SettingUtils.isDisableValue(str)) {
            this.mEnabled = false;
            this.mOverrideValue = null;
        } else {
            this.mEnabled = false;
            if (z) {
                restoreSupported();
            }
            if (this.mOverrideValue != null && findIndexOfValue(this.mOverrideValue) == -1) {
                this.mOverrideValue = findSupportedDefaultValue();
                Log.m36w("ListPreference", "setOverrideValue(" + str + ") not in list! mOverrideValue=" + this.mOverrideValue);
            }
        }
        this.mLoaded = false;
    }

    public void setOverrideValue(String str) {
        setOverrideValue(str, true);
    }

    public String getOverrideValue() {
        return this.mOverrideValue;
    }

    public int getIconId(int i) {
        return -1;
    }

    public boolean isEnabled() {
        return this.mEnabled;
    }

    public void setEnabled(boolean z) {
        this.mEnabled = z;
    }

    public boolean isVisibled() {
        return this.mVisibled;
    }

    public void setVisibled(boolean z) {
        this.mVisibled = z;
    }

    public CharSequence[] getOriginalEntryValues() {
        return this.mOriginalEntryValues;
    }

    public CharSequence[] getOriginalEntries() {
        return this.mOriginalEntries;
    }

    public CharSequence[] getOriginalSupportedEntryValues() {
        return this.mOriginalSupportedEntryValues;
    }

    public void setClickable(boolean z) {
        this.mClickable = z;
    }

    public boolean isClickable() {
        return this.mClickable;
    }

    public void showInSetting(boolean z) {
        this.mIsShownInSetting = z;
    }

    public boolean isShowInSetting() {
        return this.mIsShownInSetting;
    }

    public String[] getExtendedValues() {
        return this.mExtendedValues;
    }

    public void print() {
        if (this.mEntryValues == null || this.mDefaultValues == null) {
            Log.m36w("ListPreference", "[print]mEntryValues=" + this.mEntryValues + ", mDefaultValues=" + this.mDefaultValues);
            return;
        }
        Log.m35v("ListPreference", "[print] key=" + getKey() + ". value=" + getValue());
        for (int i = 0; i < this.mEntryValues.length; i++) {
            Log.m35v("ListPreference", "[print]entryValues[" + i + "]=" + this.mEntryValues[i]);
        }
        for (int i2 = 0; i2 < this.mDefaultValues.length; i2++) {
            Log.m35v("ListPreference", "[print]defaultValues[" + i2 + "]=" + this.mDefaultValues[i2]);
        }
    }

    private void persistStringValue(String str) {
        SharedPreferences.Editor editorEdit = getSharedPreferences(this.mKey).edit();
        editorEdit.putString(this.mKey, str);
        editorEdit.apply();
    }

    private boolean isValid(String str) {
        if ("pref_camera_id_key".equals(this.mKey) || "pref_video_quality_key".endsWith(this.mKey) || "pref_facebeauty_smooth_key".equals(this.mKey) || "pref_facebeauty_skin_color_key".equals(this.mKey) || "pref_facebeauty_sharp_key".equals(this.mKey)) {
            return true;
        }
        int length = this.mOriginalEntryValues.length;
        for (int i = 0; i < length; i++) {
            if (SettingUtils.equals(this.mOriginalEntryValues[i], str)) {
                return true;
            }
        }
        return false;
    }
}
