package com.android.camera.p001ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListAdapter;
import com.android.camera.CameraActivity;
import com.android.camera.Log;
import com.android.camera.SettingUtils;
import com.mediatek.camera.R;
import com.mediatek.camera.setting.preference.ListPreference;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class SubSettingLayout extends SettingListLayout {
    private ArrayList<ListPreference> mListItem;
    private ArrayAdapter<ListPreference> mListItemAdapter;
    private GridView mSubSettingList;

    @Override // com.android.camera.p001ui.SettingListLayout, android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mSubSettingList = (GridView) findViewById(R.id.settingSubList);
    }

    private class SettingsGridAdapter extends ArrayAdapter<ListPreference> {
        LayoutInflater mInflater;

        public SettingsGridAdapter() {
            super(SubSettingLayout.this.getContext(), 0, SubSettingLayout.this.mListItem);
            this.mInflater = LayoutInflater.from(getContext());
        }

        private int getSettingLayoutId(ListPreference listPreference) {
            return R.layout.in_line_sub_setting_sublist;
        }

        @Override // android.widget.ArrayAdapter, android.widget.Adapter
        public View getView(int i, View view, ViewGroup viewGroup) {
            View view2;
            ListPreference listPreference = (ListPreference) SubSettingLayout.this.mListItem.get(i);
            Log.m5d("SettingGridLayout", "getview pos = " + i + listPreference);
            if (view != null) {
                view2 = (listPreference != null || (view instanceof InLineSubSettingSublist)) ? view : null;
                if (view2 != null) {
                    ((InLineSettingItem) view2).initialize(listPreference);
                    SettingUtils.setEnabledState(view2, listPreference == null ? true : listPreference.isEnabled());
                    return view2;
                }
            } else {
                view2 = view;
            }
            InLineSettingItem inLineSettingItem = (InLineSettingItem) this.mInflater.inflate(getSettingLayoutId(listPreference), viewGroup, false);
            inLineSettingItem.initialize(listPreference);
            inLineSettingItem.setSettingChangedListener(SubSettingLayout.this);
            SettingUtils.setEnabledState(view2, listPreference != null ? listPreference.isEnabled() : true);
            return inLineSettingItem;
        }
    }

    public SubSettingLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mListItem = new ArrayList<>();
    }

    public void initialize(String[] strArr, boolean z) {
        CameraActivity cameraActivity = (CameraActivity) getContext();
        this.mListItem.clear();
        for (String str : strArr) {
            ListPreference listPreference = cameraActivity.getListPreference(str);
            if (listPreference != null) {
                this.mListItem.add(listPreference);
            }
        }
        this.mListItemAdapter = new SettingsGridAdapter();
        this.mSubSettingList.setAdapter((ListAdapter) this.mListItemAdapter);
        this.mSubSettingList.setOnItemClickListener(this);
        this.mSubSettingList.setSelector(android.R.color.transparent);
        this.mSubSettingList.setOnScrollListener(this);
    }

    @Override // com.android.camera.p001ui.SettingListLayout
    public void reloadPreference() {
        int childCount = this.mSubSettingList.getChildCount();
        for (int i = 0; i < childCount; i++) {
            if (this.mListItem.get(i) != null) {
                ((InLineSettingItem) this.mSubSettingList.getChildAt(i)).reloadPreference();
            }
        }
    }
}
