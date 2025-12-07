package com.android.camera.bridge;

import android.location.Location;
import com.android.camera.FileSaver;
import com.android.camera.SaveRequest;
import com.android.camera.Storage;
import com.mediatek.camera.platform.IFileSaver;
import com.mediatek.camera.util.Log;
import junit.framework.Assert;

/* loaded from: classes.dex */
public class FileSaverImpl implements IFileSaver {

    /* renamed from: -com-mediatek-camera-platform-IFileSaver$FILE_TYPESwitchesValues, reason: not valid java name */
    private static final /* synthetic */ int[] f86commediatekcameraplatformIFileSaver$FILE_TYPESwitchesValues = null;
    private FileSaver mFileSaver;
    private final FileSaver.FileSaverListener mFileSaverListener = new FileSaver.FileSaverListener() { // from class: com.android.camera.bridge.FileSaverImpl.1
        @Override // com.android.camera.FileSaver.FileSaverListener
        public void onFileSaved(SaveRequest saveRequest) {
            if (FileSaverImpl.this.mListener != null) {
                FileSaverImpl.this.mListener.onFileSaved(saveRequest.getUri());
            }
        }
    };
    IFileSaver.FILE_TYPE mFileType;
    private IFileSaver.OnFileSavedListener mListener;
    private SaveRequest mRawSaveRequest;
    private SaveRequest mSaveRequest;
    private SaveRequest mVideoSaveRequest;

    /* renamed from: -getcom-mediatek-camera-platform-IFileSaver$FILE_TYPESwitchesValues */
    private static /* synthetic */ int[] m15xdacbdea1() {
        if (f86commediatekcameraplatformIFileSaver$FILE_TYPESwitchesValues != null) {
            return f86commediatekcameraplatformIFileSaver$FILE_TYPESwitchesValues;
        }
        int[] iArr = new int[IFileSaver.FILE_TYPE.valuesCustom().length];
        try {
            iArr[IFileSaver.FILE_TYPE.JPEG.ordinal()] = 6;
        } catch (NoSuchFieldError e) {
        }
        try {
            iArr[IFileSaver.FILE_TYPE.PANORAMA.ordinal()] = 1;
        } catch (NoSuchFieldError e2) {
        }
        try {
            iArr[IFileSaver.FILE_TYPE.PIPVIDEO.ordinal()] = 2;
        } catch (NoSuchFieldError e3) {
        }
        try {
            iArr[IFileSaver.FILE_TYPE.RAW.ordinal()] = 3;
        } catch (NoSuchFieldError e4) {
        }
        try {
            iArr[IFileSaver.FILE_TYPE.REFOCUSIMAGE.ordinal()] = 7;
        } catch (NoSuchFieldError e5) {
        }
        try {
            iArr[IFileSaver.FILE_TYPE.SLOWMOTION.ordinal()] = 4;
        } catch (NoSuchFieldError e6) {
        }
        try {
            iArr[IFileSaver.FILE_TYPE.VIDEO.ordinal()] = 5;
        } catch (NoSuchFieldError e7) {
        }
        f86commediatekcameraplatformIFileSaver$FILE_TYPESwitchesValues = iArr;
        return iArr;
    }

    public FileSaverImpl(FileSaver fileSaver) {
        Assert.assertNotNull(fileSaver);
        this.mFileSaver = fileSaver;
    }

    @Override // com.mediatek.camera.platform.IFileSaver
    public void init(IFileSaver.FILE_TYPE file_type, int i, String str, int i2) {
        Log.m31d("FileSaverImpl", "[initFileSaver]fileType= " + file_type + ",resolution = " + str + ",rotation = " + i2);
        this.mFileType = file_type;
        switch (m15xdacbdea1()[file_type.ordinal()]) {
            case 1:
                this.mSaveRequest = this.mFileSaver.preparePhotoRequest(2, 0);
                break;
            case 2:
            case 4:
            case 5:
                this.mVideoSaveRequest = this.mFileSaver.prepareVideoRequest(1, i, str, i2);
                break;
            case 3:
                this.mRawSaveRequest = this.mFileSaver.preparePhotoRequest(0, 4);
                break;
            default:
                this.mSaveRequest = this.mFileSaver.preparePhotoRequest(0, 0);
                break;
        }
    }

    @Override // com.mediatek.camera.platform.IFileSaver
    public SaveRequest getVideoSaveRequest() {
        return this.mVideoSaveRequest;
    }

