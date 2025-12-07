package com.mediatek.camera.p005v2.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.media.CamcorderProfile;
import android.media.Image;
import android.util.Size;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import com.mediatek.camera.R;
import com.mediatek.camera.debug.LogHelper;
import com.mediatek.camera.p005v2.p006ui.Rotatable;
import com.mediatek.camera.p005v2.stream.pip.pipwrapping.PipEGLConfigWrapper;
import java.io.Closeable;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import junit.framework.Assert;

/* loaded from: classes.dex */
public class Utils {
    private static ImageFileNamer sImageFileNamer;
    private static final LogHelper.Tag TAG = new LogHelper.Tag(Utils.class.getSimpleName());
    public static final double[] RATIOS = {1.3333d, 1.5d, 1.6667d, 1.7778d};

    private Utils() {
    }

    public static int getImageSize(String str) {
        if (str == null) {
            return 1500000;
        }
        if (str.indexOf("-superfine") <= 0) {
            return str.indexOf("autorama") > 0 ? 163840 : 1500000;
        }
        int iIndexOf = str.indexOf("-superfine");
        if (str.indexOf(120) > 0) {
            return (int) (((Integer.parseInt(str.substring(r1 + 1, iIndexOf)) * Integer.parseInt(str.substring(0, r1))) / 76800.0d) * 13312.0d);
        }
        return 1500000;
    }

    public static void initialize(Context context) {
        sImageFileNamer = new ImageFileNamer(context.getString(R.string.image_file_name_format));
    }

    public static double findFullscreenRatio(Context context) {
        double d;
        double d2 = 1.3333d;
        if (context != null) {
            Display defaultDisplay = ((WindowManager) context.getSystemService("window")).getDefaultDisplay();
            Point point = new Point();
            defaultDisplay.getRealSize(point);
            if (point.x > point.y) {
                d = point.x / point.y;
            } else {
                d = point.y / point.x;
            }
            LogHelper.m26i(TAG, "fullscreen = " + d + " x = " + point.x + " y = " + point.y);
            for (int i = 0; i < RATIOS.length; i++) {
                if (Math.abs(RATIOS[i] - d) < Math.abs(d - d2)) {
                    d2 = RATIOS[i];
                }
            }
        }
        return d2;
    }

    public static CameraCharacteristics getCameraCharacteristics(Activity activity, String str) {
        try {
            return ((CameraManager) activity.getSystemService("camera")).getCameraCharacteristics(str);
        } catch (CameraAccessException e) {
            LogHelper.m26i(TAG, "CameraCharacteristics exception : ");
            e.printStackTrace();
            return null;
        }
    }

    public static Size filterSupportedSize(List<Size> list, Size size, Size size2) {
        SizeComparator sizeComparator = null;
        if (list == null || list.size() <= 0) {
            return null;
        }
        SizeComparator sizeComparator2 = new SizeComparator(sizeComparator);
        if (size2 == null) {
            size2 = size;
        }
        for (Size size3 : list) {
            if (sizeComparator2.compare(size3, size2) <= 0 && checkAspectRatiosMatch(size3, size)) {
                size = size3;
            }
        }
        return size;
    }

    public static List<Size> filterSizesByBound(List<Size> list, Size size) {
        ArrayList arrayList = new ArrayList();
        for (Size size2 : list) {
            if (compareSize(size, size2)) {
                arrayList.add(size2);
            }
        }
        return arrayList;
    }

    public static boolean compareSize(Size size, Size size2) {
        return new SizeComparator(null).compare(size, size2) >= 0;
    }

    public static List<Size> getSizeList(List<String> list) {
        if (list == null || list.size() <= 0) {
            return null;
        }
        ArrayList arrayList = new ArrayList(list.size());
        Iterator<T> it = list.iterator();
        while (it.hasNext()) {
            arrayList.add(getSize((String) it.next()));
        }
        return arrayList;
    }

    public static String buildSize(Size size) {
        if (size != null) {
            return "" + size.getWidth() + "x" + size.getHeight();
        }
        return "null";
    }

