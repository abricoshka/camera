package com.mediatek.camera.mode.pip;

import android.app.Activity;
import android.hardware.display.DisplayManager;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SlidingDrawer;
import com.mediatek.camera.R;
import com.mediatek.camera.p004ui.CameraView;
import com.mediatek.camera.p004ui.RotateImageView;
import com.mediatek.camera.platform.ICameraAppUi;
import com.mediatek.camera.platform.IModuleCtrl;
import com.mediatek.camera.util.Log;
import com.mediatek.camera.util.Util;

/* loaded from: classes.dex */
public class PipView extends CameraView {
    private int mCurrentEffect;
    private int mCurrentOrientation;
    private float mDensity;
    private int mDisplayHeight;
    private DisplayManager.DisplayListener mDisplayListener;
    private int mDisplayRotation;
    private int mDisplayWidth;
    private Animation mFadeIn;
    private Animation mFadeOut;
    private ICameraAppUi mICameraAppUi;
    private ImageView mIndicator;
    private boolean mIsShowingPipSetting;
    private final LinearLayout[] mItemLayouts;
    private Listener mListener;
    private final RotateImageView[] mModeViews;
    private ViewGroup mPipSettingLayout;
    private SlidingDrawer mSlidingDrawer;
    private static int[] mImageView = new int[8];
    private static int[] mImageViewFocus = new int[8];
    private static int[] mImageViewId = new int[8];
    private static int[][] mPIPFrontView = new int[8][];
    private static int[] mItemLayoutId = new int[8];
    private static int editView = R.drawable.plus;

    public interface Listener {
        void onUpdateEffect(int i, int i2, int i3, int i4);
    }

    static {
        mImageView[0] = R.drawable.effect_01;
        mImageView[1] = R.drawable.effect_02;
        mImageView[2] = R.drawable.effect_03;
        mImageView[3] = R.drawable.effect_04;
        mImageView[4] = R.drawable.effect_05;
        mImageView[5] = R.drawable.effect_06;
        mImageView[6] = R.drawable.effect_07;
        mImageView[7] = R.drawable.effect_08;
        mImageViewFocus[0] = R.drawable.effect_01_focus;
        mImageViewFocus[1] = R.drawable.effect_02_focus;
        mImageViewFocus[2] = R.drawable.effect_03_focus;
        mImageViewFocus[3] = R.drawable.effect_04_focus;
        mImageViewFocus[4] = R.drawable.effect_05_focus;
        mImageViewFocus[5] = R.drawable.effect_06_focus;
        mImageViewFocus[6] = R.drawable.effect_07_focus;
        mImageViewFocus[7] = R.drawable.effect_08_focus;
        mImageViewId[0] = R.id.pip_cubism;
        mImageViewId[1] = R.id.pip_fisheye;
        mImageViewId[2] = R.id.pip_heart;
        mImageViewId[3] = R.id.pip_instantphoto;
        mImageViewId[4] = R.id.pip_ovalblur;
        mImageViewId[5] = R.id.pip_postcard;
        mImageViewId[6] = R.id.pip_split;
        mImageViewId[7] = R.id.pip_window;
        mItemLayoutId[0] = R.id.item_layout1;
        mItemLayoutId[1] = R.id.item_layout2;
        mItemLayoutId[2] = R.id.item_layout3;
        mItemLayoutId[3] = R.id.item_layout4;
        mItemLayoutId[4] = R.id.item_layout5;
        mItemLayoutId[5] = R.id.item_layout6;
        mItemLayoutId[6] = R.id.item_layout7;
        mItemLayoutId[7] = R.id.item_layout8;
        mPIPFrontView[0] = new int[]{R.drawable.rear_01, R.drawable.front_01, R.drawable.front_01_focus};
        mPIPFrontView[1] = new int[]{R.drawable.rear_02, R.drawable.front_02, R.drawable.front_02_focus};
        mPIPFrontView[2] = new int[]{R.drawable.rear_03, R.drawable.front_03, R.drawable.front_03_focus};
        mPIPFrontView[3] = new int[]{R.drawable.rear_04, R.drawable.front_04, R.drawable.front_04_focus};
        mPIPFrontView[4] = new int[]{R.drawable.rear_05, R.drawable.front_05, R.drawable.front_05_focus};
        mPIPFrontView[5] = new int[]{R.drawable.rear_06, R.drawable.front_06, R.drawable.front_06_focus};
        mPIPFrontView[6] = new int[]{R.drawable.rear_07, R.drawable.front_07, R.drawable.front_07_focus};
        mPIPFrontView[7] = new int[]{R.drawable.rear_08, R.drawable.front_08, R.drawable.front_08_focus};
    }

