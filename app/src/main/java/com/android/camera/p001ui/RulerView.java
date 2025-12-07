package com.android.camera.p001ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.Layout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import com.android.camera.R$styleable;
import com.android.camera.p001ui.RulerViewScroller;
import com.mediatek.camera.R;

/* loaded from: classes.dex */
public class RulerView extends View {
    private boolean isDisallowIntercept;
    private boolean isDown;
    public boolean isScaleGradient;
    private boolean isScrollingPerformed;
    public boolean isShowScaleValue;
    public int mBottomLineColor;
    public float mBottomLineWidth;
    private Canvas mCanvas;
    public int mCurrentValue;
    private float mDownFocusX;
    private float mDownFocusY;
    public int mMaxScaleColor;
    private int mMaxScaleHeight;
    public float mMaxScaleHeightRatio;
    public float mMaxScaleWidth;
    public int mMaxValue;
    public int mMidScaleColor;
    private int mMidScaleHeight;
    public float mMidScaleHeightRatio;
    public float mMidScaleWidth;
    private Bitmap mMiddleImg;
    private Paint mMiddleImgPaint;
    private Bitmap mMiddleImgSelect;
    public int mMinScaleColor;
    private int mMinScaleHeight;
    public float mMinScaleHeightRatio;
    public float mMinScaleWidth;
    public int mMinValue;
    public int mScaleBase;
    private Paint mScalePaint;
    private int mScaleSpace;
    public int mScaleValueColor;
    private TextPaint mScaleValuePaint;
    public float mScaleValueSize;
    private float mTpDesiredWidth;
    private int midIconHeight;
    private int midIconWidth;
    private OnRulerViewScrollListener onWheelListener;
    private RulerViewScroller scroller;
    RulerViewScroller.ScrollingListener scrollingListener;
    private int scrollingOffset;

    public interface OnRulerViewScrollListener<T> {
        void onChanged(RulerView rulerView, T t, T t2);

        void onScrollingFinished(RulerView rulerView);

        void onScrollingStarted(RulerView rulerView);
    }

    public RulerView(Context context) {
        this(context, null);
    }

