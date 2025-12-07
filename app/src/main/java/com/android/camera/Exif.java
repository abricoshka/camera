package com.android.camera;

import com.mediatek.camera.p005v2.exif.ExifInterface;
import java.io.IOException;

/* loaded from: classes.dex */
public class Exif {
    public static ExifInterface getExif(byte[] bArr) {
        ExifInterface exifInterface = new ExifInterface();
        try {
            exifInterface.readExif(bArr);
        } catch (IOException e) {
            android.util.Log.w("CameraExif", "Failed to read EXIF data", e);
        }
        return exifInterface;
    }

    public static int getOrientation(ExifInterface exifInterface) {
        Integer tagIntValue = exifInterface.getTagIntValue(ExifInterface.TAG_ORIENTATION);
        if (tagIntValue == null) {
            return 0;
        }
        return ExifInterface.getRotationForOrientationValue(tagIntValue.shortValue());
    }

    public static int getOrientation(byte[] bArr) {
        if (bArr == null) {
            return 0;
        }
        return getOrientation(getExif(bArr));
    }

    public static long getFocusValueLow(ExifInterface exifInterface) {
        Long tagLongValue = exifInterface.getTagLongValue(ExifInterface.TAG_FOCUS_VALUE_HIGH);
        if (tagLongValue == null) {
            return 0L;
        }
        android.util.Log.i("CameraExif", "focus value low = " + tagLongValue.longValue());
        return tagLongValue.longValue();
    }

    public static long getFocusValueLow(byte[] bArr) {
        if (bArr == null) {
            return 0L;
        }
        return getFocusValueLow(getExif(bArr));
    }

    public static long getFocusValueHigh(ExifInterface exifInterface) {
        Long tagLongValue = exifInterface.getTagLongValue(ExifInterface.TAG_FOCUS_VALUE_LOW);
        if (tagLongValue == null) {
            return 0L;
        }
        android.util.Log.i("CameraExif", "focus value high = " + tagLongValue.longValue());
        return tagLongValue.longValue();
    }

    public static long getFocusValueHigh(byte[] bArr) {
        if (bArr == null) {
            return 0L;
        }
        return getFocusValueHigh(getExif(bArr));
    }
}
