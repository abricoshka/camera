package com.android.camera.p002v2.app;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.android.camera.p002v2.app.AppController;
import com.android.camera.p002v2.app.PreviewManager;
import com.android.camera.p002v2.app.SettingAgent;
import com.android.camera.p002v2.p003ui.PreviewStatusListener;
import com.android.camera.p002v2.uimanager.IndicatorManager;
import com.android.camera.p002v2.uimanager.InfoManager;
import com.android.camera.p002v2.uimanager.ModePicker;
import com.android.camera.p002v2.uimanager.OnScreenHint;
import com.android.camera.p002v2.uimanager.PickerManager;
import com.android.camera.p002v2.uimanager.RemainingManager;
import com.android.camera.p002v2.uimanager.ReviewManager;
import com.android.camera.p002v2.uimanager.RotateDialog;
import com.android.camera.p002v2.uimanager.RotateProgress;
import com.android.camera.p002v2.uimanager.SettingManager;
import com.android.camera.p002v2.uimanager.ShutterManager;
import com.android.camera.p002v2.uimanager.ThumbnailManager;
import com.android.camera.p002v2.uimanager.preference.PreferenceManager;
import com.android.camera.p002v2.util.CameraUtil;
import com.mediatek.camera.R;
import com.mediatek.camera.debug.LogHelper;
import com.mediatek.camera.p005v2.exif.Exif;
import java.util.HashMap;
import java.util.Map;

