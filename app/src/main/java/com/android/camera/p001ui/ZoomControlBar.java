package com.android.camera.p001ui;

import android.content.Context;
import android.util.AttributeSet;
import com.android.camera.Util;

/* loaded from: classes.dex */
public class ZoomControlBar extends ZoomControl {
    private int coordinateX;
    private int mLastSetSliderPosition;
    private ZoomSlider mSliderBar;
    private int mSliderLength;
    private int mSliderPosition;
    private boolean mStartChanging;
    private int mTotalIconWidth;
    private int mWidth;
    private int totalmultiple;
    private static final int THRESHOLD_FIRST_MOVE = Util.dpToPixel(15);
    private static final int ICON_SPACING = Util.dpToPixel(12);
    private static final int THRESHOLD_MOVE = Util.dpToPixel(6);

    public ZoomControlBar(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mSliderPosition = 0;
        this.mSliderLength = 750;
        this.mTotalIconWidth = 0;
        this.mLastSetSliderPosition = 0;
        this.coordinateX = 0;
        this.totalmultiple = 10;
        this.mSliderBar = new ZoomSlider(context, attributeSet);
        addView(this.mSliderBar);
        this.mSliderBar.setVisibility(0);
    }

    @Override // com.android.camera.p001ui.ZoomControl, android.view.View
    public void setActivated(boolean z) {
        super.setActivated(z);
        this.mSliderBar.setActivated(z);
    }

    @Override // android.view.View
    protected void onSizeChanged(int i, int i2, int i3, int i4) {
        this.mWidth = i;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Removed duplicated region for block: B:16:0x0039  */
    /* JADX WARN: Removed duplicated region for block: B:18:0x003d  */
    /* JADX WARN: Removed duplicated region for block: B:21:0x0052  */
    @Override // android.view.ViewGroup, android.view.View
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public boolean dispatchTouchEvent(android.view.MotionEvent r5) {
        /*
            r4 = this;
            r1 = 1
            r2 = 0
            boolean r0 = r4.isEnabled()
            if (r0 == 0) goto Lc
            int r0 = r4.mWidth
            if (r0 != 0) goto Ld
        Lc:
            return r2
        Ld:
            int r0 = r5.getAction()
            switch(r0) {
                case 0: goto L1c;
                case 1: goto L15;
                case 2: goto L28;
                case 3: goto L15;
                case 4: goto L15;
                default: goto L14;
            }
        L14:
            return r1
        L15:
            r4.setActivated(r2)
            r4.closeZoomControl()
            goto L14
        L1c:
            r4.setActivated(r1)
            r4.mStartChanging = r2
            float r0 = r5.getX()
            int r0 = (int) r0
            r4.coordinateX = r0
        L28:
            float r0 = r5.getX()
            int r0 = (int) r0
            int r2 = r4.coordinateX
            int r2 = r2 - r0
            int r3 = com.android.camera.p001ui.ZoomControlBar.THRESHOLD_FIRST_MOVE
            if (r2 >= r3) goto L39
            int r3 = com.android.camera.p001ui.ZoomControlBar.THRESHOLD_FIRST_MOVE
            int r3 = -r3
            if (r2 > r3) goto L4e
        L39:
            r4.coordinateX = r0
            if (r2 <= 0) goto L52
            r0 = r1
        L3e:
            com.android.camera.CameraActivity r2 = r4.mcontext
            int r3 = r4.mZoomIndex
            int r3 = r3 + r0
            r2.getPerformZoom(r3, r1)
            com.android.camera.ui.ZoomSlider r2 = r4.mSliderBar
            int r3 = r4.mZoomIndex
            int r0 = r0 + r3
            r2.setSliderPosition(r0)
        L4e:
            r4.requestLayout()
            goto L14
        L52:
            r0 = -1
            goto L3e
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.camera.p001ui.ZoomControlBar.dispatchTouchEvent(android.view.MotionEvent):boolean");
    }

    @Override // android.widget.RelativeLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        if (this.mZoomMax == 0) {
            return;
        }
        int i5 = i4 - i2;
        if (this.mSliderPosition != -1) {
            int i6 = this.mSliderPosition;
        } else {
            double d = (this.mSliderLength * this.mZoomIndex) / this.mZoomMax;
        }
        int i7 = this.mOrientation;
        this.mSliderBar.layout(this.mTotalIconWidth, 0, this.mWidth - this.mTotalIconWidth, i5);
        setZoomIndex(this.mZoomIndex);
    }

    @Override // com.android.camera.p001ui.ZoomControl
    public void setZoomIndex(int i) {
        super.setZoomIndex(i);
        this.mSliderPosition = -1;
        this.mSliderBar.setSliderPosition(i);
        requestLayout();
    }
}
