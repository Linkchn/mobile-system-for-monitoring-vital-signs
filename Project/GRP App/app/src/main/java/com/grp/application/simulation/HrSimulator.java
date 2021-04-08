package com.grp.application.simulation;

import com.grp.application.R;
import com.grp.application.Application;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;

import polar.com.sdk.api.model.PolarHrData;

/**
 * Class {@code HrSimulator} is the class to simulate hr device.
 *
 * @author UNNC GRP G19
 * @version 1.0
 */
public class   HrSimulator {

    BufferedReader dataReader;

    private static HrSimulator instance;

    /**
     * Private constructor.
     */
    private HrSimulator() {
        dataReader = new BufferedReader(new InputStreamReader(Application.context.getResources().openRawResource(R.raw.hr_long))); // Load heart rate date from csv file
    }

    /**
     * Get the unique instance of HrSimulator.
     * @return instance of HrSimulator
     */
    public static HrSimulator getInstance() {
        if (instance == null) {
            instance = new HrSimulator();
        }

        return instance;
    }

    /**
     * Get next PolarHrData
     * @return next PolarHrData
     * @throws IOException IOException
     */
    public PolarHrData getNextHrData() throws IOException {
        return new PolarHrData(readNextHrData(), new LinkedList<>(), false, false, false);
    }

    /**
     * Read next hr data from csv file
     * @return next hr data
     * @throws IOException IOException
     */
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
