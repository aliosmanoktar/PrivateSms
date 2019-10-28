/*
 * Copyright (c) 2019. Ali Osman OKTAR
 * aliosmanoktar@gmail.com
 */

package com.aliosman.privatesms;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class Settings {

    public enum PasswordType {
        FingerPrint(1),
        Pattern(2),
        Pin(3),
        none(-1);

        private int value;

        PasswordType(int i) {
            this.value = i;
        }

        public static PasswordType getValue(int i) {
            switch (i) {
                case 1:
                    return FingerPrint;
                case 2:
                    return Pattern;
                case 3:
                    return Pin;
                default:
                    return none;
            }
        }
    }

    private final String TAG = getClass().getName();
    public final String SharedNameString = "PrivateSmsSettings";
    public final String pattern_password = "pattern_password";
    public final String pin_password = "pin_password";
    public final String password_type = "password_type";
    private final Context context;

    public Settings(Context context) {
        this.context = context;
    }

    public String getString(String key, String defaultValue) {
        String t = getPreferences(context).getString(key, defaultValue);
        Log.e(TAG, "getString: { Key: " + key + " value: " + t + " }");
        return t;
    }

    public String getString(String key) {
        return getString(key, null);
    }

    public int getInt(String key, int defaultValue) {
        int t = getPreferences(context).getInt(key, defaultValue);
        Log.e(TAG, "getString: { Key: " + key + " value: " + t + " }");
        return t;
    }

    public int getInt(String key) {
        return getInt(key, -1);
    }


    public void setInt(String key, int value) {
        Log.e(TAG, "setString: { Key: " + key + " value: " + value + " }");
        SharedPreferences.Editor editor = getEditor(context);
        editor.putInt(key, value);
        editor.commit();
    }

    public void setString(String key, String value) {
        Log.e(TAG, "setString: { Key: " + key + " value: " + value + " }");
        SharedPreferences.Editor editor = getEditor(context);
        editor.putString(key, value);
        editor.commit();
    }

    private SharedPreferences.Editor getEditor(Context c) {
        return getPreferences(c).edit();
    }

    private SharedPreferences getPreferences(Context c) {
        return c.getSharedPreferences(SharedNameString, Context.MODE_PRIVATE);
    }

}