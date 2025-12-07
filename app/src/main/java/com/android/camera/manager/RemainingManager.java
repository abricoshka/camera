package com.android.camera.manager;

import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.TextView;
import com.android.camera.CameraActivity;
import com.android.camera.Log;
import com.android.camera.Storage;
import com.mediatek.camera.R;
import java.util.Locale;

/* loaded from: classes.dex */
public class RemainingManager extends ViewManager implements CameraActivity.Resumable, CameraActivity.OnParametersReadyListener {
    private Long mAvaliableSpace;
    private CameraActivity mContext;
    private String mDngState;
    private Handler mMainHandler;
    private boolean mParametersReady;
    private CamcorderProfile mProfile;
    private String mRemainingText;
    private TextView mRemainingView;
    private boolean mResumed;
    private OnScreenHint mStorageHint;
    private int mType;
    private WorkerHandler mWorkerHandler;
    private static final Long REMAIND_THRESHOLD = 100L;
    private static final int[] MATRIX_REMAINING_TYPE = new int[11];

    static {
        MATRIX_REMAINING_TYPE[0] = 0;
        MATRIX_REMAINING_TYPE[1] = 0;
        MATRIX_REMAINING_TYPE[2] = 0;
        MATRIX_REMAINING_TYPE[3] = 0;
        MATRIX_REMAINING_TYPE[4] = 0;
        MATRIX_REMAINING_TYPE[5] = 0;
        MATRIX_REMAINING_TYPE[8] = 1;
        MATRIX_REMAINING_TYPE[9] = 1;
        MATRIX_REMAINING_TYPE[10] = 1;
    }

    private class WorkerHandler extends Handler {
        public WorkerHandler(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            switch (message.what) {
                case 0:
                    RemainingManager.this.mAvaliableSpace = Long.valueOf(Storage.getAvailableSpace());
                    removeMessages(0);
                    sendEmptyMessageDelayed(0, 1500L);
                    break;
            }
        }
    }

    public RemainingManager(CameraActivity cameraActivity) {
        super(cameraActivity);
        this.mType = 0;
        this.mDngState = "off";
        this.mMainHandler = new Handler();
        this.mContext = cameraActivity;
        cameraActivity.addResumable(this);
        cameraActivity.addOnParametersReadyListener(this);
        setAnimationEnabled(false, false);
    }

    @Override // com.android.camera.CameraActivity.Resumable
    public void begin() {
        if (this.mWorkerHandler == null) {
            HandlerThread handlerThread = new HandlerThread("thumbnail-creation-thread");
            handlerThread.start();
            this.mWorkerHandler = new WorkerHandler(handlerThread.getLooper());
            this.mWorkerHandler.sendEmptyMessage(0);
        }
    }

    @Override // com.android.camera.CameraActivity.Resumable
    public void resume() {
        this.mResumed = true;
        this.mAvaliableSpace = Long.valueOf(Storage.getAvailableSpace());
        showHint();
    }

    @Override // com.android.camera.CameraActivity.Resumable
    public void pause() {
        this.mResumed = false;
        if (this.mStorageHint != null) {
            this.mStorageHint.cancel();
            this.mStorageHint = null;
        }
    }

    @Override // com.android.camera.CameraActivity.Resumable
    public void finish() {
        if (this.mWorkerHandler != null) {
            this.mWorkerHandler.getLooper().quit();
        }
    }

    @Override // com.android.camera.manager.ViewManager
    protected View getView() {
        View viewInflate = inflate(R.layout.remaining);
        this.mRemainingView = (TextView) viewInflate.findViewById(R.id.remaining_view);
        return viewInflate;
    }

    @Override // com.android.camera.manager.ViewManager
    protected void onRefresh() {
        super.onRefresh();
        if (this.mRemainingView != null) {
            this.mRemainingView.setText(this.mRemainingText);
        }
    }

    public boolean showIfNeed() {
        if (this.mParametersReady) {
            if (this.mAvaliableSpace == null || this.mAvaliableSpace.longValue() <= 0) {
                this.mAvaliableSpace = Long.valueOf(Storage.getAvailableSpace());
            }
            long jLongValue = this.mAvaliableSpace.longValue();
            this.mType = MATRIX_REMAINING_TYPE[getContext().getCurrentMode() % 100];
            long jComputeStorage = computeStorage(jLongValue);
            updateStorageHint(jComputeStorage);
            updateRemainingView(REMAIND_THRESHOLD.longValue(), jComputeStorage);
        }
        return isShowing();
    }

    @Override // com.android.camera.manager.ViewManager
    public void show() {
        if (!isShowing()) {
            showAways();
        }
    }

