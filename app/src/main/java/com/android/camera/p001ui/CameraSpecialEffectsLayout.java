package com.android.camera.p001ui;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.camera.CameraActivity;
import com.mediatek.camera.ISettingCtrl;
import com.mediatek.camera.R;

/* loaded from: classes.dex */
public class CameraSpecialEffectsLayout extends LinearLayout implements View.OnClickListener {
    private FrameLayout mAppRoot;
    private CameraActivity mCameraActivity;
    private EntrySpecialEffectsLayout mEntrySpecialEffectsLayout;
    View mHideSpecialeffectslayout;
    private ISettingCtrl mISettingCtrl;
    private ZZZFrameLayout mZZZFrameLayout;
    TextView tvAspect;
    TextView tvExposure;
    TextView tvFlash;
    TextView tvLive;
    TextView tvStyles;
    TextView tvTimer;

    public CameraSpecialEffectsLayout(Context context) {
        super(context);
        this.mCameraActivity = (CameraActivity) context;
    }

    public CameraSpecialEffectsLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mCameraActivity = (CameraActivity) context;
    }

    public CameraSpecialEffectsLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mCameraActivity = (CameraActivity) context;
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.tvFlash = (TextView) findViewById(R.id.tv_flash);
        this.tvLive = (TextView) findViewById(R.id.tv_live);
        this.tvTimer = (TextView) findViewById(R.id.tv_timer);
        this.tvExposure = (TextView) findViewById(R.id.tv_exposure);
        this.tvStyles = (TextView) findViewById(R.id.tv_styles);
        this.tvAspect = (TextView) findViewById(R.id.tv_aspect);
        this.mHideSpecialeffectslayout = findViewById(R.id.hide_specialeffectslayout);
        this.tvFlash.setOnClickListener(this);
        this.tvLive.setOnClickListener(this);
        this.tvTimer.setOnClickListener(this);
        this.tvExposure.setOnClickListener(this);
        this.tvStyles.setOnClickListener(this);
        this.tvAspect.setOnClickListener(this);
        this.tvAspect.setOnClickListener(this);
        this.mHideSpecialeffectslayout.setOnClickListener(this);
    }

    public void setSettingCtrl(ISettingCtrl iSettingCtrl) {
        this.mISettingCtrl = iSettingCtrl;
    }

    public void setEntrySpecialEffectsLayout(EntrySpecialEffectsLayout entrySpecialEffectsLayout, FrameLayout frameLayout, ZZZFrameLayout zZZFrameLayout) {
        this.mEntrySpecialEffectsLayout = entrySpecialEffectsLayout;
        this.mZZZFrameLayout = zZZFrameLayout;
        this.mAppRoot = frameLayout;
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        if (view.getId() == R.id.tv_flash) {
            if (this.mCameraActivity.mCameraDeviceCtrl.getCameraId() == 0) {
                if ("auto".equals(this.mISettingCtrl.getSettingValue("pref_camera_flashmode_key"))) {
                    this.mISettingCtrl.onSettingChanged("pref_camera_flashmode_key", "off");
                    this.mCameraActivity.updateFlash("off");
                    this.tvFlash.setCompoundDrawablesRelativeWithIntrinsicBounds((Drawable) null, this.mCameraActivity.getResources().getDrawable(R.drawable.zzz_new_camera_flash_off), (Drawable) null, (Drawable) null);
                    return;
                } else if ("off".equals(this.mISettingCtrl.getSettingValue("pref_camera_flashmode_key"))) {
                    this.mISettingCtrl.onSettingChanged("pref_camera_flashmode_key", "on");
                    this.mCameraActivity.updateFlash("on");
                    this.tvFlash.setCompoundDrawablesRelativeWithIntrinsicBounds((Drawable) null, this.mCameraActivity.getResources().getDrawable(R.drawable.zzz_new_camera_flash_on), (Drawable) null, (Drawable) null);
                    return;
                } else {
                    if ("on".equals(this.mISettingCtrl.getSettingValue("pref_camera_flashmode_key"))) {
                        this.mISettingCtrl.onSettingChanged("pref_camera_flashmode_key", "auto");
                        this.mCameraActivity.updateFlash("auto");
                        this.tvFlash.setCompoundDrawablesRelativeWithIntrinsicBounds((Drawable) null, this.mCameraActivity.getResources().getDrawable(R.drawable.zzz_new_camera_flash_auto), (Drawable) null, (Drawable) null);
                        return;
                    }
                    return;
                }
            }
            return;
        }
        if (view.getId() == R.id.tv_live) {
            if ("auto".equals(this.mISettingCtrl.getSettingValue("pref_live_focus_key"))) {
                this.mISettingCtrl.onSettingChanged("pref_live_focus_key", "off");
                this.tvLive.setCompoundDrawablesRelativeWithIntrinsicBounds((Drawable) null, this.mCameraActivity.getResources().getDrawable(R.drawable.zzz_new_camera_liver_2), (Drawable) null, (Drawable) null);
                return;
            } else if ("off".equals(this.mISettingCtrl.getSettingValue("pref_live_focus_key"))) {
                this.mISettingCtrl.onSettingChanged("pref_live_focus_key", "on");
                this.tvLive.setCompoundDrawablesRelativeWithIntrinsicBounds((Drawable) null, this.mCameraActivity.getResources().getDrawable(R.drawable.zzz_new_camera_liver_1), (Drawable) null, (Drawable) null);
                return;
            } else {
                if ("on".equals(this.mISettingCtrl.getSettingValue("pref_live_focus_key"))) {
                    this.mISettingCtrl.onSettingChanged("pref_live_focus_key", "auto");
                    this.tvLive.setCompoundDrawablesRelativeWithIntrinsicBounds((Drawable) null, this.mCameraActivity.getResources().getDrawable(R.drawable.zzz_new_camera_liver_0), (Drawable) null, (Drawable) null);
                    return;
                }
                return;
            }
        }
        if (view.getId() == R.id.tv_timer) {
            if ("0".equals(this.mISettingCtrl.getSettingValue("pref_camera_self_timer_key"))) {
                this.mISettingCtrl.onSettingChanged("pref_camera_self_timer_key", "3000");
                this.tvTimer.setCompoundDrawablesRelativeWithIntrinsicBounds((Drawable) null, this.mCameraActivity.getResources().getDrawable(R.drawable.zzz_new_camera_timer_3), (Drawable) null, (Drawable) null);
                this.mCameraActivity.mCameraDeviceCtrl.applyParameters();
                return;
            }
            if ("3000".equals(this.mISettingCtrl.getSettingValue("pref_camera_self_timer_key"))) {
                this.mISettingCtrl.onSettingChanged("pref_camera_self_timer_key", "5000");
                this.tvTimer.setCompoundDrawablesRelativeWithIntrinsicBounds((Drawable) null, this.mCameraActivity.getResources().getDrawable(R.drawable.zzz_new_camera_timer_5), (Drawable) null, (Drawable) null);
                this.mCameraActivity.mCameraDeviceCtrl.applyParameters();
                return;
            } else if ("5000".equals(this.mISettingCtrl.getSettingValue("pref_camera_self_timer_key"))) {
                this.mISettingCtrl.onSettingChanged("pref_camera_self_timer_key", "10000");
                this.tvTimer.setCompoundDrawablesRelativeWithIntrinsicBounds((Drawable) null, this.mCameraActivity.getResources().getDrawable(R.drawable.zzz_new_camera_timer_10), (Drawable) null, (Drawable) null);
                this.mCameraActivity.mCameraDeviceCtrl.applyParameters();
                return;
            } else {
                if ("10000".equals(this.mISettingCtrl.getSettingValue("pref_camera_self_timer_key"))) {
                    this.mISettingCtrl.onSettingChanged("pref_camera_self_timer_key", "0");
                    this.tvTimer.setCompoundDrawablesRelativeWithIntrinsicBounds((Drawable) null, this.mCameraActivity.getResources().getDrawable(R.drawable.zzz_new_camera_timer_normal), (Drawable) null, (Drawable) null);
                    this.mCameraActivity.mCameraDeviceCtrl.applyParameters();
                    return;
                }
                return;
            }
        }
        if (view.getId() != R.id.tv_exposure && view.getId() != R.id.tv_styles) {
            if (view.getId() == R.id.tv_aspect) {
                if ("1.3333".equals(this.mISettingCtrl.getSettingValue("pref_camera_picturesize_ratio_key"))) {
                    this.mISettingCtrl.onSettingChanged("pref_camera_picturesize_ratio_key", "1.7778");
                    this.tvAspect.setCompoundDrawablesRelativeWithIntrinsicBounds((Drawable) null, this.mCameraActivity.getResources().getDrawable(R.drawable.zzz_new_camera_aspect_full), (Drawable) null, (Drawable) null);
                } else if ("1.7778".equals(this.mISettingCtrl.getSettingValue("pref_camera_picturesize_ratio_key"))) {
                    this.mISettingCtrl.onSettingChanged("pref_camera_picturesize_ratio_key", "1.3333");
                    this.tvAspect.setCompoundDrawablesRelativeWithIntrinsicBounds((Drawable) null, this.mCameraActivity.getResources().getDrawable(R.drawable.zzz_new_camera_aspect_normal), (Drawable) null, (Drawable) null);
                }
                this.mCameraActivity.setPreViewFullShow();
                return;
            }
            if (view.getId() == R.id.hide_specialeffectslayout) {
                setVisibility(8);
                this.mEntrySpecialEffectsLayout.setVisibility(0);
                this.mZZZFrameLayout.setVisibility(0);
                this.mAppRoot.setVisibility(0);
            }
        }
    }
}
