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

public class HomeFragment extends Fragment {

    public HomeFragment() {}

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);
        MainActivity mainActivity = (MainActivity) getActivity();
        SwitchMaterial startCaptureDataSwitch = root.findViewById(R.id.startCaptureDataSwitch);

        mainActivity.setSwitchView(startCaptureDataSwitch, Switches.START_CAPTURE_DATA);
        startCaptureDataSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                mainActivity.showToast("Start Capture Data");
            } else {
                mainActivity.showToast("Stop Capture Data");
            }
            mainActivity.toggleSwitch(Switches.START_CAPTURE_DATA);
        });

        return root;
    }
}