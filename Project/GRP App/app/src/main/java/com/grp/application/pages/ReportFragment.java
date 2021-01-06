package com.grp.application.pages;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.grp.application.components.DurationTab;
import com.grp.application.MainActivity;
import com.example.application.R;
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