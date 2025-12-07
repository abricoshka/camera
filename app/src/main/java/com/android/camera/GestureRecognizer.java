package com.android.camera;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import com.android.camera.DownUpDetector;

/* loaded from: classes.dex */
public class GestureRecognizer {
    private final DownUpDetector mDownUpDetector;
    private final GestureDetector mGestureDetector;
    private Listener mListener;
    private boolean mListenerAvaliable;
    private final ScaleGestureDetector mScaleDetector;

    public interface Listener {
        boolean onDoubleTap(float f, float f2);

        void onDown(float f, float f2);

        boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2);

        void onLongPress(float f, float f2);

        boolean onScale(float f, float f2, float f3);

        boolean onScaleBegin(float f, float f2);

        void onScaleEnd();

        boolean onScroll(float f, float f2, float f3, float f4);

        boolean onSingleTapConfirmed(float f, float f2);

        boolean onSingleTapUp(float f, float f2);

        void onUp();
    }

    public GestureRecognizer(Context context, Listener listener) {
        this.mListener = listener;
        android.util.Log.i("GestureRecognizer", "GestureRecognizer");
        this.mGestureDetector = new GestureDetector(context, new MyGestureListener(this, null), null, true);
        this.mScaleDetector = new ScaleGestureDetector(context, new MyScaleListener(this, 0 == true ? 1 : 0));
        this.mDownUpDetector = new DownUpDetector(new MyDownUpListener(this, 0 == true ? 1 : 0));
        this.mListenerAvaliable = true;
    }

    public void onTouchEvent(MotionEvent motionEvent) {
        android.util.Log.i("remove", "Gesture onTouchEvent");
        this.mGestureDetector.onTouchEvent(motionEvent);
        this.mScaleDetector.onTouchEvent(motionEvent);
        this.mDownUpDetector.onTouchEvent(motionEvent);
    }

    private class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        /* synthetic */ MyGestureListener(GestureRecognizer gestureRecognizer, MyGestureListener myGestureListener) {
            this();
        }

        private MyGestureListener() {
        }

        @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
        public boolean onSingleTapUp(MotionEvent motionEvent) {
            android.util.Log.i("GestureRecognizer", "MyGestureListener onSingleTapUp");
            if (!GestureRecognizer.this.mListenerAvaliable) {
                return true;
            }
            return GestureRecognizer.this.mListener.onSingleTapUp(motionEvent.getX(), motionEvent.getY());
        }

        @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnDoubleTapListener
        public boolean onDoubleTap(MotionEvent motionEvent) {
            if (!GestureRecognizer.this.mListenerAvaliable) {
                return true;
            }
            return GestureRecognizer.this.mListener.onDoubleTap(motionEvent.getX(), motionEvent.getY());
        }

        @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
        public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
            if (!GestureRecognizer.this.mListenerAvaliable) {
                return true;
            }
            return GestureRecognizer.this.mListener.onScroll(f, f2, motionEvent2.getX() - motionEvent.getX(), motionEvent2.getY() - motionEvent.getY());
        }

        @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
        public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
            if (!GestureRecognizer.this.mListenerAvaliable) {
                return true;
            }
            return GestureRecognizer.this.mListener.onFling(motionEvent, motionEvent2, f, f2);
        }

        @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnDoubleTapListener
        public boolean onSingleTapConfirmed(MotionEvent motionEvent) {
            if (!GestureRecognizer.this.mListenerAvaliable) {
                return true;
            }
            return GestureRecognizer.this.mListener.onSingleTapConfirmed(motionEvent.getX(), motionEvent.getY());
        }

        @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
        public void onLongPress(MotionEvent motionEvent) {
            android.util.Log.i("GestureRecognizer", "MyGestureListener onLongPress");
            GestureRecognizer.this.mListener.onLongPress(motionEvent.getX(), motionEvent.getY());
        }
    }

    private class MyScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        /* synthetic */ MyScaleListener(GestureRecognizer gestureRecognizer, MyScaleListener myScaleListener) {
            this();
        }

        private MyScaleListener() {
        }

        @Override // android.view.ScaleGestureDetector.SimpleOnScaleGestureListener, android.view.ScaleGestureDetector.OnScaleGestureListener
        public boolean onScaleBegin(ScaleGestureDetector scaleGestureDetector) {
            if (!GestureRecognizer.this.mListenerAvaliable) {
                return true;
            }
            return GestureRecognizer.this.mListener.onScaleBegin(scaleGestureDetector.getFocusX(), scaleGestureDetector.getFocusY());
        }

        @Override // android.view.ScaleGestureDetector.SimpleOnScaleGestureListener, android.view.ScaleGestureDetector.OnScaleGestureListener
        public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
            if (!GestureRecognizer.this.mListenerAvaliable) {
                return true;
            }
            return GestureRecognizer.this.mListener.onScale(scaleGestureDetector.getFocusX(), scaleGestureDetector.getFocusY(), scaleGestureDetector.getScaleFactor());
        }

        @Override // android.view.ScaleGestureDetector.SimpleOnScaleGestureListener, android.view.ScaleGestureDetector.OnScaleGestureListener
        public void onScaleEnd(ScaleGestureDetector scaleGestureDetector) {
            if (!GestureRecognizer.this.mListenerAvaliable) {
                return;
            }
            GestureRecognizer.this.mListener.onScaleEnd();
        }
    }

    private class MyDownUpListener implements DownUpDetector.DownUpListener {
        /* synthetic */ MyDownUpListener(GestureRecognizer gestureRecognizer, MyDownUpListener myDownUpListener) {
            this();
        }

        private MyDownUpListener() {
        }

        @Override // com.android.camera.DownUpDetector.DownUpListener
        public void onDown(MotionEvent motionEvent) {
            if (!GestureRecognizer.this.mListenerAvaliable) {
                return;
            }
            GestureRecognizer.this.mListener.onDown(motionEvent.getX(), motionEvent.getY());
        }

        @Override // com.android.camera.DownUpDetector.DownUpListener
        public void onUp(MotionEvent motionEvent) {
            if (!GestureRecognizer.this.mListenerAvaliable) {
                return;
            }
            GestureRecognizer.this.mListener.onUp();
        }
    }
}
