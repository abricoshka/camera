package com.android.camera.p001ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import com.android.camera.CameraActivity;
import com.android.camera.Log;
import com.android.camera.Util;
import com.android.camera.p001ui.SettingSwitchVirtualLayout;
import com.mediatek.camera.R;
import com.mediatek.camera.setting.SettingUtils;
import com.mediatek.camera.setting.preference.ListPreference;

/* loaded from: classes.dex */
public class InLineSettingSwitchVirtual extends InLineSettingItem implements SettingSwitchVirtualLayout.Listener, CameraActivity.OnOrientationListener {
    CompoundButton.OnCheckedChangeListener mCheckedChangeListener;
    private ListPreference[] mChildPrefs;
    private CameraActivity mContext;
    private boolean mIsSubListItemEnable;
    private View.OnClickListener mOnClickListener;
    private View mSettingContainer;
    private SettingSwitchVirtualLayout mSettingLayout;
    private boolean mShowingChildList;
    private Switch mSwitch;

    public InLineSettingSwitchVirtual(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() { // from class: com.android.camera.ui.InLineSettingSwitchVirtual.1
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                InLineSettingSwitchVirtual.this.changeIndex(z ? 1 : 0);
            }
        };
        this.mOnClickListener = new View.OnClickListener() { // from class: com.android.camera.ui.InLineSettingSwitchVirtual.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                Log.m5d("InLineSettingSwitchVirtual", "onClick() mShowingChildList=" + InLineSettingSwitchVirtual.this.mShowingChildList + ", mPreference=" + InLineSettingSwitchVirtual.this.mPreference);
                if (!InLineSettingSwitchVirtual.this.mShowingChildList && InLineSettingSwitchVirtual.this.mChildPrefs != null && InLineSettingSwitchVirtual.this.mPreference != null && InLineSettingSwitchVirtual.this.mPreference.isClickable()) {
                    InLineSettingSwitchVirtual.this.expendChild();
                } else {
                    InLineSettingSwitchVirtual.this.collapseChild();
                }
            }
        };
        this.mContext = (CameraActivity) context;
    }

    @Override // com.android.camera.p001ui.InLineSettingItem
    public boolean changeIndex(int i) {
        Log.m8i("Refocus", "switch index = " + i);
        if (this.mPreference != null) {
            if (i == 1) {
                SettingUtils.writePreferredStereoCamera(this.mContext.getPreferences(), "on");
                if (this.mListener != null) {
                    this.mListener.onStereoCameraSettingChanged(this, this.mPreference, 3, false);
                }
            } else {
                SettingUtils.writePreferredStereoCamera(this.mContext.getPreferences(), "off");
                if (this.mListener != null) {
                    this.mListener.onStereoCameraSettingChanged(this, this.mPreference, 4, false);
                }
            }
        }
        this.mIsSubListItemEnable = i == 1;
        return true;
    }

    @Override // com.android.camera.p001ui.InLineSettingItem, android.view.View
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

    @Override // com.android.camera.p001ui.InLineSettingItem
    public void initialize(ListPreference listPreference) {
        Log.m8i("InLineSettingSwitchVirtual", "initialize(" + listPreference + ")");
        setTitle(listPreference);
        if (listPreference == null) {
            this.mChildPrefs = null;
        } else {
            this.mPreference = listPreference;
            reloadPreference();
        }
    }

    @Override // com.android.camera.p001ui.InLineSettingItem
    public void reloadPreference() {
        int length = this.mPreference.getEntries().length;
        this.mChildPrefs = new ListPreference[length];
        for (int i = 0; i < length; i++) {
            String strValueOf = String.valueOf(this.mPreference.getEntries()[i]);
            this.mChildPrefs[i] = this.mContext.getListPreference(strValueOf);
            Log.m5d("InLineSettingSwitchVirtual", "reloadPreference() mChildPrefs[" + i + "|" + strValueOf + "]=" + this.mChildPrefs[i]);
        }
        updateView();
    }

    @Override // com.android.camera.p001ui.InLineSettingItem
    protected void updateView() {
        int i;
        boolean z;
        Log.m5d("InLineSettingSwitchVirtual", "updateView() mPreference = " + this.mPreference + ", mChildPrefs = " + this.mChildPrefs);
        if (this.mPreference == null || this.mChildPrefs == null) {
            return;
        }
        this.mSwitch.setOnCheckedChangeListener(null);
        if (!needUpdateSwitch() && SettingUtils.readPreferredStereoCamera(this.mContext.getPreferences()).equals("on")) {
            this.mIsSubListItemEnable = true;
            this.mSwitch.setChecked(true);
        } else if (!needUpdateSwitch() || SettingUtils.readPreferredStereoCamera(this.mContext.getPreferences()).equals("off")) {
            this.mIsSubListItemEnable = false;
            this.mSwitch.setChecked(false);
        } else {
            this.mIsSubListItemEnable = false;
            this.mSwitch.setChecked(false);
        }
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
        Log.m5d("InLineSettingSwitchVirtual", "updateView() enableCount=" + i3 + ", len=" + length);
    }

    private boolean needUpdateSwitch() {
        if (this.mChildPrefs == null) {
            return false;
        }
        for (int i = 0; i < this.mChildPrefs.length; i++) {
            if (this.mChildPrefs[i].findIndexOfValue(this.mChildPrefs[i].getValue()) == 0) {
                Log.m5d("InLineSettingSwitchVirtual", "need not update Switch");
                return false;
            }
        }
        return true;
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
            this.mSettingLayout = (SettingSwitchVirtualLayout) this.mContext.inflate(R.layout.setting_switch_virtual_layout, 3);
            this.mSettingContainer = this.mSettingLayout.findViewById(R.id.container);
            this.mSettingLayout.initialize(this.mChildPrefs, this.mIsSubListItemEnable);
            this.mContext.addView(this.mSettingLayout, 3);
            this.mContext.addOnOrientationListener(this);
            this.mSettingLayout.setSettingChangedListener(this);
            setOrientation(this.mContext.getOrientationCompensation(), false);
            fadeIn(this.mSettingLayout);
            this.mSwitch.setClickable(false);
            highlight();
            z = true;
        }
        Log.m5d("InLineSettingSwitchVirtual", "expendChild() return " + z);
        return z;
    }

    @Override // com.android.camera.p001ui.InLineSettingItem
    public boolean collapseChild() {
        boolean z = true;
        if (this.mShowingChildList) {
            this.mContext.removeOnOrientationListener(this);
            this.mContext.removeView(this.mSettingLayout, 3);
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
        Log.m5d("InLineSettingSwitchVirtual", "collapseChild() return " + z);
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

    @Override // com.android.camera.ui.SettingSwitchVirtualLayout.Listener
    public void onStereoCameraSettingChanged(int i, boolean z) {
        if (this.mListener != null) {
            if (!z && SettingUtils.readPreferredStereoCamera(this.mContext.getPreferences()).equals("on")) {
                SettingUtils.writePreferredStereoCamera(this.mContext.getPreferences(), "off");
            }
            if (z && SettingUtils.readPreferredStereoCamera(this.mContext.getPreferences()).equals("off")) {
                SettingUtils.writePreferredStereoCamera(this.mContext.getPreferences(), "on");
            }
            this.mListener.onStereoCameraSettingChanged(this, this.mPreference, i, true);
        }
    }

    @Override // com.android.camera.CameraActivity.OnOrientationListener
    public void onOrientationChanged(int i) {
        setOrientation(i, true);
    }

    private void setOrientation(int i, boolean z) {
        Log.m5d("InLineSettingSwitchVirtual", "setOrientation(" + i + ", " + z + ")");
        if (this.mShowingChildList) {
            Util.setOrientation(this.mSettingLayout, i, z);
        }
    }

    @Override // com.android.camera.p001ui.InLineSettingItem, android.view.View
    public void setEnabled(boolean z) {
        super.setEnabled(z);
        this.mSwitch.setEnabled(z);
        if (!z) {
            collapseChild();
        }
    }
}
