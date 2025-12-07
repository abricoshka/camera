package com.mediatek.camera.p005v2.module;

import android.app.Activity;
import android.graphics.RectF;
import com.mediatek.camera.R;
import com.mediatek.camera.debug.LogHelper;
import com.mediatek.camera.p005v2.control.ControlImpl;
import com.mediatek.camera.p005v2.control.IControl$IAaaController;
import com.mediatek.camera.p005v2.detection.IDetectionManager;
import com.mediatek.camera.p005v2.mode.AbstractCameraMode;
import com.mediatek.camera.p005v2.mode.ModeController;
import com.mediatek.camera.p005v2.mode.normal.CaptureMode;
import com.mediatek.camera.p005v2.mode.pip.PipMode;
import com.mediatek.camera.p005v2.module.ModuleListener;
import com.mediatek.camera.p005v2.p006ui.CountDownView;
import com.mediatek.camera.p005v2.platform.app.AppController;
import com.mediatek.camera.p005v2.platform.app.AppUi;
import com.mediatek.camera.p005v2.platform.module.ModuleController;
import com.mediatek.camera.p005v2.platform.module.ModuleUi;
import com.mediatek.camera.p005v2.services.CameraServices;
import com.mediatek.camera.p005v2.services.storage.IStorageService;
import com.mediatek.camera.p005v2.setting.ISettingServant;
import com.mediatek.camera.p005v2.setting.SettingCtrl;
import com.mediatek.camera.p005v2.stream.StreamManager;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;
import junit.framework.Assert;

