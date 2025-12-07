package com.android.camera.p001ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import com.mediatek.camera.R;

/* loaded from: classes.dex */
public class ModePickerFrameLayout extends FrameLayout {
    public ModePickerFrameLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        View viewFindViewById = findViewById(R.id.mode_picker_background);
        ModePickerScrollable modePickerScrollable = (ModePickerScrollable) findViewById(R.id.mode_picker_scroller);
        if (viewFindViewById != null && modePickerScrollable != null) {
            modePickerScrollable.setBackgroundView(viewFindViewById);
        }
    }
}
