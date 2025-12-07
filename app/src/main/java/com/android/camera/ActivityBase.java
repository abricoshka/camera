package com.android.camera;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import com.android.camera.p002v2.CameraActivityBridge;
import java.util.ArrayList;
import java.util.regex.PatternSyntaxException;

/* loaded from: classes.dex */
public abstract class ActivityBase extends Activity {
    private static boolean sFirstStartAfterScreenOn = true;
    private static BroadcastReceiver sScreenOffReceiver;
    protected static int sSecureAlbumId;
    protected boolean mPaused;
    private int mResultCodeForTesting;
    private Intent mResultDataForTesting;
    protected boolean mSecureCamera;
    private boolean mIsLockScreen = false;
    private boolean mNeedShowThumbnail = true;
    private boolean mIsGotoGallery = false;
    private ArrayList<String> mSecureArray = new ArrayList<>();
    private String mPath = null;
    protected ICameraActivityBridge mCameraActivityBridge = null;
    private BroadcastReceiver mScreenOffReceiver = new BroadcastReceiver() { // from class: com.android.camera.ActivityBase.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            ActivityBase.this.finish();
            Log.m5d("ActivityBase", "mScreenOffReceiver receive");
        }
    };

    private static class ScreenOffReceiver extends BroadcastReceiver {
        /* synthetic */ ScreenOffReceiver(ScreenOffReceiver screenOffReceiver) {
            this();
        }

        private ScreenOffReceiver() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            boolean unused = ActivityBase.sFirstStartAfterScreenOn = true;
        }
    }

    public static boolean isFirstStartAfterScreenOn() {
        return sFirstStartAfterScreenOn;
    }

    public static void resetFirstStartAfterScreenOn() {
        sFirstStartAfterScreenOn = false;
    }

    @Override // android.app.Activity
    public void onCreate(Bundle bundle) {
        ScreenOffReceiver screenOffReceiver = null;
        Log.m8i("ActivityBase", "ActivityBase oncreate");
        if (Util.isWfdEnabled(this) || FeatureSwitcher.isTablet()) {
            setRequestedOrientation(-1);
            setRequestedOrientation(calculateCurrentScreenOrientation());
        }
        requestWindowFeature(9);
        Intent intent = getIntent();
        String action = intent.getAction();
        if ("android.media.action.STILL_IMAGE_CAMERA_SECURE".equals(action)) {
            this.mSecureCamera = true;
            this.mIsLockScreen = true;
            sSecureAlbumId++;
        } else if (CameraActivityBridge.ACTION_IMAGE_CAPTURE_SECURE.equals(action)) {
            this.mSecureCamera = true;
        } else {
            this.mSecureCamera = intent.getBooleanExtra(CameraActivityBridge.SECURE_CAMERA_EXTRA, false);
        }
        if (this.mSecureCamera) {
            setScreenFlags();
            this.mNeedShowThumbnail = !this.mIsLockScreen;
            this.mPath = "/secure/all/" + sSecureAlbumId;
            IntentFilter intentFilter = new IntentFilter("android.intent.action.SCREEN_OFF");
            registerReceiver(this.mScreenOffReceiver, intentFilter);
            if (sScreenOffReceiver == null) {
                sScreenOffReceiver = new ScreenOffReceiver(screenOffReceiver);
                getApplicationContext().registerReceiver(sScreenOffReceiver, intentFilter);
            }
        }
        super.onCreate(bundle);
    }

    @Override // android.app.Activity
    protected void onResume() {
        this.mIsGotoGallery = false;
        this.mPaused = false;
        updateSecureThumbnail();
        super.onResume();
    }

    @Override // android.app.Activity
    protected void onPause() {
        this.mPaused = true;
        this.mNeedShowThumbnail = true;
        if (this.mIsLockScreen && (this.mSecureArray.isEmpty() || (!this.mIsGotoGallery))) {
            Log.m8i("ActivityBase", "[onPause] Secure Camera go to Gallery" + this.mIsGotoGallery);
            this.mNeedShowThumbnail = false;
        }
        if (!this.mIsGotoGallery) {
            this.mSecureArray.clear();
        }
        super.onPause();
    }

    @Override // android.app.Activity, android.view.Window.Callback
    public boolean onSearchRequested() {
        return false;
    }

    @Override // android.app.Activity, android.view.KeyEvent.Callback
    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        if ((i == 84 || i == 82) && keyEvent.isLongPress()) {
            return true;
        }
        return super.onKeyDown(i, keyEvent);
    }

    protected void setResultEx(int i) {
        this.mResultCodeForTesting = i;
        setResult(i);
    }

    public void setResultEx(int i, Intent intent) {
        this.mResultCodeForTesting = i;
        this.mResultDataForTesting = intent;
        setResult(i, intent);
    }

    public int getResultCode() {
        return this.mResultCodeForTesting;
    }

    public Intent getResultData() {
        return this.mResultDataForTesting;
    }

    @Override // android.app.Activity
    protected void onDestroy() {
        if (this.mSecureCamera) {
            unregisterReceiver(this.mScreenOffReceiver);
        }
        super.onDestroy();
    }

    protected void addSecureAlbumItemIfNeeded(boolean z, Uri uri) throws NumberFormatException {
        if (this.mSecureCamera) {
            if (!this.mPaused || (this.mPaused && this.mIsGotoGallery)) {
                Log.m8i("ActivityBase", "addSecureAlbumItemIfNeeded uri = " + uri);
                this.mSecureArray.add(String.valueOf(Integer.parseInt(uri.getLastPathSegment())) + (z ? "+true" : "+false"));
            }
        }
    }

    protected ArrayList<String> getSecureAlbum() {
        return this.mSecureArray;
    }

    public void setPath(String str) {
    }

    public String getPath() {
        return this.mPath;
    }

    public void notifyGotoGallery() {
        this.mIsGotoGallery = true;
    }

    public int getSecureAlbumCount() {
        Log.m5d("ActivityBase", "[getSecureAlbumCount] mNeedShowThumbnail = " + this.mNeedShowThumbnail);
        return this.mNeedShowThumbnail ? 1 : 0;
    }

    public boolean isActivityOnpause() {
        Log.m8i("ActivityBase", "isActivityOnpause , mpaused = " + this.mPaused);
        return this.mPaused;
    }

    public boolean isFullScreen() {
        return true;
    }

    public boolean isSecureCamera() {
        return this.mSecureCamera;
    }

    private void setScreenFlags() {
        Window window = getWindow();
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.flags |= 524288;
        window.setAttributes(attributes);
    }

    private void updateSecureThumbnail() {
        if (this.mIsLockScreen && (!this.mSecureArray.isEmpty())) {
            if (checkSecureAlbumLive()) {
                this.mNeedShowThumbnail = true;
            } else {
                this.mNeedShowThumbnail = false;
            }
            Log.m8i("ActivityBase", "mNeedShowThumbnail = " + this.mNeedShowThumbnail);
        }
    }

    private boolean isSecureUriLive(int i) throws Throwable {
        Cursor cursorQuery;
        try {
            cursorQuery = MediaStore.Images.Media.query(getContentResolver(), MediaStore.Files.getContentUri("external"), null, "_id=(" + i + ")", null, null);
            if (cursorQuery != null) {
                try {
                    Log.m11w("ActivityBase", "<isSecureUriLive> cursor " + cursorQuery.getCount());
                    boolean z = cursorQuery.getCount() > 0;
                    if (cursorQuery != null) {
                        cursorQuery.close();
                    }
                    return z;
                } catch (Throwable th) {
                    th = th;
                    if (cursorQuery != null) {
                        cursorQuery.close();
                    }
                    throw th;
                }
            }
            if (cursorQuery != null) {
                cursorQuery.close();
            }
            return true;
        } catch (Throwable th2) {
            th = th2;
            cursorQuery = null;
        }
    }

    private boolean checkSecureAlbumLive() throws NumberFormatException {
        if (this.mSecureArray != null && (!this.mSecureArray.isEmpty())) {
            int size = this.mSecureArray.size();
            Log.m5d("ActivityBase", "<checkSecureAlbum> albumCount " + size);
            for (int i = 0; i < size; i++) {
                try {
                    String[] strArrSplit = this.mSecureArray.get(i).split("\\+");
                    int length = strArrSplit.length;
                    Log.m5d("ActivityBase", "<checkSecureAlbum> albumItemSize " + length);
                    if (length == 2) {
                        int i2 = Integer.parseInt(strArrSplit[0].trim());
                        Log.m5d("ActivityBase", "<checkSecureAlbum> secure item : id " + i2 + ", isVideo " + Boolean.parseBoolean(strArrSplit[1].trim()));
                        if (isSecureUriLive(i2)) {
                            return true;
                        }
                    } else {
                        continue;
                    }
                } catch (NullPointerException e) {
                    Log.m6e("ActivityBase", "<checkSecureAlbum> exception " + e);
                } catch (NumberFormatException e2) {
                    Log.m6e("ActivityBase", "<checkSecureAlbum> exception " + e2);
                } catch (PatternSyntaxException e3) {
                    Log.m6e("ActivityBase", "<checkSecureAlbum> exception " + e3);
                }
            }
        }
        return false;
    }

    private int calculateCurrentScreenOrientation() {
        int displayRotation = Util.getDisplayRotation(this);
        Log.m8i("ActivityBase", "calculateCurrentScreenOrientation displayRotation = " + displayRotation);
        if (displayRotation == 0) {
            return 1;
        }
        if (displayRotation == 90) {
            return 0;
        }
        if (displayRotation == 180) {
            return 9;
        }
        return displayRotation == 270 ? 8 : 1;
    }
}
