package com.grp.application.simulation;

import com.example.application.R;
import com.grp.application.Application;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Class {@code WeightSimulator} is the class to simulate scale device.
 *
 * @author UNNC GRP G19
 * @version 1.0
 */
public class WeightSimulator {

    BufferedReader dataReader;

    private static WeightSimulator instance;

    /**
     * Private constructor.
     */
    private WeightSimulator() {
        dataReader = new BufferedReader(new InputStreamReader(Application.context.getResources().openRawResource(R.raw.weight_1))); // Load weight date from csv file
    }

    /**
     * Get the unique instance of WeightSimulator.
     * @return instance of WeightSimulator
     */
    public static WeightSimulator getInstance() {
        if (instance == null) {
            instance = new WeightSimulator();
        }

        return instance;
    }

    /**
     * Read next weight data from csv file
     * @return next weight data
     * @throws IOException IOException
     */
    public float readNextWeightData() throws IOException {
        String line = dataReader.readLine();
        String[] data = line.split(",");
        if (data.length > 1) {
            return Float.parseFloat(data[1]);
        }
        return readNextWeightData();
    }
}
