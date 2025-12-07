package com.android.camera.manager;

import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.LinearLayout;
import com.android.camera.CameraActivity;
import com.android.camera.FeatureSwitcher;
import com.android.camera.Log;
import com.android.camera.p001ui.ModePickerScrollView;
import com.android.camera.p001ui.RotateImageView;
import com.mediatek.camera.R;
import com.mediatek.camera.setting.preference.ListPreference;

/* loaded from: classes.dex */
public class ModePicker extends ViewManager implements View.OnClickListener, View.OnLongClickListener {
    private static final int[] MODE_ICONS_NORMAL;
    private int mCurrentMode;
    private int mDisplayWidth;
    private boolean mExpand;
    private LinearLayout.LayoutParams mLayoutParams;
    private OnModeChangedListener mModeChangeListener;
    private int mModeMarginBottom;
    private ListPreference mModePreference;
    private OnScreenToast mModeToast;
    private final RotateImageView[] mModeViews;
    private int mModeWidth;
    private ModePickerScrollView mScrollView;
    private static final int[] MODE_ICONS_HIGHTLIGHT = new int[11];
    private static final int[] MODE_ICON_ORDER = {0, 6, 7, 5, 2, 3};

    public interface OnModeChangedListener {
        void onModeChanged(int i);
    }

    static {
        MODE_ICONS_HIGHTLIGHT[0] = R.drawable.ic_mode_photo_focus;
        MODE_ICONS_HIGHTLIGHT[2] = R.drawable.ic_mode_facebeauty_focus;
        MODE_ICONS_HIGHTLIGHT[3] = R.drawable.ic_mode_panorama_focus;
        MODE_ICONS_HIGHTLIGHT[5] = R.drawable.ic_mode_pip_focus;
        MODE_ICONS_HIGHTLIGHT[6] = R.drawable.ic_mode_refocus_focus;
        MODE_ICONS_HIGHTLIGHT[7] = R.drawable.ic_mode_denoise_focus;
        MODE_ICONS_NORMAL = new int[11];
        MODE_ICONS_NORMAL[0] = R.drawable.ic_mode_photo_normal;
        MODE_ICONS_NORMAL[2] = R.drawable.ic_mode_facebeauty_normal;
        MODE_ICONS_NORMAL[3] = R.drawable.ic_mode_panorama_normal;
        MODE_ICONS_NORMAL[5] = R.drawable.ic_mode_pip_normal;
        MODE_ICONS_NORMAL[6] = R.drawable.ic_mode_refocus_normal;
        MODE_ICONS_NORMAL[7] = R.drawable.ic_mode_denoise_normal;
    }

    public ModePicker(CameraActivity cameraActivity) {
        super(cameraActivity);
        this.mLayoutParams = new LinearLayout.LayoutParams(-2, -2);
        this.mModeViews = new RotateImageView[11];
        this.mCurrentMode = -1;
        this.mExpand = false;
        this.mModeMarginBottom = 100;
    }

    public int getCurrentMode() {
        return this.mCurrentMode;
    }

    private void setRealMode(int i) {
        Log.m5d("ModePicker", "setRealMode(" + i + ") mCurrentMode=" + this.mCurrentMode);
        if (this.mCurrentMode != i) {
            this.mCurrentMode = i;
            highlightCurrentMode();
            notifyModeChanged();
            if (this.mModeToast != null) {
                this.mModeToast.cancel();
                return;
            }
            return;
        }
        setEnabled(true);
    }

    public void setCurrentMode(int i) {
        int modeIndex = getModeIndex(i);
        if (getContext().isStereoMode()) {
            if (FeatureSwitcher.isStereoSingle3d()) {
                modeIndex += 200;
            } else {
                modeIndex += 100;
            }
        }
        Log.m5d("ModePicker", "setCurrentMode(" + i + ") realmode=" + modeIndex);
        setRealMode(modeIndex);
    }

    private void highlightCurrentMode() {
        int modeIndex = getModeIndex(this.mCurrentMode);
        for (int i = 0; i < 11; i++) {
            if (this.mModeViews[i] != null) {
                if (i == modeIndex) {
                    this.mModeViews[i].setImageResource(MODE_ICONS_HIGHTLIGHT[i]);
                } else {
                    this.mModeViews[i].setImageResource(MODE_ICONS_NORMAL[i]);
                }
            }
            if (1 == modeIndex || 4 == modeIndex || (FeatureSwitcher.isVfbEnable() && 2 == modeIndex)) {
                this.mModeViews[0].setImageResource(MODE_ICONS_HIGHTLIGHT[0]);
            }
        }
    }

