package com.mediatek.camera.p005v2.services;

import com.mediatek.camera.p005v2.platform.app.AppContext;
import com.mediatek.camera.p005v2.platform.app.AppController;
import com.mediatek.camera.p005v2.services.storage.IStorageService;
import com.mediatek.camera.p005v2.services.storage.StorageServiceImpl;
import com.mediatek.camera.p005v2.setting.SettingCtrl;
import com.mediatek.camera.p005v2.util.Utils;

/* loaded from: classes.dex */
public class CameraAppContext implements AppContext, CameraServices {
    private final AppController mAppController;
    private FileSaver mMediaSaver;
    private SettingCtrl mSettingCtrl;
    private SoundPlaybackImpl mSoundPlayback;
    private IStorageService mStorageService;

    public CameraAppContext(AppController appController) {
        this.mAppController = appController;
        Utils.initialize(appController.getActivity());
    }

    @Override // com.mediatek.camera.p005v2.platform.app.AppContext
    public void onCreate() {
        this.mSoundPlayback = new SoundPlaybackImpl(this.mAppController.getActivity());
        this.mStorageService = new StorageServiceImpl(this.mAppController.getActivity());
    }

    @Override // com.mediatek.camera.p005v2.platform.app.AppContext
    public void onResume() {
        StorageServiceImpl.getStorageMonitor().registerIntentFilter();
    }

    @Override // com.mediatek.camera.p005v2.platform.app.AppContext
    public void onPause() {
        this.mSoundPlayback.pause();
        StorageServiceImpl.getStorageMonitor().unregisterIntentFilter();
    }

    @Override // com.mediatek.camera.p005v2.platform.app.AppContext
    public void onDestroy() {
        this.mSoundPlayback.release();
    }

    @Override // com.mediatek.camera.p005v2.services.CameraServices
    public FileSaver getMediaSaver() {
        if (this.mMediaSaver == null) {
            this.mMediaSaver = new FileSaverImpl();
        }
        return this.mMediaSaver;
    }

    @Override // com.mediatek.camera.p005v2.services.CameraServices
    public IStorageService getStorageService() {
        if (this.mStorageService == null) {
            this.mStorageService = new StorageServiceImpl(this.mAppController.getActivity());
        }
        return this.mStorageService;
    }

    @Override // com.mediatek.camera.p005v2.services.CameraServices
    public SettingCtrl getSettingController() {
        if (this.mSettingCtrl == null) {
            this.mSettingCtrl = new SettingCtrl(this.mAppController.getActivity());
        }
        return this.mSettingCtrl;
    }

    @Override // com.mediatek.camera.p005v2.services.CameraServices
    public ISoundPlayback getSoundPlayback() {
        return this.mSoundPlayback;
    }
}
