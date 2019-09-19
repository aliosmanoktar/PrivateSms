/*
 * Copyright (c) 2019. Ali Osman OKTAR
 * aliosmanoktar@gmail.com
 */

package com.aliosman.privatesms.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import com.aliosman.privatesms.Adapters.ConversationAdapter;
import com.aliosman.privatesms.Listener.Interfaces.RecyclerViewListener;
import com.aliosman.privatesms.Listener.Interfaces.RecylerSelectedListener;
import com.aliosman.privatesms.Model.Conversation;
import com.aliosman.privatesms.R;
import com.aliosman.privatesms.SmsManager.MySmsManager;
import com.aliosman.privatesms.SmsManager.PrivateDatabase;

import java.util.ArrayList;
import java.util.List;


public class PrivateActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private boolean select=false;
    private MySmsManager manager=new MySmsManager();
    private String TAG = getClass().getName();
    private ConversationAdapter adapter;
    private PrivateDatabase database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_private);
        toolbar=findViewById(R.id.private_activity_toolbar);
        recyclerView=findViewById(R.id.private_activity_recylerview);
        database=new PrivateDatabase(this);
        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back));
        toolbar.setNavigationOnClickListener(back_listener);

        setTitle("Gizli");
        SetPrivateAdapter();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.privates_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.privates_menu_add:
                SetSelectRecylerConversation();
                break;
            case R.id.privates_menu_remove:
                RemoveRemoveNumbers();

        }
        return true;
    }

    private View.OnClickListener back_listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (select){
                select=false;
                SetPrivateAdapter();
            }else if (adapter.isSelect()){
                adapter.EndSelect();
            }else {
                finish();
            }
        }
    };

    private void RemoveRemoveNumbers(){
        List<Conversation> items = adapter.getSelected();
        for(Conversation item : items)
            database.RemoveNumber(item.getContact().getNumber());
        adapter.RemoveSelected();
    }
    private void SetPrivateAdapter(){
        adapter=new ConversationAdapter(manager.getPrivateConversations(this),select_listener,selectedListener);
        recyclerView.setAdapter(adapter);
    }
    /***
     * Seçilecek kişileri listeleyecek
     */
    private void SetSelectRecylerConversation(){
        select=true;
        adapter=new ConversationAdapter(manager.getConversation(this),select_listener,null);
        recyclerView.setAdapter(adapter);
    }

    private RecyclerViewListener<Conversation> select_listener = new RecyclerViewListener<Conversation>() {
        @Override
        public void Onclick(Conversation item) {
          select=false;
          database.AddNumber(item.getContact().getNumber());
          SetPrivateAdapter();
        }
    };

    private RecylerSelectedListener<Conversation> selectedListener=new RecylerSelectedListener<Conversation>() {
        @Override
        public void Selected(int count, int position) {
            if (count!=0)
                toolbar.setTitle(count+" Seçildi");
        }

        @Override
        public void SelectedEnded(List<Conversation> items) {
            toolbar.getMenu().findItem(R.id.privates_menu_add).setVisible(true);
            toolbar.getMenu().findItem(R.id.privates_menu_remove).setVisible(false);
            SetPrivateAdapter();
            toolbar.setTitle("Gizli");
        }

        @Override
        public void SelectedStart() {
            toolbar.getMenu().findItem(R.id.privates_menu_add).setVisible(false);
            toolbar.getMenu().findItem(R.id.privates_menu_remove).setVisible(true);
        }
    };
}