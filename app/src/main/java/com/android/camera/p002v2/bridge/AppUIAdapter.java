package com.android.camera.p002v2.bridge;

import android.graphics.Bitmap;
import android.widget.FrameLayout;
import com.android.camera.p002v2.app.AppController;
import com.android.camera.p002v2.app.CameraAppUI;
import com.mediatek.camera.p005v2.platform.app.AppUi;

/* loaded from: classes.dex */
public class AppUIAdapter implements AppUi {
    private final AppController mAppController;
    private final CameraAppUI mCameraAppUi;
    private OkCancelClickAdapter mOkCancelClickAdapter;
    private ShutterEventAdapter mPhotoShutterEventAdapter;
    private PlayButtonClickAdapter mPlayButtonClickAdapter;
    private RetakeButtonClickAdapter mRetakeButtonClickAdapter;
    private ShutterEventAdapter mVideoShutterEventAdapter;

    public AppUIAdapter(AppController appController) {
        this.mAppController = appController;
        this.mCameraAppUi = appController.getCameraAppUI();
    }

    @Override // com.mediatek.camera.p005v2.platform.app.AppUi
    public FrameLayout getModuleLayoutRoot() {
        return this.mAppController.getModuleLayoutRoot();
    }

    @Override // com.mediatek.camera.p005v2.platform.app.AppUi
    public void setSwipeEnabled(boolean z) {
        this.mCameraAppUi.setSwipeEnabled(z);
    }

    @Override // com.mediatek.camera.p005v2.platform.app.AppUi
    public void setAllCommonViewEnable(boolean z) {
        this.mCameraAppUi.setAllCommonViewEnable(z);
    }

    @Override // com.mediatek.camera.p005v2.platform.app.AppUi
    public void setShutterEventListener(AppUi.ShutterEventsListener shutterEventsListener, boolean z) {
        if (z) {
            this.mVideoShutterEventAdapter = new ShutterEventAdapter(shutterEventsListener);
            this.mAppController.setShutterEventListener(this.mVideoShutterEventAdapter, true);
        } else {
            this.mPhotoShutterEventAdapter = new ShutterEventAdapter(shutterEventsListener);
            this.mAppController.setShutterEventListener(this.mPhotoShutterEventAdapter, false);
        }
    }

    @Override // com.mediatek.camera.p005v2.platform.app.AppUi
    public void setOkCancelClickListener(AppUi.OkCancelClickListener okCancelClickListener) {
        if (this.mOkCancelClickAdapter == null) {
            this.mOkCancelClickAdapter = new OkCancelClickAdapter(okCancelClickListener);
        }
        this.mAppController.setOkCancelClickListener(this.mOkCancelClickAdapter);
    }

    @Override // com.mediatek.camera.p005v2.platform.app.AppUi
    public void setShutterButtonEnabled(boolean z, boolean z2) {
        this.mAppController.setShutterButtonEnabled(z, z2);
    }

    @Override // com.mediatek.camera.p005v2.platform.app.AppUi
    public void switchShutterButtonImageResource(int i, boolean z) {
        this.mCameraAppUi.switchShutterButtonImageResource(i, z);
    }

    @Override // com.mediatek.camera.p005v2.platform.app.AppUi
    public void switchShutterButtonLayout(int i) {
        this.mCameraAppUi.switchShutterButtonLayout(i);
    }

    @Override // com.mediatek.camera.p005v2.platform.app.AppUi
    public void setPlayButtonClickListener(AppUi.PlayButtonClickListener playButtonClickListener) {
        if (this.mPlayButtonClickAdapter == null) {
            this.mPlayButtonClickAdapter = new PlayButtonClickAdapter(playButtonClickListener);
        }
        this.mAppController.setPlayButtonClickListener(this.mPlayButtonClickAdapter);
    }

