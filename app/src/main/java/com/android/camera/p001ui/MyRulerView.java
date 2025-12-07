package com.android.camera.p001ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.icu.text.DecimalFormat;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Scroller;
import com.mediatek.camera.R;
import java.util.List;

/* loaded from: classes.dex */
public class MyRulerView extends View implements GestureDetector.OnGestureListener {
    private Animator.AnimatorListener animatorListener;
    private ValueAnimator.AnimatorUpdateListener flingAnimatorListener;
    private boolean isFling;
    private float mContentLength;
    private DecimalFormat mDecimalFormat;
    private float mDivideByFiveHeight;
    private float mDivideByFiveWidth;
    private float mDivideByTenHeight;
    private float mDivideByTenWidth;
    private boolean mFling;
    private GestureDetector mGestureDetectorCompat;
    private int mHighlightColor;
    private float mIntervalDistance;
    private float mIntervalValue;
    private boolean mIsDivideByFive;
    private boolean mIsDivideByTen;
    private float mMaxOverScrollDistance;
    private float mMaxValue;
    private float mMinValue;
    private int mOrientation;
    private Rect mRect;
    private int mRetainLength;
    private int mRulerColor;
    private int mRulerCount;
    private int mRulerHeight;
    private float mRulerLineHeight;
    private float mRulerLineWidth;
    private Paint mRulerPaint;
    private boolean mRulerShowText;
    private int mRulerTenColor;
    private int mRulerWidth;
    private Scroller mScroller;
    private int mSelectedIndex;
    private float mTextBaseLineDistance;
    private int mTextColor;
    private List<String> mTextList;
    private TextPaint mTextPaint;
    private float mTextSize;
    private int mViewScopeSize;
    private OnValueChangeListener onValueChangeListener;
    private ValueAnimator settlingAnimator;
    private float stopX;

    public interface OnValueChangeListener {
        void onChange(MyRulerView myRulerView, float f);
    }

    public MyRulerView(Context context) {
        this(context, null);
    }

