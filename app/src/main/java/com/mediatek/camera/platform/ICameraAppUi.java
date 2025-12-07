package com.mediatek.camera.platform;

import android.media.CamcorderProfile;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.mediatek.camera.ICameraMode;
import java.io.FileDescriptor;

/* loaded from: classes.dex */
public interface ICameraAppUi {

    public interface GestureListener {
        boolean onDoubleTap(float f, float f2);

        boolean onDown(float f, float f2, int i, int i2);

        boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2);

        boolean onLongPress(float f, float f2);

        boolean onScale(float f, float f2, float f3);

        boolean onScaleBegin(float f, float f2);

        boolean onScroll(float f, float f2, float f3, float f4);

        boolean onSingleTapUp(float f, float f2);

        boolean onUp();
    }

    void changeBackToVFBModeStatues(boolean z);

    void changeZoomForQuality();

    boolean collapseSetting(boolean z);

    boolean collapseSubSetting(boolean z);

    boolean collapseViewManager(boolean z);

    void dismissInfo();

    void dismissProgress();

    ICameraView getCameraView(CommonUiType commonUiType);

    ICameraView getCameraView(SpecViewType specViewType);

    ViewGroup getNormalViewLayer();

    ImageView getPhotoShutter();

    ShutterButtonType getShutterType();

    int getUnCropHeight();

    int getUnCropWidth();

    ViewState getViewState();

    void hideAllViews();

    void hideReview();

    boolean isNormalViewState();

    boolean isSettingShowing();

    void onDetectedSceneMode(int i, boolean z);

    void restoreSceneMode();

    void restoreViewState();

    void setCamcorderProfile(CamcorderProfile camcorderProfile);

    void setCurrentMode(ICameraMode.CameraModeType cameraModeType);

    void setGestureListener(GestureListener gestureListener);

    void setOkButtonEnabled(boolean z);

    void setPhotoShutterEnabled(boolean z);

    void setReviewListener(View.OnClickListener onClickListener, View.OnClickListener onClickListener2);

    void setSwipeEnabled(boolean z);

    void setThumbnailRefreshInterval(int i);

    void setVideoShutterEnabled(boolean z);

    void setVideoShutterMask(boolean z);

    void setViewState(ViewState viewState);

    void showAllViews();

    void showInfo(CharSequence charSequence, int i);

    void showInfo(String str);

    void showProgress(String str);

    void showRemaining();

    void showReview(String str, FileDescriptor fileDescriptor);

    void showToast(int i);

    void showToastForShort(int i);

    void switchShutterType(ShutterButtonType shutterButtonType);

    void updataVideoRecordingManager(boolean z);

    void updateFaceBeatuyEntryViewVisible(boolean z);

    long updateRemainStorage();

    void updateSnapShotUIView(boolean z);

    void updateThumbnailViewWithYuv(byte[] bArr, int i, int i2, int i3, int i4);

    void updateVideoShutterStatues(boolean z);

    public enum CommonUiType {
        SHUTTER,
        MODE_PICKER,
        THUMBNAIL,
        PICKER,
        INDICATOR,
        REMAINING,
        INFO,
        REVIEW,
        ROTATE_PROGRESS,
        ROTATE_DIALOG,
        ZOOM,
        SETTING,
        FACE_BEAUTY_ENTRY,
        BACKGROUND;

        /* renamed from: values, reason: to resolve conflict with enum method */
        public static CommonUiType[] valuesCustom() {
            return values();
        }
    }

    public enum SpecViewType {
        MODE_FACE_BEAUTY,
        MODE_PANORAMA,
        MODE_PIP,
        MODE_STEREO,
        MODE_SLOW_MOTION,
        ADDITION_CONTINUE_SHOT,
        ADDITION_EFFECT,
        ADDITION_OBJECT_TRACKING;

        /* renamed from: values, reason: to resolve conflict with enum method */
        public static SpecViewType[] valuesCustom() {
            return values();
        }
    }

    public enum ShutterButtonType {
        SHUTTER_TYPE_PHOTO_VIDEO,
        SHUTTER_TYPE_PHOTO,
        SHUTTER_TYPE_VIDEO,
        SHUTTER_TYPE_OK_CANCEL,
        SHUTTER_TYPE_CANCEL,
        SHUTTER_TYPE_CANCEL_VIDEO,
        SHUTTER_TYPE_SLOW_VIDEO;

        /* renamed from: values, reason: to resolve conflict with enum method */
        public static ShutterButtonType[] valuesCustom() {
            return values();
        }
    }

    public enum ViewState {
        VIEW_STATE_NORMAL,
        VIEW_STATE_CAPTURE,
        VIEW_STATE_PRE_RECORDING,
        VIEW_STATE_RECORDING,
        VIEW_STATE_SETTING,
        VIEW_STATE_SUB_SETTING,
        VIEW_STATE_FOCUSING,
        VIEW_STATE_SAVING,
        VIEW_STATE_REVIEW,
        VIEW_STATE_CAMERA_OPENED,
        VIEW_STATE_CAMERA_CLOSED,
        VIEW_STATE_PICKING,
        VIEW_STATE_CONTINUOUS_CAPTURE,
        VIEW_STATE_LOMOEFFECT_SETTING,
        VIEW_STATE_HIDE_ALL_VIEW;

        /* renamed from: values, reason: to resolve conflict with enum method */
        public static ViewState[] valuesCustom() {
            return values();
        }
    }
}
