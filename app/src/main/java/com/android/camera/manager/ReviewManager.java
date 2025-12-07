package com.android.camera.manager;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import com.android.camera.CameraActivity;
import com.android.camera.Thumbnail;
import com.android.camera.Util;
import com.android.camera.p001ui.RotateImageView;
import com.mediatek.camera.R;
import com.mediatek.camera.util.Log;
import java.io.FileDescriptor;

/* loaded from: classes.dex */
public class ReviewManager extends ViewManager implements View.OnClickListener {
    private FileDescriptor mFileDescriptor;
    private String mFilePath;
    private int mOrientationCompensation;
    private View.OnClickListener mPlayListener;
    private RotateImageView mPlayView;
    private View.OnClickListener mRetakeLisenter;
    private RotateImageView mRetakeView;
    private Bitmap mReviewBitmap;
    private ImageView mReviewImage;

    public ReviewManager(CameraActivity cameraActivity) {
        super(cameraActivity, -1);
    }

    @Override // com.android.camera.manager.ViewManager
    protected View getView() {
        View viewInflate = inflate(R.layout.review_layout_orig);
        this.mPlayView = (RotateImageView) viewInflate.findViewById(R.id.btn_play);
        this.mRetakeView = (RotateImageView) viewInflate.findViewById(R.id.btn_retake);
        this.mReviewImage = (ImageView) viewInflate.findViewById(R.id.review_image);
        if (this.mReviewImage != null && getContext().isImageCaptureIntent()) {
            this.mReviewImage.setVisibility(8);
        }
        if (this.mPlayView != null) {
            if (getContext().isImageCaptureIntent()) {
                this.mPlayView.setVisibility(8);
            } else {
                this.mPlayView.setVisibility(0);
            }
        }
        this.mRetakeView.setOnClickListener(this);
        this.mPlayView.setOnClickListener(this);
        return viewInflate;
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        View.OnClickListener onClickListener;
        Log.m34i("ReviewManager", "onClick, view = " + view);
        if (this.mRetakeView == view) {
            onClickListener = this.mRetakeLisenter;
        } else {
            onClickListener = this.mPlayListener;
        }
        if (onClickListener != null && view.isShown()) {
            onClickListener.onClick(view);
        }
        Log.m31d("ReviewManager", "onClick(" + view + ") listener=" + onClickListener);
    }

    @Override // com.android.camera.manager.ViewManager
    protected void onRefresh() {
        Log.m35v("ReviewManager", "onRefresh() mFileDescriptor=" + this.mFileDescriptor + ", mFilePath=" + this.mFilePath + ", OrientationCompensation=" + this.mOrientationCompensation + ", mReviewBitmap=" + this.mReviewBitmap);
        if (this.mReviewBitmap == null) {
            if (this.mFileDescriptor != null) {
                this.mReviewBitmap = Thumbnail.createVideoThumbnailBitmap(this.mFileDescriptor, getContext().getPreviewFrameWidth());
            } else if (this.mFilePath != null) {
                this.mReviewBitmap = Thumbnail.createVideoThumbnailBitmap(this.mFilePath, getContext().getPreviewFrameWidth());
            }
        }
        if (this.mReviewBitmap == null || this.mReviewImage == null) {
            if (this.mReviewImage != null) {
                this.mReviewImage.setImageBitmap(null);
            }
        } else {
            this.mReviewBitmap = Util.rotateAndMirror(this.mReviewBitmap, -this.mOrientationCompensation, false);
            this.mReviewImage.setImageBitmap(this.mReviewBitmap);
            this.mReviewImage.setVisibility(0);
        }
    }

    @Override // com.android.camera.manager.ViewManager
    protected void onRelease() {
        super.onRelease();
        if (this.mReviewImage != null) {
            this.mReviewImage.setImageBitmap(null);
        }
    }

    public void setReviewListener(View.OnClickListener onClickListener, View.OnClickListener onClickListener2) {
        this.mRetakeLisenter = onClickListener;
        this.mPlayListener = onClickListener2;
    }

    public void show(FileDescriptor fileDescriptor) {
        Log.m35v("ReviewManager", "show(" + fileDescriptor + ") mReviewBitmap=" + this.mReviewBitmap);
        this.mFileDescriptor = fileDescriptor;
        this.mReviewBitmap = null;
        show();
    }

    public void show(String str) {
        Log.m35v("ReviewManager", "show(" + str + ") mReviewBitmap=" + this.mReviewBitmap);
        this.mFilePath = str;
        this.mReviewBitmap = null;
        show();
    }
}
