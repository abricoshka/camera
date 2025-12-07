package com.android.camera.p002v2.uimanager.preference;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import com.android.camera.R$styleable;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class IconListPreference extends ListPreference {
    private int[] mIconIds;
    private int[] mOriginalIconIds;
    private int[] mOriginalSupportedIconIds;

    public IconListPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R$styleable.IconListPreference, 0, 0);
        this.mIconIds = getIds(context.getResources(), typedArrayObtainStyledAttributes.getResourceId(0, 0));
        typedArrayObtainStyledAttributes.recycle();
        this.mOriginalIconIds = this.mIconIds;
    }

    @Override // com.android.camera.p002v2.uimanager.preference.ListPreference
    public void filterUnsupported(List<String> list) {
        CharSequence[] originalEntryValues = getOriginalEntryValues();
        IntArray intArray = new IntArray();
        int length = originalEntryValues.length;
        for (int i = 0; i < length; i++) {
            if (list.indexOf(originalEntryValues[i].toString()) >= 0 && this.mIconIds != null) {
                intArray.add(this.mIconIds[i]);
            }
        }
        if (this.mIconIds != null) {
            this.mIconIds = intArray.toArray(new int[intArray.size()]);
            this.mOriginalSupportedIconIds = this.mIconIds;
        }
        super.filterUnsupported(list);
    }

    @Override // com.android.camera.p002v2.uimanager.preference.ListPreference
    public void filterDisabled(List<String> list) {
        CharSequence[] originalSupportedEntryValues = getOriginalSupportedEntryValues();
        IntArray intArray = new IntArray();
        int length = originalSupportedEntryValues.length;
        for (int i = 0; i < length; i++) {
            if (list.indexOf(originalSupportedEntryValues[i].toString()) >= 0 && this.mOriginalSupportedIconIds != null) {
                intArray.add(this.mOriginalSupportedIconIds[i]);
            }
        }
        if (this.mIconIds != null) {
            this.mIconIds = intArray.toArray(new int[intArray.size()]);
        }
        super.filterDisabled(list);
    }

    @Override // com.android.camera.p002v2.uimanager.preference.ListPreference
    public void setOriginalEntryValues(CharSequence[] charSequenceArr) {
        CharSequence[] originalEntries = getOriginalEntries();
        CharSequence[] originalEntryValues = getOriginalEntryValues();
        IntArray intArray = new IntArray();
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        int length = originalEntryValues.length;
        for (int i = 0; i < length; i++) {
            int i2 = 0;
            while (true) {
                if (i2 >= charSequenceArr.length) {
                    break;
                }
                if (!charSequenceArr[i2].equals(originalEntryValues[i])) {
                    i2++;
                } else {
                    arrayList.add(originalEntries[i]);
                    arrayList2.add(originalEntryValues[i]);
                    if (this.mIconIds != null) {
                        intArray.add(this.mIconIds[i]);
                    }
                }
            }
        }
        if (this.mIconIds != null) {
            this.mIconIds = intArray.toArray(new int[intArray.size()]);
            this.mOriginalIconIds = this.mIconIds;
        }
        int size = arrayList2.size();
        super.setOriginalEntries((CharSequence[]) arrayList.toArray(new CharSequence[size]));
        super.setOriginalEntryValues((CharSequence[]) arrayList2.toArray(new CharSequence[size]));
    }

    @Override // com.android.camera.p002v2.uimanager.preference.ListPreference
    public void restoreSupported() {
        super.restoreSupported();
        if (this.mOriginalSupportedIconIds != null) {
            this.mIconIds = this.mOriginalSupportedIconIds;
        }
    }

    @Override // com.android.camera.p002v2.uimanager.preference.ListPreference
    public int getIconId(int i) {
        if (this.mIconIds == null || i < 0 || i >= this.mIconIds.length) {
            return super.getIconId(i);
        }
        return this.mIconIds[i];
    }

    public int[] getOriginalIconIds() {
        return this.mOriginalIconIds;
    }

    public int[] getIconIds() {
        return this.mIconIds;
    }

    private int[] getIds(Resources resources, int i) throws Resources.NotFoundException {
        if (i == 0) {
            return null;
        }
        TypedArray typedArrayObtainTypedArray = resources.obtainTypedArray(i);
        int length = typedArrayObtainTypedArray.length();
        int[] iArr = new int[length];
        for (int i2 = 0; i2 < length; i2++) {
            iArr[i2] = typedArrayObtainTypedArray.getResourceId(i2, 0);
        }
        typedArrayObtainTypedArray.recycle();
        return iArr;
    }
}
