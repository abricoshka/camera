package com.android.camera;

import android.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.admin.DevicePolicyManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.hardware.Camera;
import android.hardware.display.DisplayManager;
import android.hardware.display.WifiDisplayStatus;
import android.location.Location;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.os.Process;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import com.android.camera.CameraManager;
import com.android.camera.p001ui.Rotatable;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/* loaded from: classes.dex */
public class Util {
    public static int mCurrentShutterMode;
    private static boolean mWfdEnabled;
    private static AlertDialog sAlertDialog;
    private static float sPixelDensity = 1.0f;
    private static boolean sIsMAVSupport = true;
    private static boolean sIsPANORAMASupport = true;
    private static Uri sLastUri = null;
    private static int[] sLocation = new int[2];

    public static boolean isFullPreviewMode() {
        if (mCurrentShutterMode == 4 || mCurrentShutterMode == 5 || mCurrentShutterMode == 6) {
            return false;
        }
        return true;
    }

    private Util() {
    }

    private static boolean isMountPointValid(Uri uri, ContentResolver contentResolver) {
        String string;
        Cursor cursorQuery = contentResolver.query(uri, new String[]{"_data"}, null, null, null);
        if (cursorQuery == null) {
            string = "";
        } else {
            try {
                if (!cursorQuery.moveToFirst()) {
                    string = "";
                } else {
                    string = cursorQuery.getString(0);
                }
            } finally {
                cursorQuery.close();
            }
        }
        boolean zEquals = Storage.getFileDirectory().equals(new File(string).getParent());
        Log.m5d("Util", "isMountPointValid(" + uri + ") path =" + string + ", Storage.MOUNT_POINT =" + Storage.getMountPoint() + ", return " + zEquals);
        return zEquals;
    }

    public static int getExifOrientation(ExifInterface exifInterface) {
        int attributeInt;
        if (exifInterface == null || (attributeInt = exifInterface.getAttributeInt("Orientation", -1)) == -1) {
            return 0;
        }
        switch (attributeInt) {
        }
        return 0;
    }

    public static int dpToPixel(int i) {
        return Math.round(sPixelDensity * i);
    }

    public static Bitmap rotateAndMirror(Bitmap bitmap, int i, boolean z) {
        if ((i != 0 || z) && bitmap != null) {
            Matrix matrix = new Matrix();
            if (z) {
                matrix.postScale(-1.0f, 1.0f);
                i = (i + 360) % 360;
                if (i == 0 || i == 180) {
                    matrix.postTranslate(bitmap.getWidth(), 0.0f);
                } else if (i == 90 || i == 270) {
                    matrix.postTranslate(bitmap.getHeight(), 0.0f);
                } else {
                    throw new IllegalArgumentException("Invalid degrees=" + i);
                }
            }
            if (i != 0) {
                matrix.postRotate(i, bitmap.getWidth() / 2.0f, bitmap.getHeight() / 2.0f);
            }
            try {
                Bitmap bitmapCreateBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                if (bitmap != bitmapCreateBitmap) {
                    bitmap.recycle();
                    return bitmapCreateBitmap;
                }
                return bitmap;
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
                return bitmap;
            }
        }
        return bitmap;
    }

    public static void assertError(boolean z) {
        if (!z) {
            throw new AssertionError();
        }
    }

    public static void openCamera(Activity activity, boolean z, int i) throws CameraDisabledException, InterruptedException, CameraHardwareException {
        Log.m8i("Util", "openCamera begin isPIP = " + z);
        if (((DevicePolicyManager) activity.getSystemService("device_policy")).getCameraDisabled(null)) {
            throw new CameraDisabledException();
        }
        try {
            if (z) {
                retryOpen(activity, 2, CameraHolder.instance().getBackCameraId());
                retryOpen(activity, 2, CameraHolder.instance().getFrontCameraId());
            } else {
                CameraManager.CameraProxy cameraProxyRetryOpen = retryOpen(activity, 2, i);
                Camera.Parameters parameters = cameraProxyRetryOpen.getParameters();
                if (isSetPanelToNative(parameters, activity)) {
                    ParametersHelper.setPanelSize(parameters, getPanelSizeStr(activity));
                    cameraProxyRetryOpen.setParameters(parameters);
                }
            }
        } catch (CameraHardwareException e) {
            CameraHolder.instance().release(false);
            throw e;
        }
    }

