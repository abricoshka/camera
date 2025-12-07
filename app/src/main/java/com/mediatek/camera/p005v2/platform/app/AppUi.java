package com.mediatek.camera.p005v2.platform.app;

import android.graphics.Bitmap;
import android.widget.FrameLayout;

/* loaded from: classes.dex */
public interface AppUi {

    public interface OkCancelClickListener {
        void onCancelClick();

        void onOkClick();
    }

    public interface PlayButtonClickListener {
        void onPlay();
    }

    public interface RetakeButtonClickListener {
        void onRetake();
    }

    public interface ShutterEventsListener {
        void onShutterClicked();

        void onShutterLongPressed();

        void onShutterPressed();

        void onShutterReleased();
    }

    void dismissInfo(boolean z);

    void dismissSavingProgress();

    void forceUpdateThumbnail();

    FrameLayout getModuleLayoutRoot();

    void hideHint();

    void hideIndicatorManagerUi();

    void hideModeOptionsUi();

    void hidePickerManagerUi();

    void hideReviewView();

    void hideSettingUi();

    void hideThumbnailManagerUi();

    void performCameraPickerBtnClick(boolean z);

    void setAllCommonViewEnable(boolean z);

    void setOkCancelClickListener(OkCancelClickListener okCancelClickListener);

    void setPlayButtonClickListener(PlayButtonClickListener playButtonClickListener);

    void setRetakeButtonClickListener(RetakeButtonClickListener retakeButtonClickListener);

    void setShutterButtonEnabled(boolean z, boolean z2);

    void setShutterEventListener(ShutterEventsListener shutterEventsListener, boolean z);

    void setSwipeEnabled(boolean z);

    void setThumbnailManagerEnable(boolean z);

    void showHint(String str);

    void showIndicatorManagerUi();

    void showInfo(CharSequence charSequence, int i);

    void showLeftCounts(int i, boolean z);

    void showLeftTime(long j);

    void showModeOptionsUi();

    void showPickerManagerUi();

    void showReviewView(Bitmap bitmap);

    void showReviewView(byte[] bArr, int i);

    void showSavingProgress(String str);

    void showSettingUi();

    void showThumbnailManagerUi();

    void stopShowCommonUI(boolean z);

    void switchShutterButtonImageResource(int i, boolean z);

    void switchShutterButtonLayout(int i);

    void updateAsdDetectedScene(String str);
}
