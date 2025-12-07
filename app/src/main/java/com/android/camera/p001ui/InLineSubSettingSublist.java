package com.android.camera.p001ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;
import com.android.camera.CameraActivity;
import com.android.camera.Log;
import com.android.camera.Util;
import com.mediatek.camera.R;
import com.mediatek.camera.setting.preference.ListPreference;

/* loaded from: classes.dex */
public class InLineSubSettingSublist extends InLineSettingSublist {
    private RotateImageView mImage;
    private TextView mTitleListname;

    public InLineSubSettingSublist(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mContext = (CameraActivity) context;
    }

    @Override // com.android.camera.p001ui.InLineSettingSublist, com.android.camera.p001ui.InLineSettingItem, android.view.View
    protected void onFinishInflate() {
        callSuperOnFinishInflate();
        this.mImage = (RotateImageView) findViewById(R.id.image);
        Util.setOrientation(this.mImage, this.mContext.getOrientationCompensation(), false);
        setOnClickListener(this.mOnClickListener);
    }

    @Override // com.android.camera.p001ui.InLineSettingSublist
    protected void setTextOrImage(int i, String str) {
        int iconId = this.mPreference.getIconId(i);
        if (iconId != -1) {
            this.mImage.setVisibility(0);
            this.mImage.setImageResource(iconId);
        } else {
            this.mImage.setVisibility(8);
        }
    }

    @Override // com.android.camera.p001ui.InLineSettingItem
    public void initialize(ListPreference listPreference) {
        Log.m5d("InLineSubSublist", "initialize(" + listPreference + ")");
        if (listPreference == null || this.mPreference == listPreference) {
            return;
        }
        this.mPreference = listPreference;
        reloadPreference();
    }

    @Override // com.android.camera.p001ui.InLineSettingSublist
    public boolean expendChild() {
        boolean z;
        if (this.mShowingChildList) {
            z = false;
        } else {
            this.mShowingChildList = true;
            if (this.mListener != null) {
                this.mListener.onShow(this);
            }
            this.mSettingLayout = (SettingSublistLayout) this.mContext.inflate(R.layout.sub_setting_sublist_layout, 3);
            this.mSettingContainer = this.mSettingLayout.findViewById(R.id.container);
            this.mTitleListname = (TextView) this.mSettingLayout.findViewById(R.id.ItemListTitle);
            this.mTitleListname.setText(this.mPreference.getTitle());
            this.mSettingLayout.initialize(this.mPreference);
            this.mContext.addView(this.mSettingLayout, 3);
            this.mContext.addOnOrientationListener(this);
            this.mSettingLayout.setSettingChangedListener(this);
            setOrientation(this.mContext.getOrientationCompensation(), false);
            fadeIn(this.mSettingLayout);
            highlight();
            z = true;
        }
        Log.m5d("InLineSubSublist", "expendChild() return " + z);
        return z;
    }

    @Override // com.android.camera.p001ui.InLineSettingSublist, com.android.camera.p001ui.InLineSettingItem
    public boolean collapseChild() {
        return super.collapseChild();
    }

    @Override // com.android.camera.p001ui.InLineSettingSublist
    protected void highlight() {
        if (this.mImage != null) {
            this.mImage.setBackgroundResource(R.drawable.ic_color_effects_focus);
        }
    }

    @Override // com.android.camera.p001ui.InLineSettingSublist
    protected void normalText() {
        if (this.mImage != null) {
            this.mImage.setBackground(null);
        }
    }
}
