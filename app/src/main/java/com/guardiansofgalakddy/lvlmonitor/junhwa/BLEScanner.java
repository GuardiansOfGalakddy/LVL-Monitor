package com.guardiansofgalakddy.lvlmonitor.junhwa;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static android.content.Context.BLUETOOTH_SERVICE;

public class BLEScanner {
    private static final UUID BLUETOOTH_LE_TELIT_SERVICE = UUID.fromString("0000fefb-0000-1000-8000-00805f9b34fb");
    private static final UUID TELIT_DATA_RX = UUID.fromString("00000001-0000-1000-8000-008025000000");
    private static final UUID TELIT_DATA_TX = UUID.fromString("00000002-0000-1000-8000-008025000000");
    private static final UUID TELIT_DATA_RX_CREDIT = UUID.fromString("00000003-0000-1000-8000-008025000000");
    private static final UUID TELIT_DATA_TX_CREDIT = UUID.fromString("00000004-0000-1000-8000-008025000000");
    private static final UUID CAHRACTERISTIC_NOTIFICATION_DESCRIPTOR = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

    private static final byte[] BEACON_DATA_HEADER = {0x02, 0x01, 0x06, 0x1A, (byte) 0xFF, 0x4C, 0x00, 0x02, 0x15};
    private static final byte[] BEACON_DATA_TAIL = {0x00, 0x01, 0x00, 0x02, (byte) 0xC5};
    private static final byte[] BEACON_MANUFACTURER_DATA = {0x02, 0x15, 0x0F, 0x00, 0x01, 0x01, 0x00, 0x00
            , 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01, 0x00, 0x02, (byte) 0xC5};
    private static final byte[] BEACON_MANUFACTURER_DATA_MASK = {0x01, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00
            , 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01, 0x01, 0x01, 0x01, 0x01};

    private static final byte[] REQUEST_ALARM_HISTORY = {(byte) 0xFE, (byte) 0xFE, 0x05, 0x11, 0x02, 0x03, 0x00, 0x00, (byte) 0xFD, (byte) 0xFD};

    private LocalBroadcastManager broadcastManager = null;

    private BluetoothManager bluetoothManager = null;
    private BluetoothAdapter adapter = null;
    private BluetoothGatt bluetoothGatt = null;
    private BluetoothGattCharacteristic rx, tx, rxCredit, txCredit = null;
    private BluetoothDevice device = null;

    private byte[] history = null;
    private int hPosition = 0;
    private Boolean isReceiving = false;
    private Boolean isConnected = false;

    private Context context = null;

    BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.i("gattCallback", "new State = Connected");
                Log.i("gattCallback", "Attempting to start service discovery:" + bluetoothGatt.discoverServices());
                isConnected = true;
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.i("gattCallback", "new State = Disconnected");
                isConnected = false;
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.i("onServicesDiscovered", "onServicesDiscovered received: " + gatt.getServices().size());
                BluetoothGattService terminalIOService = gatt.getService(BLUETOOTH_LE_TELIT_SERVICE);
                for (BluetoothGattCharacteristic characteristic : terminalIOService.getCharacteristics()) {
                    if (characteristic.getUuid().equals(TELIT_DATA_RX_CREDIT))
                        rxCredit = characteristic;
                    else if (characteristic.getUuid().equals(TELIT_DATA_TX_CREDIT))
                        txCredit = characteristic;
                    else if (characteristic.getUuid().equals(TELIT_DATA_RX))
                        rx = characteristic;
                    else if (characteristic.getUuid().equals(TELIT_DATA_TX))
                        tx = characteristic;

                    for (BluetoothGattDescriptor descriptor : characteristic.getDescriptors()) {
                        Log.d("descriptor", descriptor.getUuid() + " " + descriptor.getPermissions());
                    }
                }

