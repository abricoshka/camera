package com.android.camera.p001ui;

import android.content.Context;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.View;

/* loaded from: classes.dex */
public class RenWuBackGroundView extends View {
    private Paint mPaint;
    private Paint mPaint_circle;

    public RenWuBackGroundView(Context context) {
        super(context);
        init();
    }

    public RenWuBackGroundView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init();
    }

    public RenWuBackGroundView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init();
    }

    private void init() {
        this.mPaint = new Paint(1);
        this.mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        this.mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        this.mPaint.setMaskFilter(new BlurMaskFilter(10.0f, BlurMaskFilter.Blur.INNER));
        setLayerType(1, null);
        setClickable(true);
        setWillNotDraw(false);
        this.mPaint_circle = new Paint();
        this.mPaint_circle.setColor(-1);
        this.mPaint_circle.setStrokeWidth(8.0f);
        this.mPaint_circle.setAntiAlias(true);
        this.mPaint_circle.setStyle(Paint.Style.STROKE);
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawARGB(80, 0, 0, 0);
        canvas.drawCircle(getWidth() / 2, (getHeight() / 2) - dip2px(30.0f), dip2px(135.0f), this.mPaint);
        canvas.drawCircle(getWidth() / 2, (getHeight() / 2) - dip2px(30.0f), dip2px(135.0f), this.mPaint_circle);
    }

    private int dip2px(float f) {
        return (int) ((getResources().getDisplayMetrics().density * f) + 0.5f);
    }

    public void setCircleColor(int i) {
        if (this.mPaint_circle != null) {
            this.mPaint_circle.setColor(i);
            invalidate();
        }
    }
}
