package com.grp.application.export;

import android.app.backup.FileBackupHelper;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class HrExport {

    BufferedWriter bufferedWriter;
    private String Path = System.getProperty("user.dir");

    private static HrExport instance;

    public static HrExport getInstance() {
        if(instance == null){
            instance = new HrExport();
        }

        return instance;
    }

    // convert the String "weightData" to .csv type (split by ",")
    public String[] invert(String weightData){
        String inverted[] = new String[0];
        String temp[] = new String[0];
        while(weightData != null){
            temp = weightData.split(",");
            inverted = (String[]) concat(inverted, temp);
        }

        return inverted;
    }

    public void export(String weightData) throws IOException {
        int time = 0;
        String[] data = invert(weightData);

        File csv = new File(Path + "\\export\\WeightSimulation.csv");
        bufferedWriter = new BufferedWriter(new FileWriter(csv, true));

        for(int i = 0; i < data.length; i++){
            String writeText = time + "," + data[i] + "," + "\n";
            bufferedWriter.write(writeText);
            bufferedWriter.newLine();
            time++;
        }
    }

    // combine 2 string[] together
    static String[] concat(String[] a, String[] b){
        String[] c = new String[a.length + b.length];
        System.arraycopy(a, 0, c, 0,a.length);
        System.arraycopy(b, 0, c, a.length, b.length);
        return c;
    }
}
