package com.grp.application;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.application.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.tabs.TabLayout;
import com.grp.application.components.Devices;
import com.grp.application.components.DurationTab;
import com.grp.application.components.Switches;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static boolean enableStartCaptureData = false;
    private static boolean enableMsgOnNotWearDevice = false;
    private static boolean enableMsgOnNotCaptureData = true;
    private static boolean enableMsgOnReportGenerated = false;
    private static boolean enableHrDevice = false;
    private static boolean enableBrainWaveDevice = false;
    private static DurationTab durationTab = DurationTab.DAILY;
    private static Toast mToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_report, R.id.navigation_settings)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        //NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

    }

    public void showToast(String text){
        if (mToast != null) {
            mToast.cancel();
        }
        mToast = Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT);
        mToast.show();
    }

    public void setSwitchView(SwitchMaterial switchButton, Switches switches) {
        switchButton.setChecked(isSwitchChecked(switches));
    }

    public void setTabView(TabLayout tab) {
        tab.getTabAt(durationTab.getIndex()).select();
    }

    public void setDeviceView(ImageView symbol, Button button, Devices devices) {
        boolean isConnected = isDeviceConnected(devices);
        if (isConnected) {
            symbol.setImageDrawable(getDrawable(R.drawable.ic_baseline_check_circle_outline_24));
        } else {
            symbol.setImageDrawable(getDrawable(R.drawable.ic_outline_cancel_24));
        }
        button.setEnabled(!isConnected);
    }

    public void toggleSwitch(Switches switches) {
        switch (switches) {
            case START_CAPTURE_DATA:
                enableStartCaptureData = !enableStartCaptureData;
                break;
            case MSG_ON_NOT_WEAR_DEVICE:
                enableMsgOnNotWearDevice = !enableMsgOnNotWearDevice;
                break;
            case MSG_ON_NOT_CAPTURE_DATA:
                enableMsgOnNotCaptureData = !enableMsgOnNotCaptureData;
                break;
            case MSG_ON_REPORT_GENERATED:
                enableMsgOnReportGenerated = !enableMsgOnReportGenerated;
                break;
        }
    }

    public void setDurationTabSelected(DurationTab tab) {
        durationTab = tab;
    }

    public void toggleDevice(Devices devices) {
        switch (devices) {
            case HEART_RATE_DEVICE:
                enableHrDevice = !enableHrDevice;
                break;
            case BRAIN_WAVE_DEVICE:
                enableBrainWaveDevice = !enableBrainWaveDevice;
                break;
        }
    }

    public boolean isSwitchChecked(Switches switches) {
        boolean isChecked = false;
        switch (switches) {
            case START_CAPTURE_DATA:
                isChecked = enableStartCaptureData;
                break;
            case MSG_ON_NOT_WEAR_DEVICE:
                isChecked = enableMsgOnNotWearDevice;
                break;
            case MSG_ON_NOT_CAPTURE_DATA:
                isChecked = enableMsgOnNotCaptureData;
                break;
            case MSG_ON_REPORT_GENERATED:
                isChecked = enableMsgOnReportGenerated;
                break;
        }
        return isChecked;
    }

    public boolean isDeviceConnected(Devices devices) {
        boolean isConnected = false;
        switch (devices) {
            case HEART_RATE_DEVICE:
                isConnected = enableHrDevice;
                break;
            case BRAIN_WAVE_DEVICE:
                isConnected = enableBrainWaveDevice;
                break;
        }
        return isConnected;
    }
}