/* loaded from: classes.dex */
public class CameraAppUI implements PreviewManager.SurfaceCallback {
    private static final LogHelper.Tag TAG = new LogHelper.Tag(CameraAppUI.class.getSimpleName());
    private final AppController mAppController;
    private Activity mCameraActivity;
    private final GestureManager mGestureManager;
    private IndicatorManager mIndicatorManager;
    private InfoManager mInfoManager;
    private MainHandler mMainHandler;
    private ModePicker mModePicker;
    private FrameLayout mModuleUI;
    private AppController.OkCancelClickListener mOkCancelClickListener;
    private OnScreenHint mOnScreenHint;
    private int mOrientation;
    private int mOrientationCompensation;
    private AppController.ShutterEventsListener mPhotoShutterListener;
    private PickerManager mPickerManager;
    private AppController.PlayButtonClickListener mPlayButtonClickListener;
    private PreferenceManager mPreferenceManager;
    private PreviewManager mPreviewManager;
    private PreviewStatusListener mPreviewStatusListener;
    private RemainingManager mRemainingManager;
    private AppController.RetakeButtonClickListener mRetakeButtonClickListener;
    private ReviewManager mReviewManager;
    private RotateDialog mRotateDialog;
    private RotateProgress mRotateProgress;
    private SettingAgent mSettingAgent;
    private SettingManager mSettingManager;
    private ShutterManager mShutterManager;
    private ThumbnailManager mThumbnailManager;
    private AppController.ShutterEventsListener mVideoShutterListener;
    private ViewGroup mViewLayerBottom;
    private ViewGroup mViewLayerNormal;
    private ViewGroup mViewLayerOverlay;
    private ViewGroup mViewLayerSetting;
    private ViewGroup mViewLayerShutter;
    private ViewGroup mViewLayerTop;
    private int mPreviewVisibility = 0;
    private boolean mSwipeEnabled = true;
    private boolean mStopShowCommonUi = false;
    private boolean mIsSecureCamera = false;
    private ShutterManager.OnShutterButtonListener mPhotoShutterCallback = new ShutterManager.OnShutterButtonListener() { // from class: com.android.camera.v2.app.CameraAppUI.1
        @Override // com.android.camera.v2.uimanager.ShutterManager.OnShutterButtonListener
        public void onLongPressed() {
            if (CameraAppUI.this.mPhotoShutterListener != null) {
                CameraAppUI.this.mPhotoShutterListener.onShutterLongPressed();
            }
        }

        @Override // com.android.camera.v2.uimanager.ShutterManager.OnShutterButtonListener
        public void onFocused(boolean z) {
            if (z) {
                if (CameraAppUI.this.mPhotoShutterListener != null) {
                    CameraAppUI.this.mPhotoShutterListener.onShutterPressed();
                }
            } else if (CameraAppUI.this.mPhotoShutterListener != null) {
                CameraAppUI.this.mPhotoShutterListener.onShutterReleased();
            }
        }

        @Override // com.android.camera.v2.uimanager.ShutterManager.OnShutterButtonListener
        public void onPressed() {
            if (CameraAppUI.this.mSettingManager != null) {
                CameraAppUI.this.mSettingManager.collapse(true);
            }
            if (CameraAppUI.this.mPhotoShutterListener != null) {
                CameraAppUI.this.mPhotoShutterListener.onShutterClicked();
            }
        }
    };
    private ShutterManager.OnShutterButtonListener mVideoShutterCallback = new ShutterManager.OnShutterButtonListener() { // from class: com.android.camera.v2.app.CameraAppUI.2
        @Override // com.android.camera.v2.uimanager.ShutterManager.OnShutterButtonListener
        public void onLongPressed() {
            if (CameraAppUI.this.mVideoShutterListener != null) {
                CameraAppUI.this.mVideoShutterListener.onShutterLongPressed();
            }
        }

        @Override // com.android.camera.v2.uimanager.ShutterManager.OnShutterButtonListener
        public void onFocused(boolean z) {
            if (z) {
                if (CameraAppUI.this.mVideoShutterListener != null) {
                    CameraAppUI.this.mVideoShutterListener.onShutterPressed();
                }
            } else if (CameraAppUI.this.mVideoShutterListener != null) {
                CameraAppUI.this.mVideoShutterListener.onShutterReleased();
            }
        }

        @Override // com.android.camera.v2.uimanager.ShutterManager.OnShutterButtonListener
        public void onPressed() {
            if (CameraAppUI.this.mPickerManager != null) {
                CameraAppUI.this.mPickerManager.setEnable(false);
            }
            if (CameraAppUI.this.mSettingManager != null) {
                CameraAppUI.this.mSettingManager.collapse(true);
            }
            if (CameraAppUI.this.mVideoShutterListener != null) {
                CameraAppUI.this.mVideoShutterListener.onShutterClicked();
            }
        }
    };
    private ShutterManager.OnOkCancelButtonClickListener mOnOkCancelButtonClickListener = new ShutterManager.OnOkCancelButtonClickListener() { // from class: com.android.camera.v2.app.CameraAppUI.3
        @Override // com.android.camera.v2.uimanager.ShutterManager.OnOkCancelButtonClickListener
        public void onOkClick() {
            LogHelper.m26i(CameraAppUI.TAG, "[onOkClick]...");
            CameraAppUI.this.mOkCancelClickListener.onOkClick();
        }

        @Override // com.android.camera.v2.uimanager.ShutterManager.OnOkCancelButtonClickListener
        public void onCancelClick() {
            LogHelper.m26i(CameraAppUI.TAG, "[onCancelClick]...");
            CameraAppUI.this.mOkCancelClickListener.onCancelClick();
        }
    };
    private PreviewStatusListener.OnPreviewTouchedListener mOnPreviewTouchedListener = new PreviewStatusListener.OnPreviewTouchedListener() { // from class: com.android.camera.v2.app.CameraAppUI.4
        @Override // com.android.camera.v2.ui.PreviewStatusListener.OnPreviewTouchedListener
        public boolean onPreviewTouched() {
            return CameraAppUI.this.mSettingManager.collapse(false);
        }
    };
    private SettingManager.OnSettingChangedListener mSettingChangedListener = new SettingManager.OnSettingChangedListener() { // from class: com.android.camera.v2.app.CameraAppUI.5
        @Override // com.android.camera.v2.uimanager.SettingManager.OnSettingChangedListener
        public void onSettingChanged(String str, String str2) {
            LogHelper.m26i(CameraAppUI.TAG, "[onSettingChanged], key:" + str + ", value:" + str2);
            CameraAppUI.this.mSettingAgent.doSettingChange(str, str2);
        }

        @Override // com.android.camera.v2.uimanager.SettingManager.OnSettingChangedListener
        public void onSettingRestored() {
            Runnable runnable = new Runnable() { // from class: com.android.camera.v2.app.CameraAppUI.5.1
                @Override // java.lang.Runnable
                public void run() {
                    if (CameraAppUI.this.mSettingManager != null) {
                        CameraAppUI.this.mSettingManager.collapse(true);
                    }
                    if (CameraAppUI.this.mModePicker != null) {
                        CameraAppUI.this.mModePicker.restoreToNormalMode();
                    }
                    if (CameraAppUI.this.mPreferenceManager != null) {
                        CameraAppUI.this.mPreferenceManager.restoreSetting();
                    }
                }
            };
            if (CameraAppUI.this.mRotateDialog != null) {
                CameraAppUI.this.mRotateDialog.showAlertDialog(null, CameraAppUI.this.mCameraActivity.getString(R.string.confirm_restore_message), CameraAppUI.this.mCameraActivity.getString(android.R.string.cancel), null, CameraAppUI.this.mCameraActivity.getString(android.R.string.ok), runnable);
            }
        }
    };
    private SettingManager.OnSettingStatusListener mOnSettingStatusListener = new SettingManager.OnSettingStatusListener() { // from class: com.android.camera.v2.app.CameraAppUI.6
        @Override // com.android.camera.v2.uimanager.SettingManager.OnSettingStatusListener
        public void onShown() {
            LogHelper.m26i(CameraAppUI.TAG, "[onShown]...");
            if (CameraAppUI.this.mModePicker != null) {
                CameraAppUI.this.mModePicker.hide();
            }
            if (CameraAppUI.this.mPickerManager != null) {
                CameraAppUI.this.mPickerManager.hide();
            }
            if (CameraAppUI.this.mThumbnailManager != null) {
                CameraAppUI.this.mThumbnailManager.hide();
            }
            CameraAppUI.this.mPreviewVisibility = 1;
            CameraAppUI.this.mAppController.onPreviewVisibilityChanged(CameraAppUI.this.mPreviewVisibility);
        }

        @Override // com.android.camera.v2.uimanager.SettingManager.OnSettingStatusListener
        public void onHidden() {
            LogHelper.m26i(CameraAppUI.TAG, "[onHidden]...");
            if (CameraAppUI.this.mModePicker != null) {
                CameraAppUI.this.mModePicker.show();
            }
            if (CameraAppUI.this.mPickerManager != null) {
                CameraAppUI.this.mPickerManager.show();
            }
            if (CameraAppUI.this.mThumbnailManager != null) {
                CameraAppUI.this.mThumbnailManager.show();
            }
            CameraAppUI.this.mPreviewVisibility = 0;
            CameraAppUI.this.mAppController.onPreviewVisibilityChanged(CameraAppUI.this.mPreviewVisibility);
        }
    };
    private PickerManager.OnPickedListener mOnPickedListener = new PickerManager.OnPickedListener() { // from class: com.android.camera.v2.app.CameraAppUI.7
        @Override // com.android.camera.v2.uimanager.PickerManager.OnPickedListener
        public void onPicked(String str, String str2) {
            LogHelper.m26i(CameraAppUI.TAG, "[onPicked], key:" + str + ", value:" + str2);
            if ("pref_hdr_key".equals(str)) {
                new HashMap().put(str, str2);
                CameraAppUI.this.mSettingAgent.doSettingChange(str, str2);
                if ("on".equals(str2)) {
                    CameraAppUI.this.showInfo(CameraAppUI.this.mCameraActivity.getString(R.string.hdr_guide_capture), 5000);
                    return;
                }
                return;
            }
            if ("pref_camera_id_key".equals(str)) {
                CameraAppUI.this.mSettingManager.collapseImmediately();
                CameraAppUI.this.mIndicatorManager.hide();
                CameraAppUI.this.setAllCommonViewEnable(false);
                CameraAppUI.this.mAppController.onCameraPicked(str2);
                return;
            }
            CameraAppUI.this.mSettingAgent.doSettingChange(str, str2);
        }
    };
    private ModePicker.OnModeChangedListener mOnModeChangedListener = new ModePicker.OnModeChangedListener() { // from class: com.android.camera.v2.app.CameraAppUI.8
        @Override // com.android.camera.v2.uimanager.ModePicker.OnModeChangedListener
        public void onModeChanged(Map<String, String> map) {
            LogHelper.m26i(CameraAppUI.TAG, "[onModeChanged], changedModes:" + map);
            CameraAppUI.this.setAllCommonViewEnable(false);
            CameraAppUI.this.mAppController.onModeChanged(map);
        }

        @Override // com.android.camera.v2.uimanager.ModePicker.OnModeChangedListener
        public void onRestoreToNomalMode(Map<String, String> map) {
            LogHelper.m26i(CameraAppUI.TAG, "[onModeChanged], changedModes:" + map);
            CameraAppUI.this.mAppController.onModeChanged(map);
        }
    };
    private ThumbnailManager.OnThumbnailClickListener mOnThumbnailClickListener = new ThumbnailManager.OnThumbnailClickListener() { // from class: com.android.camera.v2.app.CameraAppUI.9
        @Override // com.android.camera.v2.uimanager.ThumbnailManager.OnThumbnailClickListener
        public void onThumbnailClick() {
            CameraAppUI.this.mAppController.gotoGallery();
        }
    };
    private ReviewManager.OnPlayButtonClickListener mOnPlayButtonClickListener = new ReviewManager.OnPlayButtonClickListener() { // from class: com.android.camera.v2.app.CameraAppUI.10
        @Override // com.android.camera.v2.uimanager.ReviewManager.OnPlayButtonClickListener
        public void onPlayButtonClick() {
            LogHelper.m26i(CameraAppUI.TAG, "[onPlayButtonClick]...");
            CameraAppUI.this.mPlayButtonClickListener.onPlay();
        }
    };
    private ReviewManager.OnRetakeButtonClickListener mOnRetakeButtonClickListener = new ReviewManager.OnRetakeButtonClickListener() { // from class: com.android.camera.v2.app.CameraAppUI.11
        @Override // com.android.camera.v2.uimanager.ReviewManager.OnRetakeButtonClickListener
        public void onRetakeButtonClick() {
            LogHelper.m26i(CameraAppUI.TAG, "[onRetakeButtonClick]...");
            CameraAppUI.this.mRetakeButtonClickListener.onRetake();
        }
    };

