package com.android.camera.p002v2.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.util.Size;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import com.android.camera.p002v2.p003ui.Rotatable;
import com.mediatek.camera.R;
import com.mediatek.camera.debug.LogHelper;
import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class CameraUtil {
    private static final LogHelper.Tag TAG = new LogHelper.Tag(CameraUtil.class.getSimpleName());
    private static final double[] RATIOS = {1.3333d, 1.5d, 1.6667d, 1.7778d};

    private CameraUtil() {
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

    /* JADX WARN: Multi-variable type inference failed */
    public static void setOrientation(View view, int i, boolean z) {
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
                setOrientation(viewGroup.getChildAt(i2), i, z);
            }
        }
    }

    public static int getDisplayRotation(Context context) {
        switch (((WindowManager) context.getSystemService("window")).getDefaultDisplay().getRotation()) {
        }
        return 0;
    }

    public static void setEnabledState(View view, boolean z) {
        if (view != null) {
            view.setAlpha(z ? 1.0f : 0.3f);
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

    public static String buildEnabledList(String str, String str2) {
        if (str == null) {
            return null;
        }
        if (str.split(";").length < 2) {
            return str;
        }
        return "[L];" + str2 + ";" + str;
    }

    public static boolean isBuiltList(String str) {
        boolean z = false;
        if (str != null && str.startsWith("[L];")) {
            z = true;
        }
        LogHelper.m23d(TAG, "isBuiltList(" + str + ") return " + z);
        return z;
    }

    public static List<String> getEnabledList(String str) {
        ArrayList arrayList = new ArrayList();
        if (isBuiltList(str)) {
            String[] strArrSplit = str.split(";");
            int length = strArrSplit.length;
            for (int i = 2; i < length; i++) {
                if (!arrayList.contains(strArrSplit[i])) {
                    arrayList.add(strArrSplit[i]);
                }
            }
        }
        LogHelper.m23d(TAG, "getEnabledList(" + str + ") return " + arrayList);
        return arrayList;
    }

    public static String getDefaultValue(String str) {
        String[] strArrSplit;
        String str2 = null;
        if (isBuiltList(str) && (strArrSplit = str.split(";")) != null && strArrSplit.length > 1) {
            str2 = strArrSplit[1];
        }
        LogHelper.m26i(TAG, "getDefaultValue(" + str + ") return " + str2);
        return str2;
    }

    public static boolean isDisableValue(String str) {
        boolean z = false;
        if ("disable-value".equals(str)) {
            z = true;
        }
        LogHelper.m23d(TAG, "isResetValue(" + str + ") return " + z);
        return z;
    }

    public static int getMainColor(Context context) throws Resources.NotFoundException {
        int color = context.getResources().getColor(R.color.setting_item_text_color_highlight);
        LogHelper.m23d(TAG, "getMainColor" + color);
        return color;
    }

    public static double findFullscreenRatio(Context context) {
        double d;
        double d2 = 1.3333333333333333d;
        Display defaultDisplay = ((WindowManager) context.getSystemService("window")).getDefaultDisplay();
        Point point = new Point();
        defaultDisplay.getRealSize(point);
        if (point.x > point.y) {
            d = point.x / point.y;
        } else {
            d = point.y / point.x;
        }
        LogHelper.m23d(TAG, "fullscreen = " + d + " x = " + point.x + " y = " + point.y);
        for (int i = 0; i < RATIOS.length; i++) {
            if (Math.abs(RATIOS[i] - d) < Math.abs(d - d2)) {
                d2 = RATIOS[i];
            }
        }
        LogHelper.m23d(TAG, "findFullscreenRatio, return ratio:" + d2);
        return d2;
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

    public static boolean toleranceRatio(Size size, Size size2) {
        double width = size.getWidth() / size.getHeight();
        double width2 = size2.getWidth() / size2.getHeight();
        boolean z = Math.abs(width - width2) <= 0.03d;
        LogHelper.m23d(TAG, "toleranceRatio(" + width + ", " + width2 + ") return " + z);
        return z;
    }

    public static int index(CharSequence[] charSequenceArr, String str) {
        int i;
        if (charSequenceArr == null || str == null) {
            i = -1;
        } else {
            i = 0;
            int length = charSequenceArr.length;
            while (true) {
                if (i >= length) {
                    i = -1;
                    break;
                }
                if (str.equals(charSequenceArr[i])) {
                    break;
                }
                i++;
            }
        }
        LogHelper.m23d(TAG, "index(" + charSequenceArr + ", " + str + ") return " + i);
        return i;
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

    public static boolean isMimeTypeVideo(String str) {
        if (str != null) {
            return str.startsWith("video/");
        }
        return false;
    }

    public static boolean isMimeTypeImage(String str) {
        if (str != null) {
            return str.startsWith("image/");
        }
        return false;
    }
}
