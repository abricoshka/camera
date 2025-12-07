package com.android.camera.p002v2.uimanager;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.android.camera.p002v2.p003ui.ModePickerScrollView;
import com.android.camera.p002v2.p003ui.RotateImageView;
import com.android.camera.p002v2.uimanager.preference.ListPreference;
import com.android.camera.p002v2.uimanager.preference.PreferenceManager;
import com.mediatek.camera.R;
import com.mediatek.camera.debug.LogHelper;
import java.util.LinkedHashMap;
import java.util.Map;

/* loaded from: classes.dex */
public class ModePicker extends AbstractUiManager implements View.OnClickListener, View.OnLongClickListener {
    private static final int[] MODE_ICONS_HIGHTLIGHT;
    private static final int[] MODE_ICONS_NORMAL;
    private Activity mActivity;
    private int mCurrentMode;
    private int mDisplayWidth;
    private LinearLayout.LayoutParams mLayoutParams;
    private OnModeChangedListener mListener;
    private ViewGroup mModeLayer;
    private int mModeMarginBottom;
    private OnScreenToast mModeToast;
    private final RotateImageView[] mModeViews;
    private int mModeWidth;
    private PreferenceManager mPreferenceManager;
    private ModePickerScrollView mScrollView;
    private static final LogHelper.Tag TAG = new LogHelper.Tag(ModePicker.class.getSimpleName());
    private static final int[] MODE_ICON_ORDER = {3, 2, 1, 0};
    private static final String[] KEY_OF_MODES = new String[3];

    public interface OnModeChangedListener {
        void onModeChanged(Map<String, String> map);

        void onRestoreToNomalMode(Map<String, String> map);
    }

    static {
        KEY_OF_MODES[0] = "pref_panorama_key";
        KEY_OF_MODES[1] = "pref_photo_pip_key";
        KEY_OF_MODES[2] = "pref_dual_camera_key";
        MODE_ICONS_HIGHTLIGHT = new int[4];
        MODE_ICONS_HIGHTLIGHT[3] = R.drawable.ic_mode_photo_focus;
        MODE_ICONS_HIGHTLIGHT[0] = R.drawable.ic_mode_panorama_focus;
        MODE_ICONS_HIGHTLIGHT[1] = R.drawable.ic_mode_pip_focus;
        MODE_ICONS_HIGHTLIGHT[2] = R.drawable.ic_mode_refocus_focus;
        MODE_ICONS_NORMAL = new int[4];
        MODE_ICONS_NORMAL[3] = R.drawable.ic_mode_photo_normal;
        MODE_ICONS_NORMAL[0] = R.drawable.ic_mode_panorama_normal;
        MODE_ICONS_NORMAL[1] = R.drawable.ic_mode_pip_normal;
        MODE_ICONS_NORMAL[2] = R.drawable.ic_mode_refocus_normal;
    }

    public ModePicker(Activity activity, ViewGroup viewGroup, PreferenceManager preferenceManager) {
        super(activity, viewGroup);
        this.mLayoutParams = new LinearLayout.LayoutParams(-2, -2);
        this.mModeViews = new RotateImageView[4];
        this.mModeMarginBottom = 100;
        this.mCurrentMode = 3;
        this.mActivity = activity;
        this.mModeLayer = viewGroup;
        this.mPreferenceManager = preferenceManager;
    }

    @Override // com.android.camera.p002v2.uimanager.AbstractUiManager
    protected View getView() {
        clearListener();
        View viewInflate = inflate(R.layout.mode_picker_v2);
        this.mScrollView = (ModePickerScrollView) viewInflate.findViewById(R.id.mode_picker_scroller);
        this.mModeViews[3] = (RotateImageView) viewInflate.findViewById(R.id.mode_photo);
        this.mModeViews[1] = (RotateImageView) viewInflate.findViewById(R.id.mode_photo_pip);
        this.mModeViews[2] = (RotateImageView) viewInflate.findViewById(R.id.mode_stereo_camera);
        this.mModeViews[0] = (RotateImageView) viewInflate.findViewById(R.id.mode_panorama);
        DisplayMetrics displayMetrics = this.mActivity.getResources().getDisplayMetrics();
        this.mDisplayWidth = Math.min(displayMetrics.widthPixels, displayMetrics.heightPixels);
        this.mModeWidth = getModeWidth();
        this.mModeMarginBottom = getDefaultMarginBottom();
        applyListener();
        highlightCurrentMode();
        return viewInflate;
    }

