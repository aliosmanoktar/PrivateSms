/*
 * Copyright (c) 2019. Ali Osman OKTAR
 * aliosmanoktar@gmail.com
 */

package com.aliosman.privatesms.Receiver;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;
import android.support.v4.app.RemoteInput;
import android.util.Log;

import com.aliosman.privatesms.AppContents;
import com.aliosman.privatesms.SmsManager.MySmsManager;

public class NotificationActionReceiver extends BroadcastReceiver {
    private final String TAG = getClass().getName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle extras = intent.getExtras();
        int messageId = extras.getInt(AppContents.messageId_extras, -1);
        int notificationId = extras.getInt(AppContents.notificationId_extras, -1);
        if (intent.getAction().equals(AppContents.Action_seen_sms)) {
            if (messageId != -1)
                SeenSms(context, messageId);
        } else if (intent.getAction().equals(AppContents.Action_reply_sms)) {
            String addres = extras.getString(AppContents.number_extras, null);
            String message = getMessageText(intent);
            if (addres != null && message != null)
                ReplySms(context, addres, message, messageId);
        }

        if (notificationId != -1)
            clearNotification(context, notificationId);
    }

    private void ReplySms(Context ctx, String address, String message, int MessageID) {
        Log.e(TAG, "ReplySms: { address:" + address + "} { message : " + message + " }");
        MySmsManager manager = new MySmsManager();
        manager.sendSms(ctx, message, address);
        if (MessageID != -1)
            SeenSms(ctx, MessageID);
    }

    private String getMessageText(Intent intent) {
        Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
        return remoteInput != null
                ? remoteInput.getCharSequence(AppContents.Action_reply_text).toString()
                : null;
    }

    private void SeenSms(Context ctx, int ID) {
        MySmsManager manager = new MySmsManager();
        manager.readSms(ctx, ID);
        ctx.sendBroadcast(new Intent(AppContents.conversationBroadcast));
    }

    private void clearNotification(Context ctx, int notificationID) {
        NotificationManager notificationManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(notificationID);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            for (StatusBarNotification notification : notificationManager.getActiveNotifications())
                notificationManager.cancel(notification.getTag(), notification.getId());
    }
}