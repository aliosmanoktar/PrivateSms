/*
 * Copyright (c) 2019. Ali Osman OKTAR
 * aliosmanoktar@gmail.com
 */

package com.aliosman.privatesms.Activity;

import android.content.BroadcastReceiver;
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
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.aliosman.privatesms.Adapters.MessageAdapter;
import com.aliosman.privatesms.AppContents;
import com.aliosman.privatesms.Model.Contact;
import com.aliosman.privatesms.Receiver.DeliverReceiver;
import com.aliosman.privatesms.Receiver.SentReceiver;
import com.aliosman.privatesms.Model.Message;
import com.aliosman.privatesms.R;
import com.aliosman.privatesms.SmsManager.MySmsManager;
import java.util.ArrayList;
import java.util.List;

public class MessageActivity extends AppCompatActivity {
    BroadcastReceiver sendBroadcastReceiver = new SentReceiver();
    BroadcastReceiver deliveryBroadcastReciever = new DeliverReceiver();

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

        registerReceiver(sendBroadcastReceiver, new IntentFilter(SENT));

        registerReceiver(deliveryBroadcastReciever, new IntentFilter(DELIVERED));

        smsmanager.sendSms(this,new Message()
                .setMessage(message)
                .setContact(
                        new Contact()
                                .setNumber(phoneNumber))
                .setSent(true)
                .setType(4)
                .setRead(true));

    }

    private View.OnClickListener send_click = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            sendSMS("55552155542","SmsContentTest23");
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(sentReceiver, new IntentFilter("broadCastName"));
        registerReceiver(smsReceiver,new IntentFilter(number));
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
            /*Uri uri = Uri.parse(ıntent.getStringExtra("message_uri"));
            items.add(0,smsmanager.getMessage(uri,getBaseContext()));
            recyclerView.post(new Runnable() {
                @Override
                public void run() {
                    load=false;
                    recyclerView.getAdapter().notifyDataSetChanged();
                }
            });*/
        }
    };

    private BroadcastReceiver smsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e(TAG, "onReceive: Sms Receiver");
        }
    };
}
