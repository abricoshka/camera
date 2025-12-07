package com.android.camera.p001ui;

import android.content.Context;
import android.hardware.Camera;
import android.os.Handler;
import android.support.v4.app.FrameMetricsAggregator;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.android.camera.CameraActivity;
import com.android.camera.p001ui.$Lambda$2F77a4O5cC_FZNKqb6EdMwzAco;
import com.android.camera.p001ui.ZZZFrameLayout;
import com.mediatek.camera.ISettingCtrl;
import com.mediatek.camera.R;

/* loaded from: classes.dex */
public class ZZZFrameLayout extends FrameLayout implements View.OnClickListener {
    private int centerX;
    private LinearLayout container;
    private Handler handler;
    private boolean isScrolling;
    private int[] itemPositions;
    private ImageView ivChageCamera;
    private ImageView ivEnteryPhoto;
    private int lastScrollX;
    private CameraActivity mCameraActivity;
    private Context mContext;
    private ISettingCtrl mCtrl;
    private OnWheelChangedListener mOnWheelChangedListener;
    private View mViewMid;
    private float originalMidWeight;
    private int screenWidth;
    private HorizontalScrollView scrollView;
    private Runnable snapRunnable;
    private TextView[] textViews;

    public interface OnWheelChangedListener {
    }

    public ZZZFrameLayout(Context context) {
        super(context);
        this.isScrolling = false;
        this.handler = new Handler();
        this.lastScrollX = 0;
        this.mContext = context;
    }

