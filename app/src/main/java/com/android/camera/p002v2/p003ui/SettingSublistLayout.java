package com.android.camera.p002v2.p003ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.RadioButton;
import android.widget.TextView;
import com.android.camera.p002v2.uimanager.preference.ListPreference;
import com.mediatek.camera.R;
import com.mediatek.camera.debug.LogHelper;

/* loaded from: classes.dex */
public class SettingSublistLayout extends RotateLayout implements AdapterView.OnItemClickListener {
    private static final LogHelper.Tag TAG = new LogHelper.Tag(SettingSublistLayout.class.getSimpleName());
    private MyAdapter mAdapter;
    private LayoutInflater mInflater;
    private Listener mListener;
    private ListPreference mPreference;
    private ViewGroup mSettingList;

    public interface Listener {
        void onSettingChanged(boolean z);
    }

    public SettingSublistLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mInflater = LayoutInflater.from(context);
    }

    @Override // com.android.camera.p002v2.p003ui.RotateLayout, android.view.View
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
            return SettingSublistLayout.this.mPreference.getEntries().length;
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
                view = SettingSublistLayout.this.mInflater.inflate(R.layout.setting_sublist_item, (ViewGroup) null);
                ViewHolder viewHolder3 = new ViewHolder(SettingSublistLayout.this, viewHolder2);
                viewHolder3.mImageView = (ImageView) view.findViewById(R.id.image);
                viewHolder3.mTextView = (TextView) view.findViewById(R.id.title);
                viewHolder3.mRadioButton = (RadioButton) view.findViewById(R.id.radio);
                view.setTag(viewHolder3);
                viewHolder = viewHolder3;
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }
            int iconId = SettingSublistLayout.this.mPreference.getIconId(i);
            if (SettingSublistLayout.this.mPreference.getIconId(i) == -1) {
                viewHolder.mImageView.setVisibility(8);
            } else {
                viewHolder.mImageView.setVisibility(0);
                viewHolder.mImageView.setImageResource(iconId);
            }
            viewHolder.mTextView.setText(SettingSublistLayout.this.mPreference.getEntries()[i]);
            viewHolder.mRadioButton.setChecked(i == this.mSelectedIndex);
            return view;
        }

        public void setSelectedIndex(int i) {
            this.mSelectedIndex = i;
        }

        public int getSelectedIndex() {
            return this.mSelectedIndex;
        }
    }

    private class ViewHolder {
        ImageView mImageView;
        RadioButton mRadioButton;
        TextView mTextView;

        /* synthetic */ ViewHolder(SettingSublistLayout settingSublistLayout, ViewHolder viewHolder) {
            this();
        }

        private ViewHolder() {
        }
    }

    public void initialize(ListPreference listPreference) {
        this.mPreference = listPreference;
        this.mAdapter = new MyAdapter();
        ((AbsListView) this.mSettingList).setAdapter((ListAdapter) this.mAdapter);
        ((AbsListView) this.mSettingList).setOnItemClickListener(this);
        reloadPreference();
    }

    public void reloadPreference() {
        String overrideValue = this.mPreference.getOverrideValue();
        if (overrideValue == null) {
            this.mPreference.reloadValue();
            overrideValue = this.mPreference.getValue();
        }
        int iFindIndexOfValue = this.mPreference.findIndexOfValue(overrideValue);
        if (iFindIndexOfValue != -1) {
            this.mAdapter.setSelectedIndex(iFindIndexOfValue);
            ((AbsListView) this.mSettingList).setSelection(iFindIndexOfValue);
        } else {
            LogHelper.m24e(TAG, "Invalid preference value.");
            this.mPreference.print();
        }
        LogHelper.m26i(TAG, "reloadPreference() mPreference=" + this.mPreference + ", index=" + iFindIndexOfValue);
    }

    public void setSettingChangedListener(Listener listener) {
        this.mListener = listener;
    }

    @Override // android.widget.AdapterView.OnItemClickListener
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
        LogHelper.m23d(TAG, "onItemClick(" + i + ", " + j + ") oldIndex=" + this.mAdapter.getSelectedIndex());
        boolean z = i != this.mAdapter.getSelectedIndex();
        if (z) {
            this.mPreference.setValueIndex(i);
        }
        if (this.mListener != null) {
            this.mListener.onSettingChanged(z);
        }
    }
}
