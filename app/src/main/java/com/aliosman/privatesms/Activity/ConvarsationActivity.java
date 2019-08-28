package com.aliosman.privatesms.Activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.aliosman.privatesms.Adapters.ConversationAdapter;
import com.aliosman.privatesms.Model.Conversation;
import com.aliosman.privatesms.R;
import com.aliosman.privatesms.SmsManager.SmsManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ConvarsationActivity extends AppCompatActivity {

    private List<Conversation> items = new ArrayList<Conversation>(
            Arrays.asList(
                    new Conversation().setMessage("test").setName("ali"),
                    new Conversation().setMessage("test").setName("ali"),
                    new Conversation().setMessage("test").setName("ali"),
                    new Conversation().setMessage("test").setName("ali"),
                    new Conversation().setMessage("test").setName("ali"),
                    new Conversation().setMessage("test").setName("ali"))
    );
    private RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_convarsation);
        recyclerView=findViewById(R.id.conversation_activity_recylerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new ConversationAdapter(new SmsManager().getConversation(this),null));
    }

}