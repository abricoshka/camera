package com.android.camera.p002v2.uimanager;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.android.camera.p002v2.app.AppController;
import com.android.camera.p002v2.uimanager.preference.PreferenceManager;
import com.mediatek.camera.R;
import com.mediatek.camera.debug.LogHelper;
import com.mediatek.camera.p005v2.services.storage.IStorageService;
import java.util.Locale;

/* loaded from: classes.dex */
public class RemainingManager extends AbstractUiManager {
    private PreferenceManager mPreferenceManager;
    private String mRemainingText;
    private TextView mRemainingView;
    private IStorageService mStorageService;
    private static final LogHelper.Tag TAG = new LogHelper.Tag(RemainingManager.class.getSimpleName());
    private static final Long REMAIND_THRESHOLD = 100L;

    public RemainingManager(AppController appController, Activity activity, ViewGroup viewGroup, PreferenceManager preferenceManager) {
        super(activity, viewGroup);
        this.mPreferenceManager = preferenceManager;
        this.mStorageService = appController.getAppControllerAdapter().getServices().getStorageService();
    }

    @Override // com.android.camera.p002v2.uimanager.AbstractUiManager
    protected View getView() {
        View viewInflate = inflate(R.layout.remaining_v2);
        this.mRemainingView = (TextView) viewInflate.findViewById(R.id.remaining_view);
        return viewInflate;
    }

    @Override // com.android.camera.p002v2.uimanager.AbstractUiManager
    protected void onRefresh() {
        super.onRefresh();
        if (this.mRemainingView != null) {
            this.mRemainingView.setText(this.mRemainingText);
        }
    }

    public void showLeftCounts(int i, boolean z) {
        long captureStorageSpace = this.mStorageService.getCaptureStorageSpace();
        long j = captureStorageSpace > 0 ? captureStorageSpace / i : 0L;
        if (!z && j > REMAIND_THRESHOLD.longValue()) {
            return;
        }
        this.mRemainingText = j < 0 ? stringForCount(0L) : stringForCount(j);
        super.show();
        LogHelper.m26i(TAG, "[showLeftCounts], leftCounts:" + j + ", mRemainingText:" + this.mRemainingText);
    }

    public void showLeftTime(long j) {
        long recordStorageSpace = this.mStorageService.getRecordStorageSpace();
        long j2 = recordStorageSpace > 0 ? recordStorageSpace / j : 0L;
        this.mRemainingText = j2 < 0 ? stringForTime(0L) : stringForTime(j2);
        super.show();
        LogHelper.m26i(TAG, "[showLeftTime], leftTime:" + j2 + ", mRemainingText:" + this.mRemainingText);
    }

    private static String stringForTime(long j) {
        int i = ((int) j) / 1000;
        int i2 = i % 60;
        int i3 = (i / 60) % 60;
        int i4 = i / 3600;
        return i4 > 0 ? String.format(Locale.ENGLISH, "%d:%02d:%02d", Integer.valueOf(i4), Integer.valueOf(i3), Integer.valueOf(i2)) : String.format(Locale.ENGLISH, "%02d:%02d", Integer.valueOf(i3), Integer.valueOf(i2));
    }

    private static String stringForCount(long j) {
        return String.format("%d", Long.valueOf(j));
    }
}
