package com.android.camera.manager;

import android.content.ContentResolver;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.media.CameraProfile;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.SystemProperties;
import android.view.View;
import android.widget.FrameLayout;
import com.android.camera.CameraActivity;
import com.android.camera.FileSaver;
import com.android.camera.Log;
import com.android.camera.SaveRequest;
import com.android.camera.Storage;
import com.android.camera.Thumbnail;
import com.android.camera.p001ui.RotateImageView;
import com.mediatek.camera.R;
import com.mediatek.camera.util.CameraAnimation;
import java.io.ByteArrayOutputStream;

/* loaded from: classes.dex */
public class ThumbnailViewManager extends ViewManager implements View.OnClickListener, FileSaver.FileSaverListener, CameraActivity.Resumable {
    private CameraActivity mActivity;
    private CameraAnimation mCameraAnimation;
    private CameraActivity mContext;
    private SaveRequest mCurrentSaveRequest;
    private boolean mIsSavingThumbnail;
    private boolean mIsUpdatingThumbnail;
    private boolean mIsVideoRecording;
    private long mLastRefreshTime;
    public AnimationEndListener mListener;
    private AsyncTask<Void, Void, Thumbnail> mLoadThumbnailTask;
    private Object mLock;
    private Handler mMainHandler;
    RotateImageView mPhotoIcon;
    public RotateImageView mPreviewThumb;
    private long mRefreshInterval;
    ShutterManager mShutterManager;
    private Thumbnail mThumbnail;
    private RotateImageView mThumbnailView;
    private View mView;
    private WorkerHandler mWorkerHandler;
    private int mYuvCount;
    private byte[] mYuvData;
    private int mYuvHeight;
    private int mYuvImageFormat;
    private int mYuvOrientation;
    private int mYuvWidth;

    public interface AnimationEndListener {
        void onAnianmationEnd();
    }

    public ThumbnailViewManager(CameraActivity cameraActivity) {
        super(cameraActivity);
        this.mLock = new Object();
        this.mRefreshInterval = 0L;
        this.mListener = new AnimationEndListener() { // from class: com.android.camera.manager.ThumbnailViewManager.1
            @Override // com.android.camera.manager.ThumbnailViewManager.AnimationEndListener
            public void onAnianmationEnd() {
                ThumbnailViewManager.this.mIsUpdatingThumbnail = false;
                ThumbnailViewManager.this.updateThumbnailView();
            }
        };
        this.mMainHandler = new Handler() { // from class: com.android.camera.manager.ThumbnailViewManager.2
            @Override // android.os.Handler
            public void handleMessage(Message message) {
                if (ThumbnailViewManager.this.mThumbnail == null) {
                    Log.m11w("ThumbnailViewManager", "[handleMessage]mMainHandler,mThumbnail is null,return!");
                }
                switch (message.what) {
                    case 1:
                        Log.m5d("ThumbnailViewManager", "[CMCC Performance test][Camera][Camera] camera capture end [" + System.currentTimeMillis() + "]");
                        android.util.Log.v("xiaoyao", "xxxxxxxxxxxxxxxxxxx a");
                        ThumbnailViewManager.this.mPreviewThumb.setBitmap(null);
                        ThumbnailViewManager.this.mPreviewThumb.setBitmap(ThumbnailViewManager.this.mThumbnail.getBitmap());
                        if (ThumbnailViewManager.this.mPhotoIcon != null) {
                            ThumbnailViewManager.this.mPhotoIcon.setBitmap(null);
                            ThumbnailViewManager.this.mPhotoIcon.setBitmap(ThumbnailViewManager.this.mThumbnail.getBitmap());
                        }
                        ThumbnailViewManager.this.mCameraAnimation.doCaptureAnimation(ThumbnailViewManager.this.mPreviewThumb, ThumbnailViewManager.this.getContext(), ThumbnailViewManager.this.mListener);
                        break;
                }
            }
        };
        this.mActivity = cameraActivity;
        this.mContext = cameraActivity;
        setFileter(false);
        cameraActivity.addResumable(this);
        this.mCameraAnimation = new CameraAnimation();
    }

    @Override // com.android.camera.CameraActivity.Resumable
    public void begin() {
        if (this.mWorkerHandler == null) {
            HandlerThread handlerThread = new HandlerThread("thumbnail-creation-thread");
            handlerThread.start();
            this.mWorkerHandler = new WorkerHandler(handlerThread.getLooper());
        }
    }

