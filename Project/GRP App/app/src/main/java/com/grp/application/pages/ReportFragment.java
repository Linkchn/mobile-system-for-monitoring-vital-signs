package com.grp.application.pages;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.grp.application.GRPNotification.GRPNotification;
import com.grp.application.MainActivity;
import com.example.application.R;
import com.google.android.material.tabs.TabLayout;
import com.grp.application.database.Dao;
import com.grp.application.monitor.Monitor;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

/**
 * {@code ReportFragment} is class to maintain UI elements and functions of report page.
 *
 * @author UNNC GRP G19
 * @version 1.0
 */
public class ReportFragment extends Fragment {
    private Monitor monitor;
    private MainActivity mainActivity;
    private final int hours = 24;
    private final int weekDays = 7;
    private final int monthDays = 31;
    private GraphView graphView;
    private GraphView graphView2;
    private GraphView graphView3;
    private Dao dao;

    private TabLayout durationTab;

    public ReportFragment() {}

    private DataPoint[] data(Number[] number, int num){
        DataPoint[] values = new DataPoint[num];     //creating an object of type DataPoint[] of size 'n'
        for(int i=0;i<num;i++){
            DataPoint v = new DataPoint(i, number[i].doubleValue());
            values[i] = v;
        }
        return values;
    }

    private DataPoint[] dailyData(){
        Number[] number = dao.getDailyData();
        DataPoint[] values;
        values = data(number, hours);
        return values;
    }

    private DataPoint[] weeklyData(){
        Number[] number = dao.getWeeklyData();
        DataPoint[] values;
        values = data(number, weekDays);
        return values;
    }

    private DataPoint[] monthlyData(){
        Number[] number = dao.getMonthlyData();
        DataPoint[] values;
        values = data(number, monthDays);
        return values;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_report, container, false);
        monitor = Monitor.getInstance();
        mainActivity = (MainActivity) getActivity();
        durationTab = root.findViewById(R.id.tab_duration);
        graphView = (GraphView)root.findViewById(R.id.dailyGraphView);
        graphView2 = (GraphView)root.findViewById(R.id.weeklyGraphView);
        graphView3 = (GraphView)root.findViewById(R.id.monthlyGraphView);
        dao = new Dao(mainActivity.getApplicationContext());

        durationTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (durationTab.getTabAt(0).isSelected()) {
                    monitor.showToast("Daily Tab");
                } else if (durationTab.getTabAt(1).isSelected()) {
                    monitor.showToast("Weekly Tab");
                } else if (durationTab.getTabAt(2).isSelected()) {
                    monitor.showToast("Monthly Tab");
                }
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        startPlot1();
        startPlot2();
        startPlot3();
        return root;
    }

    private void startPlot1(){
        LineGraphSeries<DataPoint> series= new LineGraphSeries<>(dailyData());
        series.setDrawDataPoints(true);
        graphView.addSeries(series);
        double max_x = 24.0;
        graphView.getViewport().setXAxisBoundsManual(true);
        graphView.getViewport().setMaxX(max_x);
    }

    private void startPlot2(){
        LineGraphSeries<DataPoint> series= new LineGraphSeries<>(weeklyData());
        series.setDrawDataPoints(true);
        graphView2.addSeries(series);
        double max_x = 7.0;
        graphView2.getViewport().setXAxisBoundsManual(true);
        graphView2.getViewport().setMaxX(max_x);
    }

    private void startPlot3(){
        LineGraphSeries<DataPoint> series= new LineGraphSeries<>(monthlyData());
        series.setDrawDataPoints(true);
        graphView3.addSeries(series);
        double max_x = 31.0;
        graphView3.getViewport().setXAxisBoundsManual(true);
        graphView3.getViewport().setMaxX(max_x);
    }

}