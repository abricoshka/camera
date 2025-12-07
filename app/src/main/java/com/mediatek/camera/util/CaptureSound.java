package com.mediatek.camera.util;

import android.app.Activity;
import android.media.AudioAttributes;
import android.media.SoundPool;
import com.mediatek.camera.R;

/* loaded from: classes.dex */
public class CaptureSound {
    private static int mUserCount = 0;
    private SoundPool mBurstSound;
    private Activity mContext;
    private int mSoundId;
    private int mStreamId;

    public CaptureSound(Activity activity) {
        this.mContext = activity;
    }

    public void load() {
        Log.m31d("CaptureSound", "[load]mUserCount = " + mUserCount);
        mUserCount++;
        this.mBurstSound = new SoundPool.Builder().setMaxStreams(10).setAudioAttributes(new AudioAttributes.Builder().setInternalLegacyStreamType(7).build()).build();
        this.mSoundId = this.mBurstSound.load(this.mContext, R.raw.camera_shutter, 1);
    }

    public void play() {
        Log.m31d("CaptureSound", "[play]mBurstSound = " + this.mBurstSound);
        if (this.mBurstSound == null) {
            load();
        }
        this.mStreamId = this.mBurstSound.play(this.mSoundId, 1.0f, 1.0f, 1, -1, 1.0f);
        if (this.mStreamId == 0) {
            load();
            mUserCount--;
            this.mStreamId = this.mBurstSound.play(this.mSoundId, 1.0f, 1.0f, 1, -1, 1.0f);
            Log.m31d("CaptureSound", "[play]done mStreamId = " + this.mStreamId);
        }
    }

    public void stop() {
        Log.m31d("CaptureSound", "[stop]mStreamId = " + this.mStreamId);
        if (this.mBurstSound != null) {
            this.mBurstSound.stop(this.mStreamId);
        }
    }

    public void release() {
        Log.m31d("CaptureSound", "[release]mBurstSound = " + this.mBurstSound + ", user count = " + mUserCount);
        if (this.mBurstSound != null) {
            mUserCount--;
            this.mBurstSound.unload(this.mSoundId);
            this.mBurstSound.release();
            this.mBurstSound = null;
        }
    }
}
