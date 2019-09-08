/*
 * Copyright (c) 2019. Ali Osman OKTAR
 * aliosmanoktar@gmail.com
 */

package com.aliosman.privatesms.SmsManager;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import com.aliosman.privatesms.ConversationComparator;
import com.aliosman.privatesms.Model.Contact;
import com.aliosman.privatesms.Model.Conversation;
import com.aliosman.privatesms.Model.Message;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MySmsManager {

    private String TAG = getClass().getName();

    private static final String _conversationString="content://mms-sms/conversations";
    private static final String _SmsString="content://sms/";
    private static final String _address="address";
    private static final String _id="_id";
    private static final String _body="body";
    private static final String _seen="seen";
    private static final String _date="date";
    private static final String _status="status";
    private static final String _read="read";
    private static final String _type="type";
    private static final String _person="person";

    public List<Conversation> getConversation(Context ctx){
        List<Conversation> items= new ArrayList<>();
        Cursor cursor = ctx.getContentResolver().query(Uri.parse(_conversationString),null, null, null, null);
        while (cursor.moveToNext()){
            String body = cursor.getString(cursor.getColumnIndex(_body));
            long TimeStamp=Long.parseLong(cursor.getString(cursor.getColumnIndex(_date)));
            String address=cursor.getString(cursor.getColumnIndex(_address));
            items.add(new Conversation()
                    .setMessage(body)
                    .setDate(TimeStamp)
                    .setContact(
                            new Contact()
                                    .setNumber(address)
                                    .setName(getName(ctx,address))
                    )
            );
        }
        Collections.sort(items,new ConversationComparator());
        return items;
    }

    public Cursor getMessageCursor(Context ctx,String address){
        Uri myMessage = Uri.parse(_SmsString);
        ContentResolver cr = ctx.getContentResolver();
        Cursor c = cr.query(myMessage, new String[] { _id, _address ,_date,
                _body, _read,_type}, _address+" = '"+address+"'", null, null);
        return c;

    }

    public List<Message> getMessages(Cursor cursor){
        List<Message> items = new ArrayList<>();
        int i = 0;
        while (cursor.moveToNext() && i<50){
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(_id));
            long date = cursor.getLong(cursor.getColumnIndexOrThrow(_date));
            String body = cursor.getString(cursor.getColumnIndexOrThrow(_body));
            boolean send = cursor.getInt(cursor.getColumnIndex(_type))==2;
            items.add(new Message()
                    .setMessage(body)
                    .setTime(date)
                    .setSent(send)
                    .setID(id)
            );
        }
        return items;
    }

    public String getName(Context ctx,String address){
        Uri Nameuri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(address));
        Cursor cs= ctx.getContentResolver().query(Nameuri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, ContactsContract.PhoneLookup.NUMBER+"='"+address+"'",null,null);
        String name="";
        if(Character.isAlphabetic(address.charAt(0)))
            name= address;
        else if(cs.getCount()>0){
            cs.moveToFirst();
            name= cs.getString(cs.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
        }
        else
            name=address;
        return name;
    }

}