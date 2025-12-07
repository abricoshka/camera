package com.android.camera.actor;

import android.content.Intent;
import android.hardware.Camera;
import android.support.v4.app.FrameMetricsAggregator;
import android.view.KeyEvent;
import android.view.View;
import com.android.camera.CameraActivity;
import com.android.camera.FocusManager;
import com.android.camera.p001ui.ShutterButton;
import com.mediatek.camera.ICameraMode;

/* loaded from: classes.dex */
public abstract class CameraActor {
    protected final CameraActivity mContext;
    protected FocusManager mFocusManager;

    public abstract int getMode();

    public CameraActor(CameraActivity cameraActivity) {
        this.mContext = cameraActivity;
    }

    public CameraActivity getContext() {
        return this.mContext;
    }

    public Camera.ErrorCallback getErrorCallback() {
        return null;
    }

    public Camera.FaceDetectionListener getFaceDetectionListener() {
        return null;
    }

    public boolean onUserInteraction() {
        return false;
    }

    public boolean onBackPressed() {
        return false;
    }

    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        return false;
    }

    public boolean onKeyUp(int i, KeyEvent keyEvent) {
        return false;
    }

    public void onActivityResult(int i, int i2, Intent intent) {
    }

    public void onMediaEject() {
    }

    public void onRestoreSettings() {
    }

    public ShutterButton.OnShutterButtonListener getVideoShutterButtonListener() {
        return null;
    }

    public ShutterButton.OnShutterButtonListener getPhotoShutterButtonListener() {
        return null;
    }

    public CameraActivity.OnSingleTapUpListener getonSingleTapUpListener() {
        return null;
    }

    public CameraActivity.OnLongPressListener getonLongPressListener() {
        return null;
    }

    public View.OnClickListener getOkListener() {
        return null;
    }

    public View.OnClickListener getCancelListener() {
        return null;
    }

    public FocusManager.Listener getFocusManagerListener() {
        return null;
    }

    public void onCameraOpenDone() {
    }

    public void onCameraOpenFailed() {
    }

    public void onCameraDisabled() {
    }

    public void onCameraParameterReady(boolean z) {
    }

    public void stopPreview() {
    }

    public void onCameraClose() {
    }

    public boolean handleFocus() {
        return false;
    }

    public void release() {
    }

    public void onDisplayRotate() {
    }

    public void setSurfaceTextureReady(boolean z) {
    }

    public void startFaceDetection() {
    }

    public void stopFaceDetection() {
    }

    public ICameraMode.CameraModeType getCameraModeType(int i) {
        switch (i) {
            case 0:
                return ICameraMode.CameraModeType.EXT_MODE_PHOTO;
            case 1:
            case 4:
            default:
                return null;
            case 2:
                return ICameraMode.CameraModeType.EXT_MODE_FACE_BEAUTY;
            case 3:
                return ICameraMode.CameraModeType.EXT_MODE_PANORAMA;
            case 5:
                return ICameraMode.CameraModeType.EXT_MODE_PHOTO_PIP;
            case 6:
                return ICameraMode.CameraModeType.EXT_MODE_STEREO_CAMERA;
            case FrameMetricsAggregator.DELAY_INDEX /* 7 */:
                return ICameraMode.CameraModeType.EXT_MODE_PHOTO_STEREO;
            case 8:
                return ICameraMode.CameraModeType.EXT_MODE_VIDEO;
            case 9:
                return ICameraMode.CameraModeType.EXT_MODE_VIDEO_PIP;
            case 10:
                return ICameraMode.CameraModeType.EXT_MODE_VIDEO_STEREO;
        }
    }
}
