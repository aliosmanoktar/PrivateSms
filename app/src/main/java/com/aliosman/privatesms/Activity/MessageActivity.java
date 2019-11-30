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
import android.os.Bundle;
import android.provider.ContactsContract;
import android.service.notification.StatusBarNotification;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsMessage;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aliosman.privatesms.Adapters.MessageAdapter;
import com.aliosman.privatesms.AppContents;
import com.aliosman.privatesms.Fragment.DialogMessageInfo;
import com.aliosman.privatesms.Listener.Interfaces.RecylerSelectedListener;
import com.aliosman.privatesms.Model.Contact;
import com.aliosman.privatesms.Model.Message;
import com.aliosman.privatesms.Model.MessageResponse;
import com.aliosman.privatesms.R;
import com.aliosman.privatesms.Receiver.DeliverReceiver;
import com.aliosman.privatesms.Receiver.SentReceiver;
import com.aliosman.privatesms.SmsManager.MySmsManager;
import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeInfoDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MessageActivity extends AppCompatActivity {
    private BroadcastReceiver sendBroadcastReceiver = new SentReceiver();
    private BroadcastReceiver deliveryBroadcastReciever = new DeliverReceiver();
    private List<Message> items = new ArrayList<>();
    private MessageAdapter messageAdapter;
    private boolean load = false;
    private int totalItemCount, lastVisibleItem;
    private String TAG = getClass().getName();
    private RecyclerView recyclerView;
    private Cursor cursor;
    private MySmsManager smsmanager = new MySmsManager();
    private TextView txt_name, txt_count;
    private EditText edit_message;
    private ImageView send;
    private Toolbar toolbar;
    private Contact contact;
    private View RootView;
    private boolean isSend = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        contact = (Contact) getIntent().getExtras().getSerializable(AppContents.contact_extras);
        if (contact == null) {
            String number = getIntent().getExtras().getString(AppContents.number_extras);
            contact = smsmanager.getContact(this, number);
        }

        toolbar = findViewById(R.id.message_activity_toolbar);
        txt_name = toolbar.findViewById(R.id.message_activity_name);
        toolbar.setTitle("");

        recyclerView = findViewById(R.id.message_activity_recylerview);
        send = findViewById(R.id.message_activity_send);
        edit_message = findViewById(R.id.message_activity_input_message);
        txt_count = findViewById(R.id.message_activity_message_count);
        RootView = findViewById(R.id.message_activity_rootView);

        cursor = smsmanager.getMessageCursor(this, contact.getThreadID());
        messageAdapter = new MessageAdapter(items, selectedListener);

        recyclerView.setAdapter(messageAdapter);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back));
        txt_name.setText(contact.getLookupKey().isEmpty() ? contact.getNumber() : contact.getNameText());

        toolbar.setNavigationOnClickListener(back_click);
        send.setOnClickListener(send_click);
        txt_name.setOnClickListener(name_click);
        edit_message.addTextChangedListener(edit_message_listener);
        Log.e(TAG, "onCreate: " + contact.getNumber());
        if (contact.getNumber().matches("[a-zA-Z]+")) {
            Log.e(TAG, "onCreate: {not number : " + contact.getNumber() + " }");
            findViewById(R.id.message_activity_send_layout).setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.messages_menu, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(sentReceiver, new IntentFilter("broadCastName"));
        registerReceiver(smsReceiver, new IntentFilter(contact.getNumber()));
        Setup();
    }

    private void Setup() {
        SetReyclerListener();
        LoadMessage();
        ClearNotification();
        smsmanager.readAllMessage(this, contact.getThreadID());

        String smsBody = getIntent().getExtras().getString(AppContents.Sms_Body);
        if (smsBody != null) {
            edit_message.setText(smsBody);
            CalculateLength(smsBody);
        }
        ReadMessagesBroadCast(contact.getThreadID());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(sentReceiver);
        unregisterReceiver(smsReceiver);
        if (isSend) {
            unregisterReceiver(deliveryBroadcastReciever);
            unregisterReceiver(sendBroadcastReceiver);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.message_menu_call:
                CallNumber();
                break;
            case R.id.message_menu_remove:
                List<Message> items = messageAdapter.getSelected();
                RemoveConversationQuestion(items);
                messageAdapter.EndSelect();
                break;
            case R.id.message_menu_info:
                List<Message> select_items = messageAdapter.getSelected();
                if (select_items.size() != 1)
                    Toast.makeText(getBaseContext(), "Mesaj Bilgisi için tek mesaj seçili olması gereklidir", Toast.LENGTH_LONG).show();
                else
                    ShowInfo(select_items.get(0));
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
            sendSMS(contact.getNumber(), edit_message.getText().toString().trim());
            edit_message.setText("");
        }
    };

    private View.OnClickListener name_click = v -> ShowContact();

    private View.OnClickListener back_click = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (messageAdapter.isSelect())
                messageAdapter.RemoveSelected();
            else {
                finish();
                ReadMessagesBroadCast(contact.getThreadID());
            }
        }
    };

    private BroadcastReceiver sentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            load = true;
            Uri uri = Uri.parse(intent.getStringExtra(AppContents.MessageUri));
            items.add(0, smsmanager.getMessage(uri, getBaseContext()));
            recyclerView.post(() -> {
                load = false;
                recyclerView.getAdapter().notifyDataSetChanged();
            });
        }
    };

    private BroadcastReceiver smsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            load = true;
            MessageResponse messageResponse = (MessageResponse) intent.getExtras().getSerializable(AppContents.messageResponse);
            int notificationID = intent.getExtras().getInt(AppContents.notificationId_extras);
            Message message = smsmanager.getMessage(smsmanager.getMessageUriWithID(messageResponse.getMessageID()), context);
            items.add(0, message);
            recyclerView.post(() -> {
                load = false;
                recyclerView.getAdapter().notifyDataSetChanged();
            });
            clearNotification(notificationID);
            smsmanager.readSms(context, messageResponse.getMessageID());
        }
    };

    private TextWatcher edit_message_listener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            String count = CalculateLength(s.toString());
            if (!count.isEmpty()) {
                if (txt_count.getVisibility() == View.GONE)
                    txt_count.setVisibility(View.VISIBLE);
                txt_count.setText(count);
            } else {
                if (txt_count.getVisibility() != View.GONE)
                    txt_count.setVisibility(View.GONE);
            }
        }
    };

    private RecylerSelectedListener<Message> selectedListener = new RecylerSelectedListener<Message>() {

        @Override
        public void Selected(int count, int position, List<Message> items) {
            if (count > 1)
                toolbar.getMenu().findItem(R.id.message_menu_info).setVisible(false);
            if (count != 0)
                txt_name.setText(count + " Seçildi");
        }

        @Override
        public void SelectedEnded(List<Message> items) {
            txt_name.setText(contact.getNameText());
            toolbar.getMenu().findItem(R.id.message_menu_remove).setVisible(false);
            toolbar.getMenu().findItem(R.id.message_menu_info).setVisible(false);
            toolbar.getMenu().findItem(R.id.message_menu_call).setVisible(true);

        }

        @Override
        public void SelectedStart() {
            toolbar.getMenu().findItem(R.id.message_menu_remove).setVisible(true);
            toolbar.getMenu().findItem(R.id.message_menu_info).setVisible(true);
            toolbar.getMenu().findItem(R.id.message_menu_call).setVisible(false);
        }
    };

    private void SetReyclerListener() {
        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                totalItemCount = linearLayoutManager.getItemCount();
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                if (!load && lastVisibleItem == totalItemCount - 1) {
                    LoadMessage();
                    load = true;
                    Log.e(TAG, "onScrolled: Add More");
                }
            }
        });
    }

    private void LoadMessage() {
        items.addAll(smsmanager.getMessages(cursor));
        recyclerView.post(() -> {
            load = false;
            recyclerView.getAdapter().notifyDataSetChanged();
        });
    }

    private void ShowInfo(Message item) {
        item.setContact(contact);
        BottomSheetDialogFragment fr = new DialogMessageInfo();
        Bundle b = new Bundle();
        b.putSerializable(AppContents.MessageInfoDialog_Message, item);
        fr.setArguments(b);
        fr.show(getSupportFragmentManager(), "tag");
    }

    private void sendSMS(String phoneNumber, String message) {
        String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";

        isSend = true;
        registerReceiver(sendBroadcastReceiver, new IntentFilter(SENT));

        registerReceiver(deliveryBroadcastReciever, new IntentFilter(DELIVERED));

        smsmanager.sendSms(this, message, phoneNumber);

    }

    private void ShowContact() {
        if (contact.getLookupKey().isEmpty()) {
            Uri uri = Uri.parse("tel: " + contact.getNumber());
            Intent intent = new Intent(ContactsContract.Intents.SHOW_OR_CREATE_CONTACT, uri);

            if (intent.resolveActivity(getPackageManager()) == null) {
                intent = new Intent(Intent.ACTION_INSERT)
                        .setType(ContactsContract.Contacts.CONTENT_TYPE)
                        .putExtra(ContactsContract.Intents.Insert.PHONE, contact.getNumber());
            }
            startActivity(intent);
        } else {
            Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_LOOKUP_URI, contact.getLookupKey());
            ContactsContract.QuickContact.showQuickContact(this, txt_name, uri,
                    ContactsContract.QuickContact.MODE_MEDIUM, null);
        }
    }

    private void clearNotification(int notificationID) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            for (StatusBarNotification notification : notificationManager.getActiveNotifications())
                if (notification.getId() == notificationID) {
                    notificationManager.cancel(notification.getTag(), notification.getId());
                }
    }

    private void ClearNotification() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            for (StatusBarNotification notification : notificationManager.getActiveNotifications())
                if (notification.getTag().equals(contact.getNumber())) {
                    notificationManager.cancel(notification.getTag(), notification.getId());
                }
    }

    private void CallNumber() {
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + contact.getNumber()));
        startActivity(intent);
    }

    private void RemoveConversationQuestion(List<Message> items) {
        new AwesomeInfoDialog(this)
                .setPositiveButtonText("Sil")
                .setNegativeButtonText("İptal ")
                .setNegativeButtonTextColor(R.color.white)
                .setPositiveButtonbackgroundColor(R.color.colorRed)
                .setNegativeButtonbackgroundColor(R.color.tools_theme)
                .setTitle("Uyarı")
                .setMessage("Geçerli mesajları silmek istediğinizden eminmisiniz?")
                .setPositiveButtonClick(() -> {
                    RemoveMessages(items);
                })
                .setNegativeButtonClick(() -> {

                }).show();
    }

    private void RemoveMessages(List<Message> items) {
        final Map<Integer, Message> messageMap = messageAdapter.RemoveAll(items);
        Snackbar snackbar = Snackbar.make(RootView, items.size() == 1
                ? "Mesaj Silindi "
                : "Mesajlar Silindi", Snackbar.LENGTH_LONG);
        snackbar.addCallback(new Snackbar.Callback() {
            @Override
            public void onDismissed(Snackbar transientBottomBar, int event) {
                Log.e(TAG, "onDismissed: " + event);
                if (event == Snackbar.Callback.DISMISS_EVENT_TIMEOUT) {
                    Log.e(TAG, "onDismissed: Silindi");
                    MessageActivity.this.items.clear();
                    smsmanager.RemoveMessages(getBaseContext(), items);
                    cursor = smsmanager.getMessageCursor(getBaseContext(), contact.getThreadID());
                    LoadMessage();
                }
            }
        });
        snackbar.setAction("Geri Al", v -> {
            messageAdapter.RestoreAll(messageMap);
        });
        snackbar.setActionTextColor(getResources().getColor(R.color.tools_theme));
        snackbar.show();
    }

    private String CalculateLength(String draft) {
        String s = "";
        int[] arr = SmsMessage.calculateLength(draft, false);
        int messages = arr[0];
        int remaining = arr[2];
        if (messages <= 1 && remaining > 10)
            s = "";
        else if (messages <= 1 && remaining <= 10)
            s += remaining;
        else
            s += (remaining + " / " + messages);
        return s;
    }

    private void ReadMessagesBroadCast(long ThreadID) {
        Intent in = new Intent(AppContents.conversationBroadcast);
        Bundle b = new Bundle();
        b.putLong(AppContents.conversationBroadcastThreadID, ThreadID);
        in.putExtras(b);
        sendBroadcast(in);
    }
}