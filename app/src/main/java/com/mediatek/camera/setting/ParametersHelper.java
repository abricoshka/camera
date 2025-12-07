package com.mediatek.camera.setting;

import android.hardware.Camera;
import android.media.CameraProfile;
import android.support.v4.app.FrameMetricsAggregator;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import com.mediatek.camera.ICameraContext;
import com.mediatek.camera.platform.Parameters;
import com.mediatek.camera.util.Log;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes.dex */
public class ParametersHelper {
    private static int sVsDofSupported = -1;
    private static int sStereoCaptureSupported = -1;
    private static int sDenoiseSupported = -1;

    public static void setParametersValue(Parameters parameters, int i, String str, String str2) {
        String str3;
        int settingId = SettingConstants.getSettingId(str);
        if (str2 == null) {
        }
        switch (settingId) {
            case FrameMetricsAggregator.DELAY_INDEX /* 7 */:
                parameters.set("slow-motion", str2);
                break;
            case 8:
                if (isParametersSupported(parameters, str, str2)) {
                    parameters.set("video-hdr", str2);
                    break;
                }
                break;
            case 9:
            case 42:
            case 55:
            case 57:
            case 58:
            default:
                Log.m32e("ParametersHelper", "[setParametersValue]key value is wrong, key:" + str);
                break;
            case 10:
            case 15:
            case 17:
            case 18:
            case 22:
            case 23:
            case 24:
            case 27:
            case 29:
            case 36:
            case 46:
            case 47:
            case 54:
                break;
            case 11:
                parameters.setExposureCompensation(Integer.parseInt(str2));
                break;
            case 12:
                if (!parameters.getSceneMode().equals(str2)) {
                    parameters.setSceneMode(str2);
                    break;
                }
                break;
            case 13:
                parameters.setWhiteBalance(str2);
                break;
            case 14:
                parameters.setColorEffect(str2);
                break;
            case 16:
                parameters.setBurstShotNum(Integer.parseInt(str2));
                break;
            case 19:
                parameters.setISOSpeed(str2);
                break;
            case 20:
                parameters.setAntibanding(str2);
                break;
            case 21:
                parameters.setVideoStabilization("on".equals(str2));
                break;
            case NotificationCompat.MessagingStyle.MAXIMUM_RETAINED_MESSAGES /* 25 */:
                int iIndexOf = str2.indexOf(120);
                if (iIndexOf == -1) {
                    Log.m36w("ParametersHelper", "[setParameters]index = -1,return!");
                    break;
                } else {
                    parameters.setPictureSize(Integer.parseInt(str2.substring(0, iIndexOf)), Integer.parseInt(str2.substring(iIndexOf + 1)));
                    break;
                }
            case 26:
                parameters.setZSDMode(str2);
                break;
            case 28:
                parameters.set("3dnr-mode", str2);
                break;
            case 30:
                parameters.set("mfb", str2);
                break;
            case 31:
                parameters.setEdgeMode(str2);
                break;
            case 32:
                parameters.setHueMode(str2);
                break;
            case 33:
                parameters.setSaturationMode(str2);
                break;
            case 34:
                parameters.setBrightnessMode(str2);
                break;
            case 35:
                parameters.setContrastMode(str2);
                break;
            case 37:
                parameters.setCameraMode(Integer.parseInt(str2));
                break;
            case 38:
                parameters.setCaptureMode(str2);
                break;
            case 39:
                parameters.setRecordingHint(Boolean.parseBoolean(str2));
                break;
            case 40:
                parameters.setFlashMode(str2);
                break;
            case 41:
                parameters.setJpegQuality(CameraProfile.getJpegEncodingQualityParameter(i, Integer.parseInt(str2)));
                break;
            case 43:
                parameters.set("fb-smooth-level", str2);
                break;
            case 44:
                parameters.set("fb-skin-color", str2);
                break;
            case 45:
                parameters.set("fb-sharp", str2);
                break;
            case 48:
                parameters.enableRecordingSound(str2);
                break;
            case 49:
                if (str2 != null) {
                    if (str2 != null && "Single".equals(str2)) {
                        str3 = "true";
                    } else {
                        str3 = "Multi".equals(str2) ? "false" : null;
                    }
                    if (str3 != null) {
                        parameters.set("fb-extreme-beauty", str3);
                        break;
                    }
                }
                break;
            case 50:
                parameters.set("fb-slim-face", str2);
                break;
            case 51:
                parameters.set("fb-enlarge-eye", str2);
                break;
            case 52:
                parameters.setDepthAFMode("on".equals(str2));
                break;
            case 53:
                parameters.setDistanceMode("on".equals(str2));
                break;
            case 56:
                if (isHeartbeatMonitorSupported(parameters)) {
                    parameters.set("mtk-heartbeat-monitor", str2);
                    break;
                }
                break;
            case 59:
                if ("multi".equals(str2)) {
                    parameters.set("mzaf-enable", String.valueOf(Boolean.TRUE));
                    break;
                } else {
                    parameters.set("mzaf-enable", String.valueOf(Boolean.FALSE));
                    break;
                }
        }
    }

