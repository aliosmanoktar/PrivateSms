/*
 * Copyright (c) 2019. Ali Osman OKTAR
 * aliosmanoktar@gmail.com
 */

package com.aliosman.privatesms;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class Settings {
    private final String TAG = getClass().getName();
    public final String SharedNameString = "PrivateSmsSettings";
    public final String pattern_password = "pattern_password";
    public final String pin_password = "pin_password";
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