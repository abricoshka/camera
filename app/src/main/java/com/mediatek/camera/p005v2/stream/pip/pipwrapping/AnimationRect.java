package com.mediatek.camera.p005v2.stream.pip.pipwrapping;

import android.graphics.Matrix;
import android.graphics.RectF;
import com.mediatek.camera.debug.LogHelper;

/* loaded from: classes.dex */
public class AnimationRect {
    private static final LogHelper.Tag TAG = new LogHelper.Tag(AnimationRect.class.getSimpleName());
    private static float mRotationLimitedMax = 180.0f;
    private static float mRotationLimitedMin = -180.0f;
    private float mCurrentScaleValue = 1.0f;
    private float mOriginalDistance = 0.0f;
    private int mPreviewWidth = -1;
    private int mPreviewHeight = -1;
    private float mCurrentRotationValue = 0.0f;
    private float[] leftTop = {0.0f, 0.0f};
    private float[] rightTop = {0.0f, 0.0f};
    private float[] leftBottom = {0.0f, 0.0f};
    private float[] rightBottom = {0.0f, 0.0f};
    private boolean mIsHighlightEnable = false;
    private Matrix mAnimationMatrix = new Matrix();
    private RectF mRectF = new RectF();

    public float[] getLeftTop() {
        return this.leftTop;
    }

    public void setLeftTop(float[] fArr) {
        this.leftTop[0] = fArr[0];
        this.leftTop[1] = fArr[1];
    }

    public float[] getRightTop() {
        return this.rightTop;
    }

    public void setRightTop(float[] fArr) {
        this.rightTop[0] = fArr[0];
        this.rightTop[1] = fArr[1];
    }

    public float[] getLeftBottom() {
        return this.leftBottom;
    }

    public void setLeftBottom(float[] fArr) {
        this.leftBottom[0] = fArr[0];
        this.leftBottom[1] = fArr[1];
    }

    public float[] getRightBottom() {
        return this.rightBottom;
    }

    public void setRightBottom(float[] fArr) {
        this.rightBottom[0] = fArr[0];
        this.rightBottom[1] = fArr[1];
    }

    private void setVetex(float f, float f2, float f3, float f4) {
        this.leftTop[0] = f;
        this.leftTop[1] = f2;
        this.rightTop[0] = f3;
        this.rightTop[1] = f2;
        this.leftBottom[0] = f;
        this.leftBottom[1] = f4;
        this.rightBottom[0] = f3;
        this.rightBottom[1] = f4;
    }

    public void setRendererSize(int i, int i2) {
        this.mPreviewWidth = i;
        this.mPreviewHeight = i2;
    }

    public void initialize(float f, float f2, float f3, float f4) {
        this.mRectF.set(f, f2, f3, f4);
        setVetex(this.mRectF.left, this.mRectF.top, this.mRectF.right, this.mRectF.bottom);
        this.mOriginalDistance = (float) Math.sqrt(((centerX() - this.rightBottom[0]) * (centerX() - this.rightBottom[0])) + ((centerY() - this.rightBottom[1]) * (centerY() - this.rightBottom[1])));
    }

    public void translate(float f, float f2, boolean z) {
        this.mAnimationMatrix.reset();
        this.mAnimationMatrix.setTranslate(f, f2);
        this.mAnimationMatrix.mapRect(this.mRectF);
        if (z) {
            checkTranslate();
        }
        setVetex(this.mRectF.left, this.mRectF.top, this.mRectF.right, this.mRectF.bottom);
    }

    public RectF getRectF() {
        return this.mRectF;
    }

    public float getCurrrentRotationValue() {
        return this.mCurrentRotationValue;
    }

    public void setHighLightEnable(boolean z) {
        this.mIsHighlightEnable = z;
    }

    public boolean getHighLightStatus() {
        return this.mIsHighlightEnable;
    }

    public float centerX() {
        return (this.rightTop[0] + this.leftBottom[0]) / 2.0f;
    }

    public float centerY() {
        return (this.rightTop[1] + this.leftBottom[1]) / 2.0f;
    }

