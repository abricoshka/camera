package com.android.camera.p002v2.uimanager.preference;

import android.content.Context;
import android.util.AttributeSet;

/* loaded from: classes.dex */
public class RecordLocationPreference extends IconListPreference {
    public RecordLocationPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @Override // com.android.camera.p002v2.uimanager.preference.ListPreference
    public String getValue() {
        return "on".equals(this.mValue) ? "on" : "off";
    }
}
