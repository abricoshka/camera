package com.android.camera.p001ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import com.mediatek.camera.R;

/* loaded from: classes.dex */
public class RenWuBottomView extends View {
    private final int MSG_RETURN_TO_DEFAULT;
    private float baground_circle_radius;
    private final int center_y;
    private int degree;
    private boolean first;
    private final int heng_kuang_width;
    private final int heng_small_circle_width;
    private boolean istouch;
    private final int kuang_padding_top;
    private final int kuang_width;
    private onChangeMode listen;
    private Bitmap mBitmap;
    private Bitmap mBitmap_kuang;
    private Handler mHandler;
    private Paint mPaint;
    private Paint mPaint_bitmap;
    private final int move_x_distance;
    private final int return_default_time;
    public final int rotate_degree;
    private final int small_circle_padding_top;
    private final int small_circle_width;
    private final int translate_x;
    float x_down;
    float x_move;
    float x_pre_move;

    public interface onChangeMode {
        void changeMode(int i);
    }

    public RenWuBottomView(Context context) {
        super(context);
        this.MSG_RETURN_TO_DEFAULT = 1;
        this.baground_circle_radius = 0.66f;
        this.center_y = 128;
        this.degree = 0;
        this.first = true;
        this.heng_kuang_width = 50;
        this.heng_small_circle_width = 25;
        this.istouch = false;
        this.kuang_padding_top = 10;
        this.kuang_width = 60;
        this.mHandler = new Handler() { // from class: com.android.camera.ui.RenWuBottomView.1
            @Override // android.os.Handler
            public void handleMessage(Message message) {
                super.handleMessage(message);
                if (message.what == 1) {
                    RenWuBottomView.this.first = true;
                    RenWuBottomView.this.istouch = false;
                    RenWuBottomView.this.invalidate();
                }
            }
        };
        this.move_x_distance = 20;
        this.return_default_time = 2000;
        this.rotate_degree = 12;
        this.small_circle_padding_top = 20;
        this.small_circle_width = 30;
        this.translate_x = 50;
        init();
    }