    @Override // com.android.camera.CameraActivity.Resumable
    public void resume() {
    }

    @Override // com.android.camera.CameraActivity.Resumable
    public void pause() {
        this.mWorkerHandler.sendEmptyMessage(3);
    }

    @Override // com.android.camera.manager.ViewManager
    public void setEnabled(boolean z) {
        super.setEnabled(z);
        if (this.mThumbnailView != null) {
            this.mThumbnailView.setEnabled(z);
            this.mThumbnailView.setClickable(z);
        }
    }

    @Override // com.android.camera.CameraActivity.Resumable
    public void finish() {
        if (this.mWorkerHandler != null) {
            this.mWorkerHandler.getLooper().quit();
        }
    }

    public void setShutterManager(ShutterManager shutterManager) {
        this.mShutterManager = shutterManager;
    }

    @Override // com.android.camera.manager.ViewManager
    protected View getView() {
        this.mView = inflate(R.layout.tw_thumbnail);
        this.mThumbnailView = (RotateImageView) this.mView.findViewById(R.id.thumbnail);
        this.mThumbnailView.setOnClickListener(this);
        this.mPreviewThumb = (RotateImageView) this.mView.findViewById(R.id.preview_thumb);
        return this.mView;
    }

    @Override // com.android.camera.manager.ViewManager
    protected void onRefresh() {
        FrameLayout frameLayout = (FrameLayout) this.mView.getParent().getParent().getParent().getParent();
        android.util.Log.v("xiaoyao", "zzzzzzzzzzzzzzzz==" + frameLayout);
        this.mPhotoIcon = (RotateImageView) frameLayout.findViewById(R.id.iv_entry_photo);
        this.mPhotoIcon.setOnClickListener(this);
        updateThumbnailView();
        if (!this.mContext.isNonePickIntent()) {
            this.mThumbnailView.setVisibility(8);
        }
    }

    public void updateVideoRecordingBackground(boolean z) {
        this.mIsVideoRecording = z;
        if (this.mThumbnailView != null) {
            if (this.mIsVideoRecording) {
                this.mThumbnailView.setVisibility(8);
            } else {
                this.mThumbnailView.setVisibility(8);
            }
        }
    }

    @Override // com.android.camera.FileSaver.FileSaverListener
    public void onFileSaved(SaveRequest saveRequest) {
        Log.m5d("ThumbnailViewManager", "[onFileSaved]... mYuvCount = " + this.mYuvCount);
        if (saveRequest.isIgnoreThumbnail()) {
            return;
        }
        this.mCurrentSaveRequest = saveRequest;
        if (this.mYuvCount == 0 && saveRequest.getUri() != null) {
            Log.m5d("ThumbnailViewManager", "[onFileSaved],send MSG_SAVE_THUMBNAIL.");
            cancelLoadThumbnail();
            this.mWorkerHandler.removeMessages(0);
            this.mWorkerHandler.sendEmptyMessage(0);
        }
        if (this.mYuvCount > 0) {
            this.mYuvCount--;
        }
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        if (getContext().isCameraIdle() && this.mThumbnail != null && getThumbnailUri() != null) {
            Log.m5d("ThumbnailViewManager", "[onClick]call gotoGallery.");
            getContext().gotoGallery();
        }
    }

    public void forceUpdate() {
        Log.m5d("ThumbnailViewManager", "[forceUpdate]...");
        getLastThumbnailUncached();
    }

    public Uri getThumbnailUri() {
        Uri uri = this.mThumbnail.getUri();
        if (uri == null && this.mCurrentSaveRequest != null && this.mCurrentSaveRequest.getUri() != null) {
            uri = this.mCurrentSaveRequest.getUri();
        }
        Log.m5d("ThumbnailViewManager", "getThumbnailUri = " + uri);
        return uri;
    }

    public String getThumbnailMimeType() {
        String type = this.mActivity.getContentResolver().getType(getThumbnailUri());
        Log.m8i("ThumbnailViewManager", "getThumbnailMimeType mimeType = " + type);
        return type;
    }