    /* JADX WARN: Removed duplicated region for block: B:31:0x00cb  */
    @Override // com.android.camera.p002v2.uimanager.AbstractUiManager
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    protected void onRefresh() {
        /*
            r10 = this;
            r9 = 4
            r8 = 1
            r7 = -2
            r6 = 20
            r1 = 0
            com.mediatek.camera.debug.LogHelper$Tag r0 = com.android.camera.p002v2.uimanager.ModePicker.TAG
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "onRefresh() mCurrentMode="
            java.lang.StringBuilder r2 = r2.append(r3)
            int r3 = r10.mCurrentMode
            java.lang.StringBuilder r2 = r2.append(r3)
            java.lang.String r2 = r2.toString()
            com.mediatek.camera.debug.LogHelper.m23d(r0, r2)
            int r0 = r10.getCountsOfSupportedModes()
            if (r0 >= r9) goto L35
            if (r0 <= r8) goto L35
            int r2 = r10.mDisplayWidth
            int r3 = r10.mModeWidth
            int r3 = r3 * r0
            int r2 = r2 - r3
            int r0 = r0 + (-1)
            int r0 = r2 / r0
            r10.mModeMarginBottom = r0
        L35:
            com.mediatek.camera.debug.LogHelper$Tag r0 = com.android.camera.p002v2.uimanager.ModePicker.TAG
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "mModeMarginBottom:"
            java.lang.StringBuilder r2 = r2.append(r3)
            int r3 = r10.mModeMarginBottom
            java.lang.StringBuilder r2 = r2.append(r3)
            java.lang.String r2 = r2.toString()
            com.mediatek.camera.debug.LogHelper.m23d(r0, r2)
            android.widget.LinearLayout$LayoutParams r0 = r10.mLayoutParams
            int r2 = r10.mModeMarginBottom
            r0.setMargins(r1, r1, r1, r2)
            r3 = r1
            r2 = r1
        L59:
            if (r3 >= r9) goto L8b
            com.android.camera.v2.ui.RotateImageView[] r0 = r10.mModeViews
            r0 = r0[r3]
            if (r0 == 0) goto Lcb
            boolean r4 = r10.isModeVisible(r3)
            com.android.camera.v2.ui.RotateImageView[] r0 = r10.mModeViews
            r5 = r0[r3]
            if (r4 == 0) goto L88
            r0 = r1
        L6c:
            r5.setVisibility(r0)
            com.android.camera.v2.ui.RotateImageView[] r0 = r10.mModeViews
            r0 = r0[r3]
            android.widget.LinearLayout$LayoutParams r5 = r10.mLayoutParams
            r0.setLayoutParams(r5)
            com.android.camera.v2.ui.RotateImageView[] r0 = r10.mModeViews
            r0 = r0[r3]
            r0.setPadding(r6, r6, r6, r6)
            if (r4 == 0) goto Lcb
            int r0 = r2 + 1
        L83:
            int r2 = r3 + 1
            r3 = r2
            r2 = r0
            goto L59
        L88:
            r0 = 8
            goto L6c
        L8b:
            int[] r0 = com.android.camera.p002v2.uimanager.ModePicker.MODE_ICON_ORDER
            int r0 = r0.length
            int r0 = r0 + (-1)
        L90:
            if (r0 < 0) goto Lb5
            int[] r3 = com.android.camera.p002v2.uimanager.ModePicker.MODE_ICON_ORDER
            r3 = r3[r0]
            com.android.camera.v2.ui.RotateImageView[] r4 = r10.mModeViews
            r4 = r4[r3]
            if (r4 == 0) goto Lc2
            com.android.camera.v2.ui.RotateImageView[] r4 = r10.mModeViews
            r4 = r4[r3]
            int r4 = r4.getVisibility()
            if (r4 != 0) goto Lc2
            android.widget.LinearLayout$LayoutParams r0 = new android.widget.LinearLayout$LayoutParams
            r0.<init>(r7, r7)
            r0.setMargins(r1, r1, r1, r1)
            com.android.camera.v2.ui.RotateImageView[] r4 = r10.mModeViews
            r3 = r4[r3]
            r3.setLayoutParams(r0)
        Lb5:
            if (r2 > r8) goto Lc5
            com.android.camera.v2.ui.ModePickerScrollView r0 = r10.mScrollView
            r1 = 8
            r0.setVisibility(r1)
        Lbe:
            r10.highlightCurrentMode()
            return
        Lc2:
            int r0 = r0 + (-1)
            goto L90
        Lc5:
            com.android.camera.v2.ui.ModePickerScrollView r0 = r10.mScrollView
            r0.setVisibility(r1)
            goto Lbe
        Lcb:
            r0 = r2
            goto L83
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.camera.p002v2.uimanager.ModePicker.onRefresh():void");
    }

    @Override // com.android.camera.p002v2.uimanager.AbstractUiManager
    public void setEnable(boolean z) {
        super.setEnable(z);
        if (this.mScrollView != null) {
            this.mScrollView.setEnabled(z);
        }
        for (int i = 0; i < 4; i++) {
            if (this.mModeViews[i] != null) {
                this.mModeViews[i].setEnabled(z);
                this.mModeViews[i].setClickable(z);
            }
        }
    }

    @Override // android.view.View.OnLongClickListener
    public boolean onLongClick(View view) {
        if (this.mModeToast == null) {
            this.mModeToast = new OnScreenToast(this.mActivity, this.mModeLayer);
        }
        this.mModeToast.showToast(view.getContentDescription());
        return false;
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        LogHelper.m26i(TAG, "[onClick], view:" + view);
        if (this.mModeToast == null) {
            this.mModeToast = new OnScreenToast(this.mActivity, this.mModeLayer);
        }
        this.mModeToast.showToast(view.getContentDescription());
        int i = 0;
        while (true) {
            if (i >= 4) {
                i = -1;
                break;
            } else if (this.mModeViews[i] == view) {
                break;
            } else {
                i++;
            }
        }
        if (this.mCurrentMode != i) {
            LinkedHashMap linkedHashMap = new LinkedHashMap();
            if (i == 3) {
                linkedHashMap.put(KEY_OF_MODES[this.mCurrentMode], "off");
            } else if (i != 3 && this.mCurrentMode == 3) {
                linkedHashMap.put(KEY_OF_MODES[i], "on");
            } else {
                String str = KEY_OF_MODES[this.mCurrentMode];
                String str2 = KEY_OF_MODES[i];
                linkedHashMap.put(str, "off");
                linkedHashMap.put(str2, "on");
            }
            this.mCurrentMode = i;
            highlightCurrentMode();
            if (this.mListener != null) {
                this.mListener.onModeChanged(linkedHashMap);
            }
        }
    }

    public void setOnModeChangedListener(OnModeChangedListener onModeChangedListener) {
        this.mListener = onModeChangedListener;
    }

    public void restoreToNormalMode() {
        LogHelper.m26i(TAG, "[restoreToNormalMode], mCurrentMode:" + this.mCurrentMode);
        if (this.mCurrentMode == 3) {
            return;
        }
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        linkedHashMap.put(KEY_OF_MODES[this.mCurrentMode], "off");
        this.mCurrentMode = 3;
        highlightCurrentMode();
        if (this.mListener != null) {
            this.mListener.onRestoreToNomalMode(linkedHashMap);
        }
    }

    private void highlightCurrentMode() {
        int modeIndex = getModeIndex(this.mCurrentMode);
        for (int i = 0; i < 4; i++) {
            if (this.mModeViews[i] != null) {
                if (i == modeIndex) {
                    this.mModeViews[i].setImageResource(MODE_ICONS_HIGHTLIGHT[i]);
                } else {
                    this.mModeViews[i].setImageResource(MODE_ICONS_NORMAL[i]);
                }
            }
        }
    }

    private int getModeIndex(int i) {
        int i2 = i % 100;
        LogHelper.m23d(TAG, "getModeIndex(" + i + ") return " + i2);
        return i2;
    }

    private int getCountsOfSupportedModes() {
        int i = 1;
        for (String str : KEY_OF_MODES) {
            ListPreference listPreference = this.mPreferenceManager.getListPreference(str);
            if (listPreference != null && listPreference.isVisibled()) {
                i++;
            }
        }
        return i;
    }

    private boolean isModeVisible(int i) {
        if (i == 3) {
            return true;
        }
        ListPreference listPreference = this.mPreferenceManager.getListPreference(KEY_OF_MODES[i]);
        return listPreference != null && listPreference.isVisibled();
    }

    private void applyListener() {
        for (int i = 0; i < 4; i++) {
            if (this.mModeViews[i] != null) {
                this.mModeViews[i].setOnClickListener(this);
                this.mModeViews[i].setOnLongClickListener(this);
            }
        }
    }

    private void clearListener() {
        for (int i = 0; i < 4; i++) {
            if (this.mModeViews[i] != null) {
                this.mModeViews[i].setOnClickListener(null);
                this.mModeViews[i].setOnLongClickListener(null);
                this.mModeViews[i] = null;
            }
        }
    }

    private int getModeWidth() {
        return BitmapFactory.decodeResource(this.mActivity.getResources(), MODE_ICONS_NORMAL[3]).getWidth() + 40;
    }

    private int getDefaultMarginBottom() {
        return ((this.mDisplayWidth - (this.mModeWidth * 4)) / 3) + (this.mModeWidth / 6);
    }
}
