package com.android.camera.p001ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import com.android.camera.Log;
import com.mediatek.camera.setting.preference.IconListPreference;
import com.mediatek.camera.setting.preference.ListPreference;

/* loaded from: classes.dex */
public class PickerButton extends RotateImageView implements View.OnClickListener {
    private boolean mForceEnable;
    protected Listener mListener;
    protected IconListPreference mPreference;

    public interface Listener {
        boolean onPicked(PickerButton pickerButton, ListPreference listPreference, String str);
    }

    public PickerButton(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mForceEnable = false;
    }

    public void setListener(Listener listener) {
        this.mListener = listener;
    }

    public void refresh() {
        if (!isVisible()) {
            setVisibility(8);
            return;
        }
        setOnClickListener(null);
        String overrideValue = this.mPreference.getOverrideValue();
        if (overrideValue == null) {
            overrideValue = this.mPreference.getValue();
        }
        int iFindIndexOfValue = this.mPreference.findIndexOfValue(overrideValue);
        int[] iconIds = this.mPreference.getIconIds();
        if (iconIds != null) {
            if (iFindIndexOfValue >= 0 && iFindIndexOfValue < iconIds.length) {
                setImageResource(iconIds[iFindIndexOfValue]);
            } else {
                setImageResource(iconIds[getValidIndexIfNotFind(overrideValue)]);
            }
        }
        setOnClickListener(this);
        setVisibility(0);
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        if (this.mPreference == null) {
            Log.m12w("PickerButton", "onClick() why mPreference is null?", new Throwable());
            return;
        }
        if (!this.mPreference.isEnabled() && (!this.mForceEnable)) {
            return;
        }
        String overrideValue = this.mPreference.getOverrideValue();
        if (overrideValue == null) {
            overrideValue = this.mPreference.getValue();
        }
        int iFindIndexOfValue = this.mPreference.findIndexOfValue(overrideValue);
        int validIndexIfNotFind = iFindIndexOfValue < 0 ? getValidIndexIfNotFind(overrideValue) : iFindIndexOfValue;
        CharSequence[] entryValues = this.mPreference.getEntryValues();
        int length = (validIndexIfNotFind + 1) % entryValues.length;
        String string = entryValues[length].toString();
        this.mPreference.setOverrideValue(null, false);
        if (this.mListener != null && this.mListener.onPicked(this, this.mPreference, string)) {
            this.mPreference.setValueIndex(length);
            refresh();
        }
    }

    public void setValue(String str) {
        if (this.mPreference != null && str != null && (!str.endsWith(this.mPreference.getValue()))) {
            this.mPreference.setValue(str);
            refresh();
        }
    }

    protected int getValidIndexIfNotFind(String str) {
        return 0;
    }

    private boolean isVisible() {
        if (this.mForceEnable) {
            return true;
        }
        return (this.mPreference == null || this.mPreference.getEntries() == null || this.mPreference.getEntries().length <= 1 || !this.mPreference.isEnabled() || this.mPreference.isShowInSetting()) ? false : true;
    }
}