    @Override // com.mediatek.camera.p005v2.platform.app.AppUi
    public void setRetakeButtonClickListener(AppUi.RetakeButtonClickListener retakeButtonClickListener) {
        if (this.mRetakeButtonClickAdapter == null) {
            this.mRetakeButtonClickAdapter = new RetakeButtonClickAdapter(retakeButtonClickListener);
        }
        this.mAppController.setRetakeButtonClickListener(this.mRetakeButtonClickAdapter);
    }

    @Override // com.mediatek.camera.p005v2.platform.app.AppUi
    public void stopShowCommonUI(boolean z) {
        this.mCameraAppUi.stopShowCommonUI(z);
    }

    @Override // com.mediatek.camera.p005v2.platform.app.AppUi
    public void showSettingUi() {
        this.mAppController.getCameraAppUI().showSettingUi();
    }

    @Override // com.mediatek.camera.p005v2.platform.app.AppUi
    public void hideSettingUi() {
        this.mCameraAppUi.hideSettingUi();
    }

    @Override // com.mediatek.camera.p005v2.platform.app.AppUi
    public void showModeOptionsUi() {
        this.mCameraAppUi.showModeOptionsUi();
    }

    @Override // com.mediatek.camera.p005v2.platform.app.AppUi
    public void hideModeOptionsUi() {
        this.mCameraAppUi.hideModeOptionsUi();
    }

    @Override // com.mediatek.camera.p005v2.platform.app.AppUi
    public void showPickerManagerUi() {
        this.mCameraAppUi.showPickerManagerUi();
    }

    @Override // com.mediatek.camera.p005v2.platform.app.AppUi
    public void hidePickerManagerUi() {
        this.mCameraAppUi.hidePickerManagerUi();
    }

    @Override // com.mediatek.camera.p005v2.platform.app.AppUi
    public void performCameraPickerBtnClick(boolean z) {
        this.mCameraAppUi.performCameraPickerBtnClick(z);
    }

    @Override // com.mediatek.camera.p005v2.platform.app.AppUi
    public void showThumbnailManagerUi() {
        this.mCameraAppUi.showThumbnailManagerUi();
    }

    @Override // com.mediatek.camera.p005v2.platform.app.AppUi
    public void hideThumbnailManagerUi() {
        this.mCameraAppUi.hideThumbnailManagerUi();
    }

    @Override // com.mediatek.camera.p005v2.platform.app.AppUi
    public void forceUpdateThumbnail() {
        this.mCameraAppUi.forceUpdateThumbnail();
    }

    @Override // com.mediatek.camera.p005v2.platform.app.AppUi
    public void setThumbnailManagerEnable(boolean z) {
        this.mCameraAppUi.setThumbnailManagerEnable(z);
    }

    @Override // com.mediatek.camera.p005v2.platform.app.AppUi
    public void showIndicatorManagerUi() {
        this.mCameraAppUi.showIndicatorManagerUi();
    }

    @Override // com.mediatek.camera.p005v2.platform.app.AppUi
    public void hideIndicatorManagerUi() {
        this.mCameraAppUi.hideIndicatorManagerUi();
    }

    @Override // com.mediatek.camera.p005v2.platform.app.AppUi
    public void showInfo(CharSequence charSequence, int i) {
        this.mCameraAppUi.showInfo(charSequence, i);
    }

    @Override // com.mediatek.camera.p005v2.platform.app.AppUi
    public void dismissInfo(boolean z) {
        this.mCameraAppUi.dismissInfo(z);
    }

    @Override // com.mediatek.camera.p005v2.platform.app.AppUi
    public void showHint(String str) {
        this.mCameraAppUi.showHint(str);
    }

    @Override // com.mediatek.camera.p005v2.platform.app.AppUi
    public void hideHint() {
        this.mCameraAppUi.hideHint();
    }

    @Override // com.mediatek.camera.p005v2.platform.app.AppUi
    public void showSavingProgress(String str) {
        this.mCameraAppUi.showSavingProgress(str);
    }

    @Override // com.mediatek.camera.p005v2.platform.app.AppUi
    public void dismissSavingProgress() {
        this.mCameraAppUi.dismissSavingProgress();
    }

