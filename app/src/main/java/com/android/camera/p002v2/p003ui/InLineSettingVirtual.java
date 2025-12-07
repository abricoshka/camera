package com.android.camera.p002v2.p003ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.widget.TextView;
import com.android.camera.p002v2.p003ui.SettingVirtualLayout;
import com.android.camera.p002v2.uimanager.preference.ListPreference;
import com.android.camera.p002v2.util.CameraUtil;
import com.mediatek.camera.R;
import com.mediatek.camera.debug.LogHelper;

/* loaded from: classes.dex */
public class InLineSettingVirtual extends InLineSettingItem implements SettingVirtualLayout.Listener {
    private static final LogHelper.Tag TAG = new LogHelper.Tag(InLineSettingVirtual.class.getSimpleName());
    private ListPreference[] mChildPrefs;
    private TextView mEntry;
    private View.OnClickListener mOnClickListener;
    private View mSettingContainer;
    private SettingVirtualLayout mSettingLayout;
    private boolean mShowingChildList;

    public InLineSettingVirtual(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mOnClickListener = new View.OnClickListener() { // from class: com.android.camera.v2.ui.InLineSettingVirtual.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                LogHelper.m23d(InLineSettingVirtual.TAG, "onClick() mShowingChildList=" + InLineSettingVirtual.this.mShowingChildList + ", mPreference=" + InLineSettingVirtual.this.mPreference);
                if (!InLineSettingVirtual.this.mShowingChildList && InLineSettingVirtual.this.mChildPrefs != null && InLineSettingVirtual.this.mPreference != null && InLineSettingVirtual.this.mPreference.isClickable()) {
                    InLineSettingVirtual.this.expendChild();
                } else {
                    InLineSettingVirtual.this.collapseChild();
                }
            }
        };
    }

    @Override // com.android.camera.p002v2.p003ui.InLineSettingItem, android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mEntry = (TextView) findViewById(R.id.current_setting);
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
        if (this.mPreference == null || this.mChildPrefs == null) {
            return;
        }
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
        if (z2) {
            this.mEntry.setText(this.mPreference.getDefaultValue());
        } else {
            this.mEntry.setText("");
        }
        this.mPreference.setEnabled(i3 == length);
        setEnabled(this.mPreference.isEnabled());
        setOnClickListener(this.mOnClickListener);
        LogHelper.m23d(TAG, "updateView() enableCount=" + i3 + ", len=" + length);
    }

    @Override // com.android.camera.v2.ui.SettingVirtualLayout.Listener
    public void onSettingChanged(ListPreference listPreference) {
        if (this.mListener != null) {
            this.mListener.onSettingChanged(this, listPreference);
        }
    }

    public boolean expendChild() {
        boolean z = false;
        if (!this.mShowingChildList) {
            this.mShowingChildList = true;
            if (this.mListener != null) {
                this.mListener.onShow(this);
            }
            this.mSettingLayout = (SettingVirtualLayout) LayoutInflater.from(getContext()).inflate(R.layout.setting_virtual_layout_v2, this.mRootView, false);
            this.mSettingContainer = this.mSettingLayout.findViewById(R.id.container);
            this.mSettingLayout.initialize(this.mChildPrefs);
            if (this.mRootView != null) {
                this.mRootView.addView(this.mSettingLayout);
            }
            this.mSettingLayout.setSettingChangedListener(this);
            fadeIn(this.mSettingLayout);
            highlight();
            z = true;
        }
        LogHelper.m23d(TAG, "expendChild() return " + z);
        return z;
    }

    @Override // com.android.camera.p002v2.p003ui.InLineSettingItem
    public boolean collapseChild() {
        boolean z = false;
        if (this.mShowingChildList) {
            if (this.mRootView != null) {
                this.mRootView.removeView(this.mSettingLayout);
            }
            fadeOut(this.mSettingLayout);
            normalText();
            this.mShowingChildList = false;
            if (this.mListener != null) {
                this.mListener.onDismiss(this);
            }
            z = true;
        }
        LogHelper.m23d(TAG, "collapseChild() return " + z);
        return z;
    }

    private void highlight() {
        if (this.mTitle != null) {
            this.mTitle.setTextColor(CameraUtil.getMainColor(getContext()));
        }
        if (this.mEntry != null) {
            this.mEntry.setTextColor(CameraUtil.getMainColor(getContext()));
        }
        setBackgroundDrawable(null);
    }

    private void normalText() {
        if (this.mTitle != null) {
            this.mTitle.setTextColor(getResources().getColor(R.color.tw_setting_item_text_color_normal));
        }
        if (this.mEntry != null) {
            this.mEntry.setTextColor(getResources().getColor(R.color.tw_setting_item_text_color_normal));
        }
        setBackgroundResource(R.drawable.setting_picker);
    }

    @Override // com.android.camera.p002v2.p003ui.InLineSettingItem, android.view.View
    public void setEnabled(boolean z) {
        super.setEnabled(z);
        if (!z) {
            collapseChild();
        }
    }
}
