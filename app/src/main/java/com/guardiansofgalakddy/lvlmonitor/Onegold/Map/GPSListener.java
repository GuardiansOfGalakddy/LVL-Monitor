package com.guardiansofgalakddy.lvlmonitor.Onegold.Map;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.guardiansofgalakddy.lvlmonitor.R;
import com.guardiansofgalakddy.lvlmonitor.junhwa.DB2OthersConnector;
import com.guardiansofgalakddy.lvlmonitor.seungju.LVLDBManager;
import com.guardiansofgalakddy.lvlmonitor.superb.MarkerDialog;
import com.guardiansofgalakddy.lvlmonitor.ui.MonitorActivity;

import java.util.HashMap;

/* GPS Listener */
/* Creating GPSListener only GPSListenerBuilder */
/* getInstance() of GPSListener is using GPSListener */
public class GPSListener implements LocationListener
        , GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener, GoogleMap.OnMarkerClickListener {
    public static final int NO_ALARM_ID = 0;
    public static final int ALARM_ID = 1;
    private final String MONITOR_MARKER_ID = "RS";
    private final String COLLECTOR_MARKER_ID = "BS";

    private static final int TWO_MINUTES = 1000 * 60 * 2;

    private static final float MY_MARKER_COLOR = BitmapDescriptorFactory.HUE_VIOLET;
    private static final float MONITOR_ALARM_MARKER_COLOR = BitmapDescriptorFactory.HUE_RED;
    private static final float MONITOR_MARKER_COLOR = BitmapDescriptorFactory.HUE_ORANGE;
    private static final float COLLECTOR_MARKER_COLOR = BitmapDescriptorFactory.HUE_CYAN;
    private static final float TEMPORARY_MARKER_COLOR = BitmapDescriptorFactory.HUE_GREEN;

    private Marker myLocationMarker;
    private Marker tempMarker = null;
    private HashMap<String, Marker> otherLocationMarkers = new HashMap<>();
    private boolean isOnStartMap;

    private GoogleMap map;
    private Context context;
    private Location currentLocation;
    private LocationManager locationManager;

    MarkerDialog md = null;

    public LVLDBManager mDbManager = null;

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
        // check best location provider
        if (isBetterLocation(location, currentLocation)) {
            currentLocation = location;
        }

        if (isOnStartMap) {
            // Map start : move camera
            showLocation(currentLocation.getLatitude(), currentLocation.getLongitude());
            isOnStartMap = false;
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

    public void updateMarkerColor(int markerAlarm, String id) {
        Marker marker = otherLocationMarkers.get(id);
        if (marker != null)
            marker.setIcon(BitmapDescriptorFactory.defaultMarker(selectMarkerColor(markerAlarm, id)));
    }

    /* remove Marker by id */
    public void removeMark(String id) {
        Marker marker = otherLocationMarkers.get(id);
        if (marker != null) {
            marker.remove();
            otherLocationMarkers.remove(id);
        }
    }

    @Override
    public void onMapClick(LatLng latLng) {
        /* If tempMarker already exist, remove tempMarker */
        if(tempMarker != null){
            tempMarker.remove();
            tempMarker = null;
        }
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        /* If tempMarker already exist, remove tempMarker */
        if(tempMarker != null){
            tempMarker.remove();
            tempMarker = null;
        }
        /* Save new marker */
        tempMarker = map.addMarker(new MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.defaultMarker(TEMPORARY_MARKER_COLOR)));
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if(marker.equals(tempMarker)){
            showMarkerDialog(marker);
        }
        return false;
    }

    public void showMarkerDialog(final Marker marker) {
        try {
            md = new MarkerDialog(context);
            md.show();
            md.setSaveButtonOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ContentValues addRowValue = new ContentValues();
                    Boolean isInDB = DB2OthersConnector.isAlreadyExistInDB(md.getSystemID(), mDbManager.getIdNLatLng());
                    if(isInDB) {
                        Toast.makeText(context,"이미 존재하는 것입니다.", Toast.LENGTH_LONG).show();
                    }
                    else {
                        LatLng latLng = marker.getPosition();
                        addRowValue.put("systemid", md.getSystemID());
                        addRowValue.put("latitude", latLng.latitude);
                        addRowValue.put("longitude", latLng.longitude);
                        mDbManager.insert(addRowValue);
                        Toast.makeText(context, "정상적으로 입력되었습니다.", Toast.LENGTH_LONG).show();
                        md.dismiss();
                        showMarker(GPSListener.ALARM_ID, md.getSystemID(), latLng.latitude, latLng.longitude);
                        marker.remove();
                    }
                }
            });
        } catch (Exception e) {
            Log.e("showDialog", e.toString());
        }
    }
    /* get current location*/
    public LatLng getCurrentLocation() {
        LatLng curPoint = new LatLng(
                currentLocation.getLatitude(),
                currentLocation.getLongitude()
        );

        return curPoint;
    }

    public GoogleMap getMap() {
        return this.map;
    }

    void setMap(GoogleMap map) {
        this.map = map;
        /* show my location button on map */
        map.setMyLocationEnabled(true);
        /* Add ClickLister on map */
        map.setOnMapClickListener(this);
        map.setOnMapLongClickListener(this);
        map.setOnMarkerClickListener(this);
    }

    void setContext(Context context){
        this.context = context;
        mDbManager = LVLDBManager.getInstance(context);
    }

    /* GPSListener's member variable initialize */
    void setInit() {
        this.context = null;
        this.isOnStartMap = true;
        this.map = null;
        this.otherLocationMarkers.clear();
    }

    /* Register GPSListener in LocationManager */
    boolean startLocationUpdate(){
        /* if map is null or context is null, return false; */
        if(map == null || context == null){
            Log.e("startLocationUpdate", "no map instance OR no context instance");
            return false;
        }

        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        try {
            /* Check location authority and location function available */
            /* False: close activity */
            if ((ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) ||
                    !locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                Toast.makeText(context, "GPS 권한이 없거나 위치 기능이 꺼져있습니다.", Toast.LENGTH_LONG).show();
                ((Activity)context).finish();
            }

            // GPSListener register
            long minTime = 10000;
            float minDistance = 0;

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, this);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTime, minDistance, this);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /* Remove GPSListener from LocationManager */
    public void removeLocationUpdate(){
        if (locationManager != null)
            locationManager.removeUpdates(this);
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
