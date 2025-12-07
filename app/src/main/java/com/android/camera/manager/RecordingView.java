package com.android.camera.manager;

import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import com.android.camera.Log;
import com.mediatek.camera.R;

/* loaded from: classes.dex */
public class RecordingView extends ViewManager implements View.OnClickListener {
    private long mCurrent;
    private View.OnClickListener mListener;
    private int mMax;
    private ImageView mPauseResume;
    private boolean mPauseResumeVisible;
    private int mProgress;
    private TextView mRecordingSizeCurrent;
    private View mRecordingSizeGroup;
    private TextView mRecordingSizeTotal;
    private boolean mRecordingSizeVisible;
    private TextView mRecordingTime;
    private boolean mRecordinging;
    private SeekBar mRecrodingSizeProgress;
    private String mTimeText;
    private boolean mTimeVisible;
    private long mTotal;

    @Override // com.android.camera.manager.ViewManager
    protected View getView() {
        View viewInflate = inflate(R.layout.recording);
        this.mRecordingTime = (TextView) viewInflate.findViewById(R.id.recording_time);
        this.mPauseResume = (ImageView) viewInflate.findViewById(R.id.btn_pause_resume);
        this.mPauseResume.setOnClickListener(this);
        this.mRecordingSizeGroup = viewInflate.findViewById(R.id.recording_size_group);
        this.mRecordingSizeCurrent = (TextView) viewInflate.findViewById(R.id.recording_current);
        this.mRecordingSizeTotal = (TextView) viewInflate.findViewById(R.id.recording_total);
        this.mRecrodingSizeProgress = (SeekBar) viewInflate.findViewById(R.id.recording_progress);
        if (this.mRecrodingSizeProgress != null) {
            this.mRecrodingSizeProgress.setOnTouchListener(new View.OnTouchListener() { // from class: com.android.camera.manager.RecordingView.1
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

    @Override // com.android.camera.manager.ViewManager
    protected void onRefresh() {
        Log.m10v("RecordingView", "onRefresh() mCurrent=" + this.mCurrent + ", mTotal=" + this.mTotal + ", mProgress=" + this.mProgress + ", mMax=" + this.mMax + ", mRecordinging=" + this.mRecordinging);
        int i = R.drawable.ic_recording_indicator_pause;
        int i2 = R.drawable.ic_recording_play;
        if (this.mRecordinging) {
            i = R.drawable.ic_recording_indicator_play;
            i2 = R.drawable.ic_recording_pause;
        }
        this.mPauseResume.setImageResource(i2);
        this.mRecordingTime.setCompoundDrawablesWithIntrinsicBounds(getContext().getResources().getDrawable(i), (Drawable) null, (Drawable) null, (Drawable) null);
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
        setTimeVisible(this.mTimeVisible);
        setPauseResumeVisible(this.mPauseResumeVisible);
        setRecordingSizeVisible(this.mRecordingSizeVisible);
    }

    @Override // com.android.camera.manager.ViewManager
    public void hide() {
        super.hide();
        setSizeProgress(0);
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        Log.m8i("RecordingView", "onClick mListener = " + this.mListener + " view = " + view + " mPauseResume = " + this.mPauseResume);
        if (this.mListener != null && this.mPauseResume == view) {
            this.mListener.onClick(this.mPauseResume);
        }
    }

    public void setPauseResumeVisible(boolean z) {
        Log.m5d("RecordingView", "setPauseResumeVisible(" + z + ") mPauseResumeVisible=" + this.mPauseResumeVisible);
        this.mPauseResumeVisible = z;
        if (this.mPauseResume != null) {
            this.mPauseResume.setVisibility(z ? 0 : 8);
        }
    }

    public void setTimeVisible(boolean z) {
        Log.m5d("RecordingView", "setTimeVisible(" + z + ") mTimeVisible=" + this.mTimeVisible);
        this.mTimeVisible = z;
        if (this.mRecordingTime != null) {
            this.mRecordingTime.setVisibility(z ? 0 : 4);
        }
    }

    public void setRecordingSizeVisible(boolean z) {
        Log.m5d("RecordingView", "setRecordingSizeVisible(" + z + ") mRecordingSizeVisible=" + this.mRecordingSizeVisible);
        this.mRecordingSizeVisible = z;
        if (this.mRecordingSizeGroup != null) {
            this.mRecordingSizeGroup.setVisibility(z ? 0 : 8);
        }
    }

    public void setSizeProgress(int i) {
        Log.m5d("RecordingView", "setSizeProgress(" + i + ")");
        this.mProgress = i;
        if (this.mRecrodingSizeProgress != null) {
            this.mRecrodingSizeProgress.setProgress(this.mProgress);
        }
    }

    private String getFileSize(long j) {
        return (j / 1024) + "K";
    }
}
