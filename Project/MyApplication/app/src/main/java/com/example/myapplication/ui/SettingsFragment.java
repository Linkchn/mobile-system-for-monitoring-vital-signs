package com.example.myapplication.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.Switches;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class SettingsFragment extends Fragment {

    public SettingsFragment() {}

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_settings, container, false);
        MainActivity mainActivity = (MainActivity) getActivity();
        SwitchMaterial msgOnNotWearDeviceSwitch = root.findViewById(R.id.switch_msg_not_wear_device);
        SwitchMaterial msgOnNotCaptureDataSwitch = root.findViewById(R.id.switch_msg_not_capture_data);
        SwitchMaterial msgOnReportGenerated = root.findViewById(R.id.switch_msg_report_generated);

        mainActivity.setSwitchView(msgOnNotWearDeviceSwitch, Switches.MSG_ON_NOT_WEAR_DEVICE);
        mainActivity.setSwitchView(msgOnNotCaptureDataSwitch, Switches.MSG_ON_NOT_CAPTURE_DATA);
        mainActivity.setSwitchView(msgOnReportGenerated, Switches.MSG_ON_REPORT_GENERATED);

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