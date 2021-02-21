package com.grp.application.polar;

import android.graphics.Color;

import com.androidplot.util.PixelUtils;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYSeriesFormatter;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import polar.com.sdk.api.model.PolarHrData;

/**
 * Class {@code TimePlotter} is the class to plot HR data.
 *
 * @author UNNC GRP G19
 * @version 1.0
 */
public class TimePlotter {
    private static final String TAG = "TimePlotter";
    private static final int NVALS = 60;  // 1 min
    private PlotterListener listener;

    private XYSeriesFormatter hrFormatter;
    private SimpleXYSeries hrSeries;
    private Double[] xHrVals = new Double[NVALS];
    private Double[] yHrVals = new Double[NVALS];

    public TimePlotter() {
        Date now = new Date();
        double endTime = now.getTime();
        double startTime = endTime - NVALS * 1000;
        double delta = (endTime - startTime) / (NVALS - 1);

        // Specify initial values to keep it from auto sizing
        for (int i = 0; i < NVALS; i++) {
            xHrVals[i] = startTime + i * delta;
            yHrVals[i] = 60d;
        }

        hrFormatter = new LineAndPointFormatter(Color.BLUE, null, null, null);
        hrFormatter.setLegendIconEnabled(false);
        hrSeries = new SimpleXYSeries(Arrays.asList(xHrVals), Arrays.asList(yHrVals), "HR");
    }

    public SimpleXYSeries getHrSeries() {
        return hrSeries;
    }


    public XYSeriesFormatter getHrFormatter() {
        return hrFormatter;
    }

    /**
     * Implements a strip chart by moving series data backwards and adding
     * new data at the end.
     *
     * @param polarHrData The HR data that came in.
     */
    public void addValues(PolarHrData polarHrData) {
        Date now = new Date();
        long time = now.getTime();
        for (int i = 0; i < NVALS - 1; i++) {
            xHrVals[i] = xHrVals[i + 1];
            yHrVals[i] = yHrVals[i + 1];
            hrSeries.setXY(xHrVals[i], yHrVals[i], i);
        }
        xHrVals[NVALS - 1] = (double) time;
        yHrVals[NVALS - 1] = (double) polarHrData.hr;
        hrSeries.setXY(xHrVals[NVALS - 1], yHrVals[NVALS - 1], NVALS - 1);

        if (listener != null) {
            listener.update();
        }
    }

    public void setListener(PlotterListener listener) {
        this.listener = listener;
    }
}
