package com.mahindracomviva.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Abhinav.Anand on 5/2/2016.
 */
public class GeneralOperationsNew {

    private SharedPreferences settings;
    private SharedPreferences.Editor editor;

    public boolean isInternetAvailable(Context context){

        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = cm.getActiveNetworkInfo();

            if(info == null){
                return false;
            }else{
                return true;
            }
        } catch (Exception e) {

            e.printStackTrace();
            return false;
        }
    }


    // get the boolean value from shared preferences
    public Boolean getBooleanSharedPreferenceValue(Context context, String key) {
        try {
            return context.getSharedPreferences("PREFS_NAME", Context.MODE_PRIVATE).getBoolean(key, false);

        } catch (Exception e) {

            return false;
        } finally {

        }
    }

    // set the boolean value from shared preferences
    public boolean setBooleanSharedPreferenceValue(Context context, String key, boolean value) {
        try {
            settings = context.getSharedPreferences("PREFS_NAME", Context.MODE_PRIVATE);
            editor = settings.edit();
            editor.putBoolean(key, value);
            editor.commit();
            return true;
        } catch (Exception e) {

            return false;
        } finally {
            settings = null;
            editor = null;
        }
    }

}
