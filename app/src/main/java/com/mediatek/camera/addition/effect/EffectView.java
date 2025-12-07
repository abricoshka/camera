package com.mediatek.camera.addition.effect;

import android.app.Activity;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.mediatek.camera.R;
import com.mediatek.camera.addition.effect.EffectLayout;
import com.mediatek.camera.p004ui.CameraView;
import com.mediatek.camera.p004ui.UIRotateLayout;
import com.mediatek.camera.platform.ICameraAppUi;
import com.mediatek.camera.platform.IModuleCtrl;
import com.mediatek.camera.setting.preference.ListPreference;
import com.mediatek.camera.util.Log;
import com.mediatek.camera.util.Util;

/* loaded from: classes.dex */
public class EffectView extends CameraView implements EffectLayout.OnItemClickListener {
    private static final String[] mEffectName = {"none", "mono", "negative", "solarize", "sepia", "posterize", "whiteboard", "blackboard", "aqua", "sepiagreen", "sepiablue", "nashville", "hefe", "valencia", "xproll", "lofi", "sierra", "kelvin", "walden", "f1977", "num"};
    private MyAdapter mAdapter;
    private int mBufferHeight;
    private int mBufferWidth;
    private String mCurrrentFocusMode;
    private float mDensity;
    private int mDisplayHeight;
    private int mDisplayWidth;
    private CharSequence[] mEffectEntries;
    private CharSequence[] mEffectEntryValues;
    private ListPreference mEffectPreference;
    private boolean mEffectsDone;
    private ViewGroup mEffectsLayout;
    private Animation mFadeIn;
    private Animation mFadeOut;
    private EffectLayout mGridView;
    private ICameraAppUi mICameraAppUi;
    private IModuleCtrl mIModuleCtrl;
    private Listener mListener;
    protected Handler mMainHandler;
    private boolean mMirror;
    private boolean mNeedScrollToFirstPosition;
    private boolean mNeedStartFaceDetection;
    private int mNumsOfEffect;
    private int mOrientation;
    private int mPadding;
    private int mSelectedPosition;
    private boolean mShowEffects;
    private boolean mSizeChanged;
    private Surface[] mSurfaceList;

    public interface Listener {
        void hideEffect(boolean z, int i);

        void onInitialize();

        void onItemClick(String str);

        void onRelease();

        void onSurfaceAvailable(Surface surface, int i, int i2, int i3);

        void onUpdateEffect(int i, int i2);
    }

    public EffectView(Activity activity) {
        super(activity);
        this.mNumsOfEffect = 0;
        this.mDisplayWidth = 0;
        this.mDisplayHeight = 0;
        this.mBufferWidth = 0;
        this.mBufferHeight = 0;
        this.mOrientation = 0;
        this.mPadding = 0;
        this.mSelectedPosition = 0;
        this.mDensity = 0.0f;
        this.mShowEffects = false;
        this.mSizeChanged = false;
        this.mNeedScrollToFirstPosition = false;
        this.mNeedStartFaceDetection = false;
        this.mEffectsDone = false;
        this.mMirror = false;
        this.mCurrrentFocusMode = null;
        this.mSurfaceList = new Surface[12];
        this.mMainHandler = new Handler() { // from class: com.mediatek.camera.addition.effect.EffectView.1
            @Override // android.os.Handler
            public void handleMessage(Message message) {
                Log.m31d("EffectView", "handleMessage(), msg:" + message);
                switch (message.what) {
                    case 0:
                        if (EffectView.this.mGridView != null) {
                            EffectView.this.mGridView.removeAllViews();
                        }
                        if (EffectView.this.mEffectsLayout != null && EffectView.this.mEffectsLayout.getParent() != null) {
                            EffectView.this.mEffectsLayout.removeAllViews();
                            ((ViewGroup) EffectView.this.mEffectsLayout.getParent()).removeView(EffectView.this.mEffectsLayout);
                        }
                        EffectView.this.mGridView = null;
                        EffectView.this.mEffectsLayout = null;
                        EffectView.this.mEffectsDone = false;
                        if (EffectView.this.mListener != null) {
                            EffectView.this.mListener.onRelease();
                            break;
                        }
                        break;
                    case 1:
                        EffectView.this.rotateGridViewItem(EffectView.this.getOrientation());
                        break;
                    case 2:
                        if (EffectView.this.mEffectsLayout != null) {
                            EffectView.this.startFadeInAnimation(EffectView.this.mEffectsLayout);
                            EffectView.this.mEffectsLayout.setAlpha(1.0f);
                            break;
                        }
                        break;
                    case 3:
                        EffectView.this.hideEffect(false, 0L);
                        break;
                }
            }
        };
        Log.m31d("EffectView", "[EffectView]constructor...");
    }

