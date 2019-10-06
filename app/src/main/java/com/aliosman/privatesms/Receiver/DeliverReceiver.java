/*
 * Copyright (c) 2019. Ali Osman OKTAR
 * aliosmanoktar@gmail.com
 */

package com.aliosman.privatesms.Receiver;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.aliosman.privatesms.AppContents;

import java.util.Calendar;

public class DeliverReceiver extends BroadcastReceiver {
    private String sms_delivered = "Sms iletildi";
    private String sms_not_delivered = "Sms iletilmedi";
    private String TAG = getClass().getName();

    @Override
    public void onReceive(Context context, Intent arg1) {
        Log.e(TAG, "onReceive: ");
        switch (getResultCode()) {
            case Activity.RESULT_OK:
                Toast.makeText(context, sms_delivered,
                        Toast.LENGTH_SHORT).show();
                SetDelivered(context, Uri.parse(arg1.getStringExtra(AppContents.MessageUri)));
                break;
            case Activity.RESULT_CANCELED:
                Toast.makeText(context, sms_not_delivered,
                        Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void SetDelivered(Context context, Uri uri) {
        ContentValues values = new ContentValues();
        values.put("status", "0");
        values.put("date_sent", Calendar.getInstance().getTimeInMillis());
        values.put("read", true);
        context.getContentResolver().update(uri, values, null, null);
    }
}