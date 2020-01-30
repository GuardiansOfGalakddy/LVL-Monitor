package com.guardiansofgalakddy.lvlmonitor.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.guardiansofgalakddy.lvlmonitor.Onegold.Map.GPSListener;
import com.guardiansofgalakddy.lvlmonitor.Onegold.Map.GPSListenerBuilder;
import com.guardiansofgalakddy.lvlmonitor.R;
import com.guardiansofgalakddy.lvlmonitor.junhwa.Aes;
import com.guardiansofgalakddy.lvlmonitor.junhwa.BLEScanner;
import com.guardiansofgalakddy.lvlmonitor.junhwa.BLEScannerBuilder;
import com.guardiansofgalakddy.lvlmonitor.seungju.Data;
import com.guardiansofgalakddy.lvlmonitor.seungju.RecyclerAdapter;

public class CollectorActivity extends AppCompatActivity {
    /* Map object */
    private SupportMapFragment mapFragment;
    private GPSListener gpsListener;

    BLEScanner scanner = null;
    BroadcastReceiver receiver = null;

    private RecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collector);

        /* Initialize Google Map */
        initGoogleMap();

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
                    title.append(String.format("%02X", content[0]&0xff));
                    title.append(String.format("%02X", content[1]&0xff));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        LocalBroadcastManager.getInstance(getApplicationContext()).
                registerReceiver(receiver, new IntentFilter("com.guardiansofgalakddy.lvlmonitor.action.broadcastuuid"));

        Button btnRequest = findViewById(R.id.btn_request);
        btnRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanner.requestHistory();
            }
        });

        String str = "test";
        Data data = new Data(str,str.getBytes(),R.drawable.blank);


        init();
        adapter.addItem(data);
        adapter.notifyDataSetChanged();
    }
    /* Google Map first setting */
    private void initGoogleMap() {
        /* Google Map Fragment register */
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_collector);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                /* set GPSListener */
                gpsListener = new GPSListenerBuilder()
                        .setMap(googleMap)
                        .setContext(CollectorActivity.this)
                        .build();
            }
        });
    }

    private void init() {
        RecyclerView recyclerView = findViewById(R.id.recyclerView);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        adapter = new RecyclerAdapter();
        recyclerView.setAdapter(adapter);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        gpsListener.removeLocationUpdate();
    }
}
