/*
 * Copyright (c) 2019. Ali Osman OKTAR
 * aliosmanoktar@gmail.com
 */

package com.aliosman.privatesms.SmsManager;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.aliosman.privatesms.ConversationComparator;
import com.aliosman.privatesms.Model.Contact;
import com.aliosman.privatesms.Model.Conversation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SmsManager {

    private String TAG = getClass().getName();

    public static final String _conversationString="content://mms-sms/conversations";
    public static final String _SmsString="content://sms/";
    public static final String _address="address";
    public static final String _id="_id";
    public static final String _body="body";
    public static final String _seen="seen";
    public static final String _date="date";
    public static final String _status="status";
    public static final String _read="read";
    public static final String _type="type";
    public static final String _person="person";

    public List<Conversation> getConversation(Context ctx){
        List<Conversation> items= new ArrayList<>();
        Cursor cursor = ctx.getContentResolver().query(Uri.parse(_conversationString),null, null, null, null);
        while (cursor.moveToNext()){
            String body = cursor.getString(cursor.getColumnIndex(_body));
            long TimeStamp=Long.parseLong(cursor.getString(cursor.getColumnIndex(_date)));
            String address=cursor.getString(cursor.getColumnIndex(_address));
            String name = cursor.getString(cursor.getColumnIndex(_person));
            items.add(new Conversation()
                    .setMessage(body)
                    .setDate(TimeStamp)
                    .setContact(
                            new Contact()
                                    .setNumber(address)
                                    .setName(name)
                    )
            );
        }
        Collections.sort(items,new ConversationComparator());
        return items;
    }

}