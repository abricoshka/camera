package com.android.camera.manager;

import android.hardware.Camera;
import android.view.View;
import com.android.camera.CameraActivity;
import com.android.camera.p001ui.RotateImageView;
import com.mediatek.camera.ISettingCtrl;
import com.mediatek.camera.R;

/* loaded from: classes.dex */
public class IdManager extends ViewManager implements View.OnClickListener {
    protected RotateImageView mIndicator;
    protected ISettingCtrl mSettingController;

    public IdManager(CameraActivity cameraActivity) {
        super(cameraActivity);
    }

    @Override // com.android.camera.manager.ViewManager
    public View getView() {
        View viewInflate = inflate(R.layout.tw_setting_indicator);
        this.mIndicator = (RotateImageView) viewInflate.findViewById(R.id.switch_camera);
        this.mIndicator.setVisibility(8);
        this.mIndicator.setOnClickListener(this);
        return viewInflate;
    }

    @Override // com.android.camera.manager.ViewManager
    public void onRefresh() {
        if (this.mIndicator != null) {
            switch (getContext().getCurrentWheelMode()) {
                case 0:
                    this.mIndicator.setVisibility(8);
                    break;
                case 1:
                    this.mIndicator.setVisibility(8);
                    break;
                default:
                    this.mIndicator.setVisibility(8);
                    break;
            }
        }
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        if (view == this.mIndicator && Camera.getNumberOfCameras() > 1) {
            getContext().onCameraPicked(getContext().getCameraId() == 0 ? 1 : 0);
        }
    }

    public void setSettingController(ISettingCtrl iSettingCtrl) {
        this.mSettingController = iSettingCtrl;
    }
}
