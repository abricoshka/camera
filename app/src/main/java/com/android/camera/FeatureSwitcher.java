package com.android.camera;

import android.app.Activity;
import android.os.SystemProperties;
import com.mediatek.camera.debug.DebugProperty;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/* loaded from: classes.dex */
public class FeatureSwitcher {
    public static boolean isSupportDoubleTapUp() {
        return false;
    }

    public static boolean isStereo3dEnable() {
        return false;
    }

    public static boolean is2SdCardSwapSupport() {
        return SystemProperties.getInt("ro.mtk_2sdcard_swap", 0) == 1;
    }

    public static boolean isStereoSingle3d() {
        return false;
    }

    public static boolean isSlowMotionSupport() {
        boolean z = SystemProperties.getInt("ro.mtk_slow_motion_support", 0) == 1;
        int i = SystemProperties.getInt("slow_motion_on_off", 0);
        if (i == 1) {
            return true;
        }
        if (i == 2) {
            return false;
        }
        return z;
    }

    public static boolean isGmoROM() {
        return SystemProperties.getInt("ro.mtk_gmo_rom_optimize", 0) == 1;
    }

    public static boolean isGmoRAM() {
        return SystemProperties.getInt("ro.mtk_gmo_ram_optimize", 0) == 1;
    }

    public static boolean isLowRAM() {
        return Util.getDeviceRam() <= 524288;
    }

    public static String whichDeanliChip() throws IOException {
        byte[] bArr = new byte[8];
        StringBuilder sb = new StringBuilder();
        try {
            FileInputStream fileInputStream = new FileInputStream("/proc/chip/hw_code");
            while (true) {
                int i = fileInputStream.read(bArr);
                if (i <= 0) {
                    break;
                }
                sb.append(new String(bArr, 0, i - 1));
            }
            if (fileInputStream != null) {
                fileInputStream.close();
            }
        } catch (FileNotFoundException e) {
            com.mediatek.camera.util.Log.m33e("FeatureSwitcher", "FileNotFoundException ", e);
        } catch (IOException e2) {
            com.mediatek.camera.util.Log.m33e("FeatureSwitcher", "exception 1: ", e2);
        }
        com.mediatek.camera.util.Log.m34i("FeatureSwitcher", "whichDeanliChip  " + sb.toString());
        if ("0321".equals(sb.toString())) {
            return "0321";
        }
        if ("0335".equals(sb.toString())) {
            return "0335";
        }
        if ("0337".equals(sb.toString())) {
            return "0337";
        }
        return null;
    }

    public static boolean isOnlyCheckBackCamera() {
        return false;
    }

    public static boolean isMtkFatOnNand() {
        return SystemProperties.getInt("ro.mtk_fat_on_nand", 0) == 1;
    }

    public static boolean isTablet() {
        return SystemProperties.get("ro.build.characteristics").equals("tablet");
    }

    public static boolean isMultiWindow() {
        return SystemProperties.getInt("ro.mtk_multiwindow", 0) == 1;
    }

    public static boolean isNativePIPEnabled() {
        if (!(SystemProperties.getInt("ro.mtk_cam_native_pip_support", 0) == 1) || -1 == CameraHolder.instance().getBackCameraId()) {
            return false;
        }
        return -1 != CameraHolder.instance().getFrontCameraId();
    }

    public static boolean isLomoEffectEnabled() {
        return SystemProperties.getInt("ro.mtk_cam_lomo_support", 0) == 1;
    }

    public static boolean isVfbEnable() {
        return SystemProperties.getInt("ro.mtk_cam_vfb", 0) == 1;
    }

    public static boolean isCfbEnable() {
        return SystemProperties.getInt("ro.mtk_cam_cfb", 0) == 1;
    }

    public static boolean isPrioritizePreviewSize() {
        return false;
    }

    public static boolean isDualCameraEnable() {
        return false;
    }

    public static boolean isSubSettingEnabled() {
        if (isTablet()) {
            return !isLomoEffectEnabled();
        }
        return false;
    }

    public static boolean isZSDHDRSupported() {
        return SystemProperties.getInt("ro.mtk_zsdhdr_support", 0) == 1;
    }

    public static boolean isApi2Enable(Activity activity) {
        return DebugProperty.isApi2Enable();
    }

    public static boolean isGpsLocationSupported() {
        boolean zEquals = "1".equals(SystemProperties.get("ro.mtk_gps_support"));
        com.mediatek.camera.util.Log.m34i("FeatureSwitcher", "isGpsLocationSupported enabled : " + zEquals);
        return zEquals;
    }
}
