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
        String sql2 = "create table "+Constants.HR_TABLE_NAME_WEEK+"(timestamp long, hr long)";
        String sql3 = "create table "+Constants.HR_TABLE_NAME_MONTH+"(timestamp long, hr long)";

        String sql4 = "create table "+Constants.WEIGHT_TABLE_NAME+"(timestamp long, weight long)";
        db.execSQL(sql1);
        db.execSQL(sql2);
        db.execSQL(sql3);
        db.execSQL(sql4);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // call back when update
        Log.d(TAG, "Update database");
    }

}
