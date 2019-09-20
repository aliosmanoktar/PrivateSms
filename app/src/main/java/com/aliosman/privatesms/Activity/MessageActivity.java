/*
 * Copyright (c) 2019. Ali Osman OKTAR
 * aliosmanoktar@gmail.com
 */

package com.aliosman.privatesms.Activity;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.service.notification.StatusBarNotification;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.aliosman.privatesms.Adapters.MessageAdapter;
import com.aliosman.privatesms.AppContents;
import com.aliosman.privatesms.Listener.Interfaces.RecylerSelectedListener;
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
    private MessageAdapter messageAdapter;
    private boolean load=false;
    private int totalItemCount,lastVisibleItem;
    private String TAG = getClass().getName();
    private RecyclerView recyclerView;
    private Cursor cursor;
    private MySmsManager smsmanager =new MySmsManager();
    private TextView txt_name;
    private EditText edit_message;
    private ImageView send;
    private Toolbar toolbar;
    private Contact contact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        contact= (Contact) getIntent().getExtras().getSerializable(AppContents.contact_extras);
        if (contact==null){
            String number = getIntent().getExtras().getString(AppContents.number_extras);
            contact=smsmanager.getContact(this,number);
        }

        toolbar=findViewById(R.id.message_activity_toolbar);
        txt_name=toolbar.findViewById(R.id.message_activity_name);
        toolbar.setTitle("");

        recyclerView = findViewById(R.id.message_activity_recylerview);
        send=findViewById(R.id.message_activity_send);
        edit_message= findViewById(R.id.message_activity_input_message);


        cursor= smsmanager.getMessageCursor(this,contact.getNumber());
        messageAdapter=new MessageAdapter(items,selectedListener);

        recyclerView.setAdapter(messageAdapter);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back));
        txt_name.setText(smsmanager.getName(this,contact.getNumber()));

        //back.setOnClickListener(back_click);
        toolbar.setNavigationOnClickListener(back_click);
        send.setOnClickListener(send_click);
        txt_name.setOnClickListener(name_click);

        SetReyclerListener();
        LoadMessage();
        ClearNotification();
        smsmanager.readAllMessage(this,contact.getNumber());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.messages_menu,menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(sentReceiver, new IntentFilter("broadCastName"));
        registerReceiver(smsReceiver,new IntentFilter(contact.getNumber()));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(sentReceiver);
        unregisterReceiver(smsReceiver);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.message_menu_call:
                CallNumber();
                break;
            case R.id.message_menu_remove:
                messageAdapter.EndSelect();
                break;
        }
        return true;
    }

    /**
     * Düzeltilmesi Gerek
     */
    private View.OnClickListener send_click = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            sendSMS(contact.getNumber(),edit_message.getText().toString().isEmpty() ? "SmsContentTest23" : edit_message.getText().toString().trim());
            edit_message.setText("");
        }
    };

    private View.OnClickListener name_click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ShowContact();
        }
    };

    private View.OnClickListener back_click = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (messageAdapter.isSelect())
                messageAdapter.RemoveSelected();
            else
                finish();
        }
    };

    private BroadcastReceiver sentReceiver =  new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent ıntent) {
            load=true;
            Uri uri = Uri.parse(ıntent.getStringExtra(AppContents.MessageUri));
            items.add(0,smsmanager.getMessage(uri,getBaseContext()));
            recyclerView.post(new Runnable() {
                @Override
                public void run() {
                    load=false;
                    recyclerView.getAdapter().notifyDataSetChanged();
                }
            });
        }
    };

    private BroadcastReceiver smsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            load=true;
            int id = intent.getExtras().getInt(AppContents.messageId_extras);
            int notificationID=intent.getExtras().getInt(AppContents.notificationId_extras);
            Message message=smsmanager.getMessage(smsmanager.getMessageUriWithID(id),context);
            items.add(0,message);
            recyclerView.post(new Runnable() {
                @Override
                public void run() {
                    load=false;
                    recyclerView.getAdapter().notifyDataSetChanged();
                }
            });
            clearNotification(notificationID);
            Log.e(TAG, "onReceive: Sms Receiver");
        }
    };

    private RecylerSelectedListener<Message> selectedListener= new RecylerSelectedListener<Message>() {

        @Override
        public void Selected(int count, int position, List<Message> items) {
            if (count>1)
                toolbar.getMenu().findItem(R.id.message_menu_info).setVisible(false);
            if (count!=0)
                txt_name.setText(count+" Seçildi");
        }

        @Override
        public void SelectedEnded(List<Message> items) {
            txt_name.setText(contact.getNameText());
            toolbar.getMenu().findItem(R.id.message_menu_remove).setVisible(false);
            toolbar.getMenu().findItem(R.id.message_menu_info).setVisible(false);
            if (items!=null)
                RemoveMessages(items);
        }

        @Override
        public void SelectedStart() {
            toolbar.getMenu().findItem(R.id.message_menu_remove).setVisible(true);
            toolbar.getMenu().findItem(R.id.message_menu_info).setVisible(true);
        }
    };

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

        smsmanager.sendSms(this,message,phoneNumber);

    }

    private void ShowContact(){
        if (contact.getLookupKey().isEmpty()) {
            Uri uri = Uri.parse("tel: "+contact.getNumber());
            Intent intent =  new Intent(ContactsContract.Intents.SHOW_OR_CREATE_CONTACT, uri);

            if (intent.resolveActivity(getPackageManager()) == null) {
                intent = new Intent(Intent.ACTION_INSERT)
                        .setType(ContactsContract.Contacts.CONTENT_TYPE)
                        .putExtra(ContactsContract.Intents.Insert.PHONE, contact.getNumber());
            }
            startActivity(intent);
        }else{
            Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_LOOKUP_URI, contact.getLookupKey());
            ContactsContract.QuickContact.showQuickContact(this, txt_name, uri,
                    ContactsContract.QuickContact.MODE_MEDIUM, null);
        }
    }

    private void clearNotification(int notificationID) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(notificationID);
    }

    private void ClearNotification(){
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            for (StatusBarNotification notification : notificationManager.getActiveNotifications())
                if (notification.getTag().equals(contact.getNumber()))
                    notificationManager.cancel(notification.getTag(),notification.getId());
    }

    private void CallNumber(){
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + contact.getNumber()));
        startActivity(intent);
    }

    private void RemoveMessages(List<Message> items){
        this.items.clear();
        smsmanager.RemoveMessages(this,items);
        cursor= smsmanager.getMessageCursor(this,contact.getNumber());
        LoadMessage();
    }
}