    public CameraAppUI(AppController appController) {
        LogHelper.m26i(TAG, "[CameraAppUI]+");
        this.mAppController = appController;
        this.mCameraActivity = this.mAppController.getActivity();
        this.mPreferenceManager = this.mAppController.getPreferenceManager();
        this.mGestureManager = this.mAppController.getGestureManager();
        this.mGestureManager.registerPreviewTouchListener(this.mOnPreviewTouchedListener);
        this.mPreviewManager = this.mAppController.getPreviewManager();
        this.mMainHandler = new MainHandler(this.mCameraActivity.getMainLooper());
    }

    @Override // com.android.camera.v2.app.PreviewManager.SurfaceCallback
    public void surfaceAvailable(Surface surface, int i, int i2) {
        if (this.mPreviewStatusListener != null) {
            this.mPreviewStatusListener.surfaceAvailable(surface, i, i2);
        }
    }

    @Override // com.android.camera.v2.app.PreviewManager.SurfaceCallback
    public void surfaceDestroyed(Surface surface) {
        if (this.mPreviewStatusListener != null) {
            this.mPreviewStatusListener.surfaceDestroyed(surface);
        }
    }

    @Override // com.android.camera.v2.app.PreviewManager.SurfaceCallback
    public void surfaceSizeChanged(Surface surface, int i, int i2) {
        if (this.mPreviewStatusListener != null) {
            this.mPreviewStatusListener.surfaceSizeChanged(surface, i, i2);
        }
    }

