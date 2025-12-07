package com.android.camera.bridge;

import android.app.Activity;
import android.media.CamcorderProfile;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.FrameMetricsAggregator;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.android.camera.CameraActivity;
import com.android.camera.FeatureSwitcher;
import com.android.camera.FileSaver;
import com.android.camera.ParametersHelper;
import com.android.camera.Storage;
import com.android.camera.manager.EffectViewManager;
import com.android.camera.manager.IdManager;
import com.android.camera.manager.IndicatorManager;
import com.android.camera.manager.InfoManager;
import com.android.camera.manager.ModePicker;
import com.android.camera.manager.OnScreenHint;
import com.android.camera.manager.PickerManager;
import com.android.camera.manager.RemainingManager;
import com.android.camera.manager.ReviewManager;
import com.android.camera.manager.RotateDialog;
import com.android.camera.manager.RotateProgress;
import com.android.camera.manager.SettingManager;
import com.android.camera.manager.ShutterManager;
import com.android.camera.manager.SubSettingManager;
import com.android.camera.manager.ThumbnailViewManager;
import com.android.camera.manager.ViewManager;
import com.android.camera.manager.ZoomManager;
import com.android.camera.p001ui.FaceBeautyEntryView;
import com.android.camera.p001ui.RenWuMainView;
import com.android.camera.p001ui.ShutterButton;
import com.mediatek.camera.ICameraMode;
import com.mediatek.camera.ISettingCtrl;
import com.mediatek.camera.R;
import com.mediatek.camera.platform.ICameraAppUi;
import com.mediatek.camera.platform.ICameraView;
import com.mediatek.camera.platform.IModuleCtrl;
import com.mediatek.camera.util.Log;
import java.io.FileDescriptor;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/* loaded from: classes.dex */
public class CameraAppUiImpl implements ICameraAppUi {

    /* renamed from: -com-mediatek-camera-ICameraMode$CameraModeTypeSwitchesValues, reason: not valid java name */
    private static final /* synthetic */ int[] f85commediatekcameraICameraMode$CameraModeTypeSwitchesValues = null;

    /* renamed from: -com-mediatek-camera-platform-ICameraAppUi$ShutterButtonTypeSwitchesValues */
    private static final /* synthetic */ int[] f60x84e7a650 = null;

    /* renamed from: -com-mediatek-camera-platform-ICameraAppUi$ViewStateSwitchesValues */
    private static final /* synthetic */ int[] f61x3202d3a3 = null;
    private final CameraActivity mCameraActivity;
    private EffectViewManager mEffectManager;
    private EffectViewManager mEffectViewManager;
    private FaceBeautyEntryView mFaceBeautyEntryView;
    private IdManager mIdManager;
    private IndicatorManager mIndicatorManager;
    private InfoManager mInfoManager;
    private MainHandler mMainHandler;
    private ModePicker mModePicker;
    private PickerManager mPickerManager;
    private RemainingManager mRemainingManager;
    private RenWuMainView mRenWuMainView;
    private ReviewManager mReviewManager;
    private RotateDialog mRotateDialog;
    private RotateProgress mRotateProgress;
    private OnScreenHint mRotateToast;
    private SettingManager mSettingManager;
    private ShutterManager mShutterManager;
    private SubSettingManager mSubSettingManager;
    private ThumbnailViewManager mThumbnailManager;
    private ViewGroup mViewLayerBottom;
    private ViewGroup mViewLayerNormal;
    private ViewGroup mViewLayerOverlay;
    private ViewGroup mViewLayerSetting;
    private ViewGroup mViewLayerShutter;
    private ViewGroup mViewLayerTop;
    private ZoomManager mZoomManager;
    private RelativeLayout renxiangControlView;
    private boolean mIsNeedBackToVFBMode = false;
    private boolean mIsInCameraPreview = true;
    private boolean mIsVideoShutterButtonEanble = true;
    private ICameraAppUi.ViewState mCurrentViewState = ICameraAppUi.ViewState.VIEW_STATE_NORMAL;
    private ICameraAppUi.ViewState mRestoreViewState = ICameraAppUi.ViewState.VIEW_STATE_NORMAL;
    private List<ViewManager> mViewManagers = new CopyOnWriteArrayList();
    private HashMap<ICameraAppUi.CommonUiType, ICameraView> mCameraViewArray = new HashMap<>();
    private float mOldSurfaceViewAlphaValue = 1.0f;
    private EffectListenerImpl mEffectListener = new EffectListenerImpl(this, null);
    boolean mVideoShutterMask = false;
    private ShutterButton.OnShutterButtonListener mPhotoShutterListener = new ShutterButton.OnShutterButtonListener() { // from class: com.android.camera.bridge.CameraAppUiImpl.1
        @Override // com.android.camera.ui.ShutterButton.OnShutterButtonListener
        public void onShutterButtonLongPressed(ShutterButton shutterButton) {
        }

        @Override // com.android.camera.ui.ShutterButton.OnShutterButtonListener
        public void onShutterButtonFocus(ShutterButton shutterButton, boolean z) {
            Log.m31d("CameraAppUiImpl", "[photo.onShutterButtonFocus] (" + shutterButton + ", " + z + ")");
            CameraAppUiImpl.this.mSettingManager.cancleHideAnimation();
            CameraAppUiImpl.this.mSettingManager.collapse(true);
            if (FeatureSwitcher.isSubSettingEnabled()) {
                CameraAppUiImpl.this.mSubSettingManager.collapse(true);
            }
            ShutterButton.OnShutterButtonListener photoShutterButtonListener = CameraAppUiImpl.this.mCameraActivity.getCameraActor().getPhotoShutterButtonListener();
            if (photoShutterButtonListener != null) {
                photoShutterButtonListener.onShutterButtonFocus(shutterButton, z);
            }
        }

        @Override // com.android.camera.ui.ShutterButton.OnShutterButtonListener
        public void onShutterButtonClick(ShutterButton shutterButton) {
            Log.m31d("CameraAppUiImpl", "[photo.onShutterButtonClick](" + shutterButton + ")isFullScreen()=" + CameraAppUiImpl.this.mCameraActivity.isFullScreen());
            if (CameraAppUiImpl.this.mCameraActivity.isFullScreen()) {
                CameraAppUiImpl.this.mSettingManager.collapse(true);
                if (FeatureSwitcher.isSubSettingEnabled()) {
                    CameraAppUiImpl.this.mSubSettingManager.collapse(true);
                }
                ShutterButton.OnShutterButtonListener photoShutterButtonListener = CameraAppUiImpl.this.mCameraActivity.getCameraActor().getPhotoShutterButtonListener();
                if (photoShutterButtonListener != null) {
                    photoShutterButtonListener.onShutterButtonClick(shutterButton);
                }
            }
        }
    };
    private ShutterButton.OnShutterButtonListener mVideoShutterListener = new ShutterButton.OnShutterButtonListener() { // from class: com.android.camera.bridge.CameraAppUiImpl.2
        @Override // com.android.camera.ui.ShutterButton.OnShutterButtonListener
        public void onShutterButtonLongPressed(ShutterButton shutterButton) {
            Log.m31d("CameraAppUiImpl", "[video.onShutterButtonLongPressed] (" + shutterButton + ")");
            CameraAppUiImpl.this.mSettingManager.collapse(true);
            if (FeatureSwitcher.isSubSettingEnabled()) {
                CameraAppUiImpl.this.mSubSettingManager.collapse(true);
            }
            ShutterButton.OnShutterButtonListener videoShutterButtonListener = CameraAppUiImpl.this.mCameraActivity.getCameraActor().getVideoShutterButtonListener();
            if (videoShutterButtonListener != null) {
                videoShutterButtonListener.onShutterButtonLongPressed(shutterButton);
            }
        }

        @Override // com.android.camera.ui.ShutterButton.OnShutterButtonListener
        public void onShutterButtonFocus(ShutterButton shutterButton, boolean z) {
            Log.m31d("CameraAppUiImpl", "[Video.onShutterButtonFocus] (" + shutterButton + ", " + z + ")");
            if (z && CameraAppUiImpl.this.mCameraActivity.isFullScreen()) {
                CameraAppUiImpl.this.setSwipeEnabled(false);
            }
            CameraAppUiImpl.this.mSettingManager.cancleHideAnimation();
            CameraAppUiImpl.this.mSettingManager.collapse(true);
            if (FeatureSwitcher.isSubSettingEnabled()) {
                CameraAppUiImpl.this.mSubSettingManager.collapse(true);
            }
            ShutterButton.OnShutterButtonListener videoShutterButtonListener = CameraAppUiImpl.this.mCameraActivity.getCameraActor().getVideoShutterButtonListener();
            if (videoShutterButtonListener != null && CameraAppUiImpl.this.mCameraActivity.isCameraOpened()) {
                videoShutterButtonListener.onShutterButtonFocus(shutterButton, z);
            }
        }

        @Override // com.android.camera.ui.ShutterButton.OnShutterButtonListener
        public void onShutterButtonClick(final ShutterButton shutterButton) {
            Log.m31d("CameraAppUiImpl", "[Video.onShutterButtonClick] (" + shutterButton + ") isFullScreen()=" + CameraAppUiImpl.this.mCameraActivity.isFullScreen() + ",isCameraOpened = " + CameraAppUiImpl.this.mCameraActivity.isCameraOpened() + ",Camera State = " + CameraAppUiImpl.this.mCameraActivity.getCameraState());
            if (CameraAppUiImpl.this.mCurrentViewState == ICameraAppUi.ViewState.VIEW_STATE_LOMOEFFECT_SETTING) {
                return;
            }
            CameraAppUiImpl.this.mSettingManager.collapse(true);
            if (FeatureSwitcher.isSubSettingEnabled()) {
                CameraAppUiImpl.this.mSubSettingManager.collapse(true);
            }
            if (CameraAppUiImpl.this.mCameraActivity.isFullScreen() && CameraAppUiImpl.this.mCameraActivity.isCameraOpened()) {
                if (CameraAppUiImpl.this.mCameraActivity.getCameraState() == 1 || CameraAppUiImpl.this.mCameraActivity.getCameraState() == 2) {
                    ShutterButton.OnShutterButtonListener videoShutterButtonListener = CameraAppUiImpl.this.mCameraActivity.getCameraActor().getVideoShutterButtonListener();
                    int mode = CameraAppUiImpl.this.mCameraActivity.getCameraActor().getMode();
                    if (videoShutterButtonListener != null) {
                        videoShutterButtonListener.onShutterButtonClick(shutterButton);
                        return;
                    }
                    if (CameraAppUiImpl.this.mModePicker.getModeIndex(mode) != 8) {
                        if (Storage.getLeftSpace() <= 0) {
                            CameraAppUiImpl.this.setSwipeEnabled(true);
                            return;
                        }
                        if (CameraAppUiImpl.this.mModePicker.getModeIndex(mode) == 5) {
                            CameraAppUiImpl.this.mModePicker.setCurrentMode(9);
                        } else if (CameraAppUiImpl.this.mModePicker.getModeIndex(CameraAppUiImpl.this.mCameraActivity.getCameraActor().getMode()) == 6 && ParametersHelper.isVsDofSupported(CameraAppUiImpl.this.mCameraActivity.getParameters())) {
                            CameraAppUiImpl.this.mModePicker.setCurrentMode(10);
                        } else {
                            CameraAppUiImpl.this.mModePicker.setCurrentMode(8);
                        }
                        CameraAppUiImpl.this.setViewState(ICameraAppUi.ViewState.VIEW_STATE_PRE_RECORDING);
                        CameraAppUiImpl.this.mMainHandler.post(new Runnable() { // from class: com.android.camera.bridge.CameraAppUiImpl.2.1
                            @Override // java.lang.Runnable
                            public void run() {
                                ShutterButton.OnShutterButtonListener videoShutterButtonListener2 = CameraAppUiImpl.this.mCameraActivity.getCameraActor().getVideoShutterButtonListener();
                                if (videoShutterButtonListener2 != null) {
                                    videoShutterButtonListener2.onShutterButtonClick(shutterButton);
                                } else {
                                    Log.m34i("CameraAppUiImpl", "error video shutter listener is null");
                                }
                            }
                        });
                    }
                }
            }
        }
    };
    String zoom_x = "1.0x";

