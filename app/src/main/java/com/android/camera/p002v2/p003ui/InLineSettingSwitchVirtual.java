package com.android.camera.p002v2.p003ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import com.android.camera.p002v2.p003ui.InLineSettingItem;
import com.android.camera.p002v2.p003ui.SettingSwitchVirtualLayout;
import com.android.camera.p002v2.uimanager.preference.ListPreference;
import com.mediatek.camera.R;
import com.mediatek.camera.debug.LogHelper;

/* loaded from: classes.dex */
public class InLineSettingSwitchVirtual extends InLineSettingItem implements SettingSwitchVirtualLayout.Listener {
    private static final LogHelper.Tag TAG = new LogHelper.Tag(InLineSettingSwitchVirtual.class.getSimpleName());
    CompoundButton.OnCheckedChangeListener mCheckedChangeListener;
    private ListPreference[] mChildPrefs;
    private boolean mIsSubListItemEnable;
    private View.OnClickListener mOnClickListener;
    private View mSettingContainer;
    private SettingSwitchVirtualLayout mSettingLayout;
    private boolean mShowingChildList;
    private Switch mSwitch;

    public InLineSettingSwitchVirtual(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() { // from class: com.android.camera.v2.ui.InLineSettingSwitchVirtual.1
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                InLineSettingSwitchVirtual.this.changeIndex(z ? 1 : 0);
            }
        };
        this.mOnClickListener = new View.OnClickListener() { // from class: com.android.camera.v2.ui.InLineSettingSwitchVirtual.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                LogHelper.m23d(InLineSettingSwitchVirtual.TAG, "onClick() mShowingChildList=" + InLineSettingSwitchVirtual.this.mShowingChildList + ", mPreference=" + InLineSettingSwitchVirtual.this.mPreference);
                if (InLineSettingSwitchVirtual.this.mShowingChildList || InLineSettingSwitchVirtual.this.mChildPrefs == null || InLineSettingSwitchVirtual.this.mPreference == null || !InLineSettingSwitchVirtual.this.mPreference.isClickable()) {
                    InLineSettingSwitchVirtual.this.collapseChild();
                } else {
                    InLineSettingSwitchVirtual.this.expendChild();
                }
            }
        };
    }

    @Override // com.android.camera.p002v2.p003ui.InLineSettingItem
    public boolean changeIndex(int i) {
        LogHelper.m26i(TAG, "switch index = " + i);
        ListPreference listPreference = this.mPreference;
        this.mIsSubListItemEnable = i == 1;
        return true;
    }

    @Override // com.android.camera.p002v2.p003ui.InLineSettingItem, android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mSwitch = (Switch) findViewById(R.id.virtual_switch);
        this.mSwitch.setOnCheckedChangeListener(this.mCheckedChangeListener);
        setOnClickListener(this.mOnClickListener);
    }

    @Override // android.view.View
    public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        onPopulateAccessibilityEvent(accessibilityEvent);
        return true;
    }

    @Override // android.view.View
    public void onPopulateAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        super.onPopulateAccessibilityEvent(accessibilityEvent);
        accessibilityEvent.getText().add(this.mPreference.getTitle() + this.mPreference.getEntry());
    }

    @Override // com.android.camera.p002v2.p003ui.InLineSettingItem
    public void initialize(ListPreference listPreference) {
        LogHelper.m26i(TAG, "initialize(" + listPreference + ")");
        setTitle(listPreference);
        if (listPreference == null) {
            this.mChildPrefs = null;
        } else {
            this.mPreference = listPreference;
            reloadPreference();
        }
    }

    @Override // com.android.camera.p002v2.p003ui.InLineSettingItem
    public void reloadPreference() {
        this.mChildPrefs = this.mPreference.getChildPreferences();
        if (this.mChildPrefs == null) {
            return;
        }
        for (ListPreference listPreference : this.mChildPrefs) {
            LogHelper.m23d(TAG, "reloadPreference() mChildPref=" + listPreference);
        }
        updateView();
    }

    @Override // com.android.camera.p002v2.p003ui.InLineSettingItem
    protected void updateView() {
        int i;
        boolean z;
        LogHelper.m23d(TAG, "updateView() mPreference = " + this.mPreference + ", mChildPrefs = " + this.mChildPrefs);
        if (this.mPreference == null || this.mChildPrefs == null) {
            return;
        }
        this.mSwitch.setOnCheckedChangeListener(null);
        setEnabled(this.mPreference.isEnabled());
        this.mSwitch.setOnCheckedChangeListener(this.mCheckedChangeListener);
        setOnClickListener(null);
        int length = this.mChildPrefs.length;
        int i2 = 0;
        int i3 = 0;
        boolean z2 = true;
        while (i2 < length) {
            ListPreference listPreference = this.mChildPrefs[i2];
            if (listPreference == null) {
                i = i3;
                z = z2;
            } else {
                String strValueOf = String.valueOf(this.mPreference.getEntryValues()[i2]);
                String overrideValue = listPreference.getOverrideValue();
                if (overrideValue == null) {
                    overrideValue = listPreference.getValue();
                }
                if (listPreference.isEnabled()) {
                    i3++;
                }
                if (!z2) {
                    i = i3;
                    z = z2;
                } else if (!strValueOf.equals(overrideValue)) {
                    i = i3;
                    z = false;
                } else {
                    i = i3;
                    z = z2;
                }
            }
            i2++;
            z2 = z;
            i3 = i;
        }
        this.mPreference.setEnabled(i3 == length);
        setEnabled(this.mPreference.isEnabled());
        setOnClickListener(this.mOnClickListener);
        LogHelper.m23d(TAG, "updateView() enableCount=" + i3 + ", len=" + length);
    }

    public boolean expendChild() {
        boolean z;
        if (this.mShowingChildList) {
            z = false;
        } else {
            this.mShowingChildList = true;
            if (this.mListener != null) {
                this.mListener.onShow(this);
            }
            this.mSettingLayout = (SettingSwitchVirtualLayout) LayoutInflater.from(getContext()).inflate(R.layout.setting_switch_virtual_layout_v2, this.mRootView, false);
            this.mSettingContainer = this.mSettingLayout.findViewById(R.id.container);
            this.mSettingLayout.initialize(this.mChildPrefs, this.mIsSubListItemEnable);
            if (this.mRootView != null) {
                this.mRootView.addView(this.mSettingLayout);
            }
            this.mSettingLayout.setSettingChangedListener(this);
            fadeIn(this.mSettingLayout);
            this.mSwitch.setClickable(false);
            highlight();
            z = true;
        }
        LogHelper.m23d(TAG, "expendChild() return " + z);
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
            normalText();
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

    private void highlight() {
        TextView textView = this.mTitle;
        setBackgroundDrawable(null);
    }

    private void normalText() {
        if (this.mTitle != null) {
            this.mTitle.setTextColor(getResources().getColor(R.color.tw_setting_item_text_color_normal));
        }
        setBackgroundResource(R.drawable.setting_picker);
    }

    @Override // com.android.camera.v2.ui.SettingSwitchVirtualLayout.Listener
    public void onStereoCameraSettingChanged(int i, boolean z) {
        InLineSettingItem.Listener listener = this.mListener;
    }

    @Override // com.android.camera.p002v2.p003ui.InLineSettingItem, android.view.View
    public void setEnabled(boolean z) {
        super.setEnabled(z);
        this.mSwitch.setEnabled(z);
        if (!z) {
            collapseChild();
        }
    }
}