    public void updateThumbnailViewWithYuv(byte[] bArr, int i, int i2, int i3, int i4) throws Throwable {
        if (isNeedDumpYuv()) {
            dumpYuv("/sdcard/postView.yuv", bArr);
        }
        this.mYuvCount++;
        Log.m5d("ThumbnailViewManager", "[updateThumbnailViewWithYuv] yuvData = " + bArr + ", yuvWidth = " + i + ", yuvHeight = " + i2 + ", orientation = " + i3 + ", imageFormat = " + i4 + ", mYuvCount = " + this.mYuvCount);
        this.mYuvData = bArr;
        this.mYuvWidth = i;
        this.mYuvHeight = i2;
        this.mYuvOrientation = i3;
        this.mYuvImageFormat = i4;
        cancelLoadThumbnail();
        this.mWorkerHandler.removeMessages(2);
        this.mWorkerHandler.sendEmptyMessage(2);
    }

    public void addFileSaver(FileSaver fileSaver) {
        if (fileSaver != null) {
            fileSaver.addListener(this);
        }
    }

    public void setRefreshInterval(int i) {
        this.mRefreshInterval = i;
        this.mLastRefreshTime = System.currentTimeMillis();
    }

    public void updateThumbnailView() {
        if (this.mThumbnailView != null && (!this.mIsUpdatingThumbnail)) {
            if (super.isShowing()) {
                Log.m5d("ThumbnailViewManager", "[updateThumbnailView]showing is true");
                if (this.mThumbnail != null && this.mThumbnail.getBitmap() != null) {
                    Log.m5d("ThumbnailViewManager", "[updateThumbnailView]showing is true,set VISIBLE.");
                    this.mThumbnailView.setBitmap(null);
                    this.mThumbnailView.setBitmap(this.mThumbnail.getBitmap());
                    this.mThumbnailView.setBackground(null);
                    android.util.Log.v("xiaoyao", "setMyPhotoIcon===dddd" + this.mPhotoIcon);
                    if (this.mPhotoIcon != null) {
                        this.mPhotoIcon.setBitmap(null);
                        this.mPhotoIcon.setBitmap(this.mThumbnail.getBitmap());
                        this.mPhotoIcon.setBackground(null);
                    }
                    if (this.mIsVideoRecording || (!this.mContext.isNonePickIntent())) {
                        this.mThumbnailView.setVisibility(8);
                        return;
                    } else {
                        this.mThumbnailView.setVisibility(8);
                        return;
                    }
                }
                Log.m5d("ThumbnailViewManager", "[updateThumbnailView]thumbnail is null,set INVISIBLE!");
                this.mThumbnailView.setVisibility(4);
                this.mThumbnailView.setBackground(null);
                return;
            }
            Log.m5d("ThumbnailViewManager", "[updateThumbnailView]showing is false,set INVISIBLE.");
            this.mThumbnailView.setVisibility(4);
            this.mThumbnailView.setBackground(null);
        }
    }