    public boolean showAways() {
        if (this.mParametersReady) {
            this.mAvaliableSpace = Long.valueOf(Storage.getAvailableSpace());
            long jLongValue = this.mAvaliableSpace.longValue();
            this.mType = MATRIX_REMAINING_TYPE[getContext().getCurrentMode() % 100];
            long jComputeStorage = computeStorage(jLongValue);
            updateStorageHint(jComputeStorage);
            updateRemainingView(Long.MAX_VALUE, jComputeStorage);
        }
        return isShowing();
    }

    public void clearAvaliableSpace() {
        this.mAvaliableSpace = null;
    }

    public void showHint() {
        boolean zIsCameraOpened = this.mContext.isCameraOpened();
        if (this.mParametersReady && zIsCameraOpened) {
            this.mMainHandler.post(new Runnable() { // from class: com.android.camera.manager.RemainingManager.1
                @Override // java.lang.Runnable
                public void run() {
                    if (RemainingManager.this.mAvaliableSpace == null || RemainingManager.this.mAvaliableSpace.longValue() <= 0) {
                        RemainingManager.this.mAvaliableSpace = Long.valueOf(Storage.getAvailableSpace());
                    }
                    RemainingManager.this.updateStorageHint(RemainingManager.this.computeStorage(RemainingManager.this.mAvaliableSpace.longValue()));
                }
            });
        }
    }

    public long updateStorage() {
        if (this.mAvaliableSpace == null || this.mAvaliableSpace.longValue() <= 0) {
            this.mAvaliableSpace = Long.valueOf(Storage.getAvailableSpace());
        }
        return computeStorage(this.mAvaliableSpace.longValue());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public long computeStorage(long j) {
        long jPictureSize;
        if ("on".equals(this.mDngState)) {
            jPictureSize = pictureSize() + 30000000;
        } else {
            jPictureSize = pictureSize();
        }
        if (j > Storage.LOW_STORAGE_THRESHOLD) {
            long j2 = j - Storage.LOW_STORAGE_THRESHOLD;
            if (this.mType != 0) {
                jPictureSize = videoFrameRate();
            }
            j = j2 / jPictureSize;
        } else if (j > 0) {
            j = 0;
        }
        Storage.setLeftSpace(j);
        return j;
    }

    private void updateRemainingView(long j, long j2) {
        if (this.mType != 0 || j2 <= j) {
            if (j2 < 0) {
                this.mRemainingText = this.mType == 0 ? stringForCount(0L) : stringForTime(0L);
            } else {
                this.mRemainingText = this.mType == 0 ? stringForCount(j2) : stringForTime(j2);
            }
            this.mShowing = true;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateStorageHint(long j) {
        String string;
        Log.m5d("RemainingManager", "updateStorageHint(" + j + ") isFullScreen=" + getContext().isFullScreen());
        if (j == -1 || j == -2 || j == -3) {
            string = getContext().getString(R.string.can_not_use_storage);
        } else {
            string = j <= 0 ? getContext().getString(R.string.storage_full) : null;
        }
        if (string != null && getContext().isFullScreen()) {
            if (this.mStorageHint == null) {
                this.mStorageHint = OnScreenHint.makeText(getContext(), string);
            } else {
                this.mStorageHint.setText(string);
            }
            this.mStorageHint.show();
            return;
        }
        if (this.mStorageHint != null) {
            this.mStorageHint.cancel();
            this.mStorageHint = null;
        }
    }

    public long pictureSize() {
        Camera.Size pictureSize;
        int i = 2785280;
        int i2 = 0;
        switch (getContext().getCameraActor().getMode()) {
            case 3:
                i2 = 1;
                i = 0;
                break;
            default:
                if (getContext().getParameters() != null && (pictureSize = getContext().getParameters().getPictureSize()) != null) {
                    i = pictureSize.width * pictureSize.height;
                    break;
                }
                break;
        }
        return Storage.getBytePerImage(i2, i);
    }

    public void setCamcorderProfile(CamcorderProfile camcorderProfile) {
        this.mProfile = camcorderProfile;
    }

    public void setDngState(String str) {
        this.mDngState = str;
    }

    public long videoFrameRate() {
        return ((this.mProfile.videoBitRate + this.mProfile.audioBitRate) >> 3) / 1000;
    }

    private static String stringForTime(long j) {
        long j2 = j / 1000;
        long j3 = j2 % 60;
        long j4 = (j2 / 60) % 60;
        long j5 = j2 / 3600;
        return j5 > 0 ? String.format(Locale.ENGLISH, "%d:%02d:%02d", Long.valueOf(j5), Long.valueOf(j4), Long.valueOf(j3)) : String.format(Locale.ENGLISH, "%02d:%02d", Long.valueOf(j4), Long.valueOf(j3));
    }

    private static String stringForCount(long j) {
        return String.format("%d", Long.valueOf(j));
    }

    @Override // com.android.camera.CameraActivity.OnParametersReadyListener
    public void onCameraParameterReady() {
        this.mParametersReady = true;
    }
}