/* loaded from: classes.dex */
public abstract class AbstractCameraModule implements ModuleController, ModuleListener, ISettingServant.ISettingChangedListener, IStorageService.IStorageStateListener, CountDownView.OnCountDownStatusListener, ModuleUi.PreviewAreaChangedListener {
    private static final LogHelper.Tag TAG = new LogHelper.Tag(AbstractCameraModule.class.getSimpleName());
    protected ControlImpl mAaaControl;
    protected AbstractModuleUi mAbstractModuleUI;
    protected final AppController mAppController;
    protected final AppUi mAppUi;
    protected String mCameraId;
    protected AbstractCameraMode mCurrentMode;
    protected int mCurrentModeIndex;
    protected IDetectionManager mDetectionManager;
    protected boolean mIsCaptureIntent;
    private ModeController.ModeGestureListener mModeGestureListener;
    protected int mOldModeIndex;
    protected boolean mPaused;
    private final CameraServices mServices;
    protected final SettingCtrl mSettingController;
    protected ISettingServant mSettingServant;
    protected IStorageService mStorageService;
    protected StreamManager mStreamManager;
    protected int mPendingSwitchCameraId = -1;
    protected boolean mPreviewSurfaceIsReadyForOpen = false;
    private final Semaphore mCameraOpenCloseLock = new Semaphore(1, true);
    private AppUi.ShutterEventsListener mPhotoShutterEventsListener = new AppUi.ShutterEventsListener() { // from class: com.mediatek.camera.v2.module.AbstractCameraModule.1
        @Override // com.mediatek.camera.v2.platform.app.AppUi.ShutterEventsListener
        public void onShutterReleased() {
            AbstractCameraModule.this.mCurrentMode.onShutterReleased(false);
        }

        @Override // com.mediatek.camera.v2.platform.app.AppUi.ShutterEventsListener
        public void onShutterPressed() {
            AbstractCameraModule.this.mCurrentMode.onShutterPressed(false);
        }

        @Override // com.mediatek.camera.v2.platform.app.AppUi.ShutterEventsListener
        public void onShutterLongPressed() {
            AbstractCameraModule.this.mCurrentMode.onShutterLongPressed(false);
        }

        @Override // com.mediatek.camera.v2.platform.app.AppUi.ShutterEventsListener
        public void onShutterClicked() {
            if (!AbstractCameraModule.this.checkSatisfyCaptureCondition()) {
                return;
            }
            String settingValue = AbstractCameraModule.this.mSettingServant.getSettingValue("pref_camera_self_timer_key");
            LogHelper.m26i(AbstractCameraModule.TAG, "seflTimer = " + settingValue);
            int iIntValue = Integer.valueOf(settingValue).intValue() / 1000;
            if (iIntValue > 0) {
                AbstractCameraModule.this.switchCommonUiByCountingDown(true);
                AbstractCameraModule.this.mAbstractModuleUI.setCountdownFinishedListener(AbstractCameraModule.this);
                AbstractCameraModule.this.mAbstractModuleUI.startCountdown(iIntValue);
                return;
            }
            AbstractCameraModule.this.mCurrentMode.onShutterClicked(false);
        }
    };
    private AppUi.ShutterEventsListener mVideoShutterEventsListener = new AppUi.ShutterEventsListener() { // from class: com.mediatek.camera.v2.module.AbstractCameraModule.2
        @Override // com.mediatek.camera.v2.platform.app.AppUi.ShutterEventsListener
        public void onShutterReleased() {
            AbstractCameraModule.this.mCurrentMode.onShutterReleased(true);
        }

        @Override // com.mediatek.camera.v2.platform.app.AppUi.ShutterEventsListener
        public void onShutterPressed() {
            AbstractCameraModule.this.mCurrentMode.onShutterPressed(true);
        }

        @Override // com.mediatek.camera.v2.platform.app.AppUi.ShutterEventsListener
        public void onShutterLongPressed() {
            AbstractCameraModule.this.mCurrentMode.onShutterLongPressed(true);
        }

        @Override // com.mediatek.camera.v2.platform.app.AppUi.ShutterEventsListener
        public void onShutterClicked() {
            AbstractCameraModule.this.mCurrentMode.onShutterClicked(true);
        }
    };
    private AppUi.OkCancelClickListener mOkCancelClickListener = new AppUi.OkCancelClickListener() { // from class: com.mediatek.camera.v2.module.AbstractCameraModule.3
        @Override // com.mediatek.camera.v2.platform.app.AppUi.OkCancelClickListener
        public void onOkClick() {
            LogHelper.m26i(AbstractCameraModule.TAG, "[onOkClick]");
            AbstractCameraModule.this.mCurrentMode.onOkClick();
        }

        @Override // com.mediatek.camera.v2.platform.app.AppUi.OkCancelClickListener
        public void onCancelClick() {
            LogHelper.m26i(AbstractCameraModule.TAG, "[onCancelClick]");
            AbstractCameraModule.this.mCurrentMode.onCancelClick();
        }
    };
    private AppUi.PlayButtonClickListener mPlayButtonClickListener = new AppUi.PlayButtonClickListener() { // from class: com.mediatek.camera.v2.module.AbstractCameraModule.4
        @Override // com.mediatek.camera.v2.platform.app.AppUi.PlayButtonClickListener
        public void onPlay() {
            LogHelper.m26i(AbstractCameraModule.TAG, "[onPlay]");
            AbstractCameraModule.this.mCurrentMode.onPlay();
        }
    };
    private AppUi.RetakeButtonClickListener mRetakeButtonClickListener = new AppUi.RetakeButtonClickListener() { // from class: com.mediatek.camera.v2.module.AbstractCameraModule.5
        @Override // com.mediatek.camera.v2.platform.app.AppUi.RetakeButtonClickListener
        public void onRetake() {
            LogHelper.m26i(AbstractCameraModule.TAG, "[onRetake]");
            AbstractCameraModule.this.mCurrentMode.onRetake();
        }
    };

