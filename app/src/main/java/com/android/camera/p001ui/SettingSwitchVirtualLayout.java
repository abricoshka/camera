package com.android.camera.p001ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListAdapter;
import android.widget.TextView;
import com.android.camera.CameraActivity;
import com.android.camera.Log;
import com.android.camera.SettingUtils;
import com.mediatek.camera.R;
import com.mediatek.camera.setting.preference.ListPreference;

/* loaded from: classes.dex */
public class SettingSwitchVirtualLayout extends RotateLayout implements AdapterView.OnItemClickListener {
    private MyAdapter mAdapter;
    private CameraActivity mContext;
    private LayoutInflater mInflater;
    private boolean mIsChecked;
    private boolean mIsSublistItemEnable;
    private Listener mListener;
    private ListPreference[] mPrefs;
    private ViewGroup mSettingList;

    public interface Listener {
        void onStereoCameraSettingChanged(int i, boolean z);
    }

    public SettingSwitchVirtualLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mInflater = LayoutInflater.from(context);
        this.mContext = (CameraActivity) context;
    }

    @Override // com.android.camera.p001ui.RotateLayout, android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mSettingList = (ViewGroup) findViewById(R.id.settingList);
    }

    private class MyAdapter extends BaseAdapter {
        private int mSelectedIndex;

        public MyAdapter() {
        }

        @Override // android.widget.Adapter
        public int getCount() {
            return SettingSwitchVirtualLayout.this.mPrefs.length;
        }

        @Override // android.widget.Adapter
        public Object getItem(int i) {
            return Integer.valueOf(i);
        }

        @Override // android.widget.Adapter
        public long getItemId(int i) {
            return i;
        }

        @Override // android.widget.Adapter
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            ViewHolder viewHolder2 = null;
            if (view == null) {
                view = SettingSwitchVirtualLayout.this.mInflater.inflate(R.layout.setting_dualcamera_sublist_item, (ViewGroup) null);
                ViewHolder viewHolder3 = new ViewHolder(SettingSwitchVirtualLayout.this, viewHolder2);
                viewHolder3.mTextView = (TextView) view.findViewById(R.id.title);
                viewHolder3.mCheckBox = (CheckBox) view.findViewById(R.id.checkbox);
                view.setTag(viewHolder3);
                viewHolder = viewHolder3;
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }
            ListPreference listPreference = SettingSwitchVirtualLayout.this.mPrefs[i];
            Log.m8i("SettingSwitchVirtualLayout", "getView mPrefs[position]" + listPreference);
            viewHolder.mPref = listPreference;
            viewHolder.mTextView.setText(listPreference.getTitle());
            viewHolder.mCheckBox.setChecked(false);
            String overrideValue = listPreference.getOverrideValue();
            if (overrideValue == null) {
                overrideValue = listPreference.getValue();
            }
            int iFindIndexOfValue = listPreference.findIndexOfValue(overrideValue);
            Log.m8i("SettingSwitchVirtualLayout", "pref.getValue = " + overrideValue + ", index = " + iFindIndexOfValue);
            if (iFindIndexOfValue == 0) {
                viewHolder.mCheckBox.setChecked(true);
            } else if (iFindIndexOfValue == 1) {
                viewHolder.mCheckBox.setChecked(false);
            }
            viewHolder.mCheckBox.setEnabled(SettingSwitchVirtualLayout.this.mPrefs[i].isEnabled());
            SettingUtils.setEnabledState(view, SettingSwitchVirtualLayout.this.mIsSublistItemEnable);
            return view;
        }

        public int getSelectedIndex() {
            return this.mSelectedIndex;
        }
    }

    private class ViewHolder {
        CheckBox mCheckBox;
        ListPreference mPref;
        TextView mTextView;

        /* synthetic */ ViewHolder(SettingSwitchVirtualLayout settingSwitchVirtualLayout, ViewHolder viewHolder) {
            this();
        }

        private ViewHolder() {
        }
    }

    public void initialize(ListPreference[] listPreferenceArr, boolean z) {
        if (listPreferenceArr == null) {
            return;
        }
        this.mIsSublistItemEnable = z;
        int length = listPreferenceArr.length;
        this.mPrefs = new ListPreference[length];
        for (int i = 0; i < length; i++) {
            this.mPrefs[i] = listPreferenceArr[i];
        }
        this.mAdapter = new MyAdapter();
        ((AbsListView) this.mSettingList).setAdapter((ListAdapter) this.mAdapter);
        ((AbsListView) this.mSettingList).setOnItemClickListener(this);
        reloadPreference();
    }

    public void reloadPreference() {
        int length = this.mPrefs.length;
        for (int i = 0; i < length; i++) {
            if (this.mPrefs[i] != null) {
                this.mPrefs[i].reloadValue();
            }
        }
        this.mAdapter.notifyDataSetChanged();
    }

    public void setSettingChangedListener(Listener listener) {
        this.mListener = listener;
    }

    @Override // android.widget.AdapterView.OnItemClickListener
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
        Log.m5d("SettingSwitchVirtualLayout", "onItemClick(" + i + ", " + j + ") oldIndex=" + this.mAdapter.getSelectedIndex());
        if (view.getAlpha() != 1.0f) {
            return;
        }
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        ListPreference listPreference = viewHolder.mPref;
        int i2 = (((i == 0 || i == 1) && this.mPrefs[0].findIndexOfValue(this.mPrefs[0].getValue()) == 1 && this.mPrefs[1].findIndexOfValue(this.mPrefs[1].getValue()) == 1) || (i == 0 && this.mPrefs[0].findIndexOfValue(this.mPrefs[0].getValue()) == 0 && this.mPrefs[1].findIndexOfValue(this.mPrefs[1].getValue()) == 1) || (i == 1 && this.mPrefs[0].findIndexOfValue(this.mPrefs[0].getValue()) == 1 && this.mPrefs[1].findIndexOfValue(this.mPrefs[1].getValue()) == 0)) ? 1 : 2;
        String value = listPreference.getValue();
        int iFindIndexOfValue = listPreference.findIndexOfValue(value);
        Log.m8i("SettingSwitchVirtualLayout", "pref.getValue = " + value + ", ind = " + iFindIndexOfValue);
        if (iFindIndexOfValue == 0) {
            viewHolder.mCheckBox.setChecked(false);
            listPreference.setValueIndex(1);
        } else if (iFindIndexOfValue == 1) {
            viewHolder.mCheckBox.setChecked(true);
            listPreference.setValueIndex(0);
            this.mIsChecked = true;
        }
        if (!this.mIsChecked && this.mPrefs[0].findIndexOfValue(this.mPrefs[0].getValue()) == 1 && this.mPrefs[1].findIndexOfValue(this.mPrefs[1].getValue()) == 1) {
            this.mIsChecked = false;
        } else {
            this.mIsChecked = true;
        }
        if (this.mListener != null) {
            this.mListener.onStereoCameraSettingChanged(i2, this.mIsChecked);
            this.mIsChecked = false;
        }
    }
}