    public void init(View view, boolean z, boolean z2) {
        LogHelper.m26i(TAG, "[init]+");
        this.mIsSecureCamera = z;
        initializeCommonUIManagers();
    }

    public void onResume() {
        this.mPreviewManager.resume();
        if (this.mThumbnailManager != null) {
            this.mThumbnailManager.onResume();
        }
    }

    public void onPause() {
        this.mPreviewManager.pause();
        if (this.mThumbnailManager != null) {
            this.mThumbnailManager.onPause();
        }
        if (this.mSettingManager != null) {
            this.mSettingManager.collapse(true);
        }
        if (this.mIndicatorManager != null) {
            this.mIndicatorManager.hide();
        }
        if (this.mInfoManager != null) {
            this.mInfoManager.hide();
        }
    }

    public void onDestroy() {
        if (this.mThumbnailManager != null) {
            this.mThumbnailManager.onDestroy();
        }
    }

    public void onConfigurationChanged(Configuration configuration) {
        if (this.mModePicker != null) {
            this.mModePicker.reInflate();
        }
        if (this.mShutterManager != null) {
            this.mShutterManager.reInflate();
        }
        if (this.mSettingManager != null) {
            this.mSettingManager.reInflate();
        }
        if (this.mPickerManager != null) {
            this.mPickerManager.reInflate();
        }
        if (this.mThumbnailManager != null) {
            this.mThumbnailManager.reInflate();
        }
        if (this.mIndicatorManager != null) {
            this.mIndicatorManager.reInflate();
        }
        if (this.mInfoManager != null) {
            this.mInfoManager.reInflate();
        }
        if (this.mRemainingManager != null) {
            this.mRemainingManager.reInflate();
        }
        this.mOrientationCompensation = (this.mOrientation + CameraUtil.getDisplayRotation(this.mCameraActivity)) % 360;
        rotateLayers(this.mOrientationCompensation);
    }

    public void setSettingAgent(SettingAgent settingAgent) {
        this.mSettingAgent = settingAgent;
        this.mSettingAgent.registerSettingChangedListener(new SettingAgent.SettingChangedListener() { // from class: com.android.camera.v2.app.CameraAppUI.12
            @Override // com.android.camera.v2.app.SettingAgent.SettingChangedListener
            public void onSettingResult(Map<String, String> map, Map<String, String> map2) {
                if (CameraAppUI.this.mPreferenceManager != null) {
                    CameraAppUI.this.mPreferenceManager.updateSettingResult(map, map2);
                }
                CameraAppUI.this.mMainHandler.post(new Runnable() { // from class: com.android.camera.v2.app.CameraAppUI.12.1
                    @Override // java.lang.Runnable
                    public void run() {
                        if (CameraAppUI.this.mSettingManager != null) {
                            CameraAppUI.this.mSettingManager.refresh();
                        }
                        if (CameraAppUI.this.mIndicatorManager != null) {
                            CameraAppUI.this.mIndicatorManager.refresh();
                        }
                        if (CameraAppUI.this.mPickerManager != null) {
                            CameraAppUI.this.mPickerManager.refresh();
                        }
                    }
                });
            }
        }, this.mMainHandler);
    }

    public boolean onBackPressed() {
        if (this.mRotateProgress.onBackPressed() || this.mRotateDialog.onBackPressed()) {
            return true;
        }
        return this.mSettingManager.onBackPressed();
    }

