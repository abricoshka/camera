package com.android.camera.manager;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.android.camera.CameraActivity;
import com.android.camera.Log;
import com.android.camera.Util;
import com.mediatek.camera.R;

/* loaded from: classes.dex */
public class OnScreenToast implements CameraActivity.OnOrientationListener {
    private Context mContext;
    private RelativeLayout mLayout;
    private View mNextView;
    private int mOrientation;
    private TextView mText;
    private View mView;
    private final WindowManager mWM;
    private final WindowManager.LayoutParams mParams = new WindowManager.LayoutParams();
    private final Handler mHandler = new Handler();
    private final Runnable mShow = new Runnable() { // from class: com.android.camera.manager.OnScreenToast.1
        @Override // java.lang.Runnable
        public void run() {
            OnScreenToast.this.handleShow();
            OnScreenToast.this.mHandler.postDelayed(OnScreenToast.this.mHide, 2000L);
            if (OnScreenToast.this.mContext instanceof CameraActivity) {
                ((CameraActivity) OnScreenToast.this.mContext).addOnOrientationListener(OnScreenToast.this);
                OnScreenToast.this.onOrientationChanged(((CameraActivity) OnScreenToast.this.mContext).getOrientationCompensation());
            }
        }
    };
    private final Runnable mHide = new Runnable() { // from class: com.android.camera.manager.OnScreenToast.2
        @Override // java.lang.Runnable
        public void run() {
            OnScreenToast.this.handleHide();
            if (OnScreenToast.this.mContext instanceof CameraActivity) {
                ((CameraActivity) OnScreenToast.this.mContext).removeOnOrientationListener(OnScreenToast.this);
            }
        }
    };

    private OnScreenToast(Context context) {
        this.mWM = (WindowManager) context.getSystemService("window");
        this.mParams.height = -1;
        this.mParams.width = -1;
        this.mParams.flags = 525336;
        this.mParams.format = -3;
        this.mParams.type = 1000;
        this.mParams.setTitle("OnScreenHint");
        this.mContext = context;
    }

    public void cancel() {
        this.mHandler.post(this.mHide);
    }

    public static OnScreenToast makeText(Context context, CharSequence charSequence) {
        OnScreenToast onScreenToast = new OnScreenToast(context);
        View viewInflate = ((LayoutInflater) context.getSystemService("layout_inflater")).inflate(R.layout.onscreen_mode_toast, (ViewGroup) null);
        TextView textView = (TextView) viewInflate.findViewById(R.id.message);
        textView.setText(charSequence);
        onScreenToast.mNextView = viewInflate;
        onScreenToast.mLayout = (RelativeLayout) viewInflate.findViewById(R.id.onscreen_toast_layout);
        onScreenToast.mText = textView;
        return onScreenToast;
    }

    public void setText(CharSequence charSequence) {
        if (this.mNextView == null) {
            throw new RuntimeException("This OnScreenHint was not created with OnScreenHint.makeText()");
        }
        TextView textView = (TextView) this.mNextView.findViewById(R.id.message);
        if (textView == null) {
            throw new RuntimeException("This OnScreenHint was not created with OnScreenHint.makeText()");
        }
        textView.setText(charSequence);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public synchronized void handleShow() {
        if (this.mView != this.mNextView) {
            handleHide();
            this.mView = this.mNextView;
            this.mParams.x = 0;
            this.mParams.y = 0;
            this.mParams.height = -1;
            this.mParams.width = -1;
            try {
                if (this.mView.getParent() != null) {
                    this.mWM.removeView(this.mView);
                }
                this.mWM.addView(this.mView, this.mParams);
            } catch (WindowManager.BadTokenException e) {
                e.printStackTrace();
            }
            Util.fadeIn(this.mView);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public synchronized void handleHide() {
        if (this.mView != null) {
            Util.fadeOut(this.mView);
            try {
                if (this.mView.getParent() != null) {
                    this.mWM.removeView(this.mView);
                }
            } catch (WindowManager.BadTokenException e) {
                e.printStackTrace();
            }
            this.mView = null;
        }
    }

    @Override // com.android.camera.CameraActivity.OnOrientationListener
    public void onOrientationChanged(int i) {
        if (this.mOrientation != i) {
            this.mOrientation = i;
            Util.setOrientation(this.mView, this.mOrientation, true);
        }
        if (this.mView != null) {
            if (isLandcape()) {
                ViewGroup.LayoutParams layoutParams = this.mLayout.getLayoutParams();
                layoutParams.height = -1;
                layoutParams.width = -2;
                this.mLayout.setLayoutParams(layoutParams);
                RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(-2, -2);
                layoutParams2.addRule(15);
                layoutParams2.addRule(9);
                this.mText.setLayoutParams(layoutParams2);
            } else {
                ViewGroup.LayoutParams layoutParams3 = this.mLayout.getLayoutParams();
                layoutParams3.width = -1;
                layoutParams3.height = -2;
                this.mLayout.setLayoutParams(layoutParams3);
                RelativeLayout.LayoutParams layoutParams4 = new RelativeLayout.LayoutParams(-2, -2);
                layoutParams4.addRule(14);
                this.mText.setLayoutParams(layoutParams4);
            }
            this.mView.requestLayout();
        }
    }

    private boolean isLandcape() {
        int orietation = ((CameraActivity) this.mContext).getOrietation();
        boolean z = orietation == 90 || orietation == 270;
        Log.m5d("OnScreenToast", "isLandcape() orientation=" + orietation + ", return " + z);
        return z;
    }

    public void showToast() {
        if (this.mNextView == null) {
            throw new RuntimeException("View is not initialized");
        }
        this.mHandler.removeCallbacks(this.mShow);
        this.mHandler.removeCallbacks(this.mHide);
        this.mHandler.post(this.mShow);
    }

    public void hideToast() {
        if (this.mNextView == null) {
            throw new RuntimeException("View is not initialized");
        }
        this.mHandler.removeCallbacks(this.mShow);
        this.mHandler.removeCallbacks(this.mHide);
        this.mHandler.post(this.mHide);
    }
}
