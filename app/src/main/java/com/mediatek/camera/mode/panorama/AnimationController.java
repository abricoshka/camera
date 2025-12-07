package com.mediatek.camera.mode.panorama;

import android.os.Handler;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import com.mediatek.camera.util.Log;

/* loaded from: classes.dex */
public class AnimationController {
    private ViewGroup mCenterArrow;
    private ViewGroup[] mDirectionIndicators;
    private int mCenterDotIndex = 0;
    private int mDirectionDotIndex = 0;
    private Handler mHanler = new Handler();
    private Runnable mApplyCenterArrowAnim = new Runnable() { // from class: com.mediatek.camera.mode.panorama.AnimationController.1
        private int dotCount = 0;

        @Override // java.lang.Runnable
        public void run() {
            if (this.dotCount == 0) {
                this.dotCount = AnimationController.this.mCenterArrow.getChildCount();
            }
            if (this.dotCount <= AnimationController.this.mCenterDotIndex) {
                Log.m36w("AnimationController", "[run]mApplyCenterArrowAnim return,dotCount = " + this.dotCount + ",mCenterDotIndex =" + AnimationController.this.mCenterDotIndex);
                return;
            }
            AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
            alphaAnimation.setDuration(1440L);
            alphaAnimation.setRepeatCount(-1);
            if (AnimationController.this.mCenterArrow != null) {
                AnimationController.this.mCenterArrow.getChildAt(AnimationController.this.mCenterDotIndex).startAnimation(alphaAnimation);
            }
            alphaAnimation.startNow();
            AnimationController.this.mCenterDotIndex++;
            AnimationController.this.mHanler.postDelayed(this, 360 / this.dotCount);
        }
    };
    private Runnable mApplyDirectionAnim = new Runnable() { // from class: com.mediatek.camera.mode.panorama.AnimationController.2
        private int dotCount = 0;

        @Override // java.lang.Runnable
        public void run() {
            for (ViewGroup viewGroup : AnimationController.this.mDirectionIndicators) {
                if (viewGroup == null) {
                    Log.m36w("AnimationController", "[run]viewGroup is null,return!");
                    return;
                }
            }
            if (this.dotCount == 0) {
                this.dotCount = AnimationController.this.mDirectionIndicators[0].getChildCount();
            }
            if (this.dotCount <= AnimationController.this.mDirectionDotIndex) {
                Log.m34i("AnimationController", "[run]mApplyDirectionAnim,return,dotCount = " + this.dotCount + ",mCenterDotIndex =" + AnimationController.this.mCenterDotIndex);
                return;
            }
            AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
            alphaAnimation.setDuration(((this.dotCount * 180) * 3) / 2);
            alphaAnimation.setRepeatCount(-1);
            AnimationController.this.mDirectionIndicators[0].getChildAt(AnimationController.this.mDirectionDotIndex).startAnimation(alphaAnimation);
            AnimationController.this.mDirectionIndicators[1].getChildAt((this.dotCount - AnimationController.this.mDirectionDotIndex) - 1).startAnimation(alphaAnimation);
            AnimationController.this.mDirectionIndicators[2].getChildAt((this.dotCount - AnimationController.this.mDirectionDotIndex) - 1).startAnimation(alphaAnimation);
            AnimationController.this.mDirectionIndicators[3].getChildAt(AnimationController.this.mDirectionDotIndex).startAnimation(alphaAnimation);
            alphaAnimation.startNow();
            AnimationController.this.mDirectionDotIndex++;
            AnimationController.this.mHanler.postDelayed(this, 90L);
        }
    };

    public AnimationController(ViewGroup[] viewGroupArr, ViewGroup viewGroup) {
        this.mDirectionIndicators = viewGroupArr;
        this.mCenterArrow = viewGroup;
    }

    public void startDirectionAnimation() {
        Log.m34i("AnimationController", "[startDirectionAnimation]...");
        this.mDirectionDotIndex = 0;
        this.mApplyDirectionAnim.run();
    }

    public void startCenterAnimation() {
        Log.m34i("AnimationController", "[startCenterAnimation]...");
        this.mCenterDotIndex = 0;
        this.mApplyCenterArrowAnim.run();
    }

    public void stopCenterAnimation() {
        Log.m34i("AnimationController", "[stopCenterAnimation]...");
        if (this.mCenterArrow != null) {
            for (int i = 0; i < this.mCenterArrow.getChildCount(); i++) {
                this.mCenterArrow.getChildAt(i).clearAnimation();
            }
        }
    }
}
