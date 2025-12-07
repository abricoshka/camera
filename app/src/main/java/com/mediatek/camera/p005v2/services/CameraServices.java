package com.mediatek.camera.p005v2.services;

import com.mediatek.camera.p005v2.services.storage.IStorageService;
import com.mediatek.camera.p005v2.setting.SettingCtrl;

/* loaded from: classes.dex */
public interface CameraServices {
    FileSaver getMediaSaver();

    SettingCtrl getSettingController();

    ISoundPlayback getSoundPlayback();

    IStorageService getStorageService();
}