    public PipView(Activity activity) {
        super(activity);
        this.mIsShowingPipSetting = false;
        this.mCurrentOrientation = -1;
        this.mCurrentEffect = 0;
        this.mModeViews = new RotateImageView[8];
        this.mItemLayouts = new LinearLayout[8];
        this.mDisplayListener = new DisplayManager.DisplayListener() { // from class: com.mediatek.camera.mode.pip.PipView.1
            @Override // android.hardware.display.DisplayManager.DisplayListener
            public void onDisplayAdded(int i) {
            }

            @Override // android.hardware.display.DisplayManager.DisplayListener
            public void onDisplayChanged(int i) {
                Log.m31d("PipView", "onDisplayChanged");
                PipView.this.reInflate();
            }

            @Override // android.hardware.display.DisplayManager.DisplayListener
            public void onDisplayRemoved(int i) {
            }
        };
        Log.m31d("PipView", "[PipView]constructor...");
    }

    @Override // com.mediatek.camera.p004ui.CameraView, com.mediatek.camera.platform.ICameraView
    public void init(Activity activity, ICameraAppUi iCameraAppUi, IModuleCtrl iModuleCtrl) {
        super.init(activity, iCameraAppUi, iModuleCtrl);
        this.mICameraAppUi = iCameraAppUi;
    }

    @Override // com.mediatek.camera.p004ui.CameraView, com.mediatek.camera.platform.ICameraView
    public void uninit() {
        Log.m31d("PipView", "[uninit]...");
        super.uninit();
        this.mCurrentEffect = 0;
    }

    @Override // com.mediatek.camera.p004ui.CameraView, com.mediatek.camera.platform.ICameraView
    public void show() {
        Log.m31d("PipView", "[show]...");
        super.show();
        if (this.mListener != null) {
            this.mListener.onUpdateEffect(mPIPFrontView[this.mCurrentEffect][0], mPIPFrontView[this.mCurrentEffect][1], mPIPFrontView[this.mCurrentEffect][2], editView);
        }
    }

    @Override // com.mediatek.camera.p004ui.CameraView, com.mediatek.camera.platform.ICameraView
    public void hide() {
        Log.m31d("PipView", "hide");
        super.hide();
        hideEffect();
    }

    @Override // com.mediatek.camera.p004ui.CameraView, com.mediatek.camera.platform.ICameraView
    public void refresh() {
        Log.m31d("PipView", "refresh");
        hideEffect();
    }

    @Override // com.mediatek.camera.p004ui.CameraView, com.mediatek.camera.platform.ICameraView
    public boolean isShowing() {
        return this.mIsShowingPipSetting;
    }

    @Override // com.mediatek.camera.p004ui.CameraView, com.mediatek.camera.platform.ICameraView
    public boolean update(int i, Object... objArr) {
        Log.m31d("PipView", "[update]type = " + i);
        switch (i) {
            case 0:
                showEffect();
                break;
            case 1:
                hideEffect();
                break;
            case 2:
                if (objArr[0] != null) {
                    this.mCurrentOrientation = ((Integer) objArr[0]).intValue();
                    rotatePipSettingViewItem(this.mCurrentOrientation);
                    break;
                }
                break;
        }
        return true;
    }