    public static String getParametersValue(Parameters parameters, String str) {
        switch (SettingConstants.getSettingId(str)) {
            case FrameMetricsAggregator.DELAY_INDEX /* 7 */:
                String str2 = parameters.get("slow-motion");
                Log.m34i("ParametersHelper", "parameters.set/value = " + str2);
                break;
            case 8:
                break;
            case 9:
            case 39:
            case 42:
            default:
                Log.m32e("ParametersHelper", "[getParametersValue]key value is wrong, key:" + str);
                break;
            case 10:
            case 15:
            case 17:
            case 18:
            case 22:
            case 23:
            case 24:
            case 27:
            case 29:
            case 36:
            case 37:
            case 46:
            case 47:
            case 48:
            case 54:
                break;
            case 11:
                break;
            case 12:
                break;
            case 13:
                break;
            case 14:
                break;
            case 16:
                break;
            case 19:
                break;
            case 20:
                break;
            case 21:
                break;
            case NotificationCompat.MessagingStyle.MAXIMUM_RETAINED_MESSAGES /* 25 */:
                Camera.Size pictureSize = parameters.getPictureSize();
                break;
            case 26:
                break;
            case 28:
                break;
            case 30:
                break;
            case 31:
                break;
            case 32:
                break;
            case 33:
                break;
            case 34:
                break;
            case 35:
                break;
            case 38:
                break;
            case 40:
                break;
            case 41:
                break;
            case 43:
                break;
            case 44:
                break;
            case 45:
                break;
            case 49:
                break;
            case 50:
                break;
            case 51:
                break;
            case 52:
                break;
            case 53:
                break;
        }
        return null;
    }

    public static boolean isParametersSupported(Parameters parameters, String str, String str2) {
        List<String> parametersSupportedValues = null;
        switch (SettingConstants.getSettingId(str)) {
            case 8:
                parametersSupportedValues = getParametersSupportedValues(parameters, str);
                break;
        }
        if (parametersSupportedValues == null || !parametersSupportedValues.contains(str2)) {
            return false;
        }
        return true;
    }

    public static List<String> getParametersSupportedValues(Parameters parameters, String str) {
        switch (SettingConstants.getSettingId(str)) {
            case FrameMetricsAggregator.DELAY_INDEX /* 7 */:
                return getSupportedValues(parameters, "slow-motion");
            case 8:
                return getSupportedValues(parameters, "video-hdr");
            case 9:
            case 10:
            case 11:
            case 15:
            case 16:
            case 17:
            case 18:
            case 21:
            case 22:
            case 23:
            case 24:
            case NotificationCompat.MessagingStyle.MAXIMUM_RETAINED_MESSAGES /* 25 */:
            case 27:
            case 29:
            case 36:
            case 37:
            case 39:
            default:
                Log.m32e("ParametersHelper", "key value is wrong, key:" + str);
                return null;
            case 12:
                return parameters.getSupportedSceneModes();
            case 13:
                return parameters.getSupportedWhiteBalance();
            case 14:
                return parameters.getSupportedColorEffects();
            case 19:
                return parameters.getSupportedISOSpeed();
            case 20:
                return parameters.getSupportedAntibanding();
            case 26:
                return parameters.getSupportedZSDMode();
            case 28:
                return getSupportedValues(parameters, "3dnr-mode");
            case 30:
                return getSupportedValues(parameters, "mfb");
            case 31:
                return parameters.getSupportedEdgeMode();
            case 32:
                return parameters.getSupportedHueMode();
            case 33:
                return parameters.getSupportedSaturationMode();
            case 34:
                return parameters.getSupportedBrightnessMode();
            case 35:
                return parameters.getSupportedContrastMode();
            case 38:
                return parameters.getSupportedCaptureMode();
            case 40:
                return parameters.getSupportedFlashModes();
        }
    }

    public static boolean isCfbSupported(Parameters parameters, ICameraContext iCameraContext) {
        if (parameters != null) {
            if (!iCameraContext.getFeatureConfig().isCfbEnable()) {
                return false;
            }
            return isSupporteFBProperties(parameters, "fb-smooth-level");
        }
        throw new RuntimeException("(ParametersHelper)why parameters is null?");
    }

