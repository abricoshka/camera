package com.android.camera.p002v2.uimanager;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import com.android.camera.p002v2.p003ui.UiUtil;
import junit.framework.Assert;

/* loaded from: classes.dex */
public abstract class AbstractUiManager {
    private final Activity mActivity;
    private Animation mFadeIn;
    private Animation mFadeOut;
    private final ViewGroup mParentViewGroup;
    private boolean mShowing;
    private View mView;
    private boolean mEnabled = true;
    private boolean mFilterEnable = true;
    private boolean mShowAnimationEnabled = true;
    private boolean mHideAnimationEnabled = true;

    protected abstract View getView();

    public AbstractUiManager(Activity activity, ViewGroup viewGroup) {
        Assert.assertNotNull(activity);
        Assert.assertNotNull(viewGroup);
        this.mActivity = activity;
        this.mParentViewGroup = viewGroup;
    }

    public final View inflate(int i) {
        return this.mActivity.getLayoutInflater().inflate(i, this.mParentViewGroup, false);
    }

    public void show() {
        if (this.mView == null) {
            this.mView = getView();
            this.mParentViewGroup.addView(this.mView);
        }
        if (this.mView != null && (!this.mShowing)) {
            this.mShowing = true;
            setEnable(this.mEnabled);
            refresh();
            fadeIn();
            this.mView.setVisibility(0);
            return;
        }
        if (this.mShowing) {
            refresh();
        }
    }

    public void refresh() {
        if (this.mShowing) {
            onRefresh();
        }
    }

    public void reInflate() {
        boolean z = this.mShowing;
        hide();
        if (this.mView != null) {
            this.mParentViewGroup.removeView(this.mView);
        }
        onRelease();
        this.mView = null;
        if (z) {
            show();
        }
    }

    public void hide() {
        if (this.mView != null && this.mShowing) {
            this.mShowing = false;
            fadeOut();
            this.mView.setVisibility(8);
        }
    }

    public boolean isShowing() {
        return this.mShowing;
    }

    public void setEnable(boolean z) {
        this.mEnabled = z;
        if (this.mView != null) {
            this.mView.setEnabled(this.mEnabled);
            if (this.mFilterEnable) {
                UiUtil.setViewEnabledState(this.mView, this.mEnabled);
            }
        }
    }

    public boolean isEnable() {
        return this.mEnabled;
    }

    public void setFilterEnable(boolean z) {
        this.mFilterEnable = z;
    }

    protected void onRefresh() {
    }

    protected void onRelease() {
    }

    protected Animation getFadeInAnimation() {
        return null;
    }

    protected Animation getFadeOutAnimation() {
        return null;
    }

    protected void fadeIn() {
        if (this.mShowAnimationEnabled) {
            if (this.mFadeIn == null) {
                this.mFadeIn = getFadeInAnimation();
            }
            if (this.mFadeIn != null) {
                this.mView.startAnimation(this.mFadeIn);
            } else {
                UiUtil.fadeIn(this.mView);
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
                UiUtil.fadeOut(this.mView);
            }
        }
    }
}
