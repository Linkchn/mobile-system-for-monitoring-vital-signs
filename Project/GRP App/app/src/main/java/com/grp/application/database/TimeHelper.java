package com.grp.application.database;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class TimeHelper {
    public static Long getDailyStartTime(Long timeStamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeStamp);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }


    public static Long getDailyEndTime(Long timeStamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeStamp);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTimeInMillis();
    }

    public static Long getWeekStartTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONDAY),
                calendar.get(Calendar.DAY_OF_MONTH),
                0,
                0,
                0);
        int dayOfWeek = calendar.get(Calendar. DAY_OF_WEEK) - 1;
        if (dayOfWeek == 0 ) {
            dayOfWeek = 7 ;
        }
        calendar.add(Calendar.DATE , -dayOfWeek + 1 );
        return calendar.getTimeInMillis();
    }

    public static Long getWeekEndTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONDAY),
                calendar.get(Calendar.DAY_OF_MONTH),
                23,
                59,
                59);
        int dayOfWeek = calendar.get(Calendar. DAY_OF_WEEK) - 1;
        if (dayOfWeek == 0 ) {
            dayOfWeek = 7 ;
        }
        calendar.add(Calendar.DATE , -dayOfWeek + 7 );
        return calendar.getTimeInMillis();
    }

    public static Long getMonthStartTime() {
        Calendar calendar = Calendar.getInstance();// 获取当前日期
//        calendar.setTimeInMillis(timeStamp);
        calendar.add(Calendar.YEAR, 0);
        calendar.add(Calendar.MONTH, 0);
        calendar.set(Calendar.DAY_OF_MONTH, 1);// 设置为1号,当前日期既为本月第一天
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

}
