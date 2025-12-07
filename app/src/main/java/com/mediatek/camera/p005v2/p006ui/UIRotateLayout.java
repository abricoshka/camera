package com.mediatek.camera.p005v2.p006ui;

import android.R;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import com.mediatek.camera.p005v2.util.Utils;

/* loaded from: classes.dex */
public class UIRotateLayout extends ViewGroup implements Rotatable {
    protected View mChild;
    private OnSizeChangedListener mListener;
    private int mOrientation;

    public interface OnSizeChangedListener {
        void onSizeChanged(int i, int i2);
    }

    public UIRotateLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setBackgroundResource(R.color.transparent);
    }

    @Override // com.mediatek.camera.p005v2.p006ui.Rotatable
    public void setOrientation(int i, boolean z) {
        int i2 = i % 360;
        if (this.mOrientation != i2) {
            this.mOrientation = i2;
            Utils.setRotatableOrientation(this.mChild, this.mOrientation, z);
            requestLayout();
        }
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mChild = getChildAt(0);
        this.mChild.setPivotX(0.0f);
        this.mChild.setPivotY(0.0f);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        int i5 = i3 - i;
        int i6 = i4 - i2;
        switch (this.mOrientation) {
            case 0:
            case 180:
                this.mChild.layout(0, 0, i5, i6);
                break;
            case 90:
            case 270:
                this.mChild.layout(0, 0, i6, i5);
                break;
        }
    }

    @Override // android.view.View
    protected void onMeasure(int i, int i2) {
        int measuredHeight;
        int measuredWidth = 0;
        switch (this.mOrientation) {
            case 0:
            case 180:
                measureChild(this.mChild, i, i2);
                measuredHeight = this.mChild.getMeasuredWidth();
                measuredWidth = this.mChild.getMeasuredHeight();
                break;
            case 90:
            case 270:
                measureChild(this.mChild, i2, i);
                measuredHeight = this.mChild.getMeasuredHeight();
                measuredWidth = this.mChild.getMeasuredWidth();
                break;
            default:
                measuredHeight = 0;
                break;
        }
        setMeasuredDimension(measuredHeight, measuredWidth);
        switch (this.mOrientation) {
            case 0:
                this.mChild.setTranslationX(0.0f);
                this.mChild.setTranslationY(0.0f);
                break;
            case 90:
                this.mChild.setTranslationX(0.0f);
                this.mChild.setTranslationY(measuredWidth);
                break;
            case 180:
                this.mChild.setTranslationX(measuredHeight);
                this.mChild.setTranslationY(measuredWidth);
                break;
            case 270:
                this.mChild.setTranslationX(measuredHeight);
                this.mChild.setTranslationY(0.0f);
                break;
        }
        this.mChild.setRotation(-this.mOrientation);
    }

    @Override // android.view.ViewGroup
    public boolean shouldDelayChildPressedState() {
        return false;
    }

    @Override // android.view.View
    protected void onSizeChanged(int i, int i2, int i3, int i4) {
        if (this.mListener != null) {
            this.mListener.onSizeChanged(i, i2);
        }
    }
}
