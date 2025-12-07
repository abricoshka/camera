package com.android.camera;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.ActivityManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.MessageQueue;
import android.os.PowerManager;
import android.os.SystemClock;
import android.os.Vibrator;
import android.os.storage.StorageVolume;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FrameMetricsAggregator;
import android.util.Size;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.android.camera.CameraManager;
import com.android.camera.GestureDispatcher;
import com.android.camera.actor.CameraActor;
import com.android.camera.actor.PhotoActor;
import com.android.camera.actor.VideoActor;
import com.android.camera.bridge.CameraAppUiImpl;
import com.android.camera.bridge.CameraDeviceCtrl;
import com.android.camera.bridge.CameraDeviceManagerImpl;
import com.android.camera.bridge.FeatureConfigImpl;
import com.android.camera.bridge.FileSaverImpl;
import com.android.camera.bridge.SelfTimerManager;
import com.android.camera.externaldevice.ExternalDeviceManager;
import com.android.camera.externaldevice.IExternalDeviceCtrl;
import com.android.camera.manager.EffectViewManager;
import com.android.camera.manager.FrameManager;
import com.android.camera.manager.ModePicker;
import com.android.camera.manager.PickerManager;
import com.android.camera.manager.RecordingView;
import com.android.camera.manager.SettingManager;
import com.android.camera.manager.ViewManager;
import com.android.camera.p001ui.FrameView;
import com.android.camera.p001ui.MyRulerView;
import com.android.camera.p001ui.PreviewFrameLayout;
import com.android.camera.p001ui.PreviewSurfaceView;
import com.android.camera.p001ui.RotateLayout;
import com.android.camera.p001ui.ZZZFrameLayout;
import com.android.camera.p002v2.CameraActivityBridge;
import com.android.camera.permission.PermissionManager;
import com.mediatek.camera.ICameraMode;
import com.mediatek.camera.ISettingCtrl;
import com.mediatek.camera.ModuleManager;
import com.mediatek.camera.R;
import com.mediatek.camera.platform.ICameraAppUi;
import com.mediatek.camera.platform.IModuleCtrl;
import com.mediatek.camera.platform.ISelfTimeManager;
import com.mediatek.camera.setting.SettingConstants;
import com.mediatek.camera.setting.preference.ListPreference;
import com.mediatek.camera.util.CameraPerformanceTracker;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/* loaded from: classes.dex */
public class CameraActivity extends ActivityBase implements PreviewFrameLayout.OnSizeChangedListener, ActivityCompat.OnRequestPermissionsResultCallback {
    private ImageView ivCameraBg;
    private long lastHandlePreviewTime;
    private ActivityManager mActivityManager;
    private CameraActor mCameraActor;
    private CameraAppUiImpl mCameraAppUi;
    public CameraDeviceCtrl mCameraDeviceCtrl;
    private ZZZFrameLayout mCameraModeSwithView;
    private String mCropValue;
    private int mDelayOtherMessageTime;
    private CharSequence mDelayShowInfo;
    private int mDenoiseSuptSensorId;
    private int mDisplayRotation;
    private EffectViewManager mEffectViewManager;
    private FileSaver mFileSaver;
    private RotateLayout mFocusAreaIndicator;
    private boolean mForceFinishing;
    private FrameManager mFrameManager;
    private GestureDispatcher mGestureDispatcher;
    private GestureRecognizer mGestureRecognizer;
    private ISelfTimeManager mISelfTimeManager;
    private ISettingCtrl mISettingCtrl;
    private boolean mIsModeChanged;
    private boolean mIsStereoToVideoMode;
    private int mLimitedDuration;
    private int mLimitedResoltion;
    private long mLimitedSize;
    private HandlerThread mLivePhotoHandlerThread;
    private LocationManager mLocationManager;
    private ContentProviderClient mMediaProviderClient;
    private ModePicker mModePicker;
    private ModuleManager mModuleManager;
    private int mNumberOfCameras;
    private long mOnResumeTime;
    private MyOrientationEventListener mOrientationListener;
    private ExternalDeviceManager mOtherDeviceConectedManager;
    private PermissionManager mPermissionManager;
    private int mPickType;
    private PowerManager mPowerManager;
    private ComboPreferences mPreferences;
    private PreviewFrameLayout mPreviewFrameLayout;
    private ProcessYUVDataThread mProcessYUVDataThread;
    private boolean mQuickCapture;
    private RecordingView mRecordingView;
    private Uri mSaveUri;
    private int mStereoCaptureSuptSensorId;
    private LinearLayout mTvInfoView;
    private Vibrator mVibrator;
    private float mWallpaperAspectio;
    String pathAwaw;
    private MyRulerView rulerView;
    private View slideCameraView;
    private TextView slideText;
    private Window window;
    public static int mCurrentMode = 4;
    public static final int[] IPHONE_ZOOM_RATIO = {50, 100, 200, 300, 400, 500, 600, 700, 800};
    public static HashMap<String, Integer> mUserActionMap = new HashMap<>();
    private float[] colorArrayChrome = {1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.5f, 0.0f};
    private float[] colorArrayFade = {1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.8f, 0.0f};
    private float[] colorArrayProcess = {1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.3f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f};
    private float[] colorArrayTransfer = {1.0f, 0.0f, 0.0f, 0.0f, 40.0f, 0.0f, 1.0f, 0.0f, 0.0f, 40.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f};
    private ImageView effectChrome = null;
    private ImageView effectFade = null;
    private ImageView effectInstant = null;
    private View effectLayout = null;
    private ImageView effectMono = null;
    private ImageView effectNoir = null;
    private ImageView effectNone = null;
    private ImageView effectProcess = null;
    private ImageView effectTonal = null;
    private ImageView effectTransfer = null;
    private boolean mShowingColorSet = false;
    private int zoom_index = 0;
    private int mCameraState = 0;
    private int mPendingSwitchCameraId = -1;
    private int mOrientation = 0;
    private int mOrientationCompensation = 0;
    private boolean mNeedRestoreIfOpenFailed = false;
    protected boolean mIsStereoMode = false;
    private int mNextMode = 0;
    private int mPrevMode = 0;
    private boolean mIsBackPressed = false;
    private boolean mIsAPI2Inited = false;
    private boolean mIsCheckingLocationPermission = false;
    private Camera.PreviewCallback mPreviewCallback = new Camera.PreviewCallback() { // from class: com.android.camera.CameraActivity.1
        @Override // android.hardware.Camera.PreviewCallback
        public void onPreviewFrame(byte[] bArr, Camera camera) {
            if (CameraActivity.this.mShowingColorSet) {
                int i = camera.getParameters().getPreviewSize().width;
                int i2 = camera.getParameters().getPreviewSize().height;
                int i3 = CameraActivity.this.getCameraId() == 0 ? 90 : 270;
                if (CameraActivity.this.mProcessYUVDataThread == null || (CameraActivity.this.mProcessYUVDataThread != null && (!CameraActivity.this.mProcessYUVDataThread.isAlive()))) {
                    CameraActivity.this.mProcessYUVDataThread = CameraActivity.this.new ProcessYUVDataThread(bArr, i, i2, i3);
                    CameraActivity.this.mProcessYUVDataThread.start();
                    return;
                }
                return;
            }
            if (CameraActivity.this.mLivePhotoHandlerThread != null) {
                long jCurrentTimeMillis = System.currentTimeMillis();
                if (jCurrentTimeMillis - CameraActivity.this.lastHandlePreviewTime > 500) {
                    CameraActivity.this.lastHandlePreviewTime = jCurrentTimeMillis;
                    new Size(camera.getParameters().getPreviewSize().width, camera.getParameters().getPreviewSize().height);
                }
            }
        }
    };
    private final DecimalFormat FORMAT = new DecimalFormat("#.#");
    private Handler mMainHandler = new Handler() { // from class: com.android.camera.CameraActivity.2
        @Override // android.os.Handler
        public void handleMessage(Message message) {
            switch (message.what) {
                case 2:
                    CameraActivity.this.notifyParametersReady();
                    break;
                case 4:
                    if (Util.getDisplayRotation(CameraActivity.this) != CameraActivity.this.mDisplayRotation) {
                        CameraActivity.this.mCameraDeviceCtrl.setDisplayOrientation();
                        CameraActivity.this.mOrientation = -1;
                        CameraActivity.this.mCameraActor.onDisplayRotate();
                    }
                    if (SystemClock.uptimeMillis() - CameraActivity.this.mOnResumeTime < 5000) {
                        CameraActivity.this.mMainHandler.sendEmptyMessageDelayed(4, 100L);
                    }
                    CameraActivity.this.notifyOrientationChanged();
                    break;
                case 5:
                    CameraActivity.this.mCameraDeviceCtrl.switchCamera(message.arg1);
                    break;
                case FrameMetricsAggregator.DELAY_INDEX /* 7 */:
                    CameraActivity.this.getWindow().clearFlags(128);
                    break;
                case 12:
                    CameraActivity.this.mCameraDeviceCtrl.applyParameters(false);
                    break;
                case 16:
                    CameraActivity.this.mCameraAppUi.showText(CameraActivity.this.mDelayShowInfo);
                    CameraActivity.this.mCameraAppUi.showIndicator(CameraActivity.this.mDelayOtherMessageTime);
                    break;
                case 17:
                    CameraActivity.this.mModePicker.setEnabled(true);
                    break;
                case 20:
                    CameraActivity.this.slideCameraView.animate().alpha(0.0f).setDuration(800L).setListener(new AnimatorListenerAdapter() { // from class: com.android.camera.CameraActivity.2.1
                        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                        public void onAnimationEnd(Animator animator) {
                            CameraActivity.this.slideCameraView.setVisibility(8);
                        }
                    });
                    break;
                case 21:
                    if (CameraActivity.this.slideCameraView.getVisibility() != 0) {
                        CameraActivity.this.slideCameraView.setAlpha(0.0f);
                        Util.setOrientation(CameraActivity.this.slideText, CameraActivity.this.mOrientation, false);
                        CameraActivity.this.slideCameraView.setVisibility(0);
                        CameraActivity.this.slideCameraView.animate().alpha(1.0f).setDuration(800L).setListener(null);
                        break;
                    }
                    break;
                case 30:
                    if (CameraActivity.this.slideCameraView.getVisibility() != 0) {
                        CameraActivity.this.mMainHandler.removeMessages(20);
                        CameraActivity.this.mMainHandler.sendEmptyMessage(21);
                        CameraActivity.this.mMainHandler.sendEmptyMessageDelayed(20, 5000L);
                        break;
                    } else {
                        int zoomIndex = CameraActivity.this.getZoomIndex() + 1;
                        CameraActivity.this.mMainHandler.removeMessages(20);
                        CameraActivity.this.mMainHandler.sendEmptyMessage(21);
                        CameraActivity.this.mMainHandler.sendEmptyMessageDelayed(20, 5000L);
                        if (zoomIndex <= CameraActivity.this.getZoomMaxIndex()) {
                            CameraActivity.this.zoom_index = zoomIndex;
                            CameraActivity.this.rulerView.setSelectedValue(CameraActivity.IPHONE_ZOOM_RATIO[zoomIndex] / 10.0f, true);
                            CameraActivity.this.getPerformZoom(zoomIndex, true);
                            break;
                        }
                    }
                    break;
                case 31:
                    if (CameraActivity.this.slideCameraView.getVisibility() != 0) {
                        CameraActivity.this.mMainHandler.removeMessages(20);
                        CameraActivity.this.mMainHandler.sendEmptyMessage(21);
                        CameraActivity.this.mMainHandler.sendEmptyMessageDelayed(20, 5000L);
                        break;
                    } else {
                        int zoomIndex2 = CameraActivity.this.getZoomIndex() - 1;
                        CameraActivity.this.mMainHandler.removeMessages(20);
                        CameraActivity.this.mMainHandler.sendEmptyMessage(21);
                        CameraActivity.this.mMainHandler.sendEmptyMessageDelayed(20, 5000L);
                        if (zoomIndex2 >= 0) {
                            CameraActivity.this.zoom_index = zoomIndex2;
                            CameraActivity.this.rulerView.setSelectedValue(CameraActivity.IPHONE_ZOOM_RATIO[zoomIndex2] / 10.0f, true);
                            CameraActivity.this.getPerformZoom(zoomIndex2, true);
                            break;
                        }
                    }
                    break;
            }
        }
    };
    private List<OnPreferenceReadyListener> mPreferenceListeners = new CopyOnWriteArrayList();
    private List<OnParametersReadyListener> mParametersListeners = new CopyOnWriteArrayList();
    private List<Resumable> mResumables = new CopyOnWriteArrayList();
    private boolean mIsFromRestore = false;
    private int mOriCameraId = -1;
    private ModePicker.OnModeChangedListener mModeChangedListener = new ModePicker.OnModeChangedListener() { // from class: com.android.camera.CameraActivity.3
        @Override // com.android.camera.manager.ModePicker.OnModeChangedListener
        public void onModeChanged(int i) {
            boolean z = true;
            Log.m5d("CameraActivity", "onModeChanged(" + i + ") current mode = " + CameraActivity.this.mCameraActor.getMode() + ", state=" + CameraActivity.this.mCameraState);
            CameraActivity.this.mPrevMode = CameraActivity.this.mCameraActor.getMode();
            CameraActivity.this.mNextMode = i;
            int i2 = CameraActivity.this.mPrevMode;
            if (CameraActivity.this.mCameraActor.getMode() != i) {
                CameraActivity.this.mIsModeChanged = true;
                String cameraMode = CameraActivity.this.mISettingCtrl.getCameraMode(CameraActivity.this.getModeSettingKey(i2));
                String cameraMode2 = CameraActivity.this.mISettingCtrl.getCameraMode(CameraActivity.this.getModeSettingKey(i));
                if (cameraMode == null || cameraMode2 == null) {
                    Log.m8i("CameraActivity", "onModeChanged old or new Camera mode is null!!!");
                    return;
                }
                if (cameraMode.equals(cameraMode2) && 2 != Integer.parseInt(cameraMode2)) {
                    z = false;
                }
                Log.m5d("CameraActivity", "needRestart = " + z);
                if (z) {
                    CameraActivity.this.mCameraActor.stopPreview();
                }
                CameraActivity.this.releaseCameraActor(i2, i);
                CameraActivity.this.mModuleManager.setModeSettingValue(CameraActivity.this.mCameraActor.getCameraModeType(i2), "off");
                CameraActivity.this.judgeSensorSwitchStereoMode();
                if (CameraActivity.this.isPIPModeSwitch(i2, i)) {
                    if (!CameraActivity.this.isPIPMode(i2)) {
                        CameraActivity.this.mOriCameraId = CameraActivity.this.getCameraId();
                    }
                    CameraActivity.this.mCameraDeviceCtrl.closeCamera(false);
                }
                CameraActivity.this.releaseCameraSwitchStereoMode(i2, i);
                switch (i) {
                    case 0:
                        CameraActivity.this.mCameraActor = new PhotoActor(CameraActivity.this, CameraActivity.this.mModuleManager, i);
                        CameraActivity.this.mCameraAppUi.updateManagerIOS();
                        break;
                    case 1:
                    case 4:
                    default:
                        CameraActivity.this.mCameraActor = new PhotoActor(CameraActivity.this, CameraActivity.this.mModuleManager, i);
                        break;
                    case 2:
                        CameraActivity.this.mCameraActor = new PhotoActor(CameraActivity.this, CameraActivity.this.mModuleManager, i);
                        break;
                    case 3:
                        CameraActivity.this.mCameraActor = new PhotoActor(CameraActivity.this, CameraActivity.this.mModuleManager, i);
                        break;
                    case 5:
                        CameraActivity.this.mCameraActor = new PhotoActor(CameraActivity.this, CameraActivity.this.mModuleManager, i);
                        break;
                    case 6:
                        CameraActivity.this.mCameraActor = new PhotoActor(CameraActivity.this, CameraActivity.this.mModuleManager, i);
                        CameraActivity.this.mCameraAppUi.resetSettings();
                        break;
                    case FrameMetricsAggregator.DELAY_INDEX /* 7 */:
                        CameraActivity.this.mCameraActor = new PhotoActor(CameraActivity.this, CameraActivity.this.mModuleManager, i);
                        break;
                    case 8:
                        CameraActivity.this.mCameraActor = new VideoActor(CameraActivity.this, CameraActivity.this.mModuleManager, i);
                        break;
                    case 9:
                        CameraActivity.this.mCameraActor = new VideoActor(CameraActivity.this, CameraActivity.this.mModuleManager, i);
                        break;
                    case 10:
                        CameraActivity.this.mCameraActor = new VideoActor(CameraActivity.this, CameraActivity.this.mModuleManager, i);
                        break;
                }
                CameraActivity.this.mCameraDeviceCtrl.setCameraActor(CameraActivity.this.mCameraActor);
                if (CameraActivity.this.mPaused || CameraActivity.this.mCameraState == 4) {
                    CameraActivity.this.mIsModeChanged = false;
                    Log.m5d("CameraActivity", "onModeChanged return mPaused = " + CameraActivity.this.mPaused);
                    return;
                }
                if (CameraActivity.this.isStereoModeChanged(i2, i)) {
                    CameraActivity.this.doStereoModeChanged(false);
                    CameraActivity.this.mIsModeChanged = false;
                    Log.m8i("CameraActivity", "onModeChanged isStereoModeChanged return");
                } else if (CameraActivity.this.isPIPModeSwitch(i2, CameraActivity.this.mCameraActor.getMode())) {
                    CameraActivity.this.doPIPModeChanged(CameraActivity.this.mOriCameraId);
                    CameraActivity.this.mIsModeChanged = false;
                    Log.m8i("CameraActivity", "onModeChanged isPIPModeSwitch return");
                } else {
                    CameraActivity.this.notifyOrientationChanged();
                    CameraActivity.this.mCameraDeviceCtrl.onModeChanged(z);
                    CameraActivity.this.mIsModeChanged = false;
                }
            }
        }
    };
    private SettingManager.SettingListener mSettingListener = new SettingManager.SettingListener() { // from class: com.android.camera.CameraActivity.4
        @Override // com.android.camera.manager.SettingManager.SettingListener
        public void onSharedPreferenceChanged(ListPreference listPreference) throws Resources.NotFoundException, NumberFormatException {
            if (!CameraActivity.this.isCameraOpened()) {
                return;
            }
            if (listPreference != null) {
                String key = listPreference.getKey();
                String value = listPreference.getValue();
                if ("pref_af_mode_key".equals(key)) {
                    Log.m5d("CameraActivity", "[onSharedPreferenceChanged] change to " + value + " AF");
                    CameraActivity.this.getFocusManager().clearFocusUi();
                }
                CameraActivity.this.mISettingCtrl.onSettingChanged(key, value);
                CameraActivity.this.updateFakeNewPictureSizes(key);
            }
            CameraActivity.this.mCameraDeviceCtrl.applyParameters(false);
        }

        @Override // com.android.camera.manager.SettingManager.SettingListener
        public void onRestorePreferencesClicked() {
            CameraActivity.this.mCameraAppUi.showAlertDialog(null, CameraActivity.this.getString(R.string.confirm_restore_message), CameraActivity.this.getString(android.R.string.cancel), null, CameraActivity.this.getString(android.R.string.ok), new Runnable() { // from class: com.android.camera.CameraActivity.4.1
                @Override // java.lang.Runnable
                public void run() throws NumberFormatException {
                    Log.m5d("CameraActivity", "[onRestorePreferencesClicked.run]");
                    CameraActivity.this.mIsFromRestore = true;
                    CameraActivity.this.mCameraActor.onRestoreSettings();
                    CameraActivity.this.mCameraAppUi.collapseViewManager(true);
                    CameraActivity.this.mCameraAppUi.resetSettings();
                    SharedPreferences global = CameraActivity.this.mPreferences.getGlobal();
                    com.mediatek.camera.setting.SettingUtils.restorePreferences(global, CameraActivity.this.isNonePickIntent());
                    com.mediatek.camera.setting.SettingUtils.upgradeGlobalPreferences(global, CameraHolder.instance().getNumberOfCameras());
                    com.mediatek.camera.setting.SettingUtils.writePreferredCameraId(global, CameraActivity.this.mCameraDeviceCtrl.getCameraId());
                    int backCameraId = CameraHolder.instance().getBackCameraId();
                    com.mediatek.camera.setting.SettingUtils.restorePreferences(CameraActivity.this.getSharePreferences(backCameraId), CameraActivity.this.isNonePickIntent());
                    com.mediatek.camera.setting.SettingUtils.upgradeLocalPreferences(CameraActivity.this.getSharePreferences(backCameraId));
                    int frontCameraId = CameraHolder.instance().getFrontCameraId();
                    com.mediatek.camera.setting.SettingUtils.restorePreferences(CameraActivity.this.getSharePreferences(frontCameraId), CameraActivity.this.isNonePickIntent());
                    com.mediatek.camera.setting.SettingUtils.upgradeLocalPreferences(CameraActivity.this.getSharePreferences(frontCameraId));
                    com.mediatek.camera.setting.SettingUtils.initialCameraPictureSize(CameraActivity.this, CameraActivity.this.mCameraDeviceCtrl.getParametersExt(), CameraActivity.this.getSharePreferences());
                    CameraActivity.this.mISettingCtrl.restoreSetting(backCameraId);
                    CameraActivity.this.mISettingCtrl.restoreSetting(frontCameraId);
                    CameraActivity.this.mCameraAppUi.resetZoom();
                    int mode = CameraActivity.this.mCameraActor.getMode();
                    if (mode == 0 || (!CameraActivity.this.isNonePickIntent()) || 200 == mode || 100 == mode) {
                        if (8 == mode && (!CameraActivity.this.isNonePickIntent())) {
                            CameraActivity.this.mISettingCtrl.onSettingChanged("video_key", "on");
                        }
                        CameraActivity.this.mCameraDeviceCtrl.applyParameters(false);
                    } else {
                        CameraActivity.this.mModePicker.setModePreference(null);
                        CameraActivity.this.mModePicker.setCurrentMode(0);
                    }
                    CameraActivity.this.mIsFromRestore = false;
                }
            });
        }

        @Override // com.android.camera.manager.SettingManager.SettingListener
        public void onSettingContainerShowing(boolean z) {
            CameraActivity.this.mModuleManager.onSettingContainerShowing(z);
            Log.m5d("CameraActivity", "onSettingContainerShowing show=" + z);
            CameraActivity.this.window = CameraActivity.this.getWindow();
            if (z) {
                CameraActivity.this.window.clearFlags(134217728);
                CameraActivity.this.window.getDecorView().setSystemUiVisibility(16);
                CameraActivity.this.window.setNavigationBarColor(Color.parseColor("#EEEEEE"));
            } else {
                CameraActivity.this.window.getDecorView().setSystemUiVisibility(6148);
                CameraActivity.this.window.addFlags(134217728);
            }
        }

        @Override // com.android.camera.manager.SettingManager.SettingListener
        public void onStereoCameraPreferenceChanged(ListPreference listPreference, int i) {
            if (listPreference != null && listPreference.getKey().equals("pref_dual_camera_key")) {
                Log.m5d("CameraActivity", "onStereoCameraPreferenceChanged, type = " + i);
                if (CameraActivity.this.getCurrentMode() == 6) {
                    if (i == 3) {
                        CameraActivity.this.enableDualCameraExtras();
                    }
                    if (i == 4) {
                        CameraActivity.this.disableDualCameraExtras();
                    }
                    if (i == 1) {
                        CameraActivity.this.singleDualCameraExtras();
                        CameraActivity.this.mCameraDeviceCtrl.applyParameters(false);
                        return;
                    } else {
                        CameraActivity.this.mCameraDeviceCtrl.applyParameters(false);
                        return;
                    }
                }
                if (i == 3) {
                    CameraActivity.this.enableDualCameraExtras();
                }
                if (i == 4) {
                    CameraActivity.this.disableDualCameraExtras();
                }
                if (i == 1) {
                    CameraActivity.this.singleDualCameraExtras();
                    CameraActivity.this.doStereoModeChanged(false);
                } else if (i == 2) {
                    CameraActivity.this.singleDualCameraExtras();
                    CameraActivity.this.mCameraDeviceCtrl.applyParameters(false);
                } else {
                    CameraActivity.this.doStereoModeChanged(false);
                }
            }
        }
    };
    private List<OnOrientationListener> mOrientationListeners = new CopyOnWriteArrayList();
    private PickerManager.PickerListener mPickerListener = new PickerManager.PickerListener() { // from class: com.android.camera.CameraActivity.5
        @Override // com.android.camera.manager.PickerManager.PickerListener
        public boolean onCameraPicked(int i) {
            Log.m5d("CameraActivity", "onCameraPicked(" + i + ") mPaused=" + CameraActivity.this.mPaused + " mPendingSwitchCameraId=" + CameraActivity.this.mPendingSwitchCameraId);
            if (CameraActivity.this.mPaused || CameraActivity.this.mPendingSwitchCameraId != -1 || (!ModeChecker.getModePickerVisible(CameraActivity.this, i, CameraActivity.this.getCurrentMode())) || (!CameraActivity.this.mCameraDeviceCtrl.isCameraOpened())) {
                return false;
            }
            int frontCameraId = CameraHolder.instance().getFrontCameraId();
            if (!CameraActivity.this.mModuleManager.switchDevice() && CameraActivity.this.isDualCameraDeviceEnable() && frontCameraId != -1) {
                return false;
            }
            CameraActivity.this.mCameraAppUi.setViewState(ICameraAppUi.ViewState.VIEW_STATE_CAMERA_CLOSED);
            CameraActivity.this.mMainHandler.obtainMessage(5, i, 0).sendToTarget();
            CameraActivity.this.mPendingSwitchCameraId = i;
            return false;
        }

        @Override // com.android.camera.manager.PickerManager.PickerListener
        public boolean onHdrPicked(String str) {
            if (CameraActivity.this.mPaused || CameraActivity.this.mPendingSwitchCameraId != -1 || (!CameraActivity.this.mCameraDeviceCtrl.isCameraOpened())) {
                return false;
            }
            CameraActivity.this.mCameraActor.stopPreview();
            CameraActivity.this.mISettingCtrl.onSettingChanged("pref_hdr_key", str);
            if ("on".equals(str)) {
                CameraActivity.this.mCameraAppUi.showInfo(CameraActivity.this.getString(R.string.hdr_guide_capture), 5000);
            }
            CameraActivity.this.mCameraDeviceCtrl.applyParameters(true);
            return true;
        }

        @Override // com.android.camera.manager.PickerManager.PickerListener
        public boolean onFlashPicked(String str) {
            if (CameraActivity.this.mPaused || CameraActivity.this.mPendingSwitchCameraId != -1 || (!CameraActivity.this.mCameraDeviceCtrl.isCameraOpened())) {
                return false;
            }
            CameraActivity.this.mISettingCtrl.onSettingChanged("pref_camera_flashmode_key", str);
            CameraActivity.this.mCameraDeviceCtrl.applyParameters(false);
            return true;
        }

        @Override // com.android.camera.manager.PickerManager.PickerListener
        public boolean onSelfTimerPicked(String str) {
            CameraActivity.this.mISettingCtrl.onSettingChanged("pref_camera_self_timer_key", str);
            CameraActivity.this.mCameraDeviceCtrl.applyParameters(false);
            return true;
        }

        @Override // com.android.camera.manager.PickerManager.PickerListener
        public boolean onEffectPicked(String str) {
            if (CameraActivity.this.mPaused || CameraActivity.this.mPendingSwitchCameraId != -1 || (!CameraActivity.this.mCameraDeviceCtrl.isCameraOpened())) {
                return false;
            }
            CameraActivity.this.mISettingCtrl.onSettingChanged("pref_camera_coloreffect_key", str);
            CameraActivity.this.mCameraDeviceCtrl.applyParameters(false);
            return true;
        }

        @Override // com.android.camera.manager.PickerManager.PickerListener
        public boolean onLiveFocusPicked(String str) {
            if (CameraActivity.this.mPaused || CameraActivity.this.mPendingSwitchCameraId != -1 || (!CameraActivity.this.mCameraDeviceCtrl.isCameraOpened())) {
                return false;
            }
            CameraActivity.this.mISettingCtrl.onSettingChanged("pref_live_focus_key", str);
            CameraActivity.this.mCameraDeviceCtrl.applyParameters(false);
            return true;
        }
    };
    public boolean mCanShowVideoShare = true;
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() { // from class: com.android.camera.CameraActivity.6
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            Log.m5d("CameraActivity", "mReceiver.onReceive(" + intent + ")");
            String action = intent.getAction();
            if (action == null) {
                Log.m5d("CameraActivity", "[mReceiver.onReceive] action is null");
                return;
            }
            if (action.equals("android.intent.action.MEDIA_EJECT")) {
                if (CameraActivity.this.isSameStorage(intent)) {
                    Storage.setStorageReady(false);
                    CameraActivity.this.mCameraActor.onMediaEject();
                    return;
                }
                return;
            }
            if (action.equals("android.intent.action.MEDIA_UNMOUNTED")) {
                if (CameraActivity.this.isSameStorage(intent)) {
                    String internalVolumePath = Storage.getInternalVolumePath();
                    if (internalVolumePath != null && !Storage.updateDirectory(internalVolumePath)) {
                        CameraActivity.this.setPath(Storage.getCameraScreenNailPath());
                    }
                } else if (!FeatureSwitcher.is2SdCardSwapSupport()) {
                    CameraActivity.this.updateStorageDirectory();
                }
                CameraActivity.this.mCameraAppUi.clearRemainAvaliableSpace();
                CameraActivity.this.mCameraAppUi.showRemainHint();
                CameraActivity.this.mCameraAppUi.forceThumbnailUpdate();
                return;
            }
            if (action.equals("android.intent.action.MEDIA_MOUNTED")) {
                CameraActivity.this.updateStorageDirectory();
                if (CameraActivity.this.isSameStorage(intent)) {
                    Storage.setStorageReady(true);
                    CameraActivity.this.mCameraAppUi.clearRemainAvaliableSpace();
                    CameraActivity.this.mCameraAppUi.showRemainHint();
                    return;
                }
                return;
            }
            if (action.equals("android.intent.action.MEDIA_CHECKING")) {
                if (CameraActivity.this.isSameStorage(intent)) {
                    CameraActivity.this.mCameraAppUi.clearRemainAvaliableSpace();
                    CameraActivity.this.mCameraAppUi.showRemainHint();
                    return;
                }
                return;
            }
            if (action.equals("android.intent.action.MEDIA_SCANNER_STARTED")) {
                if (!FeatureSwitcher.is2SdCardSwapSupport()) {
                    CameraActivity.this.updateStorageDirectory();
                }
                if (CameraActivity.this.isSameStorage(intent.getData())) {
                    CameraActivity.this.mCameraAppUi.showToast(R.string.wait);
                    return;
                }
                return;
            }
            if (action.equals("android.intent.action.MEDIA_SCANNER_FINISHED") && CameraActivity.this.isSameStorage(intent.getData())) {
                CameraActivity.this.mCameraAppUi.clearRemainAvaliableSpace();
                CameraActivity.this.mCameraAppUi.showRemainHint();
                CameraActivity.this.mCameraAppUi.forceThumbnailUpdate();
            }
        }
    };
    private IExternalDeviceCtrl.Listener mListener = new IExternalDeviceCtrl.Listener() { // from class: com.android.camera.CameraActivity.7
        @Override // com.android.camera.externaldevice.IExternalDeviceCtrl.Listener
        public void onStateChanged(boolean z) {
            Log.m5d("CameraActivity", "[onStateChanged] enable = " + z);
            CameraActivity.this.mModuleManager.setVideoRecorderEnable(!z);
        }
    };
    private int lastAwrw = 0;
    private int retory_count = 0;
    private Handler handler = null;
    private Runnable pollingRunnable = new Runnable() { // from class: com.android.camera.CameraActivity.8
        @Override // java.lang.Runnable
        public void run() {
            CameraActivity.this.checkAwraStat(CameraActivity.this.getAwrw());
            CameraActivity.this.handler.postDelayed(this, 30L);
        }
    };

    public interface OnLongPressListener {
        void onLongPress(View view, int i, int i2);
    }

    public interface OnOrientationListener {
        void onOrientationChanged(int i);
    }

    public interface OnParametersReadyListener {
        void onCameraParameterReady();
    }

    public interface OnPreferenceReadyListener {
        void onPreferenceReady();
    }

    public interface OnSingleTapUpListener {
        void onSingleTapUp(View view, int i, int i2);
    }

    public interface Resumable {
        void begin();

        void finish();

        void pause();

        void resume();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getAwrw() {
        return 0;
    }

    private void writeAot() {
    }

    static {
        mUserActionMap.put("eq", 0);
        mUserActionMap.put("ge", 0);
        mUserActionMap.put("le", 0);
    }

    public int getZoomIndex() {
        return this.zoom_index;
    }

    public int getZoomMaxIndex() {
        return IPHONE_ZOOM_RATIO.length - 1;
    }

    @Override // com.android.camera.ui.PreviewFrameLayout.OnSizeChangedListener
    public void onSizeChanged(int i, int i2) {
        if (FeatureSwitcher.isApi2Enable(this)) {
            return;
        }
        this.mCameraDeviceCtrl.onSizeChanged(i, i2);
    }

    public void setPreViewFullShow() {
        this.mCameraDeviceCtrl.setPreviewFull();
    }

    protected ICameraActivityBridge getCameraActivityBridge() {
        if (this.mCameraActivityBridge == null) {
            this.mCameraActivityBridge = CameraActivityBridgeFactory.getCameraActivityBridge(this);
        }
        return this.mCameraActivityBridge;
    }

    @Override // com.android.camera.ActivityBase, android.app.Activity
    public void onCreate(Bundle bundle) {
        Log.m5d("CameraActivity", "onCreate()");
        CameraPerformanceTracker.onEvent("CameraActivity", "CameraOnCreate", true);
        super.onCreate(bundle);
        mCurrentMode = 4;
        this.mPermissionManager = new PermissionManager(this);
        this.window = getWindow();
        this.window.addFlags(67108864);
        this.window.addFlags(134217728);
        if (FeatureSwitcher.isApi2Enable(this)) {
            getCameraActivityBridge().onCreate(bundle);
            return;
        }
        this.mActivityManager = (ActivityManager) getSystemService("activity");
        this.mPreferences = new ComboPreferences(this, isSecureCamera());
        Storage.setContext(this);
        com.mediatek.camera.setting.SettingUtils.resetCameraId(this.mPreferences.getGlobal());
        this.mCameraDeviceCtrl = new CameraDeviceCtrl(this, this.mPreferences);
        if (this.mPermissionManager.requestCameraLaunchPermissions()) {
            this.mCameraDeviceCtrl.openCamera();
        }
        ModuleCtrlImpl moduleCtrlImpl = new ModuleCtrlImpl(this);
        CameraPerformanceTracker.onEvent("CameraActivity", "InitViewManager", true);
        this.mCameraAppUi = new CameraAppUiImpl(this);
        this.mCameraAppUi.createCommonView();
        initializeCommonManagers();
        this.mCameraAppUi.initializeCommonView();
        CameraPerformanceTracker.onEvent("CameraActivity", "InitViewManager", false);
        CameraPerformanceTracker.onEvent("CameraActivity", "CameraViewOperation", true);
        setContentView(R.layout.camera);
        ((ViewGroup) findViewById(R.id.camera_app_root)).bringToFront();
        CameraPerformanceTracker.onEvent("CameraActivity", "CameraViewOperation", false);
        this.mCameraDeviceCtrl.attachSurfaceViewLayout();
        this.mCameraDeviceCtrl.setCameraAppUi(this.mCameraAppUi);
        FileSaverImpl fileSaverImpl = new FileSaverImpl(this.mFileSaver);
        FeatureConfigImpl featureConfigImpl = new FeatureConfigImpl();
        CameraDeviceManagerImpl cameraDeviceManagerImpl = new CameraDeviceManagerImpl(this, this.mCameraDeviceCtrl);
        this.mISelfTimeManager = new SelfTimerManager(this, this.mCameraAppUi, this);
        parseIntent();
        CameraPerformanceTracker.onEvent("CameraActivity", "CameraCreateModule", true);
        this.mModuleManager = new ModuleManager(this, fileSaverImpl, this.mCameraAppUi, featureConfigImpl, cameraDeviceManagerImpl, moduleCtrlImpl, this.mISelfTimeManager);
        this.mISettingCtrl = this.mModuleManager.getSettingController();
        android.util.Log.d("xiaoyao", "xxxxx                 aaa  " + this.mISettingCtrl);
        this.mCameraAppUi.setSettingCtrl(this.mISettingCtrl);
        if (isVideoCaptureIntent() || isVideoWallPaperIntent()) {
            this.mCameraActor = new VideoActor(this, this.mModuleManager, 8);
            mCurrentMode = 3;
            Util.mCurrentShutterMode = 3;
        } else {
            this.mCameraActor = new PhotoActor(this, this.mModuleManager, 0);
            mCurrentMode = 4;
            Util.mCurrentShutterMode = 4;
        }
        CameraPerformanceTracker.onEvent("CameraActivity", "CameraCreateModule", false);
        this.mCameraDeviceCtrl.setModuleManager(this.mModuleManager);
        this.mCameraDeviceCtrl.setSettingCtrl(this.mISettingCtrl);
        this.mCameraDeviceCtrl.setCameraActor(this.mCameraActor);
        this.mFileSaver.bindSaverService();
        this.mOtherDeviceConectedManager = new ExternalDeviceManager(this);
        this.mOtherDeviceConectedManager.onCreate();
        this.mOtherDeviceConectedManager.addListener(this.mListener);
        if (isNonePickIntent()) {
            com.mediatek.camera.setting.SettingUtils.updateSettingCaptureModePreferences(this.mPreferences.getLocal());
        } else {
            View viewFindViewById = findViewById(R.id.camera_cover);
            if (viewFindViewById != null) {
                viewFindViewById.setVisibility(0);
            }
            View viewFindViewById2 = findViewById(R.id.intentcover);
            if (viewFindViewById2 != null) {
                viewFindViewById2.setVisibility(0);
            }
        }
        this.ivCameraBg = (ImageView) findViewById(R.id.iv_camera_bg);
        com.mediatek.camera.setting.SettingUtils.upgradeGlobalPreferences(this.mPreferences.getGlobal(), CameraHolder.instance().getNumberOfCameras());
        initializeStereo3DMode();
        this.mVibrator = (Vibrator) getSystemService("vibrator");
        this.mDisplayRotation = Util.getDisplayRotation(this);
        Storage.initializeStorageState();
        CameraPerformanceTracker.onEvent("CameraActivity", "CreateScreenNail", true);
        CameraPerformanceTracker.onEvent("CameraActivity", "CreateScreenNail", false);
        initializeForOpeningProcess();
        initializeAfterPreview();
        CameraPerformanceTracker.onEvent("CameraActivity", "CameraOnCreate", false);
        this.window = getWindow();
        this.window.addFlags(67108864);
        this.window.addFlags(134217728);
        this.mCameraModeSwithView = (ZZZFrameLayout) findViewById(R.id.fl_cameramode_swith);
        this.slideCameraView = findViewById(R.id.slide_camer_layers);
        this.slideText = (TextView) findViewById(R.id.slide_text);
        this.slideText.setTextColor(getResources().getColor(R.color.color_selected));
        this.rulerView = (MyRulerView) findViewById(R.id.height_ruler);
        this.rulerView.setOnValueChangeListener(new MyRulerView.OnValueChangeListener() { // from class: com.android.camera.CameraActivity.9
            @Override // com.android.camera.ui.MyRulerView.OnValueChangeListener
            public void onChange(MyRulerView myRulerView, float f) {
                int zoom = CameraActivity.this.getParameters().getZoom();
                if (zoom > CameraActivity.this.getZoomMaxIndex()) {
                    zoom = CameraActivity.this.getZoomMaxIndex();
                }
                CameraActivity.this.slideText.setText(CameraActivity.this.FORMAT.format(CameraActivity.IPHONE_ZOOM_RATIO[zoom >= 1 ? zoom : 1] / 100.0f) + "x");
            }
        });
        this.rulerView.setSelectedValue(10.0f, false);
        HandlerThread handlerThread = new HandlerThread("MainCamHandler");
        handlerThread.start();
        this.handler = new Handler(handlerThread.getLooper());
    }

    public void updateFlash(String str) {
        ImageView imageView = (ImageView) findViewById(R.id.iv_selectflash);
        TextView textView = (TextView) findViewById(R.id.tv_flash);
        if ("auto".equals(str)) {
            if (imageView != null) {
                imageView.setImageResource(R.drawable.zzz_camera_entry_ef_flash_auto);
            }
            if (textView != null) {
                textView.setCompoundDrawablesRelativeWithIntrinsicBounds((Drawable) null, getResources().getDrawable(R.drawable.zzz_new_camera_flash_auto), (Drawable) null, (Drawable) null);
                return;
            }
            return;
        }
        if ("off".equals(str)) {
            if (imageView != null) {
                imageView.setImageResource(R.drawable.zzz_camera_entry_ef_flash_off);
            }
            if (textView != null) {
                textView.setCompoundDrawablesRelativeWithIntrinsicBounds((Drawable) null, getResources().getDrawable(R.drawable.zzz_new_camera_flash_off), (Drawable) null, (Drawable) null);
                return;
            }
            return;
        }
        if ("on".equals(str)) {
            if (imageView != null) {
                imageView.setImageResource(R.drawable.zzz_camera_entry_ef_flash_on);
            }
            if (textView != null) {
                textView.setCompoundDrawablesRelativeWithIntrinsicBounds((Drawable) null, getResources().getDrawable(R.drawable.zzz_new_camera_flash_on), (Drawable) null, (Drawable) null);
            }
        }
    }

    public void updateCameraModeSwithView(boolean z) {
        if (z) {
            new Thread(new $Lambda$ejefvXSovaCP_QlxbPtK_ZAvf7o((byte) 2, this)).start();
        } else {
            new Thread(new $Lambda$ejefvXSovaCP_QlxbPtK_ZAvf7o((byte) 3, this)).start();
        }
    }

    /* renamed from: lambda$-com_android_camera_CameraActivity_21447, reason: not valid java name */
    /* synthetic */ void m160lambda$com_android_camera_CameraActivity_21447() {
        runOnUiThread(new $Lambda$ejefvXSovaCP_QlxbPtK_ZAvf7o((byte) 0, this));
    }

    /* renamed from: lambda$-com_android_camera_CameraActivity_21477, reason: not valid java name */
    /* synthetic */ void m161lambda$com_android_camera_CameraActivity_21477() {
        this.mCameraModeSwithView.setVisibility(0);
    }

    /* renamed from: lambda$-com_android_camera_CameraActivity_21576, reason: not valid java name */
    /* synthetic */ void m162lambda$com_android_camera_CameraActivity_21576() {
        runOnUiThread(new $Lambda$ejefvXSovaCP_QlxbPtK_ZAvf7o((byte) 1, this));
    }

    /* renamed from: lambda$-com_android_camera_CameraActivity_21605, reason: not valid java name */
    /* synthetic */ void m163lambda$com_android_camera_CameraActivity_21605() {
        this.mCameraModeSwithView.setVisibility(8);
    }

    @Override // android.app.Activity
    protected void onRestart() {
        super.onRestart();
        if (FeatureSwitcher.isApi2Enable(this)) {
            getCameraActivityBridge().onRestart();
            return;
        }
        if (isNonePickIntent() && isMountPointChanged()) {
            finish();
            this.mForceFinishing = true;
            startActivity(getIntent());
        } else if (isMountPointChanged()) {
            Storage.updateDefaultDirectory();
        }
        Log.m5d("CameraActivity", "onRestart() mForceFinishing=" + this.mForceFinishing);
    }

    @Override // com.android.camera.ActivityBase, android.app.Activity
    protected void onResume() {
        Log.m5d("CameraActivity", "onResume() mForceFinishing=" + this.mForceFinishing);
        keepMediaProviderInstance();
        CameraPerformanceTracker.onEvent("CameraActivity", "CameraOnResume", true);
        super.onResume();
        writeAot();
        if (FeatureSwitcher.isApi2Enable(this)) {
            if (this.mPermissionManager.requestCameraLaunchPermissions()) {
                this.mIsAPI2Inited = true;
                getCameraActivityBridge().onResume();
                return;
            }
            return;
        }
        parseIntent();
        this.mOtherDeviceConectedManager.onResume();
        if (this.mForceFinishing || this.mCameraDeviceCtrl.isOpenCameraFail()) {
            this.mNeedRestoreIfOpenFailed = false;
            return;
        }
        ((ViewGroup) findViewById(R.id.camera_app_root)).setSystemUiVisibility(6148);
        CameraPerformanceTracker.onEvent("CameraActivity", "resumeNotify", true);
        if (this.mModuleManager != null) {
            this.mModuleManager.resume();
        }
        if (this.mPermissionManager.requestCameraLaunchPermissions()) {
            this.mCameraDeviceCtrl.onResume();
        }
        CameraPerformanceTracker.onEvent("CameraActivity", "resumeNotify", false);
        CameraPerformanceTracker.onEvent("CameraActivity", "updateAppView", true);
        doOnResume();
        CameraPerformanceTracker.onEvent("CameraActivity", "updateAppView", false);
        this.mCameraAppUi.forceThumbnailUpdate();
        this.mNeedRestoreIfOpenFailed = true;
        Util.enterCameraPQMode();
        if (!isCameraOpened()) {
            this.mCameraAppUi.setViewState(ICameraAppUi.ViewState.VIEW_STATE_CAMERA_CLOSED);
            Log.m5d("CameraActivity", "[onResume],camera device is opening,set view state.");
        }
        CameraPerformanceTracker.onEvent("CameraActivity", "CameraOnResume", false);
        if (Settings.System.getInt(getContentResolver(), "camera_open_grid", 0) == 1) {
            this.ivCameraBg.setVisibility(0);
        } else {
            this.ivCameraBg.setVisibility(8);
        }
    }

    public PermissionManager getPermissionManager() {
        return this.mPermissionManager;
    }

    @Override // android.app.Activity, android.support.v4.app.ActivityCompat.OnRequestPermissionsResultCallback
    public void onRequestPermissionsResult(int i, String[] strArr, int[] iArr) {
        this.mIsCheckingLocationPermission = false;
        if (iArr == null || iArr.length <= 0) {
            return;
        }
        if (this.mPermissionManager.getCameraLaunchPermissionRequestCode() == i) {
            if (this.mPermissionManager.isCameraLaunchPermissionsResultReady(strArr, iArr)) {
                if (FeatureSwitcher.isApi2Enable(this)) {
                    this.mIsAPI2Inited = true;
                    return;
                }
                return;
            } else {
                Toast.makeText(this, R.string.denied_required_permission, 1).show();
                finish();
                return;
            }
        }
        if (this.mPermissionManager.getCameraLocationPermissionRequestCode() == i) {
            if (FeatureSwitcher.isApi2Enable(this)) {
                getCameraActivityBridge().onRequestLocationPermissionResult(strArr, iArr);
            } else {
                onRequestLocationPermissionResult(strArr, iArr);
            }
            if (!this.mPermissionManager.isCameraLocationPermissionsResultReady(strArr, iArr) && !ActivityCompat.shouldShowRequestPermissionRationale(this, strArr[0])) {
                Toast.makeText(this, R.string.denied_required_permission, 1).show();
                return;
            }
            return;
        }
        super.onRequestPermissionsResult(i, strArr, iArr);
    }

    @Override // com.android.camera.ActivityBase, android.app.Activity
    protected void onPause() {
        Log.m5d("CameraActivity", "onPause() mForceFinishing=" + this.mForceFinishing);
        if (this.mMediaProviderClient != null) {
            Log.m5d("CameraActivity", "onPause() release mMediaProviderClient");
            this.mMediaProviderClient.release();
            this.mMediaProviderClient = null;
        }
        CameraPerformanceTracker.onEvent("CameraActivity", "CameraOnPause", true);
        super.onPause();
        if (this.handler != null) {
            this.handler.removeCallbacks(this.pollingRunnable);
        }
        if (this.mMainHandler != null) {
            this.mMainHandler.removeMessages(20);
            this.mMainHandler.removeMessages(21);
            this.mMainHandler.removeMessages(30);
            this.mMainHandler.removeMessages(31);
        }
        if (FeatureSwitcher.isApi2Enable(this)) {
            if (this.mIsAPI2Inited) {
                getCameraActivityBridge().onPause();
                this.mIsAPI2Inited = false;
                return;
            }
            return;
        }
        if (this.mPendingSwitchCameraId != -1) {
            this.mPendingSwitchCameraId = -1;
        }
        if (this.mForceFinishing || this.mCameraDeviceCtrl.isOpenCameraFail()) {
            this.mCameraDeviceCtrl.onPause();
            Log.m5d("CameraActivity", "onPause(),release surface texture.");
            Util.exitCameraPQMode();
            this.mOtherDeviceConectedManager.onPause();
            return;
        }
        this.mCameraDeviceCtrl.onPause();
        this.mNeedRestoreIfOpenFailed = false;
        this.mCameraAppUi.collapseViewManager(true);
        this.mModuleManager.pause();
        keepCameraForSecure();
        clearFocusAndFace();
        uninstallIntentFilter();
        callResumablePause();
        this.mOrientationListener.disable();
        this.mLocationManager.recordLocation(false);
        this.mOnResumeTime = 0L;
        this.mMainHandler.removeCallbacksAndMessages(null);
        resetScreenOn();
        Util.exitCameraPQMode();
        this.mOtherDeviceConectedManager.onPause();
        CameraPerformanceTracker.onEvent("CameraActivity", "CameraOnPause", false);
    }

    @Override // com.android.camera.ActivityBase, android.app.Activity
    protected void onDestroy() {
        Log.m5d("CameraActivity", "onDestroy() isChangingConfigurations()=" + isChangingConfigurations() + ", mForceFinishing=" + this.mForceFinishing);
        CameraPerformanceTracker.onEvent("CameraActivity", "CameraOnDestroy", true);
        super.onDestroy();
        if (FeatureSwitcher.isApi2Enable(this)) {
            getCameraActivityBridge().onDestroy();
            CameraActivityBridgeFactory.destroyCameraActivityBridge(this);
            return;
        }
        this.mNextMode = -1;
        callResumableFinish();
        if (this.mCameraActor != null) {
            this.mCameraActor.release();
        }
        if (this.mFileSaver != null) {
            this.mFileSaver.unBindSaverService();
        }
        if (this.mISelfTimeManager != null) {
            ((SelfTimerManager) this.mISelfTimeManager).releaseSelfTimer();
            this.mISelfTimeManager = null;
        }
        this.mModuleManager.destory();
        this.mCameraDeviceCtrl.onDestory();
        if (this.mForceFinishing) {
            return;
        }
        if (this.mIsBackPressed) {
            clearUserSettings();
            this.mIsBackPressed = false;
        }
        CameraPerformanceTracker.onEvent("CameraActivity", "CameraOnDestroy", false);
    }

    @Override // android.app.Activity
    protected void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (FeatureSwitcher.isApi2Enable(this)) {
            getCameraActivityBridge().onActivityResult(i, i2, intent);
        } else {
            this.mCameraActor.onActivityResult(i, i2, intent);
        }
    }

    @Override // android.app.Activity
    public void onBackPressed() {
        Log.m5d("CameraActivity", "onBackPressed()");
        if (FeatureSwitcher.isApi2Enable(this)) {
            if (getCameraActivityBridge().onBackPressed()) {
                super.onBackPressed();
            }
        } else {
            if (this.mPaused || this.mForceFinishing) {
                return;
            }
            if (this.mCameraDeviceCtrl.isOpenCameraFail()) {
                super.onBackPressed();
                return;
            }
            if (!this.mCameraAppUi.collapseViewManager(false) && (!this.mCameraActor.onBackPressed())) {
                super.onBackPressed();
            }
            this.mIsBackPressed = true;
        }
    }

    @Override // android.app.Activity, android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        if (FeatureSwitcher.isApi2Enable(this)) {
            getCameraActivityBridge().onConfigurationChanged(configuration);
            return;
        }
        Log.m5d("CameraActivity", "mCameraState = " + this.mCameraAppUi.getViewState() + ",isSettingsView = " + (this.mCameraAppUi.getViewState() == ICameraAppUi.ViewState.VIEW_STATE_SETTING));
        clearFocusAndFace();
        ViewGroup viewGroup = (ViewGroup) findViewById(R.id.camera_app_root);
        viewGroup.removeAllViews();
        getLayoutInflater().inflate(R.layout.preview_frame, viewGroup, true);
        getLayoutInflater().inflate(R.layout.view_layers, viewGroup, true);
        this.mCameraAppUi.removeAllView();
        setOrientation(false, -1);
        this.mCameraDeviceCtrl.setDisplayOrientation();
        initializeForOpeningProcess();
        this.mCameraDeviceCtrl.setPreviewFrameLayoutAspectRatio();
        updateFocusAndFace();
        this.mCameraAppUi.onConfigurationChanged();
        notifyOrientationChanged();
        this.mModuleManager.configurationChanged();
    }

    public void onSingleTapUp(View view, int i, int i2) {
        if (FeatureSwitcher.isApi2Enable(this)) {
            return;
        }
        Log.m5d("CameraActivity", "onSingleTapUp(" + view + ", " + i + ", " + i2 + ")");
        boolean zIsShowing = this.mCameraAppUi.getCameraView(ICameraAppUi.CommonUiType.ROTATE_PROGRESS).isShowing();
        if (!this.mCameraAppUi.getCameraView(ICameraAppUi.CommonUiType.ROTATE_DIALOG).isShowing() && (!zIsShowing)) {
            if (FeatureSwitcher.isSubSettingEnabled()) {
                this.mCameraAppUi.collapseSubSetting(true);
            }
            if (isCancelSingleTapUp()) {
                Log.m8i("CameraActivity", "will cancel this singleTapUp event");
            } else if (!this.mCameraAppUi.collapseSetting(true) && this.mCameraActor.getonSingleTapUpListener() != null) {
                this.mCameraActor.getonSingleTapUpListener().onSingleTapUp(view, i, i2);
            }
        }
    }

    public void onLongPress(View view, int i, int i2) {
        if (FeatureSwitcher.isApi2Enable(this)) {
            return;
        }
        Log.m5d("CameraActivity", "OnLongPress(" + view + ", " + i + ", " + i2 + "),mCurrentViewState = " + this.mCameraAppUi.getViewState());
        if (this.mCameraAppUi.getViewState() == ICameraAppUi.ViewState.VIEW_STATE_LOMOEFFECT_SETTING) {
            return;
        }
        boolean zIsShowing = this.mCameraAppUi.getCameraView(ICameraAppUi.CommonUiType.ROTATE_PROGRESS).isShowing();
        if (!this.mCameraAppUi.getCameraView(ICameraAppUi.CommonUiType.ROTATE_DIALOG).isShowing() && (!zIsShowing) && !this.mCameraAppUi.collapseSetting(true) && this.mCameraActor.getonLongPressListener() != null) {
            this.mCameraActor.getonLongPressListener().onLongPress(view, i, i2);
        }
    }

    public void onSingleTapUpBorder(View view, int i, int i2) {
        if (FeatureSwitcher.isApi2Enable(this)) {
            return;
        }
        boolean zIsShowing = this.mCameraAppUi.getCameraView(ICameraAppUi.CommonUiType.ROTATE_PROGRESS).isShowing();
        if (!this.mCameraAppUi.getCameraView(ICameraAppUi.CommonUiType.ROTATE_DIALOG).isShowing() && (!zIsShowing)) {
            this.mCameraAppUi.collapseSetting(true);
            if (FeatureSwitcher.isSubSettingEnabled()) {
                this.mCameraAppUi.collapseSubSetting(true);
            }
        }
    }

    @Override // android.app.Activity, android.view.Window.Callback
    public boolean dispatchKeyEvent(KeyEvent keyEvent) {
        if (keyEvent.getKeyCode() != 142) {
            return super.dispatchKeyEvent(keyEvent);
        }
        if (keyEvent.getAction() != 0 || (!(this.mCameraActor instanceof PhotoActor))) {
            return true;
        }
        ((PhotoActor) this.mCameraActor).onShutterButtonClick(null);
        return true;
    }

    @Override // android.app.Activity
    public void onUserInteraction() {
        if (FeatureSwitcher.isApi2Enable(this)) {
            if (!getCameraActivityBridge().onUserInteraction()) {
                super.onUserInteraction();
            }
        } else if (this.mCameraActor == null || (!this.mCameraActor.onUserInteraction())) {
            super.onUserInteraction();
        }
    }

    @Override // com.android.camera.ActivityBase, android.app.Activity, android.view.KeyEvent.Callback
    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        int repeatCount = keyEvent.getRepeatCount();
        findViewById(R.id.shutter_button_video);
        findViewById(R.id.shutter_button);
        if (i == 27 && repeatCount == 5) {
            updateVideoIcon(3);
            simulateClick(this.mCameraAppUi.getVideoShutter());
            this.mCameraModeSwithView.updateVideoIcon();
            android.util.Log.v("xiaoyao", "onKeyUpxxxxx  =ccccc==");
            return true;
        }
        if (FeatureSwitcher.isApi2Enable(this)) {
            if (getCameraActivityBridge().onKeyDown(i, keyEvent)) {
                return true;
            }
            return super.onKeyDown(i, keyEvent);
        }
        if (this.mPaused) {
            return true;
        }
        switch (i) {
            case 138:
                this.mMainHandler.removeMessages(20);
                this.mMainHandler.sendEmptyMessage(21);
                this.mMainHandler.sendEmptyMessageDelayed(20, 5000L);
                break;
            case 139:
                getZoomIndex();
                this.mMainHandler.removeMessages(20);
                this.mMainHandler.sendEmptyMessage(21);
                this.mMainHandler.sendEmptyMessageDelayed(20, 5000L);
                break;
        }
        if ((isFullScreen() && 82 == i && keyEvent.getRepeatCount() == 0 && this.mCameraAppUi.performSettingClick()) || this.mCameraActor.onKeyDown(i, keyEvent)) {
            return true;
        }
        return super.onKeyDown(i, keyEvent);
    }

    public void simulateClick(View view) {
        long jUptimeMillis = SystemClock.uptimeMillis();
        int[] iArr = new int[2];
        view.dispatchTouchEvent(MotionEvent.obtain(jUptimeMillis, jUptimeMillis, 0, iArr[0] + 5, iArr[1] + 5, 0));
        view.dispatchTouchEvent(MotionEvent.obtain(jUptimeMillis, jUptimeMillis, 1, iArr[0] + 5, iArr[1] + 5, 0));
    }

    @Override // android.app.Activity, android.view.KeyEvent.Callback
    public boolean onKeyUp(int i, KeyEvent keyEvent) {
        android.util.Log.v("xiaoyao", "keyCode  ===" + i);
        if (i == 27 && isVideoModeGroup()) {
            android.util.Log.v("xiaoyao", "onKeyUpxxxxx  =aa==" + i);
            simulateClick(this.mCameraAppUi.getVideoShutter());
            return true;
        }
        if (i == 27 && (!isVideoModeGroup())) {
            android.util.Log.v("xiaoyao", "onKeyUpxxxxx  =bbbb==" + i);
            simulateClick(this.mCameraAppUi.getPhotoShutter());
            return true;
        }
        if (i == 164) {
            int i2 = Settings.System.getInt(getContentResolver(), "actionbotton_mode", 1);
            if (this.mCameraAppUi != null && this.mCameraAppUi.getPhotoShutter() != null && this.mCameraAppUi.getVideoShutter() != null && i2 == 3) {
                if (this.mCameraAppUi.getPhotoShutter().getVisibility() == 0) {
                    simulateClick(this.mCameraAppUi.getPhotoShutter());
                } else if (this.mCameraAppUi.getVideoShutter().getVisibility() == 0) {
                    simulateClick(this.mCameraAppUi.getVideoShutter());
                }
            }
        }
        if (FeatureSwitcher.isApi2Enable(this)) {
            if (getCameraActivityBridge().onKeyUp(i, keyEvent)) {
                return true;
            }
            return super.onKeyUp(i, keyEvent);
        }
        if (this.mPaused || this.mCameraActor.onKeyUp(i, keyEvent)) {
            return true;
        }
        return super.onKeyUp(i, keyEvent);
    }

    public void gotoGallery() {
        Intent intent = new Intent("com.android.camera.action.REVIEW");
        intent.setDataAndType(this.mCameraAppUi.getThumbnailUri(), this.mCameraAppUi.getThumbnailMimeType());
        intent.putExtra("isCamera", true);
        if (isSecureCamera()) {
            intent.putExtra("isSecureCamera", true);
            intent.putExtra("secureAlbum", getSecureAlbum());
            intent.putExtra("securePath", getPath());
            notifyGotoGallery();
        }
        if (2 == this.mActivityManager.getLockTaskModeState()) {
            intent.addFlags(134742016);
        }
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Log.m7e("CameraActivity", "[startGalleryActivity] Couldn't view ", e);
        }
    }

    public ISettingCtrl getISettingCtrl() {
        return this.mISettingCtrl;
    }

    public void resetScreenOn() {
        if (FeatureSwitcher.isApi2Enable(this)) {
            return;
        }
        Log.m5d("CameraActivity", "resetScreenOn()");
        this.mMainHandler.removeMessages(7);
        getWindow().clearFlags(128);
    }

    public void keepScreenOnAwhile() {
        if (FeatureSwitcher.isApi2Enable(this)) {
            return;
        }
        Log.m5d("CameraActivity", "keepScreenOnAwhile()");
        this.mMainHandler.removeMessages(7);
        getWindow().addFlags(128);
        this.mMainHandler.sendEmptyMessageDelayed(7, 120000L);
    }

    public void setGestureListener(ICameraAppUi.GestureListener gestureListener) {
        this.mGestureDispatcher.setGestureListener(gestureListener);
    }

    public void setGestureDispatcherListener(GestureDispatcher.GestureDispatcherListener gestureDispatcherListener) {
        this.mGestureDispatcher.setGestureDispatcherListener(gestureDispatcherListener);
    }

    public SharedPreferences getSharePreferences() {
        return this.mPreferences.getSharedPreference(this, this.mCameraDeviceCtrl.getCameraId());
    }

    public SharedPreferences getSharePreferences(int i) {
        return this.mPreferences.getSharedPreference(this, i);
    }

    public void setCameraState(int i) {
        if (FeatureSwitcher.isApi2Enable(this)) {
            return;
        }
        Log.m5d("CameraActivity", "setCameraState(" + i + ")");
        this.mCameraState = i;
    }

    public int getCameraState() {
        return this.mCameraState;
    }

    public boolean isCameraOpened() {
        if (FeatureSwitcher.isApi2Enable(this)) {
            return false;
        }
        return this.mCameraDeviceCtrl.isCameraOpened();
    }

    public boolean isModeChanged() {
        return this.mIsModeChanged;
    }

    public int getPrevMode() {
        return this.mPrevMode;
    }

    public ISelfTimeManager getSelfTimeManager() {
        return this.mISelfTimeManager;
    }

    public ICameraAppUi getCameraAppUI() {
        return this.mCameraAppUi;
    }

    public FrameView getFrameView() {
        return this.mFrameManager.getFrameView();
    }

    public FrameManager getFrameManager() {
        return this.mFrameManager;
    }

    public ComboPreferences getPreferences() {
        return this.mPreferences;
    }

    public ListPreference getListPreference(int i) {
        return getListPreference(SettingConstants.getSettingKey(i));
    }

    public ListPreference getListPreference(String str) {
        return this.mISettingCtrl.getListPreference(str);
    }

    public LocationManager getLocationManager() {
        return this.mLocationManager;
    }

    public int getCurrentMode() {
        return this.mCameraActor.getMode();
    }

    public ModePicker getModePicker() {
        return this.mModePicker;
    }

    public EffectViewManager getEffectViewManager() {
        return this.mEffectViewManager;
    }

    public int getOrietation() {
        return this.mOrientation;
    }

    public int getOrientationCompensation() {
        return this.mOrientationCompensation;
    }

    public CameraActor getCameraActor() {
        return this.mCameraActor;
    }

    public ModuleManager getModuleManager() {
        return this.mModuleManager;
    }

    public int getPreviewFrameWidth() {
        return this.mCameraDeviceCtrl.getPreviewFrameWidth();
    }

    public int getPreviewFrameHeight() {
        return this.mCameraDeviceCtrl.getPreviewFrameHeight();
    }

    public int getUnCropWidth() {
        return this.mCameraDeviceCtrl.getUnCropWidth();
    }

    public int getUnCropHeight() {
        return this.mCameraDeviceCtrl.getUnCropHeight();
    }

    public GestureRecognizer getGestureRecognizer() {
        return this.mGestureRecognizer;
    }

    public void showBorder(boolean z) {
        if (FeatureSwitcher.isApi2Enable(this)) {
            return;
        }
        this.mPreviewFrameLayout.showBorder(z);
    }

    public View inflate(int i, int i2) {
        return this.mCameraAppUi.inflate(i, i2);
    }

    public void addView(View view, int i) {
        this.mCameraAppUi.addView(view, i);
    }

    public void removeView(View view, int i) {
        this.mCameraAppUi.removeView(view, i);
    }

    public boolean addOnPreferenceReadyListener(OnPreferenceReadyListener onPreferenceReadyListener) {
        if (!this.mPreferenceListeners.contains(onPreferenceReadyListener)) {
            return this.mPreferenceListeners.add(onPreferenceReadyListener);
        }
        return false;
    }

    public boolean addOnParametersReadyListener(OnParametersReadyListener onParametersReadyListener) {
        if (!this.mParametersListeners.contains(onParametersReadyListener)) {
            return this.mParametersListeners.add(onParametersReadyListener);
        }
        return false;
    }

    public boolean removeOnParametersReadyListener(OnParametersReadyListener onParametersReadyListener) {
        return this.mParametersListeners.remove(onParametersReadyListener);
    }

    public boolean addViewManager(ViewManager viewManager) {
        return this.mCameraAppUi.addViewManager(viewManager);
    }

    public boolean removeViewManager(ViewManager viewManager) {
        return this.mCameraAppUi.removeViewManager(viewManager);
    }

    public boolean addResumable(Resumable resumable) {
        if (!this.mResumables.contains(resumable)) {
            return this.mResumables.add(resumable);
        }
        return false;
    }

    public void onCameraOpenFailed() {
        restoreWhenCameraOpenFailed();
    }

    public void onCameraOpenDone() {
        this.mPendingSwitchCameraId = -1;
        this.mMainHandler.sendEmptyMessage(4);
    }

    public void onCameraPreferenceReady() {
        notifyPreferenceReady();
    }

    public void onCameraParametersReady() {
        notifyParametersReady();
    }

    private void restoreWhenCameraOpenFailed() {
        Log.m5d("CameraActivity", "restoreWhenCameraOpenFailed(), mNeedRestoreIfOpenFailed:" + this.mNeedRestoreIfOpenFailed);
        if (this.mNeedRestoreIfOpenFailed) {
            uninstallIntentFilter();
            callResumablePause();
            this.mCameraAppUi.collapseViewManager(true);
            this.mOrientationListener.disable();
            this.mLocationManager.recordLocation(false);
            this.mMainHandler.removeCallbacksAndMessages(null);
            resetScreenOn();
            Util.exitCameraPQMode();
            this.mNeedRestoreIfOpenFailed = false;
        }
    }

    private void keepCameraForSecure() {
        if (isSecureCamera() && isFirstStartAfterScreenOn()) {
            resetFirstStartAfterScreenOn();
            CameraHolder.instance().keep(1000, this.mCameraDeviceCtrl.getCameraId());
        }
    }

    private void initializeAfterPreview() {
        long jCurrentTimeMillis = System.currentTimeMillis();
        callResumableBegin();
        this.mCameraAppUi.initializeAfterPreview();
        addIdleHandler();
        Log.m10v("CameraActivity", "initializeAfterPreview() consume:" + (System.currentTimeMillis() - jCurrentTimeMillis));
    }

    private void initializeCommonManagers() {
        this.mModePicker = new ModePicker(this);
        this.mFileSaver = new FileSaver(this);
        this.mFrameManager = new FrameManager(this);
        this.mModePicker.setListener(this.mModeChangedListener);
        this.mCameraAppUi.setSettingListener(this.mSettingListener);
        this.mCameraAppUi.setPickerListener(this.mPickerListener);
        this.mCameraAppUi.addFileSaver(this.mFileSaver);
        this.mPowerManager = (PowerManager) getSystemService("power");
        Log.m10v("CameraActivity", "getSystemService,mPowerManager =" + this.mPowerManager);
        if (FeatureSwitcher.isSubSettingEnabled()) {
            this.mCameraAppUi.setSubSettingListener(this.mSettingListener);
        }
    }

    private void initializeForOpeningProcess() {
        CameraPerformanceTracker.onEvent("CameraActivity", "InitOpenProcess", true);
        this.mNumberOfCameras = CameraHolder.instance().getNumberOfCameras();
        this.mCameraAppUi.initializeViewGroup();
        this.mFocusAreaIndicator = (RotateLayout) findViewById(R.id.focus_indicator_rotate_layout);
        if (this.mGestureDispatcher == null) {
            this.mGestureDispatcher = new GestureDispatcher(this);
            this.mGestureRecognizer = new GestureRecognizer(this, this.mGestureDispatcher);
        }
        this.mPreviewFrameLayout = (PreviewFrameLayout) findViewById(R.id.frame);
        this.mGestureDispatcher.setSingleTapUpListener(this.mPreviewFrameLayout);
        this.mGestureDispatcher.setLongPressListener(this.mPreviewFrameLayout);
        this.mPreviewFrameLayout.setOnSizeChangedListener(this);
        if (this.mLocationManager == null) {
            this.mLocationManager = new LocationManager(this, null);
        }
        if (this.mOrientationListener == null) {
            this.mOrientationListener = new MyOrientationEventListener(this);
        }
        Log.m5d("CameraActivity", "initializeForOpeningProcess() mNumberOfCameras=" + this.mNumberOfCameras);
        CameraPerformanceTracker.onEvent("CameraActivity", "InitOpenProcess", false);
    }

    private void updateFocusAndFace() {
        if (this.mFrameManager != null && getFrameView() != null) {
            getFrameView().clear();
            getFrameView().setVisibility(0);
            getFrameView().setMirror(CameraHolder.instance().getCameraInfo()[this.mCameraDeviceCtrl.getCameraId()].facing == 1);
            getFrameView().resume();
        }
        FocusManager focusManager = this.mCameraDeviceCtrl.getFocusManager();
        if (focusManager != null) {
            focusManager.setFocusAreaIndicator(this.mFocusAreaIndicator);
            View viewFindViewById = this.mFocusAreaIndicator.findViewById(R.id.focus_indicator);
            int iMin = Math.min(getPreviewFrameWidth(), getPreviewFrameHeight()) / 4;
            ViewGroup.LayoutParams layoutParams = viewFindViewById.getLayoutParams();
            layoutParams.width = iMin;
            layoutParams.height = iMin;
        }
        if (this.mFrameManager != null && getFrameView() != null) {
            this.mFrameManager.initializeFrameView(false);
        }
    }

    private void doOnResume() {
        long jCurrentTimeMillis = System.currentTimeMillis();
        this.mOrientationListener.enable();
        installIntentFilter();
        callResumableResume();
        this.mCameraAppUi.checkViewManagerConfiguration();
        Log.m5d("CameraActivity", "doOnResume() consume:" + (System.currentTimeMillis() - jCurrentTimeMillis));
    }

    private void clearFocusAndFace() {
        if (getFrameView() != null) {
            getFrameView().clear();
        }
        FocusManager focusManager = this.mCameraDeviceCtrl.getFocusManager();
        if (focusManager != null) {
            focusManager.removeMessages();
        }
    }

    private boolean isCancelSingleTapUp() {
        if (this.mCameraAppUi.getViewState() == ICameraAppUi.ViewState.VIEW_STATE_LOMOEFFECT_SETTING) {
            return true;
        }
        return false;
    }

    private void notifyPreferenceReady() {
        for (OnPreferenceReadyListener onPreferenceReadyListener : this.mPreferenceListeners) {
            if (onPreferenceReadyListener != null) {
                onPreferenceReadyListener.onPreferenceReady();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyParametersReady() {
        ICameraAppUi.ViewState viewState = this.mCameraAppUi.getViewState();
        if ((isNonePickIntent() && this.mCameraActor.getMode() != 8 && this.mCameraActor.getMode() != 9 && viewState != ICameraAppUi.ViewState.VIEW_STATE_SETTING && viewState != ICameraAppUi.ViewState.VIEW_STATE_SUB_SETTING && viewState != ICameraAppUi.ViewState.VIEW_STATE_RECORDING) || isStereo3DImageCaptureIntent()) {
            this.mModePicker.show();
        }
        if (!isSecureCamera()) {
            updateCameraLocationInfo();
        }
        for (OnParametersReadyListener onParametersReadyListener : this.mParametersListeners) {
            if (onParametersReadyListener != null) {
                onParametersReadyListener.onCameraParameterReady();
            }
        }
        this.mCameraAppUi.notifyParametersReady();
    }

    private void onRequestLocationPermissionResult(String[] strArr, int[] iArr) {
        if (this.mPermissionManager.isCameraLocationPermissionsResultReady(strArr, iArr)) {
            this.mLocationManager.recordLocation(true);
            return;
        }
        ListPreference listPreference = this.mISettingCtrl.getListPreference("pref_camera_recordlocation_key");
        if (listPreference != null) {
            listPreference.setValue("off");
        }
    }

    private void updateCameraLocationInfo() {
        if (!"on".equals(this.mISettingCtrl.getSettingValue("pref_camera_recordlocation_key"))) {
            this.mLocationManager.recordLocation(false);
        } else if (!this.mIsCheckingLocationPermission) {
            if (this.mPermissionManager.requestCameraLocationPermissions()) {
                this.mLocationManager.recordLocation(true);
            } else {
                this.mIsCheckingLocationPermission = true;
            }
        }
    }

    private void callResumableBegin() {
        Iterator<T> it = this.mResumables.iterator();
        while (it.hasNext()) {
            ((Resumable) it.next()).begin();
        }
    }

    private void callResumableResume() {
        Iterator<T> it = this.mResumables.iterator();
        while (it.hasNext()) {
            ((Resumable) it.next()).resume();
        }
    }

    private void callResumablePause() {
        Iterator<T> it = this.mResumables.iterator();
        while (it.hasNext()) {
            ((Resumable) it.next()).pause();
        }
    }

    private void callResumableFinish() {
        Iterator<T> it = this.mResumables.iterator();
        while (it.hasNext()) {
            ((Resumable) it.next()).finish();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String getModeSettingKey(int i) {
        switch (i) {
            case 0:
                return "normal_key";
            case 1:
            case 4:
            default:
                return null;
            case 2:
                return "face_beauty_key";
            case 3:
                return "panorama_key";
            case 5:
                return "photo_pip_key";
            case 6:
                return "refocus_key";
            case FrameMetricsAggregator.DELAY_INDEX /* 7 */:
                return "photo_stereo_key";
            case 8:
                return "video_key";
            case 9:
                return "video_pip_key";
            case 10:
                return "video_stereo_key";
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void releaseCameraActor(int i, int i2) {
        boolean z = false;
        Log.m5d("CameraActivity", "releaseCameraActor() mode=" + this.mCameraActor.getMode());
        if (i2 != 4 && i2 != 1 && i != 4 && i != 1) {
            z = true;
        }
        if (z) {
            this.mCameraAppUi.collapseViewManager(true);
        }
        this.mCameraActor.release();
        if (i2 == 8 || i2 == 108 || i2 == 9) {
            Log.m5d("CameraActivity", "releaseCameraActor setSwipingEnabled(false)  newMode = " + i2);
        }
    }

    public void onSettingChanged(String str, String str2) {
        this.mISettingCtrl.onSettingChanged(str, str2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void enableDualCameraExtras() {
        getListPreference(53).setValueIndex(0);
        getListPreference(52).setValueIndex(0);
        onSettingChanged("pref_fast_af_key", "on");
        onSettingChanged("pref_distance_key", "on");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void disableDualCameraExtras() {
        getListPreference(53).setValueIndex(1);
        getListPreference(52).setValueIndex(1);
        onSettingChanged("pref_fast_af_key", "off");
        onSettingChanged("pref_distance_key", "off");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void singleDualCameraExtras() {
        if ("off".equals(getListPreference("pref_fast_af_key").getValue())) {
            onSettingChanged("pref_fast_af_key", "off");
        } else {
            onSettingChanged("pref_fast_af_key", "on");
        }
        if ("off".equals(getListPreference("pref_distance_key").getValue())) {
            onSettingChanged("pref_distance_key", "off");
        } else {
            onSettingChanged("pref_distance_key", "on");
        }
    }

    public void notifyPreferenceChanged(ListPreference listPreference) {
        this.mSettingListener.onSharedPreferenceChanged(listPreference);
        this.mCameraAppUi.getCameraView(ICameraAppUi.CommonUiType.SETTING).refresh();
    }

    public boolean addOnOrientationListener(OnOrientationListener onOrientationListener) {
        if (!this.mOrientationListeners.contains(onOrientationListener)) {
            return this.mOrientationListeners.add(onOrientationListener);
        }
        return false;
    }

    public boolean removeOnOrientationListener(OnOrientationListener onOrientationListener) {
        return this.mOrientationListeners.remove(onOrientationListener);
    }

    public void setOrientation(boolean z, int i) {
        this.mOrientationListener.setLock(false);
        if (z) {
            this.mOrientationListener.onOrientationChanged(i);
            this.mOrientationListener.setLock(true);
        } else {
            this.mOrientationListener.restoreOrientation();
        }
    }

    public boolean isSwitchingCamera() {
        return this.mPendingSwitchCameraId != -1;
    }

    public boolean isNonePickIntent() {
        return this.mPickType == 0;
    }

    public boolean isImageCaptureIntent() {
        return 1 == this.mPickType;
    }

    public boolean isVideoCaptureIntent() {
        return 2 == this.mPickType;
    }

    public boolean isVideoWallPaperIntent() {
        return 3 == this.mPickType;
    }

    public boolean isStereo3DImageCaptureIntent() {
        return 4 == this.mPickType;
    }

    public PreviewSurfaceView getPreviewSurfaceView() {
        if (this.mCameraDeviceCtrl == null) {
            return null;
        }
        return this.mCameraDeviceCtrl.getSurfaceView();
    }

    public boolean isDualCameraDeviceEnable() {
        return isPIPMode(getCurrentMode());
    }

    public void setResultExAndFinish(int i) {
        setResultEx(i);
        finish();
        clearUserSettings();
    }

    public void setResultExAndFinish(int i, Intent intent) {
        setResultEx(i, intent);
        finish();
        clearUserSettings();
    }

    public boolean isVideoMode() {
        Log.m5d("CameraActivity", "isVideoMode() getCurrentMode()=" + getCurrentMode());
        return 8 == getCurrentMode() || 108 == getCurrentMode() || 9 == getCurrentMode() || 10 == getCurrentMode();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyOrientationChanged() {
        for (OnOrientationListener onOrientationListener : this.mOrientationListeners) {
            if (onOrientationListener != null) {
                onOrientationListener.onOrientationChanged(this.mOrientationCompensation);
            }
        }
    }

    private class MyOrientationEventListener extends OrientationEventListener {
        private boolean mLock;
        private int mRestoreOrientation;

        public MyOrientationEventListener(Context context) {
            super(context);
            this.mLock = false;
        }

        public void setLock(boolean z) {
            this.mLock = z;
        }

        public void restoreOrientation() {
            onOrientationChanged(this.mRestoreOrientation);
        }

        @Override // android.view.OrientationEventListener
        public void onOrientationChanged(int i) {
            if (i == -1) {
                Log.m11w("CameraActivity", "[onOrientationChanged]orientation is ORIENTATION_UNKNOWN,return.");
                return;
            }
            int iRoundOrientation = Util.roundOrientation(i, this.mRestoreOrientation);
            if (!this.mLock) {
                updateOrientation(iRoundOrientation);
            }
            if (this.mRestoreOrientation != iRoundOrientation) {
                this.mRestoreOrientation = iRoundOrientation;
                CameraActivity.this.mModuleManager.onOrientationChanged(this.mRestoreOrientation);
            }
        }

        private void updateOrientation(int i) {
            int displayRotation = Util.getDisplayRotation(CameraActivity.this);
            if (CameraActivity.this.mOrientation == i && displayRotation == CameraActivity.this.mDisplayRotation) {
                return;
            }
            Log.m5d("CameraActivity", "[updateOrientation]orientation:" + i + ",mOrientation:" + CameraActivity.this.mOrientation + ",newDisplayRotation:" + displayRotation + ",mDisplayRotation:" + CameraActivity.this.mDisplayRotation);
            if (displayRotation != CameraActivity.this.mDisplayRotation) {
                CameraActivity.this.mDisplayRotation = displayRotation;
                CameraActivity.this.mCameraDeviceCtrl.setDisplayOrientation();
            }
            if (CameraActivity.this.mOrientation != i && CameraActivity.this.slideText.isShown()) {
                Util.setOrientation(CameraActivity.this.slideText, i, true);
            }
            CameraActivity.this.mOrientation = i;
            updateCompensation(CameraActivity.this.mOrientation);
            CameraActivity.this.mCameraDeviceCtrl.onOrientationChanged(CameraActivity.this.mOrientation);
        }

        private void updateCompensation(int i) {
            int displayRotation = (Util.getDisplayRotation(CameraActivity.this) + i) % 360;
            if (CameraActivity.this.mOrientationCompensation != displayRotation) {
                Log.m5d("CameraActivity", "[updateCompensation] mCompensation:" + CameraActivity.this.mOrientationCompensation + ", compensation:" + displayRotation);
                CameraActivity.this.mOrientationCompensation = displayRotation;
                CameraActivity.this.mModuleManager.onCompensationChanged(CameraActivity.this.mOrientationCompensation);
                CameraActivity.this.notifyOrientationChanged();
            }
        }
    }

    private void parseIntent() {
        Intent intent = getIntent();
        String action = intent.getAction();
        if ("android.media.action.IMAGE_CAPTURE".equals(action) || CameraActivityBridge.ACTION_IMAGE_CAPTURE_SECURE.equals(action)) {
            this.mPickType = 1;
        } else if ("com.mediatek.vlw".equals(intent.getStringExtra("identity"))) {
            this.mWallpaperAspectio = intent.getFloatExtra("ratio", 1.2f);
            intent.putExtra("android.intent.extra.quickCapture", true);
            this.mPickType = 3;
        } else if ("android.media.action.VIDEO_CAPTURE".equals(action)) {
            this.mPickType = 2;
        } else if ("android.media.action.IMAGE_CAPTURE_3D".equals(action)) {
            this.mPickType = 4;
        } else {
            this.mPickType = 0;
            com.mediatek.camera.setting.SettingUtils.setLimitResolution(0);
        }
        if (this.mPickType != 0) {
            this.mQuickCapture = intent.getBooleanExtra("android.intent.extra.quickCapture", false);
            this.mSaveUri = (Uri) intent.getParcelableExtra("output");
            this.mLimitedSize = intent.getLongExtra("android.intent.extra.sizeLimit", 0L);
            this.mCropValue = intent.getStringExtra("crop");
            this.mLimitedDuration = intent.getIntExtra("android.intent.extra.durationLimit", 0);
            this.mLimitedResoltion = intent.getIntExtra("mediatek.intent.extra.EXTRA_RESOLUTION_LIMIT", 0);
            com.mediatek.camera.setting.SettingUtils.setLimitResolution(this.mLimitedResoltion);
        }
        Log.m5d("CameraActivity", "parseIntent() mPickType=" + this.mPickType + ", mQuickCapture=" + this.mQuickCapture + ", mSaveUri=" + this.mSaveUri + ", mLimitedSize=" + this.mLimitedSize + ", mCropValue=" + this.mCropValue + ", mLimitedDuration=" + this.mLimitedDuration);
        Log.m5d("CameraActivity", "parseIntent() action=" + intent.getAction());
        Bundle extras = intent.getExtras();
        if (extras != null) {
            this.mCanShowVideoShare = extras.getBoolean("CanShare", true);
            for (String str : extras.keySet()) {
                Log.m10v("CameraActivity", "parseIntent() extra[" + str + "]=" + extras.get(str));
            }
        }
        if (intent.getCategories() != null) {
            Iterator<T> it = intent.getCategories().iterator();
            while (it.hasNext()) {
                Log.m10v("CameraActivity", "parseIntent() getCategories=" + ((String) it.next()));
            }
        }
        Log.m10v("CameraActivity", "parseIntent() data=" + intent.getData());
        Log.m10v("CameraActivity", "parseIntent() flag=" + intent.getFlags());
        Log.m10v("CameraActivity", "parseIntent() package=" + intent.getPackage());
        Log.m10v("CameraActivity", "mCanShowVideoShare = " + this.mCanShowVideoShare);
    }

    private boolean isMountPointChanged() {
        boolean z = false;
        String mountPoint = Storage.getMountPoint();
        Storage.updateDefaultDirectory();
        if (!mountPoint.equals(Storage.getMountPoint())) {
            z = true;
        }
        Log.m5d("CameraActivity", "isMountPointChanged() old=" + mountPoint + ", new=" + Storage.getMountPoint() + ", return " + z);
        return z;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateStorageDirectory() {
        if (!Storage.updateDefaultDirectory()) {
            setPath(Storage.getCameraScreenNailPath());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isSameStorage(Intent intent) {
        String path;
        String mountPoint = null;
        StorageVolume storageVolume = (StorageVolume) intent.getParcelableExtra("android.os.storage.extra.STORAGE_VOLUME");
        boolean z = false;
        if (storageVolume != null) {
            mountPoint = Storage.getMountPoint();
            path = storageVolume.getPath();
            if (mountPoint != null && mountPoint.equals(path)) {
                z = true;
            }
        } else {
            path = null;
        }
        Log.m5d("CameraActivity", "isSameStorage() mountPoint=" + mountPoint + ", intentPath=" + path + ", return " + z);
        return z;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isSameStorage(Uri uri) {
        String mountPoint;
        String path = null;
        if (!Storage.updateDefaultDirectory()) {
            Log.m5d("CameraActivity", "isSameStorage(uri)/same= updateDefaultDirectory");
            setPath(Storage.getCameraScreenNailPath());
        }
        boolean z = false;
        if (uri != null) {
            mountPoint = Storage.getMountPoint();
            path = uri.getPath();
            if (mountPoint != null && mountPoint.equals(path)) {
                z = true;
            }
        } else {
            mountPoint = null;
        }
        Log.m5d("CameraActivity", "isSameStorage(" + uri + ") mountPoint=" + mountPoint + ", intentPath=" + path + ", return " + z);
        this.mCameraAppUi.forceThumbnailUpdate();
        return z;
    }

    private void installIntentFilter() {
        IntentFilter intentFilter = new IntentFilter("android.intent.action.MEDIA_MOUNTED");
        intentFilter.addAction("android.intent.action.MEDIA_EJECT");
        intentFilter.addAction("android.intent.action.MEDIA_UNMOUNTED");
        intentFilter.addAction("android.intent.action.MEDIA_SCANNER_STARTED");
        intentFilter.addAction("android.intent.action.MEDIA_SCANNER_FINISHED");
        intentFilter.addAction("android.intent.action.MEDIA_CHECKING");
        intentFilter.addDataScheme("file");
        registerReceiver(this.mReceiver, intentFilter);
    }

    private void uninstallIntentFilter() {
        try {
            unregisterReceiver(this.mReceiver);
        } catch (IllegalArgumentException e) {
            Log.m7e("CameraActivity", "[uninstallIntentFilter] error ", e);
        }
    }

    private void clearUserSettings() {
        Log.m5d("CameraActivity", "clearUserSettings() isFinishing()=" + isFinishing());
        if (this.mISettingCtrl != null && isFinishing()) {
            this.mISettingCtrl.resetSetting();
        }
    }

    private void addIdleHandler() {
        Looper.myQueue().addIdleHandler(new MessageQueue.IdleHandler() { // from class: com.android.camera.CameraActivity.10
            @Override // android.os.MessageQueue.IdleHandler
            public boolean queueIdle() {
                Storage.ensureOSXCompatible();
                return false;
            }
        });
    }

    public boolean isStereoMode() {
        return this.mIsStereoMode;
    }

    private void initializeStereo3DMode() {
        if (isStereo3DImageCaptureIntent()) {
            this.mIsStereoMode = true;
            com.mediatek.camera.setting.SettingUtils.writePreferredCamera3DMode(this.mPreferences, "1");
        } else {
            this.mIsStereoMode = false;
            com.mediatek.camera.setting.SettingUtils.writePreferredCamera3DMode(this.mPreferences, "0");
        }
    }

    public boolean isNeedOpenStereoCamera() {
        boolean z = com.mediatek.camera.setting.SettingUtils.readPreferredStereoCamera(this.mPreferences).equals("on") || getCurrentMode() == 6 || getCurrentMode() == 7 || getCurrentMode() == 10;
        Log.m5d("CameraActivity", "[isNeedOpenStereoCamera] mIsStereoToVideoMode:" + this.mIsStereoToVideoMode + ",mode:" + getCurrentMode() + ",enable:" + z);
        if (this.mIsModeChanged && getListPreference(52) != null && "on".equals(getListPreference(52).getValue())) {
            z = true;
        }
        if (this.mIsStereoToVideoMode) {
            this.mIsStereoToVideoMode = false;
            z = false;
        }
        if (getCurrentMode() == 5) {
            z = false;
        }
        boolean zIsNonePickIntent = z ? isNonePickIntent() : false;
        Log.m5d("CameraActivity", "needOpenStereoCamera enable = " + zIsNonePickIntent);
        return zIsNonePickIntent;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isPIPMode(int i) {
        return i == 9 || i == 5;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isPIPModeSwitch(int i, int i2) {
        if (isPIPMode(i) && (!isPIPMode(i2))) {
            return true;
        }
        if (isPIPMode(i)) {
            return false;
        }
        return isPIPMode(i2);
    }

    private boolean isStereoMode(int i) {
        return i == 10 || i == 6;
    }

    private boolean isRefocusSwitchNormal(int i, int i2) {
        if (i != 6 || i2 == 8) {
            return i != 6 && i2 == 6;
        }
        return true;
    }

    private boolean isDenoiseSwitchNormal(int i, int i2) {
        if (i != 7 || i2 == 8) {
            return i != 7 && i2 == 7;
        }
        return true;
    }

    private boolean isRefocusSwitchVideo(int i, int i2) {
        Log.m5d("CameraActivity", "isRefocusSwitchVideo lastMode = " + i + ", newMode = " + i2);
        return i == 6 && i2 == 8;
    }

    private boolean isStereoModeSwitch(int i, int i2) {
        if (isStereoMode(i) && (!isStereoMode(i2))) {
            return true;
        }
        if (isStereoMode(i)) {
            return false;
        }
        return isStereoMode(i2);
    }

    private boolean isFastAfEnabled() {
        Log.m5d("CameraActivity", "isFastAfEnabled" + getListPreference("pref_fast_af_key"));
        if (getListPreference("pref_fast_af_key") != null && "on".equals(getListPreference("pref_fast_af_key").getValue())) {
            return true;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void judgeSensorSwitchStereoMode() {
        this.mStereoCaptureSuptSensorId = ParametersHelper.getIdStereoCaptureSupt(getParameters());
        this.mDenoiseSuptSensorId = ParametersHelper.getIdStereoDenoiseSupt(getParameters());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void releaseCameraSwitchStereoMode(int i, int i2) {
        if (!ParametersHelper.isVsDofSupported(getParameters())) {
            if (!isFastAfEnabled() && isRefocusSwitchVideo(i, i2)) {
                if (i2 == 8) {
                    this.mIsStereoToVideoMode = true;
                }
                doStereoModeChanged(true);
            }
            if (isRefocusSwitchNormal(i, i2)) {
                this.mCameraDeviceCtrl.closeCamera(false);
            }
        } else if (isStereoModeSwitch(i, i2)) {
            this.mCameraDeviceCtrl.closeCamera(false);
        }
        if (isDenoiseSwitchNormal(i, i2)) {
            this.mCameraDeviceCtrl.closeCamera(false);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isStereoModeChanged(int i, int i2) {
        this.mModuleManager.setModeSettingValue(this.mCameraActor.getCameraModeType(i2), "on");
        if (!ParametersHelper.isVsDofSupported(getParameters())) {
            if (isRefocusSwitchNormal(i, i2)) {
                Log.m8i("CameraActivity", "isRefocusSwitchNormal return");
                return true;
            }
        } else if (isStereoModeSwitch(i, i2)) {
            Log.m8i("CameraActivity", "isStereoModeSwitch return");
            return true;
        }
        if (isDenoiseSwitchNormal(i, i2)) {
            Log.m8i("CameraActivity", "isDenoiseSwitchNormal return");
            return true;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void doStereoModeChanged(boolean z) {
        int i;
        if (this.mNextMode == 6) {
            i = this.mStereoCaptureSuptSensorId;
        } else {
            i = this.mNextMode == 7 ? this.mDenoiseSuptSensorId : -1;
        }
        int i2 = i != 2 ? i : -1;
        Log.m8i("CameraActivity", "doStereoModeChanged id = " + i2);
        this.mCameraDeviceCtrl.openStereoCamera(i2, z);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void doPIPModeChanged(int i) {
        Log.m5d("CameraActivity", "doPIPModeChanged");
        this.mCameraAppUi.collapseViewManager(true);
        clearFocusAndFace();
        this.mCameraDeviceCtrl.unInitializeFocusManager();
        this.mPreferences.setLocalId(this, i);
        com.mediatek.camera.setting.SettingUtils.upgradeLocalPreferences(this.mPreferences.getLocal());
        com.mediatek.camera.setting.SettingUtils.writePreferredCameraId(this.mPreferences, i);
        this.mCameraDeviceCtrl.openCamera(i);
    }

    public CameraManager.CameraProxy getCameraDevice() {
        return this.mCameraDeviceCtrl.getCameraDevice();
    }

    public FocusManager getFocusManager() {
        return this.mCameraDeviceCtrl.getFocusManager();
    }

    public Camera.Parameters getParameters() {
        return this.mCameraDeviceCtrl.getParameters();
    }

    public Camera.Parameters getTopParameters() {
        return this.mCameraDeviceCtrl.getTopParameters();
    }

    public int getCameraId() {
        return this.mCameraDeviceCtrl.getCameraId();
    }

    public int getOriCameraId() {
        return this.mOriCameraId;
    }

    public void applyParameterForCapture(SaveRequest saveRequest) {
        this.mCameraDeviceCtrl.applyParameterForCapture(saveRequest);
    }

    public void applyParameterForFocus(boolean z) {
        this.mCameraDeviceCtrl.applyParameterForFocus(z);
    }

    public int getDisplayOrientation() {
        return this.mCameraDeviceCtrl.getDisplayOrientation();
    }

    public void startAsyncZoom(int i) {
        this.mCameraDeviceCtrl.startAsyncZoom(i);
    }

    public boolean isCameraIdle() {
        return this.mCameraDeviceCtrl.isCameraIdle();
    }

    private void keepMediaProviderInstance() {
        Log.m5d("CameraActivity", "keepMediaProviderInstance() mMediaProviderClient =  " + this.mMediaProviderClient);
        if (this.mMediaProviderClient == null) {
            this.mMediaProviderClient = getContentResolver().acquireContentProviderClient("media");
        }
    }

    private class ModuleCtrlImpl implements IModuleCtrl {
        private CameraActivity mCamera;

        public ModuleCtrlImpl(CameraActivity cameraActivity) {
            this.mCamera = cameraActivity;
        }

        @Override // com.mediatek.camera.platform.IModuleCtrl
        public boolean applyFocusParameters(boolean z) {
            CameraActivity.this.mCameraDeviceCtrl.applyParameterForFocus(z);
            return true;
        }

        @Override // com.mediatek.camera.platform.IModuleCtrl
        public int getOrientation() {
            return CameraActivity.this.mOrientation;
        }

        @Override // com.mediatek.camera.platform.IModuleCtrl
        public int getDisplayOrientation() {
            return CameraActivity.this.mCameraDeviceCtrl.getDisplayOrientation();
        }

        @Override // com.mediatek.camera.platform.IModuleCtrl
        public int getDisplayRotation() {
            return CameraActivity.this.mDisplayRotation;
        }

        @Override // com.mediatek.camera.platform.IModuleCtrl
        public int getOrientationCompensation() {
            return CameraActivity.this.mOrientationCompensation;
        }

        @Override // com.mediatek.camera.platform.IModuleCtrl
        public int getJpegOrientation() {
            return CameraActivity.this.mCameraDeviceCtrl.getCurCameraDevice().getJpegRotation();
        }

        @Override // com.mediatek.camera.platform.IModuleCtrl
        public boolean lockOrientation() {
            Log.m5d("CameraActivity", "[lockOrientation]...");
            this.mCamera.setOrientation(true, -1);
            return true;
        }

        @Override // com.mediatek.camera.platform.IModuleCtrl
        public boolean unlockOrientation() {
            Log.m5d("CameraActivity", "[unlockOrientation]...");
            this.mCamera.setOrientation(false, -1);
            return true;
        }

        @Override // com.mediatek.camera.platform.IModuleCtrl
        public boolean enableOrientationListener() {
            CameraActivity.this.mOrientationListener.enable();
            return true;
        }

        @Override // com.mediatek.camera.platform.IModuleCtrl
        public boolean disableOrientationListener() {
            CameraActivity.this.mOrientationListener.disable();
            return true;
        }

        @Override // com.mediatek.camera.platform.IModuleCtrl
        public Location getLocation() {
            return CameraActivity.this.mLocationManager.getCurrentLocation();
        }

        @Override // com.mediatek.camera.platform.IModuleCtrl
        public Uri getSaveUri() {
            return CameraActivity.this.mSaveUri;
        }

        @Override // com.mediatek.camera.platform.IModuleCtrl
        public String getCropValue() {
            return CameraActivity.this.mCropValue;
        }

        @Override // com.mediatek.camera.platform.IModuleCtrl
        public void setResultAndFinish(int i) {
            this.mCamera.setResultExAndFinish(i);
        }

        @Override // com.mediatek.camera.platform.IModuleCtrl
        public void setResultAndFinish(int i, Intent intent) {
            this.mCamera.setResultExAndFinish(i, intent);
        }

        @Override // com.mediatek.camera.platform.IModuleCtrl
        public boolean isSecureCamera() {
            return CameraActivity.this.mSecureCamera;
        }

        @Override // com.mediatek.camera.platform.IModuleCtrl
        public boolean isImageCaptureIntent() {
            return 1 == CameraActivity.this.mPickType;
        }

        @Override // com.mediatek.camera.platform.IModuleCtrl
        public void startFaceDetection() {
            CameraActivity.this.mCameraActor.startFaceDetection();
        }

        @Override // com.mediatek.camera.platform.IModuleCtrl
        public void stopFaceDetection() {
            CameraActivity.this.mCameraActor.stopFaceDetection();
        }

        @Override // com.mediatek.camera.platform.IModuleCtrl
        public boolean isVideoCaptureIntent() {
            return 2 == CameraActivity.this.mPickType;
        }

        @Override // com.mediatek.camera.platform.IModuleCtrl
        public boolean isNonePickIntent() {
            return CameraActivity.this.mPickType == 0;
        }

        @Override // com.mediatek.camera.platform.IModuleCtrl
        public Intent getIntent() {
            return this.mCamera.getIntent();
        }

        @Override // com.mediatek.camera.platform.IModuleCtrl
        public boolean isQuickCapture() {
            return CameraActivity.this.mQuickCapture;
        }

        @Override // com.mediatek.camera.platform.IModuleCtrl
        public void backToLastMode() {
            CameraActivity.this.mCameraDeviceCtrl.waitCameraStartUpThread(false);
            android.util.Log.v("xiaoyao", "isVideoModeGroupxxx  ===" + CameraActivity.this.mPrevMode);
            if (CameraActivity.this.isVideoModeGroup()) {
                CameraActivity.this.mPrevMode = 8;
            } else {
                CameraActivity.this.mPrevMode = 0;
            }
            CameraActivity.this.mModePicker.setCurrentMode(CameraActivity.this.mPrevMode);
        }

        @Override // com.mediatek.camera.platform.IModuleCtrl
        public void backToCallingActivity(int i, Intent intent) {
            CameraActivity.this.setResultExAndFinish(i, intent);
        }

        @Override // com.mediatek.camera.platform.IModuleCtrl
        public ComboPreferences getComboPreferences() {
            return this.mCamera.getPreferences();
        }

        @Override // com.mediatek.camera.platform.IModuleCtrl
        public void switchCameraDevice() {
            CameraActivity.this.mCameraDeviceCtrl.doSwitchCameraDevice();
        }

        @Override // com.mediatek.camera.platform.IModuleCtrl
        public ICameraMode.CameraModeType getNextMode() {
            return CameraActivity.this.mCameraActor.getCameraModeType(CameraActivity.this.mNextMode);
        }

        @Override // com.mediatek.camera.platform.IModuleCtrl
        public boolean setFaceBeautyEnalbe(boolean z) {
            CameraActivity.this.mFrameManager.enableFaceBeauty(z);
            return z;
        }

        @Override // com.mediatek.camera.platform.IModuleCtrl
        public boolean initializeFrameView(boolean z) {
            CameraActivity.this.mFrameManager.initializeFrameView(z);
            return false;
        }

        @Override // com.mediatek.camera.platform.IModuleCtrl
        public void setFaces(Camera.Face[] faceArr) {
            CameraActivity.this.getFrameView().setFaces(faceArr);
        }

        @Override // com.mediatek.camera.platform.IModuleCtrl
        public Surface getPreviewSurface() {
            return CameraActivity.this.mCameraDeviceCtrl.getSurfaceView().getHolder().getSurface();
        }

        @Override // com.mediatek.camera.platform.IModuleCtrl
        public boolean isFirstStartUp() {
            return CameraActivity.this.mCameraDeviceCtrl.isFirstStartUp();
        }

        @Override // com.mediatek.camera.platform.IModuleCtrl
        public void previewStarted() {
            CameraActivity.this.mCameraDeviceCtrl.detachSurfaceViewLayout();
        }
    }

    public void onScrollRestMode(boolean z) {
        this.mCameraAppUi.getShutterManager().onScrollRestMode(z);
    }

    public void onCameraPicked(int i) {
        this.mPickerListener.onCameraPicked(i);
    }

    public int getCurrentWheelMode() {
        return mCurrentMode;
    }

    public boolean isVideoModeGroup() {
        return mCurrentMode == 3 || mCurrentMode == 1 || mCurrentMode == 2 || mCurrentMode == 0;
    }

    public synchronized void decodeYUV420SP(int[] iArr, byte[] bArr, int i, int i2, float f) {
        int i3;
        int i4;
        int i5;
        int i6;
        int i7;
        int i8;
        int i9 = i * i2;
        int i10 = 0;
        int i11 = 0;
        int i12 = (int) (1.0f / f);
        int i13 = 0;
        int i14 = 0;
        while (true) {
            int i15 = i14;
            if (i15 < i2) {
                int i16 = i9 + ((i15 >> 1) * i);
                int i17 = 0;
                while (i17 < i) {
                    try {
                        int i18 = (bArr[(i * i15) + i17] & 255) - 16;
                        int i19 = i18 < 0 ? 0 : i18;
                        if ((i17 & 1) == 0) {
                            int i20 = i16 + 1;
                            try {
                                i11 = (bArr[i16] & 255) - 128;
                                i8 = (bArr[i20] & 255) - 128;
                                i7 = i20 + 1;
                            } catch (Exception e) {
                                i7 = i16;
                                i8 = i10;
                            }
                        } else {
                            i7 = i16;
                            i8 = i10;
                        }
                        int i21 = i19 * 1192;
                        int i22 = i21 + (i11 * 1634);
                        int i23 = (i21 - (i11 * 833)) - (i8 * 400);
                        int i24 = i21 + (i8 * 2066);
                        if (i22 < 0) {
                            i22 = 0;
                        } else if (i22 > 262143) {
                            i22 = 262143;
                        }
                        if (i23 < 0) {
                            i23 = 0;
                        } else if (i23 > 262143) {
                            i23 = 262143;
                        }
                        if (i24 < 0) {
                            i24 = 0;
                        } else if (i24 > 262143) {
                            i24 = 262143;
                        }
                        try {
                            iArr[i13] = ((i24 >> 10) & 255) | ((i23 >> 2) & 65280) | ((i22 << 6) & 16711680) | (-16777216);
                            int i25 = i17 + i12;
                            i5 = i11;
                            i6 = i13 + 1;
                            i4 = i8;
                            i16 = i7;
                            i3 = i25;
                        } catch (Exception e2) {
                            i10 = i8;
                            i16 = i7;
                            i3 = i17;
                            i4 = i10;
                            i5 = i11;
                            i6 = i13;
                            i13 = i6;
                            i11 = i5;
                            i10 = i4;
                            i17 = i3;
                        }
                    } catch (Exception e3) {
                    }
                    i13 = i6;
                    i11 = i5;
                    i10 = i4;
                    i17 = i3;
                }
                i14 = i15 + i12;
            }
        }
    }

    public class ProcessYUVDataThread extends Thread {
        private byte[] mData;
        private int mHeight;
        private int mRotation;
        private int mWidth;

        public ProcessYUVDataThread(byte[] bArr, int i, int i2, int i3) {
            this.mHeight = 0;
            this.mRotation = 0;
            this.mWidth = 0;
            this.mData = bArr;
            this.mWidth = i;
            this.mHeight = i2;
            this.mRotation = i3;
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            int[] iArr = new int[((this.mWidth * this.mHeight) / 2) / 2];
            CameraActivity.this.decodeYUV420SP(iArr, this.mData, this.mWidth, this.mHeight, 0.5f);
            Bitmap bitmapCreateBitmap = Bitmap.createBitmap(this.mWidth / 2, this.mHeight / 2, Bitmap.Config.ARGB_8888);
            bitmapCreateBitmap.setPixels(iArr, 0, this.mWidth / 2, 0, 0, this.mWidth / 2, this.mHeight / 2);
            Bitmap bitmapAdjustPhotoRotation = CameraActivity.this.adjustPhotoRotation(bitmapCreateBitmap, this.mRotation);
            if (bitmapCreateBitmap != null && (!bitmapCreateBitmap.isRecycled())) {
                bitmapCreateBitmap.recycle();
            }
            Message message = new Message();
            message.what = 18;
            message.obj = bitmapAdjustPhotoRotation;
            CameraActivity.this.mMainHandler.sendMessage(message);
        }
    }

    public Bitmap adjustPhotoRotation(Bitmap bitmap, int i) {
        float height;
        float width = 0.0f;
        Matrix matrix = new Matrix();
        matrix.setRotate(i, bitmap.getWidth() / 2.0f, bitmap.getHeight() / 2.0f);
        if (i == 90) {
            height = bitmap.getHeight();
        } else if (i == 270) {
            height = 0.0f;
            width = bitmap.getWidth();
        } else {
            height = bitmap.getHeight();
            width = bitmap.getWidth();
        }
        float[] fArr = new float[9];
        matrix.getValues(fArr);
        matrix.postTranslate(height - fArr[2], width - fArr[5]);
        Bitmap bitmapCreateBitmap = Bitmap.createBitmap(bitmap.getHeight(), bitmap.getWidth(), Bitmap.Config.ARGB_8888);
        new Canvas(bitmapCreateBitmap).drawBitmap(bitmap, matrix, new Paint());
        return bitmapCreateBitmap;
    }

    public void onRecordingViewHide() {
        if (this.mRecordingView != null && Util.isVideoGroup(mCurrentMode)) {
            this.mRecordingView.show();
        }
    }

    public void onRecordingViewShow() {
        if (this.mRecordingView != null) {
            this.mRecordingView.hide();
        }
    }

    public void switchShutterMode(int i, boolean z) {
        mCurrentMode = i;
        Util.mCurrentShutterMode = i;
        if (i == 7) {
            this.mCameraAppUi.setCurrentMode(ICameraMode.CameraModeType.EXT_MODE_PANORAMA);
        } else if (i == 4) {
            android.util.Log.d("CameraActivity", "xxxxx                  aaa ");
            this.mCameraAppUi.setCurrentMode(ICameraMode.CameraModeType.EXT_MODE_PHOTO);
        } else if (i == 5 || i == 6) {
            this.mCameraAppUi.setCurrentMode(ICameraMode.CameraModeType.EXT_MODE_PHOTO);
        } else if (i == 3 || i == 0 || i == 1 || i == 2) {
            this.mCameraAppUi.setCurrentMode(ICameraMode.CameraModeType.EXT_MODE_VIDEO);
        }
        this.mCameraAppUi.refreshModeRelatedNoShutter();
        this.mCameraAppUi.updateManager();
        updateTimerDisplay();
    }

    public void updateVideoIcon(int i) {
        this.mCameraAppUi.updateVideoIcon(i);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateFakeNewPictureSizes(String str) throws Resources.NotFoundException, NumberFormatException {
        if ("pref_camera_picturesize_key".equals(str) || "pref_camera_picturesize_ratio_key".equals(str)) {
            com.mediatek.camera.setting.SettingUtils.updateFakeNewPictureSizes(this, getCameraId(), getListPreference("pref_camera_picturesize_key"));
        }
    }

    public void updateTimerDisplay() {
        if (this.mISelfTimeManager != null) {
            this.mISelfTimeManager.updateTimerXY();
        }
    }

    public LinearLayout getmInfoView() {
        this.mTvInfoView = (LinearLayout) findViewById(R.id.tv_info_view);
        return this.mTvInfoView;
    }

    public void getPerformZoom(int i, boolean z) {
        this.mCameraAppUi.getPerformZoom(i, z);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public synchronized void checkAwraStat(int i) {
        if (i < 2) {
            return;
        }
        int iIntValue = mUserActionMap.get("eq").intValue();
        int iIntValue2 = mUserActionMap.get("ge").intValue();
        int iIntValue3 = mUserActionMap.get("le").intValue();
        if (i == this.lastAwrw) {
            iIntValue++;
            mUserActionMap.put("eq", Integer.valueOf(iIntValue));
            mUserActionMap.put("ge", 0);
            mUserActionMap.put("le", 0);
        } else if (i > this.lastAwrw) {
            iIntValue2++;
            mUserActionMap.put("ge", Integer.valueOf(iIntValue2));
            mUserActionMap.put("eq", 0);
            mUserActionMap.put("le", 0);
        } else {
            iIntValue3++;
            mUserActionMap.put("le", Integer.valueOf(iIntValue3));
            mUserActionMap.put("eq", 0);
            mUserActionMap.put("ge", 0);
        }
        this.lastAwrw = i;
        if (iIntValue > 30 && this.lastAwrw != 0 && this.retory_count < 1) {
            this.retory_count++;
            mUserActionMap.put("eq", 0);
            writeAot();
        }
        if (iIntValue2 > 4) {
            mUserActionMap.put("ge", 0);
            this.mMainHandler.removeMessages(31);
            this.mMainHandler.sendEmptyMessage(31);
        }
        if (iIntValue3 > 4) {
            mUserActionMap.put("le", 0);
            this.mMainHandler.removeMessages(30);
            this.mMainHandler.sendEmptyMessage(30);
            android.util.Log.d("huanggq", "checkAwraStat 30 le");
        }
    }

    private void writeFileString(String[] strArr) throws IOException {
        try {
            DataOutputStream dataOutputStream = new DataOutputStream(Runtime.getRuntime().exec("sh").getOutputStream());
            for (String str : strArr) {
                dataOutputStream.writeBytes(str);
            }
            dataOutputStream.flush();
            dataOutputStream.close();
        } catch (IOException e) {
        }
    }

    private String getFileString(String str) {
        try {
            return new BufferedReader(new FileReader(str)).readLine();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }
}
