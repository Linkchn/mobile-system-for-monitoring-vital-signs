package com.grp.application.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.grp.application.Constants;
import com.grp.application.simulation.HrSimulator;

import java.util.Calendar;
import java.util.Date;

import polar.com.sdk.api.model.PolarHrData;

public class DatabaseSimulator {
    private static final String TAG = "Database simulation";
    Dao dao = null;
    public DatabaseSimulator(Context context){
        dao = new Dao(context);
    }

    public void insertTestData() {
        SQLiteDatabase db = dao.getDatabase();
        PolarHrData polarHrData;
        int hr;
        String insertSql;
        long timestamp = Calendar.getInstance().getTimeInMillis();
        Log.d(TAG, "timestamp"+timestamp);
        db.beginTransaction();
        try {
            for(int i=0; i<70000; i++){
                polarHrData = HrSimulator.getInstance().getNextHrData();
                hr = polarHrData.hr;
                insertSql = "insert into "+ Constants.HR_TABLE_DETAIL +"(timestamp,hr) values(?,?)";
                db.execSQL(insertSql, new Object[]{timestamp+i*1000,hr});
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }

        db.close();

        Log.d(TAG,"finish read in test data");

    }


    public void clearTodayData() {
        SQLiteDatabase db = dao.getDatabase();
        long startTime = TimeHelper.getDailyStartTime(new Date().getTime());
        long endTime = TimeHelper.getDailyEndTime(new Date().getTime());
        dao.delete(startTime,endTime,Constants.HR_TABLE_DETAIL);
        Log.d(TAG,"Clear data of today");
    }



}
