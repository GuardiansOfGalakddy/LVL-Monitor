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
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

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
            String systemID = uuid.substring(2, 8);
            Log.d("onScanResult", uuid);
            if (systemID.equals("52532d") || systemID.equals("42532d")) {//"RS-" or "BS-"
                Log.d("onScanResult", "OK");

                Intent intent = new Intent();
                intent.setAction("com.guardiansofgalakddy.lvlmonitor.action.broadcastuuid");
                intent.putExtra("UUID", uuid);
                LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(context);
                broadcastManager.sendBroadcast(intent);
            }
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
        ScanFilter filter = new ScanFilter.Builder()
                //.setServiceUuid(new ParcelUuid(UUID.fromString(getString(R.string.ble_uuid))))
                .build();
        List<ScanFilter> filters = new List<ScanFilter>() {
            @Override
            public int size() {
                return 0;
            }

            @Override
            public boolean isEmpty() {
                return false;
            }

            @Override
            public boolean contains(@Nullable Object o) {
                return false;
            }

            @NonNull
            @Override
            public Iterator<ScanFilter> iterator() {
                return null;
            }

            @NonNull
            @Override
            public Object[] toArray() {
                return new Object[0];
            }

            @NonNull
            @Override
            public <T> T[] toArray(@NonNull T[] a) {
                return null;
            }

            @Override
            public boolean add(ScanFilter scanFilter) {
                return false;
            }

            @Override
            public boolean remove(@Nullable Object o) {
                return false;
            }

            @Override
            public boolean containsAll(@NonNull Collection<?> c) {
                return false;
            }

            @Override
            public boolean addAll(@NonNull Collection<? extends ScanFilter> c) {
                return false;
            }

            @Override
            public boolean addAll(int index, @NonNull Collection<? extends ScanFilter> c) {
                return false;
            }

            @Override
            public boolean removeAll(@NonNull Collection<?> c) {
                return false;
            }

            @Override
            public boolean retainAll(@NonNull Collection<?> c) {
                return false;
            }

            @Override
            public void clear() {

            }

            @Override
            public ScanFilter get(int index) {
                return null;
            }

            @Override
            public ScanFilter set(int index, ScanFilter element) {
                return null;
            }

            @Override
            public void add(int index, ScanFilter element) {

            }

            @Override
            public ScanFilter remove(int index) {
                return null;
            }

            @Override
            public int indexOf(@Nullable Object o) {
                return 0;
            }

            @Override
            public int lastIndexOf(@Nullable Object o) {
                return 0;
            }

            @NonNull
            @Override
            public ListIterator<ScanFilter> listIterator() {
                return null;
            }

            @NonNull
            @Override
            public ListIterator<ScanFilter> listIterator(int index) {
                return null;
            }

            @NonNull
            @Override
            public List<ScanFilter> subList(int fromIndex, int toIndex) {
                return null;
            }
        };
        filters.add(filter);

        ScanSettings settings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .build();

        mBluetoothLeScanner.startScan(filters, settings, mScanCallback);

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mBluetoothLeScanner.stopScan(mScanCallback);
            }
        }, 10000);
    }
}
