package com.mediatek.camera.util;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.view.View;
import com.android.camera.manager.ThumbnailViewManager;

/* loaded from: classes.dex */
public class CameraAnimation {
    private AnimatorSet mCaptureAnimator;

    public void doCaptureAnimation(final View view, Activity activity, final ThumbnailViewManager.AnimationEndListener animationEndListener) {
        ObjectAnimator duration;
        ObjectAnimator duration2;
        Log.m31d("CameraAnimation", "[doCaptureAnimation] activity.getRequestedOrientation() = " + activity.getRequestedOrientation());
        cancelAnimations();
        View view2 = (View) view.getParent();
        int width = (view.getWidth() / 2) + view.getLeft();
        int height = (view.getHeight() / 2) + view.getTop();
        int requestedOrientation = activity.getRequestedOrientation();
        if (requestedOrientation == 0 || requestedOrientation == 8) {
            duration = ObjectAnimator.ofFloat(view, "translationX", 0.0f, view.getWidth()).setDuration(300L);
            duration2 = ObjectAnimator.ofFloat(view, "translationY", (view2.getHeight() / 2) - height, 0.0f).setDuration(0L);
        } else {
            duration = ObjectAnimator.ofFloat(view, "translationY", 0.0f, view.getHeight()).setDuration(300L);
            duration2 = ObjectAnimator.ofFloat(view, "translationX", (view2.getHeight() / 2) - width, 0.0f).setDuration(0L);
        }
        duration2.addListener(new Animator.AnimatorListener() { // from class: com.mediatek.camera.util.CameraAnimation.1
            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationStart(Animator animator) {
            }

            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                view.setClickable(true);
            }

            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationCancel(Animator animator) {
            }

            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationRepeat(Animator animator) {
            }
        });
        this.mCaptureAnimator = new AnimatorSet();
        this.mCaptureAnimator.playTogether(duration2, duration);
        this.mCaptureAnimator.addListener(new Animator.AnimatorListener() { // from class: com.mediatek.camera.util.CameraAnimation.2
            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationStart(Animator animator) {
                view.setClickable(false);
                view.setVisibility(0);
            }

            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                view.setScaleX(1.0f);
                view.setScaleX(1.0f);
                view.setTranslationX(0.0f);
                view.setTranslationY(0.0f);
                view.setVisibility(4);
                CameraAnimation.this.mCaptureAnimator.removeAllListeners();
                CameraAnimation.this.mCaptureAnimator = null;
                animationEndListener.onAnianmationEnd();
            }

            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationCancel(Animator animator) {
                view.setVisibility(4);
            }

            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationRepeat(Animator animator) {
            }
        });
        this.mCaptureAnimator.start();
    }

    private void cancelAnimations() {
        if (this.mCaptureAnimator != null && this.mCaptureAnimator.isStarted()) {
            this.mCaptureAnimator.cancel();
        }
    }
}
