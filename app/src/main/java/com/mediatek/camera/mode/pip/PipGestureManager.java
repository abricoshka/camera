package com.mediatek.camera.mode.pip;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.graphics.RectF;
import android.view.Display;
import android.view.WindowManager;
import com.mediatek.camera.mode.pip.pipwrapping.AnimationRect;
import com.mediatek.camera.mode.pip.pipwrapping.GLUtil;
import com.mediatek.camera.util.Log;

/* loaded from: classes.dex */
public class PipGestureManager {
    private static Object mSyncTopGraphicRect = new Object();
    private final int RECT_TO_TOP;
    private Context mContext;
    private float mCurrentRectToTop;
    private RectF mEditButtonRect;
    private Listener mListener;
    private AnimationRect mTopGraphicRect;
    private float mRotatedRotation = 0.0f;
    private int mKeepLastOrientation = 0;
    private int mKeepLastDisplayRotation = 0;
    private float mXScale = 1.0f;
    private float mYScale = 1.0f;
    private boolean isTranslateAnimation = false;
    private boolean isScaleRotateAnimation = false;
    private int mPreviewWidth = 0;
    private int mPreviewHeight = 0;
    private float mRotation = 0.0f;
    private int mKeepPreviewOrientation = 0;
    private int mCurrentPreviewOrientation = 0;
    private int mEditButtonSize = 0;

    public interface Listener {
        int getButtomGraphicCameraId();

        void notifyTopGraphicIsEdited();

        void switchPIP();
    }

    public PipGestureManager(Context context, Listener listener) {
        this.mTopGraphicRect = null;
        Log.m31d("PipGestureManager", "PIPGestureManager");
        this.mContext = context;
        this.RECT_TO_TOP = 100;
        this.mListener = listener;
        this.mTopGraphicRect = new AnimationRect();
        this.mEditButtonRect = new RectF();
    }

    public void setRendererSize(int i, int i2) {
        Log.m31d("PipGestureManager", "setPreviewSize width = " + i + " height = " + i2 + " oldWidth = " + this.mPreviewWidth + " oldHeight = " + this.mPreviewHeight + " mTopGraphicRect = " + this.mTopGraphicRect + " mCurrentPreviewOrientation = " + this.mCurrentPreviewOrientation + " mKeepPreviewOrientation = " + this.mKeepPreviewOrientation);
        if (this.mTopGraphicRect != null) {
            if (i == this.mPreviewWidth && i2 == this.mPreviewHeight) {
                return;
            }
            synchronized (mSyncTopGraphicRect) {
                Display defaultDisplay = ((WindowManager) this.mContext.getSystemService("window")).getDefaultDisplay();
                Point point = new Point();
                defaultDisplay.getRealSize(point);
                int iMax = Math.max(point.x, point.y);
                if (this.mPreviewWidth == 0 && this.mPreviewHeight == 0) {
                    this.mCurrentRectToTop = (this.RECT_TO_TOP * Math.max(i, i2)) / iMax;
                    float[] fArrCreateTopRightRect = GLUtil.createTopRightRect(i, i2, this.mCurrentRectToTop);
                    this.mTopGraphicRect.setRendererSize(i, i2);
                    this.mTopGraphicRect.initialize(fArrCreateTopRightRect[0], fArrCreateTopRightRect[1], fArrCreateTopRightRect[6], fArrCreateTopRightRect[7]);
                    this.mTopGraphicRect.rotate(this.mRotation);
                } else {
                    float fMin = Math.min(i, i2) / Math.min(this.mPreviewWidth, this.mPreviewHeight);
                    float fMax = Math.max(i, i2) / Math.max(this.mPreviewWidth, this.mPreviewHeight);
                    this.mTopGraphicRect.setRendererSize(i, i2);
                    float fCenterX = this.mTopGraphicRect.centerX();
                    float fCenterY = this.mTopGraphicRect.centerY();
                    switch (((this.mCurrentPreviewOrientation - this.mKeepPreviewOrientation) + 360) % 360) {
                        case 90:
                            fCenterX = this.mPreviewHeight - fCenterY;
                            fCenterY = fCenterX;
                            break;
                        case 270:
                            float f = this.mPreviewWidth - fCenterX;
                            fCenterX = fCenterY;
                            fCenterY = f;
                            break;
                    }
                    this.mTopGraphicRect.translate(fCenterX - this.mTopGraphicRect.centerX(), fCenterY - this.mTopGraphicRect.centerY(), false);
                    this.mTopGraphicRect.translate((this.mTopGraphicRect.centerX() * fMin) - this.mTopGraphicRect.centerX(), (this.mTopGraphicRect.centerY() * fMax) - this.mTopGraphicRect.centerY(), false);
                    this.mTopGraphicRect.scale(fMin, false);
                    this.mTopGraphicRect.scaleToTranslateY(fMax / fMin);
                    this.mRotation += this.mCurrentPreviewOrientation - this.mKeepPreviewOrientation;
                    this.mRotation = AnimationRect.formatRotationValue(this.mRotation);
                    this.mRotation = AnimationRect.checkRotationLimit(this.mRotation, this.mRotatedRotation);
                    this.mTopGraphicRect.rotate(this.mRotation);
                }
                this.mKeepPreviewOrientation = this.mCurrentPreviewOrientation;
                this.mPreviewWidth = i;
                this.mPreviewHeight = i2;
                this.mTopGraphicRect.setRendererSize(i, i2);
            }
            this.mEditButtonSize = Math.min(i, i2) / 10;
            initEditButtonRect(this.mTopGraphicRect.getRightBottom()[0], this.mTopGraphicRect.getRightBottom()[1], this.mEditButtonSize);
        }
    }

