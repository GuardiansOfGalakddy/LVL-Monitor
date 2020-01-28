package com.guardiansofgalakddy.lvlmonitor.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.guardiansofgalakddy.lvlmonitor.R;
import com.guardiansofgalakddy.lvlmonitor.junhwa.Aes;
import com.guardiansofgalakddy.lvlmonitor.junhwa.BLEScanner;
import com.guardiansofgalakddy.lvlmonitor.junhwa.BLEScannerBuilder;
import com.guardiansofgalakddy.lvlmonitor.seungju.Data;

public class CollectorActivity extends AppCompatActivity {
    BLEScanner scanner = null;
    BroadcastReceiver receiver = null;

    TextView txtDevice, txtLog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collector);

        txtDevice = findViewById(R.id.txt_Device);
        txtLog = findViewById(R.id.txt_Log);

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
                txtDevice.setText(title);
            }
        };
        LocalBroadcastManager.getInstance(getApplicationContext()).
                registerReceiver(receiver, new IntentFilter("com.guardiansofgalakddy.lvlmonitor.action.broadcastuuid"));

        Button btnDiscover = findViewById(R.id.btn_Discover);
        btnDiscover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanner.discover();
            }
        });

        Button btnConnect = findViewById(R.id.btn_Connect);
        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanner.connect();
            }
        });

        Button btnRequest = findViewById(R.id.btn_request);
        btnRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanner.requestHistory();
            }
        });
    }
}
