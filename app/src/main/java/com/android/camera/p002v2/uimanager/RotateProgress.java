package com.android.camera.p002v2.uimanager;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.mediatek.camera.R;
import com.mediatek.camera.debug.LogHelper;

/* loaded from: classes.dex */
public class RotateProgress extends AbstractUiManager {
    private static final LogHelper.Tag TAG = new LogHelper.Tag(RotateProgress.class.getSimpleName());
    private String mMessage;
    private ProgressBar mRotateDialogSpinner;
    private TextView mRotateDialogText;

    public RotateProgress(Activity activity, ViewGroup viewGroup) {
        super(activity, viewGroup);
    }

    @Override // com.android.camera.p002v2.uimanager.AbstractUiManager
    protected View getView() {
        View viewInflate = inflate(R.layout.rotate_progress_v2);
        this.mRotateDialogSpinner = (ProgressBar) viewInflate.findViewById(R.id.rotate_dialog_spinner);
        this.mRotateDialogText = (TextView) viewInflate.findViewById(R.id.rotate_dialog_text);
        return viewInflate;
    }

    @Override // com.android.camera.p002v2.uimanager.AbstractUiManager
    protected void onRefresh() {
        this.mRotateDialogText.setText(this.mMessage);
        this.mRotateDialogText.setVisibility(0);
        this.mRotateDialogSpinner.setVisibility(0);
        LogHelper.m23d(TAG, "onRefresh() mMessage=" + this.mMessage);
    }

    public void showProgress(String str) {
        this.mMessage = str;
        show();
        LogHelper.m23d(TAG, "showProgress(" + str + ")");
    }

    public boolean onBackPressed() {
        if (isShowing()) {
            hide();
            return true;
        }
        return false;
    }
}