    public static boolean bottomGraphicIsMainCamera(Context context) {
        boolean z = ((CameraActivity) context).getCameraDevice() == CameraHolder.instance().getCameraProxy(CameraHolder.instance().getBackCameraId());
        Log.m8i("Util", "bottomGraphicIsMainCamera = " + z);
        return z;
    }

    private static CameraManager.CameraProxy retryOpen(Activity activity, int i, int i2) throws InterruptedException, CameraHardwareException {
        for (int i3 = 0; i3 < i; i3++) {
            try {
                Log.m8i("Util", "[retryOpen] cameraId = " + i2);
                CameraManager.CameraProxy cameraProxyOpen = CameraHolder.instance().open(i2);
                cameraProxyOpen.setErrorCallback(new CameraErrorCallback(activity));
                return cameraProxyOpen;
            } catch (CameraHardwareException e) {
                if (i3 == 0) {
                    try {
                        Thread.sleep(1000L);
                    } catch (InterruptedException e2) {
                        e2.printStackTrace();
                    }
                } else {
                    if ("eng".equals(Build.TYPE)) {
                        Log.m9i("Util", "Open Camera fail", e);
                        throw e;
                    }
                    throw e;
                }
            }
        }
        throw new CameraHardwareException(new RuntimeException("Should never get here"));
    }

    private static String getPanelSizeStr(Context context) {
        Display defaultDisplay = ((WindowManager) context.getSystemService("window")).getDefaultDisplay();
        Point point = new Point();
        defaultDisplay.getRealSize(point);
        return "" + Math.max(point.x, point.y) + "x" + Math.min(point.x, point.y);
    }

