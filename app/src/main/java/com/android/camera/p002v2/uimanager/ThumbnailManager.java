package com.android.camera.p002v2.uimanager;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.view.ViewGroup;
import com.android.camera.p002v2.Thumbnail;
import com.android.camera.p002v2.app.AppController;
import com.android.camera.p002v2.p003ui.RotateImageView;
import com.android.camera.p002v2.uimanager.ThumbnailAnimation;
import com.mediatek.camera.R;
import com.mediatek.camera.debug.LogHelper;
import com.mediatek.camera.p005v2.services.storage.IStorageService;
import java.io.File;

/* loaded from: classes.dex */
public class ThumbnailManager extends AbstractUiManager {
    private static final LogHelper.Tag TAG = new LogHelper.Tag(ThumbnailManager.class.getSimpleName());
    private Activity mActivity;
    private ContentResolver mContentResolver;
    private ThumbnailCreatorHandler mHandler;
    private Intent mIntent;
    private IntentFilter mIpoShutdownFilter;
    private BroadcastReceiver mIpoShutdownReceiver;
    private boolean mIsSecureCamera;
    private AsyncTask<Void, Void, Thumbnail> mLoadThumbnailTask;
    private Handler mMaiHandler;
    private boolean mNeedShowSecureCamera;
    private OnThumbnailClickListener mOnThumbnailClickListener;
    private RotateImageView mPreviewThumb;
    private boolean mResumed;
    private boolean mShownByIntent;
    private IStorageService mStorageService;
    private Thumbnail mThumbnail;
    private ThumbnailAnimation mThumbnailAnimation;
    private RotateImageView mThumbnailView;
    IntentFilter mUpdatePictureFilter;
    private BroadcastReceiver mUpdatePictureReceiver;

    public interface OnThumbnailClickListener {
        void onThumbnailClick();
    }

