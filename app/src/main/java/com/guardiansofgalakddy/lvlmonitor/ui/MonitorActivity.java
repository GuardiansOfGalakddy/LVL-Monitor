package com.guardiansofgalakddy.lvlmonitor.ui;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import com.guardiansofgalakddy.lvlmonitor.Onegold.Map.GPSListenerBuilder;
import com.guardiansofgalakddy.lvlmonitor.junhwa.Aes;
import com.guardiansofgalakddy.lvlmonitor.junhwa.BLEScannerBuilder;
import com.guardiansofgalakddy.lvlmonitor.seungju.Data;
import com.guardiansofgalakddy.lvlmonitor.Onegold.Map.GPSListener;
import com.guardiansofgalakddy.lvlmonitor.R;
import com.guardiansofgalakddy.lvlmonitor.seungju.RecyclerAdapter;
import com.guardiansofgalakddy.lvlmonitor.junhwa.BLEScanner;
import com.google.android.gms.maps.model.LatLng;

import com.guardiansofgalakddy.lvlmonitor.seungju.LVLDBManager;
import com.guardiansofgalakddy.lvlmonitor.superb.HistoryDialog;

/* Google Map 관련 코드 - 천우진
 *  GPSListener, startLocationService(), initGoogleMap()
 *  Manifest 수정 사항: permission, user-permission, uses-feature, uses-library, meta-data,
 *  build.gradle 수정 사항: AutoPermissions 추가, google map services 추가 */

public class MonitorActivity extends AppCompatActivity {
    /* Map object */
    private SupportMapFragment mapFragment;
    private GPSListener gpsListener;

    private RecyclerAdapter adapter;
    private BLEScanner scanner = null;
    private BroadcastReceiver receiver = null;
    private Cursor cursor = null;

    HistoryDialog historyDialog = null;

    public LVLDBManager mDbManager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor);

        /* Initialize Google Map */
        initGoogleMap();
        mDbManager = LVLDBManager.getInstance(this);
        scanner = BLEScannerBuilder.getInstance(getApplicationContext());

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent == null)
                    return;
                byte[] data = intent.getByteArrayExtra("MANUFACTURER_DATA");
                byte[] uuid = new byte[16];
                byte[] content = new byte[4];
                System.arraycopy(data, 2, uuid, 0, 16);
                StringBuilder title = new StringBuilder();
                try {
                    uuid = Aes.decrypt(uuid);
                    System.arraycopy(uuid, 0, content, 0, 4);
                    if (content[2] == 0)
                        title.append("BS-");
                    else
                        title.append("RS-");
                    title.append(String.format("%02X", content[1]&0xff));
                    title.append(String.format("%02X", content[0]&0xff));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                adapter.addItem(new Data(title.toString(), content, R.drawable.ic_menu), cursor, gpsListener);
            }
        };
        LocalBroadcastManager.getInstance(getApplicationContext()).
                registerReceiver(receiver, new IntentFilter("com.guardiansofgalakddy.lvlmonitor.action.broadcastuuid"));

        adapter = new RecyclerAdapter();
/*        adapter.setOnItemListener(new OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerAdapter.ItemViewHolder holder, View view, int position) {
                Data data = adapter.getData(position);
                byte[] content = data.getContent();
                Boolean isInDB = data.getResId() == R.drawable.ic_done;
                showDialog(content, data, isInDB);
            }
        });*/
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

    private void showDialog(byte[] uuid) {
        try {
            historyDialog = new HistoryDialog(this);
            historyDialog.initializeHistory(uuid);
            historyDialog.show();
        } catch (Exception e) {
            Log.e("showDialog", e.toString());
        }
    }

    /*private void showDialog(String uuid, final Data data, Boolean alreadyInDB) {
        try {
            historyDialog = new HistoryDialog(this);
            historyDialog.initializeHexToByte(uuid);
            historyDialog.show();
            if (!alreadyInDB) {
                //for append in DB
                historyDialog.setButtonOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ContentValues addRowValue = new ContentValues();
                        LatLng latLng = gpsListener.getCurrentLocation();

                        addRowValue.put("systemid", historyDialog.getSystemID());
                        addRowValue.put("latitude", latLng.latitude);
                        addRowValue.put("longitude", latLng.longitude);
                        mDbManager.insert(addRowValue);
                        Toast.makeText(getApplicationContext(), "정상적으로 입력되었습니다.", Toast.LENGTH_LONG).show();
                        historyDialog.dismiss();
                        data.setResId(R.drawable.ic_done);
                        adapter.notifyDataSetChanged();
                        gpsListener.showMarker(GPSListener.ALARM_ID, historyDialog.getSystemID(), latLng.latitude, latLng.longitude);
                    }
                }, alreadyInDB);
            } else {
                //for update latitude and longitude
                historyDialog.setButtonOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ContentValues updateRowValue = new ContentValues();
                        LatLng latLng = gpsListener.getCurrentLocation();
                        updateRowValue.put("latitude", latLng.latitude);
                        updateRowValue.put("longitude", latLng.longitude);

                        mDbManager.update(updateRowValue,
                                "systemid=\"" + historyDialog.getSystemID() + "\"", null);
                        Toast.makeText(getApplicationContext(), "업데이트 완료했습니다.", Toast.LENGTH_LONG).show();
                        historyDialog.dismiss();
                        gpsListener.showMarker(GPSListener.ALARM_ID, historyDialog.getSystemID(), latLng.latitude, latLng.longitude);
                    }
                }, alreadyInDB);

                //for using delete btn
                historyDialog.setDeleteButtonOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        LatLng latLng = gpsListener.getCurrentLocation();

                        mDbManager.delete("systemid=\"" + historyDialog.getSystemID() + "\"", null);
                        Toast.makeText(getApplicationContext(), "삭제했습니다.", Toast.LENGTH_LONG).show();
                        historyDialog.dismiss();
                        data.setResId(R.drawable.ic_menu);
                        adapter.notifyDataSetChanged();
                        gpsListener.removeMark(historyDialog.getSystemID());
                    }
                });
            }
        } catch (Exception e) {
            Log.e("showDialog", e.toString());
        }
    }*/

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
                /* set GPSListener */
                gpsListener = new GPSListenerBuilder()
                        .setMap(googleMap)
                        .setContext(MonitorActivity.this)
                        .build();

                cursor = mDbManager.getIdNLatLng();
                gpsListener.addMarkersFromDB(GPSListener.NO_ALARM_ID, cursor);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        gpsListener.removeLocationUpdate();
    }
}
