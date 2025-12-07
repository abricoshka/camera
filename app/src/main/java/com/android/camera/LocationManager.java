package com.android.camera;

import android.content.Context;
import android.location.Location;
import android.location.LocationRequest;
import android.os.Bundle;
import android.os.Looper;

/* loaded from: classes.dex */
public class LocationManager {
    private Context mContext;
    private Listener mListener;
    LocationListener mLocationListener = new LocationListener("fused");
    private android.location.LocationManager mLocationManager;
    private boolean mRecordLocation;

    public interface Listener {
        void hideGpsOnScreenIndicator();

        void showGpsOnScreenIndicator(boolean z);
    }

    public LocationManager(Context context, Listener listener) {
        this.mContext = context;
        this.mListener = listener;
    }

    public Location getCurrentLocation() {
        Location locationCurrent = null;
        if (FeatureSwitcher.isGpsLocationSupported() && this.mRecordLocation) {
            locationCurrent = this.mLocationListener.current();
        }
        Log.m5d("LocationManager", "getCurrentLocation() mRecordLocation=" + this.mRecordLocation + ", return " + locationCurrent);
        return locationCurrent;
    }

    public void recordLocation(boolean z) {
        Log.m5d("LocationManager", "recordLocation(" + z + ") mRecordLocation=" + this.mRecordLocation);
        if (FeatureSwitcher.isGpsLocationSupported() && this.mRecordLocation != z) {
            this.mRecordLocation = z;
            if (z) {
                startReceivingLocationUpdates();
            } else {
                stopReceivingLocationUpdates();
            }
        }
    }

    private void startReceivingLocationUpdates() {
        if (this.mLocationManager == null) {
            this.mLocationManager = (android.location.LocationManager) this.mContext.getSystemService("location");
        }
        if (this.mLocationManager != null) {
            try {
                LocationRequest locationRequestCreate = LocationRequest.create();
                locationRequestCreate.setQuality(203);
                locationRequestCreate.setProvider("fused");
                locationRequestCreate.setInterval(1000L);
                locationRequestCreate.setFastestInterval(1000L);
                locationRequestCreate.setSmallestDisplacement(0.0f);
                this.mLocationManager.requestLocationUpdates(locationRequestCreate, this.mLocationListener, (Looper) null);
            } catch (IllegalArgumentException e) {
                Log.m5d("LocationManager", "provider does not exist " + e.getMessage());
            } catch (SecurityException e2) {
                Log.m9i("LocationManager", "fail to request location update, ignore", e2);
            }
            Log.m5d("LocationManager", "startReceivingLocationUpdates");
        }
    }

    private void stopReceivingLocationUpdates() {
        if (this.mLocationManager != null) {
            try {
                this.mLocationManager.removeUpdates(this.mLocationListener);
            } catch (Exception e) {
                Log.m9i("LocationManager", "fail to remove location listners, ignore", e);
            }
            Log.m5d("LocationManager", "stopReceivingLocationUpdates");
        }
        if (this.mListener != null) {
            this.mListener.hideGpsOnScreenIndicator();
        }
    }

    private class LocationListener implements android.location.LocationListener {
        Location mLastLocation;
        String mProvider;
        boolean mValid = false;

        public LocationListener(String str) {
            this.mProvider = str;
            this.mLastLocation = new Location(this.mProvider);
        }

        @Override // android.location.LocationListener
        public void onLocationChanged(Location location) {
            Log.m5d("LocationManager", "onLocationChanged(" + location + ") mRecordLocation=" + LocationManager.this.mRecordLocation + ", mListener=" + LocationManager.this.mListener + ", mProvider=" + this.mProvider);
            if (location.getLatitude() == 0.0d && location.getLongitude() == 0.0d) {
                return;
            }
            if (LocationManager.this.mListener != null && LocationManager.this.mRecordLocation && "gps".equals(this.mProvider)) {
                LocationManager.this.mListener.showGpsOnScreenIndicator(true);
            }
            if (!this.mValid) {
                Log.m5d("LocationManager", "Got first location.");
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
            Log.m5d("LocationManager", "onStatusChanged(" + str + ", " + i + ") mRecordLocation=" + LocationManager.this.mRecordLocation + ", mListener=" + LocationManager.this.mListener);
            switch (i) {
                case 0:
                case 1:
                    this.mValid = false;
                    if (LocationManager.this.mListener != null && LocationManager.this.mRecordLocation && "gps".equals(str)) {
                        LocationManager.this.mListener.showGpsOnScreenIndicator(false);
                        break;
                    }
                    break;
            }
        }

        public Location current() {
            if (this.mValid) {
                return this.mLastLocation;
            }
            return null;
        }
    }
}