    public MyRulerView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.isFling = false;
        this.mSelectedIndex = 0;
        this.mHighlightColor = -65536;
        this.mTextColor = -16777216;
        this.mRulerColor = -16777216;
        this.mRulerTenColor = -16777216;
        this.mFling = false;
        this.mIntervalValue = 1.0f;
        this.mIntervalDistance = 0.0f;
        this.mRetainLength = 0;
        this.mIsDivideByTen = true;
        this.mIsDivideByFive = false;
        this.mRulerShowText = false;
        this.mOrientation = 0;
        this.flingAnimatorListener = new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.camera.ui.MyRulerView.1
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float fFloatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
                Log.d("flingAnimatorListener", "animationValue = " + fFloatValue);
                if (MyRulerView.this.mOrientation == 0) {
                    MyRulerView.this.scrollTo((int) fFloatValue, 0);
                } else {
                    MyRulerView.this.scrollTo(0, (int) fFloatValue);
                }
                MyRulerView.this.onValueChange();
                MyRulerView.this.invalidate();
            }
        };
        this.animatorListener = new AnimatorListenerAdapter() { // from class: com.android.camera.ui.MyRulerView.2
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                MyRulerView.this.isFling = false;
            }
        };
        init(attributeSet);
    }

    private void init(AttributeSet attributeSet) {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        this.mRulerLineWidth = displayMetrics.density * 2.0f;
        this.mDivideByFiveWidth = displayMetrics.density * 3.0f;
        this.mDivideByTenWidth = displayMetrics.density * 4.0f;
        this.mRulerLineHeight = displayMetrics.density * 15.0f;
        this.mDivideByFiveHeight = displayMetrics.density * 20.0f;
        this.mDivideByTenHeight = displayMetrics.density * 30.0f;
        this.mIntervalDistance = displayMetrics.density * 8.0f;
        this.mTextSize = displayMetrics.scaledDensity * 15.0f;
        TypedArray typedArrayObtainStyledAttributes = attributeSet != null ? getContext().obtainStyledAttributes(attributeSet, R.styleable.customRulerView) : null;
        if (typedArrayObtainStyledAttributes != null) {
            this.mOrientation = typedArrayObtainStyledAttributes.getInt(0, 0);
            this.mHighlightColor = typedArrayObtainStyledAttributes.getInt(1, this.mHighlightColor);
            this.mTextColor = typedArrayObtainStyledAttributes.getInt(4, this.mTextColor);
            this.mRulerColor = typedArrayObtainStyledAttributes.getInt(2, this.mRulerColor);
            this.mRulerTenColor = typedArrayObtainStyledAttributes.getColor(4, this.mRulerColor);
            this.mIntervalDistance = typedArrayObtainStyledAttributes.getDimension(5, this.mIntervalDistance);
            this.mRulerLineWidth = typedArrayObtainStyledAttributes.getDimension(6, this.mRulerLineWidth);
            this.mRulerLineHeight = typedArrayObtainStyledAttributes.getDimension(7, this.mRulerLineHeight);
            this.mTextSize = typedArrayObtainStyledAttributes.getDimension(8, this.mTextSize);
            this.mTextBaseLineDistance = typedArrayObtainStyledAttributes.getDimension(9, this.mTextBaseLineDistance);
            this.mIsDivideByTen = typedArrayObtainStyledAttributes.getBoolean(10, this.mIsDivideByTen);
            this.mDivideByTenHeight = typedArrayObtainStyledAttributes.getDimension(11, this.mDivideByTenHeight);
            this.mDivideByTenWidth = typedArrayObtainStyledAttributes.getDimension(12, this.mDivideByTenWidth);
            this.mIsDivideByFive = typedArrayObtainStyledAttributes.getBoolean(13, this.mIsDivideByFive);
            this.mDivideByFiveHeight = typedArrayObtainStyledAttributes.getDimension(14, this.mDivideByFiveHeight);
            this.mDivideByFiveWidth = typedArrayObtainStyledAttributes.getDimension(15, this.mDivideByFiveWidth);
            this.mIntervalValue = typedArrayObtainStyledAttributes.getFloat(16, this.mIntervalValue);
            this.mMaxValue = typedArrayObtainStyledAttributes.getFloat(17, this.mMaxValue);
            this.mMinValue = typedArrayObtainStyledAttributes.getFloat(18, this.mMinValue);
            this.mRulerShowText = typedArrayObtainStyledAttributes.getBoolean(19, this.mRulerShowText);
            this.mRetainLength = typedArrayObtainStyledAttributes.getInteger(20, 0);
        }
        typedArrayObtainStyledAttributes.recycle();
        checkRulerLineParam();
        calculateTotal();
        this.mGestureDetectorCompat = new GestureDetector(getContext(), this);
        this.mScroller = new Scroller(getContext());
        this.mRulerPaint = new Paint(1);
        this.mRulerPaint.setStrokeWidth(this.mRulerLineWidth);
        this.mRulerPaint.setStrokeCap(Paint.Cap.ROUND);
        this.mRulerPaint.setStyle(Paint.Style.FILL);
        this.mTextPaint = new TextPaint(1);
        this.mTextPaint.setTextAlign(Paint.Align.CENTER);
        this.mTextPaint.setTextSize(this.mTextSize);
        this.stopX = ((this.mRulerWidth - this.mDivideByTenHeight) / 2.0f) + this.mDivideByTenHeight;
        setSelectedIndex(0);
    }

    private void checkRulerLineParam() {
        float[] fArr = new float[3];
        fArr[0] = this.mRulerLineHeight;
        fArr[1] = this.mDivideByFiveHeight;
        fArr[2] = this.mDivideByTenHeight;
        float[] fArr2 = new float[3];
        fArr2[0] = this.mRulerLineWidth;
        fArr2[1] = this.mDivideByFiveWidth;
        fArr2[2] = this.mDivideByTenWidth;
        for (int i = 0; i < fArr.length; i++) {
            for (int i2 = 0; i2 < (fArr.length - i) - 1; i2++) {
                if (fArr[i2] > fArr[i2 + 1]) {
                    float f = fArr[i2];
                    fArr[i2] = fArr[i2 + 1];
                    fArr[i2 + 1] = f;
                }
                if (fArr2[i2] > fArr2[i2 + 1]) {
                    float f2 = fArr2[i2];
                    fArr2[i2] = fArr2[i2 + 1];
                    fArr2[i2 + 1] = f2;
                }
            }
        }
        this.mRulerLineHeight = fArr[0];
        this.mDivideByFiveHeight = fArr[1];
        this.mDivideByTenHeight = fArr[2];
        this.mRulerLineWidth = fArr2[0];
        this.mDivideByFiveWidth = fArr2[1];
        this.mDivideByTenWidth = fArr2[2];
    }

    @Override // android.view.View
    public void onMeasure(int i, int i2) {
        setMeasuredDimension(measureWidth(i), measureHeight(i2));
    }

    private int measureWidth(int i) {
        int mode = View.MeasureSpec.getMode(i);
        int size = View.MeasureSpec.getSize(i);
        int suggestedMinimumWidth = getSuggestedMinimumWidth();
        switch (mode) {
            case Integer.MIN_VALUE:
            case 1073741824:
                return size;
            default:
                return suggestedMinimumWidth;
        }
    }

    private int measureHeight(int i) {
        int suggestedMinimumHeight;
        int mode = View.MeasureSpec.getMode(i);
        int size = View.MeasureSpec.getSize(i);
        if (this.mOrientation == 0) {
            suggestedMinimumHeight = ((int) this.mTextSize) * 4;
        } else {
            suggestedMinimumHeight = getSuggestedMinimumHeight();
        }
        switch (mode) {
            case Integer.MIN_VALUE:
                return Math.min(suggestedMinimumHeight, size);
            case 1073741824:
                return Math.max(suggestedMinimumHeight, size);
            default:
                return suggestedMinimumHeight;
        }
    }

    @Override // android.view.View
    public void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        if (i != i3 || i2 != i4) {
            if (this.mOrientation == 0) {
                this.mRulerHeight = i2;
                this.mMaxOverScrollDistance = i / 2.0f;
            } else {
                this.mRulerWidth = i;
                this.mMaxOverScrollDistance = i2 / 2.0f;
            }
            this.mContentLength = ((this.mMaxValue - this.mMinValue) / this.mIntervalValue) * this.mIntervalDistance;
            this.mViewScopeSize = (int) Math.ceil(this.mMaxOverScrollDistance / this.mIntervalDistance);
        }
        this.stopX = ((this.mRulerWidth - this.mDivideByTenHeight) / 2.0f) + this.mDivideByTenHeight;
    }

    @Override // android.view.View
    public void onDraw(Canvas canvas) {
        int i;
        int i2;
        String str;
        String str2;
        super.onDraw(canvas);
        if (this.mRect == null) {
            this.mRect = new Rect();
        }
        if (this.mDecimalFormat == null) {
            this.mDecimalFormat = new DecimalFormat("##0");
        }
        int i3 = this.mSelectedIndex - this.mViewScopeSize;
        int i4 = this.mSelectedIndex + this.mViewScopeSize;
        if (this.mSelectedIndex == this.mMaxValue) {
            i = i4 + this.mViewScopeSize;
            i2 = i3;
        } else if (this.mSelectedIndex == this.mMinValue) {
            i = i4;
            i2 = i3 - this.mViewScopeSize;
        } else {
            i = i4;
            i2 = i3;
        }
        if (this.mDivideByTenWidth >= this.mIntervalDistance) {
            this.mRulerLineWidth = this.mIntervalDistance / 6.0f;
            this.mDivideByFiveWidth = this.mIntervalDistance / 3.0f;
            this.mDivideByTenWidth = this.mIntervalDistance / 2.0f;
        }
        if (this.mOrientation == 0) {
            float f = i2 * this.mIntervalDistance;
            float f2 = this.mRulerHeight - this.mTextSize;
            if (this.mDivideByTenHeight + this.mTextBaseLineDistance > f2) {
                this.mRulerLineHeight = f2 / 2.0f;
                this.mDivideByFiveHeight = (3.0f * f2) / 4.0f;
                this.mDivideByTenHeight = f2;
                this.mTextBaseLineDistance = 0.0f;
            }
            for (int i5 = i2; i5 < i; i5++) {
                if (this.mRulerCount > 0 && i5 >= 0 && i5 < this.mRulerCount) {
                    int i6 = i5 % 2;
                    int i7 = i5 % 5;
                    if (i5 == this.mSelectedIndex) {
                        this.mRulerPaint.setColor(this.mHighlightColor);
                    } else {
                        this.mRulerPaint.setColor(this.mRulerColor);
                    }
                    if (this.mIsDivideByTen && i6 == 0 && i7 == 0) {
                        this.mRulerPaint.setStrokeWidth(this.mDivideByTenWidth);
                        canvas.drawLine(f, 0.0f, f, this.mDivideByTenHeight, this.mRulerPaint);
                    } else if (!this.mIsDivideByFive || i6 == 0 || i7 != 0) {
                        this.mRulerPaint.setStrokeWidth(this.mRulerLineWidth);
                        canvas.drawLine(f, 0.0f, f, this.mRulerLineHeight, this.mRulerPaint);
                    } else {
                        this.mRulerPaint.setStrokeWidth(this.mDivideByFiveWidth);
                        canvas.drawLine(f, 0.0f, f, this.mDivideByFiveHeight, this.mRulerPaint);
                    }
                    this.mTextPaint.setColor(this.mTextColor);
                    if (this.mSelectedIndex == i5) {
                        this.mTextPaint.setColor(this.mHighlightColor);
                    }
                    if (i5 % 10 == 0) {
                        if (this.mTextList == null || this.mTextList.size() <= 0) {
                            str2 = this.mDecimalFormat.format((i5 * this.mIntervalValue) + this.mMinValue);
                        } else {
                            int i8 = i5 / 10;
                            if (i8 < this.mTextList.size()) {
                                str2 = this.mTextList.get(i8);
                            } else {
                                str2 = "";
                            }
                        }
                        this.mTextPaint.getTextBounds(str2, 0, str2.length(), this.mRect);
                        canvas.drawText(str2, 0, str2.length(), f, this.mDivideByTenHeight + this.mRect.height() + this.mTextBaseLineDistance, (Paint) this.mTextPaint);
                    }
                }
                f += this.mIntervalDistance;
            }
            return;
        }
        float f3 = i2 * this.mIntervalDistance;
        float f4 = this.mRulerWidth - this.mTextSize;
        if (this.mDivideByTenHeight + this.mTextBaseLineDistance > f4) {
            this.mRulerLineHeight = f4 / 2.0f;
            this.mDivideByFiveHeight = (3.0f * f4) / 4.0f;
            this.mDivideByTenHeight = f4;
            this.mTextBaseLineDistance = 0.0f;
        }
        float f5 = f3;
        for (int i9 = i2; i9 < i; i9++) {
            if (this.mRulerCount > 0 && i9 >= 0 && i9 < this.mRulerCount) {
                int i10 = i9 % 2;
                int i11 = i9 % 5;
                if (i9 == this.mSelectedIndex) {
                    this.mRulerPaint.setColor(this.mHighlightColor);
                } else {
                    this.mRulerPaint.setColor(this.mRulerColor);
                }
                if (this.mIsDivideByTen && i10 == 0 && i11 == 0) {
                    if (i9 == this.mSelectedIndex) {
                        this.mRulerPaint.setColor(this.mHighlightColor);
                    } else {
                        this.mRulerPaint.setColor(this.mRulerTenColor);
                    }
                    this.mRulerPaint.setStrokeWidth(this.mDivideByTenWidth);
                    canvas.drawLine(this.stopX - this.mDivideByTenHeight, f5, this.stopX, f5, this.mRulerPaint);
                } else if (!this.mIsDivideByFive || i10 == 0 || i11 != 0) {
                    this.mRulerPaint.setStrokeWidth(this.mRulerLineWidth);
                    canvas.drawLine(this.stopX - this.mRulerLineHeight, f5, this.stopX, f5, this.mRulerPaint);
                } else {
                    this.mRulerPaint.setStrokeWidth(this.mDivideByFiveWidth);
                    canvas.drawLine(this.stopX - this.mDivideByFiveHeight, f5, this.stopX, f5, this.mRulerPaint);
                }
                this.mTextPaint.setColor(this.mTextColor);
                if (this.mSelectedIndex == i9) {
                    this.mTextPaint.setColor(this.mHighlightColor);
                }
                if (i9 % 10 == 0) {
                    if (this.mTextList == null || this.mTextList.size() <= 0) {
                        str = this.mDecimalFormat.format(i9 * this.mIntervalValue);
                    } else {
                        int i12 = i9 / 10;
                        if (i12 < this.mTextList.size()) {
                            str = this.mTextList.get(i12);
                        } else {
                            str = "";
                        }
                    }
                    if (this.mRulerShowText) {
                        this.mTextPaint.getTextBounds(str, 0, str.length(), this.mRect);
                        Log.v("xiaoyao", "text2==bbb=" + str);
                        canvas.drawText(str, 0, str.length(), this.mDivideByTenHeight + (this.mRect.width() / 2) + this.mTextBaseLineDistance, ((this.mRect.height() / 2) + f5) - (this.mDivideByTenWidth / 2.0f), (Paint) this.mTextPaint);
                    }
                }
            }
            f5 += this.mIntervalDistance;
        }
    }

    private String format(float f) {
        switch (this.mRetainLength) {
            case 0:
                return new DecimalFormat("##0").format(f);
            case 1:
                return new DecimalFormat("##0.0").format(f);
            case 2:
                return new DecimalFormat("##0.00").format(f);
            case 3:
                return new DecimalFormat("##0.000").format(f);
            default:
                return new DecimalFormat("##0.0").format(f);
        }
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        boolean zOnTouchEvent = this.mGestureDetectorCompat.onTouchEvent(motionEvent);
        if (!this.mFling && 1 == motionEvent.getAction()) {
            adjustPosition();
            zOnTouchEvent = true;
        }
        if (zOnTouchEvent) {
            return true;
        }
        return super.onTouchEvent(motionEvent);
    }

    private void adjustPosition() {
        int scrollY;
        if (this.mOrientation == 0) {
            scrollY = getScrollX();
        } else {
            scrollY = getScrollY();
        }
        float f = ((this.mSelectedIndex * this.mIntervalDistance) - scrollY) - this.mMaxOverScrollDistance;
        if (f != 0.0f) {
            if (this.mOrientation == 0) {
                this.mScroller.startScroll(scrollY, 0, (int) f, 0);
            } else {
                this.mScroller.startScroll(0, scrollY, 0, (int) f);
            }
            postInvalidate();
        }
    }

    @Override // android.view.View
    public void computeScroll() {
        super.computeScroll();
        Log.d("flingAnimatorListener", "mScroller.getCurrY() = " + this.mScroller.getCurrY() + "mScroller.computeScrollOffset() = " + this.mScroller.computeScrollOffset());
        if (this.mScroller.computeScrollOffset()) {
            scrollTo(this.mScroller.getCurrX(), this.mScroller.getCurrY());
            onValueChange();
            invalidate();
        } else {
            Log.d("flingAnimatorListener", "mFling = " + this.mFling);
            if (this.mFling) {
                this.mFling = false;
                adjustPosition();
            }
        }
    }

    @Override // android.view.GestureDetector.OnGestureListener
    public boolean onDown(MotionEvent motionEvent) {
        if (!this.mScroller.isFinished()) {
            this.mScroller.forceFinished(false);
        }
        this.mFling = false;
        if (getParent() != null) {
            getParent().requestDisallowInterceptTouchEvent(true);
        }
        return true;
    }

    @Override // android.view.GestureDetector.OnGestureListener
    public void onShowPress(MotionEvent motionEvent) {
    }

    @Override // android.view.GestureDetector.OnGestureListener
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    @Override // android.view.GestureDetector.OnGestureListener
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
        float scrollY;
        if (this.mOrientation == 0) {
            scrollY = getScrollX();
        } else {
            scrollY = getScrollY();
            f = f2;
        }
        if (scrollY + f <= (-this.mMaxOverScrollDistance)) {
            f = -((int) (scrollY + this.mMaxOverScrollDistance));
        } else if (scrollY + f >= this.mContentLength - this.mMaxOverScrollDistance) {
            f = (int) ((this.mContentLength - this.mMaxOverScrollDistance) - scrollY);
        }
        if (f == 0.0f) {
            return true;
        }
        if (this.mOrientation == 0) {
            scrollBy((int) f, 0);
        } else {
            scrollBy(0, (int) f);
        }
        onValueChange();
        return true;
    }

    @Override // android.view.GestureDetector.OnGestureListener
    public void onLongPress(MotionEvent motionEvent) {
    }

    @Override // android.view.GestureDetector.OnGestureListener
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
        float scrollY;
        if (this.mOrientation == 0) {
            scrollY = getScrollX();
        } else {
            scrollY = getScrollY();
            f = f2;
        }
        if (scrollY < (-this.mMaxOverScrollDistance) || scrollY > this.mContentLength - this.mMaxOverScrollDistance) {
            return false;
        }
        this.mFling = true;
        fling(((int) (-f)) / 2);
        return true;
    }

    private void fling(int i) {
        if (this.mOrientation == 0) {
            this.mScroller.fling(getScrollX(), 0, i, 0, (int) (-this.mMaxOverScrollDistance), (int) (this.mContentLength - this.mMaxOverScrollDistance), 0, 0);
        } else {
            this.mScroller.fling(0, getScrollY(), 0, i, 0, 0, (int) (-this.mMaxOverScrollDistance), (int) (this.mContentLength - this.mMaxOverScrollDistance));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onValueChange() {
        int scrollY;
        String str;
        Log.v("xiaoyao", "mSelectedIndex=ddd==");
        if (this.mOrientation == 0) {
            scrollY = (int) (getScrollX() + this.mMaxOverScrollDistance);
        } else {
            scrollY = (int) (getScrollY() + this.mMaxOverScrollDistance);
        }
        int iRound = Math.round(scrollY / this.mIntervalDistance);
        Log.d("flingAnimatorListener", "onValueChange  tempIndex = " + iRound);
        this.mSelectedIndex = clampSelectedIndex(iRound);
        Log.v("xiaoyao", "mSelectedIndex===" + this.mSelectedIndex);
        if (this.onValueChangeListener != null) {
            if (this.mOrientation == 0) {
                str = format((this.mSelectedIndex * this.mIntervalValue) + this.mMinValue);
            } else {
                str = format(this.mSelectedIndex * this.mIntervalValue);
            }
            try {
                this.onValueChangeListener.onChange(this, Float.parseFloat(str.replace(",", ".")));
            } catch (Exception e) {
            }
        }
    }

    private int clampSelectedIndex(int i) {
        Log.v("xiaoyao", "mSelectedIndex=aaa==");
        if (i < 0) {
            return 0;
        }
        if (i > this.mRulerCount) {
            return this.mRulerCount - 1;
        }
        return i;
    }

    private void setSelectedIndex(int i) {
        Log.v("xiaoyao", "mSelectedIndex=bb==");
        this.mSelectedIndex = clampSelectedIndex(i);
        post(new Runnable() { // from class: com.android.camera.ui.MyRulerView.3
            @Override // java.lang.Runnable
            public void run() {
                int i2 = (int) ((MyRulerView.this.mSelectedIndex * MyRulerView.this.mIntervalDistance) - MyRulerView.this.mMaxOverScrollDistance);
                if (MyRulerView.this.mOrientation == 0) {
                    MyRulerView.this.scrollTo(i2, 0);
                } else {
                    MyRulerView.this.scrollTo(0, i2);
                }
                MyRulerView.this.onValueChange();
                MyRulerView.this.invalidate();
            }
        });
    }

    private void calculateTotal() {
        this.mRulerCount = ((int) ((this.mMaxValue - this.mMinValue) / this.mIntervalValue)) + 1;
    }

    public void setSelectedValue(float f, boolean z) {
        Log.v("xiaoyao", "mSelectedIndex=ccc==");
        if (z) {
            if (this.isFling) {
                return;
            } else {
                this.isFling = true;
            }
        } else {
            this.isFling = false;
        }
        if (f < this.mMinValue) {
            f = this.mMinValue;
        } else if (f > this.mMaxValue) {
            f = this.mMaxValue;
        }
        int iRound = Math.round((f - this.mMinValue) / this.mIntervalValue);
        int i = this.mOrientation;
        if (z) {
            int i2 = (int) ((this.mSelectedIndex * this.mIntervalDistance) - this.mMaxOverScrollDistance);
            this.mSelectedIndex = clampSelectedIndex(iRound);
            playSettlingAnimation(i2, (int) ((this.mSelectedIndex * this.mIntervalDistance) - this.mMaxOverScrollDistance));
            return;
        }
        setSelectedIndex(iRound);
    }

    public void setOnValueChangeListener(OnValueChangeListener onValueChangeListener) {
        this.onValueChangeListener = onValueChangeListener;
    }

    private void playSettlingAnimation(int i, int i2) {
        int i3;
        Log.d("flingAnimatorListener", "startPosition =   " + i + "   endPosition  = " + i2);
        if (i > i2) {
            i3 = i2 + 100;
        } else {
            i3 = i2 - 100;
        }
        this.settlingAnimator = ValueAnimator.ofFloat(i3, i2).setDuration(2000L);
        this.settlingAnimator.setInterpolator(new LinearInterpolator());
        this.settlingAnimator.addUpdateListener(this.flingAnimatorListener);
        this.settlingAnimator.addListener(this.animatorListener);
        this.settlingAnimator.start();
    }
}
