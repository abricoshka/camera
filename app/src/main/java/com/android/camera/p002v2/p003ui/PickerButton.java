package com.android.camera.p002v2.p003ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import com.android.camera.p002v2.uimanager.preference.IconListPreference;
import com.android.camera.p002v2.uimanager.preference.ListPreference;
import com.mediatek.camera.debug.LogHelper;

/* loaded from: classes.dex */
public class PickerButton extends RotateImageView implements View.OnClickListener {
    private static final LogHelper.Tag TAG = new LogHelper.Tag(PickerButton.class.getSimpleName());
    protected Listener mListener;
    protected IconListPreference mPreference;

    public interface Listener {
        boolean onPicked(PickerButton pickerButton, ListPreference listPreference, String str);
    }

    public PickerButton(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public void setListener(Listener listener) {
        this.mListener = listener;
    }

    public void initialize(IconListPreference iconListPreference) {
        LogHelper.m23d(TAG, "initialize(" + iconListPreference + ")");
        this.mPreference = iconListPreference;
    }

    public void refresh() {
        LogHelper.m23d(TAG, "refresh() " + this.mPreference);
        if (this.mPreference == null || (!this.mPreference.isVisibled()) || (!this.mPreference.isEnabled()) || this.mPreference.isShowInSetting()) {
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
            LogHelper.m29w(TAG, "onClick() why mPreference is null?", new Throwable());
            return;
        }
        if (!this.mPreference.isEnabled()) {
            LogHelper.m26i(TAG, "onClick() mPreference's enable = false ,return this click event");
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

    protected int getValidIndexIfNotFind(String str) {
        return 0;
    }
}
