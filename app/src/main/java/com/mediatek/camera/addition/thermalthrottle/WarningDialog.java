package com.mediatek.camera.addition.thermalthrottle;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.mediatek.camera.R;
import com.mediatek.camera.p004ui.CameraView;
import com.mediatek.camera.util.Log;

/* loaded from: classes.dex */
public class WarningDialog extends CameraView {
    private String mButton;
    private String mMessage;
    private Runnable mRunnable;
    private String mTitle;
    private View mWarningDialogButtonLayout;
    private ImageView mWarningDialogImageView;
    private TextView mWarningDialogText;
    private TextView mWarningDialogTime;
    private TextView mWarningDialogTitle;
    private View mWarningDialogTitleDivider;
    private View mWarningDialogTitleLayout;
    private TextView mWarningDialogtitleName;
    private TextView mWarningDlgButton;

    public WarningDialog(Activity activity) {
        super(activity);
    }

    @Override // com.mediatek.camera.p004ui.CameraView
    protected View getView() {
        View viewInflate = inflate(R.layout.warning_dialog);
        this.mWarningDialogTitleLayout = viewInflate.findViewById(R.id.alert_dialog_title_layout);
        this.mWarningDialogButtonLayout = viewInflate.findViewById(R.id.alert_dialog_button_layout);
        this.mWarningDialogTitle = (TextView) viewInflate.findViewById(R.id.alert_dialog_title);
        this.mWarningDialogText = (TextView) viewInflate.findViewById(R.id.alert_dialog_text);
        this.mWarningDlgButton = (Button) viewInflate.findViewById(R.id.alert_dialog_button1);
        this.mWarningDialogTitleDivider = viewInflate.findViewById(R.id.alert_dialog_title_divider);
        this.mWarningDialogImageView = (ImageView) viewInflate.findViewById(R.id.alert_dialog_title_icon);
        this.mWarningDialogTime = (TextView) viewInflate.findViewById(R.id.alert_dialog_time);
        this.mWarningDialogtitleName = (TextView) viewInflate.findViewById(R.id.alert_dialog_title_name);
        return viewInflate;
    }

    @Override // com.mediatek.camera.p004ui.CameraView, com.mediatek.camera.platform.ICameraView
    public void uninit() {
        if (isShowing()) {
            hide();
        } else {
            super.uninit();
        }
    }

    @Override // com.mediatek.camera.p004ui.CameraView, com.mediatek.camera.platform.ICameraView
    public void reset() {
        this.mTitle = null;
        this.mMessage = null;
        this.mButton = null;
        this.mRunnable = null;
    }

    @Override // com.mediatek.camera.p004ui.CameraView, com.mediatek.camera.platform.ICameraView
    public void refresh() {
        resetRotateDialog();
        if (this.mTitle != null && this.mWarningDialogTitle != null) {
            this.mWarningDialogTitle.setTextColor(getMainColor(getContext()));
            this.mWarningDialogTitle.setText(this.mTitle);
            if (this.mWarningDialogTitleLayout != null) {
                this.mWarningDialogTitleLayout.setVisibility(0);
            }
            if (this.mWarningDialogTitleDivider != null) {
                this.mWarningDialogTitleDivider.setBackgroundColor(getMainColor(getContext()));
            }
        }
        if (this.mWarningDialogText != null) {
            this.mWarningDialogText.setText(this.mMessage);
        }
        if (this.mWarningDialogtitleName != null) {
            this.mWarningDialogtitleName.setText(R.string.pref_thermal_dialog_title);
        }
        if (this.mWarningDialogTime != null) {
            this.mWarningDialogTime.setText(R.string.pref_thermal_dialog_timer);
        }
        if (this.mWarningDialogImageView != null) {
            this.mWarningDialogImageView.setImageResource(R.drawable.ic_dialog_alert);
        }
        if (this.mButton != null) {
            this.mWarningDlgButton.setText(this.mButton);
            this.mWarningDlgButton.setContentDescription(this.mButton);
            this.mWarningDlgButton.setVisibility(0);
            this.mWarningDlgButton.setOnClickListener(new View.OnClickListener() { // from class: com.mediatek.camera.addition.thermalthrottle.WarningDialog.1
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    if (WarningDialog.this.mRunnable != null) {
                        WarningDialog.this.mRunnable.run();
                    }
                    WarningDialog.this.hide();
                }
            });
            this.mWarningDialogButtonLayout.setVisibility(0);
        }
        Log.m34i("WarningDialog", "onRefresh() mTitle=" + this.mTitle + ", mMessage=" + this.mMessage + ", mButton=" + this.mButton + ", mRunnable=" + this.mRunnable);
    }

    @Override // com.mediatek.camera.p004ui.CameraView
    protected Animation getFadeInAnimation() {
        return AnimationUtils.loadAnimation(getContext(), R.anim.setting_popup_grow_fade_in);
    }

    @Override // com.mediatek.camera.p004ui.CameraView
    protected Animation getFadeOutAnimation() {
        return AnimationUtils.loadAnimation(getContext(), R.anim.setting_popup_shrink_fade_out);
    }

    public void setCountDownTime(String str) {
        this.mWarningDialogTime.setText(str);
    }

    public void showAlertDialog(String str, String str2, String str3, Runnable runnable) {
        reset();
        this.mTitle = str;
        this.mMessage = str2;
        this.mButton = str3;
        this.mRunnable = runnable;
        super.show();
    }

    private void resetRotateDialog() {
        if (this.mWarningDialogTitleLayout != null) {
            this.mWarningDialogTitleLayout.setVisibility(8);
        }
        this.mWarningDlgButton.setVisibility(8);
        this.mWarningDialogButtonLayout.setVisibility(8);
    }

    private int getMainColor(Context context) {
        return context.getResources().getColor(R.color.setting_item_text_color_highlight);
    }
}
