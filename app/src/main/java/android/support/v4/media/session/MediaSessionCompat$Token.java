package android.support.v4.media.session;

import android.os.Build;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;

/* loaded from: classes.dex */
public final class MediaSessionCompat$Token implements Parcelable {
    public static final Parcelable.Creator<MediaSessionCompat$Token> CREATOR = new Parcelable.Creator<MediaSessionCompat$Token>() { // from class: android.support.v4.media.session.MediaSessionCompat$Token.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public MediaSessionCompat$Token createFromParcel(Parcel parcel) {
            Object strongBinder;
            if (Build.VERSION.SDK_INT >= 21) {
                strongBinder = parcel.readParcelable(null);
            } else {
                strongBinder = parcel.readStrongBinder();
            }
            return new MediaSessionCompat$Token(strongBinder);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public MediaSessionCompat$Token[] newArray(int i) {
            return new MediaSessionCompat$Token[i];
        }
    };
    private final IMediaSession mExtraBinder;
    private final Object mInner;

    MediaSessionCompat$Token(Object obj) {
        this(obj, null);
    }

    MediaSessionCompat$Token(Object obj, IMediaSession iMediaSession) {
        this.mInner = obj;
        this.mExtraBinder = iMediaSession;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        if (Build.VERSION.SDK_INT >= 21) {
            parcel.writeParcelable((Parcelable) this.mInner, i);
        } else {
            parcel.writeStrongBinder((IBinder) this.mInner);
        }
    }

    public int hashCode() {
        if (this.mInner == null) {
            return 0;
        }
        return this.mInner.hashCode();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof MediaSessionCompat$Token)) {
            return false;
        }
        MediaSessionCompat$Token mediaSessionCompat$Token = (MediaSessionCompat$Token) obj;
        if (this.mInner == null) {
            return mediaSessionCompat$Token.mInner == null;
        }
        if (mediaSessionCompat$Token.mInner == null) {
            return false;
        }
        return this.mInner.equals(mediaSessionCompat$Token.mInner);
    }
}
