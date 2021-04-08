package com.grp.application.pages;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.example.application.R;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.grp.application.Application;
import com.grp.application.GRPNotification.GRPNotification;
import com.grp.application.database.Dao;
import com.grp.application.MainActivity;
import com.grp.application.ScaleSearchActivity;
import com.grp.application.export.FileLog;
import com.grp.application.monitor.Monitor;
import com.grp.application.polar.PolarDevice;
import com.grp.application.scale.Scale;

import java.io.IOException;
import java.util.Date;

import polar.com.sdk.api.PolarBleApiCallback;
import polar.com.sdk.api.errors.PolarInvalidArgument;
import polar.com.sdk.api.model.PolarDeviceInfo;
import polar.com.sdk.api.model.PolarHrData;

/**
 * {@code SettingsFragment} is class to maintain UI elements and functions of settings page.
 *
 * @author UNNC GRP G19
 * @version 1.0
 */
public class SettingsFragment extends Fragment {

    private Monitor monitor;
    private PolarDevice polarDevice;
    private ImageView symbolHrDevice;
    private ImageView symbolBrainWaveDevice;
    private Button hrConnectButton;
    private Button scaleConnectButton;
    private Button hrDeviceDisconenctButton;
    private Button scaleDeviceDisconnectButton;
    private Button ageSetButton;
    private  Button resetDatabaseButton;
    private  Button exportDatabaseButton;
    private  Button viewRecordedButton;
    private SwitchMaterial simulationSwitch;
    private SwitchMaterial msgOnNotWearDeviceSwitch;
    private SwitchMaterial msgOnNotCaptureDataSwitch;
    private SwitchMaterial msgOnReportGenerated;
    private TextView ageText;
    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(Application.context);


