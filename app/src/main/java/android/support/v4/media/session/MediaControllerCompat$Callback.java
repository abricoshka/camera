package android.support.v4.media.session;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.v4.app.FrameMetricsAggregator;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.IMediaControllerCallback;
import android.support.v4.media.session.MediaControllerCompatApi21;
import java.lang.ref.WeakReference;
import java.util.List;

/* loaded from: classes.dex */
public abstract class MediaControllerCompat$Callback implements IBinder.DeathRecipient {
    private final Object mCallbackObj;
    MessageHandler mHandler;
    boolean mHasExtraCallback;

    public MediaControllerCompat$Callback() {
        if (Build.VERSION.SDK_INT >= 21) {
            this.mCallbackObj = MediaControllerCompatApi21.createCallback(new StubApi21(this));
        } else {
            this.mCallbackObj = new StubCompat(this);
        }
    }

    public void onSessionReady() {
    }

    public void onSessionDestroyed() {
    }

    public void onSessionEvent(String str, Bundle bundle) {
    }

    public void onPlaybackStateChanged(PlaybackStateCompat playbackStateCompat) {
    }

    public void onMetadataChanged(MediaMetadataCompat mediaMetadataCompat) {
    }

    public void onQueueChanged(List<MediaSessionCompat$QueueItem> list) {
    }

    public void onQueueTitleChanged(CharSequence charSequence) {
    }

    public void onExtrasChanged(Bundle bundle) {
    }

    public void onAudioInfoChanged(MediaControllerCompat$PlaybackInfo mediaControllerCompat$PlaybackInfo) {
    }

    public void onCaptioningEnabledChanged(boolean z) {
    }

    public void onRepeatModeChanged(int i) {
    }

    @Deprecated
    public void onShuffleModeChanged(boolean z) {
    }

    public void onShuffleModeChanged(int i) {
    }

    @Override // android.os.IBinder.DeathRecipient
    public void binderDied() {
        onSessionDestroyed();
    }

    void postToHandler(int i, Object obj, Bundle bundle) {
        if (this.mHandler != null) {
            Message messageObtainMessage = this.mHandler.obtainMessage(i, obj);
            messageObtainMessage.setData(bundle);
            messageObtainMessage.sendToTarget();
        }
    }

    private static class StubApi21 implements MediaControllerCompatApi21.Callback {
        private final WeakReference<MediaControllerCompat$Callback> mCallback;

        StubApi21(MediaControllerCompat$Callback mediaControllerCompat$Callback) {
            this.mCallback = new WeakReference<>(mediaControllerCompat$Callback);
        }

        @Override // android.support.v4.media.session.MediaControllerCompatApi21.Callback
        public void onSessionDestroyed() {
            MediaControllerCompat$Callback mediaControllerCompat$Callback = this.mCallback.get();
            if (mediaControllerCompat$Callback != null) {
                mediaControllerCompat$Callback.onSessionDestroyed();
            }
        }

        @Override // android.support.v4.media.session.MediaControllerCompatApi21.Callback
        public void onSessionEvent(String str, Bundle bundle) {
            MediaControllerCompat$Callback mediaControllerCompat$Callback = this.mCallback.get();
            if (mediaControllerCompat$Callback != null) {
                if (!mediaControllerCompat$Callback.mHasExtraCallback || Build.VERSION.SDK_INT >= 23) {
                    mediaControllerCompat$Callback.onSessionEvent(str, bundle);
                }
            }
        }

        @Override // android.support.v4.media.session.MediaControllerCompatApi21.Callback
        public void onPlaybackStateChanged(Object obj) {
            MediaControllerCompat$Callback mediaControllerCompat$Callback = this.mCallback.get();
            if (mediaControllerCompat$Callback != null && !mediaControllerCompat$Callback.mHasExtraCallback) {
                mediaControllerCompat$Callback.onPlaybackStateChanged(PlaybackStateCompat.fromPlaybackState(obj));
            }
        }

        @Override // android.support.v4.media.session.MediaControllerCompatApi21.Callback
        public void onMetadataChanged(Object obj) {
            MediaControllerCompat$Callback mediaControllerCompat$Callback = this.mCallback.get();
            if (mediaControllerCompat$Callback != null) {
                mediaControllerCompat$Callback.onMetadataChanged(MediaMetadataCompat.fromMediaMetadata(obj));
            }
        }

