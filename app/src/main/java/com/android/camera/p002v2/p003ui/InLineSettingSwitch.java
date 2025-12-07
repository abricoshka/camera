package com.android.camera.p002v2.p003ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.widget.CompoundButton;
import android.widget.Switch;
import com.android.camera.p002v2.p003ui.InLineSettingItem;
import com.android.camera.p002v2.p003ui.SettingSwitchSublistLayout;
import com.android.camera.p002v2.uimanager.preference.ListPreference;
import com.mediatek.camera.R;
import com.mediatek.camera.debug.LogHelper;

/* loaded from: classes.dex */
public class InLineSettingSwitch extends InLineSettingItem implements SettingSwitchSublistLayout.Listener {
    private static final LogHelper.Tag TAG = new LogHelper.Tag(InLineSettingSwitch.class.getSimpleName());
    CompoundButton.OnCheckedChangeListener mCheckedChangeListener;
    private View.OnClickListener mOnClickListener;
    private SettingSwitchSublistLayout mSettingLayout;
    private boolean mShowingChildList;
    private Switch mSwitch;

    public InLineSettingSwitch(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() { // from class: com.android.camera.v2.ui.InLineSettingSwitch.1
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                InLineSettingSwitch.this.changeIndex(z ? 1 : 0);
            }
        };
        this.mOnClickListener = new View.OnClickListener() { // from class: com.android.camera.v2.ui.InLineSettingSwitch.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                LogHelper.m23d(InLineSettingSwitch.TAG, "onClick() mPreference=" + InLineSettingSwitch.this.mPreference);
                if (InLineSettingSwitch.this.mPreference != null && InLineSettingSwitch.this.mPreference.isClickable() && InLineSettingSwitch.this.mPreference.isEnabled()) {
                    if (InLineSettingSwitch.this.mListener != null) {
                        InLineSettingSwitch.this.mListener.onShow(InLineSettingSwitch.this);
                    }
                    if (InLineSettingSwitch.this.mPreference.getKey().equals("pref_voice_key")) {
                        if (!InLineSettingSwitch.this.mShowingChildList) {
                            InLineSettingSwitch.this.extendChild();
                            return;
                        } else {
                            InLineSettingSwitch.this.collapseChild();
                            return;
                        }
                    }
                    if (InLineSettingSwitch.this.mSwitch != null) {
                        InLineSettingSwitch.this.mSwitch.performClick();
                    }
                }
            }
        };
    }

    @Override // com.android.camera.p002v2.p003ui.InLineSettingItem, android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mSwitch = (Switch) findViewById(R.id.setting_switch);
        this.mSwitch.setOnCheckedChangeListener(this.mCheckedChangeListener);
        setOnClickListener(this.mOnClickListener);
    }

    @Override // com.android.camera.p002v2.p003ui.InLineSettingItem
    public void initialize(ListPreference listPreference) {
        super.initialize(listPreference);
        this.mSwitch.setContentDescription(getContext().getResources().getString(R.string.accessibility_switch, this.mPreference.getTitle()));
    }

    @Override // com.android.camera.p002v2.p003ui.InLineSettingItem
    protected void updateView() {
        this.mSwitch.setOnCheckedChangeListener(null);
        this.mOverrideValue = this.mPreference.getOverrideValue();
        if (this.mOverrideValue == null) {
            this.mSwitch.setChecked(this.mIndex == 1);
        } else {
            this.mSwitch.setChecked(this.mPreference.findIndexOfValue(this.mOverrideValue) == 1);
        }
        setEnabled(this.mPreference.isEnabled());
        this.mSwitch.setOnCheckedChangeListener(this.mCheckedChangeListener);
    }

    @Override // com.android.camera.p002v2.p003ui.InLineSettingItem, android.view.View
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

    public boolean extendChild() {
        boolean z;
        if (this.mShowingChildList) {
            z = false;
        } else {
            this.mShowingChildList = true;
            this.mSettingLayout = (SettingSwitchSublistLayout) LayoutInflater.from(getContext()).inflate(R.layout.setting_switch_sublist_layout_v2, this.mRootView, false);
            this.mSettingLayout.initialize(this.mPreference);
            if (this.mRootView != null) {
                this.mRootView.addView(this.mSettingLayout);
            }
            this.mSettingLayout.setSettingChangedListener(this);
            fadeIn(this.mSettingLayout);
            this.mSwitch.setClickable(false);
            z = true;
        }
        LogHelper.m23d(TAG, "extendChild() return " + z);
        return z;
    }

    @Override // com.android.camera.p002v2.p003ui.InLineSettingItem
    public boolean collapseChild() {
        boolean z = true;
        if (this.mShowingChildList) {
            if (this.mRootView != null) {
                this.mRootView.removeView(this.mSettingLayout);
            }
            fadeOut(this.mSettingLayout);
            this.mSettingLayout = null;
            this.mShowingChildList = false;
            if (this.mListener != null) {
                this.mListener.onDismiss(this);
            }
            this.mSwitch.setClickable(true);
        } else {
            z = false;
        }
        LogHelper.m23d(TAG, "collapseChild() return " + z);
        return z;
    }

    @Override // com.android.camera.v2.ui.SettingSwitchSublistLayout.Listener
    public void onVoiceCommandChanged(int i) {
        InLineSettingItem.Listener listener = this.mListener;
    }
}
