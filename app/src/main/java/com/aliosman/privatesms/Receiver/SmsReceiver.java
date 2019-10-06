/*
 * Copyright (c) 2019. Ali Osman OKTAR
 * aliosmanoktar@gmail.com
 */

package com.aliosman.privatesms.Receiver;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsMessage;

import com.aliosman.privatesms.Activity.MessageActivity;
import com.aliosman.privatesms.AppContents;
import com.aliosman.privatesms.R;
import com.aliosman.privatesms.SmsManager.MySmsManager;

import java.util.Random;

public class SmsReceiver extends BroadcastReceiver {
    private String TAG = getClass().getName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Object[] smsExtra = (Object[]) intent.getExtras().get("pdus");
        String body = "";
        String phoneNumber = "";
        for (int i = 0; i < smsExtra.length; ++i) {
            SmsMessage sms = SmsMessage.createFromPdu((byte[]) smsExtra[i]);
            phoneNumber = sms.getOriginatingAddress();
            body += sms.getMessageBody();
        }
        MySmsManager manager = new MySmsManager();
        int id = manager.ReciveMessage(context, phoneNumber, body);
        int notificationID = ShowNotification(context, body, phoneNumber, manager.getName(context, phoneNumber));
        Intent i = new Intent(phoneNumber);
        Bundle bu = new Bundle();
        bu.putInt(AppContents.messageId_extras, id);
        bu.putInt(AppContents.notificationId_extras, notificationID);
        i.putExtras(bu);
        context.sendBroadcast(i);
        context.sendBroadcast(new Intent(AppContents.conversationBroadcast));
    }

    private int ShowNotification(Context ctx, String body, String address, String name) {
        /*Intent snoozeIntent = new Intent(ctx, Test.class);
        snoozeIntent.setAction("Action Test");
        snoozeIntent.putExtra(EXTRA_NOTIFICATION_ID, 0);*/
        /*PendingIntent snoozePendingIntent =
                PendingIntent.getBroadcast(ctx, 0, snoozeIntent, 0);*/
        Intent intent = new Intent(ctx, MessageActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(AppContents.number_extras, address);
        intent.putExtras(bundle);
        PendingIntent pendingIntent = PendingIntent.getActivity(ctx, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(ctx, AppContents.ChannelID)
                .setSmallIcon(R.drawable.ic_message)
                .setContentTitle(name)
                .setContentText(body)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent);
                /*.addAction(R.drawable.ic_delete, "Test", snoozePendingIntent)
                .addAction(R.drawable.ic_delete, "Test 2", snoozePendingIntent);*/
        NotificationManager notificationManager =
                (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationBuilder.setChannelId(AppContents.ChannelID);
            NotificationChannel mChannel = new NotificationChannel(AppContents.ChannelID, AppContents.ChannelName, NotificationManager.IMPORTANCE_HIGH);
            mChannel.setShowBadge(true);
            notificationManager.createNotificationChannel(mChannel);
        }
        int NotificationID = new Random().nextInt();
        notificationManager.notify(address, NotificationID, notificationBuilder.build());
        return NotificationID;
    }
}