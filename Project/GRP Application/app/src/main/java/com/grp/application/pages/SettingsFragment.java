package com.grp.application.pages;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.grp.application.MainActivity;
import com.example.application.R;
import com.grp.application.components.Devices;
import com.grp.application.components.Switches;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class SettingsFragment extends Fragment {

    public SettingsFragment() {}

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_settings, container, false);
        MainActivity mainActivity = (MainActivity) getActivity();
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
                mainActivity.toggleDevice(Devices.HEART_RATE_DEVICE);
                mainActivity.setDeviceView(symbolHrDevice,hrConnectButton,Devices.HEART_RATE_DEVICE);
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
}