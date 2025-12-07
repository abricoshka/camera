package com.mediatek.camera.mode.panorama;

import android.app.Activity;
import android.graphics.Matrix;
import android.view.View;
import android.view.ViewGroup;
import com.mediatek.camera.R;
import com.mediatek.camera.p004ui.CameraView;
import com.mediatek.camera.p004ui.ProgressIndicator;
import com.mediatek.camera.p004ui.Rotatable;
import com.mediatek.camera.p004ui.UIRotateLayout;
import com.mediatek.camera.platform.ICameraAppUi;
import com.mediatek.camera.platform.IModuleCtrl;
import com.mediatek.camera.util.Log;

/* loaded from: classes.dex */
public class PanoramaView extends CameraView {
    private static final int[] DIRECTIONS = {0, 3, 1, 2};
    private static final int DIRECTIONS_COUNT = DIRECTIONS.length;
    private AnimationController mAnimationController;
    private int[] mBlockSizes;
    private ViewGroup mCenterIndicator;
    private ViewGroup mCollimatedArrowsDrawable;
    private ViewGroup[] mDirectionSigns;
    private Matrix mDisplayMatrix;
    private int mDisplayOrientaion;
    private int mDistanceHorizontal;
    private int mDistanceVertical;
    private int mHalfArrowHeight;
    private int mHalfArrowLength;
    private int mHoldOrientation;
    private IModuleCtrl mIMoudleCtrl;
    private boolean mIsCapturing;
    private NaviLineImageView mNaviLine;
    private boolean mNeedInitialize;
    private UIRotateLayout.OnSizeChangedListener mOnSizeChangedListener;
    private View mPanoView;
    private int mPreviewHeight;
    private int mPreviewWidth;
    private ProgressIndicator mProgressIndicator;
    private View mRootView;
    private boolean mS3DMode;
    private UIRotateLayout mScreenProgressLayout;
    private int mSensorDirection;
    private Matrix[] mSensorMatrix;
    private int mViewCategory;

    public PanoramaView(Activity activity) {
        super(activity);
        this.mDirectionSigns = new ViewGroup[4];
        this.mS3DMode = false;
        this.mNeedInitialize = true;
        this.mIsCapturing = false;
        this.mDisplayMatrix = new Matrix();
        this.mSensorDirection = 4;
        this.mHalfArrowHeight = 0;
        this.mHalfArrowLength = 0;
        this.mPreviewWidth = 0;
        this.mPreviewHeight = 0;
        this.mHoldOrientation = -1;
        this.mBlockSizes = new int[]{17, 15, 13, 12, 11, 12, 13, 15, 17};
        this.mDistanceHorizontal = 0;
        this.mDistanceVertical = 0;
        this.mOnSizeChangedListener = new UIRotateLayout.OnSizeChangedListener() { // from class: com.mediatek.camera.mode.panorama.PanoramaView.1
            @Override // com.mediatek.camera.ui.UIRotateLayout.OnSizeChangedListener
            public void onSizeChanged(int i, int i2) {
                Log.m31d("PanoramaView", "[onSizeChanged]width=" + i + " height=" + i2);
                PanoramaView.this.mPreviewWidth = Math.max(i, i2);
                PanoramaView.this.mPreviewHeight = Math.min(i, i2);
            }
        };
        Log.m34i("PanoramaView", "[PanoramaView]constructor...");
        this.mViewCategory = 0;
    }

    @Override // com.mediatek.camera.p004ui.CameraView, com.mediatek.camera.platform.ICameraView
    public void init(Activity activity, ICameraAppUi iCameraAppUi, IModuleCtrl iModuleCtrl) {
        Log.m34i("PanoramaView", "[init]...");
        this.mIMoudleCtrl = iModuleCtrl;
        setOrientation(iModuleCtrl.getOrientationCompensation());
    }

    @Override // com.mediatek.camera.p004ui.CameraView, com.mediatek.camera.platform.ICameraView
    public void show() {
        Log.m34i("PanoramaView", "[show]mNeedInitialize=" + this.mNeedInitialize);
        super.show();
        this.mDisplayOrientaion = this.mIMoudleCtrl.getDisplayOrientation();
        if (this.mNeedInitialize) {
            initializeViewManager();
            this.mNeedInitialize = false;
        }
        showCaptureView();
    }

