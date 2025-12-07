package android.support.v4.view;

import android.animation.ValueAnimator;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.view.WindowInsets;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.WeakHashMap;

/* loaded from: classes.dex */
public class ViewCompat {
    static final ViewCompatBaseImpl IMPL;

    @Retention(RetentionPolicy.SOURCE)
    private @interface ImportantForAccessibility {
    }

    @Retention(RetentionPolicy.SOURCE)
    private @interface ResolvedLayoutDirectionMode {
    }

    static class ViewCompatBaseImpl {
        static boolean sAccessibilityDelegateCheckFailed = false;
        private static WeakHashMap<View, String> sTransitionNameMap;
        WeakHashMap<View, ViewPropertyAnimatorCompat> mViewPropertyAnimatorCompatMap = null;

        ViewCompatBaseImpl() {
        }

        public void setAccessibilityDelegate(View view, @Nullable AccessibilityDelegateCompat accessibilityDelegateCompat) {
            view.setAccessibilityDelegate(accessibilityDelegateCompat != null ? accessibilityDelegateCompat.getBridge() : null);
        }

        public void postInvalidateOnAnimation(View view) {
            view.postInvalidate();
        }

        public void postOnAnimation(View view, Runnable runnable) {
            view.postDelayed(runnable, getFrameTime());
        }

        long getFrameTime() {
            return ValueAnimator.getFrameDelay();
        }

        public int getImportantForAccessibility(View view) {
            return 0;
        }

        public void setImportantForAccessibility(View view, int i) {
        }

        public int getLayoutDirection(View view) {
            return 0;
        }

        public boolean hasOverlappingRendering(View view) {
            return true;
        }

        public void setTransitionName(View view, String str) {
            if (sTransitionNameMap == null) {
                sTransitionNameMap = new WeakHashMap<>();
            }
            sTransitionNameMap.put(view, str);
        }

        public String getTransitionName(View view) {
            if (sTransitionNameMap == null) {
                return null;
            }
            return sTransitionNameMap.get(view);
        }

        public void setElevation(View view, float f) {
        }

        public float getElevation(View view) {
            return 0.0f;
        }

        public boolean getFitsSystemWindows(View view) {
            return false;
        }

        public void setOnApplyWindowInsetsListener(View view, OnApplyWindowInsetsListener onApplyWindowInsetsListener) {
        }

        public WindowInsetsCompat onApplyWindowInsets(View view, WindowInsetsCompat windowInsetsCompat) {
            return windowInsetsCompat;
        }

        public WindowInsetsCompat dispatchApplyWindowInsets(View view, WindowInsetsCompat windowInsetsCompat) {
            return windowInsetsCompat;
        }

        public boolean isAttachedToWindow(View view) {
            return view.getWindowToken() != null;
        }

        public void offsetLeftAndRight(View view, int i) {
            view.offsetLeftAndRight(i);
            if (view.getVisibility() == 0) {
                tickleInvalidationFlag(view);
                Object parent = view.getParent();
                if (parent instanceof View) {
                    tickleInvalidationFlag((View) parent);
                }
            }
        }

        public void offsetTopAndBottom(View view, int i) {
            view.offsetTopAndBottom(i);
            if (view.getVisibility() == 0) {
                tickleInvalidationFlag(view);
                Object parent = view.getParent();
                if (parent instanceof View) {
                    tickleInvalidationFlag((View) parent);
                }
            }
        }

        private static void tickleInvalidationFlag(View view) {
            float translationY = view.getTranslationY();
            view.setTranslationY(1.0f + translationY);
            view.setTranslationY(translationY);
        }
    }

    @RequiresApi(15)
    static class ViewCompatApi15Impl extends ViewCompatBaseImpl {
        ViewCompatApi15Impl() {
        }
    }

    @RequiresApi(16)
    static class ViewCompatApi16Impl extends ViewCompatApi15Impl {
        ViewCompatApi16Impl() {
        }

