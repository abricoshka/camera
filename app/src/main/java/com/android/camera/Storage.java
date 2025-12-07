package com.android.camera;

import android.content.Context;
import android.os.Environment;
import android.os.ServiceManager;
import android.os.StatFs;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import java.io.File;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicLong;
import utils.StorageManagerEx;

/* loaded from: classes.dex */
public class Storage {
    private static final AtomicLong LEFT_SPACE;
    public static final long LOW_STORAGE_THRESHOLD;
    public static final long RECORD_LOW_STORAGE_THRESHOLD;
    private static Context mContext;
    private static boolean mIsExtendStorageCanUse;
    private static String sMountPoint;
    private static StorageManager sStorageManager;
    private static boolean sStorageReady;
    public static final String DCIM = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString();
    private static final String DIRECTORY = DCIM + "/Camera";
    private static final String FOLDER_PATH = "/" + Environment.DIRECTORY_DCIM + "/Camera";
    public static final String BUCKET_ID = String.valueOf(DIRECTORY.toLowerCase().hashCode());

    static {
        if (FeatureSwitcher.isMtkFatOnNand() || FeatureSwitcher.isGmoROM()) {
            LOW_STORAGE_THRESHOLD = 10000000L;
            RECORD_LOW_STORAGE_THRESHOLD = 9600000L;
            Log.m5d("Storage", "LOW_STORAGE_THRESHOLD= 10000000");
        } else {
            LOW_STORAGE_THRESHOLD = 50000000L;
            RECORD_LOW_STORAGE_THRESHOLD = 48000000L;
            Log.m5d("Storage", "LOW_STORAGE_THRESHOLD= 50000000");
        }
        mIsExtendStorageCanUse = false;
        LEFT_SPACE = new AtomicLong(0L);
    }

    private static StorageManager getStorageManager() {
        if (sStorageManager == null) {
            try {
                sStorageManager = new StorageManager(mContext, null);
            } catch (ServiceManager.ServiceNotFoundException e) {
                e.printStackTrace();
            } catch (IllegalStateException e2) {
                e2.printStackTrace();
            }
        }
        return sStorageManager;
    }

    public static void setContext(Context context) {
        mContext = context;
    }

    public static int getBytePerImage(int i, int i2) {
        if (i == 0) {
            return (int) ((i2 / 76800.0d) * 13312.0d);
        }
        if (1 == i) {
            return 163840;
        }
        return 1500000;
    }

    public static String getInternalVolumePath() {
        StorageManager storageManager = getStorageManager();
        StorageVolume[] volumeList = storageManager.getVolumeList();
        for (int i = 0; i < volumeList.length; i++) {
            if (!volumeList[i].isRemovable() && "mounted".equals(storageManager.getVolumeState(volumeList[i].getPath()))) {
                return volumeList[i].getPath();
            }
        }
        return null;
    }

    public static long getAvailableSpace() {
        String externalStorageState;
        if (mIsExtendStorageCanUse) {
            externalStorageState = getStorageManager().getVolumeState(sMountPoint);
        } else {
            externalStorageState = Environment.getExternalStorageState();
        }
        if ("checking".equals(externalStorageState)) {
            return -2L;
        }
        if (!"mounted".equals(externalStorageState)) {
            return -1L;
        }
        File file = new File(getFileDirectory());
        file.mkdirs();
        boolean zIsDirectory = file.isDirectory();
        boolean zCanWrite = file.canWrite();
        if (!zIsDirectory || (!zCanWrite)) {
            Log.m5d("Storage", "getAvailableSpace() isDirectory=" + zIsDirectory + ", canWrite=" + zCanWrite);
            return -4L;
        }
        try {
            StatFs statFs = new StatFs(getFileDirectory());
            return statFs.getBlockSize() * statFs.getAvailableBlocks();
        } catch (Exception e) {
            Log.m7e("Storage", "Fail to access external storage", e);
            return -3L;
        }
    }

    public static void ensureOSXCompatible() {
        File file = new File(DCIM, "100ANDRO");
        if (!(!file.exists() ? file.mkdirs() : true)) {
            Log.m6e("Storage", "Failed to create " + file.getPath());
        }
    }

    public static String getMountPoint() {
        if (mIsExtendStorageCanUse) {
            return sMountPoint;
        }
        return DIRECTORY;
    }

