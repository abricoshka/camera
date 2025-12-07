package com.android.camera.p001ui;

import android.content.Context;
import android.provider.Settings;
import android.util.AttributeSet;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.widget.CompoundButton;
import android.widget.Switch;
import com.android.camera.CameraActivity;
import com.android.camera.Log;
import com.android.camera.Util;
import com.mediatek.camera.R;
import com.mediatek.camera.setting.preference.ListPreference;

/* loaded from: classes.dex */
public class InLineSettingSwitch extends InLineSettingItem implements CameraActivity.OnOrientationListener {
    CompoundButton.OnCheckedChangeListener mCheckedChangeListener;
    private CameraActivity mContext;
    private View.OnClickListener mOnClickListener;
    private SettingSwitchSublistLayout mSettingLayout;
    private boolean mShowingChildList;
    private Switch mSwitch;

    public InLineSettingSwitch(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() { // from class: com.android.camera.ui.InLineSettingSwitch.1
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                InLineSettingSwitch.this.changeIndex(z ? 1 : 0);
            }
        };
        this.mOnClickListener = new View.OnClickListener() { // from class: com.android.camera.ui.InLineSettingSwitch.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                Log.m5d("InLineSettingSwitch", "onClick() mPreference=" + InLineSettingSwitch.this.mPreference);
                if (InLineSettingSwitch.this.mPreference != null && InLineSettingSwitch.this.mPreference.isClickable() && InLineSettingSwitch.this.mPreference.isEnabled()) {
                    if (InLineSettingSwitch.this.mListener != null) {
                        InLineSettingSwitch.this.mListener.onShow(InLineSettingSwitch.this);
                    }
                    if (InLineSettingSwitch.this.mSwitch != null) {
                        InLineSettingSwitch.this.mSwitch.performClick();
                    }
                }
            }
        };
        this.mContext = (CameraActivity) context;
    }

    @Override // com.android.camera.p001ui.InLineSettingItem, android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mSwitch = (Switch) findViewById(R.id.setting_switch);
        this.mSwitch.setOnCheckedChangeListener(this.mCheckedChangeListener);
        setOnClickListener(this.mOnClickListener);
    }

    @Override // com.android.camera.p001ui.InLineSettingItem
    public void initialize(ListPreference listPreference) {
        super.initialize(listPreference);
        this.mSwitch.setContentDescription(getContext().getResources().getString(R.string.accessibility_switch, this.mPreference.getTitle()));
    }

    @Override // com.android.camera.p001ui.InLineSettingItem
    protected void updateView() {
        if ("pref_video_stabilization_key".equals(this.mPreference.getKey())) {
            this.mIndex = Settings.Secure.getInt(this.mContext.getContentResolver(), this.mPreference.getKey(), 0);
        }
        this.mSwitch.setOnCheckedChangeListener(null);
        this.mOverrideValue = this.mPreference.getOverrideValue();
        if (this.mOverrideValue == null) {
            this.mSwitch.setChecked(this.mIndex == 1);
        } else {
            int iFindIndexOfValue = this.mPreference.findIndexOfValue(this.mOverrideValue);
            if ("pref_video_stabilization_key".equals(this.mPreference.getKey())) {
                iFindIndexOfValue = Settings.Secure.getInt(this.mContext.getContentResolver(), this.mPreference.getKey(), 0);
            }
            this.mSwitch.setChecked(iFindIndexOfValue == 1);
        }
        setEnabled(this.mPreference.isEnabled());
        this.mSwitch.setOnCheckedChangeListener(this.mCheckedChangeListener);
    }

    @Override // com.android.camera.p001ui.InLineSettingItem, android.view.View
    public void setEnabled(boolean z) {
        super.setEnabled(z);
        if (this.mSwitch != null) {
            this.mSwitch.setEnabled(z);
        }
    }

    @Override // android.view.View
    public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        onPopulateAccessibilityEvent(accessibilityEvent);
        return true;
    }

    @Override // android.view.View
    public void onPopulateAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        super.onPopulateAccessibilityEvent(accessibilityEvent);
        accessibilityEvent.getText().add(this.mPreference.getTitle());
    }

    @Override // com.android.camera.CameraActivity.OnOrientationListener
    public void onOrientationChanged(int i) {
        setOrientation(i, true);
    }

    private void setOrientation(int i, boolean z) {
        Log.m5d("InLineSettingSwitch", "setOrientation(" + i + "," + z + ")");
        if (this.mShowingChildList) {
            Util.setOrientation(this.mSettingLayout, i, z);
        }
    }
}
