package com.mediatek.camera.p005v2.services.storage;

import android.content.Context;
import com.mediatek.camera.p005v2.services.storage.IStorageService;

/* loaded from: classes.dex */
public class StorageServiceImpl implements IStorageService {
    private static StorageMonitor sStorageMonitor;

    public StorageServiceImpl(Context context) {
        sStorageMonitor = new StorageMonitor(context);
        Storage.setContext(context);
        Storage.updateDefaultDirectory();
    }

    public static StorageMonitor getStorageMonitor() {
        return sStorageMonitor;
    }

    @Override // com.mediatek.camera.p005v2.services.storage.IStorageService
    public boolean isStorageReady() {
        return Storage.isStorageReady();
    }

    @Override // com.mediatek.camera.p005v2.services.storage.IStorageService
    public String getFileDirectory() {
        return Storage.getFileDirectory();
    }

    @Override // com.mediatek.camera.p005v2.services.storage.IStorageService
    public long getCaptureStorageSpace() {
        long availableSpace = Storage.getAvailableSpace();
        if (availableSpace > Storage.LOW_STORAGE_THRESHOLD) {
            return availableSpace - Storage.LOW_STORAGE_THRESHOLD;
        }
        if (availableSpace <= 0) {
            return availableSpace;
        }
        return 0L;
    }

    @Override // com.mediatek.camera.p005v2.services.storage.IStorageService
    public long getRecordStorageSpace() {
        long availableSpace = Storage.getAvailableSpace();
        if (availableSpace > Storage.RECORD_LOW_STORAGE_THRESHOLD) {
            return availableSpace - Storage.RECORD_LOW_STORAGE_THRESHOLD;
        }
        if (availableSpace <= 0) {
            return availableSpace;
        }
        return 0L;
    }

    @Override // com.mediatek.camera.p005v2.services.storage.IStorageService
    public int getStorageHintInfo() {
        return Storage.getStorageHintInfo();
    }

    @Override // com.mediatek.camera.p005v2.services.storage.IStorageService
    public void registerStorageStateListener(IStorageService.IStorageStateListener iStorageStateListener) {
        sStorageMonitor.registerStorageStateListener(iStorageStateListener);
    }

    @Override // com.mediatek.camera.p005v2.services.storage.IStorageService
    public void unRegisterStorageStateListener(IStorageService.IStorageStateListener iStorageStateListener) {
        sStorageMonitor.unRegisterStorageStateListener(iStorageStateListener);
    }
}
