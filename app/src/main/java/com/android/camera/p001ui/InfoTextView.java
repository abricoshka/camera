package com.android.camera.p001ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

/* loaded from: classes.dex */
public class InfoTextView extends TextView {
    private float centerX;
    private float centerY;
    private int colorSel;
    private int colorUnSel;
    private Paint mPaint;
    private int radiusSel;
    private int radiusUnSel;

    public InfoTextView(Context context) {
        super(context);
        this.colorUnSel = Color.parseColor("#ffeeeeee");
        this.colorSel = Color.parseColor("#ffeeee00");
        this.radiusSel = 28;
        this.radiusUnSel = 20;
        this.mPaint = new Paint(1);
    }

    public InfoTextView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.colorUnSel = Color.parseColor("#ffeeeeee");
        this.colorSel = Color.parseColor("#ffeeee00");
        this.radiusSel = 28;
        this.radiusUnSel = 20;
        this.mPaint = new Paint(1);
    }

    public InfoTextView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.colorUnSel = Color.parseColor("#ffeeeeee");
        this.colorSel = Color.parseColor("#ffeeee00");
        this.radiusSel = 28;
        this.radiusUnSel = 20;
        this.mPaint = new Paint(1);
    }

    @Override // android.widget.TextView, android.view.View
    protected void onFocusChanged(boolean z, int i, Rect rect) {
        super.onFocusChanged(z, i, rect);
        Log.d("InfoTextView", "onFocusChanged: ");
        invalidate();
    }

    @Override // android.widget.TextView, android.view.View
    protected void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        this.mPaint.setColor(Color.parseColor("#66000000"));
    }

    @Override // android.widget.TextView, android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        this.centerX = getWidth() / 2;
        this.centerY = getHeight() / 2;
    }

    @Override // android.widget.TextView, android.view.View
    protected void onDraw(Canvas canvas) {
        if (isFocused() || isPressed() || isSelected()) {
            setTextColor(this.colorSel);
            setTextSize(13.0f);
            canvas.drawCircle(this.centerX, this.centerY, this.radiusSel, this.mPaint);
        } else {
            setTextColor(this.colorUnSel);
            setTextSize(10.0f);
        }
        super.onDraw(canvas);
    }
}
