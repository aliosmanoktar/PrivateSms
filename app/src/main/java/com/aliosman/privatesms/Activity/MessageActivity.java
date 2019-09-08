/*
 * Copyright (c) 2019. Ali Osman OKTAR
 * aliosmanoktar@gmail.com
 */

package com.aliosman.privatesms.Activity;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.aliosman.privatesms.Adapters.MessageAdapter;
import com.aliosman.privatesms.AppContents;
import com.aliosman.privatesms.Receiver.DeliverReceiver;
import com.aliosman.privatesms.Receiver.SentReceiver;
import com.aliosman.privatesms.Model.Message;
import com.aliosman.privatesms.R;
import com.aliosman.privatesms.SmsManager.MySmsManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

public class MessageActivity extends AppCompatActivity {
    BroadcastReceiver sendBroadcastReceiver = new SentReceiver();
    BroadcastReceiver deliveryBroadcastReciever = new DeliverReceiver();

    String sms_sent="Sms Gönderildi";
    private List<Message> items = new ArrayList<>(
    /*Arrays.asList(new Message[]{
                    new Message().setMessage("test").setSent(false),
                    new Message().setMessage("test").setSent(false),
                    new Message().setMessage("Send").setSent(true),
                    new Message().setMessage("test").setSent(false),
                    new Message().setMessage("test").setSent(false),
                    new Message().setMessage("test").setSent(false),
                    new Message().setMessage("test").setSent(false),
                    new Message().setMessage("Send").setSent(true),
                    new Message().setMessage("test").setSent(false),
                    new Message().setMessage("test").setSent(false),
                    new Message().setMessage("test").setSent(false),
                    new Message().setMessage("test").setSent(false),
                    new Message().setMessage("Send").setSent(true),
                    new Message().setMessage("test").setSent(false),
                    new Message().setMessage("test").setSent(false),
                    new Message().setMessage("test").setSent(false),
                    new Message().setMessage("test").setSent(false),
                    new Message().setMessage("Send").setSent(true),
            })*/
    );
    private boolean load=false;
    int totalItemCount,lastVisibleItem;
    private String TAG = getClass().getName();
    private RecyclerView recyclerView;
    private String number;
    private Cursor cursor;
    private MySmsManager smsmanager =new MySmsManager();
    private TextView txt_name;
    private ImageView back,send;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        number=getIntent().getExtras().getString(AppContents.number_extras);

        txt_name=findViewById(R.id.message_activity_name);
        back=findViewById(R.id.message_activity_back);
        recyclerView = findViewById(R.id.message_activity_recylerview);
        send=findViewById(R.id.message_activity_send);

        txt_name.setText(smsmanager.getName(this,number));
        cursor= smsmanager.getMessageCursor(this,number);
        recyclerView.setAdapter(new MessageAdapter(items));

        back.setOnClickListener(back_click);
        send.setOnClickListener(send_click);

        SetReyclerListener();
        LoadMessage();

    }

    private void SetReyclerListener(){
        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                totalItemCount = linearLayoutManager.getItemCount();
                lastVisibleItem=linearLayoutManager.findLastVisibleItemPosition();
                if (!load && lastVisibleItem==totalItemCount-1){
                    LoadMessage();
                    load=true;
                    Log.e(TAG, "onScrolled: Add More");
                }
            }
        });
    }

    private void LoadMessage(){
        items.addAll(smsmanager.getMessages(cursor));
        recyclerView.post(new Runnable() {
            @Override
            public void run() {
                load=false;
                recyclerView.getAdapter().notifyDataSetChanged();
            }
        });
    }

    private void sendSMS(String phoneNumber, String message) {
        String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";

        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, new Intent(
                SENT), 0);

        PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0,
                new Intent(DELIVERED), 0);

        registerReceiver(sendBroadcastReceiver, new IntentFilter(SENT));

        registerReceiver(deliveryBroadcastReciever, new IntentFilter(DELIVERED));
        //Log.e(TAG, "sendSMS: "+smsmanager.getMessageID(this));
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);

    }
    private void SendTest(String phoneNumber,String message){
        Calendar cal = Calendar.getInstance();
        ContentValues values = new ContentValues();
        values.put("address", phoneNumber);
        values.put("body", message);
        values.put("date", cal.getTimeInMillis() + "");
        values.put("read", 1);
        values.put("type", 4);
        long threadId=getThreadID(phoneNumber);
        Log.e(TAG, "SendTest: threadID "+threadId);
        values.put("thread_id", threadId);
        Uri messageUri = getContentResolver().insert(Uri.parse("content://sms/"), values);
        Log.v("send_transaction", "inserted to uri: " + messageUri);
        Cursor query = getContentResolver().query(messageUri, new String[] {"_id"}, null, null, null);
        int messageId = -1;
        if (query != null && query.moveToFirst()) {
            messageId = query.getInt(0);
            query.close();
        }
        Log.e(TAG, "SendTest: MessageID "+messageId );
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, null, null);
    }
    private long getThreadID(String address){
        Uri.Builder uriBuilder = Uri.parse("content://mms-sms/threadID").buildUpon();
        uriBuilder.appendQueryParameter("recipient", address);
        Uri uri = uriBuilder.build();
        Cursor cursor = getContentResolver().query(uri, new String[]{"_id"}, null, null, null);
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
    private View.OnClickListener send_click = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            SendTest("55552155542","SmsContentTest2");
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        //registerReceiver(sentReceiver, new IntentFilter("broadCastName"));
    }

    private View.OnClickListener back_click = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            finish();
        }
    };

    private BroadcastReceiver sentReceiver =  new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent ıntent) {
            Bundle b = ıntent.getExtras();

            String message = b.getString("message");

            Log.e("newmesage", "" + message);
        }
    };

}
