package com.mediatek.camera.addition.thermalthrottle;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import com.mediatek.camera.ICameraAddition;
import com.mediatek.camera.ICameraContext;
import com.mediatek.camera.R;
import com.mediatek.camera.addition.CameraAddition;
import com.mediatek.camera.util.Log;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/* loaded from: classes.dex */
public class ThermalThrottle extends CameraAddition {
    private WarningDialog mAlertDialog;
    protected final Handler mHandler;
    private HandlerThread mHandlerThread;
    private boolean mIsResumed;
    private boolean mIsThermalTooHigh;
    private int mWatingTime;
    private WorkerHandler mWorkerHandler;

    public ThermalThrottle(ICameraContext iCameraContext) {
        super(iCameraContext);
        this.mHandler = new MainHandler();
        this.mIsResumed = false;
        this.mIsThermalTooHigh = false;
        this.mAlertDialog = new WarningDialog(this.mActivity);
        if (queryCPUThermalTooHigh()) {
            showThermalDlg(this.mActivity, R.string.pref_thermal_dialog_title, R.string.pref_thermal_dialog_content1);
        }
        this.mHandlerThread = new HandlerThread("ThermalThrottle-thread");
        this.mHandlerThread.start();
        this.mWorkerHandler = new WorkerHandler(this.mHandlerThread.getLooper());
        this.mWorkerHandler.sendEmptyMessageDelayed(0, 5000L);
        this.mWatingTime = 30;
    }

    @Override // com.mediatek.camera.ICameraAddition
    public boolean isSupport() {
        return true;
    }

    @Override // com.mediatek.camera.addition.CameraAddition, com.mediatek.camera.ICameraAddition
    public void resume() {
        this.mIsResumed = true;
        this.mWatingTime = 30;
        if (this.mWorkerHandler != null) {
            this.mWorkerHandler.sendEmptyMessageDelayed(0, 5000L);
        }
    }

    @Override // com.mediatek.camera.addition.CameraAddition, com.mediatek.camera.ICameraAddition
    public void pause() {
        this.mIsResumed = false;
        if (this.mWorkerHandler != null) {
            this.mWorkerHandler.removeCallbacksAndMessages(null);
        }
        if (this.mHandler != null) {
            this.mHandler.removeCallbacksAndMessages(null);
        }
    }

    @Override // com.mediatek.camera.ICameraAddition
    public void open() {
    }

    @Override // com.mediatek.camera.ICameraAddition
    public void close() {
        if (this.mWorkerHandler != null) {
            this.mWorkerHandler.getLooper().quit();
        }
        if (this.mHandlerThread != null) {
            this.mHandlerThread.quit();
        }
    }

    @Override // com.mediatek.camera.addition.CameraAddition, com.mediatek.camera.ICameraAddition
    public void destory() {
        if (this.mWorkerHandler != null) {
            this.mWorkerHandler.getLooper().quit();
        }
        if (this.mHandlerThread != null) {
            this.mHandlerThread.quit();
        }
    }

