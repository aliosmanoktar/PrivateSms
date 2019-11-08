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
import android.support.v4.app.RemoteInput;
import android.telephony.SmsMessage;

import com.aliosman.privatesms.Activity.MessageActivity;
import com.aliosman.privatesms.AppContents;
import com.aliosman.privatesms.Model.MessageResponse;
import com.aliosman.privatesms.R;
import com.aliosman.privatesms.SmsManager.MySmsManager;
import com.aliosman.privatesms.SmsManager.PrivateDatabase;

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
        MessageResponse messageResponse = manager.ReciveMessage(context, phoneNumber, body);
        PrivateDatabase database = new PrivateDatabase(context);
        int notificationID = -1;
        if (!database.CheckNumber(phoneNumber))
            notificationID = ShowNotification(context, body, phoneNumber, manager.getName(context, phoneNumber), messageResponse);
        else ShowNotification(context);
        Intent i = new Intent(phoneNumber);
        Bundle bu = new Bundle();
        bu.putSerializable(AppContents.messageResponse, messageResponse);
        bu.putInt(AppContents.notificationId_extras, notificationID);
        i.putExtras(bu);
        context.sendBroadcast(i);
        SentBroadCast(messageResponse.getThreadID(), context);
    }

    private void SentBroadCast(long ThreadID, Context ctx) {
        Intent i = new Intent(AppContents.conversationBroadcast);
        Bundle bundle = new Bundle();
        bundle.putLong(AppContents.conversationBroadcastThreadID, ThreadID);
        i.putExtras(bundle);
        ctx.sendBroadcast(i);
    }

    private int ShowNotification(Context ctx) {
        int NotificationID = new Random().nextInt();
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(ctx, AppContents.ChannelID)
                .setSmallIcon(R.drawable.ic_message)
                .setContentTitle(ctx.getString(R.string.app_name))
                .setContentText("Yeni bir mesaj olabilir")
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL);
        NotificationManager notificationManager =
                (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationBuilder.setChannelId(AppContents.ChannelID);
            NotificationChannel mChannel = new NotificationChannel(AppContents.ChannelID, AppContents.ChannelName, NotificationManager.IMPORTANCE_HIGH);
            mChannel.setShowBadge(true);
            notificationManager.createNotificationChannel(mChannel);
        }
        notificationManager.notify(NotificationID, notificationBuilder.build());
        return NotificationID;
    }

    private int ShowNotification(Context ctx, String body, String address, String name, MessageResponse response) {
        int NotificationID = new Random().nextInt();
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(ctx, AppContents.ChannelID)
                .setSmallIcon(R.drawable.ic_message)
                .setContentTitle(name)
                .setContentText(body)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(getClickIntent(ctx, address))
                .addAction(R.drawable.ic_double_tick, "Okundu olarak İşaretle", getSeenIntent(ctx, NotificationID, response))
                .addAction(getQucikReply(ctx, NotificationID, response, address));
        NotificationManager notificationManager =
                (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationBuilder.setChannelId(AppContents.ChannelID);
            NotificationChannel mChannel = new NotificationChannel(AppContents.ChannelID, AppContents.ChannelName, NotificationManager.IMPORTANCE_DEFAULT);
            mChannel.setShowBadge(true);
            notificationManager.createNotificationChannel(mChannel);
        }
        notificationManager.notify(address, NotificationID, notificationBuilder.build());
        return NotificationID;
    }

    private PendingIntent getReplyIntent(Context ctx, int NotificationID, String address, MessageResponse response) {
        Intent reply_intent = new Intent(ctx, NotificationActionReceiver.class);
        reply_intent.setAction(AppContents.Action_reply_sms);
        Bundle reply_bundle = new Bundle();
        reply_bundle.putInt(AppContents.notificationId_extras, NotificationID);
        reply_bundle.putSerializable(AppContents.messageResponse, response);
        reply_bundle.putString(AppContents.number_extras, address);
        reply_intent.putExtras(reply_bundle);
        return PendingIntent.getBroadcast(ctx, 0, reply_intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private PendingIntent getSeenIntent(Context ctx, int NotificationID, MessageResponse response) {
        Intent seen_intent = new Intent(ctx, NotificationActionReceiver.class);
        seen_intent.setAction(AppContents.Action_seen_sms);
        Bundle seen_bundle = new Bundle();
        seen_bundle.putInt(AppContents.notificationId_extras, NotificationID);
        seen_bundle.putSerializable(AppContents.messageResponse, response);
        seen_intent.putExtras(seen_bundle);
        return PendingIntent.getBroadcast(ctx, 0, seen_intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private PendingIntent getClickIntent(Context ctx, String address) {
        Intent intent = new Intent(ctx, MessageActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(AppContents.number_extras, address);
        intent.putExtras(bundle);
        return PendingIntent.getActivity(ctx, 0, intent, PendingIntent.FLAG_ONE_SHOT);
    }

    private NotificationCompat.Action getQucikReply(Context ctx, int NotificationID, MessageResponse response, String address) {
        String replyLabel = "Cevapla";
        RemoteInput remoteInput = new RemoteInput.Builder(AppContents.Action_reply_text)
                .setLabel(replyLabel)
                .build();
        NotificationCompat.Action action =
                new NotificationCompat.Action.Builder(R.drawable.ic_reply,
                        "Cevapla", getReplyIntent(ctx, NotificationID, address, response))
                        .addRemoteInput(remoteInput)
                        .build();
        return action;
    }
}