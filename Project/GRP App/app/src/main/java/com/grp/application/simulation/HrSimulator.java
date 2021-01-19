package com.grp.application.simulation;

import com.example.application.R;
import com.grp.application.Application;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;

import polar.com.sdk.api.model.PolarHrData;

public class HrSimulator {

    BufferedReader dataReader;

    private static HrSimulator instance;

    private HrSimulator() {
        dataReader = new BufferedReader(new InputStreamReader(Application.context.getResources().openRawResource(R.raw.hr_long)));
    }

    public static HrSimulator getInstance() {
        if (instance == null) {
            instance = new HrSimulator();
        }

        return instance;
    }

    public PolarHrData getNextHrData() throws IOException {
        return new PolarHrData(readNextHrData(), new LinkedList<>(), false, false, false);
    }

    private int readNextHrData() throws IOException {
        String line = dataReader.readLine();
        String[] data = line.split(",");
        if (data.length > 1) {
            int dotIndex = data[1].indexOf('.');
            if (dotIndex == -1) {
                return Integer.parseInt(data[1]);
            }
            return Integer.parseInt(data[1].substring(0, dotIndex));
        }
        return readNextHrData();
    }
}
