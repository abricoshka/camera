package com.mediatek.camera.addition.continuousshot;

import android.app.Activity;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.TextView;
import com.mediatek.camera.R;
import com.mediatek.camera.p004ui.CameraView;
import com.mediatek.camera.util.Log;

/* loaded from: classes.dex */
public class CsView extends CameraView {
    private TextView mCsInfoView;
    private SpannableString mSpannableString;

    public CsView(Activity activity) {
        super(activity);
        Log.m34i("CsView", "[CsView]constructor...");
    }

    @Override // com.mediatek.camera.p004ui.CameraView, com.mediatek.camera.platform.ICameraView
    public boolean update(int i, Object... objArr) {
        String str = (String) objArr[0];
        Log.m31d("CsView", "[update]text = " + str);
        this.mSpannableString = new SpannableString(str);
        show();
        return true;
    }

    @Override // com.mediatek.camera.p004ui.CameraView, com.mediatek.camera.platform.ICameraView
    public void refresh() {
        Log.m34i("CsView", "[refresh]...");
        if (this.mCsInfoView != null) {
            this.mSpannableString.setSpan(new RelativeSizeSpan(1.7f), 0, 2, 33);
            this.mSpannableString.setSpan(new StyleSpan(1), 0, 2, 33);
            this.mCsInfoView.setText(this.mSpannableString);
            this.mCsInfoView.setVisibility(0);
        }
    }

    @Override // com.mediatek.camera.p004ui.CameraView
    protected View getView() {
        View viewInflate = inflate(R.layout.onscreen_cs_speed);
        this.mCsInfoView = (TextView) viewInflate.findViewById(R.id.cs_info_view);
        return viewInflate;
    }
}
