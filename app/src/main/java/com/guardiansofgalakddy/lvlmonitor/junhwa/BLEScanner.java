package com.guardiansofgalakddy.lvlmonitor.junhwa;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.ParcelUuid;
import android.util.Log;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.guardiansofgalakddy.lvlmonitor.superb.HexToByte;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static android.content.Context.BLUETOOTH_SERVICE;

public class BLEScanner {
    BluetoothManager bluetoothManager = null;
    BluetoothAdapter adapter = null;
    Context context = null;

    //discover() components
    private BluetoothLeScanner mBluetoothLeScanner;
    private Handler mHandler = new Handler();
    private ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);

            if (result == null || result.getScanRecord().getServiceUuids() == null)
                return;
            String uuid = result.getScanRecord().getServiceUuids().get(0).toString();
            String[] split = uuid.split("-");
            uuid = split[0] + split[1] + split[2] + split[3] + split[4];
            byte[] bytes = HexToByte.hexStringToByteArray(uuid);
            uuid = HexToByte.byteArrayToString(bytes);

            Intent intent = new Intent();
            intent.setAction("com.guardiansofgalakddy.lvlmonitor.action.broadcastuuid");
            intent.putExtra("UUID", uuid);
            LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(context);
            broadcastManager.sendBroadcast(intent);
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
        }
    };
    //discover() components

    public Boolean initialize(Context context) {
        Log.i("BLEScanner", "initialize() called");
        this.context = context;
        if (!context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(context, "this device does not support BLE", Toast.LENGTH_SHORT).show();
            return false;
        }

        bluetoothManager = (BluetoothManager) context.getSystemService(BLUETOOTH_SERVICE);
        adapter = bluetoothManager.getAdapter();

        if (!adapter.isMultipleAdvertisementSupported()) {
            Toast.makeText(context, "Multiple advertisement not supported", Toast.LENGTH_LONG).show();
            return false;
        }

        mBluetoothLeScanner = adapter.getBluetoothLeScanner();
        return true;
    }

    public void discover() {
        long id0 = 0x0000000000000000L;
        long id1 = 0x0052532d00000000L;
        long id2 = 0x0042532d00000000L;
        long mask = 0x1111111100000000L;
        UUID uuid1 = new UUID(id1, id0);
        UUID uuid2 = new UUID(id2, id0);
        UUID maskUuid = new UUID(mask, id0);
        ParcelUuid parcelUuid1 = new ParcelUuid(uuid1);
        ParcelUuid parcelUuid2 = new ParcelUuid(uuid2);
        ParcelUuid maskParcelUuid = new ParcelUuid(maskUuid);

        ScanFilter filter1 = new ScanFilter.Builder()
                .setServiceUuid(parcelUuid1, maskParcelUuid)
                .build();
        ScanFilter filter2 = new ScanFilter.Builder()
                .setServiceUuid(parcelUuid2, maskParcelUuid)
                .build();

        List<ScanFilter> filters = new ArrayList<>();
        filters.add(filter1);
        filters.add(filter2);

        ScanSettings settings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .build();

        mBluetoothLeScanner.startScan(filters, settings, mScanCallback);

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mBluetoothLeScanner.stopScan(mScanCallback);
            }
        }, 5000);
    }
}
