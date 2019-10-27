/*
 * Copyright (c) 2019. Ali Osman OKTAR
 * aliosmanoktar@gmail.com
 */

package com.aliosman.privatesms.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.aliosman.privatesms.Adapters.ConversationAdapter;
import com.aliosman.privatesms.AppContents;
import com.aliosman.privatesms.Listener.Interfaces.RecyclerViewListener;
import com.aliosman.privatesms.Listener.Interfaces.RecylerSelectedListener;
import com.aliosman.privatesms.Model.Contact;
import com.aliosman.privatesms.Model.Conversation;
import com.aliosman.privatesms.R;
import com.aliosman.privatesms.SmsManager.MySmsManager;
import com.aliosman.privatesms.SmsManager.PrivateDatabase;
import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeInfoDialog;

import java.util.List;
import java.util.Map;

public class ConvarsationActivity extends AppCompatActivity {

    private String TAG = getClass().getName();
    private ConversationAdapter recylerAdapter = null;
    private TextView toolbar_title;
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private FloatingActionButton fab_button;
    private MySmsManager manager = new MySmsManager();
    private View RootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_convarsation);
        toolbar = findViewById(R.id.conversation_activity_toolbar);
        toolbar.setTitle("");
        toolbar_title = toolbar.findViewById(R.id.conversation_activity_toolbar_title);
        toolbar_title.setOnClickListener(title_click);
        setSupportActionBar(toolbar);
        recyclerView = findViewById(R.id.conversation_activity_recylerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(recylerAdapter);
        fab_button = findViewById(R.id.conversation_activity_fab);
        fab_button.setOnClickListener(fab_click);

        RootView = findViewById(R.id.conversation_activity_rootView);
        setDefaultSmsApp();
        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            String smsBody = bundle.getString(AppContents.Sms_Body);
            String smsAddress = bundle.getString(AppContents.number_extras);
            if (smsBody != null)
                ShowMessageActivity(smsBody, smsAddress);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(smsReceiver, new IntentFilter(AppContents.conversationBroadcast));
        ReplaceScreen();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(smsReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.conversation_select_menu, menu);
        for (int i = 0; i < menu.size(); i++) {
            Drawable drawable = menu.getItem(i).getIcon();
            if (drawable != null) {
                drawable.mutate();
                drawable.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
            }
        }
        return true;
    }


    private RecyclerViewListener<Conversation> conversation_click = new RecyclerViewListener<Conversation>() {
        @Override
        public void Onclick(Conversation item) {
            Intent i = new Intent(getBaseContext(), MessageActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable(AppContents.contact_extras, item.getContact());
            i.putExtras(bundle);
            startActivity(i);
        }
    };

    private RecylerSelectedListener<Conversation> selectedListener = new RecylerSelectedListener<Conversation>() {

        @Override
        public void Selected(int count, int position, List<Conversation> items) {
            boolean isPinnedShow = IsPinnedShow(items);
            toolbar.getMenu().findItem(R.id.conversation_menu_pinned).setVisible(isPinnedShow);
            toolbar.getMenu().findItem(R.id.conversation_menu_unpinned).setVisible(!isPinnedShow);
            if (count > 0)
                toolbar_title.setText(count + " Selected");
            else SelectedEnded(null);
        }

        private boolean IsPinnedShow(List<Conversation> items) {
            for (Conversation item : items)
                if (!item.isPinned())
                    return true;
            return false;
        }

        @Override
        public void SelectedEnded(List<Conversation> items) {
            toolbar.getMenu().findItem(R.id.conversation_menu_remove).setVisible(false);
            toolbar.getMenu().findItem(R.id.conversation_menu_pinned).setVisible(false);
            toolbar.getMenu().findItem(R.id.conversation_menu_unpinned).setVisible(false);
            toolbar_title.setText(R.string.app_name);
            toolbar.setNavigationIcon(null);
        }

        @Override
        public void SelectedStart() {
            toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back));
            toolbar.setNavigationOnClickListener(v -> recylerAdapter.RemoveSelected());
            toolbar.getMenu().findItem(R.id.conversation_menu_remove).setVisible(true);
        }
    };

    private BroadcastReceiver smsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ReplaceScreen();
        }
    };

    private void ShowMessageActivity(String body, String address) {
        Intent i;
        if (address == null) {
            i = new Intent(this, NewMessageActivity.class);
            Bundle b = new Bundle();
            b.putString(AppContents.Sms_Body, body);
            i.putExtras(b);
        } else {
            i = new Intent(this, MessageActivity.class);
            Bundle b = new Bundle();
            Contact c = new Contact().setNumber(address).setName(manager.getName(this, address)).setLookupKey("");
            b.putSerializable(AppContents.contact_extras, c);
            b.putString(AppContents.Sms_Body, body);
            i.putExtras(b);
        }
        startActivity(i);
    }

    private View.OnClickListener fab_click = v -> startActivity(new Intent(getBaseContext(), NewMessageActivity.class));

    private View.OnClickListener title_click = v -> {
        if (!recylerAdapter.isSelect())
            startActivity(new Intent(getBaseContext(), PrivateActivity.class));
    };

    /***
     * Fix Edilmesi Gerek
     */
    private void ReplaceScreen() {
        recylerAdapter = new ConversationAdapter(manager.getConversation(this), conversation_click, selectedListener);
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
        switch (item.getItemId()) {
            case R.id.conversation_menu_remove:
                List<Conversation> items = recylerAdapter.getSelected();
                RemoveConversationQuestion(items);
                recylerAdapter.EndSelect();
                break;
            case R.id.conversation_menu_pinned:
                Pinned();
                break;
            case R.id.conversation_menu_unpinned:
                Unpinned();
                break;

        }
        return true;
    }

    private void Pinned() {
        List<Conversation> items = recylerAdapter.getSelected();
        PrivateDatabase database = new PrivateDatabase(getBaseContext());
        for (Conversation item : items) {
            database.AddPinnedNumber(item.getThreadId());
        }
        recylerAdapter.EndSelect();
        ReplaceScreen();
    }

    private void Unpinned() {
        List<Conversation> items = recylerAdapter.getSelected();
        PrivateDatabase database = new PrivateDatabase(getBaseContext());
        for (Conversation item : items) {
            database.RemovePinnedNumber(item.getThreadId());
        }
        recylerAdapter.EndSelect();
        ReplaceScreen();
    }

    private void RemoveConversationQuestion(List<Conversation> items) {
        new AwesomeInfoDialog(this)
                .setPositiveButtonText("Sil")
                .setNegativeButtonText("İptal ")
                .setNegativeButtonTextColor(R.color.white)
                .setPositiveButtonbackgroundColor(R.color.colorRed)
                .setNegativeButtonbackgroundColor(R.color.tools_theme)
                .setTitle("Uyarı")
                .setMessage("Geçerli konuşmaları silmek istediğinizden eminmisiniz?")
                .setPositiveButtonClick(() -> {
                    RemoveConversations(items);
                })
                .setNegativeButtonClick(() -> {

                }).show();
    }

    private void RemoveConversations(final List<Conversation> items) {
        final Map<Integer, Conversation> conversationMap = recylerAdapter.RemoveAll(items);
        Snackbar snackbar = Snackbar.make(RootView, items.size() == 1
                ? "Konuşma Silindi "
                : "Konuşmalar Silindi", Snackbar.LENGTH_LONG);
        snackbar.addCallback(new Snackbar.Callback() {
            @Override
            public void onDismissed(Snackbar transientBottomBar, int event) {
                Log.e(TAG, "onDismissed: " + event);
                if (event == Snackbar.Callback.DISMISS_EVENT_TIMEOUT) {
                    Log.e(TAG, "onDismissed: Silindi");
                    manager.RemoveConversations(getBaseContext(), items);
                    ReplaceScreen();
                }
            }
        });
        snackbar.setAction("Geri Al", v -> {
            recylerAdapter.RestoreAll(conversationMap);
        });
        snackbar.setActionTextColor(getResources().getColor(R.color.tools_theme));
        snackbar.show();
    }
}