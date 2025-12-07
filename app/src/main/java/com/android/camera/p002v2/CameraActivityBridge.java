package com.android.camera.p002v2;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import com.android.camera.CameraActivity;
import com.android.camera.ICameraActivityBridge;
import com.android.camera.p002v2.app.AppController;
import com.android.camera.p002v2.app.CameraAppUI;
import com.android.camera.p002v2.app.GestureManager;
import com.android.camera.p002v2.app.GestureManagerImpl;
import com.android.camera.p002v2.app.ModuleManager;
import com.android.camera.p002v2.app.ModuleManagerImpl;
import com.android.camera.p002v2.app.OrientationManager;
import com.android.camera.p002v2.app.OrientationManagerImpl;
import com.android.camera.p002v2.app.PreviewManager;
import com.android.camera.p002v2.app.PreviewManagerImpl;
import com.android.camera.p002v2.app.SettingAgent;
import com.android.camera.p002v2.app.location.LocationManager;
import com.android.camera.p002v2.bridge.AppContextAdapter;
import com.android.camera.p002v2.bridge.AppControllerAdapter;
import com.android.camera.p002v2.bridge.ModeChangeAdapter;
import com.android.camera.p002v2.bridge.SettingAdapter;
import com.android.camera.p002v2.module.ModuleController;
import com.android.camera.p002v2.module.ModulesInfo;
import com.android.camera.p002v2.p003ui.PreviewStatusListener;
import com.android.camera.p002v2.uimanager.preference.PreferenceManager;
import com.android.camera.p002v2.util.CameraUtil;
import com.android.camera.p002v2.util.SettingKeys;
import com.mediatek.camera.R;
import com.mediatek.camera.debug.LogHelper;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.PatternSyntaxException;

/* loaded from: classes.dex */
public class CameraActivityBridge implements ICameraActivityBridge, AppController, OrientationManager.OnOrientationChangeListener {
    public static final String ACTION_IMAGE_CAPTURE_SECURE = "android.media.action.IMAGE_CAPTURE_SECURE";
    private static final String INTENT_ACTION_STILL_IMAGE_CAMERA_SECURE = "android.media.action.STILL_IMAGE_CAMERA_SECURE";
    private static final int MSG_CLEAR_SCREEN_ON_FLAG = 1;
    private static final int MSG_NOTIFY_PREFERENCES_READY = 0;
    private static final int NOT_SHOW_STORAGE_HINT = 1;
    private static final long SCREEN_DELAY_MS = 120000;
    public static final String SECURE_CAMERA_EXTRA = "secure_camera";
    private static final LogHelper.Tag TAG = new LogHelper.Tag(CameraActivityBridge.class.getSimpleName());
    private static AlertDialog sAlertDialog;
    protected static int sSecureAlbumId;
    private ActivityManager mActivityManager;
    private final Context mAppContext;
    private final CameraActivity mCameraActivity;
    private CameraAppUI mCameraAppUI;
    private String mCurrentModeKey;
    private CameraModule mCurrentModule;
    private int mCurrentModuleIndex;
    private GestureManagerImpl mGestureManagerImpl;
    private boolean mKeepScreenOn;
    private int mLastRawOrientation;
    private LocationManager mLocationManager;
    private Handler mMainHandler;
    private ModeChangeAdapter mModeChangeAdapter;
    private ModuleManager mModuleManager;
    private String mOldModeKey;
    private OrientationManager mOrientationManager;
    private boolean mPaused;
    private PreferenceManager mPreferenceManager;
    private PreviewManagerImpl mPreviewManager;
    private boolean mSecureCamera;
    private SettingAgent mSettingAgent;
    private int mCurrentCameraId = 0;
    private boolean mIsGotoGallery = false;
    private boolean mIsLockScreen = false;
    private boolean mNeedShowThumbnail = true;
    private ArrayList<String> mSecureArray = new ArrayList<>();
    private String mPath = null;
    private Object mStorageSpaceLock = new Object();
    private SettingAgent.SettingChangedListener mLocationSettingChangedListener = new SettingAgent.SettingChangedListener() { // from class: com.android.camera.v2.CameraActivityBridge.1
        @Override // com.android.camera.v2.app.SettingAgent.SettingChangedListener
        public void onSettingResult(final Map<String, String> map, Map<String, String> map2) {
            String str = map.get("pref_camera_recordlocation_key");
            LogHelper.m26i(CameraActivityBridge.TAG, "[onSettingResult], loaction is : " + str);
            if (str == null) {
                return;
            }
            CameraActivityBridge.this.mCameraActivity.runOnUiThread(new Runnable() { // from class: com.android.camera.v2.CameraActivityBridge.1.1
                @Override // java.lang.Runnable
                public void run() {
                    if ("on".equalsIgnoreCase((String) map.get("pref_camera_recordlocation_key"))) {
                        if (CameraActivityBridge.this.mCameraActivity.getPermissionManager().requestCameraLocationPermissions()) {
                            CameraActivityBridge.this.mLocationManager.recordLocation(true);
                        }
                    } else if ("off".equalsIgnoreCase((String) map.get("pref_camera_recordlocation_key"))) {
                        CameraActivityBridge.this.mLocationManager.recordLocation(false);
                    }
                }
            });
        }
    };
    private final AppControllerAdapter mAppControllerAdapter = new AppControllerAdapter(this);
    private final AppContextAdapter mAppContextAdapter = new AppContextAdapter(this.mAppControllerAdapter);

