package com.grp.application.simulation;

import com.example.application.R;
import com.grp.application.Application;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class WeightSimulator {

    BufferedReader dataReader;

    private static WeightSimulator instance;

    private WeightSimulator() {
        dataReader = new BufferedReader(new InputStreamReader(Application.context.getResources().openRawResource(R.raw.weight_1)));
    }

    public static WeightSimulator getInstance() {
        if (instance == null) {
            instance = new WeightSimulator();
        }

        return instance;
    }

    public float readNextWeightData() throws IOException {
        String line = dataReader.readLine();
        String[] data = line.split(",");
        if (data.length > 1) {
            return Float.parseFloat(data[1]);
        }
        return readNextWeightData();
    }
}