    @Override // com.mediatek.camera.p004ui.CameraView, com.mediatek.camera.platform.ICameraView
    public void init(Activity activity, ICameraAppUi iCameraAppUi, IModuleCtrl iModuleCtrl) {
        Log.m31d("EffectView", "[init]...");
        this.mIModuleCtrl = iModuleCtrl;
        this.mICameraAppUi = iCameraAppUi;
    }

    @Override // com.mediatek.camera.p004ui.CameraView
    protected View getView() {
        return null;
    }

    @Override // com.mediatek.camera.p004ui.CameraView, com.mediatek.camera.platform.ICameraView
    public void onOrientationChanged(int i) {
        Log.m31d("EffectView", "onOrientationChanged( " + i + "), mOrientation:" + this.mOrientation);
        if (this.mOrientation == i) {
            return;
        }
        this.mOrientation = i;
        rotateGridViewItem(i);
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    @Override // com.mediatek.camera.p004ui.CameraView, com.mediatek.camera.platform.ICameraView
    public boolean update(int i, Object... objArr) {
        switch (i) {
            case 0:
                this.mEffectPreference = (ListPreference) objArr[0];
                this.mMirror = ((Boolean) objArr[1]).booleanValue();
                showEffect();
                return false;
            case 1:
                hideEffect(((Boolean) objArr[0]).booleanValue(), ((Integer) objArr[1]).intValue());
                return false;
            case 2:
                onSizeChanged(((Integer) objArr[0]).intValue(), ((Integer) objArr[1]).intValue());
                return false;
            case 3:
                onEffectsDone();
                return false;
            case 4:
                if (this.mMainHandler != null) {
                    this.mMainHandler.removeMessages(0);
                    this.mMainHandler.sendEmptyMessage(0);
                }
                return false;
            default:
                return false;
        }
    }

    @Override // com.mediatek.camera.p004ui.CameraView, com.mediatek.camera.platform.ICameraView
    public void setListener(Object obj) {
        this.mListener = (Listener) obj;
    }

    @Override // com.mediatek.camera.addition.effect.EffectLayout.OnItemClickListener
    public void onItemClick(View view, int i) {
        Log.m31d("EffectView", "[onItemClick], position:" + i);
        if (!this.mEffectsDone) {
            return;
        }
        this.mEffectPreference.setValue(this.mEffectEntryValues[i].toString());
        if (this.mListener != null) {
            this.mListener.onItemClick(this.mEffectEntryValues[i].toString());
        }
    }

    public void showEffect() {
        Log.m31d("EffectView", "[showEffect]..., start");
        this.mMainHandler.removeMessages(0);
        this.mShowEffects = true;
        this.mEffectPreference.reloadValue();
        this.mSelectedPosition = this.mEffectPreference.findIndexOfValue(this.mEffectPreference.getValue());
        if (this.mGridView != null && this.mNeedScrollToFirstPosition) {
            this.mNeedScrollToFirstPosition = false;
            this.mGridView.scrollToSelectedPosition(this.mSelectedPosition);
            this.mGridView.showSelectedBorder(this.mSelectedPosition);
        }
        if (this.mEffectsLayout == null) {
            initialEffect();
            if (this.mEffectsDone) {
                startFadeInAnimation(this.mEffectsLayout);
            }
        }
        this.mEffectsLayout.setVisibility(0);
        Log.m31d("EffectView", "[showEffect]..., end");
    }

    public void onSizeChanged(int i, int i2) {
        Log.m31d("EffectView", "input onSizeChanged(), inputSize, width:" + i + ", height:" + i2 + "displayOrientation:" + this.mIModuleCtrl.getDisplayRotation() + ", mOrientation:" + this.mOrientation);
        this.mDisplayWidth = Math.max(i, i2);
        this.mDisplayHeight = Math.min(i, i2);
        this.mIModuleCtrl.getDisplayRotation();
        if (this.mGridView != null) {
            this.mMainHandler.post(new Runnable() { // from class: com.mediatek.camera.addition.effect.EffectView.2
                @Override // java.lang.Runnable
                public void run() {
                    Log.m31d("EffectView", "setDisplaySize(" + EffectView.this.mDisplayWidth + "," + EffectView.this.mDisplayHeight + ")");
                    if (EffectView.this.mGridView != null) {
                        EffectView.this.mGridView.setDisplaySize(EffectView.this.mDisplayWidth, EffectView.this.mDisplayHeight);
                    }
                }
            });
        }
        Log.m31d("EffectView", "onSizeChanged(), outputSize, mDisplayWidth:" + this.mDisplayWidth + ", mDisplayHeight:" + this.mDisplayHeight);
        this.mSizeChanged = true;
    }

    public void hideEffect(boolean z, long j) {
        Log.m31d("EffectView", "hideEffect(), animation:" + z + ", mEffectsLayout:" + this.mEffectsLayout);
        if (this.mEffectsLayout != null) {
            this.mMainHandler.removeMessages(0);
            this.mShowEffects = false;
            if (z) {
                startFadeOutAnimation(this.mEffectsLayout);
            }
            this.mICameraAppUi.restoreViewState();
            this.mEffectsLayout.setVisibility(8);
            this.mMainHandler.sendEmptyMessageDelayed(0, j);
        }
    }

    public void onEffectsDone() {
        Log.m31d("EffectView", "onEffectsDone()");
        this.mMainHandler.sendEmptyMessage(2);
        this.mEffectsDone = true;
    }

    protected void startFadeInAnimation(View view) {
        if (this.mFadeIn == null) {
            this.mFadeIn = AnimationUtils.loadAnimation(getContext(), R.anim.gird_effects_fade_in);
        }
        if (view != null && this.mFadeIn != null) {
            view.startAnimation(this.mFadeIn);
            this.mFadeIn = null;
        }
    }

    protected void startFadeOutAnimation(View view) {
        if (this.mFadeOut == null) {
            this.mFadeOut = AnimationUtils.loadAnimation(getContext(), R.anim.grid_effects_fade_out);
            this.mFadeOut.setAnimationListener(new Animation.AnimationListener() { // from class: com.mediatek.camera.addition.effect.EffectView.3
                @Override // android.view.animation.Animation.AnimationListener
                public void onAnimationStart(Animation animation) {
                }

                @Override // android.view.animation.Animation.AnimationListener
                public void onAnimationRepeat(Animation animation) {
                }

                @Override // android.view.animation.Animation.AnimationListener
                public void onAnimationEnd(Animation animation) {
                }
            });
        }
        if (view != null) {
            view.startAnimation(this.mFadeOut);
            this.mFadeOut = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void rotateGridViewItem(int i) {
        Log.m31d("EffectView", "rotateGridViewItem(), orientation:" + i);
        int iComputeRotation = Util.computeRotation(getContext(), i, 270);
        if (this.mGridView != null) {
            int childCount = this.mGridView.getChildCount();
            for (int i2 = 0; i2 < childCount; i2++) {
                ViewGroup viewGroup = (ViewGroup) this.mGridView.getChildAt(i2);
                if (viewGroup != null) {
                    int childCount2 = viewGroup.getChildCount();
                    for (int i3 = 0; i3 < childCount2; i3++) {
                        View childAt = viewGroup.getChildAt(i3);
                        if (childAt != null) {
                            UIRotateLayout uIRotateLayout = (UIRotateLayout) childAt.findViewById(R.id.rotate);
                            layoutByOrientation(uIRotateLayout, iComputeRotation);
                            Util.setOrientation(uIRotateLayout, iComputeRotation, true);
                        }
                    }
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void layoutByOrientation(UIRotateLayout uIRotateLayout, int i) {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) uIRotateLayout.getLayoutParams();
        switch (i) {
            case 0:
                layoutParams.addRule(11);
                layoutParams.addRule(12);
                layoutParams.removeRule(9);
                layoutParams.removeRule(10);
                break;
            case 90:
                layoutParams.addRule(11);
                layoutParams.addRule(10);
                layoutParams.removeRule(9);
                layoutParams.removeRule(12);
                break;
            case 180:
                layoutParams.addRule(9);
                layoutParams.addRule(10);
                layoutParams.removeRule(11);
                layoutParams.removeRule(12);
                break;
            case 270:
                layoutParams.addRule(9);
                layoutParams.addRule(12);
                layoutParams.removeRule(11);
                layoutParams.removeRule(10);
                break;
        }
        uIRotateLayout.setLayoutParams(layoutParams);
        uIRotateLayout.requestLayout();
    }

    private void initialEffect() {
        Log.m31d("EffectView", "[initialEffect]mEffectsLayout:" + this.mEffectsLayout + ", mSizeChanged:" + this.mSizeChanged + ", mMirror:" + this.mMirror);
        if (this.mEffectsLayout != null) {
            if (this.mSizeChanged) {
                this.mGridView.setDisplaySize(this.mDisplayWidth, this.mDisplayHeight);
                this.mSizeChanged = false;
                return;
            }
            return;
        }
        this.mEffectEntryValues = this.mEffectPreference.getEntryValues();
        this.mEffectEntries = this.mEffectPreference.getEntries();
        this.mNumsOfEffect = this.mEffectPreference.getEntryValues().length;
        Log.m31d("EffectView", "nums of effect:" + this.mNumsOfEffect);
        if (this.mListener != null) {
            this.mListener.onInitialize();
        }
        this.mEffectsLayout = (ViewGroup) inflate(R.layout.lomo_effects);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(-1, -1);
        layoutParams.topMargin = 0;
        getContext().addContentView(this.mEffectsLayout, layoutParams);
        this.mGridView = (EffectLayout) this.mEffectsLayout.findViewById(R.id.lomo_effect_gridview);
        int i = this.mDisplayWidth % 3 == 0 ? this.mDisplayWidth / 3 : (this.mDisplayWidth / 3) + 1;
        this.mGridView.setColumnWidth(i);
        this.mGridView.setColumnHeight(this.mDisplayHeight / 3);
        this.mGridView.setColumnCount(3);
        this.mGridView.setDisplaySize(i * 3, this.mDisplayHeight);
        this.mAdapter = new MyAdapter(getContext());
        this.mGridView.setAdapter(this.mAdapter);
        this.mGridView.setOnItemClickListener(this);
        this.mGridView.setSelector(R.drawable.lomo_effect_selector);
        this.mGridView.setOverScrollMode(0);
        this.mGridView.setOnScrollListener(new EffectLayout.OnScrollListener() { // from class: com.mediatek.camera.addition.effect.EffectView.4
            @Override // com.mediatek.camera.addition.effect.EffectLayout.OnScrollListener
            public void onScrollOut(EffectLayout effectLayout, int i2) {
                Log.m31d("EffectView", "onScrollOut()");
                if (i2 == 1) {
                    EffectView.this.mNeedScrollToFirstPosition = true;
                    if (EffectView.this.mListener != null) {
                        EffectView.this.mListener.hideEffect(false, 0);
                    }
                }
            }

            @Override // com.mediatek.camera.addition.effect.EffectLayout.OnScrollListener
            public void onScrollDone(EffectLayout effectLayout, int i2, int i3) {
                Log.m31d("EffectView", "onScrollDone(), startPosition:" + i2 + ", endPosition:" + i3);
                for (int i4 = i2; i4 < i3; i4++) {
                    int effectId = EffectView.this.getEffectId(EffectView.this.mEffectEntryValues[i4].toString());
                    int i5 = i4 % 12;
                    if (EffectView.this.mListener != null) {
                        EffectView.this.mListener.onUpdateEffect(i5, effectId);
                    }
                }
                if (i2 == 0) {
                    for (int i6 = i3; i6 < i3 + 3; i6++) {
                        if (EffectView.this.mListener != null) {
                            EffectView.this.mListener.onUpdateEffect(i6, -1);
                        }
                    }
                    return;
                }
                for (int i7 = i2 - 1; i7 >= i2 - 3; i7--) {
                    if (EffectView.this.mListener != null) {
                        EffectView.this.mListener.onUpdateEffect(i7, -1);
                    }
                }
            }
        });
        this.mGridView.scrollToSelectedPosition(this.mSelectedPosition);
        this.mGridView.showSelectedBorder(this.mSelectedPosition);
        this.mSizeChanged = false;
        this.mEffectsLayout.setAlpha(0.0f);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getEffectId(String str) {
        for (int i = 0; i < mEffectName.length; i++) {
            if (Util.equals(str, mEffectName[i])) {
                Log.m31d("EffectView", "effectName:" + str + ", effetId:" + i);
                return i;
            }
        }
        Log.m31d("EffectView", "effectName:" + str + ", effetId: -1");
        return -1;
    }

    public class MyAdapter extends BaseAdapter {
        LayoutInflater mLayoutInflater;

        public MyAdapter(Context context) {
            this.mLayoutInflater = LayoutInflater.from(context);
        }

        @Override // android.widget.Adapter
        public int getCount() {
            return EffectView.this.mNumsOfEffect;
        }

        @Override // android.widget.Adapter
        public Object getItem(int i) {
            return EffectView.this.mSurfaceList[i];
        }

        @Override // android.widget.Adapter
        public long getItemId(int i) {
            return i;
        }

        private class ViewHolder {
            int mPosition;
            UIRotateLayout mRotateLayout;
            TextView mTextView;
            TextureView mTextureView;

            /* synthetic */ ViewHolder(MyAdapter myAdapter, ViewHolder viewHolder) {
                this();
            }

            private ViewHolder() {
            }
        }

        @Override // android.widget.Adapter
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            ViewHolder viewHolder2 = null;
            Log.m31d("EffectView", "convertView:" + view + ", position:" + i);
            if (EffectView.this.mEffectsLayout == null) {
                Log.m31d("EffectView", "mEffectsLayout is null");
                return null;
            }
            int effectId = EffectView.this.getEffectId(EffectView.this.mEffectEntryValues[i].toString());
            if (i > 8 && i < 12 && view == null) {
                effectId = -1;
            }
            if (i >= 12) {
                EffectView.this.mListener.onUpdateEffect(i - 12, effectId);
            } else {
                EffectView.this.mListener.onUpdateEffect(i, effectId);
            }
            if (view == null) {
                view = this.mLayoutInflater.inflate(R.layout.lomo_effects_item, (ViewGroup) null);
                ViewHolder viewHolder3 = new ViewHolder(this, viewHolder2);
                viewHolder3.mTextureView = (TextureView) view.findViewById(R.id.textureview);
                viewHolder3.mTextView = (TextView) view.findViewById(R.id.effects_name);
                viewHolder3.mRotateLayout = (UIRotateLayout) view.findViewById(R.id.rotate);
                viewHolder3.mTextureView.setLayoutParams(new RelativeLayout.LayoutParams(EffectView.this.mDisplayWidth / 3, EffectView.this.mDisplayHeight / 3));
                int paddingLeft = view.getPaddingLeft();
                if (EffectView.this.mIModuleCtrl.getDisplayOrientation() == 270 || EffectView.this.mIModuleCtrl.getDisplayOrientation() == 180) {
                    viewHolder3.mTextureView.setPivotX((EffectView.this.mDisplayWidth / 6) - paddingLeft);
                    viewHolder3.mTextureView.setPivotY((EffectView.this.mDisplayHeight / 6) - paddingLeft);
                    viewHolder3.mTextureView.setRotation(180.0f);
                }
                if (EffectView.this.mMirror) {
                    viewHolder3.mTextureView.setPivotX((EffectView.this.mDisplayWidth / 6) - paddingLeft);
                    viewHolder3.mTextureView.setPivotY((EffectView.this.mDisplayHeight / 6) - paddingLeft);
                    viewHolder3.mTextureView.setRotationY(180.0f);
                }
                viewHolder3.mTextureView.setSurfaceTextureListener(EffectView.this.new LomoSurfaceTextureListener(i));
                viewHolder3.mPosition = i;
                view.setTag(viewHolder3);
                viewHolder = viewHolder3;
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }
            viewHolder.mTextView.setText(EffectView.this.mEffectEntries[i]);
            int iComputeRotation = Util.computeRotation(EffectView.this.getContext(), EffectView.this.mOrientation, 270);
            EffectView.this.layoutByOrientation(viewHolder.mRotateLayout, iComputeRotation);
            Util.setOrientation(viewHolder.mRotateLayout, iComputeRotation, true);
            return view;
        }
    }

    public class LomoSurfaceTextureListener implements TextureView.SurfaceTextureListener {
        private int mPosition;

        public LomoSurfaceTextureListener(int i) {
            this.mPosition = i;
        }

        @Override // android.view.TextureView.SurfaceTextureListener
        public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i2) {
            Log.m31d("EffectView", "onSurfacetTextureAvailable(), surface:" + surfaceTexture + ", width:" + i + ", height:" + i2 + ", mPosition:" + this.mPosition);
            EffectView.this.mSurfaceList[this.mPosition] = new Surface(surfaceTexture);
            if (EffectView.this.mListener != null) {
                EffectView.this.mListener.onSurfaceAvailable(EffectView.this.mSurfaceList[this.mPosition], i, i2, this.mPosition);
            }
        }

        @Override // android.view.TextureView.SurfaceTextureListener
        public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
        }

        @Override // android.view.TextureView.SurfaceTextureListener
        public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i2) {
            Log.m31d("EffectView", "onSurfaceTextureUpdated(), surface:" + surfaceTexture + ", width:" + i + ", height:" + i2);
        }

        @Override // android.view.TextureView.SurfaceTextureListener
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
            Log.m31d("EffectView", "onSurfaceTextureDestroyed(), surface:" + surfaceTexture + "and mPosition:" + this.mPosition);
            EffectView.this.mSurfaceList[this.mPosition] = null;
            return true;
        }
    }
}
