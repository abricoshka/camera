package com.mediatek.camera.p004ui;

import android.app.Activity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import com.mediatek.camera.R;
import com.mediatek.camera.util.Log;
import java.util.Locale;

/* loaded from: classes.dex */
public class RecordingView extends CameraView implements View.OnClickListener {
    private boolean mChange;
    private long mCurrent;
    private boolean mIsPauseResumeVisible;
    private boolean mIsRecordingSizeVisible;
    private boolean mIsRecordinging;
    private boolean mIsTimeVisible;
    private View.OnClickListener mListener;
    private int mMax;
    private ImageView mPauseResume;
    private int mProgress;
    private ImageView mRecordingIcon;
    private TextView mRecordingSizeCurrent;
    private View mRecordingSizeGroup;
    private TextView mRecordingSizeTotal;
    private TextView mRecordingTime;
    private SeekBar mRecrodingSizeProgress;
    private String mTimeText;
    private long mTotal;

    public RecordingView(Activity activity) {
        super(activity);
        this.mMax = 100;
        this.mChange = false;
    }

    @Override // com.mediatek.camera.p004ui.CameraView
    public View getView() {
        View viewInflate = inflate(R.layout.recording_ext);
        this.mRecordingTime = (TextView) viewInflate.findViewById(R.id.recording_time);
        this.mPauseResume = (ImageView) viewInflate.findViewById(R.id.btn_pause_resume);
        this.mPauseResume.setOnClickListener(this);
        this.mRecordingIcon = (ImageView) viewInflate.findViewById(R.id.icon_recording_indicator_play);
        this.mRecordingSizeGroup = viewInflate.findViewById(R.id.recording_size_group);
        this.mRecordingSizeCurrent = (TextView) viewInflate.findViewById(R.id.recording_current);
        this.mRecordingSizeTotal = (TextView) viewInflate.findViewById(R.id.recording_total);
        this.mRecrodingSizeProgress = (SeekBar) viewInflate.findViewById(R.id.recording_progress);
        if (this.mRecrodingSizeProgress != null) {
            this.mRecrodingSizeProgress.setOnTouchListener(new View.OnTouchListener() { // from class: com.mediatek.camera.ui.RecordingView.1
                @Override // android.view.View.OnTouchListener
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    return true;
                }
            });
            this.mRecrodingSizeProgress.setMax(this.mMax);
            this.mRecrodingSizeProgress.setProgress(this.mProgress);
        }
        return viewInflate;
    }

    @Override // com.mediatek.camera.p004ui.CameraView, com.mediatek.camera.platform.ICameraView
    public void refresh() {
        int i = R.drawable.ic_recording_indicator_pause;
        if (this.mIsRecordinging) {
            i = R.drawable.ic_recording_indicator_play;
        }
        this.mPauseResume.setImageResource(i);
        this.mRecordingTime.setText(this.mTimeText);
        if (this.mRecordingSizeCurrent != null) {
            this.mRecordingSizeCurrent.setText(getFileSize(this.mCurrent));
        }
        if (this.mRecordingSizeTotal != null) {
            this.mRecordingSizeTotal.setText(getFileSize(this.mTotal));
        }
        if (this.mRecrodingSizeProgress != null) {
            this.mRecrodingSizeProgress.setMax(this.mMax);
            this.mRecrodingSizeProgress.setProgress(this.mProgress);
        }
        setTimeVisible(this.mIsTimeVisible);
        setPauseResumeVisible(this.mIsPauseResumeVisible);
        setRecordingSizeVisible(this.mIsRecordingSizeVisible);
    }

    @Override // com.mediatek.camera.p004ui.CameraView, com.mediatek.camera.platform.ICameraView
    public void hide() {
        super.hide();
        setSizeProgress(0);
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        if (this.mListener != null && this.mPauseResume == view) {
            this.mListener.onClick(this.mPauseResume);
        }
    }

    @Override // com.mediatek.camera.p004ui.CameraView, com.mediatek.camera.platform.ICameraView
    public void setListener(Object obj) {
        this.mListener = (View.OnClickListener) obj;
    }

    public void setRecordingIndicator(boolean z) {
        this.mIsRecordinging = z;
        refresh();
    }

    public void showTime(long j, boolean z) {
        this.mTimeText = formatTime(j, z);
        if (j <= 0) {
            this.mChange = false;
        }
        if (this.mRecordingTime != null) {
            this.mRecordingTime.setText(this.mTimeText);
            if (this.mChange) {
                this.mChange = false;
                this.mRecordingIcon.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_recording_indicator_play));
            } else {
                this.mChange = true;
                this.mRecordingIcon.setImageDrawable(null);
            }
        }
    }

    public void setPauseResumeVisible(boolean z) {
        this.mIsPauseResumeVisible = z;
        if (this.mPauseResume != null) {
            this.mPauseResume.setVisibility(z ? 0 : 8);
        }
    }

    public void setTimeVisible(boolean z) {
        this.mIsTimeVisible = z;
        if (this.mRecordingTime != null) {
            this.mRecordingTime.setVisibility(z ? 0 : 4);
        }
    }

    public void setRecordingSizeVisible(boolean z) {
        this.mIsRecordingSizeVisible = z;
        if (this.mRecordingSizeGroup != null) {
            this.mRecordingSizeGroup.setVisibility(z ? 0 : 8);
        }
    }

    public void setCurrentSize(long j) {
        this.mCurrent = j;
        if (this.mRecordingSizeCurrent != null) {
            this.mRecordingSizeCurrent.setText(getFileSize(this.mCurrent));
        }
    }

    public void setTotalSize(long j) {
        this.mTotal = j;
        if (this.mRecordingSizeTotal != null) {
            this.mRecordingSizeTotal.setText(getFileSize(this.mTotal));
        }
    }

    public void setSizeProgress(int i) {
        this.mProgress = i;
        if (this.mRecrodingSizeProgress != null) {
            this.mRecrodingSizeProgress.setProgress(this.mProgress);
        }
    }

    private String getFileSize(long j) {
        return (j / 1024) + "K";
    }

    private String formatTime(long j, boolean z) {
        int i = ((int) j) / 1000;
        int i2 = ((int) (j % 1000)) / 10;
        int i3 = i % 60;
        int i4 = (i / 60) % 60;
        int i5 = i / 3600;
        Log.m31d("RecordingView", "formatTime(" + j + ", " + i5 + ", " + i4 + ", " + i3 + ")");
        return z ? String.format(Locale.ENGLISH, "%02d:%02d:%02d.%02d", Integer.valueOf(i5), Integer.valueOf(i4), Integer.valueOf(i3), Integer.valueOf(i2)) : String.format(Locale.ENGLISH, "%02d:%02d:%02d", Integer.valueOf(i5), Integer.valueOf(i4), Integer.valueOf(i3));
    }
}
