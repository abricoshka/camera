package com.android.camera;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import java.util.LinkedList;
import java.util.List;

/* loaded from: classes.dex */
public class FileSaverService extends Service {
    private SaveTask mContinuousSaveTask;
    private int mTaskNumber;
    private List<SaveRequest> mQueue = new LinkedList();
    private final Binder mBinder = new LocalBinder();
    private Object mListnerObject = new Object();

    public interface FileSaverListener {
        void onFileSaved(SaveRequest saveRequest);

        void onSaveDone();
    }

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        return this.mBinder;
    }

    @Override // android.app.Service
    public int onStartCommand(Intent intent, int i, int i2) {
        return 1;
    }

    @Override // android.app.Service
    public void onCreate() {
        this.mTaskNumber = 0;
    }

    public boolean isNoneSaveTask() {
        return this.mTaskNumber == 0;
    }

    public long getWaitingDataSize() {
        long j;
        long dataSize = 0;
        synchronized (this.mQueue) {
            while (true) {
                j = dataSize;
                if (this.mQueue.iterator().hasNext()) {
                    dataSize = ((SaveRequest) r5.next()).getDataSize() + j;
                }
            }
        }
        return j;
    }

    public int getWaitingCount() {
        int size;
        synchronized (this.mQueue) {
            size = this.mQueue.size();
        }
        return size;
    }

    public boolean isQueueFull() {
        Log.m10v("FileSaverService", "isQueueFull, mTaskNumber= " + this.mTaskNumber);
        return this.mTaskNumber >= 3;
    }

    public void addSaveRequest(SaveRequest saveRequest) {
        synchronized (this.mQueue) {
            this.mQueue.add(saveRequest);
        }
        if (this.mContinuousSaveTask == null) {
            this.mContinuousSaveTask = new SaveTask();
            this.mTaskNumber++;
            this.mContinuousSaveTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
            Log.m5d("FileSaverService", "[addSaveRequest]execute continuous AsyncTask.");
        }
    }

    class LocalBinder extends Binder {
        LocalBinder() {
        }

        public FileSaverService getService() {
            return FileSaverService.this;
        }
    }

    private class SaveTask extends AsyncTask<Void, Void, Void> {

        /* renamed from: r */
        SaveRequest f58r;

        public SaveTask() {
        }

        @Override // android.os.AsyncTask
        protected void onPreExecute() {
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public Void doInBackground(Void... voidArr) {
            FileSaverListener fileSaverListener = null;
            while (!FileSaverService.this.mQueue.isEmpty()) {
                this.f58r = (SaveRequest) FileSaverService.this.mQueue.get(0);
                if (fileSaverListener != null && this.f58r.getFileSaverListener() != fileSaverListener) {
                    fileSaverListener.onSaveDone();
                }
                if (Storage.isStorageReady()) {
                    try {
                        this.f58r.saveRequest();
                    } catch (RuntimeException e) {
                        e.printStackTrace();
                    }
                }
                this.f58r.notifyListener();
                synchronized (FileSaverService.this.mQueue) {
                    FileSaverService.this.mQueue.remove(0);
                }
                synchronized (FileSaverService.this.mListnerObject) {
                    this.f58r.getFileSaverListener().onFileSaved(this.f58r);
                }
                fileSaverListener = this.f58r.getFileSaverListener();
            }
            FileSaverService.this.mContinuousSaveTask = null;
            FileSaverService fileSaverService = FileSaverService.this;
            fileSaverService.mTaskNumber--;
            synchronized (FileSaverService.this.mListnerObject) {
                this.f58r.getFileSaverListener().onSaveDone();
            }
            return null;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(Void r1) {
        }
    }
}