    @Override // com.mediatek.camera.p004ui.CameraView, com.mediatek.camera.platform.ICameraView
    public void uninit() {
        Log.m34i("PanoramaView", "[uninit]...");
        super.uninit();
        this.mNeedInitialize = true;
    }

    @Override // com.mediatek.camera.p004ui.CameraView, com.mediatek.camera.platform.ICameraView
    public void reset() {
        Log.m34i("PanoramaView", "[reset] mViewCategory = " + this.mViewCategory + ",mRootView = " + this.mRootView + ",mPanoView = " + this.mPanoView);
        if (this.mRootView == null) {
            return;
        }
        this.mPanoView.setVisibility(8);
        this.mAnimationController.stopCenterAnimation();
        this.mCenterIndicator.setVisibility(8);
        if (this.mViewCategory == 0) {
            this.mSensorDirection = 4;
            this.mNaviLine.setVisibility(8);
            this.mCollimatedArrowsDrawable.setVisibility(8);
            for (int i = 0; i < 4; i++) {
                this.mDirectionSigns[i].setSelected(false);
                this.mDirectionSigns[i].setVisibility(0);
            }
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    @Override // com.mediatek.camera.p004ui.CameraView, com.mediatek.camera.platform.ICameraView
    public boolean update(int i, Object... objArr) {
        Log.m34i("PanoramaView", "[update] type =" + i);
        switch (i) {
            case 0:
                setViewsForNext(Integer.parseInt(objArr[0].toString()));
                return true;
            case 1:
                if (objArr[0] != null && objArr[1] != null && objArr[2] != null) {
                    updateMovingUI(Integer.parseInt(objArr[0].toString()), Integer.parseInt(objArr[1].toString()), Boolean.parseBoolean(objArr[2].toString()));
                }
                return true;
            case 2:
                startCenterAnimation();
                return true;
            case 3:
                this.mIsCapturing = true;
                return true;
            case 4:
                this.mIsCapturing = false;
                return true;
            default:
                return true;
        }
    }

    @Override // com.mediatek.camera.p004ui.CameraView, com.mediatek.camera.platform.ICameraView
    public void onOrientationChanged(int i) {
        Log.m34i("PanoramaView", "[onOrientationChangedis]...mIsCapturing = " + this.mIsCapturing);
        if (!this.mIsCapturing) {
            super.onOrientationChanged(i);
            this.mHoldOrientation = -1;
            if (this.mS3DMode) {
                Log.m34i("PanoramaView", "[onOrientationChanged]orientation = " + i);
                return;
            } else {
                if (this.mProgressIndicator != null) {
                    this.mProgressIndicator.setOrientation(i);
                    return;
                }
                return;
            }
        }
        this.mHoldOrientation = i;
    }

    @Override // com.mediatek.camera.p004ui.CameraView
    protected View getView() {
        View viewInflate = inflate(R.layout.pano_preview);
        this.mRootView = viewInflate.findViewById(R.id.pano_frame_layout);
        return viewInflate;
    }

    private void initializeViewManager() {
        this.mPanoView = this.mRootView.findViewById(R.id.pano_view);
        this.mScreenProgressLayout = (UIRotateLayout) this.mRootView.findViewById(R.id.on_screen_progress);
        this.mCenterIndicator = (ViewGroup) this.mRootView.findViewById(R.id.center_indicator);
        this.mDirectionSigns[0] = (ViewGroup) this.mRootView.findViewById(R.id.pano_right);
        this.mDirectionSigns[1] = (ViewGroup) this.mRootView.findViewById(R.id.pano_left);
        this.mDirectionSigns[2] = (ViewGroup) this.mRootView.findViewById(R.id.pano_up);
        this.mDirectionSigns[3] = (ViewGroup) this.mRootView.findViewById(R.id.pano_down);
        this.mAnimationController = new AnimationController(this.mDirectionSigns, (ViewGroup) this.mCenterIndicator.getChildAt(0));
        this.mDistanceHorizontal = this.mS3DMode ? 32 : 160;
        this.mDistanceVertical = this.mS3DMode ? 240 : 120;
        if (this.mViewCategory == 0) {
            this.mNaviLine = (NaviLineImageView) this.mRootView.findViewById(R.id.navi_line);
            this.mCollimatedArrowsDrawable = (ViewGroup) this.mRootView.findViewById(R.id.static_center_indicator);
            this.mProgressIndicator = new ProgressIndicator(getContext(), 9, this.mBlockSizes);
            this.mProgressIndicator.setVisibility(8);
            this.mScreenProgressLayout.setOrientation(getOrientation(), true);
            this.mProgressIndicator.setOrientation(getOrientation());
            prepareSensorMatrix();
        }
        this.mScreenProgressLayout.setOnSizeChangedListener(this.mOnSizeChangedListener);
    }

    private void prepareSensorMatrix() {
        this.mSensorMatrix = new Matrix[4];
        this.mSensorMatrix[1] = new Matrix();
        this.mSensorMatrix[1].setScale(-1.0f, -1.0f);
        this.mSensorMatrix[1].postTranslate(0.0f, this.mDistanceVertical);
        this.mSensorMatrix[0] = new Matrix();
        this.mSensorMatrix[0].setScale(-1.0f, -1.0f);
        this.mSensorMatrix[0].postTranslate(this.mDistanceHorizontal * 2, this.mDistanceVertical);
        this.mSensorMatrix[2] = new Matrix();
        this.mSensorMatrix[2].setScale(-1.0f, -1.0f);
        this.mSensorMatrix[2].postTranslate(this.mDistanceHorizontal, 0.0f);
        this.mSensorMatrix[3] = new Matrix();
        this.mSensorMatrix[3].setScale(-1.0f, -1.0f);
        this.mSensorMatrix[3].postTranslate(this.mDistanceHorizontal, this.mDistanceVertical * 2);
    }

    private void showCaptureView() {
        if (this.mHoldOrientation != -1) {
            onOrientationChanged(this.mHoldOrientation);
        }
        if (this.mS3DMode) {
            for (int i = 0; i < 4; i++) {
                this.mDirectionSigns[i].setVisibility(4);
            }
            this.mCenterIndicator.setVisibility(0);
            this.mAnimationController.startCenterAnimation();
        } else {
            this.mCenterIndicator.setVisibility(8);
        }
        this.mPanoView.setVisibility(0);
        this.mProgressIndicator.setProgress(0);
        this.mProgressIndicator.setVisibility(0);
    }

    private void setViewsForNext(int i) {
        if (!filterViewCategory(0)) {
            return;
        }
        this.mProgressIndicator.setProgress(i + 1);
        if (i == 0) {
            if (!this.mS3DMode) {
                this.mAnimationController.startDirectionAnimation();
                return;
            } else {
                this.mNaviLine.setVisibility(0);
                return;
            }
        }
        this.mNaviLine.setVisibility(4);
        this.mAnimationController.stopCenterAnimation();
        this.mCenterIndicator.setVisibility(8);
        this.mCollimatedArrowsDrawable.setVisibility(0);
    }

    private boolean filterViewCategory(int i) {
        if (this.mViewCategory != i) {
            return false;
        }
        return true;
    }

    private void updateMovingUI(int i, int i2, boolean z) {
        Log.m31d("PanoramaView", "[updateMovingUI]xy:" + i + ",direction:" + i2 + ",shown:" + z);
        if (!filterViewCategory(0)) {
            return;
        }
        if (i2 == 4 || z || this.mNaviLine.getWidth() == 0 || this.mNaviLine.getHeight() == 0) {
            this.mNaviLine.setVisibility(4);
        } else {
            updateUIShowingMatrix((short) (((-65536) & i) >> 16), (short) (65535 & i), i2);
        }
    }

    private void updateUIShowingMatrix(int i, int i2, int i3) {
        float[] fArr = {i, i2};
        this.mSensorMatrix[i3].mapPoints(fArr);
        Log.m35v("PanoramaView", "[updateUIShowingMatrix]Matrix x = " + fArr[0] + " y = " + fArr[1]);
        prepareTransformMatrix(i3);
        this.mDisplayMatrix.mapPoints(fArr);
        Log.m35v("PanoramaView", "[updateUIShowingMatrix]DisplayMatrix x = " + fArr[0] + " y = " + fArr[1]);
        int i4 = (int) fArr[0];
        int i5 = (int) fArr[1];
        this.mNaviLine.setLayoutPosition(i4 - this.mHalfArrowHeight, i5 - this.mHalfArrowLength, i4 + this.mHalfArrowHeight, i5 + this.mHalfArrowLength);
        updateDirection(i3);
        this.mNaviLine.setVisibility(0);
    }

    private void prepareTransformMatrix(int i) {
        this.mDisplayMatrix.reset();
        int i2 = this.mPreviewWidth >> 1;
        int i3 = this.mPreviewHeight >> 1;
        getArrowHL();
        float f = this.mS3DMode ? 260.0f : i2 - this.mHalfArrowLength;
        float f2 = i3 - this.mHalfArrowLength;
        this.mDisplayMatrix.postScale(f / this.mDistanceHorizontal, f2 / this.mDistanceVertical);
        switch (this.mDisplayOrientaion) {
            case 90:
                this.mDisplayMatrix.postTranslate(0.0f, (-f2) * 2.0f);
                this.mDisplayMatrix.postRotate(90.0f);
                break;
            case 180:
                this.mDisplayMatrix.postTranslate((float) ((this.mS3DMode ? 2.67d : 2.0d) * (-f)), (-f2) * 2.0f);
                this.mDisplayMatrix.postRotate(180.0f);
                break;
            case 270:
                this.mDisplayMatrix.postTranslate((-f) * 2.0f, 0.0f);
                this.mDisplayMatrix.postRotate(-90.0f);
                break;
        }
        this.mDisplayMatrix.postTranslate(this.mHalfArrowLength, this.mHalfArrowLength);
    }

    private void getArrowHL() {
        if (this.mHalfArrowHeight == 0) {
            int width = this.mNaviLine.getWidth();
            int height = this.mNaviLine.getHeight();
            if (width > height) {
                this.mHalfArrowLength = width >> 1;
                this.mHalfArrowHeight = height >> 1;
            } else {
                this.mHalfArrowHeight = width >> 1;
                this.mHalfArrowLength = height >> 1;
            }
        }
    }

    private void updateDirection(int i) {
        Log.m31d("PanoramaView", "[updateDirection]mDisplayOrientaion:" + this.mDisplayOrientaion + ",mSensorDirection =" + this.mSensorDirection);
        int i2 = 0;
        while (true) {
            if (i2 >= DIRECTIONS_COUNT) {
                i2 = 0;
                break;
            } else if (DIRECTIONS[i2] == i) {
                break;
            } else {
                i2++;
            }
        }
        switch (this.mDisplayOrientaion) {
            case 90:
                i = DIRECTIONS[(i2 + 1) % DIRECTIONS_COUNT];
                break;
            case 180:
                i = DIRECTIONS[(i2 + 2) % DIRECTIONS_COUNT];
                break;
            case 270:
                i = DIRECTIONS[((i2 - 1) + DIRECTIONS_COUNT) % DIRECTIONS_COUNT];
                break;
        }
        if (this.mSensorDirection != i) {
            this.mSensorDirection = i;
            if (this.mSensorDirection != 4) {
                setOrientationIndicator(i);
                this.mCenterIndicator.setVisibility(0);
                this.mAnimationController.startCenterAnimation();
                for (int i3 = 0; i3 < 4; i3++) {
                    this.mDirectionSigns[i3].setVisibility(4);
                }
                return;
            }
            this.mCenterIndicator.setVisibility(4);
        }
    }

    private void setOrientationIndicator(int i) {
        Log.m31d("PanoramaView", "[setOrientationIndicator]direction = " + i);
        if (i == 0) {
            ((Rotatable) this.mCollimatedArrowsDrawable).setOrientation(0, true);
            ((Rotatable) this.mCenterIndicator).setOrientation(0, true);
            this.mNaviLine.setRotation(-90.0f);
            return;
        }
        if (i == 1) {
            ((Rotatable) this.mCollimatedArrowsDrawable).setOrientation(180, true);
            ((Rotatable) this.mCenterIndicator).setOrientation(180, true);
            this.mNaviLine.setRotation(90.0f);
        } else if (i == 2) {
            ((Rotatable) this.mCollimatedArrowsDrawable).setOrientation(90, true);
            ((Rotatable) this.mCenterIndicator).setOrientation(90, true);
            this.mNaviLine.setRotation(180.0f);
        } else if (i == 3) {
            ((Rotatable) this.mCollimatedArrowsDrawable).setOrientation(270, true);
            ((Rotatable) this.mCenterIndicator).setOrientation(270, true);
            this.mNaviLine.setRotation(0.0f);
        }
    }

    private void startCenterAnimation() {
        this.mCollimatedArrowsDrawable.setVisibility(8);
        this.mAnimationController.startCenterAnimation();
        this.mCenterIndicator.setVisibility(0);
    }
}
