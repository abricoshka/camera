package com.android.camera.manager;

import android.view.View;
import com.android.camera.CameraActivity;
import com.android.camera.p001ui.RotateImageView;
import com.mediatek.camera.R;
import com.mediatek.camera.util.Log;

/* loaded from: classes.dex */
public class EffectViewManager extends ViewManager implements View.OnClickListener {
    private RotateImageView mIndicator;
    private EffectListener mListener;

    public interface EffectListener {
        boolean onClick();
    }

    public EffectViewManager(CameraActivity cameraActivity, EffectListener effectListener) {
        super(cameraActivity);
        this.mListener = effectListener;
    }

    @Override // com.android.camera.manager.ViewManager
    public View getView() {
        View viewInflate = inflate(R.layout.lomo_effect_indicator);
        this.mIndicator = (RotateImageView) viewInflate.findViewById(R.id.lomo_effect_indicator);
        this.mIndicator.setOnClickListener(this);
        Log.m34i("EffectViewManager", "[getView], view:" + viewInflate);
        return viewInflate;
    }

    @Override // com.android.camera.manager.ViewManager, com.android.camera.CameraActivity.OnOrientationListener
    public void onOrientationChanged(int i) {
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        Log.m34i("EffectViewManager", "onClick()");
        if (view == this.mIndicator && getContext().getCameraAppUI().isNormalViewState()) {
            this.mListener.onClick();
            hide();
        }
    }

    public void hideTest() {
        Log.m31d("vag321", "hideTest");
        if (this.mIndicator != null) {
            this.mIndicator.setVisibility(8);
        }
    }

    public void showTest() {
        RotateImageView rotateImageView = this.mIndicator;
    }
}
