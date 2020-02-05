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
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.guardiansofgalakddy.lvlmonitor.R;
import com.guardiansofgalakddy.lvlmonitor.junhwa.BLEScanner;
import com.guardiansofgalakddy.lvlmonitor.junhwa.BLEScannerBuilder;
import com.guardiansofgalakddy.lvlmonitor.seungju.BsData;
import com.guardiansofgalakddy.lvlmonitor.seungju.RecyclerAdapter3;

public class SearchBsActivity extends AppCompatActivity {
    private BroadcastReceiver receiver = null;
    private BLEScanner scanner = null;

    RecyclerAdapter3 adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_bs);

        scanner = BLEScannerBuilder.getInstance(getApplicationContext());

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent == null)
                    return;
                BluetoothDevice device = intent.getParcelableExtra("DEVICE");
                String deviceName = device.getName();
                String deviceMac = device.getAddress();
                Log.d("onReceive()", deviceName + "/" + deviceMac);

                adapter.addItem(new BsData(deviceName, deviceMac, device));
                //adapter.addItem(new Data(device.getName(), content, device, R.drawable.ic_menu), cursor, gpsListener);
            }
        };
        LocalBroadcastManager.getInstance(getApplicationContext()).
                registerReceiver(receiver, new IntentFilter("com.guardiansofgalakddy.lvlmonitor.action.broadcastbs"));


        Button searchButton = findViewById(R.id.btn_discover_bs);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanner.discoverBS();
            }
        });

        init();
    }

    private void init() {
        RecyclerView recyclerView = findViewById(R.id.recyclerView);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        adapter = new RecyclerAdapter3();
        recyclerView.setAdapter(adapter);
    }
}
