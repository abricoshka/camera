package com.android.camera.actor;

import android.content.Intent;
import android.hardware.Camera;
import android.view.KeyEvent;
import android.view.View;
import com.android.camera.CameraActivity;
import com.android.camera.CameraErrorCallback;
import com.android.camera.manager.RecordingView;
import com.android.camera.p001ui.ShutterButton;
import com.mediatek.camera.ICameraMode;
import com.mediatek.camera.ModuleManager;
import com.mediatek.camera.platform.ICameraAppUi;
import com.mediatek.camera.util.Log;

/* loaded from: classes.dex */
public class VideoActor extends CameraActor {
    protected CameraActivity mCameraActivity;
    private final ICameraAppUi mCameraAppUI;
    private View.OnClickListener mCancelListener;
    private int mCurrentMode;
    private CameraErrorCallback mErrorCallback;
    private ModuleManager mModuleManager;
    private View.OnClickListener mOkListener;
    private CameraActivity.OnLongPressListener mOnLongPressListener;
    private ShutterButton.OnShutterButtonListener mPhotoShutterListener;
    protected RecordingView mRecordingView;
    private CameraActivity.OnSingleTapUpListener mTapupListener;
    private ShutterButton.OnShutterButtonListener mVideoShutterListener;

    public VideoActor(CameraActivity cameraActivity, ModuleManager moduleManager, int i) {
        super(cameraActivity);
        this.mCurrentMode = 8;
        this.mPhotoShutterListener = new ShutterButton.OnShutterButtonListener() { // from class: com.android.camera.actor.VideoActor.1
            @Override // com.android.camera.ui.ShutterButton.OnShutterButtonListener
            public void onShutterButtonLongPressed(ShutterButton shutterButton) {
                VideoActor.this.mModuleManager.onShutterButtonLongPressed();
            }

            @Override // com.android.camera.ui.ShutterButton.OnShutterButtonListener
            public void onShutterButtonFocus(ShutterButton shutterButton, boolean z) {
            }

            @Override // com.android.camera.ui.ShutterButton.OnShutterButtonListener
            public void onShutterButtonClick(ShutterButton shutterButton) {
                VideoActor.this.mModuleManager.onPhotoShutterButtonClick();
            }
        };
        this.mTapupListener = new CameraActivity.OnSingleTapUpListener() { // from class: com.android.camera.actor.VideoActor.2
            @Override // com.android.camera.CameraActivity.OnSingleTapUpListener
            public void onSingleTapUp(View view, int i2, int i3) {
                VideoActor.this.mModuleManager.onSingleTapUp(view, i2, i3);
            }
        };
        this.mOnLongPressListener = new CameraActivity.OnLongPressListener() { // from class: com.android.camera.actor.VideoActor.3
            @Override // com.android.camera.CameraActivity.OnLongPressListener
            public void onLongPress(View view, int i2, int i3) {
                VideoActor.this.mModuleManager.onLongPress(view, i2, i3);
            }
        };
        this.mOkListener = new View.OnClickListener() { // from class: com.android.camera.actor.VideoActor.4
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                VideoActor.this.mModuleManager.onOkButtonPress();
            }
        };
        this.mCancelListener = new View.OnClickListener() { // from class: com.android.camera.actor.VideoActor.5
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                VideoActor.this.mModuleManager.onCancelButtonPress();
                VideoActor.this.mCameraActivity.setResultExAndFinish(0, new Intent());
            }
        };
        this.mVideoShutterListener = new ShutterButton.OnShutterButtonListener() { // from class: com.android.camera.actor.VideoActor.6
            @Override // com.android.camera.ui.ShutterButton.OnShutterButtonListener
            public void onShutterButtonLongPressed(ShutterButton shutterButton) {
            }

            @Override // com.android.camera.ui.ShutterButton.OnShutterButtonListener
            public void onShutterButtonFocus(ShutterButton shutterButton, boolean z) {
                VideoActor.this.mModuleManager.onShutterButtonFocus(z);
            }

            @Override // com.android.camera.ui.ShutterButton.OnShutterButtonListener
            public void onShutterButtonClick(ShutterButton shutterButton) {
                VideoActor.this.mCameraAppUI.updateRemainStorage();
                VideoActor.this.mCameraActivity.resetScreenOn();
                VideoActor.this.mModuleManager.onVideoShutterButtonClick();
            }
        };
        Log.m34i("VideoActor", "[VideoActor]constructor...");
        this.mCameraActivity = getContext();
        this.mErrorCallback = new CameraErrorCallback(this.mCameraActivity);
        this.mCameraAppUI = this.mCameraActivity.getCameraAppUI();
        this.mModuleManager = moduleManager;
        prepareCurrentMode(i);
    }

    @Override // com.android.camera.actor.CameraActor
    public void onMediaEject() {
        Log.m34i("VideoActor", "[onMediaEject]...");
        this.mModuleManager.onMediaEject();
    }

    @Override // com.android.camera.actor.CameraActor
    public void onCameraClose() {
        Log.m34i("VideoActor", "[onCameraClose]");
        this.mModuleManager.onCameraClose();
    }

    @Override // com.android.camera.actor.CameraActor
    public void onCameraParameterReady(boolean z) {
        Log.m34i("VideoActor", "[onCameraParameterReady]startPreview = " + z);
        this.mModuleManager.onCameraParameterReady(z);
        this.mCameraActivity.setCameraState(1);
    }

    @Override // com.android.camera.actor.CameraActor
    public void stopPreview() {
        Log.m34i("VideoActor", "[stopPreview] mVideoContext.getCameraState()=" + this.mCameraActivity.getCameraState());
        if (!this.mModuleManager.stopPreview() && this.mCameraActivity.getCameraDevice() != null) {
            this.mCameraActivity.getCameraDevice().stopPreview();
            this.mCameraActivity.setCameraState(0);
        }
    }

    @Override // com.android.camera.actor.CameraActor
    public void onRestoreSettings() {
        this.mModuleManager.onRestoreSettings();
    }

    @Override // com.android.camera.actor.CameraActor
    public int getMode() {
        return this.mCurrentMode;
    }

    @Override // com.android.camera.actor.CameraActor
    public ShutterButton.OnShutterButtonListener getVideoShutterButtonListener() {
        return this.mVideoShutterListener;
    }

    @Override // com.android.camera.actor.CameraActor
    public ShutterButton.OnShutterButtonListener getPhotoShutterButtonListener() {
        return this.mPhotoShutterListener;
    }

    @Override // com.android.camera.actor.CameraActor
    public boolean onUserInteraction() {
        if (!this.mModuleManager.onUserInteraction()) {
            this.mCameraActivity.keepScreenOnAwhile();
            return true;
        }
        return false;
    }

    @Override // com.android.camera.actor.CameraActor
    public void setSurfaceTextureReady(boolean z) {
        this.mModuleManager.setSurfaceTextureReady(z);
    }

    @Override // com.android.camera.actor.CameraActor
    public boolean onBackPressed() {
        Log.m34i("VideoActor", "[onBackPressed]");
        return this.mModuleManager.onBackPressed();
    }

    @Override // com.android.camera.actor.CameraActor
    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        return this.mModuleManager.onKeyDown(i, keyEvent);
    }

    @Override // com.android.camera.actor.CameraActor
    public CameraActivity.OnLongPressListener getonLongPressListener() {
        return this.mOnLongPressListener;
    }

    @Override // com.android.camera.actor.CameraActor
    public Camera.ErrorCallback getErrorCallback() {
        return this.mErrorCallback;
    }

    @Override // com.android.camera.actor.CameraActor
    public CameraActivity.OnSingleTapUpListener getonSingleTapUpListener() {
        return this.mTapupListener;
    }

    public void prepareCurrentMode(int i) {
        Log.m34i("VideoActor", "[prepareCurrentMode] newMode = " + i);
        this.mCurrentMode = i;
        ICameraMode.CameraModeType cameraModeType = getCameraModeType(this.mCurrentMode);
        if (cameraModeType == null) {
            cameraModeType = ICameraMode.CameraModeType.EXT_MODE_VIDEO;
        }
        this.mModuleManager.createMode(cameraModeType);
    }

    @Override // com.android.camera.actor.CameraActor
    public void release() {
        this.mModuleManager.closeMode();
        releaseActor();
    }

    @Override // com.android.camera.actor.CameraActor
    public View.OnClickListener getOkListener() {
        return this.mOkListener;
    }

    @Override // com.android.camera.actor.CameraActor
    public View.OnClickListener getCancelListener() {
        return this.mCancelListener;
    }

    public void releaseActor() {
        Log.m34i("VideoActor", "[releaseActor]...");
        this.mVideoShutterListener = null;
        if (this.mCameraActivity.getFocusManager() != null) {
            this.mCameraActivity.getFocusManager().removeMessages();
        }
        if (this.mRecordingView != null) {
            this.mRecordingView.uninit();
        }
        this.mFocusManager = null;
    }
}
