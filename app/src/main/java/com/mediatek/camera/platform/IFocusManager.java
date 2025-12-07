package com.mediatek.camera.platform;

/* loaded from: classes.dex */
public interface IFocusManager {

    public interface FocusListener {
        void autoFocus();

        void cancelAutoFocus();

        boolean capture();

        void playSound(int i);

        void setFocusParameters();

        void startFaceDetection();

        void stopFaceDetection();
    }

    void cancelAutoFocus();

    void clearFocusAndFaceUi();

    void focusAndCapture();

    boolean getFocusAreaSupported();

    String getFocusMode();

    void onAutoFocus(boolean z);

    void onAutoFocusMoving(boolean z);

    void onPreviewStarted();

    void onPreviewStopped();

    void onShutterUp();

    void onSingleTapUp(int i, int i2);

    void removeMessages();

    boolean resetTouchFocus();

    boolean setAeLock(boolean z);

    void setAfData(byte[] bArr);

    boolean setAwbLock(boolean z);

    void setDistanceInfo(String str);

    boolean setListener(FocusListener focusListener);

    boolean updateFocusUI();
}
