package com.android.camera.bridge;

import com.android.camera.FocusManager;
import com.mediatek.camera.platform.IFocusManager;
import junit.framework.Assert;

/* loaded from: classes.dex */
public class FocusManagerImpl implements IFocusManager {
    private IFocusManager.FocusListener mFocusListener;
    private FocusManager mFocusManager;
    private FocusManager.Listener mListener = new FocusManager.Listener() { // from class: com.android.camera.bridge.FocusManagerImpl.1
        @Override // com.android.camera.FocusManager.Listener
        public void autoFocus() {
            FocusManagerImpl.this.mFocusListener.autoFocus();
        }

        @Override // com.android.camera.FocusManager.Listener
        public void cancelAutoFocus() {
            FocusManagerImpl.this.mFocusListener.cancelAutoFocus();
        }

        @Override // com.android.camera.FocusManager.Listener
        public boolean capture() {
            return FocusManagerImpl.this.mFocusListener.capture();
        }

        @Override // com.android.camera.FocusManager.Listener
        public void startFaceDetection() {
            FocusManagerImpl.this.mFocusListener.startFaceDetection();
        }

        @Override // com.android.camera.FocusManager.Listener
        public void stopFaceDetection() {
            FocusManagerImpl.this.mFocusListener.stopFaceDetection();
        }

        @Override // com.android.camera.FocusManager.Listener
        public void setFocusParameters() {
            FocusManagerImpl.this.mFocusListener.setFocusParameters();
        }

        @Override // com.android.camera.FocusManager.Listener
        public void playSound(int i) {
            FocusManagerImpl.this.mFocusListener.playSound(i);
        }

        @Override // com.android.camera.FocusManager.Listener
        public boolean readyToCapture() {
            return true;
        }
    };

    public FocusManagerImpl(FocusManager focusManager) {
        Assert.assertNotNull(focusManager);
        this.mFocusManager = focusManager;
    }

    @Override // com.mediatek.camera.platform.IFocusManager
    public void setAfData(byte[] bArr) {
        this.mFocusManager.setAfData(bArr);
    }

    @Override // com.mediatek.camera.platform.IFocusManager
    public boolean setListener(IFocusManager.FocusListener focusListener) {
        this.mFocusListener = focusListener;
        this.mFocusManager.setListener(this.mListener);
        return true;
    }

    @Override // com.mediatek.camera.platform.IFocusManager
    public boolean setAeLock(boolean z) {
        this.mFocusManager.setAeLock(z);
        return true;
    }

    @Override // com.mediatek.camera.platform.IFocusManager
    public boolean setAwbLock(boolean z) {
        this.mFocusManager.setAwbLock(z);
        return true;
    }

    @Override // com.mediatek.camera.platform.IFocusManager
    public boolean resetTouchFocus() {
        this.mFocusManager.resetTouchFocus();
        return true;
    }

    @Override // com.mediatek.camera.platform.IFocusManager
    public void removeMessages() {
        this.mFocusManager.removeMessages();
    }

    @Override // com.mediatek.camera.platform.IFocusManager
    public boolean updateFocusUI() {
        this.mFocusManager.updateFocusUI();
        return true;
    }

    @Override // com.mediatek.camera.platform.IFocusManager
    public void focusAndCapture() {
        this.mFocusManager.doSnap();
    }

    @Override // com.mediatek.camera.platform.IFocusManager
    public void clearFocusAndFaceUi() {
        this.mFocusManager.clearFocusAndFaceUi();
    }

    @Override // com.mediatek.camera.platform.IFocusManager
    public void onAutoFocusMoving(boolean z) {
        this.mFocusManager.onAutoFocusMoving(z);
    }

    @Override // com.mediatek.camera.platform.IFocusManager
    public void onPreviewStarted() {
        this.mFocusManager.onPreviewStarted();
    }

    @Override // com.mediatek.camera.platform.IFocusManager
    public void onPreviewStopped() {
        this.mFocusManager.onPreviewStopped();
    }

    @Override // com.mediatek.camera.platform.IFocusManager
    public String getFocusMode() {
        return this.mFocusManager.getFocusMode();
    }

    @Override // com.mediatek.camera.platform.IFocusManager
    public void onShutterUp() {
        this.mFocusManager.onShutterUp();
    }

    @Override // com.mediatek.camera.platform.IFocusManager
    public void onAutoFocus(boolean z) {
        this.mFocusManager.onAutoFocus(z);
    }

    @Override // com.mediatek.camera.platform.IFocusManager
    public void onSingleTapUp(int i, int i2) {
        this.mFocusManager.onSingleTapUp(i, i2);
    }

    @Override // com.mediatek.camera.platform.IFocusManager
    public boolean getFocusAreaSupported() {
        return this.mFocusManager.getFocusAreaSupported();
    }

    @Override // com.mediatek.camera.platform.IFocusManager
    public void setDistanceInfo(String str) {
        this.mFocusManager.setDistanceInfo(str);
    }

    @Override // com.mediatek.camera.platform.IFocusManager
    public void cancelAutoFocus() {
        this.mFocusManager.cancelAutoFocus();
    }
}
