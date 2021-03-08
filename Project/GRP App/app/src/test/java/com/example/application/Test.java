package com.example.application;
import android.os.Environment;

import com.grp.application.export.FileLog;

import java.io.File;
import java.io.IOException;

public class Test {
    @org.junit.Test
    private void exportTest() throws IOException {
        File file = new File(Environment.getExternalStorageDirectory() + "/HR");
    }
}
