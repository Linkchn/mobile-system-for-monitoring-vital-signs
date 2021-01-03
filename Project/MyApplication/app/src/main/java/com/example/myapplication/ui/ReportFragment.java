package com.example.myapplication.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication.DurationTab;
import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.google.android.material.tabs.TabLayout;

public class ReportFragment extends Fragment {

    public ReportFragment() {}

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_report, container, false);
        MainActivity mainActivity = (MainActivity) getActivity();
        TabLayout durationTab = root.findViewById(R.id.tab_duration);

        mainActivity.setTabView(durationTab);
        durationTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (durationTab.getTabAt(DurationTab.DAILY.getIndex()).isSelected()) {
                    mainActivity.showToast("Daily Tab");
                    mainActivity.setDurationTabSelected(DurationTab.DAILY);
                } else if (durationTab.getTabAt(DurationTab.WEEKLY.getIndex()).isSelected()) {
                    mainActivity.showToast("Weekly Tab");
                    mainActivity.setDurationTabSelected(DurationTab.WEEKLY);
                } else if (durationTab.getTabAt(DurationTab.MONTHLY.getIndex()).isSelected()) {
                    mainActivity.showToast("Monthly Tab");
                    mainActivity.setDurationTabSelected(DurationTab.MONTHLY);
                }
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        return root;
    }
}