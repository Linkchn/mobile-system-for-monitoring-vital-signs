package com.grp.application.pages;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.application.R;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.grp.application.MainActivity;
import com.grp.application.ScaleSearchActivity;
import com.grp.application.monitor.Monitor;
import com.grp.application.polar.PolarDevice;
import com.grp.application.scale.Scale;

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
    private  Button resetDatabaseButton;
    private  Button exportDatabaseButton;
    private SwitchMaterial simulationSwitch;
    private SwitchMaterial msgOnNotWearDeviceSwitch;
    private SwitchMaterial msgOnNotCaptureDataSwitch;
    private SwitchMaterial msgOnReportGenerated;


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
        simulationSwitch = root.findViewById(R.id.switch_simulation);
        msgOnNotWearDeviceSwitch = root.findViewById(R.id.switch_msg_not_wear_device);
        msgOnNotCaptureDataSwitch = root.findViewById(R.id.switch_msg_not_capture_data);
        msgOnReportGenerated = root.findViewById(R.id.switch_msg_report_generated);
        resetDatabaseButton =  root.findViewById(R.id.reset_database);
        exportDatabaseButton =  root.findViewById(R.id.export_database);

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
            try {
                polarDevice.api().disconnectFromDevice(polarDevice.getDeviceId());
            } catch (PolarInvalidArgument polarInvalidArgument) {
                polarInvalidArgument.printStackTrace();
            }
        });

        // Set action for disconnect button of scale device
        scaleDeviceDisconnectButton.setOnClickListener((buttonView) ->  {
            monitor.getMonitorState().disconnectScaleDevice();
            monitor.showToast("Disconnect from Scale Device");
            resetUI();
        });

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
        });

        return root;
    }

    @Override
    public void onResume() {
        resetUI();
        super.onResume();
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
                    monitor.getPlotterHR().addValues(data);
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
            //SharedPreferences.Editor editor = sharedPreferences.edit();
            //editor.putString(SHARED_PREFS_KEY, deviceId);
            //editor.apply();
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
}