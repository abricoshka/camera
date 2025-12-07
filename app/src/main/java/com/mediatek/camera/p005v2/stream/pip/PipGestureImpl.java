package com.mediatek.camera.p005v2.stream.pip;

import android.app.Activity;
import android.graphics.Point;
import android.graphics.RectF;
import android.hardware.display.DisplayManager;
import android.util.Size;
import android.view.WindowManager;
import com.mediatek.camera.debug.LogHelper;
import com.mediatek.camera.p005v2.stream.pip.IPipGesture;
import com.mediatek.camera.p005v2.stream.pip.pipwrapping.AnimationRect;
import com.mediatek.camera.p005v2.stream.pip.pipwrapping.GLUtil;
import junit.framework.Assert;

/* loaded from: classes.dex */
public class PipGestureImpl implements IPipGesture {
    private static final LogHelper.Tag TAG = new LogHelper.Tag(PipGestureImpl.class.getSimpleName());
    private static Object mSyncTopGraphicRect = new Object();
    private final int RECT_TO_TOP;
    private final Activity mActivity;
    private final IPipGesture.GestureCallback mCallback;
    private final RectF mEditSquareRectF;
    private float mRelativeRectToTop;
    private final AnimationRect mTopGraphicRectInPortrait;
    private int mDisplayRotation = -1;
    private int mEditSquareSideLength = 0;
    private int mPreviewWidth = 0;
    private int mPreviewHeight = 0;
    private RectF mPreviewArea = null;
    private float mXScale = 1.0f;
    private float mYScale = 1.0f;
    private int mCurrentGsensorOrientation = -1;
    private boolean isTranslateAnimationEnable = false;
    private boolean isScaleRotateAnimationEnable = false;
    private DisplayManager.DisplayListener mDisplayListener = new DisplayManager.DisplayListener() { // from class: com.mediatek.camera.v2.stream.pip.PipGestureImpl.1
        @Override // android.hardware.display.DisplayManager.DisplayListener
        public void onDisplayAdded(int i) {
        }

        @Override // android.hardware.display.DisplayManager.DisplayListener
        public void onDisplayChanged(int i) {
            PipGestureImpl.this.mDisplayRotation = PipGestureImpl.this.getDisplayRotation(PipGestureImpl.this.mActivity);
            LogHelper.m23d(PipGestureImpl.TAG, "onDisplayChanged mDisplayRotation:" + PipGestureImpl.this.mDisplayRotation);
        }

        @Override // android.hardware.display.DisplayManager.DisplayListener
        public void onDisplayRemoved(int i) {
        }
    };

    public PipGestureImpl(Activity activity, IPipGesture.GestureCallback gestureCallback) {
        Assert.assertNotNull(activity);
        Assert.assertNotNull(gestureCallback);
        this.mActivity = activity;
        this.mCallback = gestureCallback;
        this.mTopGraphicRectInPortrait = new AnimationRect();
        this.mEditSquareRectF = new RectF();
        this.RECT_TO_TOP = 100;
    }

    @Override // com.mediatek.camera.p005v2.stream.pip.IPipGesture
    public void open() {
        this.mDisplayRotation = getDisplayRotation(this.mActivity);
        ((DisplayManager) this.mActivity.getSystemService("display")).registerDisplayListener(this.mDisplayListener, null);
    }

    @Override // com.mediatek.camera.p005v2.stream.pip.IPipGesture
    public void release() {
        ((DisplayManager) this.mActivity.getSystemService("display")).unregisterDisplayListener(this.mDisplayListener);
    }

    @Override // com.mediatek.camera.p005v2.stream.pip.IPipGesture
    public void setPreviewSize(Size size) {
        Assert.assertNotNull(size);
        LogHelper.m23d(TAG, "[setPreviewSize]+ : " + size.getWidth() + " x " + size.getHeight());
        int iMin = Math.min(size.getWidth(), size.getHeight());
        int iMax = Math.max(size.getWidth(), size.getHeight());
        if (this.mPreviewWidth == iMin && this.mPreviewHeight == iMax) {
            LogHelper.m26i(TAG, "[setPreviewSize]- skip for the same size : " + iMin + "x" + iMax);
            return;
        }
        this.mEditSquareSideLength = Math.min(iMin, iMax) / 10;
        if (this.mPreviewWidth == 0 || this.mPreviewHeight == 0) {
            ((WindowManager) this.mActivity.getSystemService("window")).getDefaultDisplay().getRealSize(new Point());
            this.mRelativeRectToTop = (this.RECT_TO_TOP * Math.max(iMin, iMax)) / Math.max(r3.x, r3.y);
            float[] fArrCreateTopRightRect = GLUtil.createTopRightRect(iMin, iMax, this.mRelativeRectToTop);
            this.mTopGraphicRectInPortrait.setRendererSize(Math.min(iMin, iMax), Math.max(iMin, iMax));
            this.mTopGraphicRectInPortrait.initialize(fArrCreateTopRightRect[0], fArrCreateTopRightRect[1], fArrCreateTopRightRect[6], fArrCreateTopRightRect[7]);
        } else {
            this.mTopGraphicRectInPortrait.changePortraitCooridnateSystem(iMin, iMax);
        }
        this.mPreviewWidth = iMin;
        this.mPreviewHeight = iMax;
        if (this.mPreviewArea != null) {
            this.mXScale = this.mPreviewWidth / Math.min(this.mPreviewArea.width(), this.mPreviewArea.height());
            this.mYScale = this.mPreviewHeight / Math.max(this.mPreviewArea.width(), this.mPreviewArea.height());
        }
        LogHelper.m23d(TAG, "[setPreviewSize]-");
    }

