package com.grp.application.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.grp.application.Constants;
import com.grp.application.simulation.HrSimulator;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import polar.com.sdk.api.model.PolarHrData;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHelper";
    private SQLiteDatabase db;
    /**
     * @param context
     */
    public DatabaseHelper(@Nullable Context context) {
        super(context, Constants.DATABASE_NAME, null, Constants.VERSION_CODE);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // call back when create, only for first time
        Log.d(TAG,"Create database");
        String sql1 = "create table "+Constants.HR_TABLE_NAME_DAY+"(timestamp long, hr integer)";
        String sql2 = "create table "+Constants.HR_TABLE_NAME_WEEK+"(timestamp long, hr integer)";
        String sql3 = "create table "+Constants.HR_TABLE_NAME_MONTH+"(timestamp long, hr integer)";

        String sql4 = "create table "+Constants.WEIGHT_TABLE_NAME+"(timestamp long, weight long)";
        db.execSQL(sql1);
        db.execSQL(sql2);
        db.execSQL(sql3);
        db.execSQL(sql4);

        this.db = db;

//        try {
//            readInHrData();
//            generateWeekData();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // call back when update
        Log.d(TAG, "Update database");
    }

    public void readInHrData(SQLiteDatabase db) throws IOException {
        PolarHrData polarHrData;
        int hr;
        String insertSql;
        long timestamp = Calendar.getInstance().getTimeInMillis();
//        db.execSQL(insertSql, new Object[]{100,hr});

        for(int i=0; i<70000; i++){
            polarHrData = HrSimulator.getInstance().getNextHrData();
            hr = polarHrData.hr;
            insertSql = "insert into "+Constants.HR_TABLE_NAME_DAY+"(timestamp,hr) values(?,?)";
            db.execSQL(insertSql, new Object[]{timestamp+i*1000,hr});
        }

    }

    public void generateWeekData(SQLiteDatabase db) {
        long startTime = getDailyStartTime(new Date().getTime());
        long endTime = getDailyEndTime(new Date().getTime());
        String sql = "SELECT timestamp,hr FROM "+Constants.HR_TABLE_NAME_DAY+" WHERE timestamp>"+startTime+" AND timestamp<"+endTime;
        Cursor cursor = db.rawQuery(sql, null);
        long totalHR = 0;
        int totalCount = 0;
        if(cursor.moveToFirst()){
            do{
                totalHR += cursor.getInt(cursor.getColumnIndex("hr"));
                totalCount++;
            }while (cursor.moveToNext());
        }
        Log.d(TAG,"totalCount"+totalCount+"endTime"+endTime);
        int todayHR = (int) totalHR/totalCount;
        String weekDataSql = "insert into "+Constants.HR_TABLE_NAME_WEEK+"(timestamp,hr) values(?,?)";
        long timestamp = Calendar.getInstance().getTimeInMillis();;
        db.execSQL(weekDataSql, new Object[]{timestamp, todayHR});
    }

    public static Long getDailyStartTime(Long timeStamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeStamp);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    /**
     * 获取指定某一天的结束时间戳
     *
     * @param timeStamp 毫秒级时间戳
     * @return
     */
    public static Long getDailyEndTime(Long timeStamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeStamp);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTimeInMillis();
    }
}
