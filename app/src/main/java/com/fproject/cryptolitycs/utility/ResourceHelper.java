package com.fproject.cryptolitycs.utility;

import android.content.Context;
import android.util.Log;
import android.util.TypedValue;

import com.fproject.cryptolitycs.R;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class ResourceHelper {
    private static final String MODULE_TAG = "ResourceHelper";

    public static Map<String,String> getStringMap(Context context, int resourceId) {

        String[] stringArray = context.getResources().getStringArray(resourceId);
        Map<String,String> stringMap = new HashMap<>();

        for (String string : stringArray) {

            String[] tokens = string.split("\\|", 2);
            stringMap.put(tokens[0], tokens[1]);

            //Log.d("TAG", tokens[0] + " - " + tokens[1]);
        }

        return stringMap;
    }

    public static int getThemeColor(Context context, int attrId){
        TypedValue outValue = new TypedValue();
        Integer color = new Integer(0);

        try {

            context.getTheme().resolveAttribute(attrId, outValue,true);
            color = context.getResources().getColor(outValue.resourceId);

        }
        catch (Exception exception){
            Log.d(MODULE_TAG, "getThemeColor(): " + exception.toString());
        }

        return  color;
    }
}
