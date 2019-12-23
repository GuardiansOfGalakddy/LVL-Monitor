package com.guardiansofgalakddy.lvlmonitor.Onegold.Map;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;

/* GPS Listener */
public class GPSListener implements LocationListener {
    private static final int TWO_MINUTES = 1000 * 60 * 2;
    private static final float MY_MARKER_COLOR = BitmapDescriptorFactory.HUE_ORANGE;

    private GoogleMap map;
    private Location currentLocation;
    private Marker myLocationMarker;
    private HashMap<String, Marker> otherLocationMarkers = new HashMap<>();

    /* first start check to movecamera */
    private boolean isOnStartMap;

    public GPSListener(GoogleMap map) {
        this.isOnStartMap = true;
        this.map = map;
        /* show my location button on map */
        map.setMyLocationEnabled(true);
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

        if (isOnStartMap) {
            // Map start : move camera and marker
            showCurrentLocation(latitude, longitude);
            isOnStartMap = false;
        } else {
            // Map in progress : only move marker
            // prevent camera auto move
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

    // Move camera to current location on Google Map
    public void showCurrentLocation(Double latitude, Double longitude) {
        LatLng curPoint = new LatLng(latitude, longitude);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(curPoint, 15));
        showMyLocationMarker(curPoint);
    }

    // Show my location Marker
    private void showMyLocationMarker(LatLng curPoint) {
        if (myLocationMarker == null) {
            myLocationMarker = map.addMarker(new MarkerOptions()
                    .position(curPoint)
                    .icon(BitmapDescriptorFactory.defaultMarker(MY_MARKER_COLOR))
            );
        } else {
            myLocationMarker.setPosition(curPoint);
        }
    }

    /* show one receiver Marker */
    public void showReceiverMarker(String id, Double latitude, Double longitude) {
        LatLng curPoint = new LatLng(latitude, longitude);

        Marker marker = map.addMarker(new MarkerOptions()
                .position(curPoint)
                .title(id)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
        );
        otherLocationMarkers.put(id, marker);
    }

    /* show one monitor Marker and store */
    public void showMonitorMarker(String id, Double latitude, Double longitude) {
        LatLng curPoint = new LatLng(latitude, longitude);

        Marker marker = map.addMarker(new MarkerOptions()
                .position(curPoint)
                .title(id)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
        );
        otherLocationMarkers.put(id, marker);
    }

    /* Update marker position */
    public void updateMarkerPosition(String id, Double latitude, Double longitude) {
        Marker marker = otherLocationMarkers.get(id);
        if (marker != null) {
            LatLng curPoint = new LatLng(latitude, longitude);
            marker.setPosition(curPoint);
        }
    }

    /* remove Marker by id */
    public void removeMark(String id) {
        Marker marker = otherLocationMarkers.get(id);
        if (marker != null) {
            marker.remove();
            otherLocationMarkers.remove(id);
        }
    }

    /* get current location data and store */
    public LatLng getCurrentLocation() {
        Double latitude = currentLocation.getLatitude();
        Double longitude = currentLocation.getLongitude();

        LatLng curPoint = new LatLng(latitude, longitude);
        return curPoint;
    }

    /* get current latitude */
    public double getCurrentLatitude() {
        return currentLocation.getLatitude();
    }

    /* get current longitude */
    public double getCurrentLongitude() {
        return currentLocation.getLongitude();
    }

    public GoogleMap getMap() {
        return this.map;
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
