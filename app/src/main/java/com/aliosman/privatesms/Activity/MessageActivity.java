/*
 * Copyright (c) 2019. Ali Osman OKTAR
 * aliosmanoktar@gmail.com
 */

package com.aliosman.privatesms.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import com.aliosman.privatesms.Adapters.MessageAdapter;
import com.aliosman.privatesms.Model.Message;
import com.aliosman.privatesms.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MessageActivity extends AppCompatActivity {
    private List<Message> items = new ArrayList<>(
            Arrays.asList(new Message[]{
                    new Message().setMessage("test").setSent(false),
                    new Message().setMessage("test").setSent(false),
                    new Message().setMessage("Send").setSent(true),
                    new Message().setMessage("test").setSent(false),
                    new Message().setMessage("test").setSent(false),
            })
    );
    private RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        recyclerView = findViewById(R.id.message_activity_recylerview);
        recyclerView.setAdapter(new MessageAdapter(items));
    }
}
