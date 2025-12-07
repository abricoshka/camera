package com.android.camera.p002v2.uimanager;

import android.app.Activity;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.android.camera.p002v2.p003ui.UiUtil;
import com.mediatek.camera.R;

/* loaded from: classes.dex */
public class OnScreenToast {
    private TextView mMessageView;
    private View mToastView;
    private final Handler mHandler = new Handler();
    private final Runnable mShow = new Runnable() { // from class: com.android.camera.v2.uimanager.OnScreenToast.1
        @Override // java.lang.Runnable
        public void run() {
            UiUtil.fadeIn(OnScreenToast.this.mToastView);
            OnScreenToast.this.mHandler.postDelayed(OnScreenToast.this.mHide, 2000L);
        }
    };
    private final Runnable mHide = new Runnable() { // from class: com.android.camera.v2.uimanager.OnScreenToast.2
        @Override // java.lang.Runnable
        public void run() {
            UiUtil.fadeOut(OnScreenToast.this.mToastView);
        }
    };

    public OnScreenToast(Activity activity, ViewGroup viewGroup) {
        this.mToastView = activity.getLayoutInflater().inflate(R.layout.onscreen_mode_toast_v2, viewGroup, false);
        this.mMessageView = (TextView) this.mToastView.findViewById(R.id.message);
        viewGroup.addView(this.mToastView);
    }

    public void showToast(CharSequence charSequence) {
        this.mMessageView.setText(charSequence);
        this.mHandler.removeCallbacks(this.mShow);
        this.mHandler.removeCallbacks(this.mHide);
        this.mHandler.post(this.mShow);
    }
}
