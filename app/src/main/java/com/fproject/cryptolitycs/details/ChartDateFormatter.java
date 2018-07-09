package com.fproject.cryptolitycs.details;

import android.util.Log;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by chamu on 4/7/2018.
 */

public class ChartDateFormatter implements IAxisValueFormatter {

    private SimpleDateFormat dateFormat;

    public ChartDateFormatter() {
        dateFormat = new SimpleDateFormat("dd MMM");
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        // convert from unix time stamp
        long time = (long) value * 1000L;

        return dateFormat.format(new Date(time));
    }
}

