package com.android.camera.externaldevice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.display.DisplayManager;
import android.hardware.display.WifiDisplayStatus;
import com.android.camera.CameraActivity;
import com.android.camera.externaldevice.IExternalDeviceCtrl;
import com.mediatek.camera.util.Log;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/* loaded from: classes.dex */
public class WfdDeviceCtrl implements IExternalDeviceCtrl {
    private CameraActivity mContext;
    private DisplayManager mDisplayManager;
    private List<IExternalDeviceCtrl.Listener> mListeners = new CopyOnWriteArrayList();
    private BroadcastReceiver mReceiver = new BroadcastReceiver() { // from class: com.android.camera.externaldevice.WfdDeviceCtrl.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("android.hardware.display.action.WIFI_DISPLAY_STATUS_CHANGED")) {
                WfdDeviceCtrl.this.mWifiDisplayStatus = intent.getParcelableExtra("android.hardware.display.extra.WIFI_DISPLAY_STATUS");
                WfdDeviceCtrl.this.notifyStateChanged(WfdDeviceCtrl.this.isWfdEnabled());
            }
        }
    };
    private WifiDisplayStatus mWifiDisplayStatus;

    public WfdDeviceCtrl(CameraActivity cameraActivity) {
        this.mContext = cameraActivity;
        this.mDisplayManager = (DisplayManager) this.mContext.getSystemService("display");
    }

    @Override // com.android.camera.externaldevice.IExternalDeviceCtrl
    public boolean onCreate() {
        return false;
    }

    @Override // com.android.camera.externaldevice.IExternalDeviceCtrl
    public boolean onResume() {
        this.mContext.registerReceiver(this.mReceiver, new IntentFilter("android.hardware.display.action.WIFI_DISPLAY_STATUS_CHANGED"));
        this.mWifiDisplayStatus = null;
        notifyStateChanged(isWfdEnabled());
        return false;
    }

    @Override // com.android.camera.externaldevice.IExternalDeviceCtrl
    public boolean onPause() {
        this.mContext.unregisterReceiver(this.mReceiver);
        this.mWifiDisplayStatus = null;
        return false;
    }

    @Override // com.android.camera.externaldevice.IExternalDeviceCtrl
    public void addListener(Object obj) {
        IExternalDeviceCtrl.Listener listener = (IExternalDeviceCtrl.Listener) obj;
        if (!this.mListeners.contains(listener)) {
            this.mListeners.add(listener);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isWfdEnabled() {
        if (this.mWifiDisplayStatus == null) {
            this.mWifiDisplayStatus = this.mDisplayManager.getWifiDisplayStatus();
        }
        boolean z = this.mWifiDisplayStatus.getActiveDisplayState() == 2;
        Log.m31d("WfdDeviceCtrl", "[isWfdEnabled()] mWifiDisplayStatus=" + this.mWifiDisplayStatus + ", return " + z);
        return z;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyStateChanged(boolean z) {
        Log.m31d("WfdDeviceCtrl", "[notifyStateChanged](" + z + ")");
        for (IExternalDeviceCtrl.Listener listener : this.mListeners) {
            if (listener != null) {
                listener.onStateChanged(z);
            }
        }
    }
}
