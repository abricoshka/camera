package com.android.camera.p001ui;

import android.view.View;
import com.android.camera.CameraActivity;
import com.android.camera.Log;
import com.android.camera.manager.ViewManager;
import com.mediatek.camera.R;

/* loaded from: classes.dex */
public class FaceBeautyEntryView extends ViewManager implements View.OnClickListener {
    private CameraActivity mCameraActivity;
    private RotateImageView mFaceBeautyEntryView;

    public FaceBeautyEntryView(CameraActivity cameraActivity) {
        super(cameraActivity, 1);
        this.mCameraActivity = cameraActivity;
    }

    @Override // com.android.camera.manager.ViewManager
    protected View getView() {
        View viewInflate = inflate(R.layout.facebeauty_entry);
        this.mFaceBeautyEntryView = (RotateImageView) viewInflate.findViewById(R.id.facebeauty_entry_icon);
        this.mFaceBeautyEntryView.setImageResource(R.drawable.ic_mode_facebeauty_normal);
        this.mFaceBeautyEntryView.setOnClickListener(this);
        return viewInflate;
    }

    @Override // com.android.camera.manager.ViewManager
    public void show() {
        super.show();
        if (this.mFaceBeautyEntryView != null) {
            this.mFaceBeautyEntryView.setVisibility(0);
        }
    }

    @Override // com.android.camera.manager.ViewManager
    public void hide() {
        super.hide();
        if (this.mFaceBeautyEntryView != null) {
            this.mFaceBeautyEntryView.setVisibility(8);
        }
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        Log.m5d("FaceBeautyEntryView", "[onClick]will go to VFB Mode");
        this.mCameraActivity.getModePicker().setCurrentMode(2);
    }
}
