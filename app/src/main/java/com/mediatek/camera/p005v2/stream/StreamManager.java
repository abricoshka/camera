package com.mediatek.camera.p005v2.stream;

import android.app.Activity;
import android.view.ViewGroup;
import com.mediatek.camera.debug.LogHelper;
import com.mediatek.camera.p005v2.stream.IPreviewStream;
import com.mediatek.camera.p005v2.stream.dng.DngStream;
import com.mediatek.camera.p005v2.stream.dng.IDngStream;
import com.mediatek.camera.p005v2.stream.pip.IPipStream;
import com.mediatek.camera.p005v2.stream.pip.PipStream;
import java.util.concurrent.ConcurrentHashMap;

/* loaded from: classes.dex */
public class StreamManager {
    private static final LogHelper.Tag TAG = new LogHelper.Tag(StreamManager.class.getSimpleName());
    private static ConcurrentHashMap<Activity, StreamManager> sStreamManangerList = new ConcurrentHashMap<>();
    private final Activity mActivity;
    private CaptureStream mCaptureStream;
    private DngStream mDngStream;
    private boolean mIsCaptureIntent;
    private ViewGroup mParentViewGroup;
    private PipStream mPipStream;
    private PreviewStream mPreviewStream;
    private RecordStream mRecordStream;

    private StreamManager(Activity activity) {
        this.mActivity = activity;
    }

    public static synchronized StreamManager getInstance(Activity activity) {
        StreamManager streamManager;
        streamManager = sStreamManangerList.get(activity);
        if (streamManager == null) {
            streamManager = new StreamManager(activity);
            sStreamManangerList.put(activity, streamManager);
        }
        return streamManager;
    }

    public void open(ViewGroup viewGroup, boolean z) {
        this.mParentViewGroup = viewGroup;
        this.mIsCaptureIntent = z;
    }

    public void close(Activity activity) {
        if (activity.isFinishing()) {
            LogHelper.m26i(TAG, "close activity:" + activity);
            StreamManager streamManager = sStreamManangerList.get(activity);
            sStreamManangerList.remove(activity);
            if (streamManager != null) {
                streamManager.releaseStreams();
            }
        }
    }

    public IPreviewStream getPreviewController(int i) {
        return choosePreviewStreamByModeId(i);
    }

    public IPreviewStream.PreviewCallback getPreviewCallback() {
        return getPreviewStream();
    }

    public ICaptureStream getCaptureController(int i) {
        return chooseCaptureStreamByModeId(i);
    }

    public IRecordStream getRecordController(int i) {
        return chooseRecordStreamByModeId(i);
    }

    public IDngStream getDngStreamController() {
        return getDngStream();
    }

    public IPipStream getPipStreamController() {
        return getPipStream();
    }

    protected void releaseStreams() {
        if (this.mPreviewStream != null) {
            this.mPreviewStream.releasePreviewStream();
            this.mPreviewStream = null;
        }
        if (this.mCaptureStream != null) {
            this.mCaptureStream.releaseCaptureStream();
            this.mCaptureStream = null;
        }
        if (this.mRecordStream != null) {
            this.mRecordStream.releaseRecordStream();
            this.mRecordStream = null;
        }
    }

    private IPreviewStream choosePreviewStreamByModeId(int i) {
        LogHelper.m26i(TAG, " choosePreviewStreamByModeId modeId = " + i);
        PreviewStream previewStream = getPreviewStream();
        switch (i) {
            case 0:
                return getPreviewStream();
            case 1:
            case 2:
            default:
                return previewStream;
            case 3:
                return getPipStream();
        }
    }

    private ICaptureStream chooseCaptureStreamByModeId(int i) {
        LogHelper.m26i(TAG, " chooseCaptureStreamByModeId modeId = " + i);
        CaptureStream captureStream = getCaptureStream();
        switch (i) {
            case 0:
                return getCaptureStream();
            case 1:
            case 2:
            default:
                return captureStream;
            case 3:
                return getPipStream();
        }
    }

    private IRecordStream chooseRecordStreamByModeId(int i) {
        LogHelper.m26i(TAG, " chooseRecordStreamByModeId modeId = " + i);
        RecordStream recordStream = getRecordStream();
        switch (i) {
            case 0:
                return getRecordStream();
            case 1:
            case 2:
            default:
                return recordStream;
            case 3:
                return getPipStream();
        }
    }

    private PreviewStream getPreviewStream() {
        if (this.mPreviewStream == null) {
            this.mPreviewStream = new PreviewStream();
        }
        return this.mPreviewStream;
    }

    private RecordStream getRecordStream() {
        if (this.mRecordStream == null) {
            this.mRecordStream = new RecordStream();
        }
        return this.mRecordStream;
    }

    private CaptureStream getCaptureStream() {
        if (this.mCaptureStream == null) {
            this.mCaptureStream = new CaptureStream();
        }
        return this.mCaptureStream;
    }

    private DngStream getDngStream() {
        if (this.mDngStream == null) {
            this.mDngStream = new DngStream(getCaptureStream());
        }
        return this.mDngStream;
    }

    private PipStream getPipStream() {
        if (this.mPipStream == null) {
            this.mPipStream = new PipStream(getPreviewStream(), getCaptureStream(), getRecordStream());
        }
        return this.mPipStream;
    }
}
