package com.mediatek.camera.p005v2.services;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;

/* loaded from: classes.dex */
public interface FileSaver {

    public interface OnFileSavedListener {
        void onMediaSaved(Uri uri);
    }

    public interface QueueListener {
        void onQueueStatus(boolean z);
    }

    void addImage(byte[] bArr, ContentValues contentValues, OnFileSavedListener onFileSavedListener, ContentResolver contentResolver);

    void addVideo(String str, ContentValues contentValues, OnFileSavedListener onFileSavedListener, ContentResolver contentResolver);
}
