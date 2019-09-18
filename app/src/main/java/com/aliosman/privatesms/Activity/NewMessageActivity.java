/*
 * Copyright (c) 2019. Ali Osman OKTAR
 * aliosmanoktar@gmail.com
 */

package com.aliosman.privatesms.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import com.aliosman.privatesms.Adapters.ContactAdapter;
import com.aliosman.privatesms.AppContents;
import com.aliosman.privatesms.Listener.Interfaces.RecyclerViewListener;
import com.aliosman.privatesms.Model.Contact;
import com.aliosman.privatesms.R;
import com.aliosman.privatesms.SmsManager.MySmsManager;
import java.util.ArrayList;
import java.util.List;

public class NewMessageActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private List<Contact> items;
    private ContactAdapter adapter;
    private Toolbar toolbar;
    private EditText search;
    private String TAG = getClass().getName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_message);
        toolbar=findViewById(R.id.newmessage_activity_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("");
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back));
        toolbar.setNavigationOnClickListener(back_click);

        search=toolbar.findViewById(R.id.newmessage_activity_search);
        recyclerView=findViewById(R.id.newmessage_activity_recyler);

        items=new MySmsManager().getAllNumber(this);
        adapter=new ContactAdapter(items,click);
        recyclerView.setAdapter(adapter);
        search.addTextChangedListener(search_text_listener);
    }

    private View.OnClickListener back_click=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
        }
    };

    private TextWatcher search_text_listener= new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.length() != 0) {
                adapter.setItems(Search(s.toString()));
            } else {
                adapter.setItems(items);
            }
        }
    };
    private List<Contact> Search(String s){
        List<Contact> temp = new ArrayList<>();
        for(Contact item: items)
            if (item.getNameText().contains(s) || item.getNumber().contains(s))
                temp.add(item);
            temp.add(0,new Contact().setName(s+"' Gonder").setNumber(s).setLookupKey(""));
        return temp;
    }

    private RecyclerViewListener<Contact> click = new RecyclerViewListener<Contact>() {
        @Override
        public void Onclick(Contact item) {
            Intent i = new Intent(getBaseContext(),MessageActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK );
            Bundle bundle=new Bundle();
            bundle.putSerializable(AppContents.contact_extras,item);
            i.putExtras(bundle);
            startActivity(i);
        }
    };
}
