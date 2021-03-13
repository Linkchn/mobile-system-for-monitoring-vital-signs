package com.grp.application.database;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.Nullable;

import com.grp.application.GlobalData;

public class AlarmService extends Service {

    public static String ACTION_ALARM = "action_alarm";
    private Handler mHanler = new Handler(Looper.getMainLooper());
    /**
     * 每1分钟更新一次数据
     */
    private static final int ONE_Miniute=10*1000;
    private static final int PENDING_REQUEST=0;



    /**
     * Every time Service called, this method run
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                if(GlobalData.isNeedCompute || GlobalData.isStartRecord){
                   Dao dao = new Dao(getApplicationContext());
                   dao.clearTodayDataInWeeklyTable();
//                   dao.computeInsertTodayData();
                   dao.insertTodayDataToDailyTable();
                   GlobalData.weeklyData = dao.getWeeklyData();
                   GlobalData.dailyData = dao.getDailyData();
                   GlobalData.monthlyData = dao.getMonthlyData();
                   Log.e("service", "weeklyData:" + GlobalData.weeklyData[3]);
                   if(GlobalData.isStartRecord) {
                       GlobalData.isNeedCompute = true;
                   }else {
                       GlobalData.isNeedCompute = false;
                   }

                }
            }
        }).start();

        // Start broadcast by AlarmManager
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        //
        long triggerAtTime = SystemClock.elapsedRealtime() + ONE_Miniute;//从开机到现在的毫秒书（手机睡眠(sleep)的时间也包括在内
        Intent i = new Intent(this, AlarmReceive.class);
        PendingIntent pIntent = PendingIntent.getBroadcast(this, PENDING_REQUEST, i, PENDING_REQUEST);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pIntent);



        return super.onStartCommand(intent, flags, startId);
    }

    private void computeData(){

    }





    @Override
    public void onCreate() {
        Log.i("-----------","onCreate - Thread ID = " + Thread.currentThread().getId());
        super.onCreate();

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}

