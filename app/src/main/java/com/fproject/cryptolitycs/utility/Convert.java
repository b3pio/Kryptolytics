package com.fproject.cryptolitycs.utility;

/**
 * Created by chamu on 4/15/2018.
 */
public class Convert {

    /**
     * Convert t
     */
    public static Double formattedStrToDouble(String value) {
        value = value.replace(",","");

        return Double.valueOf(value);
    }

    public static String cleanDoubleStrValue(String value) {
        return value = value.replace(",","");

    }
}
