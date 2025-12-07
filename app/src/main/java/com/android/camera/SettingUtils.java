package com.android.camera;

import android.content.Context;
import android.view.View;
import com.mediatek.camera.R;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/* loaded from: classes.dex */
public class SettingUtils {
    private static final DecimalFormat DECIMAL_FORMATOR = new DecimalFormat("######.####", new DecimalFormatSymbols(Locale.ENGLISH));

    private SettingUtils() {
    }

    public static void setEnabledState(View view, boolean z) {
        if (view != null) {
            view.setAlpha(z ? 1.0f : 0.3f);
        }
    }

    public static boolean contains(int[] iArr, int i) {
        if (iArr == null) {
            return false;
        }
        for (int i2 : iArr) {
            if (i2 == i) {
                return true;
            }
        }
        return false;
    }

    public static int index(CharSequence[] charSequenceArr, String str) {
        if (charSequenceArr == null || str == null) {
            return -1;
        }
        int length = charSequenceArr.length;
        for (int i = 0; i < length; i++) {
            if (str.equals(charSequenceArr[i])) {
                return i;
            }
        }
        return -1;
    }

    public static int getMainColor(Context context) {
        return context.getResources().getColor(R.color.setting_item_text_color_highlight);
    }
}
