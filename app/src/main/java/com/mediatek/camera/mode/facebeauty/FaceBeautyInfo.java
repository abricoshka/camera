package com.mediatek.camera.mode.facebeauty;

import android.content.Context;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.mediatek.camera.R;
import com.mediatek.camera.platform.IModuleCtrl;
import com.mediatek.camera.util.Log;
import com.mediatek.camera.util.Util;

/* loaded from: classes.dex */
public class FaceBeautyInfo {
    private float mDensity;
    private int mHeightPixel;
    private IModuleCtrl mIModuleCtrl;
    private RelativeLayout mLayout;
    private TextView mText;
    private View mView;
    private final WindowManager mWM;
    private int mWidthPixel;
    private final WindowManager.LayoutParams mParams = new WindowManager.LayoutParams();
    private final Handler mHandler = new Handler();
    private int mOrientation = -1;
    private int targetId = -1;
    private int mDefaultIconNumber = -1;
    private final Runnable mShow = new Runnable() { // from class: com.mediatek.camera.mode.facebeauty.FaceBeautyInfo.1
        @Override // java.lang.Runnable
        public void run() {
            FaceBeautyInfo.this.handleShow();
        }
    };
    private final Runnable mHide = new Runnable() { // from class: com.mediatek.camera.mode.facebeauty.FaceBeautyInfo.2
        @Override // java.lang.Runnable
        public void run() {
            FaceBeautyInfo.this.handleHide();
        }
    };

    public FaceBeautyInfo(Context context, IModuleCtrl iModuleCtrl) {
        this.mWidthPixel = -1;
        this.mHeightPixel = -1;
        this.mDensity = -1.0f;
        this.mWM = (WindowManager) context.getSystemService("window");
        this.mParams.height = -1;
        this.mParams.width = -1;
        this.mParams.flags = 524312;
        this.mParams.format = -3;
        this.mParams.type = 1000;
        this.mParams.setTitle("OnScreenHint");
        this.mIModuleCtrl = iModuleCtrl;
        new DisplayMetrics();
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        this.mDensity = displayMetrics.density;
        this.mWidthPixel = displayMetrics.widthPixels;
        this.mHeightPixel = displayMetrics.heightPixels;
        this.mView = ((LayoutInflater) context.getSystemService("layout_inflater")).inflate(R.layout.onscreen_effect_mode_toast, (ViewGroup) null);
        this.mText = (TextView) this.mView.findViewById(R.id.message);
        this.mLayout = (RelativeLayout) this.mView.findViewById(R.id.onscreen_toast_layout);
    }

    public void setText(CharSequence charSequence) {
        if (this.mText == null) {
            throw new RuntimeException("This FaceBeautyInfo was null,please check");
        }
        this.mText.setText(charSequence);
    }

    public void cancel() {
        this.mHandler.post(this.mHide);
    }

    public void showToast() {
        this.mHandler.removeCallbacks(this.mShow);
        this.mHandler.removeCallbacks(this.mHide);
        this.mHandler.post(this.mShow);
        this.mHandler.postDelayed(this.mHide, 3000L);
    }

    public void setTargetId(int i, int i2) {
        this.targetId = i;
        this.mDefaultIconNumber = i2;
    }

    public void onOrientationChanged(int i) {
        Log.m34i("FaceBeautyInfo", "[onOrientationChanged] orientation = " + i + ",mOrientation = " + this.mOrientation);
        if (this.mOrientation != i) {
            this.mOrientation = i;
            Util.setOrientation(this.mView, this.mOrientation, true);
        }
        setInfoView();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public synchronized void handleShow() {
        if (this.mView != null) {
            handleHide();
            setInfoView();
            try {
                if (this.mView.getParent() != null) {
                    this.mWM.removeView(this.mView);
                }
                Util.setOrientation(this.mView, this.mIModuleCtrl.getOrientationCompensation(), true);
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
        }
    }

    private int calcualateStaticMargin() {
        float f = this.mDensity * 52.0f;
        Log.m31d("FaceBeautyInfo", "calcualateStaticMargin,value =  " + f);
        return (int) f;
    }

    private int calcualteDefualtMargin() {
        float fMax = Math.max(this.mWidthPixel, this.mHeightPixel) - ((((this.mDefaultIconNumber * 52) + 180) + 92) * this.mDensity);
        Log.m31d("FaceBeautyInfo", "[calcualteDefualtMargin] defaultMargin = " + fMax + ",mWidthPixel = " + this.mWidthPixel + ",mHeightPixel = " + this.mHeightPixel + ",mDensity = " + this.mDensity);
        return (int) fMax;
    }

    private void setInfoView() {
        if (this.mView != null) {
            if (isLandcape()) {
                setLandScapeView();
            } else {
                setPortraitView();
            }
            this.mView.requestLayout();
        }
    }

    private boolean isLandcape() {
        boolean z = true;
        int orientation = this.mIModuleCtrl.getOrientation();
        if (orientation != 90 && orientation != 270) {
            z = false;
        }
        Log.m31d("FaceBeautyInfo", "[isLandcape] orientation=" + orientation + ", land = " + z);
        return z;
    }

    private void setLandScapeView() {
        ViewGroup.LayoutParams layoutParams = this.mLayout.getLayoutParams();
        layoutParams.height = -1;
        layoutParams.width = -2;
        this.mLayout.setLayoutParams(layoutParams);
        RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(-2, -2);
        layoutParams2.addRule(12);
        layoutParams2.addRule(9);
        int iCalcualteDefualtMargin = calcualteDefualtMargin() + ((int) (this.targetId * 52 * this.mDensity));
        layoutParams2.leftMargin = iCalcualteDefualtMargin >= 0 ? iCalcualteDefualtMargin : 0;
        layoutParams2.bottomMargin = calcualateStaticMargin();
        this.mText.setLayoutParams(layoutParams2);
    }

    private void setPortraitView() {
        ViewGroup.LayoutParams layoutParams = this.mLayout.getLayoutParams();
        layoutParams.width = -1;
        layoutParams.height = -2;
        this.mLayout.setLayoutParams(layoutParams);
        RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(-2, -2);
        layoutParams2.addRule(10);
        layoutParams2.addRule(9);
        layoutParams2.leftMargin = calcualateStaticMargin();
        layoutParams2.topMargin = calcualteDefualtMargin() + ((int) (this.targetId * 52 * this.mDensity));
        this.mText.setLayoutParams(layoutParams2);
    }
}
