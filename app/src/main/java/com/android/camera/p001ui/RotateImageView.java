package com.android.camera.p001ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.media.ThumbnailUtils;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import com.android.camera.Log;

/* loaded from: classes.dex */
public class RotateImageView extends TwoStateImageView implements Rotatable {
    private long mAnimationEndTime;
    private long mAnimationStartTime;
    private boolean mClockwise;
    private int mCurrentDegree;
    private boolean mEnableAnimation;
    private int mStartDegree;
    private int mTargetDegree;
    private Bitmap mThumb;
    private TransitionDrawable mThumbTransition;
    private Drawable[] mThumbs;
    private Path path;

    public RotateImageView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mCurrentDegree = 0;
        this.mStartDegree = 0;
        this.mTargetDegree = 0;
        this.mClockwise = false;
        this.mEnableAnimation = true;
        this.mAnimationStartTime = 0L;
        this.mAnimationEndTime = 0L;
        this.path = null;
    }

    public RotateImageView(Context context) {
        this(context, null);
    }

    @Override // com.android.camera.p001ui.Rotatable
    public void setOrientation(int i, boolean z) {
        Log.m5d("RotateImageView", "setOrientation(" + i + ", " + z + ") mOrientation=" + this.mTargetDegree);
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
            Log.m6e("RotateImageView", "drawable == null, return");
            return;
        }
        Rect bounds = drawable.getBounds();
        int i = bounds.right - bounds.left;
        int i2 = bounds.bottom - bounds.top;
        if (i == 0 || i2 == 0) {
            Log.m6e("RotateImageView", "w == 0 || h == 0, return");
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
        Path path = new Path();
        path.addCircle(i / 2.0f, i2 / 2.0f, Math.min(i, i2) / 2.0f, Path.Direction.CW);
        canvas.clipPath(path);
        drawable.draw(canvas);
        canvas.restoreToCount(saveCount);
    }

    public void setBitmap(Bitmap bitmap) {
        if (bitmap == null) {
            if (this.mThumb != null && (!this.mThumb.isRecycled())) {
                this.mThumb.recycle();
            }
            this.mThumb = null;
            this.mThumbs = null;
            setImageDrawable(null);
            return;
        }
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        this.mThumb = ThumbnailUtils.extractThumbnail(bitmap, (layoutParams.width - getPaddingLeft()) - getPaddingRight(), (layoutParams.height - getPaddingTop()) - getPaddingBottom());
        if (this.mThumbs == null || (!this.mEnableAnimation)) {
            this.mThumbs = new Drawable[2];
            this.mThumbs[1] = new BitmapDrawable(getContext().getResources(), this.mThumb);
            setImageDrawable(this.mThumbs[1]);
        } else {
            this.mThumbs[0] = this.mThumbs[1];
            this.mThumbs[1] = new BitmapDrawable(getContext().getResources(), this.mThumb);
            this.mThumbTransition = new TransitionDrawable(this.mThumbs);
            setImageDrawable(this.mThumbTransition);
            this.mThumbTransition.startTransition(500);
        }
        setVisibility(0);
    }
}
