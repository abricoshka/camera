package com.mediatek.camera.p004ui;

import android.app.Activity;
import android.content.res.Resources;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import com.mediatek.camera.R;
import com.mediatek.camera.util.Log;

/* loaded from: classes.dex */
public class ProgressIndicator {
    private static int sIndicatorMarginLong = 0;
    private static int sIndicatorMarginShort = 0;
    public int mBlockNumber;
    private int mBlockPadding;
    public ImageView mProgressBars;
    public View mProgressView;

    public ProgressIndicator(Activity activity, int i, int[] iArr) {
        this.mBlockPadding = 0;
        this.mBlockNumber = 9;
        this.mBlockPadding = 4;
        this.mProgressView = activity.findViewById(R.id.progress_indicator);
        this.mProgressView.setVisibility(0);
        this.mProgressBars = (ImageView) activity.findViewById(R.id.progress_bars);
        this.mBlockNumber = i;
        int[] iArr2 = new int[i];
        System.arraycopy(iArr, 0, iArr2, 0, i);
        float f = activity.getResources().getDisplayMetrics().density;
        if (f != 1.0f) {
            this.mBlockPadding = (int) ((this.mBlockPadding * f) + 0.5f);
            for (int i2 = 0; i2 < this.mBlockNumber; i2++) {
                iArr2[i2] = (int) ((iArr[i2] * f) + 0.5f);
            }
        }
        this.mProgressBars.setImageDrawable(new ProgressBarDrawable(activity, this.mProgressBars, iArr2, this.mBlockPadding));
        getIndicatorMargin();
    }

    public void setVisibility(int i) {
        this.mProgressView.setVisibility(i);
    }

    public void setProgress(int i) {
        this.mProgressBars.setImageLevel(i);
    }

    private void getIndicatorMargin() {
        if (sIndicatorMarginLong == 0 && sIndicatorMarginShort == 0) {
            Resources resources = this.mProgressView.getResources();
            sIndicatorMarginLong = resources.getDimensionPixelSize(R.dimen.progress_indicator_bottom_long);
            sIndicatorMarginShort = resources.getDimensionPixelSize(R.dimen.progress_indicator_bottom_short);
        }
        Log.m31d("ProgressIndicator", "[getIndicatorMargin]sIndicatorMarginLong = " + sIndicatorMarginLong + " sIndicatorMarginShort = " + sIndicatorMarginShort);
    }

    public void setOrientation(int i) {
        LinearLayout linearLayout = (LinearLayout) this.mProgressView;
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(linearLayout.getLayoutParams());
        int i2 = this.mProgressView.getResources().getConfiguration().orientation;
        if ((2 == i2 && (i == 0 || i == 180)) || (1 == i2 && (i == 90 || i == 270))) {
            layoutParams.setMargins(layoutParams.leftMargin, layoutParams.topMargin, layoutParams.rightMargin, sIndicatorMarginShort);
        } else {
            layoutParams.setMargins(layoutParams.leftMargin, layoutParams.topMargin, layoutParams.rightMargin, sIndicatorMarginLong);
        }
        layoutParams.addRule(12);
        layoutParams.addRule(14);
        linearLayout.setLayoutParams(layoutParams);
        linearLayout.requestLayout();
    }
}
