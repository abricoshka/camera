package com.mediatek.camera.p005v2.services.storage;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.ServiceManager;
import android.os.StatFs;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.util.Log;
import com.android.camera.FeatureSwitcher;
import com.mediatek.camera.R;
import java.io.File;
import utils.StorageManagerEx;

/* loaded from: classes.dex */
class Storage {
    public static final long LOW_STORAGE_THRESHOLD;
    public static final long RECORD_LOW_STORAGE_THRESHOLD;
    private static Context sContext;
    private static boolean sIsStorageReady;
    private static String sMountPoint;
    private static StorageManager sStorageManager;
    private static final String DCIM = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString();
    public static final String DIRECTORY = DCIM + "/Camera";
    private static final String FOLDER_PATH = "/" + Environment.DIRECTORY_DCIM + "/Camera";
    private static boolean isExtendStorageCanUse = false;

    Storage() {
    }

    static {
        if (FeatureSwitcher.isMtkFatOnNand() || FeatureSwitcher.isGmoROM()) {
            LOW_STORAGE_THRESHOLD = 10485760L;
            RECORD_LOW_STORAGE_THRESHOLD = 9437184L;
            Log.i("Storage", "LOW_STORAGE_THRESHOLD = 10485760");
        } else {
            LOW_STORAGE_THRESHOLD = 52428800L;
            RECORD_LOW_STORAGE_THRESHOLD = 50331648L;
            Log.i("Storage", "LOW_STORAGE_THRESHOLD = 52428800");
        }
    }

    static void setContext(Context context) {
        sContext = context;
        initializeStorageManager();
    }

    static long getAvailableSpace() {
        String volumeState = sStorageManager.getVolumeState(sMountPoint);
        if ("checking".equals(volumeState)) {
            return -2L;
        }
        if (!"mounted".equals(volumeState)) {
            return -1L;
        }
        File file = new File(getFileDirectory());
        file.mkdirs();
        if (!file.isDirectory() || (!file.canWrite())) {
            return -4L;
        }
        try {
            StatFs statFs = new StatFs(getFileDirectory());
            return statFs.getBlockSize() * statFs.getAvailableBlocks();
        } catch (IllegalArgumentException e) {
            Log.e("Storage", "Fail to access external storage", e);
            return -3L;
        }
    }

    static int getStorageHintInfo() {
        long availableSpace = getAvailableSpace();
        if (availableSpace > LOW_STORAGE_THRESHOLD) {
            availableSpace -= LOW_STORAGE_THRESHOLD;
        } else if (availableSpace > 0) {
            availableSpace = 0;
        }
        if (availableSpace == -1 || availableSpace == -3 || availableSpace == -2) {
            return R.string.can_not_use_storage;
        }
        if (availableSpace <= 0) {
            return R.string.storage_full;
        }
        return 1;
    }

    static boolean isStorageReady() {
        Log.i("Storage", "isStorageReady() mount point = " + sMountPoint + ", return " + sIsStorageReady);
        return sIsStorageReady;
    }

    static void updateDefaultDirectory() {
        mkFileDir(getFileDirectory());
        initializeStorageState();
    }

    static String getFileDirectory() {
        Log.i("Storage", " isExtendStorageCanUse = " + isExtendStorageCanUse);
        if (isExtendStorageCanUse) {
            return sMountPoint + FOLDER_PATH;
        }
        return DIRECTORY;
    }

    static boolean isSameStorage(Intent intent) {
        String path;
        String str = null;
        StorageVolume storageVolume = (StorageVolume) intent.getParcelableExtra("android.os.storage.extra.STORAGE_VOLUME");
        boolean z = false;
        if (storageVolume == null || !isExtendStorageCanUse) {
            path = null;
        } else {
            str = sMountPoint;
            path = storageVolume.getPath();
            if (str != null && str.equals(path)) {
                z = true;
            }
        }
        Log.d("Storage", "isSameStorage() mountPoint=" + str + ", intentPath=" + path + ", return " + z);
        return z;
    }

    private static void setStorageReady(boolean z) {
        Log.d("Storage", "setStorageReady(" + z + ") sIsStorageReady=" + sIsStorageReady);
        sIsStorageReady = z;
    }

    private static void initializeStorageState() {
        String defaultPath = null;
        try {
            defaultPath = StorageManagerEx.getDefaultPath();
            isExtendStorageCanUse = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (defaultPath != null) {
            sMountPoint = defaultPath;
            setStorageReady("mounted".equals(sStorageManager.getVolumeState(sMountPoint)));
        } else {
            setStorageReady("mounted".equals(Environment.getExternalStorageState()));
        }
        Log.d("Storage", "initializeStorageState()  sMountPoint=" + sMountPoint);
    }

    private static void initializeStorageManager() {
        if (sStorageManager == null) {
            try {
                sStorageManager = new StorageManager(sContext, null);
            } catch (ServiceManager.ServiceNotFoundException e) {
                e.printStackTrace();
            } catch (IllegalStateException e2) {
                e2.printStackTrace();
            }
        }
    }

    private static void mkFileDir(String str) {
        File file = new File(str);
        if (!file.exists()) {
            Log.d("Storage", "dir not exit,will create this, path = " + str);
            file.mkdirs();
        }
    }
}
