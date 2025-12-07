package com.mediatek.camera.p005v2.services.storage;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import com.mediatek.camera.p005v2.services.storage.IStorageService;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

/* loaded from: classes.dex */
public class StorageMonitor {
    private Context mContext;
    private CopyOnWriteArrayList<IStorageService.IStorageStateListener> mIStorageStateListener = new CopyOnWriteArrayList<>();
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() { // from class: com.mediatek.camera.v2.services.storage.StorageMonitor.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            Log.i("StorageMonitor", "mReceiver.onReceive(" + intent + ")");
            if (intent.getAction() == null) {
                Log.d("StorageMonitor", "[mReceiver.onReceive] action is null");
                return;
            }
            String action = intent.getAction();
            if (action.equals("android.intent.action.MEDIA_EJECT")) {
                if (Storage.isSameStorage(intent)) {
                    Iterator it = StorageMonitor.this.mIStorageStateListener.iterator();
                    while (it.hasNext()) {
                        ((IStorageService.IStorageStateListener) it.next()).onStorageStateChanged(0);
                    }
                    return;
                }
                return;
            }
            if (action.equals("android.intent.action.MEDIA_UNMOUNTED") || action.equals("android.intent.action.MEDIA_MOUNTED") || action.equals("android.intent.action.MEDIA_SCANNER_FINISHED")) {
                Storage.updateDefaultDirectory();
                Iterator it2 = StorageMonitor.this.mIStorageStateListener.iterator();
                while (it2.hasNext()) {
                    ((IStorageService.IStorageStateListener) it2.next()).onStorageStateChanged(Storage.isStorageReady() ? 1 : 0);
                }
                return;
            }
            if ((action.equals("android.intent.action.MEDIA_CHECKING") || action.equals("android.intent.action.MEDIA_SCANNER_STARTED")) && Storage.isSameStorage(intent)) {
                Iterator it3 = StorageMonitor.this.mIStorageStateListener.iterator();
                while (it3.hasNext()) {
                    ((IStorageService.IStorageStateListener) it3.next()).onStorageStateChanged(2);
                }
            }
        }
    };

    public StorageMonitor(Context context) {
        this.mContext = context;
    }

    public void registerStorageStateListener(IStorageService.IStorageStateListener iStorageStateListener) {
        Log.i("StorageMonitor", "[registerStorageStateListener], listener:" + iStorageStateListener);
        Storage.updateDefaultDirectory();
        if (iStorageStateListener != null && (!this.mIStorageStateListener.contains(iStorageStateListener))) {
            this.mIStorageStateListener.add(iStorageStateListener);
        }
    }

    public void unRegisterStorageStateListener(IStorageService.IStorageStateListener iStorageStateListener) {
        Log.i("StorageMonitor", "[unRegisterStorageStateListener], listener:" + iStorageStateListener);
        this.mIStorageStateListener.remove(iStorageStateListener);
    }

    public void registerIntentFilter() {
        IntentFilter intentFilter = new IntentFilter("android.intent.action.MEDIA_MOUNTED");
        intentFilter.addAction("android.intent.action.MEDIA_EJECT");
        intentFilter.addAction("android.intent.action.MEDIA_UNMOUNTED");
        intentFilter.addAction("android.intent.action.MEDIA_SCANNER_STARTED");
        intentFilter.addAction("android.intent.action.MEDIA_SCANNER_FINISHED");
        intentFilter.addAction("android.intent.action.MEDIA_CHECKING");
        intentFilter.addDataScheme("file");
        this.mContext.registerReceiver(this.mReceiver, intentFilter);
    }

    public void unregisterIntentFilter() {
        this.mContext.unregisterReceiver(this.mReceiver);
    }
}
