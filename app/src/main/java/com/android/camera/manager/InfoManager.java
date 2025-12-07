package com.android.camera.manager;

import android.animation.ObjectAnimator;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import com.android.camera.CameraActivity;
import com.android.camera.p001ui.InfoTextView;
import com.android.camera.p001ui.ZoomControl;
import com.mediatek.camera.R;

/* loaded from: classes.dex */
public class InfoManager extends ViewManager implements View.OnClickListener, View.OnLongClickListener {
    private boolean allowshowmZoomControl;
    private int index;
    private final Handler mHandler;
    private CharSequence mInfoText;
    private LinearLayout mInfoView;
    private InfoTextView mInfoViewFirst;
    private InfoTextView mInfoViewFourth;
    private InfoTextView mInfoViewSecond;
    private InfoTextView mInfoViewThird;
    int mSelectIndex;
    private ZoomControl mZoomControl;
    private int mZoomIndex;
    private CameraActivity mcontext;
    private int one_text;

    private class MainHandler extends Handler {
        /* synthetic */ MainHandler(InfoManager infoManager, MainHandler mainHandler) {
            this();
        }

        private MainHandler() {
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            switch (message.what) {
                case 1:
                    InfoManager.this.allowshowmZoomControl = false;
                    InfoManager.this.mInfoView.setEnabled(true);
                    InfoManager.this.mZoomControl.setVisibility(4);
                    InfoManager.this.setInfoViewTextState(InfoManager.this.index);
                    ObjectAnimator.ofFloat(InfoManager.this.mInfoView, "translationY", -87.0f, 0.0f).setDuration(0L).start();
                    break;
            }
        }
    }

    public InfoManager(CameraActivity cameraActivity) {
        super(cameraActivity);
        this.one_text = 1;
        this.mHandler = new MainHandler(this, null);
        this.allowshowmZoomControl = false;
        this.mSelectIndex = 1;
        this.mcontext = cameraActivity;
    }

