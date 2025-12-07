package com.android.camera.bridge;

import android.app.Activity;
import android.media.SoundPool;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import com.android.camera.CameraActivity;
import com.android.camera.manager.ViewManager;
import com.mediatek.camera.R;
import com.mediatek.camera.platform.ICameraAppUi;
import com.mediatek.camera.platform.ISelfTimeManager;
import com.mediatek.camera.util.Log;

/* loaded from: classes.dex */
public class SelfTimerManager extends ViewManager implements ISelfTimeManager {
    private CameraActivity mAct;
    private int mBeepOnce;
    private int mBeepTwice;
    private ICameraAppUi mCameraUi;
    private Activity mContext;
    private Animation mCountDownAnim;
    private final Handler mHandler;
    private boolean mIsLowStorageTag;
    private boolean mNeedPlaySound;
    private TextView mRemainingSecondsView;
    private int mRemainingSecs;
    private int mSelfTimerDuration;
    private SelfTimerListener mSelfTimerListener;
    private int mSelfTimerState;
    private SoundPool mSoundPool;

    public interface SelfTimerListener {
        void onTimerStart();

        void onTimerStop();

        void onTimerTimeout();
    }

    public SelfTimerManager(Activity activity, ICameraAppUi iCameraAppUi, CameraActivity cameraActivity) {
        super((CameraActivity) activity);
        this.mIsLowStorageTag = false;
        this.mNeedPlaySound = true;
        Log.m34i("SelfTimerManager", "[SelfTimerManager] constractor begin");
        this.mContext = activity;
        this.mCameraUi = iCameraAppUi;
        this.mAct = cameraActivity;
        this.mSoundPool = new SoundPool(1, 7, 0);
        this.mBeepOnce = this.mSoundPool.load(this.mContext, R.raw.beep_once, 1);
        this.mBeepTwice = this.mSoundPool.load(this.mContext, R.raw.beep_twice, 1);
        this.mHandler = new Handler(this.mContext.getMainLooper()) { // from class: com.android.camera.bridge.SelfTimerManager.1
            @Override // android.os.Handler
            public void handleMessage(Message message) {
                switch (message.what) {
                    case 9:
                        SelfTimerManager.this.selfTimerTimeout(SelfTimerManager.this.mRemainingSecs - 1);
                        break;
                }
            }
        };
        Log.m34i("SelfTimerManager", "[SelfTimerManager] constractor end");
    }

    public boolean isSelfTimerEnabled() {
        return this.mSelfTimerDuration > 0;
    }

    public void setSelfTimerDuration(String str) {
        int iIntValue = Integer.valueOf(str).intValue();
        if (iIntValue < 0 || iIntValue > 10000) {
            throw new RuntimeException("invalid self timer delay");
        }
        this.mSelfTimerDuration = iIntValue;
    }

    public boolean startSelfTimer() {
        Log.m36w("SelfTimerManager", "[startSelfTimer]mSelfTimerState = " + this.mSelfTimerState + ",mSelfTimerDuration = " + this.mSelfTimerDuration);
        if (this.mSelfTimerDuration <= 0 || this.mSelfTimerState != 0) {
            return this.mSelfTimerState == 1;
        }
        selfTimerStart();
        return true;
    }

    public void releaseSelfTimer() {
        this.mSoundPool.release();
        this.mSoundPool = null;
        this.mHandler.removeCallbacksAndMessages(null);
    }

    public synchronized void setTimerListener(SelfTimerListener selfTimerListener) {
        this.mSelfTimerListener = selfTimerListener;
    }

    public synchronized void stopSelfTimer() {
        if (this.mSelfTimerState != 0) {
            this.mHandler.removeMessages(9);
            this.mSelfTimerState = 0;
            hideTimerView();
            if (this.mSelfTimerListener != null) {
                this.mSelfTimerListener.onTimerStop();
            }
        }
    }

    @Override // com.mediatek.camera.platform.ISelfTimeManager
    public boolean isSelfTimerCounting() {
        return this.mSelfTimerState == 1;
    }

    @Override // com.android.camera.manager.ViewManager
    protected View getView() {
        this.mCountDownAnim = AnimationUtils.loadAnimation(this.mContext, R.anim.count_down_exit);
        View viewInflate = inflate(R.layout.count_down_to_capture);
        this.mRemainingSecondsView = (TextView) viewInflate.findViewById(R.id.remaining_seconds);
        updateTimerXY();
        return viewInflate;
    }

    private synchronized void selfTimerStart() {
        if (this.mSelfTimerState != 0 || this.mHandler.hasMessages(9) || this.mIsLowStorageTag) {
            Log.m36w("SelfTimerManager", "[selfTimerStart]mSelfTimerState = " + this.mSelfTimerState + ",mIsLowStorageTag = " + this.mIsLowStorageTag);
            return;
        }
        updateTimerXY();
        this.mSelfTimerListener.onTimerStart();
        this.mSelfTimerState = 1;
        showTimerView();
        Log.m34i("SelfTimerManager", "SelfTimer start");
        selfTimerTimeout(this.mSelfTimerDuration / 1000);
    }

    @Override // com.mediatek.camera.platform.ISelfTimeManager
    public void updateTimerXY() {
        if (this.mAct.isVideoModeGroup()) {
            if (this.mRemainingSecondsView != null) {
                this.mRemainingSecondsView.setTextSize(2, 70.0f);
                this.mRemainingSecondsView.setX(260.0f);
                this.mRemainingSecondsView.setY(320.0f);
                return;
            }
            return;
        }
        if (this.mRemainingSecondsView != null) {
            this.mRemainingSecondsView.setTextSize(2, 160.0f);
            this.mRemainingSecondsView.setX(0.0f);
            this.mRemainingSecondsView.setY(0.0f);
        }
    }

    private void showTimerView() {
        show();
        this.mCameraUi.hideAllViews();
        this.mRemainingSecondsView.setVisibility(0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public synchronized void selfTimerTimeout(int i) {
        Log.m34i("SelfTimerManager", "selfTimerTimeout: newVal = " + i);
        this.mRemainingSecs = i;
        if (i <= 0) {
            hideTimerView();
            this.mSelfTimerState = 2;
            if (this.mSelfTimerListener != null) {
                Log.m34i("SelfTimerManager", "onTimerTimeout");
                this.mSelfTimerListener.onTimerTimeout();
            }
            this.mSelfTimerState = 0;
        } else {
            this.mRemainingSecondsView.setText(String.format(this.mContext.getResources().getConfiguration().locale, "%d", Integer.valueOf(i)));
            if (this.mAct.isVideoModeGroup()) {
                this.mCountDownAnim.reset();
                this.mRemainingSecondsView.clearAnimation();
                this.mRemainingSecondsView.startAnimation(this.mCountDownAnim);
            }
            if (this.mNeedPlaySound) {
                if (i == 1) {
                    this.mSoundPool.play(this.mBeepTwice, 1.0f, 1.0f, 0, 0, 1.0f);
                } else if (i <= 3) {
                    this.mSoundPool.play(this.mBeepOnce, 1.0f, 1.0f, 0, 0, 1.0f);
                }
            }
            this.mHandler.sendEmptyMessageDelayed(9, 1000L);
        }
    }

    private void hideTimerView() {
        this.mRemainingSecondsView.setVisibility(4);
        hide();
        this.mCameraUi.showAllViews();
        this.mCameraUi.dismissInfo();
    }
}
