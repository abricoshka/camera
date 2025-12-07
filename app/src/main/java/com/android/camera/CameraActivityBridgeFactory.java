package com.android.camera;

import java.util.HashMap;
import java.util.Map;

/* loaded from: classes.dex */
public class CameraActivityBridgeFactory {
    private static final String TAG = CameraActivityBridgeFactory.class.getSimpleName();
    private static Map<CameraActivity, Object> mCameraActivityBridgeMap = new HashMap();

    public static synchronized ICameraActivityBridge getCameraActivityBridge(CameraActivity cameraActivity) {
        Log.m5d(TAG, "[getCameraActivityBridge]+ activity = " + cameraActivity);
        if (mCameraActivityBridgeMap.get(cameraActivity) != null) {
            return (ICameraActivityBridge) mCameraActivityBridgeMap.get(cameraActivity);
        }
        try {
            mCameraActivityBridgeMap.put(cameraActivity, Class.forName("com.android.camera.v2.CameraActivityBridge").getConstructor(CameraActivity.class).newInstance(cameraActivity));
            ICameraActivityBridge iCameraActivityBridge = (ICameraActivityBridge) mCameraActivityBridgeMap.get(cameraActivity);
            Log.m5d(TAG, "[getCameraActivityBridge]- return " + iCameraActivityBridge);
            return iCameraActivityBridge;
        } catch (Exception e) {
            e.printStackTrace();
            Log.m11w(TAG, "[getCameraActivityBridge]- return null");
            return null;
        }
    }

    public static synchronized void destroyCameraActivityBridge(CameraActivity cameraActivity) {
        Log.m8i(TAG, "destroyCameraActivityBridge map size: " + mCameraActivityBridgeMap.size());
        if (cameraActivity != null) {
            mCameraActivityBridgeMap.remove(cameraActivity);
        }
    }
}
