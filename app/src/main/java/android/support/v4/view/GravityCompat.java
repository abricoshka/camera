package android.support.v4.view;

import android.os.Build;
import android.view.Gravity;

/* loaded from: classes.dex */
public final class GravityCompat {
    public static int getAbsoluteGravity(int i, int i2) {
        if (Build.VERSION.SDK_INT >= 17) {
            return Gravity.getAbsoluteGravity(i, i2);
        }
        return (-8388609) & i;
    }

    private GravityCompat() {
    }
}
