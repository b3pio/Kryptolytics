package com.fproject.cryptolitycs.utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.fproject.cryptolitycs.R;

/**
 * Helper class for storing application settings in shared preferences.
 */
public class Settings {
    private static final String APPLICATION = "Cryptolitycs";
    private static final String THEME = "THEME";

    public static void setTheme(Context context, int theme) {

        SharedPreferences preferences = context.getSharedPreferences(Settings.APPLICATION, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putInt(Settings.THEME, theme);
        editor.apply();
    }

    public static int getTheme(Context context){
        SharedPreferences preferences = context.getSharedPreferences(Settings.APPLICATION, Context.MODE_PRIVATE);
        return preferences.getInt(Settings.THEME, R.style.BlueTheme);
    }
}
