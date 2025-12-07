package com.android.camera.p002v2.app.location;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import com.mediatek.camera.debug.LogHelper;

/* loaded from: classes.dex */
public class LocationProviderImpl implements ILocationProvider {
    private static final LogHelper.Tag TAG = new LogHelper.Tag(LocationProviderImpl.class.getSimpleName());
    private Context mContext;
    LocationListener[] mLocationListeners = {new LocationListener("gps"), new LocationListener("network")};
    private LocationManager mLocationManager;
    private boolean mRecordLocation;

    public LocationProviderImpl(Context context) {
        this.mContext = context;
    }

    @Override // com.android.camera.p002v2.app.location.ILocationProvider
    public Location getCurrentLocation() {
        if (!this.mRecordLocation) {
            return null;
        }
        for (int i = 0; i < this.mLocationListeners.length; i++) {
            Location locationCurrent = this.mLocationListeners[i].current();
            if (locationCurrent != null) {
                return locationCurrent;
            }
        }
        LogHelper.m26i(TAG, "No location received yet.");
        return null;
    }

    @Override // com.android.camera.p002v2.app.location.ILocationProvider
    public void recordLocation(boolean z) {
        LogHelper.m23d(TAG, "[recordLocation], mRecordLocation = " + this.mRecordLocation + ",recordLocation = " + z);
        if (this.mRecordLocation != z) {
            this.mRecordLocation = z;
            if (z) {
                startReceivingLocationUpdates();
            } else {
                stopReceivingLocationUpdates();
            }
        }
    }

    private void startReceivingLocationUpdates() {
        LogHelper.m23d(TAG, "startReceivingLocationUpdates ++++");
        if (this.mLocationManager == null) {
            this.mLocationManager = (LocationManager) this.mContext.getSystemService("location");
        }
        if (this.mLocationManager != null) {
            try {
                this.mLocationManager.requestLocationUpdates("network", 1000L, 0.0f, this.mLocationListeners[1]);
            } catch (IllegalArgumentException e) {
                LogHelper.m24e(TAG, "provider does not exist " + e.getMessage());
            } catch (SecurityException e2) {
                LogHelper.m25e(TAG, "fail to request location update, ignore", e2);
            }
            try {
                this.mLocationManager.requestLocationUpdates("gps", 1000L, 0.0f, this.mLocationListeners[0]);
            } catch (IllegalArgumentException e3) {
                LogHelper.m24e(TAG, "provider does not exist " + e3.getMessage());
            } catch (SecurityException e4) {
                LogHelper.m25e(TAG, "fail to request location update, ignore", e4);
            }
            LogHelper.m23d(TAG, "startReceivingLocationUpdates----");
        }
    }

    private void stopReceivingLocationUpdates() {
        if (this.mLocationManager != null) {
            LogHelper.m23d(TAG, "stopReceivingLocationUpdates++++");
            for (int i = 0; i < this.mLocationListeners.length; i++) {
                try {
                    this.mLocationManager.removeUpdates(this.mLocationListeners[i]);
                } catch (Exception e) {
                    LogHelper.m25e(TAG, "fail to remove location listners, ignore", e);
                }
            }
            LogHelper.m23d(TAG, "stopReceivingLocationUpdates----");
        }
    }

    private class LocationListener implements android.location.LocationListener {
        Location mLastLocation;
        String mProvider;
        boolean mValid = false;

        public LocationListener(String str) {
            LogHelper.m23d(LocationProviderImpl.TAG, "[LocationListener] provider = " + str);
            this.mProvider = str;
            this.mLastLocation = new Location(this.mProvider);
        }

        @Override // android.location.LocationListener
        public void onLocationChanged(Location location) {
            LogHelper.m23d(LocationProviderImpl.TAG, "[onLocationChanged]");
            if (location.getLatitude() == 0.0d && location.getLongitude() == 0.0d) {
                return;
            }
            if (!this.mValid) {
                LogHelper.m23d(LocationProviderImpl.TAG, "Got first location.");
            }
            this.mLastLocation.set(location);
            this.mValid = true;
        }

        @Override // android.location.LocationListener
        public void onProviderEnabled(String str) {
        }

        @Override // android.location.LocationListener
        public void onProviderDisabled(String str) {
            this.mValid = false;
        }

        @Override // android.location.LocationListener
        public void onStatusChanged(String str, int i, Bundle bundle) {
            switch (i) {
                case 0:
                case 1:
                    this.mValid = false;
                    break;
            }
        }

        public Location current() {
            LogHelper.m23d(LocationProviderImpl.TAG, "[current],mValid = " + this.mValid);
            if (this.mValid) {
                return this.mLastLocation;
            }
            return null;
        }
    }
}
