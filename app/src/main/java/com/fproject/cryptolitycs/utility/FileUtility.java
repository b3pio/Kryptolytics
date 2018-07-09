package com.fproject.cryptolitycs.utility;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Helper class for checking if a file is cached.
 */
public class FileUtility {

    public static final int CURRENT_TIME   = 3;
    public static final int CURRENT_DAY    = 0;
    public static final int CURRENT_HOUR   = 1;
    public static final int CURRENT_MINUTE = 2;


    /**
     * Returns true if the file exists and it was created int specified period, otherwise returns false.
     */
    public static boolean isFileCached(Context context, String fileName, int period) {
        File file = new File(context.getFilesDir(), fileName);

        if (!file.exists()) {
            return false;
        }

        return isFromSpecifiedPeriod(file, period);
    }

    /**
     * Determines whether the file is from the specified period.
     */
    public static boolean isFromSpecifiedPeriod(File file, int period) {

        switch (period) {

           case CURRENT_DAY:
               return isFromCurrentDay(file);

           case CURRENT_HOUR:
               return isFromCurrentHour(file);

           case CURRENT_MINUTE:
               return isFromCurrentMinute(file);

           case CURRENT_TIME:
               return isFromCurrentTime(file);
        }

        return false;
    }

    /**
     * Returns true if the specified file was created today, otherwise returns false.
     */
    public static boolean isFromCurrentDay(File file) {
        Date today     = new Date();
        Date fileDate  = new Date(file.lastModified());

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(today);

        int todaysDays = calendar.get(Calendar.DAY_OF_MONTH);
        int todaysMonth = calendar.get(Calendar.MONTH);
        int todaysYear  = calendar.get(Calendar.YEAR);

        calendar.setTime(fileDate);
        int filesDay = calendar.get(Calendar.DAY_OF_MONTH);
        int filesMonth = calendar.get(Calendar.MONTH);
        int filesYear  = calendar.get(Calendar.YEAR);

        if ((todaysDays == filesDay) && (todaysMonth == filesMonth) && (todaysYear == filesYear)) {
            return  true;
        }

        return false;
    }

    /**
     * Returns true if the specified file was created in the current hour, otherwise returns false.
     */
    public static boolean isFromCurrentHour(File file) {
        Date today     = new Date();
        Date fileDate  = new Date(file.lastModified());

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(today);

        int todaysDays = calendar.get(Calendar.DAY_OF_MONTH);
        int todaysMonth = calendar.get(Calendar.MONTH);
        int todaysYear  = calendar.get(Calendar.YEAR);
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);

        calendar.setTime(fileDate);
        int filesDay = calendar.get(Calendar.DAY_OF_MONTH);
        int filesMonth = calendar.get(Calendar.MONTH);
        int filesYear  = calendar.get(Calendar.YEAR);
        int filesHour  = calendar.get(Calendar.HOUR_OF_DAY);

        if ((todaysDays == filesDay) && (todaysMonth == filesMonth) &&
            (todaysYear == filesYear) && (currentHour == filesHour)) {
            return  true;
        }

        return false;
    }

    /**
     * Returns true if the specified file was created in the current minute, otherwise returns false.
     */
    public static boolean isFromCurrentMinute(File file) {
        Long today     = new Date().getTime();
        Long fileDate  = new Date(file.lastModified()).getTime();
        Long difference = today - fileDate;

        // The difference is less than a minute.
        if (TimeUnit.MILLISECONDS.toMinutes(difference) <= 1)
            return true;

        return false;
    }

    /**
     * Returns true if the specified file was created in the current minute, otherwise returns false.
     */
    public static boolean isFromCurrentTime(File file) {
        Long today     = new Date().getTime();
        Long fileDate  = new Date(file.lastModified()).getTime();
        Long difference = today - fileDate;

        if (TimeUnit.MILLISECONDS.toSeconds(difference) <= 15)
            return true;

        return false;
    }
}
