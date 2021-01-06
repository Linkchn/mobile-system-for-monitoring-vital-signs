package com.grp.application.pages;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.grp.application.MainActivity;
import com.example.application.R;
import com.grp.application.components.Devices;
import com.grp.application.components.Switches;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.grp.application.polar.PolarDevice;

import polar.com.sdk.api.PolarBleApi;
import polar.com.sdk.api.PolarBleApiCallback;
import polar.com.sdk.api.errors.PolarInvalidArgument;
import polar.com.sdk.api.model.PolarDeviceInfo;

public class SettingsFragment extends Fragment {
    private PolarDevice polarDevice;

    public SettingsFragment() {}

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_settings, container, false);
        MainActivity mainActivity = (MainActivity) getActivity();
        polarDevice = PolarDevice.getInstance(mainActivity);
        ImageView symbolHrDevice = root.findViewById(R.id.symbol_hr_device);
        ImageView symbolBrainWaveDevice = root.findViewById(R.id.symbol_brain_wave_device);
        Button hrConnectButton = root.findViewById(R.id.button_connect_hr_device);
        Button brainWaveConnectButton = root.findViewById(R.id.button_connect_brain_wave_device);
        SwitchMaterial msgOnNotWearDeviceSwitch = root.findViewById(R.id.switch_msg_not_wear_device);
        SwitchMaterial msgOnNotCaptureDataSwitch = root.findViewById(R.id.switch_msg_not_capture_data);
        SwitchMaterial msgOnReportGenerated = root.findViewById(R.id.switch_msg_report_generated);

        mainActivity.setDeviceView(symbolHrDevice, hrConnectButton, Devices.HEART_RATE_DEVICE);
        mainActivity.setDeviceView(symbolBrainWaveDevice,brainWaveConnectButton,Devices.BRAIN_WAVE_DEVICE);
        mainActivity.setSwitchView(msgOnNotWearDeviceSwitch, Switches.MSG_ON_NOT_WEAR_DEVICE);
        mainActivity.setSwitchView(msgOnNotCaptureDataSwitch, Switches.MSG_ON_NOT_CAPTURE_DATA);
        mainActivity.setSwitchView(msgOnReportGenerated, Switches.MSG_ON_REPORT_GENERATED);

        hrConnectButton.setOnClickListener((buttonView) -> {
            polarDevice.api().setApiCallback(new PolarBleApiCallback() {
                @Override
                public void deviceConnected(@NonNull PolarDeviceInfo polarDeviceInfo) {
                    mainActivity.showToast("Connected");
                    mainActivity.toggleDevice(Devices.HEART_RATE_DEVICE);
                    mainActivity.setDeviceView(symbolHrDevice,hrConnectButton,Devices.HEART_RATE_DEVICE);
                }
            });
            showPolarDeviceDialog(buttonView, mainActivity);
        });

        brainWaveConnectButton.setOnClickListener((buttonView) -> {
            mainActivity.toggleDevice(Devices.BRAIN_WAVE_DEVICE);
            mainActivity.setDeviceView(symbolBrainWaveDevice,brainWaveConnectButton,Devices.BRAIN_WAVE_DEVICE);
        });

        msgOnNotWearDeviceSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                mainActivity.showToast("Start Message");
            } else {
                mainActivity.showToast("Stop Message");
            }
            mainActivity.toggleSwitch(Switches.MSG_ON_NOT_WEAR_DEVICE);
        });
        msgOnNotCaptureDataSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                mainActivity.showToast("Start Warning");
            } else {
                mainActivity.showToast("Stop Warning");
            }
            mainActivity.toggleSwitch(Switches.MSG_ON_NOT_CAPTURE_DATA);
        });
        msgOnReportGenerated.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                mainActivity.showToast("Start Alert");
            } else {
                mainActivity.showToast("Stop Alert");
            }
            mainActivity.toggleSwitch(Switches.MSG_ON_REPORT_GENERATED);
        });

        return root;
    }

    private void showPolarDeviceDialog(View view, MainActivity mainActivity) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(mainActivity, R.style.PolarTheme);
        dialog.setTitle("Enter your Polar device's ID");

        View viewInflated = LayoutInflater.from(mainActivity.getApplicationContext()).inflate(R.layout.device_id_dialog_layout, (ViewGroup) view.getRootView(), false);

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
            System.out.println("Here\n");
            //SharedPreferences.Editor editor = sharedPreferences.edit();
            //editor.putString(SHARED_PREFS_KEY, deviceId);
            //editor.apply();
        });
        dialog.setNegativeButton("Cancel", (dialog12, which) -> dialog12.cancel());
        dialog.show();
    }
}