    public static boolean isSupporteFBProperties(Parameters parameters, String str) {
        return (getMaxLevel(parameters, str) == 0 || getMinLevel(parameters, str) == 0) ? false : true;
    }

    public static boolean isDngSupported(Parameters parameters) {
        String str;
        if (parameters == null || (str = parameters.get("dng-supported")) == null) {
            return false;
        }
        return Boolean.parseBoolean(str);
    }

    public static boolean isDepthAfSupported(Parameters parameters) {
        if (parameters == null) {
            return false;
        }
        String str = parameters.get("stereo-depth-af-values");
        return ("off".equals(str) || str == null) ? false : true;
    }

    public static boolean isVsDofSupported(Parameters parameters) {
        if (sVsDofSupported != -1) {
            return sVsDofSupported == 1;
        }
        if (parameters == null) {
            return false;
        }
        String str = parameters.get("stereo-vsdof-mode-values");
        if ("off".equals(str) || str == null) {
            sVsDofSupported = 0;
            return false;
        }
        sVsDofSupported = 1;
        return true;
    }

    public static boolean isDenoiseSupported(Parameters parameters) {
        if (sDenoiseSupported != -1) {
            return sDenoiseSupported == 1;
        }
        if (parameters == null) {
            return false;
        }
        String str = parameters.get("stereo-denoise-mode-values");
        if ("off".equals(str) || str == null) {
            sDenoiseSupported = 0;
            return false;
        }
        sDenoiseSupported = 1;
        return true;
    }

    public static boolean isDistanceInfoSuppported(Parameters parameters) {
        if (parameters == null) {
            return false;
        }
        String str = parameters.get("stereo-distance-measurement-values");
        Log.m34i("ParametersHelper", "isDistanceInfoSuppported " + str);
        return ("off".equals(str) || str == null) ? false : true;
    }

    public static boolean isHeartbeatMonitorSupported(Parameters parameters) {
        String str;
        return (parameters == null || (str = parameters.get("mtk-heartbeat-monitor-supported")) == null || !Boolean.valueOf(str).booleanValue()) ? false : true;
    }

    public static boolean isDisplayRotateSupported(Parameters parameters) {
        String str = parameters.get("disp-rot-supported");
        if (str == null || "false".equals(str)) {
            return false;
        }
        return true;
    }

    public static boolean isSingleFrameCapHdrSupported(Parameters parameters) {
        String str = parameters.get("single-frame-cap-hdr-supported");
        if (str == null || !"true".equals(str)) {
            return false;
        }
        return true;
    }

    public static void setVsDofMode(Parameters parameters, boolean z) {
        Log.m34i("ParametersHelper", "setVsDofMode:" + z);
        parameters.set("stereo-vsdof-mode", z ? "on" : "off");
    }

    public static void setStereoCaptureMode(Parameters parameters, boolean z) {
        Log.m34i("ParametersHelper", "setStereoCaptureMode:" + z);
        parameters.set("stereo-image-refocus", z ? "on" : "off");
    }

    public static void setDenoiseMode(Parameters parameters, boolean z) {
        Log.m34i("ParametersHelper", "setDenoiseMode:" + z);
        parameters.set("stereo-denoise-mode", z ? "on" : "off");
    }

    public static boolean isMultiZoneAFSupported(Parameters parameters) {
        String str;
        return (parameters == null || (str = parameters.get("is-mzaf-supported")) == null || !Boolean.valueOf(str).booleanValue()) ? false : true;
    }

    public static int getMaxLevel(Parameters parameters, String str) {
        return getInt(parameters, str + "-max");
    }

    public static int getMinLevel(Parameters parameters, String str) {
        return getInt(parameters, str + "-min");
    }

    private static int getInt(Parameters parameters, String str) {
        if (parameters == null) {
            return 0;
        }
        try {
            return Integer.parseInt(parameters.get(str));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public static ArrayList<String> split(String str) {
        if (str == null) {
            return null;
        }
        TextUtils.SimpleStringSplitter simpleStringSplitter = new TextUtils.SimpleStringSplitter(',');
        simpleStringSplitter.setString(str);
        ArrayList<String> arrayList = new ArrayList<>();
        Iterator it = simpleStringSplitter.iterator();
        while (it.hasNext()) {
            arrayList.add((String) it.next());
        }
        return arrayList;
    }

    private static List<String> getSupportedValues(Parameters parameters, String str) {
        if (parameters != null) {
            return split(parameters.get(str + "-values"));
        }
        return null;
    }
}
