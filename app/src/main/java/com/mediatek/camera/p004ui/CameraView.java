package com.mediatek.camera.p004ui;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.RelativeLayout;
import com.mediatek.camera.platform.ICameraAppUi;
import com.mediatek.camera.platform.ICameraView;
import com.mediatek.camera.platform.IModuleCtrl;
import com.mediatek.camera.util.Log;
import com.mediatek.camera.util.Util;

/* loaded from: classes.dex */
public abstract class CameraView implements ICameraView {
    protected Activity mActivity;
    private Animation mFadeIn;
    private Animation mFadeOut;
    private int mOrientation;
    private View mView;
    private boolean mIsShowing = false;
    private boolean mIsEnabled = true;

    protected abstract View getView();

    public CameraView(Activity activity) {
        this.mActivity = activity;
    }

    @Override // com.mediatek.camera.platform.ICameraView
    public void init(Activity activity, ICameraAppUi iCameraAppUi, IModuleCtrl iModuleCtrl) {
    }

    @Override // com.mediatek.camera.platform.ICameraView
    public void uninit() {
        hide();
        removeView();
    }

    @Override // com.mediatek.camera.platform.ICameraView
    public void show() {
        Log.m31d("CameraView", "[show]mShowing = " + this.mIsShowing);
        if (this.mView == null) {
            this.mView = getView();
            if (this.mView != null) {
                addView(this.mView);
                Util.setOrientation(this.mView, this.mOrientation, false);
            }
        }
        if (!this.mIsShowing) {
            this.mIsShowing = true;
            setEnabled(this.mIsEnabled);
            refresh();
            if (this.mView != null) {
                fadeIn();
                this.mView.setVisibility(0);
                return;
            }
            return;
        }
        if (this.mIsShowing) {
            refresh();
        }
    }

    @Override // com.mediatek.camera.platform.ICameraView
    public void hide() {
        Log.m31d("CameraView", "[hide]mShowing = " + this.mIsShowing);
        if (this.mView != null && this.mIsShowing) {
            this.mIsShowing = false;
            fadeOut();
            this.mView.setVisibility(8);
        }
    }

    @Override // com.mediatek.camera.platform.ICameraView
    public void refresh() {
    }

    @Override // com.mediatek.camera.platform.ICameraView
    public void reset() {
    }

    public void reInflate() {
        boolean z = this.mIsShowing;
        hide();
        removeView();
        this.mView = null;
        if (z) {
            show();
        }
    }

    @Override // com.mediatek.camera.platform.ICameraView
    public boolean update(int i, Object... objArr) {
        return true;
    }

    @Override // com.mediatek.camera.platform.ICameraView
    public boolean isShowing() {
        return this.mIsShowing;
    }

    @Override // com.mediatek.camera.platform.ICameraView
    public void setEnabled(boolean z) {
        this.mIsEnabled = z;
        if (this.mView != null) {
            this.mView.setEnabled(this.mIsEnabled);
        }
    }

    @Override // com.mediatek.camera.platform.ICameraView
    public void setListener(Object obj) {
    }

    @Override // com.mediatek.camera.platform.ICameraView
    public void onOrientationChanged(int i) {
        if (this.mOrientation != i) {
            this.mOrientation = i;
            Util.setOrientation(this.mView, this.mOrientation, true);
        }
    }

    public final Activity getContext() {
        return this.mActivity;
    }

    public final int getOrientation() {
        return this.mOrientation;
    }

    public final void setOrientation(int i) {
        this.mOrientation = i;
    }

    protected void addView(View view) {
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(-1, -1);
        layoutParams.topMargin = 0;
        if (this.mView != null) {
            getContext().addContentView(this.mView, layoutParams);
        }
    }

    protected void removeView() {
        ViewGroup viewGroup = this.mView != null ? (ViewGroup) this.mView.getParent() : null;
        if (viewGroup != null) {
            viewGroup.removeView(this.mView);
        }
        this.mView = null;
    }

    protected Animation getFadeInAnimation() {
        return null;
    }

    protected Animation getFadeOutAnimation() {
        return null;
    }

    protected View inflate(int i) {
        return getContext().getLayoutInflater().inflate(i, (ViewGroup) null);
    }

    private void fadeIn() {
        if (this.mFadeIn == null) {
            this.mFadeIn = getFadeInAnimation();
        }
        if (this.mFadeIn != null) {
            this.mView.startAnimation(this.mFadeIn);
        }
    }

    private void fadeOut() {
        if (this.mFadeOut == null) {
            this.mFadeOut = getFadeOutAnimation();
        }
        if (this.mFadeOut != null) {
            this.mView.startAnimation(this.mFadeOut);
        }
    }
}