        @Override // android.support.v4.view.ViewCompat.ViewCompatBaseImpl
        public void postInvalidateOnAnimation(View view) {
            view.postInvalidateOnAnimation();
        }

        @Override // android.support.v4.view.ViewCompat.ViewCompatBaseImpl
        public void postOnAnimation(View view, Runnable runnable) {
            view.postOnAnimation(runnable);
        }

        @Override // android.support.v4.view.ViewCompat.ViewCompatBaseImpl
        public int getImportantForAccessibility(View view) {
            return view.getImportantForAccessibility();
        }

        @Override // android.support.v4.view.ViewCompat.ViewCompatBaseImpl
        public void setImportantForAccessibility(View view, int i) {
            if (i == 4) {
                i = 2;
            }
            view.setImportantForAccessibility(i);
        }

        @Override // android.support.v4.view.ViewCompat.ViewCompatBaseImpl
        public boolean getFitsSystemWindows(View view) {
            return view.getFitsSystemWindows();
        }

        @Override // android.support.v4.view.ViewCompat.ViewCompatBaseImpl
        public boolean hasOverlappingRendering(View view) {
            return view.hasOverlappingRendering();
        }
    }

    @RequiresApi(17)
    static class ViewCompatApi17Impl extends ViewCompatApi16Impl {
        ViewCompatApi17Impl() {
        }

        @Override // android.support.v4.view.ViewCompat.ViewCompatBaseImpl
        public int getLayoutDirection(View view) {
            return view.getLayoutDirection();
        }
    }

    @RequiresApi(18)
    static class ViewCompatApi18Impl extends ViewCompatApi17Impl {
        ViewCompatApi18Impl() {
        }
    }

    @RequiresApi(19)
    static class ViewCompatApi19Impl extends ViewCompatApi18Impl {
        ViewCompatApi19Impl() {
        }

        @Override // android.support.v4.view.ViewCompat.ViewCompatApi16Impl, android.support.v4.view.ViewCompat.ViewCompatBaseImpl
        public void setImportantForAccessibility(View view, int i) {
            view.setImportantForAccessibility(i);
        }

        @Override // android.support.v4.view.ViewCompat.ViewCompatBaseImpl
        public boolean isAttachedToWindow(View view) {
            return view.isAttachedToWindow();
        }
    }

    @RequiresApi(21)
    static class ViewCompatApi21Impl extends ViewCompatApi19Impl {
        private static ThreadLocal<Rect> sThreadLocalRect;

        ViewCompatApi21Impl() {
        }

        @Override // android.support.v4.view.ViewCompat.ViewCompatBaseImpl
        public void setTransitionName(View view, String str) {
            view.setTransitionName(str);
        }

        @Override // android.support.v4.view.ViewCompat.ViewCompatBaseImpl
        public String getTransitionName(View view) {
            return view.getTransitionName();
        }

        @Override // android.support.v4.view.ViewCompat.ViewCompatBaseImpl
        public void setElevation(View view, float f) {
            view.setElevation(f);
        }

        @Override // android.support.v4.view.ViewCompat.ViewCompatBaseImpl
        public float getElevation(View view) {
            return view.getElevation();
        }

        @Override // android.support.v4.view.ViewCompat.ViewCompatBaseImpl
        public void setOnApplyWindowInsetsListener(View view, final OnApplyWindowInsetsListener onApplyWindowInsetsListener) {
            if (onApplyWindowInsetsListener == null) {
                view.setOnApplyWindowInsetsListener(null);
            } else {
                view.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() { // from class: android.support.v4.view.ViewCompat.ViewCompatApi21Impl.1
                    @Override // android.view.View.OnApplyWindowInsetsListener
                    public WindowInsets onApplyWindowInsets(View view2, WindowInsets windowInsets) {
                        return (WindowInsets) WindowInsetsCompat.unwrap(onApplyWindowInsetsListener.onApplyWindowInsets(view2, WindowInsetsCompat.wrap(windowInsets)));
                    }
                });
            }
        }

