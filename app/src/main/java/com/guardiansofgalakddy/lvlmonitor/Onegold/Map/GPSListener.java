package com.guardiansofgalakddy.lvlmonitor.Onegold.Map;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

/* GPS Listener */
public class GPSListener implements LocationListener {
    private static GPSListener gpsListener = null;

    private static final int TWO_MINUTES = 1000 * 60 * 2;
    private static final float MY_MARKER_COLOR = BitmapDescriptorFactory.HUE_ORANGE;

    private GoogleMap map;
    private MarkerOptions myLocationMarker;
    private Location currentLocation;

    private boolean isOnStartMap;
    private GPSListener() {
    }

    // singleton pattern
    public static GPSListener getInstance() {
        if (gpsListener == null) {
            gpsListener = new GPSListener();
            gpsListener.isOnStartMap = false;
        }
        return gpsListener;
    }

    public void setMap(GoogleMap map) {
        this.map = map;
    }

    public GoogleMap getMap() {
        return map;
    }

    @Override
    public void onLocationChanged(Location location) {
        Double latitude;
        Double longitude;

        // check best location provider
        if (isBetterLocation(location, currentLocation)) {
            currentLocation = location;
        }
        latitude = currentLocation.getLatitude();
        longitude = currentLocation.getLongitude();

        // move camera to current location
        if(!isOnStartMap) {
            showCurrentLocation(latitude, longitude);
            isOnStartMap = true;
        }else{
            LatLng curPoint = new LatLng(latitude, longitude);
            showMyLocationMarker(curPoint);
        }
    }

    // check newLocation better than currentLocation
    public boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            return true;
        }

        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        if (isSignificantlyNewer) {
            return true;
        } else if (isSignificantlyOlder) {
            return false;
        }

        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        boolean isFromSameProvider = isSameProvider(location.getProvider(), currentBestLocation.getProvider());

        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    // check same location Provider
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }

    // 현재 위치로 Google Map 이동
    private void showCurrentLocation(Double latitude, Double longitude) {
        LatLng curPoint = new LatLng(latitude, longitude);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(curPoint, 15));
        showMyLocationMarker(curPoint);
    }

    // Show my location Marker
    private void showMyLocationMarker(LatLng curPoint) {
        if (myLocationMarker == null) {
            myLocationMarker = new MarkerOptions()
                    .position(curPoint)
                    .icon(BitmapDescriptorFactory.defaultMarker(MY_MARKER_COLOR));
            map.addMarker(myLocationMarker);
        } else {
            myLocationMarker.position(curPoint);
        }
    }

    // Show receiver Marker
    private void showAllReceiverMarker(ArrayList<LatLng> curPoints) {
        for (LatLng curPoint : curPoints) {
            MarkerOptions receiverMarker = new MarkerOptions()
                    .position(curPoint)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
            map.addMarker(receiverMarker);
        }
    }

    // Show monitor Marker
    private void showAllMonitorMarker(ArrayList<LatLng> curPoints) {
        for (LatLng curPoint : curPoints) {
            MarkerOptions monitorMarker = new MarkerOptions()
                    .position(curPoint)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            map.addMarker(monitorMarker);
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