    public SettingsFragment() {}

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_settings, container, false);
        monitor = Monitor.getInstance();
        polarDevice = PolarDevice.getInstance();
        symbolHrDevice = root.findViewById(R.id.symbol_hr_device);
        symbolBrainWaveDevice = root.findViewById(R.id.symbol_scale_device);
        hrConnectButton = root.findViewById(R.id.button_connect_hr_device);
        scaleConnectButton = root.findViewById(R.id.button_connect_scale_device);
        hrDeviceDisconenctButton = root.findViewById(R.id.button_disconnect_hr_device);
        scaleDeviceDisconnectButton = root.findViewById(R.id.button_disconnect_scale_device);
        ageSetButton = root.findViewById(R.id.button_set_age);
        simulationSwitch = root.findViewById(R.id.switch_simulation);
        msgOnNotWearDeviceSwitch = root.findViewById(R.id.switch_msg_not_wear_device);
        msgOnNotCaptureDataSwitch = root.findViewById(R.id.switch_msg_not_capture_data);
        msgOnReportGenerated = root.findViewById(R.id.switch_msg_report_generated);
        resetDatabaseButton =  root.findViewById(R.id.reset_database);
        exportDatabaseButton =  root.findViewById(R.id.export_database);
        viewRecordedButton = root.findViewById(R.id.view_recorded_data);
        ageText = root.findViewById(R.id.text_age);

        resetUI();
        initDevice();

        // Set action for connect button of heart rate device
        hrConnectButton.setOnClickListener(this::showPolarDeviceDialog);

        // Set action for connect button of scale device
        scaleConnectButton.setOnClickListener((view) -> {
            Intent intent = new Intent(getActivity(), ScaleSearchActivity.class);
            startActivity(intent);
        });

        // Set action for disconnect button of heart rate device
        hrDeviceDisconenctButton.setOnClickListener((buttonView) -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove(Application.POLAR_KEY);
            editor.apply();

            try {
                polarDevice.api().disconnectFromDevice(polarDevice.getDeviceId());
            } catch (PolarInvalidArgument polarInvalidArgument) {
                polarInvalidArgument.printStackTrace();
            }
        });

        // Set action for disconnect button of scale device
        scaleDeviceDisconnectButton.setOnClickListener((buttonView) ->  {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove(Application.SCALE_NAME_KEY);
            editor.remove(Application.SCALE_ADDRESS_KEY);
            editor.apply();

            monitor.getMonitorState().disconnectScaleDevice();
            monitor.showToast("Disconnect from Scale Device");
            resetUI();
        });

        ageSetButton.setOnClickListener(this::showAgeSetDialog);

        // Set action for "simulation" switch
        simulationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                monitor.showToast("Start Simulation");
                monitor.getMonitorState().enableSimulation();
            } else {
                monitor.showToast("Stop Simulation");
                monitor.getMonitorState().simulationOff();
                monitor.getMonitorState().disableSimulation();
                monitor.getMonitorState().disableStartCaptureData();
                monitor.getPlotterHR().clearVal();
                monitor.getPlotterECG().clearVal();
            }
            resetUI();
        });

        // Set action for "receive message when not wear device" switch
        msgOnNotWearDeviceSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                monitor.showToast("Start Message");
                monitor.getMonitorState().enableMsgOnNotWearDevice();
            } else {
                monitor.showToast("Stop Message");
                monitor.getMonitorState().disableMsgOnNotWearDevice();
            }
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(Application.MESSAGE_KEY, isChecked);
            editor.apply();
        });

        // Set action for "receive warning when not capture data" switch
        msgOnNotCaptureDataSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                monitor.showToast("Start Warning");
                monitor.getMonitorState().enableMsgOnNotCaptureData();
            } else {
                monitor.showToast("Stop Warning");
                monitor.getMonitorState().disableMsgOnNotCaptureData();
            }
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(Application.WARNING_KEY, isChecked);
            editor.apply();
        });

        // Set action for "receive alert when report generated" switch
        msgOnReportGenerated.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                monitor.showToast("Start Alert");
                monitor.getMonitorState().enableMsgOnReportGenerated();
            } else {
                monitor.showToast("Stop Alert");
                monitor.getMonitorState().disableMsgOnReportGenerated();
            }
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(Application.ALERT_KEY, isChecked);
            editor.apply();
        });

        exportDatabaseButton.setOnClickListener((views) -> {
            exportHR();
        });

        resetDatabaseButton.setOnClickListener((views) -> {
            new AlertDialog.Builder(getContext()).setTitle("Warning")
                    .setMessage("The reports will disappear if you clear the database. Are you sure to continue? ")
                    .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Dao dao = new Dao(getContext());
                            dao.clearDatabase();
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    }).show();
        });

        viewRecordedButton.setOnClickListener((views) -> {
            openAssignFolder("%2fVitalSigns%2f");
        });

        return root;
    }


    private void openAssignFolder(String path) {

        Uri uri = Uri.parse("content://com.android.externalstorage.documents/document/primary:" + path);
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, uri);
        startActivityForResult(intent, 0);
    }

    public void onResume() {
        resetUI();
        super.onResume();
    }

    private void exportHR() {
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 101);
        } else {
            Dao dao = new Dao(getContext());
            dao.exportHrData();
        }
    }

    /**
     * Initial device.
     */
    private void initDevice() {
        polarDevice.api().setApiCallback(new PolarBleApiCallback() {
            @Override
            public void deviceConnected(@NonNull PolarDeviceInfo polarDeviceInfo) {
                monitor.showToast(polarDeviceInfo.deviceId + " is Connected");
                monitor.getMonitorState().connectHRDevice();
                resetUI();
            }

            @Override
            public void deviceDisconnected(@NonNull PolarDeviceInfo polarDeviceInfo) {
                monitor.showToast(polarDeviceInfo.deviceId + "is Disconnected");
                monitor.getMonitorState().disconnectHRDevice();
                monitor.getMonitorState().disableStartCaptureData();
                monitor.getPlotterHR().clearVal();
                monitor.getPlotterECG().clearVal();
                resetUI();
            }

            @Override
            public void hrNotificationReceived(@NonNull String identifier, @NonNull PolarHrData data) {
                if(data.hr <= 0 && monitor.getMonitorState().isMsgOnNotWearDeviceEnabled()){
                    GRPNotification grpNotification = GRPNotification.getInstance(Application.context);
                    grpNotification.sendMsgOnNotWearDevice(Application.context);
                } else {
                    monitor.getPlotterHR().addValues(data);
                    monitor.checkHrRange(data.hr);
                }
            }

            @Override
            public void ecgFeatureReady(@NonNull String identifier) {
                monitor.streamECG();
            }

            @Override
            public void accelerometerFeatureReady(@NonNull String identifier) {
                monitor.streamACC();
            }
        });
    }

    /**
     * Reset UI.
     */
    private void resetUI() {
        monitor.getViewSetter().setDeviceView(symbolHrDevice, hrConnectButton, hrDeviceDisconenctButton,
                monitor.getMonitorState().isHRDeviceConnected(), monitor.getMonitorState().isSimulationEnabled());
        monitor.getViewSetter().setDeviceView(symbolBrainWaveDevice, scaleConnectButton, scaleDeviceDisconnectButton,
                monitor.getMonitorState().isScaleDeviceConnected(), monitor.getMonitorState().isSimulationEnabled());
        monitor.getViewSetter().setSwitchView(simulationSwitch, monitor.getMonitorState().isSimulationEnabled(), !monitor.getMonitorState().isHRDeviceConnected()
        && !monitor.getMonitorState().isScaleDeviceConnected());
        monitor.getViewSetter().setSwitchView(msgOnNotWearDeviceSwitch, monitor.getMonitorState().isMsgOnNotWearDeviceEnabled());
        monitor.getViewSetter().setSwitchView(msgOnNotCaptureDataSwitch, monitor.getMonitorState().isMsgOnNotCaptureDataEnabled());
        monitor.getViewSetter().setSwitchView(msgOnReportGenerated, monitor.getMonitorState().isMsgOnReportGeneratedEnabled());
        monitor.getViewSetter().setAgeView(ageSetButton, ageText, monitor.getMonitorState().isAgeSet());
    }

    /**
     * Show heart rate device connection dialog.
     * @param view view
     */
    private void showPolarDeviceDialog(View view) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this.getContext(), R.style.PolarTheme);
        dialog.setTitle("Enter your Polar device's ID");

        View viewInflated = LayoutInflater.from(monitor.getContext()).inflate(R.layout.device_id_dialog_layout, (ViewGroup) view.getRootView(), false);

        final EditText input = viewInflated.findViewById(R.id.input);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        dialog.setView(viewInflated);

        dialog.setPositiveButton("OK", (dialog1, which) -> {
            polarDevice.setDeviceId(input.getText().toString());
            try {
                polarDevice.api().connectToDevice(polarDevice.getDeviceId());
            } catch (PolarInvalidArgument polarInvalidArgument) {
                polarInvalidArgument.printStackTrace();
            }
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(Application.POLAR_KEY, polarDevice.getDeviceId());
            editor.apply();
        });
        dialog.setNegativeButton("Cancel", (dialog12, which) -> dialog12.cancel());
        dialog.show();
    }

    /**
     * Show scale device connection dialog.
     * @param view view
     */
    private void showScaleDialog(View view) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this.getContext(), R.style.PolarTheme);
        dialog.setTitle("Enter your Scale MAC address");

        View viewInflated = LayoutInflater.from(monitor.getContext()).inflate(R.layout.scale_connection_dialog_layout, (ViewGroup) view.getRootView(), false);

        final EditText input = viewInflated.findViewById(R.id.input);
        RadioButton buttonRenpho = viewInflated.findViewById(R.id.button_renpho);
        RadioButton buttonYunmai = viewInflated.findViewById(R.id.button_yunmai);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        dialog.setView(viewInflated);

        dialog.setPositiveButton("OK", (dialog1, which) -> {
            Scale scale = Scale.getInstance();
            if (buttonRenpho.isChecked()) {
                scale.setDeviceName("QN-Scale");
            } else if (buttonYunmai.isChecked()){
                scale.setDeviceName("YUNMAI-SIGNAL");
            }
            scale.setHwAddress(input.getText().toString());
            monitor.getMonitorState().connectScaleDevice();
            resetUI();
        });
        dialog.setNegativeButton("Cancel", (dialog12, which) -> dialog12.cancel());
        dialog.show();
    }

    private void showAgeSetDialog(View view) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this.getContext(), R.style.PolarTheme);
        dialog.setTitle("Enter your age");

        View viewInflated = LayoutInflater.from(monitor.getContext()).inflate(R.layout.age_dialog_layout, (ViewGroup) view.getRootView(), false);

        final EditText input = viewInflated.findViewById(R.id.input_age);
        dialog.setView(viewInflated);

        dialog.setPositiveButton("OK", (dialog1, which) -> {
            monitor.setAge(Integer.parseInt(input.getText().toString()));
            monitor.getMonitorState().setAge();
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(Application.AGE_KEY, monitor.getAge());
            editor.apply();
            resetUI();
        });
        dialog.setNegativeButton("Cancel", (dialog12, which) -> dialog12.cancel());
        dialog.show();
    }
}