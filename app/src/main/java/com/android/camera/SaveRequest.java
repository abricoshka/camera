package com.android.camera;

import android.location.Location;
import android.net.Uri;
import com.android.camera.FileSaver;
import com.android.camera.FileSaverService;

/* loaded from: classes.dex */
public interface SaveRequest {
    void addRequest();

    Thumbnail createThumbnail(int i);

    int getDataSize();

    String getFilePath();

    FileSaverService.FileSaverListener getFileSaverListener();

    String getTempFilePath();

    Uri getUri();

    boolean isIgnoreThumbnail();

    boolean isQueueFull();

    void notifyListener();

    void prepareRequest();

    void releaseUri();

    void saveRequest();

    void setData(byte[] bArr);

    void setDuration(long j);

    void setFileName(String str);

    void setIgnoreThumbnail(boolean z);

    void setJpegRotation(int i);

    void setListener(FileSaver.FileSaverListener fileSaverListener);

    void setLocation(Location location);

    void setSize(int i, int i2);

    void setSlowMotionSpeed(int i);

    void setTag(int i);

    void setTempPath(String str);

    void updateDataTaken(long j);
}
