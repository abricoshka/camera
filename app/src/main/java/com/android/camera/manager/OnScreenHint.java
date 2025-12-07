package com.android.camera.manager;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;
import com.android.camera.CameraActivity;
import com.android.camera.Log;
import com.android.camera.Util;
import com.mediatek.camera.R;

/* loaded from: classes.dex */
public class OnScreenHint implements CameraActivity.OnOrientationListener {
    private Context mContext;
    View mNextView;
    private int mOrientation;
    View mView;
    private final WindowManager mWM;
    private final WindowManager.LayoutParams mParams = new WindowManager.LayoutParams();
    private final Handler mHandler = new Handler();
    private final Runnable mShow = new Runnable() { // from class: com.android.camera.manager.OnScreenHint.1
        @Override // java.lang.Runnable
        public void run() {
            OnScreenHint.this.handleShow();
            if (OnScreenHint.this.mContext instanceof CameraActivity) {
                ((CameraActivity) OnScreenHint.this.mContext).addOnOrientationListener(OnScreenHint.this);
                OnScreenHint.this.onOrientationChanged(((CameraActivity) OnScreenHint.this.mContext).getOrientationCompensation());
            }
        }
    };
    private final Runnable mHide = new Runnable() { // from class: com.android.camera.manager.OnScreenHint.2
        @Override // java.lang.Runnable
        public void run() {
            OnScreenHint.this.handleHide();
            if (OnScreenHint.this.mContext instanceof CameraActivity) {
                ((CameraActivity) OnScreenHint.this.mContext).removeOnOrientationListener(OnScreenHint.this);
            }
        }
    };

    private OnScreenHint(Context context) {
        this.mWM = (WindowManager) context.getSystemService("window");
        this.mParams.height = -1;
        this.mParams.width = -1;
        this.mParams.flags = 525336;
        this.mParams.format = -3;
        this.mParams.type = 1000;
        this.mParams.setTitle("OnScreenHint");
        this.mContext = context;
    }

    public void show() {
        if (this.mNextView == null) {
            throw new RuntimeException("View is not initialized");
        }
        this.mHandler.post(this.mShow);
    }

    public void cancel() {
        this.mHandler.post(this.mHide);
    }

    public static OnScreenHint makeText(Context context, CharSequence charSequence) {
        OnScreenHint onScreenHint = new OnScreenHint(context);
        View viewInflate = ((LayoutInflater) context.getSystemService("layout_inflater")).inflate(R.layout.onscreen_hint, (ViewGroup) null);
        ((TextView) viewInflate.findViewById(R.id.message)).setText(charSequence);
        onScreenHint.mNextView = viewInflate;
        return onScreenHint;
    }

    public void setText(CharSequence charSequence) {
        Log.m5d("OnScreenHint", "setText(" + charSequence + ")");
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
    }

    public void showToast() {
        if (this.mNextView == null) {
            throw new RuntimeException("View is not initialized");
        }
        this.mHandler.removeCallbacks(this.mShow);
        this.mHandler.removeCallbacks(this.mHide);
        this.mHandler.post(this.mShow);
        this.mHandler.postDelayed(this.mHide, 5000L);
    }

    public void showToastForShort() {
        if (this.mNextView == null) {
            throw new RuntimeException("View is not initialized");
        }
        this.mHandler.removeCallbacks(this.mShow);
        this.mHandler.removeCallbacks(this.mHide);
        this.mHandler.post(this.mShow);
        this.mHandler.postDelayed(this.mHide, 3000L);
    }
}
