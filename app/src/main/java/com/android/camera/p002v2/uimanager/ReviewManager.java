package com.android.camera.p002v2.uimanager;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.android.camera.p002v2.p003ui.RotateImageView;
import com.mediatek.camera.R;
import com.mediatek.camera.debug.LogHelper;

/* loaded from: classes.dex */
public class ReviewManager extends AbstractUiManager implements View.OnClickListener {
    private static final LogHelper.Tag TAG = new LogHelper.Tag(ReviewManager.class.getSimpleName());
    private Activity mActivity;
    private Intent mIntent;
    private OnPlayButtonClickListener mOnPlayButtonClickListener;
    private OnRetakeButtonClickListener mOnRetakeButtonClickListener;
    private RotateImageView mPlayView;
    private RotateImageView mRetakeView;
    private Bitmap mReviewBitmap;
    private ImageView mReviewImage;
    private ViewGroup mReviewLayer;
    private boolean mShownByIntent;

    public interface OnPlayButtonClickListener {
        void onPlayButtonClick();
    }

    public interface OnRetakeButtonClickListener {
        void onRetakeButtonClick();
    }

    public ReviewManager(Activity activity, ViewGroup viewGroup) {
        super(activity, viewGroup);
        this.mShownByIntent = true;
        this.mActivity = activity;
        this.mReviewLayer = viewGroup;
        this.mIntent = activity.getIntent();
        String action = this.mIntent != null ? this.mIntent.getAction() : null;
        if ("android.media.action.IMAGE_CAPTURE".equals(action) || "android.media.action.VIDEO_CAPTURE".equals(action) || "android.media.action.IMAGE_CAPTURE_3D".equals(action)) {
            this.mShownByIntent = true;
        }
    }

    @Override // com.android.camera.p002v2.uimanager.AbstractUiManager
    public void show() {
        LogHelper.m26i(TAG, "[show], mShownByIntent:" + this.mShownByIntent);
        if (this.mShownByIntent) {
            super.show();
        }
    }

    @Override // com.android.camera.p002v2.uimanager.AbstractUiManager
    protected View getView() {
        View viewInflate = inflate(R.layout.review_layout_v2);
        this.mPlayView = (RotateImageView) viewInflate.findViewById(R.id.btn_play);
        this.mRetakeView = (RotateImageView) viewInflate.findViewById(R.id.btn_retake);
        this.mReviewImage = (ImageView) viewInflate.findViewById(R.id.review_image);
        String action = this.mIntent.getAction();
        LogHelper.m26i(TAG, "intent.action:" + action);
        if ("android.media.action.IMAGE_CAPTURE".equals(action)) {
            this.mReviewImage.setVisibility(8);
            this.mPlayView.setVisibility(8);
        }
        if ("android.media.action.VIDEO_CAPTURE".equals(action)) {
            this.mPlayView.setVisibility(0);
        }
        this.mRetakeView.setOnClickListener(this);
        this.mPlayView.setOnClickListener(this);
        return viewInflate;
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        if (this.mPlayView == view && this.mOnPlayButtonClickListener != null) {
            this.mOnPlayButtonClickListener.onPlayButtonClick();
        }
        if (this.mRetakeView == view && this.mOnRetakeButtonClickListener != null) {
            this.mOnRetakeButtonClickListener.onRetakeButtonClick();
        }
    }

    @Override // com.android.camera.p002v2.uimanager.AbstractUiManager
    protected void onRefresh() {
        LogHelper.m26i(TAG, "[onRefresh], mReviewImage:" + this.mReviewImage + ", mReviewBitmap:" + this.mReviewBitmap);
        if (this.mReviewImage != null && this.mReviewBitmap != null) {
            this.mReviewImage.setImageBitmap(this.mReviewBitmap);
            this.mReviewImage.setVisibility(0);
        }
    }

    public void setOnRetakeButtonClickListener(OnRetakeButtonClickListener onRetakeButtonClickListener) {
        LogHelper.m26i(TAG, "[setOnRetakeButtonClickListener], listener:" + onRetakeButtonClickListener);
        this.mOnRetakeButtonClickListener = onRetakeButtonClickListener;
    }

    public void setOnPlayButtonClickListener(OnPlayButtonClickListener onPlayButtonClickListener) {
        LogHelper.m26i(TAG, "[setOnPlayButtonClickListener], listener:" + onPlayButtonClickListener);
        this.mOnPlayButtonClickListener = onPlayButtonClickListener;
    }

    public void setReviewImage(Bitmap bitmap) {
        this.mReviewBitmap = bitmap;
        super.show();
    }
}
