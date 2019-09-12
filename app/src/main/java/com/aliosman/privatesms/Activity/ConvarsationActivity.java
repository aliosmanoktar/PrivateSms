/*
 * Copyright (c) 2019. Ali Osman OKTAR
 * aliosmanoktar@gmail.com
 */

package com.aliosman.privatesms.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import com.aliosman.privatesms.Adapters.ConversationAdapter;
import com.aliosman.privatesms.AppContents;
import com.aliosman.privatesms.Listener.Interfaces.RecyclerViewListener;
import com.aliosman.privatesms.Listener.Interfaces.RecylerSelectedListener;
import com.aliosman.privatesms.Model.Conversation;
import com.aliosman.privatesms.R;
import com.aliosman.privatesms.SmsManager.MySmsManager;
import java.util.List;

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

    private String TAG = getClass().getName();
    private ConversationAdapter recylerAdapter=null;
    private TextView toolbar_title;
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_convarsation);
        toolbar=findViewById(R.id.conversation_activity_toolbar);
        toolbar.setTitle("");
        toolbar_title=toolbar.findViewById(R.id.conversation_activity_toolbar_title);
        setSupportActionBar(toolbar);
        recyclerView=findViewById(R.id.conversation_activity_recylerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(recylerAdapter);
        setDefaultSmsApp();

    }
    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(smsReceiver,new IntentFilter(AppContents.conversationBroadcast));
        ReplaceScreen();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.conversation_select_menu,menu);
        return true;
    }

    private RecyclerViewListener<Conversation> conversation_click = new RecyclerViewListener<Conversation>() {
        @Override
        public void Onclick(Conversation item) {
            Intent i = new Intent(getBaseContext(),MessageActivity.class);
            Bundle bundle=new Bundle();
            bundle.putSerializable(AppContents.contact_extras,item.getContact());
            i.putExtras(bundle);
            startActivity(i);
        }
    };

    private RecylerSelectedListener<Conversation> selectedListener = new RecylerSelectedListener<Conversation>() {
        @Override
        public void Selected(int count, int position) {
            if (count>0)
                toolbar_title.setText(count+" Selected");
        }

        @Override
        public void SelectedEnded(List<Conversation> items) {
            toolbar.getMenu().findItem(R.id.menu_add).setVisible(true);
            toolbar.getMenu().findItem(R.id.conversation_menu_remove).setVisible(false);
            toolbar_title.setText(R.string.app_name);
            toolbar.setNavigationIcon(null);
            if (items!=null)
                RemoveConversations(items);
        }

        @Override
        public void SelectedStart() {
            toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back));
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    recylerAdapter.RemoveSelected();
                }
            });
            toolbar.getMenu().findItem(R.id.menu_add).setVisible(false);
            toolbar.getMenu().findItem(R.id.conversation_menu_remove).setVisible(true);
        }
    };

    private BroadcastReceiver smsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ReplaceScreen();
        }
    };

    /***
     * Fix Edilmesi Gerek
     */
    private void ReplaceScreen(){
        recylerAdapter=new ConversationAdapter(new MySmsManager().getConversation(this),conversation_click,selectedListener);
        recyclerView.setAdapter(recylerAdapter);
    }


    private void setDefaultSmsApp() {
        Intent intent =
                new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
        intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME,
                getPackageName());
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.conversation_menu_remove: recylerAdapter.EndSelect();
        }
        return true;
    }
    private void RemoveConversations(List<Conversation> items){
        new MySmsManager().RemoveConversations(this,items);
        ReplaceScreen();
    }
}