    private float getMaxScaleValue() {
        float fMin = Math.min(getXMaxScaleValue(), getYMaxScaleValue());
        LogHelper.m23d(TAG, "getMaxScaleValue maxScaleValue = " + fMin + " mCurrentScaleValue = " + this.mCurrentScaleValue);
        float f = fMin * this.mCurrentScaleValue;
        if (f > 1.4f) {
            return 1.4f;
        }
        return f;
    }

    private float getMinScaleValue() {
        return 0.6f;
    }

    private float getXMaxScaleValue() {
        return Math.min((float) Math.sqrt((centerX() * centerX()) / ((centerX() - this.leftTop[0]) * (centerX() - this.leftTop[0]))), (float) Math.sqrt((((this.mPreviewWidth - centerX()) * (this.mPreviewWidth - centerX())) / (this.rightBottom[0] - centerX())) * (this.rightBottom[0] - centerX())));
    }

    private float getYMaxScaleValue() {
        return Math.min((float) Math.sqrt((centerY() * centerY()) / ((centerY() - this.leftTop[1]) * (centerY() - this.leftTop[1]))), (float) Math.sqrt((((this.mPreviewHeight - centerY()) * (this.mPreviewHeight - centerY())) / (this.rightBottom[1] - centerY())) * (this.rightBottom[1] - centerY())));
    }

    public void scale(float f, boolean z) {
        float f2 = 1.0f;
        LogHelper.m23d(TAG, "Before setScale scale = " + f + " getMaxScaleValue = " + getMaxScaleValue() + " getMinScaleValue = " + getMinScaleValue());
        if (z) {
            float f3 = this.mCurrentScaleValue * f;
            float f4 = (f <= 1.0f || f3 <= getMaxScaleValue()) ? f : 1.0f;
            if (f4 >= 1.0f || f3 >= getMinScaleValue()) {
                f2 = f4;
            }
            this.mCurrentScaleValue *= f2;
        } else {
            f2 = f;
        }
        LogHelper.m23d(TAG, "setScale mCurrentScaleValue = " + this.mCurrentScaleValue);
        this.mAnimationMatrix.reset();
        this.mAnimationMatrix.setScale(f2, f2, this.mRectF.centerX(), this.mRectF.centerY());
        this.mAnimationMatrix.mapRect(this.mRectF);
        setVetex(this.mRectF.left, this.mRectF.top, this.mRectF.right, this.mRectF.bottom);
        this.mOriginalDistance = (float) Math.sqrt(((centerX() - this.rightBottom[0]) * (centerX() - this.rightBottom[0])) + ((centerY() - this.rightBottom[1]) * (centerY() - this.rightBottom[1])));
        LogHelper.m23d(TAG, "After setScale scale = " + f2);
    }

    public void scaleToTranslateY(float f) {
        LogHelper.m23d(TAG, "setScaleToTranslateY");
        float[] fArr = {this.rightTop[0], this.rightTop[1]};
        this.mAnimationMatrix.reset();
        this.mAnimationMatrix.setScale(1.0f, f, this.mRectF.centerX(), this.mRectF.centerY());
        this.mAnimationMatrix.mapPoints(fArr);
        translate(0.0f, fArr[1] - this.rightTop[1], false);
    }

    public void rotate(float f) {
        rotate(f, this.mRectF.centerX(), this.mRectF.centerY());
    }

    public void rotate(float f, float f2, float f3) {
        LogHelper.m23d(TAG, "setRotate");
        setVetex(this.mRectF.left, this.mRectF.top, this.mRectF.right, this.mRectF.bottom);
        this.mAnimationMatrix.reset();
        this.mAnimationMatrix.setRotate(f, f2, f3);
        this.mAnimationMatrix.mapPoints(this.leftTop);
        this.mAnimationMatrix.mapPoints(this.rightTop);
        this.mAnimationMatrix.mapPoints(this.leftBottom);
        this.mAnimationMatrix.mapPoints(this.rightBottom);
        this.mCurrentRotationValue = f;
    }