    @Override // com.mediatek.camera.p004ui.CameraView, com.mediatek.camera.platform.ICameraView
    public void setListener(Object obj) {
        this.mListener = (Listener) obj;
    }

    @Override // com.mediatek.camera.p004ui.CameraView, com.mediatek.camera.platform.ICameraView
    public void setEnabled(boolean z) {
        super.setEnabled(z);
        Log.m31d("PipView", "setEnabled enabled= " + z);
        if (z) {
            this.mSlidingDrawer.unlock();
        } else {
            this.mSlidingDrawer.lock();
        }
    }

    @Override // com.mediatek.camera.p004ui.CameraView
    protected View getView() {
        Log.m31d("PipView", "[getView]...");
        this.mPipSettingLayout = (ViewGroup) inflate(R.layout.pip_setting);
        this.mIndicator = (ImageView) this.mPipSettingLayout.findViewById(R.id.pip_indicator);
        this.mSlidingDrawer = (SlidingDrawer) this.mPipSettingLayout.findViewById(R.id.drawer1);
        this.mSlidingDrawer.setOnDrawerCloseListener(new SlidingDrawer.OnDrawerCloseListener() { // from class: com.mediatek.camera.mode.pip.PipView.2
            @Override // android.widget.SlidingDrawer.OnDrawerCloseListener
            public void onDrawerClosed() {
                PipView.this.mDisplayRotation = Util.getDisplayRotation(PipView.this.mActivity);
                if (PipView.this.mDisplayRotation == 90 || PipView.this.mDisplayRotation == 270) {
                    PipView.this.mIndicator.setImageResource(R.drawable.land_open_row);
                } else {
                    PipView.this.mIndicator.setImageResource(R.drawable.port_close_row);
                }
                PipView.this.hideEffect();
            }
        });
        this.mSlidingDrawer.setOnDrawerOpenListener(new SlidingDrawer.OnDrawerOpenListener() { // from class: com.mediatek.camera.mode.pip.PipView.3
            @Override // android.widget.SlidingDrawer.OnDrawerOpenListener
            public void onDrawerOpened() {
                PipView.this.mDisplayRotation = Util.getDisplayRotation(PipView.this.mActivity);
                if (PipView.this.mDisplayRotation == 90 || PipView.this.mDisplayRotation == 270) {
                    PipView.this.mIndicator.setImageResource(R.drawable.land_close_row);
                } else {
                    PipView.this.mIndicator.setImageResource(R.drawable.port_open_row);
                }
                PipView.this.showEffect();
            }
        });
        DisplayMetrics displayMetrics = this.mActivity.getResources().getDisplayMetrics();
        this.mDisplayWidth = Math.max(displayMetrics.widthPixels, displayMetrics.heightPixels);
        this.mDisplayHeight = Math.min(displayMetrics.widthPixels, displayMetrics.heightPixels);
        this.mDensity = displayMetrics.density;
        if (this.mListener != null) {
            this.mListener.onUpdateEffect(mPIPFrontView[this.mCurrentEffect][0], mPIPFrontView[this.mCurrentEffect][1], mPIPFrontView[this.mCurrentEffect][2], editView);
        }
        return this.mPipSettingLayout;
    }

    @Override // com.mediatek.camera.p004ui.CameraView
    protected void addView(View view) {
        Log.m31d("PipView", "addView");
        this.mICameraAppUi.getNormalViewLayer().addView(view);
        ((DisplayManager) this.mActivity.getSystemService("display")).registerDisplayListener(this.mDisplayListener, null);
    }

    @Override // com.mediatek.camera.p004ui.CameraView
    protected void removeView() {
        Log.m31d("PipView", "removeView");
        if (this.mPipSettingLayout != null) {
            this.mPipSettingLayout.removeAllViewsInLayout();
        }
        super.removeView();
        removeAllLayout();
        clearListener();
        ((DisplayManager) this.mActivity.getSystemService("display")).unregisterDisplayListener(this.mDisplayListener);
    }

