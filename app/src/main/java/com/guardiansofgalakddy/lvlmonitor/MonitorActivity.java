package com.guardiansofgalakddy.lvlmonitor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import com.guardiansofgalakddy.lvlmonitor.junhwa.BLEScanner;

/* Google Map 관련 코드 - 천우진
 *  GPSListener, startLocationService(), initGoogleMap()
 *  Manifest 수정 사항: permission, user-permission, uses-feature, uses-library, meta-data,
 *  build.gradle 수정 사항: AutoPermissions 추가, google map services 추가 */

public class MonitorActivity extends AppCompatActivity {
    /* Map object */
    SupportMapFragment mapFragment;
    GoogleMap map;
    MarkerOptions myLocationMarker;


    private RecyclerAdapter adapter;
    private BLEScanner scanner = null;
    private BroadcastReceiver receiver = null;

    HexToByte hTB = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor);

        Button scanButton = findViewById(R.id.btn_scan);
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanner.discover();
            }
        });

        /* Google Map 설정 */
        initGoogleMap();

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent == null)
                    return;
                String uuid = intent.getStringExtra("UUID");
                Log.d("onReceive", uuid);
                addData(new Data(uuid.substring(2, 8), uuid, R.drawable.ic_menu));
            }
        };
        LocalBroadcastManager.getInstance(getApplicationContext()).
                registerReceiver(receiver, new IntentFilter("com.guardiansofgalakddy.lvlmonitor.action.broadcastuuid"));

        scanner = new BLEScanner();
        if (scanner.initialize(this) == false)
            finish();

        adapter = new RecyclerAdapter();
        adapter.setOnItemListener(new OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerAdapter.ItemViewHolder holder, View view, int position) {
                Data data = adapter.getData(position);
                String content = data.getContent();
                String[] split = content.split("-");
                try {
                    String uuid1 = split[0] + split[1] + split[2];
                    String uuid2 = split[3] + split[4];
                    showDialog(uuid1, uuid2);
                } catch (Exception e) {
                    Log.e("onItemClick", e.toString());
                }
            }
        });
        initializeRecyclerView();
    }

    private void showDialog(String uuid1, String uuid2) {
        try {
            hTB = new HexToByte(this);
            hTB.initializeHexToByte(uuid1, uuid2);
            hTB.show();
        } catch (Exception e) {
            Log.e("showDialog", e.toString());
        }
    }

    private void initializeRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recyclerView);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        recyclerView.setAdapter(adapter);
    }

    private void addData(Data data) {
        adapter.addItem(data);

        // adapter의 값이 변경되었다는 것을 알려줍니다.
        adapter.notifyDataSetChanged();
    }

    /* Google Map 처음 설정 */
    private void initGoogleMap() {
        /* Google Map Fragment 등록 */
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_monitor);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;
                map.setMyLocationEnabled(true);
                /* Google Map location setting */
                startLocationService();
            }
        });
        try {
            MapsInitializer.initialize(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* Google Map 위치 정보 get, 위치 지정 */
    private void startLocationService() {
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        try {// 위치 권한 및 위치 설정 ON 확인
            // False: finish(), 이전 액티비티로
            if ((ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) ||
                    !manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                Toast.makeText(getApplicationContext(), "GPS 권한이 없거나 위치 기능이 꺼져있습니다.", Toast.LENGTH_LONG).show();
                finish();
            }
            // 위치 정보 획득
            Location location = manager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (location != null) {
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
        } catch (Exception e) {
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
            if (isBetterLocation(location, currentLocation)) {
                currentLocation = location;
            }
            latitude = currentLocation.getLatitude();
            longitude = currentLocation.getLongitude();
            Log.d("onLocationChanged: ", latitude + ", " + longitude + ", " + currentLocation.getProvider());
            // move camera to current location
            showCurrentLocation(latitude, longitude);
        }

        // check newLocation better than currentLocation
        public boolean isBetterLocation(Location location, Location currentBestLocation) {
            if (currentBestLocation == null) {
                return true;
            }

            long timeDelta = location.getTime() - currentBestLocation.getTime();
            boolean isSignificatlyNewer = timeDelta > TWO_MINUTES;
            boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
            boolean isNewer = timeDelta > 0;

            if (isSignificatlyNewer) {
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
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(curPoint, 15));
            Log.d("showCurrentLocation : ", latitude + ", " + longitude);
        }

        // Show my location Marker
        private void showMyLocationMarker(LatLng curPoint) {
            if (myLocationMarker == null) {
                myLocationMarker = new MarkerOptions()
                        .position(curPoint)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                map.addMarker(myLocationMarker);
            } else {
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
        if (map != null) {
            map.setMyLocationEnabled(true);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (map != null) {
            map.setMyLocationEnabled(false);
        }
    }
}
