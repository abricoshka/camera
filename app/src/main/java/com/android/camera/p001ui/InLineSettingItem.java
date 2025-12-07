package com.android.camera.p001ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.android.camera.SettingUtils;
import com.mediatek.camera.R;
import com.mediatek.camera.setting.preference.ListPreference;

/* loaded from: classes.dex */
public abstract class InLineSettingItem extends RelativeLayout {
    protected Animation mFadeIn;
    protected Animation mFadeOut;
    protected int mIndex;
    protected Listener mListener;
    protected String mOverrideValue;
    protected ListPreference mPreference;
    protected TextView mTitle;

    public interface Listener {
        void onDismiss(InLineSettingItem inLineSettingItem);

        void onSettingChanged(InLineSettingItem inLineSettingItem, ListPreference listPreference);

        void onShow(InLineSettingItem inLineSettingItem);

        void onStereoCameraSettingChanged(InLineSettingItem inLineSettingItem, ListPreference listPreference, int i, boolean z);
    }

    protected abstract void updateView();

    public InLineSettingItem(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mTitle = (TextView) findViewById(R.id.title);
        this.mTitle.setHorizontallyScrolling(false);
    }

    protected void callSuperOnFinishInflate() {
        super.onFinishInflate();
    }

    protected void setTitle(ListPreference listPreference) {
        if (listPreference != null) {
            this.mTitle.setText(listPreference.getTitle());
        } else {
            this.mTitle.setText((CharSequence) null);
        }
    }

    public void initialize(ListPreference listPreference) {
        setTitle(listPreference);
        if (listPreference == null) {
            return;
        }
        this.mPreference = listPreference;
        reloadPreference();
    }

    protected boolean changeIndex(int i) {
        if (i >= this.mPreference.getEntryValues().length || i < 0) {
            return false;
        }
        this.mIndex = i;
        this.mPreference.setValueIndex(this.mIndex);
        if (this.mListener != null) {
            this.mListener.onSettingChanged(this, this.mPreference);
        }
        updateView();
        sendAccessibilityEvent(4);
        return true;
    }

    public void reloadPreference() {
        if (this.mPreference != null) {
            this.mPreference.reloadValue();
            this.mIndex = this.mPreference.findIndexOfValue(this.mPreference.getValue());
            updateView();
        }
    }

    public void setSettingChangedListener(Listener listener) {
        this.mListener = listener;
    }

    public boolean collapseChild() {
        return false;
    }

    @Override // android.view.View
    public void setEnabled(boolean z) {
        super.setEnabled(z);
        SettingUtils.setEnabledState(this, z);
    }

    public void fadeOut(View view) {
        if (view == null) {
            return;
        }
        if (this.mFadeOut == null) {
            this.mFadeOut = AnimationUtils.loadAnimation(getContext(), R.anim.setting_popup_shrink_fade_out);
        }
        if (this.mFadeOut != null) {
            view.startAnimation(this.mFadeOut);
        }
    }

    public void fadeIn(View view) {
        if (view == null) {
            return;
        }
        if (this.mFadeIn == null) {
            this.mFadeIn = AnimationUtils.loadAnimation(getContext(), R.anim.setting_popup_grow_fade_in);
        }
        if (this.mFadeIn != null) {
            view.startAnimation(this.mFadeIn);
        }
    }
}