    @Override // com.mediatek.camera.p005v2.platform.app.AppUi
    public void showReviewView(Bitmap bitmap) {
        this.mCameraAppUi.showReviewManager(bitmap);
    }

    @Override // com.mediatek.camera.p005v2.platform.app.AppUi
    public void showReviewView(byte[] bArr, int i) {
        this.mCameraAppUi.showReviewManager(bArr, i);
    }

    @Override // com.mediatek.camera.p005v2.platform.app.AppUi
    public void hideReviewView() {
        this.mCameraAppUi.hideReviewManager();
    }

    @Override // com.mediatek.camera.p005v2.platform.app.AppUi
    public void showLeftCounts(int i, boolean z) {
        this.mCameraAppUi.showLeftCounts(i, z);
    }

    @Override // com.mediatek.camera.p005v2.platform.app.AppUi
    public void showLeftTime(long j) {
        this.mCameraAppUi.showLeftTime(j);
    }

    @Override // com.mediatek.camera.p005v2.platform.app.AppUi
    public void updateAsdDetectedScene(String str) {
        this.mCameraAppUi.updateAsdDetectedScene(str);
    }

    private class ShutterEventAdapter implements AppController.ShutterEventsListener {
        private final AppUi.ShutterEventsListener mShutterEventListener;

        public ShutterEventAdapter(AppUi.ShutterEventsListener shutterEventsListener) {
            this.mShutterEventListener = shutterEventsListener;
        }

        @Override // com.android.camera.v2.app.AppController.ShutterEventsListener
        public void onShutterPressed() {
            this.mShutterEventListener.onShutterPressed();
        }

        @Override // com.android.camera.v2.app.AppController.ShutterEventsListener
        public void onShutterReleased() {
            this.mShutterEventListener.onShutterReleased();
        }

        @Override // com.android.camera.v2.app.AppController.ShutterEventsListener
        public void onShutterClicked() {
            this.mShutterEventListener.onShutterClicked();
        }

        @Override // com.android.camera.v2.app.AppController.ShutterEventsListener
        public void onShutterLongPressed() {
            this.mShutterEventListener.onShutterLongPressed();
        }
    }

    private class OkCancelClickAdapter implements AppController.OkCancelClickListener {
        private AppUi.OkCancelClickListener mOkCancelClickListener;

        public OkCancelClickAdapter(AppUi.OkCancelClickListener okCancelClickListener) {
            this.mOkCancelClickListener = okCancelClickListener;
        }

        @Override // com.android.camera.v2.app.AppController.OkCancelClickListener
        public void onOkClick() {
            this.mOkCancelClickListener.onOkClick();
        }

        @Override // com.android.camera.v2.app.AppController.OkCancelClickListener
        public void onCancelClick() {
            this.mOkCancelClickListener.onCancelClick();
        }
    }

    private class PlayButtonClickAdapter implements AppController.PlayButtonClickListener {
        private AppUi.PlayButtonClickListener mPlayButtonClickListener;

        public PlayButtonClickAdapter(AppUi.PlayButtonClickListener playButtonClickListener) {
            this.mPlayButtonClickListener = playButtonClickListener;
        }

        @Override // com.android.camera.v2.app.AppController.PlayButtonClickListener
        public void onPlay() {
            this.mPlayButtonClickListener.onPlay();
        }
    }

    private class RetakeButtonClickAdapter implements AppController.RetakeButtonClickListener {
        private AppUi.RetakeButtonClickListener mRetakeButtonClickListener;

        public RetakeButtonClickAdapter(AppUi.RetakeButtonClickListener retakeButtonClickListener) {
            this.mRetakeButtonClickListener = retakeButtonClickListener;
        }

        @Override // com.android.camera.v2.app.AppController.RetakeButtonClickListener
        public void onRetake() {
            this.mRetakeButtonClickListener.onRetake();
        }
    }
}