    public int getModeIndex(int i) {
        int i2 = i % 100;
        Log.m5d("ModePicker", "getModeIndex(" + i + ") return " + i2);
        return i2;
    }

    public void setListener(OnModeChangedListener onModeChangedListener) {
        this.mModeChangeListener = onModeChangedListener;
    }

    @Override // com.android.camera.manager.ViewManager
    protected View getView() {
        clearListener();
        View viewInflate = inflate(R.layout.tw_mode_picker);
        this.mScrollView = (ModePickerScrollView) viewInflate.findViewById(R.id.mode_picker_scroller);
        this.mModeViews[0] = (RotateImageView) viewInflate.findViewById(R.id.mode_photo);
        this.mModeViews[5] = (RotateImageView) viewInflate.findViewById(R.id.mode_photo_pip);
        this.mModeViews[6] = (RotateImageView) viewInflate.findViewById(R.id.mode_stereo_camera);
        this.mModeViews[7] = (RotateImageView) viewInflate.findViewById(R.id.mode_photo_stereo);
        this.mModeViews[2] = (RotateImageView) viewInflate.findViewById(R.id.mode_face_beauty);
        this.mModeViews[3] = (RotateImageView) viewInflate.findViewById(R.id.mode_panorama);
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        this.mDisplayWidth = Math.min(displayMetrics.widthPixels, displayMetrics.heightPixels);
        this.mModeWidth = getModeWidth();
        this.mModeMarginBottom = getDefaultMarginBottom();
        applyListener();
        highlightCurrentMode();
        return viewInflate;
    }

    private void applyListener() {
        for (int i = 0; i < 11; i++) {
            if (this.mModeViews[i] != null) {
                this.mModeViews[i].setOnClickListener(this);
                this.mModeViews[i].setOnLongClickListener(this);
            }
        }
    }

    private void clearListener() {
        for (int i = 0; i < 11; i++) {
            if (this.mModeViews[i] != null) {
                this.mModeViews[i].setOnClickListener(null);
                this.mModeViews[i].setOnLongClickListener(null);
                this.mModeViews[i] = null;
            }
        }
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        int i = 0;
        Log.m5d("ModePicker", "onClick(" + view + ") isEnabled()=" + isEnabled() + ", view.isEnabled()=" + view.isEnabled() + ", getContext().isFullScreen()=" + getContext().isFullScreen() + ",mCurrentMode = " + this.mCurrentMode);
        this.mScrollView.setVisibility(this.mExpand ? 0 : 8);
        this.mExpand = !this.mExpand;
        if (FeatureSwitcher.isVfbEnable() && this.mCurrentMode == 2 && view == this.mModeViews[0]) {
            Log.m8i("ModePicker", "onClick(,will return");
            return;
        }
        setEnabled(false);
        if (getContext().isFullScreen()) {
            while (true) {
                if (i >= 11) {
                    break;
                }
                if (this.mModeViews[i] == view) {
                    setCurrentMode(i);
                    break;
                }
                i++;
            }
            Log.m5d("ModePicker", "onClick,isCameraOpened:" + getContext().isCameraOpened());
            if (getContext().isCameraOpened()) {
                setEnabled(true);
            }
        } else {
            setEnabled(true);
        }
        if (view.getContentDescription() != null) {
            if (this.mModeToast == null) {
                this.mModeToast = OnScreenToast.makeText(getContext(), view.getContentDescription());
            } else {
                this.mModeToast.setText(view.getContentDescription());
            }
            this.mModeToast.showToast();
        }
    }

    public void hideToast() {
        Log.m5d("ModePicker", "hideToast(), mModeToast:" + this.mModeToast);
        if (this.mModeToast != null) {
            this.mModeToast.hideToast();
        }
    }

