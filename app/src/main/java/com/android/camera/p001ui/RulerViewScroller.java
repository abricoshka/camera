package com.android.camera.p001ui;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.Scroller;

/* loaded from: classes.dex */
public class RulerViewScroller {
    private Context context;
    private GestureDetector gestureDetector;
    private boolean isScrollingPerformed;
    private int lastScrollX;
    private float lastTouchedX;
    private ScrollingListener listener;
    private Scroller scroller;
    private final int MESSAGE_SCROLL = 0;
    private final int MESSAGE_JUSTIFY = 1;
    private GestureDetector.SimpleOnGestureListener gestureListener = new GestureDetector.SimpleOnGestureListener() { // from class: com.android.camera.ui.RulerViewScroller.1
        @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
        public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
            return true;
        }

        @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
        public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
            RulerViewScroller.this.lastScrollX = 0;
            RulerViewScroller.this.scroller.fling(0, RulerViewScroller.this.lastScrollX, (int) (-f), 0, -2147483647, Integer.MAX_VALUE, 0, 0);
            RulerViewScroller.this.setNextMessage(0);
            return true;
        }
    };
    private Handler animationHandler = new Handler(new Handler.Callback() { // from class: com.android.camera.ui.RulerViewScroller.2
        @Override // android.os.Handler.Callback
        public boolean handleMessage(Message message) {
            RulerViewScroller.this.scroller.computeScrollOffset();
            int currX = RulerViewScroller.this.scroller.getCurrX();
            int i = RulerViewScroller.this.lastScrollX - currX;
            RulerViewScroller.this.lastScrollX = currX;
            if (i != 0) {
                RulerViewScroller.this.listener.onScroll(i);
            }
            if (Math.abs(currX - RulerViewScroller.this.scroller.getFinalX()) < 1) {
                RulerViewScroller.this.lastScrollX = RulerViewScroller.this.scroller.getFinalX();
                RulerViewScroller.this.scroller.forceFinished(true);
            }
            if (!RulerViewScroller.this.scroller.isFinished()) {
                RulerViewScroller.this.animationHandler.sendEmptyMessage(message.what);
            } else if (message.what == 0) {
                RulerViewScroller.this.justify();
            } else {
                RulerViewScroller.this.finishScrolling();
            }
            return true;
        }
    });

    public interface ScrollingListener {
        void onFinished();

        void onJustify();

        void onScroll(int i);

        void onStarted();
    }

    public RulerViewScroller(Context context, ScrollingListener scrollingListener) {
        this.listener = scrollingListener;
        this.context = context;
        this.gestureDetector = new GestureDetector(context, this.gestureListener);
        this.gestureDetector.setIsLongpressEnabled(false);
        this.scroller = new Scroller(context);
        this.scroller.setFriction(0.05f);
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case 0:
                this.lastTouchedX = motionEvent.getX();
                this.scroller.forceFinished(true);
                clearMessages();
                break;
            case 2:
                int x = (int) (motionEvent.getX() - this.lastTouchedX);
                if (x != 0) {
                    startScrolling();
                    this.listener.onScroll(x);
                    this.lastTouchedX = motionEvent.getX();
                    break;
                }
                break;
        }
        if (!this.gestureDetector.onTouchEvent(motionEvent) && motionEvent.getAction() == 1) {
            justify();
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setNextMessage(int i) {
        clearMessages();
        this.animationHandler.sendEmptyMessage(i);
    }

    private void clearMessages() {
        this.animationHandler.removeMessages(0);
        this.animationHandler.removeMessages(1);
    }

    public void scroll(int i, int i2) {
        this.scroller.forceFinished(true);
        this.lastScrollX = 0;
        this.scroller.startScroll(0, 0, i, 0, i2 != 0 ? i2 : 400);
        setNextMessage(0);
        startScrolling();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void justify() {
        this.listener.onJustify();
        setNextMessage(1);
    }

    private void startScrolling() {
        if (!this.isScrollingPerformed) {
            this.isScrollingPerformed = true;
            this.listener.onStarted();
        }
    }

    void finishScrolling() {
        if (this.isScrollingPerformed) {
            this.listener.onFinished();
            this.isScrollingPerformed = false;
        }
    }
}
