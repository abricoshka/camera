package com.android.camera.p001ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import com.android.camera.Log;

/* loaded from: classes.dex */
public class RotateTextView extends TextView implements Rotatable {
    private long mAnimationEndTime;
    private long mAnimationStartTime;
    private boolean mClockwise;
    private int mCurrentDegree;
    private boolean mEnableAnimation;
    private int mStartDegree;
    private int mTargetDegree;
    private int textColor;

    public RotateTextView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mCurrentDegree = 0;
        this.mStartDegree = 0;
        this.mTargetDegree = 0;
        this.mClockwise = false;
        this.mEnableAnimation = true;
        this.mAnimationStartTime = 0L;
        this.mAnimationEndTime = 0L;
        this.textColor = -1;
    }

    public RotateTextView(Context context) {
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

    @Override // android.widget.TextView, android.view.View
    protected void onDraw(Canvas canvas) {
        String string = getText().toString();
        String str = null;
        String str2 = null;
        boolean z = false;
        if (string.indexOf("\n") != -1) {
            String[] strArrSplit = string.split("\n");
            str = strArrSplit[0];
            Log.m10v("RotateImageView", "str1:" + str);
            str2 = strArrSplit[1];
            Log.m10v("RotateImageView", "str2:" + str2);
            z = true;
        }
        if (TextUtils.isEmpty(string)) {
            Log.m6e("RotateImageView", "text == null, return");
            return;
        }
        TextPaint paint = getPaint();
        TextPaint paint2 = getPaint();
        Rect rect = new Rect();
        if (z && str != null && str2 != null) {
            paint2.getTextBounds(str, 0, str.length(), rect);
            int i = rect.right - rect.left;
            int i2 = rect.bottom - rect.top;
            if (i == 0 || i2 == 0) {
                Log.m6e("RotateImageView", "w == 0 || h == 0, return");
                return;
            }
            paint2.getTextBounds(str2, 0, str2.length(), rect);
            int i3 = rect.right - rect.left;
            int i4 = rect.bottom - rect.top;
            if (i3 == 0 || i4 == 0) {
                Log.m6e("RotateImageView", "w == 0 || h == 0, return");
                return;
            }
        } else {
            paint2.getTextBounds(string, 0, string.length(), rect);
            Log.m6e("RotateImageView", "text bounds:" + rect);
            int i5 = rect.right - rect.left;
            int i6 = rect.bottom - rect.top;
            if (i5 == 0 || i6 == 0) {
                Log.m6e("RotateImageView", "w == 0 || h == 0, return");
                return;
            }
        }
        if (this.mCurrentDegree != this.mTargetDegree) {
            long jCurrentAnimationTimeMillis = AnimationUtils.currentAnimationTimeMillis();
            if (jCurrentAnimationTimeMillis < this.mAnimationEndTime) {
                int i7 = (int) (jCurrentAnimationTimeMillis - this.mAnimationStartTime);
                int i8 = this.mStartDegree;
                if (!this.mClockwise) {
                    i7 = -i7;
                }
                int i9 = ((i7 * 270) / 1000) + i8;
                this.mCurrentDegree = i9 >= 0 ? i9 % 360 : (i9 % 360) + 360;
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
        if (z && str != null && str2 != null) {
            canvas.translate((width / 2) + paddingLeft, (height / 2) + paddingTop);
            canvas.rotate(-this.mCurrentDegree);
            canvas.translate(-((width / 2) + paddingLeft), -((height / 2) + paddingTop));
            int saveCount = canvas.getSaveCount();
            paint2.setTextAlign(Paint.Align.CENTER);
            paint2.setTextSize(22.0f);
            Paint.FontMetrics fontMetrics = paint2.getFontMetrics();
            float f = fontMetrics.ascent;
            float f2 = fontMetrics.descent;
            paint2.setColor(this.textColor);
            canvas.drawText(str, width / 2.0f, (((height / 4.0f) - (f / 2.0f)) - (f2 / 2.0f)) + 8.0f, paint);
            paint2.setTextSize(14.0f);
            Paint.FontMetrics fontMetrics2 = paint.getFontMetrics();
            float f3 = fontMetrics2.ascent;
            float f4 = fontMetrics2.descent;
            paint2.setColor(this.textColor);
            canvas.drawText(str2, width / 2.0f, ((((height / 4.0f) * 3.0f) - (f3 / 2.0f)) - (f4 / 2.0f)) - 8.0f, paint);
            canvas.restoreToCount(saveCount);
            return;
        }
        canvas.translate((width / 2) + paddingLeft, (height / 2) + paddingTop);
        canvas.rotate(-this.mCurrentDegree);
        canvas.translate(-((width / 2) + paddingLeft), -((height / 2) + paddingTop));
        int saveCount2 = canvas.getSaveCount();
        paint.setTextAlign(Paint.Align.CENTER);
        Paint.FontMetrics fontMetrics3 = paint.getFontMetrics();
        float f5 = fontMetrics3.ascent;
        float f6 = fontMetrics3.descent;
        paint.setColor(this.textColor);
        canvas.drawText(string, width / 2.0f, ((width / 2.0f) - (f5 / 2.0f)) - (f6 / 2.0f), paint);
        canvas.restoreToCount(saveCount2);
    }

    @Override // android.widget.TextView
    public void setTextColor(int i) {
        this.textColor = i;
    }
}
