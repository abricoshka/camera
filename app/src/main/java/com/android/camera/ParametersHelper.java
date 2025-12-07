package com.android.camera;

import android.hardware.Camera;

/* loaded from: classes.dex */
public class ParametersHelper {
    private static int sStereoCaptureSupportBySensor = -1;
    private static int sStereoDenoiseSupportBySensor = -1;

    public static boolean isNativePIPSupported(Camera.Parameters parameters) {
        if (parameters == null) {
            return false;
        }
        return "true".equals(parameters.get("native-pip-supported"));
    }

    public static int getIdStereoCaptureSupt(Camera.Parameters parameters) {
        if (sStereoCaptureSupportBySensor != -1) {
            return sStereoCaptureSupportBySensor;
        }
        if (parameters != null) {
            String str = parameters.get("stereo-capture-supported-module");
            Log.m5d("ParametersHelper", "getIdStereoCaptureSupt str = " + str);
            if (str != null) {
                if (str.contains("rear") && str.contains("front")) {
                    sStereoCaptureSupportBySensor = 2;
                } else if (str.contains("rear")) {
                    sStereoCaptureSupportBySensor = 0;
                } else if (str.contains("front")) {
                    sStereoCaptureSupportBySensor = 1;
                }
            }
        }
        return sStereoCaptureSupportBySensor;
    }

    public static boolean isVsDofSupported(Camera.Parameters parameters) {
        if (parameters == null) {
            return false;
        }
        String str = parameters.get("stereo-vsdof-mode-values");
        return ("off".equals(str) || str == null) ? false : true;
    }

    public static int getIdStereoDenoiseSupt(Camera.Parameters parameters) {
        if (sStereoDenoiseSupportBySensor != -1) {
            return sStereoDenoiseSupportBySensor;
        }
        if (parameters != null) {
            String str = parameters.get("stereo-denoise-supported-module");
            Log.m5d("ParametersHelper", "getIdStereoCaptureSupt str = " + str);
            if (str != null) {
                if (str.contains("rear") && str.contains("front")) {
                    sStereoDenoiseSupportBySensor = 2;
                } else if (str.contains("rear")) {
                    sStereoDenoiseSupportBySensor = 0;
                } else if (str.contains("front")) {
                    sStereoDenoiseSupportBySensor = 1;
                }
            }
        }
        return sStereoDenoiseSupportBySensor;
    }

    public static boolean isDisplayRotateSupported(Camera.Parameters parameters) {
        String str = parameters.get("disp-rot-supported");
        if (str == null || "false".equals(str)) {
            return false;
        }
        return true;
    }

    public static void setPanelSize(Camera.Parameters parameters, String str) {
        if (str != null) {
            parameters.set("panel-size", str);
        }
    }
}
