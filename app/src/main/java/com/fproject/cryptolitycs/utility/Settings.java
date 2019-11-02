package com.fproject.cryptolitycs.utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.fproject.cryptolitycs.R;

public class Settings {

    public static void setTheme(Context context, int theme) {
        SharedPreferences preferences = context.getSharedPreferences("foo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putInt("theme", theme);
        editor.apply();
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        //prefs.edit().putInt(context.getString(R.string.prefs_theme_key), theme).apply();
    }

    public static int getTheme(Context context){

        SharedPreferences preferences = context.getSharedPreferences("foo", Context.MODE_PRIVATE);
        return  preferences.getInt("theme", R.style.BlueTheme);
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        //return prefs.getInt(context.getString(R.string.prefs_theme_key), -1);
    }
}
