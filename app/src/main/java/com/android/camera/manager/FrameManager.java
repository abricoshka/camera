package com.android.camera.manager;

import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.view.View;
import com.android.camera.CameraActivity;
import com.android.camera.CameraHolder;
import com.android.camera.p001ui.FaceView;
import com.android.camera.p001ui.FrameView;
import com.android.camera.p001ui.ObjectView;
import com.mediatek.camera.R;

/* loaded from: classes.dex */
public class FrameManager extends ViewManager implements CameraActivity.OnOrientationListener {
    private static final int[] FACE_DETECTION_ICON = {R.drawable.ic_face_detection_focusing, R.drawable.ic_face_detection_focused, R.drawable.ic_face_detection_failed, R.drawable.ic_facebeautify_frame};
    private static final int[] OBJECT_TRACKING_ICON = {R.drawable.ic_object_tracking, R.drawable.ic_object_tracking_succeed, R.drawable.ic_object_tracking_failed};
    private CameraActivity mContext;
    private boolean mEnableFaceBeauty;
    private Drawable[] mFaceStatusIndicator;
    private FrameView mFrameView;
    private Drawable[] mTrackStatusIndicator;

    public FrameManager(CameraActivity cameraActivity) {
        super(cameraActivity);
        this.mFaceStatusIndicator = new Drawable[4];
        this.mTrackStatusIndicator = new Drawable[3];
        this.mFrameView = null;
        cameraActivity.addOnOrientationListener(this);
        this.mContext = cameraActivity;
    }

    @Override // com.android.camera.manager.ViewManager
    protected View getView() {
        return null;
    }

    public Drawable[] getViewDrawable(int i) {
        int i2 = 0;
        if (i == 0) {
            while (i2 < 3) {
                this.mTrackStatusIndicator[i2] = this.mContext.getResources().getDrawable(OBJECT_TRACKING_ICON[i2]);
                i2++;
            }
            return this.mTrackStatusIndicator;
        }
        while (i2 < 4) {
            this.mFaceStatusIndicator[i2] = this.mContext.getResources().getDrawable(FACE_DETECTION_ICON[i2]);
            i2++;
        }
        return this.mFaceStatusIndicator;
    }

    public void initializeFrameView(boolean z) {
        Camera.CameraInfo cameraInfo = CameraHolder.instance().getCameraInfo()[this.mContext.getCameraId()];
        if (z) {
            this.mFrameView = (ObjectView) this.mContext.findViewById(R.id.object_view);
        } else {
            this.mFrameView = (FaceView) this.mContext.findViewById(R.id.face_view);
            this.mFrameView.setMirror(cameraInfo.facing == 1);
        }
        this.mFrameView.clear();
        this.mFrameView.setVisibility(0);
        this.mFrameView.setDisplayOrientation(this.mContext.getDisplayOrientation());
        this.mFrameView.resume();
        setView(this.mFrameView);
    }

    public void setView(FrameView frameView) {
        this.mFrameView = frameView;
        enableFaceBeauty(this.mEnableFaceBeauty);
    }

    public FrameView getFrameView() {
        return this.mFrameView;
    }

    public void enableFaceBeauty(boolean z) {
        this.mEnableFaceBeauty = z;
        if (this.mFrameView != null) {
            this.mFrameView.enableFaceBeauty(z);
        }
    }

    @Override // com.android.camera.manager.ViewManager, com.android.camera.CameraActivity.OnOrientationListener
    public void onOrientationChanged(int i) {
        if (this.mFrameView != null) {
            this.mFrameView.setOrientation(i);
        }
    }
}
