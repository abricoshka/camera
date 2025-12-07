package android.support.v4.widget;

import android.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.p000v8.renderscript.ScriptIntrinsicBLAS;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.AbsSavedState;
import android.support.v4.view.AccessibilityDelegateCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class DrawerLayout extends ViewGroup {
    static final boolean CAN_HIDE_DESCENDANTS;
    private static final boolean SET_DRAWER_SHADOW_FROM_ELEVATION;
    private final ChildAccessibilityDelegate mChildAccessibilityDelegate;
    private boolean mChildrenCanceledTouch;
    private boolean mDisallowInterceptRequested;
    private boolean mDrawStatusBarBackground;
    private float mDrawerElevation;
    private int mDrawerState;
    private boolean mFirstLayout;
    private boolean mInLayout;
    private float mInitialMotionX;
    private float mInitialMotionY;
    private Object mLastInsets;
    private final ViewDragCallback mLeftCallback;
    private final ViewDragHelper mLeftDragger;
    private List<DrawerListener> mListeners;
    private int mLockModeEnd;
    private int mLockModeLeft;
    private int mLockModeRight;
    private int mLockModeStart;
    private int mMinDrawerMargin;
    private final ArrayList<View> mNonDrawerViews;
    private final ViewDragCallback mRightCallback;
    private final ViewDragHelper mRightDragger;
    private int mScrimColor;
    private float mScrimOpacity;
    private Paint mScrimPaint;
    private Drawable mShadowEnd;
    private Drawable mShadowLeft;
    private Drawable mShadowLeftResolved;
    private Drawable mShadowRight;
    private Drawable mShadowRightResolved;
    private Drawable mShadowStart;
    private Drawable mStatusBarBackground;
    private static final int[] THEME_ATTRS = {R.attr.colorPrimaryDark};
    static final int[] LAYOUT_ATTRS = {R.attr.layout_gravity};

    public interface DrawerListener {
        void onDrawerClosed(View view);

        void onDrawerOpened(View view);

        void onDrawerSlide(View view, float f);

        void onDrawerStateChanged(int i);
    }

    @Retention(RetentionPolicy.SOURCE)
    private @interface EdgeGravity {
    }

    @Retention(RetentionPolicy.SOURCE)
    private @interface LockMode {
    }

    static {
        CAN_HIDE_DESCENDANTS = Build.VERSION.SDK_INT >= 19;
        SET_DRAWER_SHADOW_FROM_ELEVATION = Build.VERSION.SDK_INT >= 21;
    }

    public void setDrawerLockMode(int i, int i2) {
        int absoluteGravity = GravityCompat.getAbsoluteGravity(i2, ViewCompat.getLayoutDirection(this));
        switch (i2) {
            case 3:
                this.mLockModeLeft = i;
                break;
            case 5:
                this.mLockModeRight = i;
                break;
            case 8388611:
                this.mLockModeStart = i;
                break;
            case 8388613:
                this.mLockModeEnd = i;
                break;
        }
        if (i != 0) {
            (absoluteGravity == 3 ? this.mLeftDragger : this.mRightDragger).cancel();
        }
        switch (i) {
            case 1:
                View viewFindDrawerWithGravity = findDrawerWithGravity(absoluteGravity);
                if (viewFindDrawerWithGravity != null) {
                    closeDrawer(viewFindDrawerWithGravity);
                    break;
                }
                break;
            case 2:
                View viewFindDrawerWithGravity2 = findDrawerWithGravity(absoluteGravity);
                if (viewFindDrawerWithGravity2 != null) {
                    openDrawer(viewFindDrawerWithGravity2);
                    break;
                }
                break;
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Removed duplicated region for block: B:4:0x0009 A[RETURN] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public int getDrawerLockMode(int r5) {
        /*
            r4 = this;
            r3 = 0
            r2 = 3
            int r0 = android.support.v4.view.ViewCompat.getLayoutDirection(r4)
            switch(r5) {
                case 3: goto La;
                case 5: goto L1b;
                case 8388611: goto L2c;
                case 8388613: goto L3d;
                default: goto L9;
            }
        L9:
            return r3
        La:
            int r1 = r4.mLockModeLeft
            if (r1 == r2) goto L11
            int r0 = r4.mLockModeLeft
            return r0
        L11:
            if (r0 != 0) goto L18
            int r0 = r4.mLockModeStart
        L15:
            if (r0 == r2) goto L9
            return r0
        L18:
            int r0 = r4.mLockModeEnd
            goto L15
        L1b:
            int r1 = r4.mLockModeRight
            if (r1 == r2) goto L22
            int r0 = r4.mLockModeRight
            return r0
        L22:
            if (r0 != 0) goto L29
            int r0 = r4.mLockModeEnd
        L26:
            if (r0 == r2) goto L9
            return r0
        L29:
            int r0 = r4.mLockModeStart
            goto L26
        L2c:
            int r1 = r4.mLockModeStart
            if (r1 == r2) goto L33
            int r0 = r4.mLockModeStart
            return r0
        L33:
            if (r0 != 0) goto L3a
            int r0 = r4.mLockModeLeft
        L37:
            if (r0 == r2) goto L9
            return r0
        L3a:
            int r0 = r4.mLockModeRight
            goto L37
        L3d:
            int r1 = r4.mLockModeEnd
            if (r1 == r2) goto L44
            int r0 = r4.mLockModeEnd
            return r0
        L44:
            if (r0 != 0) goto L4b
            int r0 = r4.mLockModeRight
        L48:
            if (r0 == r2) goto L9
            return r0
        L4b:
            int r0 = r4.mLockModeLeft
            goto L48
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v4.widget.DrawerLayout.getDrawerLockMode(int):int");
    }

    public int getDrawerLockMode(View view) {
        if (!isDrawerView(view)) {
            throw new IllegalArgumentException("View " + view + " is not a drawer");
        }
        return getDrawerLockMode(((LayoutParams) view.getLayoutParams()).gravity);
    }

    void updateDrawerState(int i, int i2, View view) {
        int i3 = 1;
        int viewDragState = this.mLeftDragger.getViewDragState();
        int viewDragState2 = this.mRightDragger.getViewDragState();
        if (viewDragState != 1 && viewDragState2 != 1) {
            i3 = (viewDragState == 2 || viewDragState2 == 2) ? 2 : 0;
        }
        if (view != null && i2 == 0) {
            LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
            if (layoutParams.onScreen == 0.0f) {
                dispatchOnDrawerClosed(view);
            } else if (layoutParams.onScreen == 1.0f) {
                dispatchOnDrawerOpened(view);
            }
        }
        if (i3 != this.mDrawerState) {
            this.mDrawerState = i3;
            if (this.mListeners != null) {
                for (int size = this.mListeners.size() - 1; size >= 0; size--) {
                    this.mListeners.get(size).onDrawerStateChanged(i3);
                }
            }
        }
    }

    void dispatchOnDrawerClosed(View view) {
        View rootView;
        LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
        if ((layoutParams.openState & 1) == 1) {
            layoutParams.openState = 0;
            if (this.mListeners != null) {
                for (int size = this.mListeners.size() - 1; size >= 0; size--) {
                    this.mListeners.get(size).onDrawerClosed(view);
                }
            }
            updateChildrenImportantForAccessibility(view, false);
            if (hasWindowFocus() && (rootView = getRootView()) != null) {
                rootView.sendAccessibilityEvent(32);
            }
        }
    }

    void dispatchOnDrawerOpened(View view) {
        LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
        if ((layoutParams.openState & 1) == 0) {
            layoutParams.openState = 1;
            if (this.mListeners != null) {
                for (int size = this.mListeners.size() - 1; size >= 0; size--) {
                    this.mListeners.get(size).onDrawerOpened(view);
                }
            }
            updateChildrenImportantForAccessibility(view, true);
            if (hasWindowFocus()) {
                sendAccessibilityEvent(32);
            }
        }
    }

    private void updateChildrenImportantForAccessibility(View view, boolean z) {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = getChildAt(i);
            if ((!z && (!isDrawerView(childAt))) || (z && childAt == view)) {
                ViewCompat.setImportantForAccessibility(childAt, 1);
            } else {
                ViewCompat.setImportantForAccessibility(childAt, 4);
            }
        }
    }

    void dispatchOnDrawerSlide(View view, float f) {
        if (this.mListeners != null) {
            for (int size = this.mListeners.size() - 1; size >= 0; size--) {
                this.mListeners.get(size).onDrawerSlide(view, f);
            }
        }
    }

    void setDrawerViewOffset(View view, float f) {
        LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
        if (f == layoutParams.onScreen) {
            return;
        }
        layoutParams.onScreen = f;
        dispatchOnDrawerSlide(view, f);
    }

    float getDrawerViewOffset(View view) {
        return ((LayoutParams) view.getLayoutParams()).onScreen;
    }

    int getDrawerViewAbsoluteGravity(View view) {
        return GravityCompat.getAbsoluteGravity(((LayoutParams) view.getLayoutParams()).gravity, ViewCompat.getLayoutDirection(this));
    }

    boolean checkDrawerViewAbsoluteGravity(View view, int i) {
        return (getDrawerViewAbsoluteGravity(view) & i) == i;
    }

    View findOpenDrawer() {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = getChildAt(i);
            if ((((LayoutParams) childAt.getLayoutParams()).openState & 1) == 1) {
                return childAt;
            }
        }
        return null;
    }

    void moveDrawerToOffset(View view, float f) {
        float drawerViewOffset = getDrawerViewOffset(view);
        int width = view.getWidth();
        int i = ((int) (width * f)) - ((int) (drawerViewOffset * width));
        if (!checkDrawerViewAbsoluteGravity(view, 3)) {
            i = -i;
        }
        view.offsetLeftAndRight(i);
        setDrawerViewOffset(view, f);
    }

    View findDrawerWithGravity(int i) {
        int absoluteGravity = GravityCompat.getAbsoluteGravity(i, ViewCompat.getLayoutDirection(this)) & 7;
        int childCount = getChildCount();
        for (int i2 = 0; i2 < childCount; i2++) {
            View childAt = getChildAt(i2);
            if ((getDrawerViewAbsoluteGravity(childAt) & 7) == absoluteGravity) {
                return childAt;
            }
        }
        return null;
    }

    static String gravityToString(int i) {
        if ((i & 3) == 3) {
            return "LEFT";
        }
        if ((i & 5) == 5) {
            return "RIGHT";
        }
        return Integer.toHexString(i);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mFirstLayout = true;
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mFirstLayout = true;
    }

    @Override // android.view.View
    protected void onMeasure(int i, int i2) {
        int i3;
        int i4;
        boolean z;
        boolean z2;
        int mode = View.MeasureSpec.getMode(i);
        int mode2 = View.MeasureSpec.getMode(i2);
        int size = View.MeasureSpec.getSize(i);
        int size2 = View.MeasureSpec.getSize(i2);
        if (mode == 1073741824 && mode2 == 1073741824) {
            i3 = size2;
            i4 = size;
        } else {
            if (!isInEditMode()) {
                throw new IllegalArgumentException("DrawerLayout must be measured with MeasureSpec.EXACTLY.");
            }
            if (mode != Integer.MIN_VALUE && mode == 0) {
                size = 300;
            }
            if (mode2 != Integer.MIN_VALUE && mode2 == 0) {
                i3 = 300;
                i4 = size;
            } else {
                i3 = size2;
                i4 = size;
            }
        }
        setMeasuredDimension(i4, i3);
        boolean fitsSystemWindows = this.mLastInsets != null ? ViewCompat.getFitsSystemWindows(this) : false;
        int layoutDirection = ViewCompat.getLayoutDirection(this);
        boolean z3 = false;
        boolean z4 = false;
        int childCount = getChildCount();
        for (int i5 = 0; i5 < childCount; i5++) {
            View childAt = getChildAt(i5);
            if (childAt.getVisibility() != 8) {
                LayoutParams layoutParams = (LayoutParams) childAt.getLayoutParams();
                if (fitsSystemWindows) {
                    int absoluteGravity = GravityCompat.getAbsoluteGravity(layoutParams.gravity, layoutDirection);
                    if (ViewCompat.getFitsSystemWindows(childAt)) {
                        if (Build.VERSION.SDK_INT >= 21) {
                            WindowInsets windowInsetsReplaceSystemWindowInsets = (WindowInsets) this.mLastInsets;
                            if (absoluteGravity == 3) {
                                windowInsetsReplaceSystemWindowInsets = windowInsetsReplaceSystemWindowInsets.replaceSystemWindowInsets(windowInsetsReplaceSystemWindowInsets.getSystemWindowInsetLeft(), windowInsetsReplaceSystemWindowInsets.getSystemWindowInsetTop(), 0, windowInsetsReplaceSystemWindowInsets.getSystemWindowInsetBottom());
                            } else if (absoluteGravity == 5) {
                                windowInsetsReplaceSystemWindowInsets = windowInsetsReplaceSystemWindowInsets.replaceSystemWindowInsets(0, windowInsetsReplaceSystemWindowInsets.getSystemWindowInsetTop(), windowInsetsReplaceSystemWindowInsets.getSystemWindowInsetRight(), windowInsetsReplaceSystemWindowInsets.getSystemWindowInsetBottom());
                            }
                            childAt.dispatchApplyWindowInsets(windowInsetsReplaceSystemWindowInsets);
                        }
                    } else if (Build.VERSION.SDK_INT >= 21) {
                        WindowInsets windowInsetsReplaceSystemWindowInsets2 = (WindowInsets) this.mLastInsets;
                        if (absoluteGravity == 3) {
                            windowInsetsReplaceSystemWindowInsets2 = windowInsetsReplaceSystemWindowInsets2.replaceSystemWindowInsets(windowInsetsReplaceSystemWindowInsets2.getSystemWindowInsetLeft(), windowInsetsReplaceSystemWindowInsets2.getSystemWindowInsetTop(), 0, windowInsetsReplaceSystemWindowInsets2.getSystemWindowInsetBottom());
                        } else if (absoluteGravity == 5) {
                            windowInsetsReplaceSystemWindowInsets2 = windowInsetsReplaceSystemWindowInsets2.replaceSystemWindowInsets(0, windowInsetsReplaceSystemWindowInsets2.getSystemWindowInsetTop(), windowInsetsReplaceSystemWindowInsets2.getSystemWindowInsetRight(), windowInsetsReplaceSystemWindowInsets2.getSystemWindowInsetBottom());
                        }
                        layoutParams.leftMargin = windowInsetsReplaceSystemWindowInsets2.getSystemWindowInsetLeft();
                        layoutParams.topMargin = windowInsetsReplaceSystemWindowInsets2.getSystemWindowInsetTop();
                        layoutParams.rightMargin = windowInsetsReplaceSystemWindowInsets2.getSystemWindowInsetRight();
                        layoutParams.bottomMargin = windowInsetsReplaceSystemWindowInsets2.getSystemWindowInsetBottom();
                    }
                }
                if (isContentView(childAt)) {
                    childAt.measure(View.MeasureSpec.makeMeasureSpec((i4 - layoutParams.leftMargin) - layoutParams.rightMargin, 1073741824), View.MeasureSpec.makeMeasureSpec((i3 - layoutParams.topMargin) - layoutParams.bottomMargin, 1073741824));
                } else {
                    if (!isDrawerView(childAt)) {
                        throw new IllegalStateException("Child " + childAt + " at index " + i5 + " does not have a valid layout_gravity - must be Gravity.LEFT, Gravity.RIGHT or Gravity.NO_GRAVITY");
                    }
                    if (SET_DRAWER_SHADOW_FROM_ELEVATION && ViewCompat.getElevation(childAt) != this.mDrawerElevation) {
                        ViewCompat.setElevation(childAt, this.mDrawerElevation);
                    }
                    int drawerViewAbsoluteGravity = getDrawerViewAbsoluteGravity(childAt) & 7;
                    boolean z5 = drawerViewAbsoluteGravity == 3;
                    if ((z5 && z3) || (!z5 && z4)) {
                        throw new IllegalStateException("Child drawer has absolute gravity " + gravityToString(drawerViewAbsoluteGravity) + " but this DrawerLayout already has a drawer view along that edge");
                    }
                    if (z5) {
                        boolean z6 = z4;
                        z2 = true;
                        z = z6;
                    } else {
                        z = true;
                        z2 = z3;
                    }
                    childAt.measure(getChildMeasureSpec(i, this.mMinDrawerMargin + layoutParams.leftMargin + layoutParams.rightMargin, layoutParams.width), getChildMeasureSpec(i2, layoutParams.topMargin + layoutParams.bottomMargin, layoutParams.height));
                    z3 = z2;
                    z4 = z;
                }
            }
        }
    }

    private void resolveShadowDrawables() {
        if (SET_DRAWER_SHADOW_FROM_ELEVATION) {
            return;
        }
        this.mShadowLeftResolved = resolveLeftShadow();
        this.mShadowRightResolved = resolveRightShadow();
    }

    private Drawable resolveLeftShadow() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        int layoutDirection = ViewCompat.getLayoutDirection(this);
        if (layoutDirection == 0) {
            if (this.mShadowStart != null) {
                mirror(this.mShadowStart, layoutDirection);
                return this.mShadowStart;
            }
        } else if (this.mShadowEnd != null) {
            mirror(this.mShadowEnd, layoutDirection);
            return this.mShadowEnd;
        }
        return this.mShadowLeft;
    }

    private Drawable resolveRightShadow() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        int layoutDirection = ViewCompat.getLayoutDirection(this);
        if (layoutDirection == 0) {
            if (this.mShadowEnd != null) {
                mirror(this.mShadowEnd, layoutDirection);
                return this.mShadowEnd;
            }
        } else if (this.mShadowStart != null) {
            mirror(this.mShadowStart, layoutDirection);
            return this.mShadowStart;
        }
        return this.mShadowRight;
    }

    private boolean mirror(Drawable drawable, int i) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        if (drawable == null || (!DrawableCompat.isAutoMirrored(drawable))) {
            return false;
        }
        DrawableCompat.setLayoutDirection(drawable, i);
        return true;
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        int i5;
        float f;
        this.mInLayout = true;
        int i6 = i3 - i;
        int childCount = getChildCount();
        for (int i7 = 0; i7 < childCount; i7++) {
            View childAt = getChildAt(i7);
            if (childAt.getVisibility() != 8) {
                LayoutParams layoutParams = (LayoutParams) childAt.getLayoutParams();
                if (isContentView(childAt)) {
                    childAt.layout(layoutParams.leftMargin, layoutParams.topMargin, layoutParams.leftMargin + childAt.getMeasuredWidth(), layoutParams.topMargin + childAt.getMeasuredHeight());
                } else {
                    int measuredWidth = childAt.getMeasuredWidth();
                    int measuredHeight = childAt.getMeasuredHeight();
                    if (checkDrawerViewAbsoluteGravity(childAt, 3)) {
                        i5 = ((int) (measuredWidth * layoutParams.onScreen)) + (-measuredWidth);
                        f = (measuredWidth + i5) / measuredWidth;
                    } else {
                        i5 = i6 - ((int) (measuredWidth * layoutParams.onScreen));
                        f = (i6 - i5) / measuredWidth;
                    }
                    boolean z2 = f != layoutParams.onScreen;
                    switch (layoutParams.gravity & ScriptIntrinsicBLAS.TRANSPOSE) {
                        case 16:
                            int i8 = i4 - i2;
                            int i9 = (i8 - measuredHeight) / 2;
                            if (i9 < layoutParams.topMargin) {
                                i9 = layoutParams.topMargin;
                            } else if (i9 + measuredHeight > i8 - layoutParams.bottomMargin) {
                                i9 = (i8 - layoutParams.bottomMargin) - measuredHeight;
                            }
                            childAt.layout(i5, i9, measuredWidth + i5, measuredHeight + i9);
                            break;
                        case 48:
                        default:
                            childAt.layout(i5, layoutParams.topMargin, measuredWidth + i5, measuredHeight + layoutParams.topMargin);
                            break;
                        case 80:
                            int i10 = i4 - i2;
                            childAt.layout(i5, (i10 - layoutParams.bottomMargin) - childAt.getMeasuredHeight(), measuredWidth + i5, i10 - layoutParams.bottomMargin);
                            break;
                    }
                    if (z2) {
                        setDrawerViewOffset(childAt, f);
                    }
                    int i11 = layoutParams.onScreen > 0.0f ? 0 : 4;
                    if (childAt.getVisibility() != i11) {
                        childAt.setVisibility(i11);
                    }
                }
            }
        }
        this.mInLayout = false;
        this.mFirstLayout = false;
    }

    @Override // android.view.View, android.view.ViewParent
    public void requestLayout() {
        if (!this.mInLayout) {
            super.requestLayout();
        }
    }

    @Override // android.view.View
    public void computeScroll() {
        int childCount = getChildCount();
        float fMax = 0.0f;
        for (int i = 0; i < childCount; i++) {
            fMax = Math.max(fMax, ((LayoutParams) getChildAt(i).getLayoutParams()).onScreen);
        }
        this.mScrimOpacity = fMax;
        boolean zContinueSettling = this.mLeftDragger.continueSettling(true);
        boolean zContinueSettling2 = this.mRightDragger.continueSettling(true);
        if (zContinueSettling || zContinueSettling2) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    private static boolean hasOpaqueBackground(View view) {
        Drawable background = view.getBackground();
        return background != null && background.getOpacity() == -1;
    }

    @Override // android.view.View
    public void onRtlPropertiesChanged(int i) {
        resolveShadowDrawables();
    }

    @Override // android.view.View
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.mDrawStatusBarBackground && this.mStatusBarBackground != null) {
            int systemWindowInsetTop = (Build.VERSION.SDK_INT < 21 || this.mLastInsets == null) ? 0 : ((WindowInsets) this.mLastInsets).getSystemWindowInsetTop();
            if (systemWindowInsetTop > 0) {
                this.mStatusBarBackground.setBounds(0, 0, getWidth(), systemWindowInsetTop);
                this.mStatusBarBackground.draw(canvas);
            }
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:22:0x0050  */
    @Override // android.view.ViewGroup
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    protected boolean drawChild(android.graphics.Canvas r10, android.view.View r11, long r12) {
        /*
            Method dump skipped, instructions count: 304
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v4.widget.DrawerLayout.drawChild(android.graphics.Canvas, android.view.View, long):boolean");
    }

    boolean isContentView(View view) {
        return ((LayoutParams) view.getLayoutParams()).gravity == 0;
    }

    boolean isDrawerView(View view) {
        int absoluteGravity = GravityCompat.getAbsoluteGravity(((LayoutParams) view.getLayoutParams()).gravity, ViewCompat.getLayoutDirection(view));
        return ((absoluteGravity & 3) == 0 && (absoluteGravity & 5) == 0) ? false : true;
    }

    @Override // android.view.ViewGroup
    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        View viewFindTopChildUnder;
        boolean z = false;
        int actionMasked = motionEvent.getActionMasked();
        boolean zShouldInterceptTouchEvent = this.mLeftDragger.shouldInterceptTouchEvent(motionEvent) | this.mRightDragger.shouldInterceptTouchEvent(motionEvent);
        switch (actionMasked) {
            case 0:
                float x = motionEvent.getX();
                float y = motionEvent.getY();
                this.mInitialMotionX = x;
                this.mInitialMotionY = y;
                boolean z2 = this.mScrimOpacity > 0.0f && (viewFindTopChildUnder = this.mLeftDragger.findTopChildUnder((int) x, (int) y)) != null && isContentView(viewFindTopChildUnder);
                this.mDisallowInterceptRequested = false;
                this.mChildrenCanceledTouch = false;
                z = z2;
                break;
            case 1:
            case 3:
                closeDrawers(true);
                this.mDisallowInterceptRequested = false;
                this.mChildrenCanceledTouch = false;
                break;
            case 2:
                if (this.mLeftDragger.checkTouchSlop(3)) {
                    this.mLeftCallback.removeCallbacks();
                    this.mRightCallback.removeCallbacks();
                    break;
                }
                break;
        }
        if (zShouldInterceptTouchEvent || z || hasPeekingDrawer()) {
            return true;
        }
        return this.mChildrenCanceledTouch;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        boolean z;
        View viewFindOpenDrawer;
        this.mLeftDragger.processTouchEvent(motionEvent);
        this.mRightDragger.processTouchEvent(motionEvent);
        switch (motionEvent.getAction() & 255) {
            case 0:
                float x = motionEvent.getX();
                float y = motionEvent.getY();
                this.mInitialMotionX = x;
                this.mInitialMotionY = y;
                this.mDisallowInterceptRequested = false;
                this.mChildrenCanceledTouch = false;
                return true;
            case 1:
                float x2 = motionEvent.getX();
                float y2 = motionEvent.getY();
                View viewFindTopChildUnder = this.mLeftDragger.findTopChildUnder((int) x2, (int) y2);
                if (viewFindTopChildUnder != null && isContentView(viewFindTopChildUnder)) {
                    float f = x2 - this.mInitialMotionX;
                    float f2 = y2 - this.mInitialMotionY;
                    int touchSlop = this.mLeftDragger.getTouchSlop();
                    z = (f * f) + (f2 * f2) >= ((float) (touchSlop * touchSlop)) || (viewFindOpenDrawer = findOpenDrawer()) == null || getDrawerLockMode(viewFindOpenDrawer) == 2;
                    closeDrawers(z);
                    this.mDisallowInterceptRequested = false;
                } else {
                    z = true;
                    closeDrawers(z);
                    this.mDisallowInterceptRequested = false;
                }
                return true;
            case 2:
            default:
                return true;
            case 3:
                closeDrawers(true);
                this.mDisallowInterceptRequested = false;
                this.mChildrenCanceledTouch = false;
                return true;
        }
    }

    @Override // android.view.ViewGroup, android.view.ViewParent
    public void requestDisallowInterceptTouchEvent(boolean z) {
        super.requestDisallowInterceptTouchEvent(z);
        this.mDisallowInterceptRequested = z;
        if (z) {
            closeDrawers(true);
        }
    }

    public void closeDrawers() {
        closeDrawers(false);
    }

    void closeDrawers(boolean z) {
        int childCount = getChildCount();
        boolean zSmoothSlideViewTo = false;
        for (int i = 0; i < childCount; i++) {
            View childAt = getChildAt(i);
            LayoutParams layoutParams = (LayoutParams) childAt.getLayoutParams();
            if (isDrawerView(childAt) && (!z || !(!layoutParams.isPeeking))) {
                int width = childAt.getWidth();
                if (checkDrawerViewAbsoluteGravity(childAt, 3)) {
                    zSmoothSlideViewTo |= this.mLeftDragger.smoothSlideViewTo(childAt, -width, childAt.getTop());
                } else {
                    zSmoothSlideViewTo |= this.mRightDragger.smoothSlideViewTo(childAt, getWidth(), childAt.getTop());
                }
                layoutParams.isPeeking = false;
            }
        }
        this.mLeftCallback.removeCallbacks();
        this.mRightCallback.removeCallbacks();
        if (zSmoothSlideViewTo) {
            invalidate();
        }
    }

    public void openDrawer(View view) {
        openDrawer(view, true);
    }

    public void openDrawer(View view, boolean z) {
        if (!isDrawerView(view)) {
            throw new IllegalArgumentException("View " + view + " is not a sliding drawer");
        }
        LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
        if (this.mFirstLayout) {
            layoutParams.onScreen = 1.0f;
            layoutParams.openState = 1;
            updateChildrenImportantForAccessibility(view, true);
        } else if (z) {
            layoutParams.openState |= 2;
            if (checkDrawerViewAbsoluteGravity(view, 3)) {
                this.mLeftDragger.smoothSlideViewTo(view, 0, view.getTop());
            } else {
                this.mRightDragger.smoothSlideViewTo(view, getWidth() - view.getWidth(), view.getTop());
            }
        } else {
            moveDrawerToOffset(view, 1.0f);
            updateDrawerState(layoutParams.gravity, 0, view);
            view.setVisibility(0);
        }
        invalidate();
    }

    public void openDrawer(int i) {
        openDrawer(i, true);
    }

    public void openDrawer(int i, boolean z) {
        View viewFindDrawerWithGravity = findDrawerWithGravity(i);
        if (viewFindDrawerWithGravity == null) {
            throw new IllegalArgumentException("No drawer view found with gravity " + gravityToString(i));
        }
        openDrawer(viewFindDrawerWithGravity, z);
    }

    public void closeDrawer(View view) {
        closeDrawer(view, true);
    }

    public void closeDrawer(View view, boolean z) {
        if (!isDrawerView(view)) {
            throw new IllegalArgumentException("View " + view + " is not a sliding drawer");
        }
        LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
        if (this.mFirstLayout) {
            layoutParams.onScreen = 0.0f;
            layoutParams.openState = 0;
        } else if (z) {
            layoutParams.openState |= 4;
            if (checkDrawerViewAbsoluteGravity(view, 3)) {
                this.mLeftDragger.smoothSlideViewTo(view, -view.getWidth(), view.getTop());
            } else {
                this.mRightDragger.smoothSlideViewTo(view, getWidth(), view.getTop());
            }
        } else {
            moveDrawerToOffset(view, 0.0f);
            updateDrawerState(layoutParams.gravity, 0, view);
            view.setVisibility(4);
        }
        invalidate();
    }

    public void closeDrawer(int i) {
        closeDrawer(i, true);
    }

    public void closeDrawer(int i, boolean z) {
        View viewFindDrawerWithGravity = findDrawerWithGravity(i);
        if (viewFindDrawerWithGravity == null) {
            throw new IllegalArgumentException("No drawer view found with gravity " + gravityToString(i));
        }
        closeDrawer(viewFindDrawerWithGravity, z);
    }

    public boolean isDrawerOpen(View view) {
        if (isDrawerView(view)) {
            return (((LayoutParams) view.getLayoutParams()).openState & 1) == 1;
        }
        throw new IllegalArgumentException("View " + view + " is not a drawer");
    }

    public boolean isDrawerOpen(int i) {
        View viewFindDrawerWithGravity = findDrawerWithGravity(i);
        if (viewFindDrawerWithGravity != null) {
            return isDrawerOpen(viewFindDrawerWithGravity);
        }
        return false;
    }

    public boolean isDrawerVisible(View view) {
        if (isDrawerView(view)) {
            return ((LayoutParams) view.getLayoutParams()).onScreen > 0.0f;
        }
        throw new IllegalArgumentException("View " + view + " is not a drawer");
    }

    public boolean isDrawerVisible(int i) {
        View viewFindDrawerWithGravity = findDrawerWithGravity(i);
        if (viewFindDrawerWithGravity != null) {
            return isDrawerVisible(viewFindDrawerWithGravity);
        }
        return false;
    }

    private boolean hasPeekingDrawer() {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            if (((LayoutParams) getChildAt(i).getLayoutParams()).isPeeking) {
                return true;
            }
        }
        return false;
    }

    @Override // android.view.ViewGroup
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(-1, -1);
    }

    @Override // android.view.ViewGroup
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams layoutParams) {
        if (layoutParams instanceof LayoutParams) {
            return new LayoutParams((LayoutParams) layoutParams);
        }
        if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
            return new LayoutParams((ViewGroup.MarginLayoutParams) layoutParams);
        }
        return new LayoutParams(layoutParams);
    }

    @Override // android.view.ViewGroup
    protected boolean checkLayoutParams(ViewGroup.LayoutParams layoutParams) {
        if (layoutParams instanceof LayoutParams) {
            return super.checkLayoutParams(layoutParams);
        }
        return false;
    }

    @Override // android.view.ViewGroup
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attributeSet) {
        return new LayoutParams(getContext(), attributeSet);
    }

    @Override // android.view.ViewGroup, android.view.View
    public void addFocusables(ArrayList<View> arrayList, int i, int i2) {
        if (getDescendantFocusability() == 393216) {
            return;
        }
        int childCount = getChildCount();
        boolean z = false;
        for (int i3 = 0; i3 < childCount; i3++) {
            View childAt = getChildAt(i3);
            if (isDrawerView(childAt)) {
                if (isDrawerOpen(childAt)) {
                    z = true;
                    childAt.addFocusables(arrayList, i, i2);
                }
            } else {
                this.mNonDrawerViews.add(childAt);
            }
        }
        if (!z) {
            int size = this.mNonDrawerViews.size();
            for (int i4 = 0; i4 < size; i4++) {
                View view = this.mNonDrawerViews.get(i4);
                if (view.getVisibility() == 0) {
                    view.addFocusables(arrayList, i, i2);
                }
            }
        }
        this.mNonDrawerViews.clear();
    }

    private boolean hasVisibleDrawer() {
        return findVisibleDrawer() != null;
    }

    View findVisibleDrawer() {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = getChildAt(i);
            if (isDrawerView(childAt) && isDrawerVisible(childAt)) {
                return childAt;
            }
        }
        return null;
    }

    @Override // android.view.View, android.view.KeyEvent.Callback
    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        if (i == 4 && hasVisibleDrawer()) {
            keyEvent.startTracking();
            return true;
        }
        return super.onKeyDown(i, keyEvent);
    }

    @Override // android.view.View, android.view.KeyEvent.Callback
    public boolean onKeyUp(int i, KeyEvent keyEvent) {
        if (i == 4) {
            View viewFindVisibleDrawer = findVisibleDrawer();
            if (viewFindVisibleDrawer != null && getDrawerLockMode(viewFindVisibleDrawer) == 0) {
                closeDrawers();
            }
            return viewFindVisibleDrawer != null;
        }
        return super.onKeyUp(i, keyEvent);
    }

    @Override // android.view.View
    protected void onRestoreInstanceState(Parcelable parcelable) {
        View viewFindDrawerWithGravity;
        if (!(parcelable instanceof SavedState)) {
            super.onRestoreInstanceState(parcelable);
            return;
        }
        SavedState savedState = (SavedState) parcelable;
        super.onRestoreInstanceState(savedState.getSuperState());
        if (savedState.openDrawerGravity != 0 && (viewFindDrawerWithGravity = findDrawerWithGravity(savedState.openDrawerGravity)) != null) {
            openDrawer(viewFindDrawerWithGravity);
        }
        if (savedState.lockModeLeft != 3) {
            setDrawerLockMode(savedState.lockModeLeft, 3);
        }
        if (savedState.lockModeRight != 3) {
            setDrawerLockMode(savedState.lockModeRight, 5);
        }
        if (savedState.lockModeStart != 3) {
            setDrawerLockMode(savedState.lockModeStart, 8388611);
        }
        if (savedState.lockModeEnd != 3) {
            setDrawerLockMode(savedState.lockModeEnd, 8388613);
        }
    }

    @Override // android.view.View
    protected Parcelable onSaveInstanceState() {
        SavedState savedState = new SavedState(super.onSaveInstanceState());
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            LayoutParams layoutParams = (LayoutParams) getChildAt(i).getLayoutParams();
            boolean z = layoutParams.openState == 1;
            boolean z2 = layoutParams.openState == 2;
            if (z || z2) {
                savedState.openDrawerGravity = layoutParams.gravity;
                break;
            }
        }
        savedState.lockModeLeft = this.mLockModeLeft;
        savedState.lockModeRight = this.mLockModeRight;
        savedState.lockModeStart = this.mLockModeStart;
        savedState.lockModeEnd = this.mLockModeEnd;
        return savedState;
    }

    @Override // android.view.ViewGroup
    public void addView(View view, int i, ViewGroup.LayoutParams layoutParams) {
        super.addView(view, i, layoutParams);
        if (findOpenDrawer() != null || isDrawerView(view)) {
            ViewCompat.setImportantForAccessibility(view, 4);
        } else {
            ViewCompat.setImportantForAccessibility(view, 1);
        }
        if (!CAN_HIDE_DESCENDANTS) {
            ViewCompat.setAccessibilityDelegate(view, this.mChildAccessibilityDelegate);
        }
    }

    static boolean includeChildForAccessibility(View view) {
        return (ViewCompat.getImportantForAccessibility(view) == 4 || ViewCompat.getImportantForAccessibility(view) == 2) ? false : true;
    }

    protected static class SavedState extends AbsSavedState {
        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.ClassLoaderCreator<SavedState>() { // from class: android.support.v4.widget.DrawerLayout.SavedState.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.ClassLoaderCreator
            public SavedState createFromParcel(Parcel parcel, ClassLoader classLoader) {
                return new SavedState(parcel, classLoader);
            }

            @Override // android.os.Parcelable.Creator
            public SavedState createFromParcel(Parcel parcel) {
                return new SavedState(parcel, null);
            }

            @Override // android.os.Parcelable.Creator
            public SavedState[] newArray(int i) {
                return new SavedState[i];
            }
        };
        int lockModeEnd;
        int lockModeLeft;
        int lockModeRight;
        int lockModeStart;
        int openDrawerGravity;

        public SavedState(Parcel parcel, ClassLoader classLoader) {
            super(parcel, classLoader);
            this.openDrawerGravity = 0;
            this.openDrawerGravity = parcel.readInt();
            this.lockModeLeft = parcel.readInt();
            this.lockModeRight = parcel.readInt();
            this.lockModeStart = parcel.readInt();
            this.lockModeEnd = parcel.readInt();
        }

        public SavedState(Parcelable parcelable) {
            super(parcelable);
            this.openDrawerGravity = 0;
        }

        @Override // android.support.v4.view.AbsSavedState, android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            super.writeToParcel(parcel, i);
            parcel.writeInt(this.openDrawerGravity);
            parcel.writeInt(this.lockModeLeft);
            parcel.writeInt(this.lockModeRight);
            parcel.writeInt(this.lockModeStart);
            parcel.writeInt(this.lockModeEnd);
        }
    }

    private class ViewDragCallback extends ViewDragHelper.Callback {
        private final int mAbsGravity;
        private ViewDragHelper mDragger;
        private final Runnable mPeekRunnable;
        final /* synthetic */ DrawerLayout this$0;

        public void removeCallbacks() {
            this.this$0.removeCallbacks(this.mPeekRunnable);
        }

        @Override // android.support.v4.widget.ViewDragHelper.Callback
        public boolean tryCaptureView(View view, int i) {
            return this.this$0.isDrawerView(view) && this.this$0.checkDrawerViewAbsoluteGravity(view, this.mAbsGravity) && this.this$0.getDrawerLockMode(view) == 0;
        }

        @Override // android.support.v4.widget.ViewDragHelper.Callback
        public void onViewDragStateChanged(int i) {
            this.this$0.updateDrawerState(this.mAbsGravity, i, this.mDragger.getCapturedView());
        }

        @Override // android.support.v4.widget.ViewDragHelper.Callback
        public void onViewPositionChanged(View view, int i, int i2, int i3, int i4) {
            float width;
            int width2 = view.getWidth();
            if (this.this$0.checkDrawerViewAbsoluteGravity(view, 3)) {
                width = (width2 + i) / width2;
            } else {
                width = (this.this$0.getWidth() - i) / width2;
            }
            this.this$0.setDrawerViewOffset(view, width);
            view.setVisibility(width == 0.0f ? 4 : 0);
            this.this$0.invalidate();
        }

        @Override // android.support.v4.widget.ViewDragHelper.Callback
        public void onViewCaptured(View view, int i) {
            ((LayoutParams) view.getLayoutParams()).isPeeking = false;
            closeOtherDrawer();
        }

        private void closeOtherDrawer() {
            View viewFindDrawerWithGravity = this.this$0.findDrawerWithGravity(this.mAbsGravity == 3 ? 5 : 3);
            if (viewFindDrawerWithGravity != null) {
                this.this$0.closeDrawer(viewFindDrawerWithGravity);
            }
        }

        @Override // android.support.v4.widget.ViewDragHelper.Callback
        public void onViewReleased(View view, float f, float f2) {
            int width;
            float drawerViewOffset = this.this$0.getDrawerViewOffset(view);
            int width2 = view.getWidth();
            if (this.this$0.checkDrawerViewAbsoluteGravity(view, 3)) {
                width = (f > 0.0f || (f == 0.0f && drawerViewOffset > 0.5f)) ? 0 : -width2;
            } else {
                width = this.this$0.getWidth();
                if (f < 0.0f || (f == 0.0f && drawerViewOffset > 0.5f)) {
                    width -= width2;
                }
            }
            this.mDragger.settleCapturedViewAt(width, view.getTop());
            this.this$0.invalidate();
        }

        @Override // android.support.v4.widget.ViewDragHelper.Callback
        public void onEdgeTouched(int i, int i2) {
            this.this$0.postDelayed(this.mPeekRunnable, 160L);
        }

        @Override // android.support.v4.widget.ViewDragHelper.Callback
        public boolean onEdgeLock(int i) {
            return false;
        }

        @Override // android.support.v4.widget.ViewDragHelper.Callback
        public void onEdgeDragStarted(int i, int i2) {
            View viewFindDrawerWithGravity;
            if ((i & 1) == 1) {
                viewFindDrawerWithGravity = this.this$0.findDrawerWithGravity(3);
            } else {
                viewFindDrawerWithGravity = this.this$0.findDrawerWithGravity(5);
            }
            if (viewFindDrawerWithGravity != null && this.this$0.getDrawerLockMode(viewFindDrawerWithGravity) == 0) {
                this.mDragger.captureChildView(viewFindDrawerWithGravity, i2);
            }
        }

        @Override // android.support.v4.widget.ViewDragHelper.Callback
        public int getViewHorizontalDragRange(View view) {
            if (this.this$0.isDrawerView(view)) {
                return view.getWidth();
            }
            return 0;
        }

        @Override // android.support.v4.widget.ViewDragHelper.Callback
        public int clampViewPositionHorizontal(View view, int i, int i2) {
            if (this.this$0.checkDrawerViewAbsoluteGravity(view, 3)) {
                return Math.max(-view.getWidth(), Math.min(i, 0));
            }
            int width = this.this$0.getWidth();
            return Math.max(width - view.getWidth(), Math.min(i, width));
        }

        @Override // android.support.v4.widget.ViewDragHelper.Callback
        public int clampViewPositionVertical(View view, int i, int i2) {
            return view.getTop();
        }
    }

    public static class LayoutParams extends ViewGroup.MarginLayoutParams {
        public int gravity;
        boolean isPeeking;
        float onScreen;
        int openState;

        public LayoutParams(Context context, AttributeSet attributeSet) {
            super(context, attributeSet);
            this.gravity = 0;
            TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, DrawerLayout.LAYOUT_ATTRS);
            this.gravity = typedArrayObtainStyledAttributes.getInt(0, 0);
            typedArrayObtainStyledAttributes.recycle();
        }

        public LayoutParams(int i, int i2) {
            super(i, i2);
            this.gravity = 0;
        }

        public LayoutParams(LayoutParams layoutParams) {
            super((ViewGroup.MarginLayoutParams) layoutParams);
            this.gravity = 0;
            this.gravity = layoutParams.gravity;
        }

        public LayoutParams(ViewGroup.LayoutParams layoutParams) {
            super(layoutParams);
            this.gravity = 0;
        }

        public LayoutParams(ViewGroup.MarginLayoutParams marginLayoutParams) {
            super(marginLayoutParams);
            this.gravity = 0;
        }
    }

    static final class ChildAccessibilityDelegate extends AccessibilityDelegateCompat {
        ChildAccessibilityDelegate() {
        }

        @Override // android.support.v4.view.AccessibilityDelegateCompat
        public void onInitializeAccessibilityNodeInfo(View view, AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
            super.onInitializeAccessibilityNodeInfo(view, accessibilityNodeInfoCompat);
            if (!DrawerLayout.includeChildForAccessibility(view)) {
                accessibilityNodeInfoCompat.setParent(null);
            }
        }
    }
}
