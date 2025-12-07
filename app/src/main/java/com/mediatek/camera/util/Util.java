package com.mediatek.camera.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.hardware.Camera;
import android.media.Image;
import android.media.MediaRecorder;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import com.mediatek.camera.p004ui.Rotatable;
import java.io.Closeable;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import junit.framework.Assert;

/* loaded from: classes.dex */
public class Util {
    private static final Class[] METHOD_TYPES = {String.class};

    public static void fadeIn(View view) {
        fadeIn(view, 0.0f, 1.0f, 400L);
        view.setEnabled(true);
    }

    public static void fadeIn(View view, float f, float f2, long j) {
        if (view.getVisibility() == 0) {
            return;
        }
        view.setVisibility(0);
        AlphaAnimation alphaAnimation = new AlphaAnimation(f, f2);
        alphaAnimation.setDuration(j);
        view.startAnimation(alphaAnimation);
    }

    public static void fadeOut(View view) {
        if (view.getVisibility() != 0) {
            return;
        }
        view.setEnabled(false);
        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
        alphaAnimation.setDuration(400L);
        view.startAnimation(alphaAnimation);
        view.setVisibility(8);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public static void setOrientation(View view, int i, boolean z) {
        if (view == 0) {
            android.util.Log.w("Util", "[setOrientation]view is null,return.");
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
                setOrientation(viewGroup.getChildAt(i2), i, z);
            }
        }
    }

    public static boolean equals(Object obj, Object obj2) {
        if (obj == obj2) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        return obj.equals(obj2);
    }

    public static int getSensorOrientation(Camera.CameraInfo cameraInfo) {
        return cameraInfo.orientation;
    }

    public static int getRecordingRotation(int i, int i2, Camera.CameraInfo cameraInfo) {
        if (i != -1) {
            if (cameraInfo.facing == 1) {
                return ((cameraInfo.orientation - i) + 360) % 360;
            }
            return (cameraInfo.orientation + i) % 360;
        }
        return cameraInfo.orientation;
    }

    public static String createNameFormat(long j, String str) {
        return new SimpleDateFormat(str).format(new Date(j));
    }

    public static int getDisplayRotation(Activity activity) {
        switch (activity.getWindowManager().getDefaultDisplay().getRotation()) {
        }
        return 0;
    }

    public static int computeRotation(Context context, int i, int i2) {
        if (context.getResources().getConfiguration().orientation == 1) {
            return ((i - i2) + 360) % 360;
        }
        return i;
    }

    public static Camera.Size getOptimalVideoSnapshotPictureSize(List<Camera.Size> list, double d) {
        Camera.Size size;
        Camera.Size size2 = null;
        if (list == null) {
            return null;
        }
        Iterator<T> it = list.iterator();
        while (true) {
            size = size2;
            if (!it.hasNext()) {
                break;
            }
            size2 = (Camera.Size) it.next();
            if (Math.abs((size2.width / size2.height) - d) > 0.001d) {
                size2 = size;
            } else if (size != null && size2.width <= size.width) {
                size2 = size;
            }
        }
        if (size == null) {
            for (Camera.Size size3 : list) {
                if (size == null || size3.width > size.width) {
                    size = size3;
                }
            }
        }
        return size;
    }

    public static Camera.Size getOptimalPreviewSize(Activity activity, List<Camera.Size> list, double d, boolean z, boolean z2) throws NumberFormatException {
        double d2;
        double dAbs;
        Camera.Size size;
        double dAbs2;
        double d3;
        Camera.Size size2;
        if (list == null) {
            return null;
        }
        double d4 = Double.MAX_VALUE;
        double d5 = Double.MAX_VALUE;
        Display defaultDisplay = activity.getWindowManager().getDefaultDisplay();
        Point point = new Point();
        defaultDisplay.getSize(point);
        int iMin = Math.min(point.x, point.y);
        int iMax = Math.max(point.x, point.y);
        if (z) {
            double d6 = Double.MAX_VALUE;
            Iterator<T> it = list.iterator();
            while (true) {
                d2 = d6;
                if (!it.hasNext()) {
                    break;
                }
                Camera.Size size3 = (Camera.Size) it.next();
                d6 = size3.width / size3.height;
                if (Math.abs(d6 - d) > Math.abs(d2 - d)) {
                    d6 = d2;
                }
            }
        } else {
            d2 = d;
        }
        Camera.Size size4 = null;
        for (Camera.Size size5 : list) {
            if (Math.abs((size5.width / size5.height) - d2) > 0.001d) {
                dAbs2 = d5;
                d3 = d4;
                size2 = size4;
            } else if (Math.abs(size5.height - iMin) < d4) {
                double dAbs3 = Math.abs(size5.height - iMin);
                dAbs2 = Math.abs(size5.width - iMax);
                d3 = dAbs3;
                size2 = size5;
            } else if (Math.abs(size5.height - iMin) != d4 || Math.abs(size5.width - iMax) >= d5) {
                dAbs2 = d5;
                d3 = d4;
                size2 = size4;
            } else {
                dAbs2 = Math.abs(size5.width - iMax);
                d3 = d4;
                size2 = size5;
            }
            size4 = size2;
            d4 = d3;
            d5 = dAbs2;
        }
        if (size4 == null && z2) {
            double d7 = Double.MAX_VALUE;
            double d8 = Double.parseDouble("1.3333");
            for (Camera.Size size6 : list) {
                if (Math.abs((size6.width / size6.height) - d8) > 0.001d) {
                    dAbs = d7;
                    size = size4;
                } else if (Math.abs(size6.height - iMin) < d7) {
                    dAbs = Math.abs(size6.height - iMin);
                    size = size6;
                } else {
                    dAbs = d7;
                    size = size4;
                }
                size4 = size;
                d7 = dAbs;
            }
        }
        return size4;
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
            android.util.Log.e("Util", "[makeBitmap]Got oom exception:", e);
            return null;
        }
    }

    public static void closeSilently(Closeable closeable) {
        if (closeable == null) {
            android.util.Log.w("Util", "[closeSilently]c is null,return.");
            return;
        }
        try {
            closeable.close();
        } catch (Throwable th) {
            th.printStackTrace();
        }
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

    public static void setParametersExtra(MediaRecorder mediaRecorder, String str) throws Exception {
        try {
            ReflectUtil.callMethodOnObjectWithExp(mediaRecorder, ReflectUtil.getMethod(Class.forName("android.media.MediaRecorder"), "setParameter", METHOD_TYPES), str);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e2) {
            throw new Exception(e2.getCause());
        }
    }
}