    /* renamed from: -getcom-mediatek-camera-ICameraMode$CameraModeTypeSwitchesValues, reason: not valid java name */
    private static /* synthetic */ int[] m243getcommediatekcameraICameraMode$CameraModeTypeSwitchesValues() {
        if (f85commediatekcameraICameraMode$CameraModeTypeSwitchesValues != null) {
            return f85commediatekcameraICameraMode$CameraModeTypeSwitchesValues;
        }
        int[] iArr = new int[ICameraMode.CameraModeType.valuesCustom().length];
        try {
            iArr[ICameraMode.CameraModeType.EXT_MODE_FACE_BEAUTY.ordinal()] = 1;
        } catch (NoSuchFieldError e) {
        }
        try {
            iArr[ICameraMode.CameraModeType.EXT_MODE_PANORAMA.ordinal()] = 2;
        } catch (NoSuchFieldError e2) {
        }
        try {
            iArr[ICameraMode.CameraModeType.EXT_MODE_PHOTO.ordinal()] = 3;
        } catch (NoSuchFieldError e3) {
        }
        try {
            iArr[ICameraMode.CameraModeType.EXT_MODE_PHOTO_PIP.ordinal()] = 27;
        } catch (NoSuchFieldError e4) {
        }
        try {
            iArr[ICameraMode.CameraModeType.EXT_MODE_PHOTO_STEREO.ordinal()] = 28;
        } catch (NoSuchFieldError e5) {
        }
        try {
            iArr[ICameraMode.CameraModeType.EXT_MODE_SLOW_MOTION.ordinal()] = 29;
        } catch (NoSuchFieldError e6) {
        }
        try {
            iArr[ICameraMode.CameraModeType.EXT_MODE_STEREO_CAMERA.ordinal()] = 30;
        } catch (NoSuchFieldError e7) {
        }
        try {
            iArr[ICameraMode.CameraModeType.EXT_MODE_VIDEO.ordinal()] = 4;
        } catch (NoSuchFieldError e8) {
        }
        try {
            iArr[ICameraMode.CameraModeType.EXT_MODE_VIDEO_PIP.ordinal()] = 5;
        } catch (NoSuchFieldError e9) {
        }
        try {
            iArr[ICameraMode.CameraModeType.EXT_MODE_VIDEO_STEREO.ordinal()] = 31;
        } catch (NoSuchFieldError e10) {
        }
        f85commediatekcameraICameraMode$CameraModeTypeSwitchesValues = iArr;
        return iArr;
    }

    /* renamed from: -getcom-mediatek-camera-platform-ICameraAppUi$ShutterButtonTypeSwitchesValues */
    private static /* synthetic */ int[] m13xf99319f4() {
        if (f60x84e7a650 != null) {
            return f60x84e7a650;
        }
        int[] iArr = new int[ICameraAppUi.ShutterButtonType.valuesCustom().length];
        try {
            iArr[ICameraAppUi.ShutterButtonType.SHUTTER_TYPE_CANCEL.ordinal()] = 1;
        } catch (NoSuchFieldError e) {
        }
        try {
            iArr[ICameraAppUi.ShutterButtonType.SHUTTER_TYPE_CANCEL_VIDEO.ordinal()] = 2;
        } catch (NoSuchFieldError e2) {
        }
        try {
            iArr[ICameraAppUi.ShutterButtonType.SHUTTER_TYPE_OK_CANCEL.ordinal()] = 3;
        } catch (NoSuchFieldError e3) {
        }
        try {
            iArr[ICameraAppUi.ShutterButtonType.SHUTTER_TYPE_PHOTO.ordinal()] = 4;
        } catch (NoSuchFieldError e4) {
        }
        try {
            iArr[ICameraAppUi.ShutterButtonType.SHUTTER_TYPE_PHOTO_VIDEO.ordinal()] = 5;
        } catch (NoSuchFieldError e5) {
        }
        try {
            iArr[ICameraAppUi.ShutterButtonType.SHUTTER_TYPE_SLOW_VIDEO.ordinal()] = 6;
        } catch (NoSuchFieldError e6) {
        }
        try {
            iArr[ICameraAppUi.ShutterButtonType.SHUTTER_TYPE_VIDEO.ordinal()] = 7;
        } catch (NoSuchFieldError e7) {
        }
        f60x84e7a650 = iArr;
        return iArr;
    }

