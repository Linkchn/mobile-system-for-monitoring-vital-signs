package com.grp.application.export;

import android.content.ContextWrapper;
import android.os.Environment;
import android.widget.Toast;

import com.grp.application.Application;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FileLog{

    /**
     * 保存日志到本地存储根目录下
     * @param message      保存的信息
     * @param fileName     保存的文件名称
     * @param messageTitle 保存的信息标题
     */
    public static void saveLog(String messageTitle,String message, String fileName) throws IOException {
        String path = Environment.getExternalStorageDirectory() + "/HR";
        File files = new File(path);
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-M-dd HH:mm:ss,EE", Locale.CHINA);
        String formatDate = dateFormat.format(date);
        
        if (!files.exists()) {
            files.mkdirs();
            Toast.makeText(Application.context, "no Directory", Toast.LENGTH_LONG).show();
        }
        if (files.exists()) {
            //        File saveFile = new File(sdCardDir, "aaaa.txt");
//        saveFile.createNewFile();
//        FileOutputStream outStream = new FileOutputStream(saveFile);
//        outStream.write(message.getBytes());
//        outStream.close();
            
            try {

                FileWriter fw = new FileWriter(path + File.separator
                        + fileName + ".csv");
                System.out.println("*************************************************"+message);
                fw.write(message);
                fw.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
