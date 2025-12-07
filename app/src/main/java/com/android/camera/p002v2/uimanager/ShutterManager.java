package com.android.camera.p002v2.uimanager;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import com.android.camera.p002v2.p003ui.ShutterButton;
import com.android.camera.p002v2.p003ui.UiUtil;
import com.mediatek.camera.R;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes.dex */
public class ShutterManager extends AbstractUiManager {
    private Activity mActivity;
    private View mCancelButton;
    private View mOkButton;
    private View.OnClickListener mOkCancelClickListener;
    private OnOkCancelButtonClickListener mOnOkCancelButtonClickListener;
    private List<OnShutterButtonListener> mPhotoListeners;
    private ShutterButton mPhotoShutterButton;
    private ShutterButton.Shutteristener mPhotoShutterButtonListener;
    private int mPhotoShutterButtonResId;
    private boolean mPhotoShutterEnabled;
    private int mShutterLayout;
    private List<OnShutterButtonListener> mVideoListeners;
    private ShutterButton mVideoShutterButton;
    private ShutterButton.Shutteristener mVideoShutterButtonListener;
    private int mVideoShutterButtonResId;
    private boolean mVideoShutterEnabled;
    private ViewGroup mViewGroup;

    public interface OnOkCancelButtonClickListener {
        void onCancelClick();

        void onOkClick();
    }

    public interface OnShutterButtonListener {
        void onFocused(boolean z);

        void onLongPressed();

        void onPressed();
    }

    public ShutterManager(Activity activity, ViewGroup viewGroup) {
        super(activity, viewGroup);
        this.mShutterLayout = R.layout.camera_shutter_photo_video_v2;
        this.mPhotoShutterEnabled = true;
        this.mVideoShutterEnabled = true;
        this.mPhotoListeners = new ArrayList();
        this.mVideoListeners = new ArrayList();
        this.mOkCancelClickListener = new OnOkCancelClickListener(this, 0 == true ? 1 : 0);
        setFilterEnable(false);
        this.mActivity = activity;
        this.mViewGroup = viewGroup;
        this.mPhotoShutterButtonListener = new ShutterButton.Shutteristener() { // from class: com.android.camera.v2.uimanager.ShutterManager.1
            @Override // com.android.camera.v2.ui.ShutterButton.Shutteristener
            public void onShutterButtonLongPressed() {
                Iterator it = ShutterManager.this.mPhotoListeners.iterator();
                while (it.hasNext()) {
                    ((OnShutterButtonListener) it.next()).onLongPressed();
                }
            }

            @Override // com.android.camera.v2.ui.ShutterButton.Shutteristener
            public void onShutterButtonFocus(boolean z) {
                Iterator it = ShutterManager.this.mPhotoListeners.iterator();
                while (it.hasNext()) {
                    ((OnShutterButtonListener) it.next()).onFocused(z);
                }
            }

            @Override // com.android.camera.v2.ui.ShutterButton.Shutteristener
            public void onShutterButtonClick() {
                Iterator it = ShutterManager.this.mPhotoListeners.iterator();
                while (it.hasNext()) {
                    ((OnShutterButtonListener) it.next()).onPressed();
                }
            }
        };
        this.mVideoShutterButtonListener = new ShutterButton.Shutteristener() { // from class: com.android.camera.v2.uimanager.ShutterManager.2
            @Override // com.android.camera.v2.ui.ShutterButton.Shutteristener
            public void onShutterButtonLongPressed() {
                Iterator it = ShutterManager.this.mVideoListeners.iterator();
                while (it.hasNext()) {
                    ((OnShutterButtonListener) it.next()).onLongPressed();
                }
            }

            @Override // com.android.camera.v2.ui.ShutterButton.Shutteristener
            public void onShutterButtonFocus(boolean z) {
                Iterator it = ShutterManager.this.mVideoListeners.iterator();
                while (it.hasNext()) {
                    ((OnShutterButtonListener) it.next()).onFocused(z);
                }
            }

            @Override // com.android.camera.v2.ui.ShutterButton.Shutteristener
            public void onShutterButtonClick() {
                Iterator it = ShutterManager.this.mVideoListeners.iterator();
                while (it.hasNext()) {
                    ((OnShutterButtonListener) it.next()).onPressed();
                }
            }
        };
        Intent intent = activity.getIntent();
        String action = intent != null ? intent.getAction() : null;
        if ("android.media.action.IMAGE_CAPTURE".equals(action)) {
            this.mShutterLayout = R.layout.camera_shutter_photo_v2;
        }
        if ("android.media.action.VIDEO_CAPTURE".equals(action)) {
            this.mShutterLayout = R.layout.camera_shutter_video_v2;
        }
    }

