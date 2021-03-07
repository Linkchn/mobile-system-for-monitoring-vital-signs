package com.grp.application.pages;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.grp.application.MainActivity;
import com.example.application.R;
import com.google.android.material.tabs.TabLayout;
import com.grp.application.database.Dao;
import com.grp.application.export.ShareView;
import com.grp.application.monitor.Monitor;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.Series;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

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
        graphView = (GraphView)root.findViewById(R.id.graph_view_hr);
        dao = new Dao(mainActivity.getApplicationContext());

        startPlot1();

        durationTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (durationTab.getTabAt(0).isSelected()) {
                    monitor.showToast("Daily Tab");
                    startPlot1();
                } else if (durationTab.getTabAt(1).isSelected()) {
                    monitor.showToast("Weekly Tab");
                    startPlot2();

                } else if (durationTab.getTabAt(2).isSelected()) {
                    monitor.showToast("Monthly Tab");
                    startPlot3();
                }
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        return root;
    }

    private void startPlot1(){
        graphView.removeAllSeries();
        LineGraphSeries<DataPoint> series= new LineGraphSeries<>(dailyData());
        series.setDrawDataPoints(true);
        graphView.addSeries(series);
        double max_x = 24.0;
        graphView.getViewport().setXAxisBoundsManual(true);
        graphView.getViewport().setMaxX(max_x);
        series.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPoint) {
                Toast.makeText(graphView.getContext(), "Your heart rate is: "+dataPoint.getY(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void startPlot2(){
        graphView.removeAllSeries();
        LineGraphSeries<DataPoint> series= new LineGraphSeries<>(weeklyData());
        series.setDrawDataPoints(true);
        graphView.addSeries(series);
        double max_x = 7.0;
        graphView.getViewport().setXAxisBoundsManual(true);
        graphView.getViewport().setMaxX(max_x);
        series.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPoint) {
                Toast.makeText(graphView.getContext(), "Your heart rate is: "+dataPoint.getY(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void startPlot3(){
        graphView.removeAllSeries();
        LineGraphSeries<DataPoint> series= new LineGraphSeries<>(monthlyData());
        series.setDrawDataPoints(true);
        graphView.addSeries(series);
        double max_x = 31.0;
        graphView.getViewport().setXAxisBoundsManual(true);
        graphView.getViewport().setMaxX(max_x);
        series.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPoint) {
                Toast.makeText(graphView.getContext(), "Your heart rate is: "+dataPoint.getY(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void createShareImage(View view) {
        ShareView shareView = new ShareView(mainActivity);
        shareView.setInfo("其他信息其他信息其他信息其他信息其他信息其他信息其他信息其他信息其他信息其他信息其他信息其他信息其他信息其他信息其他信息其他信息其他信息其他信息其他信息其他信息其他信息其他信息其他信息其他信息其他信息其他信息其他信息其他信息其他信息其他信息其他信息其他信息其他信息");
        final Bitmap image = shareView.createImage();
        final String path = saveImage(image);
        Log.e("xxx", path);

        if (image != null && !image.isRecycled()) {
            image.recycle();
        }
    }

    /**
     * 保存bitmap到本地
     *
     * @param bitmap
     * @return
     */
    private String saveImage(Bitmap bitmap) {

        File path = null;

        String fileName = "shareImage.png";

        File file = new File(path, fileName);

        if (file.exists()) {
            file.delete();
        }

        FileOutputStream fos = null;

        try {
            fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return file.getAbsolutePath();
    }
}