    public AbstractCameraModule(AppController appController) {
        Assert.assertNotNull(appController);
        this.mAppController = appController;
        this.mAppUi = appController.getCameraAppUi();
        this.mServices = appController.getServices();
        this.mSettingController = this.mServices.getSettingController();
        this.mStorageService = this.mServices.getStorageService();
        this.mSettingServant = this.mSettingController.getSettingServant(null);
        this.mStreamManager = StreamManager.getInstance(appController.getActivity());
        this.mCurrentModeIndex = appController.getCurrentModeIndex();
        this.mOldModeIndex = appController.getOldModeIndex();
        createCurrentMode(this.mCurrentModeIndex);
    }

    @Override // com.mediatek.camera.p005v2.platform.module.ModuleController
    public void open(Activity activity, boolean z, boolean z2) {
        this.mIsCaptureIntent = z2;
        if (this.mIsCaptureIntent) {
            this.mAppUi.setSwipeEnabled(false);
        }
        this.mAaaControl.open(activity, this.mAppUi.getModuleLayoutRoot(), z2);
        this.mDetectionManager.open(activity, this.mAppUi.getModuleLayoutRoot(), z2);
        this.mStreamManager.open(this.mAppUi.getModuleLayoutRoot(), z2);
        this.mCurrentMode.open(this.mStreamManager, this.mAppUi.getModuleLayoutRoot(), z2);
        this.mSettingServant.registerSettingChangedListener(this, null, 0);
        this.mAppUi.setShutterEventListener(this.mPhotoShutterEventsListener, false);
        this.mAppUi.setShutterEventListener(this.mVideoShutterEventsListener, true);
        this.mAppUi.setOkCancelClickListener(this.mOkCancelClickListener);
        this.mAppUi.setPlayButtonClickListener(this.mPlayButtonClickListener);
        this.mAppUi.setRetakeButtonClickListener(this.mRetakeButtonClickListener);
        doModeChange(this.mOldModeIndex, this.mCurrentModeIndex);
    }

    @Override // com.mediatek.camera.p005v2.platform.module.ModuleController
    public void close() {
        this.mAaaControl.close();
        this.mDetectionManager.close();
        this.mStreamManager.close(this.mAppController.getActivity());
        this.mCurrentMode.close();
        this.mOldModeIndex = this.mCurrentModeIndex;
        this.mSettingServant.unRegisterSettingChangedListener(this);
    }

    @Override // com.mediatek.camera.p005v2.platform.module.ModuleController
    public void onBeforeCameraPicked(String str) {
    }

    @Override // com.mediatek.camera.p005v2.platform.module.ModuleController
    public void onCameraPicked(String str) {
    }

    @Override // com.mediatek.camera.p005v2.platform.module.ModuleController
    public void resume() {
        this.mPaused = false;
        this.mAaaControl.resume();
        this.mDetectionManager.resume();
        this.mCurrentMode.resume();
        this.mStorageService.registerStorageStateListener(this);
    }

    @Override // com.mediatek.camera.p005v2.platform.module.ModuleController
    public void pause() {
        if (this.mAbstractModuleUI.isCountingDown()) {
            this.mAbstractModuleUI.cancelCountDown();
            switchCommonUiByCountingDown(false);
        }
        this.mPaused = true;
        this.mAaaControl.pause();
        this.mDetectionManager.pause();
        this.mCurrentMode.pause();
        this.mStorageService.unRegisterStorageStateListener(this);
    }

    @Override // com.mediatek.camera.v2.services.storage.IStorageService.IStorageStateListener
    public void onStorageStateChanged(int i) {
        LogHelper.m26i(TAG, "onStorageStateChange storageState = " + i);
        switch (i) {
            case 0:
                this.mCurrentMode.onMediaEjected();
                this.mAppController.updateStorageSpaceAndHint();
                this.mAppUi.forceUpdateThumbnail();
                break;
            case 1:
                this.mAppController.updateStorageSpaceAndHint();
                this.mAppUi.forceUpdateThumbnail();
                break;
            case 2:
                this.mAppUi.showHint(this.mAppController.getActivity().getResources().getString(R.string.wait));
                break;
        }
    }

