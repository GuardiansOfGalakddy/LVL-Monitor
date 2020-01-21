package com.guardiansofgalakddy.lvlmonitor.ui;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import com.guardiansofgalakddy.lvlmonitor.Onegold.Map.GPSListenerBuilder;
import com.guardiansofgalakddy.lvlmonitor.junhwa.DB2OthersConnector;
import com.guardiansofgalakddy.lvlmonitor.seungju.Data;
import com.guardiansofgalakddy.lvlmonitor.seungju.OnItemClickListener;
import com.guardiansofgalakddy.lvlmonitor.Onegold.Map.GPSListener;
import com.guardiansofgalakddy.lvlmonitor.R;
import com.guardiansofgalakddy.lvlmonitor.seungju.RecyclerAdapter;
import com.guardiansofgalakddy.lvlmonitor.junhwa.BLEScanner;
import com.google.android.gms.maps.model.LatLng;

import com.guardiansofgalakddy.lvlmonitor.seungju.LVLDBManager;
import com.guardiansofgalakddy.lvlmonitor.superb.HexToByte;

/* Google Map 관련 코드 - 천우진
 *  GPSListener, startLocationService(), initGoogleMap()
 *  Manifest 수정 사항: permission, user-permission, uses-feature, uses-library, meta-data,
 *  build.gradle 수정 사항: AutoPermissions 추가, google map services 추가 */

public class MonitorActivity extends AppCompatActivity {
    /* Map object */
    private SupportMapFragment mapFragment;
    private LocationManager locationManager;
    private GPSListener gpsListener;

    private RecyclerAdapter adapter;
    private BLEScanner scanner = null;
    private BroadcastReceiver receiver = null;
    private Cursor cursor = null;

    HexToByte hTB = null;

    public LVLDBManager mDbManager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor);

        initGoogleMap();
        mDbManager = LVLDBManager.getInstance(this);

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent == null)
                    return;
                String uuid = intent.getStringExtra("UUID");
                Log.d("onReceive", uuid);
                String systemId = uuid.substring(0, 5);

                adapter.addItem(new Data(systemId, uuid, R.drawable.ic_menu), cursor, gpsListener);
            }
        };
        LocalBroadcastManager.getInstance(getApplicationContext()).
                registerReceiver(receiver, new IntentFilter("com.guardiansofgalakddy.lvlmonitor.action.broadcastuuid"));

        scanner = new BLEScanner();
        if (!scanner.initialize(this))
            finish();

        adapter = new RecyclerAdapter();
        adapter.setOnItemListener(new OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerAdapter.ItemViewHolder holder, View view, int position) {
                Data data = adapter.getData(position);
                String content = data.getContent();
                Boolean isInDB = data.getResId() == R.drawable.ic_done;
                showDialog(content, data, isInDB);
            }
        });
        initializeRecyclerView();

        Button scanButton = findViewById(R.id.btn_scan);
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.clearData();
                cursor = mDbManager.getIdNLatLng();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        scanner.discover();
                    }
                }).start();
            }
        });
    }

    private void showDialog(String uuid, final Data data, Boolean alreadyInDB) {
        try {
            hTB = new HexToByte(this);
            hTB.initializeHexToByte(uuid);
            hTB.show();
            if (!alreadyInDB) {
                //for append in DB
                hTB.setButtonOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ContentValues addRowValue = new ContentValues();
                        LatLng latLng = gpsListener.getCurrentLocation();

                        addRowValue.put("systemid", hTB.getSystemID());
                        addRowValue.put("latitude", latLng.latitude);
                        addRowValue.put("longitude", latLng.longitude);
                        mDbManager.insert(addRowValue);
                        Toast.makeText(getApplicationContext(), "정상적으로 입력되었습니다.", Toast.LENGTH_LONG).show();
                        hTB.dismiss();
                        data.setResId(R.drawable.ic_done);
                        adapter.notifyDataSetChanged();
                        gpsListener.showMarker(GPSListener.ALARM_ID, hTB.getSystemID(), latLng.latitude, latLng.longitude);
                    }
                }, alreadyInDB);
            } else {
                //for update latitude and longitude
                hTB.setButtonOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ContentValues updateRowValue = new ContentValues();
                        LatLng latLng = gpsListener.getCurrentLocation();
                        updateRowValue.put("latitude", latLng.latitude);
                        updateRowValue.put("longitude", latLng.longitude);

                        mDbManager.update(updateRowValue,
                                "systemid=\"" + hTB.getSystemID() + "\"", null);
                        Toast.makeText(getApplicationContext(), "업데이트 완료했습니다.", Toast.LENGTH_LONG).show();
                        hTB.dismiss();
                        gpsListener.showMarker(GPSListener.ALARM_ID, hTB.getSystemID(), latLng.latitude, latLng.longitude);
                    }
                }, alreadyInDB);

                //for using delete btn
                hTB.setDeleteButtonOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        LatLng latLng = gpsListener.getCurrentLocation();

                        mDbManager.delete("systemid=\"" + hTB.getSystemID() + "\"", null);
                        Toast.makeText(getApplicationContext(), "삭제했습니다.", Toast.LENGTH_LONG).show();
                        hTB.dismiss();
                        data.setResId(R.drawable.ic_menu);
                        adapter.notifyDataSetChanged();
                        gpsListener.removeMark(hTB.getSystemID());
                    }
                });
            }
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

    /* Google Map first setting */
    private void initGoogleMap() {
        /* Google Map Fragment register */
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_monitor);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                /* Google Map setting */
                startLocationService(googleMap);

                cursor = mDbManager.getIdNLatLng();
                gpsListener.addMarkersFromDB(GPSListener.NO_ALARM_ID, cursor);
            }
        });
        try {
            MapsInitializer.initialize(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* Google Map location information get, location setting */
    private void startLocationService(GoogleMap googleMap) {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        try {// Check location authority and location function available
            // False: finish()
            if ((ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) ||
                    !locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                Toast.makeText(getApplicationContext(), "GPS 권한이 없거나 위치 기능이 꺼져있습니다.", Toast.LENGTH_LONG).show();
                finish();
            }

            /* set GPSListener */
            /* set camera and marker start location */
            /* It may be inaccurate location but soon find current location*/
            GPSListenerBuilder builder = GPSListenerBuilder.getInstance();
            gpsListener = builder
                    .setMap(googleMap)
                    .setContext(this)
                    .getGpsListener();

            /* set camera and marker start location */
            /* It may be inaccurate location but soon find current location*/
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location != null)
                gpsListener.showLocation(location.getLatitude(), location.getLongitude());

            // GPSListener register
            long minTime = 10000;
            float minDistance = 0;

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, gpsListener);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTime, minDistance, gpsListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (gpsListener != null) {
            gpsListener.getMap().setMyLocationEnabled(false);

            if (locationManager != null)
                locationManager.removeUpdates(gpsListener);
        }
    }
}
