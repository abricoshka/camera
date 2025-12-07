package com.android.camera;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.provider.MediaStore;
import java.io.FileDescriptor;

/* loaded from: classes.dex */
public class Thumbnail {
    private static int mOrientation = 0;
    private static Object sLock = new Object();
    private Bitmap mBitmap;
    private long mDateTaken;
    private String mFilePath;
    private boolean mFromFile = false;
    private long mThumbnailId;
    private Uri mUri;

    private Thumbnail(Uri uri, Bitmap bitmap, int i, long j, long j2, String str) {
        this.mThumbnailId = -1L;
        this.mDateTaken = 0L;
        this.mUri = uri;
        this.mBitmap = rotateImage(bitmap, i);
        mOrientation = i;
        this.mThumbnailId = j;
        this.mDateTaken = j2;
        this.mFilePath = str;
    }

    public Uri getUri() {
        return this.mUri;
    }

    public Bitmap getBitmap() {
        return this.mBitmap;
    }

    private static Bitmap rotateImage(Bitmap bitmap, int i) {
        if (i != 0) {
            Matrix matrix = new Matrix();
            matrix.setRotate(i, bitmap.getWidth() * 0.5f, bitmap.getHeight() * 0.5f);
            try {
                Bitmap bitmapCreateBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                if (bitmapCreateBitmap != bitmap) {
                    bitmap.recycle();
                }
                return bitmapCreateBitmap;
            } catch (IllegalArgumentException e) {
                Log.m12w("Thumbnail", "Failed to rotate thumbnail", e);
            }
        }
        return bitmap;
    }

    private static class Media {
        public final long dateTaken;
        public final String filePath;

        /* renamed from: id */
        public final long f59id;
        public final int mediaType;
        public final int orientation;
        public final Uri uri;

        public Media(long j, int i, long j2, Uri uri, int i2, String str) {
            this.f59id = j;
            this.orientation = i;
            this.dateTaken = j2;
            this.uri = uri;
            this.mediaType = i2;
            this.filePath = str;
        }

        public String toString() {
            return "Media(id=" + this.f59id + ", orientation=" + this.orientation + ", dateTaken=" + this.dateTaken + ", uri=" + this.uri + ", mediaType=" + this.mediaType + ", filePath=" + this.filePath + ")";
        }
    }

