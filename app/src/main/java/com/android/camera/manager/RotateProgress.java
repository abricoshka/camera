package com.android.camera.manager;

import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.android.camera.CameraActivity;
import com.android.camera.Log;
import com.mediatek.camera.R;

/* loaded from: classes.dex */
public class RotateProgress extends ViewManager {
    private String mMessage;
    private ProgressBar mRotateDialogSpinner;
    private TextView mRotateDialogText;

    public RotateProgress(CameraActivity cameraActivity) {
        super(cameraActivity, 4);
    }

    @Override // com.android.camera.manager.ViewManager
    protected View getView() {
        View viewInflate = getContext().inflate(R.layout.rotate_progress, getViewLayer());
        this.mRotateDialogSpinner = (ProgressBar) viewInflate.findViewById(R.id.rotate_dialog_spinner);
        this.mRotateDialogText = (TextView) viewInflate.findViewById(R.id.rotate_dialog_text);
        return viewInflate;
    }

    @Override // com.android.camera.manager.ViewManager
    protected void onRefresh() {
        this.mRotateDialogText.setText(this.mMessage);
        this.mRotateDialogText.setVisibility(0);
        this.mRotateDialogSpinner.setVisibility(0);
        Log.m5d("RotateProgress", "onRefresh() mMessage=" + this.mMessage);
    }

    public void showProgress(String str) {
        this.mMessage = str;
        show();
        Log.m5d("RotateProgress", "showProgress(" + str + ")");
    }
}
