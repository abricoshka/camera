package com.android.camera;

import android.view.MotionEvent;

/* loaded from: classes.dex */
public class DownUpDetector {
    private DownUpListener mListener;
    private boolean mStillDown;

    public interface DownUpListener {
        void onDown(MotionEvent motionEvent);

        void onUp(MotionEvent motionEvent);
    }

    public DownUpDetector(DownUpListener downUpListener) {
        this.mListener = downUpListener;
    }

    private void setState(boolean z, MotionEvent motionEvent) {
        if (z == this.mStillDown) {
            return;
        }
        this.mStillDown = z;
        if (z) {
            this.mListener.onDown(motionEvent);
        } else {
            this.mListener.onUp(motionEvent);
        }
    }

    public void onTouchEvent(MotionEvent motionEvent) {
        switch (motionEvent.getAction() & 255) {
            case 0:
            case 5:
                setState(true, motionEvent);
                break;
            case 1:
            case 3:
                setState(false, motionEvent);
                break;
        }
    }
}