    /* renamed from: -getcom-mediatek-camera-platform-ICameraAppUi$ViewStateSwitchesValues */
    private static /* synthetic */ int[] m14xb41e2b47() {
        if (f61x3202d3a3 != null) {
            return f61x3202d3a3;
        }
        int[] iArr = new int[ICameraAppUi.ViewState.valuesCustom().length];
        try {
            iArr[ICameraAppUi.ViewState.VIEW_STATE_CAMERA_CLOSED.ordinal()] = 1;
        } catch (NoSuchFieldError e) {
        }
        try {
            iArr[ICameraAppUi.ViewState.VIEW_STATE_CAMERA_OPENED.ordinal()] = 27;
        } catch (NoSuchFieldError e2) {
        }
        try {
            iArr[ICameraAppUi.ViewState.VIEW_STATE_CAPTURE.ordinal()] = 2;
        } catch (NoSuchFieldError e3) {
        }
        try {
            iArr[ICameraAppUi.ViewState.VIEW_STATE_CONTINUOUS_CAPTURE.ordinal()] = 3;
        } catch (NoSuchFieldError e4) {
        }
        try {
            iArr[ICameraAppUi.ViewState.VIEW_STATE_FOCUSING.ordinal()] = 4;
        } catch (NoSuchFieldError e5) {
        }
        try {
            iArr[ICameraAppUi.ViewState.VIEW_STATE_HIDE_ALL_VIEW.ordinal()] = 5;
        } catch (NoSuchFieldError e6) {
        }
        try {
            iArr[ICameraAppUi.ViewState.VIEW_STATE_LOMOEFFECT_SETTING.ordinal()] = 6;
        } catch (NoSuchFieldError e7) {
        }
        try {
            iArr[ICameraAppUi.ViewState.VIEW_STATE_NORMAL.ordinal()] = 7;
        } catch (NoSuchFieldError e8) {
        }
        try {
            iArr[ICameraAppUi.ViewState.VIEW_STATE_PICKING.ordinal()] = 8;
        } catch (NoSuchFieldError e9) {
        }
        try {
            iArr[ICameraAppUi.ViewState.VIEW_STATE_PRE_RECORDING.ordinal()] = 9;
        } catch (NoSuchFieldError e10) {
        }
        try {
            iArr[ICameraAppUi.ViewState.VIEW_STATE_RECORDING.ordinal()] = 10;
        } catch (NoSuchFieldError e11) {
        }
        try {
            iArr[ICameraAppUi.ViewState.VIEW_STATE_REVIEW.ordinal()] = 11;
        } catch (NoSuchFieldError e12) {
        }
        try {
            iArr[ICameraAppUi.ViewState.VIEW_STATE_SAVING.ordinal()] = 12;
        } catch (NoSuchFieldError e13) {
        }
        try {
            iArr[ICameraAppUi.ViewState.VIEW_STATE_SETTING.ordinal()] = 13;
        } catch (NoSuchFieldError e14) {
        }
        try {
            iArr[ICameraAppUi.ViewState.VIEW_STATE_SUB_SETTING.ordinal()] = 14;
        } catch (NoSuchFieldError e15) {
        }
        f61x3202d3a3 = iArr;
        return iArr;
    }

    public CameraAppUiImpl(CameraActivity cameraActivity) {
        Log.m31d("CameraAppUiImpl", "[CameraAppUiImpl] constructor... ");
        this.mCameraActivity = cameraActivity;
        this.mMainHandler = new MainHandler(cameraActivity.getMainLooper());
    }

    public void createCommonView() {
        this.mShutterManager = new ShutterManager(this.mCameraActivity);
        this.mInfoManager = new InfoManager(this.mCameraActivity);
        this.mRotateProgress = new RotateProgress(this.mCameraActivity);
        this.mRemainingManager = new RemainingManager(this.mCameraActivity);
        this.mPickerManager = new PickerManager(this.mCameraActivity);
        this.mIndicatorManager = new IndicatorManager(this.mCameraActivity);
        this.mReviewManager = new ReviewManager(this.mCameraActivity);
        this.mRotateDialog = new RotateDialog(this.mCameraActivity);
        this.mZoomManager = new ZoomManager(this.mCameraActivity);
        this.mThumbnailManager = new ThumbnailViewManager(this.mCameraActivity);
        this.mThumbnailManager.setShutterManager(this.mShutterManager);
        if (FeatureSwitcher.isVfbEnable()) {
            this.mFaceBeautyEntryView = new FaceBeautyEntryView(this.mCameraActivity);
        }
        this.mSettingManager = new SettingManager(this.mCameraActivity);
        this.mEffectManager = new EffectViewManager(this.mCameraActivity, this.mEffectListener);
        if (FeatureSwitcher.isSubSettingEnabled()) {
            this.mSubSettingManager = new SubSettingManager(this.mCameraActivity);
        }
        this.mIdManager = new IdManager(this.mCameraActivity);
    }

    public void initializeCommonView() {
        this.mModePicker = this.mCameraActivity.getModePicker();
        this.mEffectViewManager = this.mCameraActivity.getEffectViewManager();
        this.mCameraViewArray.put(ICameraAppUi.CommonUiType.SHUTTER, new CameraViewImpl(this.mShutterManager));
        this.mCameraViewArray.put(ICameraAppUi.CommonUiType.MODE_PICKER, new CameraViewImpl(this.mModePicker));
        this.mCameraViewArray.put(ICameraAppUi.CommonUiType.THUMBNAIL, new CameraViewImpl(this.mThumbnailManager));
        this.mCameraViewArray.put(ICameraAppUi.CommonUiType.PICKER, new CameraViewImpl(this.mPickerManager));
        this.mCameraViewArray.put(ICameraAppUi.CommonUiType.INDICATOR, new CameraViewImpl(this.mIndicatorManager));
        this.mCameraViewArray.put(ICameraAppUi.CommonUiType.REMAINING, new CameraViewImpl(this.mRemainingManager));
        this.mCameraViewArray.put(ICameraAppUi.CommonUiType.INFO, new CameraViewImpl(this.mInfoManager));
        this.mCameraViewArray.put(ICameraAppUi.CommonUiType.REVIEW, new CameraViewImpl(this.mReviewManager));
        this.mCameraViewArray.put(ICameraAppUi.CommonUiType.ROTATE_PROGRESS, new CameraViewImpl(this.mRotateProgress));
        this.mCameraViewArray.put(ICameraAppUi.CommonUiType.ROTATE_DIALOG, new CameraViewImpl(this.mRotateDialog));
        this.mCameraViewArray.put(ICameraAppUi.CommonUiType.ZOOM, new CameraViewImpl(this.mZoomManager));
        this.mCameraViewArray.put(ICameraAppUi.CommonUiType.SETTING, new CameraViewImpl(this.mSettingManager));
        if (this.mFaceBeautyEntryView != null) {
            this.mCameraViewArray.put(ICameraAppUi.CommonUiType.FACE_BEAUTY_ENTRY, new CameraViewImpl(this.mFaceBeautyEntryView));
        }
    }

    public void initializeAfterPreview() {
        this.mInfoManager.showText("1.0x");
        this.mInfoManager.show();
        this.mModePicker.setCurrentMode(this.mCameraActivity.getCurrentMode());
        this.mIdManager.hide();
    }

    @Override // com.mediatek.camera.platform.ICameraAppUi
    public ICameraView getCameraView(ICameraAppUi.CommonUiType commonUiType) {
        return this.mCameraViewArray.get(commonUiType);
    }

