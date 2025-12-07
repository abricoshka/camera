package com.mediatek.camera.p005v2.stream;

import android.media.CamcorderProfile;
import android.view.Surface;
import java.io.FileDescriptor;
import java.util.List;

/* loaded from: classes.dex */
public interface IRecordStream {

    public interface RecordStreamStatus {
        void onError(int i, int i2);

        void onInfo(int i, int i2);

        void onRecordingStarted(boolean z);

        void onRecordingStoped();
    }

    void enalbeAudioRecording(boolean z);

    Surface getRecordInputSurface();

    void pauseRecord();

    void prepareRecord();

    void registerRecordingObserver(RecordStreamStatus recordStreamStatus);

    void resumeRecord();

    void setAudioSource(int i);

    void setMaxDuration(int i);

    void setMaxFileSize(long j);

    void setMediaRecorderParameters(List<String> list);

    void setOrientationHint(int i);

    void setOutputFile(FileDescriptor fileDescriptor);

    void setOutputFile(String str);

    void setRecordingProfile(CamcorderProfile camcorderProfile);

    void setVideoSource(int i);

    void startRecord();

    void stopRecord(boolean z);

    void unregisterCaptureObserver(RecordStreamStatus recordStreamStatus);
}
