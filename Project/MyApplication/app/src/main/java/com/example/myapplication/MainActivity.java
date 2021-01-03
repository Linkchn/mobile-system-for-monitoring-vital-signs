package com.example.myapplication;

import android.os.Bundle;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.tabs.TabLayout;

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
        if (mToast == null) {
            mToast = Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT);
        }else {
            mToast.cancel();
            mToast = Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT);
        }
        mToast.show();
    }

    public void setSwitchView(SwitchMaterial switchButton, Switches switches) {
        switchButton.setChecked(isSwitchChecked(switches));
    }

    public void setTabView(TabLayout tab) {
        tab.getTabAt(durationTab.getIndex()).select();
    }

    public void setDurationTabSelected(DurationTab tab) {
        durationTab = tab;
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

    public boolean isSwitchChecked(Switches switches) {
        boolean isChecked;
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
            default: isChecked = false;
        }
        return isChecked;
    }
}