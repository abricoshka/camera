package com.android.camera;

import com.android.camera.bridge.ParametersExt;
import java.util.List;

/* loaded from: classes.dex */
public class ModeChecker {
    private static final String[] MODE_STRING_NORMAL = new String[10];
    private static final boolean[][] MATRIX_NORMAL_ENABLE = new boolean[11][];
    private static final boolean[][] MATRIX_PREVIEW3D_ENABLE = new boolean[11][];
    private static final boolean[][] MATRIX_SINGLE3D_ENABLE = new boolean[11][];

    static {
        MODE_STRING_NORMAL[0] = "normal";
        MODE_STRING_NORMAL[1] = "hdr";
        MODE_STRING_NORMAL[2] = "face_beauty";
        MODE_STRING_NORMAL[3] = "autorama";
        MODE_STRING_NORMAL[4] = "asd";
        MATRIX_NORMAL_ENABLE[0] = new boolean[]{true, true};
        MATRIX_NORMAL_ENABLE[1] = new boolean[]{true, false};
        MATRIX_NORMAL_ENABLE[2] = new boolean[]{true, true};
        MATRIX_NORMAL_ENABLE[3] = new boolean[]{true, false};
        MATRIX_NORMAL_ENABLE[4] = new boolean[]{true, false};
        MATRIX_NORMAL_ENABLE[8] = new boolean[]{true, true};
        MATRIX_NORMAL_ENABLE[5] = new boolean[]{true, true};
        MATRIX_NORMAL_ENABLE[9] = new boolean[]{false, false};
        MATRIX_NORMAL_ENABLE[6] = new boolean[]{true, false};
        MATRIX_NORMAL_ENABLE[10] = new boolean[]{true, false};
        MATRIX_NORMAL_ENABLE[7] = new boolean[]{true, false};
        MATRIX_PREVIEW3D_ENABLE[0] = new boolean[]{true, false};
        MATRIX_PREVIEW3D_ENABLE[1] = new boolean[]{false, false};
        MATRIX_PREVIEW3D_ENABLE[2] = new boolean[]{false, false};
        MATRIX_PREVIEW3D_ENABLE[3] = new boolean[]{false, false};
        MATRIX_PREVIEW3D_ENABLE[4] = new boolean[]{false, false};
        MATRIX_PREVIEW3D_ENABLE[8] = new boolean[]{true, false};
        MATRIX_PREVIEW3D_ENABLE[5] = new boolean[]{false, false};
        MATRIX_SINGLE3D_ENABLE[0] = new boolean[]{true, false};
        MATRIX_SINGLE3D_ENABLE[1] = new boolean[]{false, false};
        MATRIX_SINGLE3D_ENABLE[2] = new boolean[]{false, false};
        MATRIX_SINGLE3D_ENABLE[3] = new boolean[]{true, false};
        MATRIX_SINGLE3D_ENABLE[4] = new boolean[]{false, false};
        MATRIX_SINGLE3D_ENABLE[8] = new boolean[]{false, false};
        MATRIX_SINGLE3D_ENABLE[5] = new boolean[]{false, false};
    }

    public static void updateModeMatrix(CameraActivity cameraActivity, int i) {
        List<String> supportedCaptureMode = new ParametersExt(cameraActivity.getCameraDevice(), cameraActivity.getParameters(), i).getSupportedCaptureMode();
        List<String> supportedSceneModes = cameraActivity.getParameters().getSupportedSceneModes();
        int idStereoCaptureSupt = ParametersHelper.getIdStereoCaptureSupt(cameraActivity.getParameters());
        int idStereoDenoiseSupt = ParametersHelper.getIdStereoDenoiseSupt(cameraActivity.getParameters());
        if (FeatureSwitcher.isStereo3dEnable() && cameraActivity.isStereoMode()) {
            return;
        }
        for (int i2 = 0; i2 < 8; i2++) {
            if (MATRIX_NORMAL_ENABLE[i2][i] && supportedCaptureMode.indexOf(MODE_STRING_NORMAL[i2]) < 0) {
                if (i2 != 1) {
                    MATRIX_NORMAL_ENABLE[i2][i] = false;
                } else if (supportedSceneModes.indexOf(MODE_STRING_NORMAL[i2]) < 0) {
                    MATRIX_NORMAL_ENABLE[i2][i] = false;
                }
            }
        }
        MATRIX_NORMAL_ENABLE[5][i] = ParametersHelper.isNativePIPSupported(cameraActivity.getParameters());
        MATRIX_NORMAL_ENABLE[6][0] = idStereoCaptureSupt == 0 || idStereoCaptureSupt == 2;
        MATRIX_NORMAL_ENABLE[6][1] = idStereoCaptureSupt == 1 || idStereoCaptureSupt == 2;
        MATRIX_NORMAL_ENABLE[7][0] = idStereoDenoiseSupt == 0 || idStereoDenoiseSupt == 2;
        MATRIX_NORMAL_ENABLE[7][1] = idStereoDenoiseSupt == 1 || idStereoDenoiseSupt == 2;
        if (CameraHolder.instance().getBackCameraId() == -1 || CameraHolder.instance().getFrontCameraId() == -1) {
            MATRIX_NORMAL_ENABLE[5][i] = false;
        }
        updateCaptureFaceBeauty(i);
    }

    public static boolean getModePickerVisible(CameraActivity cameraActivity, int i, int i2) {
        boolean[][] zArr;
        int iUpdateCameraId = updateCameraId(cameraActivity, i);
        boolean zIsStereoMode = cameraActivity.isStereoMode();
        if (FeatureSwitcher.isStereoSingle3d() && zIsStereoMode) {
            zArr = MATRIX_SINGLE3D_ENABLE;
        } else if (zIsStereoMode) {
            zArr = MATRIX_PREVIEW3D_ENABLE;
        } else {
            zArr = MATRIX_NORMAL_ENABLE;
        }
        boolean z = zArr[i2 % 100][iUpdateCameraId];
        if (8 == i2 || 108 == i2) {
            return true;
        }
        return z;
    }

    public static int modesShowInPicker(CameraActivity cameraActivity, int i) {
        boolean[][] zArr;
        int i2 = 0;
        boolean zIsStereoMode = cameraActivity.isStereoMode();
        if (FeatureSwitcher.isStereoSingle3d() && zIsStereoMode) {
            zArr = MATRIX_SINGLE3D_ENABLE;
        } else if (zIsStereoMode) {
            zArr = MATRIX_PREVIEW3D_ENABLE;
        } else {
            zArr = MATRIX_NORMAL_ENABLE;
        }
        for (int i3 = 0; i3 < 8; i3++) {
            if (zArr[i3][i] && i3 != 4 && i3 != 1) {
                i2++;
            }
        }
        if (zArr[2][i] && FeatureSwitcher.isVfbEnable()) {
            return i2 - 1;
        }
        return i2;
    }

    private static int updateCameraId(CameraActivity cameraActivity, int i) {
        if (cameraActivity.isDualCameraDeviceEnable()) {
            return cameraActivity.getOriCameraId();
        }
        return i;
    }

    private static void updateCaptureFaceBeauty(int i) {
        MATRIX_NORMAL_ENABLE[2][i] = FeatureSwitcher.isCfbEnable();
        int length = (MATRIX_NORMAL_ENABLE[2].length - i) - 1;
        if (length > 0) {
            MATRIX_NORMAL_ENABLE[2][length] = FeatureSwitcher.isCfbEnable();
        }
    }
}