        @Override // android.support.v4.view.ViewCompat.ViewCompatBaseImpl
        public WindowInsetsCompat onApplyWindowInsets(View view, WindowInsetsCompat windowInsetsCompat) {
            WindowInsets windowInsets = (WindowInsets) WindowInsetsCompat.unwrap(windowInsetsCompat);
            WindowInsets windowInsetsOnApplyWindowInsets = view.onApplyWindowInsets(windowInsets);
            if (windowInsetsOnApplyWindowInsets != windowInsets) {
                windowInsets = new WindowInsets(windowInsetsOnApplyWindowInsets);
            }
            return WindowInsetsCompat.wrap(windowInsets);
        }

        @Override // android.support.v4.view.ViewCompat.ViewCompatBaseImpl
        public WindowInsetsCompat dispatchApplyWindowInsets(View view, WindowInsetsCompat windowInsetsCompat) {
            WindowInsets windowInsets = (WindowInsets) WindowInsetsCompat.unwrap(windowInsetsCompat);
            WindowInsets windowInsetsDispatchApplyWindowInsets = view.dispatchApplyWindowInsets(windowInsets);
            if (windowInsetsDispatchApplyWindowInsets != windowInsets) {
                windowInsets = new WindowInsets(windowInsetsDispatchApplyWindowInsets);
            }
            return WindowInsetsCompat.wrap(windowInsets);
        }

