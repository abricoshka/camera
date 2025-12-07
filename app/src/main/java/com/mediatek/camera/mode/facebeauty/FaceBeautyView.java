package com.mediatek.camera.mode.facebeauty;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.p000v8.renderscript.ScriptIntrinsicBLAS;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import com.mediatek.camera.ICameraMode;
import com.mediatek.camera.R;
import com.mediatek.camera.mode.facebeauty.FaceBeautyParametersHelper;
import com.mediatek.camera.p004ui.CameraView;
import com.mediatek.camera.p004ui.RotateImageView;
import com.mediatek.camera.platform.ICameraAppUi;
import com.mediatek.camera.platform.IModuleCtrl;
import com.mediatek.camera.util.Log;
import com.mediatek.camera.util.Util;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class FaceBeautyView extends CameraView implements View.OnClickListener {
    private int SUPPORTED_FB_EFFECTS_NUMBER;
    private SeekBar mAdjustmentValueIndicator;
    private LinearLayout mBgLinearLayout;
    private int mCurrentViewIndex;
    private String mEffectsKey;
    private String mEffectsValue;
    private RotateImageView[] mFaceBeautyImageViews;
    private FaceBeautyInfo mFaceBeautyInfo;
    private ArrayList<Integer> mFaceBeautyPropertiesValue;
    private Handler mHandler;
    private SeekBar.OnSeekBarChangeListener mHorientiaonlSeekBarLisenter;
    private ICameraAppUi mICameraAppUi;
    private IModuleCtrl mIModuleCtrl;
    private boolean mIsInCameraPreview;
    private boolean mIsInPictureTakenProgress;
    private boolean mIsShowSetting;
    private boolean mIsTimeOutMechanismRunning;
    private FaceBeautyParametersHelper.ParameterListener mListener;
    private int mSupportedDuration;
    private int mSupportedMaxValue;
    private View mView;
    private static final int[] FACE_BEAUTY_ICONS_NORMAL = new int[6];
    private static final int[] FACE_BEAUTY_ICONS_HIGHTLIGHT = new int[6];

    static {
        FACE_BEAUTY_ICONS_NORMAL[0] = R.drawable.fb_smooth_normal;
        FACE_BEAUTY_ICONS_NORMAL[1] = R.drawable.fb_whitening_normal;
        FACE_BEAUTY_ICONS_NORMAL[2] = R.drawable.fb_sharp_normal;
        FACE_BEAUTY_ICONS_NORMAL[3] = R.drawable.fb_eye_normal;
        FACE_BEAUTY_ICONS_NORMAL[4] = R.drawable.fb_setting_normal;
        FACE_BEAUTY_ICONS_NORMAL[5] = R.drawable.ic_mode_facebeauty_normal;
        FACE_BEAUTY_ICONS_HIGHTLIGHT[0] = R.drawable.fb_smooth_presse;
        FACE_BEAUTY_ICONS_HIGHTLIGHT[1] = R.drawable.fb_whitening_presse;
        FACE_BEAUTY_ICONS_HIGHTLIGHT[2] = R.drawable.fb_sharp_presse;
        FACE_BEAUTY_ICONS_HIGHTLIGHT[3] = R.drawable.fb_eye_presse;
        FACE_BEAUTY_ICONS_HIGHTLIGHT[4] = R.drawable.fb_setting_pressed;
        FACE_BEAUTY_ICONS_HIGHTLIGHT[5] = R.drawable.ic_mode_facebeauty_focus;
    }

    public FaceBeautyView(Activity activity) {
        super(activity);
        this.SUPPORTED_FB_EFFECTS_NUMBER = 0;
        this.mIsTimeOutMechanismRunning = false;
        this.mIsShowSetting = false;
        this.mIsInPictureTakenProgress = false;
        this.mIsInCameraPreview = true;
        this.mSupportedDuration = 0;
        this.mSupportedMaxValue = 0;
        this.mCurrentViewIndex = 0;
        this.mEffectsKey = null;
        this.mEffectsValue = null;
        this.mFaceBeautyPropertiesValue = new ArrayList<>();
        this.mFaceBeautyImageViews = new RotateImageView[6];
        this.mHorientiaonlSeekBarLisenter = new SeekBar.OnSeekBarChangeListener() { // from class: com.mediatek.camera.mode.facebeauty.FaceBeautyView.1
            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStopTrackingTouch(SeekBar seekBar) {
                int i;
                Log.m31d("FaceBeautyView", "[onStopTrackingTouch]index = " + FaceBeautyView.this.mCurrentViewIndex + ",Progress value is = " + FaceBeautyView.this.mEffectsValue);
                FaceBeautyParametersHelper.ParameterListener parameterListener = FaceBeautyView.this.mListener;
                if (FaceBeautyView.this.mCurrentViewIndex == 2) {
                    i = 4;
                } else {
                    i = FaceBeautyView.this.mCurrentViewIndex;
                }
                parameterListener.setVFBSharedPrefences(i, FaceBeautyView.this.mEffectsValue);
                FaceBeautyView.this.updateEffectsChache(Integer.valueOf(FaceBeautyView.this.mEffectsValue).intValue());
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
                Log.m31d("FaceBeautyView", "[onProgressChanged]progress is  =" + i);
                FaceBeautyView.this.mAdjustmentValueIndicator.setProgress(i);
                FaceBeautyView.this.mCurrentViewIndex %= FaceBeautyView.this.SUPPORTED_FB_EFFECTS_NUMBER;
                FaceBeautyView.this.setEffectsValueParameters(i);
            }
        };
        Log.m34i("FaceBeautyView", "[FaceBeautyView]constructor...");
        this.mHandler = new IndicatorHandler(activity.getMainLooper());
    }

    @Override // com.mediatek.camera.p004ui.CameraView, com.mediatek.camera.platform.ICameraView
    public void init(Activity activity, ICameraAppUi iCameraAppUi, IModuleCtrl iModuleCtrl) {
        Log.m34i("FaceBeautyView", "[init]...");
        this.mICameraAppUi = iCameraAppUi;
        this.mIModuleCtrl = iModuleCtrl;
        setOrientation(this.mIModuleCtrl.getOrientationCompensation());
        this.mView = inflate(R.layout.facebeauty_indicator);
        this.mBgLinearLayout = (LinearLayout) this.mView.findViewById(R.id.effcts_bg);
        this.mFaceBeautyImageViews[5] = (RotateImageView) this.mView.findViewById(R.id.facebeauty_icon);
        this.mFaceBeautyImageViews[4] = (RotateImageView) this.mView.findViewById(R.id.facebeauty_modify);
        this.mFaceBeautyImageViews[3] = (RotateImageView) this.mView.findViewById(R.id.facebeauty_big_eys_nose);
        this.mFaceBeautyImageViews[2] = (RotateImageView) this.mView.findViewById(R.id.facebeauty_beauty_shape);
        this.mFaceBeautyImageViews[1] = (RotateImageView) this.mView.findViewById(R.id.facebeauty_whitening);
        this.mFaceBeautyImageViews[0] = (RotateImageView) this.mView.findViewById(R.id.facebeauty_wrinkle_remove);
        this.mAdjustmentValueIndicator = (SeekBar) this.mView.findViewById(R.id.facebeauty_changevalue);
        this.mAdjustmentValueIndicator.setProgressDrawable(this.mActivity.getResources().getDrawable(R.drawable.bar_bg));
        applyListeners();
        this.mFaceBeautyInfo = new FaceBeautyInfo(activity, this.mIModuleCtrl);
    }

    @Override // com.mediatek.camera.p004ui.CameraView, com.mediatek.camera.platform.ICameraView
    public void hide() {
        Log.m34i("FaceBeautyView", "[hide]...");
        hideEffectsIconAndSeekBar();
        updateModifyIconStatus(false);
        hideFaceBeautyIcon();
        super.hide();
    }

    @Override // com.mediatek.camera.p004ui.CameraView
    protected View getView() {
        Log.m34i("FaceBeautyView", "[getView].view = " + this.mView);
        return this.mView;
    }

    @Override // com.mediatek.camera.p004ui.CameraView, com.mediatek.camera.platform.ICameraView
    public void show() {
        Log.m34i("FaceBeautyView", "[show]...,mIsShowSetting = " + this.mIsShowSetting + ",mIsInCameraPreview = " + this.mIsInCameraPreview);
        if (!this.mIsShowSetting && this.mIsInCameraPreview) {
            super.show();
            intoVfbMode();
        }
    }

    @Override // com.mediatek.camera.p004ui.CameraView, com.mediatek.camera.platform.ICameraView
    public void setListener(Object obj) {
        this.mListener = (FaceBeautyParametersHelper.ParameterListener) obj;
    }

    @Override // com.mediatek.camera.p004ui.CameraView, com.mediatek.camera.platform.ICameraView
    public boolean update(int i, Object... objArr) {
        boolean z;
        if (102 != i && 103 != i) {
            Log.m34i("FaceBeautyView", "[update] type = " + i);
        }
        switch (i) {
            case 101:
                prepareVFB();
                break;
            case 102:
                updateUI(((Integer) objArr[0]).intValue());
                break;
            case 103:
                Util.setOrientation(this.mView, ((Integer) objArr[0]).intValue(), true);
                if (this.mFaceBeautyInfo != null) {
                    this.mFaceBeautyInfo.onOrientationChanged(((Integer) objArr[0]).intValue());
                    break;
                }
                break;
            case 104:
                if (isEffectsShowing()) {
                    onModifyIconClick();
                    z = true;
                } else {
                    removeBackToNormalMsg();
                    z = false;
                }
                break;
            case 105:
                if (isEffectsShowing()) {
                    onModifyIconClick();
                    break;
                }
                break;
            case 106:
                this.mIsInCameraPreview = ((Boolean) objArr[0]).booleanValue();
                Log.m34i("FaceBeautyView", "ON_FULL_SCREEN_CHANGED, mIsInCameraPreview = " + this.mIsInCameraPreview);
                if (!this.mIsInCameraPreview) {
                    if (isEffectsShowing()) {
                        this.mICameraAppUi.showAllViews();
                    }
                    hide();
                    break;
                } else {
                    show();
                    break;
                }
            case 107:
                removeBackToNormalMsg();
                break;
            case 108:
                this.mIsShowSetting = ((Boolean) objArr[0]).booleanValue();
                Log.m34i("FaceBeautyView", "ON_SETTING_BUTTON_CLICK,mIsShowSetting =  " + this.mIsShowSetting);
                if (!this.mIsShowSetting) {
                    show();
                    break;
                } else {
                    hide();
                    break;
                }
            case 109:
                hide();
                uninit();
                break;
            case 110:
                Log.m34i("FaceBeautyView", "[ON_SELFTIMER_CAPTUEING] args[0] = " + ((Boolean) objArr[0]) + ", mIsInPictureTakenProgress = " + this.mIsInPictureTakenProgress);
                if (!((Boolean) objArr[0]).booleanValue()) {
                    if (!this.mIsInPictureTakenProgress) {
                        show();
                        break;
                    }
                } else {
                    hide();
                    removeBackToNormalMsg();
                    break;
                }
                break;
            case ScriptIntrinsicBLAS.NO_TRANSPOSE /* 111 */:
                this.mIsInPictureTakenProgress = ((Boolean) objArr[0]).booleanValue();
                Log.m34i("FaceBeautyView", "mIsInPictureTakenProgress = " + this.mIsInPictureTakenProgress + ",mIsTimeOutMechanismRunning = " + this.mIsTimeOutMechanismRunning);
                if (!this.mIsInPictureTakenProgress) {
                    show();
                    break;
                } else {
                    hide();
                    removeBackToNormalMsg();
                    break;
                }
            case ScriptIntrinsicBLAS.TRANSPOSE /* 112 */:
                removeBackToNormalMsg();
                break;
        }
        return false;
    }

    @Override // com.mediatek.camera.p004ui.CameraView, com.mediatek.camera.platform.ICameraView
    public boolean isShowing() {
        return false;
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        int i = 0;
        int i2 = 0;
        while (true) {
            if (i2 >= 6) {
                break;
            }
            if (this.mFaceBeautyImageViews[i2] != view) {
                i2++;
            } else {
                this.mCurrentViewIndex = i2;
                break;
            }
        }
        while (true) {
            int i3 = i;
            if (i3 >= this.SUPPORTED_FB_EFFECTS_NUMBER) {
                break;
            }
            if (this.mCurrentViewIndex == i3) {
                this.mFaceBeautyImageViews[i3].setImageResource(FACE_BEAUTY_ICONS_HIGHTLIGHT[i3]);
                setProgressValue(this.mFaceBeautyPropertiesValue.get(i3).intValue());
            } else {
                this.mFaceBeautyImageViews[i3].setImageResource(FACE_BEAUTY_ICONS_NORMAL[i3]);
            }
            i = i3 + 1;
        }
        Log.m31d("FaceBeautyView", "[onClick]mCurrentViewIndex = " + this.mCurrentViewIndex);
        switch (this.mCurrentViewIndex) {
            case 0:
                this.mEffectsKey = "pref_facebeauty_smooth_key";
                break;
            case 1:
                this.mEffectsKey = "pref_facebeauty_skin_color_key";
                break;
            case 2:
                this.mEffectsKey = "pref_facebeauty_sharp_key";
                break;
            case 3:
                this.mEffectsKey = "pref_facebeauty_big_eyes_key";
                break;
            case 4:
                onModifyIconClick();
                break;
            case 5:
                onFaceBeautyIconClick();
                break;
            default:
                Log.m34i("FaceBeautyView", "[onClick]click is not the facebeauty imageviews,need check");
                break;
        }
        showEffectsToast(view, this.mCurrentViewIndex);
    }

    private void applyListeners() {
        for (int i = 0; i < 6; i++) {
            if (this.mFaceBeautyImageViews[i] != null) {
                this.mFaceBeautyImageViews[i].setOnClickListener(this);
            }
        }
        if (this.mAdjustmentValueIndicator != null) {
            this.mAdjustmentValueIndicator.setOnSeekBarChangeListener(this.mHorientiaonlSeekBarLisenter);
        }
    }

    private void onModifyIconClick() {
        Log.m31d("FaceBeautyView", "[onModifyIconClick],isFaceBeautyEffectsShowing = " + isEffectsShowing());
        if (isEffectsShowing()) {
            this.mICameraAppUi.setViewState(ICameraAppUi.ViewState.VIEW_STATE_NORMAL);
            hideEffectsIconAndSeekBar();
            return;
        }
        Log.m34i("FaceBeautyView", "onModifyIconClick, mICameraAppUI = " + this.mICameraAppUi);
        this.mICameraAppUi.setViewState(ICameraAppUi.ViewState.VIEW_STATE_HIDE_ALL_VIEW);
        if (this.mBgLinearLayout != null) {
            this.mBgLinearLayout.setBackgroundResource(R.drawable.bg_icon);
        }
        showFaceBeautyEffects();
        this.mEffectsKey = "pref_facebeauty_smooth_key";
        showEffectsToast(this.mFaceBeautyImageViews[0], 0);
        setProgressValue(this.mFaceBeautyPropertiesValue.get(0).intValue());
    }

    private boolean isEffectsShowing() {
        boolean z = this.mFaceBeautyImageViews[0].getVisibility() == 0;
        Log.m31d("FaceBeautyView", "isEffectsShowing = " + z);
        return z;
    }

    private void hideEffectsIconAndSeekBar() {
        Log.m31d("FaceBeautyView", "[hideEffectsIconAndSeekBar]mSupporteFBEffectsNumber = " + this.SUPPORTED_FB_EFFECTS_NUMBER);
        hideEffectsItems();
        hideSeekBar();
        if (this.mFaceBeautyInfo != null) {
            this.mFaceBeautyInfo.cancel();
        }
        if (this.mBgLinearLayout != null) {
            this.mBgLinearLayout.setBackgroundDrawable(null);
        }
        this.mFaceBeautyImageViews[4].setImageResource(FACE_BEAUTY_ICONS_NORMAL[4]);
    }

    private void showFaceBeautyEffects() {
        Log.m31d("FaceBeautyView", "[showFaceBeautyEffects]...");
        this.mFaceBeautyImageViews[0].setImageResource(FACE_BEAUTY_ICONS_HIGHTLIGHT[0]);
        this.mFaceBeautyImageViews[0].setVisibility(0);
        this.mFaceBeautyImageViews[4].setImageResource(FACE_BEAUTY_ICONS_HIGHTLIGHT[4]);
        if (this.mBgLinearLayout != null) {
            this.mBgLinearLayout.setVisibility(0);
        }
        for (int i = 1; i < this.SUPPORTED_FB_EFFECTS_NUMBER; i++) {
            this.mFaceBeautyImageViews[i].setImageResource(FACE_BEAUTY_ICONS_NORMAL[i]);
            this.mFaceBeautyImageViews[i].setVisibility(0);
        }
        for (int i2 = this.SUPPORTED_FB_EFFECTS_NUMBER; i2 < 4; i2++) {
            this.mFaceBeautyImageViews[i2].setVisibility(8);
        }
        if (this.mAdjustmentValueIndicator != null) {
            this.mAdjustmentValueIndicator.setMax(this.mSupportedDuration);
            this.mAdjustmentValueIndicator.setVisibility(0);
        }
    }

    private void showEffectsToast(View view, int i) {
        if (i >= 0 && i < this.SUPPORTED_FB_EFFECTS_NUMBER && view.getContentDescription() != null) {
            this.mFaceBeautyInfo.setText(view.getContentDescription());
            this.mFaceBeautyInfo.cancel();
            this.mFaceBeautyInfo.setTargetId(i, this.SUPPORTED_FB_EFFECTS_NUMBER + 2);
            this.mFaceBeautyInfo.showToast();
        }
    }

    private void setProgressValue(int i) {
        this.mAdjustmentValueIndicator.setProgress(convertToParamertersValue(i));
    }

    private int convertToParamertersValue(int i) {
        return this.mSupportedMaxValue - i;
    }

    private void onFaceBeautyIconClick() {
        Log.m31d("FaceBeautyView", "[onFaceBeautyIconClick]isFaceBeautyModifyIconShowing = " + isModifyIconShowing());
        if (!isModifyIconShowing()) {
            intoVfbMode();
        } else {
            leaveVfbMode();
        }
    }

    private void intoVfbMode() {
        Log.m31d("FaceBeautyView", "[intoVfbMode]");
        this.mFaceBeautyImageViews[4].setImageResource(FACE_BEAUTY_ICONS_NORMAL[4]);
        updateModifyIconStatus(true);
        this.mFaceBeautyImageViews[5].setImageResource(FACE_BEAUTY_ICONS_HIGHTLIGHT[5]);
        this.mFaceBeautyImageViews[5].setVisibility(0);
        update(103, Integer.valueOf(this.mIModuleCtrl.getOrientationCompensation()));
        hideEffectsItems();
        hideSeekBar();
    }

    private void leaveVfbMode() {
        Log.m31d("FaceBeautyView", "[leaveVfbMode]");
        updateModifyIconStatus(false);
        this.mFaceBeautyImageViews[5].setImageResource(FACE_BEAUTY_ICONS_NORMAL[5]);
        this.mFaceBeautyImageViews[5].setVisibility(0);
        hideEffectsIconAndSeekBar();
        this.mICameraAppUi.setCurrentMode(ICameraMode.CameraModeType.EXT_MODE_PHOTO);
    }

    private void updateUI(int i) {
        if (this.mListener.canShowFbIcon(i)) {
            this.mIsTimeOutMechanismRunning = false;
            this.mView.setEnabled(true);
            showFaceBeautyIcon();
            this.mHandler.removeMessages(0);
            return;
        }
        if (i == 0 && (!this.mIsTimeOutMechanismRunning)) {
            if (isModifyIconShowing()) {
                if (this.mFaceBeautyImageViews[4].isEnabled()) {
                    Log.m34i("FaceBeautyView", "will send msg: DISAPPEAR_VFB_UI");
                    this.mView.setEnabled(false);
                    this.mHandler.removeMessages(0);
                    this.mHandler.sendEmptyMessageDelayed(0, 5000L);
                    this.mIsTimeOutMechanismRunning = true;
                    return;
                }
                return;
            }
            hideFaceBeautyIcon();
        }
    }

    private void showFaceBeautyIcon() {
        if (this.mFaceBeautyImageViews != null && (!isFBIconShowing())) {
            int i = FACE_BEAUTY_ICONS_NORMAL[5];
            if (isModifyIconShowing()) {
                updateModifyIconStatus(true);
            }
            this.mFaceBeautyImageViews[5].setImageResource(i);
            this.mFaceBeautyImageViews[5].setVisibility(0);
            Log.m31d("FaceBeautyView", "showFaceBeautyIcon....");
        }
    }

    private boolean isFBIconShowing() {
        return this.mFaceBeautyImageViews[5].getVisibility() == 0;
    }

    private boolean isModifyIconShowing() {
        boolean z = this.mFaceBeautyImageViews[4].getVisibility() == 0;
        Log.m31d("FaceBeautyView", "[isModifyIconShowing]isModifyIconShowing = " + z);
        return z;
    }

    private void hideFaceBeautyIcon() {
        if (this.mFaceBeautyImageViews != null) {
            this.mFaceBeautyImageViews[5].setVisibility(4);
        }
    }

    private void updateModifyIconStatus(boolean z) {
        if (!z && this.mFaceBeautyInfo != null) {
            this.mFaceBeautyInfo.cancel();
        }
        if (this.mBgLinearLayout != null) {
            this.mBgLinearLayout.setBackgroundDrawable(null);
            this.mBgLinearLayout.setVisibility(0);
        }
        this.mFaceBeautyImageViews[4].setImageResource(FACE_BEAUTY_ICONS_NORMAL[4]);
        this.mFaceBeautyImageViews[4].setVisibility(z ? 0 : 4);
        Log.m31d("FaceBeautyView", "[updateModifyIconStatus]isFaceBeautyModifyIconShowing = " + isModifyIconShowing());
    }

    private void hideEffectsItems() {
        for (int i = 0; i < this.SUPPORTED_FB_EFFECTS_NUMBER; i++) {
            this.mFaceBeautyImageViews[i].setVisibility(8);
        }
    }

    private void hideSeekBar() {
        if (this.mAdjustmentValueIndicator != null) {
            this.mAdjustmentValueIndicator.setVisibility(8);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateEffectsChache(int i) {
        if (this.mEffectsKey != null && this.mCurrentViewIndex >= 0 && this.mCurrentViewIndex < this.SUPPORTED_FB_EFFECTS_NUMBER && i != this.mFaceBeautyPropertiesValue.get(this.mCurrentViewIndex).intValue()) {
            this.mFaceBeautyPropertiesValue.set(this.mCurrentViewIndex, Integer.valueOf(i));
        }
        Log.m31d("FaceBeautyView", "[updateEffectsChache],targetValue = " + i);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setEffectsValueParameters(int i) {
        this.mEffectsValue = Integer.toString(convertToParamertersValue(i));
        Log.m31d("FaceBeautyView", "[setEffectsValueParameters] progress = " + i + ",mCurrentViewIndex = " + this.mCurrentViewIndex + ",will set parameters value = " + this.mEffectsValue);
        this.mListener.setParameters(this.mCurrentViewIndex == 2 ? 4 : this.mCurrentViewIndex, this.mEffectsValue);
    }

    private void prepareVFB() {
        Log.m34i("FaceBeautyView", "[prepareVFB]");
        if (this.mListener.isMultiFbMode()) {
            this.SUPPORTED_FB_EFFECTS_NUMBER = 2;
        } else {
            this.SUPPORTED_FB_EFFECTS_NUMBER = 4;
        }
        this.mFaceBeautyPropertiesValue.clear();
        int i = -1;
        int i2 = 0;
        while (i2 < this.SUPPORTED_FB_EFFECTS_NUMBER) {
            i = 2 == i2 ? i2 + 2 : i2;
            int i3 = this.mListener.getvFbSharedPreferences(i);
            this.mFaceBeautyPropertiesValue.add(Integer.valueOf(i3));
            this.mListener.setParameters(i2, Integer.toString(i3));
            i2++;
        }
        this.mSupportedMaxValue = this.mListener.getMaxLevel(i);
        this.mSupportedDuration = this.mSupportedMaxValue - this.mListener.getMinLevel(i);
    }

    protected class IndicatorHandler extends Handler {
        public IndicatorHandler(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            Log.m34i("FaceBeautyView", "[handleMessage]msg.what = " + message.what + ", mIsTimeOutMechanismRunning = " + FaceBeautyView.this.mIsTimeOutMechanismRunning);
            if (!FaceBeautyView.this.mIsTimeOutMechanismRunning) {
                Log.m34i("FaceBeautyView", "Time out mechanism not running ,so return ");
            }
            switch (message.what) {
                case 0:
                    FaceBeautyView.this.hide();
                    FaceBeautyView.this.mICameraAppUi.changeBackToVFBModeStatues(true);
                    FaceBeautyView.this.mICameraAppUi.showAllViews();
                    FaceBeautyView.this.mICameraAppUi.setCurrentMode(ICameraMode.CameraModeType.EXT_MODE_PHOTO);
                    break;
            }
        }
    }

    private void removeBackToNormalMsg() {
        Log.m34i("FaceBeautyView", "[removeMsg]:DISAPPEAR_VFB_UI");
        if (this.mHandler != null) {
            this.mHandler.removeMessages(0);
            this.mIsTimeOutMechanismRunning = false;
        }
    }
}