    @Override // com.mediatek.camera.p005v2.stream.pip.IPipGesture
    public void onPreviewAreaChanged(RectF rectF) {
        LogHelper.m23d(TAG, "onPreviewAreaChanged previewArea:" + rectF);
        this.mPreviewArea = rectF;
        if (this.mPreviewWidth > 0 && this.mPreviewHeight > 0) {
            this.mXScale = this.mPreviewWidth / Math.min(this.mPreviewArea.width(), this.mPreviewArea.height());
            this.mYScale = this.mPreviewHeight / Math.max(this.mPreviewArea.width(), this.mPreviewArea.height());
        }
    }

    @Override // com.mediatek.camera.p005v2.stream.pip.IPipGesture
    public boolean onDown(float f, float f2) {
        LogHelper.m23d(TAG, "[onDown]+ x:" + f + " y:" + f2);
        if (this.mPreviewArea == null) {
            LogHelper.m26i(TAG, "[onDown]- with mPreviewArea is null!");
            return false;
        }
        switch (this.mDisplayRotation) {
            case 90:
            case 270:
                float fHeight = this.mPreviewArea.height() - f2;
                f2 = f;
                f = fHeight;
                break;
        }
        float f3 = this.mXScale * f;
        float f4 = this.mYScale * f2;
        this.isTranslateAnimationEnable = this.mTopGraphicRectInPortrait.getRectF().contains(f3, f4);
        this.isScaleRotateAnimationEnable = this.mEditSquareRectF.contains(f3, f4);
        this.mTopGraphicRectInPortrait.setHighLightEnable(!this.isTranslateAnimationEnable ? this.isScaleRotateAnimationEnable : true);
        if (this.isTranslateAnimationEnable || this.isScaleRotateAnimationEnable) {
            this.mCallback.onTopGraphicTouched();
        }
        LogHelper.m23d(TAG, "[onDown]- x:" + f3 + " y: " + f4 + "isTranslateAnimationEnable = " + this.isTranslateAnimationEnable + " isScaleRotateAnimationEnable = " + this.isScaleRotateAnimationEnable);
        if (this.isTranslateAnimationEnable) {
            return true;
        }
        return this.isScaleRotateAnimationEnable;
    }

    @Override // com.mediatek.camera.p005v2.stream.pip.IPipGesture
    public boolean onScroll(float f, float f2, float f3, float f4) {
        if (this.mPreviewArea == null || (!this.isTranslateAnimationEnable && (!this.isScaleRotateAnimationEnable))) {
            return false;
        }
        switch (this.mDisplayRotation) {
            case 90:
            case 270:
                float f5 = -f2;
                f2 = f;
                f = f5;
                break;
            case 180:
                f = -f;
                f2 = -f2;
                break;
        }
        float f6 = this.mXScale * f;
        float f7 = this.mYScale * f2;
        if (this.isScaleRotateAnimationEnable) {
            initVertexData(-f6, -f7, 1);
        } else if (this.isTranslateAnimationEnable) {
            initVertexData(-f6, -f7, 0);
        }
        if (this.isTranslateAnimationEnable) {
            return true;
        }
        return this.isScaleRotateAnimationEnable;
    }

    @Override // com.mediatek.camera.p005v2.stream.pip.IPipGesture
    public boolean onSingleTapUp(float f, float f2) {
        if (this.isTranslateAnimationEnable && (!this.isScaleRotateAnimationEnable)) {
            this.mCallback.onTopGraphicSingleTapUp();
        }
        if (this.isTranslateAnimationEnable) {
            return true;
        }
        return this.isScaleRotateAnimationEnable;
    }

    @Override // com.mediatek.camera.p005v2.stream.pip.IPipGesture
    public boolean onLongPress(float f, float f2) {
        if (this.isTranslateAnimationEnable && (!this.isScaleRotateAnimationEnable)) {
            this.mCallback.onTopGraphicSingleTapUp();
        }
        if (this.isTranslateAnimationEnable) {
            return true;
        }
        return this.isScaleRotateAnimationEnable;
    }

