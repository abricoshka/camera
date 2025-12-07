package android.support.v4.media.session;

import android.media.session.PlaybackState;
import android.os.Bundle;
import android.support.annotation.RequiresApi;

@RequiresApi(22)
/* loaded from: classes.dex */
class PlaybackStateCompatApi22 {
    PlaybackStateCompatApi22() {
    }

    public static Bundle getExtras(Object obj) {
        return ((PlaybackState) obj).getExtras();
    }
}
