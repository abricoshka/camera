package com.android.camera.p002v2.p003ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import com.android.camera.R$styleable;

/* loaded from: classes.dex */
public class MaxLinearLayout extends LinearLayout {
    private final int mMaxHeight;
    private final int mMaxWidth;

    public MaxLinearLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R$styleable.MaxLinearLayout, 0, 0);
        this.mMaxHeight = typedArrayObtainStyledAttributes.getDimensionPixelSize(0, Integer.MAX_VALUE);
        this.mMaxWidth = typedArrayObtainStyledAttributes.getDimensionPixelSize(1, Integer.MAX_VALUE);
        typedArrayObtainStyledAttributes.recycle();
    }

    @Override // android.widget.LinearLayout, android.view.View
    protected void onMeasure(int i, int i2) {
        super.onMeasure(buildMeasureSpec(i, this.mMaxWidth), buildMeasureSpec(i2, this.mMaxHeight));
    }

    private int buildMeasureSpec(int i, int i2) {
        int mode = View.MeasureSpec.getMode(i);
        int size = View.MeasureSpec.getSize(i);
        if (i2 >= size) {
            i2 = size;
        }
        return View.MeasureSpec.makeMeasureSpec(i2, mode);
    }
}