    public static Thumbnail createThumbnail(byte[] bArr, int i, int i2, Uri uri, String str) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = i2;
        try {
            return createThumbnail(uri, BitmapFactory.decodeByteArray(bArr, 0, bArr.length, options), i, str);
        } catch (OutOfMemoryError e) {
            Log.m7e("Thumbnail", "createThumbnail fail", e);
            return null;
        }
    }

    public static Bitmap decodeLastPictureThumb(String str, int i) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = i;
        Bitmap bitmapDecodeFile = BitmapFactory.decodeFile(str, options);
        Log.m5d("Thumbnail", "lastPictureThumb = " + bitmapDecodeFile + "!!!; file path" + str);
        return bitmapDecodeFile;
    }

    public static Thumbnail createThumbnail(String str, int i, int i2, Uri uri) {
        return createThumbnail(uri, decodeLastPictureThumb(str, i2), i, str);
    }

    public static Bitmap createVideoThumbnailBitmap(FileDescriptor fileDescriptor, int i) {
        return createVideoThumbnailBitmap(null, fileDescriptor, i);
    }

    public static Bitmap createVideoThumbnailBitmap(String str, int i) {
        return createVideoThumbnailBitmap(str, null, i);
    }

    /* JADX WARN: Removed duplicated region for block: B:8:0x0016 A[ADDED_TO_REGION] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private static android.graphics.Bitmap createVideoThumbnailBitmap(java.lang.String r6, java.io.FileDescriptor r7, int r8) throws java.io.IOException {
        /*
            r1 = 0
            android.media.MediaMetadataRetriever r2 = new android.media.MediaMetadataRetriever
            r2.<init>()
            if (r6 == 0) goto L19
            r2.setDataSource(r6)     // Catch: java.lang.IllegalArgumentException -> L1d java.lang.RuntimeException -> L2b java.lang.Throwable -> L3d
        Lb:
            r4 = -1
            android.graphics.Bitmap r0 = r2.getFrameAtTime(r4)     // Catch: java.lang.IllegalArgumentException -> L1d java.lang.RuntimeException -> L2b java.lang.Throwable -> L3d
            r2.release()     // Catch: java.lang.RuntimeException -> L26
        L14:
            if (r0 == 0) goto L18
            if (r8 != 0) goto L47
        L18:
            return r0
        L19:
            r2.setDataSource(r7)     // Catch: java.lang.IllegalArgumentException -> L1d java.lang.RuntimeException -> L2b java.lang.Throwable -> L3d
            goto Lb
        L1d:
            r0 = move-exception
            r0.printStackTrace()     // Catch: java.lang.Throwable -> L3d
            r2.release()     // Catch: java.lang.RuntimeException -> L38
        L24:
            r0 = r1
            goto L14
        L26:
            r1 = move-exception
            r1.printStackTrace()
            goto L14
        L2b:
            r0 = move-exception
            r0.printStackTrace()     // Catch: java.lang.Throwable -> L3d
            r2.release()     // Catch: java.lang.RuntimeException -> L33
            goto L24
        L33:
            r0 = move-exception
            r0.printStackTrace()
            goto L24
        L38:
            r0 = move-exception
            r0.printStackTrace()
            goto L24
        L3d:
            r0 = move-exception
            r2.release()     // Catch: java.lang.RuntimeException -> L42
        L41:
            throw r0
        L42:
            r1 = move-exception
            r1.printStackTrace()
            goto L41
        L47:
            int r1 = r0.getWidth()
            int r2 = r0.getHeight()
            java.lang.String r3 = "Thumbnail"
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r5 = "bitmap = "
            java.lang.StringBuilder r4 = r4.append(r5)
            java.lang.StringBuilder r4 = r4.append(r1)
            java.lang.String r5 = "x"
            java.lang.StringBuilder r4 = r4.append(r5)
            java.lang.StringBuilder r4 = r4.append(r2)
            java.lang.String r5 = "   targetWidth="
            java.lang.StringBuilder r4 = r4.append(r5)
            java.lang.StringBuilder r4 = r4.append(r8)
            java.lang.String r4 = r4.toString()
            com.android.camera.Log.m10v(r3, r4)
            if (r1 <= r8) goto Lba
            float r3 = (float) r8
            float r4 = (float) r1
            float r3 = r3 / r4
            float r1 = (float) r1
            float r1 = r1 * r3
            int r1 = java.lang.Math.round(r1)
            float r2 = (float) r2
            float r2 = r2 * r3
            int r2 = java.lang.Math.round(r2)
            java.lang.String r3 = "Thumbnail"
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r5 = "w = "
            java.lang.StringBuilder r4 = r4.append(r5)
            java.lang.StringBuilder r4 = r4.append(r1)
            java.lang.String r5 = "h"
            java.lang.StringBuilder r4 = r4.append(r5)
            java.lang.StringBuilder r4 = r4.append(r2)
            java.lang.String r4 = r4.toString()
            com.android.camera.Log.m10v(r3, r4)
            r3 = 1
            android.graphics.Bitmap r0 = android.graphics.Bitmap.createScaledBitmap(r0, r1, r2, r3)
        Lba:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.camera.Thumbnail.createVideoThumbnailBitmap(java.lang.String, java.io.FileDescriptor, int):android.graphics.Bitmap");
    }

    public static Thumbnail createThumbnail(Uri uri, Bitmap bitmap, int i, String str) {
        if (bitmap == null) {
            Log.m6e("Thumbnail", "Failed to create thumbnail from null bitmap");
            return null;
        }
        return new Thumbnail(uri, bitmap, i, -1L, -1L, str);
    }

    public static Thumbnail createThumbnail(Uri uri, Bitmap bitmap, int i, long j, long j2, String str) {
        if (bitmap == null) {
            Log.m6e("Thumbnail", "Failed to create thumbnail from null bitmap");
            return null;
        }
        return new Thumbnail(uri, bitmap, i, j, j2, str);
    }

    public static int getLastThumbnailFromContentResolver(ContentResolver contentResolver, Thumbnail[] thumbnailArr, Thumbnail thumbnail) throws Throwable {
        Cursor cursor;
        long j;
        Media media;
        long j2;
        Uri contentUri = MediaStore.Files.getContentUri("external");
        String str = null;
        try {
            Cursor cursorQuery = contentResolver.query(contentUri.buildUpon().appendQueryParameter("limit", "1").build(), new String[]{"_id", "orientation", "datetaken", "_data", "media_type"}, "((media_type=" + Integer.toString(1) + " OR media_type=" + Integer.toString(3) + " ) AND bucket_id=" + Storage.getBucketId() + ")", null, "datetaken DESC,_id DESC");
            if (cursorQuery != null) {
                try {
                    if (cursorQuery.moveToFirst()) {
                        long j3 = cursorQuery.getLong(0);
                        int i = cursorQuery.getInt(1);
                        long j4 = cursorQuery.getLong(2);
                        String string = cursorQuery.getString(3);
                        if (thumbnail != null) {
                            Log.m5d("Thumbnail", "id:" + j3 + ",oldId:" + thumbnail.mThumbnailId + ",orientation:" + i + ",oldOrientation:" + mOrientation + ",dateTaken:" + j4 + ",oldDateTaken:" + thumbnail.mDateTaken + ",filePath:" + string + ",oldFilePath:" + thumbnail.mFilePath);
                        }
                        if (thumbnail != null && j3 == thumbnail.mThumbnailId && i == mOrientation && j4 == thumbnail.mDateTaken && !(!string.equalsIgnoreCase(thumbnail.mFilePath))) {
                            Log.m5d("Thumbnail", "same file, don't need decode again!");
                            if (cursorQuery != null) {
                                cursorQuery.close();
                            }
                            return 2;
                        }
                        media = new Media(j3, cursorQuery.getInt(1), cursorQuery.getLong(2), ContentUris.withAppendedId(contentUri, j3), cursorQuery.getInt(4), cursorQuery.getString(3));
                        str = string;
                        j2 = j4;
                        j = j3;
                    } else {
                        j = -1;
                        media = null;
                        j2 = 0;
                    }
                } catch (Throwable th) {
                    th = th;
                    cursor = cursorQuery;
                    if (cursor != null) {
                        cursor.close();
                    }
                    throw th;
                }
            } else {
                j = -1;
                media = null;
                j2 = 0;
            }
            if (cursorQuery != null) {
                cursorQuery.close();
            }
            Log.m5d("Thumbnail", "getLastThumbnailFromContentResolver() media=" + media);
            if (media == null) {
                return 0;
            }
            Bitmap thumbnail2 = null;
            Uri uriWithAppendedId = null;
            int i2 = media.orientation;
            try {
                if (media.mediaType == 1) {
                    thumbnail2 = MediaStore.Images.Thumbnails.getThumbnail(contentResolver, media.f59id, 1, null);
                    uriWithAppendedId = ContentUris.withAppendedId(MediaStore.Images.Media.getContentUri("external"), media.f59id);
                } else if (media.mediaType == 3) {
                    i2 = 0;
                    thumbnail2 = MediaStore.Video.Thumbnails.getThumbnail(contentResolver, media.f59id, 1, null);
                    uriWithAppendedId = ContentUris.withAppendedId(MediaStore.Video.Media.getContentUri("external"), media.f59id);
                }
            } catch (OutOfMemoryError e) {
                Log.m7e("Thumbnail", "getThumbnail fail", e);
            }
            if (Util.isUriValid(uriWithAppendedId, contentResolver)) {
                thumbnailArr[0] = createThumbnail(uriWithAppendedId, thumbnail2, i2, j, j2, str);
                return 1;
            }
            Log.m5d("Thumbnail", "Uri is not valid!");
            Log.m5d("Thumbnail", "Quit getLastThumbnail");
            return 2;
        } catch (Throwable th2) {
            th = th2;
            cursor = null;
        }
    }

    public String toString() {
        return "Thumbnail(mUri=" + this.mUri + ", mFromFile=" + this.mFromFile + ", mBitmap=" + this.mBitmap + ")";
    }
}