    public void setSwipeEnabled(boolean z) {
        this.mSwipeEnabled = z;
        if (this.mGestureManager != null) {
            this.mGestureManager.setScrollEnable(z);
        }
    }

    public void setAllCommonViewEnable(boolean z) {
        LogHelper.m26i(TAG, "[setAllCommonViewEnable], enable:" + z);
        this.mModePicker.setEnable(z);
        this.mSettingManager.setEnable(z);
        this.mPickerManager.setEnable(z);
        this.mShutterManager.setEnable(z);
        if (!(this.mStopShowCommonUi ? z : false)) {
            this.mThumbnailManager.setEnable(z);
        }
    }

    public Uri getThumbnailUri() {
        return this.mThumbnailManager.getThumbnailUri();
    }

    public String getThumbnailMimeType() {
        return this.mThumbnailManager.getThumbnailMimeType();
    }

    public void updateSecureThumbnail(boolean z) {
        if (this.mThumbnailManager != null) {
            this.mThumbnailManager.updateNeedShowThumbnail(z);
        }
    }

    public FrameLayout getModuleRootView() {
        LogHelper.m26i(TAG, "[getModuleRootView]+");
        return this.mModuleUI;
    }

    public void prepareModuleUI() {
        LogHelper.m26i(TAG, "[prepareModuleUI]+");
        this.mPreviewManager.setSurfaceCallback(this);
    }

    public void clearModuleUI() {
        LogHelper.m26i(TAG, "[clearModuleUI]+");
    }

    public void setPreviewStatusListener(PreviewStatusListener previewStatusListener) {
        this.mPreviewStatusListener = previewStatusListener;
        if (this.mPreviewStatusListener != null) {
            onPreviewListenerChanged();
        }
    }

    public void addPreviewAreaSizeChangedListener(PreviewStatusListener.OnPreviewAreaChangedListener onPreviewAreaChangedListener) {
        this.mPreviewManager.addPreviewAreaSizeChangedListener(onPreviewAreaChangedListener);
    }

    public void removePreviewAreaSizeChangedListener(PreviewStatusListener.OnPreviewAreaChangedListener onPreviewAreaChangedListener) {
        this.mPreviewManager.removePreviewAreaSizeChangedListener(onPreviewAreaChangedListener);
    }

    public void updatePreviewSize(int i, int i2) {
        this.mPreviewManager.updatePreviewSize(i, i2);
    }

    public void onPreviewStarted() {
        this.mPreviewManager.onPreviewStarted();
    }

    public void setShutterEventListener(AppController.ShutterEventsListener shutterEventsListener, boolean z) {
        if (z) {
            this.mVideoShutterListener = shutterEventsListener;
            this.mShutterManager.addShutterButtonListener(this.mVideoShutterCallback, true);
        } else {
            this.mPhotoShutterListener = shutterEventsListener;
            this.mShutterManager.addShutterButtonListener(this.mPhotoShutterCallback, false);
        }
    }

    public void setOkCancelClickListener(AppController.OkCancelClickListener okCancelClickListener) {
        this.mOkCancelClickListener = okCancelClickListener;
        this.mShutterManager.setOnOkCancelButtonClickListener(this.mOnOkCancelButtonClickListener);
    }

    public void setShutterButtonEnabled(boolean z, boolean z2) {
        if (this.mShutterManager != null) {
            this.mShutterManager.setShutterButtonEnabled(z, z2);
        }
    }

    public boolean isShutterButtonEnabled(boolean z) {
        if (this.mShutterManager != null) {
            return this.mShutterManager.isShutterButtonEnabled(z);
        }
        return false;
    }

    public void performShutterButtonClick(boolean z) {
        this.mShutterManager.performShutterButtonClick(z);
    }

    public void switchShutterButtonImageResource(int i, boolean z) {
        this.mShutterManager.switchShutterButtonImageResource(i, z);
    }

    public void switchShutterButtonLayout(int i) {
        this.mShutterManager.switchShutterButtonLayout(i);
    }

    public void onOrientationChanged(int i) {
        int iRoundOrientation = CameraUtil.roundOrientation(i, this.mOrientation);
        if (iRoundOrientation == this.mOrientation) {
            return;
        }
        this.mOrientation = iRoundOrientation;
        this.mGestureManager.onOrientationChanged(this.mOrientation);
        this.mOrientationCompensation = (this.mOrientation + CameraUtil.getDisplayRotation(this.mCameraActivity)) % 360;
        LogHelper.m26i(TAG, "[onOrientationChanged], mOrientation:" + this.mOrientation + ", mOrientationCompensation:" + this.mOrientationCompensation);
        rotateLayers(this.mOrientationCompensation);
    }

    public void setPlayButtonClickListener(AppController.PlayButtonClickListener playButtonClickListener) {
        this.mPlayButtonClickListener = playButtonClickListener;
        this.mReviewManager.setOnPlayButtonClickListener(this.mOnPlayButtonClickListener);
    }

