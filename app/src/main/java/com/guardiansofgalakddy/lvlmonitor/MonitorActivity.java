package com.guardiansofgalakddy.lvlmonitor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Arrays;
import java.util.Currency;
import java.util.List;

/* Google Map code - onegold11
*  GPSListener, startLocationService(), initGoogleMap()
*  Manifest changes: permission, user-permission, uses-feature, uses-library, meta-data,
*  build.gradle changes: AutoPermissions add, google map services add */

public class MonitorActivity extends AppCompatActivity {
    /* Map object */
    SupportMapFragment mapFragment;
    GoogleMap map;
    MarkerOptions myLocationMarker;


    private RecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor);

        // Recycle view start
        init();
        getData();
        /* Google Map setting */
        initGoogleMap();
    }
    private void init() {
        RecyclerView recyclerView = findViewById(R.id.recyclerView);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        adapter = new RecyclerAdapter();
        recyclerView.setAdapter(adapter);
    }

    private void getData() {
        List<String> listTitle = Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8",
                "9", "10", "11", "12", "13", "14", "15", "16");
        List<String> listContent = Arrays.asList(
                "tx1",
                "tx2",
                "tx3",
                "tx4",
                "tx5",
                "tx6",
                "tx7",
                "tx8",
                "tx9",
                "tx10",
                "tx11",
                "tx12",
                "tx13",
                "tx14",
                "tx15",
                "tx16"
        );
        List<Integer> listResId = Arrays.asList(
                R.drawable.ic_launcher_background,
                R.drawable.ic_launcher_background,
                R.drawable.ic_launcher_background,
                R.drawable.ic_launcher_background,
                R.drawable.ic_launcher_background,
                R.drawable.ic_launcher_background,
                R.drawable.ic_launcher_background,
                R.drawable.ic_launcher_background,
                R.drawable.ic_launcher_background,
                R.drawable.ic_launcher_background,
                R.drawable.ic_launcher_background,
                R.drawable.ic_launcher_background,
                R.drawable.ic_launcher_background,
                R.drawable.ic_launcher_background,
                R.drawable.ic_launcher_background,
                R.drawable.ic_launcher_background
        );

        for (int i = 0; i < listTitle.size(); i++) {
            // 각 List의 값들을 data 객체에 set 해줍니다.
            Data data = new Data();
            data.setTitle(listTitle.get(i));
            data.setContent(listContent.get(i));
            data.setResId(listResId.get(i));

            // 각 값이 들어간 data를 adapter에 추가합니다.
            adapter.addItem(data);
        }

        // adapter의 값이 변경되었다는 것을 알려줍니다.
        adapter.notifyDataSetChanged();
    }

    /* Google Map 처음 설정 */
    private void initGoogleMap(){
        /* Google Map Fragment register */
        mapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map_monitor);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;
                map.setMyLocationEnabled(true);
                /* Google Map location setting */
                startLocationService();
            }
        });
        try{
            MapsInitializer.initialize(this);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    /* Google Map location information get, location set */
    private void startLocationService(){
        LocationManager manager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        try{// check gps authority and gps On
            // False: finish(), before activity
            if((ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) ||
                    !manager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                Toast.makeText(getApplicationContext(), "GPS 권한이 없거나 위치 기능이 꺼져있습니다.", Toast.LENGTH_LONG).show();
                finish();
            }
            // get location information
            Location location = manager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if(location != null){
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                Log.d("startLocationService: ", latitude + ", " + longitude);
            }
            // GPSListener register
            GPSListener gpsListener = new GPSListener();
            long minTime = 10000;
            float minDistance = 0;
            manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, gpsListener);
            manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTime, minDistance, gpsListener);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    /* GPS Listener */
    public class GPSListener implements LocationListener {
        private static final int TWO_MINUTES = 1000 * 60 * 2;
        Location currentLocation;

        @Override
        public void onLocationChanged(Location location) {
            Double latitude;
            Double longitude;

            // check best location provider
            if(isBetterLocation(location, currentLocation)){
                currentLocation = location;
            }
            latitude = currentLocation.getLatitude();
            longitude = currentLocation.getLongitude();
            Log.d("onLocationChanged: ", latitude + ", " + longitude + ", " + currentLocation.getProvider());
            // move camera to current location
            showCurrentLoaction(latitude, longitude);
        }
        // check newLocation better than currentLocation
        public boolean isBetterLocation(Location location, Location currentBestLocation){
            if(currentBestLocation == null){
                return true;
            }

            long timeDelta = location.getTime() - currentBestLocation.getTime();
            boolean isSignificatlyNewer = timeDelta > TWO_MINUTES;
            boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
            boolean isNewer = timeDelta > 0;

            if(isSignificatlyNewer){
                return true;
            }else if(isSignificantlyOlder){
                return false;
            }

            int accuracyDelta = (int)(location.getAccuracy() - currentBestLocation.getAccuracy());
            boolean isLessAccurate = accuracyDelta > 0;
            boolean isMoreAccurate = accuracyDelta < 0;
            boolean isSignificantlyLessAccurate = accuracyDelta > 200;

            boolean isFromSameProvider = isSameProvider(location.getProvider(), currentBestLocation.getProvider());

            if(isMoreAccurate){
                return true;
            }else if(isNewer && !isLessAccurate){
                return true;
            }else if(isNewer && !isSignificantlyLessAccurate && isFromSameProvider){
                return true;
            }
            return false;
        }
        // check same location Provider
        private boolean isSameProvider(String provider1, String provider2){
            if(provider1 == null){
                return provider2 == null;
            }
            return provider1.equals(provider2);
        }
        // Move Camera to current location
        private void showCurrentLoaction(Double latitude, Double longitude){
            LatLng curPoint = new LatLng(latitude, longitude);
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(curPoint, 15));
            // show Marker
            showMyLocationMarker(curPoint);
            Log.d("showCurrentLocation : ",  latitude + ", " + longitude);
        }
        // Show my location Marker
        private void showMyLocationMarker(LatLng curPoint){
            if(myLocationMarker != null){
                myLocationMarker = new MarkerOptions();
                myLocationMarker.position(curPoint);
                map.addMarker(myLocationMarker);
            }else{
                myLocationMarker.position(curPoint);
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

    @Override
    protected void onResume() {
        super.onResume();
        if(map != null){
            map.setMyLocationEnabled(true);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(map != null){
            map.setMyLocationEnabled(false);
        }
    }
}
