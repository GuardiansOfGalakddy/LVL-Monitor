package com.guardiansofgalakddy.lvlmonitor.Onegold.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

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

    private GoogleMap map;
    private Location currentLocation;
    private Marker myLocationMarker;
    private HashMap<String, Marker> otherLocationMarkers = new HashMap<>();
    private Marker tempMarker = null;
    private boolean isOnStartMap;

    private Context context;

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
            final LatLng latLng = marker.getPosition();
            Log.d("abcde", "Ffff");
            showMarkerDialog(marker);
            /*Boolean isInDB = DB2OthersConnector.isAlreadyExistInDB(md.getSystemID(), LVLDBManager.getInstance(context).getIdNLatLng());
            Log.d("abcde", "" + isInDB);
            if (isInDB)
                Toast.makeText(context, "이미 존재합니다", Toast.LENGTH_SHORT).show();
            else
                showMarkerDialog(isInDB);*/
            /*try {
                md = new MarkerDialog(context);
                md.show();
                md.setSaveButtonOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d("abcde", "여기까지!");
                        ContentValues addRowValue = new ContentValues();
                        Boolean isInDB = DB2OthersConnector.isAlreadyExistInDB(md.getSystemID(), LVLDBManager.getInstance(context).getIdNLatLng());
                        if(isInDB) {
                            Log.d("abcde", "존재하는 것임");
                            Toast.makeText(context,"이미 존재하는 것입니다.", Toast.LENGTH_LONG).show();
                        }
                        else {
                            Log.d("abcde", "이번엔 여기까지 ㅇㅇ");
                            addRowValue.put("systemid", md.getSystemID());
                            addRowValue.put("latitude", latLng.latitude);
                            addRowValue.put("longitude", latLng.longitude);
                            mDbManager.insert(addRowValue);
                            Toast.makeText(context, "정상적으로 입력되었습니다.", Toast.LENGTH_LONG).show();
                            md.dismiss();
                            showMarker(GPSListener.ALARM_ID, md.getSystemID(), latLng.latitude, latLng.longitude);
                        }
                    }
                });
            } catch (Exception e) {
                Log.e("showDialog", e.toString());
            }*/

            //Dialog에서 입력된 것을 여기다가 넣으셈 ㅇㅇ 이런식으로 하면 이미 있는지 검사할 수 있을것
            Toast.makeText(context, "위도: " + latLng.latitude + "\n경도: " + latLng.longitude, Toast.LENGTH_LONG).show();
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
                    Log.d("abcde", "여기까지!");
                    ContentValues addRowValue = new ContentValues();
                    Boolean isInDB = DB2OthersConnector.isAlreadyExistInDB(md.getSystemID(), LVLDBManager.getInstance(context).getIdNLatLng());
                    if(isInDB) {
                        Log.d("abcde", "존재하는 것임");
                        Toast.makeText(context,"이미 존재하는 것입니다.", Toast.LENGTH_LONG).show();
                    }
                    else {
                        LatLng latLng = marker.getPosition();
                        Log.d("abcde", "이번엔 여기까지 ㅇㅇ");
                        addRowValue.put("systemid", md.getSystemID());
                        addRowValue.put("latitude", latLng.latitude);
                        addRowValue.put("longitude", latLng.longitude);
                        mDbManager.insert(addRowValue);
                        Toast.makeText(context, "정상적으로 입력되었습니다.", Toast.LENGTH_LONG).show();
                        md.dismiss();
                        showMarker(GPSListener.ALARM_ID, md.getSystemID(), latLng.latitude, latLng.longitude);
                    }
                }
            });
        } catch (Exception e) {
            Log.e("showDialog", e.toString());
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
        /* Add ClickLister on map */
        map.setOnMapClickListener(this);
        map.setOnMapLongClickListener(this);
        map.setOnMarkerClickListener(this);
    }

    void setContext(Context context){
        this.context = context;
        mDbManager = LVLDBManager.getInstance(context);
    }
    void setInit() {
        this.context = null;
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