    @Override // com.mediatek.camera.v2.platform.module.ModuleUi.PreviewAreaChangedListener
    public void onPreviewAreaChanged(RectF rectF) {
        this.mDetectionManager.onPreviewAreaChanged(rectF);
        this.mAaaControl.onPreviewAreaChanged(rectF);
        this.mAbstractModuleUI.onPreviewAreaChanged(rectF);
        this.mCurrentMode.onPreviewAreaChanged(rectF);
    }

    @Override // com.mediatek.camera.p005v2.platform.module.ModuleController
    public void onPreviewVisibilityChanged(int i) {
        this.mCurrentMode.onPreviewVisibilityChanged(i);
    }

    @Override // com.mediatek.camera.p005v2.platform.module.ModuleController
    public boolean onBackPressed() {
        if (!this.mAbstractModuleUI.isCountingDown()) {
            return this.mCurrentMode.onBackPressed();
        }
        this.mAbstractModuleUI.cancelCountDown();
        switchCommonUiByCountingDown(false);
        return true;
    }

    @Override // com.mediatek.camera.v2.setting.ISettingServant.ISettingChangedListener
    public void onSettingChanged(Map<String, String> map) {
        LogHelper.m26i(TAG, "[onSettingChanged]+ ");
        LogHelper.m26i(TAG, "[onSettingChanged]-");
    }

    @Override // com.mediatek.camera.v2.ui.CountDownView.OnCountDownStatusListener
    public void onRemainingSecondsChanged(int i) {
        if (i == 1) {
            this.mServices.getSoundPlayback().play(R.raw.timer_final_second, 0.6f);
        } else if (i == 2 || i == 3) {
            this.mServices.getSoundPlayback().play(R.raw.timer_increment, 0.6f);
        }
    }

    @Override // com.mediatek.camera.v2.ui.CountDownView.OnCountDownStatusListener
    public void onCountDownFinished() {
        if (checkSatisfyCaptureCondition()) {
            switchCommonUiByCountingDown(false);
            this.mCurrentMode.onShutterClicked(false);
        }
    }

    @Override // com.mediatek.camera.p005v2.module.ModuleListener
    public synchronized void onPreviewSurfaceReady() {
        this.mPreviewSurfaceIsReadyForOpen = true;
    }

    @Override // com.mediatek.camera.p005v2.module.ModuleListener
    public void requestChangeCaptureRequets(boolean z, ModuleListener.RequestType requestType, ModuleListener.CaptureType captureType) {
    }

    @Override // com.mediatek.camera.p005v2.module.ModuleListener
    public void requestChangeCaptureRequets(boolean z, boolean z2, ModuleListener.RequestType requestType, ModuleListener.CaptureType captureType) {
    }

    @Override // com.mediatek.camera.p005v2.module.ModuleListener
    public void requestChangeSessionOutputs(boolean z) {
    }

    @Override // com.mediatek.camera.p005v2.module.ModuleListener
    public void requestChangeSessionOutputs(boolean z, boolean z2) {
    }

    @Override // com.mediatek.camera.p005v2.module.ModuleListener
    public IControl$IAaaController get3AController(String str) {
        return null;
    }

    public boolean onDown(float f, float f2) {
        LogHelper.m26i(TAG, "onDown " + this + " x:" + f + " y:" + f2 + " mModeGestureListener : " + this.mModeGestureListener);
        if (this.mModeGestureListener != null) {
            return this.mModeGestureListener.onDown(f, f2);
        }
        return false;
    }

    public boolean onUp() {
        if (this.mModeGestureListener != null) {
            return this.mModeGestureListener.onUp();
        }
        return false;
    }

    public boolean onScroll(float f, float f2, float f3, float f4) {
        if (this.mModeGestureListener != null) {
            return this.mModeGestureListener.onScroll(f, f2, f3, f4);
        }
        return false;
    }