    public ImageView getVideoShutter() {
        return this.mShutterManager.getVideoShutter();
    }

    @Override // com.mediatek.camera.platform.ICameraAppUi
    public ImageView getPhotoShutter() {
        return this.mShutterManager.getPhotoShutter();
    }

    @Override // com.mediatek.camera.platform.ICameraAppUi
    public void switchShutterType(ICameraAppUi.ShutterButtonType shutterButtonType) {
        switch (m13xf99319f4()[shutterButtonType.ordinal()]) {
            case 1:
                this.mShutterManager.switchShutter(4);
                break;
            case 2:
                this.mShutterManager.switchShutter(5);
                break;
            case 3:
                this.mShutterManager.switchShutter(3);
                break;
            case 4:
                this.mShutterManager.switchShutter(1);
                break;
            case 5:
                this.mShutterManager.switchShutter(0);
                break;
            case 6:
                this.mShutterManager.switchShutter(6);
                break;
            case FrameMetricsAggregator.DELAY_INDEX /* 7 */:
                this.mShutterManager.switchShutter(2);
                break;
        }
    }

    @Override // com.mediatek.camera.platform.ICameraAppUi
    public ICameraAppUi.ShutterButtonType getShutterType() {
        int shutterType = this.mShutterManager.getShutterType();
        switch (shutterType) {
            case 0:
                return ICameraAppUi.ShutterButtonType.SHUTTER_TYPE_PHOTO_VIDEO;
            case 1:
                return ICameraAppUi.ShutterButtonType.SHUTTER_TYPE_PHOTO;
            case 2:
                return ICameraAppUi.ShutterButtonType.SHUTTER_TYPE_VIDEO;
            case 3:
                return ICameraAppUi.ShutterButtonType.SHUTTER_TYPE_OK_CANCEL;
            case 4:
                return ICameraAppUi.ShutterButtonType.SHUTTER_TYPE_CANCEL;
            case 5:
                return ICameraAppUi.ShutterButtonType.SHUTTER_TYPE_CANCEL_VIDEO;
            case 6:
                return ICameraAppUi.ShutterButtonType.SHUTTER_TYPE_SLOW_VIDEO;
            default:
                Log.m36w("CameraAppUiImpl", "[getShutterType] illegal type:" + shutterType);
                return null;
        }
    }

    @Override // com.mediatek.camera.platform.ICameraAppUi
    public void setCurrentMode(ICameraMode.CameraModeType cameraModeType) {
        Log.m31d("CameraAppUiImpl", "[setCurrentMode] mode = " + cameraModeType + ",curMode:" + this.mModePicker.getCurrentMode());
        int modePickerMode = getModePickerMode(cameraModeType);
        setPreViewBG();
        android.util.Log.d("zbx", "setCurrentMode: mode=" + cameraModeType);
        if (modePickerMode == this.mModePicker.getCurrentMode()) {
            return;
        }
        this.mModePicker.setEnabled(false);
        this.mModePicker.setCurrentMode(modePickerMode);
        this.mModePicker.setEnabled(true);
    }

    public void setPreViewBG() {
    }

    public void updateVideoIcon(int i) {
        this.mShutterManager.switchShutterMode(i);
    }

    @Override // com.mediatek.camera.platform.ICameraAppUi
    public void setThumbnailRefreshInterval(int i) {
        this.mThumbnailManager.setRefreshInterval(i);
    }

    public void forceThumbnailUpdate() {
        this.mThumbnailManager.forceUpdate();
    }

    public Uri getThumbnailUri() {
        return this.mThumbnailManager.getThumbnailUri();
    }

    public String getThumbnailMimeType() {
        return this.mThumbnailManager.getThumbnailMimeType();
    }

    @Override // com.mediatek.camera.platform.ICameraAppUi
    public void updateThumbnailViewWithYuv(byte[] bArr, int i, int i2, int i3, int i4) throws Throwable {
        this.mThumbnailManager.updateThumbnailViewWithYuv(bArr, i, i2, i3, i4);
    }

    public void setCameraId(int i) {
        this.mPickerManager.setCameraId(i);
    }

    @Override // com.mediatek.camera.platform.ICameraAppUi
    public void updateSnapShotUIView(boolean z) {
        this.mCameraActivity.showBorder(z);
        this.mZoomManager.setEnabled(!z);
        ShutterButton photoShutter = this.mShutterManager.getPhotoShutter();
        if (photoShutter != null) {
            photoShutter.setEnabled(!z);
        }
    }

    public void setDngState(String str) {
        this.mRemainingManager.setDngState(str);
    }

    public void showRemainHint() {
        this.mRemainingManager.showHint();
    }

    public void clearRemainAvaliableSpace() {
        this.mRemainingManager.clearAvaliableSpace();
    }

    public void notifyParametersReady() {
        Log.m31d("CameraAppUiImpl", "[notifyParametersReady]");
        if (ICameraAppUi.ViewState.VIEW_STATE_RECORDING == getViewState() || ICameraAppUi.ViewState.VIEW_STATE_PRE_RECORDING == getViewState()) {
            return;
        }
        if (isEffectConditionSatisfied()) {
            this.mEffectManager.show();
        } else {
            this.mEffectManager.hide();
        }
    }

    @Override // com.mediatek.camera.platform.ICameraAppUi
    public long updateRemainStorage() {
        return this.mRemainingManager.updateStorage();
    }

    public void setSettingCtrl(ISettingCtrl iSettingCtrl) {
        this.mSettingManager.setSettingController(iSettingCtrl);
        this.mShutterManager.setSettingController(iSettingCtrl);
        this.mIdManager.setSettingController(iSettingCtrl);
        if (FeatureSwitcher.isSubSettingEnabled()) {
            this.mSubSettingManager.setSettingController(iSettingCtrl);
        }
    }

    public void showText(CharSequence charSequence) {
        this.mInfoManager.showText(charSequence);
    }

    @Override // com.mediatek.camera.platform.ICameraAppUi
    public boolean collapseSetting(boolean z) {
        return this.mSettingManager.collapse(z);
    }

    @Override // com.mediatek.camera.platform.ICameraAppUi
    public boolean collapseSubSetting(boolean z) {
        return this.mSubSettingManager.collapse(z);
    }

    public boolean performSettingClick() {
        return this.mSettingManager.handleMenuEvent();
    }

    @Override // com.mediatek.camera.platform.ICameraAppUi
    public boolean isSettingShowing() {
        return this.mSettingManager.isShowSettingContainer();
    }

    public void setSettingListener(SettingManager.SettingListener settingListener) {
        this.mSettingManager.setListener(settingListener);
    }

    public void setSubSettingListener(SettingManager.SettingListener settingListener) {
        this.mSubSettingManager.setListener(settingListener);
    }

    @Override // com.mediatek.camera.platform.ICameraAppUi
    public void changeZoomForQuality() {
        this.mZoomManager.changeZoomForQuality();
    }

    @Override // com.mediatek.camera.platform.ICameraAppUi
    public void onDetectedSceneMode(int i, boolean z) {
        this.mIndicatorManager.onDetectedSceneMode(i);
        if (this.mCameraActivity.getISettingCtrl().getListPreference("pref_hdr_key") == null) {
            return;
        }
        if (z) {
            this.mPickerManager.forceEnable("pref_hdr_key");
            showToast(this.mCameraActivity.getString(R.string.asd_hdr_guide));
        } else {
            this.mPickerManager.cancelForcedEnable("pref_hdr_key");
            hideToast();
        }
    }

    @Override // com.mediatek.camera.platform.ICameraAppUi
    public void restoreSceneMode() {
        this.mPickerManager.cancelForcedEnable("pref_hdr_key");
        hideToast();
        this.mIndicatorManager.restoreSceneMode();
    }

    @Override // com.mediatek.camera.platform.ICameraAppUi
    public void setSwipeEnabled(boolean z) {
    }

