package android.support.v4.media;

import android.os.Build;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.media.MediaBrowserCompatApi23;
import android.support.v4.os.ResultReceiver;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public final class MediaBrowserCompat {
    static final boolean DEBUG = Log.isLoggable("MediaBrowserCompat", 3);

    public static class MediaItem implements Parcelable {
        public static final Parcelable.Creator<MediaItem> CREATOR = new Parcelable.Creator<MediaItem>() { // from class: android.support.v4.media.MediaBrowserCompat.MediaItem.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public MediaItem createFromParcel(Parcel parcel) {
                return new MediaItem(parcel);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public MediaItem[] newArray(int i) {
                return new MediaItem[i];
            }
        };
        private final MediaDescriptionCompat mDescription;
        private final int mFlags;

        MediaItem(Parcel parcel) {
            this.mFlags = parcel.readInt();
            this.mDescription = MediaDescriptionCompat.CREATOR.createFromParcel(parcel);
        }

        @Override // android.os.Parcelable
        public int describeContents() {
            return 0;
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeInt(this.mFlags);
            this.mDescription.writeToParcel(parcel, i);
        }

        public String toString() {
            StringBuilder sb = new StringBuilder("MediaItem{");
            sb.append("mFlags=").append(this.mFlags);
            sb.append(", mDescription=").append(this.mDescription);
            sb.append('}');
            return sb.toString();
        }
    }

    public static abstract class ItemCallback {
        final Object mItemCallbackObj;

        public ItemCallback() {
            if (Build.VERSION.SDK_INT >= 23) {
                this.mItemCallbackObj = MediaBrowserCompatApi23.createItemCallback(new StubApi23());
            } else {
                this.mItemCallbackObj = null;
            }
        }

        public void onItemLoaded(MediaItem mediaItem) {
        }

        public void onError(@NonNull String str) {
        }

        private class StubApi23 implements MediaBrowserCompatApi23.ItemCallback {
            StubApi23() {
            }

            @Override // android.support.v4.media.MediaBrowserCompatApi23.ItemCallback
            public void onItemLoaded(Parcel parcel) {
                if (parcel == null) {
                    ItemCallback.this.onItemLoaded(null);
                    return;
                }
                parcel.setDataPosition(0);
                MediaItem mediaItemCreateFromParcel = MediaItem.CREATOR.createFromParcel(parcel);
                parcel.recycle();
                ItemCallback.this.onItemLoaded(mediaItemCreateFromParcel);
            }

            @Override // android.support.v4.media.MediaBrowserCompatApi23.ItemCallback
            public void onError(@NonNull String str) {
                ItemCallback.this.onError(str);
            }
        }
    }

    public static abstract class SearchCallback {
        public void onSearchResult(@NonNull String str, Bundle bundle, @NonNull List<MediaItem> list) {
        }

        public void onError(@NonNull String str, Bundle bundle) {
        }
    }

    public static abstract class CustomActionCallback {
        public void onProgressUpdate(String str, Bundle bundle, Bundle bundle2) {
        }

        public void onResult(String str, Bundle bundle, Bundle bundle2) {
        }

        public void onError(String str, Bundle bundle, Bundle bundle2) {
        }
    }

    private static class ItemReceiver extends ResultReceiver {
        private final ItemCallback mCallback;
        private final String mMediaId;

        @Override // android.support.v4.os.ResultReceiver
        protected void onReceiveResult(int i, Bundle bundle) {
            if (bundle != null) {
                bundle.setClassLoader(MediaBrowserCompat.class.getClassLoader());
            }
            if (i != 0 || bundle == null || (!bundle.containsKey("media_item"))) {
                this.mCallback.onError(this.mMediaId);
                return;
            }
            Parcelable parcelable = bundle.getParcelable("media_item");
            if (parcelable == null || (parcelable instanceof MediaItem)) {
                this.mCallback.onItemLoaded((MediaItem) parcelable);
            } else {
                this.mCallback.onError(this.mMediaId);
            }
        }
    }

    private static class SearchResultReceiver extends ResultReceiver {
        private final SearchCallback mCallback;
        private final Bundle mExtras;
        private final String mQuery;

        @Override // android.support.v4.os.ResultReceiver
        protected void onReceiveResult(int i, Bundle bundle) {
            ArrayList arrayList = null;
            if (bundle != null) {
                bundle.setClassLoader(MediaBrowserCompat.class.getClassLoader());
            }
            if (i != 0 || bundle == null || (!bundle.containsKey("search_results"))) {
                this.mCallback.onError(this.mQuery, this.mExtras);
                return;
            }
            Parcelable[] parcelableArray = bundle.getParcelableArray("search_results");
            if (parcelableArray != null) {
                ArrayList arrayList2 = new ArrayList();
                for (Parcelable parcelable : parcelableArray) {
                    arrayList2.add((MediaItem) parcelable);
                }
                arrayList = arrayList2;
            }
            this.mCallback.onSearchResult(this.mQuery, this.mExtras, arrayList);
        }
    }

    private static class CustomActionResultReceiver extends ResultReceiver {
        private final String mAction;
        private final CustomActionCallback mCallback;
        private final Bundle mExtras;

        @Override // android.support.v4.os.ResultReceiver
        protected void onReceiveResult(int i, Bundle bundle) {
            if (this.mCallback == null) {
            }
            switch (i) {
                case -1:
                    this.mCallback.onError(this.mAction, this.mExtras, bundle);
                    break;
                case 0:
                    this.mCallback.onResult(this.mAction, this.mExtras, bundle);
                    break;
                case 1:
                    this.mCallback.onProgressUpdate(this.mAction, this.mExtras, bundle);
                    break;
                default:
                    Log.w("MediaBrowserCompat", "Unknown result code: " + i + " (extras=" + this.mExtras + ", resultData=" + bundle + ")");
                    break;
            }
        }
    }
}
