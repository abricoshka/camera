package com.mediatek.camera.p005v2.p006ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import com.mediatek.camera.debug.LogHelper;

/* loaded from: classes.dex */
public class RotateImageView extends TwoStateImageView implements Rotatable {
    private static final LogHelper.Tag TAG = new LogHelper.Tag(RotateImageView.class.getSimpleName());
    private long mAnimationEndTime;
    private long mAnimationStartTime;
    private boolean mClockwise;
    private int mCurrentDegree;
    private boolean mEnableAnimation;
    private int mStartDegree;
    private int mTargetDegree;

    public RotateImageView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mCurrentDegree = 0;
        this.mStartDegree = 0;
        this.mTargetDegree = 0;
        this.mClockwise = false;
        this.mEnableAnimation = true;
        this.mAnimationStartTime = 0L;
        this.mAnimationEndTime = 0L;
    }

    public RotateImageView(Context context) {
        this(context, null);
    }

    @Override // com.mediatek.camera.p005v2.p006ui.Rotatable
    public void setOrientation(int i, boolean z) {
        LogHelper.m23d(TAG, "setOrientation(" + i + ", " + z + ") mOrientation=" + this.mTargetDegree);
        this.mEnableAnimation = z;
        int i2 = i >= 0 ? i % 360 : (i % 360) + 360;
        if (i2 == this.mTargetDegree) {
            return;
        }
        this.mTargetDegree = i2;
        if (this.mEnableAnimation) {
            this.mStartDegree = this.mCurrentDegree;
            this.mAnimationStartTime = AnimationUtils.currentAnimationTimeMillis();
            int i3 = this.mTargetDegree - this.mCurrentDegree;
            if (i3 < 0) {
                i3 += 360;
            }
            this.mClockwise = (i3 > 180 ? i3 - 360 : i3) >= 0;
            this.mAnimationEndTime = this.mAnimationStartTime + ((Math.abs(r2) * 1000) / 270);
        } else {
            this.mCurrentDegree = this.mTargetDegree;
        }
        invalidate();
    }

    @Override // android.widget.ImageView, android.view.View
    protected void onDraw(Canvas canvas) {
        Drawable drawable = getDrawable();
        if (drawable == null) {
            LogHelper.m24e(TAG, "drawable == null, return");
            return;
        }
        Rect bounds = drawable.getBounds();
        int i = bounds.right - bounds.left;
        int i2 = bounds.bottom - bounds.top;
        if (i == 0 || i2 == 0) {
            LogHelper.m24e(TAG, "w == 0 || h == 0, return");
            return;
        }
        if (this.mCurrentDegree != this.mTargetDegree) {
            long jCurrentAnimationTimeMillis = AnimationUtils.currentAnimationTimeMillis();
            if (jCurrentAnimationTimeMillis < this.mAnimationEndTime) {
                int i3 = (int) (jCurrentAnimationTimeMillis - this.mAnimationStartTime);
                int i4 = this.mStartDegree;
                if (!this.mClockwise) {
                    i3 = -i3;
                }
                int i5 = ((i3 * 270) / 1000) + i4;
                this.mCurrentDegree = i5 >= 0 ? i5 % 360 : (i5 % 360) + 360;
                invalidate();
            } else {
                this.mCurrentDegree = this.mTargetDegree;
            }
        }
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();
        int width = (getWidth() - paddingLeft) - paddingRight;
        int height = (getHeight() - paddingTop) - paddingBottom;
        int saveCount = canvas.getSaveCount();
        if (getScaleType() == ImageView.ScaleType.FIT_CENTER && (width < i || height < i2)) {
            float fMin = Math.min(width / i, height / i2);
            canvas.scale(fMin, fMin, width / 2.0f, height / 2.0f);
        }
        canvas.translate(paddingLeft + (width / 2), paddingTop + (height / 2));
        canvas.rotate(-this.mCurrentDegree);
        canvas.translate((-i) / 2, (-i2) / 2);
        drawable.draw(canvas);
        canvas.restoreToCount(saveCount);
    }
}
