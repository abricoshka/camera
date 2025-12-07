package com.android.camera.p001ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

/* loaded from: classes.dex */
public class ShutterButton extends RotateImageView implements View.OnLongClickListener {
    private OnShutterButtonListener mListener;
    private boolean mLongPressed;
    private boolean mOldPressed;

    public interface OnShutterButtonListener {
        void onShutterButtonClick(ShutterButton shutterButton);

        void onShutterButtonFocus(ShutterButton shutterButton, boolean z);

        void onShutterButtonLongPressed(ShutterButton shutterButton);
    }

    public ShutterButton(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setOnLongClickListener(this);
    }

    public void setOnShutterButtonListener(OnShutterButtonListener onShutterButtonListener) {
        this.mListener = onShutterButtonListener;
    }

    @Override // android.widget.ImageView, android.view.View
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        final boolean zIsPressed = isPressed();
        if (zIsPressed != this.mOldPressed) {
            if (!zIsPressed) {
                post(new Runnable() { // from class: com.android.camera.ui.ShutterButton.1
                    @Override // java.lang.Runnable
                    public void run() {
                        ShutterButton.this.callShutterButtonFocus(zIsPressed);
                    }
                });
            } else {
                callShutterButtonFocus(zIsPressed);
            }
            this.mOldPressed = zIsPressed;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void callShutterButtonFocus(boolean z) {
        if (this.mListener != null && isEnabled() && isClickable()) {
            this.mListener.onShutterButtonFocus(this, z);
        }
        this.mLongPressed = false;
    }

    @Override // android.view.View
    public boolean performClick() {
        boolean zPerformClick = super.performClick();
        if (this.mListener != null && isEnabled() && isClickable() && (!this.mLongPressed)) {
            this.mListener.onShutterButtonClick(this);
        }
        return zPerformClick;
    }

    @Override // android.view.View.OnLongClickListener
    public boolean onLongClick(View view) {
        if (this.mListener != null && isEnabled() && isClickable()) {
            this.mListener.onShutterButtonLongPressed(this);
            this.mLongPressed = true;
            return false;
        }
        return false;
    }
}
