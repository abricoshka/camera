package com.mediatek.camera;

import android.graphics.SurfaceTexture;

/* loaded from: classes.dex */
public interface ICameraMode {
    boolean close();

    boolean execute(ActionType actionType, Object... objArr);

    SurfaceTexture getBottomSurfaceTexture();

    ModeState getModeState();

    SurfaceTexture getTopSurfaceTexture();

    boolean isDeviceUseSurfaceView();

    boolean isDisplayUseSurfaceView();

    boolean isNeedDualCamera();

    boolean open();

    void pause();

    void resume();

    public enum CameraModeType {
        EXT_MODE_PHOTO,
        EXT_MODE_VIDEO,
        EXT_MODE_FACE_BEAUTY,
        EXT_MODE_PANORAMA,
        EXT_MODE_PHOTO_PIP,
        EXT_MODE_VIDEO_PIP,
        EXT_MODE_SLOW_MOTION,
        EXT_MODE_STEREO_CAMERA,
        EXT_MODE_VIDEO_STEREO,
        EXT_MODE_PHOTO_STEREO;

        /* renamed from: values, reason: to resolve conflict with enum method */
        public static CameraModeType[] valuesCustom() {
            return values();
        }
    }

    public enum ActionType {
        ACTION_ON_CAMERA_OPEN,
        ACTION_ON_CAMERA_PARAMETERS_READY,
        ACTION_ON_CAMERA_CLOSE,
        ACTION_ORITATION_CHANGED,
        ACTION_FACE_DETECTED,
        ACTION_PHOTO_SHUTTER_BUTTON_CLICK,
        ACTION_VIDEO_SHUTTER_BUTTON_CLICK,
        ACTION_SHUTTER_BUTTON_FOCUS,
        ACTION_SHUTTER_BUTTON_LONG_PRESS,
        ACTION_OK_BUTTON_CLICK,
        ACTION_CANCEL_BUTTON_CLICK,
        ACTION_ON_LONG_PRESS,
        ACTION_ON_SINGLE_TAP_UP,
        ACTION_PREVIEW_VISIBLE_CHANGED,
        ACTION_ON_FULL_SCREEN_CHANGED,
        ACTION_ON_BACK_KEY_PRESS,
        ACTION_ON_KEY_EVENT_PRESS,
        ACTION_ON_SURFACE_TEXTURE_READY,
        ACTION_ON_MEDIA_EJECT,
        ACTION_ON_RESTORE_SETTINGS,
        ACTION_ON_USER_INTERACTION,
        ACTION_ON_START_PREVIEW,
        ACTION_ON_STOP_PREVIEW,
        ACTION_SET_DISPLAYROTATION,
        ACTION_ON_PREVIEW_DISPLAY_SIZE_CHANGED,
        ACTION_ON_PREVIEW_BUFFER_SIZE_CHANGED,
        ACTION_ON_SETTING_BUTTON_CLICK,
        ACTION_SWITCH_DEVICE,
        ACTION_NOTIFY_SURFCEVIEW_DISPLAY_IS_READY,
        ACTION_ON_COMPENSATION_CHANGED,
        ACTION_DISABLE_VIDEO_RECORD,
        ACTION_ON_CONFIGURATION_CHANGED,
        ACTION_ON_SELFTIMER_STATE,
        ACTION_NOTIFY_SURFCEVIEW_DESTROYED,
        ACTION_CAN_DO_AUTO_FOCUS;

        /* renamed from: values, reason: to resolve conflict with enum method */
        public static ActionType[] valuesCustom() {
            return values();
        }
    }

    public enum ModeState {
        STATE_UNKNOWN,
        STATE_IDLE,
        STATE_FOCUSING,
        STATE_CAPTURING,
        STATE_RECORDING,
        STATE_SAVING,
        STATE_CLOSED;

        /* renamed from: values, reason: to resolve conflict with enum method */
        public static ModeState[] valuesCustom() {
            return values();
        }
    }
}
