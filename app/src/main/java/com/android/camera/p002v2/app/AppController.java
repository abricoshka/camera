package com.android.camera.p002v2.app;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.widget.FrameLayout;
import com.android.camera.p002v2.app.location.LocationManager;
import com.android.camera.p002v2.bridge.AppControllerAdapter;
import com.android.camera.p002v2.bridge.ModeChangeAdapter;
import com.android.camera.p002v2.p003ui.PreviewStatusListener;
import com.android.camera.p002v2.uimanager.preference.PreferenceManager;
import java.util.Map;

/* loaded from: classes.dex */
public interface AppController {

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

    void enableKeepScreenOn(boolean z);

    Activity getActivity();

    AppControllerAdapter getAppControllerAdapter();

    CameraAppUI getCameraAppUI();

    String getCurrentMode();

    GestureManager getGestureManager();

    LocationManager getLocationManager();

    FrameLayout getModuleLayoutRoot();

    String getOldMode();

    PreferenceManager getPreferenceManager();

    PreviewManager getPreviewManager();

    void gotoGallery();

    void notifyNewMedia(Uri uri);

    void onCameraPicked(String str);

    void onModeChanged(Map<String, String> map);

    void onPreviewStarted();

    void onPreviewVisibilityChanged(int i);

    void setModeChangeListener(ModeChangeAdapter modeChangeAdapter);

    void setOkCancelClickListener(OkCancelClickListener okCancelClickListener);

    void setPlayButtonClickListener(PlayButtonClickListener playButtonClickListener);

    void setPreviewStatusListener(PreviewStatusListener previewStatusListener);

    void setResultExAndFinish(int i);

    void setResultExAndFinish(int i, Intent intent);

    void setRetakeButtonClickListener(RetakeButtonClickListener retakeButtonClickListener);

    void setShutterButtonEnabled(boolean z, boolean z2);

    void setShutterEventListener(ShutterEventsListener shutterEventsListener, boolean z);

    void showErrorAndFinish(int i);

    void updatePreviewAreaChangedListener(PreviewStatusListener.OnPreviewAreaChangedListener onPreviewAreaChangedListener, boolean z);

    void updatePreviewSize(int i, int i2);

    void updateStorageSpaceAndHint();
}
