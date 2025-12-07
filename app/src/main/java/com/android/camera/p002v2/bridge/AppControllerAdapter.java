package com.android.camera.p002v2.bridge;

import android.app.Activity;
import android.content.Intent;
import android.graphics.RectF;
import android.net.Uri;
import com.android.camera.p002v2.app.location.LocationManager;
import com.android.camera.p002v2.p003ui.PreviewStatusListener;
import com.mediatek.camera.p005v2.platform.ModeChangeListener;
import com.mediatek.camera.p005v2.platform.app.AppContext;
import com.mediatek.camera.p005v2.platform.app.AppController;
import com.mediatek.camera.p005v2.platform.app.AppUi;
import com.mediatek.camera.p005v2.platform.device.CameraDeviceManager;
import com.mediatek.camera.p005v2.platform.module.ModuleUi;
import com.mediatek.camera.p005v2.services.CameraAppContext;
import com.mediatek.camera.p005v2.services.CameraServices;
import java.util.ArrayList;
import java.util.Iterator;
import junit.framework.Assert;

/* loaded from: classes.dex */
public class AppControllerAdapter implements AppController {
    private final com.android.camera.p002v2.app.AppController mAppController;
    private AppUIAdapter mAppUIAdapter;
    private CameraAppContext mCameraContext;
    private CameraDeviceManager mCameraDeviceManager;
    private ModeChangeAdapter mModeChangeAdapter;
    private ModulePreviewAreaChangedListener mPreviewAreaChangedListener;
    private ModuleUIAdapter mPreviewStatusListner;

    public AppControllerAdapter(com.android.camera.p002v2.app.AppController appController) {
        Assert.assertNotNull(appController);
        this.mAppController = appController;
    }

    @Override // com.mediatek.camera.p005v2.platform.app.AppController
    public Activity getActivity() {
        return this.mAppController.getActivity();
    }

    @Override // com.mediatek.camera.p005v2.platform.app.AppController
    public int getCurrentModeIndex() {
        return ModeChangeAdapter.getModeIndexFromKey(this.mAppController.getCurrentMode());
    }

    @Override // com.mediatek.camera.p005v2.platform.app.AppController
    public int getOldModeIndex() {
        return ModeChangeAdapter.getModeIndexFromKey(this.mAppController.getOldMode());
    }

    @Override // com.mediatek.camera.p005v2.platform.app.AppController
    public CameraDeviceManager getCameraManager() {
        if (this.mCameraDeviceManager == null) {
            this.mCameraDeviceManager = CameraDeviceManager.get(getActivity());
        }
        return this.mCameraDeviceManager;
    }

    @Override // com.mediatek.camera.p005v2.platform.app.AppController
    public void updateStorageSpaceAndHint() {
        this.mAppController.updateStorageSpaceAndHint();
    }

    @Override // com.mediatek.camera.p005v2.platform.app.AppController
    public LocationManager getLocationManager() {
        return this.mAppController.getLocationManager();
    }

    @Override // com.mediatek.camera.p005v2.platform.app.AppController
    public void notifyNewMedia(Uri uri) {
        this.mAppController.notifyNewMedia(uri);
    }

    @Override // com.mediatek.camera.p005v2.platform.app.AppController
    public AppUi getCameraAppUi() {
        if (this.mAppUIAdapter == null) {
            this.mAppUIAdapter = new AppUIAdapter(this.mAppController);
        }
        return this.mAppUIAdapter;
    }

    @Override // com.mediatek.camera.p005v2.platform.app.AppController
    public void setModuleUiListener(ModuleUi moduleUi) {
        if (this.mPreviewStatusListner == null || moduleUi != this.mPreviewStatusListner.getModuleUi()) {
            this.mPreviewStatusListner = new ModuleUIAdapter(moduleUi);
        }
        this.mAppController.setPreviewStatusListener(this.mPreviewStatusListner);
    }

    @Override // com.mediatek.camera.p005v2.platform.app.AppController
    public void updatePreviewSize(int i, int i2) {
        this.mAppController.updatePreviewSize(i, i2);
    }

    @Override // com.mediatek.camera.p005v2.platform.app.AppController
    public void onPreviewStarted() {
        this.mAppController.onPreviewStarted();
    }

    @Override // com.mediatek.camera.p005v2.platform.app.AppController
    public void showErrorAndFinish(int i) {
        this.mAppController.showErrorAndFinish(i);
    }

