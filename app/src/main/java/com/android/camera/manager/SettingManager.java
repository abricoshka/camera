package com.android.camera.manager;

import android.content.res.Resources;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TabHost;
import com.android.camera.CameraActivity;
import com.android.camera.FeatureSwitcher;
import com.android.camera.Log;
import com.android.camera.Util;
import com.android.camera.p001ui.RotateImageView;
import com.android.camera.p001ui.SettingListLayout;
import com.mediatek.camera.ISettingCtrl;
import com.mediatek.camera.R;
import com.mediatek.camera.platform.ICameraAppUi;
import com.mediatek.camera.setting.SettingConstants;
import com.mediatek.camera.setting.SettingUtils;
import com.mediatek.camera.setting.preference.CameraPreference;
import com.mediatek.camera.setting.preference.ListPreference;
import com.mediatek.camera.setting.preference.PreferenceGroup;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class SettingManager extends ViewManager implements View.OnClickListener, SettingListLayout.Listener, CameraActivity.OnPreferenceReadyListener, TabHost.OnTabChangeListener {
    private MyPagerAdapter mAdapter;
    private boolean mCancleHideAnimation;
    private Animation mFadeIn;
    private Animation mFadeOut;
    protected RotateImageView mIndicator;
    private boolean mIsStereoFeatureSwitch;
    protected SettingListener mListener;
    protected Handler mMainHandler;
    protected View.OnClickListener mOnClickListener;
    private ViewPager mPager;
    private ListPreference mPreference;
    private boolean mPreferenceReady;
    private View mSettingBack;
    protected ISettingCtrl mSettingController;
    protected ViewGroup mSettingLayout;
    protected boolean mShowingContainer;
    private TabHost mTabHost;

    public interface SettingListener {
        void onRestorePreferencesClicked();

        void onSettingContainerShowing(boolean z);

        void onSharedPreferenceChanged(ListPreference listPreference);

        void onStereoCameraPreferenceChanged(ListPreference listPreference, int i);
    }

    public SettingManager(CameraActivity cameraActivity) {
        super(cameraActivity);
        this.mCancleHideAnimation = false;
        this.mOnClickListener = new View.OnClickListener() { // from class: com.android.camera.manager.SettingManager.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                SettingManager.this.collapse(true);
            }
        };
        this.mMainHandler = new Handler() { // from class: com.android.camera.manager.SettingManager.2
            @Override // android.os.Handler
            public void handleMessage(Message message) {
                Log.m5d("SettingManager", "handleMessage(" + message + ")");
                switch (message.what) {
                    case 0:
                        if (SettingManager.this.mSettingLayout != null && SettingManager.this.mSettingLayout.getParent() != null) {
                            SettingManager.this.getContext().removeView(SettingManager.this.mSettingLayout, 3);
                            break;
                        }
                        break;
                }
            }
        };
        cameraActivity.addOnPreferenceReadyListener(this);
    }

    @Override // com.android.camera.manager.ViewManager
    protected View getView() {
        View viewInflate = inflate(R.layout.setting_indicator);
        this.mIndicator = (RotateImageView) viewInflate.findViewById(R.id.setting_indicator);
        this.mIndicator.setOnClickListener(this);
        return viewInflate;
    }

    @Override // com.android.camera.manager.ViewManager
    public void onRefresh() throws Resources.NotFoundException, NumberFormatException {
        Log.m5d("SettingManager", "onRefresh() isShowing()=" + isShowing() + ", mShowingContainer=" + this.mShowingContainer);
        if (this.mShowingContainer && this.mAdapter != null) {
            this.mAdapter.notifyDataSetChanged();
        }
        updateFakeNewPictureSizes();
    }

    @Override // com.android.camera.manager.ViewManager
    public void show() {
        super.show();
        this.mIndicator.setVisibility(8);
    }

    @Override // com.android.camera.manager.ViewManager
    public void hide() {
        collapse(true);
        super.hide();
    }

    @Override // com.android.camera.manager.ViewManager
    protected void onRelease() {
        super.onRelease();
        releaseSettingResource();
    }

    @Override // com.android.camera.manager.ViewManager
    public boolean collapse(boolean z) {
        boolean z2 = false;
        if (this.mShowingContainer && this.mAdapter != null) {
            if (!this.mAdapter.collapse(z)) {
                hideSetting();
            }
            z2 = true;
        }
        Log.m5d("SettingManager", "collapse(" + z + ") mShowingContainer=" + this.mShowingContainer + ", return " + z2);
        return z2;
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) throws Resources.NotFoundException {
        if (this.mPreferenceReady && view == this.mIndicator) {
            if (!this.mShowingContainer) {
                showSetting();
            } else {
                collapse(true);
            }
        }
    }

    @Override // com.android.camera.manager.ViewManager, com.android.camera.CameraActivity.OnOrientationListener
    public void onOrientationChanged(int i) {
        super.onOrientationChanged(i);
        Util.setOrientation(this.mSettingLayout, i, true);
    }

    public void superOrientationChanged(int i) {
        super.onOrientationChanged(i);
    }

    @Override // com.android.camera.ui.SettingListLayout.Listener
    public void onRestorePreferencesClicked() {
        Log.m5d("SettingManager", "onRestorePreferencesClicked() mShowingContainer=" + this.mShowingContainer);
        if (this.mListener != null && this.mShowingContainer) {
            this.mListener.onRestorePreferencesClicked();
        }
    }

    @Override // com.android.camera.ui.SettingListLayout.Listener
    public void onSettingChanged(SettingListLayout settingListLayout, ListPreference listPreference) {
        Log.m5d("SettingManager", "onSettingChanged(" + settingListLayout + ")");
        if (this.mListener != null) {
            this.mListener.onSharedPreferenceChanged(listPreference);
            this.mPreference = listPreference;
        }
        refresh();
    }

    @Override // com.android.camera.ui.SettingListLayout.Listener
    public void onStereoCameraSettingChanged(SettingListLayout settingListLayout, ListPreference listPreference, int i, boolean z) throws Resources.NotFoundException {
        Log.m5d("SettingManager", "onStereo3dSettingChanged(" + settingListLayout + "), type = " + i);
        if (this.mListener != null) {
            this.mIsStereoFeatureSwitch = true;
            this.mListener.onStereoCameraPreferenceChanged(listPreference, i);
            this.mPreference = listPreference;
        }
        if (getContext().getCurrentMode() == 6 || (getContext().getCurrentMode() != 6 && i == 2)) {
            refresh();
            return;
        }
        if (this.mShowingContainer && this.mAdapter != null && !this.mAdapter.collapse(true)) {
            if (this.mShowingContainer && this.mSettingLayout != null) {
                this.mMainHandler.removeMessages(0);
                this.mSettingLayout.setVisibility(8);
                getContext().getCameraAppUI().restoreViewState();
                this.mMainHandler.sendEmptyMessageDelayed(0, 3000L);
            }
            setChildrenClickable(false);
        }
        if (getContext().isFullScreen()) {
            this.mMainHandler.removeMessages(0);
            initializeSettings();
            refresh();
            highlightCurrentSetting(this.mPager.getCurrentItem());
            this.mSettingLayout.setVisibility(0);
            if (this.mSettingLayout.getParent() == null) {
                getContext().addView(this.mSettingLayout, 3);
            }
            getContext().getCameraAppUI().setViewState(ICameraAppUi.ViewState.VIEW_STATE_SETTING);
            setChildrenClickable(true);
        }
    }

    @Override // com.android.camera.CameraActivity.OnPreferenceReadyListener
    public void onPreferenceReady() {
        releaseSettingResource();
        this.mPreferenceReady = true;
    }

    @Override // android.widget.TabHost.OnTabChangeListener
    public void onTabChanged(String str) throws Resources.NotFoundException {
        int currentTab = -1;
        if (this.mTabHost != null && this.mPager != null) {
            currentTab = this.mTabHost.getCurrentTab();
            this.mPager.setCurrentItem(currentTab);
        }
        Log.m5d("SettingManager", "onTabChanged(" + str + ") currentIndex=" + currentTab);
    }

    public void setListener(SettingListener settingListener) {
        this.mListener = settingListener;
    }

    public void setSettingController(ISettingCtrl iSettingCtrl) {
        this.mSettingController = iSettingCtrl;
    }

    public boolean handleMenuEvent() {
        boolean z = false;
        if (isEnabled() && isShowing() && this.mIndicator != null) {
            this.mIndicator.performClick();
            z = true;
        }
        Log.m5d("SettingManager", "handleMenuEvent() isEnabled()=" + isEnabled() + ", isShowing()=" + isShowing() + ", mIndicator=" + this.mIndicator + ", return " + z);
        return z;
    }

    protected void releaseSettingResource() {
        Log.m8i("SettingManager", "releaseSettingResource()");
        if (this.mIsStereoFeatureSwitch) {
            this.mIsStereoFeatureSwitch = false;
            Log.m5d("SettingManager", "releaseSettingResource is stereo feature, no need release");
            return;
        }
        collapse(true);
        if (this.mSettingLayout != null) {
            this.mAdapter = null;
            this.mPager = null;
            this.mSettingLayout = null;
        }
    }

    public void showSetting() throws Resources.NotFoundException {
        Log.m5d("SettingManager", "showSetting() mShowingContainer=" + this.mShowingContainer + ", getContext().isFullScreen()=" + getContext().isFullScreen());
        if (getContext().isFullScreen()) {
            if (!this.mShowingContainer && this.mSettingController.isSettingsInitialized() && getContext().getCameraAppUI().isNormalViewState()) {
                this.mMainHandler.removeMessages(0);
                this.mShowingContainer = true;
                this.mListener.onSettingContainerShowing(this.mShowingContainer);
                initializeSettings();
                refresh();
                highlightCurrentSetting(this.mPager.getCurrentItem());
                this.mSettingLayout.setVisibility(0);
                if (this.mSettingLayout.getParent() == null) {
                    getContext().addView(this.mSettingLayout, 3);
                }
                getContext().getCameraAppUI().setViewState(ICameraAppUi.ViewState.VIEW_STATE_SETTING);
                startFadeInAnimation(this.mSettingLayout);
            }
            setChildrenClickable(true);
        }
    }

    public void resetSettings() {
        if (this.mSettingLayout != null && this.mSettingLayout.getParent() != null) {
            getContext().removeView(this.mSettingLayout, 3);
        }
        this.mSettingLayout = null;
    }

    private void initializeSettings() throws Resources.NotFoundException {
        if (this.mSettingLayout == null && this.mSettingController.getPreferenceGroup() != null) {
            this.mSettingLayout = (ViewGroup) getContext().inflate(R.layout.setting_container, 3);
            this.mTabHost = (TabHost) this.mSettingLayout.findViewById(R.id.tab_title);
            this.mTabHost.setup();
            this.mSettingBack = this.mSettingLayout.findViewById(R.id.setting_back);
            this.mSettingBack.setOnClickListener(this.mOnClickListener);
            int[] iArr = SettingConstants.SETTING_GROUP_COMMON_FOR_TAB;
            if (FeatureSwitcher.isSubSettingEnabled()) {
                iArr = SettingConstants.SETTING_GROUP_MAIN_COMMON_FOR_TAB;
            } else if (FeatureSwitcher.isLomoEffectEnabled() && getContext().isNonePickIntent()) {
                iArr = SettingConstants.SETTING_GROUP_COMMON_FOR_LOMOEFFECT;
            }
            ArrayList arrayList = new ArrayList();
            if (getContext().isNonePickIntent() || getContext().isStereoMode()) {
                if (FeatureSwitcher.isPrioritizePreviewSize()) {
                    int[] iArr2 = new int[SettingConstants.SETTING_GROUP_COMMON_FOR_TAB_PREVIEW.length + iArr.length + SettingConstants.SETTING_GROUP_CAMERA_FOR_TAB_NO_PREVIEW.length + SettingConstants.SETTING_GROUP_VIDEO_FOR_TAB_NO_PREVIEW.length];
                    System.arraycopy(SettingConstants.SETTING_GROUP_COMMON_FOR_TAB_PREVIEW, 0, iArr2, 0, SettingConstants.SETTING_GROUP_COMMON_FOR_TAB_PREVIEW.length);
                    System.arraycopy(iArr, 0, iArr2, SettingConstants.SETTING_GROUP_COMMON_FOR_TAB_PREVIEW.length, iArr.length);
                    System.arraycopy(SettingConstants.SETTING_GROUP_CAMERA_FOR_TAB_NO_PREVIEW, 0, iArr2, SettingConstants.SETTING_GROUP_COMMON_FOR_TAB_PREVIEW.length + iArr.length, SettingConstants.SETTING_GROUP_CAMERA_FOR_TAB_NO_PREVIEW.length);
                    System.arraycopy(SettingConstants.SETTING_GROUP_VIDEO_FOR_TAB_NO_PREVIEW, 0, iArr2, iArr.length + SettingConstants.SETTING_GROUP_COMMON_FOR_TAB_PREVIEW.length + SettingConstants.SETTING_GROUP_CAMERA_FOR_TAB_NO_PREVIEW.length, SettingConstants.SETTING_GROUP_VIDEO_FOR_TAB_NO_PREVIEW.length);
                    arrayList.add(new Holder("common", R.drawable.ic_tab_common_setting, iArr2));
                } else if (getContext().isStereoMode()) {
                    int[] iArr3 = new int[iArr.length + SettingConstants.SETTING_GROUP_CAMERA_3D_FOR_TAB.length + SettingConstants.SETTING_GROUP_VIDEO_FOR_TAB.length];
                    System.arraycopy(SettingConstants.SETTING_GROUP_CAMERA_3D_FOR_TAB, 0, iArr3, 0, SettingConstants.SETTING_GROUP_CAMERA_3D_FOR_TAB.length);
                    System.arraycopy(iArr, 0, iArr3, SettingConstants.SETTING_GROUP_CAMERA_3D_FOR_TAB.length, iArr.length);
                    System.arraycopy(SettingConstants.SETTING_GROUP_VIDEO_FOR_TAB, 0, iArr3, iArr.length + SettingConstants.SETTING_GROUP_CAMERA_3D_FOR_TAB.length, SettingConstants.SETTING_GROUP_VIDEO_FOR_TAB.length);
                    arrayList.add(new Holder("common", R.drawable.ic_tab_common_setting, iArr3));
                } else {
                    int[] iArr4 = new int[iArr.length + SettingConstants.SETTING_GROUP_CAMERA_FOR_TAB.length + SettingConstants.SETTING_GROUP_VIDEO_FOR_TAB.length];
                    System.arraycopy(SettingConstants.SETTING_GROUP_CAMERA_FOR_TAB, 0, iArr4, 0, SettingConstants.SETTING_GROUP_CAMERA_FOR_TAB.length);
                    System.arraycopy(iArr, 0, iArr4, SettingConstants.SETTING_GROUP_CAMERA_FOR_TAB.length, iArr.length);
                    System.arraycopy(SettingConstants.SETTING_GROUP_VIDEO_FOR_TAB, 0, iArr4, iArr.length + SettingConstants.SETTING_GROUP_CAMERA_FOR_TAB.length, SettingConstants.SETTING_GROUP_VIDEO_FOR_TAB.length);
                    arrayList.add(new Holder("common", R.drawable.ic_tab_camera_setting, iArr4));
                }
            } else if (FeatureSwitcher.isPrioritizePreviewSize()) {
                if (getContext().isImageCaptureIntent()) {
                    int[] iArr5 = new int[iArr.length + SettingConstants.SETTING_GROUP_COMMON_FOR_TAB_PREVIEW.length + SettingConstants.SETTING_GROUP_CAMERA_FOR_TAB_NO_PREVIEW.length];
                    System.arraycopy(SettingConstants.SETTING_GROUP_COMMON_FOR_TAB_PREVIEW, 0, iArr5, 0, SettingConstants.SETTING_GROUP_COMMON_FOR_TAB_PREVIEW.length);
                    System.arraycopy(iArr, 0, iArr5, SettingConstants.SETTING_GROUP_COMMON_FOR_TAB_PREVIEW.length, iArr.length);
                    System.arraycopy(SettingConstants.SETTING_GROUP_CAMERA_FOR_TAB_NO_PREVIEW, 0, iArr5, iArr.length + SettingConstants.SETTING_GROUP_COMMON_FOR_TAB_PREVIEW.length, SettingConstants.SETTING_GROUP_CAMERA_FOR_TAB_NO_PREVIEW.length);
                    arrayList.add(new Holder("common", R.drawable.ic_tab_common_setting, iArr5));
                } else {
                    int[] iArr6 = new int[iArr.length + SettingConstants.SETTING_GROUP_VIDEO_FOR_TAB_NO_PREVIEW.length];
                    System.arraycopy(SettingConstants.SETTING_GROUP_VIDEO_FOR_TAB_NO_PREVIEW, 0, iArr6, 0, SettingConstants.SETTING_GROUP_VIDEO_FOR_TAB_NO_PREVIEW.length);
                    System.arraycopy(iArr, 0, iArr6, SettingConstants.SETTING_GROUP_VIDEO_FOR_TAB_NO_PREVIEW.length, iArr.length);
                    arrayList.add(new Holder("common", R.drawable.ic_tab_common_setting, iArr6));
                }
            } else if (getContext().isImageCaptureIntent()) {
                int[] iArr7 = new int[iArr.length + SettingConstants.SETTING_GROUP_CAMERA_FOR_TAB.length];
                System.arraycopy(SettingConstants.SETTING_GROUP_CAMERA_FOR_TAB, 0, iArr7, 0, SettingConstants.SETTING_GROUP_CAMERA_FOR_TAB.length);
                System.arraycopy(iArr, 0, iArr7, SettingConstants.SETTING_GROUP_CAMERA_FOR_TAB.length, iArr.length);
                arrayList.add(new Holder("common", R.drawable.ic_tab_common_setting, iArr7));
            } else {
                int[] iArr8 = new int[iArr.length + SettingConstants.SETTING_GROUP_VIDEO_FOR_TAB_NO_PREVIEW.length];
                System.arraycopy(SettingConstants.SETTING_GROUP_VIDEO_FOR_TAB_NO_PREVIEW, 0, iArr8, 0, SettingConstants.SETTING_GROUP_VIDEO_FOR_TAB_NO_PREVIEW.length);
                System.arraycopy(iArr, 0, iArr8, SettingConstants.SETTING_GROUP_VIDEO_FOR_TAB_NO_PREVIEW.length, iArr.length);
                arrayList.add(new Holder("common", R.drawable.ic_tab_common_setting, iArr8));
            }
            int size = arrayList.size();
            ArrayList arrayList2 = new ArrayList();
            int i = 0;
            while (i < size) {
                Holder holder = (Holder) arrayList.get(i);
                SettingListLayout settingListLayout = (SettingListLayout) getContext().inflate(R.layout.setting_list_layout, 3);
                new ArrayList();
                settingListLayout.initialize(getListPreferences(holder.mSettingKeys, i == 0));
                arrayList2.add(settingListLayout);
                ImageView imageView = new ImageView(getContext());
                if (imageView != null) {
                    imageView.setBackgroundResource(R.drawable.bg_tab_title);
                    imageView.setImageResource(holder.mIndicatorIconRes);
                    imageView.setScaleType(ImageView.ScaleType.CENTER);
                }
                this.mTabHost.addTab(this.mTabHost.newTabSpec(holder.mIndicatorKey).setIndicator(imageView).setContent(android.R.id.tabcontent));
                i++;
            }
            this.mAdapter = new MyPagerAdapter(arrayList2);
            this.mPager = (ViewPager) this.mSettingLayout.findViewById(R.id.pager);
            this.mPager.setAdapter(this.mAdapter);
            this.mPager.setOnPageChangeListener(this.mAdapter);
            this.mTabHost.setOnTabChangedListener(this);
        }
        Util.setOrientation(this.mSettingLayout, getOrientation(), false);
    }

    private ArrayList<ListPreference> getListPreferences(int[] iArr, boolean z) {
        ArrayList<ListPreference> arrayList = new ArrayList<>();
        for (int i : iArr) {
            ListPreference listPreference = this.mSettingController.getListPreference(SettingConstants.getSettingKey(i));
            if (listPreference != null && listPreference.isVisibled() && listPreference.isShowInSetting()) {
                arrayList.add(listPreference);
            }
        }
        if ("on".equals(this.mSettingController.getSettingValue("pref_slow_motion_key")) || "on".equals(this.mSettingController.getSettingValue("refocus_key"))) {
            arrayList.remove(this.mSettingController.getListPreference("pref_video_quality_key"));
        }
        if (!"on".equals(this.mSettingController.getSettingValue("refocus_key"))) {
            arrayList.remove(this.mSettingController.getListPreference("pref_refocus_video_quality_key"));
        }
        if (z) {
            arrayList.add(null);
        }
        return arrayList;
    }

    public void cancleHideAnimation() {
        this.mCancleHideAnimation = true;
    }

    public void hideSetting() {
        Log.m5d("SettingManager", "hideSetting() mShowingContainer=" + this.mShowingContainer + ", mSettingLayout=" + this.mSettingLayout);
        setChildrenClickable(false);
        if (this.mShowingContainer && this.mSettingLayout != null) {
            this.mMainHandler.removeMessages(0);
            if (!this.mCancleHideAnimation) {
                startFadeOutAnimation(this.mSettingLayout);
            }
            this.mSettingLayout.setVisibility(8);
            this.mShowingContainer = false;
            getContext().getCameraAppUI().getCameraView(ICameraAppUi.CommonUiType.MODE_PICKER).setEnabled(true);
            getContext().getCameraAppUI().restoreViewState();
            this.mListener.onSettingContainerShowing(this.mShowingContainer);
            this.mMainHandler.sendEmptyMessageDelayed(0, 3000L);
        }
        this.mCancleHideAnimation = false;
    }

    protected void setChildrenClickable(boolean z) {
        Log.m5d("SettingManager", "setChildrenClickable(" + z + ") ");
        PreferenceGroup preferenceGroup = this.mSettingController.getPreferenceGroup();
        if (preferenceGroup != null) {
            int size = preferenceGroup.size();
            for (int i = 0; i < size; i++) {
                CameraPreference cameraPreference = preferenceGroup.get(i);
                if (cameraPreference instanceof ListPreference) {
                    ((ListPreference) cameraPreference).setClickable(z);
                }
            }
        }
    }

    protected void startFadeInAnimation(View view) {
        if (this.mFadeIn == null) {
            this.mFadeIn = AnimationUtils.loadAnimation(getContext(), R.anim.setting_popup_grow_fade_in);
        }
        if (view != null && this.mFadeIn != null) {
            view.startAnimation(this.mFadeIn);
        }
    }

    protected void startFadeOutAnimation(View view) {
        if (this.mFadeOut == null) {
            this.mFadeOut = AnimationUtils.loadAnimation(getContext(), R.anim.setting_popup_shrink_fade_out);
        }
        if (view != null && this.mFadeOut != null) {
            view.startAnimation(this.mFadeOut);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void highlightCurrentSetting(int i) {
        if (this.mTabHost != null) {
            this.mTabHost.setCurrentTab(i);
        }
    }

    private class Holder {
        int mIndicatorIconRes;
        String mIndicatorKey;
        int[] mSettingKeys;

        public Holder(String str, int i, int[] iArr) {
            this.mIndicatorKey = str;
            this.mIndicatorIconRes = i;
            this.mSettingKeys = iArr;
        }
    }

    public boolean isShowSettingContainer() {
        return this.mShowingContainer;
    }

    private class MyPagerAdapter extends PagerAdapter implements ViewPager.OnPageChangeListener {
        private final List<SettingListLayout> mPageViews;

        public MyPagerAdapter(List<SettingListLayout> list) {
            this.mPageViews = new ArrayList(list);
        }

        @Override // android.support.v4.view.PagerAdapter
        public void destroyItem(View view, int i, Object obj) {
            Log.m5d("SettingManager", "MyPagerAdapter.destroyItem(" + i + ")");
            ((ViewPager) view).removeView(this.mPageViews.get(i));
        }

        @Override // android.support.v4.view.PagerAdapter
        public void finishUpdate(View view) {
        }

        @Override // android.support.v4.view.PagerAdapter
        public int getCount() {
            return this.mPageViews.size();
        }

        @Override // android.support.v4.view.PagerAdapter
        public Object instantiateItem(View view, int i) {
            Log.m8i("SettingManager", "MyPagerAdapter.instantiateItem(" + i + ")");
            ((ViewPager) view).addView(this.mPageViews.get(i), 0);
            return this.mPageViews.get(i);
        }

        @Override // android.support.v4.view.PagerAdapter
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }

        @Override // android.support.v4.view.PagerAdapter
        public void restoreState(Parcelable parcelable, ClassLoader classLoader) {
        }

        @Override // android.support.v4.view.PagerAdapter
        public Parcelable saveState() {
            return null;
        }

        @Override // android.support.v4.view.PagerAdapter
        public void startUpdate(View view) {
        }

        @Override // android.support.v4.view.ViewPager.OnPageChangeListener
        public void onPageScrollStateChanged(int i) {
        }

        @Override // android.support.v4.view.ViewPager.OnPageChangeListener
        public void onPageScrolled(int i, float f, int i2) {
            if (SettingManager.this.mPreference != null) {
                String key = SettingManager.this.mPreference.getKey();
                if (key.equals("pref_camera_edge_key") || key.equals("pref_camera_hue_key") || key.equals("pref_camera_saturation_key") || key.equals("pref_camera_brightness_key") || key.equals("pref_camera_contrast_key")) {
                    return;
                }
            }
            SettingManager.this.mMainHandler.post(new Runnable() { // from class: com.android.camera.manager.SettingManager.MyPagerAdapter.1
                @Override // java.lang.Runnable
                public void run() {
                    MyPagerAdapter.this.collapse(true);
                }
            });
        }

        @Override // android.support.v4.view.ViewPager.OnPageChangeListener
        public void onPageSelected(int i) {
            SettingManager.this.highlightCurrentSetting(i);
            collapse(true);
        }

        @Override // android.support.v4.view.PagerAdapter
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
            for (SettingListLayout settingListLayout : this.mPageViews) {
                if (settingListLayout != null) {
                    settingListLayout.setSettingChangedListener(SettingManager.this);
                    settingListLayout.reloadPreference();
                }
            }
        }

        public boolean collapse(boolean z) {
            boolean z2;
            int size = this.mPageViews.size();
            int i = 0;
            while (true) {
                if (i < size) {
                    SettingListLayout settingListLayout = this.mPageViews.get(i);
                    if (settingListLayout == null || !settingListLayout.collapseChild() || !(!z)) {
                        i++;
                    } else {
                        z2 = true;
                        break;
                    }
                } else {
                    z2 = false;
                    break;
                }
            }
            Log.m5d("SettingManager", "MyPagerAdapter.collapse(" + z + ") return " + z2);
            return z2;
        }
    }

    private void updateFakeNewPictureSizes() throws Resources.NotFoundException, NumberFormatException {
        if (this.mShowingContainer) {
            SettingUtils.updateFakeNewPictureSizes(getContext(), getContext().getCameraId(), this.mSettingController.getListPreference("pref_camera_picturesize_key"));
        }
    }
}
