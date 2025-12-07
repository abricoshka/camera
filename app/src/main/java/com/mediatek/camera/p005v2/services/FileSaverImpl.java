package com.mediatek.camera.p005v2.services;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import com.mediatek.camera.debug.LogHelper;
import com.mediatek.camera.p005v2.services.FileSaver;
import java.io.File;

/* loaded from: classes.dex */
public class FileSaverImpl implements FileSaver {
    private static final LogHelper.Tag TAG = new LogHelper.Tag(FileSaverImpl.class.getSimpleName());
    private long mMemoryUse = 0;
    private FileSaver.QueueListener mQueueListener;

    @Override // com.mediatek.camera.p005v2.services.FileSaver
    public void addImage(byte[] bArr, ContentValues contentValues, FileSaver.OnFileSavedListener onFileSavedListener, ContentResolver contentResolver) {
        if (isQueueFull()) {
            LogHelper.m24e(TAG, "Cannot add image when the queue is full");
        } else {
            this.mMemoryUse += bArr.length;
            new ImageSaveTask(bArr, contentValues, onFileSavedListener, contentResolver).execute(new Void[0]);
        }
    }

    @Override // com.mediatek.camera.p005v2.services.FileSaver
    public void addVideo(String str, ContentValues contentValues, FileSaver.OnFileSavedListener onFileSavedListener, ContentResolver contentResolver) {
        new VideoSaveTask(str, contentValues, onFileSavedListener, contentResolver).execute(new Void[0]);
    }

    public boolean isQueueFull() {
        return this.mMemoryUse >= 20971520;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onQueueAvailable() {
        if (this.mQueueListener != null) {
            this.mQueueListener.onQueueStatus(false);
        }
    }

    private class ImageSaveTask extends AsyncTask<Void, Void, Uri> {
        private byte[] mData;
        private final FileSaver.OnFileSavedListener mListener;
        private final ContentResolver mResolver;
        private final ContentValues mValues;

        public ImageSaveTask(byte[] bArr, ContentValues contentValues, FileSaver.OnFileSavedListener onFileSavedListener, ContentResolver contentResolver) {
            this.mData = bArr;
            this.mValues = new ContentValues(contentValues);
            this.mListener = onFileSavedListener;
            this.mResolver = contentResolver;
        }

        @Override // android.os.AsyncTask
        protected void onPreExecute() {
        }

        /* JADX INFO: Access modifiers changed from: protected */
        /* JADX WARN: Can't wrap try/catch for region: R(7:(2:56|10)|(3:50|11|(2:54|13))|58|14|(1:16)(1:36)|17|18) */
        /* JADX WARN: Code restructure failed: missing block: B:38:0x00e9, code lost:
        
            r0 = move-exception;
         */
        /* JADX WARN: Code restructure failed: missing block: B:39:0x00ea, code lost:
        
            com.mediatek.camera.debug.LogHelper.m25e(com.mediatek.camera.p005v2.services.FileSaverImpl.TAG, "[saveImageToDatabase]Failed to write MediaStore,UnsupportedOperationException:", r0);
         */
        /* JADX WARN: Code restructure failed: missing block: B:40:0x00f5, code lost:
        
            r0 = move-exception;
         */
        /* JADX WARN: Code restructure failed: missing block: B:41:0x00f6, code lost:
        
            com.mediatek.camera.debug.LogHelper.m25e(com.mediatek.camera.p005v2.services.FileSaverImpl.TAG, "[saveImageToDatabase]Failed to write MediaStore,IllegalArgumentException:", r0);
         */
        /* JADX WARN: Multi-variable type inference failed */
        /* JADX WARN: Removed duplicated region for block: B:16:0x0075 A[Catch: UnsupportedOperationException -> 0x00e9, IllegalArgumentException -> 0x00f5, TryCatch #8 {IllegalArgumentException -> 0x00f5, UnsupportedOperationException -> 0x00e9, blocks: (B:14:0x0063, B:16:0x0075, B:17:0x007f, B:36:0x00d9), top: B:58:0x0063 }] */
        /* JADX WARN: Removed duplicated region for block: B:36:0x00d9 A[Catch: UnsupportedOperationException -> 0x00e9, IllegalArgumentException -> 0x00f5, TRY_ENTER, TRY_LEAVE, TryCatch #8 {IllegalArgumentException -> 0x00f5, UnsupportedOperationException -> 0x00e9, blocks: (B:14:0x0063, B:16:0x0075, B:17:0x007f, B:36:0x00d9), top: B:58:0x0063 }] */
        /* JADX WARN: Type inference failed for: r1v14, types: [java.lang.Object, java.lang.String] */
        /* JADX WARN: Type inference failed for: r1v3, types: [java.lang.StringBuilder] */
        /* JADX WARN: Type inference failed for: r1v4 */
        /* JADX WARN: Type inference failed for: r1v5, types: [java.io.FileOutputStream] */
        /* JADX WARN: Type inference failed for: r1v7 */
        @Override // android.os.AsyncTask
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        public android.net.Uri doInBackground(java.lang.Void... r6) throws java.lang.Throwable {
            /*
                Method dump skipped, instructions count: 261
                To view this dump change 'Code comments level' option to 'DEBUG'
            */
            throw new UnsupportedOperationException("Method not decompiled: com.mediatek.camera.v2.services.FileSaverImpl.ImageSaveTask.doInBackground(java.lang.Void[]):android.net.Uri");
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(Uri uri) {
            if (this.mListener != null) {
                this.mListener.onMediaSaved(uri);
            }
            boolean zIsQueueFull = FileSaverImpl.this.isQueueFull();
            FileSaverImpl.this.mMemoryUse -= this.mData.length;
            if (FileSaverImpl.this.isQueueFull() != zIsQueueFull) {
                FileSaverImpl.this.onQueueAvailable();
            }
        }
    }

    private class VideoSaveTask extends AsyncTask<Void, Void, Uri> {
        private final FileSaver.OnFileSavedListener mListener;
        private final String mPath;
        private final ContentResolver mResolver;
        private final ContentValues mValues;

        public VideoSaveTask(String str, ContentValues contentValues, FileSaver.OnFileSavedListener onFileSavedListener, ContentResolver contentResolver) {
            this.mPath = str;
            this.mValues = contentValues;
            this.mListener = onFileSavedListener;
            this.mResolver = contentResolver;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public Uri doInBackground(Void... voidArr) {
            new File(this.mPath).renameTo(new File(this.mValues.getAsString("_data")));
            try {
                try {
                    Uri uriInsert = this.mResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, this.mValues);
                    LogHelper.m27v(FileSaverImpl.TAG, "Current video URI: " + uriInsert);
                    return uriInsert;
                } catch (Exception e) {
                    LogHelper.m25e(FileSaverImpl.TAG, "failed to add video to media store", e);
                    LogHelper.m27v(FileSaverImpl.TAG, "Current video URI: " + ((Object) null));
                    return null;
                }
            } catch (Throwable th) {
                LogHelper.m27v(FileSaverImpl.TAG, "Current video URI: " + ((Object) null));
                throw th;
            }
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(Uri uri) {
            if (this.mListener != null) {
                this.mListener.onMediaSaved(uri);
            }
        }
    }
}