    public void onViewOrientationChanged(int i) {
        Log.m31d("PipGestureManager", "onOrientationChanged orientation = " + i + " mKeepLastOrientation = " + this.mKeepLastOrientation);
        synchronized (mSyncTopGraphicRect) {
            if (i != this.mKeepLastOrientation) {
                this.mRotatedRotation += ((360 - i) + this.mKeepLastOrientation) % 360;
                this.mRotatedRotation = AnimationRect.formatRotationValue(this.mRotatedRotation);
                rotate(i - this.mKeepLastOrientation);
                this.mKeepLastOrientation = i;
                Log.m31d("PipGestureManager", "onOrientationChanged orientation = " + i + " mKeepLastOrientation = " + this.mKeepLastOrientation + " mRotatedRotation = " + this.mRotatedRotation);
            }
        }
    }

    public void setDisplayRotation(int i) {
        Log.m31d("PipGestureManager", "setDisplayRotation displayRotation = " + i);
        synchronized (mSyncTopGraphicRect) {
            if (i != this.mKeepLastDisplayRotation) {
                Log.m31d("PipGestureManager", "setDisplayRotation rotate = " + (this.mKeepLastDisplayRotation - i));
                rotate(this.mKeepLastDisplayRotation - i);
                if (Math.abs(this.mKeepLastDisplayRotation - i) >= 180) {
                    Log.m31d("PipGestureManager", "setDisplayRotation translate x = " + (this.mPreviewWidth - (this.mTopGraphicRect.centerX() * 2.0f)) + " y = " + (this.mPreviewHeight - (this.mTopGraphicRect.centerY() * 2.0f)));
                    initVertexData(this.mPreviewWidth - (this.mTopGraphicRect.centerX() * 2.0f), this.mPreviewHeight - (this.mTopGraphicRect.centerY() * 2.0f), 0);
                }
                this.mKeepLastDisplayRotation = i;
            }
        }
    }

