package com.grp.application;

import android.os.Environment;

import com.grp.application.export.FileLog;

import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class FileLogTest {
    /**
     * Test for class FileLog
     * @param
     * @return
     * @throw
     */

    FileLog fileLog;

    private String message = "This is a message";
    private String fileName = "export";


    @Test
    public void fileExist() throws IOException {
        File directory = new File(Environment.getExternalStorageDirectory() + "/HR");
        File file = new File(Environment.getExternalStorageDirectory() + "/HR/" + fileName);
        assertEquals(true, directory.isDirectory());
        assertEquals(true,file.exists());
    }

}