    @Override // com.mediatek.camera.platform.ICameraAppUi
    public void setGestureListener(ICameraAppUi.GestureListener gestureListener) {
        this.mCameraActivity.setGestureListener(gestureListener);
    }

    @Override // com.mediatek.camera.platform.ICameraAppUi
    public ViewGroup getNormalViewLayer() {
        return this.mViewLayerNormal;
    }

    @Override // com.mediatek.camera.platform.ICameraAppUi
    public void changeBackToVFBModeStatues(boolean z) {
        Log.m31d("CameraAppUiImpl", "[changeBackToVFBModeStatues] isNeed = " + z);
        this.mIsNeedBackToVFBMode = z;
    }

    @Override // com.mediatek.camera.platform.ICameraAppUi
    public void updateFaceBeatuyEntryViewVisible(boolean z) {
        Log.m31d("CameraAppUiImpl", "[updateFaceBeatuyEntryViewVisible] visible = " + z + ",mIsInCameraPreview = " + this.mIsInCameraPreview + ",mIsNeedBackToVFBMode = " + this.mIsNeedBackToVFBMode);
        if (this.mFaceBeautyEntryView != null) {
            if (!z || !this.mIsInCameraPreview) {
                this.mFaceBeautyEntryView.hide();
            } else if (!this.mIsNeedBackToVFBMode) {
                this.mFaceBeautyEntryView.show();
            } else {
                changeBackToVFBModeStatues(false);
                setCurrentMode(ICameraMode.CameraModeType.EXT_MODE_FACE_BEAUTY);
            }
        }
    }

    @Override // com.mediatek.camera.platform.ICameraAppUi
    public void showRemaining() {
        this.mMainHandler.removeMessages(1000);
        this.mMainHandler.obtainMessage(1002, false).sendToTarget();
    }

    public void refreshModeRelated() {
        this.mModePicker.refresh();
        this.mPickerManager.refresh();
        this.mShutterManager.refresh();
    }

    public void refreshModeRelatedNoShutter() {
        this.mModePicker.refresh();
        this.mPickerManager.refresh();
        updateManagerIOS();
    }

    public void updateManagerIOS() {
        if (this.mCameraActivity == null) {
        }
        int currentWheelMode = this.mCameraActivity.getCurrentWheelMode();
        setRenXiangMode(currentWheelMode);
        switch (currentWheelMode) {
            case 4:
            case 6:
                showIdicatorMy();
                android.util.Log.d("CameraAppUiImpl", "xxxxx                  bb ");
                break;
            case 5:
            default:
                hideIdicatorMy();
                break;
        }
    }

    public void hideIdicatorMy() {
        this.mEffectManager.hideTest();
    }

    public void showIdicatorMy() {
        this.mEffectManager.showTest();
    }

    @Override // com.mediatek.camera.platform.ICameraAppUi
    public void setReviewListener(View.OnClickListener onClickListener, View.OnClickListener onClickListener2) {
        this.mReviewManager.setReviewListener(onClickListener, onClickListener2);
    }

    @Override // com.mediatek.camera.platform.ICameraAppUi
    public void showReview(String str, FileDescriptor fileDescriptor) {
        if (str != null) {
            setViewState(ICameraAppUi.ViewState.VIEW_STATE_REVIEW);
            this.mReviewManager.show(str);
        } else if (fileDescriptor != null) {
            setViewState(ICameraAppUi.ViewState.VIEW_STATE_REVIEW);
            this.mReviewManager.show(fileDescriptor);
        } else {
            setViewState(ICameraAppUi.ViewState.VIEW_STATE_REVIEW);
            this.mReviewManager.show();
        }
    }

    @Override // com.mediatek.camera.platform.ICameraAppUi
    public void hideReview() {
        this.mReviewManager.hide();
        restoreViewState();
    }

    @Override // com.mediatek.camera.platform.ICameraAppUi
    public void showProgress(String str) {
        setViewState(ICameraAppUi.ViewState.VIEW_STATE_SAVING);
        this.mRotateProgress.showProgress(str);
    }

    @Override // com.mediatek.camera.platform.ICameraAppUi
    public void dismissProgress() {
        this.mRotateProgress.hide();
    }

    @Override // com.mediatek.camera.platform.ICameraAppUi
    public void showInfo(String str) {
        showInfo(str, 3000);
    }

    @Override // com.mediatek.camera.platform.ICameraAppUi
    public void showInfo(CharSequence charSequence, int i) {
        this.mMainHandler.removeMessages(1000);
        doShowInfo(charSequence, i);
    }

    @Override // com.mediatek.camera.platform.ICameraAppUi
    public void dismissInfo() {
        this.mMainHandler.removeMessages(1000);
        this.mMainHandler.sendEmptyMessage(1000);
    }

    @Override // com.mediatek.camera.platform.ICameraAppUi
    public boolean collapseViewManager(boolean z) {
        boolean zCollapse = false;
        if (this.mRotateDialog.isShowing() && (!z)) {
            this.mRotateDialog.collapse(z);
            zCollapse = true;
        } else {
            Iterator<T> it = this.mViewManagers.iterator();
            while (it.hasNext()) {
                zCollapse = ((ViewManager) it.next()).collapse(z);
                if (!z && zCollapse) {
                    break;
                }
            }
        }
        Log.m31d("CameraAppUiImpl", "collapseViewManager(" + z + ") return " + zCollapse);
        return zCollapse;
    }

    @Override // com.mediatek.camera.platform.ICameraAppUi
    public void restoreViewState() {
        setViewState(ICameraAppUi.ViewState.VIEW_STATE_NORMAL);
    }

    @Override // com.mediatek.camera.platform.ICameraAppUi
    public void setPhotoShutterEnabled(boolean z) {
        this.mShutterManager.setPhotoShutterEnabled(z);
    }

    @Override // com.mediatek.camera.platform.ICameraAppUi
    public void updateVideoShutterStatues(boolean z) {
        this.mIsVideoShutterButtonEanble = z;
    }

    @Override // com.mediatek.camera.platform.ICameraAppUi
    public void setVideoShutterEnabled(boolean z) {
        this.mShutterManager.setVideoShutterEnabled(z ? this.mIsVideoShutterButtonEanble : false);
    }

    @Override // com.mediatek.camera.platform.ICameraAppUi
    public void setOkButtonEnabled(boolean z) {
        this.mShutterManager.setOkButtonEnabled(z);
    }

    @Override // com.mediatek.camera.platform.ICameraAppUi
    public int getUnCropHeight() {
        return this.mCameraActivity.getUnCropHeight();
    }

    @Override // com.mediatek.camera.platform.ICameraAppUi
    public int getUnCropWidth() {
        return this.mCameraActivity.getUnCropWidth();
    }

    @Override // com.mediatek.camera.platform.ICameraAppUi
    public void hideAllViews() {
        if (this.mCameraActivity.isNonePickIntent() && this.mCameraActivity.getParameters() != null) {
            this.mModePicker.hide();
            this.mThumbnailManager.hide();
        }
        this.mSettingManager.hide();
        this.mIndicatorManager.hide();
        this.mPickerManager.hide();
        this.mRemainingManager.hide();
        this.mEffectManager.hide();
        this.mMainHandler.removeMessages(1000);
        this.mShutterManager.setShutterTextVisible(false);
        this.mIdManager.hide();
        this.mZoomManager.hide();
    }

    @Override // com.mediatek.camera.platform.ICameraAppUi
    public void showAllViews() {
        if (this.mCameraActivity.isNonePickIntent() && this.mCameraActivity.getParameters() != null) {
            this.mModePicker.show();
            this.mThumbnailManager.show();
        }
        this.mSettingManager.show();
        this.mIndicatorManager.show();
        this.mPickerManager.show();
        this.mRemainingManager.show();
        if (isEffectConditionSatisfied()) {
            this.mEffectManager.show();
        }
        this.mShutterManager.setShutterTextVisible(true);
        this.mIdManager.onRefresh();
        this.mZoomManager.show();
    }

