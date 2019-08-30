/*
 * Copyright (c) 2019. Ali Osman OKTAR
 * aliosmanoktar@gmail.com
 */

package com.aliosman.privatesms.Activity;

import android.database.Cursor;
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
import com.aliosman.privatesms.Model.Message;
import com.aliosman.privatesms.R;
import com.aliosman.privatesms.SmsManager.SmsManager;

import java.util.ArrayList;
import java.util.List;

public class MessageActivity extends AppCompatActivity {
    private List<Message> items = new ArrayList<>(
/*            Arrays.asList(new Message[]{
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
    private SmsManager smsmanager =new SmsManager();
    private TextView txt_name;
    private ImageView back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        number=getIntent().getExtras().getString(AppContents.number_extras);
        txt_name=findViewById(R.id.message_activity_name);
        back=findViewById(R.id.message_activity_back);
        back.setOnClickListener(back_click);
        txt_name.setText(smsmanager.getName(this,number));
        cursor= smsmanager.getMessageCursor(this,number);
        recyclerView = findViewById(R.id.message_activity_recylerview);
        recyclerView.setAdapter(new MessageAdapter(items));
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
        LoadMessage();
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
    private View.OnClickListener back_click = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            finish();
        }
    };
}