        @Override // android.support.v4.media.session.MediaControllerCompatApi21.Callback
        public void onQueueChanged(List<?> list) {
            MediaControllerCompat$Callback mediaControllerCompat$Callback = this.mCallback.get();
            if (mediaControllerCompat$Callback != null) {
                mediaControllerCompat$Callback.onQueueChanged(MediaSessionCompat$QueueItem.fromQueueItemList(list));
            }
        }

        @Override // android.support.v4.media.session.MediaControllerCompatApi21.Callback
        public void onQueueTitleChanged(CharSequence charSequence) {
            MediaControllerCompat$Callback mediaControllerCompat$Callback = this.mCallback.get();
            if (mediaControllerCompat$Callback != null) {
                mediaControllerCompat$Callback.onQueueTitleChanged(charSequence);
            }
        }

        @Override // android.support.v4.media.session.MediaControllerCompatApi21.Callback
        public void onExtrasChanged(Bundle bundle) {
            MediaControllerCompat$Callback mediaControllerCompat$Callback = this.mCallback.get();
            if (mediaControllerCompat$Callback != null) {
                mediaControllerCompat$Callback.onExtrasChanged(bundle);
            }
        }

        @Override // android.support.v4.media.session.MediaControllerCompatApi21.Callback
        public void onAudioInfoChanged(int i, int i2, int i3, int i4, int i5) {
            MediaControllerCompat$Callback mediaControllerCompat$Callback = this.mCallback.get();
            if (mediaControllerCompat$Callback != null) {
                mediaControllerCompat$Callback.onAudioInfoChanged(new MediaControllerCompat$PlaybackInfo(i, i2, i3, i4, i5));
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    static class StubCompat extends IMediaControllerCallback.Stub {
        private final WeakReference<MediaControllerCompat$Callback> mCallback;

        StubCompat(MediaControllerCompat$Callback mediaControllerCompat$Callback) {
            this.mCallback = new WeakReference<>(mediaControllerCompat$Callback);
        }

        @Override // android.support.v4.media.session.IMediaControllerCallback
        public void onEvent(String str, Bundle bundle) throws RemoteException {
            MediaControllerCompat$Callback mediaControllerCompat$Callback = this.mCallback.get();
            if (mediaControllerCompat$Callback != null) {
                mediaControllerCompat$Callback.postToHandler(1, str, bundle);
            }
        }

        @Override // android.support.v4.media.session.IMediaControllerCallback
        public void onSessionDestroyed() throws RemoteException {
            MediaControllerCompat$Callback mediaControllerCompat$Callback = this.mCallback.get();
            if (mediaControllerCompat$Callback != null) {
                mediaControllerCompat$Callback.postToHandler(8, null, null);
            }
        }

        @Override // android.support.v4.media.session.IMediaControllerCallback
        public void onPlaybackStateChanged(PlaybackStateCompat playbackStateCompat) throws RemoteException {
            MediaControllerCompat$Callback mediaControllerCompat$Callback = this.mCallback.get();
            if (mediaControllerCompat$Callback != null) {
                mediaControllerCompat$Callback.postToHandler(2, playbackStateCompat, null);
            }
        }

        @Override // android.support.v4.media.session.IMediaControllerCallback
        public void onMetadataChanged(MediaMetadataCompat mediaMetadataCompat) throws RemoteException {
            MediaControllerCompat$Callback mediaControllerCompat$Callback = this.mCallback.get();
            if (mediaControllerCompat$Callback != null) {
                mediaControllerCompat$Callback.postToHandler(3, mediaMetadataCompat, null);
            }
        }

        @Override // android.support.v4.media.session.IMediaControllerCallback
        public void onQueueChanged(List<MediaSessionCompat$QueueItem> list) throws RemoteException {
            MediaControllerCompat$Callback mediaControllerCompat$Callback = this.mCallback.get();
            if (mediaControllerCompat$Callback != null) {
                mediaControllerCompat$Callback.postToHandler(5, list, null);
            }
        }

        @Override // android.support.v4.media.session.IMediaControllerCallback
        public void onQueueTitleChanged(CharSequence charSequence) throws RemoteException {
            MediaControllerCompat$Callback mediaControllerCompat$Callback = this.mCallback.get();
            if (mediaControllerCompat$Callback != null) {
                mediaControllerCompat$Callback.postToHandler(6, charSequence, null);
            }
        }

        @Override // android.support.v4.media.session.IMediaControllerCallback
        public void onCaptioningEnabledChanged(boolean z) throws RemoteException {
            MediaControllerCompat$Callback mediaControllerCompat$Callback = this.mCallback.get();
            if (mediaControllerCompat$Callback != null) {
                mediaControllerCompat$Callback.postToHandler(11, Boolean.valueOf(z), null);
            }
        }

        @Override // android.support.v4.media.session.IMediaControllerCallback
        public void onRepeatModeChanged(int i) throws RemoteException {
            MediaControllerCompat$Callback mediaControllerCompat$Callback = this.mCallback.get();
            if (mediaControllerCompat$Callback != null) {
                mediaControllerCompat$Callback.postToHandler(9, Integer.valueOf(i), null);
            }
        }

        @Override // android.support.v4.media.session.IMediaControllerCallback
        public void onShuffleModeChangedDeprecated(boolean z) throws RemoteException {
            MediaControllerCompat$Callback mediaControllerCompat$Callback = this.mCallback.get();
            if (mediaControllerCompat$Callback != null) {
                mediaControllerCompat$Callback.postToHandler(10, Boolean.valueOf(z), null);
            }
        }

        @Override // android.support.v4.media.session.IMediaControllerCallback
        public void onShuffleModeChanged(int i) throws RemoteException {
            MediaControllerCompat$Callback mediaControllerCompat$Callback = this.mCallback.get();
            if (mediaControllerCompat$Callback != null) {
                mediaControllerCompat$Callback.postToHandler(12, Integer.valueOf(i), null);
            }
        }

        @Override // android.support.v4.media.session.IMediaControllerCallback
        public void onExtrasChanged(Bundle bundle) throws RemoteException {
            MediaControllerCompat$Callback mediaControllerCompat$Callback = this.mCallback.get();
            if (mediaControllerCompat$Callback != null) {
                mediaControllerCompat$Callback.postToHandler(7, bundle, null);
            }
        }

        @Override // android.support.v4.media.session.IMediaControllerCallback
        public void onVolumeInfoChanged(ParcelableVolumeInfo parcelableVolumeInfo) throws RemoteException {
            MediaControllerCompat$Callback mediaControllerCompat$Callback = this.mCallback.get();
            if (mediaControllerCompat$Callback != null) {
                mediaControllerCompat$Callback.postToHandler(4, parcelableVolumeInfo != null ? new MediaControllerCompat$PlaybackInfo(parcelableVolumeInfo.volumeType, parcelableVolumeInfo.audioStream, parcelableVolumeInfo.controlType, parcelableVolumeInfo.maxVolume, parcelableVolumeInfo.currentVolume) : null, null);
            }
        }

        @Override // android.support.v4.media.session.IMediaControllerCallback
        public void onSessionReady() throws RemoteException {
            MediaControllerCompat$Callback mediaControllerCompat$Callback = this.mCallback.get();
            if (mediaControllerCompat$Callback != null) {
                mediaControllerCompat$Callback.postToHandler(13, null, null);
            }
        }
    }

    private class MessageHandler extends Handler {
        boolean mRegistered;
        final /* synthetic */ MediaControllerCompat$Callback this$1;

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            if (!this.mRegistered) {
            }
            switch (message.what) {
                case 1:
                    this.this$1.onSessionEvent((String) message.obj, message.getData());
                    break;
                case 2:
                    this.this$1.onPlaybackStateChanged((PlaybackStateCompat) message.obj);
                    break;
                case 3:
                    this.this$1.onMetadataChanged((MediaMetadataCompat) message.obj);
                    break;
                case 4:
                    this.this$1.onAudioInfoChanged((MediaControllerCompat$PlaybackInfo) message.obj);
                    break;
                case 5:
                    this.this$1.onQueueChanged((List) message.obj);
                    break;
                case 6:
                    this.this$1.onQueueTitleChanged((CharSequence) message.obj);
                    break;
                case FrameMetricsAggregator.DELAY_INDEX /* 7 */:
                    this.this$1.onExtrasChanged((Bundle) message.obj);
                    break;
                case 8:
                    this.this$1.onSessionDestroyed();
                    break;
                case 9:
                    this.this$1.onRepeatModeChanged(((Integer) message.obj).intValue());
                    break;
                case 10:
                    this.this$1.onShuffleModeChanged(((Boolean) message.obj).booleanValue());
                    break;
                case 11:
                    this.this$1.onCaptioningEnabledChanged(((Boolean) message.obj).booleanValue());
                    break;
                case 12:
                    this.this$1.onShuffleModeChanged(((Integer) message.obj).intValue());
                    break;
                case 13:
                    this.this$1.onSessionReady();
                    break;
            }
        }
    }
}
