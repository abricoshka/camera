package com.mediatek.camera.p005v2.exif;

import com.mediatek.camera.debug.LogHelper;
import java.io.IOException;

/* loaded from: classes.dex */
public class Exif {
    private static final LogHelper.Tag TAG = new LogHelper.Tag(Exif.class.getSimpleName());

    public static ExifInterface getExif(byte[] bArr) {
        ExifInterface exifInterface = new ExifInterface();
        try {
            exifInterface.readExif(bArr);
        } catch (IOException e) {
            LogHelper.m29w(TAG, "Failed to read EXIF data", e);
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
}
