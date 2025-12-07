package com.android.camera.p002v2.uimanager;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Looper;
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
import com.android.camera.FeatureSwitcher;
import com.android.camera.p002v2.p003ui.RotateImageView;
import com.android.camera.p002v2.p003ui.SettingListLayout;
import com.android.camera.p002v2.uimanager.preference.ListPreference;
import com.android.camera.p002v2.uimanager.preference.PreferenceManager;
import com.android.camera.p002v2.util.CameraUtil;
import com.android.camera.p002v2.util.SettingKeys;
import com.mediatek.camera.R;
import com.mediatek.camera.debug.LogHelper;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class SettingManager extends AbstractUiManager implements View.OnClickListener, TabHost.OnTabChangeListener, SettingListLayout.Listener {
    private static final LogHelper.Tag TAG = new LogHelper.Tag(SettingManager.class.getSimpleName());
    private Activity mActivity;
    private MyPagerAdapter mAdapter;
    private Animation mFadeIn;
    private Animation mFadeOut;
    private RotateImageView mIndicator;
    private Intent mIntent;
    private MainHandler mMainHandler;
    private OnSettingStatusListener mOnSettingStatusListener;
    private ViewPager mPager;
    private ListPreference mPreference;
    private PreferenceManager mPreferenceManager;
    private OnSettingChangedListener mSettingChangedListener;
    private ViewGroup mSettingLayout;
    private ViewGroup mSettingViewLayer;
    private boolean mShowingContainer;
    private TabHost mTabHost;

    public interface OnSettingChangedListener {
        void onSettingChanged(String str, String str2);

        void onSettingRestored();
    }

    public interface OnSettingStatusListener {
        void onHidden();

        void onShown();
    }

    public SettingManager(Activity activity, ViewGroup viewGroup, PreferenceManager preferenceManager) {
        super(activity, viewGroup);
        this.mShowingContainer = false;
        this.mActivity = activity;
        this.mSettingViewLayer = viewGroup;
        this.mMainHandler = new MainHandler(activity.getMainLooper());
        this.mPreferenceManager = preferenceManager;
        this.mIntent = this.mActivity.getIntent();
    }

    @Override // com.android.camera.p002v2.uimanager.AbstractUiManager
    protected View getView() {
        View viewInflate = inflate(R.layout.setting_indicator_v2);
        this.mIndicator = (RotateImageView) viewInflate.findViewById(R.id.setting_indicator);
        this.mIndicator.setOnClickListener(this);
        return viewInflate;
    }

    @Override // com.android.camera.p002v2.uimanager.AbstractUiManager
    protected void onRefresh() {
        LogHelper.m26i(TAG, "onRefresh(), mShowingContainer=" + this.mShowingContainer);
        if (this.mShowingContainer && this.mAdapter != null) {
            this.mAdapter.notifyDataSetChanged();
        }
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) throws Resources.NotFoundException {
        if (view == this.mIndicator) {
            if (!this.mShowingContainer) {
                showSetting();
            } else {
                collapse(true);
            }
        }
    }

    @Override // android.widget.TabHost.OnTabChangeListener
    public void onTabChanged(String str) throws Resources.NotFoundException {
        int currentTab = -1;
        if (this.mTabHost != null && this.mPager != null) {
            currentTab = this.mTabHost.getCurrentTab();
            this.mPager.setCurrentItem(currentTab);
        }
        LogHelper.m26i(TAG, "onTabChanged(" + str + ") currentIndex=" + currentTab);
    }

    @Override // com.android.camera.v2.ui.SettingListLayout.Listener
    public void onSettingChanged(SettingListLayout settingListLayout, ListPreference listPreference) {
        if (this.mSettingChangedListener == null || listPreference == null) {
            return;
        }
        LogHelper.m26i(TAG, "[onSettingChanged], key:" + listPreference.getKey() + ", value:" + listPreference.getValue());
        this.mSettingChangedListener.onSettingChanged(listPreference.getKey(), listPreference.getValue());
    }

    @Override // com.android.camera.v2.ui.SettingListLayout.Listener
    public void onRestoreSetting() {
        LogHelper.m26i(TAG, "[onRestoreSetting]...");
        if (this.mSettingChangedListener != null) {
            this.mSettingChangedListener.onSettingRestored();
        }
    }

    public boolean onBackPressed() {
        LogHelper.m26i(TAG, "[onBackPressed]...");
        return collapse(false);
    }

    public void setSettingChangedListener(OnSettingChangedListener onSettingChangedListener) {
        this.mSettingChangedListener = onSettingChangedListener;
    }

    public void setSettingStatusListener(OnSettingStatusListener onSettingStatusListener) {
        this.mOnSettingStatusListener = onSettingStatusListener;
    }

    public boolean collapse(boolean z) {
        boolean z2 = false;
        if (this.mShowingContainer && this.mAdapter != null) {
            if (!this.mAdapter.collapse(z)) {
                hideSetting();
            }
            z2 = true;
        }
        LogHelper.m26i(TAG, "collapse(" + z + ") mShowingContainer=" + this.mShowingContainer + ", return " + z2);
        return z2;
    }

    public void collapseImmediately() {
        LogHelper.m26i(TAG, "[collapseImmediately]...");
        this.mMainHandler.removeMessages(0);
        if (this.mAdapter != null) {
            this.mAdapter.collapse(true);
        }
        if (this.mSettingLayout != null && this.mSettingViewLayer != null) {
            this.mSettingViewLayer.removeView(this.mSettingLayout);
        }
        this.mShowingContainer = false;
        this.mSettingLayout = null;
    }

    private void showSetting() throws Resources.NotFoundException {
        LogHelper.m26i(TAG, "showSetting() mShowingContainer=" + this.mShowingContainer);
        if (!this.mShowingContainer) {
            this.mMainHandler.removeMessages(0);
            this.mShowingContainer = true;
            initializeSettings();
            refresh();
            highlightCurrentSetting(this.mPager.getCurrentItem());
            this.mSettingLayout.setVisibility(0);
            if (this.mSettingLayout.getParent() == null) {
                this.mSettingViewLayer.addView(this.mSettingLayout);
            }
            if (this.mOnSettingStatusListener != null) {
                this.mOnSettingStatusListener.onShown();
            }
            startFadeInAnimation(this.mSettingLayout);
            this.mIndicator.setImageResource(R.drawable.ic_setting_focus);
        }
    }

    private void hideSetting() {
        LogHelper.m26i(TAG, "hideSetting() mShowingContainer=" + this.mShowingContainer + ", mSettingLayout=" + this.mSettingLayout);
        if (!this.mShowingContainer || this.mSettingLayout == null) {
            return;
        }
        this.mMainHandler.removeMessages(0);
        startFadeOutAnimation(this.mSettingLayout);
        this.mSettingLayout.setVisibility(8);
        this.mShowingContainer = false;
        if (this.mOnSettingStatusListener != null) {
            this.mOnSettingStatusListener.onHidden();
        }
        this.mIndicator.setImageResource(R.drawable.ic_setting_normal);
        this.mMainHandler.sendEmptyMessageDelayed(0, 3000L);
    }

    private void initializeSettings() throws Resources.NotFoundException {
        if (this.mSettingLayout == null) {
            this.mSettingLayout = (ViewGroup) this.mActivity.getLayoutInflater().inflate(R.layout.setting_container_v2, this.mSettingViewLayer, false);
            this.mTabHost = (TabHost) this.mSettingLayout.findViewById(R.id.tab_title);
            this.mTabHost.setup();
            String action = this.mIntent.getAction();
            LogHelper.m26i(TAG, "intent.action:" + action);
            ArrayList arrayList = new ArrayList();
            int[] iArr = SettingKeys.SETTING_GROUP_COMMON_FOR_TAB;
            int[] iArr2 = SettingKeys.SETTING_GROUP_CAMERA_FOR_TAB;
            int[] iArr3 = SettingKeys.SETTING_GROUP_VIDEO_FOR_TAB;
            if (FeatureSwitcher.isSubSettingEnabled()) {
                iArr = SettingKeys.SETTING_GROUP_MAIN_COMMON_FOR_TAB;
            } else if (FeatureSwitcher.isLomoEffectEnabled()) {
                iArr = SettingKeys.SETTING_GROUP_COMMON_FOR_LOMOEFFECT;
            }
            if ("android.media.action.IMAGE_CAPTURE".equals(action)) {
                iArr3 = null;
            }
            int[] iArr4 = "android.media.action.VIDEO_CAPTURE".equals(action) ? null : iArr2;
            if (iArr != null) {
                arrayList.add(new Holder("common", R.drawable.ic_tab_common_setting, iArr));
            }
            if (iArr4 != null) {
                arrayList.add(new Holder("camera", R.drawable.ic_tab_camera_setting, iArr4));
            }
            if (iArr3 != null) {
                arrayList.add(new Holder("video", R.drawable.ic_tab_video_setting, iArr3));
            }
            int size = arrayList.size();
            ArrayList arrayList2 = new ArrayList();
            int i = 0;
            while (i < size) {
                Holder holder = (Holder) arrayList.get(i);
                SettingListLayout settingListLayout = (SettingListLayout) this.mActivity.getLayoutInflater().inflate(R.layout.setting_list_layout_v2, this.mSettingViewLayer, false);
                new ArrayList();
                settingListLayout.setRootView(this.mSettingViewLayer);
                settingListLayout.initialize(getListPreferences(holder.mSettingKeys, i == 0));
                settingListLayout.setSettingChangedListener(this);
                arrayList2.add(settingListLayout);
                ImageView imageView = new ImageView(this.mActivity);
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
        CameraUtil.setOrientation(this.mSettingLayout, ((Integer) this.mSettingViewLayer.getTag()).intValue(), false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void highlightCurrentSetting(int i) {
        if (this.mTabHost != null) {
            this.mTabHost.setCurrentTab(i);
        }
    }

    private void startFadeInAnimation(View view) {
        if (this.mFadeIn == null) {
            this.mFadeIn = AnimationUtils.loadAnimation(this.mActivity, R.anim.setting_popup_grow_fade_in);
        }
        if (view != null && this.mFadeIn != null) {
            view.startAnimation(this.mFadeIn);
        }
    }

    private void startFadeOutAnimation(View view) {
        if (this.mFadeOut == null) {
            this.mFadeOut = AnimationUtils.loadAnimation(this.mActivity, R.anim.setting_popup_shrink_fade_out);
        }
        if (view != null && this.mFadeOut != null) {
            view.startAnimation(this.mFadeOut);
        }
    }

    private ArrayList<ListPreference> getListPreferences(int[] iArr, boolean z) {
        ArrayList<ListPreference> arrayList = new ArrayList<>();
        for (int i : iArr) {
            ListPreference listPreference = this.mPreferenceManager.getListPreference(SettingKeys.getSettingKey(i));
            if (listPreference != null && listPreference.isShowInSetting() && listPreference.isVisibled()) {
                arrayList.add(listPreference);
            }
        }
        if (z) {
            arrayList.add(null);
        }
        return arrayList;
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

    private class MainHandler extends Handler {
        public MainHandler(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            LogHelper.m26i(SettingManager.TAG, "msg:" + message);
            switch (message.what) {
                case 0:
                    if (SettingManager.this.mSettingLayout != null && SettingManager.this.mSettingViewLayer != null) {
                        SettingManager.this.mSettingViewLayer.removeView(SettingManager.this.mSettingLayout);
                    }
                    SettingManager.this.mSettingLayout = null;
                    break;
            }
        }
    }

    private class MyPagerAdapter extends PagerAdapter implements ViewPager.OnPageChangeListener {
        private final List<SettingListLayout> mPageViews;

        public MyPagerAdapter(List<SettingListLayout> list) {
            this.mPageViews = new ArrayList(list);
        }

        @Override // android.support.v4.view.PagerAdapter
        public void destroyItem(View view, int i, Object obj) {
            LogHelper.m26i(SettingManager.TAG, "MyPagerAdapter.destroyItem(" + i + ")");
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
            LogHelper.m26i(SettingManager.TAG, "MyPagerAdapter.instantiateItem(" + i + ")");
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
            SettingManager.this.mMainHandler.post(new Runnable() { // from class: com.android.camera.v2.uimanager.SettingManager.MyPagerAdapter.1
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
            LogHelper.m26i(SettingManager.TAG, "[notifyDataSetChanged]...");
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
            LogHelper.m26i(SettingManager.TAG, "MyPagerAdapter.collapse(" + z + ") return " + z2);
            return z2;
        }
    }
}
