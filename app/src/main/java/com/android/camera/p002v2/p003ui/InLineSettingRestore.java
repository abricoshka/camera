package com.android.camera.p002v2.p003ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;
import com.android.camera.p002v2.uimanager.preference.ListPreference;
import com.mediatek.camera.R;

/* loaded from: classes.dex */
public class InLineSettingRestore extends InLineSettingItem {
    public InLineSettingRestore(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @Override // com.android.camera.p002v2.p003ui.InLineSettingItem
    protected void setTitle(ListPreference listPreference) {
        ((TextView) findViewById(R.id.title)).setText(getContext().getString(R.string.pref_restore_detail));
    }

    @Override // com.android.camera.p002v2.p003ui.InLineSettingItem
    protected void updateView() {
    }
}