    public CameraActivityBridge(CameraActivity cameraActivity) {
        this.mCameraActivity = cameraActivity;
        this.mAppContext = this.mCameraActivity.getApplication().getBaseContext();
    }

    @Override // com.android.camera.ICameraActivityBridge
    public void onCreate(Bundle bundle) {
        LogHelper.m26i(TAG, "[onCreate]...");
        Intent intent = this.mCameraActivity.getIntent();
        String action = intent.getAction();
        if (INTENT_ACTION_STILL_IMAGE_CAMERA_SECURE.equals(action)) {
            this.mSecureCamera = true;
            this.mIsLockScreen = true;
            sSecureAlbumId++;
        } else if (ACTION_IMAGE_CAPTURE_SECURE.equals(action)) {
            this.mSecureCamera = true;
        } else {
            this.mSecureCamera = intent.getBooleanExtra(SECURE_CAMERA_EXTRA, false);
        }
        this.mActivityManager = (ActivityManager) this.mCameraActivity.getSystemService("activity");
        LogHelper.m26i(TAG, "[onCreate]...mSecureCamera = " + this.mSecureCamera);
        if (this.mSecureCamera) {
            this.mNeedShowThumbnail = !this.mIsLockScreen;
            this.mPath = "/secure/all/" + sSecureAlbumId;
        }
        this.mAppContextAdapter.onCreate();
        this.mMainHandler = new MainHandler(this.mCameraActivity, this.mCameraActivity.getMainLooper());
        this.mCameraActivity.setContentView(R.layout.camera_activity);
        this.mSettingAgent = new SettingAdapter(this.mAppControllerAdapter);
        this.mSettingAgent.clearSharedPreferencesValue(SettingKeys.MODE_KEYS, String.valueOf(this.mCurrentCameraId));
        this.mPreferenceManager = new PreferenceManager(getActivity(), this.mSettingAgent);
        this.mCameraAppUI = new CameraAppUI(this);
        this.mCameraAppUI.setSettingAgent(this.mSettingAgent);
        View viewFindViewById = this.mCameraActivity.findViewById(R.id.camera_view_container);
        viewFindViewById.bringToFront();
        this.mCameraAppUI.init(viewFindViewById, this.mSecureCamera, isCaptureIntent());
        this.mCameraAppUI.prepareModuleUI();
        this.mCameraAppUI.updateSecureThumbnail(this.mNeedShowThumbnail);
        this.mOrientationManager = new OrientationManagerImpl(this.mCameraActivity);
        this.mOrientationManager.addOnOrientationChangeListener(this.mMainHandler, this);
        this.mLocationManager = new LocationManager(this.mAppContext);
        this.mSettingAgent.registerSettingChangedListener(this.mLocationSettingChangedListener, this.mMainHandler);
        this.mModuleManager = new ModuleManagerImpl();
        ModulesInfo.setupModules(this.mAppContext, this.mModuleManager);
        this.mCurrentModeKey = "normal_key";
        this.mOldModeKey = "normal_key";
        setModuleFromModeIndex(0);
        this.mCurrentModule.init(this.mCameraActivity, this.mSecureCamera, isCaptureIntent());
    }