    private void notifyModeChanged() {
        if (this.mModeChangeListener != null) {
            this.mModeChangeListener.onModeChanged(getCurrentMode());
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:31:0x0094  */
    @Override // com.android.camera.manager.ViewManager
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public void onRefresh() {
        /*
            r9 = this;
            r3 = 8
            r8 = 1
            r7 = 20
            r1 = 0
            java.lang.String r0 = "ModePicker"
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r4 = "onRefresh() mCurrentMode="
            java.lang.StringBuilder r2 = r2.append(r4)
            int r4 = r9.mCurrentMode
            java.lang.StringBuilder r2 = r2.append(r4)
            java.lang.String r2 = r2.toString()
            com.android.camera.Log.m5d(r0, r2)
            com.android.camera.CameraActivity r0 = r9.getContext()
            int r0 = com.android.camera.ModeChecker.modesShowInPicker(r0, r1)
            r2 = 4
            if (r0 >= r2) goto L3b
            if (r0 <= r8) goto L3b
            int r2 = r9.mDisplayWidth
            int r4 = r9.mModeWidth
            int r4 = r4 * r0
            int r2 = r2 - r4
            int r0 = r0 + (-1)
            int r0 = r2 / r0
            r9.mModeMarginBottom = r0
        L3b:
            r5 = r1
            r4 = r1
        L3d:
            r0 = 11
            if (r5 >= r0) goto L7d
            com.android.camera.ui.RotateImageView[] r0 = r9.mModeViews
            r0 = r0[r5]
            if (r0 == 0) goto L94
            com.android.camera.CameraActivity r0 = r9.getContext()
            com.android.camera.CameraActivity r2 = r9.getContext()
            int r2 = r2.getCameraId()
            boolean r0 = com.android.camera.ModeChecker.getModePickerVisible(r0, r2, r5)
            r2 = 2
            if (r2 != r5) goto L61
            boolean r2 = com.android.camera.FeatureSwitcher.isVfbEnable()
            if (r2 == 0) goto L61
            r0 = r1
        L61:
            com.android.camera.ui.RotateImageView[] r2 = r9.mModeViews
            r6 = r2[r5]
            if (r0 == 0) goto L7b
            r2 = r1
        L68:
            r6.setVisibility(r2)
            com.android.camera.ui.RotateImageView[] r2 = r9.mModeViews
            r2 = r2[r5]
            r2.setPadding(r7, r7, r7, r7)
            if (r0 == 0) goto L94
            int r0 = r4 + 1
        L76:
            int r2 = r5 + 1
            r5 = r2
            r4 = r0
            goto L3d
        L7b:
            r2 = r3
            goto L68
        L7d:
            if (r4 <= r8) goto L85
            boolean r0 = r9.mExpand
            r0 = r0 ^ 1
            if (r0 == 0) goto L8e
        L85:
            com.android.camera.ui.ModePickerScrollView r0 = r9.mScrollView
            r0.setVisibility(r3)
        L8a:
            r9.highlightCurrentMode()
            return
        L8e:
            com.android.camera.ui.ModePickerScrollView r0 = r9.mScrollView
            r0.setVisibility(r1)
            goto L8a
        L94:
            r0 = r4
            goto L76
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.camera.manager.ModePicker.onRefresh():void");
    }

    @Override // android.view.View.OnLongClickListener
    public boolean onLongClick(View view) {
        if (view.getContentDescription() != null) {
            if (this.mModeToast == null) {
                this.mModeToast = OnScreenToast.makeText(getContext(), view.getContentDescription());
            } else {
                this.mModeToast.setText(view.getContentDescription());
            }
            this.mModeToast.showToast();
            return false;
        }
        return false;
    }

    @Override // com.android.camera.manager.ViewManager
    public void setEnabled(boolean z) {
        super.setEnabled(z);
        if (this.mScrollView != null) {
            this.mScrollView.setEnabled(z);
        }
        for (int i = 0; i < 11; i++) {
            if (this.mModeViews[i] != null) {
                this.mModeViews[i].setEnabled(z);
                this.mModeViews[i].setClickable(z);
            }
        }
    }

    @Override // com.android.camera.manager.ViewManager
    protected void onRelease() {
        super.onRelease();
        this.mModeToast = null;
    }

    private int getModeWidth() {
        return BitmapFactory.decodeResource(getContext().getResources(), MODE_ICONS_NORMAL[0]).getWidth() + 40;
    }

    private int getDefaultMarginBottom() {
        return ((this.mDisplayWidth - (this.mModeWidth * 4)) / 3) + (this.mModeWidth / 6);
    }

    public void setModePreference(ListPreference listPreference) {
        this.mModePreference = listPreference;
    }
}