    @Override // com.mediatek.camera.platform.IFileSaver
    public void setRawFlagEnabled(boolean z) {
        this.mFileSaver.enableRawFlag(z);
    }

    @Override // com.mediatek.camera.platform.IFileSaver
    public boolean saveRawFile(byte[] bArr, int i, int i2, String str, long j, Location location, int i3, IFileSaver.OnFileSavedListener onFileSavedListener) {
        Log.m31d("FileSaverImpl", "[saveRawFile]title =" + str);
        if (this.mRawSaveRequest == null || bArr == null) {
            Log.m36w("FileSaverImpl", "[savePhotoFile]fail,mRawSaveRequest = " + this.mRawSaveRequest);
            return false;
        }
        this.mListener = onFileSavedListener;
        this.mRawSaveRequest.setData(bArr);
        this.mRawSaveRequest.setSize(i, i2);
        this.mRawSaveRequest.setFileName(str);
        this.mRawSaveRequest.updateDataTaken(j);
        this.mRawSaveRequest.setIgnoreThumbnail(true);
        this.mRawSaveRequest.setLocation(location);
        this.mRawSaveRequest.setListener(this.mFileSaverListener);
        this.mRawSaveRequest.addRequest();
        this.mRawSaveRequest = null;
        return true;
    }

    @Override // com.mediatek.camera.platform.IFileSaver
    public boolean savePhotoFile(byte[] bArr, String str, long j, Location location, int i, IFileSaver.OnFileSavedListener onFileSavedListener) {
        Log.m31d("FileSaverImpl", "[savePhotoFile]title =" + str);
        if (this.mSaveRequest == null || bArr == null) {
            Log.m36w("FileSaverImpl", "[savePhotoFile]fail,mSaveRequest = " + this.mSaveRequest);
            return false;
        }
        this.mListener = onFileSavedListener;
        if (this.mSaveRequest.getDataSize() > 0) {
            Log.m31d("FileSaverImpl", "[savePhotoFile]Current SaveRequest is used, copy new one!");
            this.mSaveRequest = this.mFileSaver.copyPhotoRequest(this.mSaveRequest);
        }
        this.mSaveRequest.setData(bArr);
        this.mSaveRequest.setFileName(str);
        this.mSaveRequest.setTag(i);
        this.mSaveRequest.updateDataTaken(j);
        this.mSaveRequest.setLocation(location);
        this.mSaveRequest.setListener(this.mFileSaverListener);
        this.mSaveRequest.addRequest();
        return true;
    }

    @Override // com.mediatek.camera.platform.IFileSaver
    public boolean saveVideoFile(Location location, String str, long j, int i, IFileSaver.OnFileSavedListener onFileSavedListener) {
        Log.m31d("FileSaverImpl", "[saveVideoFile]tempPath =" + str + ",duration =" + j + ",tag =" + i);
        if (this.mVideoSaveRequest == null || str == null || onFileSavedListener == null) {
            Log.m36w("FileSaverImpl", "[saveVideoFile]fail, you should need to call init.so retrun!");
            return false;
        }
        this.mListener = onFileSavedListener;
        this.mVideoSaveRequest.setLocation(location);
        this.mVideoSaveRequest.setTempPath(str);
        this.mVideoSaveRequest.setDuration(j);
        if (this.mFileType == IFileSaver.FILE_TYPE.SLOWMOTION) {
            this.mVideoSaveRequest.setSlowMotionSpeed(i);
        } else {
            this.mVideoSaveRequest.setSlowMotionSpeed(0);
        }
        this.mVideoSaveRequest.setListener(this.mFileSaverListener);
        this.mVideoSaveRequest.addRequest();
        return true;
    }

    @Override // com.mediatek.camera.platform.IFileSaver
    public long getWaitingDataSize() {
        return this.mFileSaver.getWaitingDataSize();
    }

    @Override // com.mediatek.camera.platform.IFileSaver
    public void waitDone() {
        this.mFileSaver.waitDone();
    }

    @Override // com.mediatek.camera.platform.IFileSaver
    public boolean isEnoughSpace() {
        return 1 <= Storage.getLeftSpace();
    }

    @Override // com.mediatek.camera.platform.IFileSaver
    public long getAvailableSpace() {
        return Storage.getAvailableSpace();
    }
}