    public static Size getSize(String str) {
        Size size = null;
        int iIndexOf = str.indexOf(120);
        if (iIndexOf != -1) {
            size = new Size(Integer.parseInt(str.substring(0, iIndexOf)), Integer.parseInt(str.substring(iIndexOf + 1)));
        }
        LogHelper.m23d(TAG, "getSize(" + str + ") return " + size);
        return size;
    }

    public static Size getOptimalPreviewSize(Context context, List<Size> list, double d) {
        double dAbs;
        if (list == null) {
            return null;
        }
        Display defaultDisplay = ((WindowManager) context.getSystemService("window")).getDefaultDisplay();
        Point point = new Point();
        defaultDisplay.getRealSize(point);
        int iMin = Math.min(point.x, point.y);
        int iMax = Math.max(point.x, point.y);
        Size size = null;
        double d2 = Double.MAX_VALUE;
        double d3 = Double.MAX_VALUE;
        Iterator<T> it = list.iterator();
        while (true) {
            double d4 = d3;
            Size size2 = size;
            double d5 = d2;
            if (!it.hasNext()) {
                return size2;
            }
            Size size3 = (Size) it.next();
            if (Math.abs((size3.getWidth() / size3.getHeight()) - d) > 0.03d) {
                dAbs = d4;
                d2 = d5;
                size = size2;
            } else if (Math.abs(size3.getHeight() - iMin) < d5) {
                double dAbs2 = Math.abs(size3.getHeight() - iMin);
                dAbs = Math.abs(size3.getWidth() - iMax);
                d2 = dAbs2;
                size = size3;
            } else if (Math.abs(size3.getHeight() - iMin) != d5 || Math.abs(size3.getWidth() - iMax) >= d4) {
                dAbs = d4;
                d2 = d5;
                size = size2;
            } else {
                dAbs = Math.abs(size3.getWidth() - iMax);
                d2 = d5;
                size = size3;
            }
            d3 = dAbs;
        }
    }

    public static Size getOptimalSize(List<Size> list, int i, int i2) {
        double dAbs;
        Size size = null;
        double d = Double.MAX_VALUE;
        double d2 = Double.MAX_VALUE;
        double d3 = i / i2;
        int iMin = Math.min(i, i2);
        int iMax = Math.max(i, i2);
        Iterator<T> it = list.iterator();
        while (true) {
            double d4 = d2;
            Size size2 = size;
            double d5 = d;
            if (!it.hasNext()) {
                LogHelper.m23d(TAG, "[getOptimalSize]width:" + size2.getWidth() + ",height:" + size2.getHeight());
                return size2;
            }
            Size size3 = (Size) it.next();
            if (Math.abs((size3.getWidth() / size3.getHeight()) - d3) > 0.03d) {
                dAbs = d4;
                d = d5;
                size = size2;
            } else if (Math.abs(size3.getHeight() - iMin) < d5) {
                double dAbs2 = Math.abs(size3.getHeight() - iMin);
                dAbs = Math.abs(size3.getWidth() - iMax);
                d = dAbs2;
                size = size3;
            } else if (Math.abs(size3.getHeight() - iMin) != d5 || Math.abs(size3.getWidth() - iMax) >= d4) {
                dAbs = d4;
                d = d5;
                size = size2;
            } else {
                dAbs = Math.abs(size3.getWidth() - iMax);
                d = d5;
                size = size3;
            }
            d2 = dAbs;
        }
    }

    public static List<String> filterPictureSizesByRatio(List<String> list, double d) {
        ArrayList arrayList = new ArrayList();
        int i = 0;
        while (true) {
            int i2 = i;
            if (i2 < list.size()) {
                Size size = getSize(list.get(i2));
                if (Math.abs((size.getWidth() / size.getHeight()) - d) <= 0.03d) {
                    arrayList.add(list.get(i2));
                }
                i = i2 + 1;
            } else {
                return arrayList;
            }
        }
    }

