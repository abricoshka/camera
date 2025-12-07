package android.support.v4.text;

import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import java.util.Locale;

/* loaded from: classes.dex */
public final class TextUtilsCompat {
    private static final Locale ROOT = new Locale("", "");

    public static int getLayoutDirectionFromLocale(@Nullable Locale locale) {
        if (Build.VERSION.SDK_INT >= 17) {
            return TextUtils.getLayoutDirectionFromLocale(locale);
        }
        if (locale != null && (!locale.equals(ROOT))) {
            String strMaximizeAndGetScript = ICUCompat.maximizeAndGetScript(locale);
            if (strMaximizeAndGetScript == null) {
                return getLayoutDirectionFromFirstChar(locale);
            }
            if (strMaximizeAndGetScript.equalsIgnoreCase("Arab") || strMaximizeAndGetScript.equalsIgnoreCase("Hebr")) {
                return 1;
            }
            return 0;
        }
        return 0;
    }

    private static int getLayoutDirectionFromFirstChar(@NonNull Locale locale) {
        switch (Character.getDirectionality(locale.getDisplayName(locale).charAt(0))) {
            case 1:
            case 2:
                return 1;
            default:
                return 0;
        }
    }

    private TextUtilsCompat() {
    }
}
