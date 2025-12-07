package com.mediatek.camera.p005v2.services.storage;

/* loaded from: classes.dex */
public interface IStorageService {

    public interface IStorageStateListener {
        void onStorageStateChanged(int i);
    }

    long getCaptureStorageSpace();

    String getFileDirectory();

    long getRecordStorageSpace();

    int getStorageHintInfo();

    boolean isStorageReady();

    void registerStorageStateListener(IStorageStateListener iStorageStateListener);

    void unRegisterStorageStateListener(IStorageStateListener iStorageStateListener);
}
