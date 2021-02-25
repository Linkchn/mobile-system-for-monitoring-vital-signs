package com.grp.application.export;

import android.app.backup.FileBackupHelper;
import android.os.Environment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

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
    public String[] invert(String hrData){
        String inverted[] = new String[0];
        String temp[] = new String[0];
        while(hrData != null){
            temp = hrData.split(",");
            inverted = (String[]) concat(inverted, temp);
        }

        return inverted;
    }

    public void export(String weightData, Date recordTime) throws IOException {
        int time = 0;
        String[] data = invert(weightData);

        String filePath = Environment.getExternalStorageDirectory().getPath();
        String file = filePath + "/" + recordTime.toString() + ".csv";
        File csv = new File(file);
        bufferedWriter = new BufferedWriter(new FileWriter(csv, true));

        for(int i = 0; i < data.length; i++){
            String writeText = time + "," + data[i] + "," + "\n";
            bufferedWriter.write(writeText);
            bufferedWriter.newLine();
            time++;
        }
        bufferedWriter.close();
    }

    // combine 2 string[] together
    static String[] concat(String[] a, String[] b){
        String[] c = new String[a.length + b.length];
        System.arraycopy(a, 0, c, 0,a.length);
        System.arraycopy(b, 0, c, a.length, b.length);
        return c;
    }
}
