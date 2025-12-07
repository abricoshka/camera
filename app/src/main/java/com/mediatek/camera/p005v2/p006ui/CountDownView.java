package com.mediatek.camera.p005v2.p006ui;

import android.content.Context;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.mediatek.camera.R;
import com.mediatek.camera.debug.LogHelper;

/* loaded from: classes.dex */
public class CountDownView extends FrameLayout {
    private static final LogHelper.Tag TAG = new LogHelper.Tag(CountDownView.class.getSimpleName());
    private final Handler mHandler;
    private OnCountDownStatusListener mListener;
    private final RectF mPreviewArea;
    private TextView mRemainingSecondsView;
    private int mRemainingSecs;

    public interface OnCountDownStatusListener {
        void onCountDownFinished();

        void onRemainingSecondsChanged(int i);
    }

    public CountDownView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mRemainingSecs = 0;
        this.mHandler = new MainHandler(this, null);
        this.mPreviewArea = new RectF();
    }

    public boolean isCountingDown() {
        return this.mRemainingSecs > 0;
    }

    public void onPreviewAreaChanged(RectF rectF) {
        LogHelper.m26i(TAG, "onPreviewAreaChanged " + rectF);
        this.mPreviewArea.set(rectF);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void remainingSecondsChanged(int i) {
        this.mRemainingSecs = i;
        if (this.mListener != null) {
            this.mListener.onRemainingSecondsChanged(this.mRemainingSecs);
        }
        if (i == 0) {
            setVisibility(4);
            if (this.mListener != null) {
                this.mListener.onCountDownFinished();
                return;
            }
            return;
        }
        this.mRemainingSecondsView.setText(String.format(getResources().getConfiguration().locale, "%d", Integer.valueOf(i)));
        startFadeOutAnimation();
        this.mHandler.sendEmptyMessageDelayed(1, 1000L);
    }

    private void startFadeOutAnimation() {
        int measuredWidth = this.mRemainingSecondsView.getMeasuredWidth();
        int measuredHeight = this.mRemainingSecondsView.getMeasuredHeight();
        this.mRemainingSecondsView.setScaleX(1.0f);
        this.mRemainingSecondsView.setScaleY(1.0f);
        this.mRemainingSecondsView.setTranslationX(this.mPreviewArea.centerX() - (measuredWidth / 2));
        this.mRemainingSecondsView.setTranslationY(this.mPreviewArea.centerY() - (measuredHeight / 2));
        this.mRemainingSecondsView.setPivotX(measuredWidth / 2);
        this.mRemainingSecondsView.setPivotY(measuredHeight / 2);
        this.mRemainingSecondsView.setAlpha(1.0f);
        this.mRemainingSecondsView.animate().scaleX(2.5f).scaleY(2.5f).alpha(0.0f).setDuration(800L).start();
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mRemainingSecondsView = (TextView) findViewById(R.id.remaining_seconds);
    }

    public void setCountDownStatusListener(OnCountDownStatusListener onCountDownStatusListener) {
        this.mListener = onCountDownStatusListener;
    }

    public void startCountDown(int i) {
        if (i <= 0) {
            LogHelper.m28w(TAG, "Invalid input for countdown timer: " + i + " seconds");
            return;
        }
        if (isCountingDown()) {
            cancelCountDown();
        }
        setVisibility(0);
        remainingSecondsChanged(i);
    }

    public void cancelCountDown() {
        if (this.mRemainingSecs > 0) {
            this.mRemainingSecs = 0;
            this.mHandler.removeMessages(1);
            setVisibility(4);
        }
    }

    private class MainHandler extends Handler {
        /* synthetic */ MainHandler(CountDownView countDownView, MainHandler mainHandler) {
            this();
        }

        private MainHandler() {
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            if (message.what == 1) {
                CountDownView.this.remainingSecondsChanged(CountDownView.this.mRemainingSecs - 1);
            }
        }
    }
}