    private class LoadThumbnailTask extends AsyncTask<Void, Void, Thumbnail> {
        public LoadThumbnailTask() {
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public Thumbnail doInBackground(Void... voidArr) throws Throwable {
            try {
                ContentResolver contentResolver = ThumbnailViewManager.this.getContext().getContentResolver();
                if (isCancelled()) {
                    Log.m11w("ThumbnailViewManager", "[doInBackground]task is cancel,return.");
                    return null;
                }
                if (!Storage.isStorageReady()) {
                    return null;
                }
                Thumbnail[] thumbnailArr = new Thumbnail[1];
                int lastThumbnailFromContentResolver = Thumbnail.getLastThumbnailFromContentResolver(contentResolver, thumbnailArr, ThumbnailViewManager.this.mThumbnail);
                Log.m5d("ThumbnailViewManager", "getLastThumbnailFromContentResolver code = " + lastThumbnailFromContentResolver);
                switch (lastThumbnailFromContentResolver) {
                    case 2:
                        if (!ThumbnailViewManager.this.getContext().isSecureCamera() || ThumbnailViewManager.this.getContext().getSecureAlbumCount() > 0) {
                            cancel(true);
                            break;
                        }
                        break;
                }
                return null;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(Thumbnail thumbnail) {
            Log.m5d("ThumbnailViewManager", "[onPostExecute]isCancelled()=" + isCancelled());
            if (isCancelled()) {
                return;
            }
            if (ThumbnailViewManager.this.getContext().isSecureCamera() && ThumbnailViewManager.this.getContext().getSecureAlbumCount() <= 0) {
                ThumbnailViewManager.this.mThumbnail = null;
            } else {
                ThumbnailViewManager.this.mThumbnail = thumbnail;
            }
            ThumbnailViewManager.this.updateThumbnailView();
        }
    }

    private void getLastThumbnailUncached() {
        Log.m5d("ThumbnailViewManager", "[getLastThumbnailUncached]...");
        cancelLoadThumbnail();
        synchronized (this.mLock) {
            this.mLoadThumbnailTask = new LoadThumbnailTask().execute(new Void[0]);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendUpdateThumbnail() {
        Log.m5d("ThumbnailViewManager", "[sendUpdateThumbnail]...");
        this.mIsUpdatingThumbnail = true;
        this.mMainHandler.removeMessages(1);
        this.mMainHandler.obtainMessage(1, this.mThumbnail).sendToTarget();
    }

    private class WorkerHandler extends Handler {
        public WorkerHandler(Looper looper) {
            super(looper);
            Log.m5d("ThumbnailViewManager", "[WorkerHandler]new...");
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            Log.m5d("ThumbnailViewManager", "[handleMessage]WorkerHandler,msg.what = " + message.what);
            long jCurrentTimeMillis = System.currentTimeMillis();
            switch (message.what) {
                case 0:
                    ThumbnailViewManager.this.mIsSavingThumbnail = true;
                    SaveRequest saveRequest = ThumbnailViewManager.this.mCurrentSaveRequest;
                    if (ThumbnailViewManager.this.mThumbnailView == null) {
                        ThumbnailViewManager.this.getView();
                    }
                    if (ThumbnailViewManager.this.mThumbnailView != null) {
                        if (ThumbnailViewManager.this.mRefreshInterval != 0 && jCurrentTimeMillis - ThumbnailViewManager.this.mLastRefreshTime < ThumbnailViewManager.this.mRefreshInterval) {
                            Log.m5d("ThumbnailViewManager", "[handleMessage]WorkerHandler, sendEmptyMessageDelayed.");
                            sendEmptyMessageDelayed(0, ThumbnailViewManager.this.mRefreshInterval - (jCurrentTimeMillis - ThumbnailViewManager.this.mLastRefreshTime));
                        } else {
                            ThumbnailViewManager.this.mLastRefreshTime = jCurrentTimeMillis;
                            Thumbnail thumbnailCreateThumbnail = saveRequest.createThumbnail(ThumbnailViewManager.this.mThumbnailView.getLayoutParams().width);
                            if (thumbnailCreateThumbnail != null) {
                                ThumbnailViewManager.this.mThumbnail = thumbnailCreateThumbnail;
                            } else {
                                Log.m11w("ThumbnailViewManager", "[handleMessage]WorkerHandler,thumb is null!");
                            }
                            ThumbnailViewManager.this.sendUpdateThumbnail();
                        }
                    }
                    ThumbnailViewManager.this.mIsSavingThumbnail = false;
                    break;
                case 2:
                    ThumbnailViewManager.this.mIsSavingThumbnail = true;
                    if (ThumbnailViewManager.this.mThumbnailView == null) {
                        ThumbnailViewManager.this.getView();
                    }
                    if (ThumbnailViewManager.this.mThumbnailView != null) {
                        Thumbnail thumbnailCreateThumbnailWithYuv = ThumbnailViewManager.this.createThumbnailWithYuv(ThumbnailViewManager.this.mYuvData, ThumbnailViewManager.this.mThumbnailView.getLayoutParams().width, ThumbnailViewManager.this.mYuvWidth, ThumbnailViewManager.this.mYuvHeight, ThumbnailViewManager.this.mYuvOrientation, ThumbnailViewManager.this.mYuvImageFormat);
                        if (thumbnailCreateThumbnailWithYuv != null) {
                            ThumbnailViewManager.this.mThumbnail = thumbnailCreateThumbnailWithYuv;
                            ThumbnailViewManager.this.sendUpdateThumbnail();
                        } else {
                            Log.m11w("ThumbnailViewManager", "[handleMessage]WorkerHandler,thumb is null!");
                        }
                    }
                    ThumbnailViewManager.this.mIsSavingThumbnail = false;
                    break;
                case 3:
                    if (ThumbnailViewManager.this.mCurrentSaveRequest != null) {
                        ThumbnailViewManager.this.mCurrentSaveRequest.releaseUri();
                        break;
                    }
                    break;
            }
        }
    }

    private void cancelLoadThumbnail() {
        synchronized (this.mLock) {
            if (this.mLoadThumbnailTask != null) {
                Log.m5d("ThumbnailViewManager", "[cancelLoadThumbnail]...");
                this.mLoadThumbnailTask.cancel(true);
                this.mLoadThumbnailTask = null;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Thumbnail createThumbnailWithYuv(byte[] bArr, int i, int i2, int i3, int i4, int i5) {
        if (bArr == null) {
            return null;
        }
        Log.m5d("ThumbnailViewManager", "[createThumbnailWithYuv]...");
        return Thumbnail.createThumbnail(covertYuvDataToJpeg(bArr, i2, i3, i5), i4, Integer.highestOneBit((int) Math.ceil(i2 / i)), null, null);
    }

    private byte[] covertYuvDataToJpeg(byte[] bArr, int i, int i2, int i3) {
        Rect rect = new Rect(0, 0, i, i2);
        YuvImage yuvImage = new YuvImage(bArr, i3, i, i2, null);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        yuvImage.compressToJpeg(rect, CameraProfile.getJpegEncodingQualityParameter(2), byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    private boolean isNeedDumpYuv() {
        boolean z = SystemProperties.getInt("debug.thumbnailFromYuv.enable", 0) == 1;
        Log.m5d("ThumbnailViewManager", "[isNeedDumpYuv] return :" + z);
        return z;
    }

    /* JADX WARN: Removed duplicated region for block: B:30:0x004f A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private void dumpYuv(java.lang.String r5, byte[] r6) throws java.lang.Throwable {
        /*
            r4 = this;
            r2 = 0
            java.lang.String r0 = "ThumbnailViewManager"
            java.lang.String r1 = "[dumpYuv] begin"
            com.android.camera.Log.m5d(r0, r1)     // Catch: java.io.IOException -> L2f java.lang.Throwable -> L4b
            java.io.FileOutputStream r1 = new java.io.FileOutputStream     // Catch: java.io.IOException -> L2f java.lang.Throwable -> L4b
            r1.<init>(r5)     // Catch: java.io.IOException -> L2f java.lang.Throwable -> L4b
            r1.write(r6)     // Catch: java.lang.Throwable -> L5e java.io.IOException -> L60
            r1.close()     // Catch: java.lang.Throwable -> L5e java.io.IOException -> L60
            if (r1 == 0) goto L1a
            r1.close()     // Catch: java.io.IOException -> L24
        L1a:
            java.lang.String r0 = "ThumbnailViewManager"
            java.lang.String r1 = "[dumpYuv] end"
            com.android.camera.Log.m5d(r0, r1)
            return
        L24:
            r0 = move-exception
            java.lang.String r1 = "ThumbnailViewManager"
            java.lang.String r2 = "[dumpYuv]IOException:"
            com.android.camera.Log.m7e(r1, r2, r0)
            goto L1a
        L2f:
            r0 = move-exception
            r1 = r2
        L31:
            java.lang.String r2 = "ThumbnailViewManager"
            java.lang.String r3 = "[dumpYuv]Failed to write image,ex:"
            com.android.camera.Log.m7e(r2, r3, r0)     // Catch: java.lang.Throwable -> L5e
            if (r1 == 0) goto L1a
            r1.close()     // Catch: java.io.IOException -> L40
            goto L1a
        L40:
            r0 = move-exception
            java.lang.String r1 = "ThumbnailViewManager"
            java.lang.String r2 = "[dumpYuv]IOException:"
            com.android.camera.Log.m7e(r1, r2, r0)
            goto L1a
        L4b:
            r0 = move-exception
            r1 = r2
        L4d:
            if (r1 == 0) goto L52
            r1.close()     // Catch: java.io.IOException -> L53
        L52:
            throw r0
        L53:
            r1 = move-exception
            java.lang.String r2 = "ThumbnailViewManager"
            java.lang.String r3 = "[dumpYuv]IOException:"
            com.android.camera.Log.m7e(r2, r3, r1)
            goto L52
        L5e:
            r0 = move-exception
            goto L4d
        L60:
            r0 = move-exception
            goto L31
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.camera.manager.ThumbnailViewManager.dumpYuv(java.lang.String, byte[]):void");
    }
}
