package com.mediatek.camera.mode.panorama;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import com.mediatek.camera.util.Log;

/* loaded from: classes.dex */
public class NaviLineImageView extends ImageView {
    private int mBottom;
    private boolean mFirstDraw;
    private int mLeft;
    private int mRight;
    private int mTop;

    public NaviLineImageView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mLeft = 0;
        this.mTop = 0;
        this.mRight = 0;
        this.mBottom = 0;
        this.mFirstDraw = false;
    }

    @Override // android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        Log.m35v("NaviLineImageView", "[onLayout]changed=" + z + " left =" + i + " top = " + i2 + " right = " + i3 + " bottom = " + i4);
        super.onLayout(z, i, i2, i3, i4);
    }

    @Override // android.view.View
    public void layout(int i, int i2, int i3, int i4) {
        Log.m35v("NaviLineImageView", "[layout]left =" + i + " top = " + i2 + " right = " + i3 + " bottom = " + i4);
        if (!this.mFirstDraw || (this.mLeft == i && this.mTop == i2 && this.mRight == i3 && this.mBottom == i4)) {
            super.layout(i, i2, i3, i4);
            this.mFirstDraw = true;
        }
    }

    public void setLayoutPosition(int i, int i2, int i3, int i4) {
        Log.m35v("NaviLineImageView", "[setLayoutPosition] left =" + i + " top = " + i2 + " right = " + i3 + " bottom = " + i4);
        this.mLeft = i;
        this.mTop = i2;
        this.mRight = i3;
        this.mBottom = i4;
        layout(i, i2, i3, i4);
    }
}
