package com.mediatek.camera.p005v2.stream;

import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.view.Surface;
import com.mediatek.camera.debug.LogHelper;
import com.mediatek.camera.p005v2.stream.IRecordStream;
import com.mediatek.camera.util.ReflectUtil;
import com.mediatek.media.MediaRecorderEx;
import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import junit.framework.Assert;

/* loaded from: classes.dex */
public class RecordStream implements IRecordStream {
    private double mCaptureRateFps;
    private FileDescriptor mFileDescriptor;
    private String mFileName;
    private float mLocationLatitude;
    private float mLocationLongitude;
    private int mMaxDurationMs;
    private long mMaxFileSizeBytes;
    protected MediaRecorder mMediaRecorder;
    private boolean mNeedRecordingAudio;
    private CamcorderProfile mProfile;
    private int mRecordingOrientation;
    private int mVideoSource;
    private static final LogHelper.Tag TAG = new LogHelper.Tag(RecordStream.class.getSimpleName());
    private static final String[] PREF_CAMERA_VIDEO_HD_RECORDING_ENTRYVALUES = {"normal", "indoor"};
    private static final Class[] METHOD_TYPES = {String.class};
    protected final ArrayList<IRecordStream.RecordStreamStatus> mRecordingStreamObservers = new ArrayList<>();
    private boolean isExtraSuccess = false;
    private int mAudioeSource = 5;
    private List<String> mRecorderParameters = new ArrayList();
    private MediaRecorder.OnInfoListener mInfoListener = new MediaRecorder.OnInfoListener() { // from class: com.mediatek.camera.v2.stream.RecordStream.1
        @Override // android.media.MediaRecorder.OnInfoListener
        public void onInfo(MediaRecorder mediaRecorder, int i, int i2) {
            LogHelper.m26i(RecordStream.TAG, "onInfo what = " + i + " extra = " + i2);
            Iterator<T> it = RecordStream.this.mRecordingStreamObservers.iterator();
            while (it.hasNext()) {
                ((IRecordStream.RecordStreamStatus) it.next()).onInfo(i, i2);
            }
        }
    };
    private MediaRecorder.OnErrorListener mErrorListener = new MediaRecorder.OnErrorListener() { // from class: com.mediatek.camera.v2.stream.RecordStream.2
        @Override // android.media.MediaRecorder.OnErrorListener
        public void onError(MediaRecorder mediaRecorder, int i, int i2) {
            LogHelper.m26i(RecordStream.TAG, "onError what = " + i + " extra = " + i2);
            Iterator<T> it = RecordStream.this.mRecordingStreamObservers.iterator();
            while (it.hasNext()) {
                ((IRecordStream.RecordStreamStatus) it.next()).onError(i, i2);
            }
        }
    };

    public void releaseRecordStream() {
    }

    @Override // com.mediatek.camera.p005v2.stream.IRecordStream
    public void registerRecordingObserver(IRecordStream.RecordStreamStatus recordStreamStatus) {
        if (recordStreamStatus != null && (!this.mRecordingStreamObservers.contains(recordStreamStatus))) {
            this.mRecordingStreamObservers.add(recordStreamStatus);
        }
    }

    @Override // com.mediatek.camera.p005v2.stream.IRecordStream
    public void unregisterCaptureObserver(IRecordStream.RecordStreamStatus recordStreamStatus) {
        if (recordStreamStatus != null && this.mRecordingStreamObservers.contains(recordStreamStatus)) {
            this.mRecordingStreamObservers.remove(recordStreamStatus);
        }
    }

    @Override // com.mediatek.camera.p005v2.stream.IRecordStream
    public void setRecordingProfile(CamcorderProfile camcorderProfile) {
        this.mProfile = camcorderProfile;
    }

    @Override // com.mediatek.camera.p005v2.stream.IRecordStream
    public void setMaxDuration(int i) {
        this.mMaxDurationMs = i;
    }

    @Override // com.mediatek.camera.p005v2.stream.IRecordStream
    public void setMaxFileSize(long j) {
        this.mMaxFileSizeBytes = j;
    }

    @Override // com.mediatek.camera.p005v2.stream.IRecordStream
    public void setOutputFile(String str) {
        this.mFileName = str;
    }

    @Override // com.mediatek.camera.p005v2.stream.IRecordStream
    public void setOutputFile(FileDescriptor fileDescriptor) {
        this.mFileDescriptor = fileDescriptor;
    }

    @Override // com.mediatek.camera.p005v2.stream.IRecordStream
    public void setOrientationHint(int i) {
        this.mRecordingOrientation = i;
    }

    @Override // com.mediatek.camera.p005v2.stream.IRecordStream
    public void setMediaRecorderParameters(List<String> list) {
        this.mRecorderParameters = list;
    }

    @Override // com.mediatek.camera.p005v2.stream.IRecordStream
    public void enalbeAudioRecording(boolean z) {
        this.mNeedRecordingAudio = z;
    }

    @Override // com.mediatek.camera.p005v2.stream.IRecordStream
    public void setAudioSource(int i) {
        this.mAudioeSource = i;
    }

    @Override // com.mediatek.camera.p005v2.stream.IRecordStream
    public void setVideoSource(int i) {
        this.mVideoSource = i;
    }

    @Override // com.mediatek.camera.p005v2.stream.IRecordStream
    public void prepareRecord() throws IllegalStateException, IOException, IllegalArgumentException {
        checkPrepareCondition();
        this.mMediaRecorder = new MediaRecorder();
        initializeRecorder();
        doPrepareRecord();
        this.mMediaRecorder.setOnErrorListener(getErrorListener());
        this.mMediaRecorder.setOnInfoListener(getInfoListener());
    }