    private void rotatePipSettingViewItem(int i) {
        Log.m31d("PipView", "rotatePipSettingViewItem (orientation) = " + i);
        this.mDisplayRotation = Util.getDisplayRotation(this.mActivity);
        if (this.mDisplayRotation == 90 || this.mDisplayRotation == 270) {
            for (int i2 = 0; i2 < 8; i2++) {
                if (this.mModeViews[i2] != null && this.mModeViews[i2].isShown()) {
                    Util.setOrientation(this.mModeViews[i2], i, false);
                }
            }
            return;
        }
        for (int i3 = 0; i3 < 8; i3++) {
            if (this.mModeViews[i3] != null && this.mModeViews[i3].isShown()) {
                Util.setOrientation(this.mModeViews[i3], i + 180, false);
            }
        }
    }

    private void initialEffect() {
        Log.m31d("PipView", "initialEffect()");
        initialModeViewsAndLayout();
        applyListener();
        highlightCurrentMode();
    }

    private void initialModeViewsAndLayout() {
        this.mDisplayRotation = Util.getDisplayRotation(this.mActivity);
        for (int i = 0; i < 8; i++) {
            if (this.mModeViews[i] == null) {
                this.mModeViews[i] = (RotateImageView) this.mPipSettingLayout.findViewById(mImageViewId[i]);
            }
            if (this.mItemLayouts[i] == null) {
                this.mItemLayouts[i] = (LinearLayout) this.mPipSettingLayout.findViewById(mItemLayoutId[i]);
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) this.mItemLayouts[i].getLayoutParams();
                if (this.mDisplayRotation == 90 || this.mDisplayRotation == 270) {
                    layoutParams.setMargins(0, 0, getItemLayoutMargin(), 0);
                } else {
                    layoutParams.setMargins(0, getItemLayoutMargin(), 0, 0);
                }
                this.mItemLayouts[i].setLayoutParams(layoutParams);
            }
        }
        Log.m31d("PipView", "initialModeViewsAndLayout mOrientation = " + this.mDisplayRotation);
    }

    private int getItemLayoutMargin() {
        int i = ((int) ((this.mDisplayWidth - ((this.mDensity * 100.0f) * 5.0f)) - 100.0f)) / 6;
        Log.m31d("PipView", "getItemLayoutMargin itemLayoutMargin = " + i + "mDisplayWidth = " + this.mDisplayWidth);
        return i;
    }

    private void applyListener() {
        ViewClickListener viewClickListener = null;
        for (int i = 0; i < 8; i++) {
            if (this.mModeViews[i] != null) {
                this.mModeViews[i].setOnClickListener(new ViewClickListener(this, viewClickListener));
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showEffect() {
        Log.m31d("PipView", "[showEffect]...");
        if (!this.mIsShowingPipSetting && this.mPipSettingLayout != null) {
            this.mICameraAppUi.hideAllViews();
            this.mIsShowingPipSetting = true;
            initialEffect();
            if (this.mCurrentOrientation != -1) {
                rotatePipSettingViewItem(this.mCurrentOrientation);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void hideEffect() {
        Log.m31d("PipView", "[hideEffect]mIsShowingPipSetting = " + this.mIsShowingPipSetting);
        if (this.mIsShowingPipSetting && this.mPipSettingLayout != null) {
            this.mIsShowingPipSetting = false;
            this.mSlidingDrawer.close();
            this.mICameraAppUi.showAllViews();
        }
    }

    private class ViewClickListener implements View.OnClickListener {
        /* synthetic */ ViewClickListener(PipView pipView, ViewClickListener viewClickListener) {
            this();
        }

        private ViewClickListener() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            Log.m31d("PipView", "onClick v.getId() = " + view.getId());
            if (PipView.this.mListener == null) {
                Log.m36w("PipView", "onClick mListener = null");
            }
            switch (view.getId()) {
                case R.id.pip_window /* 2131493028 */:
                    PipView.this.mListener.onUpdateEffect(PipView.mPIPFrontView[7][0], PipView.mPIPFrontView[7][1], PipView.mPIPFrontView[7][2], PipView.editView);
                    PipView.this.setImageFocusView(7);
                    break;
                case R.id.pip_split /* 2131493030 */:
                    PipView.this.mListener.onUpdateEffect(PipView.mPIPFrontView[6][0], PipView.mPIPFrontView[6][1], PipView.mPIPFrontView[6][2], PipView.editView);
                    PipView.this.setImageFocusView(6);
                    break;
                case R.id.pip_postcard /* 2131493032 */:
                    PipView.this.mListener.onUpdateEffect(PipView.mPIPFrontView[5][0], PipView.mPIPFrontView[5][1], PipView.mPIPFrontView[5][2], PipView.editView);
                    PipView.this.setImageFocusView(5);
                    break;
                case R.id.pip_ovalblur /* 2131493034 */:
                    PipView.this.mListener.onUpdateEffect(PipView.mPIPFrontView[4][0], PipView.mPIPFrontView[4][1], PipView.mPIPFrontView[4][2], PipView.editView);
                    PipView.this.setImageFocusView(4);
                    break;
                case R.id.pip_instantphoto /* 2131493036 */:
                    PipView.this.mListener.onUpdateEffect(PipView.mPIPFrontView[3][0], PipView.mPIPFrontView[3][1], PipView.mPIPFrontView[3][2], PipView.editView);
                    PipView.this.setImageFocusView(3);
                    break;
                case R.id.pip_heart /* 2131493038 */:
                    PipView.this.mListener.onUpdateEffect(PipView.mPIPFrontView[2][0], PipView.mPIPFrontView[2][1], PipView.mPIPFrontView[2][2], PipView.editView);
                    PipView.this.setImageFocusView(2);
                    break;
                case R.id.pip_fisheye /* 2131493040 */:
                    PipView.this.mListener.onUpdateEffect(PipView.mPIPFrontView[1][0], PipView.mPIPFrontView[1][1], PipView.mPIPFrontView[1][2], PipView.editView);
                    PipView.this.setImageFocusView(1);
                    break;
                case R.id.pip_cubism /* 2131493042 */:
                    PipView.this.mListener.onUpdateEffect(PipView.mPIPFrontView[0][0], PipView.mPIPFrontView[0][1], PipView.mPIPFrontView[0][2], PipView.editView);
                    PipView.this.setImageFocusView(0);
                    break;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setImageFocusView(int i) {
        this.mCurrentEffect = i;
        for (int i2 = 0; i2 < 8; i2++) {
            if (this.mModeViews[i2] != null) {
                if (i2 == i) {
                    this.mModeViews[i2].setImageResource(mImageViewFocus[i2]);
                } else {
                    this.mModeViews[i2].setImageResource(mImageView[i2]);
                }
            }
        }
    }

    private void clearListener() {
        for (int i = 0; i < 8; i++) {
            if (this.mModeViews[i] != null) {
                this.mModeViews[i].setOnClickListener(null);
                this.mModeViews[i] = null;
            }
        }
    }

    private void highlightCurrentMode() {
        Log.m31d("PipView", "highlightCurrentMode()");
        for (int i = 0; i < 8; i++) {
            if (this.mModeViews[i] != null) {
                if (i == this.mCurrentEffect) {
                    this.mModeViews[i].setImageResource(mImageViewFocus[i]);
                } else {
                    this.mModeViews[i].setImageResource(mImageView[i]);
                }
            }
        }
    }

    private void removeAllLayout() {
        Log.m31d("PipView", "removeAllLayout()");
        this.mPipSettingLayout = null;
        this.mFadeIn = null;
        this.mFadeOut = null;
        for (int i = 0; i < 8; i++) {
            if (this.mItemLayouts[i] != null) {
                this.mItemLayouts[i].removeAllViews();
                this.mItemLayouts[i] = null;
            }
        }
    }
}