    public boolean onDown(float f, float f2, int i, int i2) {
        Log.m31d("PipGestureManager", "onDown x = " + f + " y = " + f2 + " relativeWidth = " + i + " relativeHeight = " + i2);
        switch (GLUtil.getDisplayRotation((Activity) this.mContext)) {
            case 90:
                f = i2 - f2;
                f2 = f;
                break;
            case 180:
                f = i - f;
                f2 = i2 - f2;
                break;
            case 270:
                float f3 = i - f;
                f = f2;
                f2 = f3;
                break;
        }
        if ((GLUtil.getDisplayOrientation(0, this.mListener.getButtomGraphicCameraId()) % 90 == 0) == (GLUtil.getDisplayRotation((Activity) this.mContext) % 180 == 0)) {
            i2 = i;
            i = i2;
        }
        this.mXScale = this.mPreviewWidth / i2;
        this.mYScale = this.mPreviewHeight / i;
        float f4 = this.mXScale * f;
        float f5 = this.mYScale * f2;
        Log.m31d("PipGestureManager", "scale: mXScale = " + this.mXScale + "mYScale = " + this.mYScale);
        this.isTranslateAnimation = this.mTopGraphicRect.getRectF().contains(f4, f5);
        this.isScaleRotateAnimation = this.mEditButtonRect.contains(f4, f5);
        this.mTopGraphicRect.setHighLightEnable(!this.isTranslateAnimation ? this.isScaleRotateAnimation : true);
        Log.m31d("PipGestureManager", "isTranslateAnimation = " + this.isTranslateAnimation + " isScaleAnimation = " + this.isScaleRotateAnimation);
        if (this.isTranslateAnimation || this.isScaleRotateAnimation) {
            this.mListener.notifyTopGraphicIsEdited();
        }
        if (this.isTranslateAnimation) {
            return true;
        }
        return this.isScaleRotateAnimation;
    }

    public boolean onScroll(float f, float f2, float f3, float f4) {
        Log.m31d("PipGestureManager", "before onScroll dx = " + f + " dy = " + f2 + " totalX = " + f3 + " totalY = " + f4 + " isTranslateAnimation = " + this.isTranslateAnimation + " isScaleAnimation = " + this.isScaleRotateAnimation);
        if (!this.isTranslateAnimation && (!this.isScaleRotateAnimation)) {
            return false;
        }
        synchronized (mSyncTopGraphicRect) {
            switch (this.mKeepLastDisplayRotation) {
                case 90:
                    f = -f2;
                    f2 = f;
                    break;
                case 180:
                    f = -f;
                    f2 = -f2;
                    break;
                case 270:
                    float f5 = -f;
                    f = f2;
                    f2 = f5;
                    break;
            }
            float f6 = this.mXScale * f;
            float f7 = this.mYScale * f2;
            if (this.isScaleRotateAnimation) {
                initVertexData(-f6, -f7, 1);
            } else if (this.isTranslateAnimation) {
                initVertexData(-f6, -f7, 0);
            }
        }
        if (this.isTranslateAnimation) {
            return true;
        }
        return this.isScaleRotateAnimation;
    }

    public boolean onUp() {
        Log.m31d("PipGestureManager", "onUp");
        this.mTopGraphicRect.setHighLightEnable(false);
        this.isScaleRotateAnimation = false;
        this.isTranslateAnimation = false;
        return false;
    }

    public boolean onSingleTapUp(float f, float f2) {
        Log.m31d("PipGestureManager", "onSingleTapUp x = " + f + " y = " + f2 + " isTranslateAnimation = " + this.isTranslateAnimation);
        if (this.isTranslateAnimation && (!this.isScaleRotateAnimation)) {
            this.mListener.switchPIP();
        }
        if (this.isTranslateAnimation) {
            return true;
        }
        return this.isScaleRotateAnimation;
    }

    public boolean onLongPress(float f, float f2) {
        if (this.isTranslateAnimation) {
            this.mListener.switchPIP();
        }
        if (this.isTranslateAnimation) {
            return true;
        }
        return this.isScaleRotateAnimation;
    }

