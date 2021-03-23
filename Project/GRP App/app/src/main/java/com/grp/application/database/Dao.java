package com.grp.application.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.grp.application.Constants;
import com.grp.application.HeartRateData;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * @author Hudie LIU
 */
public class Dao {
    private static final String TAG = "Dao";
    private final DatabaseHelper mHelper;
    private final long ONE_DAY = 24*60*60*1000;
    private final long ONE_HOUR = 60*60*1000;
    public Dao(Context context) {
        // Create db
        mHelper = new DatabaseHelper(context);
    }

    public void insert(long timestamp, long data, String tableName) {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("timestamp", timestamp);
        if(tableName == Constants.WEIGHT_TABLE){
            values.put("weight",data);
        }else{
            values.put("hr", data);
        }
        db.insert(tableName, null, values);
        db.close();
    }

    public void insertHRdata(ArrayList<HeartRateData> list) {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        long timestampCurrent = 0;
        long hrMax = 0;
        long hrMin = 1000;
        long totalHr = 0;
        int numOfHr = 0;
        db.beginTransaction();
        try {
            for(int i=0; i<list.size(); i++){

                long hr = list.get(i).getHeartRate();
                long timestamp = list.get(i).getTimestamp();
                if(hr>hrMax){
                    hrMax = hr;
                    timestampCurrent = timestamp;
                }
                if(hr<hrMin){
                    hrMin = hr;
                    timestampCurrent = timestamp;
                }
                totalHr+= hr;
                numOfHr++;
                ContentValues values = new ContentValues();
                values.put("timestamp", timestamp);
                values.put("hr", hr);
                db.insert(Constants.HR_TABLE_STORE, null, values);
//                Log.i("db",values.toString());
            }

            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }

        if(numOfHr>0){
            long hr = totalHr/numOfHr;
            long timestamp = list.get(0).getTimestamp();
            ContentValues values = new ContentValues();
            values.put("timestamp", timestamp);
            values.put("hr", hr);
//            values.put("number", numOfHr);
            db.insert(Constants.HR_TABLE_DETAIL, null, values);
        }
        db.close();

        replaceHrData(timestampCurrent,hrMax, hrMin);

    }

