/*
 * Copyright (c) 2019. Ali Osman OKTAR
 * aliosmanoktar@gmail.com
 */

package com.aliosman.privatesms.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import com.aliosman.privatesms.Adapters.ContactAdapter;
import com.aliosman.privatesms.AppContents;
import com.aliosman.privatesms.Listener.Interfaces.RecyclerViewListener;
import com.aliosman.privatesms.Model.Contact;
import com.aliosman.privatesms.R;
import com.aliosman.privatesms.SmsManager.MySmsManager;

public class NewMessageActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_message);
        recyclerView=findViewById(R.id.newmessage_activity_recyler);
        recyclerView.setAdapter(new ContactAdapter(new MySmsManager().getAllNumber(this),click));
    }
    private RecyclerViewListener<Contact> click = new RecyclerViewListener<Contact>() {
        @Override
        public void Onclick(Contact item) {
            Intent i = new Intent(getBaseContext(),MessageActivity.class);
            Bundle bundle=new Bundle();
            bundle.putSerializable(AppContents.contact_extras,item);
            i.putExtras(bundle);
            startActivity(i);
        }
    };
}