    public void setRetakeButtonClickListener(AppController.RetakeButtonClickListener retakeButtonClickListener) {
        this.mRetakeButtonClickListener = retakeButtonClickListener;
        this.mReviewManager.setOnRetakeButtonClickListener(this.mOnRetakeButtonClickListener);
    }

    public void stopShowCommonUI(boolean z) {
        LogHelper.m26i(TAG, "[stopShowCommonUI], stop:" + z);
        this.mStopShowCommonUi = z;
    }

    public void showSettingUi() {
        if (this.mSettingManager != null) {
            this.mSettingManager.show();
        }
    }

    public void hideSettingUi() {
        if (this.mSettingManager != null) {
            this.mSettingManager.collapse(true);
            this.mSettingManager.hide();
        }
    }

    public void showModeOptionsUi() {
        if (this.mModePicker != null) {
            this.mModePicker.show();
        }
    }

    public void hideModeOptionsUi() {
        if (this.mModePicker != null) {
            this.mModePicker.hide();
        }
    }

    public void showPickerManagerUi() {
        if (this.mPickerManager != null) {
            this.mPickerManager.show();
            this.mPickerManager.setEnable(true);
        }
    }

    public void hidePickerManagerUi() {
        if (this.mPickerManager != null) {
            this.mPickerManager.hide();
            this.mPickerManager.setEnable(false);
        }
    }

    public void performCameraPickerBtnClick(boolean z) {
        LogHelper.m26i(TAG, "[performCameraPickerBtnClick], force:" + z);
        if (this.mPickerManager != null) {
            if (this.mPickerManager.isEnable() || z) {
                this.mPickerManager.performCameraPickerBtnClick();
            }
        }
    }

    public void showThumbnailManagerUi() {
        if (this.mThumbnailManager != null) {
            this.mThumbnailManager.show();
        }
    }

    public void setThumbnailManagerEnable(boolean z) {
        if (this.mThumbnailManager != null) {
            this.mThumbnailManager.setEnable(z);
        }
    }

    public void forceUpdateThumbnail() {
        if (this.mThumbnailManager != null) {
            this.mThumbnailManager.forceUpdate();
        }
    }

    public void hideThumbnailManagerUi() {
        if (this.mThumbnailManager != null) {
            this.mThumbnailManager.hide();
        }
    }

    public void showIndicatorManagerUi() {
        if (this.mIndicatorManager != null) {
            this.mIndicatorManager.show();
        }
    }

    public void hideIndicatorManagerUi() {
        if (this.mIndicatorManager != null) {
            this.mIndicatorManager.hide();
        }
    }

    public void showInfo(CharSequence charSequence, int i) {
        LogHelper.m26i(TAG, "[showInfo], text:" + charSequence + ", showMs:" + i);
        this.mMainHandler.removeMessages(1000);
        if (this.mRemainingManager.isShowing()) {
            this.mMainHandler.sendMessageDelayed(this.mMainHandler.obtainMessage(1000, i, 0, charSequence), 1000L);
        } else {
            doShowInfo(charSequence, i);
        }
    }

    public void dismissInfo(boolean z) {
        if (this.mInfoManager != null) {
            this.mMainHandler.removeMessages(1000);
            this.mMainHandler.removeMessages(1003);
            doDismissInfo(z);
        }
    }

    public void notifyPreferenceReady() {
        LogHelper.m26i(TAG, "[notifyPreferenceReady]...");
        if (!this.mStopShowCommonUi && (!this.mReviewManager.isShowing())) {
            this.mShutterManager.show();
            this.mSettingManager.show();
            this.mPickerManager.notifyPreferenceReady();
            this.mPickerManager.show();
            this.mModePicker.show();
            this.mRemainingManager.show();
            this.mInfoManager.hide();
            this.mMainHandler.sendEmptyMessageDelayed(1001, 3000L);
        }
        setAllCommonViewEnable(true);
        rotateLayers(this.mOrientationCompensation);
    }

    public void notifyMediaSaved(Uri uri) {
        if (this.mThumbnailManager != null) {
            this.mThumbnailManager.notifyFileSaved(uri);
        }
    }

    public void showLeftCounts(final int i, final boolean z) {
        LogHelper.m26i(TAG, "[showLeftCounts], bytePerCount:" + i + ", showAlways:" + z);
        this.mCameraActivity.runOnUiThread(new Runnable() { // from class: com.android.camera.v2.app.CameraAppUI.13
            @Override // java.lang.Runnable
            public void run() {
                if (CameraAppUI.this.mRemainingManager != null) {
                    CameraAppUI.this.mMainHandler.removeMessages(1001);
                    CameraAppUI.this.mIndicatorManager.hide();
                    CameraAppUI.this.mInfoManager.hide();
                    CameraAppUI.this.mRemainingManager.showLeftCounts(i, z);
                    CameraAppUI.this.mMainHandler.sendEmptyMessageDelayed(1001, 3000L);
                }
            }
        });
    }

