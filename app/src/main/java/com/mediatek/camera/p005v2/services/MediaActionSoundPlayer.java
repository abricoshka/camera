package com.mediatek.camera.p005v2.services;

import android.annotation.TargetApi;
import android.media.MediaActionSound;
import com.mediatek.camera.debug.LogHelper;

@TargetApi(16)
/* loaded from: classes.dex */
class MediaActionSoundPlayer {
    private static final LogHelper.Tag TAG = new LogHelper.Tag(MediaActionSoundPlayer.class.getSimpleName());
    private MediaActionSound mSound = new MediaActionSound();

    protected MediaActionSoundPlayer() {
        this.mSound.load(2);
        this.mSound.load(3);
        this.mSound.load(1);
        this.mSound.load(0);
    }

    protected synchronized void play(int i) {
        switch (i) {
            case 0:
                this.mSound.play(1);
                break;
            case 1:
                this.mSound.play(2);
                break;
            case 2:
                this.mSound.play(3);
                break;
            case 3:
                this.mSound.play(0);
                break;
            default:
                LogHelper.m28w(TAG, "Unrecognized action:" + i);
                break;
        }
    }

    protected void release() {
        if (this.mSound != null) {
            this.mSound.release();
            this.mSound = null;
        }
    }
}
