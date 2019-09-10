/*
 * Copyright (c) 2019. Ali Osman OKTAR
 * aliosmanoktar@gmail.com
 */

package com.aliosman.privatesms.Receiver;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsMessage;
import com.aliosman.privatesms.SmsManager.MySmsManager;

import java.util.Random;

public class SmsReceiver extends BroadcastReceiver {
    private String TAG = getClass().getName();
    private String ChannelID="10561";
    private String ChannelName="Messages";
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
        ShowNotification(context,body,phoneNumber);
    }
    private void ShowNotification(Context ctx,String body,String address){
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(ctx,ChannelID)
                .setSmallIcon(android.R.drawable.ic_dialog_alert)
                .setContentTitle(address)
                .setContentText(body)
                .setAutoCancel(true);
        NotificationManager notificationManager =
                (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationBuilder.setChannelId(ChannelID);
            NotificationChannel mChannel = new NotificationChannel(ChannelID, ChannelName,NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(mChannel);
        }
        notificationManager.notify(new Random().nextInt(), notificationBuilder.build());
    }

}