        @Override // android.support.v4.view.ViewCompat.ViewCompatBaseImpl
        public void offsetLeftAndRight(View view, int i) {
            Rect emptyTempRect = getEmptyTempRect();
            boolean z = false;
            Object parent = view.getParent();
            if (parent instanceof View) {
                View view2 = (View) parent;
                emptyTempRect.set(view2.getLeft(), view2.getTop(), view2.getRight(), view2.getBottom());
                z = !emptyTempRect.intersects(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
            }
            super.offsetLeftAndRight(view, i);
            if (z && emptyTempRect.intersect(view.getLeft(), view.getTop(), view.getRight(), view.getBottom())) {
                ((View) parent).invalidate(emptyTempRect);
            }
        }

        @Override // android.support.v4.view.ViewCompat.ViewCompatBaseImpl
        public void offsetTopAndBottom(View view, int i) {
            Rect emptyTempRect = getEmptyTempRect();
            boolean z = false;
            Object parent = view.getParent();
            if (parent instanceof View) {
                View view2 = (View) parent;
                emptyTempRect.set(view2.getLeft(), view2.getTop(), view2.getRight(), view2.getBottom());
                z = !emptyTempRect.intersects(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
            }
            super.offsetTopAndBottom(view, i);
            if (z && emptyTempRect.intersect(view.getLeft(), view.getTop(), view.getRight(), view.getBottom())) {
                ((View) parent).invalidate(emptyTempRect);
            }
        }

        private static Rect getEmptyTempRect() {
            if (sThreadLocalRect == null) {
                sThreadLocalRect = new ThreadLocal<>();
            }
            Rect rect = sThreadLocalRect.get();
            if (rect == null) {
                rect = new Rect();
                sThreadLocalRect.set(rect);
            }
            rect.setEmpty();
            return rect;
        }
    }

    @RequiresApi(23)
    static class ViewCompatApi23Impl extends ViewCompatApi21Impl {
        ViewCompatApi23Impl() {
        }

        @Override // android.support.v4.view.ViewCompat.ViewCompatApi21Impl, android.support.v4.view.ViewCompat.ViewCompatBaseImpl
        public void offsetLeftAndRight(View view, int i) {
            view.offsetLeftAndRight(i);
        }

        @Override // android.support.v4.view.ViewCompat.ViewCompatApi21Impl, android.support.v4.view.ViewCompat.ViewCompatBaseImpl
        public void offsetTopAndBottom(View view, int i) {
            view.offsetTopAndBottom(i);
        }
    }

    @RequiresApi(24)
    static class ViewCompatApi24Impl extends ViewCompatApi23Impl {
        ViewCompatApi24Impl() {
        }
    }

    @RequiresApi(26)
    static class ViewCompatApi26Impl extends ViewCompatApi24Impl {
        ViewCompatApi26Impl() {
        }
    }

    static {
        if (Build.VERSION.SDK_INT >= 26) {
            IMPL = new ViewCompatApi26Impl();
            return;
        }
        if (Build.VERSION.SDK_INT >= 24) {
            IMPL = new ViewCompatApi24Impl();
            return;
        }
        if (Build.VERSION.SDK_INT >= 23) {
            IMPL = new ViewCompatApi23Impl();
            return;
        }
        if (Build.VERSION.SDK_INT >= 21) {
            IMPL = new ViewCompatApi21Impl();
            return;
        }
        if (Build.VERSION.SDK_INT >= 19) {
            IMPL = new ViewCompatApi19Impl();
            return;
        }
        if (Build.VERSION.SDK_INT >= 18) {
            IMPL = new ViewCompatApi18Impl();
            return;
        }
        if (Build.VERSION.SDK_INT >= 17) {
            IMPL = new ViewCompatApi17Impl();
            return;
        }
        if (Build.VERSION.SDK_INT >= 16) {
            IMPL = new ViewCompatApi16Impl();
        } else if (Build.VERSION.SDK_INT >= 15) {
            IMPL = new ViewCompatApi15Impl();
        } else {
            IMPL = new ViewCompatBaseImpl();
        }
    }

    public static void setAccessibilityDelegate(View view, AccessibilityDelegateCompat accessibilityDelegateCompat) {
        IMPL.setAccessibilityDelegate(view, accessibilityDelegateCompat);
    }

    public static void postInvalidateOnAnimation(View view) {
        IMPL.postInvalidateOnAnimation(view);
    }

    public static void postOnAnimation(View view, Runnable runnable) {
        IMPL.postOnAnimation(view, runnable);
    }

    public static int getImportantForAccessibility(View view) {
        return IMPL.getImportantForAccessibility(view);
    }

    public static void setImportantForAccessibility(View view, int i) {
        IMPL.setImportantForAccessibility(view, i);
    }

    public static int getLayoutDirection(View view) {
        return IMPL.getLayoutDirection(view);
    }

    public static void setElevation(View view, float f) {
        IMPL.setElevation(view, f);
    }

    public static float getElevation(View view) {
        return IMPL.getElevation(view);
    }

    public static void setTransitionName(View view, String str) {
        IMPL.setTransitionName(view, str);
    }

    public static String getTransitionName(View view) {
        return IMPL.getTransitionName(view);
    }

    public static boolean getFitsSystemWindows(View view) {
        return IMPL.getFitsSystemWindows(view);
    }

    public static void setOnApplyWindowInsetsListener(View view, OnApplyWindowInsetsListener onApplyWindowInsetsListener) {
        IMPL.setOnApplyWindowInsetsListener(view, onApplyWindowInsetsListener);
    }

    public static WindowInsetsCompat onApplyWindowInsets(View view, WindowInsetsCompat windowInsetsCompat) {
        return IMPL.onApplyWindowInsets(view, windowInsetsCompat);
    }

    public static WindowInsetsCompat dispatchApplyWindowInsets(View view, WindowInsetsCompat windowInsetsCompat) {
        return IMPL.dispatchApplyWindowInsets(view, windowInsetsCompat);
    }

    public static boolean hasOverlappingRendering(View view) {
        return IMPL.hasOverlappingRendering(view);
    }

    public static void offsetTopAndBottom(View view, int i) {
        IMPL.offsetTopAndBottom(view, i);
    }

    public static void offsetLeftAndRight(View view, int i) {
        IMPL.offsetLeftAndRight(view, i);
    }

    public static boolean isAttachedToWindow(View view) {
        return IMPL.isAttachedToWindow(view);
    }

    protected ViewCompat() {
    }
}
