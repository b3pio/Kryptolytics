package com.fproject.cryptolytics.utility;

import android.content.Context;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class ResourceHelper {

    public static Map<String,String> getStringMap(Context context, int resourceId) {

        String[] stringArray = context.getResources().getStringArray(resourceId);
        Map<String,String> stringMap = new HashMap<>();

        for (String string : stringArray) {

            String[] tokens = string.split("\\|", 2);
            stringMap.put(tokens[0], tokens[1]);

            Log.d("TAG", tokens[0] + " - " + tokens[1]);
        }

        return stringMap;
    }
}
