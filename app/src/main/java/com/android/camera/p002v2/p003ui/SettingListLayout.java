package com.android.camera.p002v2.p003ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import com.android.camera.p002v2.p003ui.InLineSettingItem;
import com.android.camera.p002v2.uimanager.preference.ListPreference;
import com.android.camera.p002v2.util.CameraUtil;
import com.mediatek.camera.R;
import com.mediatek.camera.debug.LogHelper;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class SettingListLayout extends FrameLayout implements InLineSettingItem.Listener, AdapterView.OnItemClickListener, AbsListView.OnScrollListener {
    private static final LogHelper.Tag TAG = new LogHelper.Tag(SettingListLayout.class.getSimpleName());
    private InLineSettingItem mLastItem;
    private ArrayList<ListPreference> mListItem;
    private ArrayAdapter<ListPreference> mListItemAdapter;
    private Listener mListener;
    private ViewGroup mRootView;
    private ListView mSettingList;

    public interface Listener {
        void onRestoreSetting();

        void onSettingChanged(SettingListLayout settingListLayout, ListPreference listPreference);
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mSettingList = (ListView) findViewById(R.id.settingList);
    }

    public void setRootView(ViewGroup viewGroup) {
        this.mRootView = viewGroup;
    }

    private class SettingsListAdapter extends ArrayAdapter<ListPreference> {
        LayoutInflater mInflater;

        public SettingsListAdapter() {
            super(SettingListLayout.this.getContext(), 0, SettingListLayout.this.mListItem);
            this.mInflater = LayoutInflater.from(getContext());
        }

        private int getSettingLayoutId(ListPreference listPreference) {
            if (listPreference == null) {
                return R.layout.in_line_setting_restore_v2;
            }
            if (SettingListLayout.this.isSwitchSettingItem(listPreference)) {
                return R.layout.in_line_setting_switch_v2;
            }
            if (SettingListLayout.this.isVirtualSettingItem(listPreference)) {
                return R.layout.in_line_setting_virtual_v2;
            }
            if (SettingListLayout.this.isSwitchVirtualItem(listPreference)) {
                return R.layout.in_line_setting_switch_virtual_v2;
            }
            return R.layout.in_line_setting_sublist_v2;
        }

        @Override // android.widget.ArrayAdapter, android.widget.Adapter
        public View getView(int i, View view, ViewGroup viewGroup) {
            View view2 = null;
            ListPreference listPreference = (ListPreference) SettingListLayout.this.mListItem.get(i);
            if (view != null) {
                if (listPreference != null ? !SettingListLayout.this.isSwitchSettingItem(listPreference) ? !SettingListLayout.this.isVirtualSettingItem(listPreference) ? !SettingListLayout.this.isSwitchVirtualItem(listPreference) ? (view instanceof InLineSettingSublist) : (view instanceof InLineSettingSwitchVirtual) : (view instanceof InLineSettingVirtual) : (view instanceof InLineSettingSwitch) : (view instanceof InLineSettingRestore)) {
                    view2 = view;
                }
                if (view2 != null) {
                    ((InLineSettingItem) view2).initialize(listPreference);
                    CameraUtil.setEnabledState(view2, listPreference == null ? true : listPreference.isEnabled());
                    return view2;
                }
            } else {
                view2 = view;
            }
            int settingLayoutId = getSettingLayoutId(listPreference);
            InLineSettingItem inLineSettingItem = (InLineSettingItem) this.mInflater.inflate(settingLayoutId, viewGroup, false);
            if (settingLayoutId == R.layout.in_line_setting_restore) {
                inLineSettingItem.setId(R.id.restore_default);
            }
            inLineSettingItem.setRootView(SettingListLayout.this.mRootView);
            inLineSettingItem.initialize(listPreference);
            inLineSettingItem.setSettingChangedListener(SettingListLayout.this);
            CameraUtil.setEnabledState(view2, listPreference != null ? listPreference.isEnabled() : true);
            return inLineSettingItem;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isSwitchSettingItem(ListPreference listPreference) {
        if ("pref_camera_recordlocation_key".equals(listPreference.getKey()) || "pref_camera_recordaudio_key".equals(listPreference.getKey()) || "pref_video_eis_key".equals(listPreference.getKey()) || "pref_video_3dnr_key".equals(listPreference.getKey()) || "pref_camera_zsd_key".equals(listPreference.getKey()) || "pref_voice_key".equals(listPreference.getKey()) || "pref_face_detect_key".equals(listPreference.getKey()) || "pref_hdr_key".equals(listPreference.getKey()) || "pref_slow_motion_key".equals(listPreference.getKey()) || "perf_camera_ais_key".equals(listPreference.getKey()) || "pref_asd_key".equals(listPreference.getKey())) {
            return true;
        }
        return "pref_dng_key".equals(listPreference.getKey());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isVirtualSettingItem(ListPreference listPreference) {
        return "pref_camera_image_properties_key".equals(listPreference.getKey());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isSwitchVirtualItem(ListPreference listPreference) {
        return "pref_dual_camera_key".equals(listPreference.getKey());
    }

    public void setSettingChangedListener(Listener listener) {
        this.mListener = listener;
    }

    public SettingListLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mListItem = new ArrayList<>();
    }

    public void initialize(ArrayList<ListPreference> arrayList) {
        this.mListItem = arrayList;
        this.mListItemAdapter = new SettingsListAdapter();
        this.mSettingList.setAdapter((ListAdapter) this.mListItemAdapter);
        this.mSettingList.setOnItemClickListener(this);
        this.mSettingList.setSelector(android.R.color.transparent);
        this.mSettingList.setOnScrollListener(this);
    }

    @Override // com.android.camera.v2.ui.InLineSettingItem.Listener
    public void onSettingChanged(InLineSettingItem inLineSettingItem, ListPreference listPreference) {
        if (this.mLastItem != null && this.mLastItem != inLineSettingItem) {
            this.mLastItem.collapseChild();
        }
        if (this.mListener != null) {
            listPreference.setOverrideValue(null, false);
            this.mListener.onSettingChanged(this, listPreference);
        }
    }

    @Override // android.widget.AdapterView.OnItemClickListener
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
        if (i == this.mListItem.size() - 1 && this.mListener != null) {
            this.mListener.onRestoreSetting();
        }
    }

    public void reloadPreference() {
        int childCount = this.mSettingList.getChildCount();
        for (int i = 0; i < childCount; i++) {
            if (this.mListItem.get(i) != null) {
                ((InLineSettingItem) this.mSettingList.getChildAt(i)).reloadPreference();
            }
        }
    }

    @Override // com.android.camera.v2.ui.InLineSettingItem.Listener
    public void onDismiss(InLineSettingItem inLineSettingItem) {
        LogHelper.m27v(TAG, "onDismiss(" + inLineSettingItem + ") mLastItem=" + this.mLastItem);
        this.mLastItem = null;
    }

    @Override // com.android.camera.v2.ui.InLineSettingItem.Listener
    public void onShow(InLineSettingItem inLineSettingItem) {
        LogHelper.m23d(TAG, "onShow(" + inLineSettingItem + ") mLastItem=" + this.mLastItem);
        if (this.mLastItem != null && this.mLastItem != inLineSettingItem) {
            this.mLastItem.collapseChild();
        }
        this.mLastItem = inLineSettingItem;
    }

    public boolean collapseChild() {
        boolean zCollapseChild = false;
        if (this.mLastItem != null) {
            zCollapseChild = this.mLastItem.collapseChild();
        }
        LogHelper.m23d(TAG, "collapseChild() return " + zCollapseChild);
        return zCollapseChild;
    }

    @Override // android.widget.AbsListView.OnScrollListener
    public void onScroll(AbsListView absListView, int i, int i2, int i3) {
        LogHelper.m23d(TAG, "onScroll(" + i + ", " + i2 + ", " + i3 + ")");
    }

    @Override // android.widget.AbsListView.OnScrollListener
    public void onScrollStateChanged(AbsListView absListView, int i) {
        LogHelper.m23d(TAG, "onScrollStateChanged(" + i + ")");
        if (i == 1) {
            collapseChild();
        }
    }
}
