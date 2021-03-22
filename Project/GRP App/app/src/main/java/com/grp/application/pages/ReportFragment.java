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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

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
    private TextView Weight;
    private static int[] newZeroArray;

    private TabLayout durationTab;

    public ReportFragment() {}

    private static Object[] doubleToObject(double[] array){
        int len = array.length;
        Number[] newArray = new Number[len];
        for(int i=0;i<len;i++){
            Number num = array[i];
            newArray[i] = num;
        }
        return newArray;
    }

    private double[] numberToDouble(Number[] number){
        double[] array = new double[number.length];
        for(int i=0;i<number.length;i++){
            array[i] = number[i].doubleValue();
            BigDecimal bd = BigDecimal.valueOf(array[i]).setScale(2, RoundingMode.HALF_UP);
            array[i] = bd.doubleValue();
        }
        return array;
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
        Weight = root.findViewById(R.id.Weight);

        lineChart.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                //最好在h5页面加载完毕后再加载数据，防止html的标签还未加载完成，不能正常显示
                refreshDailyChart();
                refreshDailyWeight();
                refreshDailyRate(dao.getDailyData());
                weightChart.setVisibility(View.GONE);
            }
        });

        durationTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (durationTab.getTabAt(0).isSelected()) {
                    monitor.showToast("Daily Tab");
                    refreshDailyWeight();
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
        Object[] array = new Object[]{
                "00:00", "1:00", "2:00", "3:00", "4:00", "5:00", "6:00","7:00","8:00",
                "9:00","10:00","11:00","12:00","13:00","14:00","15:00","16:00","17:00",
                "18:00","19:00","20:00","21:00","22:00","23:00"
        };
        Object[] y = doubleToObject(dealZero(numberToDouble(dao.getDailyData())));
        Object[] x = removeElement(array);
        lineChart.refreshEchartsWithOption(EchartOptionUtil.getLineChartOptions(x, y, "Heart rate"));
    }

    private void refreshWeeklyChart(){
        Object[] y = doubleToObject(dealZero(numberToDouble(dao.getWeeklyData())));
        Object[] x = removeElement(getWeek());
        lineChart.refreshEchartsWithOption(EchartOptionUtil.getLineChartOptions(x, y, "Heart rate"));
    }

    private void refreshMonthlyChart(){
        Object[] y = doubleToObject(dealZero(numberToDouble(dao.getMonthlyData())));
        Object[] x = removeElement(getDate());
        lineChart.refreshEchartsWithOption(EchartOptionUtil.getLineChartOptions(x, y, "Heart rate"));
    }

    private void refreshWeeklyWeight(){
        Object[] y  = doubleToObject(dealZero(numberToDouble(dao.getWeeklyWeight())));
        Object[] x = removeElement(getWeek());
        weightChart.refreshEchartsWithOption(EchartOptionUtil.getLineChartOptions(x, y, "Weight"));
    }

    private void refreshMonthlyWeight(){
        Object[] y = doubleToObject(dealZero(numberToDouble(dao.getMonthlyWeight())));
        Object[] x = removeElement(getDate());
        weightChart.refreshEchartsWithOption(EchartOptionUtil.getLineChartOptions(x, y, "Weight"));
    }

    private void refreshDailyWeight(){
        double[] num = numberToDouble(dao.getDailyData());
        if(num[0] != 0){
            double number = num[0];
            BigDecimal bd = BigDecimal.valueOf(number).setScale(2, RoundingMode.HALF_UP);
            number = bd.doubleValue();
            Weight.setText("Weight: " + number + "Kg");
        }
    }

    private double getAverageRate(Number[] number){
        double[] num = numberToDouble(number);
        double total = 0;
        int len =num.length;
        for(int i=1;i<num.length;i++){
            if(num[i] == 0){
                len--;
                continue;
            }
            total += (double)num[i];
        }
        double result = total/(len);
        BigDecimal bd = new BigDecimal(result).setScale(2, RoundingMode.HALF_UP);
        result = bd.doubleValue();
        return result;
    }

    private void refreshDailyRate(Number[] number){
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        low.setText("The highest heart rate: "+ castToDouble(dao.getMaxHrDay().getHeartRate())+"\nThe time is: "+formatter.format(dao.getMaxHrDay().getTimestamp()));
        high.setText("The lowest heart rate: " + castToDouble(dao.getMinHrDay().getHeartRate()) +"\nThe time is: "+formatter.format(dao.getMinHrDay().getTimestamp()));
        double averageRate = getAverageRate(number);
        average.setText("The Average heart rate: " + averageRate);
    }

    private void refreshMonthlyRate(Number[] number){
        SimpleDateFormat formatter = new SimpleDateFormat("YY/MM/dd");
        low.setText("The highest heart rate: "+ castToDouble(dao.getMaxHrMonth().getHeartRate())+"\nThe date is: "+formatter.format(dao.getMaxHrMonth().getTimestamp()));
        high.setText("The lowest heart rate: " + castToDouble(dao.getMinHrMonth().getHeartRate()) +"\nThe date is: "+formatter.format(dao.getMinHrMonth().getTimestamp()));
        double averageRate = getAverageRate(number);
        average.setText("The Average heart rate: " + averageRate);
    }

    private void refreshWeeklyRate(Number[] number){
        SimpleDateFormat formatter = new SimpleDateFormat("EEEE");
        low.setText("The highest heart rate: "+ castToDouble(dao.getMaxHrWeek().getHeartRate())+"\nThe day is: "+formatter.format(dao.getMaxHrWeek().getTimestamp()));
        high.setText("The lowest heart rate: " + castToDouble(dao.getMinHrWeek().getHeartRate()) +"\nThe day is: "+formatter.format(dao.getMinHrWeek().getTimestamp()));
        double averageRate = getAverageRate(number);
        average.setText("The Average heart rate: " + averageRate);
    }

    private static Object[] getWeek(){
        Date today = new Date();
        Calendar cal = new GregorianCalendar();
        cal.setTime(today);
        DateFormat formatter = new SimpleDateFormat("EE");
        Object[] storeWeek = new Object[7];
        for(int i=0;i<7;i++){
            Date date = cal.getTime();
            storeWeek[6-i] = formatter.format(date);
            cal.add(Calendar.DAY_OF_MONTH, -1);
        }
        return storeWeek;
    }

    private static Object[] getDate(){
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd");
        Date today = new Date();
        Calendar cal = new GregorianCalendar();
        cal.setTime(today);
        Object[] storeDate = new Object[30];
        for(int i=0;i<30;i++){
            Date date = cal.getTime();
            storeDate[29-i]=formatter.format(date);
            cal.add(Calendar.DAY_OF_MONTH, -1);
        }
        return storeDate;
    }

    private double[] dealZero(double[] array){
        int len = 0;
        int len_zero = 0;
        for (int i=0; i<array.length; i++){
            if (array[i] != 0){
                len++;
            }else {
                len_zero++;
            }
        }
        double [] newArray = new double[len];
        newZeroArray = new int[len_zero];
        int Index= 0;
        for (int i=0, j=0; i<array.length; i++){
            if (array[i] != 0) {
                newArray[j] = array[i];
                j++;
            }else{
                newZeroArray[Index] = i;
                Index++;
            }
        }
        return newArray;
    }

    private static Object[] removeElement(Object[] array){
        int len = array.length;
        int length = len - newZeroArray.length;
        Object[] newArray;
        if(length<0) {
            newArray = new Object[]{};
        }
        if(newZeroArray.length == 0){
            newArray = array;
        }else {
            newArray = new Object[length];
            int j=0;
            for (int i = 0, k = 0; i<array.length; i++){
                if (i == newZeroArray[j]) {
                    if(j<newZeroArray.length-1){
                        j++;
                    }
                    continue;
                }
                newArray[k++] = array[i];
            }
        }
        return newArray;
    }
}

