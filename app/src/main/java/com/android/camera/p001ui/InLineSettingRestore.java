package com.android.camera.p001ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;
import com.mediatek.camera.R;
import com.mediatek.camera.setting.preference.ListPreference;

/* loaded from: classes.dex */
public class InLineSettingRestore extends InLineSettingItem {
    public InLineSettingRestore(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @Override // com.android.camera.p001ui.InLineSettingItem
    protected void setTitle(ListPreference listPreference) {
        ((TextView) findViewById(R.id.title)).setText(getContext().getString(R.string.pref_restore_detail));
    }

    @Override // com.android.camera.p001ui.InLineSettingItem
    protected void updateView() {
    }
}
