package com.mediatek.camera.mode.stereocamera;

import android.app.Activity;
import android.graphics.Matrix;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import com.android.camera.CameraActivity;
import com.android.camera.Util;
import com.android.camera.p001ui.FocusIndicatorRotateLayout;
import com.android.camera.p001ui.RotateLayout;
import com.mediatek.camera.R;
import com.mediatek.camera.p004ui.CameraView;
import com.mediatek.camera.platform.ICameraAppUi;
import com.mediatek.camera.platform.IModuleCtrl;
import com.mediatek.camera.util.Log;

/* loaded from: classes.dex */
public class StereoView extends CameraView implements SeekBar.OnSeekBarChangeListener, CameraActivity.OnOrientationListener {
    private Activity mContext;
    private TextView mDofView;
    private RotateLayout mFocusAreaIndicator;
    private View mFocusIndicator;
    private FocusIndicatorRotateLayout mFocusIndicatorRotateLayout;
    private ICameraAppUi mICameraAppUi;
    private IModuleCtrl mIModuleCtrl;
    private String mLevel;
    private Listener mListener;
    private int mOrientation;
    private int mPreviewHeight;
    private int mPreviewWidth;
    private long mProcessTime;
    private SeekBar mStereoSeekBar;
    private View mView;
    private ViewHandler mViewHandler;
    private static int sProgress = 140;
    private static final String[] DOFDATA = {"F11", "F10", "F9.0", "F8.0", "F7.2", "F6.3", "F5.6", "F4.5", "F3.6", "F2.8", "F2.2", "F1.8", "F1.4", "F1.2", "F1.0", "F0.8"};

    public interface Listener {
        void onTouchPositionChanged(String str);

        void onVsDofLevelChanged(String str);
    }

    public StereoView(Activity activity) {
        super(activity);
        this.mLevel = "7";
        this.mContext = activity;
        this.mViewHandler = new ViewHandler(this.mContext.getMainLooper());
        ((CameraActivity) this.mContext).addOnOrientationListener(this);
    }

    @Override // com.mediatek.camera.p004ui.CameraView, com.mediatek.camera.platform.ICameraView
    public void init(Activity activity, ICameraAppUi iCameraAppUi, IModuleCtrl iModuleCtrl) {
        super.init(activity, iCameraAppUi, iModuleCtrl);
        this.mICameraAppUi = iCameraAppUi;
        this.mIModuleCtrl = iModuleCtrl;
        this.mOrientation = iModuleCtrl.getOrientationCompensation();
        this.mFocusAreaIndicator = (RotateLayout) activity.findViewById(R.id.focus_indicator_rotate_layout);
        this.mFocusIndicatorRotateLayout = (FocusIndicatorRotateLayout) this.mFocusAreaIndicator;
        this.mFocusIndicator = this.mFocusAreaIndicator.findViewById(R.id.focus_indicator);
    }

    @Override // com.mediatek.camera.p004ui.CameraView, com.mediatek.camera.platform.ICameraView
    public void uninit() {
        Log.m31d("StereoView", "[uninit]...");
        super.uninit();
        ((CameraActivity) this.mContext).removeOnOrientationListener(this);
    }

    @Override // com.mediatek.camera.p004ui.CameraView
    protected View getView() {
        Log.m31d("StereoView", "[getView]...");
        this.mView = inflate(R.layout.stereo_view);
        ((ImageView) this.mView.findViewById(R.id.small_aperture)).setVisibility(0);
        ((ImageView) this.mView.findViewById(R.id.big_aperture)).setVisibility(0);
        this.mDofView = (TextView) this.mView.findViewById(R.id.dof_view);
        this.mStereoSeekBar = (SeekBar) this.mView.findViewById(R.id.refocusSeekBar);
        this.mStereoSeekBar.setVisibility(0);
        this.mStereoSeekBar.setProgress(sProgress);
        this.mStereoSeekBar.setOnSeekBarChangeListener(this);
        return this.mView;
    }

    @Override // com.mediatek.camera.p004ui.CameraView
    protected void addView(View view) {
        Log.m31d("StereoView", "addView");
        this.mICameraAppUi.getNormalViewLayer().addView(view);
    }

    @Override // com.mediatek.camera.p004ui.CameraView, com.mediatek.camera.platform.ICameraView
    public void onOrientationChanged(int i) {
        if (this.mOrientation != i) {
            this.mOrientation = i;
            Util.setOrientation(this.mView, this.mOrientation, true);
        }
    }

    @Override // com.mediatek.camera.p004ui.CameraView, com.mediatek.camera.platform.ICameraView
    public void reset() {
        Log.m31d("StereoView", "[reset]...");
        this.mLevel = "7";
        sProgress = 140;
    }

    @Override // com.mediatek.camera.p004ui.CameraView, com.mediatek.camera.platform.ICameraView
    public void refresh() {
        Log.m31d("StereoView", "[refresh]...");
        this.mStereoSeekBar.setProgress(sProgress);
        this.mLevel = String.valueOf(sProgress / 20);
        this.mListener.onVsDofLevelChanged(this.mLevel);
    }

