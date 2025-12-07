package com.mediatek.camera.mode;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.media.CamcorderProfile;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.os.SystemClock;
import com.android.camera.Storage;
import com.mediatek.camera.ISettingCtrl;
import com.mediatek.camera.platform.IModuleCtrl;
import com.mediatek.camera.util.Log;
import java.io.File;
import java.io.IOException;

/* loaded from: classes.dex */
public class VideoModeHelper {
    private static final String[] PREF_CAMERA_VIDEO_HD_RECORDING_ENTRYVALUES = {"normal", "indoor"};
    private static final Long VIDEO_4G_SIZE = 4294967296L;
    private Activity mActivity;
    private IModuleCtrl mIModuleCtrl;
    private ISettingCtrl mISettingCtrl;

    public VideoModeHelper(Activity activity, IModuleCtrl iModuleCtrl, ISettingCtrl iSettingCtrl) {
        this.mActivity = activity;
        this.mIModuleCtrl = iModuleCtrl;
        this.mISettingCtrl = iSettingCtrl;
    }

    public long getRecorderMaxSize(long j) {
        long availableSpace = Storage.getAvailableSpace() - Storage.RECORD_LOW_STORAGE_THRESHOLD;
        return (j <= 0 || j >= availableSpace) ? availableSpace : j;
    }

    public long getRequestSizeLimit(CamcorderProfile camcorderProfile, boolean z) {
        if (this.mIModuleCtrl.isVideoCaptureIntent()) {
            return this.mIModuleCtrl.getIntent().getLongExtra("android.intent.extra.sizeLimit", 0L);
        }
        return 0L;
    }

    public int getRequestDurationLimited() {
        if (this.mIModuleCtrl.isVideoCaptureIntent()) {
            return this.mIModuleCtrl.getIntent().getIntExtra("android.intent.extra.durationLimit", 0);
        }
        return 0;
    }

    public boolean canShowShareVideoIcon() {
        Bundle extras;
        boolean z = true;
        if (this.mIModuleCtrl.isVideoCaptureIntent() && (extras = this.mIModuleCtrl.getIntent().getExtras()) != null) {
            z = extras.getBoolean("CanShare", true);
        }
        Log.m34i("VideoModeHelper", "[canShowShareVideoIcon]can show = " + z);
        return z;
    }

    public void closeVideoFileDescriptor(ParcelFileDescriptor parcelFileDescriptor) throws IOException {
        if (parcelFileDescriptor != null) {
            try {
                parcelFileDescriptor.close();
            } catch (IOException e) {
                Log.m33e("VideoModeHelper", "[closeVideoFileDescriptor] Fail to close fd", e);
            }
        }
    }

    public long getDuration(String str) throws IOException {
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        try {
            mediaMetadataRetriever.setDataSource(str);
            return Long.valueOf(mediaMetadataRetriever.extractMetadata(9)).longValue();
        } catch (IllegalArgumentException e) {
            return -1L;
        } catch (RuntimeException e2) {
            return -2L;
        } finally {
            mediaMetadataRetriever.release();
        }
    }

    public String convertOutputFormatToMimeType(int i) {
        if (i == 2) {
            Log.m34i("VideoModeHelper", "[convertOutputFormatToMimeType] return video/mp4");
            return "video/mp4";
        }
        Log.m34i("VideoModeHelper", "[convertOutputFormatToMimeType] return video/m3gpp");
        return "video/3gpp";
    }

    public String convertOutputFormatToFileExt(int i) {
        if (i == 2) {
            Log.m34i("VideoModeHelper", "[convertOutputFormatToFileExt] return .mp4");
            return ".mp4";
        }
        Log.m34i("VideoModeHelper", "[convertOutputFormatToFileExt] return .3gp");
        return ".3gp";
    }

    public boolean getMicrophone() {
        return "on".equals(this.mISettingCtrl.getSettingValue("pref_camera_recordaudio_key"));
    }

    public void renameVideoFile(String str) {
        File file = new File(str);
        File file2 = new File(str + "_" + SystemClock.currentThreadTimeMillis());
        if (!file.renameTo(file2)) {
            Log.m34i("VideoModeHelper", "[renameVideoFile] Rename to new file " + file2.getName());
        }
    }

    public void deleteVideoFile(String str) {
        if (!new File(str).delete()) {
            Log.m34i("VideoModeHelper", "[deleteVideoFile] Could not delete " + str);
        }
    }

    public void doReturnToCaller(boolean z, Uri uri) {
        Log.m31d("VideoModeHelper", "[doReturnToCaller](" + z + "),uri = " + uri);
        Intent intent = new Intent();
        int i = 0;
        if (z) {
            i = -1;
            intent.setData(uri);
            intent.addFlags(1);
            Log.m31d("VideoModeHelper", "[doReturnToCaller](" + z + "),mCurrentVideoUri = " + uri);
        }
        this.mIModuleCtrl.backToCallingActivity(i, intent);
    }

    public void startPlayVideoActivity(Uri uri, CamcorderProfile camcorderProfile) {
        Log.m31d("VideoModeHelper", "[startPlayVideoActivity], mCurrentVideoUri = " + uri + ",profile = " + camcorderProfile);
        if (camcorderProfile == null) {
            Log.m32e("VideoModeHelper", "[startPlayVideoActivity] current proflie is error,please check!");
        }
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addFlags(1);
        intent.putExtra("CanShare", canShowShareVideoIcon());
        intent.setDataAndType(uri, convertOutputFormatToMimeType(camcorderProfile.fileFormat));
        try {
            this.mActivity.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Log.m33e("VideoModeHelper", "[startPlayVideoActivity] Couldn't view video " + uri, e);
        }
    }
}