    private void checkTranslate() {
        if (this.mPreviewWidth <= 0 || this.mPreviewHeight <= 0) {
            return;
        }
        if (this.mRectF.left < 0.0f) {
            this.mAnimationMatrix.reset();
            this.mAnimationMatrix.setTranslate(-this.mRectF.left, 0.0f);
            this.mAnimationMatrix.mapRect(this.mRectF);
        }
        if (this.mRectF.right > this.mPreviewWidth) {
            this.mAnimationMatrix.reset();
            this.mAnimationMatrix.setTranslate(this.mPreviewWidth - this.mRectF.right, 0.0f);
            this.mAnimationMatrix.mapRect(this.mRectF);
        }
        if (this.mRectF.top < 0.0f) {
            this.mAnimationMatrix.reset();
            this.mAnimationMatrix.setTranslate(0.0f, -this.mRectF.top);
            this.mAnimationMatrix.mapRect(this.mRectF);
        }
        if (this.mRectF.bottom > this.mPreviewHeight) {
            this.mAnimationMatrix.reset();
            this.mAnimationMatrix.setTranslate(0.0f, this.mPreviewHeight - this.mRectF.bottom);
            this.mAnimationMatrix.mapRect(this.mRectF);
        }
    }

    public AnimationRect copy() {
        AnimationRect animationRect = new AnimationRect();
        animationRect.mCurrentScaleValue = this.mCurrentScaleValue;
        animationRect.mAnimationMatrix.set(this.mAnimationMatrix);
        animationRect.mOriginalDistance = this.mOriginalDistance;
        animationRect.mRectF.set(this.mRectF);
        animationRect.mPreviewWidth = this.mPreviewWidth;
        animationRect.mPreviewHeight = this.mPreviewHeight;
        animationRect.mCurrentRotationValue = this.mCurrentRotationValue;
        animationRect.setLeftTop(getLeftTop());
        animationRect.setRightTop(getRightTop());
        animationRect.setLeftBottom(getLeftBottom());
        animationRect.setRightBottom(getRightBottom());
        animationRect.setHighLightEnable(this.mIsHighlightEnable);
        return animationRect;
    }

    public void changePortraitCooridnateSystem(int i, int i2) {
        float fMin = Math.min(i, i2);
        float fMax = Math.max(i, i2);
        float fMin2 = fMin / Math.min(this.mPreviewWidth, this.mPreviewHeight);
        float fCenterX = centerX();
        float fCenterY = centerY();
        translate((fMin2 * fCenterX) - fCenterX, ((fMax / Math.max(this.mPreviewWidth, this.mPreviewHeight)) * fCenterY) - fCenterY, false);
        scale(fMin2, false);
        rotate(this.mCurrentRotationValue);
        setRendererSize((int) fMin, (int) fMax);
    }

    public void changeToLandscapeCooridnateSystem(int i, int i2, int i3) {
        float f;
        float f2 = 0.0f;
        int iMin = Math.min(i, i2);
        int iMax = Math.max(i, i2);
        changePortraitCooridnateSystem(iMin, iMax);
        float fCenterX = centerX();
        float fCenterY = centerY();
        switch (i3) {
            case 90:
                f2 = iMin - fCenterX;
                f = fCenterY;
                break;
            case 270:
                f = iMax - fCenterY;
                f2 = fCenterX;
                break;
            default:
                f = 0.0f;
                break;
        }
        translate(f - fCenterX, f2 - fCenterY, false);
        rotate(this.mCurrentRotationValue - i3);
    }

    public void changeCooridnateSystem(int i, int i2, int i3) {
        float fMin = Math.min(i, i2) / Math.min(this.mPreviewWidth, this.mPreviewHeight);
        float fMax = Math.max(i, i2) / Math.max(this.mPreviewWidth, this.mPreviewHeight);
        float fCenterX = centerX();
        float fCenterY = centerY();
        switch (i3) {
            case 90:
                float f = this.mPreviewWidth - fCenterX;
                fCenterX = fCenterY;
                fCenterY = f;
                break;
            case 270:
                fCenterX = this.mPreviewHeight - fCenterY;
                fCenterY = fCenterX;
                break;
        }
        translate(fCenterX - centerX(), fCenterY - centerY(), false);
        translate((fCenterX * fMin) - fCenterX, (fCenterY * fMax) - fCenterY, false);
        scale(fMin, false);
        scaleToTranslateY(fMax / fMin);
        rotate(formatRotationValue(360 - i3) + this.mCurrentRotationValue);
    }

    public static float formatRotationValue(float f) {
        float f2 = f > 180.0f ? f - 360.0f : f;
        if (f2 < -180.0f) {
            f2 += 360.0f;
        }
        return f2 % 360.0f;
    }
}