    @Override // com.mediatek.camera.addition.CameraAddition, com.mediatek.camera.ICameraAddition
    public boolean execute(ICameraAddition.AdditionActionType additionActionType, Object... objArr) {
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateCountDownTime(Activity activity) {
        Log.m31d("ThermalThrottle", "[updateCountDownTime]mCountDown = " + this.mWatingTime + ",mIsResumed = " + this.mIsResumed);
        if (isTemperTooHigh()) {
            if (this.mWatingTime > 0) {
                this.mWatingTime--;
                this.mAlertDialog.setCountDownTime(String.valueOf(this.mWatingTime));
                if (this.mIsResumed) {
                    this.mHandler.sendEmptyMessageDelayed(1, 1000L);
                    return;
                }
                return;
            }
            if (this.mWatingTime == 0) {
                this.mIFileSaver.waitDone();
                activity.finish();
                return;
            }
            return;
        }
        if (this.mAlertDialog.isShowing()) {
            this.mAlertDialog.hide();
            this.mWatingTime = 30;
        }
    }

    class MainHandler extends Handler {
        MainHandler() {
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            Log.m31d("ThermalThrottle", "[handleMessage]MainHandler,msg.what = " + message.what);
            switch (message.what) {
                case 1:
                    ThermalThrottle.this.updateCountDownTime(ThermalThrottle.this.mActivity);
                    break;
            }
        }
    }

    private class WorkerHandler extends Handler {
        public WorkerHandler(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            switch (message.what) {
                case 0:
                    if (ThermalThrottle.this.queryCPUThermalTooHigh() && (!ThermalThrottle.this.mAlertDialog.isShowing())) {
                        Log.m31d("ThermalThrottle", "[handleMessage]WorkerHandler, mCountDown = " + ThermalThrottle.this.mWatingTime);
                        if (ThermalThrottle.this.mWatingTime == 30) {
                            ThermalThrottle.this.showThermalDlg(ThermalThrottle.this.mActivity, R.string.pref_thermal_dialog_content2);
                            ThermalThrottle.this.mHandler.removeMessages(1);
                            ThermalThrottle.this.mHandler.sendEmptyMessageDelayed(1, 1000L);
                        }
                    }
                    ThermalThrottle.this.mWorkerHandler.sendEmptyMessageDelayed(0, 5000L);
                    break;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showThermalDlg(Activity activity, final int i) {
        final Runnable runnable = new Runnable() { // from class: com.mediatek.camera.addition.thermalthrottle.ThermalThrottle.1
            @Override // java.lang.Runnable
            public void run() {
                ThermalThrottle.this.mAlertDialog.hide();
            }
        };
        this.mActivity.runOnUiThread(new Runnable() { // from class: com.mediatek.camera.addition.thermalthrottle.ThermalThrottle.2
            @Override // java.lang.Runnable
            public void run() {
                ThermalThrottle.this.mAlertDialog.hide();
                ThermalThrottle.this.mAlertDialog.showAlertDialog(null, ThermalThrottle.this.mActivity.getString(i), ThermalThrottle.this.mActivity.getString(android.R.string.ok), runnable);
            }
        });
    }

    private void showThermalDlg(final Activity activity, int i, int i2) {
        new AlertDialog.Builder(activity).setCancelable(false).setIconAttribute(android.R.attr.alertDialogIcon).setTitle(i).setMessage(i2).setNeutralButton(R.string.dialog_ok, new DialogInterface.OnClickListener() { // from class: com.mediatek.camera.addition.thermalthrottle.ThermalThrottle.3
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i3) {
                activity.finish();
            }
        }).show();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean queryCPUThermalTooHigh() throws Throwable {
        String line;
        int iIntValue;
        int i = 0;
        try {
            try {
                FileReader fileReader = new FileReader("/proc/driver/cl_cam");
                BufferedReader bufferedReader = new BufferedReader(fileReader);
                line = bufferedReader.readLine();
                try {
                    iIntValue = Integer.valueOf(line).intValue();
                } catch (IOException e) {
                    e = e;
                    iIntValue = 0;
                }
                try {
                    try {
                        bufferedReader.close();
                        fileReader.close();
                        Log.m34i("Thermal", "queryCPUThermal temperInt:" + iIntValue);
                    } catch (Throwable th) {
                        th = th;
                        i = iIntValue;
                        Log.m34i("Thermal", "queryCPUThermal temperInt:" + i);
                        throw th;
                    }
                } catch (IOException e2) {
                    e = e2;
                    System.out.println(e.toString());
                    Log.m34i("Thermal", "queryCPUThermal temperInt:" + iIntValue);
                    if (line == null) {
                    }
                    this.mIsThermalTooHigh = false;
                    return this.mIsThermalTooHigh;
                }
            } catch (Throwable th2) {
                th = th2;
                Log.m34i("Thermal", "queryCPUThermal temperInt:" + i);
                throw th;
            }
        } catch (IOException e3) {
            e = e3;
            line = null;
            iIntValue = 0;
        }
        if (line == null && iIntValue == 1) {
            this.mIsThermalTooHigh = true;
            return this.mIsThermalTooHigh;
        }
        this.mIsThermalTooHigh = false;
        return this.mIsThermalTooHigh;
    }

    private boolean isTemperTooHigh() {
        return this.mIsThermalTooHigh;
    }
}