    public static void showErrorAndFinish(Activity activity, int i) {
        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() { // from class: com.android.camera.Util.1
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i2) {
                Process.killProcess(Process.myPid());
            }
        };
        if (activity.isFinishing() || sAlertDialog != null) {
            return;
        }
        sAlertDialog = new AlertDialog.Builder(activity).setCancelable(false).setIconAttribute(R.attr.alertDialogIcon).setTitle("").setMessage(i).setNeutralButton(com.mediatek.camera.R.string.dialog_ok, onClickListener).show();
    }

    public static void hideAlertDialog(Activity activity) {
        if (sAlertDialog != null) {
            activity.runOnUiThread(new Runnable() { // from class: com.android.camera.Util.2
                @Override // java.lang.Runnable
                public void run() {
                    Util.sAlertDialog.dismiss();
                    AlertDialog unused = Util.sAlertDialog = null;
                }
            });
        }
    }

    public static int clamp(int i, int i2, int i3) {
        if (i > i3) {
            return i3;
        }
        if (i < i2) {
            return i2;
        }
        return i;
    }

    public static int getDisplayRotation(Activity activity) {
        if (!isWfdEnabled(activity) && !FeatureSwitcher.isTablet()) {
            return 0;
        }
        switch (activity.getWindowManager().getDefaultDisplay().getRotation()) {
        }
        return 0;
    }

    public static int getDisplayOrientation(int i, int i2) {
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        Camera.getCameraInfo(i2, cameraInfo);
        if (cameraInfo.facing == 1) {
            return (360 - ((cameraInfo.orientation + i) % 360)) % 360;
        }
        return ((cameraInfo.orientation - i) + 360) % 360;
    }

    public static boolean isWfdEnabled(Context context) {
        mWfdEnabled = false;
        WifiDisplayStatus wifiDisplayStatus = ((DisplayManager) context.getSystemService("display")).getWifiDisplayStatus();
        mWfdEnabled = wifiDisplayStatus.getActiveDisplayState() == 2;
        Log.m10v("Util", "isWfdEnabled() mWfdStatus=" + wifiDisplayStatus + ", return " + mWfdEnabled);
        return mWfdEnabled;
    }

    public static int roundOrientation(int i, int i2) {
        boolean z = true;
        if (i2 != -1) {
            int iAbs = Math.abs(i - i2);
            if (Math.min(iAbs, 360 - iAbs) < 50) {
                z = false;
            }
        }
        if (z) {
            return (((i + 45) / 90) * 90) % 360;
        }
        return i2;
    }

    public static boolean isBottomHasHighFrameRate(Context context) {
        Camera.Parameters parameters = ((CameraActivity) context).getParameters();
        Camera.Parameters topParameters = ((CameraActivity) context).getTopParameters();
        int previewFrameRate = parameters == null ? 0 : parameters.getPreviewFrameRate();
        int previewFrameRate2 = topParameters == null ? 0 : topParameters.getPreviewFrameRate();
        Log.m8i("Util", "isBottomHasHighFrameRate bottomFrameRate = " + previewFrameRate + " topFrameTate = " + previewFrameRate2);
        return previewFrameRate >= previewFrameRate2;
    }

    public static int getCameraFacingIntentExtras(Activity activity) {
        int backCameraId;
        int intExtra = activity.getIntent().getIntExtra("android.intent.extras.CAMERA_FACING", -1);
        if (isFrontCameraIntent(intExtra)) {
            int frontCameraId = CameraHolder.instance().getFrontCameraId();
            if (frontCameraId != -1) {
                return frontCameraId;
            }
        } else if (isBackCameraIntent(intExtra) && (backCameraId = CameraHolder.instance().getBackCameraId()) != -1) {
            return backCameraId;
        }
        return -1;
    }

    private static boolean isFrontCameraIntent(int i) {
        return i == 1;
    }

    private static boolean isBackCameraIntent(int i) {
        return i == 0;
    }

    public static int[] getRelativeLocation(View view, View view2) {
        view.getLocationInWindow(sLocation);
        int i = sLocation[0];
        int i2 = sLocation[1];
        view2.getLocationInWindow(sLocation);
        int[] iArr = sLocation;
        iArr[0] = iArr[0] - i;
        int[] iArr2 = sLocation;
        iArr2[1] = iArr2[1] - i2;
        return sLocation;
    }

    public static boolean isUriValid(Uri uri, ContentResolver contentResolver) throws IOException {
        if (uri == null) {
            return false;
        }
        try {
            ParcelFileDescriptor parcelFileDescriptorOpenFileDescriptor = contentResolver.openFileDescriptor(uri, "r");
            if (parcelFileDescriptorOpenFileDescriptor == null) {
                Log.m6e("Util", "Fail to open URI. URI=" + uri);
                return false;
            }
            parcelFileDescriptorOpenFileDescriptor.close();
            return isMountPointValid(uri, contentResolver);
        } catch (IOException e) {
            return false;
        }
    }

    public static void dumpRect(RectF rectF, String str) {
        Log.m10v("Util", str + "=(" + rectF.left + "," + rectF.top + "," + rectF.right + "," + rectF.bottom + ")");
    }

    public static void rectFToRect(RectF rectF, Rect rect) {
        rect.left = Math.round(rectF.left);
        rect.top = Math.round(rectF.top);
        rect.right = Math.round(rectF.right);
        rect.bottom = Math.round(rectF.bottom);
    }

    public static int[] pointFToPoint(float[] fArr) {
        return new int[]{Math.round(fArr[0]), Math.round(fArr[1])};
    }

    public static void prepareMatrix(Matrix matrix, boolean z, int i, int i2, int i3) {
        Log.m5d("Util", "prepareMatrix mirror =" + z + " displayOrientation=" + i + " viewWidth=" + i2 + " viewHeight=" + i3);
        matrix.setScale(z ? -1 : 1, 1.0f);
        matrix.postRotate(i);
        matrix.postScale(i2 / 2000.0f, i3 / 2000.0f);
        matrix.postTranslate(i2 / 2.0f, i3 / 2.0f);
    }

    public static void fadeIn(View view, float f, float f2, long j) {
        if (view.getVisibility() == 0) {
            return;
        }
        view.setVisibility(0);
        AlphaAnimation alphaAnimation = new AlphaAnimation(f, f2);
        alphaAnimation.setDuration(j);
        view.startAnimation(alphaAnimation);
    }

    public static void fadeIn(View view) {
        fadeIn(view, 0.0f, 1.0f, 400L);
        view.setEnabled(true);
    }

    public static void fadeOut(View view) {
        if (view.getVisibility() != 0) {
            return;
        }
        view.setEnabled(false);
        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
        alphaAnimation.setDuration(400L);
        view.startAnimation(alphaAnimation);
        view.setVisibility(8);
    }

    public static int getJpegRotation(int i, int i2) {
        Camera.CameraInfo cameraInfo = CameraHolder.instance().getCameraInfo()[i];
        if (i2 != -1) {
            if (cameraInfo.facing == 1) {
                return ((cameraInfo.orientation - i2) + 360) % 360;
            }
            return (cameraInfo.orientation + i2) % 360;
        }
        return cameraInfo.orientation;
    }

    public static void setGpsParameters(Camera.Parameters parameters, Location location) {
        boolean z = true;
        parameters.removeGpsData();
        parameters.setGpsTimestamp(System.currentTimeMillis() / 1000);
        if (location != null) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            if (latitude == 0.0d && longitude == 0.0d) {
                z = false;
            }
            if (z) {
                Log.m5d("Util", "Set gps location");
                parameters.setGpsLatitude(latitude);
                parameters.setGpsLongitude(longitude);
                parameters.setGpsProcessingMethod(location.getProvider().toUpperCase());
                if (location.hasAltitude()) {
                    parameters.setGpsAltitude(location.getAltitude());
                } else {
                    parameters.setGpsAltitude(0.0d);
                }
                if (location.getTime() != 0) {
                    parameters.setGpsTimestamp(location.getTime() / 1000);
                }
            }
        }
    }

    public static class ImageFileNamer {
        private SimpleDateFormat mFormat;
        private long mLastDate;
        private String mSameDngName;
        private int mSameSecondCount;
        private boolean mNextIsSameName = false;
        private int mRawNameFlag = 0;
        private int mTypeflag = 3;

        public ImageFileNamer(String str) {
            this.mFormat = new SimpleDateFormat(str, Locale.ENGLISH);
        }

        public String generateName(long j) {
            String str = this.mFormat.format(new Date(j));
            if (j / 1000 == this.mLastDate / 1000) {
                this.mSameSecondCount++;
                return str + "_" + this.mSameSecondCount;
            }
            this.mLastDate = j;
            this.mSameSecondCount = 0;
            return str;
        }

        public String generateRawName(long j, int i) {
            if (i == 4) {
                this.mTypeflag = 2;
            } else {
                this.mTypeflag = 1;
            }
            if (this.mRawNameFlag != 0 && (this.mRawNameFlag & this.mTypeflag) != 0) {
                this.mNextIsSameName = false;
                this.mRawNameFlag = 0;
            }
            this.mRawNameFlag |= this.mTypeflag;
            if (this.mNextIsSameName && this.mSameDngName != null) {
                this.mNextIsSameName = false;
                return this.mSameDngName;
            }
            String str = this.mFormat.format(new Date(j));
            if (j / 1000 == this.mLastDate / 1000) {
                this.mSameSecondCount++;
                str = str + "_" + this.mSameSecondCount;
            } else {
                this.mLastDate = j;
                this.mSameSecondCount = 0;
            }
            String str2 = str + "_RAW";
            this.mNextIsSameName = true;
            this.mSameDngName = str2;
            return str2;
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    public static void setOrientation(View view, int i, boolean z) {
        if (view == 0) {
            return;
        }
        if (view instanceof Rotatable) {
            ((Rotatable) view).setOrientation(i, z);
            return;
        }
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            int childCount = viewGroup.getChildCount();
            for (int i2 = 0; i2 < childCount; i2++) {
                setOrientation(viewGroup.getChildAt(i2), i, z);
            }
        }
    }

    public static void enterCameraPQMode() {
        Log.m5d("Util", "enterCameraPQMode()");
    }

    public static void exitCameraPQMode() {
        Log.m5d("Util", "exitCameraPQMode()");
    }

    /* JADX WARN: Removed duplicated region for block: B:10:0x0020  */
    /* JADX WARN: Removed duplicated region for block: B:42:0x008e  */
    /* JADX WARN: Removed duplicated region for block: B:51:0x007e A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static long getDeviceRam() throws java.lang.Throwable {
        /*
            r2 = 0
            r4 = 0
            java.lang.String r0 = "/proc/meminfo"
            java.io.BufferedReader r1 = new java.io.BufferedReader     // Catch: java.io.IOException -> L5b java.io.FileNotFoundException -> L6b java.lang.Throwable -> L7b
            java.io.FileReader r3 = new java.io.FileReader     // Catch: java.io.IOException -> L5b java.io.FileNotFoundException -> L6b java.lang.Throwable -> L7b
            r3.<init>(r0)     // Catch: java.io.IOException -> L5b java.io.FileNotFoundException -> L6b java.lang.Throwable -> L7b
            r0 = 8
            r1.<init>(r3, r0)     // Catch: java.io.IOException -> L5b java.io.FileNotFoundException -> L6b java.lang.Throwable -> L7b
            java.lang.String r0 = r1.readLine()     // Catch: java.lang.Throwable -> L87 java.io.FileNotFoundException -> L8a java.io.IOException -> L8c
            if (r0 == 0) goto L19
            r2 = r0
        L19:
            if (r1 == 0) goto L1e
            r1.close()     // Catch: java.io.IOException -> L56
        L1e:
            if (r2 == 0) goto L8e
            r0 = 58
            int r0 = r2.indexOf(r0)
            r1 = 107(0x6b, float:1.5E-43)
            int r1 = r2.indexOf(r1)
            int r0 = r0 + 1
            java.lang.String r0 = r2.substring(r0, r1)
            java.lang.String r0 = r0.trim()
            int r0 = java.lang.Integer.parseInt(r0)
            long r0 = (long) r0
            java.lang.String r2 = "Util"
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "getDeviceRam = "
            java.lang.StringBuilder r3 = r3.append(r4)
            java.lang.StringBuilder r3 = r3.append(r0)
            java.lang.String r3 = r3.toString()
            com.android.camera.Log.m5d(r2, r3)
        L55:
            return r0
        L56:
            r0 = move-exception
            r0.printStackTrace()
            goto L1e
        L5b:
            r0 = move-exception
            r1 = r2
        L5d:
            r0.printStackTrace()     // Catch: java.lang.Throwable -> L87
            if (r1 == 0) goto L1e
            r1.close()     // Catch: java.io.IOException -> L66
            goto L1e
        L66:
            r0 = move-exception
            r0.printStackTrace()
            goto L1e
        L6b:
            r0 = move-exception
            r1 = r2
        L6d:
            r0.printStackTrace()     // Catch: java.lang.Throwable -> L87
            if (r1 == 0) goto L1e
            r1.close()     // Catch: java.io.IOException -> L76
            goto L1e
        L76:
            r0 = move-exception
            r0.printStackTrace()
            goto L1e
        L7b:
            r0 = move-exception
        L7c:
            if (r2 == 0) goto L81
            r2.close()     // Catch: java.io.IOException -> L82
        L81:
            throw r0
        L82:
            r1 = move-exception
            r1.printStackTrace()
            goto L81
        L87:
            r0 = move-exception
            r2 = r1
            goto L7c
        L8a:
            r0 = move-exception
            goto L6d
        L8c:
            r0 = move-exception
            goto L5d
        L8e:
            r0 = r4
            goto L55
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.camera.Util.getDeviceRam():long");
    }

    private static boolean isSetPanelToNative(Camera.Parameters parameters, Activity activity) {
        boolean zIsDisplayRotateSupported = ParametersHelper.isDisplayRotateSupported(parameters);
        Log.m5d("Util", "isSetPanelToNative displayRotSupported = " + zIsDisplayRotateSupported + ", isWfdEnable = " + mWfdEnabled);
        if (zIsDisplayRotateSupported) {
            return !mWfdEnabled;
        }
        return false;
    }

    public static boolean isVideoGroup(int i) {
        return i == 2 || i == 3 || i == 0 || i == 1;
    }
}