    @Override // com.mediatek.camera.p004ui.CameraView, com.mediatek.camera.platform.ICameraView
    public void show() {
        Log.m31d("StereoView", "[show]...");
        super.show();
        Util.setOrientation(this.mView, this.mOrientation, true);
        this.mLevel = String.valueOf(sProgress / 20);
        this.mListener.onVsDofLevelChanged(this.mLevel);
    }

    @Override // com.mediatek.camera.p004ui.CameraView, com.mediatek.camera.platform.ICameraView
    public boolean update(int i, Object... objArr) {
        Log.m34i("StereoView", "[update]...type = " + i + ", x = " + ((Integer) objArr[0]) + ", y = " + ((Integer) objArr[1]));
        onSingleTapUp(((Integer) objArr[0]).intValue(), ((Integer) objArr[1]).intValue());
        return true;
    }

    @Override // android.widget.SeekBar.OnSeekBarChangeListener
    public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
        long jCurrentTimeMillis = System.currentTimeMillis();
        int i2 = i / 20;
        this.mDofView.setText(DOFDATA[i2]);
        if (jCurrentTimeMillis - this.mProcessTime >= 50 && !String.valueOf(i2).equals(this.mLevel)) {
            Log.m31d("StereoView", "onProgressChanged level = " + this.mLevel);
            this.mLevel = String.valueOf(i / 20);
            this.mListener.onVsDofLevelChanged(this.mLevel);
        }
        this.mProcessTime = System.currentTimeMillis();
    }

    @Override // android.widget.SeekBar.OnSeekBarChangeListener
    public void onStartTrackingTouch(SeekBar seekBar) {
        Log.m31d("StereoView", "onStartTrackingTouch");
        this.mViewHandler.removeMessages(0);
        this.mViewHandler.sendEmptyMessage(1);
    }

    @Override // android.widget.SeekBar.OnSeekBarChangeListener
    public void onStopTrackingTouch(SeekBar seekBar) {
        sProgress = this.mStereoSeekBar.getProgress();
        this.mLevel = String.valueOf(this.mStereoSeekBar.getProgress() / 20);
        Log.m31d("StereoView", "onStopTrackingTouch level = " + this.mLevel);
        this.mListener.onVsDofLevelChanged(this.mLevel);
        this.mViewHandler.sendEmptyMessageDelayed(0, 1000L);
    }

    @Override // com.mediatek.camera.p004ui.CameraView, com.mediatek.camera.platform.ICameraView
    public void setListener(Object obj) {
        this.mListener = (Listener) obj;
    }

    private class ViewHandler extends Handler {
        public ViewHandler(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            Log.m31d("StereoView", "[handleMessage]msg.what= " + message.what);
            switch (message.what) {
                case 0:
                    StereoView.this.mDofView.setVisibility(8);
                    break;
                case 1:
                    StereoView.this.mDofView.setVisibility(0);
                    break;
                case 2:
                    StereoView.this.mFocusIndicatorRotateLayout.showFail(true);
                    break;
            }
        }
    }

    private void onSingleTapUp(int i, int i2) {
        this.mPreviewHeight = this.mICameraAppUi.getUnCropHeight();
        this.mPreviewWidth = this.mICameraAppUi.getUnCropWidth();
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) this.mFocusIndicatorRotateLayout.getLayoutParams();
        int[] rules = layoutParams.getRules();
        rules[13] = -1;
        if (this.mFocusIndicator != null) {
            int iMin = Math.min(this.mPreviewWidth, this.mPreviewHeight) / 4;
            ViewGroup.LayoutParams layoutParams2 = this.mFocusIndicator.getLayoutParams();
            layoutParams2.width = iMin;
            layoutParams2.height = iMin;
        }
        int width = this.mFocusIndicatorRotateLayout.getWidth();
        int height = this.mFocusIndicatorRotateLayout.getHeight();
        int iClamp = Util.clamp(i - (width / 2), 0, this.mPreviewWidth - width);
        int iClamp2 = Util.clamp(i2 - (height / 2), 0, this.mPreviewHeight - height);
        if (layoutParams.getLayoutDirection() != 1) {
            layoutParams.setMargins(iClamp, iClamp2, 0, 0);
        } else {
            layoutParams.setMargins(0, iClamp2, this.mPreviewWidth - (width + iClamp), 0);
        }
        rules[13] = 0;
        this.mFocusIndicatorRotateLayout.clear();
        this.mFocusIndicatorRotateLayout.showStart();
        this.mFocusIndicatorRotateLayout.requestLayout();
        this.mViewHandler.sendEmptyMessageDelayed(2, 500L);
        applyTouchPointParameter(i, i2);
    }

    private void applyTouchPointParameter(int i, int i2) {
        Matrix matrix = new Matrix();
        Matrix matrix2 = new Matrix();
        Util.prepareMatrix(matrix2, true, this.mIModuleCtrl.getDisplayOrientation(), this.mPreviewWidth, this.mPreviewHeight);
        matrix2.invert(matrix);
        float[] fArr = {i, i2};
        matrix.mapPoints(fArr);
        int[] iArrPointFToPoint = Util.pointFToPoint(fArr);
        String str = iArrPointFToPoint[0] + "," + iArrPointFToPoint[1];
        Log.m34i("StereoView", "nativePoint = " + str);
        this.mListener.onTouchPositionChanged(str);
    }
}