    @Override // com.android.camera.manager.ViewManager
    protected View getView() {
        View viewInflate = inflate(R.layout.onscreen_info);
        this.mInfoView = this.mcontext.getmInfoView();
        if (this.mInfoView == null) {
            return viewInflate;
        }
        try {
            this.mInfoViewFirst = (InfoTextView) this.mInfoView.findViewById(R.id.zoom_first);
            this.mInfoViewFirst.setOnClickListener(this);
            this.mInfoViewFirst.setOnLongClickListener(this);
            this.mInfoViewSecond = (InfoTextView) this.mInfoView.findViewById(R.id.zoom_second);
            this.mInfoViewSecond.setOnClickListener(this);
            this.mInfoViewSecond.setOnLongClickListener(this);
            this.mInfoViewThird = (InfoTextView) this.mInfoView.findViewById(R.id.zoom_third);
            this.mInfoViewThird.setOnClickListener(this);
            this.mInfoViewThird.setOnLongClickListener(this);
            this.mInfoViewFourth = (InfoTextView) this.mInfoView.findViewById(R.id.zoom_fourth);
            this.mInfoViewFourth.setOnClickListener(this);
            this.mInfoViewFourth.setOnLongClickListener(this);
            this.mInfoViewSecond.setSelected(true);
            this.mZoomControl = (ZoomControl) this.mcontext.findViewById(R.id.zoom_control);
            this.mZoomControl.setZoomMax(10);
            this.mZoomControl.setconntext(this.mcontext);
            if (getContext() != null && getContext().getParameters() != null) {
                this.mZoomControl.setZoomIndex(getContext().getParameters().getZoom());
            }
        } catch (Exception e) {
        }
        this.mInfoView.setOnLongClickListener(new View.OnLongClickListener() { // from class: com.android.camera.manager.InfoManager.1
            @Override // android.view.View.OnLongClickListener
            public boolean onLongClick(View view) {
                InfoManager.this.allowshowmZoomControl = true;
                if (InfoManager.this.mcontext.getCurrentWheelMode() == 6) {
                    ObjectAnimator.ofFloat(InfoManager.this.mInfoView, "translationY", 0.0f, -240.0f).setDuration(0L).start();
                } else {
                    ObjectAnimator.ofFloat(InfoManager.this.mInfoView, "translationY", 0.0f, -115.0f).setDuration(0L).start();
                }
                InfoManager.this.zoomControlShowUI();
                return true;
            }
        });
        return viewInflate;
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.zoom_first /* 2131492897 */:
                this.mSelectIndex = 0;
                break;
            case R.id.zoom_second /* 2131492898 */:
                this.mSelectIndex = 1;
                break;
            case R.id.zoom_third /* 2131492899 */:
                this.mSelectIndex = 2;
                break;
            case R.id.zoom_fourth /* 2131492900 */:
                this.mSelectIndex = 5;
                break;
            default:
                this.mSelectIndex = 1;
                break;
        }
        setInfoViewItems(this.mSelectIndex);
    }

    @Override // android.view.View.OnLongClickListener
    public boolean onLongClick(View view) {
        this.allowshowmZoomControl = true;
        if (this.mcontext.getCurrentWheelMode() == 6) {
            ObjectAnimator.ofFloat(this.mInfoView, "translationY", 0.0f, -240.0f).setDuration(0L).start();
        } else {
            ObjectAnimator.ofFloat(this.mInfoView, "translationY", 0.0f, -135.0f).setDuration(0L).start();
        }
        zoomControlShowUI();
        return true;
    }

    public void setInfoViewItems(int i) {
        if (this.mInfoView == null || this.mInfoViewSecond == null) {
            return;
        }
        setInfoViewText(i);
        setInfoViewTextState(i);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setInfoViewTextState(int i) {
        if (i == 0) {
            setInfoViewItemState(this.mInfoViewFirst, true);
            setInfoViewItemState(this.mInfoViewSecond, false);
            setInfoViewItemState(this.mInfoViewThird, false);
            setInfoViewItemState(this.mInfoViewFourth, false);
            this.mZoomIndex = 1;
            this.mcontext.getPerformZoom(1, true);
            return;
        }
        if (i == 1) {
            setInfoViewItemState(this.mInfoViewFirst, false);
            setInfoViewItemState(this.mInfoViewSecond, true);
            setInfoViewItemState(this.mInfoViewThird, false);
            setInfoViewItemState(this.mInfoViewFourth, false);
            this.mZoomIndex = 1;
            this.mcontext.getPerformZoom(1, true);
            return;
        }
        if (i == 2) {
            setInfoViewItemState(this.mInfoViewFirst, false);
            setInfoViewItemState(this.mInfoViewSecond, false);
            setInfoViewItemState(this.mInfoViewThird, true);
            setInfoViewItemState(this.mInfoViewFourth, false);
            this.mZoomIndex = 2;
            this.mcontext.getPerformZoom(2, true);
            return;
        }
        if (i == 3) {
            setInfoViewItemState(this.mInfoViewFirst, false);
            setInfoViewItemState(this.mInfoViewSecond, false);
            setInfoViewItemState(this.mInfoViewThird, false);
            setInfoViewItemState(this.mInfoViewFourth, true);
            this.mZoomIndex = 3;
            this.mcontext.getPerformZoom(3, true);
            return;
        }
        if (i == 4) {
            setInfoViewItemState(this.mInfoViewFirst, false);
            setInfoViewItemState(this.mInfoViewSecond, false);
            setInfoViewItemState(this.mInfoViewThird, false);
            setInfoViewItemState(this.mInfoViewFourth, true);
            this.mZoomIndex = 4;
            this.mcontext.getPerformZoom(4, true);
            return;
        }
        if (i == 5) {
            setInfoViewItemState(this.mInfoViewFirst, false);
            setInfoViewItemState(this.mInfoViewSecond, false);
            setInfoViewItemState(this.mInfoViewThird, false);
            setInfoViewItemState(this.mInfoViewFourth, true);
            this.mZoomIndex = 5;
            this.mcontext.getPerformZoom(5, true);
        }
    }

    public void setInfoViewItemState(InfoTextView infoTextView, boolean z) {
        infoTextView.setSelected(z);
    }

    public void showText(CharSequence charSequence) {
        Log.d("zbx1109", "showText(" + charSequence + ")");
        this.mInfoText = charSequence;
        int zoom = 0;
        if (getContext() != null && getContext().getParameters() != null) {
            zoom = getContext().getParameters().getZoom();
        }
        if (this.mZoomControl != null) {
            this.mZoomControl.setZoomIndex(zoom);
        }
        if (zoom != this.mZoomIndex) {
            this.mZoomIndex = zoom;
            if (this.allowshowmZoomControl) {
                zoomControlShowUI();
            }
        }
        show();
    }

    @Override // com.android.camera.manager.ViewManager
    public void show() {
        super.show();
        Log.d("zbx1109", "show: ");
    }

    @Override // com.android.camera.manager.ViewManager
    public void hide() {
        this.mInfoText = null;
        onRefresh();
        super.hide();
        Log.d("zbx1109", "hide: ");
    }

    private void setInfoViewText(int i) {
        this.mInfoViewFirst.setText(".5");
        this.mInfoViewSecond.setText("1");
        this.mInfoViewThird.setText("2");
        Log.v("xiaoyao", "setInfoViewText==" + i);
        this.mInfoViewFourth.setText("5");
        if (i == 0) {
            this.mInfoViewFirst.setText("0.5x");
            return;
        }
        if (i == 1) {
            this.mInfoViewSecond.setText("1x");
        } else if (i == 2) {
            this.mInfoViewThird.setText("2x");
        } else if (i > 2) {
            this.mInfoViewFourth.setText(i + "x");
        }
    }

    @Override // com.android.camera.manager.ViewManager
    protected void onRefresh() {
        Log.d("zbx1109", "onRefresh() mInfoView=" + this.mInfoView + ", mInfoText=" + this.mInfoText);
        if (this.mInfoView != null && this.mInfoViewThird != null) {
            if (getContext() != null && getContext().getParameters() != null) {
                this.index = getContext().getParameters().getZoom();
            }
            Log.d("zbx", "onRefresh: index=" + this.index);
            Log.d("zbx", "onRefresh: mSelectIndex=" + this.mSelectIndex);
            if (this.index == 0) {
                this.index = 1;
            }
            if (this.mZoomControl.getVisibility() != 0 && this.mSelectIndex == 0) {
                this.index = 0;
            }
            setInfoViewText(this.index);
            if (this.index > 3) {
                this.mInfoViewFourth.setText(this.index + "x");
                setInfoViewItemState(this.mInfoViewFirst, false);
                setInfoViewItemState(this.mInfoViewSecond, false);
                setInfoViewItemState(this.mInfoViewThird, false);
                setInfoViewItemState(this.mInfoViewFourth, true);
            } else {
                Log.d("zbx", "onRefresh: 111111111index=" + this.index);
                setInfoViewState2();
            }
            this.mInfoView.setVisibility(this.mInfoText == null ? 4 : 0);
        }
    }

    private void setInfoViewState2() {
        Log.d("zbx", "setInfoViewState2: ");
        setInfoViewItemState(this.mInfoViewFirst, false);
        setInfoViewItemState(this.mInfoViewSecond, false);
        setInfoViewItemState(this.mInfoViewThird, false);
        setInfoViewItemState(this.mInfoViewFourth, false);
        if (this.index > 2) {
            setInfoViewItemState(this.mInfoViewFourth, true);
            return;
        }
        if (this.index > 1) {
            setInfoViewItemState(this.mInfoViewThird, true);
        } else if (this.index > 0) {
            setInfoViewItemState(this.mInfoViewSecond, true);
        } else {
            setInfoViewItemState(this.mInfoViewFirst, true);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void zoomControlShowUI() {
        com.android.camera.Log.m5d("hehsilei", "zoomControlShowUI");
        this.mHandler.removeMessages(1);
        this.mInfoView.setEnabled(false);
        this.mZoomControl.setVisibility(0);
        this.mHandler.sendEmptyMessageDelayed(1, 1500L);
    }
}
