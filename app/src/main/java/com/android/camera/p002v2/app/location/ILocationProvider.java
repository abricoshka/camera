package com.android.camera.p002v2.app.location;

import android.location.Location;

/* loaded from: classes.dex */
public interface ILocationProvider {
    Location getCurrentLocation();

    void recordLocation(boolean z);
}
