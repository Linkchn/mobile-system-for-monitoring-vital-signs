package com.grp.application.database;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class TimeHelper {
    /**
     * Get the timestamp of the start of a day
     *
     * @param timeStamp timestamp of now
     * @return the timestamp of the start of a day
     */
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
     * Get the timestamp of the end of a day
     *
     * @param timeStamp timestamp of now
     * @return the timestamp of the end of a day
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
