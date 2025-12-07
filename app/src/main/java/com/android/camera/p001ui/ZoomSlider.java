package com.android.camera.p001ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import com.mediatek.camera.R;

/* loaded from: classes.dex */
public class ZoomSlider extends View {
    Rect desrect;
    private Drawable mBarDrawable;
    private PaintFlagsDrawFilter mPfd;
    private Drawable mSliderDrawable;
    private int mSliderPosition;
    private Bitmap mcountHand;

    public ZoomSlider(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mSliderPosition = 0;
        this.desrect = new Rect();
        this.mBarDrawable = context.getResources().getDrawable(R.drawable.ic_zoom_big);
        this.mBarDrawable.setCallback(this);
        this.mBarDrawable.setVisible(true, false);
        this.mSliderDrawable = context.getResources().getDrawable(R.drawable.ic_zoom_slider);
        this.mcountHand = BitmapFactory.decodeResource(getResources(), R.drawable.ic_zoom_slider);
    }

    public void setSliderPosition(int i) {
        this.mSliderPosition = i;
        invalidate();
    }

    @Override // android.view.View
    protected void onSizeChanged(int i, int i2, int i3, int i4) {
        this.mBarDrawable.setBounds(0, 0, i, i2);
    }

    @Override // android.view.View
    protected void drawableStateChanged() {
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        this.mBarDrawable.draw(canvas);
        this.mPfd = new PaintFlagsDrawFilter(0, 3);
        canvas.rotate((-(this.mSliderPosition - 5)) * 7.6f, 375.0f, 490.0f);
        this.desrect.set(0, 0, 750, 217);
        canvas.drawBitmap(this.mcountHand, (Rect) null, this.desrect, (Paint) null);
        canvas.setDrawFilter(this.mPfd);
    }
}
