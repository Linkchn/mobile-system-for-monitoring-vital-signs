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
                insertSql = "insert into "+ Constants.HR_TABLE_NAME_DAY+"(timestamp,hr) values(?,?)";
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

    public void computeInsertTodayData() {
        SQLiteDatabase db = dao.getDatabase();
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

        if(totalCount>0){
            int todayHR = (int) totalHR/totalCount;
            long timestamp = Calendar.getInstance().getTimeInMillis();
            dao.insert(timestamp,todayHR,Constants.HR_TABLE_NAME_WEEK);
            dao.insert(timestamp,todayHR,Constants.HR_TABLE_NAME_MONTH);
            Log.d(TAG,"totalCount"+totalCount+",endTime"+endTime);
        }
    }

    public void clearTodayData() {
        SQLiteDatabase db = dao.getDatabase();
        long startTime = getDailyStartTime(new Date().getTime());
        long endTime = getDailyEndTime(new Date().getTime());
        dao.delete(startTime,endTime,Constants.HR_TABLE_NAME_DAY);
        Log.d(TAG,"Clear data of today");
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
     *
     * @param timeStamp
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
