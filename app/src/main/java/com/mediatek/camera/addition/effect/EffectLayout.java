package com.mediatek.camera.addition.effect;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.StateSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EdgeEffect;
import android.widget.LinearLayout;
import com.mediatek.camera.R;
import com.mediatek.camera.util.Log;
import java.util.LinkedList;

/* loaded from: classes.dex */
public class EffectLayout extends ViewGroup {
    private BaseAdapter mAdapter;
    private int mBottomPosition;
    private int mColumnCount;
    private int mColumnHeight;
    private int mColumnWidth;
    private Context mContext;
    private float mDensity;
    private int mDispalyHeight;
    private int mDisplayWidth;
    private int mDownPointX;
    private int mDownPointY;
    private int mDownRow;
    private long mDownTime;
    private EdgeEffect mEdgeGlowBottom;
    private EdgeEffect mEdgeGlowTop;
    private int mEventState;
    private Handler mHandler;
    private OnItemClickListener mItemClickListener;
    private int mLastDirection;
    private int mLastMoveDistance;
    private int mLastTopRow;
    private int mMotionY;
    private long mMoveTime;
    private boolean mNeedScrollOut;
    private boolean mNeedUpdateView;
    private OnScrollListener mOnScrollListener;
    private int mPressX;
    private int mPressY;
    private View mPressedView;
    private LinkedList<View> mRecycleView;
    private int mSelectedPosition;
    private View mSelectedView;
    private int mSelectionBottomPadding;
    private int mSelectionLeftPadding;
    private int mSelectionRightPadding;
    private int mSelectionTopPadding;
    private Drawable mSelectorDrawable;
    private Rect mSelectorRect;
    private int mTop;
    private int mTopPosition;
    private boolean mTouchFocused;
    private int mUpPointX;
    private int mUpPointY;
    private long mUpTime;

    public interface OnItemClickListener {
        void onItemClick(View view, int i);
    }

    public interface OnScrollListener {
        void onScrollDone(EffectLayout effectLayout, int i, int i2);

        void onScrollOut(EffectLayout effectLayout, int i);
    }