    public ZZZFrameLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.isScrolling = false;
        this.handler = new Handler();
        this.lastScrollX = 0;
        this.mContext = context;
    }

    public ZZZFrameLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.isScrolling = false;
        this.handler = new Handler();
        this.lastScrollX = 0;
        this.mContext = context;
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mCameraActivity = (CameraActivity) this.mContext;
        this.scrollView = (HorizontalScrollView) findViewById(R.id.horizontalScrollView);
        this.container = (LinearLayout) findViewById(R.id.container);
        this.screenWidth = getResources().getDisplayMetrics().widthPixels;
        View viewFindViewById = findViewById(R.id.v_id1);
        View viewFindViewById2 = findViewById(R.id.v_id2);
        this.mViewMid = findViewById(R.id.v_mid);
        this.ivChageCamera = (ImageView) findViewById(R.id.iv_change_camera);
        this.ivEnteryPhoto = (ImageView) findViewById(R.id.iv_entry_photo);
        this.ivChageCamera.setOnClickListener(this);
        viewFindViewById.setOnClickListener(this);
        viewFindViewById2.setOnClickListener(this);
        if (this.mViewMid.getLayoutParams() instanceof LinearLayout.LayoutParams) {
            this.originalMidWeight = ((LinearLayout.LayoutParams) this.mViewMid.getLayoutParams()).weight;
        }
        this.textViews = new TextView[8];
        this.itemPositions = new int[8];
        for (int i = 0; i < 8; i++) {
            TextView textView = new TextView(this.mContext);
            String str = "";
            switch (i) {
                case 0:
                    str = "TIME-LAPSE";
                    break;
                case 1:
                    str = "SLO-MO";
                    break;
                case 2:
                    str = "CINEMATIC";
                    break;
                case 3:
                    str = "VIDEO";
                    break;
                case 4:
                    str = "PHOTO";
                    break;
                case 5:
                    str = "PORTRAIT";
                    break;
                case 6:
                    str = "SPATIAL";
                    break;
                case FrameMetricsAggregator.DELAY_INDEX /* 7 */:
                    str = "PANO";
                    break;
            }
            textView.setId(2130706432 + i);
            textView.setText(str);
            textView.setOnClickListener(this);
            textView.setTextSize(12.0f);
            textView.setTextColor(-1);
            textView.setGravity(17);
            textView.setPadding(30, 20, 30, 20);
            textView.setTag(Integer.valueOf(i));
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams((int) (textView.getPaint().measureText(str) + textView.getPaddingLeft() + textView.getPaddingRight()), -2);
            layoutParams.setMargins(5, 5, 5, 5);
            textView.setLayoutParams(layoutParams);
            this.container.addView(textView);
            this.textViews[i] = textView;
        }
        this.scrollView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserverOnGlobalLayoutListenerC01891());
        this.scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() { // from class: com.android.camera.ui.-$Lambda$2F77a4O5cC_FZNKqb6EdM-wzAco.1
            private final /* synthetic */ void $m$0() {
                ((ZZZFrameLayout) this).m408lambda$com_android_camera_ui_ZZZFrameLayout_6202();
            }

            @Override // android.view.ViewTreeObserver.OnScrollChangedListener
            public final void onScrollChanged() {
                $m$0();
            }
        });
        this.scrollView.setOnTouchListener(new View.OnTouchListener() { // from class: com.android.camera.ui.-$Lambda$2F77a4O5cC_FZNKqb6EdM-wzAco

            /* renamed from: com.android.camera.ui.-$Lambda$2F77a4O5cC_FZNKqb6EdM-wzAco$2 */
            final /* synthetic */ class RunnableC01692 implements Runnable {
                private final /* synthetic */ byte $id;

                /* renamed from: -$f0, reason: not valid java name */
                private final /* synthetic */ Object f89$f0;

                private final /* synthetic */ void $m$0() {
                    ((ZZZFrameLayout.ViewTreeObserverOnGlobalLayoutListenerC01891) this.f89$f0).m414lambda$com_android_camera_ui_ZZZFrameLayout$1_5294();
                }

                private final /* synthetic */ void $m$1() {
                    ((ZZZFrameLayout) this.f89$f0).m405lambda$com_android_camera_ui_ZZZFrameLayout_12135();
                }

                private final /* synthetic */ void $m$2() {
                    ((ZZZFrameLayout) this.f89$f0).m404lambda$com_android_camera_ui_ZZZFrameLayout_11730();
                }

                private final /* synthetic */ void $m$3() {
                    ((ZZZFrameLayout) this.f89$f0).m411lambda$com_android_camera_ui_ZZZFrameLayout_7369();
                }

                private final /* synthetic */ void $m$4() {
                    ((ZZZFrameLayout) this.f89$f0).m413lambda$com_android_camera_ui_ZZZFrameLayout_7609();
                }

                private final /* synthetic */ void $m$5() {
                    ((ZZZFrameLayout) this.f89$f0).m400lambda$com_android_camera_ui_ZZZFrameLayout_10165();
                }

                private final /* synthetic */ void $m$6() {
                    ((ZZZFrameLayout) this.f89$f0).m402lambda$com_android_camera_ui_ZZZFrameLayout_10344();
                }

                private final /* synthetic */ void $m$7() {
                    ((ZZZFrameLayout) this.f89$f0).m403lambda$com_android_camera_ui_ZZZFrameLayout_11140();
                }

                private final /* synthetic */ void $m$8() {
                    ((ZZZFrameLayout) this.f89$f0).m406lambda$com_android_camera_ui_ZZZFrameLayout_14067();
                }

                private final /* synthetic */ void $m$9() {
                    ((ZZZFrameLayout) this.f89$f0).m407lambda$com_android_camera_ui_ZZZFrameLayout_14123();
                }

                public /* synthetic */ RunnableC01692(byte b, Object obj) {
                    this.$id = b;
                    this.f89$f0 = obj;
                }

                @Override // java.lang.Runnable
                public final void run() {
                    switch (this.$id) {
                        case 0:
                            $m$0();
                            return;
                        case 1:
                            $m$1();
                            return;
                        case 2:
                            $m$2();
                            return;
                        case 3:
                            $m$3();
                            return;
                        case 4:
                            $m$4();
                            return;
                        case 5:
                            $m$5();
                            return;
                        case 6:
                            $m$6();
                            return;
                        case FrameMetricsAggregator.DELAY_INDEX /* 7 */:
                            $m$7();
                            return;
                        case 8:
                            $m$8();
                            return;
                        case 9:
                            $m$9();
                            return;
                        default:
                            throw new AssertionError();
                    }
                }
            }

            private final /* synthetic */ boolean $m$0(View view, MotionEvent motionEvent) {
                return ((ZZZFrameLayout) this).m409lambda$com_android_camera_ui_ZZZFrameLayout_6466(view, motionEvent);
            }

            @Override // android.view.View.OnTouchListener
            public final boolean onTouch(View view, MotionEvent motionEvent) {
                return $m$0(view, motionEvent);
            }
        });
    }

    /* renamed from: com.android.camera.ui.ZZZFrameLayout$1 */
    class ViewTreeObserverOnGlobalLayoutListenerC01891 implements ViewTreeObserver.OnGlobalLayoutListener {
        ViewTreeObserverOnGlobalLayoutListenerC01891() {
        }

        @Override // android.view.ViewTreeObserver.OnGlobalLayoutListener
        public void onGlobalLayout() {
            ZZZFrameLayout.this.scrollView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            ZZZFrameLayout.this.centerX = ZZZFrameLayout.this.scrollView.getWidth() / 2;
            int width = (ZZZFrameLayout.this.scrollView.getWidth() - ZZZFrameLayout.this.textViews[0].getWidth()) / 2;
            ZZZFrameLayout.this.container.setPadding(width, 0, width, 0);
            ZZZFrameLayout.this.container.post(new $Lambda$2F77a4O5cC_FZNKqb6EdMwzAco.RunnableC01692((byte) 0, this));
        }

        /* renamed from: lambda$-com_android_camera_ui_ZZZFrameLayout$1_5294, reason: not valid java name */
        /* synthetic */ void m414lambda$com_android_camera_ui_ZZZFrameLayout$1_5294() {
            ZZZFrameLayout.this.container.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() { // from class: com.android.camera.ui.ZZZFrameLayout.1.1
                @Override // android.view.ViewTreeObserver.OnGlobalLayoutListener
                public void onGlobalLayout() {
                    ZZZFrameLayout.this.container.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    ZZZFrameLayout.this.calculateItemPositions();
                    ZZZFrameLayout.this.FirstscrollToCenter(4);
                }
            });
            ZZZFrameLayout.this.container.requestLayout();
        }
    }

    /* renamed from: lambda$-com_android_camera_ui_ZZZFrameLayout_6202, reason: not valid java name */
    /* synthetic */ void m408lambda$com_android_camera_ui_ZZZFrameLayout_6202() {
        this.lastScrollX = this.scrollView.getScrollX();
        m405lambda$com_android_camera_ui_ZZZFrameLayout_12135();
        if (this.snapRunnable != null) {
            this.handler.removeCallbacks(this.snapRunnable);
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* renamed from: lambda$-com_android_camera_ui_ZZZFrameLayout_6466, reason: not valid java name */
    /* synthetic */ boolean m409lambda$com_android_camera_ui_ZZZFrameLayout_6466(View view, MotionEvent motionEvent) {
        byte b = 4;
        byte b2 = 3;
        final byte b3 = 1;
        final byte b4 = 0;
        switch (motionEvent.getAction()) {
            case 0:
                this.isScrolling = true;
                if (this.snapRunnable != null) {
                    this.handler.removeCallbacks(this.snapRunnable);
                    this.snapRunnable = null;
                }
                return false;
            case 1:
            case 3:
                this.isScrolling = false;
                final int iFindCenterIndex = findCenterIndex();
                EntryCameraMode(iFindCenterIndex);
                if (iFindCenterIndex == 3 || iFindCenterIndex == 4) {
                    this.handler.postDelayed(new Runnable() { // from class: com.android.camera.ui.-$Lambda$2F77a4O5cC_FZNKqb6EdM-wzAco.4
                        private final /* synthetic */ void $m$0() {
                            ((ZZZFrameLayout) this).m410lambda$com_android_camera_ui_ZZZFrameLayout_7281(iFindCenterIndex);
                        }

                        private final /* synthetic */ void $m$1() {
                            ((ZZZFrameLayout) this).m412lambda$com_android_camera_ui_ZZZFrameLayout_7523(iFindCenterIndex);
                        }

                        @Override // java.lang.Runnable
                        public final void run() {
                            switch (b4) {
                                case 0:
                                    $m$0();
                                    return;
                                case 1:
                                    $m$1();
                                    return;
                                default:
                                    throw new AssertionError();
                            }
                        }
                    }, 500L);
                    this.handler.postDelayed(new $Lambda$2F77a4O5cC_FZNKqb6EdMwzAco.RunnableC01692(b2, this), 500L);
                } else if (iFindCenterIndex >= 0) {
                    this.handler.postDelayed(new Runnable() { // from class: com.android.camera.ui.-$Lambda$2F77a4O5cC_FZNKqb6EdM-wzAco.4
                        private final /* synthetic */ void $m$0() {
                            ((ZZZFrameLayout) this).m410lambda$com_android_camera_ui_ZZZFrameLayout_7281(iFindCenterIndex);
                        }

                        private final /* synthetic */ void $m$1() {
                            ((ZZZFrameLayout) this).m412lambda$com_android_camera_ui_ZZZFrameLayout_7523(iFindCenterIndex);
                        }

                        @Override // java.lang.Runnable
                        public final void run() {
                            switch (b3) {
                                case 0:
                                    $m$0();
                                    return;
                                case 1:
                                    $m$1();
                                    return;
                                default:
                                    throw new AssertionError();
                            }
                        }
                    }, 500L);
                    this.handler.postDelayed(new $Lambda$2F77a4O5cC_FZNKqb6EdMwzAco.RunnableC01692(b, this), 500L);
                }
                return true;
            case 2:
                setMidViewWeight(3.15f);
                return false;
            default:
                return false;
        }
    }

    /* renamed from: lambda$-com_android_camera_ui_ZZZFrameLayout_7369, reason: not valid java name */
    /* synthetic */ void m411lambda$com_android_camera_ui_ZZZFrameLayout_7369() {
        setMidViewWeight(0.8f);
    }

    /* renamed from: lambda$-com_android_camera_ui_ZZZFrameLayout_7609, reason: not valid java name */
    /* synthetic */ void m413lambda$com_android_camera_ui_ZZZFrameLayout_7609() {
        setMidViewWeight(1.7f);
    }

    private void setMidViewWeight(float f) {
        ViewGroup.LayoutParams layoutParams = this.mViewMid.getLayoutParams();
        if (layoutParams instanceof LinearLayout.LayoutParams) {
            LinearLayout.LayoutParams layoutParams2 = (LinearLayout.LayoutParams) layoutParams;
            layoutParams2.weight = f;
            this.mViewMid.setLayoutParams(layoutParams2);
        }
        RelativeLayout.LayoutParams layoutParams3 = (RelativeLayout.LayoutParams) this.ivChageCamera.getLayoutParams();
        RelativeLayout.LayoutParams layoutParams4 = (RelativeLayout.LayoutParams) this.ivEnteryPhoto.getLayoutParams();
        if (f == 3.15f) {
            layoutParams3.leftMargin = 86;
            layoutParams4.rightMargin = 92;
            this.ivChageCamera.setLayoutParams(layoutParams3);
            this.mViewMid.setBackground(getResources().getDrawable(R.drawable.zzz_camera_larger_bg));
            return;
        }
        if (f == 1.7f) {
            layoutParams3.leftMargin = 10;
            layoutParams4.rightMargin = 16;
            this.ivChageCamera.setLayoutParams(layoutParams3);
            this.mViewMid.setBackground(getResources().getDrawable(R.drawable.zzz_camera_mid_bg));
            return;
        }
        layoutParams3.leftMargin = 86;
        layoutParams4.rightMargin = 92;
        this.ivChageCamera.setLayoutParams(layoutParams3);
        this.mViewMid.setBackground(getResources().getDrawable(R.drawable.zzz_camera_normal_bg));
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        final byte b = 1;
        final Integer num = (Integer) view.getTag();
        if (view.getId() == R.id.iv_change_camera) {
            Log.v("xiaoyao", "onClickxxxxxxxxxxxx  bbb " + Camera.getNumberOfCameras());
            if (Camera.getNumberOfCameras() > 1) {
                CameraActivity cameraActivity = (CameraActivity) this.mContext;
                cameraActivity.onCameraPicked(cameraActivity.getCameraId() == 0 ? (byte) 1 : (byte) 0);
                return;
            }
            return;
        }
        if (num != null) {
            EntryCameraMode(num.intValue());
            if (num.intValue() == 3 || num.intValue() == 4) {
                this.handler.postDelayed(new Runnable() { // from class: com.android.camera.ui.-$Lambda$2F77a4O5cC_FZNKqb6EdM-wzAco.3
                    private final /* synthetic */ void $m$0() {
                        ((ZZZFrameLayout) this).m399lambda$com_android_camera_ui_ZZZFrameLayout_10091((Integer) num);
                    }

                    private final /* synthetic */ void $m$1() {
                        ((ZZZFrameLayout) this).m401lambda$com_android_camera_ui_ZZZFrameLayout_10272((Integer) num);
                    }

                    @Override // java.lang.Runnable
                    public final void run() {
                        switch (b) {
                            case 0:
                                $m$0();
                                return;
                            case 1:
                                $m$1();
                                return;
                            default:
                                throw new AssertionError();
                        }
                    }
                }, 500L);
                this.handler.postDelayed(new $Lambda$2F77a4O5cC_FZNKqb6EdMwzAco.RunnableC01692((byte) 5, this), 500L);
            } else {
                this.handler.postDelayed(new Runnable() { // from class: com.android.camera.ui.-$Lambda$2F77a4O5cC_FZNKqb6EdM-wzAco.3
                    private final /* synthetic */ void $m$0() {
                        ((ZZZFrameLayout) this).m399lambda$com_android_camera_ui_ZZZFrameLayout_10091((Integer) num);
                    }

                    private final /* synthetic */ void $m$1() {
                        ((ZZZFrameLayout) this).m401lambda$com_android_camera_ui_ZZZFrameLayout_10272((Integer) num);
                    }

                    @Override // java.lang.Runnable
                    public final void run() {
                        switch (b) {
                            case 0:
                                $m$0();
                                return;
                            case 1:
                                $m$1();
                                return;
                            default:
                                throw new AssertionError();
                        }
                    }
                }, 500L);
                this.handler.postDelayed(new $Lambda$2F77a4O5cC_FZNKqb6EdMwzAco.RunnableC01692((byte) 6, this), 500L);
            }
        }
    }

    /* renamed from: lambda$-com_android_camera_ui_ZZZFrameLayout_10091, reason: not valid java name */
    /* synthetic */ void m399lambda$com_android_camera_ui_ZZZFrameLayout_10091(Integer num) {
        m410lambda$com_android_camera_ui_ZZZFrameLayout_7281(num.intValue());
    }

    /* renamed from: lambda$-com_android_camera_ui_ZZZFrameLayout_10165, reason: not valid java name */
    /* synthetic */ void m400lambda$com_android_camera_ui_ZZZFrameLayout_10165() {
        setMidViewWeight(0.8f);
    }

    /* renamed from: lambda$-com_android_camera_ui_ZZZFrameLayout_10272, reason: not valid java name */
    /* synthetic */ void m401lambda$com_android_camera_ui_ZZZFrameLayout_10272(Integer num) {
        m412lambda$com_android_camera_ui_ZZZFrameLayout_7523(num.intValue());
    }

    /* renamed from: lambda$-com_android_camera_ui_ZZZFrameLayout_10344, reason: not valid java name */
    /* synthetic */ void m402lambda$com_android_camera_ui_ZZZFrameLayout_10344() {
        setMidViewWeight(1.7f);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void calculateItemPositions() {
        int left = this.container.getLeft();
        for (int i = 0; i < this.textViews.length; i++) {
            this.itemPositions[i] = this.textViews[i].getLeft() + left + (this.textViews[i].getWidth() / 2);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: scrollToCenter, reason: merged with bridge method [inline-methods] */
    public void m412lambda$com_android_camera_ui_ZZZFrameLayout_7523(int i) {
        if (i < 0 || i >= this.textViews.length) {
            return;
        }
        this.scrollView.smoothScrollTo(Math.max(0, Math.min(this.itemPositions[i] - this.centerX, this.container.getWidth() - this.scrollView.getWidth())), 0);
        this.handler.postDelayed(new $Lambda$2F77a4O5cC_FZNKqb6EdMwzAco.RunnableC01692((byte) 7, this), 100L);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: ToscrollToCenter, reason: merged with bridge method [inline-methods] */
    public void m410lambda$com_android_camera_ui_ZZZFrameLayout_7281(int i) {
        if (i < 0 || i >= this.textViews.length) {
            return;
        }
        int i2 = this.itemPositions[i] - this.centerX;
        if (i == 3) {
            i2 += 72;
        }
        if (i == 4) {
            i2 -= 66;
        }
        this.scrollView.smoothScrollTo(Math.max(0, Math.min(i2, this.container.getWidth() - this.scrollView.getWidth())), 0);
        this.handler.postDelayed(new $Lambda$2F77a4O5cC_FZNKqb6EdMwzAco.RunnableC01692((byte) 2, this), 100L);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void FirstscrollToCenter(int i) {
        this.scrollView.scrollBy(Math.max(0, Math.min((this.itemPositions[i] - this.centerX) - 66, this.container.getWidth() - this.scrollView.getWidth())), 0);
        this.handler.postDelayed(new $Lambda$2F77a4O5cC_FZNKqb6EdMwzAco.RunnableC01692((byte) 1, this), 100L);
    }

    private int findCenterIndex() {
        int scrollX = this.scrollView.getScrollX() + this.centerX;
        int i = -1;
        int i2 = Integer.MAX_VALUE;
        for (int i3 = 0; i3 < this.textViews.length; i3++) {
            int iAbs = Math.abs(this.itemPositions[i3] - scrollX);
            if (iAbs < i2) {
                i2 = iAbs;
                i = i3;
            }
        }
        return i;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: updateHighlightedItem, reason: merged with bridge method [inline-methods] and merged with bridge method [inline-methods] and merged with bridge method [inline-methods] */
    public void m405lambda$com_android_camera_ui_ZZZFrameLayout_12135() {
        int iFindCenterIndex = findCenterIndex();
        if (iFindCenterIndex < 0) {
            return;
        }
        for (int i = 0; i < this.textViews.length; i++) {
            this.textViews[i].setTextColor(-1);
            this.textViews[i].setBackground(null);
        }
        TextView textView = this.textViews[iFindCenterIndex];
        textView.setTextColor(-256);
        if (iFindCenterIndex == 0) {
            textView.setBackground(getResources().getDrawable(R.drawable.zzz_camera_time_bg));
            return;
        }
        if (iFindCenterIndex == 2) {
            textView.setBackground(getResources().getDrawable(R.drawable.zzz_camera_cinematic_bg));
            return;
        }
        if (iFindCenterIndex == 3 || iFindCenterIndex == 4) {
            textView.setBackground(getResources().getDrawable(R.drawable.zzz_camera_video_photo_bg));
            return;
        }
        if (iFindCenterIndex == 5) {
            textView.setBackground(getResources().getDrawable(R.drawable.zzz_camera_portrait_bg));
            return;
        }
        if (iFindCenterIndex == 6 || iFindCenterIndex == 1) {
            textView.setBackground(getResources().getDrawable(R.drawable.zzz_camera_sloandspatial_bg));
        } else if (iFindCenterIndex == 7) {
            textView.setBackground(getResources().getDrawable(R.drawable.zzz_camera_pano_bg));
        }
    }

    public void updateVideoIcon() {
        EntryCameraMode(3);
        this.handler.postDelayed(new $Lambda$2F77a4O5cC_FZNKqb6EdMwzAco.RunnableC01692((byte) 8, this), 50L);
        this.handler.postDelayed(new $Lambda$2F77a4O5cC_FZNKqb6EdMwzAco.RunnableC01692((byte) 9, this), 50L);
    }

    /* renamed from: lambda$-com_android_camera_ui_ZZZFrameLayout_14067, reason: not valid java name */
    /* synthetic */ void m406lambda$com_android_camera_ui_ZZZFrameLayout_14067() {
        m410lambda$com_android_camera_ui_ZZZFrameLayout_7281(3);
    }

    /* renamed from: lambda$-com_android_camera_ui_ZZZFrameLayout_14123, reason: not valid java name */
    /* synthetic */ void m407lambda$com_android_camera_ui_ZZZFrameLayout_14123() {
        setMidViewWeight(0.8f);
    }

    private void EntryCameraMode(int i) {
        OnWheelChangedListener onWheelChangedListener = this.mOnWheelChangedListener;
        this.mCameraActivity.updateVideoIcon(i);
    }

    public void setActivityAndCtrl(CameraActivity cameraActivity, ISettingCtrl iSettingCtrl) {
        this.mCameraActivity = cameraActivity;
        this.mCtrl = iSettingCtrl;
    }
}
