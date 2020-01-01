package com.guardiansofgalakddy.lvlmonitor.Onegold.Map;

import android.database.Cursor;
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
/* Creating GPSListener only GPSListenerBuilder */
/* getInstance() of GPSListener is using GPSListener */
public class GPSListener implements LocationListener {
    public static final int NO_ALARM_ID = 0;
    public static final int ALARM_ID = 1;
    private final String MONITOR_MARKER_ID = "RS";
    private final String COLLECTOR_MARKER_ID = "BS";

    private static final int TWO_MINUTES = 1000 * 60 * 2;

    private static final float MY_MARKER_COLOR = BitmapDescriptorFactory.HUE_VIOLET;
    private static final float MONITOR_ALARM_MARKER_COLOR = BitmapDescriptorFactory.HUE_RED;
    private static final float COLLECTOR_ALARM_MARKER_COLOR = BitmapDescriptorFactory.HUE_BLUE;
    private static final float MONITOR_MARKER_COLOR = BitmapDescriptorFactory.HUE_ORANGE;
    private static final float COLLECTOR_MARKER_COLOR = BitmapDescriptorFactory.HUE_CYAN;

    private GoogleMap map;
    private Location currentLocation;
    private Marker myLocationMarker;
    private HashMap<String, Marker> otherLocationMarkers = new HashMap<>();
    private boolean isOnStartMap;

    private GPSListener() {

    }

    private static class GPSListenerHolder {
        private static final GPSListener instance = new GPSListener();
    }

    public static GPSListener getInstance() {
        return GPSListenerHolder.instance;
    }

    @Override
    public void onLocationChanged(Location location) {
        double latitude;
        double longitude;

        // check best location provider
        if (isBetterLocation(location, currentLocation)) {
            currentLocation = location;
        }
        latitude = currentLocation.getLatitude();
        longitude = currentLocation.getLongitude();

        if (isOnStartMap) {
            // Map start : move camera and marker
            showLocation(latitude, longitude);
            isOnStartMap = false;
        } else {
            // Map in progress : only move marker
            // prevent camera auto move
            LatLng curPoint = new LatLng(latitude, longitude);
            //showMyLocationMarker(curPoint);
        }
    }

    // check newLocation better than currentLocation
    private boolean isBetterLocation(Location location, Location currentBestLocation) {
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
    public void showLocation(Double latitude, Double longitude) {
        LatLng curPoint = new LatLng(latitude, longitude);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(curPoint, 15));
        //showMyLocationMarker(curPoint);
    }

    public void showCurrentLocation() {
        LatLng curPoint = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(curPoint, 15));
    }

    // Show my location Marker
    private void showMyLocationMarker(LatLng curPoint) {
        if (myLocationMarker == null) {
            myLocationMarker = map.addMarker(new MarkerOptions()
                    .position(curPoint)
                    .icon(BitmapDescriptorFactory.defaultMarker(MY_MARKER_COLOR)));
        } else {
            myLocationMarker.setPosition(curPoint);
        }
    }

    /* show one monitor Marker and store */
    public void showMarker(int markerAlarm, String id, Double latitude, Double longitude) {
        LatLng curPoint = new LatLng(latitude, longitude);

        Marker marker = map.addMarker(new MarkerOptions()
                .position(curPoint)
                .title(id)
                .icon(BitmapDescriptorFactory.defaultMarker(selectMarkerColor(markerAlarm, id))));
        otherLocationMarkers.put(id, marker);
    }

    private float selectMarkerColor(int markerAlarm, String id) {
        switch (id.substring(0, 2)) {
            case MONITOR_MARKER_ID: {
                if (markerAlarm == ALARM_ID)
                    return MONITOR_ALARM_MARKER_COLOR;
                else
                    return MONITOR_MARKER_COLOR;
            }
            case COLLECTOR_MARKER_ID: {
                if (markerAlarm == ALARM_ID)
                    return COLLECTOR_ALARM_MARKER_COLOR;
                else
                    return COLLECTOR_MARKER_COLOR;
            }
            default:
                return 0;
        }
    }

    /* show Marker using DB Cursor */
    public void addMarkersFromDB(int markerAlarm, Cursor cursor) {
        if (cursor == null || cursor.getCount() == 0)
            return;

        cursor.moveToFirst();
        while (true) {
            showMarker(markerAlarm, cursor.getString(0), cursor.getDouble(1), cursor.getDouble(2));
            if (!cursor.moveToNext())
                break;
        }
    }

    /* Update marker position */
    public void updateMarkerPosition(int markerAlarm, String id, Double latitude, Double longitude) {
        Marker marker = otherLocationMarkers.get(id);
        if (marker != null) {
            LatLng curPoint = new LatLng(latitude, longitude);
            marker.setPosition(curPoint);
            marker.setIcon(BitmapDescriptorFactory.defaultMarker(selectMarkerColor(markerAlarm, id)));
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

    /* get current location*/
    public LatLng getCurrentLocation() {
        Double latitude = currentLocation.getLatitude();
        Double longitude = currentLocation.getLongitude();

        LatLng curPoint = new LatLng(latitude, longitude);

        return curPoint;
    }

    public GoogleMap getMap() {
        return this.map;
    }

    void setMap(GoogleMap map) {
        this.map = map;
        /* show my location button on map */
        map.setMyLocationEnabled(true);
    }

    void setInit() {
        this.isOnStartMap = true;
        this.map = null;
        this.otherLocationMarkers.clear();
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
