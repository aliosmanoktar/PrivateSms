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

import com.aliosman.privatesms.AppContents;
import com.aliosman.privatesms.SmsManager.MySmsManager;

public class NotificationActionReceiver extends BroadcastReceiver {
    private final String TAG = getClass().getName();

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(AppContents.Action_seen_sms)) {
            Bundle extras = intent.getExtras();
            int messageId = extras.getInt(AppContents.messageId_extras, -1);
            if (messageId != -1)
                SeenSms(context, messageId);
            int notificationId = extras.getInt(AppContents.notificationId_extras, -1);
            if (notificationId != -1)
                clearNotification(context, notificationId);
        }
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