                //connect to terminal io service
                subscribe(txCredit);
            } else {
                Log.w("onServicesDiscovered", "onServicesDiscovered received: " + status);
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            if (characteristic.getUuid().equals(TELIT_DATA_TX)) {
                byte[] data = characteristic.getValue();
                Log.d("receive", Aes.byteArrayToHexString(data));
                if (isReceiving) {
                    if (data[data.length - 2] == (byte) 0xFD && data[data.length - 1] == (byte) 0xFD) {
                        System.arraycopy(data, 0, history, hPosition, data.length - 2);
                        hPosition = 0;
                        isReceiving = false;
                        Intent intent = new Intent();
                        intent.setAction("com.guardiansofgalakddy.lvlmonitor.action.sendhistory");
                        intent.putExtra("HISTORY", history);
                        broadcastManager.sendBroadcast(intent);
                    } else {
                        System.arraycopy(data, 0, history, hPosition, 20);
                        hPosition += 20;
                    }
                } else if (data.length == 20){
                    history = new byte[data[10] * 24];
                    isReceiving = true;
                    System.arraycopy(data, 12, history, 0, 8);
                    hPosition += 8;
                }
            }
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            Log.i("onDescriptorWrite", descriptor.toString() + " " + status);
            if (descriptor.getCharacteristic().equals(txCredit))
                subscribe(tx);
            else if (descriptor.getCharacteristic().equals(tx))
                trySend(rxCredit, 64);

            super.onDescriptorWrite(gatt, descriptor, status);
        }
    };

    //discover components
    private BluetoothLeScanner mBluetoothLeScanner;
    private Handler mHandler = new Handler();
    private ScanCallback rsScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            if (result == null)
                return;

            byte[] data = result.getScanRecord().getManufacturerSpecificData().valueAt(0);
            Log.i("onScanResult", "Receive >> " + Aes.byteArrayToHexString(data));
            //device = result.getDevice();

            Intent intent = new Intent();
            intent.setAction("com.guardiansofgalakddy.lvlmonitor.action.broadcastuuid");
            intent.putExtra("MANUFACTURER_DATA", data);
            //intent.putExtra("DEVICE", device);
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

    private ScanCallback bsScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            if (result == null)
                return;

            byte[] data = result.getScanRecord().getBytes();
            Log.i("onScanResult", "Receive >> " + Aes.byteArrayToHexString(data));
            device = result.getDevice();

            Intent intent = new Intent();
            intent.setAction("com.guardiansofgalakddy.lvlmonitor.action.broadcastbs");
            intent.putExtra("DEVICE", device);
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
    //discover components

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
        broadcastManager = LocalBroadcastManager.getInstance(context);
        return true;
    }

    public void discoverRS() {
        ScanFilter filter = new ScanFilter.Builder()
                .setManufacturerData(0x4C, BEACON_MANUFACTURER_DATA, BEACON_MANUFACTURER_DATA_MASK)
                .build();

        List<ScanFilter> filters = new ArrayList<>();
        filters.add(filter);

        ScanSettings settings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .build();

        mBluetoothLeScanner.startScan(filters, settings, rsScanCallback);

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mBluetoothLeScanner.stopScan(rsScanCallback);
            }
        }, 5000);
    }

    public void discoverBS() {
        ScanFilter filter = new ScanFilter.Builder()
                .setServiceUuid(new ParcelUuid(BLUETOOTH_LE_TELIT_SERVICE))
                .build();

        List<ScanFilter> filters = new ArrayList<>();
        filters.add(filter);

        ScanSettings settings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .build();

        mBluetoothLeScanner.startScan(filters, settings, bsScanCallback);

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mBluetoothLeScanner.stopScan(bsScanCallback);
            }
        }, 5000);
    }

    public void connect(BluetoothDevice device) {
        this.device = device;
        bluetoothGatt = device.connectGatt(context, false, gattCallback, BluetoothDevice.TRANSPORT_LE);
    }

    public void disConnect() {
        bluetoothGatt.disconnect();
    }

    private void subscribe(BluetoothGattCharacteristic characteristic) {
        bluetoothGatt.setCharacteristicNotification(characteristic, true);
        characteristic.setWriteType(2);
        BluetoothGattDescriptor descriptor = characteristic.getDescriptor(CAHRACTERISTIC_NOTIFICATION_DESCRIPTOR);
        if (characteristic.getUuid().equals(TELIT_DATA_TX_CREDIT))
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE);
        else if (characteristic.getUuid().equals(TELIT_DATA_TX))
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        bluetoothGatt.writeDescriptor(descriptor);
    }

    private void trySend(BluetoothGattCharacteristic characteristic, int nCredits) {
        boolean bResult = characteristic.setValue(nCredits, BluetoothGattCharacteristic.FORMAT_UINT8, 0);
        Log.v("ble", "trySendCredits setValue(): " + bResult);
        bResult = bluetoothGatt.writeCharacteristic(characteristic);
        Log.v("ble", "trySendCredits writeCharacteristic(): " + bResult);
    }

    public void requestHistory() {
        rx.setValue(REQUEST_ALARM_HISTORY);
        bluetoothGatt.writeCharacteristic(rx);
    }

    public Boolean getConnected() {
        return isConnected;
    }
}
