package com.grp.application.export;

import android.os.Environment;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FileLog {


    /**
     * Save
     *
     * @param message  The content of message
     * @param fileName name of the file to export
     */
    public static void saveLog(String message, String fileName, String type) throws IOException {
        String path = Environment.getExternalStorageDirectory() + File.separator + type;
        File files = new File(path);
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-M-dd HH:mm:ss,EE", Locale.getDefault());
        String formatDate = dateFormat.format(date) + "\n";

        if (!files.exists()) {
            files.mkdirs();
        }
        try {

            FileWriter fw = new FileWriter(path + File.separator
                    + fileName + ".csv");
            fw.write(formatDate);
            if (type == "VitalSigns/HR" || type == "VitalSigns/Exported") {
                fw.write("timestamp,bpm\n");
            } else if (type == "VitalSigns/ECG") {
                fw.write("timestamp,uV\n");
            } else if (type == "VitalSigns/ACC") {
                fw.write("timestamp,x,y,z\n");
            }
            fw.write(message);
            fw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
