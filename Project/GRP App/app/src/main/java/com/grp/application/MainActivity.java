package com.grp.application;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import com.example.application.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.grp.application.database.DatabaseHelper;
import com.grp.application.monitor.Monitor;
import com.grp.application.polar.PolarDevice;
import com.grp.application.scale.Scale;
//import com.jjoe64.graphview.GraphView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.PreferenceManager;

import polar.com.sdk.api.errors.PolarInvalidArgument;

/**
 * {@code MainActivity} is the root activity for the application.
 *
 * @author UNNC GRP G19
 * @version 1.0
 */
public class MainActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(Application.context);

    //
    private static final String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                //
                int REQUEST_PERMISSION_CODE = 1;
                ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_PERMISSION_CODE);
            }
        }

        try {
            checkPreferences();
        } catch (PolarInvalidArgument polarInvalidArgument) {
            polarInvalidArgument.printStackTrace();
        }
        checkBT();
        requestMyPermissions();
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_report, R.id.navigation_settings)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        //7NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        // Create database
        DatabaseHelper helper = new DatabaseHelper(this);
        helper.getWritableDatabase();



        // Database simulator
//        DatabaseSimulator databaseSimulator = new DatabaseSimulator(getApplicationContext());
//        databaseSimulator.insertTestData();
//        databaseSimulator.computeInsertTodayData();
//        databaseSimulator.clearTodayData();


    }

    /**
     * Check whether Bluetooth is enable.
     * Request permission if Bluetooth is off.
     */
    public void checkBT() {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter != null && !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 2);
        }
        this.requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1); //Request permission
    }

    private void requestMyPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
        }
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
        }
    }

    private void checkPreferences() throws PolarInvalidArgument {
        String polarDeviceID = sharedPreferences.getString(Application.POLAR_KEY, "");
        String scaleDeviceAddress = sharedPreferences.getString(Application.SCALE_ADDRESS_KEY, "");
        boolean isMessageEnabled = sharedPreferences.getBoolean(Application.MESSAGE_KEY, false);
        boolean isWarningEnabled = sharedPreferences.getBoolean(Application.WARNING_KEY, true);
        boolean isAlertEnabled = sharedPreferences.getBoolean(Application.ALERT_KEY, false);
        Monitor monitor = Monitor.getInstance();

        if (!polarDeviceID.equals("")) {
            PolarDevice polarDevice = PolarDevice.getInstance();
            polarDevice.setDeviceId(polarDeviceID);
            polarDevice.api().connectToDevice(polarDevice.getDeviceId());
        }
        if (!scaleDeviceAddress.equals("")) {
            Scale scale = Scale.getInstance();
            scale.setDeviceName(sharedPreferences.getString(Application.SCALE_NAME_KEY, ""));
            scale.setHwAddress(scaleDeviceAddress);
            monitor.getMonitorState().connectScaleDevice();
        }
        if (isMessageEnabled) {
            monitor.getMonitorState().enableMsgOnNotWearDevice();
        }
        if (isWarningEnabled) {
            monitor.getMonitorState().enableMsgOnNotCaptureData();
        }
        if (isAlertEnabled) {
            monitor.getMonitorState().enableMsgOnReportGenerated();
        }
    }

    private static final int REQUEST_EXTERNAL_STORAGE = 1;

    private void alarmTry() {

    }
}