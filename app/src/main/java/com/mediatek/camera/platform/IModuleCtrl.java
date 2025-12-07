package com.mediatek.camera.platform;

import android.content.Intent;
import android.hardware.Camera;
import android.location.Location;
import android.net.Uri;
import android.view.Surface;
import com.android.camera.ComboPreferences;
import com.mediatek.camera.ICameraMode;

/* loaded from: classes.dex */
public interface IModuleCtrl {
    boolean applyFocusParameters(boolean z);

    void backToCallingActivity(int i, Intent intent);

    void backToLastMode();

    boolean disableOrientationListener();

    boolean enableOrientationListener();

    ComboPreferences getComboPreferences();

    String getCropValue();

    int getDisplayOrientation();

    int getDisplayRotation();

    Intent getIntent();

    int getJpegOrientation();

    Location getLocation();

    ICameraMode.CameraModeType getNextMode();

    int getOrientation();

    int getOrientationCompensation();

    Surface getPreviewSurface();

    Uri getSaveUri();

    boolean initializeFrameView(boolean z);

    boolean isFirstStartUp();

    boolean isImageCaptureIntent();

    boolean isNonePickIntent();

    boolean isQuickCapture();

    boolean isSecureCamera();

    boolean isVideoCaptureIntent();

    boolean lockOrientation();

    void previewStarted();

    boolean setFaceBeautyEnalbe(boolean z);

    void setFaces(Camera.Face[] faceArr);

    void setResultAndFinish(int i);

    void setResultAndFinish(int i, Intent intent);

    void startFaceDetection();

    void stopFaceDetection();

    void switchCameraDevice();

    boolean unlockOrientation();
}