    public EffectLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mLastTopRow = -1;
        this.mDensity = 0.0f;
        this.mTouchFocused = false;
        this.mLastDirection = -1;
        this.mNeedUpdateView = false;
        this.mEventState = 2;
        this.mSelectorRect = new Rect();
        this.mSelectionLeftPadding = 0;
        this.mSelectionTopPadding = 0;
        this.mSelectionRightPadding = 0;
        this.mSelectionBottomPadding = 0;
        this.mNeedScrollOut = false;
        this.mRecycleView = new LinkedList<>();
        this.mContext = context;
        this.mDensity = context.getResources().getDisplayMetrics().density;
        this.mSelectorDrawable = getResources().getDrawable(R.drawable.bg_pressed);
        this.mSelectorDrawable.setCallback(this);
        setWillNotDraw(false);
        this.mHandler = new Handler() { // from class: com.mediatek.camera.addition.effect.EffectLayout.1
            @Override // android.os.Handler
            public void handleMessage(Message message) {
                switch (message.what) {
                    case 100:
                        break;
                    case 101:
                        int[] iArr = (int[]) message.obj;
                        EffectLayout.this.scrollViewByDistance(iArr[0], iArr[1], iArr[2]);
                        EffectLayout.this.showSelectedBorder(EffectLayout.this.mSelectedPosition);
                        break;
                    case 102:
                        EffectLayout.this.onItemClick((View) message.obj);
                        break;
                    case 103:
                        EffectLayout.this.scrollDone();
                        break;
                }
            }
        };
    }

    @Override // android.view.ViewGroup, android.view.View
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        Log.m31d("EffectLayout", "onLayout(), left:" + i + ", top:" + i2 + ", right:" + i3 + ", bottom:" + i4);
        int childCount = getChildCount();
        for (int i5 = 0; i5 < childCount; i5++) {
            View childAt = getChildAt(i5);
            childAt.layout(childAt.getLeft(), childAt.getTop(), childAt.getRight(), childAt.getBottom());
        }
    }

    @Override // android.view.View
    protected void onMeasure(int i, int i2) {
        Log.m31d("EffectLayout", "onMeasure()");
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(this.mDisplayWidth, 1073741824), View.MeasureSpec.makeMeasureSpec(this.mDispalyHeight, 1073741824));
        int childCount = getChildCount();
        for (int i3 = 0; i3 < childCount; i3++) {
            getChildAt(i3).measure(this.mColumnWidth, this.mColumnHeight);
        }
    }

    @Override // android.view.View
    public void onDraw(Canvas canvas) {
        Log.m31d("EffectLayout", "onDraw");
        if (this.mNeedScrollOut) {
            this.mHandler.post(new Runnable() { // from class: com.mediatek.camera.addition.effect.EffectLayout.2
                @Override // java.lang.Runnable
                public void run() {
                    Log.m31d("EffectLayout", "onScrollOut");
                    if (EffectLayout.this.mOnScrollListener != null) {
                        EffectLayout.this.mOnScrollListener.onScrollOut(EffectLayout.this, 1);
                    }
                }
            });
            this.mNeedScrollOut = false;
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    @Override // android.view.ViewGroup
    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        Log.m31d("EffectLayout", "action = " + motionEvent.getAction() + ", mTopPosition = " + this.mTopPosition + ", X = " + motionEvent.getX() + ", Y = " + motionEvent.getY());
        switch (motionEvent.getAction()) {
            case 0:
                this.mDownTime = System.currentTimeMillis();
                this.mPressX = (int) motionEvent.getX();
                int y = (int) motionEvent.getY();
                this.mMotionY = y;
                this.mPressY = y;
                if (this.mTopPosition >= this.mPressY) {
                    return true;
                }
                this.mEventState = 0;
                if (this.mHandler != null) {
                    this.mHandler.removeMessages(101);
                }
                releaseEdgeEffects();
                clearPressedState();
                this.mPressedView = getViewByPosition(this.mPressX, this.mPressY);
                this.mDownRow = computeCurrentRow(this.mPressY);
                this.mDownPointX = this.mPressX;
                this.mDownPointY = this.mMotionY;
                return false;
            case 1:
                this.mEventState = 2;
                this.mUpTime = System.currentTimeMillis();
                this.mUpPointX = (int) motionEvent.getX();
                this.mUpPointY = (int) motionEvent.getY();
                if (isTouchFocus(this.mUpPointX, this.mUpPointY, false) && (!this.mTouchFocused) && this.mPressedView != null) {
                    this.mSelectorRect.setEmpty();
                    setPressed(true);
                    this.mPressedView.setPressed(true);
                    positionSelector(this.mDownRow, this.mPressedView);
                }
                releaseEdgeEffects();
                if (this.mTopPosition > 0 && this.mTopPosition <= this.mDispalyHeight / 2) {
                    scrollViewByDistance(this.mTopPosition, 0, 50);
                } else if (this.mTopPosition > this.mDispalyHeight / 2) {
                    scrollViewByDistance(this.mTopPosition, this.mDispalyHeight, 50);
                }
                if (this.mTopPosition < 0) {
                    int i = this.mTopPosition % this.mColumnHeight;
                    if (Math.abs(i) >= this.mColumnHeight / 2) {
                        scrollViewByDistance(this.mTopPosition, this.mTopPosition - (this.mColumnHeight - Math.abs(i)), 20);
                    } else {
                        scrollViewByDistance(this.mTopPosition, this.mTopPosition - i, 20);
                    }
                }
                Log.m31d("EffectLayout", "mUpPointX:" + this.mUpPointX + ", mUpPointY:" + this.mUpPointY + ", distance:" + Math.abs(this.mUpPointY - this.mDownPointY));
                if (Math.abs(this.mUpPointY - this.mDownPointY) <= (this.mDensity * 8.0f) + 0.5f && Math.abs(this.mUpPointX - this.mDownPointX) <= (this.mDensity * 8.0f) + 0.5f) {
                    this.mTouchFocused = false;
                    return false;
                }
                if (isSelectedViewInSight() && this.mSelectedView != null) {
                    this.mSelectedView.setBackgroundResource(R.drawable.selected_border);
                }
                invalidate();
                return true;
            case 2:
                this.mEventState = 1;
                this.mMoveTime = System.currentTimeMillis();
                int x = (int) motionEvent.getX();
                int y2 = (int) motionEvent.getY();
                if (isTouchFocus(x, y2, true) && (!this.mTouchFocused) && this.mPressedView != null) {
                    this.mSelectorRect.setEmpty();
                    setPressed(true);
                    this.mPressedView.setPressed(true);
                    positionSelector(this.mDownRow, this.mPressedView);
                    this.mTouchFocused = true;
                } else if (!isTouchFocus(x, y2, true) && this.mTouchFocused && this.mPressedView != null) {
                    this.mSelectorRect.setEmpty();
                    setPressed(false);
                    this.mPressedView.setPressed(false);
                    this.mTouchFocused = false;
                }
                int iAbs = y2 - this.mMotionY;
                this.mLastMoveDistance = iAbs;
                if (Math.abs(iAbs) > this.mColumnHeight) {
                    iAbs = (this.mColumnHeight * iAbs) / Math.abs(iAbs);
                }
                if (Math.abs(iAbs) > 0) {
                    scrollViewByDistance(iAbs);
                }
                this.mMotionY = y2;
                return false;
            case 3:
                this.mSelectorRect.setEmpty();
                releaseEdgeEffects();
                invalidate();
                return false;
            default:
                return false;
        }
    }

    public void scrollToSelectedPosition(int i) {
        int i2 = i / 3;
        if (i2 > 1) {
            scrollViewByDistance(this.mTopPosition, -((i2 - 1) * this.mColumnHeight), this.mColumnHeight / 2);
        } else if (i2 <= 1) {
            scrollViewByDistance(this.mTopPosition, 0, this.mColumnHeight / 2);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void scrollViewByDistance(int i, int i2, int i3) {
        int i4;
        boolean z;
        Log.m31d("EffectLayout", "scrollViewByDistance(), startPoint:" + i + ", endPoint:" + i2 + ",step:" + i3 + ", mEventState:" + this.mEventState);
        int i5 = i2 - i;
        if (i5 <= 0) {
            boolean z2 = i5 + i3 <= 0;
            if (i5 + i3 <= 0) {
                i5 = -i3;
            }
            scrollViewByDistance(i5);
            boolean z3 = z2;
            i4 = i - i3;
            z = z3;
        } else {
            boolean z4 = i5 - i3 >= 0;
            if (i5 - i3 >= 0) {
                i5 = i3;
            }
            scrollViewByDistance(i5);
            boolean z5 = z4;
            i4 = i + i3;
            z = z5;
        }
        int[] iArr = {i4, i2, i3};
        if (this.mEventState == 2) {
            if (z) {
                this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(101, iArr), 10L);
            } else {
                this.mHandler.sendEmptyMessage(103);
            }
        }
    }

    private void scrollViewByDistance(int i) {
        if ((i < 0 || this.mTopPosition > 0) && i < 0 && this.mBottomPosition >= this.mColumnHeight * 3 && this.mBottomPosition + i < this.mColumnHeight * 3) {
            this.mEdgeGlowBottom.onPull(i / getHeight());
            if (!this.mEdgeGlowTop.isFinished()) {
                this.mEdgeGlowTop.onRelease();
            }
            if (!this.mEdgeGlowTop.isFinished() || (!this.mEdgeGlowBottom.isFinished())) {
                postInvalidateOnAnimation();
            }
            i = (this.mColumnHeight * 3) - this.mBottomPosition;
        }
        Log.m31d("EffectLayout", "mTopPosition = " + this.mTopPosition + "deltaY = " + i);
        if (this.mTopPosition < 0 && Math.abs(i) > this.mColumnHeight) {
            if (i > 0) {
                i -= this.mColumnHeight;
            } else {
                i += this.mColumnHeight;
            }
        }
        this.mTopPosition += i;
        this.mBottomPosition += i;
        scrollBy(0, -i);
        if (this.mTopPosition <= 0) {
            scrollView(this.mTopPosition);
        }
        if (this.mTopPosition >= this.mDispalyHeight) {
            this.mNeedScrollOut = true;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void scrollDone() {
        if (this.mTopPosition <= 0) {
            int i = (-this.mTopPosition) / this.mColumnHeight;
            if (Math.abs(this.mTopPosition % this.mColumnHeight) >= this.mColumnHeight / 2) {
                i++;
            }
            int i2 = i * 3;
            int count = i2 + 9;
            if (count > this.mAdapter.getCount()) {
                count = this.mAdapter.getCount();
            }
            if (this.mOnScrollListener != null) {
                this.mOnScrollListener.onScrollDone(this, i2, count);
            }
            this.mNeedUpdateView = true;
        }
    }

    private View getViewByPosition(int i, int i2) {
        int iComputeCurrentRow = computeCurrentRow(i2);
        int iComputeCurrentColumn = computeCurrentColumn(i);
        LinearLayout linearLayout = (LinearLayout) getChildAt(iComputeCurrentRow % 4);
        if (linearLayout != null) {
            return linearLayout.getChildAt(iComputeCurrentColumn);
        }
        return null;
    }

    private boolean isTouchFocus(int i, int i2, boolean z) {
        int iAbs = Math.abs(i - this.mPressX);
        int iAbs2 = Math.abs(i2 - this.mPressY);
        int i3 = (int) ((this.mDensity * 8.0f) + 0.5f);
        if (z) {
            long j = this.mMoveTime - this.mDownTime;
            Log.m31d("EffectLayout", "Moving, distanceX:" + iAbs + ", distanceY:" + iAbs2 + ", max:" + i3 + ", timeInterval:" + j);
            return iAbs < i3 && iAbs2 < i3 && j > 100;
        }
        Log.m31d("EffectLayout", "Up, distanceX:" + iAbs + ", distanceY:" + iAbs2 + ", max:" + i3 + ", timeInterval:" + (this.mUpTime - this.mDownTime));
        return iAbs < i3 && iAbs2 < i3;
    }

    private void scrollView(int i) {
        int i2 = (-i) / this.mColumnHeight;
        int count = this.mAdapter.getCount();
        if (this.mLastTopRow == i2) {
            int i3 = i2 + 3;
            final LinearLayout linearLayout = (LinearLayout) getChildAt(i3 % 4);
            if (this.mNeedUpdateView) {
                int i4 = i3 * 3;
                for (int i5 = i4; i5 < this.mColumnCount + i4 && i5 < count; i5++) {
                    this.mAdapter.getView(i5, this.mRecycleView.get(i5 % 12), linearLayout);
                }
                this.mNeedUpdateView = false;
            }
            this.mHandler.postDelayed(new Runnable() { // from class: com.mediatek.camera.addition.effect.EffectLayout.3
                @Override // java.lang.Runnable
                public void run() {
                    if (linearLayout != null) {
                        linearLayout.setAlpha(1.0f);
                    }
                }
            }, 150L);
            return;
        }
        Log.m31d("EffectLayout", "topRow:" + i2 + ", mLastTopRow:" + this.mLastTopRow + ",topPosition:" + i);
        int i6 = (-i) % this.mColumnHeight;
        if (this.mLastTopRow > i2) {
            final LinearLayout linearLayout2 = (LinearLayout) getChildAt(i2);
            if (linearLayout2 != null) {
                linearLayout2.layout(0, this.mColumnHeight * i2, this.mColumnWidth * this.mColumnCount, (i2 + 1) * this.mColumnHeight);
                this.mHandler.postDelayed(new Runnable() { // from class: com.mediatek.camera.addition.effect.EffectLayout.4
                    @Override // java.lang.Runnable
                    public void run() {
                        if (linearLayout2 != null) {
                            linearLayout2.setAlpha(1.0f);
                        }
                    }
                }, 150L);
            }
            int i7 = i2 * 3;
            for (int i8 = i7; i8 < this.mColumnCount + i7; i8++) {
                this.mAdapter.getView(i8, this.mRecycleView.get(i8), linearLayout2);
                this.mRecycleView.get(i8).setVisibility(0);
                this.mRecycleView.get(i8).setClickable(true);
            }
        } else if (i6 >= 0) {
            if (i2 > 0) {
                int i9 = i2 - 1;
                int i10 = i9 + 4;
                final LinearLayout linearLayout3 = (LinearLayout) getChildAt(i9);
                if (linearLayout3 != null) {
                    linearLayout3.layout(0, this.mColumnHeight * i10, this.mColumnWidth * this.mColumnCount, (i10 + 1) * this.mColumnHeight);
                    this.mHandler.post(new Runnable() { // from class: com.mediatek.camera.addition.effect.EffectLayout.5
                        @Override // java.lang.Runnable
                        public void run() {
                            if (linearLayout3 != null) {
                                linearLayout3.setAlpha(0.0f);
                            }
                        }
                    });
                }
                if (i9 - 1 >= 0) {
                    final LinearLayout linearLayout4 = (LinearLayout) getChildAt(i9 - 1);
                    this.mHandler.post(new Runnable() { // from class: com.mediatek.camera.addition.effect.EffectLayout.6
                        @Override // java.lang.Runnable
                        public void run() {
                            if (linearLayout3 != null) {
                                linearLayout4.setAlpha(1.0f);
                            }
                        }
                    });
                }
                int i11 = i10 * 3;
                int i12 = i9 * 3;
                int i13 = i12;
                for (int i14 = i11; i14 < this.mColumnCount + i11 && i14 < count; i14++) {
                    this.mAdapter.getView(i14, this.mRecycleView.get(i13), linearLayout3);
                    i13++;
                }
                int i15 = (this.mColumnCount + i11) - count;
                for (int i16 = 0; i16 < i15; i16++) {
                    View childAt = linearLayout3 != null ? linearLayout3.getChildAt((this.mColumnCount - i16) - 1) : null;
                    if (childAt != null) {
                        childAt.setVisibility(4);
                        childAt.setClickable(false);
                    }
                }
            } else if (i2 == 0) {
                LinearLayout linearLayout5 = (LinearLayout) getChildAt(3);
                int i17 = 9;
                while (true) {
                    int i18 = i17;
                    if (i18 >= this.mColumnCount + 9 || i18 >= count) {
                        break;
                    }
                    this.mAdapter.getView(i18, this.mRecycleView.get(i18), linearLayout5);
                    i17 = i18 + 1;
                }
            }
        }
        this.mLastTopRow = i2;
    }

    public void showSelectedBorder(int i) {
        ViewGroup viewGroup;
        View childAt;
        this.mSelectedPosition = i;
        if (isSelectedViewInSight() && (viewGroup = (ViewGroup) getChildAt((i / 3) % 4)) != null && (childAt = viewGroup.getChildAt(i % 3)) != null) {
            this.mSelectedView = childAt;
            childAt.setBackgroundResource(R.drawable.selected_border);
        }
    }

    private boolean isSelectedViewInSight() {
        int i = ((this.mSelectedPosition / 3) + 1) * this.mColumnHeight;
        if (i >= (-this.mTopPosition) && i <= (-this.mTopPosition) + this.mDispalyHeight) {
            return true;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onItemClick(View view) {
        this.mSelectorRect.setEmpty();
        setPressed(false);
        if (this.mPressedView == null) {
            return;
        }
        this.mPressedView.setPressed(false);
        clearPressedState();
        this.mSelectedView = view;
        view.setBackgroundResource(R.drawable.selected_border);
        int iPointToPosition = pointToPosition(this.mUpPointX, this.mUpPointY);
        if (iPointToPosition >= this.mAdapter.getCount()) {
            iPointToPosition = this.mAdapter.getCount() - 1;
        }
        this.mSelectedPosition = iPointToPosition;
        if (this.mItemClickListener != null) {
            this.mItemClickListener.onItemClick(view, iPointToPosition);
        }
    }

    private int pointToPosition(int i, int i2) {
        int iComputeCurrentRow = computeCurrentRow(i2);
        return (iComputeCurrentRow * this.mColumnCount) + computeCurrentColumn(i);
    }

    private int computeCurrentRow(int i) {
        return (i - this.mTopPosition) / this.mColumnHeight;
    }

    private int computeCurrentColumn(int i) {
        return i / this.mColumnWidth;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mItemClickListener = onItemClickListener;
    }

    public void setColumnCount(int i) {
        this.mColumnCount = i;
    }

    public void setColumnWidth(int i) {
        this.mColumnWidth = i;
    }

    public void setColumnHeight(int i) {
        this.mColumnHeight = i;
    }

    public void setDisplaySize(int i, int i2) {
        if (this.mDisplayWidth != i || this.mDispalyHeight != i2) {
            this.mDisplayWidth = i;
            this.mDispalyHeight = i2;
            requestLayout();
        }
    }

    private void bindView() {
        removeAllViews();
        this.mRecycleView.clear();
        int count = this.mAdapter.getCount();
        int i = (this.mDispalyHeight / this.mColumnHeight) * this.mColumnCount;
        int iMin = Math.min(count, this.mColumnCount + i);
        Log.m31d("EffectLayout", "bindView(), maxCountsInScreen:" + i + ", count:" + iMin);
        int i2 = 0;
        View[] viewArr = null;
        for (int i3 = 0; i3 < iMin; i3++) {
            if (i2 % this.mColumnCount == 0) {
                viewArr = new View[this.mColumnCount];
            }
            View view = this.mAdapter.getView(i3, null, null);
            if (view != null) {
                this.mRecycleView.add(view);
                view.setOnClickListener(new View.OnClickListener() { // from class: com.mediatek.camera.addition.effect.EffectLayout.7
                    @Override // android.view.View.OnClickListener
                    public void onClick(View view2) {
                        EffectLayout.this.mHandler.sendMessageDelayed(EffectLayout.this.mHandler.obtainMessage(102, view2), 200L);
                    }
                });
                viewArr[i2] = view;
                i2++;
                if (i2 == this.mColumnCount) {
                    LinearLayout linearLayout = new LinearLayout(getContext());
                    linearLayout.setMotionEventSplittingEnabled(false);
                    addLayout(linearLayout, viewArr);
                    i2 = 0;
                } else if (i3 >= iMin - 1 && i2 > 0) {
                    addLayout(new LinearLayout(getContext()), viewArr);
                }
            }
        }
    }

    private void addLayout(LinearLayout linearLayout, View[] viewArr) {
        linearLayout.setOrientation(0);
        linearLayout.setLayoutDirection(0);
        addCell(linearLayout, viewArr);
        addView(linearLayout);
        linearLayout.measure(this.mColumnWidth * this.mColumnCount, this.mColumnHeight);
        linearLayout.layout(0, this.mTop, this.mColumnWidth * this.mColumnCount, this.mTop + this.mColumnHeight);
        this.mTop += this.mColumnHeight;
    }

    private void addCell(LinearLayout linearLayout, View[] viewArr) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(this.mColumnWidth, this.mColumnHeight);
        for (View view : viewArr) {
            if (view != null) {
                linearLayout.addView(view, layoutParams);
            }
        }
    }

    public void setAdapter(BaseAdapter baseAdapter) {
        this.mAdapter = baseAdapter;
        this.mBottomPosition = this.mColumnHeight * (baseAdapter.getCount() / this.mColumnCount);
        if (baseAdapter.getCount() % this.mColumnCount != 0) {
            this.mBottomPosition += this.mColumnHeight;
        }
        bindView();
    }

    private void clearPressedState() {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            LinearLayout linearLayout = (LinearLayout) getChildAt(i);
            int childCount2 = linearLayout.getChildCount();
            for (int i2 = 0; i2 < childCount2; i2++) {
                linearLayout.getChildAt(i2).setBackgroundResource(R.drawable.unselected_border);
            }
        }
    }

    public void setOnScrollListener(OnScrollListener onScrollListener) {
        this.mOnScrollListener = onScrollListener;
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        drawSelector(canvas);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        this.mSelectorDrawable.setState(getDrawableState());
        invalidate();
    }

    @Override // android.view.View
    protected void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        this.mSelectorDrawable.setBounds(0, 0, i, i2);
    }

    @Override // android.view.View
    protected boolean verifyDrawable(Drawable drawable) {
        return super.verifyDrawable(drawable) || drawable == this.mSelectorDrawable;
    }

    @Override // android.view.ViewGroup, android.view.View
    public void jumpDrawablesToCurrentState() {
        super.jumpDrawablesToCurrentState();
        if (this.mSelectorDrawable != null) {
            this.mSelectorDrawable.jumpToCurrentState();
        }
    }

    public void setSelector(int i) {
        setSelector(getResources().getDrawable(i));
    }

    public void setSelector(Drawable drawable) {
        if (this.mSelectorDrawable != null) {
            this.mSelectorDrawable.setCallback(null);
            unscheduleDrawable(this.mSelectorDrawable);
        }
        this.mSelectorDrawable = drawable;
        Rect rect = new Rect();
        drawable.getPadding(rect);
        this.mSelectionLeftPadding = rect.left;
        this.mSelectionTopPadding = rect.top;
        this.mSelectionRightPadding = rect.right;
        this.mSelectionBottomPadding = rect.bottom;
        drawable.setCallback(this);
        updateSelectorState();
    }

    private void updateSelectorState() {
        if (this.mSelectorDrawable != null) {
            if (shouldShowSelector()) {
                this.mSelectorDrawable.setState(getDrawableState());
            } else {
                this.mSelectorDrawable.setState(StateSet.NOTHING);
            }
        }
    }

    private boolean shouldShowSelector() {
        if (isInTouchMode()) {
            return isPressed();
        }
        return true;
    }

    private void drawSelector(Canvas canvas) {
        if (!this.mSelectorRect.isEmpty()) {
            Drawable drawable = this.mSelectorDrawable;
            drawable.setBounds(this.mSelectorRect);
            drawable.draw(canvas);
        }
    }

    private void positionSelector(int i, View view) {
        Rect rect = this.mSelectorRect;
        rect.set(view.getLeft(), this.mColumnHeight * i, view.getRight(), (i + 1) * this.mColumnHeight);
        positionSelector(rect.left, rect.top, rect.right, rect.bottom);
        refreshDrawableState();
    }

    private void positionSelector(int i, int i2, int i3, int i4) {
        this.mSelectorRect.set(i - this.mSelectionLeftPadding, i2 - this.mSelectionTopPadding, this.mSelectionRightPadding + i3, this.mSelectionBottomPadding + i4);
    }

    @Override // android.view.View
    public void setOverScrollMode(int i) {
        if (i != 2) {
            if (this.mEdgeGlowTop == null) {
                Context context = getContext();
                this.mEdgeGlowTop = new EdgeEffect(context);
                this.mEdgeGlowBottom = new EdgeEffect(context);
            }
        } else {
            this.mEdgeGlowTop = null;
            this.mEdgeGlowBottom = null;
        }
        super.setOverScrollMode(i);
    }

    @Override // android.view.View
    public void draw(Canvas canvas) {
        super.draw(canvas);
        drawEdgeEffects(canvas);
    }

    private void drawEdgeEffects(Canvas canvas) {
        if (this.mEdgeGlowTop != null) {
            int scrollY = getScrollY();
            if (!this.mEdgeGlowTop.isFinished()) {
                int iSave = canvas.save();
                int width = getWidth();
                canvas.translate(0.0f, Math.min(0, scrollY));
                this.mEdgeGlowTop.setSize(width, getHeight());
                if (this.mEdgeGlowTop.draw(canvas)) {
                    postInvalidateOnAnimation();
                }
                canvas.restoreToCount(iSave);
            }
            if (!this.mEdgeGlowBottom.isFinished()) {
                int iSave2 = canvas.save();
                int width2 = getWidth();
                int height = getHeight();
                canvas.translate(-width2, Math.max(getScrollRange(), scrollY) + height);
                canvas.rotate(180.0f, width2, 0.0f);
                this.mEdgeGlowBottom.setSize(width2, height);
                if (this.mEdgeGlowBottom.draw(canvas)) {
                    postInvalidateOnAnimation();
                }
                canvas.restoreToCount(iSave2);
            }
        }
    }

    private int getScrollRange() {
        if (getChildCount() > 0) {
            return Math.max(0, getChildAt(0).getHeight() - getHeight());
        }
        return 0;
    }

    private void releaseEdgeEffects() {
        if (this.mEdgeGlowTop != null) {
            this.mEdgeGlowTop.onRelease();
            this.mEdgeGlowBottom.onRelease();
        }
    }

    private void finishGlows() {
        if (this.mEdgeGlowTop != null) {
            this.mEdgeGlowTop.finish();
            this.mEdgeGlowBottom.finish();
        }
    }

    @Override // android.view.View
    public void onWindowFocusChanged(boolean z) {
        super.onWindowFocusChanged(z);
        if (!hasWindowFocus()) {
            finishGlows();
            invalidate();
        }
    }
}
