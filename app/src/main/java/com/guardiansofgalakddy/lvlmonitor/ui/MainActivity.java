package com.guardiansofgalakddy.lvlmonitor.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.oss.licenses.OssLicensesMenuActivity;
import com.google.android.material.navigation.NavigationView;
import com.guardiansofgalakddy.lvlmonitor.R;
import com.guardiansofgalakddy.lvlmonitor.seungju.LVLDBManager;
import com.guardiansofgalakddy.lvlmonitor.seungju.dbData;
import com.pedro.library.AutoPermissions;
import com.pedro.library.AutoPermissionsListener;

public class MainActivity extends AppCompatActivity implements AutoPermissionsListener {
    private DrawerLayout drawerLayout;
    private LinearLayout button1, button2;

    /* Back button check */
    private long pressedTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
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
                        Intent intento = new Intent(getApplicationContext(),DBlistActivity.class);
                        startActivity(intento);
                        break;
                    case R.id.navigation_item_tmp2:
                        LVLDBManager mDbManager = LVLDBManager.getInstance(getApplicationContext());
                        for(int i=0;i<10;i++)
                        {

                            ContentValues addRowValue = new ContentValues();
                            addRowValue.put("systemid","Title"+String.valueOf(i));
                            addRowValue.put("latitude",i);
                            addRowValue.put("longitude",i+100);

                            long insertRecordId = mDbManager.insert(addRowValue);

                        }

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

        button1 = findViewById(R.id.btn_monitor);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MonitorActivity.class);
                startActivity(intent);
            }
        });
        button2 = findViewById(R.id.btn_collector);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SearchBsActivity.class);
                startActivity(intent);
            }
        });


        /* AutoPermission 권한 요청 */
        AutoPermissions.Companion.loadAllPermissions(this, 101);
    }

    @Override
    public void onBackPressed() {
        if(System.currentTimeMillis() > pressedTime + 2000){
            pressedTime = System.currentTimeMillis();
            Toast.makeText(getApplicationContext(), "한번 더 누르면 종료됩니다.", Toast.LENGTH_LONG).show();
        }else{
            finish();
        }
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

    @Override
    public void onDenied(int i, String[] strings) {
        finish();
    }

    @Override
    public void onGranted(int i, String[] strings) {

    }
}
