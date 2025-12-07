package com.android.camera.p002v2.p003ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import com.mediatek.camera.debug.LogHelper;

/* loaded from: classes.dex */
public class ShutterButton extends RotateImageView implements View.OnLongClickListener {
    private static final LogHelper.Tag TAG = new LogHelper.Tag(ShutterButton.class.getSimpleName());
    private Shutteristener mListener;
    private boolean mLongPressed;
    private boolean mOldPressed;

    public interface Shutteristener {
        void onShutterButtonClick();

        void onShutterButtonFocus(boolean z);

        void onShutterButtonLongPressed();
    }

    public ShutterButton(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setOnLongClickListener(this);
    }

    @Override // android.widget.ImageView, android.view.View
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        final boolean zIsPressed = isPressed();
        LogHelper.m26i(TAG, "drawableStateChanged() pressed = " + zIsPressed);
        if (zIsPressed != this.mOldPressed) {
            if (!zIsPressed) {
                post(new Runnable() { // from class: com.android.camera.v2.ui.ShutterButton.1
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

    @Override // android.view.View
    public boolean performClick() {
        boolean zPerformClick = super.performClick();
        if (this.mListener != null && isEnabled() && isClickable() && (!this.mLongPressed)) {
            this.mListener.onShutterButtonClick();
        }
        return zPerformClick;
    }

    @Override // android.view.View.OnLongClickListener
    public boolean onLongClick(View view) {
        if (this.mListener != null && isEnabled() && isClickable()) {
            this.mListener.onShutterButtonLongPressed();
            this.mLongPressed = true;
            return false;
        }
        return false;
    }

    public void setShutterListener(Shutteristener shutteristener) {
        this.mListener = shutteristener;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void callShutterButtonFocus(boolean z) {
        if (this.mListener != null && isEnabled() && isClickable()) {
            this.mListener.onShutterButtonFocus(z);
        }
        this.mLongPressed = false;
    }
}
