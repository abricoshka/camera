package com.mediatek.camera.p005v2.mode.normal;

import android.content.Intent;
import android.media.CamcorderProfile;
import android.media.MediaMetadataRetriever;
import android.os.ParcelFileDescriptor;
import com.mediatek.camera.R;
import com.mediatek.camera.debug.LogHelper;
import com.mediatek.camera.p005v2.platform.app.AppController;
import com.mediatek.camera.p005v2.services.CameraServices;
import com.mediatek.camera.p005v2.services.storage.IStorageService;
import com.mediatek.camera.p005v2.setting.ISettingServant;
import com.mediatek.camera.p005v2.setting.SettingCtrl;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/* loaded from: classes.dex */
public class VideoHelper {
    private static final LogHelper.Tag TAG = new LogHelper.Tag(VideoHelper.class.getSimpleName());
    private static final Long VIDEO_4G_SIZE = 4294967296L;
    private Intent mIntent;
    private boolean mIsCaptureIntent;
    private SettingCtrl mSettingCtroller;
    private ISettingServant mSettingServant;
    private IStorageService mStorageService;
    private StringBuffer mVideoTempPath = new StringBuffer();

    public VideoHelper(CameraServices cameraServices, Intent intent, boolean z, SettingCtrl settingCtrl) {
        this.mStorageService = cameraServices.getStorageService();
        this.mIntent = intent;
        this.mIsCaptureIntent = z;
        this.mSettingCtroller = settingCtrl;
    }

    public int getRecordingQuality(int i) {
        this.mSettingServant = this.mSettingCtroller.getSettingServant(String.valueOf(i));
        int iIntValue = Integer.valueOf(this.mSettingServant.getSettingValue("pref_video_quality_key")).intValue();
        Intent intent = this.mIntent;
        if (intent.hasExtra("android.intent.extra.videoQuality")) {
            iIntValue = intent.getIntExtra("android.intent.extra.videoQuality", 0);
            if (iIntValue <= 0) {
                iIntValue = 0;
            } else if (!CamcorderProfile.hasProfile(i, iIntValue)) {
                iIntValue = CamcorderProfile.hasProfile(i, 5) ? 5 : 0;
            }
        }
        LogHelper.m26i(TAG, "[getRecordingQuality] videoQualityValue = " + iIntValue);
        return iIntValue;
    }

    public CamcorderProfile fetchProfile(int i, int i2) {
        LogHelper.m26i(TAG, "[fetchProfile](" + i + ",  cameraId = " + i2 + ")");
        CamcorderProfile camcorderProfile = CamcorderProfile.get(i2, i);
        if (camcorderProfile != null) {
            LogHelper.m26i(TAG, "[fetchProfile()] mProfile.videoFrameRate=" + camcorderProfile.videoFrameRate + ", mProfile.videoFrameWidth=" + camcorderProfile.videoFrameWidth + ", mProfile.videoFrameHeight=" + camcorderProfile.videoFrameHeight + ", mProfile.audioBitRate=" + camcorderProfile.audioBitRate + ", mProfile.videoBitRate=" + camcorderProfile.videoBitRate + ", mProfile.quality=" + camcorderProfile.quality + ", mProfile.duration=" + camcorderProfile.duration);
        }
        return camcorderProfile;
    }

    public long getRequestSizeLimit(CamcorderProfile camcorderProfile, boolean z, boolean z2, Intent intent) {
        if (z2) {
            return intent.getLongExtra("android.intent.extra.sizeLimit", 0L);
        }
        return 0L;
    }

    public long getRecorderMaxSize(long j) {
        long recordStorageSpace = this.mStorageService.getRecordStorageSpace();
        return (j <= 0 || j >= recordStorageSpace) ? recordStorageSpace : j;
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

    public void closeVideoFileDescriptor(ParcelFileDescriptor parcelFileDescriptor) throws IOException {
        if (parcelFileDescriptor != null) {
            try {
                parcelFileDescriptor.close();
            } catch (IOException e) {
                LogHelper.m25e(TAG, "[closeVideoFileDescriptor] Fail to close fd", e);
            }
        }
    }

    public String createFileTitle(long j, AppController appController) {
        return new SimpleDateFormat(appController.getActivity().getString(R.string.video_file_name_format)).format(new Date(j));
    }

    public String generateVideoFileName(int i, String str) {
        this.mVideoTempPath.delete(0, this.mVideoTempPath.length());
        this.mVideoTempPath.append(this.mStorageService.getFileDirectory());
        this.mVideoTempPath.append("/videorecorder");
        this.mVideoTempPath.append(convertOutputFormatToFileExt(i));
        if (str != null) {
            this.mVideoTempPath.append("_");
            this.mVideoTempPath.append(str);
        }
        this.mVideoTempPath.append(".tmp");
        LogHelper.m26i(TAG, "[generateVideoFilename] mVideoFilename = " + this.mVideoTempPath.toString());
        return this.mVideoTempPath.toString();
    }

    public String convertOutputFormatToMimeType(int i) {
        if (i == 2) {
            return "video/mp4";
        }
        return "video/3gpp";
    }

    public String convertOutputFormatToFileExt(int i) {
        if (i == 2) {
            LogHelper.m26i(TAG, "[convertOutputFormatToFileExt] return .mp4");
            return ".mp4";
        }
        LogHelper.m26i(TAG, "[convertOutputFormatToFileExt] return .3gp");
        return ".3gp";
    }
}