    public void rotate(int i) {
        Log.m31d("PipGestureManager", "rotate degrees = " + i + " mTopGraphicRect = " + this.mTopGraphicRect);
        if (this.mTopGraphicRect == null) {
            return;
        }
        this.mRotation += -i;
        this.mRotation = AnimationRect.formatRotationValue(this.mRotation);
        this.mRotation = AnimationRect.checkRotationLimit(this.mRotation, this.mRotatedRotation);
        this.mTopGraphicRect.rotate(this.mRotation);
    }

    public AnimationRect getTopGraphicRect() {
        AnimationRect animationRectCopy;
        synchronized (mSyncTopGraphicRect) {
            initEditButtonRect(this.mTopGraphicRect.getRightBottom()[0], this.mTopGraphicRect.getRightBottom()[1], this.mEditButtonSize);
            animationRectCopy = this.mTopGraphicRect.copy();
        }
        return animationRectCopy;
    }

    private void initVertexData(float f, float f2, int i) {
        switch (i) {
            case 0:
                this.mTopGraphicRect.translate(f, f2, true);
                this.mTopGraphicRect.rotate(this.mRotation);
                break;
            case 1:
                float f3 = this.mTopGraphicRect.getRightBottom()[0] + f;
                float f4 = this.mTopGraphicRect.getRightBottom()[1] + f2;
                float fAdjustScaleDistance = this.mTopGraphicRect.adjustScaleDistance((float) Math.sqrt(((this.mTopGraphicRect.centerX() - f3) * (this.mTopGraphicRect.centerX() - f3)) + ((this.mTopGraphicRect.centerY() - f4) * (this.mTopGraphicRect.centerY() - f4)))) / ((float) Math.sqrt(((this.mTopGraphicRect.centerX() - this.mTopGraphicRect.getRightBottom()[0]) * (this.mTopGraphicRect.centerX() - this.mTopGraphicRect.getRightBottom()[0])) + ((this.mTopGraphicRect.centerY() - this.mTopGraphicRect.getRightBottom()[1]) * (this.mTopGraphicRect.centerY() - this.mTopGraphicRect.getRightBottom()[1]))));
                this.mTopGraphicRect.translate(0.0f, 0.0f, true);
                this.mTopGraphicRect.scale(fAdjustScaleDistance, true);
                this.mTopGraphicRect.rotate(this.mRotation);
                this.mRotation = ((float) rotateAngle(f, f2)) + this.mRotation;
                this.mRotation = AnimationRect.formatRotationValue(this.mRotation);
                this.mRotation = AnimationRect.checkRotationLimit(this.mRotation, this.mRotatedRotation);
                this.mTopGraphicRect.rotate(this.mRotation);
                break;
        }
    }

    private double rotateAngle(float f, float f2) {
        float fCenterX = this.mTopGraphicRect.centerX();
        float fCenterY = this.mTopGraphicRect.centerY();
        float f3 = this.mTopGraphicRect.getRightBottom()[0];
        float f4 = this.mTopGraphicRect.getRightBottom()[1];
        float f5 = f3 + f;
        return ((((Math.atan2((f4 + f2) - fCenterY, f5 - fCenterX) * 180.0d) / 3.141592653589793d) + 360.0d) % 360.0d) - ((((Math.atan2(f4 - fCenterY, f3 - fCenterX) * 180.0d) / 3.141592653589793d) + 360.0d) % 360.0d);
    }

    private void initEditButtonRect(float f, float f2, float f3) {
        Log.m31d("PipGestureManager", "initVertexData rCenterX = " + f + " rCenterY = " + f2 + " edge = " + f3);
        this.mEditButtonRect.set(f - (f3 / 2.0f), f2 - (f3 / 2.0f), (f3 / 2.0f) + f, (f3 / 2.0f) + f2);
    }
}
