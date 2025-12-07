package com.android.camera.p001ui;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.SystemProperties;
import android.util.AttributeSet;
import android.view.View;
import com.android.camera.CameraActivity;
import com.android.camera.Log;
import com.android.camera.Util;
import com.mediatek.camera.R;

/* loaded from: classes.dex */
public class MultiZoneAfView extends View {
    private static final int[] MZAF_ICON = {R.drawable.ic_multi_zone_focus_focusing, R.drawable.ic_multi_zone_focus_focused};
    private Drawable mAfIndicator;
    private Drawable[] mAfStatusIndicators;
    private float mAnimatorRatio;
    private CameraActivity mContext;
    private int mDisplayOrientation;
    private Matrix mMatrix;
    private boolean mMirror;
    private int mOrientation;
    private RectF mRect;
    private float mScaleRatio;
    private ValueAnimator mValueAnimator;
    private MultiWindow[] mWindows;

    public MultiZoneAfView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mScaleRatio = 0.0f;
        this.mAnimatorRatio = 1.0f;
        this.mMatrix = new Matrix();
        this.mRect = new RectF();
        this.mValueAnimator = new ValueAnimator();
        this.mAfStatusIndicators = new Drawable[MZAF_ICON.length];
        this.mContext = (CameraActivity) context;
        getViewDrawable();
        this.mAfIndicator = this.mAfStatusIndicators[0];
        this.mScaleRatio = Float.parseFloat(SystemProperties.get("multizone.af.window.ratio", "0.4"));
    }

    public void setMirror(boolean z) {
        this.mMirror = z;
    }

    public void updateFocusWindows(MultiWindow[] multiWindowArr) {
        this.mWindows = multiWindowArr;
    }

    public void showWindows(boolean z) {
        this.mValueAnimator.cancel();
        if (z) {
            this.mValueAnimator = ValueAnimator.ofFloat(1.0f, 1.2f).setDuration(1000L);
        } else {
            this.mValueAnimator = ValueAnimator.ofFloat(this.mAnimatorRatio, 1.0f).setDuration(200L);
        }
        this.mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.camera.ui.MultiZoneAfView.1
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                MultiZoneAfView.this.mAnimatorRatio = ((Float) valueAnimator.getAnimatedValue()).floatValue();
                if (MultiZoneAfView.this.mAnimatorRatio * MultiZoneAfView.this.mScaleRatio <= 1.0f) {
                    MultiZoneAfView.this.invalidate();
                }
            }
        });
        this.mValueAnimator.start();
    }

    public void clear() {
        this.mWindows = null;
        invalidate();
    }

    public void setOrientation(int i) {
        this.mOrientation = i;
        invalidate();
    }

    public void setDisplayOrientation(int i) {
        this.mDisplayOrientation = i;
        Log.m5d("MultiZoneAfView", "mDisplayOrientation=" + i);
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        if (this.mWindows != null && this.mWindows.length > 0) {
            Log.m5d("MultiZoneAfView", "onDraw length " + this.mWindows.length + " ,mDisplayOrientation = " + this.mDisplayOrientation + " ,mOrientation= " + this.mOrientation + ",mMirror =" + this.mMirror);
            int unCropWidth = this.mContext.getUnCropWidth();
            int unCropHeight = this.mContext.getUnCropHeight();
            if ((unCropHeight <= unCropWidth || (this.mDisplayOrientation != 0 && this.mDisplayOrientation != 180)) && (unCropHeight >= unCropWidth || (this.mDisplayOrientation != 90 && this.mDisplayOrientation != 270))) {
                unCropHeight = unCropWidth;
                unCropWidth = unCropHeight;
            }
            Util.prepareMatrix(this.mMatrix, this.mMirror, this.mDisplayOrientation, unCropHeight, unCropWidth);
            Matrix matrix = new Matrix();
            float[] fArr = {(getWidth() - unCropHeight) / 2.0f, (getHeight() - unCropWidth) / 2.0f};
            canvas.save();
            this.mMatrix.postRotate(this.mOrientation);
            canvas.rotate(-this.mOrientation);
            matrix.postRotate(this.mContext.getOrientationCompensation());
            matrix.mapPoints(fArr);
            for (int i = 0; i < this.mWindows.length; i++) {
                this.mRect.set(this.mWindows[i].mBounds);
                Util.dumpRect(this.mRect, "Original rect");
                this.mMatrix.mapRect(this.mRect);
                Util.dumpRect(this.mRect, "Transformed rect");
                Log.m5d("MultiZoneAfView", "window[ " + i + " ] result " + this.mWindows[i].mResult);
                if (this.mWindows[i].mResult > 0) {
                    this.mAfIndicator = this.mAfStatusIndicators[1];
                } else {
                    this.mAfIndicator = this.mAfStatusIndicators[0];
                }
                this.mRect.offset(fArr[0], fArr[1]);
                this.mAfIndicator.setBounds(scale());
                this.mAfIndicator.draw(canvas);
            }
            canvas.restore();
        }
        super.onDraw(canvas);
    }

    private Drawable[] getViewDrawable() {
        int length = this.mAfStatusIndicators.length;
        for (int i = 0; i < length; i++) {
            this.mAfStatusIndicators[i] = this.mContext.getResources().getDrawable(MZAF_ICON[i]);
        }
        return this.mAfStatusIndicators;
    }

    private Rect scale() {
        Rect rect = new Rect();
        float fCenterX = this.mRect.centerX();
        float fCenterY = this.mRect.centerY();
        float fWidth = this.mRect.width();
        float fHeight = this.mRect.height();
        rect.set((int) (fCenterX - (((this.mAnimatorRatio * fWidth) * this.mScaleRatio) / 2.0f)), (int) (fCenterY - (((this.mAnimatorRatio * fHeight) * this.mScaleRatio) / 2.0f)), (int) (fCenterX + (((fWidth * this.mAnimatorRatio) * this.mScaleRatio) / 2.0f)), (int) (fCenterY + (((this.mAnimatorRatio * fHeight) * this.mScaleRatio) / 2.0f)));
        return rect;
    }

    public static final class MultiWindow {
        public Rect mBounds;
        public int mResult;

        public MultiWindow(Rect rect, int i) {
            this.mBounds = rect;
            this.mResult = i;
        }

        public String toString() {
            return String.format("{ bounds: %s, result: %s}", this.mBounds, Integer.valueOf(this.mResult));
        }
    }
}
