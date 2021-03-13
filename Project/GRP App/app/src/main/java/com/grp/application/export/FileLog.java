package com.grp.application.export;

import android.os.Environment;
import android.widget.Toast;

import com.grp.application.Application;

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
     * @param heart_beat_per_minute
     * @param message  The content of message
     * @param fileName name of the file to export
     */
    public static void saveLog(String heart_beat_per_minute, String message, String fileName) throws IOException {
        String path = Environment.getExternalStorageDirectory() + "/HR";
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
            fw.write(message);
            fw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