    @Override // com.android.camera.ICameraActivityBridge
    public void onRestart() {
        LogHelper.m26i(TAG, "[onRestart]...");
    }

    @Override // com.android.camera.ICameraActivityBridge
    public void onResume() throws Resources.NotFoundException {
        LogHelper.m26i(TAG, "[onResume]...");
        hideAlertDialog();
        this.mPaused = false;
        initializePreferences(this.mCurrentCameraId, true);
        this.mAppContextAdapter.onResume();
        keepScreenOnForAWhile();
        this.mOrientationManager.resume();
        this.mCurrentModule.resume();
        updateStorageSpaceAndHint();
        updateSecureThumbnail();
        this.mCameraAppUI.updateSecureThumbnail(this.mNeedShowThumbnail);
        this.mCameraAppUI.onResume();
        LogHelper.m26i(TAG, "[onResume] end...");
    }

    @Override // com.android.camera.ICameraActivityBridge
    public void onPause() {
        LogHelper.m26i(TAG, "[onPause]...");
        this.mPaused = true;
        this.mNeedShowThumbnail = true;
        hideAlertDialog();
        if (this.mIsLockScreen && this.mSecureArray.isEmpty()) {
            this.mNeedShowThumbnail = false;
        }
        if (!this.mIsGotoGallery) {
            this.mSecureArray.clear();
        }
        this.mCameraAppUI.updateSecureThumbnail(this.mNeedShowThumbnail);
        this.mIsGotoGallery = false;
        this.mAppContextAdapter.onPause();
        this.mCameraAppUI.onPause();
        this.mOrientationManager.pause();
        resetScreenOn();
        this.mCurrentModule.pause();
    }

    @Override // com.android.camera.ICameraActivityBridge
    public void onDestroy() {
        LogHelper.m26i(TAG, "[onDestroy]...");
        this.mAppContextAdapter.onDestroy();
        this.mCameraAppUI.onDestroy();
        if (this.mOrientationManager != null) {
            this.mOrientationManager.removeOnOrientationChangeListener(this.mMainHandler, this);
            this.mOrientationManager = null;
        }
        this.mCurrentModule.destroy();
        this.mPreferenceManager.clearSharedPreferencesValue();
        closeGpsLocation();
    }

    @Override // com.android.camera.ICameraActivityBridge
    public boolean onBackPressed() {
        LogHelper.m26i(TAG, "[onBackPressed]...");
        if (this.mCurrentModule.onBackPressed() || this.mCameraAppUI.onBackPressed()) {
            return false;
        }
        return true;
    }

    @Override // com.android.camera.ICameraActivityBridge
    public void onConfigurationChanged(Configuration configuration) {
        LogHelper.m26i(TAG, "[onConfigurationChanged]... newConfig = " + configuration);
        this.mCameraAppUI.onConfigurationChanged(configuration);
    }

    @Override // com.android.camera.ICameraActivityBridge
    public void onActivityResult(int i, int i2, Intent intent) {
    }

    @Override // com.android.camera.ICameraActivityBridge
    public boolean onUserInteraction() {
        if (!this.mCameraActivity.isFinishing()) {
            keepScreenOnForAWhile();
            return false;
        }
        return false;
    }

    public void onSaveInstanceState(Bundle bundle) {
    }