    public RulerView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public RulerView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mScaleBase = 3;
        this.mScaleSpace = 20;
        this.scrollingListener = new RulerViewScroller.ScrollingListener() { // from class: com.android.camera.ui.RulerView.1
            @Override // com.android.camera.ui.RulerViewScroller.ScrollingListener
            public void onStarted() {
                RulerView.this.isScrollingPerformed = true;
                if (RulerView.this.onWheelListener != null) {
                    RulerView.this.onWheelListener.onScrollingStarted(RulerView.this);
                }
            }

            @Override // com.android.camera.ui.RulerViewScroller.ScrollingListener
            public void onScroll(int i2) {
                RulerView.this.doScroll(i2);
            }

            @Override // com.android.camera.ui.RulerViewScroller.ScrollingListener
            public void onFinished() {
                if (RulerView.this.outOfRange()) {
                    return;
                }
                if (RulerView.this.isScrollingPerformed) {
                    if (RulerView.this.onWheelListener != null) {
                        RulerView.this.onWheelListener.onScrollingFinished(RulerView.this);
                    }
                    RulerView.this.isScrollingPerformed = false;
                }
                RulerView.this.scrollingOffset = 0;
                RulerView.this.invalidate();
            }

            @Override // com.android.camera.ui.RulerViewScroller.ScrollingListener
            public void onJustify() {
                if (!RulerView.this.outOfRange() && Math.abs(RulerView.this.scrollingOffset) > 1) {
                    if (RulerView.this.scrollingOffset < (-RulerView.this.mScaleSpace) / 2) {
                        RulerView.this.scroller.scroll(RulerView.this.mScaleSpace + RulerView.this.scrollingOffset, 0);
                    } else if (RulerView.this.scrollingOffset > RulerView.this.mScaleSpace / 2) {
                        RulerView.this.scroller.scroll(RulerView.this.scrollingOffset - RulerView.this.mScaleSpace, 0);
                    } else {
                        RulerView.this.scroller.scroll(RulerView.this.scrollingOffset, 0);
                    }
                }
            }
        };
        this.isDown = false;
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R$styleable.RulerView);
        this.mMaxScaleColor = typedArrayObtainStyledAttributes.getColor(0, -16776961);
        this.mMidScaleColor = typedArrayObtainStyledAttributes.getColor(1, -16776961);
        this.mMinScaleColor = typedArrayObtainStyledAttributes.getColor(2, -16776961);
        this.mMaxScaleColor = typedArrayObtainStyledAttributes.getColor(0, -16776961);
        this.mScaleValueColor = typedArrayObtainStyledAttributes.getColor(13, -16776961);
        this.mBottomLineColor = typedArrayObtainStyledAttributes.getColor(3, -16776961);
        this.mMaxScaleWidth = typedArrayObtainStyledAttributes.getDimensionPixelSize(4, 15);
        this.mMidScaleWidth = typedArrayObtainStyledAttributes.getDimensionPixelSize(5, 12);
        this.mMinScaleWidth = typedArrayObtainStyledAttributes.getDimensionPixelSize(6, 10);
        this.mBottomLineWidth = typedArrayObtainStyledAttributes.getDimensionPixelSize(7, 15);
        this.mScaleValueSize = typedArrayObtainStyledAttributes.getDimensionPixelSize(14, 12);
        this.mScaleSpace = typedArrayObtainStyledAttributes.getDimensionPixelSize(15, 20);
        this.mMaxScaleHeightRatio = typedArrayObtainStyledAttributes.getFloat(8, 0.3f);
        this.mMidScaleHeightRatio = typedArrayObtainStyledAttributes.getFloat(9, 0.2f);
        this.mMinScaleHeightRatio = typedArrayObtainStyledAttributes.getFloat(10, 0.1f);
        this.isShowScaleValue = typedArrayObtainStyledAttributes.getBoolean(11, true);
        this.isScaleGradient = typedArrayObtainStyledAttributes.getBoolean(12, true);
        this.mMaxValue = typedArrayObtainStyledAttributes.getInteger(17, 100);
        this.mMinValue = typedArrayObtainStyledAttributes.getInteger(18, 0);
        this.mScaleBase = typedArrayObtainStyledAttributes.getInteger(19, 1);
        this.mCurrentValue = typedArrayObtainStyledAttributes.getInteger(16, 0);
        setCurrentValue(this.mCurrentValue);
        this.mMiddleImg = BitmapFactory.decodeResource(getResources(), typedArrayObtainStyledAttributes.getResourceId(20, R.drawable.mid_arrow));
        this.mMiddleImgSelect = BitmapFactory.decodeResource(getResources(), typedArrayObtainStyledAttributes.getResourceId(21, R.drawable.mid_arrow));
        typedArrayObtainStyledAttributes.recycle();
        this.mScalePaint = new Paint(1);
        this.mScalePaint.setStyle(Paint.Style.STROKE);
        this.mScalePaint.setAntiAlias(true);
        this.mScaleValuePaint = new TextPaint(1);
        this.mScaleValuePaint.setColor(this.mScaleValueColor);
        this.mScaleValuePaint.setTextSize(this.mScaleValueSize);
        this.mScaleValuePaint.setTextAlign(Paint.Align.CENTER);
        this.mTpDesiredWidth = Layout.getDesiredWidth("0", this.mScaleValuePaint);
        this.mMiddleImgPaint = new Paint(1);
        this.mMiddleImgPaint.setStyle(Paint.Style.STROKE);
        this.mMiddleImgPaint.setAntiAlias(true);
        this.scroller = new RulerViewScroller(context, this.scrollingListener);
    }

    @Override // android.view.View
    protected void onMeasure(int i, int i2) {
        setMeasuredDimension(measureWidthSize(i), measureHeightSize(i2));
    }

    private int measureHeightSize(int i) {
        int mode = View.MeasureSpec.getMode(i);
        int size = View.MeasureSpec.getSize(i);
        if (mode != 1073741824) {
            int height = (int) (this.mMiddleImg.getHeight() + getPaddingTop() + getPaddingBottom() + (this.mScaleValuePaint.getTextSize() * 2.0f));
            return mode == Integer.MIN_VALUE ? Math.min(height, size) : height;
        }
        return size;
    }

    private int measureWidthSize(int i) {
        int mode = View.MeasureSpec.getMode(i);
        int size = View.MeasureSpec.getSize(i);
        if (mode != 1073741824) {
            if (mode != Integer.MIN_VALUE) {
                return 400;
            }
            return Math.min(400, size);
        }
        return size;
    }

    @Override // android.view.View
    protected void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        if (i == 0 || i2 == 0) {
            return;
        }
        int paddingTop = (i2 - getPaddingTop()) - getPaddingBottom();
        this.mMaxScaleHeight = (int) (paddingTop * this.mMaxScaleHeightRatio);
        this.mMidScaleHeight = (int) (paddingTop * this.mMidScaleHeightRatio);
        this.mMinScaleHeight = (int) (paddingTop * this.mMinScaleHeightRatio);
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int measuredWidth = (getMeasuredWidth() - getPaddingLeft()) - getPaddingRight();
        int measuredHeight = (getMeasuredHeight() - getPaddingTop()) - getPaddingBottom();
        this.midIconWidth = measuredWidth;
        this.midIconHeight = measuredHeight;
        this.mCanvas = canvas;
        drawScaleLine(canvas, measuredWidth, measuredHeight);
        drawMiddleImg(canvas, measuredWidth, measuredHeight);
    }

    private void drawMiddleImg(Canvas canvas, int i, int i2) {
        int width = (i - this.mMiddleImg.getWidth()) / 2;
        int textSize = ((int) (this.mScaleValuePaint.getTextSize() / 2.0f)) + 30;
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(25.0f);
        if (this.isDown) {
            paint.setColor(-256);
            canvas.drawBitmap(this.mMiddleImgSelect, width, textSize, this.mMiddleImgPaint);
        } else {
            paint.setColor(-1);
            canvas.drawBitmap(this.mMiddleImg, width, textSize, this.mMiddleImgPaint);
        }
        canvas.drawText(getText(), width - 18, 22.0f, paint);
    }

    private String getText() {
        switch (this.mCurrentValue) {
            case -6:
                return "-2.0";
            case -5:
                return "-1.7";
            case -4:
                return "-1.3";
            case -3:
                return "-1.0";
            case -2:
                return "-0.7";
            case -1:
                return "-0.3";
            case 0:
                return "0.0";
            case 1:
                return "0.3";
            case 2:
                return "0.7";
            case 3:
                return "1.0";
            case 4:
                return "1.3";
            case 5:
                return "1.7";
            case 6:
                return "2.0";
            default:
                return "-2.0";
        }
    }

    private void drawScaleLine(Canvas canvas, int i, int i2) {
        drawScaleLine(canvas, ((int) Math.ceil((i / 2.0f) / this.mScaleSpace)) + 2, this.scrollingOffset, this.mCurrentValue, i, i2);
    }

    private void drawScaleLine(Canvas canvas, int i, int i2, int i3, int i4, int i5) {
        int textSize = (((int) ((i5 - this.mTpDesiredWidth) - this.mScaleValuePaint.getTextSize())) - getPaddingBottom()) + 30;
        for (int i6 = 0; i6 < i; i6++) {
            float f = (i4 / 2.0f) + (this.mScaleSpace * i6) + i2;
            int i7 = i3 + i6;
            if (f <= i4 && i7 >= this.mMinValue / this.mScaleBase && i7 <= this.mMaxValue / this.mScaleBase) {
                drawScaleLine(canvas, i7, f, textSize, i, i6, i5);
            }
            if (i7 < this.mMaxValue / this.mScaleBase && i7 >= this.mMinValue / this.mScaleBase) {
                drawBottomLine(canvas, getAlpha(i, i6), f - (this.mMaxScaleWidth / 2.0f), textSize, this.mScaleSpace + f + (this.mMaxScaleWidth / 2.0f), textSize);
            }
            float f2 = ((i4 / 2.0f) - (this.mScaleSpace * i6)) + i2;
            int i8 = i3 - i6;
            if (f2 > getPaddingLeft() && i8 >= this.mMinValue / this.mScaleBase && i8 <= this.mMaxValue / this.mScaleBase) {
                drawScaleLine(canvas, i8, f2, textSize, i, i6, i5);
            }
            if (i8 >= this.mMinValue / this.mScaleBase && i8 < this.mMaxValue / this.mScaleBase) {
                drawBottomLine(canvas, getAlpha(i, i6), f2 - (this.mMaxScaleWidth / 2.0f), textSize, this.mScaleSpace + f2 + (this.mMaxScaleWidth / 2.0f), textSize);
            }
        }
    }

    private void drawBottomLine(Canvas canvas, int i, float f, float f2, float f3, float f4) {
        this.mScalePaint.setColor(this.mBottomLineColor);
        this.mScalePaint.setStrokeWidth(this.mBottomLineWidth);
        this.mScalePaint.setAlpha(i);
    }

    public void drawScaleLine(Canvas canvas, int i, float f, int i2, int i3, int i4, int i5) {
        if (i % 3 == 0) {
            if (i % 6 == 0) {
                drawScaleLine(canvas, this.mMaxScaleWidth, this.mMaxScaleColor, getAlpha(i3, i4), f, i2, f, i2 - this.mMaxScaleHeight);
                if (this.isShowScaleValue) {
                    this.mScaleValuePaint.setAlpha(getAlpha(i3, i4));
                    return;
                }
                return;
            }
            drawScaleLine(canvas, this.mMidScaleWidth, this.mMidScaleColor, getAlpha(i3, i4), f, i2, f, i2 - this.mMidScaleHeight);
            return;
        }
        drawScaleLine(canvas, this.mMinScaleWidth, this.mMinScaleColor, getAlpha(i3, i4), f, i2, f, i2 - this.mMinScaleHeight);
    }

    private void drawScaleLine(Canvas canvas, float f, int i, int i2, float f2, float f3, float f4, float f5) {
        this.mScalePaint.setStrokeWidth(f);
        this.mScalePaint.setColor(i);
        this.mScalePaint.setAlpha(i2);
        canvas.drawLine(f2, f3, f4, f5, this.mScalePaint);
    }

    private int getAlpha(int i, int i2) {
        if (this.isScaleGradient) {
            return (255 / i) * (i - i2);
        }
        return 255;
    }

    public void setCurrentValue(int i) {
        if (i < 0) {
            i = 0;
        }
        if (i <= this.mMinValue) {
            i = this.mMinValue;
        }
        if (i >= this.mMaxValue) {
            i = this.mMaxValue;
        }
        this.mCurrentValue = i / this.mScaleBase;
        invalidate();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean outOfRange() {
        int i;
        if (this.mCurrentValue < this.mMinValue / this.mScaleBase) {
            i = (this.mCurrentValue - (this.mMinValue / this.mScaleBase)) * this.mScaleSpace;
        } else {
            i = this.mCurrentValue > this.mMaxValue / this.mScaleBase ? (this.mCurrentValue - (this.mMaxValue / this.mScaleBase)) * this.mScaleSpace : 0;
        }
        if (i == 0) {
            return false;
        }
        this.scrollingOffset = 0;
        this.scroller.scroll(-i, 100);
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void doScroll(int i) {
        this.scrollingOffset += i;
        int i2 = this.scrollingOffset / this.mScaleSpace;
        if (i2 != 0) {
            int iMin = Math.min(Math.max(this.mMinValue, this.mCurrentValue * this.mScaleBase), this.mMaxValue);
            this.mCurrentValue -= i2;
            this.scrollingOffset -= i2 * this.mScaleSpace;
            if (this.onWheelListener != null) {
                this.onWheelListener.onChanged(this, iMin + "", Math.min(Math.max(this.mMinValue, this.mCurrentValue * this.mScaleBase), this.mMaxValue) + "");
            }
        }
        invalidate();
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (!isEnabled()) {
            return true;
        }
        switch (motionEvent.getAction()) {
            case 0:
                this.mDownFocusX = motionEvent.getX();
                this.mDownFocusY = motionEvent.getY();
                this.isDown = true;
                break;
            case 1:
            case 3:
                this.isDown = false;
                if (getParent() != null) {
                    getParent().requestDisallowInterceptTouchEvent(false);
                }
                this.isDisallowIntercept = false;
                break;
            case 2:
                if (!this.isDisallowIntercept && Math.abs(motionEvent.getY() - this.mDownFocusY) < Math.abs(motionEvent.getX() - this.mDownFocusX)) {
                    this.isDisallowIntercept = true;
                    if (getParent() != null) {
                        getParent().requestDisallowInterceptTouchEvent(true);
                        break;
                    }
                }
                break;
        }
        return this.scroller.onTouchEvent(motionEvent);
    }
}
