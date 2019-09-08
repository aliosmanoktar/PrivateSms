/*
 * Copyright (c) 2019. Ali Osman OKTAR
 * aliosmanoktar@gmail.com
 */

package com.aliosman.privatesms.Receiver;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

public class SentReceiver extends BroadcastReceiver {
    private String TAG = getClass().getName();
    @Override
    public void onReceive(Context context, Intent arg1) {
        //Log.e(TAG,new Gson().toJson(arg1));
        Uri uri = getUri(arg1);
        Log.e(TAG, "onReceive: "+uri );

        Intent i = new Intent("broadCastName");
        // Data you need to pass to activity
        i.putExtra("message_uri",uri.toString());
        context.sendBroadcast(i);

        switch (getResultCode()) {
            case Activity.RESULT_OK:
                Toast.makeText(context, "Sms Send", Toast.LENGTH_SHORT)
                        .show();
                break;
            case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                Toast.makeText(context, "Generic failure",
                        Toast.LENGTH_SHORT).show();
                break;
            case SmsManager.RESULT_ERROR_NO_SERVICE:
                Toast.makeText(context, "No service",
                        Toast.LENGTH_SHORT).show();
                break;
            case SmsManager.RESULT_ERROR_NULL_PDU:
                Toast.makeText(context, "Null PDU", Toast.LENGTH_SHORT)
                        .show();
                break;
            case SmsManager.RESULT_ERROR_RADIO_OFF:
                Toast.makeText(context, "Radio off",
                        Toast.LENGTH_SHORT).show();
                break;
        }

    }
    private Uri getUri(Intent intent) {
        Uri uri;
        try {
            uri = Uri.parse(intent.getStringExtra("message_uri"));

            if (uri.equals("")) {
                return null;
            }
        } catch (Exception e) {
            return null;
        }

        return uri;
    }
}