    public static int getJpegRotation(int i, CameraCharacteristics cameraCharacteristics) {
        if (i == -1) {
            return 0;
        }
        int iIntValue = ((Integer) cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION)).intValue();
        if (((Integer) cameraCharacteristics.get(CameraCharacteristics.LENS_FACING)).intValue() == 0) {
            i = -i;
        }
        int i2 = ((iIntValue + i) + 360) % 360;
        LogHelper.m26i(TAG, "getJpegRotation : " + i2);
        return i2;
    }

    public static int getRecordingRotation(int i, CameraCharacteristics cameraCharacteristics) {
        int iIntValue = ((Integer) cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION)).intValue();
        boolean z = ((Integer) cameraCharacteristics.get(CameraCharacteristics.LENS_FACING)).intValue() == 0;
        if (i == -1) {
            return iIntValue;
        }
        if (z) {
            return ((iIntValue - i) + 360) % 360;
        }
        return (iIntValue + i) % 360;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public static void setRotatableOrientation(View view, int i, boolean z) {
        if (view == 0) {
            return;
        }
        if (view instanceof Rotatable) {
            ((Rotatable) view).setOrientation(i, z);
            return;
        }
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            int childCount = viewGroup.getChildCount();
            for (int i2 = 0; i2 < childCount; i2++) {
                setRotatableOrientation(viewGroup.getChildAt(i2), i, z);
            }
        }
    }

    public static int getDngOrientation(int i) {
        if (i == 0) {
            return 1;
        }
        if (i == 90) {
            return 6;
        }
        if (i == 180) {
            return 3;
        }
        return 8;
    }

    public static int getDisplayRotation(Context context) {
        switch (((WindowManager) context.getSystemService("window")).getDefaultDisplay().getRotation()) {
        }
        return 0;
    }

    public static int roundOrientation(int i, int i2) {
        boolean z = true;
        if (i2 != -1) {
            int iAbs = Math.abs(i - i2);
            if (Math.min(iAbs, 360 - iAbs) < 50) {
                z = false;
            }
        }
        if (z) {
            return (((i + 45) / 90) * 90) % 360;
        }
        return i2;
    }

    public static byte[] acquireJpegBytesAndClose(Image image) {
        Assert.assertNotNull(image);
        if (image.getFormat() == 256) {
            ByteBuffer buffer = image.getPlanes()[0].getBuffer();
            byte[] bArr = new byte[buffer.remaining()];
            buffer.get(bArr);
            buffer.rewind();
            image.close();
            return bArr;
        }
        throw new RuntimeException("Unsupported image format.");
    }

    public static byte[] acquireRawBytesAndClose(Image image) {
        Assert.assertNotNull(image);
        if (image.getFormat() == 32) {
            ByteBuffer buffer = image.getPlanes()[0].getBuffer();
            byte[] bArr = new byte[buffer.remaining()];
            buffer.get(bArr);
            buffer.rewind();
            image.close();
            return bArr;
        }
        throw new RuntimeException("Unsupported image format.");
    }

    public static byte[] getContinuousRGBADataFromImage(Image image) {
        byte[] bArr = null;
        LogHelper.m26i(TAG, "getContinuousRGBADataFromImage begin");
        if (image.getFormat() != PipEGLConfigWrapper.getInstance().getPixelFormat()) {
            LogHelper.m26i(TAG, "error format = " + image.getFormat());
            return null;
        }
        int format = image.getFormat();
        int width = image.getWidth();
        int height = image.getHeight();
        Image.Plane[] planes = image.getPlanes();
        if (format == PipEGLConfigWrapper.getInstance().getPixelFormat()) {
            PixelFormat pixelFormat = new PixelFormat();
            PixelFormat.getPixelFormatInfo(format, pixelFormat);
            ByteBuffer buffer = planes[0].getBuffer();
            int rowStride = planes[0].getRowStride();
            int pixelStride = planes[0].getPixelStride();
            bArr = new byte[(pixelFormat.bitsPerPixel * (width * height)) / 8];
            int i = rowStride - (pixelStride * width);
            int i2 = 0;
            for (int i3 = 0; i3 < height; i3++) {
                int i4 = width * pixelStride;
                buffer.get(bArr, i2, i4);
                buffer.position(buffer.position() + i);
                i2 += i4;
            }
        }
        LogHelper.m26i(TAG, "getContinuousRGBADataFromImage end");
        return bArr;
    }

    public static CamcorderProfile getVideoProfile(int i, int i2) {
        return CamcorderProfile.get(i, i2);
    }

    public static String createJpegName(long j) {
        String strGenerateName;
        synchronized (sImageFileNamer) {
            strGenerateName = sImageFileNamer.generateName(j);
        }
        return strGenerateName;
    }

    public static String createDngName(long j) {
        String strGenerateDngName;
        synchronized (sImageFileNamer) {
            strGenerateDngName = sImageFileNamer.generateDngName(j);
        }
        return strGenerateDngName;
    }

    public static String buildEnableList(String[] strArr) {
        String str = null;
        if (strArr == null) {
            return null;
        }
        if (strArr != null) {
            ArrayList arrayList = new ArrayList();
            int length = strArr.length;
            str = "";
            for (int i = 0; i < length; i++) {
                if (!arrayList.contains(strArr[i])) {
                    arrayList.add(strArr[i]);
                    if (i == length - 1) {
                        str = str + strArr[i];
                    } else {
                        str = str + strArr[i] + ";";
                    }
                }
            }
        }
        LogHelper.m23d(TAG, "buildEnableList, return " + str);
        return str;
    }

    public static void closeSilently(Closeable closeable) {
        if (closeable == null) {
            return;
        }
        try {
            closeable.close();
        } catch (Throwable th) {
            th.printStackTrace();
        }
    }

    public static Rect cropRegionForZoom(Activity activity, String str, float f) {
        Rect rect = (Rect) getCameraCharacteristics(activity, str).get(CameraCharacteristics.SENSOR_INFO_ACTIVE_ARRAY_SIZE);
        int iWidth = rect.width() / 2;
        int iHeight = rect.height() / 2;
        int iWidth2 = (int) ((rect.width() * 0.5f) / f);
        int iHeight2 = (int) ((rect.height() * 0.5f) / f);
        return new Rect(iWidth - iWidth2, iHeight - iHeight2, iWidth + iWidth2, iHeight2 + iHeight);
    }

    public static Bitmap makeBitmap(byte[] bArr, int i) {
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeByteArray(bArr, 0, bArr.length, options);
            if (options.mCancel || options.outWidth == -1 || options.outHeight == -1) {
                return null;
            }
            options.inSampleSize = computeSampleSize(options, -1, i);
            options.inJustDecodeBounds = false;
            options.inDither = false;
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            return BitmapFactory.decodeByteArray(bArr, 0, bArr.length, options);
        } catch (OutOfMemoryError e) {
            LogHelper.m25e(TAG, "Got oom exception ", e);
            return null;
        }
    }

    public static int computeSampleSize(BitmapFactory.Options options, int i, int i2) {
        int iComputeInitialSampleSize = computeInitialSampleSize(options, i, i2);
        if (iComputeInitialSampleSize <= 8) {
            int i3 = 1;
            while (i3 < iComputeInitialSampleSize) {
                i3 <<= 1;
            }
            return i3;
        }
        return ((iComputeInitialSampleSize + 7) / 8) * 8;
    }

    private static int computeInitialSampleSize(BitmapFactory.Options options, int i, int i2) {
        double d = options.outWidth;
        double d2 = options.outHeight;
        int iCeil = i2 < 0 ? 1 : (int) Math.ceil(Math.sqrt((d * d2) / i2));
        int iMin = i < 0 ? 128 : (int) Math.min(Math.floor(d / i), Math.floor(d2 / i));
        if (iMin < iCeil) {
            return iCeil;
        }
        if (i2 < 0 && i < 0) {
            return 1;
        }
        if (i < 0) {
            return iCeil;
        }
        return iMin;
    }

    public static Bitmap rotate(Bitmap bitmap, int i) {
        return rotateAndMirror(bitmap, i, false);
    }

    public static Bitmap rotateAndMirror(Bitmap bitmap, int i, boolean z) {
        if ((i != 0 || z) && bitmap != null) {
            Matrix matrix = new Matrix();
            if (z) {
                matrix.postScale(-1.0f, 1.0f);
                i = (i + 360) % 360;
                if (i == 0 || i == 180) {
                    matrix.postTranslate(bitmap.getWidth(), 0.0f);
                } else if (i == 90 || i == 270) {
                    matrix.postTranslate(bitmap.getHeight(), 0.0f);
                } else {
                    throw new IllegalArgumentException("Invalid degrees=" + i);
                }
            }
            if (i != 0) {
                matrix.postRotate(i, bitmap.getWidth() / 2.0f, bitmap.getHeight() / 2.0f);
            }
            try {
                Bitmap bitmapCreateBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                if (bitmap != bitmapCreateBitmap) {
                    bitmap.recycle();
                    return bitmapCreateBitmap;
                }
                return bitmap;
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
                return bitmap;
            }
        }
        return bitmap;
    }

    private static class ImageFileNamer {
        private final SimpleDateFormat mFormat;
        private long mLastDate;
        private boolean mNextIsSameName = false;
        private String mSameDngName;
        private int mSameSecondCount;

        public ImageFileNamer(String str) {
            this.mFormat = new SimpleDateFormat(str);
        }

        public String generateName(long j) {
            String str = this.mFormat.format(new Date(j));
            if (j / 1000 == this.mLastDate / 1000) {
                this.mSameSecondCount++;
                return str + "_" + this.mSameSecondCount;
            }
            this.mLastDate = j;
            this.mSameSecondCount = 0;
            return str;
        }

        public String generateDngName(long j) {
            if (this.mNextIsSameName && this.mSameDngName != null) {
                this.mNextIsSameName = false;
                return this.mSameDngName;
            }
            String str = this.mFormat.format(new Date(j));
            if (j / 1000 == this.mLastDate / 1000) {
                this.mSameSecondCount++;
                str = str + "_" + this.mSameSecondCount;
            } else {
                this.mLastDate = j;
                this.mSameSecondCount = 0;
            }
            String str2 = str + "_RAW";
            if (!this.mNextIsSameName) {
                this.mNextIsSameName = true;
                this.mSameDngName = str2;
            }
            return str2;
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:31:0x008d  */
    /* JADX WARN: Removed duplicated region for block: B:8:0x0046  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static android.graphics.Bitmap createBitmapFromVideo(java.lang.String r7, java.io.FileDescriptor r8, int r9) throws java.io.IOException {
        /*
            Method dump skipped, instructions count: 259
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mediatek.camera.p005v2.util.Utils.createBitmapFromVideo(java.lang.String, java.io.FileDescriptor, int):android.graphics.Bitmap");
    }

    private static class SizeComparator implements Comparator<Size> {
        /* synthetic */ SizeComparator(SizeComparator sizeComparator) {
            this();
        }

        private SizeComparator() {
        }

        @Override // java.util.Comparator
        public int compare(Size size, Size size2) {
            return Utils.compareSizes(size.getWidth(), size.getHeight(), size2.getWidth(), size2.getHeight());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static int compareSizes(int i, int i2, int i3, int i4) {
        long j = i2 * i;
        long j2 = i3 * i4;
        if (j == j2) {
            j = i;
            j2 = i3;
        }
        if (j < j2) {
            return -1;
        }
        return j > j2 ? 1 : 0;
    }

    private static boolean checkAspectRatiosMatch(Size size, Size size2) {
        return ((double) Math.abs((((float) size.getWidth()) / ((float) size.getHeight())) - (((float) size2.getWidth()) / ((float) size2.getHeight())))) < 0.03d;
    }
}
