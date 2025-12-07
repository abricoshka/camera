package com.mediatek.camera.p005v2.services;

import android.content.Context;

/* loaded from: classes.dex */
public class SoundPlaybackImpl implements ISoundPlayback {
    private Context mContext;
    private MediaActionSoundPlayer mMediaActionSoundPlayer = new MediaActionSoundPlayer();
    private SoundPlayer mSoundPlayer;

    public SoundPlaybackImpl(Context context) {
        this.mContext = context;
        this.mSoundPlayer = new SoundPlayer(this.mContext);
    }

    @Override // com.mediatek.camera.p005v2.services.ISoundPlayback
    public void play(int i) {
        this.mMediaActionSoundPlayer.play(i);
    }

    @Override // com.mediatek.camera.p005v2.services.ISoundPlayback
    public void play(int i, float f) {
        this.mSoundPlayer.play(i, f);
    }

    public void pause() {
        this.mSoundPlayer.unloadSound();
    }

    public void release() {
        this.mMediaActionSoundPlayer.release();
        this.mSoundPlayer.unloadSound();
        this.mSoundPlayer.release();
    }
}