    @Override // com.mediatek.camera.platform.ICameraAppUi
    public void setViewState(ICameraAppUi.ViewState viewState) {
        Log.m31d("CameraAppUiImpl", "[setViewState],mCurrentViewState:" + this.mCurrentViewState + ",newState:" + viewState);
        if (this.mCurrentViewState == viewState) {
        }
        if (this.mSettingManager.isShowSettingContainer()) {
            viewState = ICameraAppUi.ViewState.VIEW_STATE_SETTING;
        } else if (FeatureSwitcher.isSubSettingEnabled() && this.mSubSettingManager.isShowSettingContainer()) {
            viewState = ICameraAppUi.ViewState.VIEW_STATE_SUB_SETTING;
        }
        if (viewState == ICameraAppUi.ViewState.VIEW_STATE_CAMERA_CLOSED) {
            this.mRestoreViewState = this.mCurrentViewState;
        }
        if (this.mCurrentViewState == ICameraAppUi.ViewState.VIEW_STATE_CAMERA_CLOSED && viewState != ICameraAppUi.ViewState.VIEW_STATE_CAMERA_OPENED) {
            Log.m31d("CameraAppUiImpl", "[setViewState] set restore view state mRestoreViewState:" + this.mRestoreViewState + ",state:" + viewState);
            this.mRestoreViewState = viewState;
            return;
        }
        if (viewState == ICameraAppUi.ViewState.VIEW_STATE_CAMERA_OPENED) {
            this.mCurrentViewState = this.mRestoreViewState;
            Log.m31d("CameraAppUiImpl", "[setViewState] view state:" + this.mCurrentViewState);
        } else {
            this.mCurrentViewState = viewState;
        }
        switch (m14xb41e2b47()[this.mCurrentViewState.ordinal()]) {
            case 1:
                setViewManagerEnable(false);
                this.mShutterManager.setEnabled(false);
                this.mIndicatorManager.hide();
                if (this.mFaceBeautyEntryView != null) {
                    this.mFaceBeautyEntryView.hide();
                }
                this.mMainHandler.removeMessages(1002);
                this.mPickerManager.show();
                break;
            case 2:
                this.mModePicker.hideToast();
                this.mSettingManager.collapse(true);
                if (FeatureSwitcher.isSubSettingEnabled()) {
                    this.mSubSettingManager.collapse(true);
                }
                setViewManagerEnable(false);
                this.mShutterManager.setEnabled(false);
                this.mPickerManager.show();
                break;
            case 3:
                this.mIndicatorManager.refresh();
                this.mModePicker.hideToast();
                this.mSettingManager.collapse(true);
                if (FeatureSwitcher.isSubSettingEnabled()) {
                    this.mSubSettingManager.collapse(true);
                }
                setViewManagerVisible(false);
                setViewManagerEnable(false);
                this.mShutterManager.setVideoShutterEnabled(false);
                this.mPickerManager.show();
                this.mShutterManager.setShutterTextVisible(false);
                this.mIdManager.hide();
                break;
            case 4:
                setViewManagerEnable(false);
                this.mPickerManager.show();
                break;
            case 5:
                hideAllViews();
                break;
            case 6:
                this.mModePicker.hide();
                this.mThumbnailManager.hide();
                this.mShutterManager.setShutterTextVisible(false);
                this.mThumbnailManager.hide();
                setViewManagerEnable(false);
                this.mEffectManager.setEnabled(true);
                this.mSettingManager.hide();
                this.mIndicatorManager.refresh();
                this.mShutterManager.setEnabled(false);
                this.mShutterManager.hide();
                hideToast();
                if (this.mFaceBeautyEntryView != null) {
                    this.mFaceBeautyEntryView.hide();
                }
                this.mPickerManager.hide();
                break;
            case FrameMetricsAggregator.DELAY_INDEX /* 7 */:
                setViewManagerVisible(true);
                setViewManagerEnable(true);
                this.mShutterManager.setEnabled(true);
                this.mShutterManager.setVideoShutterEnabled(this.mIsVideoShutterButtonEanble);
                this.mSettingManager.setFileter(true);
                this.mSettingManager.setAnimationEnabled(true, true);
                this.mShutterManager.setShutterTextVisible(true);
                this.mCameraActivity.onRecordingViewHide();
                if (FeatureSwitcher.isSubSettingEnabled()) {
                    this.mSubSettingManager.setFileter(true);
                    this.mSubSettingManager.setAnimationEnabled(true, true);
                }
                if ((!this.mCameraActivity.isVideoMode() || (!this.mCameraActivity.isNonePickIntent())) && this.mCameraActivity.isCameraOpened()) {
                    this.mPickerManager.show();
                }
                if (!this.mMainHandler.hasMessages(1000)) {
                    showIndicator(0);
                } else {
                    Log.m31d("CameraAppUiImpl", "[setViewState]mMainHandler has message MSG_SHOW_ONSCREEN_INDICATOR");
                }
                this.mPickerManager.show();
                break;
            case 8:
                this.mShutterManager.setEnabled(true);
                setViewManagerVisible(false);
                setViewManagerEnable(false);
                break;
            case 9:
                this.mModePicker.setEnabled(false);
                this.mPickerManager.setEnabled(false);
                this.mShutterManager.setEnabled(false);
                this.mSettingManager.setEnabled(false);
                this.mEffectManager.setEnabled(false);
                this.mModePicker.hide();
                this.mPickerManager.hide();
                this.mSettingManager.hide();
                this.mEffectManager.hide();
                this.mShutterManager.setShutterTextVisible(false);
                this.mIdManager.setEnabled(false);
                this.mIdManager.hide();
                this.mCameraActivity.onRecordingViewShow();
                break;
            case 10:
                this.mEffectManager.setEnabled(false);
                this.mModePicker.hideToast();
                this.mEffectManager.hide();
                this.mPickerManager.hide();
                this.mShutterManager.setEnabled(true);
                this.mShutterManager.setVideoShutterEnabled(this.mIsVideoShutterButtonEanble);
                this.mSettingManager.collapse(true);
                if (FeatureSwitcher.isSubSettingEnabled()) {
                    this.mSubSettingManager.collapse(true);
                }
                setViewManagerVisible(false);
                setViewManagerEnable(false);
                this.mShutterManager.setShutterTextVisible(false);
                this.mZoomManager.setEnabled(true);
                break;
            case 11:
                this.mShutterManager.setEnabled(true);
                setViewManagerVisible(false);
                setViewManagerEnable(false);
                this.mPickerManager.show();
                break;
            case 12:
                this.mModePicker.hideToast();
                this.mShutterManager.setEnabled(false);
                setViewManagerVisible(false);
                setViewManagerEnable(false);
                this.mPickerManager.show();
                break;
            case 13:
                this.mModePicker.hide();
                this.mThumbnailManager.hide();
                this.mPickerManager.hide();
                this.mEffectManager.hide();
                setViewManagerEnable(false);
                this.mSettingManager.setEnabled(true);
                this.mIndicatorManager.refresh();
                if (this.mFaceBeautyEntryView != null) {
                    this.mFaceBeautyEntryView.hide();
                }
                this.mPickerManager.show();
                break;
            case 14:
                this.mModePicker.hide();
                this.mThumbnailManager.hide();
                this.mPickerManager.hide();
                setViewManagerEnable(false);
                if (FeatureSwitcher.isSubSettingEnabled()) {
                    this.mSubSettingManager.setEnabled(true);
                }
                this.mPickerManager.show();
                break;
        }
    }

    @Override // com.mediatek.camera.platform.ICameraAppUi
    public ICameraAppUi.ViewState getViewState() {
        return this.mCurrentViewState;
    }

    @Override // com.mediatek.camera.platform.ICameraAppUi
    public ICameraView getCameraView(ICameraAppUi.SpecViewType specViewType) {
        return ViewFactory.getInstance().createViewManager(this.mCameraActivity, specViewType);
    }

    @Override // com.mediatek.camera.platform.ICameraAppUi
    public void showToastForShort(int i) {
        String string = this.mCameraActivity.getString(i);
        Log.m31d("CameraAppUiImpl", "[showToastForShort](" + string + ")");
        showToastForShort(string);
    }

