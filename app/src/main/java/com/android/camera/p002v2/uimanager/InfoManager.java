package com.android.camera.p002v2.uimanager;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.android.camera.p002v2.p003ui.UiUtil;
import com.mediatek.camera.R;
import com.mediatek.camera.debug.LogHelper;

/* loaded from: classes.dex */
public class InfoManager extends AbstractUiManager {
    private static final LogHelper.Tag TAG = new LogHelper.Tag(InfoManager.class.getSimpleName());
    private View mInfoLayout;
    private CharSequence mInfoText;
    private TextView mInfoView;
    private ViewGroup mParentView;

    public InfoManager(Activity activity, ViewGroup viewGroup) {
        super(activity, viewGroup);
        this.mParentView = viewGroup;
    }

    @Override // com.android.camera.p002v2.uimanager.AbstractUiManager
    protected View getView() {
        View viewInflate = inflate(R.layout.onscreen_info_v2);
        this.mInfoLayout = viewInflate;
        this.mInfoView = (TextView) viewInflate.findViewById(R.id.info_view);
        return viewInflate;
    }

    @Override // com.android.camera.p002v2.uimanager.AbstractUiManager
    protected void onRefresh() {
        LogHelper.m23d(TAG, "onRefresh() mInfoView=" + this.mInfoView + ", mInfoText=" + this.mInfoText);
        if (this.mInfoView != null) {
            this.mInfoView.setText(this.mInfoText);
            this.mInfoView.setVisibility(this.mInfoText != null ? 0 : 4);
        }
    }

    public void showText(CharSequence charSequence) {
        LogHelper.m23d(TAG, "showText(" + charSequence + ")");
        this.mInfoText = charSequence;
        show();
        UiUtil.setOrientation(this.mInfoLayout, ((Integer) this.mParentView.getTag()).intValue(), false);
    }
}
