package com.guardiansofgalakddy.lvlmonitor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.oss.licenses.OssLicensesMenuActivity;
import com.google.android.material.navigation.NavigationView;
import com.pedro.library.AutoPermissions;
import com.pedro.library.AutoPermissionsListener;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class MainActivity extends AppCompatActivity implements AutoPermissionsListener {
    private DrawerLayout drawerLayout;
    private Button button1, button2;

    BluetoothManager bluetoothManager = null;
    BluetoothAdapter adapter = null;

    //discover() components
    private BluetoothLeScanner mBluetoothLeScanner;
    private Handler mHandler = new Handler();
    private ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            //Log.d("BLE", "onScanResult");
            if(result == null || /*result.getDevice() == null || TextUtils.isEmpty(result.getDevice().getName())*/result.getScanRecord().getServiceUuids() == null)
                return;

            StringBuilder builder = new StringBuilder(result.getScanRecord().getServiceUuids().get(0).toString());
            Log.d("onScanResult", builder.toString());
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        actionBar.setDisplayHomeAsUpEnabled(true);

        drawerLayout = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.navigation_item_tmp:
                        Toast.makeText(getApplicationContext(), "tmp1", Toast.LENGTH_LONG).show();
                        break;
                    case R.id.navigation_item_tmp2:
                        Toast.makeText(getApplicationContext(), "tmp2", Toast.LENGTH_LONG).show();
                        break;
                    case R.id.nav_sub_menu_open_source:
                        Intent intent = new Intent(getApplicationContext(), OssLicensesMenuActivity.class);
                        startActivity(intent);
                        break;
                }
                return true;
            }
        });

        button1 = findViewById(R.id.button);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MonitorActivity.class);
                startActivity(intent);
            }
        });

        button2 = findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                discover();
                //Intent intent = new Intent(getApplicationContext(), CollectorActivity.class);
                //startActivity(intent);
            }
        });

        /* AutoPermission 권한 요청 */
        AutoPermissions.Companion.loadAllPermissions(this, 101);
        initialize();

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
  
    private void initialize() {
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "this device does not support BLE", Toast.LENGTH_SHORT).show();
            finish();
        }

        bluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        adapter = bluetoothManager.getAdapter();

        if(!adapter.isMultipleAdvertisementSupported()) {
            Toast.makeText(this, "Multiple advertisement not supported", Toast.LENGTH_LONG).show();
            button2.setEnabled(false);
        }

        mBluetoothLeScanner = adapter.getBluetoothLeScanner();
    }

    private void discover() {
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

    @Override
    public void onDenied(int i, String[] strings) {
        finish();
    }

    @Override
    public void onGranted(int i, String[] strings) {

    }
}
