/*
 * Copyright (c) 2019. Ali Osman OKTAR
 * aliosmanoktar@gmail.com
 */

package com.aliosman.privatesms.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import com.aliosman.privatesms.Adapters.ConversationAdapter;
import com.aliosman.privatesms.AppContents;
import com.aliosman.privatesms.Listener.Interfaces.RecyclerViewListener;
import com.aliosman.privatesms.Model.Conversation;
import com.aliosman.privatesms.R;
import com.aliosman.privatesms.SmsManager.MySmsManager;

public class ConvarsationActivity extends AppCompatActivity {

    /*private List<Conversation> items = new ArrayList<Conversation>(
            Arrays.asList(
                    new Conversation().setMessage("test").setContact(new Contact().setName("Ali")),
                    new Conversation().setMessage("test").setContact(new Contact().setName("ali")),
                    new Conversation().setMessage("test").setContact(new Contact().setName("ali")),
                    new Conversation().setMessage("test").setContact(new Contact().setName("ali")),
                    new Conversation().setMessage("test").setContact(new Contact().setName("ali")),
                    new Conversation().setMessage("test").setContact(new Contact().setName("ali"))
            )
    );*/
    private RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_convarsation);
        recyclerView=findViewById(R.id.conversation_activity_recylerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        setDefaultSmsApp();
    }

    @Override
    protected void onResume() {
        super.onResume();
        recyclerView.setAdapter(new ConversationAdapter(new MySmsManager().getConversation(this),conversation_click));
    }

    private RecyclerViewListener<Conversation> conversation_click = new RecyclerViewListener<Conversation>() {
        @Override
        public void Onclick(Conversation item) {
            Intent i = new Intent(getBaseContext(),MessageActivity.class);
            Bundle bundle=new Bundle();
            bundle.putString(AppContents.number_extras,item.getContact().getNumber());
            i.putExtras(bundle);
            startActivity(i);
        }
    };

    private void setDefaultSmsApp() {
        Intent intent =
                new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
        intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME,
                getPackageName());
        startActivity(intent);
    }
}