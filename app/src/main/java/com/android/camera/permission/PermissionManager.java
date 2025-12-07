package com.android.camera.permission;

import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import com.android.camera.CameraActivity;
import com.android.camera.Log;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/* loaded from: classes.dex */
public class PermissionManager {
    private static final int CAM_REQUEST_CODE_ASK_LAUNCH_PERMISSIONS = 100;
    private static final int CAM_REQUEST_CODE_ASK_LOCATION_PERMISSIONS = 101;
    private static final String TAG = "PermissionManager";
    private final CameraActivity mActivity;
    private List<String> mLauchPermissionList = new ArrayList();
    private List<String> mLocationPermissionList = new ArrayList();

    public PermissionManager(CameraActivity cameraActivity) {
        this.mActivity = cameraActivity;
        initCameraLaunchPermissionList();
        initCameraLocationPermissionList();
    }

    private void initCameraLaunchPermissionList() {
        this.mLauchPermissionList.add("android.permission.CAMERA");
        this.mLauchPermissionList.add("android.permission.RECORD_AUDIO");
        this.mLauchPermissionList.add("android.permission.WRITE_EXTERNAL_STORAGE");
        this.mLauchPermissionList.add("android.permission.READ_EXTERNAL_STORAGE");
    }

    private void initCameraLocationPermissionList() {
        this.mLocationPermissionList.add("android.permission.ACCESS_COARSE_LOCATION");
        this.mLocationPermissionList.add("android.permission.ACCESS_FINE_LOCATION");
    }

    private List<String> getNeedCheckPermissionList(List<String> list) {
        if (list.size() <= 0) {
            return list;
        }
        ArrayList arrayList = new ArrayList();
        for (String str : list) {
            if (ContextCompat.checkSelfPermission(this.mActivity, str) != 0) {
                arrayList.add(str);
            }
        }
        return arrayList;
    }

    public boolean checkCameraLaunchPermissions() {
        return getNeedCheckPermissionList(this.mLauchPermissionList).size() <= 0;
    }

    public boolean requestCameraLaunchPermissions() {
        List<String> needCheckPermissionList = getNeedCheckPermissionList(this.mLauchPermissionList);
        if (needCheckPermissionList.size() > 0) {
            Log.m5d(TAG, "requestCameraLaunchPermissions(), user check");
            ActivityCompat.requestPermissions(this.mActivity, (String[]) needCheckPermissionList.toArray(new String[needCheckPermissionList.size()]), CAM_REQUEST_CODE_ASK_LAUNCH_PERMISSIONS);
            return false;
        }
        Log.m5d(TAG, "requestCameraLaunchPermissions(), all on");
        return true;
    }

    public boolean requestCameraLocationPermissions() {
        List<String> needCheckPermissionList = getNeedCheckPermissionList(this.mLocationPermissionList);
        if (needCheckPermissionList.size() > 0) {
            Log.m5d(TAG, "requestCameraLocationPermissions(), user check");
            ActivityCompat.requestPermissions(this.mActivity, (String[]) needCheckPermissionList.toArray(new String[needCheckPermissionList.size()]), CAM_REQUEST_CODE_ASK_LOCATION_PERMISSIONS);
            return false;
        }
        Log.m5d(TAG, "requestCameraLocationPermissions(), all on");
        return true;
    }

    public int getCameraLaunchPermissionRequestCode() {
        return CAM_REQUEST_CODE_ASK_LAUNCH_PERMISSIONS;
    }

    public int getCameraLocationPermissionRequestCode() {
        return CAM_REQUEST_CODE_ASK_LOCATION_PERMISSIONS;
    }

    public boolean isCameraLaunchPermissionsResultReady(String[] strArr, int[] iArr) {
        HashMap map = new HashMap();
        map.put("android.permission.CAMERA", 0);
        map.put("android.permission.RECORD_AUDIO", 0);
        map.put("android.permission.WRITE_EXTERNAL_STORAGE", 0);
        map.put("android.permission.READ_EXTERNAL_STORAGE", 0);
        for (int i = 0; i < strArr.length; i++) {
            map.put(strArr[i], Integer.valueOf(iArr[i]));
        }
        return ((Integer) map.get("android.permission.CAMERA")).intValue() == 0 && ((Integer) map.get("android.permission.RECORD_AUDIO")).intValue() == 0 && ((Integer) map.get("android.permission.WRITE_EXTERNAL_STORAGE")).intValue() == 0 && ((Integer) map.get("android.permission.READ_EXTERNAL_STORAGE")).intValue() == 0;
    }

    public boolean isCameraLocationPermissionsResultReady(String[] strArr, int[] iArr) {
        HashMap map = new HashMap();
        map.put("android.permission.ACCESS_COARSE_LOCATION", 0);
        map.put("android.permission.ACCESS_FINE_LOCATION", 0);
        for (int i = 0; i < strArr.length; i++) {
            map.put(strArr[i], Integer.valueOf(iArr[i]));
        }
        return ((Integer) map.get("android.permission.ACCESS_COARSE_LOCATION")).intValue() == 0 && ((Integer) map.get("android.permission.ACCESS_FINE_LOCATION")).intValue() == 0;
    }
}
