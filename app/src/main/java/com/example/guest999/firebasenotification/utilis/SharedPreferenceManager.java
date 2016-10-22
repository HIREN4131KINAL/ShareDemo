package com.example.guest999.firebasenotification.utilis;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Garima
 */


public class SharedPreferenceManager {

    public static void setDefaults(String key, String value, Context context) {
        SharedPreferences prefs =
                PreferenceManager.getDefaultSharedPreferences(context);

        SharedPreferences.Editor editor = prefs.edit();

        editor.putString(key, value);

        editor.apply();
    }

    public static String getDefaults(String key, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key, null);
    }


    public static void setDefaults_boolean(String key, boolean value, Context context) {
        SharedPreferences prefs =
                PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public static Boolean getDefaults_boolean(String key, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean(key, Boolean.parseBoolean(null));
    }

    public static void ClearAllPreferences(Context context) {

        //remove all your prefs
        context.getSharedPreferences("prefs", 0).edit().clear().commit();
    }
}
