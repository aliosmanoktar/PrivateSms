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

    private void ReplaceScreen(){
        recyclerView.setAdapter(new ConversationAdapter(new MySmsManager().getConversation(this),conversation_click,selectedListener));
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

    private RecylerSelectedListener<Conversation> selectedListener = new RecylerSelectedListener<Conversation>() {
        @Override
        public void Selected(int count, int position) {
            if (count>0)
                toolbar_title.setText(count+" Selected");
        }

        @Override
        public void SelectedEnded(List<Conversation> items) {
            toolbar.getMenu().findItem(R.id.menu_add).setVisible(true);
            toolbar.getMenu().findItem(R.id.menu_remove).setVisible(false);
            toolbar_title.setText(R.string.app_name);
            toolbar.setNavigationIcon(null);
        }

        @Override
        public void SelectedStart() {
            toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back));
            toolbar.getMenu().findItem(R.id.menu_add).setVisible(false);
            toolbar.getMenu().findItem(R.id.menu_remove).setVisible(true);
        }
    };
    private void setDefaultSmsApp() {
        Intent intent =
                new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
        intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME,
                getPackageName());
        startActivity(intent);
    }

    private BroadcastReceiver smsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ReplaceScreen();
        }
    };
}