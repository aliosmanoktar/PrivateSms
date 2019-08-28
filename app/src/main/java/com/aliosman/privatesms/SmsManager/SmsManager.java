/*
 * Copyright (c) 2019. Ali Osman OKTAR
 * aliosmanoktar@gmail.com
 */

package com.aliosman.privatesms.SmsManager;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.aliosman.privatesms.Model.Conversation;

import java.util.ArrayList;
import java.util.List;

public class SmsManager {

    private String TAG = getClass().getName();


    public List<Conversation> GetConversation(Context ctx){
        List<Conversation> items = new ArrayList<>();
        Uri mSmsinboxQueryUri = Uri.parse("content://mms-sms/conversations/");
        Cursor cursor1 = ctx.getContentResolver().query(mSmsinboxQueryUri,new String[] { "_id", "thread_id", "address", "person", "date","body", "type" }, null, null, null);
        String[] columns = new String[] { "address", "person", "date", "body","type" };
        if (cursor1.getCount() > 0) {
            String count = Integer.toString(cursor1.getCount());
            while (cursor1.moveToNext()){
                String address = cursor1.getString(cursor1.getColumnIndex(columns[0]));
                String name = cursor1.getString(cursor1.getColumnIndex(columns[1]));
                long date = cursor1.getLong(cursor1.getColumnIndex(columns[2]));
                String msg = cursor1.getString(cursor1.getColumnIndex(columns[3]));
                int type = cursor1.getInt(cursor1.getColumnIndex(columns[4]));
                Conversation item=new Conversation()
                        .setMessage(msg)
                        .setName(name)
                        .setNumber(address)
                        .setSent(type==2)
                        .setDate(date);
                items.add(item);
                Log.e(TAG, "GetConversation: "+item.toString());
            }
        }
        return items;
    }
}
