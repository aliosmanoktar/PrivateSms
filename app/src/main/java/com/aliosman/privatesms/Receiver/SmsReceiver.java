/*
 * Copyright (c) 2019. Ali Osman OKTAR
 * aliosmanoktar@gmail.com
 */

package com.aliosman.privatesms.Receiver;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsMessage;
import com.aliosman.privatesms.SmsManager.MySmsManager;

public class SmsReceiver extends BroadcastReceiver {
    private String TAG = getClass().getName();
    @Override
    public void onReceive(Context context, Intent intent) {
        Object[] smsExtra = (Object[]) intent.getExtras().get("pdus");
        String body = "";
        String phoneNumber="";
        for (int i = 0; i < smsExtra.length; ++i) {
            SmsMessage sms = SmsMessage.createFromPdu((byte[]) smsExtra[i]);
            phoneNumber=sms.getOriginatingAddress();
            body += sms.getMessageBody();
        }
        new MySmsManager().ReciveMessage(context,phoneNumber,body);
        /*Notification notification = new Notification.Builder(context)
                .setContentText(body)
                .setContentTitle("New Message")
                .setSmallIcon(android.R.drawable.ic_dialog_alert)
                .setStyle(new Notification.BigTextStyle().bigText(body))
                .build();
        Intent i = new Intent(addres);
        context.sendBroadcast(i);
        Log.e(TAG, "onReceive: " );
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(1, notification);*/
    }
}
