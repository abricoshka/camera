package com.android.camera.manager;

import android.view.View;
import android.view.animation.Animation;
import com.android.camera.CameraActivity;
import com.android.camera.SettingUtils;
import com.android.camera.Util;

/* loaded from: classes.dex */
public abstract class ViewManager implements CameraActivity.OnOrientationListener {
    private int mConfigOrientation;
    private CameraActivity mContext;
    private boolean mEnabled;
    private Animation mFadeIn;
    private Animation mFadeOut;
    private boolean mFilter;
    private boolean mHideAnimationEnabled;
    private int mOrientation;
    private boolean mShowAnimationEnabled;
    protected boolean mShowing;
    private View mView;
    private final int mViewLayer;

    protected abstract View getView();

    public ViewManager(CameraActivity cameraActivity, int i) {
        this.mEnabled = true;
        this.mFilter = true;
        this.mShowAnimationEnabled = true;
        this.mHideAnimationEnabled = true;
        this.mConfigOrientation = -1;
        this.mContext = cameraActivity;
        this.mContext.addViewManager(this);
        this.mContext.addOnOrientationListener(this);
        this.mOrientation = this.mContext.getOrientationCompensation();
        this.mViewLayer = i;
    }

    public ViewManager(CameraActivity cameraActivity) {
        this(cameraActivity, 0);
    }

    @Override // com.android.camera.CameraActivity.OnOrientationListener
    public void onOrientationChanged(int i) {
        if (this.mOrientation != i) {
            this.mOrientation = i;
            Util.setOrientation(this.mView, this.mOrientation, true);
        }
    }

    public void show() {
        if (this.mView == null) {
            this.mConfigOrientation = this.mContext.getResources().getConfiguration().orientation;
            this.mView = getView();
            if (this.mView != null) {
                getContext().addView(this.mView, this.mViewLayer);
                Util.setOrientation(this.mView, this.mOrientation, false);
            }
        }
        if (this.mView != null && (!this.mShowing)) {
            this.mShowing = true;
            setEnabled(this.mEnabled);
            refresh();
            fadeIn();
            this.mView.setVisibility(0);
            return;
        }
        if (this.mShowing) {
            refresh();
        }
    }

    public void hide() {
        if (this.mView != null && this.mShowing) {
            this.mShowing = false;
            fadeOut();
            this.mView.setVisibility(8);
        }
    }

    public final void uninit() {
        if (this.mView != null) {
            getContext().removeView(this.mView, this.mViewLayer);
        }
        onRelease();
        this.mView = null;
        this.mContext.removeViewManager(this);
        this.mContext.removeOnOrientationListener(this);
    }

    public boolean isShowing() {
        return this.mShowing;
    }

    public boolean isEnabled() {
        return this.mEnabled;
    }

    public final CameraActivity getContext() {
        return this.mContext;
    }

    public int getViewLayer() {
        return this.mViewLayer;
    }

    public void setFileter(boolean z) {
        this.mFilter = z;
    }

    public int getOrientation() {
        return this.mOrientation;
    }

    public void setAnimationEnabled(boolean z, boolean z2) {
        this.mShowAnimationEnabled = z;
        this.mHideAnimationEnabled = z2;
    }

    public boolean getShowAnimationEnabled() {
        return this.mShowAnimationEnabled;
    }

    public boolean getHideAnimationEnabled() {
        return this.mHideAnimationEnabled;
    }

    public void checkConfiguration() {
        int i = this.mContext.getResources().getConfiguration().orientation;
        if (this.mConfigOrientation != -1 && i != this.mConfigOrientation) {
            reInflate();
        }
    }

    protected void fadeIn() {
        if (this.mShowAnimationEnabled) {
            if (this.mFadeIn == null) {
                this.mFadeIn = getFadeInAnimation();
            }
            if (this.mFadeIn != null) {
                this.mView.startAnimation(this.mFadeIn);
            } else {
                Util.fadeIn(this.mView);
            }
        }
    }

    protected void fadeOut() {
        if (this.mHideAnimationEnabled) {
            if (this.mFadeOut == null) {
                this.mFadeOut = getFadeOutAnimation();
            }
            if (this.mFadeOut != null) {
                this.mView.startAnimation(this.mFadeOut);
            } else {
                Util.fadeOut(this.mView);
            }
        }
    }

    public final View inflate(int i) {
        return getContext().inflate(i, this.mViewLayer);
    }

    public final void reInflate() {
        boolean z = this.mShowing;
        if (this.mView != null) {
            getContext().removeView(this.mView, this.mViewLayer);
        }
        onRelease();
        this.mView = null;
        if (z) {
            show();
        }
    }

    public final void refresh() {
        if (this.mShowing) {
            onRefresh();
        }
    }

    public boolean collapse(boolean z) {
        return false;
    }

    public void setEnabled(boolean z) {
        this.mEnabled = z;
        if (this.mView != null) {
            this.mView.setEnabled(this.mEnabled);
            if (this.mFilter) {
                SettingUtils.setEnabledState(this.mView, this.mEnabled);
            }
        }
    }

    protected void onRelease() {
    }

    protected void onRefresh() {
    }

    protected Animation getFadeInAnimation() {
        return null;
    }

    protected Animation getFadeOutAnimation() {
        return null;
    }
}
