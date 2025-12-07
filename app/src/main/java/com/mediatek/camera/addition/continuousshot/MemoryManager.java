package com.mediatek.camera.addition.continuousshot;

import android.app.ActivityManager;
import android.content.ComponentCallbacks2;
import android.content.Context;
import android.content.res.Configuration;
import com.android.internal.util.MemInfoReader;
import com.mediatek.camera.util.Log;

/* loaded from: classes.dex */
public class MemoryManager implements ComponentCallbacks2 {
    private int mCount;
    private final long mDvmSlowdownThreshold;
    private final long mDvmStopThreshold;
    private long mLeftStorage;
    private final long mMaxDvmMemory;
    private final MemInfoReader mMemInfoReader;
    private final long mMiniMemFreeMb;
    private long mPengdingSize;
    private long mStartTime;
    private int mSuitableSpeed;
    private final long mSystemSlowdownThreshold;
    private final long mSystemStopThreshold;
    private long mUsedStorage;
    private MemoryAction mMemoryActon = MemoryAction.NORMAL;
    private Runtime mRuntime = Runtime.getRuntime();

    public enum MemoryAction {
        NORMAL,
        ADJSUT_SPEED,
        STOP;

        /* renamed from: values, reason: to resolve conflict with enum method */
        public static MemoryAction[] valuesCustom() {
            return values();
        }
    }

    public MemoryManager(Context context) {
        context.registerComponentCallbacks(this);
        this.mMaxDvmMemory = this.mRuntime.maxMemory();
        this.mDvmSlowdownThreshold = (long) (this.mMaxDvmMemory * 0.4f);
        this.mDvmStopThreshold = (long) (this.mMaxDvmMemory * 0.1f);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        ((ActivityManager) context.getSystemService("activity")).getMemoryInfo(memoryInfo);
        this.mMiniMemFreeMb = (memoryInfo.foregroundAppThreshold / 1024) / 1024;
        this.mSystemSlowdownThreshold = 100 / (toMb(this.mMaxDvmMemory) <= 512 ? 2L : 1L);
        this.mSystemStopThreshold = this.mSystemSlowdownThreshold / 2;
        this.mMemInfoReader = new MemInfoReader();
    }

    @Override // android.content.ComponentCallbacks
    public void onLowMemory() {
        Log.m31d("MemoryManager", "[onLowMemory]...");
        this.mMemoryActon = MemoryAction.STOP;
    }

    @Override // android.content.ComponentCallbacks2
    public void onTrimMemory(int i) {
        Log.m31d("MemoryManager", "[onTrimMemory]level: " + i);
        switch (i) {
            case 15:
            case 20:
            case 40:
                this.mMemoryActon = MemoryAction.ADJSUT_SPEED;
                break;
            case 60:
            case 80:
                this.mMemoryActon = MemoryAction.STOP;
                break;
            default:
                this.mMemoryActon = MemoryAction.NORMAL;
                break;
        }
    }

    @Override // android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration configuration) {
    }

    public void init(long j) {
        this.mMemoryActon = MemoryAction.NORMAL;
        this.mLeftStorage = j;
        this.mUsedStorage = 0L;
        this.mPengdingSize = 0L;
        this.mCount = 0;
    }

    public void start() {
        this.mStartTime = System.currentTimeMillis();
    }

    public MemoryAction getMemoryAction(long j, long j2) {
        Log.m31d("MemoryManager", "[getMemoryAction]pictureSize=" + toMb(j) + " MB, pendingSize=" + toMb(j2) + " MB");
        this.mCount++;
        this.mUsedStorage += j;
        this.mPengdingSize = j2;
        long jCurrentTimeMillis = System.currentTimeMillis() - this.mStartTime;
        long j3 = (this.mCount * 1024) / jCurrentTimeMillis;
        long j4 = ((this.mUsedStorage - this.mPengdingSize) / jCurrentTimeMillis) / 1024;
        if (this.mUsedStorage >= this.mLeftStorage) {
            Log.m31d("MemoryManager", "[getMemoryAction]Storage size check, need to stop");
            return MemoryAction.STOP;
        }
        this.mSuitableSpeed = (int) (((((this.mUsedStorage - this.mPengdingSize) * this.mCount) * 1024) / jCurrentTimeMillis) / this.mUsedStorage);
        this.mMemInfoReader.readMemInfo();
        long[] rawInfo = this.mMemInfoReader.getRawInfo();
        long j5 = rawInfo[3] / 1024;
        long j6 = rawInfo[1] / 1024;
        if (j5 <= j6) {
            j5 = j6;
        }
        long j7 = j5 - this.mMiniMemFreeMb;
        if (j7 < this.mSystemStopThreshold) {
            Log.m31d("MemoryManager", "[getMemoryAction]System memory check, need to stop");
            return MemoryAction.STOP;
        }
        if (j7 < this.mSystemSlowdownThreshold) {
            Log.m31d("MemoryManager", "[getMemoryAction]System memory check,need to slowdown");
            return MemoryAction.ADJSUT_SPEED;
        }
        if (this.mPengdingSize >= this.mDvmSlowdownThreshold) {
            Log.m31d("MemoryManager", "[getMemoryAction]DVM memory check,need to slowdown");
            return MemoryAction.ADJSUT_SPEED;
        }
        if (this.mMaxDvmMemory - (this.mRuntime.totalMemory() - this.mRuntime.freeMemory()) <= this.mDvmStopThreshold) {
            Log.m31d("MemoryManager", "[getMemoryAction]DVM memory check,need to stop");
            return MemoryAction.STOP;
        }
        return MemoryAction.NORMAL;
    }

    public int getSuitableContinuousShotSpeed() {
        if (this.mSuitableSpeed < 1) {
            this.mSuitableSpeed = 1;
            Log.m34i("MemoryManager", "[getSuitableContinuousShotSpeed]Current performance is very poor!");
        }
        return this.mSuitableSpeed;
    }

    private long toMb(long j) {
        return (j / 1024) / 1024;
    }
}
