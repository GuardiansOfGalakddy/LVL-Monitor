package com.guardiansofgalakddy.lvlmonitor.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.guardiansofgalakddy.lvlmonitor.Onegold.Map.GPSListener;
import com.guardiansofgalakddy.lvlmonitor.Onegold.Map.GPSListenerBuilder;
import com.guardiansofgalakddy.lvlmonitor.R;
import com.guardiansofgalakddy.lvlmonitor.junhwa.Aes;
import com.guardiansofgalakddy.lvlmonitor.junhwa.BLEScanner;
import com.guardiansofgalakddy.lvlmonitor.junhwa.BLEScannerBuilder;
import com.guardiansofgalakddy.lvlmonitor.seungju.Data;
import com.guardiansofgalakddy.lvlmonitor.seungju.OnItemClickListener;
import com.guardiansofgalakddy.lvlmonitor.seungju.RecyclerAdapter;
import com.guardiansofgalakddy.lvlmonitor.superb.HistoryDialog;

public class CollectorActivity extends AppCompatActivity {
    public final static int NO_DEVICE = 103;

    private GPSListener gpsListener = null;
    private BLEScanner scanner = null;
    private Handler handler = null;

    private HistoryDialog historyDialog = null;
    private RecyclerAdapter adapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collector);

        BluetoothDevice device = getIntent().getParcelableExtra("DEVICE");
        if (device == null) {
            Toast.makeText(getApplicationContext(), "비정상적인 접근입니다.", Toast.LENGTH_LONG).show();
            finishActivity(NO_DEVICE);
        }

        final Button btnRequest = findViewById(R.id.btn_request);

        /* Initialize Google Map */
        initGoogleMap();
        historyDialog = new HistoryDialog(this);

        scanner = BLEScannerBuilder.getInstance(getApplicationContext());
        btnRequest.setText("connecting...");
        scanner.connect(device);
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent == null)
                    return;
                byte[] data = intent.getByteArrayExtra("HISTORY");
                Log.d("history", Aes.byteArrayToHexString(data));

                for (int i = 0; i < (data.length / 24); i++) {
                    byte[] history = new byte[24];
                    System.arraycopy(data, i * 24, history, 0, 24);
                    StringBuilder title = new StringBuilder();
                    title.append(String.format("%02X", history[2] & 0xff).charAt(0) == '0' ? "BS-" : "RS-");
                    title.append(String.format("%02X", history[1] & 0xff));
                    title.append(String.format("%02X", history[0] & 0xff));
                    adapter.addItem(new Data(title.toString(), history, R.drawable.blank));
                }
            }
        };
        LocalBroadcastManager.getInstance(getApplicationContext()).
                registerReceiver(receiver, new IntentFilter("com.guardiansofgalakddy.lvlmonitor.action.sendhistory"));


        if (scanner == null)
            btnRequest.setText("Bluetooth adapter not found");
        handler = new Handler();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!scanner.getConnected())
                    ;
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        btnRequest.setText("request history");
                        btnRequest.setEnabled(true);
                    }
                });
            }
        });
        thread.start();

        btnRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.clearData();
                scanner.requestHistory();
            }
        });

        recyclerViewInit();
    }

    /* Google Map first setting */
    private void initGoogleMap() {
        /* Google Map Fragment register */
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_collector);
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

    private void recyclerViewInit() {
        RecyclerView recyclerView = findViewById(R.id.recyclerView);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        adapter = new RecyclerAdapter();
        recyclerView.setAdapter(adapter);
        adapter.setOnItemListener(new OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerAdapter.ItemViewHolder holder, View view, int position) {
                Data data = adapter.getData(position);
                showDialog(data.getContent());
            }
        });
    }

    private void showDialog(byte[] bytes) {
        try {
            historyDialog = new HistoryDialog(this);
            historyDialog.initializeHistory(bytes);
            historyDialog.show();
        } catch (Exception e) {
            Log.e("showDialog", e.toString());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        gpsListener.removeLocationUpdate();
        scanner.disConnect();
    }
}