    @Override // com.android.camera.p002v2.uimanager.AbstractUiManager
    protected View getView() {
        View viewInflate = inflate(this.mShutterLayout);
        UiUtil.setOrientation(viewInflate, ((Integer) this.mViewGroup.getTag()).intValue(), false);
        this.mPhotoShutterButton = (ShutterButton) viewInflate.findViewById(R.id.shutter_button);
        if (this.mPhotoShutterButton != null) {
            this.mPhotoShutterButton.setShutterListener(this.mPhotoShutterButtonListener);
        }
        this.mVideoShutterButton = (ShutterButton) viewInflate.findViewById(R.id.shutter_button_video);
        if (this.mVideoShutterButton != null) {
            this.mVideoShutterButton.setShutterListener(this.mVideoShutterButtonListener);
        }
        this.mOkButton = viewInflate.findViewById(R.id.done_button);
        if (this.mOkButton != null) {
            this.mOkButton.setOnClickListener(this.mOkCancelClickListener);
        }
        this.mCancelButton = viewInflate.findViewById(R.id.btn_cancel);
        if (this.mCancelButton != null) {
            this.mCancelButton.setOnClickListener(this.mOkCancelClickListener);
        }
        return viewInflate;
    }

    @Override // com.android.camera.p002v2.uimanager.AbstractUiManager
    protected void onRefresh() {
        super.onRefresh();
        if (this.mPhotoShutterButton != null) {
            boolean zIsEnable = this.mPhotoShutterEnabled ? isEnable() : false;
            this.mPhotoShutterButton.setEnabled(zIsEnable);
            this.mPhotoShutterButton.setClickable(zIsEnable);
            if (this.mPhotoShutterButtonResId > 0) {
                this.mPhotoShutterButton.setImageResource(this.mPhotoShutterButtonResId);
            }
        }
        if (this.mVideoShutterButton != null) {
            boolean zIsEnable2 = this.mVideoShutterEnabled ? isEnable() : false;
            this.mVideoShutterButton.setEnabled(zIsEnable2);
            this.mVideoShutterButton.setClickable(zIsEnable2);
            if (this.mVideoShutterButtonResId > 0) {
                this.mVideoShutterButton.setImageResource(this.mVideoShutterButtonResId);
            }
        }
    }

    @Override // com.android.camera.p002v2.uimanager.AbstractUiManager
    public void setEnable(boolean z) {
        super.setEnable(z);
        refresh();
    }

    public void performShutterButtonClick(boolean z) {
        if (z) {
            if (this.mVideoShutterButton != null) {
                this.mVideoShutterButton.performClick();
            }
        } else if (this.mPhotoShutterButton != null) {
            this.mPhotoShutterButton.performClick();
        }
    }

    public void switchShutterButtonImageResource(int i, boolean z) {
        if (z) {
            this.mVideoShutterButtonResId = i;
        } else {
            this.mPhotoShutterButtonResId = i;
        }
        refresh();
    }

    public void switchShutterButtonLayout(int i) {
        if (this.mShutterLayout != i) {
            this.mShutterLayout = i;
            reInflate();
        }
    }

    public boolean isShutterButtonEnabled(boolean z) {
        if (z) {
            return this.mVideoShutterEnabled;
        }
        return this.mPhotoShutterEnabled;
    }

    public void setShutterButtonEnabled(boolean z, boolean z2) {
        if (z2) {
            this.mVideoShutterEnabled = z;
        } else {
            this.mPhotoShutterEnabled = z;
        }
        refresh();
    }

    public void setOnOkCancelButtonClickListener(OnOkCancelButtonClickListener onOkCancelButtonClickListener) {
        this.mOnOkCancelButtonClickListener = onOkCancelButtonClickListener;
    }

    public void addShutterButtonListener(OnShutterButtonListener onShutterButtonListener, boolean z) {
        if (z) {
            if (onShutterButtonListener != null && (!this.mVideoListeners.contains(onShutterButtonListener))) {
                this.mVideoListeners.add(onShutterButtonListener);
                return;
            }
            return;
        }
        if (onShutterButtonListener != null && (!this.mPhotoListeners.contains(onShutterButtonListener))) {
            this.mPhotoListeners.add(onShutterButtonListener);
        }
    }

    private class OnOkCancelClickListener implements View.OnClickListener {
        /* synthetic */ OnOkCancelClickListener(ShutterManager shutterManager, OnOkCancelClickListener onOkCancelClickListener) {
            this();
        }

        private OnOkCancelClickListener() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            if (view == ShutterManager.this.mOkButton) {
                if (ShutterManager.this.mOnOkCancelButtonClickListener != null) {
                    ShutterManager.this.mOnOkCancelButtonClickListener.onOkClick();
                }
            } else if (view == ShutterManager.this.mCancelButton && ShutterManager.this.mOnOkCancelButtonClickListener != null) {
                ShutterManager.this.mOnOkCancelButtonClickListener.onCancelClick();
            }
        }
    }
}
