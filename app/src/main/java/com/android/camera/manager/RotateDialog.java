package com.android.camera.manager;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import com.android.camera.CameraActivity;
import com.android.camera.Log;
import com.android.camera.SettingUtils;
import com.android.camera.p001ui.RotateLayout;
import com.mediatek.camera.R;

/* loaded from: classes.dex */
public class RotateDialog extends ViewManager {
    private String mButton1;
    private String mButton2;
    private Animation mDialogFadeIn;
    private Animation mDialogFadeOut;
    private String mMessage;
    private RotateLayout mRotateDialog;
    private TextView mRotateDialogButton1;
    private TextView mRotateDialogButton2;
    private View mRotateDialogButtonLayout;
    private TextView mRotateDialogText;
    private TextView mRotateDialogTitle;
    private View mRotateDialogTitleDivider;
    private View mRotateDialogTitleLayout;
    private Runnable mRunnable1;
    private Runnable mRunnable2;
    private String mTitle;

    public RotateDialog(CameraActivity cameraActivity) {
        super(cameraActivity, 4);
    }

    @Override // com.android.camera.manager.ViewManager
    protected View getView() {
        View viewInflate = getContext().inflate(R.layout.rotate_dialog, getViewLayer());
        this.mRotateDialog = (RotateLayout) viewInflate.findViewById(R.id.rotate_dialog_layout);
        this.mRotateDialogTitleLayout = viewInflate.findViewById(R.id.rotate_dialog_title_layout);
        this.mRotateDialogButtonLayout = viewInflate.findViewById(R.id.rotate_dialog_button_layout);
        this.mRotateDialogTitle = (TextView) viewInflate.findViewById(R.id.rotate_dialog_title);
        this.mRotateDialogText = (TextView) viewInflate.findViewById(R.id.rotate_dialog_text);
        this.mRotateDialogButton1 = (Button) viewInflate.findViewById(R.id.rotate_dialog_button1);
        this.mRotateDialogButton2 = (Button) viewInflate.findViewById(R.id.rotate_dialog_button2);
        this.mRotateDialogTitleDivider = viewInflate.findViewById(R.id.rotate_dialog_title_divider);
        return viewInflate;
    }

    private void resetRotateDialog() {
        if (this.mRotateDialogTitleLayout != null) {
            this.mRotateDialogTitleLayout.setVisibility(8);
        }
        if (this.mRotateDialogButton1 != null) {
            this.mRotateDialogButton1.setVisibility(8);
        }
        if (this.mRotateDialogButton2 != null) {
            this.mRotateDialogButton2.setVisibility(8);
        }
        if (this.mRotateDialogButtonLayout != null) {
            this.mRotateDialogButtonLayout.setVisibility(8);
        }
    }

    private void resetValues() {
        this.mTitle = null;
        this.mMessage = null;
        this.mButton1 = null;
        this.mButton2 = null;
        this.mRunnable1 = null;
        this.mRunnable2 = null;
    }

    @Override // com.android.camera.manager.ViewManager
    protected void onRefresh() {
        resetRotateDialog();
        if (this.mTitle != null && this.mRotateDialogTitle != null) {
            this.mRotateDialogTitle.setTextColor(SettingUtils.getMainColor(getContext()));
            this.mRotateDialogTitle.setText(this.mTitle);
            if (this.mRotateDialogTitleLayout != null) {
                this.mRotateDialogTitleLayout.setVisibility(0);
            }
            if (this.mRotateDialogTitleDivider != null) {
                this.mRotateDialogTitleDivider.setBackgroundColor(SettingUtils.getMainColor(getContext()));
            }
        }
        if (this.mRotateDialogText != null) {
            this.mRotateDialogText.setText(this.mMessage);
        }
        if (this.mButton1 != null) {
            this.mRotateDialogButton1.setText(this.mButton1);
            this.mRotateDialogButton1.setContentDescription(this.mButton1);
            this.mRotateDialogButton1.setVisibility(0);
            this.mRotateDialogButton1.setEnabled(true);
            this.mRotateDialogButton1.setOnClickListener(new View.OnClickListener() { // from class: com.android.camera.manager.RotateDialog.1
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    RotateDialog.this.mRotateDialogButton1.setEnabled(false);
                    RotateDialog.this.hide();
                    if (RotateDialog.this.mRunnable1 != null) {
                        RotateDialog.this.mRunnable1.run();
                    }
                }
            });
            this.mRotateDialogButtonLayout.setVisibility(0);
        }
        if (this.mButton2 != null) {
            this.mRotateDialogButton2.setText(this.mButton2);
            this.mRotateDialogButton2.setContentDescription(this.mButton2);
            this.mRotateDialogButton2.setVisibility(0);
            this.mRotateDialogButton2.setEnabled(true);
            this.mRotateDialogButton2.setOnClickListener(new View.OnClickListener() { // from class: com.android.camera.manager.RotateDialog.2
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    RotateDialog.this.mRotateDialogButton2.setEnabled(false);
                    if (RotateDialog.this.mRunnable2 != null) {
                        RotateDialog.this.mRunnable2.run();
                    }
                    RotateDialog.this.hide();
                }
            });
            this.mRotateDialogButtonLayout.setVisibility(0);
        }
        Log.m5d("RotateDialog", "onRefresh() mTitle=" + this.mTitle + ", mMessage=" + this.mMessage + ", mButton1=" + this.mButton1 + ", mButton2=" + this.mButton2 + ", mRunnable1=" + this.mRunnable1 + ", mRunnable2=" + this.mRunnable2);
    }

    public void showAlertDialog(String str, String str2, String str3, Runnable runnable, String str4, Runnable runnable2) {
        resetValues();
        this.mTitle = str;
        this.mMessage = str2;
        this.mButton1 = str3;
        this.mButton2 = str4;
        this.mRunnable1 = runnable;
        this.mRunnable2 = runnable2;
        show();
    }

    @Override // com.android.camera.manager.ViewManager
    public boolean collapse(boolean z) {
        if (isShowing()) {
            Log.m5d("RotateDialog", "[collapse] mRunnable1:" + this.mRunnable1);
            hide();
            if (this.mRunnable1 != null) {
                this.mRunnable1.run();
                return true;
            }
            return true;
        }
        return super.collapse(z);
    }

    @Override // com.android.camera.manager.ViewManager
    protected Animation getFadeInAnimation() {
        return AnimationUtils.loadAnimation(getContext(), R.anim.setting_popup_grow_fade_in);
    }

    @Override // com.android.camera.manager.ViewManager
    protected Animation getFadeOutAnimation() {
        return AnimationUtils.loadAnimation(getContext(), R.anim.setting_popup_shrink_fade_out);
    }

    @Override // com.android.camera.manager.ViewManager
    protected void fadeIn() {
        if (getShowAnimationEnabled()) {
            if (this.mDialogFadeIn == null) {
                this.mDialogFadeIn = getFadeInAnimation();
            }
            if (this.mDialogFadeIn != null && this.mRotateDialog != null) {
                this.mRotateDialog.startAnimation(this.mDialogFadeIn);
            }
        }
    }

    @Override // com.android.camera.manager.ViewManager
    protected void fadeOut() {
        if (getHideAnimationEnabled()) {
            if (this.mDialogFadeOut == null) {
                this.mDialogFadeOut = getFadeOutAnimation();
            }
            if (this.mDialogFadeOut != null && this.mRotateDialog != null) {
                this.mRotateDialog.startAnimation(this.mDialogFadeOut);
            }
        }
    }
}
