package com.grp.application.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.grp.application.Constants;

/**
 * @author Hudie LIU
 */
public class Dao {
    private static final String TAG = "Dao";
    private final DatabaseHelper mHelper;
    public Dao(Context context) {
        // Create db
        mHelper = new DatabaseHelper(context);
    }

    public void insert(long timestamp, int heartRate, String tableName) {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("timestamp", timestamp);
        values.put("hr", heartRate);

        db.insert(tableName, null, values);
        db.close();
    }

    public void delete(long startTime, long endTime, String tableName) {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        db.delete(tableName, "timestamp>? AND timesatmp<?", new String[]{String.valueOf(startTime), String.valueOf(endTime)});
        db.close();
    }

    public SQLiteDatabase getDatabase() {
        return mHelper.getWritableDatabase();
    }


    public void delete() {

    }

    public void update() {

    }

    public void query() {

    }
}
