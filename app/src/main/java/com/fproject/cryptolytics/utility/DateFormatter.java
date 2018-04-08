package com.fproject.cryptolytics.utility;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by chamu on 4/8/2018.
 */

public class DateFormatter {

    public static String format(float value) {
        // Convert from unix time stamp
        long time = (long) value * 1000L;

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM");
        return dateFormat.format(new Date(time));
    }
}
