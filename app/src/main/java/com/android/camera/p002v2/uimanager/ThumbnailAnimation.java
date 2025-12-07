package com.android.camera.p002v2.uimanager;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.view.View;

/* loaded from: classes.dex */
public class ThumbnailAnimation {
    private AnimatorSet mThumbnailAnimator;

    public interface AnimationListener {
        void onAnimationEnd();
    }

    public void doCaptureAnimation(final View view, Activity activity, final AnimationListener animationListener) {
        ObjectAnimator duration;
        ObjectAnimator duration2;
        cancelAnimations();
        View view2 = (View) view.getParent();
        int width = (view.getWidth() / 2) + view.getLeft();
        int height = (view.getHeight() / 2) + view.getTop();
        if (activity.getRequestedOrientation() == 1) {
            duration = ObjectAnimator.ofFloat(view, "translationY", 0.0f, view2.getWidth() - view.getLeft()).setDuration(300L);
            duration2 = ObjectAnimator.ofFloat(view, "translationX", (view2.getHeight() / 2) - width, 0.0f).setDuration(0L);
        } else {
            duration = ObjectAnimator.ofFloat(view, "translationX", 0.0f, view2.getHeight() - view.getLeft()).setDuration(300L);
            duration2 = ObjectAnimator.ofFloat(view, "translationY", (view2.getHeight() / 2) - height, 0.0f).setDuration(0L);
        }
        duration2.addListener(new Animator.AnimatorListener() { // from class: com.android.camera.v2.uimanager.ThumbnailAnimation.1
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
        this.mThumbnailAnimator = new AnimatorSet();
        this.mThumbnailAnimator.playTogether(duration2, duration);
        this.mThumbnailAnimator.addListener(new Animator.AnimatorListener() { // from class: com.android.camera.v2.uimanager.ThumbnailAnimation.2
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
                ThumbnailAnimation.this.mThumbnailAnimator.removeAllListeners();
                ThumbnailAnimation.this.mThumbnailAnimator = null;
                if (animationListener != null) {
                    animationListener.onAnimationEnd();
                }
            }

            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationCancel(Animator animator) {
                view.setVisibility(4);
            }

            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationRepeat(Animator animator) {
            }
        });
        this.mThumbnailAnimator.start();
    }

    private void cancelAnimations() {
        if (this.mThumbnailAnimator != null && this.mThumbnailAnimator.isStarted()) {
            this.mThumbnailAnimator.cancel();
        }
    }
}
