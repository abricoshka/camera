package com.android.camera.p002v2.app.location;

import android.content.Context;
import android.location.Location;

/* loaded from: classes.dex */
public class LocationManager {
    ILocationProvider mLocationProvider;
    private boolean mRecordLocation;

    public LocationManager(Context context) {
        this.mLocationProvider = new LocationProviderImpl(context);
    }

    public void recordLocation(boolean z) {
        this.mRecordLocation = z;
        this.mLocationProvider.recordLocation(this.mRecordLocation);
    }

    public Location getCurrentLocation() {
        return this.mLocationProvider.getCurrentLocation();
    }
}
