package com.android.camera.manager;

import android.view.View;
import com.android.camera.CameraActivity;
import com.android.camera.ModeChecker;
import com.android.camera.Util;
import com.android.camera.p001ui.ShutterButton;
import com.android.camera.p001ui.ZZZFrameLayout;
import com.mediatek.camera.ISettingCtrl;
import com.mediatek.camera.R;

/* loaded from: classes.dex */
public class ShutterManager extends ViewManager {
    private ZZZFrameLayout mCameraModeSwitch;
    private View mCancelButton;
    private boolean mCancelButtonEnabled;
    private View.OnClickListener mCancelListener;
    private CameraActivity mContext;
    private int mCurrentMode;
    private boolean mFullScreen;
    private ISettingCtrl mISettingController;
    private View mOkButton;
    private boolean mOkButtonEnabled;
    private View.OnClickListener mOklistener;
    private ShutterButton.OnShutterButtonListener mPhotoListener;
    private ShutterButton mPhotoShutter;
    private boolean mPhotoShutterEnabled;
    private View mShutterBarView;
    private int mShutterType;
    private ShutterButton.OnShutterButtonListener mVideoListener;
    private ShutterButton mVideoShutter;
    private boolean mVideoShutterEnabled;
    private boolean mVideoShutterMasked;

    public ShutterManager(CameraActivity cameraActivity) {
        super(cameraActivity, 2);
        this.mShutterType = 0;
        this.mPhotoShutterEnabled = true;
        this.mVideoShutterEnabled = true;
        this.mCancelButtonEnabled = true;
        this.mOkButtonEnabled = true;
        this.mFullScreen = true;
        this.mCurrentMode = 4;
        this.mShutterBarView = null;
        setFileter(false);
        this.mContext = cameraActivity;
    }

    @Override // com.android.camera.manager.ViewManager
    protected View getView() {
        int i = R.layout.tw_camera_shutter_photo_video;
        switch (this.mShutterType) {
            case 1:
                i = R.layout.tw_camera_shutter_photo;
                break;
            case 2:
                i = R.layout.tw_camera_shutte_video;
                break;
            case 3:
                i = R.layout.camera_shutter_ok_cancel;
                break;
            case 4:
                i = R.layout.camera_shutter_cancel;
                break;
            case 5:
                i = R.layout.camera_shutter_cancel_video;
                break;
            case 6:
                i = R.layout.camera_shutter_slow_video;
                break;
        }
        switch (this.mShutterType) {
            case 0:
                setShutterTextVisible(true);
                break;
            case 1:
                setShutterTextVisible(false);
                break;
            case 2:
                setShutterTextVisible(false);
                break;
            default:
                setShutterTextVisible(true);
                break;
        }
        this.mShutterBarView = inflate(i);
        this.mPhotoShutter = (ShutterButton) this.mShutterBarView.findViewById(R.id.shutter_button);
        this.mCameraModeSwitch = (ZZZFrameLayout) this.mShutterBarView.findViewById(R.id.fl_cameramode_swith);
        this.mVideoShutter = (ShutterButton) this.mShutterBarView.findViewById(R.id.shutter_button_video);
        if (this.mCameraModeSwitch != null) {
            this.mCameraModeSwitch.setActivityAndCtrl(this.mContext, this.mISettingController);
        }
        this.mOkButton = this.mShutterBarView.findViewById(R.id.done_button);
        this.mCancelButton = this.mShutterBarView.findViewById(R.id.btn_cancel);
        applyListener();
        return this.mShutterBarView;
    }

    @Override // com.android.camera.manager.ViewManager
    protected void onRelease() {
        if (this.mPhotoShutter != null) {
            this.mPhotoShutter.setOnShutterButtonListener(null);
        }
        if (this.mVideoShutter != null) {
            this.mVideoShutter.setOnShutterButtonListener(null);
        }
        if (this.mOkButton != null) {
            this.mOkButton.setOnClickListener(null);
        }
        if (this.mCancelButton != null) {
            this.mCancelButton.setOnClickListener(null);
        }
        this.mPhotoShutter = null;
        this.mVideoShutter = null;
        this.mOkButton = null;
        this.mCancelButton = null;
    }

    public void setSettingController(ISettingCtrl iSettingCtrl) {
        this.mISettingController = iSettingCtrl;
    }

