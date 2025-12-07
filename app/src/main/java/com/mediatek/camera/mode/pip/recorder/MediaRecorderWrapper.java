package com.mediatek.camera.mode.pip.recorder;

import android.media.MediaRecorder;
import android.view.Surface;
import com.mediatek.camera.util.Log;
import com.mediatek.camera.util.Util;
import java.io.IOException;

/* loaded from: classes.dex */
public class MediaRecorderWrapper implements MediaRecorder.OnInfoListener {
    private MediaRecorder mMediaRecorder;
    private OnInfoListener mOnInfoListener;

    public interface OnInfoListener {
        void onInfo(MediaRecorderWrapper mediaRecorderWrapper, int i, int i2);
    }

    @Override // android.media.MediaRecorder.OnInfoListener
    public void onInfo(MediaRecorder mediaRecorder, int i, int i2) {
        if (this.mOnInfoListener != null) {
            this.mOnInfoListener.onInfo(this, i, i2);
        }
    }

    public MediaRecorderWrapper() {
        Log.m31d("MediaRecorderWrapper", "MediaRecorderWrapper");
        this.mMediaRecorder = new MediaRecorder();
    }

    public void setOutputFormat(int i) throws IllegalStateException {
        Log.m31d("MediaRecorderWrapper", "Initialize >>> setOutputFormat = " + i);
        this.mMediaRecorder.setOutputFormat(i);
    }

    public void setMaxFileSize(long j) throws IllegalArgumentException {
        Log.m31d("MediaRecorderWrapper", "Initialize >>> setMaxFileSize = " + j);
        this.mMediaRecorder.setMaxFileSize(j);
    }

    public void setOutputFile(String str) throws IllegalStateException {
        Log.m31d("MediaRecorderWrapper", "Initialize >>> setOutputFile = " + str);
        this.mMediaRecorder.setOutputFile(str);
    }

    public void setOrientationHint(int i) throws IllegalArgumentException {
        Log.m31d("MediaRecorderWrapper", "Initialize >>> setOrientationHint = " + i);
        this.mMediaRecorder.setOrientationHint(i);
    }

    public void setLocation(long j, long j2) throws IllegalArgumentException {
        Log.m31d("MediaRecorderWrapper", "Initialize >>> setLocation latitude = " + j + " longitude = " + j2);
        this.mMediaRecorder.setLocation(j, j2);
    }

    public void setVideoEncoder(int i) throws IllegalStateException {
        Log.m31d("MediaRecorderWrapper", "Initialize >>> setVideoEncoder video_encoder =  " + i);
        this.mMediaRecorder.setVideoEncoder(i);
    }

    public void setVideoSource(int i) throws IllegalStateException {
        Log.m31d("MediaRecorderWrapper", "Initialize >>> setVideoSource video_source =  " + i);
        this.mMediaRecorder.setVideoSource(i);
    }

    public void setVideoFrameRate(int i) throws IllegalStateException {
        Log.m31d("MediaRecorderWrapper", "Initialize >>> setVideoFrameRate videoFrameRate =  " + i);
        this.mMediaRecorder.setVideoFrameRate(i);
    }

    public void setVideoSize(int i, int i2) throws IllegalStateException {
        Log.m31d("MediaRecorderWrapper", "Initialize >>> setVideoSize videoFrameWidth =  " + i + " videoFrameHeight = " + i2);
        this.mMediaRecorder.setVideoSize(i, i2);
    }

    public void setVideoEncodingBitRate(int i) {
        Log.m31d("MediaRecorderWrapper", "Initialize >>> setVideoEncodingBitRate bitRate =  " + i);
        this.mMediaRecorder.setVideoEncodingBitRate(i);
    }

    public void setAudioEncoder(int i) throws IllegalStateException {
        Log.m31d("MediaRecorderWrapper", "Initialize >>> setAudioEncoder audio_encoder =  " + i);
        this.mMediaRecorder.setAudioEncoder(i);
    }

    public void setAudioSource(int i) throws IllegalStateException {
        Log.m31d("MediaRecorderWrapper", "Initialize >>> setAudioSource audioSource =  " + i);
        this.mMediaRecorder.setAudioSource(i);
    }

    public void setAudioEncodingBitRate(int i) {
        Log.m31d("MediaRecorderWrapper", "Initialize >>> setAudioEncodingBitRate bitRate =  " + i);
        this.mMediaRecorder.setAudioEncodingBitRate(i);
    }

    public void setAudioChannels(int i) {
        Log.m31d("MediaRecorderWrapper", "Initialize >>> setAudioChannels numChannels =  " + i);
        this.mMediaRecorder.setAudioChannels(i);
    }

    public void setAudioSamplingRate(int i) {
        Log.m31d("MediaRecorderWrapper", "Initialize >>> setAudioSamplingRate samplingRate =  " + i);
        this.mMediaRecorder.setAudioSamplingRate(i);
    }

    public void setParametersExtra() {
        setMediaRecorderParameters(this.mMediaRecorder);
    }

    public void prepare() throws IllegalStateException, IOException {
        Log.m31d("MediaRecorderWrapper", "prepare begin");
        this.mMediaRecorder.prepare();
        Log.m31d("MediaRecorderWrapper", "prepare end");
    }

    public Surface getSurface() throws IllegalStateException {
        return this.mMediaRecorder.getSurface();
    }

    public void start() throws IllegalStateException {
        Log.m31d("MediaRecorderWrapper", "start begin");
        this.mMediaRecorder.start();
        Log.m31d("MediaRecorderWrapper", "start end");
    }

    public void pause(MediaRecorderWrapper mediaRecorderWrapper) throws IllegalStateException {
        Log.m31d("MediaRecorderWrapper", "pause begin");
        if (mediaRecorderWrapper == null) {
            Log.m32e("MediaRecorderWrapper", "Null MediaRecorderWrapper!");
        } else {
            this.mMediaRecorder.pause();
            Log.m31d("MediaRecorderWrapper", "pause end");
        }
    }

    public void resume(MediaRecorderWrapper mediaRecorderWrapper) throws IllegalStateException {
        Log.m31d("MediaRecorderWrapper", "resume begin");
        if (mediaRecorderWrapper == null) {
            Log.m32e("MediaRecorderWrapper", "[resume]Null MediaRecorderWrapper!");
        } else {
            this.mMediaRecorder.resume();
            Log.m31d("MediaRecorderWrapper", "resume end");
        }
    }

    public void stop() throws IllegalStateException {
        Log.m31d("MediaRecorderWrapper", "stop begin");
        this.mMediaRecorder.stop();
        Log.m31d("MediaRecorderWrapper", "stop end");
    }

    public void release() {
        Log.m31d("MediaRecorderWrapper", "release begin");
        this.mMediaRecorder.release();
        Log.m31d("MediaRecorderWrapper", "release end");
    }

    public void setOnInfoListener(OnInfoListener onInfoListener) {
        Log.m31d("MediaRecorderWrapper", "setOnInfoListener listener = " + onInfoListener);
        this.mOnInfoListener = onInfoListener;
        this.mMediaRecorder.setOnInfoListener(this);
    }

    private void setMediaRecorderParameters(MediaRecorder mediaRecorder) {
        try {
            Util.setParametersExtra(mediaRecorder, "media-recorder-info=1998");
            Util.setParametersExtra(mediaRecorder, "media-recorder-info=899");
            Util.setParametersExtra(mediaRecorder, "media-recorder-info=1999");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
