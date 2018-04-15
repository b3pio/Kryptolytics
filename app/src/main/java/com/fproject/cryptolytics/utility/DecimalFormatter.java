package com.fproject.cryptolytics.utility;

import android.util.Log;

import java.text.DecimalFormat;
import java.text.ParseException;

/**
 * Created by chamu on 3/22/2018.
 */
public class DecimalFormatter {

    /**
     * Gets the numbers after the decimals point.
     */
    private static String getDecimals(String valueStr) {

        if (valueStr.contains(".")){
            String[] parts = valueStr.split("\\.");

            if (parts.length == 2){
                return "." + parts[1];
            }

            return ".";
        }

        return "";
    }

    /**
     *
     */
    public static String formatString(String valueStr) {
        Double value = Double.valueOf(valueStr);

        DecimalFormat decimalFormat = new DecimalFormat("###,###,##0");
        decimalFormat.setMaximumFractionDigits(0);

        return decimalFormat.format(value) + getDecimals(valueStr);
    }

    public static Double formatStringToDouble(String valueStr) {
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
        decimalFormat.setMaximumIntegerDigits(8);

        try {
            return decimalFormat.parse(valueStr).doubleValue();

        }
        catch (ParseException ex) {
            return 0D;
        }

    }

    /**
     * Format double value to Grouping separator
     */
    public static String formatDouble(Double value){

        DecimalFormat decimalFormat = new DecimalFormat("###,###,##0.########");
        decimalFormat.setMaximumFractionDigits(8);

        return decimalFormat.format(value);
    }


    public static String formatFloat(float value) {

        DecimalFormat decimalFormat = new DecimalFormat("###,###,##0.00######");
        decimalFormat.setMaximumFractionDigits(2);

        return decimalFormat.format(value) ;
    }
}
