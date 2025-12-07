package com.mediatek.camera.mode.facebeauty;

import android.R;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import com.mediatek.camera.p004ui.Rotatable;
import com.mediatek.camera.util.Util;

/* loaded from: classes.dex */
public class FaceBeautyRotateLayout extends ViewGroup implements Rotatable {
    private View mChild;
    private int mOrientation;

    public FaceBeautyRotateLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setBackgroundResource(R.color.transparent);
    }

    @Override // com.mediatek.camera.p004ui.Rotatable
    public void setOrientation(int i, boolean z) {
        int i2 = ((i - 270) + 360) % 360;
        if (this.mOrientation != i2) {
            this.mOrientation = i2;
            Util.setOrientation(this.mChild, this.mOrientation, z);
            requestLayout();
        }
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        this.mChild = getChildAt(0);
        this.mChild.setPivotX(0.0f);
        this.mChild.setPivotY(0.0f);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        View view = this.mChild;
        view.layout(0, 0, i4 - i2, i3 - i);
    }

    @Override // android.view.View
    protected void onMeasure(int i, int i2) {
        measureChild(this.mChild, i2, i);
        int measuredHeight = this.mChild.getMeasuredHeight();
        setMeasuredDimension(measuredHeight, this.mChild.getMeasuredWidth());
        this.mChild.setTranslationX(measuredHeight);
        this.mChild.setTranslationY(0.0f);
        this.mChild.setRotation(-270.0f);
    }

    @Override // android.view.ViewGroup
    public boolean shouldDelayChildPressedState() {
        return false;
    }
}