    public void showLeftTime(long j) {
        LogHelper.m26i(TAG, "[showLeftTime], bytePerMs:" + j);
        if (this.mRemainingManager != null) {
            this.mMainHandler.removeMessages(1001);
            this.mRemainingManager.showLeftTime(j);
            this.mMainHandler.sendEmptyMessageDelayed(1001, 3000L);
        }
    }

    public void updateAsdDetectedScene(String str) {
        this.mIndicatorManager.updateAsdDetectedScene(str);
        if (!"hdr".equals(str) ? "backlight-portrait".equals(str) : true) {
            showHint(this.mCameraActivity.getString(R.string.asd_hdr_guide));
            this.mPickerManager.forceEnablePickerButton("pref_hdr_key");
        } else {
            hideHint();
            this.mPickerManager.forceDisablePickerButton("pref_hdr_key");
        }
    }

    public void showSavingProgress(String str) {
        if (this.mRotateProgress != null) {
            this.mRotateProgress.showProgress(str);
        }
    }

    public void dismissSavingProgress() {
        if (this.mRotateProgress != null) {
            this.mRotateProgress.hide();
        }
    }

    public void showReviewManager(Bitmap bitmap) {
        if (this.mReviewManager != null) {
            this.mReviewManager.setReviewImage(bitmap);
        }
        if (this.mPickerManager != null) {
            this.mPickerManager.hide();
        }
        if (this.mSettingManager != null) {
            this.mSettingManager.hide();
        }
        View viewFindViewById = this.mCameraActivity.findViewById(R.id.face_detection_view);
        if (viewFindViewById != null) {
            viewFindViewById.setVisibility(8);
        }
        View viewFindViewById2 = this.mCameraActivity.findViewById(R.id.focus_indicator_rotate_layout);
        if (viewFindViewById2 != null) {
            viewFindViewById2.setVisibility(8);
        }
    }

    public void showReviewManager(byte[] bArr, int i) {
        new DecodeTask(bArr, false, i).execute(new Void[0]);
    }

    public void hideReviewManager() {
        if (this.mReviewManager != null) {
            this.mReviewManager.hide();
        }
        if (this.mPickerManager != null) {
            this.mPickerManager.show();
        }
        if (this.mSettingManager != null) {
            this.mSettingManager.show();
        }
        View viewFindViewById = this.mCameraActivity.findViewById(R.id.face_detection_view);
        if (viewFindViewById != null) {
            viewFindViewById.setVisibility(0);
        }
        View viewFindViewById2 = this.mCameraActivity.findViewById(R.id.focus_indicator_rotate_layout);
        if (viewFindViewById2 != null) {
            viewFindViewById2.setVisibility(0);
        }
    }

    public void showHint(String str) {
        this.mOnScreenHint.showHint(str);
    }

    public void hideHint() {
        this.mOnScreenHint.hideHint();
    }

    private void initializeViewGroup() {
        this.mViewLayerBottom = (ViewGroup) this.mCameraActivity.findViewById(R.id.view_layer_bottom);
        this.mViewLayerNormal = (ViewGroup) this.mCameraActivity.findViewById(R.id.view_layer_normal);
        this.mViewLayerTop = (ViewGroup) this.mCameraActivity.findViewById(R.id.view_layer_top);
        this.mViewLayerShutter = (ViewGroup) this.mCameraActivity.findViewById(R.id.view_layer_shutter);
        this.mViewLayerSetting = (ViewGroup) this.mCameraActivity.findViewById(R.id.view_layer_setting);
        this.mViewLayerOverlay = (ViewGroup) this.mCameraActivity.findViewById(R.id.view_layer_overlay);
        this.mModuleUI = (FrameLayout) this.mCameraActivity.findViewById(R.id.view_layer_module);
    }

    private void initializeCommonUIManagers() {
        initializeViewGroup();
        this.mShutterManager = new ShutterManager(this.mCameraActivity, this.mViewLayerShutter);
        this.mSettingManager = new SettingManager(this.mCameraActivity, this.mViewLayerSetting, this.mPreferenceManager);
        this.mSettingManager.setSettingChangedListener(this.mSettingChangedListener);
        this.mSettingManager.setSettingStatusListener(this.mOnSettingStatusListener);
        this.mPickerManager = new PickerManager(this.mCameraActivity, this.mViewLayerNormal, this.mPreferenceManager);
        this.mPickerManager.setOnPickedListener(this.mOnPickedListener);
        this.mModePicker = new ModePicker(this.mCameraActivity, this.mViewLayerNormal, this.mPreferenceManager);
        this.mModePicker.setOnModeChangedListener(this.mOnModeChangedListener);
        this.mIndicatorManager = new IndicatorManager(this.mCameraActivity, this.mViewLayerNormal, this.mPreferenceManager);
        this.mRemainingManager = new RemainingManager(this.mAppController, this.mCameraActivity, this.mViewLayerNormal, this.mPreferenceManager);
        this.mThumbnailManager = new ThumbnailManager(this.mAppController, this.mCameraActivity, this.mViewLayerNormal, this.mIsSecureCamera);
        this.mThumbnailManager.setOnThumbnailClickListener(this.mOnThumbnailClickListener);
        this.mThumbnailManager.show();
        this.mReviewManager = new ReviewManager(this.mCameraActivity, this.mViewLayerBottom);
        this.mRotateDialog = new RotateDialog(this.mCameraActivity, this.mViewLayerOverlay);
        this.mRotateProgress = new RotateProgress(this.mCameraActivity, this.mViewLayerOverlay);
        this.mInfoManager = new InfoManager(this.mCameraActivity, this.mViewLayerNormal);
        this.mOnScreenHint = new OnScreenHint(this.mCameraActivity, this.mViewLayerNormal);
        rotateLayers(this.mOrientation);
    }

