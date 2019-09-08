/*
 * Copyright (c) 2019. Ali Osman OKTAR
 * aliosmanoktar@gmail.com
 */

package com.aliosman.privatesms.SmsManager;

import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.util.Log;

import com.aliosman.privatesms.ConversationComparator;
import com.aliosman.privatesms.Model.Contact;
import com.aliosman.privatesms.Model.Conversation;
import com.aliosman.privatesms.Model.Message;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class MySmsManager {

    private final String TAG = getClass().getName();
    private final String SENT = "SMS_SENT";
    private final String DELIVERED = "SMS_DELIVERED";
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
    private static final String _thread_id ="thread_id";
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

    private Cursor getMessageCursor(Uri uri,Context ctx){
        ContentResolver cr = ctx.getContentResolver();
        return cr.query(uri, new String[] { _id, _address ,_date,
                _body, _read,_type},null,null,null);
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

    public Message getMessage(Uri uri,Context ctx){
        Cursor cursor = getMessageCursor(uri,ctx);
        cursor.moveToFirst();
        int id = cursor.getInt(cursor.getColumnIndexOrThrow(_id));
        long date = cursor.getLong(cursor.getColumnIndexOrThrow(_date));
        String body = cursor.getString(cursor.getColumnIndexOrThrow(_body));
        boolean send = cursor.getInt(cursor.getColumnIndex(_type))==2;
        return new Message()
                .setMessage(body)
                .setTime(date)
                .setSent(send)
                .setID(id);
    }

    public String getName(Context ctx, String address){
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

    /**
     * Parçalı gönderme eklenecek
     * @param ctx
     * @param message
     * @param phoneNumber
     */
    public void sendSms(Context ctx, String message, String phoneNumber){

        Calendar cal = Calendar.getInstance();
        ContentValues values = new ContentValues();
        values.put(_address, phoneNumber);
        values.put(_body, message);
        values.put(_date, cal.getTimeInMillis() + "");
        values.put(_read, 1);
        values.put(_type, 4);
        long threadId=getThreadID(ctx,phoneNumber);
        values.put(_thread_id, threadId);
        Uri messageUri = ctx.getContentResolver().insert(Uri.parse(_SmsString), values);
        Cursor query = ctx.getContentResolver().query(messageUri, new String[] {_id}, null, null, null);
        int messageId = -1;
        if (query != null && query.moveToFirst()) {
            messageId = query.getInt(0);
            query.close();
        }

        Intent sentIntent = new Intent(SENT);
        sentIntent.putExtra("message_uri", messageUri == null ? "" : messageUri.toString());

        Intent deliveredIntent = new Intent(DELIVERED);
        deliveredIntent.putExtra("message_uri", messageUri == null ? "" : messageUri.toString());

        PendingIntent sentPI = PendingIntent.getBroadcast(ctx, messageId, sentIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        PendingIntent deliveredPI = PendingIntent.getBroadcast(ctx, messageId,
                deliveredIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);
    }

    /**
     * Eksik var
     * Random olarak oluşturmak yerine conversation eklenerek onun id'si Kullanılmalı
     * @param ctx
     * @param phoneNumber
     * @return
     */
    private long getThreadID(Context ctx,String phoneNumber){
        Uri.Builder uriBuilder = Uri.parse("content://mms-sms/threadID").buildUpon();
        uriBuilder.appendQueryParameter("recipient", phoneNumber);
        Uri uri = uriBuilder.build();
        Log.e(TAG, "getThreadID: "+uri );
        Cursor cursor = ctx.getContentResolver().query(uri, new String[]{"_id"}, null, null, null);
        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    long id = cursor.getLong(0);
                    cursor.close();
                    return id;
                } else {

                }
            } finally {
                cursor.close();
            }
        }
        Random random = new Random();
        return random.nextLong();
    }
}