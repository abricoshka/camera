package com.android.camera.p001ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.android.camera.CameraActivity;
import com.mediatek.camera.ISettingCtrl;
import com.mediatek.camera.R;

/* loaded from: classes.dex */
public class EntrySpecialEffectsLayout extends LinearLayout implements View.OnClickListener {
    private FrameLayout mAppRoot;
    private CameraActivity mCameraActivity;
    private CameraSpecialEffectsLayout mCameraSpecialEffectsLayout;
    private ISettingCtrl mISettingCtrl;
    private ZZZFrameLayout mZZZFrameLayout;
    ImageView tvEntryEf;
    ImageView tvSelectFlash;

    public EntrySpecialEffectsLayout(Context context) {
        super(context);
        this.mCameraActivity = (CameraActivity) context;
    }

    public EntrySpecialEffectsLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mCameraActivity = (CameraActivity) context;
    }

    public EntrySpecialEffectsLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mCameraActivity = (CameraActivity) context;
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.tvSelectFlash = (ImageView) findViewById(R.id.iv_selectflash);
        this.tvEntryEf = (ImageView) findViewById(R.id.iv_entry_ef);
        this.tvSelectFlash.setOnClickListener(this);
        this.tvEntryEf.setOnClickListener(this);
    }

    public void setSettingCtrl(ISettingCtrl iSettingCtrl) {
        this.mISettingCtrl = iSettingCtrl;
    }

    public void setCameraSpecialEffectsLayout(CameraSpecialEffectsLayout cameraSpecialEffectsLayout, FrameLayout frameLayout, ZZZFrameLayout zZZFrameLayout) {
        this.mCameraSpecialEffectsLayout = cameraSpecialEffectsLayout;
        this.mZZZFrameLayout = zZZFrameLayout;
        this.mAppRoot = frameLayout;
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        if (view.getId() == R.id.iv_selectflash) {
            if (this.mCameraActivity.mCameraDeviceCtrl.getCameraId() == 0) {
                if ("auto".equals(this.mISettingCtrl.getSettingValue("pref_camera_flashmode_key"))) {
                    this.mISettingCtrl.onSettingChanged("pref_camera_flashmode_key", "off");
                    this.mCameraActivity.updateFlash("off");
                    this.tvSelectFlash.setImageResource(R.drawable.zzz_camera_entry_ef_flash_off);
                    return;
                } else {
                    this.mISettingCtrl.onSettingChanged("pref_camera_flashmode_key", "auto");
                    this.mCameraActivity.updateFlash("auto");
                    this.tvSelectFlash.setImageResource(R.drawable.zzz_camera_entry_ef_flash_auto);
                    return;
                }
            }
            return;
        }
        if (view.getId() == R.id.iv_entry_ef) {
            this.mCameraSpecialEffectsLayout.setVisibility(0);
            setVisibility(8);
            this.mZZZFrameLayout.setVisibility(8);
            this.mAppRoot.setVisibility(8);
        }
    }
}
