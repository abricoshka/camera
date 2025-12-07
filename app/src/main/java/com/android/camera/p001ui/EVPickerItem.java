package com.android.camera.p001ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import com.mediatek.camera.R;

/* loaded from: classes.dex */
public class EVPickerItem extends ImageView {
    private Boolean mChecked;
    protected Drawable mFrame;
    private Rect mFrameBounds;
    private Drawable mOverlay;

    public EVPickerItem(Context context) {
        this(context, null);
    }

    public EVPickerItem(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public EVPickerItem(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mFrameBounds = new Rect();
        this.mChecked = false;
        this.mFrame = getResources().getDrawable(R.drawable.ev_selector_overlay);
        this.mFrame.setCallback(this);
    }

    @Override // android.widget.ImageView, android.view.View
    protected boolean verifyDrawable(Drawable drawable) {
        return super.verifyDrawable(drawable) || drawable == this.mFrame || drawable == this.mOverlay;
    }

    @Override // android.widget.ImageView, android.view.View
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        if (this.mFrame != null) {
            this.mFrame.setState(getDrawableState());
        }
    }

    @Override // android.widget.ImageView, android.view.View
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Rect rect = this.mFrameBounds;
        if (rect.isEmpty()) {
            int width = getWidth();
            int height = getHeight();
            rect.set(0, 0, width, height);
            this.mFrame.setBounds(rect);
            if (this.mOverlay != null) {
                this.mOverlay.setBounds(width - this.mOverlay.getIntrinsicWidth(), height - this.mOverlay.getIntrinsicHeight(), width, height);
            }
        }
        this.mFrame.draw(canvas);
        if (this.mOverlay != null) {
            this.mOverlay.draw(canvas);
        }
    }

    @Override // android.view.View
    protected void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        this.mFrameBounds.setEmpty();
    }

    private void toggle() {
        this.mChecked = Boolean.valueOf(!this.mChecked.booleanValue());
    }

    @Override // android.view.View
    public boolean performClick() {
        toggle();
        setSelected(this.mChecked.booleanValue());
        return super.performClick();
    }
}
