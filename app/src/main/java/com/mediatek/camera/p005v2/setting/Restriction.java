package com.mediatek.camera.p005v2.setting;

import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class Restriction {
    private List<Restriction> mRestrictions;
    private final int mSettingIndex;
    private List<String> mValues;
    private int mType = 0;
    private boolean mEnable = true;

    public Restriction(int i) {
        this.mSettingIndex = i;
    }

    public int getIndex() {
        return this.mSettingIndex;
    }

    public List<String> getValues() {
        return this.mValues;
    }

    public List<Restriction> getRestrictioins() {
        return this.mRestrictions;
    }

    public Restriction setEnable(boolean z) {
        this.mEnable = z;
        return this;
    }

    public Restriction setType(int i) {
        this.mType = i;
        return this;
    }

    public Restriction setValues(String... strArr) {
        if (strArr != null) {
            this.mValues = new ArrayList();
            for (String str : strArr) {
                this.mValues.add(str);
            }
        }
        return this;
    }

    public Restriction setRestrictions(Restriction... restrictionArr) {
        if (restrictionArr != null) {
            this.mRestrictions = new ArrayList();
            for (Restriction restriction : restrictionArr) {
                this.mRestrictions.add(restriction);
            }
        }
        return this;
    }
}
