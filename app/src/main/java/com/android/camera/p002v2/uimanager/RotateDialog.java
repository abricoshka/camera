package com.android.camera.p002v2.uimanager;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import com.android.camera.p002v2.p003ui.RotateLayout;
import com.android.camera.p002v2.util.CameraUtil;
import com.mediatek.camera.R;
import com.mediatek.camera.debug.LogHelper;

/* loaded from: classes.dex */
public class RotateDialog extends AbstractUiManager {
    private static final LogHelper.Tag TAG = new LogHelper.Tag(RotateDialog.class.getSimpleName());
    private Activity mActivity;
    private String mButton1;
    private String mButton2;
    private Animation mDialogFadeIn;
    private Animation mDialogFadeOut;
    private boolean mHideAnimationEnabled;
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
    private boolean mShowAnimationEnabled;
    private String mTitle;

    public RotateDialog(Activity activity, ViewGroup viewGroup) {
        super(activity, viewGroup);
        this.mShowAnimationEnabled = true;
        this.mHideAnimationEnabled = true;
        this.mActivity = activity;
    }

    @Override // com.android.camera.p002v2.uimanager.AbstractUiManager
    protected View getView() {
        View viewInflate = inflate(R.layout.rotate_dialog_v2);
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

    @Override // com.android.camera.p002v2.uimanager.AbstractUiManager
    protected void onRefresh() {
        resetRotateDialog();
        if (this.mTitle != null && this.mRotateDialogTitle != null) {
            this.mRotateDialogTitle.setTextColor(CameraUtil.getMainColor(this.mActivity));
            this.mRotateDialogTitle.setText(this.mTitle);
            if (this.mRotateDialogTitleLayout != null) {
                this.mRotateDialogTitleLayout.setVisibility(0);
            }
            if (this.mRotateDialogTitleDivider != null) {
                this.mRotateDialogTitleDivider.setBackgroundColor(CameraUtil.getMainColor(this.mActivity));
            }
        }
        if (this.mRotateDialogText != null) {
            this.mRotateDialogText.setText(this.mMessage);
        }
        if (this.mButton1 != null) {
            this.mRotateDialogButton1.setText(this.mButton1);
            this.mRotateDialogButton1.setContentDescription(this.mButton1);
            this.mRotateDialogButton1.setVisibility(0);
            this.mRotateDialogButton1.setOnClickListener(new View.OnClickListener() { // from class: com.android.camera.v2.uimanager.RotateDialog.1
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
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
            this.mRotateDialogButton2.setOnClickListener(new View.OnClickListener() { // from class: com.android.camera.v2.uimanager.RotateDialog.2
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    if (RotateDialog.this.mRunnable2 != null) {
                        RotateDialog.this.mRunnable2.run();
                    }
                    RotateDialog.this.hide();
                }
            });
            this.mRotateDialogButtonLayout.setVisibility(0);
        }
        LogHelper.m23d(TAG, "onRefresh() mTitle=" + this.mTitle + ", mMessage=" + this.mMessage + ", mButton1=" + this.mButton1 + ", mButton2=" + this.mButton2 + ", mRunnable1=" + this.mRunnable1 + ", mRunnable2=" + this.mRunnable2);
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

    public boolean onBackPressed() {
        LogHelper.m26i(TAG, "[onBackPressed]...");
        return collapse(false);
    }

    public boolean collapse(boolean z) {
        if (isShowing()) {
            LogHelper.m23d(TAG, "[collapse] mRunnable1:" + this.mRunnable1);
            hide();
            if (this.mRunnable1 != null) {
                this.mRunnable1.run();
                return true;
            }
            return true;
        }
        return false;
    }

    @Override // com.android.camera.p002v2.uimanager.AbstractUiManager
    protected Animation getFadeInAnimation() {
        return AnimationUtils.loadAnimation(this.mActivity, R.anim.setting_popup_grow_fade_in);
    }

    @Override // com.android.camera.p002v2.uimanager.AbstractUiManager
    protected Animation getFadeOutAnimation() {
        return AnimationUtils.loadAnimation(this.mActivity, R.anim.setting_popup_shrink_fade_out);
    }

    @Override // com.android.camera.p002v2.uimanager.AbstractUiManager
    protected void fadeIn() {
        if (this.mShowAnimationEnabled) {
            if (this.mDialogFadeIn == null) {
                this.mDialogFadeIn = getFadeInAnimation();
            }
            if (this.mDialogFadeIn != null && this.mRotateDialog != null) {
                this.mRotateDialog.startAnimation(this.mDialogFadeIn);
            }
        }
    }

    @Override // com.android.camera.p002v2.uimanager.AbstractUiManager
    protected void fadeOut() {
        if (this.mHideAnimationEnabled) {
            if (this.mDialogFadeOut == null) {
                this.mDialogFadeOut = getFadeOutAnimation();
            }
            if (this.mDialogFadeOut != null && this.mRotateDialog != null) {
                this.mRotateDialog.startAnimation(this.mDialogFadeOut);
            }
        }
    }
}
