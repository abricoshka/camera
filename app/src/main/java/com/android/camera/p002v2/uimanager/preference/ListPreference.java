package com.android.camera.p002v2.uimanager.preference;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import com.android.camera.R$styleable;
import com.android.camera.p002v2.util.CameraUtil;
import com.mediatek.camera.debug.LogHelper;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class ListPreference extends CameraPreference {
    private static final LogHelper.Tag TAG = new LogHelper.Tag(ListPreference.class.getSimpleName());
    private ListPreference[] mChildPreferences;
    private boolean mClickable;
    private String mDefaultValue;
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

    public ListPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
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
            this.mDefaultValue = typedArrayObtainStyledAttributes.getString(1);
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
        String strFindSupportedDefaultValue = findSupportedDefaultValue();
        if (this.mValue == null) {
            return strFindSupportedDefaultValue;
        }
        return this.mValue;
    }

    public String getDefaultValue() {
        return this.mDefaultValue;
    }

    public void setDefaultValue(String str) {
        this.mDefaultValue = str;
    }

    private String findSupportedDefaultValue() {
        for (int i = 0; i < this.mDefaultValues.length; i++) {
            for (int i2 = 0; i2 < this.mEntryValues.length; i2++) {
                if (this.mEntryValues[i2].equals(this.mDefaultValues[i])) {
                    return this.mDefaultValues[i].toString();
                }
            }
        }
        for (int i3 = 0; i3 < this.mDefaultValues.length; i3++) {
            LogHelper.m26i(TAG, "default value:" + this.mDefaultValues[i3]);
        }
        return this.mEntryValues[0].toString();
    }

    public void setValue(String str) {
        this.mValue = str;
    }

    public void setValueIndex(int i) {
        if (i < 0 || i >= this.mEntryValues.length) {
            print();
            LogHelper.m29w(TAG, "setValueIndex(" + i + ")", new Throwable());
        } else {
            setValue(this.mEntryValues[i].toString());
        }
    }

    public int findIndexOfValue(String str) {
        int length = this.mEntryValues.length;
        for (int i = 0; i < length; i++) {
            if (CameraUtil.equals(this.mEntryValues[i], str)) {
                return i;
            }
        }
        print();
        LogHelper.m28w(TAG, "[findIndexOfValue]" + str + ") not find!!");
        return -1;
    }

    public String getEntry() {
        int iFindIndexOfValue = findIndexOfValue(getValue());
        if (iFindIndexOfValue < 0 || iFindIndexOfValue >= this.mEntries.length) {
            print();
            LogHelper.m29w(TAG, "[getEntry]", new Throwable());
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
        LogHelper.m23d(TAG, "[restoreSupported]mOriginalSupportedEntries=" + this.mOriginalSupportedEntries);
        if (this.mOriginalSupportedEntries != null) {
            this.mEntries = this.mOriginalSupportedEntries;
        }
        if (this.mOriginalSupportedEntryValues != null) {
            this.mEntryValues = this.mOriginalSupportedEntryValues;
        }
    }

    public void setOverrideValue(String str, boolean z) {
        LogHelper.m23d(TAG, "[setOverrideValue], key: + " + this.mKey + ", override =" + str + ", restoreSupported =" + z);
        this.mOverrideValue = str;
        if (str == null) {
            this.mEnabled = true;
            if (z) {
                restoreSupported();
            }
        } else if (CameraUtil.isBuiltList(str)) {
            this.mEnabled = true;
            this.mOverrideValue = CameraUtil.getDefaultValue(str);
            filterDisabled(CameraUtil.getEnabledList(str));
        } else if (CameraUtil.isDisableValue(str)) {
            this.mEnabled = false;
            this.mOverrideValue = null;
        } else {
            this.mEnabled = false;
            if (z) {
                restoreSupported();
            }
            if (this.mOverrideValue != null && findIndexOfValue(this.mOverrideValue) == -1) {
                this.mOverrideValue = findSupportedDefaultValue();
                LogHelper.m28w(TAG, "override value:" + str + " do not in entry values, override value change to default value");
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
        LogHelper.m23d(TAG, "setEnabled(" + z + ")");
        this.mEnabled = z;
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

    public boolean isClickable() {
        return this.mClickable;
    }

    public void showInSetting(boolean z) {
        this.mIsShownInSetting = z;
    }

    public boolean isShowInSetting() {
        return this.mIsShownInSetting;
    }

    public boolean isVisibled() {
        return this.mVisibled;
    }

    public String[] getExtendedValues() {
        return this.mExtendedValues;
    }

    public void setChildPreferences(ListPreference[] listPreferenceArr) {
        this.mChildPreferences = listPreferenceArr;
    }

    public ListPreference[] getChildPreferences() {
        return this.mChildPreferences;
    }

    public void print() {
        if (this.mEntryValues == null || this.mDefaultValues == null) {
            LogHelper.m28w(TAG, "[print]mEntryValues=" + this.mEntryValues + ", mDefaultValues=" + this.mDefaultValues);
            return;
        }
        LogHelper.m27v(TAG, "[print] key=" + getKey() + ". value=" + getValue());
        for (int i = 0; i < this.mEntryValues.length; i++) {
            LogHelper.m27v(TAG, "[print]entryValues[" + i + "]=" + this.mEntryValues[i]);
        }
        for (int i2 = 0; i2 < this.mDefaultValues.length; i2++) {
            LogHelper.m27v(TAG, "[print]defaultValues[" + i2 + "]=" + this.mDefaultValues[i2]);
        }
    }
}
