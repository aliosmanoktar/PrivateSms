/*
 * Copyright (c) 2019. Ali Osman OKTAR
 * aliosmanoktar@gmail.com
 */

package com.aliosman.privatesms.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.FileProvider;
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
import com.aliosman.privatesms.Fragment.DialogUpdate;
import com.aliosman.privatesms.Listener.Interfaces.RecyclerViewListener;
import com.aliosman.privatesms.Listener.Interfaces.RecylerSelectedListener;
import com.aliosman.privatesms.Model.Contact;
import com.aliosman.privatesms.Model.Conversation;
import com.aliosman.privatesms.Model.Version;
import com.aliosman.privatesms.R;
import com.aliosman.privatesms.SmsManager.MySmsManager;
import com.aliosman.privatesms.SmsManager.PrivateDatabase;
import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeInfoDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ConvarsationActivity extends AppCompatActivity {

    private List<Conversation> items = new ArrayList<>();
    private boolean load = false;
    private int totalItemCount, lastVisibleItem;
    private String TAG = getClass().getName();
    private ConversationAdapter recylerAdapter = null;
    private TextView toolbar_title;
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private FloatingActionButton fab_button;
    private MySmsManager manager = new MySmsManager();
    private View RootView;
    private Cursor cursor;

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


        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String smsBody = bundle.getString(AppContents.Sms_Body);
            String smsAddress = bundle.getString(AppContents.number_extras);
            if (smsBody != null)
                ShowMessageActivity(smsBody, smsAddress);
        }

        recylerAdapter = new ConversationAdapter(items, conversation_click, selectedListener);
        recyclerView.setAdapter(recylerAdapter);
        SetReyclerListener();
        ResetRecylerListener();
        CheckVersion();
        setDefaultSmsApp();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(smsReceiver, new IntentFilter(AppContents.conversationBroadcast));
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
            case R.id.conversation_menu_settings:
                startActivity(new Intent(this, SettingsActivity.class));
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
            toolbar.getMenu().findItem(R.id.conversation_menu_pinned).setVisible(false);
            toolbar.getMenu().findItem(R.id.conversation_menu_unpinned).setVisible(false);
            if (count > 0)
                toolbar_title.setText(count + " Selected");
            else SelectedEnded(null);
        }

        private boolean IsPinnedShow(List<Conversation> items) {
            /*for (Conversation item : items)
                if (!item.isPinned())
                    return true;*/
            return false;
        }

        @Override
        public void SelectedEnded(List<Conversation> items) {
            toolbar.getMenu().findItem(R.id.conversation_menu_remove).setVisible(false);
            toolbar.getMenu().findItem(R.id.conversation_menu_pinned).setVisible(false);
            toolbar.getMenu().findItem(R.id.conversation_menu_unpinned).setVisible(false);
            toolbar.getMenu().findItem(R.id.conversation_menu_settings).setVisible(true);
            toolbar_title.setText(R.string.app_name);
            toolbar.setNavigationIcon(null);
        }

        @Override
        public void SelectedStart() {
            toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back));
            toolbar.setNavigationOnClickListener(v -> recylerAdapter.RemoveSelected());
            toolbar.getMenu().findItem(R.id.conversation_menu_remove).setVisible(true);
            toolbar.getMenu().findItem(R.id.conversation_menu_settings).setVisible(false);
        }
    };

    private BroadcastReceiver smsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Long threadID = intent.getExtras().getLong(AppContents.conversationBroadcastThreadID, -1);
            if (!new PrivateDatabase(context).CheckThreadID(threadID))
                ReplaceItem(threadID);
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
            startActivity(new Intent(getBaseContext(), LockActivity.class));
    };

    private void ResetRecylerListener() {
        items.clear();
        items.addAll(manager.getPinnedNumbers(this));
        cursor = manager.getConversation(this);
        LoadConversation();
    }

    private void SetReyclerListener() {
        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                totalItemCount = items.size();
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                if (!load && lastVisibleItem == totalItemCount - 1) {
                    LoadConversation();
                    load = true;
                    Log.e(TAG, "onScrolled: Add More");
                }
            }
        });
    }

    private void LoadConversation() {
        items.addAll(manager.getConversation(cursor, this));
        recyclerView.post(() -> {
            load = false;
            recyclerView.getAdapter().notifyDataSetChanged();
        });
    }

    private void setDefaultSmsApp() {
        Intent intent =
                new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
        intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME,
                getPackageName());
        startActivity(intent);
    }

    private int getConversationIndex(long ThreadID) {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getThreadId() == ThreadID)
                return i;
        }
        return -1;
    }

    private void ReplaceItem(long ThreadID) {
        if (ThreadID != -1) {
            int index = getConversationIndex(ThreadID);
            if (index != -1) {
                Conversation item = manager.getConversationItem(this, ThreadID);
                items.remove(index);
                items.add(index, item);
                recylerAdapter.notifyDataSetChanged();
            } else {
                Conversation item = manager.getConversationItem(this, ThreadID);
                items.add(0, item);
                recylerAdapter.notifyDataSetChanged();
                Log.e(TAG, "onReceive: Index ID -1");
            }
        } else {
            Log.e(TAG, "onReceive: ThreadID ID -1");
        }
    }

    private void Pinned() {
        List<Conversation> items = recylerAdapter.getSelected();
        PrivateDatabase database = new PrivateDatabase(getBaseContext());
        for (Conversation item : items) {
            database.AddPinnedNumber(item.getThreadId());
        }
        recylerAdapter.EndSelect();
        ResetRecylerListener();
        //ReplaceScreen();
    }

    private void Unpinned() {
        List<Conversation> items = recylerAdapter.getSelected();
        PrivateDatabase database = new PrivateDatabase(getBaseContext());
        for (Conversation item : items) {
            database.RemovePinnedNumber(item.getThreadId());
        }
        recylerAdapter.EndSelect();
        ResetRecylerListener();
        //ReplaceScreen();
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
                    //ReplaceScreen();
                }
            }
        });
        snackbar.setAction("Geri Al", v -> {
            recylerAdapter.RestoreAll(conversationMap);
        });
        snackbar.setActionTextColor(getResources().getColor(R.color.tools_theme));
        snackbar.show();
    }

    private void CheckVersion() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Version");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Version version = dataSnapshot.getValue(Version.class);
                if (CheckVersion(version)) {
                    UpdateQuestions(version);
                    //DownloadFile(version);
                } else Log.e(TAG, "onDataChange: Günceleme Yok");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void UpdateQuestions(Version version) {
        new AwesomeInfoDialog(this)
                .setPositiveButtonText("Güncelle")
                .setNegativeButtonText("İptal")
                .setNegativeButtonTextColor(R.color.white)
                .setPositiveButtonbackgroundColor(R.color.tools_theme)
                .setNegativeButtonbackgroundColor(R.color.colorRed)
                .setTitle("Uyarı")
                .setMessage("Güncelleme mevcut")
                .setPositiveButtonClick(() -> DownloadFile(version))
                .setNegativeButtonClick(() -> {

                }).show();
    }

    private boolean CheckVersion(Version update) {
        Version defult = new Version();
        return defult.getVersionCode() < update.getVersionCode();
    }

    private void DownloadFile(Version version) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        DialogFragment dialogFragment = new DialogUpdate();
        Bundle bundle = new Bundle();
        DialogUpdate.CallBack callBack = (DialogUpdate.CallBack) () -> {
            dialogFragment.dismissAllowingStateLoss();
            Intent intent = new Intent("android.intent.action.VIEW");
            intent.addCategory("android.intent.category.DEFAULT");
            File file = new File(getExternalCacheDir().getAbsolutePath() + File.separator + "app.apk");//new File(getFilesDir().getPath()+ File.separator+"app.apk");
            intent.setDataAndType(FileProvider.getUriForFile(getApplicationContext(),
                    "com.aliosman.privatesms.fileprovider",
                    file), "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(intent);
        };
        dialogFragment.setArguments(bundle);

        bundle.putSerializable(AppContents.Update_View_extras_listener, callBack);
        bundle.putSerializable(AppContents.Update_View_extas_version, version);
        dialogFragment.show(transaction, "dialog");
    }

}