package com.android.camera.p001ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.widget.TextView;
import com.android.camera.CameraActivity;
import com.android.camera.Log;
import com.android.camera.SettingUtils;
import com.android.camera.Util;
import com.android.camera.p001ui.SettingVirtualLayout;
import com.mediatek.camera.R;
import com.mediatek.camera.setting.preference.ListPreference;

/* loaded from: classes.dex */
public class InLineSettingVirtual extends InLineSettingItem implements SettingVirtualLayout.Listener, CameraActivity.OnOrientationListener {
    private ListPreference[] mChildPrefs;
    private CameraActivity mContext;
    private TextView mEntry;
    private View.OnClickListener mOnClickListener;
    private View mSettingContainer;
    private SettingVirtualLayout mSettingLayout;
    private boolean mShowingChildList;

    public InLineSettingVirtual(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mOnClickListener = new View.OnClickListener() { // from class: com.android.camera.ui.InLineSettingVirtual.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                Log.m5d("InLineSettingVirtual", "onClick() mShowingChildList=" + InLineSettingVirtual.this.mShowingChildList + ", mPreference=" + InLineSettingVirtual.this.mPreference);
                if (!InLineSettingVirtual.this.mShowingChildList && InLineSettingVirtual.this.mChildPrefs != null && InLineSettingVirtual.this.mPreference != null && InLineSettingVirtual.this.mPreference.isClickable()) {
                    InLineSettingVirtual.this.expendChild();
                } else {
                    InLineSettingVirtual.this.collapseChild();
                }
            }
        };
        this.mContext = (CameraActivity) context;
    }

    @Override // com.android.camera.p001ui.InLineSettingItem, android.view.View
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

    @Override // com.android.camera.p001ui.InLineSettingItem
    public void initialize(ListPreference listPreference) {
        Log.m8i("InLineSettingVirtual", "initialize(" + listPreference + ")");
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
            Log.m5d("InLineSettingVirtual", "reloadPreference() mChildPrefs[" + i + "|" + strValueOf + "]=" + this.mChildPrefs[i]);
        }
        updateView();
    }

    @Override // com.android.camera.p001ui.InLineSettingItem
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
        Log.m5d("InLineSettingVirtual", "updateView() enableCount=" + i3 + ", len=" + length);
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
            this.mSettingLayout = (SettingVirtualLayout) this.mContext.inflate(R.layout.setting_virtual_layout, 3);
            this.mSettingContainer = this.mSettingLayout.findViewById(R.id.container);
            this.mSettingLayout.initialize(this.mChildPrefs);
            this.mContext.addView(this.mSettingLayout, 3);
            this.mContext.addOnOrientationListener(this);
            this.mSettingLayout.setSettingChangedListener(this);
            setOrientation(this.mContext.getOrientationCompensation(), false);
            fadeIn(this.mSettingLayout);
            highlight();
            z = true;
        }
        Log.m5d("InLineSettingVirtual", "expendChild() return " + z);
        return z;
    }

    @Override // com.android.camera.p001ui.InLineSettingItem
    public boolean collapseChild() {
        boolean z = false;
        if (this.mShowingChildList) {
            this.mContext.removeOnOrientationListener(this);
            this.mContext.removeView(this.mSettingLayout, 3);
            fadeOut(this.mSettingLayout);
            normalText();
            this.mShowingChildList = false;
            if (this.mListener != null) {
                this.mListener.onDismiss(this);
            }
            z = true;
        }
        Log.m5d("InLineSettingVirtual", "collapseChild() return " + z);
        return z;
    }

    private void highlight() {
        if (this.mTitle != null) {
            this.mTitle.setTextColor(SettingUtils.getMainColor(getContext()));
        }
        if (this.mEntry != null) {
            this.mEntry.setTextColor(SettingUtils.getMainColor(getContext()));
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

    @Override // com.android.camera.ui.SettingVirtualLayout.Listener
    public void onSettingChanged(ListPreference listPreference) {
        if (this.mListener != null) {
            this.mListener.onSettingChanged(this, listPreference);
        }
    }

    @Override // com.android.camera.CameraActivity.OnOrientationListener
    public void onOrientationChanged(int i) {
        setOrientation(i, true);
    }

    private void setOrientation(int i, boolean z) {
        Log.m5d("InLineSettingVirtual", "setOrientation(" + i + ", " + z + ")");
        if (this.mShowingChildList) {
            Util.setOrientation(this.mSettingLayout, i, z);
        }
    }

    @Override // com.android.camera.p001ui.InLineSettingItem, android.view.View
    public void setEnabled(boolean z) {
        super.setEnabled(z);
        if (!z) {
            collapseChild();
        }
    }
}
