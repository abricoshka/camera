package com.android.camera.p001ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.camera.Log;
import com.mediatek.camera.R;
import com.mediatek.camera.setting.preference.ListPreference;

/* loaded from: classes.dex */
public class SettingSwitchSublistLayout extends RotateLayout implements AdapterView.OnItemClickListener {
    private MyAdapter mAdapter;
    private LayoutInflater mInflater;
    private ListPreference mPreference;
    private ViewGroup mSettingList;

    public SettingSwitchSublistLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mInflater = LayoutInflater.from(context);
    }

    @Override // com.android.camera.p001ui.RotateLayout, android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mSettingList = (ViewGroup) findViewById(R.id.settingList);
    }

    private class MyAdapter extends BaseAdapter {
        private int mSelectedIndex;
        final /* synthetic */ SettingSwitchSublistLayout this$0;

        @Override // android.widget.Adapter
        public int getCount() {
            if (this.this$0.mPreference != null) {
                return this.this$0.mPreference.getExtendedValues().length;
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
                view = this.this$0.mInflater.inflate(R.layout.setting_switch_sublist_item, (ViewGroup) null);
                ViewHolder viewHolder3 = new ViewHolder(this.this$0, viewHolder2);
                viewHolder3.mImageView = (ImageView) view.findViewById(R.id.image);
                viewHolder3.mTextView = (TextView) view.findViewById(R.id.title);
                view.setTag(viewHolder3);
                viewHolder = viewHolder3;
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }
            viewHolder.mImageView.setVisibility(0);
            viewHolder.mImageView.setImageResource(0);
            if (this.this$0.mPreference != null) {
                viewHolder.mTextView.setText(this.this$0.mPreference.getExtendedValues()[i].toString());
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

    @Override // android.widget.AdapterView.OnItemClickListener
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
        Log.m5d("SwitchSublistLayout", "onItemClick(" + i + " , " + j + ") and oldIndex = " + this.mAdapter.getSelectedIndex());
    }
}