    @Override // com.mediatek.camera.p005v2.stream.pip.IPipGesture
    public boolean onUp() {
        LogHelper.m23d(TAG, "onUp");
        this.mTopGraphicRectInPortrait.setHighLightEnable(false);
        this.isScaleRotateAnimationEnable = false;
        this.isScaleRotateAnimationEnable = false;
        return false;
    }

    @Override // com.mediatek.camera.p005v2.stream.pip.IPipGesture
    public AnimationRect getTopGraphicRect(int i) {
        AnimationRect animationRectCheckDisplayRotation;
        synchronized (mSyncTopGraphicRect) {
            this.mCurrentGsensorOrientation = i;
            AnimationRect animationRectCopy = this.mTopGraphicRectInPortrait.copy();
            animationRectCopy.rotate((-i) + animationRectCopy.getCurrrentRotationValue());
            float f = animationRectCopy.getRightBottom()[0];
            float f2 = animationRectCopy.getRightBottom()[1];
            if (this.mDisplayRotation == 270) {
                f = animationRectCopy.getLeftTop()[0];
                f2 = animationRectCopy.getLeftTop()[1];
            }
            updateEditSquare(f, f2, this.mEditSquareSideLength);
            animationRectCheckDisplayRotation = checkDisplayRotation(this.mDisplayRotation, animationRectCopy);
        }
        return animationRectCheckDisplayRotation;
    }

    private void updateEditSquare(float f, float f2, float f3) {
        this.mEditSquareRectF.set(f - (f3 / 2.0f), f2 - (f3 / 2.0f), (f3 / 2.0f) + f, (f3 / 2.0f) + f2);
    }

    private AnimationRect checkDisplayRotation(int i, AnimationRect animationRect) {
        if (i == 270) {
            animationRect.translate(this.mPreviewWidth - (animationRect.centerX() * 2.0f), this.mPreviewHeight - (animationRect.centerY() * 2.0f), true);
            animationRect.rotate(animationRect.getCurrrentRotationValue());
        }
        return animationRect;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getDisplayRotation(Activity activity) {
        switch (activity.getWindowManager().getDefaultDisplay().getRotation()) {
        }
        return 0;
    }

    private void initVertexData(float f, float f2, int i) {
        switch (i) {
            case 0:
                this.mTopGraphicRectInPortrait.translate(f, f2, true);
                this.mTopGraphicRectInPortrait.rotate(this.mTopGraphicRectInPortrait.getCurrrentRotationValue());
                break;
            case 1:
                AnimationRect animationRectCopy = this.mTopGraphicRectInPortrait.copy();
                animationRectCopy.rotate((-this.mCurrentGsensorOrientation) + animationRectCopy.getCurrrentRotationValue());
                float f3 = animationRectCopy.getRightBottom()[0] + f;
                float f4 = animationRectCopy.getRightBottom()[1] + f2;
                float fSqrt = ((float) Math.sqrt(((animationRectCopy.centerX() - f3) * (animationRectCopy.centerX() - f3)) + ((animationRectCopy.centerY() - f4) * (animationRectCopy.centerY() - f4)))) / ((float) Math.sqrt(((animationRectCopy.centerX() - animationRectCopy.getRightBottom()[0]) * (animationRectCopy.centerX() - animationRectCopy.getRightBottom()[0])) + ((animationRectCopy.centerY() - animationRectCopy.getRightBottom()[1]) * (animationRectCopy.centerY() - animationRectCopy.getRightBottom()[1]))));
                float fRotateAngle = (float) rotateAngle(f, f2, animationRectCopy);
                if (this.mDisplayRotation == 270) {
                    fSqrt = 1.0f / fSqrt;
                    fRotateAngle = -fRotateAngle;
                }
                this.mTopGraphicRectInPortrait.scale(fSqrt, true);
                this.mTopGraphicRectInPortrait.rotate(this.mTopGraphicRectInPortrait.getCurrrentRotationValue());
                this.mTopGraphicRectInPortrait.rotate(fRotateAngle + this.mTopGraphicRectInPortrait.getCurrrentRotationValue());
                break;
        }
    }

    private double rotateAngle(float f, float f2, AnimationRect animationRect) {
        float fCenterX = animationRect.centerX();
        float fCenterY = animationRect.centerY();
        float f3 = animationRect.getRightBottom()[0];
        float f4 = animationRect.getRightBottom()[1];
        float f5 = f3 + f;
        double dAtan2 = ((((Math.atan2((f4 + f2) - fCenterY, f5 - fCenterX) * 180.0d) / 3.141592653589793d) + 360.0d) % 360.0d) - ((((Math.atan2(f4 - fCenterY, f3 - fCenterX) * 180.0d) / 3.141592653589793d) + 360.0d) % 360.0d);
        LogHelper.m23d(TAG, "rotateAngle angle:" + dAtan2);
        return dAtan2;
    }
}