    @Override // com.android.camera.ICameraActivityBridge
    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        return false;
    }

    @Override // com.android.camera.ICameraActivityBridge
    public boolean onKeyUp(int i, KeyEvent keyEvent) {
        return false;
    }

    @Override // com.android.camera.p002v2.app.AppController
    public Activity getActivity() {
        return this.mCameraActivity;
    }

    public Context getAndroidContext() {
        return this.mAppContext;
    }

    public String getModuleScope() {
        return null;
    }

    public String getCameraScope() {
        return null;
    }

    public void launchActivityByIntent(Intent intent) {
    }

    public void openContextMenu(View view) {
    }

    public void registerForContextMenu(View view) {
    }

    public boolean isPaused() {
        return this.mCameraActivity.isActivityOnpause();
    }

    public ModuleController getCurrentModuleController() {
        return null;
    }

    public int getCurrentModuleIndex() {
        return this.mCurrentModuleIndex;
    }

    @Override // com.android.camera.p002v2.app.AppController
    public String getCurrentMode() {
        return this.mCurrentModeKey;
    }

    @Override // com.android.camera.p002v2.app.AppController
    public String getOldMode() {
        return this.mOldModeKey;
    }

    public int getQuickSwitchToModuleId(int i) {
        return 0;
    }

    public int getPreferredChildModeIndex(int i) {
        return 0;
    }

    @Override // com.android.camera.p002v2.app.AppController
    public void onModeChanged(Map<String, String> map) {
        LogHelper.m26i(TAG, "onModeChanged changedModes = " + map);
        this.mOldModeKey = this.mCurrentModeKey;
        Iterator<String> it = map.keySet().iterator();
        ArrayList arrayList = new ArrayList();
        while (it.hasNext()) {
            arrayList.add(it.next());
        }
        String str = (String) arrayList.get(arrayList.size() - 1);
        String str2 = map.get(str);
        String str3 = this.mCurrentModeKey;
        this.mCurrentModeKey = str;
        if ("off".equals(str2)) {
            this.mCurrentModeKey = "normal_key";
        }
        boolean zIsNeedSwitchModule = ModeChangeAdapter.isNeedSwitchModule(str3, this.mCurrentModeKey);
        LogHelper.m26i(TAG, "onModeChanged needSwitchModule =" + zIsNeedSwitchModule + ",mCurrentModeKey = " + this.mCurrentModeKey);
        if (zIsNeedSwitchModule) {
            int moduleIndex = ModeChangeAdapter.getModuleIndex(this.mCurrentModeKey);
            if (moduleIndex != this.mCurrentModuleIndex) {
                closeModule(this.mCurrentModule);
                setModuleFromModeIndex(moduleIndex);
                openModule(this.mCurrentModule);
                return;
            }
            return;
        }
        if (this.mModeChangeAdapter != null) {
            this.mModeChangeAdapter.onModeChanged(this.mCurrentModeKey);
        }
    }

    @Override // com.android.camera.p002v2.app.AppController
    public void setModeChangeListener(ModeChangeAdapter modeChangeAdapter) {
        this.mModeChangeAdapter = modeChangeAdapter;
    }

    public void onSettingsSelected() {
    }

    @Override // com.android.camera.p002v2.app.AppController
    public void onPreviewVisibilityChanged(int i) {
        this.mCurrentModule.onPreviewVisibilityChanged(i);
    }

    @Override // com.android.camera.p002v2.app.AppController
    public PreviewManager getPreviewManager() {
        if (this.mPreviewManager == null) {
            this.mPreviewManager = new PreviewManagerImpl(getActivity());
        }
        return this.mPreviewManager;
    }

    public void freezeScreenUntilPreviewReady() {
    }

    public SurfaceTexture getPreviewBuffer() {
        return null;
    }

    public void onPreviewReadyToStart() {
    }

    @Override // com.android.camera.p002v2.app.AppController
    public void onPreviewStarted() {
        this.mCameraAppUI.onPreviewStarted();
    }

    public void setupOneShotPreviewListener() {
    }

    public void updatePreviewAspectRatio(float f) {
    }

    public void updatePreviewTransformFullscreen(Matrix matrix, float f) {
    }

    public RectF getFullscreenRect() {
        return null;
    }

    public void updatePreviewTransform(Matrix matrix) {
    }

    @Override // com.android.camera.p002v2.app.AppController
    public void setPreviewStatusListener(PreviewStatusListener previewStatusListener) {
        this.mCameraAppUI.setPreviewStatusListener(previewStatusListener);
    }

    @Override // com.android.camera.p002v2.app.AppController
    public void updatePreviewAreaChangedListener(PreviewStatusListener.OnPreviewAreaChangedListener onPreviewAreaChangedListener, boolean z) {
        if (z) {
            this.mCameraAppUI.addPreviewAreaSizeChangedListener(onPreviewAreaChangedListener);
        } else {
            this.mCameraAppUI.removePreviewAreaSizeChangedListener(onPreviewAreaChangedListener);
        }
    }

    @Override // com.android.camera.p002v2.app.AppController
    public void updatePreviewSize(int i, int i2) {
        this.mCameraAppUI.updatePreviewSize(i, i2);
    }

    @Override // com.android.camera.p002v2.app.AppController
    public FrameLayout getModuleLayoutRoot() {
        return this.mCameraAppUI.getModuleRootView();
    }

    public void lockOrientation() {
        if (this.mOrientationManager != null) {
            this.mOrientationManager.lockOrientation();
        }
    }

    public void unlockOrientation() {
        if (this.mOrientationManager != null) {
            this.mOrientationManager.unlockOrientation();
        }
    }

    @Override // com.android.camera.p002v2.app.AppController
    public void setShutterButtonEnabled(boolean z, boolean z2) {
        this.mCameraAppUI.setShutterButtonEnabled(z, z2);
    }

    @Override // com.android.camera.p002v2.app.AppController
    public void setShutterEventListener(AppController.ShutterEventsListener shutterEventsListener, boolean z) {
        this.mCameraAppUI.setShutterEventListener(shutterEventsListener, z);
    }

    @Override // com.android.camera.p002v2.app.AppController
    public void setOkCancelClickListener(AppController.OkCancelClickListener okCancelClickListener) {
        this.mCameraAppUI.setOkCancelClickListener(okCancelClickListener);
    }

    public boolean isShutterButtonEnabled(boolean z) {
        return this.mCameraAppUI.isShutterButtonEnabled(z);
    }

    public void performShutterButtonClick(boolean z) {
        this.mCameraAppUI.performShutterButtonClick(z);
    }

    public void startPreCaptureAnimation(boolean z) {
    }

    public void startPreCaptureAnimation() {
    }

    public void cancelPreCaptureAnimation() {
    }

    public void startPostCaptureAnimation() {
    }

    public void startPostCaptureAnimation(Bitmap bitmap) {
    }

    public void cancelPostCaptureAnimation() {
    }

    @Override // com.android.camera.p002v2.app.AppController
    public void notifyNewMedia(Uri uri) throws NumberFormatException {
        boolean z = false;
        LogHelper.m26i(TAG, "notifyNewMedia uri = " + uri + " mPaused = " + this.mPaused);
        if (this.mPaused) {
            return;
        }
        updateStorageSpaceAndHint();
        String type = this.mCameraActivity.getContentResolver().getType(uri);
        if (CameraUtil.isMimeTypeVideo(type)) {
            z = true;
        } else {
            CameraUtil.isMimeTypeImage(type);
        }
        if (type.endsWith("image/x-adobe-dng")) {
            LogHelper.m28w(TAG, "DNG type, no need update thumbnail");
            return;
        }
        addSecureAlbumItemIfNeeded(z, uri);
        if (this.mCameraAppUI != null) {
            this.mCameraAppUI.notifyMediaSaved(uri);
        }
    }

    @Override // com.android.camera.p002v2.app.AppController
    public void onCameraPicked(String str) throws Resources.NotFoundException {
        this.mCurrentModule.onBeforeCameraPicked(str);
        this.mCurrentCameraId = Integer.parseInt(str);
        initializePreferences(this.mCurrentCameraId, false);
        this.mCurrentModule.onCameraPicked(str);
    }

    @Override // com.android.camera.p002v2.app.AppController
    public void enableKeepScreenOn(boolean z) {
        if (this.mPaused) {
            return;
        }
        this.mKeepScreenOn = z;
        if (this.mKeepScreenOn) {
            this.mMainHandler.removeMessages(1);
            this.mCameraActivity.getWindow().addFlags(128);
        } else {
            keepScreenOnForAWhile();
        }
    }

    public OrientationManager getOrientationManager() {
        return this.mOrientationManager;
    }

    @Override // com.android.camera.p002v2.app.AppController
    public GestureManager getGestureManager() {
        if (this.mGestureManagerImpl == null) {
            this.mGestureManagerImpl = new GestureManagerImpl(this);
        }
        return this.mGestureManagerImpl;
    }

    @Override // com.android.camera.p002v2.app.AppController
    public PreferenceManager getPreferenceManager() {
        return this.mPreferenceManager;
    }

    @Override // com.android.camera.p002v2.app.AppController
    public CameraAppUI getCameraAppUI() {
        return this.mCameraAppUI;
    }

    @Override // com.android.camera.p002v2.app.AppController
    public void showErrorAndFinish(int i) {
        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() { // from class: com.android.camera.v2.CameraActivityBridge.2
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i2) {
                CameraActivityBridge.this.mCameraActivity.finish();
            }
        };
        if (this.mCameraActivity.isFinishing() || sAlertDialog != null || this.mPaused) {
            return;
        }
        sAlertDialog = new AlertDialog.Builder(this.mCameraActivity).setCancelable(false).setIconAttribute(android.R.attr.alertDialogIcon).setTitle("").setMessage(i).setNeutralButton(R.string.dialog_ok, onClickListener).show();
    }

    public void hideAlertDialog() {
        if (sAlertDialog != null) {
            this.mCameraActivity.runOnUiThread(new Runnable() { // from class: com.android.camera.v2.CameraActivityBridge.3
                @Override // java.lang.Runnable
                public void run() {
                    CameraActivityBridge.sAlertDialog.dismiss();
                    AlertDialog unused = CameraActivityBridge.sAlertDialog = null;
                }
            });
        }
    }

    @Override // com.android.camera.p002v2.app.AppController
    public AppControllerAdapter getAppControllerAdapter() {
        return this.mAppControllerAdapter;
    }

    @Override // com.android.camera.p002v2.app.AppController
    public void gotoGallery() {
        Intent intent;
        if (this.mCameraAppUI.getThumbnailMimeType().contains("image/")) {
            intent = new Intent("android.intent.action.VIEW");
        } else {
            intent = new Intent("com.android.camera.action.REVIEW");
        }
        intent.setDataAndType(this.mCameraAppUI.getThumbnailUri(), this.mCameraAppUI.getThumbnailMimeType());
        intent.putExtra("isCamera", true);
        if (this.mSecureCamera) {
            intent.putExtra("isSecureCamera", true);
            intent.putExtra("secureAlbum", getSecureAlbum());
            intent.putExtra("securePath", this.mPath);
            notifyGotoGallery();
        }
        if (2 == this.mActivityManager.getLockTaskModeState()) {
            intent.addFlags(134742016);
        }
        try {
            this.mCameraActivity.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            LogHelper.m25e(TAG, "[startGalleryActivity] Couldn't view ", e);
        }
    }

    @Override // com.android.camera.p002v2.app.AppController
    public void setResultExAndFinish(int i) {
        this.mCameraActivity.setResultExAndFinish(i);
    }

    @Override // com.android.camera.p002v2.app.AppController
    public void setResultExAndFinish(int i, Intent intent) {
        this.mCameraActivity.setResultExAndFinish(i, intent);
    }

    @Override // com.android.camera.p002v2.app.AppController
    public void setPlayButtonClickListener(AppController.PlayButtonClickListener playButtonClickListener) {
        this.mCameraAppUI.setPlayButtonClickListener(playButtonClickListener);
    }

    @Override // com.android.camera.p002v2.app.AppController
    public void setRetakeButtonClickListener(AppController.RetakeButtonClickListener retakeButtonClickListener) {
        this.mCameraAppUI.setRetakeButtonClickListener(retakeButtonClickListener);
    }

    @Override // com.android.camera.p002v2.app.AppController
    public LocationManager getLocationManager() {
        return this.mLocationManager;
    }

    @Override // com.android.camera.v2.app.OrientationManager.OnOrientationChangeListener
    public void onOrientationChanged(int i) {
        if (i != this.mLastRawOrientation) {
            LogHelper.m26i(TAG, "orientation changed (from:to) " + this.mLastRawOrientation + ":" + i);
        }
        if (i == -1) {
            return;
        }
        this.mLastRawOrientation = i;
        if (this.mCurrentModule != null) {
            this.mCurrentModule.onOrientationChanged(i);
        }
        if (this.mCameraAppUI != null) {
            this.mCameraAppUI.onOrientationChanged(i);
        }
    }

    private void keepScreenOnForAWhile() {
        if (this.mKeepScreenOn) {
            return;
        }
        this.mMainHandler.removeMessages(1);
        this.mCameraActivity.getWindow().addFlags(128);
        this.mMainHandler.sendEmptyMessageDelayed(1, SCREEN_DELAY_MS);
    }

    private void resetScreenOn() {
        this.mKeepScreenOn = false;
        this.mMainHandler.removeMessages(1);
        this.mCameraActivity.getWindow().clearFlags(128);
    }

    private void openModule(CameraModule cameraModule) {
        cameraModule.init(this.mCameraActivity, this.mSecureCamera, isCaptureIntent());
        cameraModule.resume();
    }

    private void closeModule(CameraModule cameraModule) {
        cameraModule.pause();
        cameraModule.destroy();
        this.mCameraAppUI.clearModuleUI();
    }

    private void initializePreferences(final int i, boolean z) throws Resources.NotFoundException {
        LogHelper.m26i(TAG, "[initializePreferences], cameraId:" + i + ", runnabled:" + z);
        if (!z) {
            this.mPreferenceManager.initializePreferences(R.xml.camera_preferences_v2, i);
            this.mMainHandler.sendEmptyMessage(0);
        } else {
            new Thread(new Runnable() { // from class: com.android.camera.v2.CameraActivityBridge.4
                @Override // java.lang.Runnable
                public void run() throws Resources.NotFoundException {
                    CameraActivityBridge.this.mPreferenceManager.initializePreferences(R.xml.camera_preferences_v2, i);
                    CameraActivityBridge.this.mMainHandler.sendEmptyMessage(0);
                }
            }, "initialize-preferences-thread").start();
        }
    }

    private class MainHandler extends Handler {
        final WeakReference<CameraActivity> mActivity;

        public MainHandler(CameraActivity cameraActivity, Looper looper) {
            super(looper);
            this.mActivity = new WeakReference<>(cameraActivity);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            if (this.mActivity.get() == null) {
            }
            switch (message.what) {
                case 0:
                    if (CameraActivityBridge.this.mCameraAppUI != null) {
                        CameraActivityBridge.this.mCameraAppUI.notifyPreferenceReady();
                        break;
                    }
                    break;
                case 1:
                    if (!CameraActivityBridge.this.mPaused) {
                        CameraActivityBridge.this.mCameraActivity.getWindow().clearFlags(128);
                        break;
                    }
                    break;
            }
        }
    }

    private void setModuleFromModeIndex(int i) {
        ModuleManager.ModuleAgent moduleAgent = this.mModuleManager.getModuleAgent(i);
        if (moduleAgent == null) {
            return;
        }
        this.mCurrentModuleIndex = moduleAgent.getModuleId();
        this.mCurrentModule = (CameraModule) moduleAgent.createModule(this);
    }

    private boolean isCaptureIntent() {
        if ("android.media.action.VIDEO_CAPTURE".equals(this.mCameraActivity.getIntent().getAction()) || "android.media.action.IMAGE_CAPTURE".equals(this.mCameraActivity.getIntent().getAction()) || ACTION_IMAGE_CAPTURE_SECURE.equals(this.mCameraActivity.getIntent().getAction())) {
            return true;
        }
        return false;
    }

    /* JADX WARN: Type inference failed for: r0v0, types: [com.android.camera.v2.CameraActivityBridge$5] */
    @Override // com.android.camera.p002v2.app.AppController
    public void updateStorageSpaceAndHint() {
        new AsyncTask<Void, Void, Integer>() { // from class: com.android.camera.v2.CameraActivityBridge.5
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.os.AsyncTask
            public Integer doInBackground(Void... voidArr) {
                Integer numValueOf;
                LogHelper.m26i(CameraActivityBridge.TAG, "updateStorageSpaceAndHint doInBackground");
                synchronized (CameraActivityBridge.this.mStorageSpaceLock) {
                    numValueOf = Integer.valueOf(CameraActivityBridge.this.mAppControllerAdapter.getServices().getStorageService().getStorageHintInfo());
                }
                return numValueOf;
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.os.AsyncTask
            public void onPostExecute(Integer num) {
                LogHelper.m23d(CameraActivityBridge.TAG, "[updateStorageSpaceAndHint], onPostExecute, bytes:" + num.intValue());
                CameraActivityBridge.this.updateStorageHint(num.intValue());
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
    }

    @Override // com.android.camera.ICameraActivityBridge
    public void onRequestLocationPermissionResult(String[] strArr, int[] iArr) {
        if (this.mCameraActivity.getPermissionManager().isCameraLocationPermissionsResultReady(strArr, iArr)) {
            this.mLocationManager.recordLocation(true);
        } else {
            this.mSettingAgent.doSettingChange("pref_camera_recordlocation_key", "off");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateStorageHint(int i) {
        LogHelper.m23d(TAG, "[updateStorageHint], info:" + i);
        String string = i != 1 ? this.mAppContext.getResources().getString(i) : null;
        if (string != null) {
            this.mCameraAppUI.showHint(string);
        } else {
            this.mCameraAppUI.hideHint();
        }
    }

    private void closeGpsLocation() {
        if (this.mLocationManager != null) {
            this.mLocationManager.recordLocation(false);
        }
    }

    private void notifyGotoGallery() {
        this.mIsGotoGallery = true;
    }

    protected ArrayList<String> getSecureAlbum() {
        return this.mSecureArray;
    }

    public void setPath(String str) {
    }

    private void addSecureAlbumItemIfNeeded(boolean z, Uri uri) throws NumberFormatException {
        if (this.mSecureCamera) {
            LogHelper.m26i(TAG, "addSecureAlbumItemIfNeeded uri = " + uri);
            this.mSecureArray.add(String.valueOf(Integer.parseInt(uri.getLastPathSegment())) + (z ? "+true" : "+false"));
        }
    }

    private void updateSecureThumbnail() {
        if (this.mIsLockScreen && (!this.mSecureArray.isEmpty())) {
            if (checkSecureAlbumLive()) {
                this.mNeedShowThumbnail = true;
            } else {
                this.mNeedShowThumbnail = false;
            }
            LogHelper.m26i(TAG, "mNeedShowThumbnail = " + this.mNeedShowThumbnail);
        }
    }

    private boolean isSecureUriLive(int i) throws Throwable {
        Cursor cursorQuery;
        try {
            cursorQuery = MediaStore.Images.Media.query(this.mCameraActivity.getContentResolver(), MediaStore.Files.getContentUri("external"), null, "_id=(" + i + ")", null, null);
            if (cursorQuery != null) {
                try {
                    LogHelper.m28w(TAG, "<isSecureUriLive> cursor " + cursorQuery.getCount());
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
            LogHelper.m23d(TAG, "<checkSecureAlbum> albumCount " + size);
            for (int i = 0; i < size; i++) {
                try {
                    String[] strArrSplit = this.mSecureArray.get(i).split("\\+");
                    int length = strArrSplit.length;
                    LogHelper.m23d(TAG, "<checkSecureAlbum> albumItemSize " + length);
                    if (length == 2) {
                        int i2 = Integer.parseInt(strArrSplit[0].trim());
                        LogHelper.m23d(TAG, "<checkSecureAlbum> secure item : id " + i2 + ", isVideo " + Boolean.parseBoolean(strArrSplit[1].trim()));
                        if (isSecureUriLive(i2)) {
                            return true;
                        }
                    } else {
                        continue;
                    }
                } catch (NullPointerException e) {
                    LogHelper.m24e(TAG, "<checkSecureAlbum> exception " + e);
                } catch (NumberFormatException e2) {
                    LogHelper.m24e(TAG, "<checkSecureAlbum> exception " + e2);
                } catch (PatternSyntaxException e3) {
                    LogHelper.m24e(TAG, "<checkSecureAlbum> exception " + e3);
                }
            }
        }
        return false;
    }
}
