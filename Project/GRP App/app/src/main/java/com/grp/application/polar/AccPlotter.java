package com.grp.application.polar;

import com.androidcommunications.polar.api.ble.model.gatt.client.BlePMDClient;
import com.androidplot.xy.SimpleXYSeries;

import java.util.Arrays;

import polar.com.sdk.api.model.PolarAccelerometerData;

public class AccPlotter {
    private static final String TAG = "AccPlotter";

    private AccData[] plotNumbers = new AccData[500];
    private int dataIndex;
    private PlotterListener listener;


    public AccPlotter() {

    }

    public void sendSingleSample(int x, int y, int z) {
        plotNumbers[dataIndex].x = x;
        plotNumbers[dataIndex].y = y;
        plotNumbers[dataIndex].z = z;
        if (dataIndex >= plotNumbers.length - 1) {
            dataIndex = 0;
        }
        if (dataIndex < plotNumbers.length - 1) {
            plotNumbers[dataIndex + 1] = null;
        }

        dataIndex++;
        if (listener != null) {
            listener.update();
        }
    }

    public void setListener(PlotterListener listener) {
        this.listener = listener;
    }

    public static class AccData {
        public int x;
        public int y;
        public int z;

        public AccData(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }
}
