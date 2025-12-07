package com.android.camera;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.location.Location;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.IBinder;
import android.provider.MediaStore;
import com.android.camera.FileSaverService;
import com.android.camera.Util;
import com.mediatek.camera.R;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/* loaded from: classes.dex */
public class FileSaver {
    private ContentResolver mContentResolver;
    private CameraActivity mContext;
    private HashMap<Integer, Util.ImageFileNamer> mFileNamer;
    private FileSaverService mSaverService;
    private List<FileSaverListener> mSaverListenerList = new CopyOnWriteArrayList();
    private Object mSaveServiceObject = new Object();
    private boolean mIsRawEnabled = false;
    private ServiceConnection mConnection = new ServiceConnection() { // from class: com.android.camera.FileSaver.1
        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            FileSaver.this.mSaverService = ((FileSaverService.LocalBinder) iBinder).getService();
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName componentName) {
            synchronized (FileSaver.this.mSaveServiceObject) {
                if (FileSaver.this.mSaverService != null) {
                    FileSaver.this.mSaverService = null;
                }
            }
        }
    };
    private FileSaverService.FileSaverListener mFileSaverListener = new FileSaverService.FileSaverListener() { // from class: com.android.camera.FileSaver.2
        @Override // com.android.camera.FileSaverService.FileSaverListener
        public void onFileSaved(SaveRequest saveRequest) {
            Iterator it = FileSaver.this.mSaverListenerList.iterator();
            while (it.hasNext()) {
                ((FileSaverListener) it.next()).onFileSaved(saveRequest);
            }
        }

        @Override // com.android.camera.FileSaverService.FileSaverListener
        public void onSaveDone() {
            synchronized (FileSaver.this) {
                FileSaver.this.notifyAll();
            }
        }
    };

    public interface FileSaverListener {
        void onFileSaved(SaveRequest saveRequest);
    }

    public FileSaver(CameraActivity cameraActivity) {
        this.mContext = cameraActivity;
        this.mContentResolver = this.mContext.getContentResolver();
    }

    public void bindSaverService() {
        this.mContext.bindService(new Intent(this.mContext, (Class<?>) FileSaverService.class), this.mConnection, 1);
    }

    public void unBindSaverService() {
        synchronized (this.mSaveServiceObject) {
            if (this.mSaverService != null) {
                this.mSaverService = null;
            }
        }
        if (this.mConnection != null) {
            this.mContext.unbindService(this.mConnection);
        }
    }

    public void waitDone() {
        Log.m5d("FileSaver", "[waitDone]");
        if (this.mSaverService == null) {
            Log.m6e("FileSaver", "[waitDone]mSaverService is null,return.");
            return;
        }
        synchronized (this) {
            if (this.mSaverService != null && (!this.mSaverService.isNoneSaveTask())) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    Log.m7e("FileSaver", "[waitDone]exception :", e);
                }
            }
        }
    }

    public long getWaitingDataSize() {
        return this.mSaverService.getWaitingDataSize();
    }

    public void enableRawFlag(boolean z) {
        this.mIsRawEnabled = z;
    }

    public SaveRequest preparePhotoRequest(int i, int i2) {
        SaveRequest photoOperator;
        PanoOperator panoOperator = null;
        byte b = 0;
        if (i == 2) {
            photoOperator = new PanoOperator(this, i2, panoOperator);
        } else {
            photoOperator = new PhotoOperator(this, i2, b == true ? 1 : 0);
        }
        photoOperator.prepareRequest();
        this.mContext.applyParameterForCapture(photoOperator);
        return photoOperator;
    }

    public SaveRequest copyPhotoRequest(SaveRequest saveRequest) {
        if (!(saveRequest instanceof PhotoOperator)) {
            return null;
        }
        return ((PhotoOperator) saveRequest).copyRequest();
    }

    public SaveRequest prepareVideoRequest(int i, int i2, String str, int i3) {
        VideoOperator videoOperator = new VideoOperator(this, i, i2, str, i3, null);
        videoOperator.prepareRequest();
        return videoOperator;
    }

    public boolean addListener(FileSaverListener fileSaverListener) {
        if (!this.mSaverListenerList.contains(fileSaverListener)) {
            return this.mSaverListenerList.add(fileSaverListener);
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void addSaveRequest(SaveRequest saveRequest) {
        synchronized (this) {
            synchronized (this.mSaveServiceObject) {
                if (this.mSaverService == null) {
                    Log.m6e("FileSaver", "[addSaveRequest]mSaverService is null,return.");
                    return;
                }
                while (this.mSaverService.getWaitingCount() >= 100) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        Log.m7e("FileSaver", "[addSaveRequest]exception:", e);
                    }
                }
                Log.m5d("FileSaver", "[addSaveRequest]mSaverService.addSaveRequest...");
                this.mSaverService.addSaveRequest(saveRequest);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String convertOutputFormatToFileExt(int i) {
        if (i == 2) {
            return ".mp4";
        }
        return ".3gp";
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String convertOutputFormatToMimeType(int i) {
        if (i == 2) {
            return "video/mp4";
        }
        return "video/3gpp";
    }

    /* JADX INFO: Access modifiers changed from: private */
    public synchronized String createName(int i, long j) {
        String strGenerateName;
        synchronized (this) {
            if (this.mFileNamer == null) {
                this.mFileNamer = new HashMap<>();
                Util.ImageFileNamer imageFileNamer = new Util.ImageFileNamer(this.mContext.getString(R.string.image_file_name_format));
                this.mFileNamer.put(0, imageFileNamer);
                this.mFileNamer.put(2, imageFileNamer);
                this.mFileNamer.put(1, new Util.ImageFileNamer(this.mContext.getString(R.string.video_file_name_format)));
            }
            strGenerateName = this.mFileNamer.get(Integer.valueOf(i)) != null ? this.mFileNamer.get(Integer.valueOf(i)).generateName(j) : null;
            Log.m5d("FileSaver", "[createName]fileType = " + i + ",name = " + strGenerateName);
        }
        return strGenerateName;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public synchronized String createRawName(int i, long j, int i2) {
        String strGenerateRawName;
        synchronized (this) {
            if (this.mFileNamer == null) {
                this.mFileNamer = new HashMap<>();
                Util.ImageFileNamer imageFileNamer = new Util.ImageFileNamer(this.mContext.getString(R.string.image_file_name_format));
                this.mFileNamer.put(0, imageFileNamer);
                this.mFileNamer.put(2, imageFileNamer);
                this.mFileNamer.put(1, new Util.ImageFileNamer(this.mContext.getString(R.string.video_file_name_format)));
            }
            strGenerateRawName = this.mFileNamer.get(Integer.valueOf(i)) != null ? this.mFileNamer.get(Integer.valueOf(i)).generateRawName(j, i2) : null;
            Log.m5d("FileSaver", "[createName]fileType = " + i + ",name = " + strGenerateRawName);
        }
        return strGenerateRawName;
    }

    private abstract class RequestOperator implements SaveRequest {
        byte[] mData;
        long mDataSize;
        long mDateTaken;
        long mDuration;
        String mFileName;
        String mFilePath;
        int mFileType;
        long mFocusValueHigh;
        long mFocusValueLow;
        int mHeight;
        boolean mIgnoreThumbnail;
        FileSaverListener mListener;
        Location mLocation;
        String mMimeType;
        int mOrientation;
        String mResolution;
        int mSlowMotionSpeed;
        int mTag;
        String mTempFilePath;
        int mTempJpegRotation;
        int mTempOutputFileFormat;
        int mTempPictureType;
        String mTitle;
        Uri mUri;
        int mWidth;

        /* synthetic */ RequestOperator(FileSaver fileSaver, RequestOperator requestOperator) {
            this();
        }

        private RequestOperator() {
        }

        @Override // com.android.camera.SaveRequest
        public boolean isQueueFull() {
            return FileSaver.this.mSaverService.isQueueFull();
        }

        @Override // com.android.camera.SaveRequest
        public boolean isIgnoreThumbnail() {
            return this.mIgnoreThumbnail;
        }

        @Override // com.android.camera.SaveRequest
        public String getTempFilePath() {
            return this.mTempFilePath;
        }

        @Override // com.android.camera.SaveRequest
        public String getFilePath() {
            return this.mFilePath;
        }

        @Override // com.android.camera.SaveRequest
        public int getDataSize() {
            if (this.mData == null) {
                return 0;
            }
            return this.mData.length;
        }

        @Override // com.android.camera.SaveRequest
        public Uri getUri() {
            return this.mUri;
        }

        @Override // com.android.camera.SaveRequest
        public void releaseUri() {
            this.mUri = null;
        }

        @Override // com.android.camera.SaveRequest
        public void setIgnoreThumbnail(boolean z) {
            this.mIgnoreThumbnail = z;
        }

        @Override // com.android.camera.SaveRequest
        public void setData(byte[] bArr) {
            if (bArr == null) {
                Log.m11w("FileSaver", "[setData]data is null,please check the reason!");
            }
            this.mData = bArr;
        }

        @Override // com.android.camera.SaveRequest
        public void setSize(int i, int i2) {
            this.mWidth = i;
            this.mHeight = i2;
        }

        @Override // com.android.camera.SaveRequest
        public void setDuration(long j) {
            this.mDuration = j;
        }

        @Override // com.android.camera.SaveRequest
        public void setSlowMotionSpeed(int i) {
            this.mSlowMotionSpeed = i;
        }

        @Override // com.android.camera.SaveRequest
        public void setTag(int i) {
            this.mTag = i;
        }

        @Override // com.android.camera.SaveRequest
        public void setJpegRotation(int i) {
            this.mTempJpegRotation = i;
        }

        @Override // com.android.camera.SaveRequest
        public void setLocation(Location location) {
            this.mLocation = location;
        }

        @Override // com.android.camera.SaveRequest
        public void setTempPath(String str) {
            this.mTempFilePath = str;
        }

        @Override // com.android.camera.SaveRequest
        public void setFileName(String str) {
            this.mFileName = str;
        }

        @Override // com.android.camera.SaveRequest
        public void setListener(FileSaverListener fileSaverListener) {
            this.mListener = fileSaverListener;
        }

        @Override // com.android.camera.SaveRequest
        public void notifyListener() {
            if (this.mListener != null) {
                this.mListener.onFileSaved(this);
            }
        }

        @Override // com.android.camera.SaveRequest
        public void updateDataTaken(long j) {
            this.mDateTaken = j;
        }

        @Override // com.android.camera.SaveRequest
        public FileSaverService.FileSaverListener getFileSaverListener() {
            return FileSaver.this.mFileSaverListener;
        }

        public void saveImageToDatabase(RequestOperator requestOperator) {
            ContentValues contentValues = new ContentValues(14);
            contentValues.put("title", requestOperator.mTitle);
            contentValues.put("_display_name", requestOperator.mFileName);
            contentValues.put("datetaken", Long.valueOf(requestOperator.mDateTaken));
            contentValues.put("mime_type", requestOperator.mMimeType);
            contentValues.put("_data", requestOperator.mFilePath);
            contentValues.put("_size", Long.valueOf(requestOperator.mDataSize));
            if (requestOperator.mLocation != null) {
                contentValues.put("latitude", Double.valueOf(requestOperator.mLocation.getLatitude()));
                contentValues.put("longitude", Double.valueOf(requestOperator.mLocation.getLongitude()));
            }
            contentValues.put("orientation", Integer.valueOf(requestOperator.mOrientation));
            contentValues.put("width", Integer.valueOf(requestOperator.mWidth));
            contentValues.put("height", Integer.valueOf(requestOperator.mHeight));
            try {
                requestOperator.mUri = FileSaver.this.mContentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                if (requestOperator.mUri != null) {
                    FileSaver.this.mContext.addSecureAlbumItemIfNeeded(false, requestOperator.mUri);
                    Log.m5d("FileSaver", "[saveImageToDatabase]mUri = " + requestOperator.mUri);
                }
            } catch (IllegalArgumentException e) {
                Log.m7e("FileSaver", "[saveImageToDatabase]Failed to write MediaStore,IllegalArgumentException:", e);
            } catch (UnsupportedOperationException e2) {
                Log.m7e("FileSaver", "[saveImageToDatabase]Failed to write MediaStore,UnsupportedOperationException:", e2);
            }
        }

        public String toString() {
            return "RequestOperator(mUri=" + this.mUri + ", mTempFilePath=" + this.mTempFilePath + ", mFilePath=" + this.mFilePath + ", mIgnoreThumbnail=" + this.mIgnoreThumbnail + ")";
        }
    }

    private class PhotoOperator extends RequestOperator {
        /* synthetic */ PhotoOperator(FileSaver fileSaver, int i, PhotoOperator photoOperator) {
            this(i);
        }

        private PhotoOperator(int i) {
            super(FileSaver.this, null);
            this.mTempPictureType = i;
        }

        @Override // com.android.camera.SaveRequest
        public void prepareRequest() {
            this.mFileType = 0;
            this.mDateTaken = System.currentTimeMillis();
            Location currentLocation = FileSaver.this.mContext.getLocationManager().getCurrentLocation();
            if (currentLocation != null) {
                this.mLocation = new Location(currentLocation);
            }
        }

        @Override // com.android.camera.SaveRequest
        public void addRequest() {
            if (this.mData == null) {
                Log.m11w("FileSaver", "[addRequest]PhotoOperator,data is null,return!");
            } else {
                FileSaver.this.addSaveRequest(this);
            }
        }

        public PhotoOperator copyRequest() {
            PhotoOperator photoOperator = FileSaver.this.new PhotoOperator(this.mTempPictureType);
            photoOperator.mFileType = 0;
            photoOperator.mDateTaken = System.currentTimeMillis();
            photoOperator.mLocation = this.mLocation;
            photoOperator.mTempJpegRotation = this.mTempJpegRotation;
            return photoOperator;
        }

        @Override // com.android.camera.SaveRequest
        public synchronized void saveRequest() {
            if (this.mData == null) {
                Log.m11w("FileSaver", "[saveRequest]mData is null,return!");
                return;
            }
            this.mDataSize = this.mData.length;
            if (this.mTempPictureType == 4) {
                this.mOrientation = this.mTempJpegRotation;
            } else {
                int orientation = Exif.getOrientation(this.mData);
                this.mFocusValueHigh = Exif.getFocusValueHigh(this.mData);
                this.mFocusValueLow = Exif.getFocusValueLow(this.mData);
                this.mOrientation = orientation;
            }
            if (this.mFileName != null) {
                this.mTitle = this.mFileName.substring(0, this.mFileName.indexOf(46));
            } else {
                this.mTitle = getTitleName();
                this.mFileName = Storage.generateFileName(this.mTitle, this.mTempPictureType);
            }
            this.mFilePath = Storage.generateFilepath(this.mFileName);
            this.mTempFilePath = this.mFilePath + ".tmp";
            saveImageToSDCard(this.mTempFilePath, this.mFilePath, this.mData);
            this.mMimeType = Storage.generateMimetype(this.mTitle, this.mTempPictureType);
            if (this.mTempPictureType != 4) {
                checkDataProperty();
            }
            saveImageToDatabase(this);
        }

        private String getTitleName() {
            if (FileSaver.this.mIsRawEnabled) {
                return FileSaver.this.createRawName(this.mFileType, this.mDateTaken, this.mTempPictureType);
            }
            return FileSaver.this.createName(this.mFileType, this.mDateTaken);
        }

        private void checkDataProperty() {
            ExifInterface exifInterface;
            try {
                exifInterface = new ExifInterface(this.mFilePath);
            } catch (IOException e) {
                e.printStackTrace();
                exifInterface = null;
            }
            if (exifInterface != null) {
                this.mWidth = exifInterface.getAttributeInt("ImageWidth", 0);
                this.mHeight = exifInterface.getAttributeInt("ImageLength", 0);
                return;
            }
            Camera.Size pictureSize = FileSaver.this.mContext.getParameters().getPictureSize();
            if (pictureSize != null) {
                this.mWidth = pictureSize.width;
                this.mHeight = pictureSize.height;
            }
        }

        /* JADX WARN: Multi-variable type inference failed */
        /* JADX WARN: Removed duplicated region for block: B:34:0x004a A[EXC_TOP_SPLITTER, SYNTHETIC] */
        /* JADX WARN: Type inference failed for: r1v6, types: [java.lang.String] */
        /* JADX WARN: Type inference failed for: r1v9, types: [java.lang.String] */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        private void saveImageToSDCard(java.lang.String r5, java.lang.String r6, byte[] r7) throws java.lang.Throwable {
            /*
                r4 = this;
                r2 = 0
                java.io.FileOutputStream r1 = new java.io.FileOutputStream     // Catch: java.io.IOException -> L2a java.lang.Throwable -> L46
                r1.<init>(r5)     // Catch: java.io.IOException -> L2a java.lang.Throwable -> L46
                r1.write(r7)     // Catch: java.lang.Throwable -> L59 java.io.IOException -> L5b
                r1.close()     // Catch: java.lang.Throwable -> L59 java.io.IOException -> L5b
                java.io.File r0 = new java.io.File     // Catch: java.lang.Throwable -> L59 java.io.IOException -> L5b
                r0.<init>(r5)     // Catch: java.lang.Throwable -> L59 java.io.IOException -> L5b
                java.io.File r2 = new java.io.File     // Catch: java.lang.Throwable -> L59 java.io.IOException -> L5b
                r2.<init>(r6)     // Catch: java.lang.Throwable -> L59 java.io.IOException -> L5b
                r0.renameTo(r2)     // Catch: java.lang.Throwable -> L59 java.io.IOException -> L5b
                if (r1 == 0) goto L1e
                r1.close()     // Catch: java.io.IOException -> L1f
            L1e:
                return
            L1f:
                r0 = move-exception
                java.lang.String r1 = "FileSaver"
                java.lang.String r2 = "[saveImageToSDCard]IOException:"
                com.android.camera.Log.m7e(r1, r2, r0)
                goto L1e
            L2a:
                r0 = move-exception
                r1 = r2
            L2c:
                java.lang.String r2 = "FileSaver"
                java.lang.String r3 = "[saveImageToSDCard]Failed to write image,ex:"
                com.android.camera.Log.m7e(r2, r3, r0)     // Catch: java.lang.Throwable -> L59
                if (r1 == 0) goto L1e
                r1.close()     // Catch: java.io.IOException -> L3b
                goto L1e
            L3b:
                r0 = move-exception
                java.lang.String r1 = "FileSaver"
                java.lang.String r2 = "[saveImageToSDCard]IOException:"
                com.android.camera.Log.m7e(r1, r2, r0)
                goto L1e
            L46:
                r0 = move-exception
                r1 = r2
            L48:
                if (r1 == 0) goto L4d
                r1.close()     // Catch: java.io.IOException -> L4e
            L4d:
                throw r0
            L4e:
                r1 = move-exception
                java.lang.String r2 = "FileSaver"
                java.lang.String r3 = "[saveImageToSDCard]IOException:"
                com.android.camera.Log.m7e(r2, r3, r1)
                goto L4d
            L59:
                r0 = move-exception
                goto L48
            L5b:
                r0 = move-exception
                goto L2c
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.camera.FileSaver.PhotoOperator.saveImageToSDCard(java.lang.String, java.lang.String, byte[]):void");
        }

        @Override // com.android.camera.SaveRequest
        public Thumbnail createThumbnail(int i) {
            if (this.mUri == null || this.mData == null) {
                return null;
            }
            Thumbnail thumbnailCreateThumbnail = Thumbnail.createThumbnail(this.mData, this.mOrientation, Integer.highestOneBit((int) Math.ceil(this.mWidth / i)), this.mUri, this.mFilePath);
            Log.m5d("FileSaver", "[createThumbnail]PhotoOperator,mFileName = " + this.mFileName);
            return thumbnailCreateThumbnail;
        }
    }

    private class PanoOperator extends RequestOperator {
        /* synthetic */ PanoOperator(FileSaver fileSaver, int i, PanoOperator panoOperator) {
            this(i);
        }

        private PanoOperator(int i) {
            super(FileSaver.this, null);
            this.mTempPictureType = i;
        }

        @Override // com.android.camera.SaveRequest
        public void prepareRequest() {
            this.mFileType = 2;
            this.mDateTaken = System.currentTimeMillis();
            Location currentLocation = FileSaver.this.mContext.getLocationManager().getCurrentLocation();
            if (currentLocation != null) {
                this.mLocation = new Location(currentLocation);
            }
            this.mTitle = FileSaver.this.createName(this.mFileType, this.mDateTaken);
            this.mFileName = Storage.generateFileName(this.mTitle, this.mTempPictureType);
            this.mFilePath = Storage.generateFilepath(this.mFileName);
            this.mTempFilePath = this.mFilePath + ".tmp";
        }

        @Override // com.android.camera.SaveRequest
        public void addRequest() {
            FileSaver.this.addSaveRequest(this);
        }

        /* JADX WARN: Multi-variable type inference failed */
        /* JADX WARN: Removed duplicated region for block: B:11:0x0058  */
        /* JADX WARN: Removed duplicated region for block: B:37:0x00c5 A[EXC_TOP_SPLITTER, SYNTHETIC] */
        /* JADX WARN: Type inference failed for: r1v0 */
        /* JADX WARN: Type inference failed for: r1v2, types: [java.io.FileOutputStream] */
        /* JADX WARN: Type inference failed for: r1v7, types: [java.lang.String] */
        @Override // com.android.camera.SaveRequest
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        public void saveRequest() throws java.lang.Throwable {
            /*
                r5 = this;
                r2 = 0
                java.io.FileOutputStream r1 = new java.io.FileOutputStream     // Catch: java.io.IOException -> La3 java.lang.Throwable -> Lc1
                java.lang.String r0 = r5.mTempFilePath     // Catch: java.io.IOException -> La3 java.lang.Throwable -> Lc1
                r1.<init>(r0)     // Catch: java.io.IOException -> La3 java.lang.Throwable -> Lc1
                byte[] r0 = r5.mData     // Catch: java.lang.Throwable -> Le0 java.io.IOException -> Le2
                r1.write(r0)     // Catch: java.lang.Throwable -> Le0 java.io.IOException -> Le2
                r1.close()     // Catch: java.lang.Throwable -> Le0 java.io.IOException -> Le2
                java.io.File r0 = new java.io.File     // Catch: java.lang.Throwable -> Le0 java.io.IOException -> Le2
                java.lang.String r2 = r5.mTempFilePath     // Catch: java.lang.Throwable -> Le0 java.io.IOException -> Le2
                r0.<init>(r2)     // Catch: java.lang.Throwable -> Le0 java.io.IOException -> Le2
                java.io.File r2 = new java.io.File     // Catch: java.lang.Throwable -> Le0 java.io.IOException -> Le2
                java.lang.String r3 = r5.mFilePath     // Catch: java.lang.Throwable -> Le0 java.io.IOException -> Le2
                r2.<init>(r3)     // Catch: java.lang.Throwable -> Le0 java.io.IOException -> Le2
                r0.renameTo(r2)     // Catch: java.lang.Throwable -> Le0 java.io.IOException -> Le2
                if (r1 == 0) goto L26
                r1.close()     // Catch: java.io.IOException -> L98
            L26:
                java.io.File r0 = new java.io.File
                java.lang.String r1 = r5.mFilePath
                r0.<init>(r1)
                long r0 = r0.length()
                r5.mDataSize = r0
                android.media.ExifInterface r0 = new android.media.ExifInterface     // Catch: java.io.IOException -> Ld4
                java.lang.String r1 = r5.mFilePath     // Catch: java.io.IOException -> Ld4
                r0.<init>(r1)     // Catch: java.io.IOException -> Ld4
                int r1 = com.android.camera.Util.getExifOrientation(r0)     // Catch: java.io.IOException -> Ld4
                java.lang.String r2 = "ImageWidth"
                r3 = 0
                int r2 = r0.getAttributeInt(r2, r3)     // Catch: java.io.IOException -> Ld4
                java.lang.String r3 = "ImageLength"
                r4 = 0
                int r0 = r0.getAttributeInt(r3, r4)     // Catch: java.io.IOException -> Ld4
                r5.mWidth = r2     // Catch: java.io.IOException -> Ld4
                r5.mHeight = r0     // Catch: java.io.IOException -> Ld4
                r5.mOrientation = r1     // Catch: java.io.IOException -> Ld4
            L54:
                java.lang.String r0 = r5.mFileName
                if (r0 != 0) goto L8a
                com.android.camera.FileSaver r0 = com.android.camera.FileSaver.this
                int r1 = r5.mFileType
                long r2 = r5.mDateTaken
                java.lang.String r0 = com.android.camera.FileSaver.m202wrap3(r0, r1, r2)
                r5.mTitle = r0
                java.lang.String r0 = r5.mTitle
                int r1 = r5.mTempPictureType
                java.lang.String r0 = com.android.camera.Storage.generateFileName(r0, r1)
                r5.mFileName = r0
                java.lang.String r0 = "FileSaver"
                java.lang.StringBuilder r1 = new java.lang.StringBuilder
                r1.<init>()
                java.lang.String r2 = "[saveRequest]PhotoOperator,mFileName = "
                java.lang.StringBuilder r1 = r1.append(r2)
                java.lang.String r2 = r5.mFileName
                java.lang.StringBuilder r1 = r1.append(r2)
                java.lang.String r1 = r1.toString()
                com.android.camera.Log.m5d(r0, r1)
            L8a:
                java.lang.String r0 = r5.mTitle
                int r1 = r5.mTempPictureType
                java.lang.String r0 = com.android.camera.Storage.generateMimetype(r0, r1)
                r5.mMimeType = r0
                r5.saveImageToDatabase(r5)
                return
            L98:
                r0 = move-exception
                java.lang.String r1 = "FileSaver"
                java.lang.String r2 = "[saveRequest]PanoOperator,exception:"
                com.android.camera.Log.m7e(r1, r2, r0)
                goto L26
            La3:
                r0 = move-exception
                r1 = r2
            La5:
                java.lang.String r2 = "FileSaver"
                java.lang.String r3 = "[saveRequest]PanoOperator,Failed to write image"
                com.android.camera.Log.m7e(r2, r3, r0)     // Catch: java.lang.Throwable -> Le0
                if (r1 == 0) goto L26
                r1.close()     // Catch: java.io.IOException -> Lb5
                goto L26
            Lb5:
                r0 = move-exception
                java.lang.String r1 = "FileSaver"
                java.lang.String r2 = "[saveRequest]PanoOperator,exception:"
                com.android.camera.Log.m7e(r1, r2, r0)
                goto L26
            Lc1:
                r0 = move-exception
                r1 = r2
            Lc3:
                if (r1 == 0) goto Lc8
                r1.close()     // Catch: java.io.IOException -> Lc9
            Lc8:
                throw r0
            Lc9:
                r1 = move-exception
                java.lang.String r2 = "FileSaver"
                java.lang.String r3 = "[saveRequest]PanoOperator,exception:"
                com.android.camera.Log.m7e(r2, r3, r1)
                goto Lc8
            Ld4:
                r0 = move-exception
                java.lang.String r1 = "FileSaver"
                java.lang.String r2 = "[saveRequest]PanoOperator,cannot read exif:"
                com.android.camera.Log.m7e(r1, r2, r0)
                goto L54
            Le0:
                r0 = move-exception
                goto Lc3
            Le2:
                r0 = move-exception
                goto La5
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.camera.FileSaver.PanoOperator.saveRequest():void");
        }

        @Override // com.android.camera.SaveRequest
        public Thumbnail createThumbnail(int i) {
            if (this.mUri == null) {
                return null;
            }
            Thumbnail thumbnailCreateThumbnail = Thumbnail.createThumbnail(this.mFilePath, this.mOrientation, Integer.highestOneBit(Math.max((int) Math.ceil(this.mWidth / FileSaver.this.mContext.getPreviewFrameWidth()), (int) Math.ceil(this.mWidth / FileSaver.this.mContext.getPreviewFrameHeight()))), this.mUri);
            Log.m5d("FileSaver", "[createThumbnail]PanoOperator,mFileName = " + this.mFileName);
            return thumbnailCreateThumbnail;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:23:0x0070  */
    /* JADX WARN: Type inference failed for: r0v4, types: [android.content.ContentResolver] */
    /* JADX WARN: Type inference failed for: r1v1, types: [android.net.Uri] */
    /* JADX WARN: Type inference failed for: r1v2 */
    /* JADX WARN: Type inference failed for: r1v4, types: [android.database.Cursor] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public boolean isColumExistInDbForVideo() throws java.lang.Throwable {
        /*
            r9 = this;
            r8 = 0
            r7 = -1
            r6 = 0
            android.net.Uri r0 = android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            android.net.Uri$Builder r0 = r0.buildUpon()
            java.lang.String r1 = "limit"
            java.lang.String r2 = "1"
            android.net.Uri$Builder r0 = r0.appendQueryParameter(r1, r2)
            android.net.Uri r1 = r0.build()
            com.android.camera.CameraActivity r0 = r9.mContext
            android.content.ContentResolver r0 = r0.getContentResolver()
            r2 = 0
            r3 = 0
            r4 = 0
            r5 = 0
            android.database.Cursor r1 = r0.query(r1, r2, r3, r4, r5)     // Catch: java.lang.Exception -> L5f java.lang.Throwable -> L6c
            if (r1 == 0) goto L78
            java.lang.String r0 = "orientation"
            int r0 = r1.getColumnIndex(r0)     // Catch: java.lang.Throwable -> L74 java.lang.Exception -> L76
        L2e:
            if (r0 == r7) goto L5d
            r2 = 1
        L31:
            if (r1 == 0) goto L36
            r1.close()
        L36:
            r1 = r2
        L37:
            java.lang.String r2 = "FileSaver"
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "[isColumnExistInDB] - index = "
            java.lang.StringBuilder r3 = r3.append(r4)
            java.lang.StringBuilder r0 = r3.append(r0)
            java.lang.String r3 = " isInDB "
            java.lang.StringBuilder r0 = r0.append(r3)
            java.lang.StringBuilder r0 = r0.append(r1)
            java.lang.String r0 = r0.toString()
            com.android.camera.Log.m8i(r2, r0)
            return r1
        L5d:
            r2 = r8
            goto L31
        L5f:
            r0 = move-exception
            r1 = r6
        L61:
            r0.printStackTrace()     // Catch: java.lang.Throwable -> L74
            if (r1 == 0) goto L69
            r1.close()
        L69:
            r0 = r7
            r1 = r8
            goto L37
        L6c:
            r0 = move-exception
            r1 = r6
        L6e:
            if (r1 == 0) goto L73
            r1.close()
        L73:
            throw r0
        L74:
            r0 = move-exception
            goto L6e
        L76:
            r0 = move-exception
            goto L61
        L78:
            r0 = r7
            goto L2e
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.camera.FileSaver.isColumExistInDbForVideo():boolean");
    }

    private class VideoOperator extends RequestOperator {
        /* synthetic */ VideoOperator(FileSaver fileSaver, int i, int i2, String str, int i3, VideoOperator videoOperator) {
            this(i, i2, str, i3);
        }

        private VideoOperator(int i, int i2, String str, int i3) {
            super(FileSaver.this, null);
            this.mFileType = i;
            this.mTempOutputFileFormat = i2;
            this.mResolution = str;
            this.mOrientation = i3;
        }

        @Override // com.android.camera.SaveRequest
        public void prepareRequest() {
            this.mFileType = 1;
            this.mDateTaken = System.currentTimeMillis();
            this.mTitle = FileSaver.this.createName(this.mFileType, this.mDateTaken);
            this.mFileName = this.mTitle + FileSaver.this.convertOutputFormatToFileExt(this.mTempOutputFileFormat);
            this.mMimeType = FileSaver.this.convertOutputFormatToMimeType(this.mTempOutputFileFormat);
            this.mFilePath = Storage.generateFilepath(this.mFileName);
        }

        @Override // com.android.camera.SaveRequest
        public void addRequest() {
            FileSaver.this.addSaveRequest(this);
        }

        @Override // com.android.camera.SaveRequest
        public void saveRequest() {
            try {
                File file = new File(this.mTempFilePath);
                File file2 = new File(this.mFilePath);
                file.renameTo(file2);
                this.mDataSize = file2.length();
                ContentValues contentValues = new ContentValues(13);
                contentValues.put("title", this.mTitle);
                contentValues.put("_display_name", this.mFileName);
                contentValues.put("datetaken", Long.valueOf(this.mDateTaken));
                contentValues.put("mime_type", this.mMimeType);
                contentValues.put("_data", this.mFilePath);
                contentValues.put("_size", Long.valueOf(this.mDataSize));
                if (FileSaver.this.isColumExistInDbForVideo()) {
                    contentValues.put("orientation", Integer.valueOf(this.mOrientation));
                }
                if (this.mLocation != null) {
                    contentValues.put("latitude", Double.valueOf(this.mLocation.getLatitude()));
                    contentValues.put("longitude", Double.valueOf(this.mLocation.getLongitude()));
                }
                contentValues.put("resolution", this.mResolution);
                contentValues.put("duration", Long.valueOf(this.mDuration));
                this.mUri = FileSaver.this.mContentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, contentValues);
                if (this.mUri != null) {
                    FileSaver.this.mContext.addSecureAlbumItemIfNeeded(true, this.mUri);
                }
            } catch (IllegalArgumentException e) {
                Log.m7e("FileSaver", "[saveRequest]VideoOperator,Failed to write MediaStore,exception:", e);
            } catch (UnsupportedOperationException e2) {
                Log.m7e("FileSaver", "[saveImageToDatabase]Failed to write MediaStore,UnsupportedOperationException:", e2);
            }
            Log.m5d("FileSaver", "[saveRequest]VideoOperator,end of wirte to DB,mUri = " + this.mUri);
        }

        @Override // com.android.camera.SaveRequest
        public Thumbnail createThumbnail(int i) {
            Bitmap bitmapCreateVideoThumbnailBitmap;
            if (this.mUri == null || (bitmapCreateVideoThumbnailBitmap = Thumbnail.createVideoThumbnailBitmap(this.mFilePath, i)) == null) {
                return null;
            }
            return Thumbnail.createThumbnail(this.mUri, bitmapCreateVideoThumbnailBitmap, 0, this.mFilePath);
        }
    }
}
