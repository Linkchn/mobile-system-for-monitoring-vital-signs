package com.grp.application.pages;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.application.R;
import com.google.android.material.tabs.TabLayout;
import com.grp.application.GRPchart.EchartOptionUtil;
import com.grp.application.GRPchart.EchartView;
import com.grp.application.MainActivity;
import com.grp.application.database.Dao;
import com.grp.application.monitor.Monitor;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * {@code ReportFragment} is class to maintain UI elements and functions of report page.
 *
 * @author UNNC GRP G19
 * @version 1.0
 */
public class ReportFragment extends Fragment {
    private Monitor monitor;
    private MainActivity mainActivity;
    private Dao dao;
    private EchartView lineChart;
    private EchartView weightChart;
    private TextView low;
    private TextView high;
    private TextView average;

    private TabLayout durationTab;

    public ReportFragment() {}

    private Object[] dailyData(){
        Number[] number = dao.getDailyData();
        numberToDouble(number);
        return number;
    }

    private Object[] weeklyData(){
        Number[] number = dao.getWeeklyData();
        numberToDouble(number);
        return number;
    }

    private Object[] monthlyData(){
        Number[] number = dao.getMonthlyData();
        numberToDouble(number);
        return number;
    }

    private static void numberToDouble(Number[] number){
        for(int i=0;i<number.length;i++){
            number[i] = number[i].doubleValue();
            BigDecimal bd = BigDecimal.valueOf((Double) number[i]).setScale(2, RoundingMode.HALF_UP);
            number[i] = bd.doubleValue();
        }
    }

    private static double castToDouble(long number){
        BigDecimal bd = BigDecimal.valueOf(number).setScale(2, RoundingMode.HALF_UP);
        double num = bd.doubleValue();
        return num;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_report, container, false);
        monitor = Monitor.getInstance();
        mainActivity = (MainActivity) getActivity();
        durationTab = root.findViewById(R.id.tab_duration);
        dao = new Dao(mainActivity.getApplicationContext());
        lineChart = root.findViewById(R.id.lineChart);
        weightChart = root.findViewById(R.id.weightChart);
        low = root.findViewById(R.id.low);
        high = root.findViewById(R.id.high);
        average = root.findViewById(R.id.average);

        lineChart.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                //最好在h5页面加载完毕后再加载数据，防止html的标签还未加载完成，不能正常显示
                refreshDailyChart();
                refreshDailyRate(dao.getDailyData());
                weightChart.setVisibility(View.GONE);
            }
        });

        durationTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (durationTab.getTabAt(0).isSelected()) {
                    monitor.showToast("Daily Tab");
                    refreshDailyChart();
                    refreshDailyRate(dao.getDailyData());
                    weightChart.setVisibility(View.GONE);
                } else if (durationTab.getTabAt(1).isSelected()) {
                    monitor.showToast("Weekly Tab");
                    weightChart.setVisibility(View.VISIBLE);
                    refreshWeeklyChart();
                    refreshWeeklyWeight();
                    refreshWeeklyRate(dao.getWeeklyData());
                } else if (durationTab.getTabAt(2).isSelected()) {
                    monitor.showToast("Monthly Tab");
                    refreshMonthlyChart();
                    weightChart.setVisibility(View.VISIBLE);
                    refreshMonthlyWeight();
                    refreshMonthlyRate(dao.getMonthlyData());
                }
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        return root;
    }

    private void refreshDailyChart(){
        Object[] x = new Object[]{
                "00:00", "1:00", "2:00", "3:00", "4:00", "5:00", "6:00","7:00","8:00",
                "9:00","10:00","11:00","12:00","13:00","14:00","15:00","16:00","17:00",
                "18:00","19:00","20:00","21:00","22:00","23:00"
        };
        Object[] y = dailyData();
        lineChart.refreshEchartsWithOption(EchartOptionUtil.getLineChartOptions(x, y, "Heart rate"));
    }

    private void refreshWeeklyChart(){
        Object[] x = new Object[]{
                "Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"
        };
        Object[] y = weeklyData();
        lineChart.refreshEchartsWithOption(EchartOptionUtil.getLineChartOptions(x, y, "Heart rate"));
    }

    private void refreshMonthlyChart(){
        Object[] x = new Object[]{
                "Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"
        };
        Object[] y = new Object[]{
                820, 932, 901, 934, 1290, 1330, 1320
        };
        lineChart.refreshEchartsWithOption(EchartOptionUtil.getLineChartOptions(x, y, "Heart rate"));
    }

    private void refreshWeeklyWeight(){
        Object[] x = new Object[]{
                "00:00", "1:00", "2:00", "3:00", "4:00", "5:00", "6:00","7:00","8:00",
                "9:00","10:00","11:00","12:00","13:00","14:00","15:00","16:00","17:00",
                "18:00","19:00","20:00","21:00","22:00","23:00"
        };
        Object[] y = dailyData();
        weightChart.refreshEchartsWithOption(EchartOptionUtil.getLineChartOptions(x, y, "Weight"));
    }

    private void refreshMonthlyWeight(){
        Object[] x = new Object[]{
                "00:00", "1:00", "2:00", "3:00", "4:00", "5:00", "6:00","7:00","8:00",
                "9:00","10:00","11:00","12:00","13:00","14:00","15:00","16:00","17:00",
                "18:00","19:00","20:00","21:00","22:00","23:00"
        };
        Object[] y = dailyData();
        weightChart.refreshEchartsWithOption(EchartOptionUtil.getLineChartOptions(x, y, "Weight"));
    }


    private double getAverageRate(Number[] number){
        numberToDouble(number);
        double total = 0;
        for(int i=1;i<number.length;i++){
            total += (double)number[i];
        }
        double result = total/(number.length);
        BigDecimal bd = new BigDecimal(result).setScale(2, RoundingMode.HALF_UP);
        result = bd.doubleValue();
        return result;
    }

    private void refreshDailyRate(Number[] number){
        low.setText("The highest heart rate: "+ castToDouble(dao.getMaxHrDay().getHeartRate())+"  The time is: "+dao.getMaxHrDay().getTimestamp());
        high.setText("The lowest heart rate: " + castToDouble(dao.getMinHrDay().getHeartRate()) +"  The time is: "+dao.getMinHrDay().getTimestamp());
        double averageRate = getAverageRate(number);
        average.setText("The Average heart rate: " + averageRate);
    }

    private void refreshMonthlyRate(Number[] number){
        low.setText("The highest heart rate: "+ castToDouble(dao.getMaxHrMonth().getHeartRate())+"  The time is: "+dao.getMaxHrMonth().getTimestamp());
        high.setText("The lowest heart rate: " + castToDouble(dao.getMinHrMonth().getHeartRate()) +"  The time is: "+dao.getMinHrMonth().getTimestamp());
        double averageRate = getAverageRate(number);
        average.setText("The Average heart rate: " + averageRate);
    }

    private void refreshWeeklyRate(Number[] number){
        low.setText("The highest heart rate: "+ castToDouble(dao.getMaxHrWeek().getHeartRate())+"  The time is: "+dao.getMaxHrWeek().getTimestamp());
        high.setText("The lowest heart rate: " + castToDouble(dao.getMinHrWeek().getHeartRate()) +"  The time is: "+dao.getMinHrWeek().getTimestamp());
        double averageRate = getAverageRate(number);
        average.setText("The Average heart rate: " + averageRate);
    }
}