    public RenWuBottomView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.MSG_RETURN_TO_DEFAULT = 1;
        this.baground_circle_radius = 0.66f;
        this.center_y = 128;
        this.degree = 0;
        this.first = true;
        this.heng_kuang_width = 50;
        this.heng_small_circle_width = 25;
        this.istouch = false;
        this.kuang_padding_top = 10;
        this.kuang_width = 60;
        this.mHandler = new Handler() { // from class: com.android.camera.ui.RenWuBottomView.1
            @Override // android.os.Handler
            public void handleMessage(Message message) {
                super.handleMessage(message);
                if (message.what == 1) {
                    RenWuBottomView.this.first = true;
                    RenWuBottomView.this.istouch = false;
                    RenWuBottomView.this.invalidate();
                }
            }
        };
        this.move_x_distance = 20;
        this.return_default_time = 2000;
        this.rotate_degree = 12;
        this.small_circle_padding_top = 20;
        this.small_circle_width = 30;
        this.translate_x = 50;
        init();
    }

    public RenWuBottomView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.MSG_RETURN_TO_DEFAULT = 1;
        this.baground_circle_radius = 0.66f;
        this.center_y = 128;
        this.degree = 0;
        this.first = true;
        this.heng_kuang_width = 50;
        this.heng_small_circle_width = 25;
        this.istouch = false;
        this.kuang_padding_top = 10;
        this.kuang_width = 60;
        this.mHandler = new Handler() { // from class: com.android.camera.ui.RenWuBottomView.1
            @Override // android.os.Handler
            public void handleMessage(Message message) {
                super.handleMessage(message);
                if (message.what == 1) {
                    RenWuBottomView.this.first = true;
                    RenWuBottomView.this.istouch = false;
                    RenWuBottomView.this.invalidate();
                }
            }
        };
        this.move_x_distance = 20;
        this.return_default_time = 2000;
        this.rotate_degree = 12;
        this.small_circle_padding_top = 20;
        this.small_circle_width = 30;
        this.translate_x = 50;
        init();
    }

    private void init() {
        this.baground_circle_radius = Float.parseFloat(getResources().getString(R.string.moderenxiang_radius));
        this.mPaint = new Paint(1);
        this.mPaint.setColor(Color.argb(80, 0, 0, 0));
        this.mPaint.setStrokeWidth(1.0f);
        this.mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        this.mPaint_bitmap = new Paint();
        this.mPaint_bitmap.setAntiAlias(true);
        this.mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.renwu_circle);
        this.mBitmap_kuang = BitmapFactory.decodeResource(getResources(), R.drawable.renwu_kuang);
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();
        Log.d("zbx", "onDraw: first=" + this.first);
        Log.d("zbx", "onDraw: istouch=" + this.istouch);
        if (!this.first || this.istouch) {
            canvas.save();
            int i = this.degree;
            canvas.drawCircle(width / 2, dip2px(128.0f) + height, width * this.baground_circle_radius, this.mPaint);
            Rect rect = new Rect((width / 2) - dip2px(15.0f), dip2px(20.0f), (width / 2) + dip2px(15.0f), dip2px(50.0f));
            new Rect((width / 2) - dip2px(28.0f), dip2px(20.0f), (width / 2) + dip2px(28.0f), dip2px(45.0f));
            new Rect((width / 2) - dip2px(25.0f), dip2px(20.0f), (width / 2) + dip2px(25.0f), dip2px(40.0f));
            new Rect((width / 2) - dip2px(23.0f), dip2px(20.0f), (width / 2) + dip2px(23.0f), dip2px(35.0f));
            Rect rect2 = new Rect((width / 2) - dip2px(30.0f), dip2px(10.0f), (width / 2) + dip2px(30.0f), dip2px(70.0f));
            canvas.rotate(this.degree, width / 2, dip2px(128.0f) + height);
            if (this.degree != 0) {
                canvas.drawBitmap(this.mBitmap, (Rect) null, rect, (Paint) null);
            }
            canvas.rotate(12.0f, width / 2, dip2px(128.0f) + height);
            int i2 = i + 12;
            if (i2 != 0) {
                canvas.drawBitmap(this.mBitmap, (Rect) null, rect, (Paint) null);
            }
            canvas.rotate(12.0f, width / 2, dip2px(128.0f) + height);
            int i3 = i2 + 12;
            if (i3 != 0) {
                canvas.drawBitmap(this.mBitmap, (Rect) null, rect, (Paint) null);
            }
            canvas.rotate(12.0f, width / 2, dip2px(128.0f) + height);
            int i4 = i3 + 12;
            if (i4 != 0) {
                canvas.drawBitmap(this.mBitmap, (Rect) null, rect, (Paint) null);
            }
            canvas.rotate(12.0f, width / 2, height + dip2px(128.0f));
            if (i4 + 12 != 0) {
                canvas.drawBitmap(this.mBitmap, (Rect) null, rect, (Paint) null);
            }
            canvas.restore();
            canvas.drawBitmap(this.mBitmap_kuang, (Rect) null, rect2, (Paint) null);
        }
        this.first = false;
        Rect rect3 = new Rect((width / 2) - (dip2px(25.0f) / 2), ((height / 2) + 25) - (dip2px(25.0f) / 2), (width / 2) + (dip2px(25.0f) / 2), (height / 2) + 25 + (dip2px(25.0f) / 2));
        Rect rect4 = new Rect((width / 2) - (dip2px(21.0f) / 2), ((height / 2) + 25) - (dip2px(21.0f) / 2), (width / 2) + (dip2px(21.0f) / 2), (height / 2) + 25 + (dip2px(21.0f) / 2));
        Rect rect5 = new Rect((width / 2) - (dip2px(17.0f) / 2), ((height / 2) + 25) - (dip2px(17.0f) / 2), (width / 2) + (dip2px(17.0f) / 2), (height / 2) + 25 + (dip2px(17.0f) / 2));
        Rect rect6 = new Rect((width / 2) - (dip2px(13.0f) / 2), ((height / 2) + 25) - (dip2px(13.0f) / 2), (width / 2) + (dip2px(13.0f) / 2), (height / 2) + 25 + (dip2px(13.0f) / 2));
        Rect rect7 = new Rect((width / 2) - dip2px(25.0f), ((height / 2) + 25) - dip2px(25.0f), (width / 2) + dip2px(25.0f), (height / 2) + 25 + dip2px(25.0f));
        switch (this.degree) {
            case -48:
                canvas.translate((-dip2px(50.0f)) * 3, 0.0f);
                canvas.drawBitmap(this.mBitmap, (Rect) null, rect6, (Paint) null);
                canvas.translate(dip2px(50.0f), 0.0f);
                canvas.drawBitmap(this.mBitmap, (Rect) null, rect5, (Paint) null);
                canvas.translate(dip2px(50.0f), 0.0f);
                canvas.drawBitmap(this.mBitmap, (Rect) null, rect4, (Paint) null);
                canvas.translate(dip2px(50.0f), 0.0f);
                canvas.drawBitmap(this.mBitmap, (Rect) null, rect3, (Paint) null);
                canvas.translate(dip2px(50.0f), 0.0f);
                canvas.drawBitmap(this.mBitmap_kuang, (Rect) null, rect7, (Paint) null);
                break;
            case -36:
                canvas.translate((-dip2px(50.0f)) * 3, 0.0f);
                canvas.drawBitmap(this.mBitmap, (Rect) null, rect5, (Paint) null);
                canvas.translate(dip2px(50.0f), 0.0f);
                canvas.drawBitmap(this.mBitmap, (Rect) null, rect4, (Paint) null);
                canvas.translate(dip2px(50.0f), 0.0f);
                canvas.drawBitmap(this.mBitmap, (Rect) null, rect3, (Paint) null);
                canvas.translate(dip2px(50.0f), 0.0f);
                canvas.drawBitmap(this.mBitmap_kuang, (Rect) null, rect7, (Paint) null);
                canvas.translate(dip2px(50.0f), 0.0f);
                canvas.drawBitmap(this.mBitmap, (Rect) null, rect3, (Paint) null);
                break;
            case -24:
                canvas.translate((-dip2px(50.0f)) * 2, 0.0f);
                canvas.drawBitmap(this.mBitmap, (Rect) null, rect4, (Paint) null);
                canvas.translate(dip2px(50.0f), 0.0f);
                canvas.drawBitmap(this.mBitmap, (Rect) null, rect3, (Paint) null);
                canvas.translate(dip2px(50.0f), 0.0f);
                canvas.drawBitmap(this.mBitmap_kuang, (Rect) null, rect7, (Paint) null);
                canvas.translate(dip2px(50.0f), 0.0f);
                canvas.drawBitmap(this.mBitmap, (Rect) null, rect3, (Paint) null);
                canvas.translate(dip2px(50.0f), 0.0f);
                canvas.drawBitmap(this.mBitmap, (Rect) null, rect4, (Paint) null);
                break;
            case -12:
                canvas.translate(-dip2px(50.0f), 0.0f);
                canvas.drawBitmap(this.mBitmap, (Rect) null, rect3, (Paint) null);
                canvas.translate(dip2px(50.0f), 0.0f);
                canvas.drawBitmap(this.mBitmap_kuang, (Rect) null, rect7, (Paint) null);
                canvas.translate(dip2px(50.0f), 0.0f);
                canvas.drawBitmap(this.mBitmap, (Rect) null, rect3, (Paint) null);
                canvas.translate(dip2px(50.0f), 0.0f);
                canvas.drawBitmap(this.mBitmap, (Rect) null, rect4, (Paint) null);
                canvas.translate(dip2px(50.0f), 0.0f);
                canvas.drawBitmap(this.mBitmap, (Rect) null, rect5, (Paint) null);
                break;
            case 0:
                canvas.drawBitmap(this.mBitmap_kuang, (Rect) null, rect7, (Paint) null);
                canvas.translate(dip2px(50.0f), 0.0f);
                canvas.drawBitmap(this.mBitmap, (Rect) null, rect3, (Paint) null);
                canvas.translate(dip2px(50.0f), 0.0f);
                canvas.drawBitmap(this.mBitmap, (Rect) null, rect4, (Paint) null);
                canvas.translate(dip2px(50.0f), 0.0f);
                canvas.drawBitmap(this.mBitmap, (Rect) null, rect5, (Paint) null);
                canvas.translate(dip2px(50.0f), 0.0f);
                canvas.drawBitmap(this.mBitmap, (Rect) null, rect6, (Paint) null);
                break;
        }
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case 0:
                this.x_down = motionEvent.getX();
                break;
            case 1:
                if (this.degree <= 0 && this.degree > -6) {
                    this.degree = 0;
                } else if (this.degree <= -6 && this.degree > -12) {
                    this.degree = -12;
                } else if (this.degree <= -12 && this.degree > -18) {
                    this.degree = -12;
                } else if (this.degree <= -18 && this.degree > -24) {
                    this.degree = -24;
                } else if (this.degree <= -24 && this.degree > -30) {
                    this.degree = -24;
                } else if (this.degree <= -30 && this.degree > -36) {
                    this.degree = -36;
                } else if (this.degree <= -36 && this.degree > -42) {
                    this.degree = -36;
                } else if (this.degree <= -42 && this.degree >= -48) {
                    this.degree = -48;
                }
                this.listen.changeMode(this.degree);
                invalidate();
                this.mHandler.sendEmptyMessageDelayed(1, 2000L);
                this.x_pre_move = 0.0f;
                this.x_move = 0.0f;
                this.x_down = 0.0f;
                break;
            case 2:
                this.istouch = true;
                this.mHandler.removeMessages(1);
                this.x_move = motionEvent.getX();
                if (this.x_move - this.x_down > dip2px(20.0f) && (this.x_move - this.x_pre_move > dip2px(5.0f) || this.x_pre_move == 0.0f)) {
                    this.x_pre_move = this.x_move;
                    this.degree += 2;
                    if (this.degree >= 0) {
                        this.degree = 0;
                    }
                } else if (this.x_down - this.x_move > dip2px(20.0f) && (this.x_pre_move - this.x_move > dip2px(5.0f) || this.x_pre_move == 0.0f)) {
                    this.x_pre_move = this.x_move;
                    this.degree -= 2;
                    if (this.degree <= -48) {
                        this.degree = -48;
                    }
                }
                this.listen.changeMode(this.degree);
                invalidate();
                break;
        }
        return true;
    }

    public void setdefault() {
        this.first = true;
        this.istouch = false;
        this.degree = 0;
        invalidate();
    }

    public int getdegree() {
        return 12;
    }

    public void setOnChangeMode(onChangeMode onchangemode) {
        this.listen = onchangemode;
    }

    public int dip2px(float f) {
        return (int) ((getResources().getDisplayMetrics().density * f) + 0.5f);
    }
}
