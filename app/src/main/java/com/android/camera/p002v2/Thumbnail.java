package com.android.camera.p002v2;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.v4.app.FragmentTransaction;
import com.android.camera.p002v2.util.CameraUtil;
import com.mediatek.camera.debug.LogHelper;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;

/* loaded from: classes.dex */
public class Thumbnail {
    private static final LogHelper.Tag TAG = new LogHelper.Tag(Thumbnail.class.getSimpleName());
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

    public String getFilePath() {
        return this.mFilePath;
    }

    public Bitmap getBitmap() {
        return this.mBitmap;
    }

    public void setFromFile(boolean z) {
        this.mFromFile = z;
    }

    public boolean fromFile() {
        return this.mFromFile;
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
                LogHelper.m29w(TAG, "Failed to rotate thumbnail", e);
            }
        }
        return bitmap;
    }

    public void saveLastThumbnailToFile(File file) {
        DataOutputStream dataOutputStream;
        FileOutputStream fileOutputStream;
        FileOutputStream fileOutputStream2;
        BufferedOutputStream bufferedOutputStream = null;
        File file2 = new File(file, "last_thumb");
        synchronized (sLock) {
            try {
                fileOutputStream = new FileOutputStream(file2);
                try {
                    BufferedOutputStream bufferedOutputStream2 = new BufferedOutputStream(fileOutputStream, FragmentTransaction.TRANSIT_ENTER_MASK);
                    try {
                        dataOutputStream = new DataOutputStream(bufferedOutputStream2);
                        try {
                            dataOutputStream.writeUTF(this.mUri.toString());
                            this.mBitmap.compress(Bitmap.CompressFormat.JPEG, 90, dataOutputStream);
                            dataOutputStream.close();
                            CameraUtil.closeSilently(fileOutputStream);
                            CameraUtil.closeSilently(bufferedOutputStream2);
                            CameraUtil.closeSilently(dataOutputStream);
                        } catch (IOException e) {
                            e = e;
                            bufferedOutputStream = bufferedOutputStream2;
                            fileOutputStream2 = fileOutputStream;
                            try {
                                LogHelper.m25e(TAG, "Fail to store bitmap. path=" + file2.getPath(), e);
                                CameraUtil.closeSilently(fileOutputStream2);
                                CameraUtil.closeSilently(bufferedOutputStream);
                                CameraUtil.closeSilently(dataOutputStream);
                            } catch (Throwable th) {
                                th = th;
                                fileOutputStream = fileOutputStream2;
                                CameraUtil.closeSilently(fileOutputStream);
                                CameraUtil.closeSilently(bufferedOutputStream);
                                CameraUtil.closeSilently(dataOutputStream);
                                throw th;
                            }
                        } catch (Throwable th2) {
                            th = th2;
                            bufferedOutputStream = bufferedOutputStream2;
                            CameraUtil.closeSilently(fileOutputStream);
                            CameraUtil.closeSilently(bufferedOutputStream);
                            CameraUtil.closeSilently(dataOutputStream);
                            throw th;
                        }
                    } catch (IOException e2) {
                        e = e2;
                        dataOutputStream = null;
                        bufferedOutputStream = bufferedOutputStream2;
                        fileOutputStream2 = fileOutputStream;
                    } catch (Throwable th3) {
                        th = th3;
                        dataOutputStream = null;
                        bufferedOutputStream = bufferedOutputStream2;
                    }
                } catch (IOException e3) {
                    e = e3;
                    dataOutputStream = null;
                    fileOutputStream2 = fileOutputStream;
                } catch (Throwable th4) {
                    th = th4;
                    dataOutputStream = null;
                }
            } catch (IOException e4) {
                e = e4;
                dataOutputStream = null;
                fileOutputStream2 = null;
            } catch (Throwable th5) {
                th = th5;
                dataOutputStream = null;
                fileOutputStream = null;
            }
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r1v0, types: [java.lang.String] */
    /* JADX WARN: Type inference failed for: r1v1, types: [java.io.Closeable] */
    /* JADX WARN: Type inference failed for: r1v10 */
    /* JADX WARN: Type inference failed for: r1v2 */
    /* JADX WARN: Type inference failed for: r1v7 */
    public static Thumbnail getLastThumbnailFromFile(String str, File file, ContentResolver contentResolver) {
        FileInputStream fileInputStream;
        BufferedInputStream bufferedInputStream;
        DataInputStream dataInputStream;
        ?? r1 = "last_thumb";
        File file2 = new File(file, "last_thumb");
        synchronized (sLock) {
            try {
                try {
                    fileInputStream = new FileInputStream(file2);
                    try {
                        bufferedInputStream = new BufferedInputStream(fileInputStream, FragmentTransaction.TRANSIT_ENTER_MASK);
                        try {
                            dataInputStream = new DataInputStream(bufferedInputStream);
                        } catch (IOException e) {
                            e = e;
                            dataInputStream = null;
                        } catch (OutOfMemoryError e2) {
                            e = e2;
                            dataInputStream = null;
                        } catch (Throwable th) {
                            th = th;
                            r1 = 0;
                            CameraUtil.closeSilently(fileInputStream);
                            CameraUtil.closeSilently(bufferedInputStream);
                            CameraUtil.closeSilently(r1);
                            throw th;
                        }
                    } catch (IOException e3) {
                        e = e3;
                        dataInputStream = null;
                        bufferedInputStream = null;
                    } catch (OutOfMemoryError e4) {
                        e = e4;
                        dataInputStream = null;
                        bufferedInputStream = null;
                    } catch (Throwable th2) {
                        th = th2;
                        r1 = 0;
                        bufferedInputStream = null;
                    }
                } catch (IOException e5) {
                    e = e5;
                    dataInputStream = null;
                    bufferedInputStream = null;
                    fileInputStream = null;
                } catch (OutOfMemoryError e6) {
                    e = e6;
                    dataInputStream = null;
                    bufferedInputStream = null;
                    fileInputStream = null;
                } catch (Throwable th3) {
                    th = th3;
                    r1 = 0;
                    bufferedInputStream = null;
                    fileInputStream = null;
                }
                try {
                    Uri uri = Uri.parse(dataInputStream.readUTF());
                    if (!isUriValid(uri, contentResolver, str)) {
                        dataInputStream.close();
                        CameraUtil.closeSilently(fileInputStream);
                        CameraUtil.closeSilently(bufferedInputStream);
                        CameraUtil.closeSilently(dataInputStream);
                        return null;
                    }
                    Bitmap bitmapDecodeStream = BitmapFactory.decodeStream(dataInputStream);
                    dataInputStream.close();
                    CameraUtil.closeSilently(fileInputStream);
                    CameraUtil.closeSilently(bufferedInputStream);
                    CameraUtil.closeSilently(dataInputStream);
                    Thumbnail thumbnailCreateThumbnail = createThumbnail(uri, bitmapDecodeStream, 0);
                    if (thumbnailCreateThumbnail != null) {
                        thumbnailCreateThumbnail.setFromFile(true);
                    }
                    return thumbnailCreateThumbnail;
                } catch (IOException e7) {
                    e = e7;
                    LogHelper.m26i(TAG, "Fail to load bitmap. " + e);
                    CameraUtil.closeSilently(fileInputStream);
                    CameraUtil.closeSilently(bufferedInputStream);
                    CameraUtil.closeSilently(dataInputStream);
                    return null;
                } catch (OutOfMemoryError e8) {
                    e = e8;
                    LogHelper.m25e(TAG, "loadFrom file fail", e);
                    CameraUtil.closeSilently(fileInputStream);
                    CameraUtil.closeSilently(bufferedInputStream);
                    CameraUtil.closeSilently(dataInputStream);
                    return null;
                }
            } catch (Throwable th4) {
                th = th4;
            }
        }
    }

    private static class Media {
        public final long dateTaken;
        public final String filePath;

        /* renamed from: id */
        public final long f63id;
        public final int mediaType;
        public final int orientation;
        public final Uri uri;

        public Media(long j, int i, long j2, Uri uri, int i2, String str) {
            this.f63id = j;
            this.orientation = i;
            this.dateTaken = j2;
            this.uri = uri;
            this.mediaType = i2;
            this.filePath = str;
        }

        public String toString() {
            return "Media(id=" + this.f63id + ", orientation=" + this.orientation + ", dateTaken=" + this.dateTaken + ", uri=" + this.uri + ", mediaType=" + this.mediaType + ", filePath=" + this.filePath + ")";
        }
    }

    public static Thumbnail createThumbnail(Uri uri, Bitmap bitmap, int i) {
        if (bitmap == null) {
            LogHelper.m24e(TAG, "Failed to create thumbnail from null bitmap");
            return null;
        }
        return new Thumbnail(uri, bitmap, i, -1L, -1L, null);
    }

    public static Thumbnail createThumbnail(Uri uri, Bitmap bitmap, int i, long j, long j2, String str) {
        if (bitmap == null) {
            LogHelper.m24e(TAG, "Failed to create thumbnail from null bitmap");
            return null;
        }
        return new Thumbnail(uri, bitmap, i, j, j2, str);
    }

    public static int getLastThumbnailFromContentResolver(String str, ContentResolver contentResolver, Thumbnail[] thumbnailArr) throws Throwable {
        Cursor cursor;
        long j;
        Media media;
        long j2;
        LogHelper.m26i(TAG, "getLastThumbnailFromContentResolver() begin.");
        Uri contentUri = MediaStore.Files.getContentUri("external");
        String str2 = null;
        try {
            Cursor cursorQuery = contentResolver.query(contentUri.buildUpon().appendQueryParameter("limit", "1").build(), new String[]{"_id", "orientation", "datetaken", "_data", "media_type"}, "((media_type=" + Integer.toString(1) + " OR media_type=" + Integer.toString(3) + " ) AND bucket_id=" + getBucketId(str) + ")", null, "datetaken DESC,_id DESC");
            if (cursorQuery != null) {
                try {
                    if (cursorQuery.moveToFirst()) {
                        long j3 = cursorQuery.getLong(0);
                        cursorQuery.getInt(1);
                        long j4 = cursorQuery.getLong(2);
                        String string = cursorQuery.getString(3);
                        media = new Media(j3, cursorQuery.getInt(1), cursorQuery.getLong(2), ContentUris.withAppendedId(contentUri, j3), cursorQuery.getInt(4), cursorQuery.getString(3));
                        str2 = string;
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
            LogHelper.m23d(TAG, "getLastThumbnailFromContentResolver() media=" + media);
            if (media == null) {
                return 0;
            }
            Bitmap thumbnail = null;
            int i = media.orientation;
            try {
                if (media.mediaType == 1) {
                    thumbnail = MediaStore.Images.Thumbnails.getThumbnail(contentResolver, media.f63id, 1, null);
                } else if (media.mediaType == 3) {
                    i = 0;
                    thumbnail = MediaStore.Video.Thumbnails.getThumbnail(contentResolver, media.f63id, 1, null);
                }
            } catch (OutOfMemoryError e) {
                LogHelper.m25e(TAG, "getThumbnail fail", e);
            }
            if (isUriValid(media.uri, contentResolver, str)) {
                thumbnailArr[0] = createThumbnail(media.uri, thumbnail, i, j, j2, str2);
                return 1;
            }
            LogHelper.m23d(TAG, "Uri is not valid!");
            LogHelper.m23d(TAG, "Quit getLastThumbnail");
            return 2;
        } catch (Throwable th2) {
            th = th2;
            cursor = null;
        }
    }

    public String toString() {
        return "Thumbnail(mUri=" + this.mUri + ", mFromFile=" + this.mFromFile + ", mBitmap=" + this.mBitmap + ")";
    }

    private static String getBucketId(String str) {
        return String.valueOf(str.toLowerCase(Locale.ENGLISH).hashCode());
    }

    private static boolean isUriValid(Uri uri, ContentResolver contentResolver, String str) throws IOException {
        if (uri == null) {
            return false;
        }
        try {
            ParcelFileDescriptor parcelFileDescriptorOpenFileDescriptor = contentResolver.openFileDescriptor(uri, "r");
            if (parcelFileDescriptorOpenFileDescriptor == null) {
                LogHelper.m24e(TAG, "Fail to open URI. URI=" + uri);
                return false;
            }
            parcelFileDescriptorOpenFileDescriptor.close();
            return isMountPointValid(uri, contentResolver, str);
        } catch (IOException e) {
            return false;
        }
    }

    private static boolean isMountPointValid(Uri uri, ContentResolver contentResolver, String str) {
        String string;
        Cursor cursorQuery = contentResolver.query(uri, new String[]{"_data"}, null, null, null);
        if (cursorQuery == null) {
            string = "";
        } else {
            try {
                if (!cursorQuery.moveToFirst()) {
                    string = "";
                } else {
                    string = cursorQuery.getString(0);
                }
            } finally {
                cursorQuery.close();
            }
        }
        boolean zEquals = str.equals(new File(string).getParent());
        LogHelper.m23d(TAG, "isMountPointValid(" + uri + ") path =" + string);
        return zEquals;
    }
}
