package com.guardiansofgalakddy.lvlmonitor.Onegold.Map;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

/* GPS Listener */
public class GPSListener implements LocationListener {
    private static final int TWO_MINUTES = 1000 * 60 * 2;
    private static final float MY_MARKER_COLOR = BitmapDescriptorFactory.HUE_ORANGE;

    private GoogleMap map;
    private MarkerOptions myLocationMarker;
    private Location currentLocation;

    private boolean isOnStartMap;

    public GPSListener(GoogleMap map) {
        this.isOnStartMap = true;
        this.map = map;
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

        if(isOnStartMap) {
            // Map start : move camera and marker
            showCurrentLocation(latitude, longitude);
            isOnStartMap = false;
        }else{
            // Map in progress : only move marker
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


    /* Show all receiver Marker */
    /*
    public void showAllReceiverMarker(ArrayList<Receiver> curPoints) {
        for (Receiver receiver : curPoints) {
            MarkerOptions receiverMarker = new MarkerOptions()
                    .position(receiver.getPoint())
                    .title(receiver.getId())
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
            map.addMarker(receiverMarker);
        }
    }
    */
    /* Show all monitor Marker */
    /*
    public void showAllMonitorMarker(ArrayList<Monitor> curPoints) {
        for (Monitor monitor : curPoints) {
            MarkerOptions monitorMarker = new MarkerOptions()
                    .position(monitor.getPoint())
                    .title(monitor.getId())
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            map.addMarker(monitorMarker);
        }
    }
    */
    /* show one receiver Marker */
    public void showReceiverMarker(String id, Double latitude, Double longitude){
        LatLng curPoint = new LatLng(latitude, longitude);

        MarkerOptions receiverMarker = new MarkerOptions()
                .position(curPoint)
                .title(id)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        map.addMarker(receiverMarker);
    }
    /* show one monitor Marker */
    public void showMonitorMarker(String id, Double latitude, Double longitude){
        LatLng curPoint = new LatLng(latitude, longitude);

        MarkerOptions monitorMarker = new MarkerOptions()
                .position(curPoint)
                .title(id)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        map.addMarker(monitorMarker);
    }
    /* get current location data */
    public LatLng getCurrentLocation(){
        Double latitude = currentLocation.getLatitude();
        Double longitude = currentLocation.getLongitude();

        LatLng curPoint = new LatLng(latitude, longitude);
        return curPoint;
    }
    /* get current latitude */
    public double getCurrentLatitude(){
        return currentLocation.getLatitude();
    }
    /* get current longitude */
    public double getCurrentLongitude(){
        return currentLocation.getLongitude();
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
