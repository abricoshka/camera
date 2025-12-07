package com.mediatek.camera.p005v2.detection.facedetection;

import android.app.Activity;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.hardware.camera2.CaptureRequest;
import android.view.ViewGroup;
import com.mediatek.camera.R;
import com.mediatek.camera.debug.LogHelper;
import com.mediatek.camera.p005v2.setting.ISettingServant;
import com.mediatek.camera.p005v2.util.Utils;
import com.mediatek.camera.p005v2.vendortag.TagRequest;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

/* loaded from: classes.dex */
public class FdViewManager implements ISettingServant.ISettingChangedListener {
    private static final LogHelper.Tag TAG = new LogHelper.Tag(FdViewManager.class.getSimpleName());
    private Activity mActivity;
    private FdView mFdView;
    private final ISettingServant mISettingServant;
    private ArrayList<String> mCaredSettingChangedKeys = new ArrayList<>();
    private boolean mMirror = false;
    private boolean mIsForceFace3aSupported = false;

    public FdViewManager(ISettingServant iSettingServant) {
        this.mISettingServant = iSettingServant;
    }

    public void open(Activity activity, ViewGroup viewGroup) {
        LogHelper.m26i(TAG, "open");
        this.mActivity = activity;
        activity.getLayoutInflater().inflate(R.layout.facedetection_view, viewGroup, true);
        this.mFdView = (FdView) viewGroup.findViewById(R.id.face_detection_view);
        this.mFdView.setVisibility(0);
        addCaredSettingChangedKeys("pref_camera_id_key");
        this.mISettingServant.registerSettingChangedListener(this, this.mCaredSettingChangedKeys, 1);
    }

    public void close() {
        this.mISettingServant.unRegisterSettingChangedListener(this);
        this.mIsForceFace3aSupported = false;
    }

    public void onOrientationChanged(int i) {
        this.mFdView.onOrientationChanged(i);
    }

    public void onPreviewAreaChanged(RectF rectF) {
        this.mFdView.onPreviewAreaChanged(rectF);
    }

    public boolean isForceFace3aSupported() {
        return this.mIsForceFace3aSupported;
    }

    public void initFaceView() {
        this.mFdView.setBlockDraw(false);
        updateFaceViewStatus();
    }

    public void showFaceView(final int[] iArr, final Rect[] rectArr, final byte[] bArr, final Point[][] pointArr, final Rect rect) {
        this.mActivity.runOnUiThread(new Runnable() { // from class: com.mediatek.camera.v2.detection.facedetection.FdViewManager.1
            @Override // java.lang.Runnable
            public void run() {
                FdViewManager.this.mFdView.setFaces(iArr, rectArr, bArr, pointArr, rect);
            }
        });
    }

    public void hideFaceView() {
        this.mFdView.setBlockDraw(true);
        this.mFdView.clear();
    }

    @Override // com.mediatek.camera.v2.setting.ISettingServant.ISettingChangedListener
    public void onSettingChanged(Map<String, String> map) {
        if ("on".equals(this.mISettingServant.getSettingValue("pref_face_detect_key")) && map.get("pref_camera_id_key") != null) {
            updateFaceViewStatus();
        }
    }

    private void updateFaceViewStatus() {
        this.mMirror = "1".equals(this.mISettingServant.getCameraId());
        this.mIsForceFace3aSupported = false;
        Iterator<T> it = Utils.getCameraCharacteristics(this.mActivity, this.mISettingServant.getCameraId()).getAvailableCaptureRequestKeys().iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            } else if (((CaptureRequest.Key) it.next()).getName().equals(TagRequest.STATISTICS_FORCE_FACE_3A.getName())) {
                this.mIsForceFace3aSupported = true;
                break;
            }
        }
        this.mFdView.clear();
        this.mFdView.setMirror(this.mMirror);
    }

    private void addCaredSettingChangedKeys(String str) {
        if (str != null && (!this.mCaredSettingChangedKeys.contains(str))) {
            this.mCaredSettingChangedKeys.add(str);
        }
    }
}