    @Override // com.mediatek.camera.p005v2.stream.IRecordStream
    public void startRecord() throws IllegalStateException {
        this.mMediaRecorder.start();
        Iterator<T> it = this.mRecordingStreamObservers.iterator();
        while (it.hasNext()) {
            ((IRecordStream.RecordStreamStatus) it.next()).onRecordingStarted(this.isExtraSuccess);
        }
    }

    @Override // com.mediatek.camera.p005v2.stream.IRecordStream
    public void pauseRecord() throws IllegalStateException {
        try {
            this.mMediaRecorder.pause();
        } catch (IllegalStateException e) {
            LogHelper.m24e(TAG, "Could not pause media recorder. ");
        }
    }

    @Override // com.mediatek.camera.p005v2.stream.IRecordStream
    public void resumeRecord() throws IllegalStateException {
        this.mMediaRecorder.resume();
    }

    @Override // com.mediatek.camera.p005v2.stream.IRecordStream
    public void stopRecord(boolean z) throws IllegalStateException {
        try {
            this.mMediaRecorder.stop();
            this.mMediaRecorder.release();
            if (!z) {
                deleteVideoFile();
            }
            Iterator<T> it = this.mRecordingStreamObservers.iterator();
            while (it.hasNext()) {
                ((IRecordStream.RecordStreamStatus) it.next()).onRecordingStoped();
            }
            this.mProfile = null;
        } catch (RuntimeException e) {
            deleteVideoFile();
            throw e;
        }
    }

    public boolean deleteVideoFile() {
        if (this.mFileName == null) {
            return true;
        }
        boolean zDelete = new File(this.mFileName).delete();
        this.mFileName = null;
        LogHelper.m26i(TAG, "deleteVideoFile result:" + zDelete);
        return zDelete;
    }

    @Override // com.mediatek.camera.p005v2.stream.IRecordStream
    public Surface getRecordInputSurface() {
        return this.mMediaRecorder.getSurface();
    }

    protected void checkPrepareCondition() {
        boolean z = true;
        Assert.assertNotNull(this.mProfile);
        if (this.mFileDescriptor == null && this.mFileName == null) {
            z = false;
        }
        Assert.assertTrue(z);
    }

    protected void initializeRecorder() throws IllegalStateException, IllegalArgumentException {
        Assert.assertNotNull(this.mMediaRecorder);
        if (this.mNeedRecordingAudio) {
            this.mMediaRecorder.setAudioSource(this.mAudioeSource);
            this.mMediaRecorder.setAudioChannels(this.mProfile.audioChannels);
        }
        this.mMediaRecorder.setVideoSource(this.mVideoSource);
        this.mMediaRecorder.setOutputFormat(this.mProfile.fileFormat);
        this.mMediaRecorder.setVideoFrameRate(this.mProfile.videoFrameRate);
        this.mMediaRecorder.setVideoSize(this.mProfile.videoFrameWidth, this.mProfile.videoFrameHeight);
        this.mMediaRecorder.setVideoEncodingBitRate(this.mProfile.videoBitRate);
        this.mMediaRecorder.setVideoEncoder(this.mProfile.videoCodec);
        MediaRecorderEx.setVideoBitOffSet(this.mMediaRecorder, 1, true);
        if (this.mNeedRecordingAudio) {
            this.mMediaRecorder.setAudioEncoder(this.mProfile.audioCodec);
            this.mMediaRecorder.setAudioEncodingBitRate(this.mProfile.audioBitRate);
            this.mMediaRecorder.setAudioSamplingRate(this.mProfile.audioSampleRate);
        }
        if (this.mCaptureRateFps > 0.0d) {
            this.mMediaRecorder.setCaptureRate(this.mCaptureRateFps);
        }
        if (this.mLocationLatitude > 0.0f && this.mLocationLongitude > 0.0f) {
            this.mMediaRecorder.setLocation(this.mLocationLatitude, this.mLocationLongitude);
        }
        if (this.mMaxFileSizeBytes > 0) {
            this.mMediaRecorder.setMaxFileSize(this.mMaxFileSizeBytes);
        }
        if (this.mFileDescriptor != null) {
            this.mMediaRecorder.setOutputFile(this.mFileDescriptor);
        } else {
            this.mMediaRecorder.setOutputFile(this.mFileName);
        }
        this.mMediaRecorder.setOrientationHint(this.mRecordingOrientation);
        this.mMediaRecorder.setMaxDuration(this.mMaxDurationMs);
        setRecorderParameters(this.mRecorderParameters);
    }

    protected MediaRecorder.OnErrorListener getErrorListener() {
        return this.mErrorListener;
    }

    protected MediaRecorder.OnInfoListener getInfoListener() {
        return this.mInfoListener;
    }

    protected void doPrepareRecord() throws IllegalStateException, IOException {
        try {
            this.mMediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalStateException e2) {
            e2.printStackTrace();
        }
    }

    private void setRecorderParameters(List<String> list) {
        if (this.mRecorderParameters != null) {
            for (int i = 0; i < this.mRecorderParameters.size(); i++) {
                try {
                    setParametersExtra(this.mMediaRecorder, this.mRecorderParameters.get(i));
                } catch (Exception e) {
                    this.isExtraSuccess = false;
                    e.printStackTrace();
                    return;
                }
            }
            this.isExtraSuccess = true;
        }
    }

    public void setParametersExtra(MediaRecorder mediaRecorder, String str) {
        try {
            ReflectUtil.callMethodOnObject(mediaRecorder, ReflectUtil.getMethod(Class.forName("android.media.MediaRecorder"), "setParameter", METHOD_TYPES), str);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
