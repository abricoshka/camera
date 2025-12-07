package com.android.camera.p001ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.RadioButton;
import android.widget.TextView;
import com.android.camera.Log;
import com.mediatek.camera.R;
import com.mediatek.camera.setting.preference.IconListPreference;
import com.mediatek.camera.setting.preference.ListPreference;

/* loaded from: classes.dex */
public class SettingVirtualLayout extends RotateLayout implements View.OnClickListener {
    private MyAdapter mAdapter;
    private LayoutInflater mInflater;
    private Listener mListener;
    private ListPreference[] mPrefs;
    private ViewGroup mSettingList;

    public interface Listener {
        void onSettingChanged(ListPreference listPreference);
    }

    public SettingVirtualLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mInflater = LayoutInflater.from(context);
    }

    @Override // com.android.camera.p001ui.RotateLayout, android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mSettingList = (ViewGroup) findViewById(R.id.settingList);
    }

    private class MyAdapter extends BaseAdapter {
        public MyAdapter() {
        }

        @Override // android.widget.Adapter
        public int getCount() {
            return SettingVirtualLayout.this.mPrefs.length;
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
                view = SettingVirtualLayout.this.mInflater.inflate(R.layout.setting_virtual_item, (ViewGroup) null);
                ViewHolder viewHolder3 = new ViewHolder(SettingVirtualLayout.this, viewHolder2);
                viewHolder3.mTitle = (TextView) view.findViewById(R.id.title);
                viewHolder3.mEffectImage1 = (ImageView) view.findViewById(R.id.effectImage1);
                viewHolder3.mEffectImage2 = (ImageView) view.findViewById(R.id.effectImage2);
                viewHolder3.mEffectImage3 = (ImageView) view.findViewById(R.id.effectImage3);
                viewHolder3.mTitle1 = (TextView) view.findViewById(R.id.title1);
                viewHolder3.mTitle2 = (TextView) view.findViewById(R.id.title2);
                viewHolder3.mTitle3 = (TextView) view.findViewById(R.id.title3);
                viewHolder3.mRadio1 = (RadioButton) view.findViewById(R.id.radio1);
                viewHolder3.mRadio2 = (RadioButton) view.findViewById(R.id.radio2);
                viewHolder3.mRadio3 = (RadioButton) view.findViewById(R.id.radio3);
                viewHolder3.mRadio1.setOnClickListener(SettingVirtualLayout.this);
                viewHolder3.mRadio2.setOnClickListener(SettingVirtualLayout.this);
                viewHolder3.mRadio3.setOnClickListener(SettingVirtualLayout.this);
                view.setTag(viewHolder3);
                viewHolder = viewHolder3;
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }
            ListPreference listPreference = SettingVirtualLayout.this.mPrefs[i];
            viewHolder.mPref = listPreference;
            viewHolder.mTitle.setText(listPreference.getTitle());
            if (listPreference instanceof IconListPreference) {
                viewHolder.mEffectImage1.setImageResource(((IconListPreference) listPreference).getIconId(0));
                viewHolder.mEffectImage2.setImageResource(((IconListPreference) listPreference).getIconId(1));
                viewHolder.mEffectImage3.setImageResource(((IconListPreference) listPreference).getIconId(2));
            }
            viewHolder.mTitle1.setText(listPreference.getEntries()[0]);
            viewHolder.mTitle2.setText(listPreference.getEntries()[1]);
            viewHolder.mTitle3.setText(listPreference.getEntries()[2]);
            viewHolder.mRadio1.setChecked(false);
            viewHolder.mRadio2.setChecked(false);
            viewHolder.mRadio3.setChecked(false);
            String overrideValue = listPreference.getOverrideValue();
            if (overrideValue == null) {
                overrideValue = listPreference.getValue();
            }
            switch (listPreference.findIndexOfValue(overrideValue)) {
                case 0:
                    viewHolder.mRadio1.setChecked(true);
                    break;
                case 1:
                    viewHolder.mRadio2.setChecked(true);
                    break;
                case 2:
                    viewHolder.mRadio3.setChecked(true);
                    break;
                default:
                    throw new RuntimeException("Why has none value? " + listPreference.getValue());
            }
            viewHolder.mRadio1.setTag(viewHolder);
            viewHolder.mRadio2.setTag(viewHolder);
            viewHolder.mRadio3.setTag(viewHolder);
            boolean zIsEnabled = SettingVirtualLayout.this.mPrefs[i].isEnabled();
            viewHolder.mRadio1.setEnabled(zIsEnabled);
            viewHolder.mRadio2.setEnabled(zIsEnabled);
            viewHolder.mRadio3.setEnabled(zIsEnabled);
            return view;
        }

        @Override // android.widget.BaseAdapter, android.widget.ListAdapter
        public boolean isEnabled(int i) {
            return SettingVirtualLayout.this.mPrefs[i].isEnabled();
        }
    }

    private class ViewHolder {
        ImageView mEffectImage1;
        ImageView mEffectImage2;
        ImageView mEffectImage3;
        ListPreference mPref;
        RadioButton mRadio1;
        RadioButton mRadio2;
        RadioButton mRadio3;
        TextView mTitle;
        TextView mTitle1;
        TextView mTitle2;
        TextView mTitle3;

        /* synthetic */ ViewHolder(SettingVirtualLayout settingVirtualLayout, ViewHolder viewHolder) {
            this();
        }

        private ViewHolder() {
        }
    }

    public void initialize(ListPreference[] listPreferenceArr) {
        if (listPreferenceArr == null) {
            return;
        }
        int length = listPreferenceArr.length;
        this.mPrefs = new ListPreference[length];
        for (int i = 0; i < length; i++) {
            this.mPrefs[i] = listPreferenceArr[i];
        }
        this.mAdapter = new MyAdapter();
        ((AbsListView) this.mSettingList).setAdapter((ListAdapter) this.mAdapter);
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

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        ListPreference listPreference = viewHolder.mPref;
        Log.m8i("SettingVirtualLayout", "onClick(" + view + ") pref=" + listPreference);
        if (listPreference == null) {
            return;
        }
        RadioButton radioButton = viewHolder.mRadio1;
        switch (view.getId()) {
            case R.id.radio1 /* 2131493090 */:
                listPreference.setValueIndex(0);
                radioButton = viewHolder.mRadio1;
                break;
            case R.id.radio2 /* 2131493093 */:
                listPreference.setValueIndex(1);
                radioButton = viewHolder.mRadio2;
                break;
            case R.id.radio3 /* 2131493096 */:
                listPreference.setValueIndex(2);
                radioButton = viewHolder.mRadio3;
                break;
        }
        RadioButton[] radioButtonArr = {viewHolder.mRadio1, viewHolder.mRadio2, viewHolder.mRadio3};
        for (int i = 0; i < 3; i++) {
            if (radioButton != radioButtonArr[i]) {
                radioButtonArr[i].setChecked(false);
            }
        }
        if (this.mListener != null) {
            this.mListener.onSettingChanged(listPreference);
        }
    }
}
