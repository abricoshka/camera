package android.support.v4.media.session;

import android.os.Bundle;
import android.os.RemoteException;
import android.os.ResultReceiver;
import android.support.annotation.RequiresApi;
import android.support.v4.app.BundleCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.IMediaSession;
import android.support.v4.media.session.MediaControllerCompat$Callback;
import android.util.Log;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;

@RequiresApi(21)
/* loaded from: classes.dex */
class MediaControllerCompat$MediaControllerImplApi21 {
    private HashMap<MediaControllerCompat$Callback, ExtraCallback> mCallbackMap;
    private IMediaSession mExtraBinder;
    private final List<MediaControllerCompat$Callback> mPendingCallbacks;

    /* JADX INFO: Access modifiers changed from: private */
    public void processPendingCallbacks() {
        if (this.mExtraBinder == null) {
            return;
        }
        synchronized (this.mPendingCallbacks) {
            for (MediaControllerCompat$Callback mediaControllerCompat$Callback : this.mPendingCallbacks) {
                ExtraCallback extraCallback = new ExtraCallback(mediaControllerCompat$Callback);
                this.mCallbackMap.put(mediaControllerCompat$Callback, extraCallback);
                mediaControllerCompat$Callback.mHasExtraCallback = true;
                try {
                    this.mExtraBinder.registerCallbackListener(extraCallback);
                    mediaControllerCompat$Callback.onSessionReady();
                } catch (RemoteException e) {
                    Log.e("MediaControllerCompat", "Dead object in registerCallback.", e);
                }
            }
            this.mPendingCallbacks.clear();
        }
    }

    private static class ExtraBinderRequestResultReceiver extends ResultReceiver {
        private WeakReference<MediaControllerCompat$MediaControllerImplApi21> mMediaControllerImpl;

        @Override // android.os.ResultReceiver
        protected void onReceiveResult(int i, Bundle bundle) {
            MediaControllerCompat$MediaControllerImplApi21 mediaControllerCompat$MediaControllerImplApi21 = this.mMediaControllerImpl.get();
            if (mediaControllerCompat$MediaControllerImplApi21 == null || bundle == null) {
                return;
            }
            mediaControllerCompat$MediaControllerImplApi21.mExtraBinder = IMediaSession.Stub.asInterface(BundleCompat.getBinder(bundle, "android.support.v4.media.session.EXTRA_BINDER"));
            mediaControllerCompat$MediaControllerImplApi21.processPendingCallbacks();
        }
    }

    private static class ExtraCallback extends MediaControllerCompat$Callback.StubCompat {
        ExtraCallback(MediaControllerCompat$Callback mediaControllerCompat$Callback) {
            super(mediaControllerCompat$Callback);
        }

        @Override // android.support.v4.media.session.MediaControllerCompat$Callback.StubCompat, android.support.v4.media.session.IMediaControllerCallback
        public void onSessionDestroyed() throws RemoteException {
            throw new AssertionError();
        }

        @Override // android.support.v4.media.session.MediaControllerCompat$Callback.StubCompat, android.support.v4.media.session.IMediaControllerCallback
        public void onMetadataChanged(MediaMetadataCompat mediaMetadataCompat) throws RemoteException {
            throw new AssertionError();
        }

        @Override // android.support.v4.media.session.MediaControllerCompat$Callback.StubCompat, android.support.v4.media.session.IMediaControllerCallback
        public void onQueueChanged(List<MediaSessionCompat$QueueItem> list) throws RemoteException {
            throw new AssertionError();
        }

        @Override // android.support.v4.media.session.MediaControllerCompat$Callback.StubCompat, android.support.v4.media.session.IMediaControllerCallback
        public void onQueueTitleChanged(CharSequence charSequence) throws RemoteException {
            throw new AssertionError();
        }

        @Override // android.support.v4.media.session.MediaControllerCompat$Callback.StubCompat, android.support.v4.media.session.IMediaControllerCallback
        public void onExtrasChanged(Bundle bundle) throws RemoteException {
            throw new AssertionError();
        }

        @Override // android.support.v4.media.session.MediaControllerCompat$Callback.StubCompat, android.support.v4.media.session.IMediaControllerCallback
        public void onVolumeInfoChanged(ParcelableVolumeInfo parcelableVolumeInfo) throws RemoteException {
            throw new AssertionError();
        }
    }
}
