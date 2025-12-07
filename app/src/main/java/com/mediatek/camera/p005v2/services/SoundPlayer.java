package com.mediatek.camera.p005v2.services;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.SparseIntArray;
import com.android.camera.p002v2.util.ApiHelper;
import com.mediatek.camera.debug.LogHelper;
import java.util.HashMap;

/* loaded from: classes.dex */
public class SoundPlayer implements SoundPool.OnLoadCompleteListener {
    private static final LogHelper.Tag TAG = new LogHelper.Tag(SoundPlayer.class.getSimpleName());
    private final Context mAppContext;
    private float mVolume;
    private final SparseIntArray mResourceToSoundId = new SparseIntArray();
    private final HashMap<Integer, Boolean> mSoundIDReadyMap = new HashMap<>();
    private final SoundPool mSoundPool = new SoundPool(1, getAudioTypeForSoundPool(), 0);
    private int mSoundIDToPlay = 0;

    public SoundPlayer(Context context) {
        this.mAppContext = context;
        this.mSoundPool.setOnLoadCompleteListener(this);
    }

    private void loadSound(int i) {
        this.mResourceToSoundId.put(i, this.mSoundPool.load(this.mAppContext, i, 1));
    }

    public void play(int i, float f) {
        this.mSoundIDToPlay = this.mResourceToSoundId.get(i, 0);
        this.mVolume = f;
        if (this.mSoundIDToPlay == 0) {
            loadSound(i);
            this.mSoundIDToPlay = this.mResourceToSoundId.get(i);
        } else if (!this.mSoundIDReadyMap.get(Integer.valueOf(this.mSoundIDToPlay)).booleanValue()) {
            LogHelper.m28w(TAG, "sound id " + this.mSoundIDToPlay + " is in loading and not ready yet");
        } else {
            this.mSoundPool.play(this.mSoundIDToPlay, f, f, 0, 0, 1.0f);
        }
    }

    private void unloadSound(int i) {
        Integer numValueOf = Integer.valueOf(this.mResourceToSoundId.get(i));
        if (numValueOf == null) {
            throw new IllegalStateException("Sound not loaded. Must call #loadSound first.");
        }
        this.mSoundPool.unload(numValueOf.intValue());
    }

    public void unloadSound() {
        int size = this.mResourceToSoundId.size();
        for (int i = 0; i < size; i++) {
            unloadSound(this.mResourceToSoundId.keyAt(i));
        }
        this.mResourceToSoundId.clear();
    }

    public void release() {
        this.mSoundPool.release();
    }

    @Override // android.media.SoundPool.OnLoadCompleteListener
    public void onLoadComplete(SoundPool soundPool, int i, int i2) {
        if (i2 != 0) {
            LogHelper.m24e(TAG, "onLoadComplete : " + i + " load failed , status is " + i2);
            return;
        }
        LogHelper.m23d(TAG, "onLoadComplete : " + i + " load success");
        this.mSoundIDReadyMap.put(Integer.valueOf(i), true);
        if (i == this.mSoundIDToPlay) {
            this.mSoundIDToPlay = 0;
            this.mSoundPool.play(i, this.mVolume, this.mVolume, 0, 0, 1.0f);
        }
    }

    private static int getAudioTypeForSoundPool() {
        return ApiHelper.getIntFieldIfExists(AudioManager.class, "STREAM_SYSTEM_ENFORCED", null, 2);
    }
}