    public void insertHRdataDetail(ArrayList<HeartRateData> list) {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        long timestampCurrent = 0;
        long hrMax = 0;
        long hrMin = 1000;
        db.beginTransaction();
        try {
            for(int i=0; i<list.size(); i++){

                long hr = list.get(i).getHeartRate();
                long timestamp = list.get(i).getTimestamp();
                if(hr>hrMax){
                    hrMax = hr;
                    timestampCurrent = timestamp;
                }
                if(hr<hrMin){
                    hrMin = hr;
                    timestampCurrent = timestamp;
                }
                ContentValues values = new ContentValues();
                values.put("timestamp", timestamp);
                values.put("hr", hr);
                db.insert(Constants.HR_TABLE_DETAIL, null, values);
//                Log.i("db",values.toString());
            }

            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
        db.close();

        replaceHrData(timestampCurrent,hrMax, hrMin);

    }

    private void replaceHrData(long timestamp, long maxHr, long minHr) {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        long currMaxHr = 0;
        long currMinHr = 1000;
        long startTime = TimeHelper.getDailyStartTime(timestamp);
        long endTime = TimeHelper.getDailyEndTime(timestamp);
        String[] args = {Long.toString(startTime),Long.toString(endTime)};
        Cursor cursor1 = db.query(Constants.HR_TABLE_MAX, new String[]{"timestamp","hr"},"timestamp>=? AND timestamp<=?", args,null,null,null );
        Cursor cursor2 = db.query(Constants.HR_TABLE_MIN, new String[]{"timestamp","hr"},"timestamp>=? AND timestamp<=?", args,null,null,null );
        if(cursor1.moveToFirst()){
            currMaxHr = cursor1.getLong(cursor1.getColumnIndex("hr"));
        }
        if(cursor2.moveToFirst()){
            currMinHr = cursor2.getLong(cursor2.getColumnIndex("hr"));
        }

        if(maxHr>currMaxHr){
            delete(startTime,endTime,Constants.HR_TABLE_MAX);
            insert(timestamp,maxHr,Constants.HR_TABLE_MAX);
        }

        if(minHr<currMinHr) {
            delete(startTime,endTime,Constants.HR_TABLE_MIN);
            insert(timestamp,minHr,Constants.HR_TABLE_MIN);
        }

        db.close();
    }

    public HeartRateData getMaxHrDay() {
        long timestamp = System.currentTimeMillis();
        SQLiteDatabase db = mHelper.getWritableDatabase();
        long maxHr = 0;
        long time = 0;
        long startTime = TimeHelper.getDailyStartTime(timestamp);
        long endTime = TimeHelper.getDailyEndTime(timestamp);
        String[] args = {Long.toString(startTime),Long.toString(endTime)};
        Cursor cursor = db.query(Constants.HR_TABLE_MAX, new String[]{"timestamp","hr"},"timestamp>=? AND timestamp<=?", args,null,null,null );
        if(cursor.moveToFirst()){
            maxHr = cursor.getLong(cursor.getColumnIndex("hr"));
            time = cursor.getLong(cursor.getColumnIndex("timestamp"));
        }
        db.close();
        return new HeartRateData(maxHr,time);
    }

    public HeartRateData getMinHrDay() {
        long timestamp = System.currentTimeMillis();
        SQLiteDatabase db = mHelper.getWritableDatabase();
        long minHr = 0;
        long time = 0;
        long startTime = TimeHelper.getDailyStartTime(timestamp);
        long endTime = TimeHelper.getDailyEndTime(timestamp);
        String[] args = {Long.toString(startTime),Long.toString(endTime)};
        Cursor cursor = db.query(Constants.HR_TABLE_MIN, new String[]{"timestamp","hr"},"timestamp>=? AND timestamp<=?", args,null,null,null );
        if(cursor.moveToFirst()){
            minHr = cursor.getLong(cursor.getColumnIndex("hr"));
            time = cursor.getLong(cursor.getColumnIndex("timestamp"));
        }
        db.close();
        return new HeartRateData(minHr,time);
    }

    public HeartRateData getMaxHrWeek() {
        long timestamp = System.currentTimeMillis();
        SQLiteDatabase db = mHelper.getWritableDatabase();
        long maxHr = 0;
        long time = 0;
        long startTime = TimeHelper.getDailyStartTime(timestamp)-6*ONE_DAY;
        long endTime = TimeHelper.getDailyEndTime(timestamp);
        String[] args = {Long.toString(startTime),Long.toString(endTime)};
        Cursor cursor = db.query(Constants.HR_TABLE_MAX, new String[]{"timestamp","hr"},"timestamp>=? AND timestamp<=?", args,null,null,null );
        if(cursor.moveToFirst()){
            do{
                long temHr = cursor.getLong(cursor.getColumnIndex("hr"));
                if(temHr>maxHr){
                    maxHr = temHr;
                    time = cursor.getLong(cursor.getColumnIndex("timestamp"));
                }
            }while (cursor.moveToNext());
        }
        db.close();
        return new HeartRateData(maxHr,time);
    }

    public HeartRateData getMinHrWeek() {
        long timestamp = System.currentTimeMillis();
        SQLiteDatabase db = mHelper.getWritableDatabase();
        long minHr = 1000;
        long time = 0;
        long startTime = TimeHelper.getDailyStartTime(timestamp)-6*ONE_DAY;
        long endTime = TimeHelper.getDailyEndTime(timestamp);
        String[] args = {Long.toString(startTime),Long.toString(endTime)};
        Cursor cursor = db.query(Constants.HR_TABLE_MIN, new String[]{"timestamp","hr"},"timestamp>=? AND timestamp<=?", args,null,null,null );
        if(cursor.moveToFirst()){
            do{
                long temHr = cursor.getLong(cursor.getColumnIndex("hr"));
                if(temHr<minHr){
                    minHr = temHr;
                    time = cursor.getLong(cursor.getColumnIndex("timestamp"));
                }
            }while (cursor.moveToNext());
        }
        db.close();
        return new HeartRateData(minHr,time);
    }

    public HeartRateData getMaxHrMonth() {
        long timestamp = System.currentTimeMillis();
        SQLiteDatabase db = mHelper.getWritableDatabase();
        long maxHr = 0;
        long time = 0;
        long startTime = TimeHelper.getDailyStartTime(timestamp)-30*ONE_DAY;
        long endTime = TimeHelper.getDailyEndTime(timestamp);
        String[] args = {Long.toString(startTime),Long.toString(endTime)};
        Cursor cursor = db.query(Constants.HR_TABLE_MAX, new String[]{"timestamp","hr"},"timestamp>=? AND timestamp<=?", args,null,null,null );
        if(cursor.moveToFirst()){
            do{
                long temHr = cursor.getLong(cursor.getColumnIndex("hr"));
                if(temHr>maxHr){
                    maxHr = temHr;
                    time = cursor.getLong(cursor.getColumnIndex("timestamp"));
                }
            }while (cursor.moveToNext());
        }
        db.close();
        return new HeartRateData(maxHr,time);
    }

    public HeartRateData getMinHrMonth() {
        long timestamp = System.currentTimeMillis();
        SQLiteDatabase db = mHelper.getWritableDatabase();
        long minHr = 1000;
        long time = 0;
        long startTime = TimeHelper.getDailyStartTime(timestamp)-30*ONE_DAY;
        long endTime = TimeHelper.getDailyEndTime(timestamp);
        String[] args = {Long.toString(startTime),Long.toString(endTime)};
        Cursor cursor = db.query(Constants.HR_TABLE_MIN, new String[]{"timestamp","hr"},"timestamp>=? AND timestamp<=?", args,null,null,null );
        if(cursor.moveToFirst()){
            do{
                long temHr = cursor.getLong(cursor.getColumnIndex("hr"));
                if(temHr<minHr){
                    minHr = temHr;
                    time = cursor.getLong(cursor.getColumnIndex("timestamp"));
                }
            }while (cursor.moveToNext());
        }
        db.close();
        return new HeartRateData(minHr,time);
    }

    public void clearTodayDataInWeeklyTable(){
        SQLiteDatabase db = mHelper.getWritableDatabase();
        long startTime = TimeHelper.getDailyStartTime(new Date().getTime());
        long endTime = TimeHelper.getDailyEndTime(new Date().getTime());
        String[] args = {Long.toString(startTime),Long.toString(endTime)};

        db.delete(Constants.HR_TABLE_DAILY, "timestamp>=? AND timestamp<=?", args);
        db.close();
    }

//    public void insertTodayDataToDailyTable(){
//        SQLiteDatabase db = mHelper.getWritableDatabase();
//        long startTime = TimeHelper.getDailyStartTime(new Date().getTime());
//        long endTime = TimeHelper.getDailyEndTime(new Date().getTime());
//        String[] args = {Long.toString(startTime),Long.toString(endTime)};
//        Cursor cursor = db.query(Constants.HR_TABLE_DETAIL, new String[]{"timestamp","hr"},"timestamp>=? AND timestamp<=?", args,null,null,null );
//        long totalHR = 0;
//        int totalCount = 0;
//        if(cursor.moveToFirst()){
//            do{
//                Log.i("count",Integer.toString(cursor.getColumnIndex("number")));
//                totalHR += cursor.getLong(cursor.getColumnIndex("hr"));
//                totalCount ++;
//            }while (cursor.moveToNext());
//        }
//
//        if(totalCount>0){
//            long todayHR = totalHR/totalCount;
//            long timestamp = Calendar.getInstance().getTimeInMillis();
//            insert(timestamp,todayHR,Constants.HR_TABLE_DAILY);
//            Log.d(TAG,"todayHR"+todayHR+",endTime"+endTime);
//        }
//    }

    public void computeInsertTodayData() {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        long startTime = TimeHelper.getDailyStartTime(new Date().getTime());
        long endTime = TimeHelper.getDailyEndTime(new Date().getTime());
        String sql = "SELECT timestamp,hr FROM "+Constants.HR_TABLE_DETAIL +" WHERE timestamp>"+startTime+" AND timestamp<"+endTime;
        Cursor cursor = db.rawQuery(sql, null);
        long totalHR = 0;
        int totalCount = 0;
        if(cursor.moveToFirst()){
            do{
                totalHR += cursor.getLong(cursor.getColumnIndex("hr"));
                totalCount++;
            }while (cursor.moveToNext());
        }

        if(totalCount>0){
            int todayHR = (int) totalHR/totalCount;
            long timestamp = Calendar.getInstance().getTimeInMillis();
            insert(timestamp,todayHR,Constants.HR_TABLE_DAILY);
            Log.d(TAG,"totalCount"+totalCount+",endTime"+endTime);
        }
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
        long startTime = TimeHelper.getDailyStartTime(System.currentTimeMillis());
        SQLiteDatabase db = mHelper.getWritableDatabase();
        for(int i=0; i<24; i++){
            long hourStartTime = startTime+i* ONE_HOUR;
            long hourEndTime = startTime+(i+1)* ONE_HOUR;
            String sql = "SELECT timestamp,hr FROM "+Constants.HR_TABLE_DETAIL +" WHERE timestamp>"+hourStartTime+" AND timestamp<"+hourEndTime;
            Cursor cursor = db.rawQuery(sql, null);
            long totalHR = 0;
            int totalCount = 0;
            if(cursor.moveToFirst()){
                do{
                    totalHR += cursor.getLong(cursor.getColumnIndex("hr"));
                    totalCount++;
                }while (cursor.moveToNext());
            }
            if(totalCount>0){
                hrList[i] = (long) totalHR/totalCount;
            }else {
                hrList[i] = 0;
            }
//            Log.d(TAG, i+":"+String.valueOf(hrList[i]));
        }
        db.close();
        return hrList;
    }

    public Number[] getWeeklyData() {
        Number[] hrList = new Number[7];
        long startTime = TimeHelper.getDailyStartTime(System.currentTimeMillis()) - 6*ONE_DAY;
        SQLiteDatabase db = mHelper.getWritableDatabase();
        for(int i=0; i<7; i++){
            long dayStartTime = startTime+i*ONE_DAY;
            long dayEndTime = startTime+(i+1)*ONE_DAY;
            String sql = "SELECT timestamp,hr FROM "+Constants.HR_TABLE_DETAIL +" WHERE timestamp>"+dayStartTime+" AND timestamp<"+dayEndTime;
            Cursor cursor = db.rawQuery(sql, null);
//            Cursor cursor = query(hourStartTime,hourEndTime,Constants.HR_TABLE_NAME_DAY);
            long totalHR = 0;
            int totalCount = 0;
            if(cursor.moveToFirst()){
                do{
                    totalHR += cursor.getLong(cursor.getColumnIndex("hr"));
                    totalCount++;
                }while (cursor.moveToNext());
            }
            if(totalCount>0){
                hrList[i] = (long) totalHR/totalCount;
            }else {
                hrList[i] = 0;
            }
//            Log.d(TAG, i+":"+String.valueOf(hrList[i]));
        }
        db.close();

        return hrList;
    }

    public Number[] getMonthlyData() {
        Number[] hrList = new Number[31];
        long startTime =  TimeHelper.getDailyStartTime(System.currentTimeMillis()) - 30*ONE_DAY;
        SQLiteDatabase db = mHelper.getWritableDatabase();
        for(int i=0; i<31; i++){
            long dayStartTime = startTime+i*ONE_DAY;
            long dayEndTime = startTime+(i+1)*ONE_DAY;
            String sql = "SELECT timestamp,hr FROM "+Constants.HR_TABLE_DETAIL+" WHERE timestamp>"+dayStartTime+" AND timestamp<"+dayEndTime;
            Cursor cursor = db.rawQuery(sql, null);
//            Cursor cursor = query(hourStartTime,hourEndTime,Constants.HR_TABLE_NAME_DAY);
            long totalHR = 0;
            int totalCount = 0;
            if(cursor.moveToFirst()){
                do{
                    totalHR += cursor.getLong(cursor.getColumnIndex("hr"));
                    totalCount++;
                }while (cursor.moveToNext());
            }
            if(totalCount>0){
                hrList[i] = (long) totalHR/totalCount;
            }else {
                hrList[i] = 0;
            }
        }
        db.close();
        return hrList;
    }

    public Number[] getDailyWeight(){
        Number[] weightList = new Number[1];
        long startTime = TimeHelper.getDailyStartTime(System.currentTimeMillis());
        long endTime = startTime+1*ONE_DAY;
        SQLiteDatabase db = mHelper.getWritableDatabase();
        String sql = "SELECT timestamp,weitgt FROM "+Constants.WEIGHT_TABLE+" WHERE timestamp>"+startTime+" AND timestamp<"+endTime;
        Cursor cursor = db.rawQuery(sql, null);
        long totalWeight = 0;
        int totalCount = 0;
        if(cursor.moveToFirst()){
            do{
                totalWeight += cursor.getLong(cursor.getColumnIndex("weight"));
                totalCount++;
            }while (cursor.moveToNext());
        }
        if(totalCount>0){
            weightList[0] = (long) totalWeight/totalCount;
        }else {
            weightList[0] = 0;
        }
        db.close();
        return weightList;
    }

    public Number[] getWeeklyWeight() {
        Number[] weightList = new Number[7];
        long startTime = TimeHelper.getDailyStartTime(System.currentTimeMillis()) - 6*ONE_DAY;
        SQLiteDatabase db = mHelper.getWritableDatabase();
        for(int i=0; i<7; i++){
            long dayStartTime = startTime+i*ONE_DAY;
            long dayEndTime = startTime+(i+1)*ONE_DAY;
            String sql = "SELECT timestamp,weight FROM "+Constants.WEIGHT_TABLE+" WHERE timestamp>"+dayStartTime+" AND timestamp<"+dayEndTime;
            Cursor cursor = db.rawQuery(sql, null);
//            Cursor cursor = query(hourStartTime,hourEndTime,Constants.HR_TABLE_NAME_DAY);
            long totalWeight = 0;
            int totalCount = 0;
            if(cursor.moveToFirst()){
                do{
                    totalWeight += cursor.getLong(cursor.getColumnIndex("weight"));
                    totalCount++;
                }while (cursor.moveToNext());
            }
            if(totalCount>0){
                weightList[i] = (long) totalWeight/totalCount;
            }else {
                weightList[i] = 0;
            }
        }
        db.close();
        return weightList;
    }

    public Number[] getMonthlyWeight() {
        Number[] weightList = new Number[31];
        long startTime =  TimeHelper.getDailyStartTime(System.currentTimeMillis()) - 30*ONE_DAY;
        SQLiteDatabase db = mHelper.getWritableDatabase();
        for(int i=0; i<31; i++){
            long dayStartTime = startTime+i*ONE_DAY;
            long dayEndTime = startTime+(i+1)*ONE_DAY;
            String sql = "SELECT timestamp,weight FROM "+Constants.WEIGHT_TABLE+" WHERE timestamp>"+dayStartTime+" AND timestamp<"+dayEndTime;
            Cursor cursor = db.rawQuery(sql, null);
            long totalWeight = 0;
            int totalCount = 0;
            if(cursor.moveToFirst()){
                do{
                    totalWeight += cursor.getLong(cursor.getColumnIndex("weight"));
                    totalCount++;
                }while (cursor.moveToNext());
            }
            if(totalCount>0){
                weightList[i] = (long) totalWeight/totalCount;
            }else {
                weightList[i] = 0;
            }
        }
        db.close();
        return weightList;
    }

    public void clearDatabase() {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        db.delete(Constants.HR_TABLE_MAX,"1=1",null);
        db.delete(Constants.HR_TABLE_MIN,"1=1",null);
        db.delete(Constants.HR_TABLE_DAILY,"1=1",null);
        db.delete(Constants.HR_TABLE_DETAIL,"1=1",null);
        db.delete(Constants.WEIGHT_TABLE,"1=1",null);

        db.close();

    }

    public void exportHr() {

    }
    public SQLiteDatabase getDatabase() {
        return mHelper.getWritableDatabase();
    }


    public void delete() {

    }

    public void update() {

    }


}
