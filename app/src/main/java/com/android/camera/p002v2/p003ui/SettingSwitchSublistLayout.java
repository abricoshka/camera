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
import android.widget.TextView;
import com.android.camera.p002v2.uimanager.preference.ListPreference;
import com.mediatek.camera.R;
import com.mediatek.camera.debug.LogHelper;

/* loaded from: classes.dex */
public class SettingSwitchSublistLayout extends RotateLayout implements AdapterView.OnItemClickListener {
    private static final LogHelper.Tag TAG = new LogHelper.Tag("SwitchSublistLayout");
    private MyAdapter mAdapter;
    private LayoutInflater mInflater;
    private Listener mListener;
    private ListPreference mPreference;
    private ViewGroup mSettingList;

    public interface Listener {
        void onVoiceCommandChanged(int i);
    }

    public SettingSwitchSublistLayout(Context context, AttributeSet attributeSet) {
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
            if (SettingSwitchSublistLayout.this.mPreference != null) {
                return SettingSwitchSublistLayout.this.mPreference.getExtendedValues().length;
            }
            return 0;
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
                view = SettingSwitchSublistLayout.this.mInflater.inflate(R.layout.setting_switch_sublist_item, (ViewGroup) null);
                ViewHolder viewHolder3 = new ViewHolder(SettingSwitchSublistLayout.this, viewHolder2);
                viewHolder3.mImageView = (ImageView) view.findViewById(R.id.image);
                viewHolder3.mTextView = (TextView) view.findViewById(R.id.title);
                view.setTag(viewHolder3);
                viewHolder = viewHolder3;
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }
            viewHolder.mImageView.setVisibility(0);
            viewHolder.mImageView.setImageResource(0);
            if (SettingSwitchSublistLayout.this.mPreference != null) {
                viewHolder.mTextView.setText(SettingSwitchSublistLayout.this.mPreference.getExtendedValues()[i].toString());
            }
            return view;
        }

        public int getSelectedIndex() {
            return this.mSelectedIndex;
        }
    }

    private class ViewHolder {
        ImageView mImageView;
        TextView mTextView;

        /* synthetic */ ViewHolder(SettingSwitchSublistLayout settingSwitchSublistLayout, ViewHolder viewHolder) {
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
    }

    public void setSettingChangedListener(Listener listener) {
        this.mListener = listener;
    }

    @Override // android.widget.AdapterView.OnItemClickListener
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
        LogHelper.m23d(TAG, "onItemClick(" + i + ", " + j + ") oldIndex=" + this.mAdapter.getSelectedIndex());
        if (this.mListener != null) {
            this.mListener.onVoiceCommandChanged(i);
        }
    }
}