    @Override // com.mediatek.camera.p005v2.platform.app.AppController
    public void addPreviewAreaSizeChangedListener(ModuleUi.PreviewAreaChangedListener previewAreaChangedListener) {
        ModulePreviewAreaChangedListener modulePreviewAreaChangedListener = null;
        if (this.mPreviewAreaChangedListener == null) {
            this.mPreviewAreaChangedListener = new ModulePreviewAreaChangedListener(this, modulePreviewAreaChangedListener);
            this.mAppController.updatePreviewAreaChangedListener(this.mPreviewAreaChangedListener, true);
        }
        this.mPreviewAreaChangedListener.addPreviewAreaChangedListener(previewAreaChangedListener);
    }

    @Override // com.mediatek.camera.p005v2.platform.app.AppController
    public void removePreviewAreaSizeChangedListener(ModuleUi.PreviewAreaChangedListener previewAreaChangedListener) {
        if (this.mPreviewAreaChangedListener != null) {
            this.mPreviewAreaChangedListener.removePreviewAreaChangedListener(previewAreaChangedListener);
            if (this.mPreviewAreaChangedListener.getListenerCount() == 0) {
                this.mAppController.updatePreviewAreaChangedListener(this.mPreviewAreaChangedListener, false);
                this.mPreviewAreaChangedListener = null;
            }
        }
    }

    @Override // com.mediatek.camera.p005v2.platform.app.AppController
    public void enableKeepScreenOn(boolean z) {
        this.mAppController.enableKeepScreenOn(z);
    }

    @Override // com.mediatek.camera.p005v2.platform.app.AppController
    public AppContext getAppContext() {
        if (this.mCameraContext == null) {
            this.mCameraContext = new CameraAppContext(this);
        }
        return this.mCameraContext;
    }

    @Override // com.mediatek.camera.p005v2.platform.app.AppController
    public CameraServices getServices() {
        if (this.mCameraContext == null) {
            this.mCameraContext = new CameraAppContext(this);
        }
        return this.mCameraContext;
    }

    @Override // com.mediatek.camera.p005v2.platform.app.AppController
    public void setModeChangeListener(ModeChangeListener modeChangeListener) {
        this.mModeChangeAdapter = new ModeChangeAdapter(modeChangeListener);
        this.mAppController.setModeChangeListener(this.mModeChangeAdapter);
    }

    @Override // com.mediatek.camera.p005v2.platform.app.AppController
    public void setResultExAndFinish(int i) {
        this.mAppController.setResultExAndFinish(i);
    }

    @Override // com.mediatek.camera.p005v2.platform.app.AppController
    public void setResultExAndFinish(int i, Intent intent) {
        this.mAppController.setResultExAndFinish(i, intent);
    }

    private class ModulePreviewAreaChangedListener implements PreviewStatusListener.OnPreviewAreaChangedListener {
        private ArrayList<ModuleUi.PreviewAreaChangedListener> mListeners;
        private RectF mPreviewArea;

        /* synthetic */ ModulePreviewAreaChangedListener(AppControllerAdapter appControllerAdapter, ModulePreviewAreaChangedListener modulePreviewAreaChangedListener) {
            this();
        }

        private ModulePreviewAreaChangedListener() {
            this.mPreviewArea = new RectF();
            this.mListeners = new ArrayList<>();
        }

        public void addPreviewAreaChangedListener(ModuleUi.PreviewAreaChangedListener previewAreaChangedListener) {
            if (previewAreaChangedListener != null && (!this.mListeners.contains(previewAreaChangedListener))) {
                this.mListeners.add(previewAreaChangedListener);
            }
            if (this.mPreviewArea.width() != 0.0f || this.mPreviewArea.height() != 0.0f) {
                previewAreaChangedListener.onPreviewAreaChanged(this.mPreviewArea);
            }
        }

        public void removePreviewAreaChangedListener(ModuleUi.PreviewAreaChangedListener previewAreaChangedListener) {
            if (previewAreaChangedListener != null && this.mListeners.contains(previewAreaChangedListener)) {
                this.mListeners.remove(previewAreaChangedListener);
            }
        }

        public int getListenerCount() {
            return this.mListeners.size();
        }

        @Override // com.android.camera.v2.ui.PreviewStatusListener.OnPreviewAreaChangedListener
        public void onPreviewAreaChanged(RectF rectF) {
            this.mPreviewArea = rectF;
            Iterator<T> it = this.mListeners.iterator();
            while (it.hasNext()) {
                ((ModuleUi.PreviewAreaChangedListener) it.next()).onPreviewAreaChanged(rectF);
            }
        }
    }
}