    public boolean onSingleTapUp(float f, float f2) {
        if (this.mModeGestureListener != null) {
            return this.mModeGestureListener.onSingleTapUp(f, f2);
        }
        return false;
    }

    public boolean onLongPress(float f, float f2) {
        if (this.mModeGestureListener != null) {
            return this.mModeGestureListener.onLongPress(f, f2);
        }
        return false;
    }

    protected void switchToNewMode(int i) {
        LogHelper.m23d(TAG, "switchToNewMode old --> new : " + this.mCurrentModeIndex + " --> " + i);
        if (this.mCurrentModeIndex != i) {
            closeMode(this.mCurrentMode);
            createCurrentMode(i);
            this.mOldModeIndex = this.mCurrentModeIndex;
            this.mCurrentModeIndex = i;
            openMode(this.mCurrentMode);
        }
    }

    protected void closeMode(AbstractCameraMode abstractCameraMode) {
    }

    protected void createCurrentMode(int i) {
        LogHelper.m26i(TAG, "[createCurrentMode]+ modeIndex: " + i);
        switch (i) {
            case 0:
                this.mCurrentMode = new CaptureMode(this.mAppController, this);
                break;
            case 1:
            case 2:
            default:
                this.mCurrentMode = new CaptureMode(this.mAppController, this);
                break;
            case 3:
                this.mCurrentMode = new PipMode(this.mAppController, this);
                break;
        }
        this.mModeGestureListener = this.mCurrentMode.getModeGestureListener();
        LogHelper.m26i(TAG, "[createCurrentMode]- mModeGestureListener: " + this.mModeGestureListener);
    }

    protected void doModeChange(int i, int i2) {
        LogHelper.m23d(TAG, "[doModeChange], oldModeIndex:" + i + ", newModeIndex:" + i2);
        HashMap map = new HashMap();
        switch (i) {
            case 3:
                map.put("photo_pip_key", "off");
                break;
        }
        switch (i2) {
            case 3:
                map.put("photo_pip_key", "on");
                break;
        }
        LogHelper.m23d(TAG, "[doModeChange], changedModes:" + map);
        this.mSettingController.doSettingChange(map);
    }

    protected void showErrorAndFinish(int i) {
        if (1 == i || 2 == i) {
            this.mAppController.showErrorAndFinish(R.string.cannot_connect_camera_new);
        } else if (3 == i || 4 == i || 5 == i) {
            this.mAppController.showErrorAndFinish(R.string.camera_disabled);
        }
    }

    protected void acquireOpenCloseLock() throws InterruptedException {
        try {
            this.mCameraOpenCloseLock.acquire();
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted while waiting to acquire camera-open lock.", e);
        }
    }

    protected void releaseOpenCloseLock() {
        this.mCameraOpenCloseLock.release();
    }

    protected void openMode(AbstractCameraMode abstractCameraMode) {
    }

    protected boolean checkSatisfyCaptureCondition() {
        return true;
    }

    protected void switchCommonUiByCountingDown(boolean z) {
        if (z) {
            this.mAppUi.setShutterButtonEnabled(false, false);
            this.mAppUi.setShutterButtonEnabled(false, true);
            this.mAppUi.setSwipeEnabled(false);
            this.mAppUi.hideModeOptionsUi();
            this.mAppUi.hideSettingUi();
            this.mAppUi.hidePickerManagerUi();
            this.mAppUi.hideThumbnailManagerUi();
            this.mAppUi.hideIndicatorManagerUi();
            return;
        }
        this.mAppUi.setShutterButtonEnabled(true, false);
        this.mAppUi.setShutterButtonEnabled(true, true);
        this.mAppUi.setSwipeEnabled(true);
        this.mAppUi.showModeOptionsUi();
        this.mAppUi.showSettingUi();
        this.mAppUi.showPickerManagerUi();
        this.mAppUi.showThumbnailManagerUi();
        this.mAppUi.showIndicatorManagerUi();
    }
}