    public void showToastForShort(String str) {
        Log.m31d("CameraAppUiImpl", "showToast(" + str + ")");
        if (str != null && this.mCameraActivity.isFullScreen()) {
            if (this.mRotateToast == null) {
                this.mRotateToast = OnScreenHint.makeText(this.mCameraActivity, str);
            } else {
                this.mRotateToast.setText(str);
            }
            this.mRotateToast.showToastForShort();
        }
    }

    @Override // com.mediatek.camera.platform.ICameraAppUi
    public void setVideoShutterMask(boolean z) {
        this.mShutterManager.setVideoShutterMask(z);
        this.mVideoShutterMask = z;
    }

    @Override // com.mediatek.camera.platform.ICameraAppUi
    public boolean isNormalViewState() {
        return this.mCurrentViewState == ICameraAppUi.ViewState.VIEW_STATE_NORMAL;
    }

    @Override // com.mediatek.camera.platform.ICameraAppUi
    public void setCamcorderProfile(CamcorderProfile camcorderProfile) {
        this.mRemainingManager.setCamcorderProfile(camcorderProfile);
    }

    @Override // com.mediatek.camera.platform.ICameraAppUi
    public void showToast(int i) {
        showToast(this.mCameraActivity.getString(i));
    }

    public void showToast(String str) {
        Log.m31d("CameraAppUiImpl", "showToast(" + str + ")");
        if (str != null && this.mCameraActivity.isFullScreen()) {
            if (this.mRotateToast == null) {
                this.mRotateToast = OnScreenHint.makeText(this.mCameraActivity, str);
            } else {
                this.mRotateToast.setText(str);
            }
            this.mRotateToast.showToast();
        }
    }

    public void showRemainingAways() {
        Log.m31d("CameraAppUiImpl", "[showRemainingAways]");
        this.mMainHandler.removeMessages(1000);
        this.mMainHandler.obtainMessage(1002, true).sendToTarget();
    }

    public void applayViewCallbacks() {
        this.mShutterManager.setShutterListener(this.mPhotoShutterListener, this.mVideoShutterListener, this.mCameraActivity.getCameraActor().getOkListener(), this.mCameraActivity.getCameraActor().getCancelListener());
    }

    public void clearViewCallbacks() {
        this.mShutterManager.setShutterListener(null, null, null, null);
    }

    public void resetSettings() {
        this.mSettingManager.resetSettings();
    }

    public void hideToast() {
        Log.m31d("CameraAppUiImpl", "[hideToast]");
        if (this.mRotateToast != null) {
            this.mRotateToast.cancel();
        }
    }

    public void showAlertDialog(String str, String str2, String str3, Runnable runnable, String str4, Runnable runnable2) {
        this.mRotateDialog.showAlertDialog(str, str2, str3, runnable, str4, runnable2);
    }

    public void showIndicator(int i) {
        Log.m31d("CameraAppUiImpl", "[showIndicator] (" + i + ")");
        this.mMainHandler.removeMessages(1000);
        if (i > 0) {
            this.mMainHandler.sendEmptyMessageDelayed(1000, i);
        } else {
            this.mMainHandler.sendEmptyMessage(1000);
        }
    }

    public boolean addViewManager(ViewManager viewManager) {
        if (!this.mViewManagers.contains(viewManager)) {
            return this.mViewManagers.add(viewManager);
        }
        return false;
    }

    public boolean removeViewManager(ViewManager viewManager) {
        return this.mViewManagers.remove(viewManager);
    }

    public void addFileSaver(FileSaver fileSaver) {
        this.mThumbnailManager.addFileSaver(fileSaver);
    }

    public void initializeViewGroup() {
        this.mViewLayerNormal = (ViewGroup) this.mCameraActivity.findViewById(R.id.view_layer_normal);
        this.mViewLayerTop = (ViewGroup) this.mCameraActivity.findViewById(R.id.view_layer_top);
        this.mViewLayerShutter = (ViewGroup) this.mCameraActivity.findViewById(R.id.view_layer_shutter);
        this.mViewLayerSetting = (ViewGroup) this.mCameraActivity.findViewById(R.id.view_layer_setting);
        this.mViewLayerOverlay = (ViewGroup) this.mCameraActivity.findViewById(R.id.view_layer_overlay);
        this.renxiangControlView = (RelativeLayout) this.mCameraActivity.findViewById(R.id.renxiang_layout);
        this.mRenWuMainView = (RenWuMainView) this.mCameraActivity.findViewById(R.id.mode_renxinag);
    }

    public void removeAllView() {
        if (this.mViewLayerNormal != null) {
            this.mViewLayerNormal.removeAllViews();
        }
        if (this.mViewLayerShutter != null) {
            this.mViewLayerShutter.removeAllViews();
        }
        if (this.mViewLayerSetting != null) {
            this.mViewLayerSetting.removeAllViews();
        }
        if (this.mViewLayerOverlay != null) {
            this.mViewLayerOverlay.removeAllViews();
        }
    }

    public void addView(View view, int i) {
        ViewGroup viewLayer = getViewLayer(i);
        if (viewLayer != null) {
            viewLayer.addView(view);
        }
    }

    public void removeView(View view, int i) {
        ViewGroup viewLayer = getViewLayer(i);
        if (viewLayer != null) {
            viewLayer.removeView(view);
        }
    }

    public View inflate(int i, int i2) {
        return this.mCameraActivity.getLayoutInflater().inflate(i, getViewLayer(i2), false);
    }

    public void resetZoom() {
        this.mZoomManager.resetZoom();
    }

    public void setZoomParameter() {
        this.mZoomManager.setZoomParameter();
    }

    public void checkViewManagerConfiguration() {
        Iterator<T> it = this.mViewManagers.iterator();
        while (it.hasNext()) {
            ((ViewManager) it.next()).checkConfiguration();
        }
    }

    public void onConfigurationChanged() {
        Iterator<T> it = this.mViewManagers.iterator();
        while (it.hasNext()) {
            ((ViewManager) it.next()).reInflate();
        }
        this.mRotateToast = null;
    }

    public void setPickerListener(PickerManager.PickerListener pickerListener) {
        this.mPickerManager.setListener(pickerListener);
    }