    public static boolean isStorageReady() {
        Log.m5d("Storage", "isStorageReady() mount point = " + sMountPoint + ", return " + sStorageReady);
        return sStorageReady;
    }

    public static void setStorageReady(boolean z) {
        Log.m5d("Storage", "setStorageReady(" + z + ") sStorageReady=" + sStorageReady);
        sStorageReady = z;
    }

    public static void mkFileDir(String str) {
        File file = new File(str);
        if (!file.exists()) {
            Log.m5d("Storage", "dir not exit,will create this, path = " + str);
            file.mkdirs();
        }
    }

    public static boolean initializeStorageState() {
        String defaultPath = null;
        StorageManager storageManager = getStorageManager();
        try {
            defaultPath = StorageManagerEx.getDefaultPath();
            mIsExtendStorageCanUse = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        boolean z = false;
        Log.m8i("Storage", "[initializeStorageState] defaultPath = " + defaultPath);
        if (defaultPath != null) {
            String str = sMountPoint;
            sMountPoint = defaultPath;
            if (str != null && str.equalsIgnoreCase(sMountPoint)) {
                z = true;
            }
            setStorageReady("mounted".equals(storageManager.getVolumeState(sMountPoint)));
            Log.m5d("Storage", "initializeStorageState() old=" + str + ", sMountPoint=" + sMountPoint + " return " + z);
        } else {
            setStorageReady("mounted".equals(Environment.getExternalStorageState()));
        }
        return z;
    }

    public static boolean updateDefaultDirectory() {
        mkFileDir(getFileDirectory());
        return initializeStorageState();
    }

    public static boolean updateDirectory(String str) {
        StorageManager storageManager = getStorageManager();
        boolean z = false;
        String str2 = sMountPoint;
        sMountPoint = str;
        if (str2 != null && str2.equalsIgnoreCase(sMountPoint)) {
            z = true;
        }
        mkFileDir(getFileDirectory());
        setStorageReady("mounted".equals(storageManager.getVolumeState(sMountPoint)));
        Log.m5d("Storage", "updateDefaultDirectory() old=" + str2 + ", sMountPoint=" + sMountPoint + " return " + z);
        return z;
    }

    public static String getFileDirectory() {
        Log.m8i("Storage", "[getFileDirectory] mIsExtendStorageCanUse = " + mIsExtendStorageCanUse);
        if (mIsExtendStorageCanUse) {
            return sMountPoint + FOLDER_PATH;
        }
        return DIRECTORY;
    }

    public static String getCameraScreenNailPath() {
        String str = sMountPoint + FOLDER_PATH;
        String str2 = "/local/all/" + getBucketId(getFileDirectory());
        Log.m5d("Storage", "getCameraScreenNailPath() , return " + str2);
        return str2;
    }

    public static String getBucketId(String str) {
        return String.valueOf(str.toLowerCase(Locale.ENGLISH).hashCode());
    }

    public static String getBucketId() {
        return getBucketId(getFileDirectory());
    }

    public static String generateFileName(String str, int i) {
        if (i == 1 || i == 3) {
            return str + ".mpo";
        }
        if (i == 2) {
            return str + ".jps";
        }
        if (i == 0) {
            return str + ".jpg";
        }
        if (i == 4) {
            return str + ".dng";
        }
        return str;
    }

    public static String generateMimetype(String str, int i) {
        if (i == 1 || i == 3) {
            return "image/mpo";
        }
        if (i == 2) {
            return "image/x-jps";
        }
        if (i == 4) {
            return "image/x-adobe-dng";
        }
        return "image/jpeg";
    }

    public static String generateFilepath(String str) {
        int iLastIndexOf = str.lastIndexOf("/");
        if (iLastIndexOf != -1) {
            mkFileDir(getFileDirectory() + "/" + str.substring(0, iLastIndexOf));
        }
        return getFileDirectory() + '/' + str;
    }

    public static long getLeftSpace() {
        Log.m5d("Storage", "getLeftSpace() return " + LEFT_SPACE.get());
        return LEFT_SPACE.get();
    }

    public static void setLeftSpace(long j) {
        LEFT_SPACE.set(j);
        Log.m5d("Storage", "setLeftSpace(" + j + ")");
    }
}
