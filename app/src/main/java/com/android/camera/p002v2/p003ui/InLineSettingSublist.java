package com.android.camera.p002v2.p003ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.camera.p002v2.p003ui.SettingSublistLayout;
import com.android.camera.p002v2.util.CameraUtil;
import com.mediatek.camera.R;
import com.mediatek.camera.debug.LogHelper;

/* loaded from: classes.dex */
public class InLineSettingSublist extends InLineSettingItem implements SettingSublistLayout.Listener {
    private static final LogHelper.Tag TAG = new LogHelper.Tag(InLineSettingSublist.class.getSimpleName());
    private Context mContext;
    private TextView mEntry;
    private ImageView mImage;
    protected View.OnClickListener mOnClickListener;
    private View mSettingContainer;
    private SettingSublistLayout mSettingLayout;
    protected boolean mShowingChildList;

    public InLineSettingSublist(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mOnClickListener = new View.OnClickListener() { // from class: com.android.camera.v2.ui.InLineSettingSublist.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                LogHelper.m23d(InLineSettingSublist.TAG, "onClick() mShowingChildList=" + InLineSettingSublist.this.mShowingChildList + ", mPreference=" + InLineSettingSublist.this.mPreference);
                if (!InLineSettingSublist.this.mShowingChildList && InLineSettingSublist.this.mPreference != null && InLineSettingSublist.this.mPreference.isClickable()) {
                    InLineSettingSublist.this.expendChild();
                } else {
                    InLineSettingSublist.this.collapseChild();
                }
            }
        };
        this.mContext = context;
    }

    @Override // com.android.camera.p002v2.p003ui.InLineSettingItem, android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mEntry = (TextView) findViewById(R.id.current_setting);
        this.mImage = (ImageView) findViewById(R.id.image);
        setOnClickListener(this.mOnClickListener);
    }

    @Override // com.android.camera.p002v2.p003ui.InLineSettingItem
    protected void updateView() {
        if (this.mPreference == null) {
            return;
        }
        setOnClickListener(null);
        String overrideValue = this.mPreference.getOverrideValue();
        if (overrideValue == null) {
            setTextOrImage(this.mIndex, this.mPreference.getEntry());
        } else {
            int iFindIndexOfValue = this.mPreference.findIndexOfValue(overrideValue);
            if (iFindIndexOfValue != -1) {
                setTextOrImage(iFindIndexOfValue, String.valueOf(this.mPreference.getEntries()[iFindIndexOfValue]));
            } else {
                LogHelper.m24e(TAG, "Fail to find override value=" + overrideValue);
                this.mPreference.print();
            }
        }
        setEnabled(this.mPreference.isEnabled());
        setOnClickListener(this.mOnClickListener);
    }

    protected void setTextOrImage(int i, String str) {
        int iconId = this.mPreference.getIconId(i);
        if (iconId != -1) {
            this.mEntry.setVisibility(8);
            this.mImage.setVisibility(0);
            this.mImage.setImageResource(iconId);
        } else {
            this.mEntry.setVisibility(0);
            this.mEntry.setText(str);
            this.mImage.setVisibility(8);
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
        accessibilityEvent.getText().add(this.mPreference.getTitle() + this.mPreference.getEntry());
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
            this.mSettingLayout = (SettingSublistLayout) LayoutInflater.from(getContext()).inflate(R.layout.setting_sublist_layout_v2, this.mRootView, false);
            this.mSettingContainer = this.mSettingLayout.findViewById(R.id.container);
            this.mSettingLayout.initialize(this.mPreference);
            if (this.mRootView != null) {
                this.mRootView.addView(this.mSettingLayout);
                CameraUtil.setOrientation(this.mSettingLayout, ((Integer) this.mRootView.getTag()).intValue(), false);
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

    protected void highlight() {
        if (this.mTitle != null) {
            this.mTitle.setTextColor(CameraUtil.getMainColor(getContext()));
        }
        if (this.mEntry != null) {
            this.mEntry.setTextColor(CameraUtil.getMainColor(getContext()));
        }
        setBackgroundDrawable(null);
    }

    protected void normalText() {
        if (this.mTitle != null) {
            this.mTitle.setTextColor(getResources().getColor(R.color.tw_setting_item_text_color_normal));
        }
        if (this.mEntry != null) {
            this.mEntry.setTextColor(getResources().getColor(R.color.tw_setting_item_text_color_normal));
        }
        setBackgroundResource(R.drawable.setting_picker);
    }

    @Override // com.android.camera.v2.ui.SettingSublistLayout.Listener
    public void onSettingChanged(boolean z) {
        LogHelper.m23d(TAG, "onSettingChanged(" + z + ") mListener=" + this.mListener);
        if (this.mListener != null && z) {
            this.mListener.onSettingChanged(this, this.mPreference);
        }
        collapseChild();
    }

    @Override // com.android.camera.p002v2.p003ui.InLineSettingItem, android.view.View
    public void setEnabled(boolean z) {
        super.setEnabled(z);
        if (!z) {
            collapseChild();
        }
    }
}
