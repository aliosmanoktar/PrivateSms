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

import com.aliosman.privatesms.AppContents;
import com.aliosman.privatesms.ContactComparator;
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

    /**
     * Değerleri String yerine index Olarak Güncelle
     **/
    private final String TAG = getClass().getName();
    private final String SENT = "SMS_SENT";
    private final String DELIVERED = "SMS_DELIVERED";
    private static final String _conversationString = "content://mms-sms/conversations";
    private static final String _SmsString = "content://sms/";
    private static final String _address = "address";
    private static final String _id = "_id";
    private static final String _body = "body";
    private static final String _seen = "seen";
    private static final String _date = "date";
    private static final String _msgCount = "msg_count";
    private static final String _status = "status";
    private static final String _read = "read";
    private static final String _type = "type";
    private static final String _thread_id = "thread_id";
    private static final String _person = "person";

    public List<Conversation> getConversation(Context ctx) {
        String selection = "";
        List<String> numbers = new PrivateDatabase(ctx).getAllPrivateNumbers();
        for (int i = 0; i < numbers.size(); i++) {
            selection += (_address + " != '" + numbers.get(i) + "'");
            if (i != (numbers.size() - 1))
                selection += " AND ";
        }
        return getConversationFromSelection(ctx, selection);
    }

    public List<Conversation> getPrivateConversations(Context ctx) {
        String selection = "";
        List<String> numbers = new PrivateDatabase(ctx).getAllPrivateNumbers();
        if (numbers.isEmpty())
            return new ArrayList<>();
        for (int i = 0; i < numbers.size(); i++) {
            selection += (_address + " = '" + numbers.get(i) + "'");
            if (i != (numbers.size() - 1))
                selection += "OR ";
        }
        return getConversationFromSelection(ctx, selection);
    }

    private List<Conversation> getConversationFromSelection(Context ctx, String selection) {
        List<Conversation> items = new ArrayList<>();
        List<String> pinned = new PrivateDatabase(ctx).getAllPinnedNumbers();
        Cursor cursor = ctx.getContentResolver().query(Uri.parse(_conversationString), null, selection, null, null);
        while (cursor.moveToNext()) {
            String body = cursor.getString(cursor.getColumnIndex(_body));
            long TimeStamp = Long.parseLong(cursor.getString(cursor.getColumnIndex(_date)));
            String address = cursor.getString(cursor.getColumnIndex(_address));
            int read = cursor.getInt(cursor.getColumnIndex(_read));
            int type = cursor.getInt(cursor.getColumnIndex(_type));
            items.add(new Conversation()
                    .setMessage(body)
                    .setDate(TimeStamp)
                    .setRead(read == 1)
                    .setType(type)
                    .setCount(getNonReadSmsCount(ctx, address))
                    .setPinned(pinned.contains(address))
                    .setContact(
                            getContact(ctx, address)
                    )
            );
        }
        Collections.sort(items, new ConversationComparator());
        return items;
    }

    public Cursor getMessageCursor(Context ctx, String address) {
        Uri myMessage = Uri.parse(_SmsString);
        ContentResolver cr = ctx.getContentResolver();
        Cursor c = cr.query(myMessage, new String[]{_id, _address, _date,
                _body, _read, _type}, _address + " = '" + address + "'", null, null);
        return c;

    }

    private Cursor getMessageCursor(Uri uri, Context ctx) {
        ContentResolver cr = ctx.getContentResolver();
        return cr.query(uri, new String[]{_id, _address, _date,
                _body, _read, _type}, null, null, null);
    }

    public List<Message> getMessages(Cursor cursor) {
        List<Message> items = new ArrayList<>();
        int i = 0;
        while (cursor.moveToNext() && i < 50) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(_id));
            long date = cursor.getLong(cursor.getColumnIndexOrThrow(_date));
            String body = cursor.getString(cursor.getColumnIndexOrThrow(_body));
            int type = cursor.getInt(cursor.getColumnIndex(_type));
            items.add(new Message()
                    .setMessage(body)
                    .setTime(date)
                    .setType(type)
                    .setID(id)
            );
        }
        return items;
    }

    public Message getMessage(Uri uri, Context ctx) {
        Cursor cursor = getMessageCursor(uri, ctx);
        cursor.moveToFirst();
        int id = cursor.getInt(cursor.getColumnIndexOrThrow(_id));
        long date = cursor.getLong(cursor.getColumnIndexOrThrow(_date));
        String body = cursor.getString(cursor.getColumnIndexOrThrow(_body));
        int type = cursor.getInt(cursor.getColumnIndex(_type));

        return new Message()
                .setMessage(body)
                .setTime(date)
                .setType(type)
                .setID(id);
    }

    public String getName(Context ctx, String address) {
        Uri Nameuri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(address));
        Cursor cs = ctx.getContentResolver().query(Nameuri, null, ContactsContract.PhoneLookup.NUMBER + "='" + address + "'", null, null);
        String name = "";
        if (Character.isAlphabetic(address.charAt(0)))
            name = address;
        else if (cs != null && cs.getCount() > 0) {
            cs.moveToFirst();
            name = cs.getString(cs.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
        } else
            name = address;
        return name;
    }

    public Contact getContact(Context ctx, String address) {
        Uri Nameuri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(address));
        Cursor cs = ctx.getContentResolver().query(Nameuri, null, ContactsContract.PhoneLookup.NUMBER + "='" + address + "'", null, null);
        String name = "";
        String lookoup = "";
        int id = -1;
        if (Character.isAlphabetic(address.charAt(0)))
            name = address;
        else if (cs != null && cs.getCount() > 0) {
            cs.moveToFirst();
            id = cs.getInt(cs.getColumnIndex(ContactsContract.PhoneLookup.CONTACT_ID));
            lookoup = cs.getString(cs.getColumnIndex(ContactsContract.PhoneLookup.LOOKUP_KEY));
            name = cs.getString(cs.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
        } else
            name = address;
        return new Contact().setLookupKey(lookoup).setNumber(address).setName(name).setID(id);
    }

    /**
     * Parçalı gönderme eklenecek-Test Edilmesi Gerek
     *
     * @param ctx
     * @param messageBody
     * @param phoneNumber
     */
    public void sendSms(Context ctx, String messageBody, String phoneNumber) {
        Calendar cal = Calendar.getInstance();
        Message message = new Message()
                .setMessage(messageBody)
                .setType(4)
                .setTime(cal.getTimeInMillis())
                .setRead(true)
                .setContact(
                        new Contact()
                                .setNumber(phoneNumber));
        int messageId = AddMessage(ctx, message);
        Uri messageUri = getMessageUriWithID(messageId);
        Intent sentIntent = new Intent(SENT);
        sentIntent.putExtra(AppContents.MessageUri, messageUri == null ? "" : messageUri.toString());

        Intent deliveredIntent = new Intent(DELIVERED);
        deliveredIntent.putExtra(AppContents.MessageUri, messageUri == null ? "" : messageUri.toString());

        PendingIntent sentPI = PendingIntent.getBroadcast(ctx, messageId, sentIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        PendingIntent deliveredPI = PendingIntent.getBroadcast(ctx, messageId,
                deliveredIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        ArrayList<PendingIntent> sentPIS = new ArrayList<>();
        ArrayList<PendingIntent> deliveredPIS = new ArrayList<>();
        SmsManager sms = SmsManager.getDefault();
        ArrayList<String> parts = sms.divideMessage(messageBody);
        for (int i = 0; i < parts.size(); i++) {
            sentPIS.add(sentPI);
            deliveredPIS.add(deliveredPI);
        }
        sms.sendMultipartTextMessage(phoneNumber, null, parts, sentPIS, deliveredPIS);
    }

    public Uri getMessageUriWithID(int ID) {
        return Uri.parse(_SmsString + ID);
    }

    public int ReciveMessage(Context ctx, String phoneNumber, String messageBody) {
        Calendar cal = Calendar.getInstance();
        Message message = new Message()
                .setRead(false)
                .setType(1)
                .setMessage(messageBody)
                .setTime(cal.getTimeInMillis())
                .setContact(
                        new Contact().setNumber(phoneNumber)
                );
        return AddMessage(ctx, message);
    }

    private int AddMessage(Context ctx, Message message) {
        ContentValues values = new ContentValues();
        values.put(_address, message.getContact().getNumber());
        values.put(_body, message.getMessage());
        values.put(_date, message.getTime() + "");
        values.put(_read, message.isRead() ? 1 : 0);
        values.put(_type, message.getType());
        long threadId = getThreadID(ctx, message.getContact().getNumber());
        values.put(_thread_id, threadId);
        Uri messageUri = ctx.getContentResolver().insert(Uri.parse(_SmsString), values);
        Cursor query = ctx.getContentResolver().query(messageUri, new String[]{_id}, null, null, null);
        int messageId = -1;
        if (query != null && query.moveToFirst()) {
            messageId = query.getInt(0);
            query.close();
        }
        query.close();
        return messageId;
    }

    /**
     * Eksik var
     * Random olarak oluşturmak yerine conversation eklenerek onun id'si Kullanılmalı
     *
     * @param ctx
     * @param phoneNumber
     * @return
     */
    private long getThreadID(Context ctx, String phoneNumber) {
        Uri.Builder uriBuilder = Uri.parse("content://mms-sms/threadID").buildUpon();
        uriBuilder.appendQueryParameter("recipient", phoneNumber);
        Uri uri = uriBuilder.build();
        Log.e(TAG, "getThreadID: " + uri);
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

    public void readAllMessage(Context ctx, String phoneNumber) {
        ContentValues values = new ContentValues();
        values.put(_read, 1);
        ctx.getContentResolver().update(Uri.parse(_SmsString), values, _address + " = '" + phoneNumber + "'", null);
    }

    public void readSms(Context ctx, int ID) {
        ContentValues values = new ContentValues();
        values.put(_read, 1);
        ctx.getContentResolver().update(Uri.parse(_SmsString), values, _id + " = " + ID, null);
    }

    public int getNonReadSmsCount(Context ctx, String phoneNumber) {
        Cursor cur = ctx.getContentResolver().query(Uri.parse(_SmsString), null, _address + " = '" + phoneNumber + "' and read=0", null, null);
        return cur.getCount();
    }

    public void RemoveConversations(Context ctx, List<Conversation> items) {
        ContentResolver cr = ctx.getContentResolver();
        for (Conversation item : items)
            cr.delete(Uri.parse(_SmsString), _address + " = '" + item.getContact().getNumber() + "'", null);
    }

    public void RemoveMessages(Context ctx, List<Message> items) {
        ContentResolver cr = ctx.getContentResolver();
        for (Message item : items)
            cr.delete(Uri.parse(_SmsString), _id + " = " + item.getID(), null);
    }

    /***
     * Hatalar var düzeltilmesi gerek
     * @param ctx
     * @return
     */
    public List<Contact> getAllNumber(Context ctx) {
        List<Contact> items = new ArrayList<>();
        Cursor cs = ctx.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        while (cs.moveToNext()) {
            String id = cs.getString(cs.getColumnIndex(ContactsContract.Contacts._ID));
            String name = cs.getString(cs.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            String lookoup = cs.getString(cs.getColumnIndex(ContactsContract.PhoneLookup.LOOKUP_KEY));
            String number = "";
            if (cs.getInt(cs.getColumnIndex(
                    ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                Cursor pCur = ctx.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);
                while (pCur.moveToNext()) {
                    number = pCur.getString(pCur.getColumnIndex(
                            ContactsContract.CommonDataKinds.Phone.NUMBER));
                }
                pCur.close();
            }

            items.add(
                    new Contact()
                            .setName(name)
                            .setLookupKey(lookoup)
                            .setNumber(number)
            );
        }
        Collections.sort(items, new ContactComparator());
        return items;
    }

}