package com.grp.application.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

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
        db.delete(tableName, "timestamp>? AND timestamp<?", new String[]{String.valueOf(startTime), String.valueOf(endTime)});
        db.close();
    }

    public Cursor query(long startTime, long endTime, String tableName) {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        String sql = "SELECT timestamp,hr FROM "+tableName+" WHERE timestamp>"+startTime+" AND timestamp<"+endTime;
        Cursor cursor = db.rawQuery(sql, null);
        db.close();
        return cursor;
    }

    public Number[] getDailyData() {
        Number[] hrList = new Number[24];
        long startTime = timeHelper.getDailyStartTime(System.currentTimeMillis());
        SQLiteDatabase db = mHelper.getWritableDatabase();
        for(int i=0; i<24; i++){
            long hourStartTime = startTime+i*3600000;
            long hourEndTime = startTime+(i+1)*3600000;
            String sql = "SELECT timestamp,hr FROM "+Constants.HR_TABLE_NAME_DAY+" WHERE timestamp>"+hourStartTime+" AND timestamp<"+hourEndTime;
            Cursor cursor = db.rawQuery(sql, null);
//            Cursor cursor = query(hourStartTime,hourEndTime,Constants.HR_TABLE_NAME_DAY);
            int totalHR = 0;
            int totalCount = 0;
            if(cursor.moveToFirst()){
                do{
                    totalHR += cursor.getInt(cursor.getColumnIndex("hr"));
                    totalCount++;
                }while (cursor.moveToNext());
            }
            if(totalCount>0){
                hrList[i] = (float) totalHR/totalCount;
            }else {
                hrList[i] = 0;
            }
//            Log.d(TAG, i+":"+String.valueOf(hrList[i]));
        }

        return hrList;
    }

    public Number[] getWeeklyData() {
        Number[] hrList = new Number[7];
        long startTime = timeHelper.getWeekStartTime();
        SQLiteDatabase db = mHelper.getWritableDatabase();
        for(int i=0; i<7; i++){
            long dayStartTime = startTime+i*3600000*24;
            long dayEndTime = startTime+(i+1)*3600000*24;
            String sql = "SELECT timestamp,hr FROM "+Constants.HR_TABLE_NAME_WEEK+" WHERE timestamp>"+dayStartTime+" AND timestamp<"+dayEndTime;
            Cursor cursor = db.rawQuery(sql, null);
//            Cursor cursor = query(hourStartTime,hourEndTime,Constants.HR_TABLE_NAME_DAY);
            int totalHR = 0;
            int totalCount = 0;
            if(cursor.moveToFirst()){
                do{
                    totalHR += cursor.getInt(cursor.getColumnIndex("hr"));
                    totalCount++;
                }while (cursor.moveToNext());
            }
            if(totalCount>0){
                hrList[i] = (float) totalHR/totalCount;
            }else {
                hrList[i] = 0;
            }
//            Log.d(TAG, i+":"+String.valueOf(hrList[i]));
        }

        return hrList;
    }

    public Number[] getMonthlyData() {
        Number[] hrList = new Number[31];
        long startTime = timeHelper.getWeekStartTime();
        SQLiteDatabase db = mHelper.getWritableDatabase();
        for(int i=0; i<31; i++){
            long dayStartTime = startTime+i*3600000*24;
            long dayEndTime = startTime+(i+1)*3600000*24;
            String sql = "SELECT timestamp,hr FROM "+Constants.HR_TABLE_NAME_MONTH+" WHERE timestamp>"+dayStartTime+" AND timestamp<"+dayEndTime;
            Cursor cursor = db.rawQuery(sql, null);
//            Cursor cursor = query(hourStartTime,hourEndTime,Constants.HR_TABLE_NAME_DAY);
            int totalHR = 0;
            int totalCount = 0;
            if(cursor.moveToFirst()){
                do{
                    totalHR += cursor.getInt(cursor.getColumnIndex("hr"));
                    totalCount++;
                }while (cursor.moveToNext());
            }
            if(totalCount>0){
                hrList[i] = (float) totalHR/totalCount;
            }else {
                hrList[i] = 0;
            }
//            Log.d(TAG, i+":"+String.valueOf(hrList[i]));
        }

        return hrList;
    }


    public SQLiteDatabase getDatabase() {
        return mHelper.getWritableDatabase();
    }


    public void delete() {

    }

    public void update() {

    }


}
