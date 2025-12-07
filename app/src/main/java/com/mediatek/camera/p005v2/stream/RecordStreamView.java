package com.mediatek.camera.p005v2.stream;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import com.mediatek.camera.R;
import com.mediatek.camera.debug.LogHelper;
import com.mediatek.camera.p005v2.stream.IRecordStream;
import java.util.Locale;

/* loaded from: classes.dex */
public class RecordStreamView {
    private static final LogHelper.Tag TAG = new LogHelper.Tag(RecordStreamView.class.getSimpleName());
    private final Activity mActivity;
    private TextView mCurrentRecordingSizeView;
    private final boolean mIsCaptureIntent;
    private long mLimitSize;
    private final Handler mMainHandler;
    private final ViewGroup mParentViewGroup;
    private ImageView mPauseResumeButton;
    private boolean mRecording;
    private final IRecordStream mRecordingController;
    private boolean mRecordingPaused;
    private long mRecordingPausedDuration;
    private TextView mRecordingSizeTotalView;
    private View mRecordingSizeViewGroup;
    private long mRecordingStartTime;
    private TextView mRecordingTimeView;
    private View mRecordingTimeViewGroup;
    private long mRecordingTotalDuration;
    private SeekBar mRecrodingSizeSeekBar;
    private View.OnClickListener mPauseResumeClickListener = new View.OnClickListener() { // from class: com.mediatek.camera.v2.stream.RecordStreamView.1
        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            if (RecordStreamView.this.mRecording) {
                if (RecordStreamView.this.mRecordingPaused) {
                    RecordStreamView.this.mRecordingController.resumeRecord();
                    RecordStreamView.this.mRecordingStartTime = SystemClock.uptimeMillis() - RecordStreamView.this.mRecordingPausedDuration;
                    RecordStreamView.this.mRecordingPausedDuration = 0L;
                    RecordStreamView.this.mRecordingPaused = false;
                    RecordStreamView.this.updateRecordingViewIcon();
                    return;
                }
                RecordStreamView.this.mRecordingController.pauseRecord();
                RecordStreamView.this.mRecordingPausedDuration = SystemClock.uptimeMillis() - RecordStreamView.this.mRecordingStartTime;
                RecordStreamView.this.mRecordingPaused = true;
                RecordStreamView.this.updateRecordingViewIcon();
            }
        }
    };
    private int mShowRecordingTimeViewIndicator = 0;
    private View mRecordingRootView = null;
    private final RecordingCallback mRecordingCallback = new RecordingCallback(this, null);

    public RecordStreamView(Activity activity, IRecordStream iRecordStream, ViewGroup viewGroup, boolean z) {
        this.mActivity = activity;
        this.mMainHandler = new RecordingHandler(this.mActivity.getMainLooper());
        this.mParentViewGroup = viewGroup;
        this.mIsCaptureIntent = z;
        this.mRecordingController = iRecordStream;
        this.mRecordingController.registerRecordingObserver(this.mRecordingCallback);
        this.mLimitSize = this.mActivity.getIntent().getLongExtra("android.intent.extra.sizeLimit", 0L);
    }

    public void close() {
        this.mRecordingController.unregisterCaptureObserver(this.mRecordingCallback);
    }

    private class RecordingHandler extends Handler {
        public RecordingHandler(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            switch (message.what) {
                case 0:
                    RecordStreamView.this.updateRecordingTime();
                    break;
            }
        }
    }

    private class RecordingCallback implements IRecordStream.RecordStreamStatus {
        /* synthetic */ RecordingCallback(RecordStreamView recordStreamView, RecordingCallback recordingCallback) {
            this();
        }

        private RecordingCallback() {
        }

        @Override // com.mediatek.camera.v2.stream.IRecordStream.RecordStreamStatus
        public void onRecordingStarted(boolean z) {
            LogHelper.m26i(RecordStreamView.TAG, "onRecordingStarted canUseExtra = " + z);
            RecordStreamView.this.mRecording = true;
            RecordStreamView.this.mRecordingPaused = false;
            RecordStreamView.this.mRecordingPausedDuration = 0L;
            RecordStreamView.this.mRecordingTotalDuration = 0L;
            RecordStreamView.this.show(z);
            if (!z) {
                RecordStreamView.this.mRecordingStartTime = SystemClock.uptimeMillis();
                RecordStreamView.this.updateRecordingTime();
            }
        }

        @Override // com.mediatek.camera.v2.stream.IRecordStream.RecordStreamStatus
        public void onRecordingStoped() {
            LogHelper.m26i(RecordStreamView.TAG, "onRecordingStoped");
            RecordStreamView.this.mRecording = false;
            RecordStreamView.this.mMainHandler.removeMessages(0);
            RecordStreamView.this.hide();
        }

        @Override // com.mediatek.camera.v2.stream.IRecordStream.RecordStreamStatus
        public void onInfo(int i, int i2) {
            if (i == 1998) {
                RecordStreamView.this.mRecordingStartTime = SystemClock.uptimeMillis();
                RecordStreamView.this.updateRecordingTime();
            }
            if (i == 895 && 0 < RecordStreamView.this.mLimitSize) {
                int i3 = (int) ((i2 * 100) / RecordStreamView.this.mLimitSize);
                LogHelper.m26i(RecordStreamView.TAG, "extra = " + i2 + " : mLimitSize = " + RecordStreamView.this.mLimitSize + "  : progress = " + i3);
                if (100 >= i3) {
                    RecordStreamView.this.mCurrentRecordingSizeView.setText(RecordStreamView.this.formatFileSize(i2));
                    RecordStreamView.this.mRecrodingSizeSeekBar.setProgress(i3);
                }
            }
        }

        @Override // com.mediatek.camera.v2.stream.IRecordStream.RecordStreamStatus
        public void onError(int i, int i2) {
            RecordStreamView.this.mRecording = false;
            RecordStreamView.this.mMainHandler.removeMessages(0);
            RecordStreamView.this.hide();
        }
    }

    private View getView() {
        LogHelper.m26i(TAG, "getView");
        View viewInflate = this.mActivity.getLayoutInflater().inflate(R.layout.recording_ext_v2, this.mParentViewGroup, true);
        View viewFindViewById = viewInflate.findViewById(R.id.recording_root_group);
        this.mRecordingTimeViewGroup = viewInflate.findViewById(R.id.recording_time_group);
        this.mRecordingTimeView = (TextView) viewInflate.findViewById(R.id.recording_time);
        this.mPauseResumeButton = (ImageView) viewInflate.findViewById(R.id.btn_pause_resume);
        this.mPauseResumeButton.setOnClickListener(this.mPauseResumeClickListener);
        this.mRecordingSizeViewGroup = (ViewGroup) viewInflate.findViewById(R.id.recording_size_group);
        this.mCurrentRecordingSizeView = (TextView) viewInflate.findViewById(R.id.recording_current);
        this.mRecrodingSizeSeekBar = (SeekBar) viewInflate.findViewById(R.id.recording_progress);
        this.mRecordingSizeTotalView = (TextView) viewInflate.findViewById(R.id.recording_total);
        this.mRecrodingSizeSeekBar.setOnTouchListener(new View.OnTouchListener() { // from class: com.mediatek.camera.v2.stream.RecordStreamView.2
            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });
        return viewFindViewById;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void show(boolean z) {
        LogHelper.m26i(TAG, "show");
        if (this.mRecordingRootView == null) {
            this.mRecordingRootView = getView();
        }
        updateRecordingViewIcon();
        this.mRecordingRootView.setVisibility(0);
        this.mRecordingTimeViewGroup.setVisibility(0);
        this.mRecordingTimeView.setText(formatTime(0L, false));
        this.mRecordingTimeView.setVisibility(0);
        this.mPauseResumeButton.setVisibility(0);
        if (this.mIsCaptureIntent && this.mLimitSize > 0 && z) {
            this.mCurrentRecordingSizeView.setText("0");
            this.mRecrodingSizeSeekBar.setProgress(0);
            this.mRecordingSizeTotalView.setText(formatFileSize(this.mLimitSize));
            this.mRecordingSizeViewGroup.setVisibility(0);
            return;
        }
        this.mRecordingSizeViewGroup.setVisibility(8);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void hide() {
        if (this.mRecordingRootView == null) {
            return;
        }
        this.mMainHandler.removeMessages(0);
        this.mRecordingRootView.setVisibility(4);
        this.mRecordingTimeViewGroup.setVisibility(4);
        this.mRecordingTimeView.setVisibility(4);
        this.mPauseResumeButton.setVisibility(4);
        this.mRecordingSizeViewGroup.setVisibility(4);
        this.mParentViewGroup.removeView(this.mRecordingRootView);
        this.mRecordingRootView = null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateRecordingViewIcon() {
        int i = R.drawable.ic_recording_indicator_play;
        int i2 = R.drawable.ic_recording_pause;
        if (this.mRecordingPaused) {
            i = R.drawable.ic_recording_indicator_pause;
            i2 = R.drawable.ic_recording_play;
        }
        this.mRecordingTimeView.setCompoundDrawablesWithIntrinsicBounds(this.mActivity.getResources().getDrawable(i), (Drawable) null, (Drawable) null, (Drawable) null);
        this.mPauseResumeButton.setImageResource(i2);
    }

    private void showTime(long j, boolean z) {
        String time = formatTime(j, z);
        if (this.mRecordingTimeView != null) {
            this.mRecordingTimeView.setText(time);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateRecordingTime() {
        if (!this.mRecording) {
            return;
        }
        this.mRecordingTotalDuration = SystemClock.uptimeMillis() - this.mRecordingStartTime;
        if (this.mRecordingPaused) {
            this.mRecordingTotalDuration = this.mRecordingPausedDuration;
        }
        showTime(this.mRecordingTotalDuration, false);
        this.mShowRecordingTimeViewIndicator = 1 - this.mShowRecordingTimeViewIndicator;
        if (this.mRecordingPaused && 1 == this.mShowRecordingTimeViewIndicator) {
            this.mRecordingTimeView.setVisibility(4);
        } else {
            this.mRecordingTimeView.setVisibility(0);
        }
        long j = 500;
        if (!this.mRecordingPaused) {
            j = 1000 - (this.mRecordingTotalDuration % 1000);
        }
        LogHelper.m23d(TAG, "[updateRecordingTime()],actualNextUpdateDelay = " + j);
        this.mMainHandler.sendEmptyMessageDelayed(0, j);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String formatFileSize(long j) {
        return (j / 1024) + "K";
    }

    private String formatTime(long j, boolean z) {
        int i = ((int) j) / 1000;
        int i2 = ((int) (j % 1000)) / 10;
        int i3 = i % 60;
        int i4 = (i / 60) % 60;
        int i5 = i / 3600;
        if (!z) {
            return i5 > 0 ? String.format(Locale.ENGLISH, "%d:%02d:%02d", Integer.valueOf(i5), Integer.valueOf(i4), Integer.valueOf(i3)) : String.format(Locale.ENGLISH, "%02d:%02d", Integer.valueOf(i4), Integer.valueOf(i3));
        }
        if (i5 > 0) {
            return String.format(Locale.ENGLISH, "%d:%02d:%02d.%02d", Integer.valueOf(i5), Integer.valueOf(i4), Integer.valueOf(i3), Integer.valueOf(i2));
        }
        return String.format(Locale.ENGLISH, "%02d:%02d.%02d", Integer.valueOf(i4), Integer.valueOf(i3), Integer.valueOf(i2));
    }
}
