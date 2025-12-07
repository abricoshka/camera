package com.android.camera.manager;

import android.view.View;
import android.view.ViewGroup;
import com.android.camera.CameraActivity;
import com.android.camera.Log;
import com.android.camera.Util;
import com.android.camera.p001ui.RotateImageView;
import com.android.camera.p001ui.SubSettingLayout;
import com.mediatek.camera.R;
import com.mediatek.camera.platform.ICameraAppUi;
import com.mediatek.camera.setting.SettingConstants;

/* loaded from: classes.dex */
public class SubSettingManager extends SettingManager {
    private SubSettingLayout mPageView;

    public SubSettingManager(CameraActivity cameraActivity) {
        super(cameraActivity);
    }

    @Override // com.android.camera.manager.SettingManager, com.android.camera.manager.ViewManager
    protected View getView() {
        View viewInflate = inflate(R.layout.sub_setting_indicator);
        this.mIndicator = (RotateImageView) viewInflate.findViewById(R.id.sub_setting_indicator);
        this.mIndicator.setOnClickListener(this);
        return viewInflate;
    }

    @Override // com.android.camera.manager.SettingManager, android.view.View.OnClickListener
    public void onClick(View view) {
        if (view == this.mIndicator && !this.mShowingContainer) {
            showSetting();
        }
    }

    @Override // com.android.camera.manager.SettingManager, com.android.camera.manager.ViewManager
    public void onRefresh() {
        if (this.mShowingContainer) {
            notifyDataSetChanged();
        }
    }

    @Override // com.android.camera.manager.SettingManager, com.android.camera.manager.ViewManager
    public boolean collapse(boolean z) {
        boolean z2 = false;
        if (this.mShowingContainer && this.mPageView != null) {
            this.mPageView.collapseChild();
            hideSetting();
            z2 = true;
        }
        Log.m10v("SubSettingManager", "collapse(" + z + ") mShowingContainer=" + this.mShowingContainer + ", return " + z2);
        return z2;
    }

    @Override // com.android.camera.manager.SettingManager, com.android.camera.CameraActivity.OnPreferenceReadyListener
    public void onPreferenceReady() {
        releaseSettingResource();
    }

    @Override // com.android.camera.manager.SettingManager, com.android.camera.manager.ViewManager, com.android.camera.CameraActivity.OnOrientationListener
    public void onOrientationChanged(int i) {
        super.superOrientationChanged(i);
        Util.setOrientation(this.mPageView, getOrientation(), true);
        Util.setOrientation(this.mIndicator, getIndicatorOrientation(), true);
    }

    @Override // com.android.camera.manager.SettingManager, com.android.camera.manager.ViewManager
    public void show() {
        super.show();
        Util.setOrientation(this.mIndicator, getIndicatorOrientation(), false);
    }

    private int getIndicatorOrientation() {
        return getContext().getResources().getConfiguration().orientation == 2 ? 0 : 270;
    }

    @Override // com.android.camera.manager.SettingManager
    public void showSetting() {
        Log.m5d("SubSettingManager", "showSetting... start");
        if (getContext().isFullScreen()) {
            if (!this.mShowingContainer && getContext().getCameraAppUI().isNormalViewState()) {
                this.mMainHandler.removeMessages(0);
                this.mShowingContainer = true;
                initializeSettings();
                refresh();
                this.mSettingLayout.setVisibility(0);
                if (this.mSettingLayout.getParent() == null) {
                    Log.m8i("LeiLei", "showSetting getContext() = " + getContext());
                    getContext().addView(this.mSettingLayout, 3);
                }
                getContext().getCameraAppUI().setViewState(ICameraAppUi.ViewState.VIEW_STATE_SUB_SETTING);
                startFadeInAnimation(this.mSettingLayout);
                this.mIndicator.setVisibility(8);
            }
            setChildrenClickable(true);
            Log.m5d("SubSettingManager", "showSetting... end");
        }
    }

    private void initializeSettings() {
        if (this.mSettingLayout == null && this.mSettingController.getPreferenceGroup() != null) {
            this.mSettingLayout = (ViewGroup) getContext().inflate(R.layout.sub_setting_container, 3);
            this.mPageView = (SubSettingLayout) this.mSettingLayout.findViewById(R.id.sub_pager);
            this.mPageView.initialize(getSettinKeys(SettingConstants.SETTING_GROUP_SUB_COMMON), true);
        }
        Util.setOrientation(this.mPageView, getOrientation(), false);
    }

    private String[] getSettinKeys(int[] iArr) {
        String[] strArr = new String[iArr.length];
        for (int i = 0; i < iArr.length; i++) {
            strArr[i] = SettingConstants.getSettingKey(iArr[i]);
        }
        return strArr;
    }

    @Override // com.android.camera.manager.SettingManager
    public void hideSetting() {
        Log.m10v("SubSettingManager", "hideSetting() mShowingContainer=" + this.mShowingContainer + ", mSettingLayout=" + this.mSettingLayout);
        if (this.mShowingContainer && this.mSettingLayout != null) {
            this.mMainHandler.removeMessages(0);
            startFadeOutAnimation(this.mSettingLayout);
            this.mSettingLayout.setVisibility(8);
            this.mShowingContainer = false;
            if (getContext().getCameraAppUI().getViewState() == ICameraAppUi.ViewState.VIEW_STATE_SUB_SETTING) {
                getContext().getCameraAppUI().restoreViewState();
            }
            this.mMainHandler.sendEmptyMessageDelayed(0, 3000L);
        }
        setChildrenClickable(false);
        this.mIndicator.setVisibility(0);
    }

    public void notifyDataSetChanged() {
        this.mPageView.setSettingChangedListener(this);
        this.mPageView.reloadPreference();
    }
}