    public ThumbnailManager(AppController appController, Activity activity, ViewGroup viewGroup, boolean z) {
        super(activity, viewGroup);
        this.mShownByIntent = true;
        this.mResumed = false;
        this.mIsSecureCamera = false;
        this.mNeedShowSecureCamera = true;
        this.mUpdatePictureFilter = new IntentFilter("com.android.gallery3d.action.UPDATE_PICTURE");
        this.mUpdatePictureReceiver = new BroadcastReceiver() { // from class: com.android.camera.v2.uimanager.ThumbnailManager.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) {
                LogHelper.m26i(ThumbnailManager.TAG, "mDeletePictureReceiver.onReceive(" + intent + ")");
                if (ThumbnailManager.this.isShowing()) {
                    ThumbnailManager.this.getLastThumbnailUncached();
                }
            }
        };
        this.mIpoShutdownFilter = new IntentFilter("android.intent.action.ACTION_SHUTDOWN_IPO");
        this.mIpoShutdownReceiver = new BroadcastReceiver() { // from class: com.android.camera.v2.uimanager.ThumbnailManager.2
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) {
                LogHelper.m23d(ThumbnailManager.TAG, "[onReceive]intent = " + intent);
                ThumbnailManager.this.saveThumbnailToFile();
            }
        };
        this.mIsSecureCamera = z;
        setFilterEnable(false);
        this.mStorageService = appController.getAppControllerAdapter().getServices().getStorageService();
        this.mActivity = activity;
        this.mContentResolver = activity.getContentResolver();
        this.mMaiHandler = new Handler(activity.getMainLooper());
        HandlerThread handlerThread = new HandlerThread("thumbnail-creation-thread");
        handlerThread.start();
        this.mHandler = new ThumbnailCreatorHandler(handlerThread.getLooper());
        this.mThumbnailAnimation = new ThumbnailAnimation();
        LocalBroadcastManager.getInstance(this.mActivity).registerReceiver(this.mUpdatePictureReceiver, this.mUpdatePictureFilter);
        this.mActivity.registerReceiver(this.mIpoShutdownReceiver, this.mIpoShutdownFilter);
        this.mIntent = activity.getIntent();
        String action = this.mIntent != null ? this.mIntent.getAction() : null;
        if ("android.media.action.IMAGE_CAPTURE".equals(action) || "android.media.action.VIDEO_CAPTURE".equals(action) || "android.media.action.IMAGE_CAPTURE_3D".equals(action)) {
            this.mShownByIntent = false;
        }
    }

    @Override // com.android.camera.p002v2.uimanager.AbstractUiManager
    public void show() {
        LogHelper.m26i(TAG, "[show], mShownByIntent:" + this.mShownByIntent);
        if (this.mShownByIntent) {
            super.show();
        }
    }

    @Override // com.android.camera.p002v2.uimanager.AbstractUiManager
    protected View getView() {
        View viewInflate = inflate(R.layout.thumbnail_v2);
        this.mThumbnailView = (RotateImageView) viewInflate.findViewById(R.id.thumbnail);
        this.mThumbnailView.setOnClickListener(new View.OnClickListener() { // from class: com.android.camera.v2.uimanager.ThumbnailManager.3
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (ThumbnailManager.this.mOnThumbnailClickListener != null) {
                    ThumbnailManager.this.mOnThumbnailClickListener.onThumbnailClick();
                }
            }
        });
        this.mPreviewThumb = (RotateImageView) viewInflate.findViewById(R.id.preview_thumb);
        return viewInflate;
    }

    @Override // com.android.camera.p002v2.uimanager.AbstractUiManager
    protected void onRefresh() {
        LogHelper.m26i(TAG, "[onRefresh]...");
        updateThumbnailView();
    }

    @Override // com.android.camera.p002v2.uimanager.AbstractUiManager
    public void setEnable(boolean z) {
        super.setEnable(z);
        if (this.mThumbnailView != null) {
            this.mThumbnailView.setEnabled(z);
            this.mThumbnailView.setClickable(z);
        }
    }

    public Uri getThumbnailUri() {
        if (this.mThumbnail != null) {
            return Uri.parse("file://" + this.mThumbnail.getFilePath());
        }
        LogHelper.m26i(TAG, "[getThumbnailUri], null");
        return null;
    }

    public String getThumbnailMimeType() {
        if (this.mThumbnail != null) {
            return getMimeType(this.mThumbnail.getFilePath());
        }
        LogHelper.m26i(TAG, "[getThumbnailMimeType], null");
        return null;
    }

    private String getMimeType(String str) throws IllegalArgumentException {
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        String strExtractMetadata = "image/jpeg";
        if (str != null) {
            try {
                mediaMetadataRetriever.setDataSource(str);
                strExtractMetadata = mediaMetadataRetriever.extractMetadata(12);
            } catch (IllegalArgumentException e) {
                return "image/jpeg";
            } catch (IllegalStateException e2) {
                return "image/jpeg";
            } catch (RuntimeException e3) {
                return "image/jpeg";
            }
        }
        LogHelper.m26i(TAG, "[getMimeType] mime = " + strExtractMetadata);
        return strExtractMetadata;
    }

    public void setOnThumbnailClickListener(OnThumbnailClickListener onThumbnailClickListener) {
        this.mOnThumbnailClickListener = onThumbnailClickListener;
    }

    public void onResume() {
        LogHelper.m26i(TAG, "[onResume] mShownByIntent = " + this.mShownByIntent);
        this.mResumed = true;
        if (this.mShownByIntent) {
            this.mLoadThumbnailTask = new LoadThumbnailTask(false).execute(new Void[0]);
        }
    }

    public void onPause() {
        LogHelper.m26i(TAG, "[onPause]...");
        this.mResumed = false;
        cancelLoadThumbnail();
        saveThumbnailToFile();
    }

    public void onDestroy() {
        LogHelper.m26i(TAG, "[onDestroy]...");
        if (this.mHandler != null) {
            this.mHandler.getLooper().quit();
        }
    }

    public void forceUpdate() {
        getLastThumbnailUncached();
    }

    public void notifyFileSaved(Uri uri) {
        LogHelper.m26i(TAG, "[notifyFileSaved], uri:" + uri + ", mShownByIntent:" + this.mShownByIntent);
        if (uri == null) {
            return;
        }
        cancelLoadThumbnail();
        this.mHandler.sendEmptyMessage(0);
    }

    public void updateNeedShowThumbnail(boolean z) {
        LogHelper.m26i(TAG, "[updateNeedShowThumbnail] isNeedShow " + z);
        this.mNeedShowSecureCamera = z;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateThumbnailView() {
        LogHelper.m26i(TAG, "[updateThumbnailView]this = " + this);
        if (this.mThumbnailView != null && isShowing()) {
            if (this.mThumbnail != null && this.mThumbnail.getBitmap() != null) {
                LogHelper.m26i(TAG, "[updateThumbnailView]showing is true,set VISIBLE.");
                this.mThumbnailView.setBitmap(null);
                this.mThumbnailView.setBitmap(this.mThumbnail.getBitmap());
                this.mThumbnailView.setVisibility(0);
                return;
            }
            LogHelper.m26i(TAG, "[updateThumbnailView]showing is true,but thumbnail is null,set INVISIBLE!");
            this.mThumbnailView.setBitmap(null);
            this.mThumbnailView.setVisibility(4);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Thumbnail getLastThumbnailFromContentResolver(IStorageService iStorageService, ContentResolver contentResolver) throws Throwable {
        Thumbnail[] thumbnailArr = new Thumbnail[1];
        int lastThumbnailFromContentResolver = Thumbnail.getLastThumbnailFromContentResolver(iStorageService.getFileDirectory(), contentResolver, thumbnailArr);
        LogHelper.m23d(TAG, "getLastThumbnailFromContentResolver code = " + lastThumbnailFromContentResolver);
        switch (lastThumbnailFromContentResolver) {
            case 0:
                return null;
            case 1:
                return thumbnailArr[0];
            case 2:
                return null;
            default:
                return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateThumbnailViewWithAnimation() {
        if (this.mThumbnail != null && this.mPreviewThumb != null) {
            this.mPreviewThumb.setBitmap(null);
            this.mPreviewThumb.setBitmap(this.mThumbnail.getBitmap());
            this.mThumbnailAnimation.doCaptureAnimation(this.mPreviewThumb, this.mActivity, new ThumbnailAnimation.AnimationListener() { // from class: com.android.camera.v2.uimanager.ThumbnailManager.4
                @Override // com.android.camera.v2.uimanager.ThumbnailAnimation.AnimationListener
                public void onAnimationEnd() {
                    ThumbnailManager.this.updateThumbnailView();
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void saveThumbnailToFile() {
        SaveThumbnailTask saveThumbnailTask = null;
        LogHelper.m23d(TAG, "[saveThumbnailToFile], mThumbnail:" + this.mThumbnail);
        if (this.mThumbnail != null && (!this.mThumbnail.fromFile())) {
            LogHelper.m23d(TAG, "[saveThumbnailToFile]execute...");
            new SaveThumbnailTask(this, saveThumbnailTask).execute(this.mThumbnail);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void getLastThumbnailUncached() {
        LogHelper.m23d(TAG, "[getLastThumbnailUncached]...");
        cancelLoadThumbnail();
        this.mLoadThumbnailTask = new LoadThumbnailTask(false).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
    }

    private void cancelLoadThumbnail() {
        if (this.mLoadThumbnailTask != null) {
            LogHelper.m23d(TAG, "[cancelLoadThumbnail]...");
            this.mLoadThumbnailTask.cancel(true);
            this.mLoadThumbnailTask = null;
        }
    }

    private class ThumbnailCreatorHandler extends Handler {
        public ThumbnailCreatorHandler(Looper looper) {
            super(looper);
            LogHelper.m26i(ThumbnailManager.TAG, "[ThumbnailCreatorHandler]new...");
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            LogHelper.m26i(ThumbnailManager.TAG, "[handleMessage]ThumbnailCreatorHandler,msg:" + message);
            System.currentTimeMillis();
            switch (message.what) {
                case 0:
                    ThumbnailManager.this.mThumbnail = ThumbnailManager.this.getLastThumbnailFromContentResolver(ThumbnailManager.this.mStorageService, ThumbnailManager.this.mContentResolver);
                    if (!ThumbnailManager.this.mResumed) {
                        ThumbnailManager.this.saveThumbnailToFile();
                    }
                    if (ThumbnailManager.this.mMaiHandler != null) {
                        ThumbnailManager.this.mMaiHandler.post(new Runnable() { // from class: com.android.camera.v2.uimanager.ThumbnailManager.ThumbnailCreatorHandler.1
                            @Override // java.lang.Runnable
                            public void run() {
                                ThumbnailManager.this.updateThumbnailViewWithAnimation();
                            }
                        });
                        break;
                    }
                    break;
            }
        }
    }

    private class LoadThumbnailTask extends AsyncTask<Void, Void, Thumbnail> {
        private boolean mLookAtCache;

        protected LoadThumbnailTask(boolean z) {
            this.mLookAtCache = z;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public Thumbnail doInBackground(Void... voidArr) {
            LogHelper.m26i(ThumbnailManager.TAG, "[doInBackground]begin.mLookAtCache = " + this.mLookAtCache);
            Thumbnail lastThumbnailFromFile = this.mLookAtCache ? Thumbnail.getLastThumbnailFromFile(ThumbnailManager.this.mStorageService.getFileDirectory(), ThumbnailManager.this.mActivity.getFilesDir(), ThumbnailManager.this.mContentResolver) : null;
            if (isCancelled()) {
                LogHelper.m28w(ThumbnailManager.TAG, "[doInBackground]task is cancel,return.");
                return null;
            }
            if (lastThumbnailFromFile == null && ThumbnailManager.this.mStorageService.isStorageReady()) {
                return ThumbnailManager.this.getLastThumbnailFromContentResolver(ThumbnailManager.this.mStorageService, ThumbnailManager.this.mContentResolver);
            }
            return lastThumbnailFromFile;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(Thumbnail thumbnail) {
            LogHelper.m23d(ThumbnailManager.TAG, "[onPostExecute]isCancelled()=" + isCancelled() + ",mIsSecureCamera = " + ThumbnailManager.this.mIsSecureCamera + ",mNeedShowSecureCamera = " + ThumbnailManager.this.mNeedShowSecureCamera);
            if (isCancelled()) {
                return;
            }
            if (ThumbnailManager.this.mIsSecureCamera && (!ThumbnailManager.this.mNeedShowSecureCamera)) {
                ThumbnailManager.this.mThumbnail = null;
            } else {
                ThumbnailManager.this.mThumbnail = thumbnail;
            }
            ThumbnailManager.this.updateThumbnailView();
        }
    }

    private class SaveThumbnailTask extends AsyncTask<Thumbnail, Void, Void> {
        /* synthetic */ SaveThumbnailTask(ThumbnailManager thumbnailManager, SaveThumbnailTask saveThumbnailTask) {
            this();
        }

        private SaveThumbnailTask() {
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public Void doInBackground(Thumbnail... thumbnailArr) {
            LogHelper.m23d(ThumbnailManager.TAG, "[doInBackground]length = " + thumbnailArr.length);
            File filesDir = ThumbnailManager.this.mActivity.getFilesDir();
            for (Thumbnail thumbnail : thumbnailArr) {
                thumbnail.saveLastThumbnailToFile(filesDir);
            }
            return null;
        }
    }
}