    public ShutterManager getShutterManager() {
        return this.mShutterManager;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void doShowIndicator() {
        Log.m31d("CameraAppUiImpl", "[doShowIndicator]");
        if (this.mCameraActivity.getCurrentWheelMode() == 5) {
            this.mInfoManager.hide();
        } else {
            this.mInfoManager.showText(this.zoom_x);
        }
        this.mRemainingManager.hide();
        if (this.mCurrentViewState == ICameraAppUi.ViewState.VIEW_STATE_NORMAL && ((!this.mCameraActivity.isVideoMode() || (!this.mCameraActivity.isNonePickIntent())) && this.mCameraActivity.isCameraOpened())) {
            this.mPickerManager.show();
        }
        if (this.mCurrentViewState != ICameraAppUi.ViewState.VIEW_STATE_SAVING) {
            this.mIndicatorManager.show();
        }
    }

    @Override // com.mediatek.camera.platform.ICameraAppUi
    public void updataVideoRecordingManager(boolean z) {
        this.mThumbnailManager.updateVideoRecordingBackground(z);
        this.mCameraActivity.updateCameraModeSwithView(!z);
        if (!z) {
            this.mIdManager.hide();
        } else {
            this.mIdManager.hide();
        }
        Log.m31d("CameraAppUiImpl", "[updataVideoRecordingManager] isVideoRecording:" + z);
        if (z) {
            this.mPickerManager.hide();
        } else {
            this.mPickerManager.show();
        }
    }

    private final class MainHandler extends Handler {
        public MainHandler(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            Log.m31d("CameraAppUiImpl", "msg id=" + message.what);
            switch (message.what) {
                case 1000:
                    CameraAppUiImpl.this.doShowIndicator();
                    break;
                case 1001:
                    CameraAppUiImpl.this.mRemainingManager.hide();
                    CameraAppUiImpl.this.mInfoManager.showText((CharSequence) message.obj);
                    CameraAppUiImpl.this.showIndicator(message.arg1);
                    break;
                case 1002:
                    CameraAppUiImpl.this.doShowRemaining(((Boolean) message.obj).booleanValue());
                    break;
            }
        }
    }

    private void setViewManagerVisible(boolean z) {
        if (z) {
            if (this.mCameraActivity.isNonePickIntent() && this.mCameraActivity.getParameters() != null) {
                this.mModePicker.show();
                this.mThumbnailManager.show();
                this.mZoomManager.show();
            }
            this.mShutterManager.show();
            this.mSettingManager.show();
            if (FeatureSwitcher.isSubSettingEnabled()) {
                this.mSubSettingManager.show();
            }
            if (isEffectConditionSatisfied()) {
                this.mEffectManager.show();
                return;
            }
            return;
        }
        this.mModePicker.hide();
        this.mPickerManager.hide();
        this.mSettingManager.hide();
        this.mZoomManager.hide();
        if (FeatureSwitcher.isSubSettingEnabled()) {
            this.mSubSettingManager.hide();
        }
        this.mEffectManager.hide();
        if (this.mFaceBeautyEntryView != null) {
            this.mFaceBeautyEntryView.hide();
        }
    }

    private void setViewManagerEnable(boolean z) {
        if (this.mCameraActivity.isNonePickIntent()) {
            if (!this.mCameraActivity.isModeChanged()) {
                this.mModePicker.setEnabled(z);
            }
            this.mThumbnailManager.setEnabled(z);
        }
        this.mSettingManager.setEnabled(z);
        this.mPickerManager.setEnabled(z);
        this.mIdManager.setEnabled(z);
        if (z) {
            this.mIdManager.hide();
        }
        this.mZoomManager.setEnabled(z);
        if (this.mFaceBeautyEntryView != null) {
            this.mFaceBeautyEntryView.setEnabled(z);
        }
        if (FeatureSwitcher.isSubSettingEnabled()) {
            this.mSubSettingManager.setEnabled(z);
        }
        this.mEffectManager.setEnabled(z);
    }

    private void doShowInfo(final CharSequence charSequence, final int i) {
        Log.m31d("CameraAppUiImpl", "doShowInfo(" + charSequence + ", " + i + ")");
        this.mCameraActivity.runOnUiThread(new Runnable() { // from class: com.android.camera.bridge.CameraAppUiImpl.3
            @Override // java.lang.Runnable
            public void run() {
                CameraAppUiImpl.this.mIndicatorManager.hide();
                CameraAppUiImpl.this.mRemainingManager.hide();
                CameraAppUiImpl.this.zoom_x = charSequence.toString();
                if (charSequence.toString().equals(CameraAppUiImpl.this.mCameraActivity.getResources().getString(R.string.count_down_title_text)) || CameraAppUiImpl.this.mCameraActivity.getCurrentWheelMode() == 5) {
                    CameraAppUiImpl.this.mInfoManager.hide();
                } else {
                    CameraAppUiImpl.this.mInfoManager.showText(CameraAppUiImpl.this.zoom_x);
                }
                if (i != -1) {
                    CameraAppUiImpl.this.showIndicator(i);
                }
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void doShowRemaining(boolean z) {
        Log.m31d("CameraAppUiImpl", "[doShowRemaining](" + z + ")");
    }

    private ViewGroup getViewLayer(int i) {
        Log.m31d("CameraAppUiImpl", "[getViewLayer] layer:" + i);
        switch (i) {
            case -1:
                return this.mViewLayerBottom;
            case 0:
                return this.mViewLayerNormal;
            case 1:
                return this.mViewLayerTop;
            case 2:
                return this.mViewLayerShutter;
            case 3:
                return this.mViewLayerSetting;
            case 4:
                return this.mViewLayerOverlay;
            default:
                throw new RuntimeException("Wrong layer:" + i);
        }
    }

    private boolean isEffectConditionSatisfied() {
        if (this.mCameraActivity.getISettingCtrl().getListPreference("pref_camera_coloreffect_key") == null || this.mCurrentViewState == ICameraAppUi.ViewState.VIEW_STATE_SETTING || !FeatureSwitcher.isLomoEffectEnabled() || 3 == this.mCameraActivity.getCurrentMode() || 8 == this.mCameraActivity.getCurrentMode()) {
            return false;
        }
        return true;
    }

    private class EffectListenerImpl implements EffectViewManager.EffectListener {
        /* synthetic */ EffectListenerImpl(CameraAppUiImpl cameraAppUiImpl, EffectListenerImpl effectListenerImpl) {
            this();
        }

        private EffectListenerImpl() {
        }

        @Override // com.android.camera.manager.EffectViewManager.EffectListener
        public boolean onClick() {
            CameraAppUiImpl.this.mCameraActivity.getModuleManager().onEffectClick();
            return true;
        }
    }

    private int getModePickerMode(ICameraMode.CameraModeType cameraModeType) {
        switch (m243getcommediatekcameraICameraMode$CameraModeTypeSwitchesValues()[cameraModeType.ordinal()]) {
        }
        return 0;
    }

    private class CameraViewImpl implements ICameraView {
        private ViewManager mViewManager;

        public CameraViewImpl(ViewManager viewManager) {
            this.mViewManager = viewManager;
        }

        @Override // com.mediatek.camera.platform.ICameraView
        public void init(Activity activity, ICameraAppUi iCameraAppUi, IModuleCtrl iModuleCtrl) {
        }

        @Override // com.mediatek.camera.platform.ICameraView
        public void show() {
            this.mViewManager.show();
        }

        @Override // com.mediatek.camera.platform.ICameraView
        public void hide() {
            this.mViewManager.hide();
        }

        @Override // com.mediatek.camera.platform.ICameraView
        public void uninit() {
        }

        @Override // com.mediatek.camera.platform.ICameraView
        public void reset() {
        }

        @Override // com.mediatek.camera.platform.ICameraView
        public boolean isShowing() {
            return this.mViewManager.isShowing();
        }

        @Override // com.mediatek.camera.platform.ICameraView
        public boolean update(int i, Object... objArr) {
            return true;
        }

        @Override // com.mediatek.camera.platform.ICameraView
        public void refresh() {
            this.mViewManager.refresh();
        }

        @Override // com.mediatek.camera.platform.ICameraView
        public void setEnabled(boolean z) {
            this.mViewManager.setEnabled(z);
        }

        @Override // com.mediatek.camera.platform.ICameraView
        public void onOrientationChanged(int i) {
        }

        @Override // com.mediatek.camera.platform.ICameraView
        public void setListener(Object obj) {
        }
    }

    public void setRenXiangMode(int i) {
        android.util.Log.d("zbx", "modeStatus=" + i);
        try {
            this.mRenWuMainView.setBottomViewDefault();
            if (i == 5) {
                this.renxiangControlView.setVisibility(0);
            } else {
                this.renxiangControlView.setVisibility(8);
            }
        } catch (Exception e) {
        }
    }

    public void getPerformZoom(int i, boolean z) {
        this.mZoomManager.performZoom(i, z);
    }

    public void updateManager() {
        Log.m31d("CameraAppUiImpl", "[updateManager]");
        int currentWheelMode = this.mCameraActivity.getCurrentWheelMode();
        if (this.zoom_x == null || TextUtils.isEmpty(this.zoom_x)) {
            this.zoom_x = "1.0x";
        }
        setRenXiangMode(currentWheelMode);
        switch (currentWheelMode) {
            case 4:
                this.mInfoManager.showText(this.zoom_x);
                break;
            case 5:
                this.mInfoManager.hide();
                break;
            case 6:
                this.mInfoManager.showText(this.zoom_x);
                break;
            case FrameMetricsAggregator.DELAY_INDEX /* 7 */:
                this.mInfoManager.showText(this.zoom_x);
                break;
            default:
                this.mInfoManager.showText(this.zoom_x);
                break;
        }
    }
}
