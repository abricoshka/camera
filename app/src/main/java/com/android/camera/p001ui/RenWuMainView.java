package com.android.camera.p001ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.android.camera.p001ui.RenWuBottomView;
import com.mediatek.camera.R;

/* loaded from: classes.dex */
public class RenWuMainView extends FrameLayout {
    private RenWuBackGroundView backgroundview;
    private RenWuBottomView bottomview;
    private TextView textview;

    public RenWuMainView(Context context) {
        super(context);
        init();
    }

    public RenWuMainView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init();
    }

    public RenWuMainView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init();
    }

    private void init() {
        View viewInflate = ((LayoutInflater) getContext().getSystemService("layout_inflater")).inflate(R.layout.layout_renwu_main, (ViewGroup) this, false);
        addView(viewInflate);
        this.textview = (TextView) viewInflate.findViewById(R.id.textview);
        this.backgroundview = (RenWuBackGroundView) viewInflate.findViewById(R.id.backgroundview);
        this.backgroundview.setVisibility(8);
        this.bottomview = (RenWuBottomView) viewInflate.findViewById(R.id.bottomview);
        this.bottomview.setOnChangeMode(new RenWuBottomView.onChangeMode() { // from class: com.android.camera.ui.RenWuMainView.1
            @Override // com.android.camera.ui.RenWuBottomView.onChangeMode
            public void changeMode(int i) {
                if (i == 0) {
                    RenWuMainView.this.backgroundview.setVisibility(8);
                    RenWuMainView.this.textview.setText(RenWuMainView.this.getResources().getString(R.string.str_renxiang_ziran));
                    return;
                }
                if (i == (-RenWuMainView.this.bottomview.getdegree())) {
                    RenWuMainView.this.backgroundview.setVisibility(8);
                    RenWuMainView.this.textview.setText(RenWuMainView.this.getResources().getString(R.string.str_renxiang_sheyinshi));
                    return;
                }
                if (i == (-RenWuMainView.this.bottomview.getdegree()) * 2) {
                    RenWuMainView.this.backgroundview.setVisibility(8);
                    RenWuMainView.this.textview.setText(RenWuMainView.this.getResources().getString(R.string.str_renxiang_lunkuoguang));
                } else if (i == (-RenWuMainView.this.bottomview.getdegree()) * 3) {
                    RenWuMainView.this.backgroundview.setVisibility(0);
                    RenWuMainView.this.textview.setText(RenWuMainView.this.getResources().getString(R.string.str_renxiang_wutai));
                } else if (i == (-RenWuMainView.this.bottomview.getdegree()) * 4) {
                    RenWuMainView.this.backgroundview.setVisibility(0);
                    RenWuMainView.this.textview.setText(RenWuMainView.this.getResources().getString(R.string.str_renxiang_dansewutai));
                }
            }
        });
        this.backgroundview.setCircleColor(-1);
    }

    public void setBottomViewDefault() {
        if (this.bottomview != null) {
            this.backgroundview.setVisibility(8);
            this.textview.setText(getResources().getString(R.string.str_renxiang_ziran));
            this.bottomview.setdefault();
        }
    }
}
