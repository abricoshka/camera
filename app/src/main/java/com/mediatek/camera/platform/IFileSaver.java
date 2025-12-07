package com.mediatek.camera.platform;

import android.location.Location;
import android.net.Uri;
import com.android.camera.SaveRequest;

/* loaded from: classes.dex */
public interface IFileSaver {

    public interface OnFileSavedListener {
        void onFileSaved(Uri uri);
    }

    long getAvailableSpace();

    SaveRequest getVideoSaveRequest();

    long getWaitingDataSize();

    void init(FILE_TYPE file_type, int i, String str, int i2);

    boolean isEnoughSpace();

    boolean savePhotoFile(byte[] bArr, String str, long j, Location location, int i, OnFileSavedListener onFileSavedListener);

    boolean saveRawFile(byte[] bArr, int i, int i2, String str, long j, Location location, int i3, OnFileSavedListener onFileSavedListener);

    boolean saveVideoFile(Location location, String str, long j, int i, OnFileSavedListener onFileSavedListener);

    void setRawFlagEnabled(boolean z);

    void waitDone();

    public enum FILE_TYPE {
        JPEG,
        RAW,
        VIDEO,
        PIPVIDEO,
        PANORAMA,
        SLOWMOTION,
        REFOCUSIMAGE;

        /* renamed from: values, reason: to resolve conflict with enum method */
        public static FILE_TYPE[] valuesCustom() {
            return values();
        }
    }
}
