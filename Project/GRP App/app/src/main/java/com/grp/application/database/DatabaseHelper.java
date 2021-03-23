package com.grp.application.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.grp.application.Constants;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHelper";
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
        String sql1 = "create table "+Constants.HR_TABLE_DETAIL +"(timestamp long, hr long)";
        String sql2 = "create table "+Constants.HR_TABLE_DAILY +"(timestamp long, hr long)";
        String sql4 = "create table "+Constants.HR_TABLE_MAX +"(timestamp long, hr long)";
        String sql5 = "create table "+Constants.HR_TABLE_MIN +"(timestamp long, hr long)";
        String sql6 = "create table "+Constants.HR_TABLE_STORE +"(timestamp long, hr long)";
//        String sql3 = "create table "+Constants.HR_TABLE_NAME_MONTH+"(timestamp long, hr long)";

        String sql3 = "create table "+Constants.WEIGHT_TABLE +"(timestamp long, weight long)";
        db.execSQL(sql1);
        db.execSQL(sql2);
        db.execSQL(sql3);
        db.execSQL(sql4);
        db.execSQL(sql5);
        db.execSQL(sql6);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // call back when update
        Log.d(TAG, "Update database");
    }

}