    private void rotateLayers(int i) {
        this.mViewLayerBottom.setTag(Integer.valueOf(i));
        this.mViewLayerNormal.setTag(Integer.valueOf(i));
        this.mViewLayerTop.setTag(Integer.valueOf(i));
        this.mViewLayerShutter.setTag(Integer.valueOf(i));
        this.mViewLayerSetting.setTag(Integer.valueOf(i));
        this.mViewLayerOverlay.setTag(Integer.valueOf(i));
        CameraUtil.setOrientation(this.mViewLayerBottom, i, true);
        CameraUtil.setOrientation(this.mViewLayerNormal, i, true);
        CameraUtil.setOrientation(this.mViewLayerTop, i, true);
        CameraUtil.setOrientation(this.mViewLayerShutter, i, true);
        CameraUtil.setOrientation(this.mViewLayerSetting, i, true);
        CameraUtil.setOrientation(this.mViewLayerOverlay, i, true);
    }

    private void onPreviewListenerChanged() {
        PreviewStatusListener.OnGestureListener gestureListener = this.mPreviewStatusListener.getGestureListener();
        PreviewStatusListener.OnPreviewTouchedListener touchListener = this.mPreviewStatusListener.getTouchListener();
        if (this.mGestureManager != null) {
            this.mGestureManager.setPreviewGestureListener(gestureListener);
            this.mGestureManager.registerPreviewTouchListener(touchListener);
        }
    }

    private final class MainHandler extends Handler {
        public MainHandler(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            LogHelper.m26i(CameraAppUI.TAG, "msg id=" + message.what);
            switch (message.what) {
                case 1000:
                    CameraAppUI.this.doShowInfo((CharSequence) message.obj, message.arg1);
                    break;
                case 1001:
                    CameraAppUI.this.doShowIndicator();
                    break;
                case 1003:
                    CameraAppUI.this.doDismissInfo(false);
                    break;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void doShowIndicator() {
        if (this.mRemainingManager != null) {
            this.mRemainingManager.hide();
        }
        if (this.mIndicatorManager != null && (!this.mStopShowCommonUi)) {
            this.mIndicatorManager.show();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void doShowInfo(CharSequence charSequence, int i) {
        this.mRemainingManager.hide();
        this.mPickerManager.hide();
        this.mIndicatorManager.hide();
        this.mInfoManager.showText(charSequence);
        if (i > 0) {
            this.mMainHandler.sendMessageDelayed(this.mMainHandler.obtainMessage(1003), i);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void doDismissInfo(boolean z) {
        this.mInfoManager.hide();
        if (!z && (!this.mStopShowCommonUi)) {
            this.mPickerManager.show();
            this.mIndicatorManager.show();
        }
    }

    private class DecodeTask extends AsyncTask<Void, Void, Bitmap> {
        private final byte[] mData;
        private final int mHeight;
        private final boolean mMirror;

        public DecodeTask(byte[] bArr, boolean z, int i) {
            this.mData = bArr;
            this.mMirror = z;
            this.mHeight = i;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public Bitmap doInBackground(Void... voidArr) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            if (this.mHeight > 1440) {
                options.inSampleSize = 4;
            }
            Bitmap bitmapDecodeByteArray = BitmapFactory.decodeByteArray(this.mData, 0, this.mData.length, options);
            int orientation = Exif.getOrientation(this.mData);
            if (orientation != 0 || this.mMirror) {
                Matrix matrix = new Matrix();
                if (this.mMirror) {
                    matrix.setScale(-1.0f, 1.0f);
                }
                matrix.preRotate(orientation);
                return Bitmap.createBitmap(bitmapDecodeByteArray, 0, 0, bitmapDecodeByteArray.getWidth(), bitmapDecodeByteArray.getHeight(), matrix, false);
            }
            return bitmapDecodeByteArray;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(Bitmap bitmap) {
            CameraAppUI.this.showReviewManager(bitmap);
        }
    }
}