    private void applyListener() {
        if (this.mPhotoShutter != null) {
            this.mPhotoShutter.setOnShutterButtonListener(this.mPhotoListener);
        }
        if (this.mVideoShutter != null) {
            this.mVideoShutter.setOnShutterButtonListener(this.mVideoListener);
        }
        if (this.mOkButton != null) {
            this.mOkButton.setOnClickListener(this.mOklistener);
        }
        if (this.mCancelButton != null) {
            this.mCancelButton.setOnClickListener(this.mCancelListener);
        }
    }

    public void setShutterListener(ShutterButton.OnShutterButtonListener onShutterButtonListener, ShutterButton.OnShutterButtonListener onShutterButtonListener2, View.OnClickListener onClickListener, View.OnClickListener onClickListener2) {
        this.mPhotoListener = onShutterButtonListener;
        this.mVideoListener = onShutterButtonListener2;
        this.mOklistener = onClickListener;
        this.mCancelListener = onClickListener2;
        applyListener();
    }

    public void switchShutter(int i) {
        if (this.mShutterType != i) {
            this.mShutterType = i;
            reInflate();
        }
    }

    public void setShutterTextVisible(boolean z) {
    }

    public void switchShutterMode(int i) {
        this.mCurrentMode = i;
        this.mContext.switchShutterMode(i, false);
        this.mPhotoShutter.setVisibility(Util.isVideoGroup(this.mCurrentMode) ? 8 : 0);
        this.mVideoShutter.setVisibility(Util.isVideoGroup(this.mCurrentMode) ? 0 : 8);
        onRefresh();
    }

    public int getShutterType() {
        return this.mShutterType;
    }

    @Override // com.android.camera.manager.ViewManager
    public void onRefresh() {
        boolean z = false;
        if (this.mVideoShutter != null) {
            boolean modePickerVisible = ModeChecker.getModePickerVisible(getContext(), getContext().getCameraId(), 8);
            if (!this.mVideoShutterEnabled || !isEnabled() || !this.mFullScreen) {
                modePickerVisible = false;
            }
            if (this.mShutterType != 1) {
                this.mVideoShutter.setEnabled(modePickerVisible);
                this.mVideoShutter.setClickable(modePickerVisible);
            }
            if (this.mISettingController != null) {
                "on".equals(this.mISettingController.getSettingValue("pref_slow_motion_key"));
            }
            if (this.mVideoShutterMasked) {
                if (this.mCurrentMode == 1) {
                    this.mVideoShutter.setImageResource(R.drawable.btn_slow_video_mask);
                } else if (this.mCurrentMode == 0) {
                    this.mVideoShutter.setImageResource(R.drawable.btn_video_time_lapse_mask);
                } else {
                    this.mVideoShutter.setImageResource(R.drawable.btn_video_mask);
                }
            } else if (this.mCurrentMode == 1) {
                this.mVideoShutter.setImageResource(R.drawable.btn_slow_video);
            } else if (this.mCurrentMode == 0) {
                this.mVideoShutter.setImageResource(R.drawable.btn_video_time_lapse);
            } else {
                this.mVideoShutter.setImageResource(R.drawable.btn_video);
            }
        }
        if (this.mPhotoShutter != null) {
            boolean z2 = (this.mPhotoShutterEnabled && isEnabled()) ? this.mFullScreen : false;
            this.mPhotoShutter.setEnabled(z2);
            this.mPhotoShutter.setClickable(z2);
        }
        if (this.mOkButton != null) {
            boolean z3 = (this.mOkButtonEnabled && isEnabled()) ? this.mFullScreen : false;
            this.mOkButton.setEnabled(z3);
            this.mOkButton.setClickable(z3);
        }
        if (this.mCancelButton != null) {
            if (this.mCancelButtonEnabled && isEnabled()) {
                z = this.mFullScreen;
            }
            this.mCancelButton.setEnabled(z);
            this.mCancelButton.setClickable(z);
        }
    }

    public ShutterButton getPhotoShutter() {
        return this.mPhotoShutter;
    }

    public ShutterButton getVideoShutter() {
        return this.mVideoShutter;
    }

    public void onScrollRestMode(boolean z) {
    }

    public void setPhotoShutterEnabled(boolean z) {
        this.mPhotoShutterEnabled = z;
        refresh();
    }

    public void setVideoShutterEnabled(boolean z) {
        this.mVideoShutterEnabled = z;
        refresh();
    }

    public void setOkButtonEnabled(boolean z) {
        this.mOkButtonEnabled = z;
        refresh();
    }

    public void setVideoShutterMask(boolean z) {
        this.mVideoShutterMasked = z;
        refresh();
    }

    @Override // com.android.camera.manager.ViewManager
    public void setEnabled(boolean z) {
        super.setEnabled(z);
        refresh();
    }
}
